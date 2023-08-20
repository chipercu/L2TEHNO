package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.util.GArray;

public class PledgeReceiveWarList extends L2GameServerPacket
{
	private GArray<WarInfo> infos = new GArray<WarInfo>();
	private int _updateType;
	@SuppressWarnings("unused")
	private int _page;

	public PledgeReceiveWarList(L2Clan clan, int type, int page)
	{
		_updateType = type;
		_page = page;
		infos.clear();
		GArray<L2Clan> clans = _updateType == 1 ? clan.getAttackerClans() : clan.getEnemyClans();
		for(L2Clan _clan : clans)
		{
			if(_clan == null)
				continue;
			int war_type = clan.isAtWarWith(_clan.getClanId()) && _clan.isAtWarWith(clan.getClanId()) ? 2 : 0;
			infos.add(new WarInfo(_clan.getName(), _updateType, 0, _clan.getMembersCount(), war_type));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x3f);
		writeD(_updateType); //which type of war list sould be revamped by this packet
		writeD(0x00); //page number goes here(_page ), made it static cuz not sure how many war to add to one page so TODO here
		writeD(infos.size());
		for(WarInfo _info : infos)
		{
			writeS(_info.clan_name);
			writeD(_info.unk1);
			writeD(_info.unk2); //filler ??
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x40);

		writeD(_page);
		writeD(infos.size());
		for(WarInfo _info : infos)
		{
			writeS(_info.clan_name);
			writeD(_info.war_type); // 0 - Объявление, 1 - Перемирие, 2 - Война, 3 - Побеждает, 4 - Поражение, 5 - Ничья
			writeD(31536000);

			writeD(0x00);
			writeD(0x00);
			writeD(_info.members_count);
		}
		return true;
	}

	static class WarInfo
	{
		public String clan_name;
		public int unk1, unk2, members_count, war_type;

		public WarInfo(String _clan_name, int _unk1, int _unk2, int m_count, int war)
		{
			clan_name = _clan_name;
			unk1 = _unk1;
			unk2 = _unk2;
			members_count = m_count;
			war_type = war;
		}
	}
}