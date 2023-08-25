package l2open.gameserver.serverpackets;

import java.util.List;

import l2open.gameserver.model.clan_find.PledgeWaitingInfo;

/**
 * @author Sdw
 */
public class ExPledgeDraftListSearch extends L2GameServerPacket
{
	final List<PledgeWaitingInfo> _pledgeRecruitList;

	public ExPledgeDraftListSearch(List<PledgeWaitingInfo> pledgeRecruitList)
	{
		_pledgeRecruitList = pledgeRecruitList;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x150);

		writeD(_pledgeRecruitList.size());
		for (PledgeWaitingInfo prl : _pledgeRecruitList)
		{
			writeD(prl.getPlayerId());
			writeS(prl.getPlayerName());
			writeD(prl.getKarma());
			writeD(prl.getPlayerClassId());
			writeD(prl.getPlayerLvl());
		}
	}
}