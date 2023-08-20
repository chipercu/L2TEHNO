package l2open.gameserver.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.clan_find.PledgeRecruitInfo;
import l2open.gameserver.model.clan_find.ClanEntryManager;
import l2open.gameserver.tables.ClanTable;

public class ExPledgeRecruitInfo extends L2GameServerPacket
{
	/*private final String _clanName;
	private final String _leaderName;
	private final int _clanLevel;
	private final int _clanMemberCount;
	private final List<SubUnit> _subUnits = new ArrayList<SubUnit>();*/

	private final PledgeRecruitInfo _pledgeRecruitInfo;
	private final L2Clan _clan;

	public ExPledgeRecruitInfo(int clanId)
	{
		_pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
		_clan = ClanTable.getInstance().getClan(clanId);
	}
	/*public ExPledgeRecruitInfo(L2Clan clan)
	{
		_clanName = clan.getName();
		_leaderName = clan.getLeader().getName();
		_clanLevel = clan.getLevel();
		_clanMemberCount = clan.getAllSize();

		for(SubUnit su : clan.getAllSubUnits())
		{
			if(su.getType() == L2Clan.SUBUNIT_MAIN_CLAN)
				continue;

			_subUnits.add(su);
		}
	}*/

	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x149);

		/*writeS(_clanName);
		writeS(_leaderName);
		writeD(_clanLevel);
		writeD(_clanMemberCount);
		writeD(_subUnits.size());
		for(SubUnit su : _subUnits)
		{
			writeD(su.getType());
			writeS(su.getName());
		}*/
		if (_pledgeRecruitInfo == null)
		{
			writeS(_clan.getName());
			writeS(_clan.getLeaderName());
			writeD(_clan.getLevel());
			writeD(_clan.getMembersCount());
			writeD(0x00);
		}
		else
		{
			writeS(_pledgeRecruitInfo.getClan().getName());
			writeS(_pledgeRecruitInfo.getClan().getLeaderName());
			writeD(_pledgeRecruitInfo.getClan().getLevel());
			writeD(_pledgeRecruitInfo.getClan().getMembersCount());
			writeD(_pledgeRecruitInfo.getKarma());
		}
	}
}