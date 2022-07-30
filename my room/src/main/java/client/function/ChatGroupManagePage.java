package client.function;

import io.netty.channel.ChannelHandlerContext;

import java.util.Scanner;

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

    }
}