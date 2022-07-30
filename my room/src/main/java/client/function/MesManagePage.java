package client.function;

import io.netty.channel.ChannelHandlerContext;

import java.util.Scanner;

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

    // 查看未读消息 --> 展示history_msg中state=1，toc=me的消息，展示完将state设为0
    public static void CheckUnreadMsg(ChannelHandlerContext ctx, String me) {

    }

    // 查看好友请求
    public static void CheckFriendApply(ChannelHandlerContext ctx, String me){

    }

    // 查看群通知
    public static void CheckGroupMsg(ChannelHandlerContext ctx, String me) {

    }
}