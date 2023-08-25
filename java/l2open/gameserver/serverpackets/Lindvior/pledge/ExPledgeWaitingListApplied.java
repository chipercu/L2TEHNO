package l2open.gameserver.serverpackets;

import l2open.gameserver.model.clan_find.ClanEntryManager;
import l2open.gameserver.model.clan_find.PledgeApplicantInfo;
import l2open.gameserver.model.clan_find.PledgeRecruitInfo;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends L2GameServerPacket
{
	private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
	private final PledgeRecruitInfo _pledgeRecruitInfo;
	
	public ExPledgeWaitingListApplied(int clanId, int playerId)
	{
		_pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
		_pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x14D);

		writeD(_pledgeRecruitInfo.getClan().getClanId());
		writeS(_pledgeRecruitInfo.getClan().getName());
		writeS(_pledgeRecruitInfo.getClan().getLeaderName());
		writeD(_pledgeRecruitInfo.getClan().getLevel());
		writeD(_pledgeRecruitInfo.getClan().getMembersCount());
		writeD(_pledgeRecruitInfo.getKarma());
		writeS(_pledgeRecruitInfo.getInformation());
		writeS(_pledgePlayerRecruitInfo.getMessage());
	}
}