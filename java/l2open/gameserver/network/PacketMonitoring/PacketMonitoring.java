package l2open.gameserver.network.PacketMonitoring;

import javolution.util.FastMap;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Created by a.kiperku
 * Date: 24.08.2023
 */

public class PacketMonitoring {

    private static final int maxPackedFromClientInTime = 10;
    private static final int maxFilterTime = 1000;

    private static final Logger _log = Logger.getLogger(PacketMonitoring.class.getName());

    public static PacketMonitoring getInstance() {
        return PacketMonitoring.LazyHolder.INSTANCE;
    }

    private static FastMap<L2GameClient, ClientMonitoring> clients = new FastMap<>();


    public int analyze(L2GameClient client, int packetId){

        if (client.getActiveChar() != null && client.getActiveChar().isBlocked()){

        }
        if (packetId == 14){
            return 1;
        }
        if (!clients.containsKey(client)){
            clients.put(client, new ClientMonitoring(System.currentTimeMillis(), 1, new Packet(packetId, System.currentTimeMillis())));
        }else {
            final ClientMonitoring clientMonitoring = clients.get(client);
            final Packet lastPacket = clientMonitoring.getLastPacket();
            if (lastPacket.getId() == packetId ){
                lastPacket.increaseFludCount();
                final long l = System.currentTimeMillis() - lastPacket.getReadTime();
                if (lastPacket.getFludCount() > 5){
                    if (l < 250){
                        System.out.println("flud");
                        lastPacket.reset();
                        client.getActiveChar().block();
                        client.getActiveChar().scriptRequest("Замечен флуд пакетами", "FludProtect", new Object[0]);
                        return 0;
                    }else {
                        lastPacket.reset();
                    }
                }
            }else {
                clients.get(client).setLastPacket(new Packet(packetId, System.currentTimeMillis()));
            }
            clients.get(client).increasePacketCount();
            if (clients.get(client).getPacketCount() > maxPackedFromClientInTime){
                if (System.currentTimeMillis() - clients.get(client).getLastCheckTime() < maxFilterTime){
                    System.out.println("Packet_Spammer [" + client.getIpAddr() + "] " + client.getLoginName());
                }else {
                    clients.get(client).setLastCheckTime(System.currentTimeMillis());
                    clients.get(client).setPacketCount(0);
                }
            }
        }
        return 1;
    }

    private static class LazyHolder {
        private static final PacketMonitoring INSTANCE = new PacketMonitoring();

        private LazyHolder() {
        }
    }


    public void handleIncompletePacket(L2GameClient client) {
        L2Player activeChar = client.getActiveChar();

        if (activeChar == null)
            _log.warning("Packet(2) not completed. Maybe cheater. IP:" + client.getIpAddr() + ", account:" + client.getLoginName());
        else
            _log.warning("Packet(2) not completed. Maybe cheater. IP:" + client.getIpAddr() + ", account:" + client.getLoginName() + ", character:" + activeChar.getName());

        client.onClientPacketFail();
    }


}
