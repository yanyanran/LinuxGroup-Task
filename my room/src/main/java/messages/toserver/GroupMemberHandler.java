package messages.toserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toclient.ServerToClientMsg;

import java.sql.*;
import java.util.ArrayList;

/**
 * 查看群组成员handler
 * 输出：（用户名） （群身份）（在线情况）（是否是自己的好友）
 * */
public class GroupMemberHandler extends SimpleChannelInboundHandler<GroupMemberMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // ArrayList用来存好友列表
    public static ArrayList<String> list = new ArrayList<>();  // 暂存名单
    String result = null;  // add to list

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMemberMsg msg) throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
            Connection con = DriverManager.getConnection(url, user, pass);
            String me = msg.getMe();
            int id = msg.getId();

            System.out.println("正在验证用户" + me + "是否为群" + id + "的群成员....");
            String sql = "select group_name from group_list where user='" + me + "'and group_id='" + id + "'";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                System.out.println("身份验证通过！开始查找群" + id + "的群成员...");
                String groupName = rs.getString("group_name");

                // 输出：（用户名group_list） （群身份group_list）（在线情况client）（是否是自己的好友friend_list）
                String sql2 = "";

            } else {
                System.out.println("身份验证失败！该用户不是该群的群成员！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false, "您不在该群聊中！无法查看该群的群成员！");
                ctx.writeAndFlush(msg2);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}