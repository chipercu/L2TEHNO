package l2open.gameserver.loginservercon.gspackets;

import l2open.gameserver.loginservercon.AttLS;

public class BlowFishKey extends GameServerBasePacket
{
	public BlowFishKey(byte[] data, AttLS loginServer)
	{
		writeC(0x00);
		if(data == null || data.length == 0)
		{
			writeD(0);
			return;
		}

		try
		{
			data = loginServer.getRsa().encryptRSA(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		writeD(data.length);
		writeB(data);
	}
}