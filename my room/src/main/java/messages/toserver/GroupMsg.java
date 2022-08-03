package messages.toserver;

import messages.UserMessage;

public class GroupMsg extends UserMessage {
    private String me;

    public GroupMsg(String me) {
        this.me = me;
    }

    public String getMe() {
        return this.getMe();
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}