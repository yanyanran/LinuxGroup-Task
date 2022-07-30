package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义服务端心跳监测器
 * 客户端在指定时间内未触发相应操作执行此方法，即认为与客户端断开连接
 *  ctx 全局上下文对象
 *  evt 事件
 */
public class HeartBreakHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断当前事件是否为IdleStateEvent
        if (evt instanceof IdleStateEvent) {
            //将evt强转为IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            //判断到底发生的事件是什么
            String eventType = null;
            //由于IdleStateEvent底层判断事件是根据枚举类型来的，所以直接判断即可
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "发生超时事件，事件类型为：" + eventType);
            System.out.println("服务器做相应处理");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("发生异常！");
    }
}