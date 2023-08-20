package l2open.loginserver.gameservercon.lspackets;

public class KickPlayer extends ServerBasePacket
{
	public KickPlayer(String account)
	{
		writeC(0x04);
		writeS(account);
	}
}