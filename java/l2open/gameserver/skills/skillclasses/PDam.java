package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.FinishRotating;
import l2open.gameserver.serverpackets.StartRotating;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.Formulas.AttackInfo;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class PDam extends L2Skill
{
	public final boolean _onCrit;
	public final boolean _directHp;
	public final boolean _turner;
	public final boolean _blow;

	public PDam(StatsSet set)
	{
		super(set);
		_onCrit = set.getBool("onCrit", false);
		_directHp = set.getBool("directHp", false);
		_turner = set.getBool("turner", false);
		_blow = set.getBool("blow", false);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();

		for(L2Character target : targets)
			if(target != null && !target.isDead())
			{
				if(target.p_ignore_skill_freya && (getId() == 6274 || getId() == 6275 || getId() == 6662))
					continue;
				else if(getTraitType().fullResist(target))
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(_displayId, _displayLevel));
					continue;
				}
				else if(_turner && !target.isInvul() && !target.block_hp.get())
				{
					target.broadcastPacket2(new StartRotating(target, target.getHeading(), 1, 65535));
					target.broadcastPacket2(new FinishRotating(target, activeChar.getHeading(), 65535));
					target.setHeading(activeChar.getHeading());
					target.sendPacket(new SystemMessage(SystemMessage.S1_S2S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));
				}

				boolean reflected = target.checkReflectSkill(activeChar, this);
				if(reflected)
					target = activeChar;

				AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, _blow, ss, _onCrit, false, false);

				// 405:300+ сначала летал, потом дамаг
				// 450:300+ сначала летал, потом дамаг
				if(getId() == 405 || getId() == 450)
				{
					Formulas.calcLethalHit(activeChar, target, this);
					if(!info.miss || info.damage >= 1)
						target.reduceCurrentHp(info.damage, activeChar, this, true, true, _directHp, true, false, info.damage, true, false, info.crit, false);
				}
				else
				{
					if(!info.miss || info.damage >= 1)
						target.reduceCurrentHp(info.damage, activeChar, this, true, true, _directHp, true, false, info.damage, true, false, info.crit, false);
					Formulas.calcLethalHit(activeChar, target, this);
				}

				if(!reflected)
					target.doCounterAttack(this, activeChar);

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSuicideAttack())
		{
			activeChar.doDie(null);
			activeChar.onDecay();
		}
		else if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}