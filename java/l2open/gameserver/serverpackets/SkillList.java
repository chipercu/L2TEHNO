package l2open.gameserver.serverpackets;

import javolution.util.FastTable;
import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTreeTable;

/**
 * format   d (dddc)
 */
public class SkillList extends L2GameServerPacket
{
	private FastTable<L2Skill> _skills;
	private boolean canEnchant;
	private final int _learnedSkill=0;

	public SkillList(L2Player p)
	{
		_skills = new FastTable<L2Skill>();
		_skills.addAll(p.getAllSkills());
		canEnchant = p.getTransformation() == 0/* && (!ConfigValue.MultiProfa || !p.isSubClassActive())*/;
	//	l2open.util.Util.test();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x5f);
		writeD(_skills.size());

		for(L2Skill temp : _skills)
		{
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			writeD(temp.getDisplayLevel());
			writeD(temp.getDisplayId());
			writeC(temp.isNotUse()); // иконка скилла серая если не 0
			writeC(canEnchant ? SkillTreeTable.isEnchantable(temp) : 0); // для заточки: если 1 скилл можно точить
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x5f);
		writeD(_skills.size());

		for(L2Skill temp : _skills)
		{
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			writeD(temp.getDisplayLevel());
			writeD(temp.getDisplayId());
			writeD(-1); // writeD(temp.getSkillType() == SkillType.EMDAM ? temp.getDisplayId() : -1);
			writeC(temp.isNotUse()); // иконка скилла серая если не 0
			writeC(canEnchant ? SkillTreeTable.isEnchantable(temp) : 0); // для заточки: если 1 скилл можно точить
		}
		writeD(_learnedSkill);
		getClient().sendPacket(new ExAcquirableSkillListByClass(getClient().getActiveChar()));
		return true;
	}
}