package client;

import io.netty.channel.ChannelHandlerContext;
import messages.settoservermsg.LoginMsg;
import messages.settoservermsg.LogoutMsg;
import messages.settoservermsg.OfflineMsg;
import messages.settoservermsg.RegisterMsg;
import server.handler.LoginConnectSqlHandler;

import java.util.Scanner;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;

public class LoginClientHandler {
    private static String username;
    private static String password;
    private static String password2;
    static Scanner input = new Scanner(System.in);

    // home page
    public LoginClientHandler(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---- Welcome to MyChatRoom -----");
        System.out.println("请选择：\n 1:用户登录\n 2：用户注册\n 3：注销用户\n 4：退出");
        System.out.println("--------------------------------");
        int i = input.nextInt();

        switch (i) {
            case 1:
                login(ctx);
                break;
            case 2:
                register(ctx);
                break;
            case 3:
                logout(ctx);
                // return home page
                new LoginClientHandler(ctx);
                break;
            case 4:
                System.out.println("~ 886 ~");
                System.exit(0);
            default:
                System.out.println("错误输入!请输入正确的选项");
                // return home page
                new LoginClientHandler(ctx);
        }
    }

    // 1 --> 登陆
    public static void login(ChannelHandlerContext ctx) throws Exception {
        System.out.println("请输入您的姓名： ");
        username = input.next();
        System.out.println("请输入您的密码： ");
        password = input.next();

        // handler查询密码和帐号名是否对应
        LoginMsg msg = new LoginMsg(username,password);
        ctx.writeAndFlush(msg);

        // lock --> 服务端返回消息后继续
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1) {
            new LoginSuccessHandler(ctx,username);
        }else {
            login(ctx);
        }
    }

    // 2 --> 注册
    public void register(ChannelHandlerContext ctx) throws Exception {
        System.out.println("请输入您的姓名： ");
        username = input.next();
        System.out.println("请输入您的密码： ");
        password = input.next();
        System.out.println("请再次输入您的密码以确认密码：");
        password2 = input.next();

        // 客户端将这些消息发过去
        RegisterMsg msg = new RegisterMsg(username, password, password2);
        ctx.writeAndFlush(msg);

        // lock
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1) {
            System.out.println("------注册成功，请登录-------\n");
            new LoginClientHandler(ctx);
        }else {
            System.out.println("您是否还要继续注册帐号？  1----是   2----退出\n");
            int i = input.nextInt();
            switch (i) {
                case 1:
                    register(ctx);
                case 2:
                    new LoginClientHandler(ctx);
            }
        }
    }

    // 3 --> 注销
    public static void logout(ChannelHandlerContext ctx) throws Exception {
        System.out.println("请输入您要注销的账号名称：");
        username = input.next();
        System.out.println("请输入密码： ");
        password = input.next();

        System.out.println("您确定要注销此帐户吗？\n 确定注销：输入1 ---- 不注销了：输入2");
        int i = input.nextInt();
        switch (i) {
            case 1:
                LogoutMsg msg = new LogoutMsg(username, password);
                ctx.writeAndFlush(msg);

                // lock
                try {
                    synchronized (waitMessage) {
                        waitMessage.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(waitSuccess == 1) {
                    System.out.println("-------帐户已被注销--------");
                    // new login ######
                    new LoginClientHandler(ctx);
                }else {
                    System.out.println("您是否还要继续注销操作？ 1----继续 2----退出\n");
                    int j = input.nextInt();
                    switch (j) {
                        case 1:
                            logout(ctx);
                        case 2:
                            new LoginClientHandler(ctx);
                    }
                }
                break;
            case 2:
                System.out.println("-------您选择保留您的帐户--------");
                new LoginClientHandler(ctx);
                break;
            default:
                System.out.println("!!!错误输入!!!");
                System.exit(0);
        }
    }

    // 离线：state --> 0
    public static void setState(ChannelHandlerContext ctx) throws Exception {
        OfflineMsg msg = new OfflineMsg(username);
        System.out.println("setStateMsg: " + msg);
        ctx.writeAndFlush(msg);

        // lock
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1) {
            System.out.println("-------您已退出登陆--------");
            new LoginClientHandler(ctx);
            login(ctx);
        }
    }
}