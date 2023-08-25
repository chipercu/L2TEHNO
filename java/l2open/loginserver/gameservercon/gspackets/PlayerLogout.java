package l2open.loginserver.gameservercon.gspackets;

import l2open.config.ConfigValue;
import l2open.loginserver.GameServerTable;
import l2open.loginserver.LoginController;
import l2open.loginserver.gameservercon.AttGS;

import java.util.logging.Logger;

public class PlayerLogout extends ClientBasePacket
{
	public static final Logger log = Logger.getLogger(PlayerLogout.class.getName());

	public PlayerLogout(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String account = readS();

		getGameServer().removeAccountFromGameServer(account);
		LoginController.getInstance().removeAuthedLoginClient(account);

		if(ConfigValue.Debug)
			log.info("Player " + account + " logged out from gameserver [" + getGameServer().getServerId() + "] " + GameServerTable.getInstance().getServerNameById(getGameServer().getServerId()));
	}
}