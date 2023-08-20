package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.PledgeCrest;

public class RequestPledgeCrest extends L2GameClientPacket
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
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _crestId == 0 || activeChar.is_block)
			return;
		byte[] data = CrestCache.getPledgeCrest(_crestId);
		if(data != null)
		{
			PledgeCrest pc = new PledgeCrest(_crestId, data);
			sendPacket(pc);
		}
	}

	public boolean isFilter()
	{
		return false;
	}
}