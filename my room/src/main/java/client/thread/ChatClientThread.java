//package client.thread;
//
//import io.netty.channel.Channel;
//import messages.settoservermsg.ChatMsg;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.net.Socket;
//
///**
// * 客户端接收线程
// * (已弃用)
// **/
//public class ChatClientThread extends Thread{
//    private Channel client;
//    private boolean startFlag = true;  // 后加，用于正常关闭线程，而不是像线程的stop方法一样强制关闭
//
//    public ChatClientThread(Channel client) {
//        this.client = client;
//    }
//
//    /**
//     * 接收服务器发送的消息
//     */
//    @Override
//    public void run(){
//        ObjectInputStream ois = null;
//        try {
//            while(startFlag) {
//               ChatMsg message = (ChatMsg) ois.readObject();
//                System.out.println("---【服务器消息】" + message.getMsgBody());
//                String str = "客户端" + client.remoteAddress().toString().substring(1) +"发送消息：bye";
//                // 如果是客户端下线的消息，则修改标识，正常结束线程
//                if(str.equals(message.getMsgBody())) {
//                    startFlag = false;
//                }
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if(null != ois){
//                try {
//                    // 此时关闭ois相当于关闭了客户端的oos、ois、socket，因为这是客户端退出之前的最后一步，所以在这里关闭oos、ois、socket都不会报错，而在程序执行到这里之前提前关闭oos是不允许的（也就是在服务端通信线程那里去关闭），因为那时关闭的话，这里的ois就没法读取内容了
//                    ois.close(); // 关闭客户端输入流，此种关闭方式会导致客户端的输入输出流都不再使用。也会自动关闭掉client
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
//
