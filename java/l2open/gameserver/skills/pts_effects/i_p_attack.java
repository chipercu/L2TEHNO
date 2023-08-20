package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
 /**
 * {i_npc_kill;80}
 * @i_npc_kill
 * @80 - шанс.
 **/
public class i_p_attack extends L2Effect
{
	public i_p_attack(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		/*if(_effected != null && !_effected.isDead())
		{
			if(_effected.p_ignore_skill_freya && (getId() == 6274 || getId() == 6275 || getId() == 6662))
				continue;
			else if(getTraitType().fullResist(_effected))
			{
				getEffector().sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(_effected).addSkillName(_displayId, _displayLevel));
				continue;
			}
			else if(_turner && !_effected.isInvul() && !_effected.block_hp.get())
			{
				_effected.broadcastPacket2(new StartRotating(_effected, _effected.getHeading(), 1, 65535));
				_effected.broadcastPacket2(new FinishRotating(_effected, getEffector().getHeading(), 65535));
				_effected.setHeading(getEffector().getHeading());
				_effected.sendPacket(new SystemMessage(SystemMessage.S1_S2S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));
			}
			boolean reflected = _effected.checkReflectSkill(getEffector(), getSkill());
			if(reflected)
				_effected = getEffector();

			AttackInfo info = Formulas.calcPhysDam(getEffector(), _effected, getSkill(), false, _blow, ss, _onCrit, false, false);

			// 405:300+ сначала летал, потом дамаг
			// 450:300+ сначала летал, потом дамаг
			if(getId() == 405 || getId() == 450)
			{
				Formulas.calcLethalHit(getEffector(), _effected, getSkill());
				if(!info.miss || info.damage >= 1)
					_effected.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, _directHp, true, false, info.damage, true, false, info.crit, false);
			}
			else
			{
				if(!info.miss || info.damage >= 1)
					_effected.reduceCurrentHp(info.damage, getEffector(), getSkill(), true, true, _directHp, true, false, info.damage, true, false, info.crit, false);
				Formulas.calcLethalHit(getEffector(), _effected, getSkill());
			}

			if(!reflected)
				_effected.doCounterAttack(getSkill(), getEffector());
		}*/
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
