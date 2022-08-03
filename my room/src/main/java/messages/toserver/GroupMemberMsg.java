package messages.toserver;

import messages.UserMessage;

public class GroupMemberMsg extends UserMessage {
    private String me;
    private int id;

    public GroupMemberMsg(String me, int id) {
        this.me = me;
        this.id = id;
    }

    public String getMe() {
        return this.me;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}