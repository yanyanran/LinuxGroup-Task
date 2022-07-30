package messages.settoservermsg;

import messages.UserMessage;

/**
 * 好友申请消息类型
 * （0申请、1回复）
 * */
public class FriendRequestsMsg extends UserMessage {
    private String fromUser;
    private String toUser;
    private int num;

    public FriendRequestsMsg(String fromUser, String toUser, int num) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.num = num;
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

    @Override
    public String toString() {
        return fromUser + "向" + toUser + "发送好友申请";
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}