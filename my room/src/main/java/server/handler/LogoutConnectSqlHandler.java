package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import messages.UserMessage;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.LogoutMsg;

import java.sql.*;
import java.util.Scanner;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;

public class LogoutConnectSqlHandler extends SimpleChannelInboundHandler<LogoutMsg> {
    private static String url = "jdbc:mysql://localhost:3306/C hatRoomClient?client=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端想要读取数据库因为有用户在有用户在注销帐号...", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutMsg msg) throws Exception {
        // 连接MySQL
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pass);

        String username = msg.getUsername();
        String password = msg.getPassword();
        System.out.println("客户端申请注销用户: " + username);

        Statement stmt = con.createStatement();
        String sql = "delete from client where username=? and password=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,username);
        ps.setString(2,password);
        ps.executeUpdate();

        // 可能还要加一个帐号与密码是否匹配的判断

        ServerToClientMsg msg2 = new ServerToClientMsg(true,"-------帐户已被注销--------");
        msg2.setMessageType(UserMessage.logoutmsg);
        ctx.writeAndFlush(msg2);
    }
}