import java.util.List;

interface TreeMethods{
    public abstract Node createHuffmanTree(List<Node> nodes);
    public abstract List<Node> getNodes(byte[] bytes);
    public abstract void preOrder(Node node);
    public abstract void preOrderWithoutRecursion();
    public abstract void mirrorTree(Node root);
    public abstract void Swap(Node root);
    public abstract void mirrorTreeWithQueue(Node root);
    public abstract void levelTree(Node root);
}