package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Summon;

public class PetStatusShow extends L2GameServerPacket
{
	private int _summonType;
	private int _summonId;

	public PetStatusShow(L2Summon summon)
	{
		_summonType = summon.getSummonType();
		_summonId = summon.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb1);
		writeD(_summonType);
		if(getClient().isLindvior())
			writeD(_summonId);
	}
}