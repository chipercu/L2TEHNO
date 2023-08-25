package ai.SelMahumTrainingGrounds;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.model.*;

/**
 * @author: Drizzy
 * @date: 25.10.2013
 * open-team.ru
 * АИ для 2-го костра в селмахум по ПТС.
 **/

public class ai_xel_campfire_dummy extends DefaultAI
{
	private L2Character myself = null;
	public ai_xel_campfire_dummy(L2Character self)
	{
		super(self);
		myself = self;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		SendScriptEvent(getActor().getMyLeader(),2219022,GetIndexFromCreature(myself));
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character speller)
	{
		if(skill.getId() == 9075)
		{
			myself.doCast(SkillTable.getInstance().getInfo(6688, 1), myself, true);
			//AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(6688,1), 1000000);
			BroadcastScriptEvent(2219024,GetIndexFromCreature(speller),600);
		}
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 2219022)
		{
			Suicide(myself);
		}
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}
}
