package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2ClanMember;

public class PledgeShowMemberListAdd extends L2GameServerPacket
{
	private String member_name;
	private int member_level, member_class_id, member_race, member_sex, member_online, member_PledgeType;

	public PledgeShowMemberListAdd(L2ClanMember member)
	{
		member_name = member.getName();
		member_level = member.getLevel();
		member_class_id = member.getClassId();
		member_online = member.isOnline() ? member.getObjectId() : 0;
		member_PledgeType = member.getPledgeType();
		member_sex = member.getSex();
		member_race = member.getRace();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5c);
		writeS(member_name);
		writeD(member_level);
		writeD(member_class_id);
		writeD(member_sex);
		writeD(member_race);
		writeD(member_online); // obj_id=online 0=offline
		writeD(member_PledgeType);
	}
}