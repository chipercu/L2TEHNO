package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Drain extends L2Skill
{
    private float _absorbAbs;

    public Drain(StatsSet set)
	{
        super(set);
        _absorbAbs = set.getFloat("absorbAbs", 0.f);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
        int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
        boolean ss = isSSPossible() && activeChar.getChargedSoulShot();

		boolean corpseSkill = _targetType == SkillTargetType.TARGET_CORPSE;

        for (L2Character target : targets)
            if (target != null && !target.block_hp.get())
			{
				boolean reflected = !corpseSkill && target.checkReflectSkill(activeChar, this);
				if(reflected)
					target = activeChar;
				if(getTraitType().fullResist(target))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(_displayId, _displayLevel));
					continue;
				}

                if(getPower() > 0 || _absorbAbs > 0) // Если == 0 значит скилл "отключен"
                {
                    if(target.isDead() && !corpseSkill)
                        continue;

					double hp = 0.;
					double targetHp = target.getCurrentHp();

                    if(!corpseSkill)
					{
                        double damage = isMagic() ? Formulas.calcMagicDam(activeChar, target, this, sps, false) : Formulas.calcPhysDam(activeChar, target, this, false, false, ss, false, false, false).damage;
                        double targetCP = target.getCurrentCp();

                        // Нельзя восстанавливать HP из CP
                        if (damage > targetCP || !target.isPlayer())
                            hp = (damage - targetCP) * _absorbPart;

                        target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
						if(!reflected)
							target.doCounterAttack(this, activeChar);
                    }

                    if (_absorbAbs == 0 && _absorbPart == 0)
                        continue;

                    hp += _absorbAbs;

                    // Нельзя восстановить больше hp, чем есть у цели.
                    if (hp > targetHp && !corpseSkill)
                        hp = targetHp;

                    double addToHp = Math.max(0, hp);

                    if(addToHp > 0 && !target.isDoor() && !activeChar.isHealBlocked(false, true) && !activeChar.block_hp.get())
                        activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);

                    if(target.isDead() && corpseSkill && !target.isPlayer())
					{
                        activeChar.getAI().setAttackTarget(null);
                        target.endDecayTask();
                    }
                }

                getEffects(activeChar, target, getActivateRate() > 0, false);
            }

        if (isMagic() ? sps != 0 : ss)
            activeChar.unChargeShots(isMagic());
    }
}