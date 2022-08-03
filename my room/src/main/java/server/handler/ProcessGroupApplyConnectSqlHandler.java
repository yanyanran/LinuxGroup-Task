package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.ProcessGroupApplyMsg;

import java.sql.*;

/**
 * 管理员/群主处理入群申请handler
 *  通过0：写入group_list，并给申请方发送已通过申请通知
 *  拒绝1：给申请方发送被拒绝通知
 * */
public class ProcessGroupApplyConnectSqlHandler extends SimpleChannelInboundHandler<ProcessGroupApplyMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProcessGroupApplyMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            int id = msg.getMsgId();
            int result = msg.getNum();
            int groupID = Integer.parseInt(msg.getGroupID());
            String time = msg.getTime();
            String groupName = null;
            String ms1 = "您的入群申请已通过!您已加入群聊" + groupID;
            String ms2 = "您加入群聊"+ groupID +"的申请被驳回";

            // 回复前先查一下消息状态state是不是已经置为0（0就是已处理）
            System.out.println("正在检测此申请是否已被处理...");
            String checkState = "selcet state from history_msg where id='"+ id +"'";
            Statement ptmt = con.createStatement();
            ResultSet res = ptmt.executeQuery(checkState);

            if(res.next()) {
                int msgState = res.getInt("state");
                if(msgState == 0) {
                    System.out.println("该申请已被其他管理员处理！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false,"该申请已被其他管理员处理！\n");
                    ctx.writeAndFlush(msg2);
                } else {
                    // 还没处理 action
                    System.out.println("正在解析发送申请方用户名...");
                    String sql = "select fromc from history_msg where id='"+ id +"'and state=1";
                    ResultSet rs = ptmt.executeQuery(sql);
                    if(rs.next()) {
                        String from = rs.getString("fromc");
                        System.out.println("id对应申请信息存在！发送好友申请方用户名为：" + from);

                        /**
                         * 【通过申请】
                         * （判断是否在线）在线发送回复后存表，不在线直接存表
                         * 将用户存到group_list中（需要知道群ID和群名）
                         *  history_msg---id消息state设为0---新建回复消息存到历史记录中
                         * */
                        if(result == 0) {
                            // 群名从群ID中索引获取
                            System.out.println("用户"+ from +"给群" + groupName + "发送的群申请已通过！正在通过群ID索引搜索群名....");
                            String sql2 = "select group_name from grup_list where group_id='"+ groupID +"'";
                            ResultSet rs2 = ptmt.executeQuery(sql2);
                            if(rs2.next()) {
                                // 获取到群名
                                groupName = rs2.getString("group_name");
                                System.out.println("搜索成功！搜索到该群名为：" + groupName);
                            }

                            // 插入数据表 -- group_list
                            System.out.println("正在将用户添加到群聊列表中...");
                            String sql3 = "insert into group_list(group_id, group_name, user, user_type) values('" + groupID + "', '"+ groupName+"', '"+ from +"', 1)";
                            int rs3 = ptmt.executeUpdate(sql3);
                            if(rs3 > 0) {
                                System.out.println("用户设置添加成功！正在将history_msg中的申请消息状态标记为“已处理”....");
                                String sql4 = "update history_msg set state=0 where id='" + id + "'";
                                int rs4 = ptmt.executeUpdate(sql4);
                                if (rs4 > 0) {
                                    System.out.println("申请信息已标记为“已处理状态”！查询对方是否在线...");
                                    String sql5 = "select State from client where username='" + from + "'";
                                    ResultSet rs5 = ptmt.executeQuery(sql5);
                                    while (rs5.next()) {
                                        int state = rs5.getInt("State");
                                        if (state == 1) {
                                            // 在线，发送消息通知申请方
                                            System.out.println("对方在线！正在给对方发送回复申请信息....");
                                            Channel channel = ChatHandlerMap.getChannel(from);
                                            channel.writeAndFlush(msg);
                                        }
                                    }

                                    // 将回复消息写入history_msg
                                    System.out.println("正在将回复消息存入历史记录中.....");
                                    String sql6 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + groupName + "','" + from + "',4,'" + ms1 + "',1,'" + time + "')";
                                    int rs6 = ptmt.executeUpdate(sql6);
                                    if (rs6 > 0) {
                                        System.out.println("存入完成！回复申请成功！");
                                        ServerToClientMsg msg2 = new ServerToClientMsg(true, "回复申请成功！用户" + from + "已加入群聊" + groupName + "\n");
                                        ctx.writeAndFlush(msg2);
                                    } else {
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
                        }else if(result == 1) {
                            /**
                             * 【拒绝申请】
                             * （判断是否在线）在线发送回复后存表，不在线直接存表
                             *  history_msg---id消息state设为0---新建回复消息存到历史记录中
                             * */
                            // 群名从群ID中索引获取
                            System.out.println("用户"+ from +"给群" + groupName + "发送的群申请已拒绝！正在通过群ID索引搜索群名....");
                            String sql2 = "select group_name from grup_list where group_id='"+ groupID +"'";
                            ResultSet rs2 = ptmt.executeQuery(sql2);
                            if(rs2.next()) {
                                // 获取到群名
                                groupName = rs2.getString("group_name");
                                System.out.println("搜索成功！搜索到该群名为：" + groupName);
                            }

                            System.out.println("正在将history_msg中的申请消息状态标记为“已处理”....");
                            String sql3 = "update history_msg set state=0 where id='" + id + "'";
                            int rs3 = ptmt.executeUpdate(sql3);
                            if (rs3 > 0) {
                                System.out.println("申请信息已标记为“已处理状态”！查询对方是否在线...");
                                String sql4 = "select State from client where username='" + from + "'";
                                ResultSet rs4 = ptmt.executeQuery(sql4);
                                while (rs4.next()) {
                                    int state = rs4.getInt("State");
                                    if (state == 1) {
                                        // 在线，发送消息通知申请方
                                        System.out.println("对方在线！正在给对方发送回复申请信息....");
                                        Channel channel = ChatHandlerMap.getChannel(from);
                                        channel.writeAndFlush(msg);
                                    }
                                }

                                // 将回复消息写入history_msg
                                System.out.println("正在将回复消息存入历史记录中.....");
                                String sql5 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + groupName + "','" + from + "',4,'" + ms2 + "',1,'" + time + "')";
                                int rs5 = ptmt.executeUpdate(sql5);
                                if (rs5 > 0) {
                                    System.out.println("存入完成！回复申请成功！");
                                    ServerToClientMsg msg2 = new ServerToClientMsg(true, "回复申请成功！用户" + from + "加入群聊" + groupName + "的申请已被您拒绝！\n");
                                        ctx.writeAndFlush(msg2);
                                } else {
                                    System.out.println("消息写入失败！");
                                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "消息写入失败！");
                                        ctx.writeAndFlush(msg2);
                                }
                            }else {
                                System.out.println("标记失败！");
                                ServerToClientMsg msg2 = new ServerToClientMsg(false, "标记失败！");
                                ctx.writeAndFlush(msg2);
                            }
                        }
                    } else {
                        System.out.println("操作失败！id对应申请信息不存在！");
                        ServerToClientMsg msg2 = new ServerToClientMsg(false,"您输入的id对应申请不存在！\n");
                        ctx.writeAndFlush(msg2);
                    }
                }
            } else {
                System.out.println("操作失败！查找不到该申请用户！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"查找不到该申请用户！\n");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}