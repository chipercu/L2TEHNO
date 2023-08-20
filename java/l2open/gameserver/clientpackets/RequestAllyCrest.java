package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.serverpackets.AllianceCrest;

public class RequestAllyCrest extends L2GameClientPacket
{
	// format: cd

	private int _crestId;

	@Override
	public void readImpl()
	{
		_crestId = readD();
	}

	@Override
	public void runImpl()
	{
		if(_crestId == 0)
			return;
		byte[] data = CrestCache.getAllyCrest(_crestId);
		if(data != null)
		{
			AllianceCrest ac = new AllianceCrest(_crestId, data);
			sendPacket(ac);
		}
	}

	public boolean isFilter()
	{
		return false;
	}
}