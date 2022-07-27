package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.ChatMsg;
import server.ChatServer;
import server.thread.ChatServerThread;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天handler
 * Chat Server Handler
 * 连接数据库，writeAndFlush
 **/

public class ChatServerHandler extends SimpleChannelInboundHandler<ChatMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection con;
    // 声明一个共用的客户端集合，存储所有在线客户端
    public static List<Channel> clients = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMsg msg) throws Exception {
        System.out.println(msg);

        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pass);

        // 记得传from和to
        String from = msg.getFrom();
        String to = msg.getTo();
        String type = msg.getMsgType();
        String message = msg.getMsgBody();

        System.out.println("用户[" + from +"]给用户[" + to + "]发送消息中");

        // 判断是否为黑名单好友
        int flag = 0;
        String sql3 = "select type from firend_list where user1=? and user2=?";
        PreparedStatement stmt = con.prepareStatement(sql3);
        stmt.setString(1,from);
        stmt.setString(2,to);
        ResultSet rs = stmt.executeQuery();
        stmt.setString(1,to);
        stmt.setString(2,from);
        ResultSet rs2 = stmt.executeQuery();

        if(rs.next()) {
            int t = rs.getInt("type");
            if(t == 1) {
                flag = 1;
            }
        }
        if(rs2.next()) {
            int t = rs.getInt("type");
            if(t == 1) {
                flag = 1;
            }
        }

        // 为黑名单好友
        if(flag == 1) {
            System.out.println("好友关系异常！客户端" + from + "或发消息对象"+ to +"为一方黑名单好友，消息发送失败！");
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"消息发送失败！黑名单好友无法发送消息！\n");
            ctx.writeAndFlush(msg2);
        }else {
            // 好友关系正常 --> 判断to是否在线
            String sql = "select State from client where username='"+ to +"'";
            PreparedStatement ptmt = con.prepareStatement(sql);
            ResultSet m = ptmt.executeQuery(sql);
            while(m.next()) {
                int state = m.getInt("State");
                if(state == 1) {
                    // online
                    System.out.println("用户[" + to + "]在线");
                    // 写入历史消息中
                    String sql2 = "insert into history_msg (fromc, toc,msg_type, msg, state) values(?,?,?,?,0)";
                    PreparedStatement stmt2 = con.prepareStatement(sql2);
                    stmt.setString(1,from);
                    stmt.setString(2,to);

                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"消息发送成功！");
                    ctx.writeAndFlush(msg2);

                    // 需要显示消息给to方
                    // ...

                }else {
                    // offline
                    System.out.println("用户[" + to + "]不在线");
                    // 写入离线消息中和历史消息中（2）
                    ServerToClientMsg msg2 = new ServerToClientMsg(false,"对方处于离线状态");
                    ctx.writeAndFlush(msg2);


                }
            }
        }
    }
}