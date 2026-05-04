public class PacketRule {
    int id;
    String srcIp;
    String destIp;
    int priority;

    public PacketRule(int id, String srcIp, String destIp, int priority) {
        this.id = id;
        this.srcIp = srcIp;
        this.destIp = destIp;
        this.priority = priority;
    }
}