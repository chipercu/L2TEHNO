package l2open.gameserver.loginservercon;

import l2open.gameserver.network.L2GameClient;

/**
 * @Author: Death
 * @Date: 13/11/2007
 * @Time: 20:14:14
 */
public class KickWaitingClientTask extends l2open.common.RunnableImpl
{
	private final L2GameClient client;

	public KickWaitingClientTask(L2GameClient client)
	{
		this.client = client;
	}

	public void runImpl()
	{
		client.closeNow(false);
	}
}
