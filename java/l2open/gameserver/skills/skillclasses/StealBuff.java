package l2open.gameserver.skills.skillclasses;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class StealBuff extends L2Skill
{
	private final int _stealCount;
	private final ReentrantLock _effect_loc = new ReentrantLock();

	public StealBuff(StatsSet set)
	{
		super(set);
		_stealCount = set.getInteger("stealCount", 1);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isPlayer())
					continue;

				if(target.checkReflectSkill(activeChar, this))
					target = activeChar;

				ConcurrentLinkedQueue<L2Skill> eff = target.getEffectList().getAllSkills(true);
				if(eff.size() == 0)
					continue;

				boolean update = false;
				int counter = 0;

				double cancel_res_multiplier = target.calcStat(Stats.CANCEL_RECEPTIVE, 0, null, null); // constant resistance is applied for whole cycle of cancellation
				L2Skill[] a = eff.toArray(new L2Skill[eff.size()]);
				// Сначало крадем песни/танцы
				for(int i = 0; counter < _stealCount && i < a.length; i++)
				{
					L2Skill e = a[a.length - i - 1];

					if(e != null && e.isMusic() && e.isCancelable() && !e.isOffensive())
					{
						if(calcStealChance(target, activeChar, e, cancel_res_multiplier))
						{
							if(cloneEffect(activeChar, target, e))
							{
								update = true;
								target.getEffectList().stopEffect(e.getId());
							}
						}
						counter++;
					}
				}
				// Потом остальное
				for(int i = 0; counter < _stealCount && i < a.length; i++)
				{
					L2Skill e = a[a.length - i - 1];
					if(e != null && !e.isMusic() && e.isCancelable() && !e.isToggle() && !e.isPassive() && (!e.isOffensive() || e.getId() == 368) && e.getAbnormalType() != SkillAbnormalType.vp_up && e.getId() != 1540)
					{
						if(calcStealChance(target, activeChar, e, cancel_res_multiplier))
						{
							if(cloneEffect(activeChar, target, e))
							{
								update = true;
								target.getEffectList().stopEffect(e.getId());
							}
						}
						counter++;
					}
				}
				target.updateEffectIcons();
				if(update)
				{
					activeChar.sendChanges();
					activeChar.updateEffectIcons();
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
	
	private boolean calcStealChance2(L2Character effected, L2Character effector, L2Skill skill)
	{
		double cancel_res_multiplier = effected.calcStat(Stats.CANCEL_RECEPTIVE, 1, null, null);
		int dml = effector.getLevel() - effected.getLevel();
		double prelimChance = (dml + 45) * (1 - cancel_res_multiplier * .01);
		if(prelimChance < 25)
			prelimChance = 25;
		else if(prelimChance > 75)
			prelimChance = 75;
		return Rnd.chance(prelimChance);
	}

	private boolean calcStealChance(L2Character effected, L2Character effector, L2Skill skill, double cancel_res_multiplier)
	{
		double prelimChance=0, eml, dml;
		long buffTime;

		eml = skill.getMagicLevel();
		dml = getMagicLevel() - (eml == 0 ? effected.getLevel() : eml); // FIXME: no effect can have have mLevel == 0. Tofix in skilldata

		L2Effect ef = effected.getEffectList().getEffectBySkillId(skill.getId());
		if(ef != null)
		{
			buffTime = ef.getTimeLeft();
			cancel_res_multiplier = 1 - (cancel_res_multiplier * .01);
			prelimChance = (2. * dml + 80 + buffTime / 120000) /* * cancel_res_multiplier*/; // retail formula
		}

		if(prelimChance < 25)
			prelimChance = 25;
		else if(prelimChance > 75)
			prelimChance = 75;
		return Rnd.chance(prelimChance);
	}

	private boolean cloneEffect(L2Character cha, L2Character target, L2Skill skill)
	{
		if(cha.p_block_buff.get())
			return false;
		_effect_loc.lock();
		boolean result = false;
		try
		{
			L2Effect eff = target.getEffectList().getEffectBySkillId(skill.getId());
			if(!skill.checkSkillAbnormal(cha) && !skill.isBlockedByChar(cha, skill))
				for(EffectTemplate et : skill.getEffectTemplates())
				{
					if(!et._applyOnCaster || skill.getId() == 368)
					{
						L2Effect effect = et.getEffect(new Env(cha, cha, skill));
						if(effect != null)
						{
							if(effect.getEffectType() == EffectType.i_dispel_by_slot || effect.getEffectType() == EffectType.i_dispel_by_slot_probability)
							{
								effect.onStart();
								effect.onActionTime();
								effect.onExit();
							}
							else if(eff != null)
							{
								effect.setCount(eff.getCount());
								effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());
								cha.getEffectList().addEffect(effect);
								result = true;
							}
						}
					}
				}
		}
		finally
		{
			_effect_loc.unlock();
		}
		return result;
	}
}