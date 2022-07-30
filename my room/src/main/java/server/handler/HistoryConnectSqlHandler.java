package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.HistoryMsg;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 连接数据库
 * 列出XX聊天记录Handler
 * */
public class HistoryConnectSqlHandler extends SimpleChannelInboundHandler<HistoryMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // 用map存消息记录 --> <id，消息体>
    public static Map<Integer,String> msgMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HistoryMsg msg) throws Exception {
        // 获取
        String me = msg.getMe();
        String friend = msg.getFriend();
        System.out.println("用户" + me +"指定查看与用户"+ friend +"的聊天记录...");
        Connection con = null;
        PreparedStatement ps = null;
        int flag = 0;  // 判断是否有聊天记录
        int flag2 = 0; // 判断是否是好友

        // history_msg
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);

            // 判断好友是否存在
            System.out.println("正在判断查询记录帐号是否是用户好友...");
            // 查询
            Statement stm2 = con.createStatement();
            String sql2 = "select type from friend_list where user1='" + friend + "'and user2='" + me + "'";
            ResultSet rs1 = stm2.executeQuery(sql2);
            Statement stm3 = con.createStatement();
            String sql3 = "select type from friend_list where user1='" + me + "'and user2='" + friend + "'";
            ResultSet rs2 = stm3.executeQuery(sql3);

            // 遍历
            if (rs1.next()) {
                flag2++;
            }
            if (rs2.next()) {
                flag2++;
            }
            //System.out.println("flag2: " + flag2);

            if (flag2 == 0) {
                // 查询消息的对象不是自己的好友
                System.out.println("不是用户好友");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, " 查询失败！此用户不是您的好友！\n");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println("是用户好友，开始查询聊天记录...");
                String sql = "select id,fromc,toc,sendtime,msg from history_msg where (fromc='" + me + "' and toc='" + friend + "') or (fromc='" + friend + "' and toc='" + me + "')";
                Statement stmt = con.createStatement();
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
                    System.out.println("两用户间记录查询结果：无聊天记录!");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false," 聊天记录为空！\n");
                    ctx.writeAndFlush(msg2);
                }else {
                    System.out.println("两用户间记录查询结果：有聊天记录！");
                    //  把map传给客户端
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap);
                    ctx.writeAndFlush(msg2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}