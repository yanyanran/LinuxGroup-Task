package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import messages.MessageCode;
import server.handler.*;

/**
 * 聊天室服务端
 */
public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        // 处理连接事件线程组
        EventLoopGroup bossGroup = null;
        // 处理读写事件线程组
        EventLoopGroup workerGroup = null;
        ChannelFuture future = null;
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            // 服务端启动助手
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    // 通道初始化对象
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // handler在pipeline中的添加位置很有讲究！！！一定注意！！！编码器如果放在最后是无法识别各大msg的！！！（走大坑）
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            /** 向pipeline中添加自定义业务处理handler  */
                            ch.pipeline().addLast("connect-handler",new ConnectServerHandler());
                            ch.pipeline().addLast("login-handler",new LoginConnectSqlHandler());
                            ch.pipeline().addLast("logout-handler",new LogoutConnectSqlHandler());
                            ch.pipeline().addLast("offline-handler",new OfflineConnectSqlHandler());
                            ch.pipeline().addLast("register-handler",new RegisterConnectSqlHandler());
                            ch.pipeline().addLast("friend-handler", new AddFriendConnectSqlHandler());
                            ch.pipeline().addLast("chat-handler",new ChatConnectSqlHandler());
                            ch.pipeline().addLast("black-list-handler",new BlackListConnectSqlHandler());
                            ch.pipeline().addLast("friend-list-handler",new FriendListConnectSqlHandler());
                            ch.pipeline().addLast("history-handler",new HistoryConnectSqlHandler());
                            // 长度协议解码器
                            ch.pipeline().addFirst(new LengthFieldBasedFrameDecoder(1024*1024*1024, 9, 4, 2, 0));
                            // //添加编解码器
                            ch.pipeline().addFirst(new MessageCode());
                        }
                    });

            // 启动服务端 绑定端口
            future = serverBootstrap.bind(port);
            Channel channel = future.sync().channel();

            /**
             * Future - Listener
             * */
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("端口绑定成功!");
                        System.out.println(" ~ Welcome To MyChatRoom ~ \n---- 服务端启动成功! ----");
                    } else {
                        System.out.println("端口绑定失败!");
                    }
                }
            });

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ChatServer(8000).run();
    }
}