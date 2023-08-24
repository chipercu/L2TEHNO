package l2open.gameserver.network.PacketMonitoring;

/**
 * Created by a.kiperku
 * Date: 24.08.2023
 */

public class Packet {
    private int id;
    private long readTime;
    private int fludCount;

    public Packet(int lastPacketId, long lastPacketIdTime) {
        this.id = lastPacketId;
        this.readTime = lastPacketIdTime;
    }

    public void reset(){
        setFludCount(0);
        setReadTime(System.currentTimeMillis());
    }

    public void increaseFludCount(){
        this.fludCount++;
    }

    public int getFludCount() {
        return fludCount;
    }

    public void setFludCount(int fludCount) {
        this.fludCount = fludCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }
}
