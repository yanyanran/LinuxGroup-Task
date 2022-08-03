package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.UnreadGroupReplyMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 连接数据库
 * 查看未读群验证消息Handler
 * 展示history_msg中state=1，toc=me,msg_type=4的消息，展示完将state设为0
 * */
public class UnreadGroupReplyConnectSqlHandler extends SimpleChannelInboundHandler<UnreadGroupReplyMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    Connection con = null;
    // 用map存消息记录 --> <id，消息体>
    public static Map<Integer,String> msgMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnreadGroupReplyMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);
            int flag = 0;
            int id = 0;
            String me = msg.getMe();
            System.out.println("开始查询用户" + me + "的未读消息....");
            String sql = "select id,fromc,msg,sendtime from history_msg where toc='"+ me +"'and state=1 and msg_type=4";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while(rs.next()) {
                flag = 1;
                id = rs.getInt("id");
                String time = rs.getString("sendtime");
                String from = rs.getString("fromc");
                String msgBody = rs.getString("msg");
                String resultMsg = "[" + time + "]" + from + "处理了您的入群申请：" + msgBody;
                //  写入map
                msgMap.put(id, resultMsg);
            }

            if(flag == 0) {
                System.out.println("查询结果：没有未读群申请回复！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"您没有未读群申请回复！\n");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println("查询结果：有未读群申请回复");
                // state置0
                String sql2 = "update history_msg set state=0 where id='"+ id +"'";
                int rs2 = stm.executeUpdate(sql2);
                if(rs2 > 0) {
                    System.out.println("消息已标记为“已读”状态！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap);
                    ctx.writeAndFlush(msg2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}