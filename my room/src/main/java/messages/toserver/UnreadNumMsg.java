package messages.toserver;

import messages.UserMessage;

/**
 * 登陆后出现的未读消息数量（查找只需记数）
 * */
public class UnreadNumMsg extends UserMessage {
    private String me;

    public UnreadNumMsg(String me){
        this.me = me;
    }

    public String getToMe() {
        return this.me;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}