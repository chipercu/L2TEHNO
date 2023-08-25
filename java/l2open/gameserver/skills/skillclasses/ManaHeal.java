package l2open.gameserver.skills.skillclasses;

import l2open.config.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class ManaHeal extends L2Skill
{
	private final boolean _staticManaHeal;
	private final boolean _ManaHealByLvl;
	private final boolean _ManaHealByRecharge;
	public ManaHeal(StatsSet set)
	{
		super(set);
		_staticManaHeal = set.getBool("staticManaHeal", false);
		_ManaHealByLvl = set.getBool("ManaHealByLvl", false);
		_ManaHealByRecharge = set.getBool("ManaHealByRecharge", false);

	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!isHandler() && oneTarget(activeChar) && !checkTarget(target, activeChar))
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
			if(checkTarget(target, activeChar))
			{
				if(target.isDead() || target.isHealBlocked(true, false) || target.block_mp.get())
					continue;

				double mp = _power;

				int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
				if(sps > 0 && ConfigValue.ManahealSpSBonus)
					mp *= sps == 2 ? 1.5 : 1.3;

				if(_ManaHealByRecharge)
				{
					mp = target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, mp, null, null);
				}
				else if(_ManaHealByLvl)
				{
					mp = target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, mp, null, null);
					if(target.getLevel() > getMagicLevel())
					{
						int lvlDiff = target.getLevel() - getMagicLevel();
						//if target is too high compared to skill level, the amount of recharged mp gradually decreases.
						if (lvlDiff == 6)		//6 levels difference:
							mp *= 0.9;			//only 90% effective
						else if (lvlDiff == 7)
							mp *= 0.8;			//80%
						else if (lvlDiff == 8)
							mp *= 0.7;			//70%
						else if (lvlDiff == 9)
							mp *= 0.6;			//60%
						else if (lvlDiff == 10)
							mp *= 0.5;			//50%
						else if (lvlDiff == 11)
							mp *= 0.4;			//40%
						else if (lvlDiff == 12)
							mp *= 0.3;			//30%
						else if (lvlDiff == 13)
							mp *= 0.2;			//20%
						else if (lvlDiff == 14)
							mp *= 0.1;			//10%
						else if (lvlDiff >= 15)	//15 levels or more:
							mp = 0;				//0mp recharged
					}
				}

				mp = Math.max(0, mp);

				if(mp < 0)
					mp = 0;

				mp = target.setCurrentMp(mp + target.getCurrentMp());
				StatusUpdate sump = new StatusUpdate(target.getObjectId());
				sump.addAttribute(StatusUpdate.CUR_MP, (int) target.getCurrentMp());
				target.sendPacket(sump);

				SystemMessage sm;
				if(activeChar.isPlayer() && activeChar != target)
				{
					sm = new SystemMessage(SystemMessage.XS2S_MP_HAS_BEEN_RESTORED_BY_S1);
					sm.addString(activeChar.getName());
					sm.addNumber((int) mp);
					target.sendPacket(sm);
				}
				else
				{
					sm = new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED);
					sm.addNumber((int) mp);
					target.sendPacket(sm);
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}

	/**
	 * Нельзя речарджить речарджеров и улучшенных куриц.
	 */
	private final boolean checkTarget(L2Character target, L2Character activeChar)
	{
		if(target == null || target.isDead() || target.isHealBlocked(true, false))
			return false;

		// бутылки, хербы, и массовый речардж действует на всех
		if(getTargetType() == SkillTargetType.TARGET_SELF || !oneTarget(activeChar))
			return true;

		// петы и саммоны могут речарджить всех, ограничения только для игроков
		if(target.isPlayer() && activeChar.isPlayer() && getId() == SKILL_RECHARGE && target.getPlayer().getSkillLevel(SKILL_RECHARGE) > 0)
			return false;

		// речардж кукабурр - большая халява
		if(target.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID && !ConfigValue.AltPetRecharge)
			return false;

		return true;
	}
}