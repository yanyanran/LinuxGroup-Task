package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;

import java.sql.*;

public class AddFriendConnectSqlHandler extends SimpleChannelInboundHandler<FriendMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    // add friend
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

            boolean flag = false;
            int flag2 = 0;
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
                    // 给对方发送好友请求
                    // 设置数据表 -- friend_list
                    String sql4 = "insert into friend_list(user1,send,user2) values(?,?,?)";
                    ps = con.prepareStatement(sql4);
                    ps.setString(1, me);
                    ps.setString(2, me);
                    ps.setString(3, friendName);
                    int resultSet2 = ps.executeUpdate();
                    if (resultSet2 > 0) {
                        // 插入成功
                        System.out.println("Success");
                    } else {
                        // 插入失败
                        System.out.println("Failure");
                    }
                    // 发送好友申请
                    // .................................

                    System.out.println("好友申请发送成功！等待对方验证");
                }
            } else {
                ServerToClientMsg setMsg = new ServerToClientMsg(false, "不存在帐号" + friendName + "!");
                System.out.println(setMsg);
                ctx.writeAndFlush(ctx);
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