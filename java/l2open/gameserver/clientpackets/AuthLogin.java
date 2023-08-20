package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.SessionKey;
import l2open.gameserver.model.*;
import l2open.gameserver.network.L2GameClient;

/**
 * cSddddd
 * cSdddddQ
 * loginName + keys must match what the loginserver used.
 */
public class AuthLogin extends L2GameClientPacket
{
	private String _loginName;
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	private byte _lang;

	private byte[] _data;

	@Override
	public void readImpl()
	{
				_log.info("RequestAuthLogin: "+getByteBuffer().remaining());

		_loginName = readS(32).toLowerCase().trim();
				_log.info("RequestAuthLogin: "+getByteBuffer().remaining());

		_playKey2 = readD();
		_playKey1 = readD();
		_loginKey1 = readD();
		_loginKey2 = readD();
		_lang = (byte)readD();
		//readQ(); unk
		// ignore the rest
		if(ConfigValue.CCPGuardEnable)
		{
			_data = new byte[ConfigValue.CCPGuardSize2];
			ccpGuard.Protection.doReadAuthLogin(getClient(), _buf, _data);
		}
		else
			_buf.clear();
	}

	@Override
	public void runImpl()
	{
		if(ConfigValue.CCPGuardEnable)
			if(!ccpGuard.Protection.doAuthLogin(getClient(), _data, _loginName))
				return;
		SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);

		final L2GameClient client = getClient();
		client.client_lang = _lang;
		client.setSessionId(key);
		client.setLoginName(_loginName);
		if(ConfigValue.GGCheck)
		{
			client.sendPacket(Msg.GameGuardQuery);
			ThreadPoolManager.getInstance().schedule(new GGTest(client), 500, true);
		}
		LSConnection.getInstance(client.getLSId()).addWaitingClient(client);
		for(L2Player cha : L2ObjectsStorage.getPlayers())
		{
			try
			{
				if(cha != null && cha.getNetConnection() != null && cha.getNetConnection().getLoginName().equals(_loginName) && !cha.isInOfflineMode()/* || cha.getNetConnection() == null || cha.getNetConnection().getLoginName() == null*/)
				{
					cha.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
					cha.logout(false, false, true, true);
				}
			}
			catch(Exception e)
			{
				//_log.info("AuthLogin(75): cha != null: "+(cha != null)+" cha.getNetConnection() != null: "+(cha.getNetConnection() != null)+" cha.getNetConnection().getLoginName() != null: "+(cha.getNetConnection().getLoginName() != null)+" _loginName != null: "+(_loginName != null));
			}
		}
	}

	private class GGTest extends l2open.common.RunnableImpl
	{
		private final L2GameClient targetClient;

		private int attempts = 0;

		public GGTest(L2GameClient targetClient)
		{
			this.targetClient = targetClient;
		}

		public void runImpl()
		{
			if(!targetClient.isGameGuardOk())
				if(attempts < 3)
				{
					targetClient.sendPacket(Msg.GameGuardQuery);
					attempts++;
					ThreadPoolManager.getInstance().schedule(this, 500 * attempts);
				}
				else
					targetClient.closeNow(false);
		}
	}
}