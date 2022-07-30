package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ChatHandlerMap;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;
import messages.settoservermsg.FriendRequestsMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 *  申请时设置user1、send、user2
 *  回复时 --> 通过就设置yes、type;拒绝就删除申请时设置的三个值
 * */
public class ApplyConnectSqlHandler extends SimpleChannelInboundHandler<FriendRequestsMsg>{
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendRequestsMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String from = msg.getFromUser();
            String to = msg.getToUser();
            int num = msg.getNum();

            // 申请
            if (num == 0) {
                // 给对方发送好友请求
                Channel channel = ChatHandlerMap.getChannel(to);
                channel.writeAndFlush(msg);

                // 设置数据表 -- friend_list
                String sql = "insert into friend_list(user1,send,user2) values('" + from + "','" + from + "','" + to + "')";
                Statement ps = con.createStatement();
                int rs = ps.executeUpdate(sql);
                if (rs > 0) {
                    // 插入成功
                    System.out.println("数据表插入成功!好友申请发送成功！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"好友申请发送成功！等待对方验证...\n");
                    ctx.writeAndFlush(msg2);
                } else {
                    // 插入失败
                    System.out.println("数据表插入失败!");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false,"好友申请发送失败！");
                    ctx.writeAndFlush(msg2);
                }
            } else // 回复
                if (num == 1) {

                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}