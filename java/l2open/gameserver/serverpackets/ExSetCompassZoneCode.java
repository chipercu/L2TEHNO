package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

/**
 * http://forum.l2jserver.com/thread.php?threadid=22736
 */
public class ExSetCompassZoneCode extends L2GameServerPacket
{
	private static int ZONE_ALTERED = 0x08;
	@SuppressWarnings("unused")
	private static int ZONE_ALTERED2 = 0x09;
	@SuppressWarnings("unused")
	private static int ZONE_REMINDER = 0x0A;
	private static int ZONE_SIEGE = 0x0B;
	private static int ZONE_PEACE = 0x0C;
	private static int ZONE_SS = 0x0D;
	private static int ZONE_PVP = 0x0E; // 1, 2, 3, 4, 5, 6, 7
	private static int ZONE_GENERAL_FIELD = 0x0F; //0 и > 15

	int _zone = -1;

	public ExSetCompassZoneCode(L2Player player)
	{
		//Приоритеты ифам от фонаря:)

		if(player.isInDangerArea())
			_zone = ZONE_ALTERED;
		else if(player.isOnSiegeField())
			_zone = ZONE_SIEGE;
		else if(player.isInCombatZone())
			_zone = ZONE_PVP;
		else if(player.isInPeaceZone())
			_zone = ZONE_PEACE;
		else if(player.isInSSZone())
			_zone = ZONE_SS;
		else
			_zone = ZONE_GENERAL_FIELD;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x33);
		writeD(_zone);
	}
}