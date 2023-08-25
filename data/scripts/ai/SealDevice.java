package ai;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncTemplate;

/**
 * AI рейд босов SealDevice в 5 части эпик цепочки
 * @author DarkShadow74
 */
public class SealDevice extends DefaultAI
{
	private static final FuncTemplate ft1 = new FuncTemplate(null, "Set", Stats.REFLECT_DAMAGE_PERCENT, 0x10, 3);
	private static final FuncTemplate ft2 = new FuncTemplate(null, "Set", Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0x10, 3);
	private static final FuncTemplate ft3 = new FuncTemplate(null, "Set", Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0x10, 3);

	public SealDevice(L2Character actor)
	{
		super(actor);
		actor.addStatFunc(ft1.getFunc(this));
		actor.addStatFunc(ft2.getFunc(this));
		actor.addStatFunc(ft3.getFunc(this));
		AI_TASK_DELAY = 1000;
		AI_TASK_ACTIVE_DELAY = 1000;
	}

	public boolean isGlobalAI()
	{
		return true;
	}

	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	protected void onEvtAggression(L2Character target, int aggro)
	{}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}

}