package messages.toserver;

import messages.UserMessage;

public class DisbandGroupMsg extends UserMessage {
    private int id;
    private String me;
    private String time;

    public DisbandGroupMsg(String me, int id, String time) {
        this.me = me;
        this.id = id;
        this.time = time;
    }

    public String getMe() {
        return this.me;
    }

    public int getId() {
        return this.id;
    }

    public String getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "------ *>解散群通知：群聊"+ id + "已被解散*< ------";
    }
    @Override
    public int getMessageType() {
        return 0;
    }
}