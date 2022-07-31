package messages.toserver;

import messages.UserMessage;

/**
 * 查看未读消息
 * */
public class UnreadMsg extends UserMessage {
    private String from;
    private String to;


    @Override
    public int getMessageType() {
        return 0;
    }
}