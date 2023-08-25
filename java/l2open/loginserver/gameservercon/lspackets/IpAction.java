package l2open.loginserver.gameservercon.lspackets;

public class IpAction extends ServerBasePacket
{
	public IpAction(String ip, boolean ban, String gm)
	{
		writeC(0x07);
		writeS(ip);
		writeC(ban ? 1 : 0);
		if(ban)
			writeS(gm);
	}
}
