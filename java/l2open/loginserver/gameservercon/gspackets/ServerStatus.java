package l2open.loginserver.gameservercon.gspackets;

import l2open.gameserver.loginservercon.Attribute;
import l2open.loginserver.GameServerTable;
import l2open.loginserver.gameservercon.AttGS;
import l2open.loginserver.gameservercon.GameServerInfo;

import java.util.logging.Logger;

public class ServerStatus extends ClientBasePacket
{
	protected static Logger _log = Logger.getLogger(ServerStatus.class.getName());

	public ServerStatus(byte[] decrypt, AttGS gameserver)
	{
		super(decrypt, gameserver);
	}

	@Override
	public void read()
	{
		GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServerById(getGameServer().getServerId());
		if(gsi != null)
		{
			int size = readD();
			for(int i = 0; i < size; i++)
			{
				int type = readD();
				int value = readD();
				switch(type)
				{
					case Attribute.SERVER_LIST_CLOCK:
						gsi.setShowingClock(value == Attribute.ON);
						break;
					case Attribute.SERVER_LIST_SQUARE_BRACKET:
						gsi.setShowingBrackets(value == Attribute.ON);
						break;
					case Attribute.TEST_SERVER:
						gsi.setTestServer(value == Attribute.ON);
						gsi.setShowingBrackets(value == Attribute.ON);
						break;
					case Attribute.MAX_PLAYERS:
						gsi.setMaxPlayers(value);
						break;
					case Attribute.GM_ONLY_SERVER:
						gsi.setGMOnly(value == Attribute.ON);
						break;
					case Attribute.ONLINE:
						gsi.setOnline(value == Attribute.ON);
						break;
					case Attribute.BIT_MASK:
						gsi.setBitmask(value);
						break;
				}
			}
		}
	}
}