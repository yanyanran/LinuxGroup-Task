package messages.settoclientmsg;

import messages.UserMessage;

import java.util.Map;

// server --> client
public class ServerToClientMsg extends UserMessage {
        private boolean success;
        private String reason;
        private int ServerToClientMsg;
        int MessageType = ServerToClientMsg;
        Map<Integer,String> msgMap;

        public ServerToClientMsg(boolean success,String reason) {
            this.success = success;
            this.reason = reason;
        }

        public ServerToClientMsg(boolean success,String reason,String username) {
            this.success = success;
            this.reason = reason;
            this.username = username;
        }

        public ServerToClientMsg(boolean success, Map<Integer, String> msgMap) {
            this.success = success;
            this.msgMap = msgMap;
         }

        public void setMessageType(int msgType) {
                this.MessageType = msgType;
        }

        public boolean getSuccess() {
            return this.success;
        }

        public String getReason() {
            return this.reason;
        }

        public Map<Integer,String> getMsgMap() {
            return this.msgMap;
        }

        public static String getMe() {
            return username;
        }

        public int getMessageType() {
            return this.MessageType;
        }

        public String toString() {
            return "success = " + success + ", reason = " + reason + "me = " + username;
        }
}