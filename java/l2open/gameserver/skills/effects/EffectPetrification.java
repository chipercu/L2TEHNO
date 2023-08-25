package l2open.gameserver.skills.effects;

import l2open.gameserver.model.L2Effect;
import l2open.gameserver.skills.Env;

public final class EffectPetrification extends L2Effect
{
	public EffectPetrification(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isParalyzeImmune())
			return false;
		if(_effected.isParalyzed() || _effected.isInvul())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.setParalyzedSkill(true);
		_effected.block_hp_mp(_obj_id, getSkill(), true);
		_effected.block_hp_mp(_obj_id, getSkill(), false);
		_effected.setPetrification(true);
		_effected.p_block_buff.getAndSet(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.setParalyzedSkill(false);
		_effected.unblock_hp_mp(_obj_id, getSkill(), true);
		_effected.unblock_hp_mp(_obj_id, getSkill(), false);
		_effected.setPetrification(false);
		_effected.p_block_buff.setAndGet(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}