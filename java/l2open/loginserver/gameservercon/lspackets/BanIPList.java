package l2open.loginserver.gameservercon.lspackets;

import javolution.util.FastList;
import l2open.loginserver.IpManager;
import l2open.util.BannedIp;

public class BanIPList extends ServerBasePacket
{
	public BanIPList()
	{
		FastList<BannedIp> baniplist = IpManager.getInstance().getBanList();
		writeC(0x05);
		writeD(baniplist.size());
		for(BannedIp ip : baniplist)
		{
			writeS(ip.ip);
			writeS(ip.admin);
		}
		FastList.recycle(baniplist);
	}
}
