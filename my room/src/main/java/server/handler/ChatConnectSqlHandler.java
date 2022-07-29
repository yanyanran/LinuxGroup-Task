package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ChatHandlerMap;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.ChatMsg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天handler
 * Chat Server Handler
 * 连接数据库，writeAndFlush
 **/
public class ChatConnectSqlHandler extends SimpleChannelInboundHandler<ChatMsg> {
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

        // 传发送方from、接收方to、消息类型type、消息体message、发送时间time
        String from = msg.getFrom();
        String to = msg.getTo();
        int type = msg.getMsgType();
        String message = msg.getMsgBody();
        String time = msg.getTime();
        int flag = 0;   // 判断是否为黑名单好友
        int flag1 = 0;  // 判断是否为好友

        System.out.println(" 正在判断帐号" + to +"是否为用户" + from + "的好友....");
        // 查询 friend_list
        String sql = "select user2 from friend_list where user1=send and user2=yes and user1='" + from + "'and user2='" + to +"'";
        String sql2 = "select user1 from friend_list where user1=send and user2=yes and user2='" + from + "' and user1='"+ to +"'";
        Statement stm = con.createStatement();
        Statement stm2 = con.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSet rs2 = stm2.executeQuery(sql2);
        if(rs.next()) {
            flag1 = 1;
        }
        if (rs2.next()) {
            flag1 = 1;
        }
        System.out.println("数据库查找好友完毕...");

        if(flag1 == 0) {
            System.out.println("非好友关系!");
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"对方不是您的好友！");
            ctx.writeAndFlush(msg2);
        } else {
            System.out.println("是好友关系!");
            // 判断是否为黑名单好友
            System.out.println(" 正在判断帐号" + to +"是否为用户" + from + "的黑名单好友....");
            String sql3 = "select type from friend_list where user1='" + from + "' and user2='" + to + "'";
            Statement stmt = con.createStatement();
            ResultSet rs3 = stmt.executeQuery(sql3);
            String sql7 = "select type from friend_list where user1='" + to + "' and user2='" + from + "'";
            Statement stmt3 = con.createStatement();
            ResultSet rs4 = stmt3.executeQuery(sql7);

            if(rs3.next()) {
                int t = rs3.getInt("type");
                if(t == 1) {
                    flag = 1;
                }
            }
            if(rs4.next()) {
                int t = rs4.getInt("type");
                if(t == 1) {
                    flag = 1;
                }
            }

            // 为黑名单好友
            if(flag == 1) {
                System.out.println("好友关系异常！客户端" + from + "或发消息对象"+ to +"为一方黑名单好友，消息发送失败！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"拉黑好友无法发送消息！\n");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println(" 好友关系验证成功！");
                // 判断to是否在线
                String sql4 = "select State from client where username='"+ to +"'";
                Statement ptmt = con.createStatement();
                ResultSet m = ptmt.executeQuery(sql4);
                while(m.next()) {
                    int state = m.getInt("State");
                    if(state == 1) {
                        // online
                        System.out.println("用户[" + to + "]在线");
                        System.out.println("time: "+ time + " 用户[" + from +"]给用户[" + to + "]发送消息中...");

                        // !!!!!!send!!!!!!
                        // 利用channel显示消息给to方
                        Channel channel = ChatHandlerMap.getChannel(to);
                        channel.writeAndFlush(msg);

                        // 写入历史消息中，state --> 0
                        String sql5 = "insert into history_msg (fromc, toc, msg_type, msg, sendtime, state) values(?,?,?,?,?,0)";
                        PreparedStatement stmt2 = con.prepareStatement(sql5);
                        stmt2.setString(1,from);
                        stmt2.setString(2,to);
                        stmt2.setInt(3,type);
                        stmt2.setString(4,message);
                        stmt2.setString(5,time);
                        stmt2.executeUpdate();

                        System.out.println("【" + time + "】" + "用户" + from + "成功给用户" + to + "发送消息" + "：" + msg);
                        ServerToClientMsg msg2 = new ServerToClientMsg(true, "");
                        ctx.writeAndFlush(msg2);
                    }else {
                        // offline
                        System.out.println("用户[" + to + "]不在线");
                        System.out.println("time: "+ time + " 用户[" + from +"]给用户[" + to + "]发送消息中...");

                        // 消息存到数据库，但不能发出去

                        // 写入历史消息中，state --> 1
                        String sql6 = "insert into history_msg (fromc, toc,msg_type, msg, sendtime, state) values(?,?,?,?,?,1)";
                        PreparedStatement stmt2 = con.prepareStatement(sql6);
                        stmt2.setString(1,from);
                        stmt2.setString(2,to);
                        stmt2.setInt(3,type);
                        stmt2.setString(4,message);
                        stmt2.setString(5,time);
                        stmt2.executeUpdate();

                        System.out.println("【" + time + "】" + "用户" + from + "成功给用户" + to + "发送消息" + "：" + msg + "(对方离线)");
                        ServerToClientMsg msg2 = new ServerToClientMsg(true, "【" + time + "】" + from + "：" + msg + "已发送（对方离线）\n");
                        ctx.writeAndFlush(msg2);
                    }
                }
            }
        }
    }
}