package l2open.gameserver.serverpackets;

import java.util.List;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.clan_find.PledgeRecruitInfo;

public class ExPledgeRecruitBoardSearch extends L2GameServerPacket
{
	final List<PledgeRecruitInfo> _clanList;
	final private int _currentPage;
	final private int _totalNumberOfPage;
	final private int _clanOnCurrentPage;
	final private int _startIndex;
	final private int _endIndex;
	
	final static int CLAN_PER_PAGE = 12;
	
	public ExPledgeRecruitBoardSearch(List<PledgeRecruitInfo> clanList, int currentPage)
	{
		_clanList = clanList;
		_currentPage = currentPage;
		_totalNumberOfPage = (int) Math.ceil((double) _clanList.size() / CLAN_PER_PAGE);
		_startIndex = (_currentPage - 1) * CLAN_PER_PAGE;
		_endIndex = (_startIndex + CLAN_PER_PAGE) > _clanList.size() ? _clanList.size() : _startIndex + CLAN_PER_PAGE;
		_clanOnCurrentPage = _endIndex - _startIndex;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x14B);

		writeD(_currentPage);
		writeD(_totalNumberOfPage);
		writeD(_clanOnCurrentPage);
		
		for (int i = _startIndex; i < _endIndex; i++)
		{
			PledgeRecruitInfo pri = _clanList.get(i);
			if(pri != null)
			{
				writeD(pri.getClanId());
				writeD(pri.getClan().getAllyId());
			}
		}
		for (int i = _startIndex; i < _endIndex; i++)
		{
			PledgeRecruitInfo pri = _clanList.get(i);
			if(pri != null)
			{
				final L2Clan clan = pri.getClan();
				writeD(clan.getAllyCrestId());
				writeD(clan.getCrestId());
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getLevel());
				writeD(clan.getMembersCount());
				writeD(pri.getKarma());
				writeS(pri.getInformation());
				//writeD(pri.getApplicationType());
				//writeD(pri.getRecruitType());
			}
		}
	}
}