package ai.ForgeOfTheGods;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;

/**
 * @author: Drizzy
 * @date: 19.08.2012
 * AI for lavazavr in the FoG - 100% PTS.
 */

public class wizard_bonus_forge extends Fighter
{
	private L2Character myself = null;
	private int Skill01_ID = 4607;
	private int TID_BONUS_TIME = 78001;
	private int TID_SKILL_COOLTIME = 78002;
	private L2Character c_ai1 = null;
	private long _lifeTime = 0;
	private int Aggressive_Time = 59 * 1000;

	public wizard_bonus_forge(L2Character actor)
	{
		super(actor);
		myself = actor;
		actor.p_block_move(true, null);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		_lifeTime = System.currentTimeMillis();
		AddTimerEx(TID_BONUS_TIME,( 60 * 1000 ));
		AddTimerEx(TID_SKILL_COOLTIME,( 3 * 1000 ));
		super.onEvtSpawn();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		c_ai1 = attacker;
		if(_lifeTime < System.currentTimeMillis() + Aggressive_Time)
		{
			return;
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		c_ai1 = attacker;
		if(_lifeTime < System.currentTimeMillis() + Aggressive_Time)
		{
			return;
		}
		super.onEvtClanAttacked(attacked_member, attacker, damage);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_BONUS_TIME)
		{
			Suicide(myself);
		}
		else if(timer_id == TID_SKILL_COOLTIME)
		{
			if(IsNullCreature(c_ai1) == 0 && _lifeTime >= System.currentTimeMillis() + Aggressive_Time )
			{
				AddUseSkillDesire(c_ai1, SkillTable.getInstance().getInfo(Skill01_ID,1), 1);
			}
			AddTimerEx(TID_SKILL_COOLTIME,( 3 * 1000 ));
		}
	}
}
