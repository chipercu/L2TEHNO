package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.model.entity.residence.Fortress;

public class ExShowFortressSiegeInfo extends L2GameServerPacket
{
	private int _fortId;
	private int _barraksCount;
	private int _commandersMax;
	private int _commandersCurrent;

	public ExShowFortressSiegeInfo(Fortress fort)
	{
		_fortId = fort.getId();
		_barraksCount = fort.getFortSize();
		_commandersMax = FortressSiegeManager.getCommanderSpawnList(_fortId).size();
		_commandersCurrent = fort.getSiege().getCommanders().size();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x17);
		writeD(_fortId); // Fortress Id
		writeD(_barraksCount); // Total Barracks Count
		writeD(_commandersMax - _commandersCurrent + (_commandersMax == 4 ? 1 : 0));
	}
}