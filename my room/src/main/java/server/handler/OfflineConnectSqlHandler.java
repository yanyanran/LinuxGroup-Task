package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.OfflineMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

// MySQL离线设置状态为0
public class OfflineConnectSqlHandler extends SimpleChannelInboundHandler<OfflineMsg> {
    private static String url = "jdbc:mysql://localhost:3306/C hatRoomClient?client=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, OfflineMsg msg) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pass);

        String username = msg.getUsername();
        System.out.println("用户离线，申请将用户"+ username +"的state设置为0");

        String sql = "update client set State=0 where username='"+ username +"'";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.executeUpdate(sql);

        System.out.println("设置成功");
        ServerToClientMsg msg2 = new ServerToClientMsg(true, "已退出登陆");
        ctx.writeAndFlush(msg2);
    }
}