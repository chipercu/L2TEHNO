package l2open.gameserver.loginservercon;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;

/**
 * @Author: Death
 * @Date: 13/11/2007
 * @Time: 20:46:51
 */
public class KickPlayerInGameTask extends l2open.common.RunnableImpl
{
	private final L2GameClient client;

	public KickPlayerInGameTask(L2GameClient client)
	{
		this.client = client;
	}

	public void runImpl()
	{
		L2Player activeChar = client.getActiveChar();

		if(activeChar != null)
			activeChar.logout(false, false, true, true);
		else
		{
			client.sendPacket(Msg.ServerClose(null));
			client.closeNow(false);
		}
	}
}
