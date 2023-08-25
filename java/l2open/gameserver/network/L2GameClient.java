package l2open.gameserver.network;

import l2open.config.*;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.network.MMOClient;
import l2open.extensions.network.MMOConnection;
import l2open.gameserver.SecondaryPasswordAuth;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.PlayerManager;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.SessionKey;
import l2open.gameserver.loginservercon.gspackets.PlayerLogout;
import l2open.gameserver.model.CharSelectInfoPackage;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.NetPing;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Log;
import l2open.util.Util;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.strixguard.network.IStrixClient;
import org.strixguard.network.crypt.GuardGameCrypt;


/**
 * Represents a client connected on Game Server
 */
public final class L2GameClient extends MMOClient<MMOConnection<L2GameClient>> /**implements IStrixClient**/
{
	protected static Logger _log = Logger.getLogger(L2GameClient.class.getName());

	/**
	 * Тип защиты, которую использует клиент.
	 * 0 - без защиты.
	 * 1 - LameGuard
	 * 2 - SmartGuard
	 * 3 - StrixGuard
	 **/
	public int guard_type = 0;

	public GameCrypt _crypt = null;
	public GuardGameCrypt _crypt_strix = null;

	public ccpGuard.crypt.GameCrypt _cryptCCP = null;
	public ccpGuard.ProtectInfo _prot_info = null;
	private SecondaryPasswordAuth _secondaryAuth;
	
	private float _bonus = 1;
	private long _bonus_expire = 0;
	public GameClientState _state;
	private int _upTryes = 0, _upTryesTotal = 0;
	private long _upTryesRefresh = 0;
	private int _loginServerId = 0;
	private byte[] hwidS = new byte[16];
	public long pingTime = 0;
	public byte ping_send = -1;
	private long antispamSession;

	public void setLSId(int id)
	{
		_loginServerId = id;
	}

	public int getLSId()
	{
		return _loginServerId;
	}

	private String _hwid = "NULL";

	public String getHWID()
	{
		return _hwid;
	}

	public void setHWID(String hwid)
	{
		if(hwid == null)
			System.out.println("lol");
		_hwid = hwid;
	}

	public void setLameGuardHWID(ByteBuffer _data)
	{
		try
		{
			_data.getInt();
			byte[] data = new byte[256];
			_data.get(data);
			_data.getInt();
			com.lameguard.crypt.impl.VMPC c = new com.lameguard.crypt.impl.VMPC();
			c.setup(com.lameguard.Config.LAMEGUARD_CLIENT_CRYPT_KEY, com.lameguard.Config.LAMEGUARD_CLIENT_CRYPT_IV);
			c.crypt(data, 0, 256);
			byte b = com.lameguard.crypt.LamePacket.ck(data, 0, 255);
			int offset = 1;
			byte[] tmp = new byte[32];
			offset = com.lameguard.crypt.LamePacket.readB(data, 1, tmp, 4);
			com.lameguard.crypt.LamePacket.readB(data, offset, hwidS, 16);
			setHWID(getHwid(hwidS, com.lameguard.Config.LAMEGUARD_HWID_BAN));
			//System.out.println("L2GameClient: -> setLameGuardHWID: hwid="+com.lameguard.utils.Utils.asHex(hwidS)+"  HWID2: "+getActiveChar().getHWIDs());
		}
		catch(Exception e)
		{
			//System.out.println("L2GameClient: -> Error: -> setLameGuardHWID: Account="+getLoginName()+" IP="+getIpAddr());
		}
		finally
		{
			_data.clear();
			_data = null;
		}
	}

	// hwid - наш хвид
	// mask - маска
	public String getHwid(byte[] hwid, int mask)
	{
		byte[] hwid1 = new byte[16];
		int i;
		for(i=0;i<16;i++)
			hwid1[i] = (byte)i;
		if(mask == 0)
		{
			_log.warning("lol-100500");
			return com.lameguard.utils.Utils.asHex(hwid);
		}

		if((mask & 8) == 8)
			for(i = 0; i < 2;i++)
				hwid1[i] = hwid[i];
		if((mask & 4) == 4)
			for(i = 2; i < 6;i++)
				hwid1[i] = hwid[i];
		if((mask & 2) == 2)
			for(i = 6; i < 10;i++)
				hwid1[i] = hwid[i];
		if((mask & 1) == 1)
			for(i = 10; i < 14;i++)
				hwid1[i] = hwid[i];
		return com.lameguard.utils.Utils.asHex(hwid1);
	}
/*	private OpcodeObfuscator obf;
	private int keyObf = 0;

	public void setObfKey(int obfKey)
	{
		keyObf = obfKey;
	}

	public int getObfKey()
	{
        return keyObf;
    }

    public int enableObfuscation(int obfKey)
	{
        obf = OpcodeObfuscator.init_tables(obfKey);
        return obfKey;
    }

    public OpcodeObfuscator getObfuscator()
	{
        if(obf == null)
            obf = new OpcodeObfuscator();
        return obf;
    }*/

