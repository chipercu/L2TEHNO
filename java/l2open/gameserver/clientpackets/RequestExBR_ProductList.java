package l2open.gameserver.clientpackets;

import l2open.gameserver.itemmall.ItemMall;
import l2open.gameserver.model.L2Player;

public class RequestExBR_ProductList extends L2GameClientPacket
{
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        L2Player player = getClient().getActiveChar();

        if (player == null)
            return;
        else
            ItemMall.getInstance().showList(player);
    }
}