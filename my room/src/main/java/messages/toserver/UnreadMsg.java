package messages.toserver;

import messages.UserMessage;

/**
 * 查看未读消息
 * */
public class UnreadMsg extends UserMessage {
    private String me;
     public UnreadMsg(String me) {
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