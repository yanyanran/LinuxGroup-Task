package client.function;

import io.netty.channel.ChannelHandlerContext;
import messages.toserver.BlacklistMsg;
import messages.toserver.FriendMsg;
import messages.toserver.FriendApplyMsg;
import messages.toserver.ListMsg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static client.ChatClient.*;
import static client.function.ChatFriendManagePage.FriendsChat;
import static client.function.ChatFriendManagePage.showHistoryMsg;

/**
 * Client Page
 * （A）好友管理页面
 * */
public class FriendManagePage {
    static Scanner input = new Scanner(System.in);
    static String friendName;

    // 都对数据库 friend_list 操作
    public FriendManagePage(ChannelHandlerContext ctx, String me) throws Exception {
        boolean s = true;
        while(s) {
            //System.out.println(me);
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
                    setBlackList(ctx, me);
                    break;
                case "C":
                    //System.out.println("me: " + me);
                    addFriend(ctx, me);
                    break;
                case "D":
                    deleteFriend(ctx, me);
                    break;
                case "E":
                    // return login success main page
                    s = false;
                    break;
            }
        }
    }

    // 查看好友列表 --> show friend_list.type=0
    public void setFriendList(ChannelHandlerContext ctx, String me) throws Exception {
        //System.out.println(me);
        // send to server
        ListMsg msg = new ListMsg(me,1);
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
            userList.clear();  // 清空不影响下一次存储和输出

            System.out.println("您想操作：\n【1】向好友发起会话\n【2】查看好友聊天记录（* 输入除1、2外任意键即可退出当前页面 *） \n请输入您的选择：");
            int i = input.nextInt();
            if(i == 1) {
                FriendsChat(ctx,me);
            }else if(i == 2) {
                showHistoryMsg(ctx,me);
            } else {
                // 这里有问题：可以new 但不可以操作
                new FriendManagePage(ctx, me);
            }
        }else {
            System.out.println("您的好友列表为空！");
            // return friend main page
        }
    }

    // 查看黑名单 --> show friend_list.type=1
    public void setBlackList(ChannelHandlerContext ctx,String me) throws Exception {
        ListMsg msg = new ListMsg(me,2);
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
            System.out.println("以下是您的黑名单好友列表：");
            // 循环输出list内容
            for(int i = 0; i < userList.size(); i++) {
                System.out.println(userList.get(i));
            }
            userList.clear();  // 清空不影响下一次存储和输出

            System.out.println("您想操作：\n【1】 添加黑名单好友\n【2】删除黑名单好友\n* 输入除1、2外任意键即可退出当前页面* \n请输入您的选择：");
            int i = input.nextInt();

            // 添加黑名单好友
            if(i == 1) {
                System.out.println("----* 您无法向黑名单好友发消息 - 也无法接收黑名单好友给你发消息 *----");
                System.out.println("-----------------------* 拉黑请谨慎 *-----------------------");
                System.out.println("请输入您想要拉黑的好友名：");
                String user = input.next();
                System.out.println("是否确定拉黑好友[" + user + "]？\n Y--确定  N--取消\n【请输入您的选择】:");
                switch (user.toUpperCase()) {
                    case "Y":
                        addBlackList(ctx,me,user);
                        break;
                    case "N":
                        new FriendManagePage(ctx, me);
                        break;
                }
            }else // 删除黑名单好友
                if(i ==2) {
                System.out.println("请输入您想要取消黑名单状态的好友名：");
                String user = input.next();
                System.out.println("是否确定取消好友[" + user + "]的黑名单状态？\n Y--确定  N--取消\n【请输入您的选择】:");
                switch (user.toUpperCase()) {
                    case "Y":
                        deleteBlackList(ctx,me,user);
                        break;
                    case "N":
                        new FriendManagePage(ctx, me);
                        break;
                }
            } else {
                new FriendManagePage(ctx, me);
            }
        }else {
            System.out.println("您的黑名单好友列表为空！");
            // return friend main page
            return;
        }
    }

    // 添加黑名单好友
    public void addBlackList(ChannelHandlerContext ctx, String me,String addWho) throws Exception {
        BlacklistMsg msg = new BlacklistMsg(me, addWho, 1);
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
            System.out.println("已成功将好友" + addWho + "拉黑");
            new FriendManagePage(ctx, me);
        }else {
            System.out.print("拉黑失败！\n您是否需要继续此操作？：【Y】继续 【除Y任意键】退出\n请输入您的选择：");
            if(input.next() == "Y") {
                setBlackList(ctx,me);
            }else {
                return;
            }
        }
    }

    // 删除黑名单好友
    public void deleteBlackList(ChannelHandlerContext ctx, String me,String deleteWho) throws Exception {
        BlacklistMsg msg = new BlacklistMsg(me, deleteWho, 2);
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
            System.out.println("已成功将好友" + deleteWho + "解除拉黑");
            new FriendManagePage(ctx, me);
        }else {
            System.out.print("解除拉黑失败！\n您是否需要继续此操作？：【Y】继续 【除Y任意键】退出\n请输入您的选择：");
            if(input.next() == "Y") {
                setBlackList(ctx,me);
            }else {
                return;
            }
        }
    }

    // 添加好友
    public void addFriend(ChannelHandlerContext ctx, String me) {
        System.out.println("-----------* 添加好友 *-------------");
        System.out.println("【请输入您想要添加的好友名】：");
        friendName = input.next();
        System.out.println("是否确定添加[" + friendName + "]为好友？\n Y--确定  N--取消\n【请输入您的选择】:");
        String i = input.next();
        switch (i.toUpperCase()) {
            case "Y":
                FriendMsg msg = new FriendMsg(friendName, me, 0);
                ctx.writeAndFlush(msg);

                // 等待服务端回信
                try {
                    synchronized (waitMessage) {
                        waitMessage.wait();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(waitSuccess == 1) {  // 可添加
                    // 获取发送时间
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");
                    Date date = new Date();
                    String time = sdf.format((date));

                    FriendApplyMsg msg2 = new FriendApplyMsg(me,friendName,time);
                    ctx.writeAndFlush(msg2);
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 发送好友申请成功
                    if(waitSuccess == 1) {
                        System.out.println("等待对方验证...");
                        return;
                    }else {
                        System.out.println("请重新操作！");
                        return;
                    }
                } else {
                    System.out.println("添加操作失败！");
                    return;
                }
            case "N":
                System.out.println("您已取消操作，正在跳转页面...");
                // return previous page
                return;
        }
 }

    // 删除好友
    public void deleteFriend(ChannelHandlerContext ctx, String me) {
        System.out.println("-----------* 删除好友 *-------------");
        System.out.println("【请输入您想要删除的好友名】：");
        friendName = input.next();
        System.out.println("是否确定删除好友[" + friendName + "]？\n Y--确定  N--取消\n【请输入您的选择】:");
        String i = input.next();
        switch (i.toUpperCase()) {
            case "Y":
                FriendMsg msg = new FriendMsg(friendName, me ,1);
                ctx.writeAndFlush(msg);
                try {
                    synchronized (waitMessage) {
                        waitMessage.wait();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(waitSuccess == 1) {
                    System.out.println("Success！");
                    return;
                } else {
                    System.out.println("删除失败！");
                    return;
                }
            case "N":
                System.out.println("您已取消操作，正在跳转页面...");
                // return previous page
                return;
        }
    }
}