    public static enum GameClientState
	{
		CONNECTED,
		AUTHED,
		IN_GAME
	}

	private String _loginName;
	private L2Player _activeChar;
	private SessionKey _sessionId = null;
	private MMOConnection<L2GameClient> _connection = null;

	//private byte[] _filter;

	private int revision = 0;
	private boolean _gameGuardOk = false;

	public byte client_lang = -1;

	private GArray<Integer> _charSlotMapping = new GArray<Integer>();
	private PacketLogger pktLogger = null;
	private boolean pktLoggerMatch = false;
	public StatsSet account_fields = null;

	public L2GameClient(MMOConnection<L2GameClient> con, int offline)
	{
		super(con);
		switch(offline)
		{
			case 0:
				_state = GameClientState.IN_GAME;
				break;
			case 1:
				_state = GameClientState.CONNECTED;
				_connection = con;
				_sessionId = new SessionKey(-1, -1, -1, -1);

				if(ConfigValue.CCPGuardEnable)
				{
					_cryptCCP = new ccpGuard.crypt.GameCrypt();
					_prot_info = new ccpGuard.ProtectInfo(this, getIpAddr(), offline == 0 ? true : false);
				}
				else if(ConfigValue.StrixGuardEnable)
					_crypt_strix = new GuardGameCrypt();
				else
					_crypt = new GameCrypt();

				if(ConfigValue.LogClientPackets || ConfigValue.LogServerPackets)
				{
					pktLogger = new PacketLogger(this, ConfigValue.LogPacketsFlushSize);
					if(ConfigSystem.PACKETLOGGER_IPS != null)
						if(ConfigSystem.PACKETLOGGER_IPS.isIpInNets(getIpAddr()))
							pktLoggerMatch = true;
				}
				Log.logTrace("openGameSession["+antispamSession+"]: "+toString(), "network", "log_connect");
				break;
			case 2:
				_state = GameClientState.CONNECTED;
				_connection = con;
				_sessionId = new SessionKey(-1, -1, -1, -1);

				if(ConfigValue.CCPGuardEnable)
				{
					_cryptCCP = new ccpGuard.crypt.GameCrypt();
					_prot_info = new ccpGuard.ProtectInfo(this, getIpAddr(), false);
				}
				else if(ConfigValue.StrixGuardEnable)
					_crypt_strix = new GuardGameCrypt();
				else
					_crypt = new GameCrypt();

				if(ConfigValue.LogClientPackets || ConfigValue.LogServerPackets)
				{
					pktLogger = new PacketLogger(this, ConfigValue.LogPacketsFlushSize);
					if(ConfigSystem.PACKETLOGGER_IPS != null)
						if(ConfigSystem.PACKETLOGGER_IPS.isIpInNets(getIpAddr()))
							pktLoggerMatch = true;
				}
				break;
		}
	}

	public L2GameClient(MMOConnection<L2GameClient> con)
	{
		this(con, 1);
	}

	public L2GameClient(MMOConnection<L2GameClient> con, boolean offline)
	{
		this(con, offline ? 0 : 1);
	}

	public void OnOfflineTrade()
	{
		_charSlotMapping = null;
	}

	public void disconnectOffline()
	{
		onDisconnection();
	}

	@Override
	protected void onForcedDisconnection()
	{
		super.onForcedDisconnection();
		_state = null;
	}

