package l2open.gameserver.loginservercon.gspackets;

/**
 * @Author: SYS
 * @Date: 10/4/2008
 */
public class LockAccountIP extends GameServerBasePacket
{

	public LockAccountIP(String account, String IP, int time)
	{
		writeC(0x0b);
		writeS(account);
		writeS(IP);
		writeD(time);
	}
}