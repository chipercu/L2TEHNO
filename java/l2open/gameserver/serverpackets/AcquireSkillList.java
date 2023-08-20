package l2open.gameserver.serverpackets;

import l2open.util.GArray;

/**
 * sample
 *
 * a3
 * 05000000
 * 03000000 03000000 06000000 3c000000 00000000 	power strike
 * 10000000 02000000 06000000 3c000000 00000000 	mortal blow
 * 38000000 04000000 06000000 36010000 00000000 	power shot
 * 4d000000 01000000 01000000 98030000 01000000 	ATTACK aura  920sp
 * 8e000000 03000000 03000000 cc010000 00000000     Armor Mastery
 *
 * format   d (ddddd)
 * skillid, level, maxlevel?,
 * 
 * Пример снифа изучения дополнительных клановых скилов:
 * 0000: 90 03 00 00 00 02 00 00 00 64 02 00 00 01 00 00    .........d......
 * 0010: 00 01 00 00 00 a0 28 00 00 01 00 00 00 d2 07 00    ......(.........
 * 0020: 00 67 02 00 00 01 00 00 00 01 00 00 00 a0 28 00    .g............(.
 * 0030: 00 01 00 00 00 d2 07 00 00                         .........
 */
public class AcquireSkillList extends L2GameServerPacket
{
	private final GArray<Skill> _skills;
	private int _skillsType;
	public static final int USUAL = 0;
	public static final int FISHING = 1;
	public static final int CLAN = 2;
	public static final int CLAN_ADDITIONAL = 3;
	public static final int TRANSFORMATION = 4;
	public static final int TRANSFER = 5;
	public static final int COLLECTION = 6;
	public static final int CERTIFICATION = 7;
	public static final int OTHER = -100;
	public static final int AUTO_UP = -200;

	class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int spCost;
		public int requirements;

		Skill(int id, int nextLevel, int maxLevel, int spCost, int requirements)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.spCost = spCost;
			this.requirements = requirements;
		}
	}

	public AcquireSkillList(int type)
	{
		_skills = new GArray<Skill>();
		_skillsType = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x90);
		writeD(_skillsType); // Kamael: 0: standart, 1: fishing, 2: clans, 3: clan_additional, 4: transformation
		writeD(_skills.size());

		for(Skill temp : _skills)
		{
			writeD(temp.id);
			writeD(temp.nextLevel);
			writeD(temp.maxLevel);
			writeD(temp.spCost);
			writeD(temp.requirements);
			if(_skillsType == CLAN_ADDITIONAL)
				writeD(2002); // хз почему 2002, но в снифе было именно так
		}
	}
}