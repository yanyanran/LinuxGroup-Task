package messages.toserver;

import messages.UserMessage;

// 注销时 客户端向服务端发的消息
public class LogoutMsg extends UserMessage {
    private String username;
    private String password;

    public LogoutMsg(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String toString() {
        return "username = " + username + " password = " + password;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}