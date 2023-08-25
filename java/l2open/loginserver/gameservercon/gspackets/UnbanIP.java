package l2open.loginserver.gameservercon.gspackets;

import l2open.loginserver.IpManager;
import l2open.loginserver.gameservercon.AttGS;
import l2open.loginserver.gameservercon.GSConnection;
import l2open.loginserver.gameservercon.lspackets.BanIPList;
import l2open.loginserver.gameservercon.lspackets.IpAction;

public class UnbanIP extends ClientBasePacket
{
	public UnbanIP(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String ip = readS();
		IpManager.getInstance().UnbanIp(ip);

		GSConnection.getInstance().broadcastPacket(new BanIPList());
		GSConnection.getInstance().broadcastPacket(new IpAction(ip, false, ""));
	}
}