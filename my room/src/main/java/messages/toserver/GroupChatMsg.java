package messages.toserver;

import messages.UserMessage;

public class GroupChatMsg extends UserMessage {
    public GroupChatMsg(){

    }

    @Override
    public int getMessageType() {
        return 0;
    }
}