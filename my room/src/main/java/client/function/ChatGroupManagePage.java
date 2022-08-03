package client.function;

import io.netty.channel.ChannelHandlerContext;
import messages.toclient.ServerToClientMsg;
import messages.toserver.GroupMsg;
import messages.toserver.HistoryGroupMsg;

import javax.swing.*;
import java.util.*;

import static client.ChatClient.*;

/**
 * Client Page
 *  (D)群聊天页面
 *  */
public class ChatGroupManagePage {
    static Scanner input = new Scanner(System.in);

    public ChatGroupManagePage(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            System.out.println("(A) 群聊天");
            System.out.println("(B) 查看群聊记录");
            System.out.println("(C) 退出");
            System.out.println("【请输入您的选择】:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    groupChat(ctx, me);
                    break;
                case "B":
                    showHistoryGroupMsg(ctx, me);
                    break;
                case "C":
                    // return login success main page
                    s = false;
                    break;
            }
        }
    }

    // 群聊天
    public static void groupChat(ChannelHandlerContext ctx, String me) {

    }

    // 查看群聊记录
    public static void showHistoryGroupMsg(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            // 展示用户加入的群列表 -- group_list
            GroupMsg msg = new GroupMsg(me);
            ctx.writeAndFlush(msg);
            try {
                synchronized (waitMessage) {
                    waitMessage.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (waitSuccess == 1) {
                System.out.println("以下是您已加入的群列表：");
                // 输出群ID和群名（map）
                Map<Integer, String> groupList = ServerToClientMsg.getMsgMap();
                msgMap.clear();  // 归0
                // map按照键排个序
                List<Map.Entry<Integer, String>> list = new ArrayList<Map.Entry<Integer, String>>(groupList.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
                    @Override
                    public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                        // 升序排
                        return Integer.parseInt(String.valueOf(o1.getKey())) - Integer.parseInt(String.valueOf(o2.getKey()));
                    }
                });
                // 输出
                for (Map.Entry<Integer, String> entry : list) {
                    System.out.println(entry.getValue());
                }

                // 查看群聊记录 -- group_msg
                System.out.println("请问您想查看哪个群的历史记录？请输入该群ID号：");
                int id = input.nextInt();
                HistoryGroupMsg Msg = new HistoryGroupMsg(id);
                ctx.writeAndFlush(Msg);
                try {
                    synchronized (waitMessage) {
                        waitMessage.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (waitSuccess == 1) {
                    System.out.println("该群聊天记录如下：");
                    // 循环输出list内容
                    for (int i = 0; i < userList.size(); i++) {
                        System.out.println(userList.get(i));
                    }
                    userList.clear();  // 清空不影响下一次存储和输出
                    System.out.println("您是否还要继续查询群记录操作？（1 -- 继续 除1任意数字键 -- 退出）");
                    if(input.nextInt() == 1) {
                        return;
                    }else {
                        s = false;
                        return;
                    }
                } else {
                    System.out.println("没有查询到此群相关聊天记录！");
                    System.out.println("您是否还要继续查询群记录操作？（1 -- 继续 除1任意数字键 -- 退出）");
                    if(input.nextInt() == 1) {
                        return;
                    }else {
                        s = false;
                        return;
                    }
                }
            } else {
                System.out.println("您没有加入任何群聊！");
                s = false;
                return;
            }
        }
    }
}