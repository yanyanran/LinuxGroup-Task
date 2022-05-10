import javax.swing.tree.TreeNode;
import java.util.*;

    /**
     * 构造树节点node
     * */
class Node implements Comparable<Node>{
    Byte data;  //存数据
    int weight;  //权值
    Node left;  //指向左子节点
    Node right;  //指向右子节点

    public Node(Byte data,int weight){
        this.data = data;
        this.weight = weight;
    }

    @Override
    public int compareTo(Node o){
        return this.weight - o.weight;
    }

    @Override
    public String toString(){
        return "Node [data = " + data + ", weight = " +weight + "]";
    }
}

public class TextNode {
    public static void main(String[] args) {
        String content = "whatwhatwhat";
        byte[] contentBytes = content.getBytes();  //转换成Byte数组
        List<Node> nodes = getNodes(contentBytes);
        byte[] huffmanCodeBytes = huffmanZip(contentBytes);
        System.out.println(nodes.toString());
    }

    private static List<Node> getNodes(byte[] bytes){
        // 创建Arraylist
        ArrayList<Node> nodes = new ArrayList<Node>();

        // 创建一个HashMap 遍历整个byte数组，key保存当前的byte的值，value用于统计相同字节出现的频率，再把这个HashMap中的节点转换成Node节点
        Map<Byte, Integer> counts = new HashMap();
        for(byte b : bytes){
            if(!counts.containsKey(b)){
                counts.put(b, 1);
            }else {
                counts.put(b, counts.get(b) + 1);
            }
        }
        // 把每一个键对转成一个Node对象，并加入到nodes集合中
        for(Map.Entry<Byte, Integer> entry : counts.entrySet()){
            nodes.add(new Node(entry.getKey(),entry.getValue()));
        }
        return nodes;
    }

    /**
     * 通过list创建哈夫曼树
     * */
    public static Node createHuffmanTree(List<Node> nodes) { // List<T>集合泛型类
        //通过while循环 经过反复遍历排序删除从下而上将列表元素搭建为哈夫曼树
        while (nodes.size() > 1) { // 传入的list节点>1 才构建二叉树
            // 对集合里的元素排序
            Collections.sort(nodes);
            // 取出权值最小的俩节点
            Node left = nodes.get(0);   // 获取指定索引0处的元素
            Node right = nodes.get(1);  // 获取指定索引1处的元素
            // 构建一个新的二叉树节点，并初始化为null
            Node parent = new Node(null, left.weight + right.weight);
            // 把两个节点挂在一个非子节点上
            parent.left = left;
            parent.right = right;
            // 移除清空左右两个节点
            nodes.remove(left);
            nodes.remove(right);
            // 把这个非叶子节点加入到nodes中
            nodes.add(parent);
        }
        return nodes.get(0); // 返回root节点
    }

    // 声明哈夫曼编码表，可在多个方法中共同使用
    public static Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();

    /**
     * 生成树对应的哈夫曼编码
     * 思路：将哈夫曼编码存放在map中 Map<Byte,String>
     * */
    //getCodes方法
    private static Map<Byte, String> getCodes(Node root) {
        // 特殊情况：空节点
        if (root == null)  return null;
        // 特殊情况：只有root节点
        if (root.left == null && root.right == null)
            huffmanCodes.put(root.data, "0");

        // 在生成哈夫曼编码表时，需要去拼接路径，定义一个StringBuilder存储叶子节点的路径
        StringBuilder builder = new StringBuilder();

        // 处理左子树
        getCodes(root.left, "0", builder);
        // 处理右子树
        getCodes(root.right, "1", builder);

        return huffmanCodes;
    }

    /**
     * 重载getCodes方法
     * 功能：将传入的node节点的所有叶子节点的哈夫曼得到，并放入huffmanCodes中
     */
    private static void getCodes(Node node, String code, StringBuilder builder) {
        StringBuilder builder2 = new StringBuilder(builder); // 重建StringBuilder

        // 追加一个字符串code --> builder2
        builder2.append(code);

        if (node != null) {
            // 判断当前是叶子节点还是非叶子节点
            if (node.data == null) {
                // 左边递归
                getCodes(node.left, "0", builder2);
                // 右边递归
                getCodes(node.right, "1", builder2);
            } else {
                // 找到叶子节点
                huffmanCodes.put(node.data, builder2.toString());
            }
        }
    }

    /**
     * 将字符串对应的byte[]数组，通过生成哈夫曼编码表，返回一个哈夫曼编码处理后的byte[]数组
     */
    //最后一个字节如果不满8bit，要用一个endLen记录下它的长度，在后面逆向解压中要用到
    static int endLen;
    public static byte[] zip(byte[] bytes, Map<Byte, String> huffmanCode) {
        // 利用huffmanCodes将byte转成哈夫曼对应的字符串
        StringBuilder builder = new StringBuilder("");
        // 遍历数组
        for (byte b : bytes) {
            builder.append(huffmanCodes.get(b));
        }
        // 将二进制转成byte[]
        // 统计返回的哈夫曼编码有多长
        int len = (builder.length() % 8) == 0 ? builder.length() / 8 : builder.length() / 8 + 1;
        endLen = builder.length() % 8;
        // 创建 存储后的bytes压缩数组
        byte[] huffmanCodeBytes = new byte[len];
        // 记录是第几个byte
        int index = 0;

        for (int i = 0; i < builder.length(); i += 8) {// 每8位-->一个byte，所以+8
            String strByte;

            // 两种情况i+8超过最后位置和不超过的分别赋值
            strByte = i + 8 > builder.length() ? builder.substring(i) : builder.substring(i, i + 8);

            // 后面一个参数2表示转换成二进制
            huffmanCodeBytes[index++] = (byte) Integer.parseInt(strByte, 2);
        }
        return huffmanCodeBytes;
    }
    //
    /**
     *
     * 创建一个接口函数封装好实现的细节
     * @return 返回处理后的字节数组
     */
    // 前序遍历（递归）
    static void preOrder(Node node) {
        if (node == null)  return;
        System.out.printf("%s ", node.data);
        preOrder(node.left);
        preOrder(node.right);
    }

 /*   // 前序遍历（非递归）
    public void preOrderWithoutRecursion() {
        Stack nodes = new Stack<>(); // 查看栈顶的数据，返回栈顶元素，底层数组中最后一个元素，同时把栈中的该元素删除
        nodes.push(nodes.get(0));  //nodes.get(0) --> root
        while (!nodes.isEmpty()) {
            Node current = nodes.pop();
            System.out.printf("%s ", current.data);

            if (current.right != null) {
                nodes.push(current.right);
            }
            if (current.left != null) {
                nodes.push(current.left);
            }
        }
    }
  */
    public static byte[] huffmanZip(byte[] bytes) {
        System.out.println("处理前的字节数组："+Arrays.toString(bytes)+" 长度="+bytes.length);
        List<Node> nodes = getNodes(bytes);
        // 创建哈夫曼树
        Node huffmanTreeRoot = createHuffmanTree(nodes);
        // 对应的哈夫曼编码
        Map<Byte, String> huffmanCodes = getCodes(huffmanTreeRoot);
        // 根据生成的哈夫曼编码，得到压缩后的数组
        byte[] huffmanCodeBytes = zip(bytes, huffmanCodes);
        System.out.println("处理后的字节数组："+Arrays.toString(huffmanCodeBytes)+" 长度="+huffmanCodeBytes.length);
        preOrder(huffmanTreeRoot);
        return huffmanCodeBytes;
    }
}

