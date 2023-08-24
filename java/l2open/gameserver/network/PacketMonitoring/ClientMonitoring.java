package l2open.gameserver.network.PacketMonitoring;

/**
 * Created by a.kiperku
 * Date: 24.08.2023
 */

public class ClientMonitoring {

    private Long lastCheckTime;
    private int packetCount;
    private int packetId = 0;

    private Packet lastPacket;

    public ClientMonitoring(Long lastCheck, int packetCount, Packet lastPacket) {
        this.lastCheckTime = lastCheck;
        this.packetCount = packetCount;
        this.lastPacket = lastPacket;
    }

    public Packet getLastPacket() {
        return lastPacket;
    }

    public void setLastPacket(Packet lastPacket) {
        this.lastPacket = lastPacket;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public int increasePacketCount(){
        this.packetCount ++;
        return this.packetCount;
    }

    public Long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public void setPacketCount(int packetCount) {
        this.packetCount = packetCount;
    }
}
