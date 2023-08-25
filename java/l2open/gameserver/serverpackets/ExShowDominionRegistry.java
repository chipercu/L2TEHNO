package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;

//dSSSdddddddd[dd[d]]
public class ExShowDominionRegistry extends L2GameServerPacket
{
	private int _territoryId;
	private String _territoryOwnerClanName;
	private String _territoryOwnerLeaderName;
	private String _territoryOwnerAllyName;
	private int _clanReq;
	private int _mercReq;
	private int _warTime;
	private int _currentTime;
	private int _registredAsPlayer;
	private int _registredAsClan;
	private GArray<CastleFlagsInfo> _cfi = new GArray<CastleFlagsInfo>();

	public ExShowDominionRegistry(L2Player activeChar, int terrId)
	{
		_territoryId = terrId;
		Castle castle = CastleManager.getInstance().getCastleByIndex(terrId);
		if(castle == null)
		{
			_log.info("Cant find castle with ID " + terrId);
			return;
		}
		L2Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
		_territoryOwnerClanName = clan == null ? "No Clan" : clan.getName();
		_territoryOwnerLeaderName = clan == null ? "No Owner" : clan.getLeaderName();
		_territoryOwnerAllyName = clan == null || clan.getAlliance() == null ? "No Ally" : clan.getAlliance().getAllyName();
		_warTime = (int) (TerritorySiege.getSiegeDate().getTimeInMillis() / 1000);
		_currentTime = (int) (System.currentTimeMillis() / 1000);
		_mercReq = TerritorySiege.getPlayersForTerritory(terrId);
		_clanReq = TerritorySiege.getClansForTerritory(terrId);
		_registredAsPlayer = TerritorySiege.getTerritoryForPlayer(activeChar.getObjectId());
		_registredAsClan = TerritorySiege.getTerritoryForClan(activeChar.getClanId());
		for(Castle c : CastleManager.getInstance().getCastles().values())
			_cfi.add(new CastleFlagsInfo(c.getId(), c.getFlags()));
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x90);

		writeD(_territoryId); // Current Territory Id
		writeS(_territoryOwnerClanName); // Owners Clan
		writeS(_territoryOwnerLeaderName); // Owner Clan Leader
		writeS(_territoryOwnerAllyName); // Owner Alliance
		writeD(_clanReq); // Clan Request
		writeD(_mercReq); // Merc Request
		writeD(_warTime); // War Time
		writeD(_currentTime); // Current Time
		writeD(_registredAsClan == _territoryId ? 1 : 0); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(_registredAsPlayer == _territoryId ? 1 : 0); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(0x01); // unknown
		writeD(_cfi.size()); // Territory Count
		for(CastleFlagsInfo cf : _cfi)
		{
			writeD(0x50 + cf.id); // Territory Id
			writeD(cf.flags.length); // Emblem Count
			for(int flag : cf.flags)
				writeD(0x50 + flag); // Emblem ID - should be in for loop for emblem count
		}
	}

	private class CastleFlagsInfo
	{
		public int id;
		public int[] flags;

		public CastleFlagsInfo(int id_, int[] flags_)
		{
			id = id_;
			flags = flags_;
		}
	}
}