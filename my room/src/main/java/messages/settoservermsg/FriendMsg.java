package messages.settoservermsg;

import messages.UserMessage;

public class FriendMsg extends UserMessage {
    private String friendName;
    private String me;

    public FriendMsg(String friendName,String me) {
        this.friendName = friendName;
        this.me = me;
    }

    public String getFriendName() {
        return this.friendName;
    }

    public String getMe() {
        return this.me;
    }


    public String toString() {
        return "friendName = " + friendName + "meName: " + me;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}