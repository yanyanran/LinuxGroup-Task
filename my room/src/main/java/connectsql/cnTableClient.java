package connectsql;

import c.login.LoginMainPage;

import java.sql.*;

import static c.login.Login.login;

// 操作数据表client，分为注册、登陆、注销。三种sql语句的组合
public class cnTableClient {
    // 连接client表
    private static String url = "jdbc:mysql://localhost:3306/C hatRoomClient?client=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;

    public static void choose(int num,Connection con, String username, String password) throws Exception {
        switch (num) {
            case 1:
                LoginServer(username,password);
                break;
            case 2:
                RegisterServer(con);
                break;
            case 3:
                LogoutServer(con);
                break;
            default:

        }
    }

    public static void LoginServer(String username, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, pass);

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

    public static void RegisterServer(Connection con) {

    }

    public static void LogoutServer(Connection con) {

    }
}