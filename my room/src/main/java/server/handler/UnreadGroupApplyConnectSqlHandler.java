package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.UnreadGroupApplyMsg;

import java.sql.*;

import static client.ChatClient.msgMap;

/**
 * 连接数据库
 * 查看入群请求Handler
 * 查找history_msg中toc=me、state=1、msg_type=4的消息
 * */
public class UnreadGroupApplyConnectSqlHandler extends SimpleChannelInboundHandler<UnreadGroupApplyMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    String from;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnreadGroupApplyMsg msg) throws Exception {
        try {
            int flag = 0;
            String Msg = null;
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getToMe();
            System.out.println("开始查找用户" + me + "的未处理入群申请....");
            // state=1才是未处理的
            String sql = "select id,fromc,sendtime,msg from history_msg where toc='" + me + "'and msg_type=4 and state=1)";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                flag = 1;
                int id = rs.getInt("id");
                from = rs.getString("fromc");
                String sendTime = rs.getString("sendtime");
                Msg = rs.getString("msg");  // 传msg
                String resultMsg = "【" + sendTime + "】 " + from + Msg;
                //  写入map
                msgMap.put(id, resultMsg);
            }

            // 判断
            if (flag == 0) {
                System.out.println("查询结果：无未处理入群申请!");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, " 未处理入群申请列表为空！\n");
                ctx.writeAndFlush(msg2);
            } else {
                System.out.println("查询结果：有未处理入群申请!");
                //  把map传给客户端
                ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap, Msg);
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}