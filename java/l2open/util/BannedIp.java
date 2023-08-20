package l2open.util;

public class BannedIp
{
	public String ip;
	public String admin;
	public int expireTime = -1;

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof BannedIp))
			return false;

		BannedIp b = (BannedIp) obj;

		return ip.equals(b.ip) && admin.equals(b.admin);
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 13 * hash + (ip != null ? ip.hashCode() : 0);
		hash = 13 * hash + (admin != null ? admin.hashCode() : 0);
		hash = 13 * hash + expireTime;
		return hash;
	}
}
