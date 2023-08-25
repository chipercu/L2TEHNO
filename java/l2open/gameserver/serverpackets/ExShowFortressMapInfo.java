package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.instances.L2CommanderInstance;
import l2open.util.GArray;

public class ExShowFortressMapInfo extends L2GameServerPacket
{
	private int _fortId;
	private int _barraksCount;
	private int _fortStatus;
	private int _commandersMax;
	private int[] _commanders;

	public ExShowFortressMapInfo(Fortress fortress)
	{
		_fortId = fortress.getId();
		_barraksCount = fortress.getFortSize();
		_fortStatus = fortress.getSiege().getAttackerClans().isEmpty() ? 0 : 1;

		GArray<SiegeSpawn> commanders_list = FortressSiegeManager.getCommanderSpawnList(_fortId);
		_commandersMax = FortressSiegeManager.getCommanderSpawnList(_fortId).size();
		_commanders = new int[_commandersMax];
		int i = 0;
		for(SiegeSpawn sp : commanders_list)
			_commanders[i++] = isSpawned(fortress, sp.getNpcId()) ? 1 : 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x7d);

		writeD(_fortId);
		writeD(_fortStatus); // fortress siege status
		writeD(_barraksCount); // barracks count

		if(_fortStatus == 0)
			for(int i = 0; i < _barraksCount; i++)
				writeD(0);
		else
		{
			int count = 0;
			for(int i : _commanders)
			{
				count++;
				if(_commandersMax == 4 && count == 4)
					writeD(1); // TODO: control room emulated
				writeD(i);
			}
		}
	}

	private boolean isSpawned(Fortress fortress, int npcId)
	{
		for(L2CommanderInstance commander : fortress.getSiege().getCommanders())
			if(commander.getNpcId() == npcId)
				return true;
		return false;
	}
}