package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;

public class ManaDam extends L2Skill 
{
    public ManaDam(StatsSet set) 
	{
        super(set);
    }
	
	@Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				double mAtk = activeChar.getMAtk(target, this);
				if(sps == 2)
					mAtk *= 4;
				else if(sps == 1)
					mAtk *= 2;

				double mDef = target.getMDef(activeChar, this);
				if(mDef < 1.)
					mDef = 1.;

				double damage = Math.sqrt(mAtk) * this.getPower() * (target.getMaxMp() / 97) / mDef;

				boolean crit = Formulas.calcMCrit(activeChar.getMagicCriticalRate(target, this) * (activeChar.isPlayer() ? activeChar.getPlayer().getTemplate().m_atk_crit_chance_mod : 1));
				if(crit)
				{
					activeChar.sendPacket(Msg.MAGIC_CRITICAL_HIT);
					damage *= activeChar.calcStat(Stats.MCRITICAL_DAMAGE, activeChar.isPlayable() && target.isPlayable() ? 2.5 : 3., target, this);
				}
				
				int levelDiff = target.getLevel() - activeChar.getLevel();	
				double magic_rcpt = target.calcStat(Stats.p_resist_dd_magic, 1, activeChar, this) - activeChar.calcStat(Stats.MAGIC_POWER, target, this);
				double failChance = 4. * Math.max(1, levelDiff) * (1 + magic_rcpt / 100);
				if (Rnd.chance(failChance))
				{
					SystemMessage msg;
					if (levelDiff > 9 || (activeChar.calcStat(Stats.MAGIC_POWER, target, this) <= -900) && Rnd.chance(50))
					{
						damage = 1.0;
						msg = new SystemMessage(2269).addName(target).addName(activeChar);
						activeChar.sendPacket(msg);
						target.sendPacket(msg);
					}
				}

				target.reduceCurrentMp(damage, activeChar);

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}	
}