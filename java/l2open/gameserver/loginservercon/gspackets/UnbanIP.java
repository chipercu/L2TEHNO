package l2open.gameserver.loginservercon.gspackets;

public class UnbanIP extends GameServerBasePacket
{
	public UnbanIP(String ip)
	{
		writeC(0x0a);
		writeS(ip);
	}
}