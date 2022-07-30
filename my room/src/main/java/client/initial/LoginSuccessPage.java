package client.initial;

import client.function.*;
import io.netty.channel.ChannelHandlerContext;
import messages.toserver.UnreadNumMsg;

import java.util.Scanner;

import static client.ChatClient.*;

/**
 * Client Page
 *  用户主菜单
 *  */
public class LoginSuccessPage {
    static Scanner input = new Scanner(System.in);

    // 登陆完成后显示页面
    public LoginSuccessPage(ChannelHandlerContext ctx, String me) throws Exception {
        while(true) {
            // 显示有几条未读消息
            UnreadNumMsg msg = new UnreadNumMsg(me);
            ctx.writeAndFlush(msg);
            try {
                synchronized (waitMessage) {
                    waitMessage.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(waitSuccess == 1) {
                System.out.println("-------- *>您有"+ unreadNum +"条未读消息 请注意查收<* --------");
            }else {
                System.out.println("-------- *>您没有未读消息<* --------");
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
                    new FriendManagePage(ctx, me);
                    break;
                case "B":
                    new GroupManagePage(ctx, me);
                    break;
                case "C":
                    new ChatFriendManagePage(ctx, me);
                    break;
                case "D":
                    new ChatGroupManagePage(ctx, me);
                    break;
                case "E":
                    new MesManagePage(ctx, me);
                    break;
                case "F":
                    System.out.println("您确定退出登录吗? (Y) 确定 (N) 取消\n请输入：");
                    SignOut(ctx);
                    break;
                default:
                    System.out.println("输入有误!请重新选择：\n");
                    new LoginSuccessPage(ctx, me);
            }
        }
    }

    // 退出登录
    public static void SignOut(ChannelHandlerContext ctx) throws Exception {
        String i = input.nextLine();
        switch (i.toUpperCase()) {
            case "Y":   // state --> 0
                LoginClientPage.setState(ctx);
                break;
            case "N":
                new LoginClientPage(ctx);
            default:
                System.out.println("【您的输入有误！请重新输入】：");
                SignOut(ctx);
        }
    }
}