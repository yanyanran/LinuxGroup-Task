package server.handler;

import client.LoginClientHandler;
import client.LoginSuccessHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.LoginMsg;
import messages.settoservermsg.RegisterMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginConnectSqlHandler extends SimpleChannelInboundHandler<LoginMsg> {
    private static String url = "jdbc:mysql://localhost:8000/C hatRoomClient?client=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端想要读取数据库因为有用户在登陆帐号...", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMsg msg) throws Exception {
        System.out.println(msg);
        //System.out.println("11111111111111111");

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url,user,pass);

        String username = msg.getUsername();
        String password = msg.getPassword();

        String sql = "select id,username,password,State from client where username=? and password=?";
        PreparedStatement ptmt = con.prepareStatement(sql);

        // 判断用户是否处于登陆状态，避免不同客户端重复登陆同个帐号
        String sql2 = "select State from client where username='"+ username +"'";
        ResultSet m = ptmt.executeQuery(sql2);

        while(m.next()) {
            int state = m.getInt("State");
            // state判断---用户是否在线（不能同时登陆相同帐号）
            if(state == 1) {
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "登陆失败，此用户正处于登陆状态！请重新登陆");
                System.out.println(msg2);
                ctx.writeAndFlush(msg2);
                // login(ctx);
            } else {
                ptmt.setString(1, username);
                ptmt.setString(2, password);
                ResultSet rs = ptmt.executeQuery();
                if(rs.next()){
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, "-------账号登录成功--------");
                    System.out.println(msg2);
                    ctx.writeAndFlush(msg2);

                    // 登陆之后state立刻设为1，表示在线状态 state --> 1
                    String sql3 = "update client set State=1 where username='"+ username +"'";
                    ptmt.executeUpdate(sql3);

                    // 登陆完成 显示主页面
                    new LoginClientHandler(ctx);
                }else{
                    ServerToClientMsg msg2 = new ServerToClientMsg(false,"-------名称或密码错误！---------\\n\" + \"请重新登录:");
                    System.out.println(msg2);
                    ctx.writeAndFlush(msg2);
                }
            }
        }
    }

}