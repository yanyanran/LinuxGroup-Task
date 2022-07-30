package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.UnreadNumMsg;

import java.sql.*;

public class UnreadNumConnectSqlHandler extends SimpleChannelInboundHandler<UnreadNumMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnreadNumMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getToMe();
            int num = 0;

            System.out.println("开始计算"+ me +"的未读消息数量...");
            String sql = "select msg from history_msg where toc='"+ me +"' and state=1";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while(rs.next()) {
                num++;
            }

            // 有未读
            if(num != 0) {
                System.out.println("查询计算完毕！用户" + me + "的未读消息数为：" + num + "条!");
                ServerToClientMsg msg2 = new ServerToClientMsg(true, num);
                ctx.writeAndFlush(msg2);
            }else {   // 没未读
                System.out.println("查询计算完毕！用户" + me + "没有未读消息！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,0);
                ctx.writeAndFlush(msg2);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}