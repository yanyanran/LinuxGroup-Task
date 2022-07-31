package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.UnreadApplyMsg;

import java.sql.*;

import static client.ChatClient.msgMap;

public class UnreadApplyConnectSqlHandler extends SimpleChannelInboundHandler<UnreadApplyMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    String from;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnreadApplyMsg msg) throws Exception {
        try {
            int flag = 0;
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getToMe();
            System.out.println("开始查找用户" + me + "的未处理好友申请....");
            String sql = "select id,fromc,sendtime from history_msg where toc='" + me + "'and msg_type=3)";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                flag = 1;
                int id = rs.getInt("id");
                from = rs.getString("fromc");
                String sendTime = rs.getString("sendtime");
                String resultMsg = "【" + sendTime + "】 " + from + "申请添加您为好友";
                //  写入map
                msgMap.put(id, resultMsg);
            }

            // 判断
            if (flag == 0) {
                System.out.println("查询结果：无未处理好友申请!");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, " 未处理好友申请列表为空！\n");
                ctx.writeAndFlush(msg2);
            } else {
                System.out.println("查询结果：有未处理好友申请!");
                //  把map传给客户端
                ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap, from);
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}