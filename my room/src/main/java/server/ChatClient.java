package server;

import client.login.Login;
import client.login.LoginMainPage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * 聊天室的客户端
 * 运行后用户先登陆，登陆成功后客户端再与服务器端连接，再输出上线离线内容
 */
public class ChatClient {
    private String ip;  // IP
    private int port;   // 端口号

    public ChatClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() throws Exception {
        // 创建线程组
        EventLoopGroup group = null;
        try {
            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();  // 创建客户端启动助手
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        // 创建一个通道初始化对象
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //编解码器
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            // 添加自定义业务处理handler
                            ch.pipeline().addLast(new ChatClientHandler());
                        }
                    });

            // 启动客户端 等待连接服务端
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();

            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress().toString().substring(1) +"--------");
            LoginMainPage.LoginPage();
            /*
             * Future - Listener
             *
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("数据发送成功.");
                    } else {
                        System.out.println("数据发送失败.");
                    }
                }
            });*/

            /*
            // 向服务端发送消息
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg);
            }*/
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        // 用户登陆
        Login.run();
    }
}