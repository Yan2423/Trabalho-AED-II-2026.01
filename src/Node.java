
public class Node {
    PacketRule data;
    Node left;
    Node right;
    Node parent;
    
    boolean color;

    int height;

    public Node (PacketRule data){
        this.data = data;
    }
}
