package l2open.gameserver.serverpackets;

import l2open.gameserver.model.clan_find.ClanEntryStatus;

/**
 * @author Sdw
 */
public class ExPledgeRecruitApplyInfo extends L2GameServerPacket
{
	private final ClanEntryStatus _status;
	
	public ExPledgeRecruitApplyInfo(ClanEntryStatus status)
	{
		_status = status;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x14A);

		writeD(_status.ordinal());
	}
}