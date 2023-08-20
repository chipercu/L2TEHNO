package l2open.gameserver.loginservercon.gspackets;

public class PlayerLogout extends GameServerBasePacket
{
	public PlayerLogout(String player)
	{
		writeC(0x03);
		writeS(player);
	}
}