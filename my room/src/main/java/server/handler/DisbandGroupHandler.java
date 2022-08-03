package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.DisbandGroupMsg;

import java.sql.*;
import java.util.ArrayList;

/**
 *    群主解散群聊handler
 * 1：查询操作人是否是该群群主
 * 2：向成员发送解散群的通知
 * 3：通知写入历史记录
 * 4：删除group_list中group_id=id的项
 * */
public class DisbandGroupHandler extends SimpleChannelInboundHandler<DisbandGroupMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DisbandGroupMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String time = msg.getTime();
            String me = msg.getMe();
            String to;
            int id = msg.getId();
            ArrayList<String> OnLineList = new ArrayList<>();
            ArrayList<String> OffLineList = new ArrayList<>();  // add添加

            System.out.println("正在查询操作人"+ me +"是否是群"+ id +"群主...");
            String sql = "select group_name from group_list where user='"+ me +"'and user_type=0 and group_id='"+ id +"'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                System.out.println("群主身份验证通过！正在发送群解散消息给群用户中...");
                String groupName = rs.getString("group_name");

                // 发送解散群的通知
                // 确定to群体：遍历group_list里面group_id=id的，对应user判断是否在线后存入ArrayList中
                String ms = "群聊"+ groupName + "已被解散";
                String sql2 = "select user from group_list where group_id='"+ id +"'and (user_type=1 or user_type=2)";
                ResultSet rs2 = stmt.executeQuery(sql2);
                System.out.println("群成员在线情况分配中...");
                while(rs2.next()) {
                    String user = rs2.getString("user");
                    // 查询对方是否在线决定把他放到哪个list里去
                    String sql3 = "select state from client where username='"+ user +"'";
                    ResultSet rs3 = stmt.executeQuery(sql3);
                    while(rs3.next()) {
                        int state = rs3.getInt("state");
                        if(state == 0) {
                            OffLineList.add(user);
                        }else {
                            OnLineList.add(user);
                        }
                    }
                }

                // 逐个给在线群成员send
                System.out.println("获取群成员列表成功，正在给群成员发送解散群通知中...");
                for(int i = 0; i < OnLineList.size(); i++) {
                    Channel channel = ChatHandlerMap.getChannel(OnLineList.get(i));
                    channel.writeAndFlush(msg);
                }
                System.out.println("发送完成!正在将通知写入历史记录表中...");
                // 两个list都要写入历史消息 history_msg -- msg_type:0（群通知）
                for(int i = 0; i < OnLineList.size(); i++) {
                    to = OnLineList.get(i);
                    String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + me + "','" + to + "',0,'" + ms + "',1,'" + time + "')";
                    int rs4 = stmt.executeUpdate(sql4);
                    if(rs4 > 0) {
                        System.out.println("对" + to + "发送的通知插入历史记录成功！");
                    } else {
                        System.out.println("对" + to + "发送的通知插入历史记录异常！");
                    }
                }
                for(int i = 0; i < OffLineList.size(); i++) {
                    to = OffLineList.get(i);
                    String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + me + "','" + to + "',0,'" + ms + "',1,'" + time + "')";
                    int rs4 = stmt.executeUpdate(sql4);
                    if(rs4 > 0) {
                        System.out.println("对" + to + "发送的通知插入历史记录成功！");
                    } else {
                        System.out.println("对" + to + "发送的通知插入历史记录异常！");
                    }
                }

                // 从group_list中清除
                System.out.println("申请信息写入历史记录完毕！正在清除数据库group_list中的相关项....");
                String sql5 = "delete from group_list where group_id='"+ id +"'";
                int rs5 = stmt.executeUpdate(sql5);
                if(rs5 > 0) {
                    System.out.println("清除完成！解散群聊"+ groupName +"成功！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"操作成功！");
                    ctx.writeAndFlush(msg2);
                } else {
                    System.out.println("清除失败！解散群聊"+ groupName +"失败！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false,"清除数据表失败！");
                    ctx.writeAndFlush(msg2);
                }
            } else {
                System.out.println("身份验证异常！该用户不是该群群主！无权解散群聊！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"您不是该群群主！无权解散该群！");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}