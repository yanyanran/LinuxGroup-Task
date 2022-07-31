package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ChatHandlerMap;
import messages.toclient.ServerToClientMsg;
import messages.toserver.FriendProcessApplyMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            String from = msg.getFromUser();
            String to = msg.getToUser();
            int result = msg.getNum();

            // 通过
            /**
             * 【通过申请】
             * （判断是否在线）在线发送回复后存表，不在线直接存表
             *  history_msg---id消息state设为0 新建回复消息存到历史记录中
             *  补全friend_list里的yes、type
             * */
            if(result == 0) {
                System.out.println("用户"+ from +"给用户" + to + "发送的好友申请已通过！");
                // 设置数据表 -- friend_list
                /**先试试insert into 不行再用update*/
                String sql = "insert into friend_list(yes,type) values('" + to + "',0) where user1='" + from + "'and send='" + from + "'and user2='" + to + "'";
                Statement ps = con.createStatement();
                int rs = ps.executeUpdate(sql);

                //需要将回复申请消息写入history_msg
                // ....


                if(rs >0) {
                    // 插入成功
                    System.out.println("数据表插入成功!");
                    // 发送消息通知申请方
                    Channel channel = ChatHandlerMap.getChannel(from);
                    channel.writeAndFlush(msg);

                    System.out.println("回复申请成功！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"回复申请成功！你们已经是好友，快开始聊天吧！ ");
                    ctx.writeAndFlush(msg2);
                }
            }else // 拒绝
            /**
             * 【拒绝申请】
             * （判断是否在线）在线发送回复后存表，不在线直接存表
             *  history_msg---id消息state设为0 新建回复消息存到历史记录中
             *  删除friend_list里面的user1、user2、send
             * */
                if(result == 1) {
                    System.out.println("用户"+ to +"拒绝了用户" + from + "发送的好友申请！");
                    // 设置数据表 -- friend_list
                    String sql = "delete from friend_list where user1='" + from + "'and send='" + from + "'and user2='" + to + "'";
                    Statement ps = con.createStatement();
                    ps.executeUpdate(sql);
                    System.out.println("数据表清空成功!");

                    // 发送消息通知申请方
                    Channel channel = ChatHandlerMap.getChannel(from);
                    channel.writeAndFlush(msg);

                    System.out.println("回复申请成功！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"回复申请成功！您拒绝了"+ from +"的好友申请");
                    ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}