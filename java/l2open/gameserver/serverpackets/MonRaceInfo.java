package l2open.gameserver.serverpackets;

import l2open.gameserver.model.instances.L2NpcInstance;

/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....
 *
 * format
 * dddc dddh (ddc)
 */
public class MonRaceInfo extends L2GameServerPacket
{
	private int _unknown1;
	private int _unknown2;
	private L2NpcInstance[] _monsters;
	private int[][] _speeds;

	public MonRaceInfo(int unknown1, int unknown2, L2NpcInstance[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race
		 * 0 15322 to start race
		 * 13765 -1 in middle of race
		 * -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}

	//  0xf3;;EtcStatusUpdatePacket;ddddd

	@Override
	protected final void writeImpl()
	{
		writeC(0xe3);

		writeD(_unknown1);
		writeD(_unknown2);
		writeD(8);

		for(int i = 0; i < 8; i++)
		{
			//_log.info("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			writeD(_monsters[i].getObjectId()); //npcObjectID
			writeD(_monsters[i].getTemplate().npcId + 1000000); //npcID
			writeD(14107); //origin X
			writeD(181875 + 58 * (7 - i)); //origin Y
			writeD(-3566); //origin Z
			writeD(12080); //end X
			writeD(181875 + 58 * (7 - i)); //end Y
			writeD(-3566); //end Z
			writeF(_monsters[i].getColHeight()); //coll. height
			writeF(_monsters[i].getColRadius()); //coll. radius
			writeD(120); // ?? unknown
			for(int j = 0; j < 20; j++)
				writeC(_unknown1 == 0 ? _speeds[i][j] : 0);
			writeD(0);
			writeD(0x00); // ? GraciaFinal
		}
	}
}