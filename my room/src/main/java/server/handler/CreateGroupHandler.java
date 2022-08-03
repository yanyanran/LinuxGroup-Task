package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;
import messages.toserver.CreateGroupMsg;

import java.sql.*;

public class CreateGroupHandler extends SimpleChannelInboundHandler<CreateGroupMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    Connection con = null;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(url, user, pass);
            String me = msg.getCreateMan();
            String name = msg.getGroupName();

            System.out.println("开始创建群聊，正在将相关信息写入数据库中....");
            String sql = "insert into group_list(user,group_name,user_type)values(?, ?, 0)";    // 设置为群主
            PreparedStatement ptmt = con.prepareStatement(sql);
            ptmt.setString(1, me);
            ptmt.setString(2, name);
            boolean rs = ptmt.execute();
            if (rs == true) {
                // 获取群ID
                System.out.println("写入成功！正在获取群id号....");
                String sql2 = "select group_id from group_list where user='" + me + "'and group_name='"+ name +"'and user_type=0";
                Statement stmt = con.createStatement();
                ResultSet rs2 = stmt.executeQuery(sql2);
                if (rs2.next()) {
                    int id = rs2.getInt("group_id");
                    System.out.println("获取成功！由用户" + me + "创建的群" + name + "的群id为：" + id);
                    ServerToClientMsg msg2 = new ServerToClientMsg(true, id);
                    ctx.writeAndFlush(msg2);
                } else {
                    System.out.println("未查询到该群id号码！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(false, "未查询到该群id号码！");
                }
            } else {
                System.out.println("数据写入失败！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "数据写入失败！");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}