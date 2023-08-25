package l2open.gameserver.loginservercon.gspackets;

public class ChangeAccountPoints extends GameServerBasePacket
{
	public ChangeAccountPoints(String account, int points, String comments)
	{
		writeC(0x0c);
		writeD(points);
		writeS(account);
		writeS(comments);
	}
}