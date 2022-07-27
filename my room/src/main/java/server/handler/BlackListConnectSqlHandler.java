package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;
import messages.settoservermsg.FriendMsg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BlackListConnectSqlHandler extends SimpleChannelInboundHandler<FriendMsg> {
    private static final String url = "jdbc:mysql://localhost:3306/ChatRoomClient?useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String user = "root";
    private static final String pass = "123456";
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendMsg msg) throws Exception {
        String me = msg.getMe();
        String friend = msg.getFriendName();
        int num = msg.getNum();
        int flag = 0;   // 判断是否是好友

        System.out.println(" 正在判断拉黑目标帐号" + friend +"是否为用户" + me + "的好友....");
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(url, user, pass);
        // 查询 friend_list --> state:0
        String sql = "select user2 from friend_list where user1=send and user2=yes and type=0 and user1=?";
        PreparedStatement stm = con.prepareStatement(sql);
        stm.setString(1,me);
        ResultSet rs = stm.executeQuery(sql);
        while (rs.next()) {
            flag = 1;
        }
        System.out.println("数据库查找好友完毕...");

        if(flag == 0) {
            System.out.println("拉黑失败！因为不是好友");
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"对方不是您的好友，无法拉黑！\n");
        }else {
            System.out.println("好友验证成功，正在判断用户操作....");


        }
        // 添加黑名单好友
        if(num == 1) {

        } else  // 删除黑名单好友
        if(num == 2) {

        }
    }
}