package l2open.loginserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.loginserver.LoginController;
import l2open.loginserver.SessionKey;
import l2open.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2open.loginserver.serverpackets.PlayOk;

/**
 * Fromat is ddc
 * d: first part of session id
 * d: second part of session id
 * c: server ID
 */
public class RequestServerLogin extends L2LoginClientPacket
{
	private int _skey1;
	private int _skey2;
	private int _serverId;

	@Override
	public boolean readImpl()
	{
		if(getAvaliableBytes() >= 9)
		{
			_skey1 = readD();
			_skey2 = readD();
			_serverId = readC();
			return true;
		}
		return false;
	}

	/**
	 * @see l2open.extensions.network.ReceivablePacket#run()
	 */
	@Override
	public void runImpl()
	{
		SessionKey sk = getClient().getSessionKey();

		// if we didnt showed the license we cant check these values
		if(!ConfigValue.ShowLicence || sk.checkLoginPair(_skey1, _skey2))
		{
			if(LoginController.getInstance().isLoginPossible(getClient(), _serverId))
				getClient().sendPacket(new PlayOk(sk));
			else
				getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
		}
		else
			getClient().close(LoginFailReason.REASON_ACCESS_FAILED);
	}
}