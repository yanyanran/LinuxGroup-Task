package messages.toserver;

import messages.UserMessage;

/**
 * 查看未读群验证回复
 * */
public class UnreadGroupReplyMsg extends UserMessage {
    private String me;

    public UnreadGroupReplyMsg(String me) {
        this.me = me;
    }

    public String getMe() {
        return this.me;

    }
    @Override
    public int getMessageType() {
        return 0;
    }
}