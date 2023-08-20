package ai.PlainsOfLizardmen;

import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * АИ для грибов в плейнс оф лизардмен.
 */
public class ai_tantaar_vegetation_buffer extends DefaultAI
{
	private L2Character myself = null;
	private int TID_DESPAWN = 78001;
	private int TIME_DESPAWN = 5;
	private L2Character c_ai0;
	public int CharId = 0;
	public int CaseId = 0;

	public ai_tantaar_vegetation_buffer(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		if(CharId != 0)
		{
			c_ai0 = L2ObjectsStorage.getCharacter(CharId);
			if(IsNullCreature(c_ai0) == 0)
			{
				switch(CaseId)
				{
					case 0:
						getActor().doCast(SkillTable.getInstance().getInfo(6428,1), c_ai0, true);
						break;
					case 1:
						getActor().doCast(SkillTable.getInstance().getInfo(6430,1), c_ai0, true);
						break;
					case 2:
						getActor().doCast(SkillTable.getInstance().getInfo(6648,1), c_ai0, true);
						break;
					case 3:
						getActor().doCast(SkillTable.getInstance().getInfo(6429,1), c_ai0, true);
						break;
				}
			}
		}
		AddTimerEx(TID_DESPAWN,( TIME_DESPAWN * 1000 ));
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		Suicide(myself);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_DESPAWN)
		{
			Suicide(myself);
		}
	}
}
