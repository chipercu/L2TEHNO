package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.model.instances.SeducedInvestigatorInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class HealPercent extends L2Skill
{
	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || target.isDoor() || target instanceof L2SiegeHeadquarterInstance)
			return false;
		if(activeChar.isPlayable() && (target.isMonster() && !(target instanceof SeducedInvestigatorInstance)))
			return false;
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	public HealPercent(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.block_hp.get() || target.isHealBlocked(true, false) && getId() != 1258 && !(target instanceof SeducedInvestigatorInstance))
					continue;

				getEffects(activeChar, target, getActivateRate() > 0, false);

				double addToHp = Math.max(0, target.getMaxHp() * _power / 100);

				if(addToHp > 0)
					addToHp = target.setCurrentHp(addToHp + target.getCurrentHp(), false);
				if(target.isPlayer())
					if(activeChar != target)
						if(activeChar.isNpc() || activeChar.isMonster())
							target.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
						else
							target.sendPacket(new SystemMessage(SystemMessage.XS2S_HP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
					else
						activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}