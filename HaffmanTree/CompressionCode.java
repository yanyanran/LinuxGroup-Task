import java.util.List;
import java.util.Map;

interface CompressionCode{
    public abstract byte[] huffmanZip(byte[] bytes);
    public abstract Map<Byte, String> getCodes(Node root);
    public abstract void getCodes(Node node, String code, StringBuilder builder);
    public abstract byte[] zip(byte[] bytes, Map<Byte, String> huffmanCode);
}