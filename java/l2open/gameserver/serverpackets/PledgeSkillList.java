package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Skill;
import l2open.util.GArray;

/**
 * Пример (828 протокол):
 * 0000: fe 3a 00 05 00 00 00 00 00 00 00 72 01 00 00 01    .:.........r....
 * 0010: 00 00 00 79 01 00 00 01 00 00 00 7b 01 00 00 02    ...y.......{....
 * 0020: 00 00 00 86 01 00 00 01 00 00 00 87 01 00 00 01    ................
 * 0030: 00 00 00                                           ...
 */
public class PledgeSkillList extends L2GameServerPacket
{
	private GArray<SkillInfo> infos = new GArray<SkillInfo>();

	public PledgeSkillList(L2Clan clan)
	{
		for(L2Skill sk : clan.getAllSkills())
			infos.add(new SkillInfo(sk.getId(), sk.getLevel()));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x3a);
		writeD(infos.size());
		writeD(0x00);
		for(SkillInfo info : infos)
		{
			writeD(info._id);
			writeD(info._level);
		}
		infos.clear();
	}

	static class SkillInfo
	{
		public int _id, _level;

		public SkillInfo(int id, int level)
		{
			_id = id;
			_level = level;
		}
	}
}