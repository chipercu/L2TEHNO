package l2open.gameserver.skills.pts_effects;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.skills.effects.EffectTemplate;

/**
 * {i_delete_hate;40}
 * @i_delete_hate
 * @40 - шанс срабатывания.
 **/
/**
 * @author : Diagod
 **/
public class i_delete_hate extends L2Effect
{
	public i_delete_hate(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc() && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			npc.clearAggroList(true);
			npc.getAI().clearTasks();
			npc.getAI().setGlobalAggro(System.currentTimeMillis() + 10000);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}