package sc;

import sc.ChatServer;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @program:
 * @description: 服务器通信线程
 * @author:
 **/
public class ServerThread extends Thread{
    //存放每个客户端与对应的唯一Object输出流对象(因为每个socket的输出流只能获取一次，所以需要建个映射关系去取每个socket对应的输出流)
    private static Map<Socket,ObjectOutputStream> oosMap = new HashMap<>();
    private Socket client;
    private boolean startFlag;  //后加，用于处理客户端下线时的通信报错问题

    public ServerThread(Socket client, String threadName) {
        super(threadName);
        this.client = client;
        this.startFlag = true;
    }

    public void setStartFlag(boolean startFlag) {
        this.startFlag = startFlag;
    }

    /**
     * 接收客户端的消息线程，并在收到任何客户端消息后群发消息给所有客户端
     */
    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        String str = "";
        UserMessage message = null;
        try {
            ois = new ObjectInputStream(client.getInputStream());
            oos = new ObjectOutputStream(client.getOutputStream());
            oosMap.put(client,oos);
            while(startFlag) {
                //读取客户端发送的消息
                UserMessage msg = (UserMessage) ois.readObject();
                if ("File".equals(msg.getMsgType())) {
                    str = "客户端" + client.getPort() + "发送文件：" + msg.getMsgBody();
                    message = new UserMessage("File", str);
                    //将文件存储在本地固定的服务器路径下
                    saveClientFileToLocal(new File(msg.getMsgBody()));
                } else{
                    str = "客户端" + client.getPort() + "发送消息：" + msg.getMsgBody();
                    message = new UserMessage("String", str);
                }
                //群发给所有客户端
                sendClientGroups(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向所有客户端群发消息
     * @param message
     */
    public void sendClientGroups(UserMessage message){
        ObjectOutputStream oos = null;
        try {
            System.out.println("------ " + Thread.currentThread().getName() + "群发客户端start ------");

            for (int i = 0; i < ChatServer.clients.size(); i++) {
                oosMap.get(ChatServer.clients.get(i)).writeObject(message);
                System.out.println("【消息转发成功】转发端口" + ChatServer.clients.get(i).getPort() + "的客户端：  " + message );
            }
            System.out.println("------ " + Thread.currentThread().getName() + "群发客户端end ------");
            String str = "客户端" + client.getPort() +"发送消息：bye";
            if(str.equals(message.getMsgBody())){
                startFlag = false;
                ChatServer.clients.remove(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != oos){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据客户端发来的文件路径，将对应文件保存到本地的固定路径下（模拟服务器端存储文件）
     */
    public void saveClientFileToLocal(File sourceFile){
        //指名服务器文件的本地存储目录: d:/homework_server/客户端端口号
        File serverDir = new File("d:/homework_server/" + client.getPort());

        if(!serverDir.exists()) {
        }

        if(!serverDir.exists()) serverDir.mkdirs();
        //拼接得到服务器下存储的目标文件路径
        File desFile = new File(serverDir.getPath() + "/" + sourceFile.getName());

        //写入文件
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bos = new BufferedOutputStream(new FileOutputStream(desFile));
            byte[] bArr = new byte[1024];
            int res = 0;
            while((res = bis.read(bArr)) != -1){
                bos.write(bArr,0,res);
            }
            System.out.println("【File保存成功】文件名：" + sourceFile.getName() + " | 保存路径：" + desFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != bis){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != bos){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public String toString() {
        return "ServerThread{" +
                "client=" + client.getPort() +
                ", startFlag=" + startFlag +
                '}';
    }
}

