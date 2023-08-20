package l2open.gameserver.serverpackets;

import l2open.gameserver.model.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket
{
	private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
		_privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x3c);
		writeD(_privs.length);
		for(RankPrivs element : _privs)
		{
			writeD(element.getRank());
			writeD(element.getParty());
		}
	}
}