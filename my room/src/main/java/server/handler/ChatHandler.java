package server.handler;

import io.netty.channel.unix.Socket;

import java.util.ArrayList;
import java.util.List;

public class ChatHandler {
    // 声明一个共用的客户端集合，存储所有在线客户端
    public static List<Socket> clients = new ArrayList<>();

}