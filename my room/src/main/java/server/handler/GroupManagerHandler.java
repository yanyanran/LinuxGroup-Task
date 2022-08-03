package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.GroupManagerMsg;

import java.sql.*;

/**
 * 添加/删除管理员handler
 * 设置成功后发消息通知对方
 * */
public class GroupManagerHandler extends SimpleChannelInboundHandler<GroupManagerMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    Connection con = null;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupManagerMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);
            String me = msg.getMe();
            String name = msg.getName();  // 管理员的名字
            String time = msg.getTime();
            int num = msg.getNum();
            int id = msg.getGroupID();

            /**
             * 添加管理员并通知该用户
             * */
            if(num == 0) {
                System.out.println("正在查询操作人是否是此群的群主...");
                String sql = "select group_name from group_list where user='"+ me +"'and user_type=0 and group_id='"+ id +"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()) {
                    System.out.println("群主身份验证通过！正在查询操作对象的群内身份....");
                    String groupName = rs.getString("group_name");
                    // 通知消息体
                    String ms1 = "您已被群主设置为群"+ groupName +"的管理员";

                    String sql2 = "select user_type from group_list where user='"+ name +"'and group_id='"+ id +"'";
                    ResultSet rs2 = stmt.executeQuery(sql2);
                    if (rs2.next()) {
                        int type = rs2.getInt("user_type");
                        // 身份验证为群主
                        if(type == 0) {
                            System.out.println("身份异常！操作对象为群主！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！不可设置群主为管理员！");
                            ctx.writeAndFlush(msg2);
                            // 身份验证为管理员
                        } else if(type == 2) {
                            System.out.println("身份异常！操作对象已经是管理员！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！" + name + "已经是该群管理员！");
                            ctx.writeAndFlush(msg2);
                            // 身份验证正常
                        } else if(type == 1) {
                            System.out.println("身份验证成功！正在设置"+ name +"为群"+ groupName +"的管理员....");
                            String sql3 = "update group_list set user_type=2 where group_id='"+ id +"'and user='"+ name +"'";
                            int rs3 = stmt.executeUpdate(sql3);
                            if(rs3 > 0) {
                                System.out.println("设置管理员成功！ 正在判断对方是否在线....");
                                String sql4 = "select State from client where username='"+ name +"'";
                                ResultSet rs4 = stmt.executeQuery(sql4);
                                if(rs4.next()) {
                                    // 在线
                                    if(rs4.getInt("State") == 1) {
                                        System.out.println("用户[" + name + "]在线，通知消息正在发送....");
                                        Channel channel = ChatHandlerMap.getChannel(name);
                                        channel.writeAndFlush(msg);
                                    } else {
                                        // 不在线
                                        System.out.println("用户[" + name + "]不在线");
                                    }

                                    System.out.println("正在将通知消息写入历史消息中....");
                                    String sql5 = "insert into client (fromc,toc,msg_type,msg,state,sendtime) values(?,?,0,?,1,?)";
                                    PreparedStatement ptmt = con.prepareStatement(sql5);
                                    ptmt.setString(1, me);
                                    ptmt.setString(2, name);
                                    ptmt.setString(3, ms1);
                                    ptmt.setString(4, time);
                                    boolean rs5 = ptmt.execute();
                                    if(rs5 == true) {
                                        System.out.println("通知消息写入历史记录成功！");
                                        ServerToClientMsg msg2 = new ServerToClientMsg(true, "设置成功！");
                                        ctx.writeAndFlush(msg2);
                                    } else {
                                        System.out.println("通知消息写入历史记录失败！");
                                        ServerToClientMsg msg2 = new ServerToClientMsg(false, "通知消息写入数据库失败！");
                                        ctx.writeAndFlush(msg2);
                                    }
                                }
                            } else {
                                System.out.println("修改用户身份失败！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(false, "设置管理员失败!");
                                ctx.writeAndFlush(msg2);
                            }
                        }
                    } else {
                        System.out.println("状态异常！查找不到此人！");
                        ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！用户"+ name +"不是群成员！");
                        ctx.writeAndFlush(msg2);
                    }
                } else {
                    System.out.println("群主身份验证失败！"+ me +"没有权限管理群"+ id +"的管理员！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "您无权添加设置本群的群管理员！");
                    ctx.writeAndFlush(msg2);
                }
                /**
                 * 移除管理员并通知该用户
                 * */
            } else if(num == 1) {
                System.out.println("正在查询操作人是否是此群的群主...");
                String sql = "select group_name from group_list where user='"+ me +"'and user_type=0 and group_id='"+ id +"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()) {
                    System.out.println("群主身份验证通过！正在查询操作对象的群内身份....");
                    String groupName = rs.getString("group_name");
                    // 通知消息体
                    String ms2 = "您已被群主解除群"+ groupName + "的管理员身份";

                    String sql2 = "select user_type from group_list where user='"+ name +"'and group_id='"+ id +"'";
                    ResultSet rs2 = stmt.executeQuery(sql2);
                    if (rs2.next()) {
                        int type = rs2.getInt("user_type");
                        // 身份验证为群主
                        if(type == 0) {
                            System.out.println("身份异常！操作对象为群主！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！不可移除群主！");
                            ctx.writeAndFlush(msg2);
                            // 身份验证为群众
                        } else if(type == 1) {
                            System.out.println("身份异常！操作对象不是管理员！");
                            ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！" + name + "不是该群管理员！");
                            ctx.writeAndFlush(msg2);
                            // 身份验证正常
                        } else if(type == 2) {
                            System.out.println("身份验证成功！正在移除"+ name +"在群"+ groupName +"的管理员身份....");
                            String sql3 = "update group_list set user_type=1 where group_id='"+ id +"'and user='"+ name +"'";
                            int rs3 = stmt.executeUpdate(sql3);
                            if(rs3 > 0) {
                                System.out.println("移除管理员成功！ 正在判断对方是否在线....");
                                String sql4 = "select State from client where username='"+ name +"'";
                                ResultSet rs4 = stmt.executeQuery(sql4);
                                if(rs4.next()) {
                                    // 在线
                                    if(rs4.getInt("State") == 1) {
                                        System.out.println("用户[" + name + "]在线，通知消息正在发送....");
                                        Channel channel = ChatHandlerMap.getChannel(name);
                                        channel.writeAndFlush(msg);
                                    } else {
                                        // 不在线
                                        System.out.println("用户[" + name + "]不在线");
                                    }

                                    System.out.println("正在将通知消息写入历史消息中....");
                                    String sql5 = "insert into client (fromc,toc,msg_type,msg,state,sendtime) values(?,?,0,?,1,?)";
                                    PreparedStatement ptmt = con.prepareStatement(sql5);
                                    ptmt.setString(1, me);
                                    ptmt.setString(2, name);
                                    ptmt.setString(3, ms2);
                                    ptmt.setString(4, time);
                                    boolean rs5 = ptmt.execute();
                                    if(rs5 == true) {
                                        System.out.println("通知消息写入历史记录成功！");
                                        ServerToClientMsg msg2 = new ServerToClientMsg(true, "设置成功！");
                                        ctx.writeAndFlush(msg2);
                                    } else {
                                        System.out.println("通知消息写入历史记录失败！");
                                        ServerToClientMsg msg2 = new ServerToClientMsg(false, "通知消息写入数据库失败！");
                                        ctx.writeAndFlush(msg2);
                                    }
                                }
                            } else {
                                System.out.println("修改用户身份失败！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(false, "移除管理员失败!");
                                ctx.writeAndFlush(msg2);
                            }
                        }
                    } else {
                        System.out.println("状态异常！查找不到此人！");
                        ServerToClientMsg msg2 = new ServerToClientMsg(false,"设置失败！用户"+ name +"不是群成员！");
                        ctx.writeAndFlush(msg2);
                    }
                } else {
                    System.out.println("群主身份验证失败！"+ me +"没有权限管理群"+ id +"的管理员！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "您无权移除本群的群管理员！");
                    ctx.writeAndFlush(msg2);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}