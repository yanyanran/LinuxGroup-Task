package messages.toserver;

import messages.UserMessage;

// 注册时 客户端向服务端发的消息
public class RegisterMsg extends UserMessage {
    private String username;
    private String password;
    private String password2;

    public RegisterMsg(String username, String password, String password2) {
        this.username = username;
        this.password = password;
        this.password2 = password2;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPassword2() {
        return this.password2;
    }

    public String toString() {
        return "username = " + username + " password = " + password;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}