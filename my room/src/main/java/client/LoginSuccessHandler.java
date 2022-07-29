package client;

import client.function.ChatManageHandler;
import client.function.FriendManageHandler;
import client.function.GroupManageHandler;
import io.netty.channel.ChannelHandlerContext;
import messages.UserMessage;
import messages.settoclientmsg.ChatHandlerMap;

import java.util.Scanner;


public class LoginSuccessHandler {
    static Scanner input = new Scanner(System.in);

    // 登陆完成后显示页面
    public LoginSuccessHandler(ChannelHandlerContext ctx,String me) throws Exception {
        int unreadMessage = 0;  // 这条代表数据库中“未读消息列表”的消息数( ????未完成?????? )
        if (unreadMessage > 0) {
            UserMessage.getUnreadMessage();  // 主页面显示有几条未读消息
        }

        // main page 实现主要五大板块
        System.out.println("(A) 好友管理");
        System.out.println("(B) 聊天群管理");
        System.out.println("(C) 好友聊天");
        System.out.println("(D) 群聊天");
        System.out.println("(E) 消息管理");

        System.out.println("(F) 退出登陆");
        System.out.println("【请输入您的选择】:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                //System.out.println(me);     // 当前用户名传进来了
                new FriendManageHandler(ctx, me);
                break;
            case "B":
                new GroupManageHandler(ctx, me);
                break;
            case "C":
                new ChatManageHandler(ctx,me);
                break;
            case "D":
                GroupChat(ctx, me);
                break;
            case "E":
                MesManagement(ctx, me);
                break;
            case "F":
                System.out.println("您确定退出登录吗? (Y) 确定 (N) 取消\n请输入：");
                SignOut(ctx);
                break;
            default:
                System.out.println("输入有误!请重新选择：\n");
                new LoginSuccessHandler(ctx,me);
        }
    }

    // 群聊天页面
    public static void GroupChat(ChannelHandlerContext ctx, String me) {
        System.out.println("(A) 发起聊天");
        System.out.println("(B) 查看聊天记录");
        System.out.println("【请输入您的选择】:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                break;
            case "B":
                break;
        }
    }

    // 消息管理页面
    public static void MesManagement(ChannelHandlerContext ctx, String me) {
        System.out.println("(A) 未读消息");
        System.out.println("(B) 查看好友请求");
        System.out.println("(C) 查看群通知");
        System.out.println("【请输入您的选择】:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                break;
            case "B":
                break;
            case "C":
                break;
        }
    }

    // 退出登录
    public static void SignOut(ChannelHandlerContext ctx) throws Exception {
        String i = input.nextLine();
        switch (i.toUpperCase()) {
            case "Y":   // state --> 0
                LoginClientHandler.setState(ctx);
                break;
            case "N":
                new LoginClientHandler(ctx);
            default:
                System.out.println("【您的输入有误！请重新输入】：");
                SignOut(ctx);
        }
    }
}