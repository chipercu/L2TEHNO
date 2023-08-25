package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestGoodsInventoryInfo  extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{

	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		//player.sendPacket(new ExGoodsInventoryInfo(player));
	}
}
