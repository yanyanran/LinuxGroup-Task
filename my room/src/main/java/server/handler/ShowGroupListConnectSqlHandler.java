package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.GroupMsg;

import java.sql.*;

import static client.ChatClient.msgMap;

public class ShowGroupListConnectSqlHandler extends SimpleChannelInboundHandler<GroupMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMsg msg) throws Exception {
        try {
            int flag = 0;
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getMe();

            System.out.println("开始查找" + me + "用户加入的群列表....");
            String sql = "select group_id,group_name from group_list where user='" + me + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                flag = 1;
                String name = rs.getString("group_name");
                int id = rs.getInt("group_id");
                msgMap.put(id, name);
            }

            if (flag == 0) {
                System.out.println("该用户没有加入任何人群聊！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "查询结果为空！");
                ctx.writeAndFlush(msg2);
            } else {
                System.out.println("已查找到该用户加入的所有群聊！");
                ServerToClientMsg msg2 = new ServerToClientMsg(true, msgMap);
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}