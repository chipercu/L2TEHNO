package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

import java.util.Collection;

public class GMViewSkillInfo extends L2GameServerPacket
{
	private String _charName;
	private Collection<L2Skill> _skills;
	private boolean _isClanSkillsDisabled;

	public GMViewSkillInfo(L2Player cha)
	{
		_charName = cha.getName();
		_skills = cha.getAllSkills();
		_isClanSkillsDisabled = cha.getClan() != null && cha.getClan().getReputationScore() < 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x97);
		writeS(_charName);
		writeD(_skills.size());
		for(L2Skill skill : _skills)
		{
			writeD(skill.isPassive() ? 1 : 0);
			writeD(skill.getDisplayLevel());
			writeD(skill.getId());
			writeC(_isClanSkillsDisabled && skill.isClanSkill() ? 1 : 0);
			writeC(SkillTable.getInstance().getMaxLevel(skill.getId()) > 100 ? 1 : 0);
		}
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0x97);
		writeS(_charName);
		writeD(_skills.size());

		for(L2Skill temp : _skills)
		{
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			writeD(temp.getDisplayLevel());
			writeD(temp.getDisplayId());
			writeD(-1); // writeD(temp.getSkillType() == SkillType.EMDAM ? temp.getDisplayId() : -1);
			writeC(temp.isNotUse()); // иконка скилла серая если не 0
			writeC(SkillTable.getInstance().getMaxLevel(temp.getId()) > 100 ? 1 : 0);
		}
		writeD(0);
		return true;
	}
}