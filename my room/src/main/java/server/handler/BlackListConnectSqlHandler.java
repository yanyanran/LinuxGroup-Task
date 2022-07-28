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
        int flag2 = 0;  // 判断是否已是黑名单好友

        System.out.println(" 正在判断拉黑目标帐号" + friend +"是否为用户" + me + "的好友....");
        Class.forName(JDBC_DRIVER);
        Connection con = DriverManager.getConnection(url, user, pass);

        // 查询 friend_list --> state:0
        String sql = "select user2,type from friend_list where user1=send and user2=yes and user1=?";
        String sql2 = "select user1 from friend_list where user1=send and user2=yes and user2=?";
        PreparedStatement stm = con.prepareStatement(sql);
        PreparedStatement stm2 = con.prepareStatement(sql2);
        stm.setString(1,me);
        stm2.setString(1,me);
        ResultSet rs = stm.executeQuery(sql);
        ResultSet rs2 = stm.executeQuery(sql2);

        // 检查type --> 是否已是黑名单好友
        while (rs.next()) {
            flag = 1;
            int t = rs.getInt("type");
            if(t == 1) {
                flag2 = 1;
            }
        }
        while (rs2.next()) {
            flag = 1;
            int t = rs2.getInt("type");
            if(t == 1) {
                flag2 = 1;
            }
        }
        System.out.println("数据库查找好友完毕...");

        if(flag == 0) {
            System.out.println("拉黑失败！因为不是好友关系！");
            ServerToClientMsg msg2 = new ServerToClientMsg(false,"对方不是您的好友，无法拉黑！\n");
            ctx.writeAndFlush(msg2);
        }else {
            if (flag2 == 1) {
                System.out.println("拉黑失败！好友已在黑名单中！");
                ServerToClientMsg msg2 = new ServerToClientMsg(false,"不得添加重复黑名单好友！");
                ctx.writeAndFlush(msg2);
            }else {
                System.out.println("好友验证成功，正在判断用户操作....");

                // 添加黑名单好友
                if(num == 1) {
                    System.out.println("向数据库申请添加黑名单好友中...");
                    String sql3 = "update friend_list set type=1 where (user1=? and user2=?) or (user2=? and user1=?) ";
                    PreparedStatement stm3 = con.prepareStatement(sql3);
                    stm3.setString(1,me);
                    stm3.setString(2,friend);
                    stm3.setString(3,me);
                    stm3.setString(4,friend);
                    stm.executeUpdate(sql3);

                    System.out.println("已成功添加"+ friend +"到用户"+ me +"的黑名单！");
                    ServerToClientMsg msg2 = new ServerToClientMsg(true,"拉黑好友操作成功！");
                    ctx.writeAndFlush(msg2);
                } else  // 删除黑名单好友
                    if(num == 2) {
                        System.out.println("向数据库申请删除黑名单好友中...");
                        String sql3 = "update friend_list set type=0 where (user1=? and user2=?) or (user2=? and user1=?) ";
                        PreparedStatement stm3 = con.prepareStatement(sql3);
                        stm3.setString(1,me);
                        stm3.setString(2,friend);
                        stm3.setString(3,me);
                        stm3.setString(4,friend);
                        stm.executeUpdate(sql3);

                        System.out.println("已成功解除"+ friend +"的屏蔽！");
                        ServerToClientMsg msg3 = new ServerToClientMsg(true,"解除拉黑关系成功！");
                        ctx.writeAndFlush(msg3);
                    }
            }
        }
    }
}