package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.toserver.ChatMsg;

import static client.ChatClient.*;

// 是不是应该接收Channel类型msg呢？
public class ChatReceiveHandler extends SimpleChannelInboundHandler<ChatMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMsg msg) throws Exception {
        //System.out.println(msg.getMsgBody());
        // 唤醒线程
        synchronized (waitMessage){
            waitMessage.notifyAll();
        }
        System.out.println("---- *您有未读消息* ----");
    }
}