package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.cache.ItemInfoCache;
import l2open.gameserver.serverpackets.ExRpItemLink;
import l2open.gameserver.serverpackets.ExRpItemLink.ItemInfo;

public class RequestExRqItemLink extends L2GameClientPacket
{
	// format: (ch)d
	int _item;

	@Override
	public void readImpl()
	{
		_item = readD();
	}

	@Override
	public void runImpl()
	{
		ItemInfo item;
		if((item = ItemInfoCache.getInstance().get(_item)) == null)
			sendPacket(Msg.ActionFail);
		else
		{
			sendPacket(new ExRpItemLink(item));
			if(getClient() != null && getClient().getActiveChar() != null && getClient().getActiveChar().isGM())
				getClient().getActiveChar().sendMessage("ItemId: " + item.getItemId());
		}
	}
}