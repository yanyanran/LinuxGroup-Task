package messages.toserver;

import messages.UserMessage;

public class ListMsg extends UserMessage {
    // 不能设为static!!!
    private String me;
    private int num;

    public ListMsg(String me,int num) {
        this.me = me;
        this.num = num;
    }

    public String getMe() {
        return this.me;
    }

    public int getNum() {
        return this.num;
    }

    public String toString() {
        return "meName: " + me;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}