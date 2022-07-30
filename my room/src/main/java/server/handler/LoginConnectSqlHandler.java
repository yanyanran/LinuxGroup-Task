package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import messages.settoclientmsg.ChatHandlerMap;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.LoginMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginConnectSqlHandler extends SimpleChannelInboundHandler<LoginMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端想要读取数据库因为有用户在登陆帐号...", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMsg msg) throws Exception {
        System.out.println(msg);

        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(url,user,pass);

        String username = msg.getUsername();
        String password = msg.getPassword();

        String sql = "select id,username,password,State from client where username='"+ username +"' and password='"+ password +"'";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ResultSet m = ptmt.executeQuery(sql);

        if (m.next()) {
            int state = m.getInt("State");
            // 判断用户是否处于登陆状态，避免不同客户端重复登陆同个帐号
            // state判断---用户是否在线
            if(state == 1) {
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "登陆失败，此用户正处于登陆状态！请重新登陆");
                System.out.println(msg2);
                ctx.writeAndFlush(msg2);
                // login(ctx);
            } else {
                    // 登陆成功把username传过去
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, "------登陆成功-------",username);
                    System.out.println(msg2);
                    ctx.writeAndFlush(msg2);

                    // 通信channel建立起来
                    ChatHandlerMap.add(username,ctx.channel());
                    // 登陆之后state立刻设为1，表示在线状态 state --> 1
                    String sql3 = "update client set State=1 where username='"+ username +"'";
                    ptmt.executeUpdate(sql3);

                    // 登陆完成
                    System.out.println(" Server: 帐号" +  username +"上线");
            }
        }else {
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"-------名称或密码错误！---------\n" + "请重新登录:");
            System.out.println(msg2);
            ctx.writeAndFlush(msg2);
        }
    }
}