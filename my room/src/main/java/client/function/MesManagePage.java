package client.function;

import io.netty.channel.ChannelHandlerContext;
import messages.toclient.ServerToClientMsg;
import messages.toserver.FriendProcessApplyMsg;
import messages.toserver.UnreadApplyMsg;
import messages.toserver.UnreadMsg;

import java.text.SimpleDateFormat;
import java.util.*;

import static client.ChatClient.*;

/**
 * Client Page
 *  （E）消息管理页面
 *  */
public class MesManagePage {
    static Scanner input = new Scanner(System.in);

    public MesManagePage(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            System.out.println("(A) 查看未读消息");
            System.out.println("(B) 查看好友请求");
            System.out.println("(C) 查看群通知");
            System.out.println("(D) 退出");
            System.out.println("【请输入您的选择】:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    CheckUnreadMsg(ctx, me);
                    break;
                case "B":
                    CheckFriendApply(ctx, me);
                    break;
                case "C":
                    CheckGroupMsg(ctx, me);
                    break;
                case "D":
                    // return login success main page
                    s = false;
                    break;
            }
        }
    }

    // 查看未读消息 --> 展示history_msg中state=1，toc=me,type=1or2的消息，展示完将state设为0
    public static void CheckUnreadMsg(ChannelHandlerContext ctx, String me) {
        UnreadMsg msg = new UnreadMsg();

    }

    // 查看好友请求  --> 查看history_msg中的id,state=1，toc=me，type=3的消息，展示完将state设为0
    public static void CheckFriendApply(ChannelHandlerContext ctx, String me){
        boolean s = true;
        while (s) {
            // 展示id + msg
            UnreadApplyMsg msg = new UnreadApplyMsg(me);
            ctx.writeAndFlush(msg);
            try {
                synchronized (waitMessage) {
                    waitMessage.wait();
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(waitSuccess == 1){
                System.out.println("以下是您还未处理的好友申请：");
                Map<Integer,String> unapplyMap = ServerToClientMsg.getMsgMap();
                msgMap.clear();

                // map按照键排个序
                List<Map.Entry<Integer,String>> list = new ArrayList<Map.Entry<Integer, String>>(unapplyMap.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        // 升序排序
                        return Integer.parseInt(String.valueOf(o1.getKey()))-Integer.parseInt(String.valueOf(o2.getKey()));
                    }
                });
                // 输出
                for (Map.Entry<Integer, String> entry : list) {
                    System.out.println(entry.getValue());
                }

                // 用户选择处理id （顺着id可以找到from方
                System.out.println("请选择您要处理的申请id：");
                int id = input.nextInt();
                System.out.println("是否通过好友请求？（Y--通过申请  N--拒绝申请）");
                String i = input.next();
                // catch time
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");
                Date date = new Date();
                String time = sdf.format((date));

                if (i.equals("Y")) {  // 通过申请
                    // 返回处理结果给from
                    FriendProcessApplyMsg msg2 = new FriendProcessApplyMsg(id,me,0,time);
                    ctx.writeAndFlush(msg2);
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(waitSuccess == 1) {
                        System.out.println("处理成功！您是否选择继续操作？【退出--输入1 继续--输入除1外任意数字键】");
                        int ip = input.nextInt();
                        if(ip == 1) {
                            s = false;
                        } else {
                            continue;
                        }
                    }else {
                        System.out.println("处理失败！您是否选择重新操作？【退出--输入1 继续--输入除1外任意数字键】");
                        int ip = input.nextInt();
                        if(ip == 1) {
                            s = false;
                        } else {
                            continue;
                        }
                    }
                } else if (i.equals("N")) { // 拒绝申请
                    // 返回处理结果给from
                    FriendProcessApplyMsg msg2 = new FriendProcessApplyMsg(id,me,1,time);
                    ctx.writeAndFlush(msg2);
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(waitSuccess == 1) {
                        System.out.println("处理成功！您是否选择继续操作？【退出--输入1 继续--输入除1外任意数字键】");
                        int ip = input.nextInt();
                        if(ip == 1) {
                            s = false;
                        } else {
                            continue;
                        }
                    }else {
                        System.out.println("处理失败！您是否选择重新操作？【退出--输入1 继续--输入除1外任意数字键】");
                        int ip = input.nextInt();
                        if(ip == 1) {
                            s = false;
                        } else {
                            continue;
                        }
                    }
                } else {
                    System.out.println("输入有误！您是否选择重新操作？【退出--输入1 继续--输入除1外任意数字键】");
                    int ip = input.nextInt();
                    if(ip == 1) {
                        s = false;
                    } else {
                        continue;
                    }
                }
            }else {
                System.out.println("您没有未处理好友申请！");
            }
        }
    }

    // 查看群通知
    public static void CheckGroupMsg(ChannelHandlerContext ctx, String me) {

    }
}