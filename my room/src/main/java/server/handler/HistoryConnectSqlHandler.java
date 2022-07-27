package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.HistoryMsg;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class HistoryConnectSqlHandler extends SimpleChannelInboundHandler<HistoryMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static Map<Integer,String> msgMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HistoryMsg msg) throws Exception {
        // 用map存消息记录 --> <id，消息体>


        String me = msg.getMe();
        String friend = msg.getFriend();
        System.out.println("用户" + me +"指定查看与用户"+ friend +"的聊天记录...");
        Connection con = null;
        PreparedStatement ps = null;
        int flag = 0;  // 判断是否有聊天记录

        // history_msg
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);

            System.out.println("开始查询...");
            String sql = "select id,fromc,toc,sendtime,msg from history_msg where (fromc=? and toc=?) or (fromc=? and toc=?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1,me);
            stmt.setString(2,friend);
            stmt.setString(3,friend);
            stmt.setString(4,me);
            // 结果集
            ResultSet rs = stmt.executeQuery(sql);

            // 消息按照id大小排序输出（先小后大）
            while (rs.next()) {
                flag = 1;
                int id = rs.getInt("id");
                String from = rs.getString("fromc");
                String to = rs.getString("toc");
                String sendTime = rs.getString("sendtime");
                String msg1 = rs.getString("msg");
                String resultMsg = "[" + sendTime + "]" +  "from" + from + "to" + to + ": " +msg;
                //  写入map
                msgMap.put(id, resultMsg);
            }

            // 判断
            if(flag == 0) {
                System.out.println("两用户间记录查询：无聊天记录!");
                ServerToClientMsg msg2 = new ServerToClientMsg(false," 聊天记录为空！\n");
            }else {
                System.out.println("两用户间记录查询：有聊天记录！");
                //  把map传给客户端
                ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}