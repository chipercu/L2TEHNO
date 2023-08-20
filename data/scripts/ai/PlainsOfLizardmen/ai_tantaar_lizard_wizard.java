package ai.PlainsOfLizardmen;

import l2open.config.ConfigSystem;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Mystic;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * Общий аи для всех магов в плейнс оф лизардмен.
 */

public class ai_tantaar_lizard_wizard extends Mystic
{
	private L2Character myself = null;
	private int TID_ATTRACT_TO_FUNGUS_KILLA = 780002;
	public L2Character c_ai0;
	private L2Character c_ai1;
	private L2Character c0;

	public ai_tantaar_lizard_wizard(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			c_ai0 = attacker;
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		if(skill.getId() == 6427)
		{
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6622, 1), 1);
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(getActor().getNpcId() == 22773)
		{
			if(IsNullCreature(L2ObjectsStorage.getCharacter(script_event_arg3)) == 0)
			{
				c_ai1 = L2ObjectsStorage.getCharacter(script_event_arg3);
			}
			AddTimerEx(TID_ATTRACT_TO_FUNGUS_KILLA,(7 * 1000));
			if(script_event_arg1 == 78010087)
			{
				c0 = L2ObjectsStorage.getCharacter(script_event_arg2);
				if(IsNullCreature(c0) == 0)
				{
					getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					getActor().clearAggroList(false);
					getActor().setRunning();
					AddMoveToDesire((c0.getX() + Rnd.get(25)) - Rnd.get(25), ((c0.getY() + Rnd.get(25)) - Rnd.get(25)), ((c0.getZ() + Rnd.get(25)) - Rnd.get(25)), 1000000);
				}
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_ATTRACT_TO_FUNGUS_KILLA)
		{
			if(IsNullCreature(c_ai1) == 0)
			{
				getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, c_ai1, 10);
				getActor().getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, c_ai1);
			}
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(IsNullCreature(c_ai0) == 0)
		{
			CreateOnePrivateEx(18919,"PlainsOfLizardmen.ai_auragrafter","L2TerrainObject", "ID", c_ai0.getObjectId(), getActor().getX(),getActor().getY(),getActor().getZ(),0);
			if((Rnd.get(1000 / (ConfigSystem.getQuestDropRates(423) != 0 ? ConfigSystem.getQuestDropRates(423) : 1)) == 0) && getActor().getNpcId() != 18862)
				CreateOnePrivateEx(18862,"PlainsOfLizardmen.ai_tantaar_lizard_warrior","L2Monster", "ID", c_ai0.getObjectId(),getActor().getX(),getActor().getY(),getActor().getZ(), 0);
		}
		super.MY_DYING(killer);
	}
}
