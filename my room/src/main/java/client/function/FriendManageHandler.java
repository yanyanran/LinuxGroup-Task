package client.function;

import client.LoginSuccessHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Scanner;

/**
 * （A）好友管理页面
 * */
public class FriendManageHandler {
    static Scanner input = new Scanner(System.in);
    // 都对数据库 friend_list 操作
    public FriendManageHandler(ChannelHandlerContext ctx) throws Exception {
        System.out.println("(A) 查看好友列表");
        System.out.println("(B) 查看黑名单");
        System.out.println("(C) 添加好友");
        System.out.println("(D) 删除好友");
        System.out.println("(E) 返回");
        System.out.println("请输入您的选择:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                setFriendList(ctx);
                break;
            case "B":
                setBlackList(ctx);
                break;
            case "C":
                addFriend(ctx);
                break;
            case "D":
                deleteFriend(ctx);
                break;
            case "E":
                // return login success main page
                new LoginSuccessHandler(ctx);
                break;
        }
    }

    // 查看好友列表 --> show friend_list.type=0
    public static void setFriendList(ChannelHandlerContext ctx) {

    }

    // 查看黑名单 --> show friend_list.type=1
    public static void setBlackList(ChannelHandlerContext ctx) {

    }

    // 添加好友
    public static void addFriend(ChannelHandlerContext ctx) {

    }

    // 删除好友
    public static void deleteFriend(ChannelHandlerContext ctx) {

    }
}