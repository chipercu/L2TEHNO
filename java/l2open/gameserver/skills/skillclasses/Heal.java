package l2open.gameserver.skills.skillclasses;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Heal extends L2Skill 
{
    private final int cubHealPower;

    @Override
    public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first) 
	{
        if(target == null || target.isDoor() || target instanceof L2SiegeHeadquarterInstance)
            return false;

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    public Heal(StatsSet set) 
	{
        super(set);
        cubHealPower = set.getInteger("cubHealPower", 0);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets) 
	{
        // Надо уточнить формулу.
        double hp = _power;
        if(cubHealPower > 0) 
            hp = cubHealPower;
        if(!isHandler() || isStaticHeal())
            hp += (int)Math.sqrt(activeChar.getMAtk(null, this));
		//else if(activeChar.isPlayer())
		//	_log.info("Heal("+getId()+"): !isHandler="+(!isHandler())+" isStaticHeal="+isStaticHeal());

        int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;

		// TODO: Не понятно как влияют соски, но коэфициент 100% зависит от маг. атаки.
        if(sps == 2)
            hp *= getHpConsume() == 0 ? 1.5 : 1.265822784810127;
        else if(sps == 1)
            hp *= getHpConsume() == 0 ? 1.3 : 1.050632911392405;

		//if(Formulas.calcMCrit(4.5)) // guess
		//	hp *= 3.; // TODO: DS: apply on all targets ?

       /* if(activeChar.getSkillMastery(getId()) == 3) 
		{
            activeChar.removeSkillMastery(getId());
            hp *= 3;
        }*/

        for(L2Character target : targets)
            if(target != null) 
			{
                if(target.isDead() || target.isHealBlocked(true, false) || target.block_hp.get() || target != activeChar && (target.isPlayer() || activeChar.isPlayer()) && (target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped()))
                    continue;

                double maxNewHp = hp * target.calcStat(Stats.HEAL_EFFECTIVNESS, 100, activeChar, this) / 100;
				maxNewHp = activeChar.calcStat(Stats.HEAL_POWER, maxNewHp, target, this);
                double addToHp = Math.max(0, maxNewHp);

                if(addToHp > 0)
					addToHp = target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                if(getId() == 4051)
                    target.sendPacket(Msg.REJUVENATING_HP);
                else if(target.isPlayer())
                    if(activeChar == target)
                        activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
                    else
						if(activeChar.isNpc() || activeChar.isMonster())
							target.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
						else
                        	target.sendPacket(new SystemMessage(SystemMessage.XS2S_HP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                else if(target.isSummon() || target.isPet()) 
				{
                    L2Player owner = target.getPlayer();
                    if(owner != null)
					{
                        if (activeChar == target) // Пет лечит сам себя
                            owner.sendMessage(new CustomMessage("YOU_HAVE_RESTORED_S1_HP_OF_YOUR_PET", owner).addNumber(Math.round(addToHp)));
                        else if (owner == activeChar) // Хозяин лечит пета
                            owner.sendMessage(new CustomMessage("YOU_HAVE_RESTORED_S1_HP_OF_YOUR_PET", owner).addNumber(Math.round(addToHp)));
                        else
                            // Пета лечит кто-то другой
                            owner.sendMessage(new CustomMessage("S1_HAS_BEEN_RESTORED_S2_HP_OF_YOUR_PET", owner).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                    }
                }
                getEffects(activeChar, target, getActivateRate() > 0, false);
            }

        if(isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}