package messages.toserver;

import messages.UserMessage;

public class HistoryGroupMsg extends UserMessage {
    private int groupID;

    public HistoryGroupMsg(int groupID) {
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}