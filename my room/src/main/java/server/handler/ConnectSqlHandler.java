package server.handler;

import c.login.LoginMainPage;
import com.mysql.cj.xdevapi.Statement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoservermsg.RegisterMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static c.login.Login.login;

public class ConnectSqlHandler extends SimpleChannelInboundHandler<RegisterMsg> {
    private static String url = "jdbc:mysql://localhost:3306/C hatRoomClient?client=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;
    //传输器
    static Statement stat = null;
    //sql语句的执行结果
    static ResultSet rs = null;
    //记录语句的输入
    static PreparedStatement ps =null;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterMsg msg) throws Exception {
        System.out.println(msg);

        String username = msg.getUsername();
        String password = msg.getPassword();

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, pass);
        boolean flag;

        String sql = "select id,username,password,State from client where username=? and password=?";
        PreparedStatement ptmt = con.prepareStatement(sql);
        // 判断用户是否处于登陆状态，避免不同客户端重复登陆同个帐号
        String sql2 = "select State from client where username='"+ username +"'";
        ResultSet m = ptmt.executeQuery(sql2);
        while(m.next()) {
            int state = m.getInt("State");  // state判断
            if(state == 1) {
                System.out.println("登陆失败，此用户正处于登陆状态！请重新登陆");
                login();
            } else {
                ptmt.setString(1, username);
                ptmt.setString(2, password);
                ResultSet rs = ptmt.executeQuery();
                if(rs.next()){
                    System.out.println("-------账号登录成功--------");

                    // 登陆之后state立刻设为1，表示在线状态 state --> 1
                    String sql3 = "update client set State=1 where username='"+ username +"'";
                    ptmt.executeUpdate(sql3);

                    // 登陆完成 显示主页面
                    LoginMainPage.LoginPage();
                }else{
                    System.out.println("-------名称或密码错误！---------\n" + "请重新登录:");
                    login();
                }
            }
        }

        
    }
}