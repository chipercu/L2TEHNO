package l2open.loginserver.gameservercon.lspackets;

/**
 * @Author: Abaddon
 */
public class MoveCharToAccResponse extends ServerBasePacket
{
	public MoveCharToAccResponse(String player, int response)
	{
		writeC(0x08);
		writeS(player);
		// 0 - wrong pass, 1 - wrong new acc, 2 - success
		writeD(response);
	}
}
