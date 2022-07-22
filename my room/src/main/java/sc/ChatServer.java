package sc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @program:
 * @description: 服务器主线程
 * @author:
 **/
public class ChatServer {
    public static List<Socket> clients = new ArrayList<>();  // 存储所有在线客户端

    public static void main(String[] args) {
        startService();
    }

    public static void startService(){
        ServerSocket ss = null;
        int threadNum = 0;
        try {
            // 创建一个服务器对象
            ss = new ServerSocket(8888);

            // 等待客户端连接
            while(true) {
                System.out.println("等待客户端连接中...");
                Socket client = ss.accept();
                threadNum++;
                // 将新连接的客户端保存到list中，用于后续群发给所有客户端消息
                clients.add(client);
                // 只要有一个客户端连接成功，就新创建一个线程接收客户端消息
                new ServerThread(client,"线程" + threadNum).start();
                System.out.println("【连接成功】客户端" + client.getPort() + "，已分配[线程" + threadNum + "]与其通信");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

