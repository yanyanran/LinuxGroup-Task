package server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import messages.toclient.ServerToClientMsg;
import messages.toserver.RegisterMsg;

import java.sql.*;

public class RegisterConnectSqlHandler extends SimpleChannelInboundHandler<RegisterMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端想要读取数据库因为有用户在有用户在注册帐号...", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterMsg msg) throws Exception {
        try {
            System.out.println("用户尝试注册帐号中...\n");
            ServerToClientMsg setMsg;
            boolean flag = false; // 判断用户名是否存在的flag

            // 连接MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);

            // 获取用户输入的username
            String username = msg.getUsername();
            // 这里需要判断此用户名在数据库中是否有重合项（用户名唯一）
            // 查询
            String name = username;
            java.sql.Statement statement = con.createStatement();
            String ssql = "select username from client";
            ResultSet resultSet = statement.executeQuery(ssql);

            // 遍历
            while (resultSet.next()) {
                String sqlname = resultSet.getString("username");
                // 有重复,将flag设为true
                if (sqlname.equals(name) == true) {
                    flag = true;
                }
            }

            // 1、没有重复项，可注册
            if (flag == false) {
                String p1 = msg.getPassword();
                String p2 = msg.getPassword2();
                // 两次密码输入一样 ，成功注册写入数据库
                if ((p1).equals(p2)) {
                    String password = p1;
                    String sql = "insert into client (username,password,State) values(?,?,0)";
                    PreparedStatement ptmt = con.prepareStatement(sql);
                    ptmt.setString(1, username);
                    ptmt.setString(2, password);
                    ptmt.execute();
                    setMsg = new ServerToClientMsg(true, "用户注册成功！\n");
                    System.out.println(setMsg);
                    ctx.writeAndFlush(setMsg);
                }else {
                    setMsg = new ServerToClientMsg(false, "两次输入的密码不一致，请重新操作!\n");
                    System.out.println("注册中止，因为客户端输入的两次密码不一致！\n");
                    ctx.writeAndFlush(setMsg);
                }
            } else {
                // 2、用户名已存在，重来
                setMsg = new ServerToClientMsg(false, "客户端注册帐号终止，因为帐号名已存在\n");
                System.out.println(setMsg);
                ctx.writeAndFlush(setMsg);
            }
        } finally {
            con.close();
        }
    }
}