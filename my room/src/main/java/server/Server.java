package server;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        new ChatServer(9998).run();
    }
}