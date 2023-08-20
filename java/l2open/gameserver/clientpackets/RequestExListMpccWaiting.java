package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExListMpccWaiting;

public class RequestExListMpccWaiting extends L2GameClientPacket
{
	private int _listId;
	private int _locationId;
	private boolean _allLevels;

	@Override
	public void readImpl()
	{
		_listId = readD();
		_locationId = readD();
		_allLevels = readD() == 1;
	}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		player.sendPacket(new ExListMpccWaiting(player, _listId, _locationId, _allLevels));
	}
}