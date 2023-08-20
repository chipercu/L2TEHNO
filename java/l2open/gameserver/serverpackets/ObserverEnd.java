package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.util.Location;

public class ObserverEnd extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverEnd(L2Player observer)
	{
		_loc = observer.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xec);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}