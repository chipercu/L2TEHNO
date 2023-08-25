package l2open.gameserver.loginservercon.gspackets;

public class BanIP extends GameServerBasePacket
{
	public BanIP(String ip, String admin)
	{
		writeC(0x07);
		writeS(ip);
		writeS(admin);
	}
}