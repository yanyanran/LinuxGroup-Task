package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.LogoutMsg;

import java.sql.*;

public class LogoutConnectSqlHandler extends SimpleChannelInboundHandler<LogoutMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
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
        // 加一个帐号与密码是否匹配的判断
        String sqlCheck = "select id,username,password,State from client where username=? and password=?";
        PreparedStatement ptmt = con.prepareStatement(sqlCheck);
        ptmt.setString(1, username);
        ptmt.setString(2, password);
        ResultSet rs = ptmt.executeQuery();
        if(rs.next()){  // 密码匹配
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            ps.executeUpdate();

            ServerToClientMsg msg2 = new ServerToClientMsg(true,"帐户已被注销\n");
            System.out.println("客户端身份验证通过，注销成功！\n");
            ctx.writeAndFlush(msg2);
        }else {
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"帐号与密码不匹配！请重试\n");
            System.out.println("客户端身份验证未通过，注销失败！\n");
            ctx.writeAndFlush(msg2);
            // 重来
        }


    }
}