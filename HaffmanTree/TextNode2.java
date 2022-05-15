import javax.swing.tree.TreeNode;
import java.io.*;
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



public class TextNode implements CompressionCode,DecompressCode,TreeMethods{
    public void main(String[] args){
        String content ="ddddddddddddd";
        byte[] contentBytes = content.getBytes();  //转换成Byte数组
        List<Node> nodes = getNodes(contentBytes);
        byte[] huffmanCodeBytes = huffmanZip(contentBytes);
        byte[] source = decode(huffmanCodes, huffmanCodeBytes);
        System.out.println();
        System.out.println("原来的字符串 = " + new String(source));
        System.out.println();
        System.out.println(nodes.toString());
    }

        /**
     * 通过list创建哈夫曼树
     * */
        @Override
    public Node createHuffmanTree(List<Node> nodes){ // List<T>集合泛型类
        //通过while循环 经过反复遍历排序删除从下而上将列表元素搭建为哈夫曼树
        while(nodes.size() > 1) { // 传入的list节点>1 才构建二叉树
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
    /***
     * 创建一个函数huffmanZip 封装好实现细节
     */
    @Override
    public byte[] huffmanZip(byte[] bytes){
        System.out.println("处理前的字节数组："+Arrays.toString(bytes)+" 长度 = "+bytes.length);
        List<Node> nodes = getNodes(bytes);
        // 创建哈夫曼树
        Node huffmanTreeRoot = createHuffmanTree(nodes);
        // 对应的哈夫曼编码
        Map<Byte, String> huffmanCodes = getCodes(huffmanTreeRoot);
        // 根据生成的哈夫曼编码，得到压缩后的数组
        byte[] huffmanCodeBytes = zip(bytes, huffmanCodes);
        System.out.println("处理后的字节数组："+Arrays.toString(huffmanCodeBytes)+" 长度 = "+huffmanCodeBytes.length);
        preOrder(huffmanTreeRoot);
        return huffmanCodeBytes;
    }

    @Override
    public List<Node> getNodes(byte[] bytes){
        // 创建Arraylist
        ArrayList<Node> nodes = new ArrayList<Node>();
        // 创建一个HashMap 遍历整个byte数组，key保存当前的byte的值，value用于统计相同字节出现的频率，再把这个HashMap中的节点转换成Node节点
        Map<Byte, Integer> counts = new HashMap();
        for(byte b : bytes){
            if(!counts.containsKey(b)){
                counts.put(b, 1);
            }else{
                counts.put(b, counts.get(b) + 1);
            }
        }
        // 把每一个键对转成一个Node对象，并加入到nodes集合中
        for(Map.Entry<Byte, Integer> entry : counts.entrySet()){
            nodes.add(new Node(entry.getKey(),entry.getValue()));
        }
        return nodes;
    }

    /*-----------------------压缩---------------------------------------*/


    // 声明哈夫曼编码表，可在多个方法中共同使用
    public Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();

    /**
     * 生成树对应的哈夫曼编码
     * 思路：将哈夫曼编码存放在map中 Map<Byte,String>
     * */
    //getCodes方法
    @Override
    public Map<Byte, String> getCodes(Node root) {
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
    @Override
    public void getCodes(Node node, String code, StringBuilder builder) {
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
    @Override
    public byte[] zip(byte[] bytes, Map<Byte, String> huffmanCode) {
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
     * 返回处理后的字节数组
     */
    // 前序遍历（递归）
    @Override
    public void preOrder(Node node) {
        if (node == null)  return;
        System.out.printf("%s ", node.data);
        preOrder(node.left);
        preOrder(node.right);
    }

    // 前序遍历（非递归）
    @Override
    public void preOrderWithoutRecursion() {
        Stack nodes = new Stack<>(); // 查看栈顶的数据，返回栈顶元素，底层数组中最后一个元素，同时把栈中的该元素删除
        nodes.push(nodes.get(0));  //nodes.get(0) --> root
        while (!nodes.isEmpty()) {
            Node current = (Node)nodes.pop();
            System.out.printf("%s ", current.data);

            if (current.right != null) {
                nodes.push(current.right);
            }
            if (current.left != null) {
                nodes.push(current.left);
            }
        }
    }

    /*-----------------------解压---------------------------------------*
    /*
    * 1、字节数组转换成二进制字符串
    * 2、逆向处理生成好的哈夫曼编码表
    * 3、根据逆向生成的哈夫曼表查询生成原来的字节数组
    */

    /**       1
     * 将一个byte转成一个二进制的字符串
     * b传入的一个字节
     * flag标志是否为最后一个字节(true表示不是，false表示是)
     * return --> b对应对二进制对字符串(按补码返回)
     */
    @Override
    public String byteToBitString(boolean flag, byte b) {
        int temp = b; // 将b转成int
        temp |= 256;
        String str = Integer.toBinaryString(temp);// 返回的是temp对应的二进制补码
        if (flag || (flag == false && endLen == 0)) {
            //字符串的截取，只拿后八位
            return str.substring(str.length() - 8);
        } else {
            //不满8bit有多少位拿多少位
            return str.substring(str.length() - endLen);
        }
    }

    @Override
    public byte[] decode(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {

        /**      2
         * 逆向处理生成好的哈夫曼编码表
         * 把哈夫曼编码表进行调换，因为要进行反向查询
         */
        Map<String, Byte> map = new HashMap<String, Byte>();
        for(Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        /**         3
         * 根据逆向生成的哈夫曼表查询生成原来的字节数组
         * huffmanCodes 哈夫曼编码表
         * huffmanBytes 哈夫曼编码得到对字节数组
         * return 原来对字符串对应对数组
         */
        // 1.先得到 huffmanBytes 对应对二进制字符串，形式10101000...
        StringBuilder builder = new StringBuilder();
        // 2.将byte数组转成二进制的字符串
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            // 判断是不是最后一个字节
            boolean flag = (i == huffmanBytes.length - 1);
            builder.append(byteToBitString(!flag, b));
        }
        // 创建集合，存放byte
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < builder.length();) {
            int count = 1; // 小的计数器
            boolean flag = true;
            Byte b = null;
            while (flag) {
                // 取出一个bit '1'或者'0'
                String key = builder.substring(i, i + count); // i 不动 让count移动，直到匹配到一个字符
                b = map.get(key);
                if (b == null) {// 没有匹配到
                    count++;
                } else {
                    flag = false;
                }
            }
            list.add(b);
            i = i + count;
        }
        // 当for循环结束以后，list中存放了所有当字符
        // 把list中的数据放入到byte[] 并返回
        byte b[] = new byte[list.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = list.get(i);
        }
        return b;
    }

    /*--------------------------------------------------------------*/
    //二叉树的镜像（递归）
    public void mirrorTree(Node root){
        if(root == null ) return;

        //交换该节点指向的左右节点
        Node temp=root.left;
        root.left=root.right;
        root.right=temp;

        //对其左右孩子进行镜像处理
        mirrorTree(root.left);
        mirrorTree(root.right);
    }

    //二叉树的镜像（非递归：层次遍历）
    @Override
    public void Swap(Node root)
    {
        Node temp;
        temp=root.right;
        root.right=root.left;
        root.left=temp;
    }
    @Override
    public void mirrorTreeWithQueue(Node root)
    {
        if(root==null)
            return;
        //如果树为 null 直接返回。否则将根节点入队列
        Queue<Node> queue= new LinkedList<Node>();
        queue.add(root);
        while(!queue.isEmpty())
        {
            //队列不为空时，节点出队，交换该节点的左右子树
            Node root1=queue.poll();
            Swap(root);
            if(root1.right!=null)
            {
                queue.add(root1.right);
                //如果左子树不为 null 入队
            }
            if(root1.left!=null)
            {
                queue.add(root1.left);
                //如果右子树不为 null 入队
            }
        }
    }

    //层序遍历二叉树（广度优先搜索）
    @Override
    public void levelTree(Node root) {
        if (root == null)  return;

        Queue<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node current;

        while(!q.isEmpty()){
            current = q.peek();
            System.out.print(current.data + " ");
            if(current.left != null){
                q.add(current.left);
            }
            if(current.right != null){
                q.add(current.right);
            }
            q.poll();
        }
    }
}
