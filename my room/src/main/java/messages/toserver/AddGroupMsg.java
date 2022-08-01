package messages.toserver;

import messages.UserMessage;

public class AddGroupMsg extends UserMessage {
    private String me;
    private int groupID;
    private String time;

    public AddGroupMsg(String me, int groupID, String time) {
        this.me = me;
        this.groupID = groupID;
        this.time = time;
    }

    public String getMe() {
        return this.me;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public String getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "[" + time + "]" + me +"申请加入群聊" + groupID;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}