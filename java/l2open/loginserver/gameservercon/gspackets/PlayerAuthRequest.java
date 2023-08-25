package l2open.loginserver.gameservercon.gspackets;

import l2open.config.ConfigValue;
import l2open.loginserver.L2LoginClient;
import l2open.loginserver.LoginController;
import l2open.loginserver.SessionKey;
import l2open.loginserver.gameservercon.AttGS;
import l2open.loginserver.gameservercon.lspackets.PlayerAuthResponse;
import l2open.loginserver.serverpackets.LoginFail.LoginFailReason;

import java.util.logging.Logger;

public class PlayerAuthRequest extends ClientBasePacket
{
	private static final Logger _log = Logger.getLogger(PlayerAuthRequest.class.getName());

	public PlayerAuthRequest(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		String account = readS().trim();
		int playOkId1 = readD();
		int playOkId2 = readD();
		int loginOkId1 = readD();
		int loginOkId2 = readD();

		L2LoginClient client = LoginController.getInstance().getAuthedClient(account);

		if(client == null)
		{
			_log.warning("Client is null for account " + account);
			sendPacket(new PlayerAuthResponse(account));
			return;
		}

		SessionKey key = client.getSessionKey();

		int lPlayOk1 = key.playOkID1;
		int lPlayOk2 = key.playOkID2;
		int lLoginOk1 = key.loginOkID1;
		int lLoginOk2 = key.loginOkID2;

		boolean isAuthedOnLs;
		if(ConfigValue.ShowLicence)
			isAuthedOnLs = playOkId1 == lPlayOk1 && playOkId2 == lPlayOk2 && loginOkId1 == lLoginOk1 && loginOkId2 == lLoginOk2;
		else
			isAuthedOnLs = playOkId1 == lPlayOk1 && playOkId2 == lPlayOk2;

		sendPacket(new PlayerAuthResponse(client, isAuthedOnLs));
		getGameServer().removeAccountFromGameServer(account);
		LoginController.getInstance().removeAuthedLoginClient(account);//.close(LoginFailReason.REASON_NO_MESSAGE);
	}
}
