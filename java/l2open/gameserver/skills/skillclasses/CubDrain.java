package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

/**
 * @author : Ragnarok
 * @date : 30.08.11   0:52
 */
public class CubDrain extends L2Skill 
{
    private float absorbAbs;

    public CubDrain(StatsSet set) 
	{
        super(set);
        absorbAbs = set.getFloat("absorbAbs", 0.f);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets) 
	{
        for(L2Character target : targets) 
		{
            if(target != null && !target.block_hp.get()) 
			{
                if(target.isDead() && _targetType != SkillTargetType.TARGET_CORPSE)
                    continue;
                double hp = 0.;
                double targetHp = target.getCurrentHp();
                if (_targetType != SkillTargetType.TARGET_CORPSE) 
				{
                    double damage = isMagic() ? Formulas.calcMagicDam(activeChar, target, this, 1, true) : Formulas.calcPhysDam(activeChar, target, this, false, false, true, false, false, false).damage;
                    double targetCP = target.getCurrentCp();
                    // Нельзя восстанавливать HP из CP
                    if (damage > targetCP || !target.isPlayer()) 
					{
                        hp = (damage - targetCP) * _absorbPart;
                    }
                    target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
                }
                if(absorbAbs == 0 && _absorbPart == 0) 
                    continue;
                hp += absorbAbs;
                // Нельзя восстановить больше hp, чем есть у цели.
                if(hp > targetHp && _targetType != SkillTargetType.TARGET_CORPSE) 
                    hp = targetHp;
                double addToHp = Math.max(0, hp);
                if(addToHp > 0 && !target.isDoor() && !activeChar.isHealBlocked(true, true) && !activeChar.block_hp.get()) 
                    activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);
                if(target.isDead() && _targetType == SkillTargetType.TARGET_CORPSE && !target.isPlayer()) 
				{
                    activeChar.getAI().setAttackTarget(null);
                    target.endDecayTask();
                }
            }
            getEffects(activeChar, target, getActivateRate() > 0, false);
        }
    }
}
