package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;
import messages.settoservermsg.ListMsg;

import java.sql.*;
import java.util.ArrayList;

/**
 * 连接数据库
 * 列出好友列表和黑名单好友Handler
 * */
// 共享FriendMsg试试 --> 不可以共享
public class FriendListConnectSqlHandler extends SimpleChannelInboundHandler<ListMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // ArrayList用来存好友列表
    public static ArrayList<String> list = new ArrayList<>();  // 暂存名单

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ListMsg msg) throws Exception {
        int flag = 0;  // 判断有没有好友
        String me = msg.getMe();
        //System.out.println(me);
        int num = msg.getNum();

        // 查好友列表
        if(num == 1) {
            System.out.println("查询用户"+ me +"的好友中...");
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);

            // 查询 friend_list --> type:0
            String sql = "select user2 from friend_list where user1=send and user2=yes and type=0 and user1='" + me + "'";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                flag = 1;
                String friend = rs.getString("user2");
//                // 遍历list看是否有一样的，不一样就添加
//                for(int i = 0; i < list.size(); i++) {
//                    if(list.get(i) != friend) {
//                        list.add(friend);
//                    }
//                }
                list.add(friend);
            }
            String sql2 = "select user1 from friend_list where user1=send and user2=yes and type=0 and user2='" + me + "'";
            Statement stm2 = con.createStatement();
            ResultSet rs2 = stm2.executeQuery(sql2);
            while (rs2.next()) {
                flag = 1;
                String friend = rs.getString("user1");
                // 遍历list看是否有一样的，不一样就添加
//                for(int i = 0; i < list.size(); i++) {
//                    if(list.get(i) != friend) {
//                        list.add(friend);
//                    }
//                }
                list.add(friend);
            }
            System.out.println("数据库查找好友完毕...");

            if(flag == 0) {
                System.out.println("用户["+ me +"]的好友列表是空的！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "无好友!");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println("展示用户" + me +"的好友：");
                for(int i = 0; i < list.size(); i++) {
                    System.out.println(list.get(i));
                }
                //  把list传给客户端
                ServerToClientMsg msg2 = new ServerToClientMsg(true, list);
                ctx.writeAndFlush(msg2);
                list.clear();   // list清空即可
            }
        } else // 查看黑名单列表
            if (num == 2) {
                System.out.println("查询用户"+ me +"的黑名单好友中...");
                Class.forName(JDBC_DRIVER);
                Connection con = DriverManager.getConnection(url, user, pass);

                // 查询 friend_list --> type:0
                String sql = "select user2 from friend_list where user1=send and user2=yes and type=1 and user1='" + me + "'";
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery(sql);
                while (rs.next()) {
                    flag = 1;
                    String friend = rs.getString("user2");
                    // 遍历list看是否有一样的，不一样就添加
//                    for(int i = 0; i < list.size(); i++) {
//                        if(list.get(i) != friend) {
//                            list.add(friend);
//                        }
//                    }
                    list.add(friend);
                }
                String sql2 = "select user1 from friend_list where user1=send and user2=yes and type=1 and user2='" + me + "'";
                Statement stm2 = con.createStatement();
                ResultSet rs2 = stm2.executeQuery(sql2);
                while (rs2.next()) {
                    flag = 1;
                    String friend = rs.getString("user1");
                    // 遍历list看是否有一样的，不一样就添加
//                    for(int i = 0; i < list.size(); i++) {
//                        if(list.get(i) != friend) {
//                            list.add(friend);
//                        }
//                    }
                    list.add(friend);
                }

                System.out.println("数据库查找黑名单好友完毕...");
                if(flag == 0) {
                    System.out.println("用户["+ me +"]的黑名单好友列表是空的！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "无黑名单好友!");
                    ctx.writeAndFlush(msg2);
                }else {
                    System.out.println("展示用户" + me +"的黑名单好友：");
                    for(int i = 0; i < list.size(); i++) {
                        System.out.println(list.get(i));
                    }
                    //  把list传给客户端
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, list);
                    ctx.writeAndFlush(msg2);
                    list.clear();   // list清空
                }
            }
    }
}