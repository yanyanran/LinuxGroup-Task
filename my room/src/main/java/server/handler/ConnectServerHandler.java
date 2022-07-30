package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toserver.OfflineMsg;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static client.ChatClient.waitMessage;

/**
 * 聊天室业务处理类
 */
public class ConnectServerHandler extends SimpleChannelInboundHandler<String> {
    public static List<Channel> channelList = new ArrayList<>();
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static String user = "root";
    private static String pass = "123456";
    private static Connection con;
    static {
        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通道就绪事件 --channel在线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //当有新的客户端连接的时候, 将通道放入集合
        channelList.add(channel);

        SimpleDateFormat sdf = new SimpleDateFormat();  // 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");  // a为am/pm的标记
        Date date = new Date(); // 获取当前时间

        //new ServerThread(channel,"线程").start();
        System.out.println("[Server]: " + "客户端[" + channel.remoteAddress().toString().substring(1) + "]" + " 在 " + sdf.format((date)) + "客户端连接成功.");

    }

    /**
     * 通道未就绪--channel下线
     * 当有客户端断开连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        /**直接关闭程序退出也要让state变为0*/
        // 获取当前user
        String userName = ChatHandlerMap.getUser(channel);
        System.out.println("用户名：" + userName);  // ！！！！！有问题 无法获取username（null）！！！！！！

        // connect mysql State --> 0
        Class.forName("com.mysql.cj.jdbc.Driver");
        String sql = "update client set State=0 where username='"+ userName +"'";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(sql);

        // 移除对应的通道
        channelList.remove(channel);

        SimpleDateFormat sdf = new SimpleDateFormat();  // 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");  // a为am/pm的标记
        Date date = new Date(); // 获取当前时间

        System.out.println("[Server]: " + "客户端[" + channel.remoteAddress().toString().substring(1) + "]" + " 在 " + sdf.format((date)) + "客户端断开连接.");
    }

    /**
     * 异常处理事件
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.print("输出异常：");
        // 输出异常内容
        cause.printStackTrace();
        Channel channel = ctx.channel();
        //移除集合
        channelList.remove(channel);
        System.out.println("[Server]:" + channel.remoteAddress().toString().substring(1) + "异常.");
    }

    /**
     * 通道读取事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server接收到客户端[" + ctx.channel().remoteAddress().toString().substring(1) + "]" +"发来的消息： "+ msg);
    }
}