package messages.settoservermsg;

import messages.UserMessage;

import java.io.File;

//   和friendMsg一样吼
public class HistoryMsg extends UserMessage {
    private static String friend;
    private static String me;

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