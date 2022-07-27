package client.function;

import client.LoginSuccessHandler;
import io.netty.channel.ChannelHandlerContext;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;

import java.util.ArrayList;
import java.util.Scanner;

import static client.ChatClient.*;
import static client.function.ChatManageHandler.FriendsChat;
import static client.function.ChatManageHandler.showHistoryMsg;

/**
 * （A）好友管理页面
 * */
public class FriendManageHandler {
    static Scanner input = new Scanner(System.in);
    static String friendName;

    // 都对数据库 friend_list 操作
    public FriendManageHandler(ChannelHandlerContext ctx, String me) throws Exception {
        System.out.println("(A) 查看好友列表");
        System.out.println("(B) 查看黑名单");
        System.out.println("(C) 添加好友");
        System.out.println("(D) 删除好友");
        System.out.println("(E) 返回");
        System.out.println("【请输入您的选择】:");
        String i = input.nextLine();

        switch (i.toUpperCase()) {
            case "A":
                setFriendList(ctx, me);
                break;
            case "B":
                setBlackList(ctx,me);
                break;
            case "C":
                //System.out.println("me: " + me);
                addFriend(ctx, me);
                break;
            case "D":
                deleteFriend(ctx);
                break;
            case "E":
                // return login success main page
                new LoginSuccessHandler(ctx,me);
                break;
        }
    }

    // 查看好友列表 --> show friend_list.type=0
    public void setFriendList(ChannelHandlerContext ctx, String me) throws Exception {
        // send to server
        FriendMsg msg = new FriendMsg(me,1);
        ctx.writeAndFlush(msg);

        // lock and wait
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1) {
            System.out.println("以下是您的好友列表：");
            // 循环输出list内容
            for(int i = 0; i < userList.size(); i++) {
                System.out.println(userList.get(i));
            }
            System.out.println("您想操作：\n【1】向好友发起会话\n【2】查看好友聊天记录\n* 输入除1、2外任意键即可退出当前页面* \n请输入您的选择：");
            int i = input.nextInt();
            if(i == 1) {
                FriendsChat(ctx,me);
            }else if(i ==2) {
                showHistoryMsg(ctx,me);
            } else {
                new FriendManageHandler(ctx, me);
            }
        }else {
            System.out.println("您的好友列表为空！");
            // return friend main page
            new FriendManageHandler(ctx, me);
        }
    }

    // 查看黑名单 --> show friend_list.type=1
    public void setBlackList(ChannelHandlerContext ctx,String me) throws Exception {
        FriendMsg msg = new FriendMsg(me,2);
        ctx.writeAndFlush(msg);

        // lock and wait
        try {
            synchronized (waitMessage) {
                waitMessage.wait();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(waitSuccess == 1) {
            System.out.println("----* 您无法向黑名单好友发消息 - 也无法接收黑名单好友给你发消息 *----");
            System.out.println("-----------------------* 拉黑请谨慎 *-----------------------");
            System.out.println("以下是您的黑名单好友列表：");
            // 循环输出list内容
            for(int i = 0; i < userList.size(); i++) {
                System.out.println(userList.get(i));
            }
            System.out.println("您想操作：\n【1】 添加黑名单好友\n【2】删除黑名单好友\n* 输入除1、2外任意键即可退出当前页面* \n请输入您的选择：");
            int i = input.nextInt();
            if(i == 1) {

            }else if(i ==2) {

            } else {
                new FriendManageHandler(ctx, me);
            }
        }else {
            System.out.println("您的黑名单好友列表为空！");
            // return friend main page
            new FriendManageHandler(ctx, me);
        }

    }

    // 添加好友
    public void addFriend(ChannelHandlerContext ctx, String me) {
        //
        System.out.println("-----------* 添加好友 *-------------");
        System.out.println("【请输入您想要添加的好友名字】：");
        friendName = input.next();
        System.out.println("是否确定添加[" + friendName + "]为好友？\n Y--确定  N--取消\n【请输入您的选择】:");
        String i = input.next();
        switch (i.toUpperCase()) {
            case "Y":
                FriendMsg msg = new FriendMsg(friendName, me);
                ctx.writeAndFlush(msg);

                // 等待服务端回信
                try {
                    synchronized (waitMessage) {
                        waitMessage.wait();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // ......
                break;
            case "N":
                System.out.println("【您已取消操作】\n");
                break;
        }
 }

    // 删除好友
    public void deleteFriend(ChannelHandlerContext ctx) {

    }
}