package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.settoclientmsg.ServerToClientMsg;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;

// 服务端给客户端回消息handler
public class ResponseHandler extends SimpleChannelInboundHandler<ServerToClientMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerToClientMsg message) throws Exception {

        boolean success=message.getSuccess();
        String reason=message.getReason();
        int ResponseMessageType = message.getMessageType();

        if(!success){
            System.out.print("操作失败 "+reason);
            waitSuccess = 0;
        }else {
            System.out.print("操作成功 "+reason);
            waitSuccess = 1;
        }

        //不管操作成功还是失败都需要唤醒界面的主线程
        synchronized (waitMessage) {
            waitMessage.notifyAll();
        }
    }
}