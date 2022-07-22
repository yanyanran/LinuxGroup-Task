package sc;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @program:
 * @description: 客户端发送线程（主线程）
 * @author:
 **/
public class ChatClient {
    private static boolean startFlag = true;  //后加，用于处理客户端下线时的通信报错问题

    public static void main(String[] args) {
        lunchClient();
    }

    public static void lunchClient(){
        Socket client = null;
        Scanner sc = new Scanner(System.in);
        ObjectOutputStream oos = null;
        UserMessage message = null;
        try {
            // 创建一个客户端连接
            client = new Socket("127.0.0.1", 8889);
            System.out.println(client.getLocalPort() + "连接服务器成功！");

            // 为该客户端启动一个消息接收线程
            ClientReceiveThread receiveThread = new ClientReceiveThread(client);
            receiveThread.start();

            //3、主线程无限循环，来做该客户端的消息发送线程
            oos = new ObjectOutputStream(client.getOutputStream());
            while (startFlag) {
                System.out.println("*********  请输入要发送的消息类型...（0:文本内容 1:文件）  *********");
                String type = sc.next();
                if("0".equals(type)){
                    System.out.println("*********  请输入要发送的消息内容...  *********");
                    String msgBody = sc.next();
                    if("bye".equals(msgBody)){
                        //receiveThread.stop();// 终止该客户端的接收线程，该方式不安全，已弃用，不再使用该方式关闭接收线程，改为用startFlag标识关闭
                        startFlag = false; // 执行完本次消息发送后，退出循环，关闭客户端
                    }
                    message = new UserMessage("String", msgBody);
                }else if("1".equals(type)){
                    System.out.println("请输入要发送的本地文件路径：");
                    String filePath = new Scanner(System.in).nextLine();
                    //校验文件路径是否正确,是否为文件
                    File file = new File(filePath);
                    if(!file.exists() || !file.isFile()){
                        System.out.println("输入路径不存在或者该路径不是文件！");
                        continue;
                    }
                    message = new UserMessage("File", filePath);
                }else{
                    System.out.println("输入消息类型不存在！请重新输入...");
                    continue;
                }
                oos.writeObject(message);  // 向服务器发送消息
                System.out.println("消息发送成功!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != oos){
                try {
                    //oos.close();
                    client.shutdownOutput(); //此种关闭流的方式，是单方面关闭输出流，client的输入流可以继续使用，也不会导致client关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sc.close();
        }
    }
}

