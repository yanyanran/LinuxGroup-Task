package messages.toserver;

import messages.UserMessage;

/**
 * 回复好友申请消息类型
 * （0通过申请、1拒绝申请）
 * */
public class FriendProcessApplyMsg extends UserMessage {
    private String fromUser;
    private String toUser;
    private int num;    //
    private String time;

    public FriendProcessApplyMsg(String fromUser, String toUser, String time , int num) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.num = num;     // 通过num区分对方是同意了还是拒绝了
        this.time = time;
    }

    public String getFromUser() {
        return this.fromUser;
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