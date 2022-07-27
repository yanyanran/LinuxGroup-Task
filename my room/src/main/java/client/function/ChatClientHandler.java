package client.function;

import client.thread.ChatClientThread;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import messages.UserMessage;
import messages.settoservermsg.ChatMsg;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static client.ChatClient.waitMessage;
import static client.ChatClient.waitSuccess;

// set ChatMsg
public class ChatClientHandler {
    private static boolean startFlag = true;  // 后加，用于处理客户端下线时的通信报错问题

    public ChatClientHandler(ChannelHandlerContext ctx) {
        Channel client = null;
        Scanner input = new Scanner(System.in);
        ObjectOutputStream oos = null;

        try {
            // 2、为该客户端启动一个消息接收线程
            //ChatClientThread receiveThread = new ChatClientThread(client);
            //receiveThread.start();

            // 3、主线程无限循环，来做该客户端的消息发送线程
            while (startFlag) {
                System.out.println("------ 请输入要发送的消息类型（0:文本内容 1:文件）------");
                // choose
                String type = input.next();
                // MESSAGE
                if("0".equals(type)){
                    System.out.println("------ 请输入要发送的消息内容 ------\n如想退出当前会话 请输入bye");
                    // input
                    String msgBody = input.next();
                    if("bye".equals(msgBody)){
                        startFlag = false; // 执行完本次消息发送后，退出循环，关闭
                    }

                    // 发给服务端
                    ChatMsg msg = new ChatMsg("String", msgBody);
                    ctx.writeAndFlush(msg);
                    // lock and wait
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(waitSuccess == 1) {
                        System.out.println("消息发送成功!");


                    }else {

                    }
                    // FILE
                }else if("1".equals(type)){
                    System.out.println("请输入要发送的本地文件路径：");
                    String filePath = new Scanner(System.in).nextLine();
                    // 校验文件路径是否正确,是否为文件
                    File file = new File(filePath);
                    if(!file.exists() || !file.isFile()){
                        System.out.println("输入路径不存在或者该路径不是文件！");
                        continue;
                    }

                    // 发给服务端
                    ChatMsg msg = new ChatMsg("File", filePath);
                    ctx.writeAndFlush(msg);
                    // lock and wait
                    try {
                        synchronized (waitMessage) {
                            waitMessage.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(waitSuccess == 1) {
                        System.out.println("文件发送成功!");


                    }else {

                    }

                }else{
                    System.out.println("输入消息类型不存在！请重新输入...");
                    continue;
                }

            }
        } finally {
            // 释放资源
            if(null != oos){
                client.close(); // 此关闭流方式是单方面关闭输出流，client的输入流可以继续使用，不会导致client关闭
            }
            input.close();
        }
    }
}