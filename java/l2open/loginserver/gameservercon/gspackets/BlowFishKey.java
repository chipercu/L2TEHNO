package l2open.loginserver.gameservercon.gspackets;

import l2open.loginserver.gameservercon.AttGS;

public class BlowFishKey extends ClientBasePacket
{
	public BlowFishKey(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		int keyLength = readD();
		if(keyLength == 0)
		{
			getGameServer().initBlowfish(null);
			return;
		}

		byte[] data = readB(keyLength);
		try
		{
			data = getGameServer().RSADecrypt(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		getGameServer().initBlowfish(data);
	}
}
