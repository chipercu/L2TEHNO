package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ClanMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private final L2ClanMember _member;
	private final L2Clan _clan;

	public PledgeReceiveMemberInfo(L2ClanMember member)
	{
		_clan = member.getClan();
		_member = member;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x3e);

		writeD(_member.getPledgeType());
		writeS(_member.getName());
		writeS(_member.getTitle());
		writeD(_member.getPowerGrade());

		if(_member.getPledgeType() != 0)
			writeS(_clan.getSubPledge(_member.getPledgeType()).getName());
		else
			writeS(_clan.getName());

		writeS(_member.getRelatedName()); // apprentice/sponsor name if any
	}
}