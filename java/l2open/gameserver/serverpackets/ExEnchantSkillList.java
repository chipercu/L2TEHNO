package l2open.gameserver.serverpackets;

import java.util.Vector;

public class ExEnchantSkillList extends L2GameServerPacket
{
	public enum EnchantSkillType
	{
		NORMAL,
		SAFE,
		UNTRAIN,
		CHANGE_ROUTE,
	}

	private final Vector<Skill> _skills;
	private final EnchantSkillType _type;

	class Skill
	{
		public int id;
		public int level;

		Skill(int id, int nextLevel)
		{
			this.id = id;
			level = nextLevel;
		}
	}

	public void addSkill(int id, int level)
	{
		_skills.add(new Skill(id, level));
	}

	public ExEnchantSkillList(EnchantSkillType type)
	{
		_type = type;
		_skills = new Vector<Skill>();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x29);

		writeD(_type.ordinal());
		writeD(_skills.size());
		for(Skill sk : _skills)
		{
			writeD(sk.id);
			writeD(sk.level);
		}
	}
}