package quests.common;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.RadarControl;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */

public class CommonComponent {


    public static void addRadarToNpc(L2Player player, L2NpcInstance npc){
        player.sendPacket(new RadarControl(2, 1, npc.getLoc()));// Убираем флажок на карте и стрелку на компасе
        player.sendPacket(new RadarControl(0, 2, npc.getLoc()));// Ставим флажок на карте и стрелку на компасе
        //player.radar.addMarker(npc.getX(), npc.getY(), npc.getZ());
    }
}
