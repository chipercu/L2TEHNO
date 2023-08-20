package l2open.loginserver;

import l2open.config.ConfigValue;
import l2open.extensions.network.MMOClient;
import l2open.extensions.network.MMOConnection;
import l2open.gameserver.templates.StatsSet;
import l2open.loginserver.crypt.LoginCrypt;
import l2open.loginserver.crypt.ScrambledKeyPair;
import l2open.loginserver.serverpackets.AccountKicked;
import l2open.loginserver.serverpackets.AccountKicked.AccountKickedReason;
import l2open.loginserver.serverpackets.L2LoginServerPacket;
import l2open.loginserver.serverpackets.LoginFail;
import l2open.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2open.util.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>>
{
	private static Logger _log = Logger.getLogger(L2LoginClient.class.getName());

    public static enum LoginClientState
	{
		CONNECTED,
		AUTHED_GG,
		AUTHED_LOGIN,
		FAKE_LOGIN
	}

	private LoginClientState _state;
	public boolean PlayOK = false;

	// Crypt
	private LoginCrypt _loginCrypt;
	private ScrambledKeyPair _scrambledPair;
	private byte[] _blowfishKey;

	
	private String _account;
	private int _accessLevel;
	private int _lastServer;
	private SessionKey _sessionKey;
	private int _sessionId;
	private float _bonus = 1;
	private int _bonusExpire = 0;
	private String _ipAddr;

	public int region = -1;
	public int proxy_add = -1;

	public long[] _connectionStartTime = new long[2];

	private Map<Integer, Integer> _charsOnServers;
	private Map<Integer, long[]> _charsToDelete;

	public L2LoginClient(MMOConnection<L2LoginClient> con)
	{
		super(con);
		_state = LoginClientState.CONNECTED;
		
		_scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		_blowfishKey = LoginController.getInstance().getBlowfishKey();
		_connectionStartTime[0] = System.currentTimeMillis();
		_connectionStartTime[1] = 0L;
		_loginCrypt = new LoginCrypt();
		_loginCrypt.setKey(_blowfishKey, false);
		_sessionId = con.hashCode();
		_ipAddr = getConnection().getSocket().getInetAddress().getHostAddress();
		String ip = getIpAddress();
		if(!ConfigValue.SkipBannedIp && IpManager.getInstance().CheckIp(ip))
		{
			close(new AccountKicked(AccountKickedReason.REASON_PERMANENTLY_BANNED));
			_log.warning("Drop connection from banned IP: " + ip);
		}
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		boolean ret;
		try
		{
			ret = _loginCrypt.decrypt(buf.array(), buf.position(), size);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			closeNow(true);
			return false;
		}

		if(!ret)
		{
			//_log.warning("Wrong checksum from client: " + toString());
			closeNow(true);
		}

		return ret;
	}

	@Override
	public boolean encrypt(ByteBuffer buf, int size)
	{
		final int offset = buf.position();
		try
		{
			size = _loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}

		buf.position(offset + size);
		return true;
	}

	public LoginClientState getState()
	{
		return _state;
	}

	public void setState(LoginClientState state)
	{
		_state = state;
	}

	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}

	public byte[] getScrambledModulus()
	{
		if(_scrambledPair == null || _scrambledPair._scrambledModulus == null)
		{
			closeNow(true);
			return null;
		}

		return _scrambledPair._scrambledModulus;
	}

	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair._pair.getPrivate();
	}

	public String getAccount()
	{
		return _account;
	}

	public void setAccount(String account)
	{
		_account = account;
	}

	public void setAccessLevel(int accessLevel)
	{
		_accessLevel = accessLevel;
	}

	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setLastServer(int lastServer)
	{
		_lastServer = lastServer;
	}

	public int getLastServer()
	{
		return _lastServer;
	}

	public void setSessionId(int val)
	{
		_sessionId = val;
	}

	public int getSessionId()
	{
		return _sessionId;
	}

	public void setSessionKey(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}

	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}

	public void sendPacket(L2LoginServerPacket lsp)
	{
		getConnection().sendPacket(lsp);
	}

	public void close(LoginFailReason reason)
	{
		if(getConnection() != null)
			getConnection().close(new LoginFail(reason));
	}

	public void close(L2LoginServerPacket lsp)
	{
		if(getConnection() != null)
			getConnection().close(lsp);
	}

	@Override
	public void onDisconnection()
	{
		if(ConfigValue.Debug)
		{
			_log.info("DISCONNECTED: " + toString());
			Util.test();
		}

		LoginController lc = LoginController.getInstance();
		if(getState() == LoginClientState.AUTHED_LOGIN && !PlayOK && _account != null && lc.isAccountInLoginServer(_account))
			lc.removeAuthedLoginClient(_account);

		_loginCrypt = null;
		_scrambledPair = null;
		_blowfishKey = null;

		super.onDisconnection();
	}

	/*@Override
	public String toString()
	{
		InetAddress address = getConnection().getSocket().getInetAddress();
		if(getState() == LoginClientState.AUTHED_LOGIN)
			return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";
		return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
	}*/
	
	@Override
	public String toString()
	{
		switch(_state)
		{
			case AUTHED_LOGIN:
				return "[ Account : " + getAccount() + " IP: " + getIpAddress() + "]";
			default:
				return "[ State : " + getState() + " IP: " + getIpAddress() + "]";
		}
	}

	public void setBonus(float value, int expire)
	{
		_bonus = value;
		_bonusExpire = expire;
	}

	public float getBonus()
	{
		return _bonus;
	}

	public int getBonusExpire()
	{
		return _bonusExpire;
	}

	public String getIpAddress()
	{
		return _ipAddr;
		/*try
		{
			return getConnection().getSocket().getInetAddress().getHostAddress();
		}
		catch(Exception e)
		{
			return "Null IP";
		}*/
	}

	public void setCharsOnServ(int servId, int chars)
	{
		if(_charsOnServers == null)
			_charsOnServers = new HashMap<Integer, Integer>();
		_charsOnServers.put(servId, chars);
	}

	public Map<Integer, Integer> getCharsOnServ()
	{
		return _charsOnServers;
	}

	public void setCharsWaitingDelOnServ(int servId, long[] charsToDel)
	{
		if(_charsToDelete == null)
			_charsToDelete = new HashMap<Integer, long[]>();
		_charsToDelete.put(servId, charsToDel);
	}

	public Map<Integer, long[]> getCharsWaitingDelOnServ()
	{
		return _charsToDelete;
	}
}