package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

/**
 * @author : Ragnarok
 * @date : 04.09.11   12:50
 */
public class CubMDam extends L2Skill 
{
    public CubMDam(StatsSet set) 
	{
        super(set);
        this._power = 1.0;
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets) 
	{
        for (L2Character target : targets) 
		{
            if (target != null) 
			{
                if (target.isDead())
                    continue;
                double damage = Formulas.calcMagicDam(activeChar, target, this, 1, true);
                if (damage >= 1.0) 
				{
                    target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
                }
                getEffects(activeChar, target, getActivateRate() > 0, false);
            }
        }
    }
}
