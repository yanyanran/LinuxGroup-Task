package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天室业务处理类
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    public static List<Channel> channelList = new ArrayList<>();

    /**
     * 通道就绪事件 --channel在线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //当有新的客户端连接的时候, 将通道放入集合
        channelList.add(channel);

        SimpleDateFormat sdf = new SimpleDateFormat();  // 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");  // a为am/pm的标记
        Date date = new Date(); // 获取当前时间

        System.out.println("[Server]:" + "[" + channel.remoteAddress().toString().substring(1) + "]" + " 在 " + sdf.format((date)) + "上线/客户端连接成功.");
    }

    /**
     * 通道未就绪--channel下线
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //当有客户端断开连接的时候,就移除对应的通道
        channelList.remove(channel);

        SimpleDateFormat sdf = new SimpleDateFormat();  // 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a ");  // a为am/pm的标记
        Date date = new Date(); // 获取当前时间

        System.out.println("[Server]:" + "[" + channel.remoteAddress().toString().substring(1) + "]" + " 在 " + sdf.format((date)) + "离线/客户端端开连接.");
    }

    /**
     * 异常处理事件
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        //移除集合
        channelList.remove(channel);
        System.out.println("[Server]:" + channel.remoteAddress().toString().substring(1) + "异常.");
    }

    /**
     * 通道读取事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server接收到客户端[" + ctx.channel().remoteAddress().toString().substring(1) + "]" +"发来的消息： "+ msg);
    }
}