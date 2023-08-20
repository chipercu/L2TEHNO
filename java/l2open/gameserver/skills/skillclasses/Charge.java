package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Formulas.AttackInfo;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;

public class Charge extends L2Skill
{
	public static final int MAX_CHARGE = 8;

	private int _charges;
	private boolean _fullCharge;

	public Charge(StatsSet set)
	{
		super(set);
		_charges = set.getInteger("charges", getLevel());
		_fullCharge = set.getBool("fullCharge", false);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		L2Player player = (L2Player) activeChar;

		//Камушки можно юзать даже если заряд > 7, остальное только если заряд < уровень скила
		if(getPower() <= 0 && getId() != 2165 && player.getIncreasedForce() >= _charges)
		{
			activeChar.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return false;
		}
		else if(getId() == 2165)
			player.sendPacket(new MagicSkillUse(player, player, 2165, 1, 0, 0));

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if(ss && getTargetType() != SkillTargetType.TARGET_SELF)
			activeChar.unChargeShots(false);

		for(L2Character target : targets)
		{
			if(target.isDead() || target == activeChar)
				continue;
			
			boolean reflected = target.checkReflectSkill(activeChar, this);
			if(reflected)
				target = activeChar;

			if(getPower() > 0) // Если == 0 значит скилл "отключен"
			{
				if(Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, activeChar, this))) 
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(activeChar));
					target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(activeChar));
					continue;
				}
				AttackInfo info = Formulas.calcPhysDam(activeChar, target, null, false, false, ss, false, true, false);
				target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, info.damage, true, false, info.crit, false);

				activeChar.sendHDmgMsg(target, activeChar, this, (int)info.damage, info.crit, false);
				if(!reflected)
					target.doCounterAttack(this, activeChar);
			}

			getEffects(activeChar, target, getActivateRate() > 0, false);
		}
		chargePlayer((L2Player) activeChar, getId());
	}

	public void chargePlayer(L2Player player, Integer skillId)
	{
		if(player.getIncreasedForce() >= _charges)
		{
			player.sendPacket(Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return;
		}
		if(_fullCharge)
			player.setIncreasedForce(_charges);
		else
			player.setIncreasedForce(player.getIncreasedForce() + 1);
	}
}