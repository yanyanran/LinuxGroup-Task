package messages.settoservermsg;

import messages.UserMessage;

public class FriendMsg extends UserMessage {
    private String friendName;
    private static String me;
    private int num;

    public FriendMsg(String friendName,String me) {
        this.friendName = friendName;
        this.me = me;
    }

    public FriendMsg(String me,int num) {
        this.me = me;
        this.num = num;
    }

    public FriendMsg(String me,String friendName,int num) {
        this.friendName = friendName;
        this.me = me;
        this.num = num;
    }

    public String getFriendName() {
        return this.friendName;
    }

    public String getMe() {
        return this.me;
    }

    public int getNum() {
        return this.num;
    }

    public String toString() {
        return "friendName = " + friendName + "meName: " + me;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}