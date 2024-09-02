package l2open.gameserver.skills.skillclasses;

import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;

public class Spoil extends L2Skill {
    public Spoil(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets) {
        if (!activeChar.isPlayer())
            return;

        int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : (activeChar.getChargedSoulShot() ? 2 : 0)) : 0;
        if (ss > 0 && getPower() > 0)
            activeChar.unChargeShots(false);

        for (L2Character target : targets)
            if (target != null && !target.isDead()) {
                if (target.isMonster())
                    if (((L2MonsterInstance) target).isSpoiled())
                        activeChar.sendPacket(Msg.ALREADY_SPOILED);
                    else {
                        L2MonsterInstance monster = (L2MonsterInstance) target;
                        boolean success;
                        int monsterLevel = monster.getLevel();
                        int modifier = Math.abs(monsterLevel - activeChar.getLevel());
                        double rateOfSpoil = ConfigValue.BasePercentChanceOfSpoilSuccess;

                        if (modifier > 8)
                            rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;

                        rateOfSpoil = rateOfSpoil * getMagicLevel() / monsterLevel;

                        if (getId() == 947)
                            rateOfSpoil = 99;

                        if (rateOfSpoil < ConfigValue.MinimumPercentChanceOfSpoilSuccess)
                            rateOfSpoil = ConfigValue.MinimumPercentChanceOfSpoilSuccess;
                        else if (rateOfSpoil > 99.)
                            rateOfSpoil = 99.;

                        activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Spoil.Chance", activeChar).addNumber((long) rateOfSpoil));
                        success = Rnd.chance(rateOfSpoil);

                        if (success) {
                            monster.setSpoiled(true, (L2Player) activeChar);
                            activeChar.sendPacket(Msg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
                        } else
                            activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
                    }
                if (getPower() > 0) {
                    double damage = isMagic() ? Formulas.calcMagicDam(activeChar, target, this, ss, false) : Formulas.calcPhysDam(activeChar, target, this, false, false, ss > 0, false, false, false).damage;
                    target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
                    target.doCounterAttack(this, activeChar);
                    Formulas.calcLethalHit(activeChar, target, this);
                }

                getEffects(activeChar, target, false, false);

                target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));
            }
    }
}