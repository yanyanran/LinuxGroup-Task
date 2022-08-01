package client.function;

import io.netty.channel.ChannelHandlerContext;
import messages.toserver.AddGroupMsg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;

/**
 * Client Page
 *  (B)聊天群管理页面
 * */
public class GroupManagePage {
    static Scanner input = new Scanner(System.in);

    // 都对数据库 group_list 操作
    public GroupManagePage(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            System.out.println("(A) 查看我的群列表");
            System.out.println("(B) 创建新的群聊");
            System.out.println("(C) 申请加入群聊");
            System.out.println("(D) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    setJoinGroups(ctx, me);
                    break;
                case "B":
                    createNewGroup(ctx, me);
                    break;
                case "C":
                    applicationToGroup(ctx, me);
                    break;
                case "D":
                    // return login success main page
                    s = false;
                    break;
            }
        }
    }

    // (A)查看我的群列表
    /** 列出群列表时格式为：id+群名 */
    public static void setJoinGroups(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            System.out.println("(A) 我加入的群");
            System.out.println("(B) 我创建的群");
            System.out.println("(C) 我管理的群");
            System.out.println("(D) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    MyJoinGroup(ctx, me);
                    break;
                case "B":
                    MyCreateGroup(ctx, me);
                    break;
                case "C":
                    MyManageGroup(ctx, me);
                    break;
                case "D":
                    s = false;
                    break;
            }
        }
    }

    /**
     * (A) 我加入的群
     * group_list---type:1 群众
     * */
    public static void MyJoinGroup(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            // 开头列出我加入的群列表
            // ....

            System.out.println("(A) 查看群组成员");
            System.out.println("(B) 退出群聊");
            System.out.println("(C) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    CheckGroupMembers(ctx, me, 1);
                    break;
                case "B":
                    exitGroup(ctx, me, 1);
                    break;
                case "C":
                    s = false;
                    break;
            }
        }
    }

    /**
     * (B) 我创建的群
     * group_list---type:0 群主
     * */
    public static void MyCreateGroup(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            // 开头列出我创建的群列表
            // ....

            System.out.println("(A) 查看群组成员");
            System.out.println("(B) 添加群管理员");
            System.out.println("(C) 删除群管理员");
            System.out.println("(D) 删除群成员");
            System.out.println("(E) 解散群聊");
            System.out.println("(F) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    CheckGroupMembers(ctx, me, 0);
                    break;
                case "B":
                    AddGroupManager(ctx, me);
                    break;
                case "C":
                    DeleteGroupManager(ctx, me);
                    break;
                case "D":
                    DeleteGroupManager(ctx, me, 0);
                    break;
                case "E":
                    DisbandGroup(ctx, me);
                    break;
                case "F":
                    s = false;
                    break;
            }
        }
    }

    // 添加群管理员
    public static void AddGroupManager(ChannelHandlerContext ctx, String me) {

    }

    // 删除群管理员
    public static void DeleteGroupManager(ChannelHandlerContext ctx, String me) {

    }

    // 解散群聊
    public static void DisbandGroup(ChannelHandlerContext ctx, String me) {

    }

    /**
     * (C) 我管理的群
     * group_list---type:2 管理员
     * */
    public static void MyManageGroup(ChannelHandlerContext ctx, String me) {
        boolean s = true;
        while (s) {
            // 开头列出我管理的群列表
            // ....

            System.out.println("(A) 查看群组成员");
            System.out.println("(B) 删除群成员");
            System.out.println("(C) 退出群聊");
            System.out.println("(D) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    CheckGroupMembers(ctx, me, 2);
                    break;
                case "B":
                    DeleteGroupManager(ctx, me, 2);
                    break;
                case "C":
                    exitGroup(ctx, me, 2);
                    break;
                case "D":
                    s = false;
                    break;
            }
        }
    }


    // (B)创建新的群聊
    public static void createNewGroup(ChannelHandlerContext ctx, String me) {
        // 允许创建同名的群名，靠群ID区分

    }

    // (C)申请加入群聊
    public static void applicationToGroup(ChannelHandlerContext ctx, String me) {
        System.out.println("请输入您想加入的群ID号:");
        int id = input.nextInt();
        System.out.println("您确定申请加入此群吗？（Y--确定 N--取消）：");
        String i = input.next();
        if(i.equals("Y")) {
            // 获取发送时间
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");
            Date date = new Date();
            String time = sdf.format((date));

            // id传给服务端
            AddGroupMsg msg = new AddGroupMsg(me, id, time);
            ctx.writeAndFlush(msg);
            try {
                synchronized (waitMessage) {
                    waitMessage.wait();
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(waitSuccess == 1) {
                System.out.println("等待群管理员处理申请");
            } else {
                System.out.println("没有找到该群！");
            }
            return;
        } else if(i.equals("N")) {
            System.out.println("您已取消申请!");
            return;
        } else {
            System.out.println("您的输入有误！请重新操作！");
            return;
        }
    }

    /**
     *  查看群成员
     * 三个身份的查看群成员共用一个方法
     * type -- 0群主、1群众、2管理员
     */
    public static void CheckGroupMembers(ChannelHandlerContext ctx, String me, int type) {
        if (type == 0) { // 群主

        } else if(type == 1) { // 群众

        } else if(type ==2) { // 管理员

        }
    }

    /**
     * 删除群成员
     * 两个身份的删除群成员共用一个方法
     * type -- 0群主、2管理员
     * */
    public static void DeleteGroupManager(ChannelHandlerContext ctx, String me, int type) {
        if(type == 0) { // 群主

        } else if(type == 2) { // 管理员

        }
    }

    /**
     * 退出群聊
     * 两个身份的删除群成员共用一个方法
     * type -- 1群众、2管理员
     * */
    public static void exitGroup(ChannelHandlerContext ctx, String me, int type) {
        if(type == 1) { // 群众

        } else if(type == 2) { // 管理员

        }
    }
}