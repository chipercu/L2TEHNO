package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * АИ для тотемов таркхана. Сделано с птс скриптов.
 */
public class ai_totem_of_cocracon_torumba extends DefaultAI
{
	private L2Character myself = null;
	private int display_skill = 6374;
	private int weak_skill = 6370;
	private int DISPLAY_TIMER = 5507;
	private int DESPAWN_TIMER = 5509;
	private L2Character c_ai4 = null;

	public ai_totem_of_cocracon_torumba(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		AddTimerEx(DISPLAY_TIMER,( 15 * 1000 ));
		AddTimerEx(DESPAWN_TIMER,( 23 * 1000 ));
	}


	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == DISPLAY_TIMER)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(display_skill,1),1);
		}
		else if(timer_id == DESPAWN_TIMER)
		{
			getActor().doDie(getActor());
		}
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		if(skill.getId() == display_skill)
		{
			if(IsNullCreature(c_ai4) == 0)
			{
				AddUseSkillDesire(c_ai4,SkillTable.getInstance().getInfo(weak_skill,1),1);
			}
		}
		super.onEvtFinishCasting(skill, caster,target);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 8)
		{
			c_ai4 = L2ObjectsStorage.getNpc(script_event_arg2);
		}
	}
}
