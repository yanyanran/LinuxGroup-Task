package client.function;

import client.LoginSuccessPage;
import io.netty.channel.ChannelHandlerContext;

import java.util.Scanner;

/**
 * Client Page
 *  (B)聊天群管理页面
 * */
public class GroupManagePage {
    static Scanner input = new Scanner(System.in);
    // 都对数据库 group_list 操作
    public GroupManagePage(ChannelHandlerContext ctx, String me) throws Exception {
        boolean s = true;
        while (s) {
            System.out.println("(A) 查看我加入的群列表");  // 我加入的群、我创建的群、我管理的群
            System.out.println("(B) 创建新的群聊");
            System.out.println("(C) 申请加入群聊");
            System.out.println("(D) 退出群聊");
            System.out.println("(E) 返回");
            System.out.println("请输入您的选择:");
            String i = input.nextLine();

            switch (i.toUpperCase()) {
                case "A":
                    setJoinGroups(ctx);
                    break;
                case "B":
                    createNewGroup(ctx);
                    break;
                case "C":
                    applicationToGroup(ctx);
                    break;
                case "D":
                    exitGroup(ctx);
                    break;
                case "E":
                    // return login success main page
                    s = false;
                    break;
            }
        }
    }

    // 查看我加入的群列表
    public static void setJoinGroups(ChannelHandlerContext ctx) {

    }

    // 创建新的群聊
    public static void createNewGroup(ChannelHandlerContext ctx) {

    }

    // 申请加入群聊
    public static void applicationToGroup(ChannelHandlerContext ctx) {

    }

    // 退出群聊
    public static void exitGroup(ChannelHandlerContext ctx) {

    }
}