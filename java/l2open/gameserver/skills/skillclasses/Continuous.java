package l2open.gameserver.skills.skillclasses;

import l2open.config.*;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Continuous extends L2Skill
{
	private final int _lethal1;
	private final int _lethal2;

	public Continuous(StatsSet set)
	{
		super(set);
		_lethal1 = set.getInteger("lethal1", 0);
		_lethal2 = set.getInteger("lethal2", 0);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				// Player holding a cursed weapon can't be buffed and can't buff
				if(getSkillType() == L2Skill.SkillType.BUFF && target != activeChar)
					if(target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped())
						continue;

				if(target.checkReflectSkill(activeChar, this))
					target = activeChar;

				if(getAbsorbPartStatic() > 0 && !target.isDoor() && !activeChar.isHealBlocked(false, true) && !activeChar.block_hp.get())
					activeChar.setCurrentHp(getAbsorbPartStatic() + activeChar.getCurrentHp(), false);
				Formulas.calcLethalHit(activeChar, target, this);

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}