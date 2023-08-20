package l2open.loginserver;

import l2open.util.NetList;

public class Lock
{
	private NetList ips;

	public Lock()
	{}

	public void addIP(String ip)
	{
		if(ips == null)
			ips = new NetList();
		ips.AddNet(ip);
	}

	public boolean checkIP(String ip)
	{
		if(ips == null)
			return false;
		return ips.isIpInNets(ip);
	}
}