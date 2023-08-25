package l2open.gameserver.serverpackets;

import l2open.gameserver.model.clan_find.*;

public class ExPledgeRecruitBoardDetail extends L2GameServerPacket
{
	final PledgeRecruitInfo _pledgeRecruitInfo;
	
	public ExPledgeRecruitBoardDetail(PledgeRecruitInfo pledgeRecruitInfo)
	{
		_pledgeRecruitInfo = pledgeRecruitInfo;
	}

	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x14C);

		writeD(_pledgeRecruitInfo.getClanId());
		writeD(_pledgeRecruitInfo.getKarma());
		writeS(_pledgeRecruitInfo.getInformation());
		writeS(_pledgeRecruitInfo.getDetailedInformation());
		//writeD(_pledgeRecruitInfo.getApplicationType());
		//writeD(_pledgeRecruitInfo.getRecruitType());
	}
}