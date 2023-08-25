package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Rnd;

/**
 * @author Diagod
 **/
public class SearchThing extends Fighter
{
	public SearchThing(L2Character actor)
	{
		super(actor);
		((L2NpcInstance)actor).setHideName(true);
		AddTimerEx(1000,60000);
		AddTimerEx(1001,600000);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance npc = getActor();
		if(npc == null || skill == null || npc.isDead())
			return;
		if(skill.getId() == 629)
		{
			if(npc != null)
				npc.doDie(caster);

			Functions.spawn(caster.getX(), caster.getY() - 130, caster.getZ(), 13098);
			Functions.spawn(caster.getX() - 150, caster.getY() + 120, caster.getZ(), 13098);
			Functions.spawn(caster.getX() + 150, caster.getY() + 120, caster.getZ(), 13098);
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		L2NpcInstance npc = getActor();
		if(timer_id == 1000)
		{
			if(Rnd.get(3) < 1)
			{
				if(Rnd.get(2) < 1)
					L2NpcInstance.Say(null, npc, 1600020);
				else
					L2NpcInstance.Say(null, npc, 1600021);
			}
			AddTimerEx(1000,60000);
		}
		else if(timer_id == 1001)
			if(npc != null)
				npc.doDie(null);
	}
}