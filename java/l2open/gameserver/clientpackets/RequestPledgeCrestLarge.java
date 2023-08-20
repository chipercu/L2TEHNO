package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.CrestCache;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExPledgeCrestLarge;

public class RequestPledgeCrestLarge extends L2GameClientPacket
{
	// format: chd
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
		if(activeChar == null || activeChar.is_block || _crestId == 0)
			return;
		byte[] data = CrestCache.getPledgeCrestLarge(_crestId);
		if(data != null)
		{
			/*if(getClient().isLindvior())
				for(int i = 0; i <= 4; i++)
				{
					byte[] dest1 = new byte[14336];
					byte[] dest2 = new byte[8320];
					if (i < 4)
					{
						System.arraycopy(data, (14336 * i), dest1, 0, 14336);
						sendPacket(new ExPledgeCrestLarge(_crestId, dest1, i));
					}
					else
					{
						System.arraycopy(data, (14336 * i), dest2, 0, 8320);
						sendPacket(new ExPledgeCrestLarge(_crestId, dest2, i));
					}
				}
			else*/
				sendPacket(new ExPledgeCrestLarge(_crestId, data, 0));
		}
	}

	public boolean isFilter()
	{
		return false;
	}
}