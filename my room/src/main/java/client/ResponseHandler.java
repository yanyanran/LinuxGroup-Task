package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;

import java.util.HashMap;
import java.util.Map;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;
import static client.ChatClient.msgMap;

// 服务端给客户端回消息handler
public class ResponseHandler extends SimpleChannelInboundHandler<ServerToClientMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerToClientMsg msg) throws Exception {
        boolean success = msg.getSuccess();
        String reason = msg.getReason();
        Map<Integer,String> map = msg.getMsgMap();

        if(!success) {
            System.out.print("操作失败 " + reason + "\n");
            waitSuccess = 0;
        }else {
            System.out.print("操作成功 " + reason + "\n");
            waitSuccess = 1;
            // 接收map
            msgMap = map;
        }

        // 唤醒线程
        synchronized (waitMessage) {
            waitMessage.notifyAll();
        }
    }
}