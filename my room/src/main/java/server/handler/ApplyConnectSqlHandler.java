package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.FriendApplyMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 好友申请(申请时)
 * 设置user1、send、user2
 * */
public class ApplyConnectSqlHandler extends SimpleChannelInboundHandler<FriendApplyMsg>{
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendApplyMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String from = msg.getFromUser();
            String to = msg.getToUser();

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
                ServerToClientMsg msg2 = new ServerToClientMsg(true,"好友申请发送成功！");
                ctx.writeAndFlush(msg2);
            } else {
                // 插入失败
                System.out.println("数据表插入失败!");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"好友申请发送失败！");
                ctx.writeAndFlush(msg2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}