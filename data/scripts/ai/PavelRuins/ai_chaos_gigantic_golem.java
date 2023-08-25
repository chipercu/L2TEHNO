package ai.PavelRuins;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * @date: 16.08.2012
 * @AI для рб в Pavel Ruins (100% PTS).
 */
public class ai_chaos_gigantic_golem extends Fighter
{
	private L2Character myself = null;
	private long i_ai7 = 0;
	private int i_ai8 = 0;

	public ai_chaos_gigantic_golem(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		i_ai7 = System.currentTimeMillis();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(i_ai8 == 0)
		{
			AddTimerEx(7801,30000);
			i_ai8 = 1;
			i_ai7 = System.currentTimeMillis();
			if((((getActor().getCurrentHp() - damage) * 100) / getActor().getMaxHp()) > 70)
			{
				CreatePrivates(25705, 25705, 25705);
			}
		}
		if((((getActor().getCurrentHp() - damage) * 100) / getActor().getMaxHp()) < 30 && Rnd.get(100) < 5)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(6263,1),1);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 7801)
		{
			if((i_ai7 + (3 * 60 * 1000) ) <= System.currentTimeMillis())
			{
				i_ai8 = 0;
				BroadcastScriptEvent(10029,0,6000);
			}
			else
			{
				AddTimerEx(7801,30000);
			}
		}
	}

}
