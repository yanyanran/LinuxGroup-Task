import java.util.Map;

interface DecompressCode {
    public abstract String byteToBitString(boolean flag, byte b);
    public abstract byte[] decode(Map<Byte, String> huffmanCodes, byte[] huffmanBytes);
}