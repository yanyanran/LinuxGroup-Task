package messages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import messages.UserMessage;
import messages.settoservermsg.LoginMsg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCode extends ByteToMessageCodec<UserMessage> {

    private int messageType;
    private int sequenceId;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UserMessage msg, ByteBuf out) throws Exception {
        // 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 1 字节的版本
        out.writeByte(1);
        // 1 字节的序列化方式 0:jdk
        out.writeByte(0);
        // 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 4 个字节的请求序号
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充使其满足2的n次方
        out.writeByte(0xff);
        // 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        byte serializerType = in.readByte();
        int length = in.readInt();
        in.readByte();
        in.readByte();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);

        if(serializerType == 0) {
            // jdk序列化
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            UserMessage message = (UserMessage) ois.readObject();
            out.add(message);
        }
    }
    private static final Map<Integer,Class<?>> messageClasses = new HashMap<>();

    public static Class<?> getMessageClass(int messageType){
        return messageClasses.get(messageType);
    }
    
    public int getSequenceId(){
        return sequenceId;
    }
}