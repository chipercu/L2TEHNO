package l2open.gameserver.loginservercon.lspackets;

import l2open.config.ConfigValue;
import l2open.gameserver.loginservercon.AttLS;
import l2open.gameserver.loginservercon.Attribute;
import l2open.gameserver.loginservercon.gspackets.PlayerInGame;
import l2open.gameserver.loginservercon.gspackets.PlayersInGame;
import l2open.gameserver.loginservercon.gspackets.ServerStatus;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;
import l2open.util.Stats;

import java.util.logging.Logger;

public class AuthResponse extends LoginServerBasePacket
{
	private static final Logger log = Logger.getLogger(AuthResponse.class.getName());

	private int _serverId;
	private String _serverName;

	public AuthResponse(byte[] decrypt, AttLS loginServer)
	{
		super(decrypt, loginServer);
	}

	@Override
	public void read()
	{
		_serverId = readC();
		_serverName = readS();
		getLoginServer().setLicenseShown(readC() == 1);
		try
		{
			getLoginServer().setProtocolVersion(readH());
		}
		catch(Exception e)
		{
			getLoginServer().setProtocolVersion(0);
		}

		log.info("Registered on login as Server " + _serverId + " : " + _serverName);

		GArray<Attribute> attributes = new GArray<Attribute>();

		attributes.add(new Attribute(Attribute.SERVER_LIST_SQUARE_BRACKET, ConfigValue.ServerListBrackets ? Attribute.ON : Attribute.OFF));
		attributes.add(new Attribute(Attribute.SERVER_LIST_CLOCK, ConfigValue.ServerListClock ? Attribute.ON : Attribute.OFF));
		attributes.add(new Attribute(Attribute.TEST_SERVER, ConfigValue.TestServer ? Attribute.ON : Attribute.OFF));
		attributes.add(new Attribute(Attribute.GM_ONLY_SERVER, ConfigValue.ServerGMOnly ? Attribute.ON : Attribute.OFF));
		attributes.add(new Attribute(Attribute.ONLINE, Attribute.ON));
		
		int bits = 0;

		// 1: Normal, 2: Relax, 4: Public Test, 8: No Label, 16: Character Creation Restricted, 32: Event, 64: Free
		if(ConfigValue.ServerNormal)
			bits |= 0x01;
		if(ConfigValue.ServerListClock)
			bits |= 0x02;
		if(ConfigValue.TestServer)
			bits |= 0x04;
		if(ConfigValue.ServerNoLabel)
			bits |= 0x08;
		if(ConfigValue.ServerOnlyCreate)
			bits |= 0x10;
		if(ConfigValue.ServerEvent)
			bits |= 0x20;
		if(ConfigValue.ServerFree)
			bits |= 0x40;
		if(ConfigValue.ServerClassic)
			bits |= 0x0400;
		/**
		*0*NORMAL,
		*1*RELAX,
		*2*TEST,
		*3*NO_LABEL,
		*4*RESTRICTED,
		*5*EVENT,
		*6*FREE,
		*7*UNK_7,
		*8*UNK_8,
		*9*UNK_9,
		*10*CLASSIC;

		private int _mask;

		ServerType()
		{
			_mask = 1 << ordinal();
		}
		**/

		attributes.add(new Attribute(Attribute.BIT_MASK, bits));

		getLoginServer().setAuthResponsed(true);
		sendPacket(new ServerStatus(attributes));

		if(L2ObjectsStorage.getAllPlayersCount() > 0)
		{
			GArray<String> playerList = new GArray<String>();
			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				if(player.isInOfflineMode())
					continue;
				if(player.getAccountName() == null || player.getAccountName().isEmpty())
				{
					log.warning("AuthResponse: empty accname for " + player);
					continue;
				}
				playerList.add(player.getAccountName());
				getLoginServer().getCon().addAccountInGame(player.getNetConnection());
			}

			int online = Stats.getOnline(true);

			sendPacket(new PlayerInGame(null, getLoginServer().getProtocolVersion(), online));
			if(getLoginServer().getProtocolVersion() > 1)
				sendPackets(PlayersInGame.makePlayersInGame(online, getLoginServer().getProtocolVersion(), playerList));
			else
				for(String name : playerList)
					sendPacket(new PlayerInGame(name, getLoginServer().getProtocolVersion(), online));
		}
	}
}