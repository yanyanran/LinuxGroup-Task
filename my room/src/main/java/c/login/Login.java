package c.login;

import java.sql.*;
import java.util.Scanner;

public class Login {
    private static String username;
    private static String password;
    private static Connection con;
    static Scanner input = new Scanner(System.in);

    // 2 --> 注册
    public static void register() throws Exception {
        System.out.println("请输入您的用户名： ");
        username = input.next();

        // 这里需要判断此用户名在数据库中是否有重合项
        // 查询
        int flag = 0;
        String name = username;
        Statement statement = con.createStatement();
        String ssql = "select username from client";
        ResultSet resultSet = statement.executeQuery(ssql);
        // 遍历
        while(resultSet.next()) {
            String sqlname = resultSet.getString("username");
            // 有重复,将flag设为1
            if(sqlname.equals(name) == true) {
                flag = 1;
            }
        }

        // 1、没有重复项，可注册
        if(flag == 0) {
            System.out.println("请输入您的密码： ");
            String p1 = input.next();
            System.out.println("请再次输入您的密码以确认密码：");
            String p2 = input.next();
            // 两次密码输入一样 ，成功注册写入数据库
            if((p1).equals(p2)) {
                password = p1;
                String sql = "insert into client (username,password) values(?,?)";
                PreparedStatement ptmt = con.prepareStatement(sql);
                ptmt.setString(1, username);
                ptmt.setString(2, password);
                ptmt.execute();
                System.out.println("------注册成功，请登录-------");
                login();
            }
        }else{  // 2、用户名已存在，重来
            System.out.println("错误：用户名已存在");
            register();
        }
    }

    // 1 --> 登陆
    public static void login() throws Exception {
        System.out.println("请输入您的姓名： ");
        username = input.next();
        System.out.println("请输入您的密码： ");
        password = input.next();

        // 调用


    }

    // 3 --> 注销
    public static void logout() throws Exception {
        System.out.println("请输入您要注销的账号名称：");
        username = input.next();
        System.out.println("请输入密码： ");
        password = input.next();

        Statement stmt = con.createStatement();
        String sql = "delete from client where username=? and password=?";
        System.out.println("您确定要注销此帐户吗？\n 确定注销：输入1 ---- 不注销了：输入2");
        int i = input.nextInt();
        switch (i) {
            case 1:
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,username);
                ps.setString(2,password);
                ps.executeUpdate();
                System.out.println("-------帐户已被注销--------");
                break;
            case 2:
                System.out.println("-------您选择保留您的帐户--------");
                break;
            default:
                System.out.println("!!!错误输入!!!");
                System.exit(0);
        }
    }

    // 主页面
    public static void homePage() throws Exception {
        System.out.println("---- Welcome to MyChatRoom -----");
        System.out.println("请选择：\n 1:用户登录\n 2：用户注册\n 3：注销用户\n 4：退出");
        System.out.println("--------------------------------");

        int i = input.nextInt();

        // setMsqlToLogin();
        // LoginServer(con);  // 发给服务端

        switch (i) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                logout();
                homePage();
                break;
            case 4:
                System.out.println("~ 886 ~");
                System.exit(0);
            default:
                System.out.println("错误输入!请输入正确的选项");
                homePage();
        }
    }

    // 离线：state --> 0
    public static void setState() throws Exception {
        String sql = "update client set State=0 where username='"+ username +"'";
        PreparedStatement ptmt = con.prepareStatement(sql);
        ptmt.executeUpdate(sql);
    }
}