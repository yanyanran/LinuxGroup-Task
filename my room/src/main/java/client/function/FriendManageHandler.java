package client.function;

import client.LoginSuccessHandler;
import io.netty.channel.ChannelHandlerContext;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;

import java.util.Scanner;

import static client.ChatClient.waitMessage;

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
                setFriendList(ctx);
                break;
            case "B":
                setBlackList(ctx);
                break;
            case "C":
                //System.out.println("me: " + me);
                addFriend(ctx,me);
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
    public void setFriendList(ChannelHandlerContext ctx) {

    }

    // 查看黑名单 --> show friend_list.type=1
    public void setBlackList(ChannelHandlerContext ctx) {

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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