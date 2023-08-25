package l2open.gameserver.loginservercon.lspackets;

import l2open.gameserver.loginservercon.AttLS;
import l2open.gameserver.network.L2GameClient;

public class PointConnectionG extends LoginServerBasePacket
{
	public PointConnectionG(byte[] decrypt, AttLS loginServer)
	{
		super(decrypt, loginServer);
	}
	@Override
	public void read()
	{
		String acc = readS();
		int point = readD();
	}
}
