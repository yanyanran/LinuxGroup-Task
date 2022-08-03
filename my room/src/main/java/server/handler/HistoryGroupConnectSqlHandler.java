package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.HistoryGroupMsg;

import java.sql.*;
import java.util.ArrayList;

/**
 * 查找群聊历史记录 -- group_msg
 * */
public class HistoryGroupConnectSqlHandler extends SimpleChannelInboundHandler<HistoryGroupMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static ArrayList<String> GroupMsgList = new ArrayList<>();  // 暂存消息列

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HistoryGroupMsg msg) throws Exception {
        try {
            int flag = 0;
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            int id = msg.getGroupID();

            System.out.println("开始查找群聊" + id + "的历史记录....");
            String sql = "select fromc,msg,sendtime from group_msg where id='"+ id +"'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                flag = 1;
                // 无消息id（可能会导致消息乱序输出，不确定，试试看）
                String from = rs.getString("fromc");
                String time = rs.getString("sendtime");
                String Msg = rs.getString("msg");
                String result = "【" + time + "】 " + from +  ":" + Msg;
                GroupMsgList.add(result);
            }

            if(flag == 0) {
                System.out.println("该群没有聊天记录！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"查询结果为空！");
                ctx.writeAndFlush(msg2);
            } else {
                System.out.println("已获取到该群聊天记录！正在转发到客户端中....");
                ServerToClientMsg msg2 = new ServerToClientMsg(true,GroupMsgList);  // 传list
                ctx.writeAndFlush(msg2);
                GroupMsgList.clear();   // list清空即可
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}