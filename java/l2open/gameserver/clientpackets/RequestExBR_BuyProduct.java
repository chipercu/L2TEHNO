package l2open.gameserver.clientpackets;

import l2open.gameserver.itemmall.ItemMall;
import l2open.gameserver.model.L2Player;

public class RequestExBR_BuyProduct extends L2GameClientPacket
{
    private int iProductID;
    private int iAmount;

    @Override
    public void readImpl() {
        iProductID = readD();
        iAmount = readD();
    }

    @Override
    public void runImpl() {
        L2Player player = getClient().getActiveChar();

        if (player == null)
            return;
        else
            ItemMall.getInstance().requestBuyItem(player, iProductID, iAmount);
    }
}