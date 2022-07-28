package client;

import client.thread.ChatReceiveHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import messages.MessageCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 聊天室的客户端
 * 运行后用户先登陆，登陆成功后客户端再与服务器端连接，再输出上线离线内容
 */
public class ChatClient {
    private static int num;    // 线程数
    private static String ip;  // IP
    private static int port;   // 端口号
    public static volatile Object waitMessage = new Object();    // 服务端消息返回 --> notify唤醒
    public static volatile int waitSuccess;   // 1成功，0失败
    public static volatile Map<Integer,String> msgMap = new HashMap<>(); //  存消息记录的map
    public static volatile ArrayList<String> userList = new ArrayList<>();  //  存用户列表的list
    public static volatile boolean unreadMsg = false;// 未读消息提醒
    public static volatile boolean is = false; // 好友发消息提醒

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // 选择聊天调用 setMsg(channelFuture);
    public static void setMsg(ChannelFuture ch) {
        Channel channel  = ch.channel();
        // 向服务端发送消息 --> 聊天
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            channel.writeAndFlush(msg);
        }
    }

    public static void clientThreadPool() throws Exception {
        // 创建线程组
        EventLoopGroup group = null;
        ChannelFuture channelFuture = null;
        try {
            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();  // 创建客户端启动助手
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        // 创建一个channel初始化对象
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 长度协议解码器
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(20*1024, 9, 4, 2, 0));
                            // 编解码器
                            ch.pipeline().addLast(new MessageCode());
                            //ch.pipeline().addLast(new StringDecoder());
                            //ch.pipeline().addLast(new StringEncoder());
                            /** 添加自定义业务处理handler */
                            ch.pipeline().addLast(new ClientHandler());
                            //ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            // 服务端给客户端回消息handler
                            ch.pipeline().addLast(new ResponseHandler());
                            ch.pipeline().addLast(new ChatReceiveHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive( ChannelHandlerContext ctx) throws Exception {
                                    // 创一个线程跑界面
                                    new Thread(()->{
                                        try {
                                            num++;
                                            new LoginClientHandler(ctx);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }},"线程" + num).start();
                                }
                            });
                        }
                    });

            // 启动客户端 等待连接服务端
            channelFuture = bootstrap.connect(ip, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        // chat future
        new ChatClient("127.0.01", 8000).clientThreadPool();
    }
}