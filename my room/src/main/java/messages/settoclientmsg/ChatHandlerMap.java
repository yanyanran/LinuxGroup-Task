package messages.settoclientmsg;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * map --> 绑定channel和对应帐号（聊天用）
 * key、channel需要分开两个map
 * */
public final class ChatHandlerMap {
    private static Map<String, Channel> channelMap = new HashMap<>();
    private static Map<Channel,String> userMap = new HashMap<>();

    public static void add(String user, Channel chatHandler) {
        channelMap.put(user,chatHandler);
    }

    public static String getUser(Channel channel) {
        // 检查map是否存在指定的key对应的映射关系
        if(userMap.containsKey(channel)){
            return userMap.get(channel);
        }else {
            return null;
        }
    }

    public static Channel getChannel(String user) {
        // 检查map是否存在指定的key对应的映射关系
        if(channelMap.containsKey(user)){
            return channelMap.get(user);
        }else {
            return null;
        }
    }
}