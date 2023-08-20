package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.tables.ClanTable;
import l2open.util.GArray;

public class ExShowAgitInfo extends L2GameServerPacket
{
	private GArray<AgitInfo> infos = new GArray<AgitInfo>();

	public ExShowAgitInfo()
	{
		String clan_name, leader_name;
		int ch_id, lease;

		for(ClanHall clanHall : ClanHallManager.getInstance().getClanHalls().values())
		{
			ch_id = clanHall.getId();
			lease = clanHall.getLease() == 0 ? 1 : 0;

			L2Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
			clan_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getName();
			leader_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getLeader().getName();
			infos.add(new AgitInfo(clan_name, leader_name, ch_id, lease));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x16);
		writeD(infos.size());
		for(AgitInfo _info : infos)
		{
			writeD(_info.ch_id);
			writeS(_info.clan_name);
			writeS(_info.leader_name);
			writeD(_info.lease);
		}
		infos.clear();
	}

	static class AgitInfo
	{
		public String clan_name, leader_name;
		public int ch_id, lease;

		public AgitInfo(String _clan_name, String _leader_name, int _ch_id, int _lease)
		{
			clan_name = _clan_name;
			leader_name = _leader_name;
			ch_id = _ch_id;
			lease = _lease;
		}
	}
}