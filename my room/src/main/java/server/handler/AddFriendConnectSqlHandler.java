package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ChatHandlerMap;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;

import java.sql.*;

/**
 * 连接数据库
 * 添加(0)、删除(1)好友Handler
 * */
public class AddFriendConnectSqlHandler extends SimpleChannelInboundHandler<FriendMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendMsg msg) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        Statement stm = null;
        Statement stm2 = null;
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);

            String friendName = msg.getFriendName();
            String me = msg.getMe();
            int num = msg.getNum();
            boolean flag = false;
            int flag2 = 0;

            // add friend
            if(num == 0) {
                System.out.println("用户" + me + "正在申请添加帐号【" + friendName + "】为好友....");

                // 1、判断client中是否存在此帐号(flag)
                System.out.println("正在查询是否存在此帐号...");
                // 查询
                stm = con.createStatement();
                String sql = "select username from client";
                ResultSet resultSet = stm.executeQuery(sql);

                // 遍历
                while (resultSet.next()) {
                    String name = resultSet.getString("username");
                    // 帐号存在,flag设为true
                    if (name.equals(friendName) == true) {
                        flag = true;
                    }
                }

                // 帐号存在
                if (flag == true) {
                    System.out.println("帐号【 " + friendName + " 】存在");

                    // 2、再判断此人是否在自己的好友列表中(flag2) --- 查friend_list
                    System.out.println("正在判断此帐号是否已是用户好友...");
                    // 查询
                    stm2 = con.createStatement();
                    String sql2 = "select type from firend_list where user1='" + friendName + "'and user2='" + me + "'";
                    ResultSet rs = stm2.executeQuery(sql2);
                    String sql3 = "select type from firend_list where user1='" + me + "'and user2='" + friendName + "'";
                    ResultSet rs2 = stm2.executeQuery(sql3);

                    // 遍历
                    while (rs.next()) {
                        flag2++;
                    }
                    while (rs2.next()) {
                        flag2++;
                    }
                    System.out.println("flag2: " + flag2);

                    if (flag2 != 0) {
                        // 已经是自己的好友
                        System.out.println("已经是好友，无需添加！");
                        ServerToClientMsg msg2 = new ServerToClientMsg(false, "用户添加失败，因为已经是好友!");
                        ctx.writeAndFlush(msg2);
                    } else {
                        System.out.println("不是好友 转战添加好友...");
                        ServerToClientMsg msg2 = new ServerToClientMsg(true,"");
                        ctx.writeAndFlush(msg2);
                    }
                } else {  // 帐号不存在
                    System.out.println("添加失败，不存在此帐号！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "用户添加失败，因为不存在此帐号" + friendName + "!");
                    ctx.writeAndFlush(msg2);
                }
            } else if(num == 1) {  // delete friend
                System.out.println("用户" + me + "正在申请删除好友【" + friendName + "】....");
                System.out.println("正在判断此帐号是否是用户好友...");
                // 查询
                stm = con.createStatement();
                String sql = "select type from firend_list where user1='" + friendName + "'and user2='" + me + "'";
                ResultSet rs = stm.executeQuery(sql);
                String sql2 = "select type from firend_list where user1='" + me + "'and user2='" + friendName + "'";
                ResultSet rs2 = stm.executeQuery(sql2);

                // 遍历
                while (rs.next()) {
                    flag2++;
                }
                while (rs2.next()) {
                    flag2++;
                }
                //System.out.println("flag2: " + flag2);

                if (flag2 != 0) {
                    // 是好友
                    System.out.println("构成好友关系！正在删除中...");
                    String sql3 = "delete from friend_list where (user1='" + friendName + "'and user2='" + me + "') or (user1='" + me + "'and user2='" + friendName + "')";
                    stm.executeQuery(sql3);

                    System.out.println("操作成功！已成功删除好友!" + friendName);
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, "已删除该好友");
                    ctx.writeAndFlush(msg2);
                } else {
                    System.out.println("操作失败！此人不是用户"+ me +"的好友，用户无权删除！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "此人不是您的好友，无权删除！");
                    ctx.writeAndFlush(msg2);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }}
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}
            if (stm2 != null) {
                try {
                    stm2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}
        }
    }
}