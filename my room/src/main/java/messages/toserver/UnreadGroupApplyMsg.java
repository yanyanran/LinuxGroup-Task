package messages.toserver;

import messages.UserMessage;

/**
 * 未处理群申请
 * */
public class UnreadGroupApplyMsg extends UserMessage {
    private String me;

    public UnreadGroupApplyMsg(String me){
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