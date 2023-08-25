package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class PartySmallWindowAdd extends L2GameServerPacket
{
	//dddSdddddddddddd{ddSddddd}
	private final PartySmallWindowAll.PartySmallWindowMemberInfo member;

	public PartySmallWindowAdd(L2Player _member)
	{
		member = new PartySmallWindowAll.PartySmallWindowMemberInfo(_member);
	}

	@Override
	protected final void writeImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;

		writeC(0x4F);
		writeD(player.getObjectId()); // leader
		writeD(0x00);// distribution

		writeD(member._id);
		writeS(member._name);
		writeD(member.curCp);
		writeD(member.maxCp);

		if(getClient().isLindvior())
			writeD(0x00); // vitality
		
		writeD(member.curHp);
		writeD(member.maxHp);
		writeD(member.curMp);
		writeD(member.maxMp);
		writeD(member.level);
		writeD(member.class_id);
		writeD(0x00);//writeD(0x01); ??
		writeD(member.race_id);
		writeD(0x00);
		writeD(0x00);
		if(getClient().isLindvior())
			writeD(0x00); // replace Идет ли поиск замены игроку
	}
}