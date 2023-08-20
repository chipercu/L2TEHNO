package l2open.gameserver.loginservercon.gspackets;

public class ChangeAccessLevel extends GameServerBasePacket
{
	public ChangeAccessLevel(String player, int access, String comments, int banTime)
	{
		writeC(0x04);
		writeD(access);
		writeS(player);
		writeS(comments);
		writeD(banTime);
	}
}