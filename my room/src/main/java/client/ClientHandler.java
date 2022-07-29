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
 * 收到消息立即提醒handler
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    public static List<Channel> channelList = new ArrayList<>();
    /**
     * 通道读取就绪事件
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //String message = msg.toString();
        System.out.println("------------ >*【新消息提醒：您收到一条新消息】*< ------------");
    }
}