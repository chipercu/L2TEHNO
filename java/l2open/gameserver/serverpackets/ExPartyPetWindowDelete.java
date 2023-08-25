package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;

public class ExPartyPetWindowDelete extends L2GameServerPacket
{
	private int _summonObjectId;
	private int _ownerObjectId;
	private String _summonName;

	public ExPartyPetWindowDelete(L2Summon summon)
	{
		L2Player player = summon.getPlayer();
		if(player == null)
			return;
		_summonObjectId = summon.getObjectId();
		_summonName = summon.getName();
		_ownerObjectId = player.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		if(_summonObjectId == 0)
			return;
		writeC(EXTENDED_PACKET);
		writeHG(0x6a);
		writeD(_summonObjectId);
		writeD(_ownerObjectId);
		writeS(_summonName);
	}
}