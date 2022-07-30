package server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 客户端服务端都要有
 * GroupChatMsg
 * */
public class GroupChatHandler extends SimpleChannelInboundHandler<String> {

    //所有的channel存入map集合中，目的是为了私聊好获取用户
    private static Map<String,Channel> allChannels = new HashMap<String,Channel>();

    //格式化所有日期时间
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //转化日期
    private String currentDate = sdf.format(new Date());

    /**
     * handlerAdded 表示连接建立，一旦连接建立，第一个被执行
     * 将当前channel加入到map集合
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //推送客户加入聊天的信息推送给其它在线的客户端
        //该方法会将channelGroup中所有的channel遍历并发送消息
        allChannels.forEach((k, ch) ->{
            ch.writeAndFlush(currentDate+" \n [客户端]" + channel.remoteAddress() + "加入聊天\n");
        });
        //获取端口号
        String key = channel.remoteAddress().toString().split(":")[1];
        allChannels.put(key, channel);
    }

    /**
     * 表示断开连接了，将xx客户离开信息推送给当前在线的客户
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //推送客户加入聊天的信息推送给其它在线的客户端
        //该方法会将map中所有的channel遍历并发送消息
        allChannels.forEach((k, ch) ->{
            ch.writeAndFlush(currentDate+" \n [客户端]" + channel.remoteAddress() + "离线\n");
        });
        System.out.println("当前在线人数：" + allChannels.size());
    }

    /**
     * 读取数据并将数据转发给在线的客户端
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        // 获取到当前channel
        Channel channel = ctx.channel();

        // 私聊用户发送消息
        if(s.contains("#")){
            String id = s.split("#")[1];
            String body = s.split("#")[2];
            Channel userChannel = allChannels.get(id);
            String key = channel.remoteAddress().toString().split(":")[1];
            userChannel.writeAndFlush(currentDate+"\n "+key+"【私聊】 [用户] "+id+" 说 : "+body);
            return;
        }

        //循环遍历hashmap集合进行转发消息
        allChannels.forEach((k, ch) -> {
            if (channel != ch) {
                ch.writeAndFlush(currentDate + " \n [客户端]" + channel.remoteAddress() + "：" + s + "\n");
            } else { // 发送消息给自己，回显自己发送的消息
                channel.writeAndFlush(currentDate + " \n [我]：" + s + "\n");
            }
        });
    }

    /**
     * 表示channel处于活动状态
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(currentDate + " -- " + ctx.channel().remoteAddress() + "上线~");
    }

    /**
     * 失去连接时会触发此方法
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String key = channel.remoteAddress().toString().split(":")[1];
        allChannels.remove(key);
        System.out.println(currentDate + " -- " + ctx.channel().remoteAddress() + "离线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭
        ctx.close();
    }
}