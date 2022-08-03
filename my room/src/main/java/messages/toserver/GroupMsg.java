package messages.toserver;

import messages.UserMessage;

/**
 * 关于获取群列表
 * type：0群主、1群众、2管理员、3全部显示
 * */
public class GroupMsg extends UserMessage {
    private String me;
    private int type;

    public GroupMsg(String me,int type) {
        this.me = me;
        this.type = type;
    }

    public String getMe() {
        return this.me;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}