	@Override
	protected void onDisconnection()
	{
		//Util.checkPerMission();
		stopPingTask();

		L2Player player = getActiveChar();

		if(pktLogger != null)
		{
			if(!pktLogger.assigned() && pktLoggerMatch)
				pktLogger.assign();
			pktLogger.close();
			pktLogger = null;
		}

		Log.logTrace("closeGameSession["+antispamSession+"]: "+toString(), "network", "log_connect");

		if(getLoginName() == null || getLoginName().equals("") || _state != GameClientState.IN_GAME && _state != GameClientState.AUTHED)
			return;
		if(ConfigValue.CCPGuardEnable)
			ccpGuard.Protection.doDisconection(this);
		try
		{
			if(player != null && player.isInOfflineMode())
				//LSConnection.getInstance().sendPacket(new PlayerLogout(getLoginName()));
				return;

			LSConnection.getInstance(getLSId()).removeAccount(this);

			if(player != null) // this should only happen on connection loss
			{
				player.logout(true, false, false, false);

				if(player.getNetConnection() != null)
				{
					if(!player.isInOfflineMode())
						player.getNetConnection().closeNow(false);
					player.setNetConnection(null);
				}

				player.setConnected(false);
				if(RainbowSpringSiege.getInstance().isPlayerInArena(player))
					RainbowSpringSiege.getInstance().removeFromArena(player);
			}
			//else
			//	System.out.println("L2GameClient");

			setConnection(null);
		}
		catch(Exception e1)
		{
			_log.log(Level.WARNING, "error while disconnecting client", e1);
		}
		finally
		{
			LSConnection.getInstance(getLSId()).sendPacket(new PlayerLogout(getLoginName()));
		}
		_activeChar = null;
		super.onDisconnection();
		_state = null;
	}

	public void markRestoredChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if(objid < 0)
			return;

		//if(_activeChar != null && _activeChar.getObjectId() == objid)
		//	_activeChar.setDeleteTimer(0);

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "data error on restore char:", e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void markToDeleteChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if(objid < 0)
			return;

