package client.function;

import client.LoginSuccessHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import messages.settoservermsg.ChatMsg;
import messages.settoservermsg.HistoryMsg;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static client.ChatClient.*;

/**
 *  好友聊天页面Client
 *  */
public class ChatManageHandler {
    static Scanner input = new Scanner(System.in);
    private static boolean startFlag = true;  // 后加，用于处理客户端下线时的通信报错问题

    // 传from to
    public ChatManageHandler(ChannelHandlerContext ctx, String me) throws Exception {
        System.out.println("(A) 发起聊天");
        System.out.println("(B) 查看聊天记录");
        System.out.println("(C) 退出");
        System.out.println("【请输入您的选择】:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                FriendsChat(ctx,me);
                break;
            case "B":
                showHistoryMsg(ctx,me);
                break;
            case "C":
                // return login success main page
                new LoginSuccessHandler(ctx,me);
                break;
        }
    }

    // 聊天
    public static void FriendsChat(ChannelHandlerContext ctx,String from) {
        Channel client = null;

        // 开头打印好友列表
        // .....

        System.out.println("您想对哪个好友发起会话？请输入对方用户名：");
        String to = input.next();
        // 查找是否存在此好友
        // .....

        try {
            // 2、为该客户端启动一个消息接收线程
            //ChatClientThread receiveThread = new ChatClientThread(client);
            //receiveThread.start();

            // 3、主线程无限循环，来做该客户端的消息发送线程
            while (startFlag) {
                System.out.println("------ 请输入要发送的消息类型（0:文本内容 1:文件）------");
                // choose
                String type = input.next();
                // MESSAGE
                if("0".equals(type)){
                    System.out.println("------ 请输入要发送的消息内容 ------\n如想退出当前会话 请输入bye");
                    // input
                    String msgBody = input.next();
                    if("bye".equals(msgBody)){
                        startFlag = false; // 执行完本次消息发送后，退出循环，关闭
                    }

                    // time
                    SimpleDateFormat sdf = new SimpleDateFormat();  // 格式化时间
                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");  // a为am/pm的标记
                    Date date = new Date(); // 获取当前时间
                    String time = sdf.format((date));

                    // 发给服务端
                    ChatMsg msg = new ChatMsg(from, to,"String", msgBody,time);
                    ctx.writeAndFlush(msg);

                    // lock and wait
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(waitSuccess == 1) {
                        System.out.println("消息发送成功!");
                        // ....

                    }else {
                        System.out.println("拉黑好友无法发送消息");
                        // ....
                    }
                    // FILE
                }else if("1".equals(type)){
                    System.out.println("请输入要发送的本地文件路径：");
                    String filePath = new Scanner(System.in).nextLine();
                    // 校验文件路径是否正确,是否为文件
                    File file = new File(filePath);
                    if(!file.exists() || !file.isFile()){
                        System.out.println("输入路径不存在或者该路径不是文件！");
                        continue;
                    }
                    // 发给服务端
                    ChatMsg msg = new ChatMsg("File", filePath);
                    ctx.writeAndFlush(msg);

                    // lock and wait
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(waitSuccess == 1) {
                        System.out.println("文件发送成功!");
                        // ....
                    }else {
                        System.out.println("拉黑好友无法发送消息");
                        // ....
                    }
                }else{
                    System.out.println("输入消息类型不存在！请重新输入...");
                    continue;
                }
            }
        } finally {
            // 释放资源
            if(null != client){
                client.close(); // 此关闭流方式是单方面关闭输出流，client的输入流可以继续使用，不会导致client关闭
            }
            input.close();
        }
    }

    // 查看历史消息
    public static void showHistoryMsg(ChannelHandlerContext ctx, String me) throws Exception {
        // 开头打印好友列表和好友状态
        // .....
        Integer maxKey = 0;
        System.out.println("您想查看与哪个好友的聊天记录？请输入对方用户名：");
        String friend = input.next();

        HistoryMsg msg = new HistoryMsg(me, friend);
        ctx.writeAndFlush(msg);

        // lock and wait
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1){
            System.out.println("查询成功！以下是你和用户[" + friend+ "]的聊天记录：");
            Map<Integer,String> historyMsg = msgMap;
            /*  ----弃用----
            // 遍历查到最大的键
            for(Map.Entry<Integer, String> m : msgMap.entrySet()) {
                int current = m.getKey();
                if(current > maxKey) {
                    maxKey = current;
                }
            }
            // 拿到最大id
            //System.out.println("maxID --> " + maxKey);
             */

            // map按照键排个序
            List<Map.Entry<Integer,String>> list = new ArrayList<Map.Entry<Integer, String>>(msgMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
                @Override
                public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                    // 升序排序
                    return Integer.parseInt(String.valueOf(o1.getKey()))-Integer.parseInt(String.valueOf(o2.getKey()));
                }
            });

            // 输出聊天记录
            for (Map.Entry<Integer, String> entry : list) {
                System.out.println(entry.getValue());
            }
        }else {
            System.out.println("------ 查询失败 ------");
            System.out.println("您是否还要继续查询？ （Y---继续查询；除Y任意键---退出查询）");
            if(input.next().toUpperCase() == "Y") {
                showHistoryMsg(ctx,me);
            }else {
                new ChatManageHandler(ctx,me);
            }
        }

    }
}