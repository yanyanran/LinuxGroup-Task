package client.thread;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoservermsg.ChatMsg;

import static client.ChatClient.*;

public class ChatReceiveHandler extends SimpleChannelInboundHandler<ChatMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMsg msg) throws Exception {
        System.out.println(msg.getMsgBody());
        // 唤醒线程
        synchronized (waitMessage){
            waitMessage.notifyAll();
        }
        System.out.println("---- *您有未读消息* ----");
    }
}