		L2Player old_player = L2ObjectsStorage.getPlayer(objid);
		if(old_player != null)
			old_player.logout(false, false, true, true);
		//	old_player.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
		//_log.info("L2GameClient: markToDeleteChar["+charslot+"]["+objid+"]["+(_activeChar != null ? _activeChar.getObjectId() : -1)+"]");

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
			statement.setLong(1, (int) (System.currentTimeMillis() / 1000));
			statement.setInt(2, objid);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "data error on update deletime char:", e);
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void deleteChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if(objid == -1)
			return;

		L2Player old_player = L2ObjectsStorage.getPlayer(objid);
		if(old_player != null)
			old_player.logout(false, false, true, true);

		PlayerManager.deleteCharByObjId(objid);
	}

	public L2Player loadCharFromDisk(int charslot)
	{
		Integer objectId = getObjectIdForSlot(charslot);
		if(objectId == -1)
			return null;

		L2Player character = null;
		L2Player old_player = L2ObjectsStorage.getPlayer(objectId);

		//_log.info("L2GameClient: loadCharFromDisk["+L2ObjectsStorage.getAllPlayersCount()+"]: objectId="+objectId+" old_player="+old_player+" isInOfflineMode="+(old_player == null ? -1 : old_player.isInOfflineMode()));
		if(old_player != null)
		{
			if(old_player.isInOfflineMode() || old_player.isLogoutStarted())
			{
				// оффтрейдового чара проще выбить чем восстанавливать
				//_log.info("Tipa kick: "+old_player.getName());
				old_player.logout(false, false, true, true);
			}
			else
			{
				old_player.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
				LSConnection.getInstance(getLSId()).sendPacket(new PlayerLogout(getLoginName()));

				if(old_player.getNetConnection() != null)
				{
					old_player.getNetConnection().setActiveChar(null);
					old_player.getNetConnection().closeNow(false);
				}
				old_player.setLogoutStarted(false);
				old_player.setNetConnection(this);
				character = old_player;
			}
		}

		if(character == null)
			character = PlayerData.getInstance().restore(objectId, 0);

		if(character != null)
		{
			// preinit some values for each login
			character.setRunning(); // running is default
			character.standUp(); // standing is default

			character.updateStats();
			character.setOnlineStatus(true);
			setActiveChar(character);
			character.restoreBonus();
			character.bookmarks.restore();
			PlayerData.getInstance().loadHwidLock(character);
			if(ConfigValue.LameGuard || ConfigValue.ScriptsGuardEnable || ConfigValue.StrixGuardEnable || ConfigValue.SmartGuard)
				character._hwid = getHWID();
			if(!character.isInOfflineMode())
				PlayerData.getInstance().storeHWID(character, getHWID());
			if(ConfigValue.UseClientLang)
			{
				if(client_lang == ConfigValue.UseClientLangEngId)
					character.setVar("lang@", "en");
				else if(client_lang == ConfigValue.UseClientLangRuId)
					character.setVar("lang@", "ru");
			}

			if(pktLogger != null)
				if(!pktLogger.assigned())
				{
					if(!pktLoggerMatch)
						if(ConfigValue.LogPacketsFromChars != null)
						{
							String char_name = character.getName();
							for(String s_mask : ConfigValue.LogPacketsFromChars)
							{
								if(char_name.matches(s_mask))
								{
									pktLoggerMatch = true;
									break;
								}
							}
						}
					if(pktLoggerMatch)
						pktLogger.assign();
					else
						pktLogger = null;
				}
		}
		else
			_log.warning("could not restore obj_id: " + objectId + " in slot:" + charslot);

		return character;
	}

	public int getObjectIdForSlot(int charslot)
	{
		if(charslot < 0 || charslot >= _charSlotMapping.size())
		{
			_log.warning(getLoginName() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		return _charSlotMapping.get(charslot);
	}

	public int getSlotForObjectId(int obj_id)
	{
		for(int i=0;i<_charSlotMapping.size();i++)
			if(_charSlotMapping.get(i) == obj_id)
				return i;
		return -1;
	}

	@Override
	public MMOConnection<L2GameClient> getConnection()
	{
		return _connection;
	}

	public L2Player getActiveChar()
	{
		return _activeChar;
	}

	/**
	 * @return Returns the sessionId.
	 */
	public SessionKey getSessionId()
	{
		return _sessionId;
	}

	public String getLoginName()
	{
		return _loginName;
	}

	public void setLoginName(String loginName)
	{
		_loginName = loginName;

		if(ConfigValue.SAEnabled)
			_secondaryAuth = new SecondaryPasswordAuth(this);
		if(pktLogger != null && !pktLoggerMatch && ConfigValue.LogPacketsFromAccounts != null)
			for(String s_mask : ConfigValue.LogPacketsFromAccounts)
				if(loginName.matches(s_mask))
				{
					pktLoggerMatch = true;
					break;
				}
	}

	public void setActiveChar(L2Player cha)
	{
		_activeChar = cha;
		if(cha != null)
			// we store the connection in the player object so that external
			// events can directly send events to the players client
			// might be changed later to use a central event management and distribution system
			_activeChar.setNetConnection(this);
	}

	public void setSessionId(SessionKey sessionKey)
	{
		_sessionId = sessionKey;
	}

	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping.clear();

		for(CharSelectInfoPackage element : chars)
		{
			int objectId = element.getObjectId();
			_charSlotMapping.add(objectId);
		}
	}

	public void setCharSelection(int c)
	{
		_charSlotMapping.clear();
		_charSlotMapping.add(c);
	}

	/**
	 * @return Returns the revision.
	 */
	public int getRevision()
	{
		return revision;
	}

	/**
	 * @param revision The revision to set.
	 */
	public void setRevision(int revision)
	{
		this.revision = revision;
	}

	public void setGameGuardOk(boolean gameGuardOk)
	{
		_gameGuardOk = gameGuardOk;
	}

	public boolean isGameGuardOk()
	{
		return _gameGuardOk;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		if(pktLogger != null && ConfigValue.LogServerPackets)
			pktLogger.log_packet((byte) 1, buf, size);
		if(ConfigValue.CCPGuardEnable)
			_cryptCCP.encrypt(buf.array(), buf.position(), size);
		else if(ConfigValue.StrixGuardEnable)
			_crypt_strix.encrypt(buf.array(), buf.position(), size);
		else
			_crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}

    @Override
    public boolean decrypt(ByteBuffer buf, int size)
	{
		if(ConfigValue.CCPGuardEnable)
			_cryptCCP.decrypt(buf.array(), buf.position(), size);
		else if(ConfigValue.StrixGuardEnable)
			return _crypt_strix.decrypt(buf.array(), buf.position(), size);
		else
			_crypt.decrypt(buf.array(), buf.position(), size);
        if (pktLogger != null && ConfigValue.LogClientPackets)
            pktLogger.log_packet((byte) 0, buf, size);
        return true;
    }

	public void sendPacket(L2GameServerPacket... gsp)
	{
		if(getConnection() == null)
			return;
		getConnection().sendPacket(gsp);
	}

	@SuppressWarnings("unchecked")
	public void sendPackets(Collection<L2GameServerPacket> gsp)
	{
		if(getConnection() == null || _activeChar == null || _activeChar.isBot() || _activeChar.isInOfflineMode())
			return;
		getConnection().sendPackets((Collection) gsp);
	}

	public void close(L2GameServerPacket gsp)
	{
		getConnection().close(gsp);
	}

	public String getIpAddr()
	{
		try
		{
			return _connection.getSocket().getInetAddress().getHostAddress();
		}
		catch(NullPointerException e)
		{
			return "Disconnected";
		}
	}

	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		if(ConfigValue.CCPGuardEnable)
			_cryptCCP.setKey(key);
		else if(ConfigValue.StrixGuardEnable)
			_crypt_strix.setKey(key);
		else
			_crypt.setKey(key);
		return key;
	}

	public float getBonus()
	{
		return _bonus;
	}

	public void setBonus(float bonus)
	{
		_bonus = bonus;
	}

	/**
	 * @return время окончания бонуса в unixtime
	 */
	public long getBonusExpire()
	{
		return _bonus_expire;
	}

	public void setBonusExpire(long time)
	{
		if(time < 0)
			return;
		if(time < System.currentTimeMillis() / 1000)
		{
			_bonus = 1;
			return;
		}
		_bonus_expire = time;
	}

	public GameClientState getState()
	{
		return _state;
	}

	public void setState(GameClientState state)
	{
		_state = state;
	}

	/**
	 * @return произведено ли отключение игрока
	 */
	public boolean onClientPacketFail()
	{
		if(isPacketsFailed())
			return true;

		if(_upTryesRefresh == 0)
			_upTryesRefresh = System.currentTimeMillis() + 5000;
		else if(_upTryesRefresh < System.currentTimeMillis())
		{
			_upTryesRefresh = System.currentTimeMillis() + 5000;
			_upTryes = 0;
		}

		_upTryes++;
		_upTryesTotal++;

		if(_upTryes > 4 || _upTryesTotal > 10)
		{
			_log.warning("Too many client packet fails, connection closed. IP: " + getIpAddr() + ", account:" + getLoginName());
			L2Player activeChar = getActiveChar();
			if(activeChar != null)
				activeChar.logout(false, false, true, true);
			else
				closeNow(true);
			_upTryesTotal = Integer.MAX_VALUE;
			return true;
		}

		return false;
	}

	public boolean isPacketsFailed()
	{
		return _upTryesTotal == Integer.MAX_VALUE;
	}

	public SecondaryPasswordAuth getSecondaryAuth()
	{
		return _secondaryAuth;
	}

	private Future<?> _pingTask;
	private int _pingTryCount = 0;
	private int _pingTime = 0;

	public void startPingTask()
	{
		_pingTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			public void run()
			{
				//if((this) > 2)
				//	closeNow(true);
				//else
					sendPacket(new NetPing((int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime())));
			}
		}
		,0, 30000L);
	}

	public void onNetPing(int time)
	{
		_pingTryCount++;
		_pingTime = (int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime() - time);
	}

	public int getPing()
	{
		return _pingTime;
	}

	public void stopPingTask()
	{
		if(_pingTask != null)
		{
			_pingTask.cancel(false);
			_pingTask = null;
		}
	}

	@Override
	public String toString()
	{
		return "L2GameClient: " + (_activeChar == null ? _loginName : _activeChar) + "@" + getIpAddr();
	}

	public boolean isLindvior()
	{
		return ConfigValue.EnableLindvior && (getRevision() >= 525 || getRevision() < 100);
	}
	//---------------------------------------------------------------------------------------------------------------
	//TODO[K] - Guard section start
	private String vmpHwid;
	private String windowsHwid;
	private boolean isProtected;

	//@Override
	public String getVMPHWID()
	{
		return vmpHwid;
	}

	//@Override
	public void setVMPHWID(final String value)
	{
		vmpHwid = value;
	}

	//@Override
	public boolean isProtected()
	{
		return isProtected;
	}

	//@Override
	public void setProtected(boolean value)
	{
		isProtected = value;
	}
	//@Override
	public void setWindowsHWID(final String value)
	{
		windowsHwid = value;
	}

	//@Override
	public String getWindowInfo()
	{
		return windowsHwid;
	}
	//TODO[K] - Guard section end
	public long getAntispamSession()
	{
		return antispamSession;
	}
}