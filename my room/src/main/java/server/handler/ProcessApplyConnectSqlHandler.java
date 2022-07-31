package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.FriendProcessApplyMsg;

import java.sql.*;

/**
 * 回复好友申请
 *  通过0：设置yes、type，并给申请方发送已通过申请通知
 *  拒绝1：删除申请时设置的三个值，并给申请方发送被拒绝通知
 * */
public class ProcessApplyConnectSqlHandler extends SimpleChannelInboundHandler<FriendProcessApplyMsg>{
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendProcessApplyMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            int id = msg.getMsgId();  // 顺着id可以在history_msg里找到from方
            String to = msg.getToUser();
            int result = msg.getNum();
            String time = msg.getTime();
            String ms1 = "对方通过了您的好友申请";
            String ms2 = "对方拒绝了您的好友申请";

            System.out.println("正在解析发送申请方用户名...");
            String sql2 = "select fromc from history_msg where id='"+ id +"'and state=1";
            PreparedStatement ptmt = con.prepareStatement(sql2);
            ResultSet m = ptmt.executeQuery(sql2);
            if(m.next()) {
                String from = m.getString("fromc");
                System.out.println("id对应申请信息存在！发送好友申请方用户名为：" + from);

                /**
                 * 【通过申请】
                 * （判断是否在线）在线发送回复后存表，不在线直接存表
                 *  history_msg---id消息state设为0---新建回复消息存到历史记录中
                 *  补全friend_list里的yes、type
                 * */
                // 通过
                if(result == 0) {
                    System.out.println("用户"+ from +"给用户" + to + "发送的好友申请已通过！");
                    // 设置数据表 -- friend_list
                    /**先试试insert into 不行再用update*/
                    String sql = "insert into friend_list(yes,type) values('" + to + "',0) where user1='" + from + "'and send='" + from + "'and user2='" + to + "'";
                    Statement ps = con.createStatement();
                    int rs = ps.executeUpdate(sql);
                    if(rs > 0) {
                        // 插入成功
                        System.out.println("数据表插入成功!");
                        // 原本history_msg里的申请信息的state置为0
                        System.out.println("正在将history_msg中的申请消息状态标记为“已处理”....");
                        String sql3 = "update history_msg set state=0 where id='"+ id +"'";
                        int rs2 = ps.executeUpdate(sql3);
                        if(rs2 > 0) {
                            System.out.println("申请信息已标记为“已处理状态”！查询对方是否在线...");
                            String sql5 = "select State from client where username='"+ from +"'";
                            ResultSet rs4 = ps.executeQuery(sql5);
                            while(rs4.next()) {
                                int state = m.getInt("State");
                                if (state == 1) {
                                    // 在线，发送消息通知申请方
                                    System.out.println("对方在线！正在给对方发送回复申请信息....");
                                    Channel channel = ChatHandlerMap.getChannel(from);
                                    channel.writeAndFlush(msg);
                                }
                            }
                            // 需要将回复消息写入history_msg
                            String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + from + "','" + to + "',3,'" + ms1 + "',1,'" + time + "')";
                            int rs3 = ps.executeUpdate(sql4);
                            if(rs3 > 0) {
                                System.out.println("回复申请成功！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(true,"回复申请成功！您与" + from + "已经是好友，快开始聊天吧！\n");
                                ctx.writeAndFlush(msg2);
                            }else {
                                System.out.println("消息写入失败！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(false, "消息写入失败！");
                                ctx.writeAndFlush(msg2);
                            }
                        }else {
                            System.out.println("标记失败！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false, "标记失败！");
                            ctx.writeAndFlush(msg2);
                        }
                    } else {
                        System.out.println("数据表插入失败！");
                        ServerToClientMsg msg2 = new ServerToClientMsg(false,"数据表插入失败！");
                        ctx.writeAndFlush(msg2);
                    }
                }else
                /**
                 * 【拒绝申请】
                 * （判断是否在线）在线发送回复后存表，不在线直接存表
                 *  history_msg---id消息state设为0---新建回复消息存到历史记录中
                 *  删除friend_list里面的user1、user2、send
                 * */
                    // 拒绝
                    if(result == 1) {
                        System.out.println("用户"+ to +"拒绝了用户" + from + "发送的好友申请！");
                        // 设置数据表 -- friend_list
                        String sql = "delete from friend_list where user1='" + from + "'and send='" + from + "'and user2='" + to + "'";
                        Statement ps = con.createStatement();
                        int rs = ps.executeUpdate(sql);
                        if(rs > 0) {
                            // 清空成功
                            System.out.println("数据表清空成功!");
                            // 原本history_msg里的申请信息的state置为0
                            System.out.println("正在将history_msg中的申请消息状态标记为“已处理”....");
                            String sql3 = "update history_msg set state=0 where id='"+ id +"'";
                            int rs2 = ps.executeUpdate(sql3);
                            if(rs2 > 0) {
                                System.out.println("申请信息已标记为“已处理状态”！查询对方是否在线...");
                                String sql5 = "select State from client where username='"+ from +"'";
                                ResultSet rs4 = ps.executeQuery(sql5);
                                while(rs4.next()) {
                                    int state = m.getInt("State");
                                    if (state == 1) {
                                        // 在线，发送消息通知申请方
                                        System.out.println("对方在线！正在给对方发送回复申请信息....");
                                        Channel channel = ChatHandlerMap.getChannel(from);
                                        channel.writeAndFlush(msg);
                                    }
                                }
                                // 需要将回复消息写入history_msg
                                String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + from + "','" + to + "',3,'" + ms2 + "',1,'" + time + "')";
                                int rs3 = ps.executeUpdate(sql4);
                                if(rs3 > 0) {
                                    System.out.println("回复申请成功！");
                                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"回复申请成功！您拒绝了"+ from +"的好友申请!\n");
                                    ctx.writeAndFlush(msg2);
                                }else {
                                    System.out.println("消息写入失败！");
                                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "消息写入失败！");
                                    ctx.writeAndFlush(msg2);
                                }
                            }else {
                                System.out.println("标记失败！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(false, "标记失败！");
                                ctx.writeAndFlush(msg2);
                            }
                        } else {
                            System.out.println("数据表插入失败！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"数据表插入失败！");
                            ctx.writeAndFlush(msg2);
                        }
                    }
            }else {
                System.out.println("操作失败！id对应申请信息不存在！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"您输入的id对应申请不存在！\n");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}