package messages.toserver;

import messages.UserMessage;

public class HistoryMsg extends UserMessage {
    private String friend;
    private String me;

    public HistoryMsg(String me, String friend) {
        this.friend = friend;
        this.me = me;
    }

    public String getFriend() {
        return this.friend;
    }

    public String getMe() {
        return this.me;
    }

    public String toString() {
        return "friendName = " + friend + "meName: " + me;
    }
    @Override
    public int getMessageType() {
        return 0;
    }
}