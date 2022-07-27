package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// 初始版本handler
/**
 * 聊天室处理类
 */
public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    public static List<Channel> channelList = new ArrayList<>();

    /**
     * 通道就绪事件
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // ServerHandler --> Read0读取
        ctx.writeAndFlush(Unpooled.copiedBuffer("用户上线了", CharsetUtil.UTF_8));
    }

    /**
     * 通道读取就绪事件
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] buffer = new byte[buf.readableBytes()];
        buf.readBytes(buffer);
        String message = new String(buffer,"utf-8");
        System.out.println("Client,接收到服务端发来的消息:" + message);

        // 收到消息后回复
        ByteBuf byteBuf = Unpooled.copiedBuffer("你好，服务端", Charset.forName("utf-8"));
        ctx.writeAndFlush(byteBuf);

        /*
        Channel channel = ctx.channel();
        for (Channel channel1 : channelList) {
            // 排除自身通道
            if (channel != channel1) {
                channel1.writeAndFlush("[" + channel.remoteAddress().toString().substring(1) + "]说:" + msg);
            }
        }*/
    }

}