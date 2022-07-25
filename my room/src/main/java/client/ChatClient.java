package client;

import c.login.LoginHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import server.handler.LoginConnectSqlHandler;

import java.util.Scanner;

/**
 * 聊天室的客户端
 * 运行后用户先登陆，登陆成功后客户端再与服务器端连接，再输出上线离线内容
 */
public class ChatClient {
    private static String ip;  // IP
    private static int port;   // 端口号
    public static final Object waitMessage = new Object();    // 服务端消息返回时，notify线程 View handler
    public static int waitSuccess;   // 1表示消息成功、0表示消息失败

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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        // 创建一个通道初始化对象
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 编解码器
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            // 添加自定义业务处理handler
                            ch.pipeline().addLast(new ChatClientHandler());
                            // 添加访问数据库handler
                            ch.pipeline().addLast(new LoginConnectSqlHandler());
                            // 服务端给客户端回消息handler
                            ch.pipeline().addLast(new ResponseHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    new Thread(()->{
                                        try {
                                            new LoginHandler(ctx);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    },"system.in").start();
                                }

                            });
                        }
                    });

            // 启动客户端 等待连接服务端
            channelFuture = bootstrap.connect(ip, port).sync();

            // 用户登陆
            // new LoginHandler(ctx);

            group.shutdownGracefully();
            // Login.homePage();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // chat future
        new ChatClient("127.0.01", 8000).clientThreadPool();
    }
}