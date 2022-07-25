package client;

import io.netty.channel.ChannelHandlerContext;
import messages.UserMessage;

import java.util.Scanner;

public class LoginSuccessHandler {
    static Scanner input = new Scanner(System.in);

    // 登陆完成后显示页面
    public LoginSuccessHandler(ChannelHandlerContext ctx) throws Exception {
        int unreadMessage = 0;  // 这条代表数据库中“未读消息列表”的消息数
        if (unreadMessage > 0) {
            UserMessage.getUnreadMessage();  // 主页面显示有几条未读消息
        }
        // main page
        System.out.println("(A) 好友管理");
        System.out.println("(B) 聊天群管理");
        System.out.println("(C) 好友聊天");
        System.out.println("(D) 群聊天");
        System.out.println("(E) 消息管理");
        System.out.println("(F) 退出登陆");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                FriendManage(ctx);
                break;
            case "B":
                GroupManage(ctx);
                break;
            case "C":
                FriendsChat(ctx);
                break;
            case "D":
                GroupChat(ctx);
                break;
            case "E":
                MesManagement(ctx);
                break;
            case "F":
                System.out.println("您确定退出登录吗? (Y) 确定 (N) 取消\n请输入：");
                SignOut(ctx);
                break;
            default:
                System.out.println("输入有误!请重新选择：\n");
                new LoginSuccessHandler(ctx);
        }
    }

    // 好友管理页面
    public static void FriendManage(ChannelHandlerContext ctx) {
        System.out.println("(A) 查看好友列表");
        System.out.println("(B) 查看黑名单");
        System.out.println("(C) 添加好友");
        System.out.println("(D) 删除好友");
        System.out.println("(E) 返回");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                break;
            case "B":
                break;
            case "C":
                break;
            case "D":
                break;
            case "E":
                break;
        }
    }

    // 群管理页面
    public static void GroupManage(ChannelHandlerContext ctx) {
        System.out.println("(A) 查看我加入的群列表");  // 我加入的群、我创建的群、我创建的群
        System.out.println("(B) 创建新的群聊");
        System.out.println("(C) 申请加入群聊");
        System.out.println("(D) 退出群聊");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                break;
            case "B":
                break;
            case "C":
                break;
            case "D":
                break;
        }
    }

    // 好友聊天页面
    public static void FriendsChat(ChannelHandlerContext ctx) {
        System.out.println("(A) 发起聊天");
        System.out.println("(B) 查看聊天记录");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                // setMsg();
                break;
            case "B":
                break;
        }
    }

    // 群聊天页面
    public static void GroupChat(ChannelHandlerContext ctx) {
        System.out.println("(A) 发起聊天");
        System.out.println("(B) 查看聊天记录");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                break;
            case "B":
                break;
        }
    }

    // 消息管理页面
    public static void MesManagement(ChannelHandlerContext ctx) {
        System.out.println("(A) 未读消息");
        System.out.println("(B) 查看好友请求");
        System.out.println("(C) 查看群通知");
        System.out.println("请输入您的选择:");
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
                System.out.println("您的输入有误！请重新输入：");
                SignOut(ctx);
        }
    }
}