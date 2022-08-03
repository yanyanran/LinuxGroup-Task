package messages.toserver;

import messages.UserMessage;

public class CreateGroupMsg extends UserMessage {
    private String createMan;
    private String groupName;

    public CreateGroupMsg(String createMan, String groupName) {
        this.createMan = createMan;
        this.groupName = groupName;
    }

    public String getCreateMan() {
        return this.createMan;
    }

    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}