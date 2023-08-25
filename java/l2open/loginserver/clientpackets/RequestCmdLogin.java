package l2open.loginserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.loginserver.L2LoginClient;
import l2open.loginserver.L2LoginClient.LoginClientState;
import l2open.loginserver.LoginController;
import l2open.loginserver.LoginController.State;
import l2open.loginserver.LoginController.Status;
import l2open.loginserver.gameservercon.GameServerInfo;
import l2open.loginserver.gameservercon.lspackets.KickPlayer;
import l2open.loginserver.serverpackets.AccountKicked;
import l2open.loginserver.serverpackets.AccountKicked.AccountKickedReason;
import l2open.loginserver.serverpackets.LoginFail;
import l2open.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2open.loginserver.serverpackets.LoginOk;
import l2open.loginserver.serverpackets.ServerList;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

public class RequestCmdLogin extends L2LoginClientPacket
{
	private byte[] _raw = new byte[128];

	private String _user;
	private String _password;
	@SuppressWarnings("unused")
	private int _ncotp;

	public String getPassword()
	{
		return _password;
	}

	public String getUser()
	{
		return _user;
	}

	public int getOneTimePassword()
	{
		return _ncotp;
	}

	@Override
	public boolean readImpl()
	{
		L2LoginClient client = getClient();

		if(getAvaliableBytes() >= 128)
		{
			try
			{
				readD();
				readB(_raw);
				readD();
				readH();
				readC();
				return true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void runImpl()
	{
		L2LoginClient client = getClient();

		byte[] decrypted;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		}
		catch(GeneralSecurityException e)
		{
			_log.warning("RequestCmdLogin: "+_raw.length);
			e.printStackTrace();
			return;
		}

		_user = new String(decrypted, 0x40, 14).trim();
		_user = _user.toLowerCase();
		_password = new String(decrypted, 0x60, 16).trim();
		_ncotp = decrypted[0x7c];
		_ncotp |= decrypted[0x7d] << 8;
		_ncotp |= decrypted[0x7e] << 16;
		_ncotp |= decrypted[0x7f] << 24;

		LoginController lc = LoginController.getInstance();

		Status status = lc.tryAuthLogin(_user, _password, client);
		if(status.state == State.IN_USE)
		{
			client.close(LoginFailReason.REASON_ACCOUNT_IN_USE);

			L2LoginClient oldClient = lc.getAuthedClient(_user);

			// кикаем другого клиента, подключенного к логину
			if(oldClient == null)
			{
				GameServerInfo gsi = lc.getAccountOnGameServer(_user);
				if(gsi != null && gsi.isAuthed())
					//gsi.getGameServer().kickPlayer(_user);
					gsi.getGameServer().sendPacket(new KickPlayer(_user));
			}
			//else
				// запускаем таймер кика другого клиента, на 10 секунд...таймер скидывается, если выполнить какое-то действие.
				

			// кикаем другого клиента из игры
			/*
			GameServerInfo gsi = lc.getAccountOnGameServer(_user);
			if(gsi != null && gsi.isAuthed())
				gsi.getGameServer().kickPlayer(_user);
				*/

			//if(lc.isAccountInLoginServer(_user))
			//	lc.removeAuthedLoginClient(_user).close(LoginFailReason.REASON_ACCOUNT_IN_USE);

			//status.state = State.VALID;
		}
		else if(status.state == State.VALID)
		{
			client.setAccount(_user);
			lc.getCharactersOnAccount(_user);
			client.setState(LoginClientState.AUTHED_LOGIN);
			client.setSessionKey(lc.assignSessionKeyToClient());
			lc.addAuthedLoginClient(_user, client);
			client.setBonus(status.bonus, status.bonus_expire);
			if(ConfigValue.ShowLicence)
				client.sendPacket(new LoginOk(client.getSessionKey()));
			else
				client.sendPacket(new ServerList(client));
		}
		else if(status.state == State.WRONG)
		{
			if(ConfigValue.FakeLogin)  
			{ 
				client.setState(LoginClientState.FAKE_LOGIN);  
				client.setSessionKey(lc.assignSessionKeyToClient());  
				if (ConfigValue.ShowLicence)  
					client.sendPacket(new LoginOk(getClient().getSessionKey()));  
				else  
					getClient().sendPacket(new ServerList(getClient()));  
			}  
			else  
				client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG); 
		}
		else if(status.state == State.BANNED)
			client.close(new LoginFail(LoginFailReason.REASON_ACCESS_FAILED));
		else if(status.state == State.IP_ACCESS_DENIED)
			client.close(LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
		else if(status.state == State.REASON_PERMANENTLY_BANNED)
			client.close(new LoginFail(LoginFailReason.REASON_ACCESS_FAILED));
	}
}