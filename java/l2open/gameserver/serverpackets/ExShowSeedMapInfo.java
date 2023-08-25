package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.util.Location;

/**
 * Probably the destination coordinates where you should fly your clan's airship.<BR>
 * Exactly at the specified coordinates an airship controller is spawned.<BR>
 * Sent while being in Gracia, when world map is opened, in response to RequestSeedPhase.<BR>
 * FE A1 00		- opcodes<BR>
 * 02 00 00 00	- list size<BR>
 * <BR>
 * B7 3B FC FF	- x<BR>
 * 38 D8 03 00	- y<BR>
 * EB 10 00 00	- z<BR>
 * D3 0A 00 00	- heading?<BR>
 * <BR>
 * F6 BC FC FF	- x<BR>
 * 48 37 03 00	- y<BR>
 * 30 11 00 00	- z<BR>
 * CE 0A 00 00	- heading?
 */
public class ExShowSeedMapInfo extends L2GameServerPacket
{
	private static final Location[] ENTRANCES = { new Location(-246857, 251960, 4331, 1), new Location(-213770, 210760, 4400, 2), };

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xA1);
		writeD(ENTRANCES.length);
		for(Location loc : ENTRANCES)
		{
			writeD(loc.x);
			writeD(loc.y);
			writeD(loc.z);
			switch(loc.h)
			{
				case 1: // Seed of Destruction
					if(ServerVariables.getLong("SoD_opened", 0) * 1000L - System.currentTimeMillis() <= 0)
						writeD(2771);
					else
						writeD(2772);
					break;
				case 2: // Seed of Immortality
					writeD(SeedOfInfinityManager.getCurrentCycle() + 2765);
					break;
			}
		}
	}
}