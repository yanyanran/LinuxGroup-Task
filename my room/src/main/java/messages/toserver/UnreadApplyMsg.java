package messages.toserver;

import messages.UserMessage;

/**
 * 未处理好友申请
 * */
public class UnreadApplyMsg extends UserMessage {
    private String me;

    public UnreadApplyMsg(String me){
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