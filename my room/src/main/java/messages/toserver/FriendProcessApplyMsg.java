package messages.toserver;

import messages.UserMessage;

/**
 * 回复好友申请消息类型
 * （0通过申请、1拒绝申请）
 * */
public class FriendProcessApplyMsg extends UserMessage {
    private int msgId;
    private String toUser;
    private int num;    // 通过num区分对方是同意了还是拒绝了
    String time;

    public FriendProcessApplyMsg(int msgId, String toUser, int num, String time) {
        this.msgId = msgId;
        this.toUser = toUser;
        this.num = num;
        this.time = time;
    }

    public int getMsgId() {
        return this.msgId;
    }

    public String getToUser() {
        return this.toUser;
    }

    public int getNum() {
        return this.num;
    }

    public String getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        // 申请通过和申请拒绝区分
        return "----- *>您对" + toUser + "发出的好友申请已被处理<* -----";
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}