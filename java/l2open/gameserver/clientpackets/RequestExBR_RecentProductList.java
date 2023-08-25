package l2open.gameserver.clientpackets;

import l2open.gameserver.itemmall.ItemMall;
import l2open.gameserver.model.L2Player;

public class RequestExBR_RecentProductList extends L2GameClientPacket
{
    public void readImpl() {
    }

    public void runImpl() {
        L2Player player = getClient().getActiveChar();
        if (player == null)
            return;
        ItemMall.getInstance().recentProductList(player);
    }
}