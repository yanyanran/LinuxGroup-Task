package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.AddGroupMsg;

import java.sql.*;
import java.util.ArrayList;

/**
 * 发送入群申请
 * 给管理员（2）和群主（0）发
 * msg_type -- 4 --群申请
 * */
public class AddGroupConnectSqlHandler extends SimpleChannelInboundHandler<AddGroupMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddGroupMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getMe();
            String time = msg.getTime();
            int groupID = msg.getGroupID();
            String groupName;
            String to;
            ArrayList<String> OnLineList = new ArrayList<>();  // add添加
            ArrayList<String> OffLineList = new ArrayList<>();  // add添加

            // 判断是否存在此群
            System.out.println("正在查询是否存在该群...");
            String sql = "select group_name from group_list where group_id='"+ groupID +"'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                System.out.println("群存在！正在获取该群管理员和群主列表....");
                groupName = rs.getString("group_name") ;
                String ms = String.valueOf(groupID); // 在这里存群id号

                // 确定to群体：遍历group_list里面user_type=0/2的，对应user存入ArrayList中
                String sql2 = "select user from group_list where user_type=0 or user_type=2";
                ResultSet rs2 = stmt.executeQuery(sql2);
                System.out.println("群管理在线情况分配中...");
                while(rs2.next()) {
                    String m = rs2.getString("user");
                    // 查询对方是否在线决定把他放到哪个list里去
                    String sql3 = "select state from client where username='"+ m +"'";
                    ResultSet rs3 = stmt.executeQuery(sql3);
                    while(rs3.next()) {
                        int state = rs3.getInt("state");
                        if(state == 0) {
                            OffLineList.add(m);
                        }else {
                            OnLineList.add(m);
                        }
                    }
                }

                // 逐个给在线群管理send apply
                System.out.println("获取群管理列表成功，正在给群管理发送申请消息中...");
                for(int i = 0; i < OnLineList.size(); i++) {
                    Channel channel = ChatHandlerMap.getChannel(OnLineList.get(i));
                    channel.writeAndFlush(msg);
                }

                System.out.println("发送申请完成，正在将申请信息写入历史记录表中...");
                // 写入历史消息两个list都要 history_msg -- msg_type:4（群申请）
                for(int i = 0; i < OnLineList.size(); i++) {
                    to = OnLineList.get(i);
                    String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + me + "','" + to + "',4,'" + ms + "',1,'" + time + "')";
                    int rs4 = stmt.executeUpdate(sql4);
                    if(rs4 > 0) {
                        System.out.println("对" + to + "发送的消息插入历史记录成功！");
                    } else {
                        System.out.println("对" + to + "发送的消息插入历史记录异常！");
                    }
                }
                for(int i = 0; i < OffLineList.size(); i++) {
                    to = OffLineList.get(i);
                    String sql4 = "insert into history_msg(fromc,toc,msg_type,msg,state,sendtime) values('" + me + "','" + to + "',4,'" + ms + "',1,'" + time + "')";
                    int rs4 = stmt.executeUpdate(sql4);
                    if(rs4 > 0) {
                        System.out.println("对" + to + "发送的消息插入历史记录成功！");
                    } else {
                        System.out.println("对" + to + "发送的消息插入历史记录异常！");
                    }
                }

                System.out.println("申请信息写入历史记录完毕！");
                ServerToClientMsg msg2 = new ServerToClientMsg(true,"申请提交成功！");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println("该群不存在！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"该群ID对应的群不存在！");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}