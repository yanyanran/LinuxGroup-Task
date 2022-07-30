package messages.toserver;

import messages.UserMessage;

// 登陆时 客户端向服务端发的消息
public class LoginMsg extends UserMessage {
    private String username;
    private String password;

    public LoginMsg(String username, String password) {
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