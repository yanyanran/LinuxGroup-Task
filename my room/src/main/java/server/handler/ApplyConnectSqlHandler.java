package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.FriendApplyMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 发送好友申请
 * 设置user1、send、user2
 * 在线发送后存表 不在线直接存表
 * */
public class ApplyConnectSqlHandler extends SimpleChannelInboundHandler<FriendApplyMsg>{
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendApplyMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String from = msg.getFromUser();
            String to = msg.getToUser();
            String time = msg.getTime();
            String ms = "申请添加您为好友";

            // 设置数据表 -- friend_list
            System.out.println("开始初始化好友列表...");
            String sql = "insert into friend_list(user1,send,user2) values('" + from + "','" + from + "','" + to + "')";
            Statement ps = con.createStatement();
            int rs = ps.executeUpdate(sql);
            if (rs > 0) {
                // 插入成功 --> 判断to是否在线
                String sql3 = "select State from client where username='"+ to +"'";
                Statement ptmt = con.createStatement();
                ResultSet m = ptmt.executeQuery(sql3);
                while(m.next()) {
                    int state = m.getInt("State");
                    if (state == 1) {   // 在线
                        System.out.println("用户[" + to + "]在线!正在给对方发送好友请求....");
                        // send apply
                        Channel channel = ChatHandlerMap.getChannel(to);
                        channel.writeAndFlush(msg);

                        // 将申请消息写入history_msg -- state=1 type=3
                        System.out.println("将好友申请写入历史消息表中....");
                        String sql2 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + from + "','" + to + "',3,'" + ms + "',1,'" + time + "')";
                        Statement ps2 = con.createStatement();
                        int rs2 = ps2.executeUpdate(sql2);

                        if (rs2 > 0) {
                            System.out.println("数据表插入成功!好友申请发送成功！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(true, "好友申请发送成功！");
                            ctx.writeAndFlush(msg2);
                        }else {
                            // 插入失败
                            System.out.println("数据表插入失败!");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"好友申请发送失败！");
                            ctx.writeAndFlush(msg2);
                        }
                    }else {  // 不在线 -- 消息存到数据库，不发出去
                        System.out.println("用户[" + to + "]不在线");
                        // 写入历史消息中，state --> 1
                        // 写入历史消息中history_msg -- state=0 type=3
                        System.out.println("将好友申请写入历史消息表中....");
                        String sql2 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + from + "','" + to + "',3,'" + ms + "',1,'" + time + "')";
                        Statement ps2 = con.createStatement();
                        int rs2 = ps2.executeUpdate(sql2);
                        if (rs2 > 0) {
                            System.out.println("数据表插入成功!好友申请发送成功！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(true, "好友申请发送成功！");
                            ctx.writeAndFlush(msg2);
                        }else {
                            // 插入失败
                            System.out.println("数据表插入失败!");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"好友申请发送失败！");
                            ctx.writeAndFlush(msg2);
                        }
                    }
                }
            } else {
                // 插入失败
                System.out.println("数据表插入失败!");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"好友申请发送失败！");
                ctx.writeAndFlush(msg2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}