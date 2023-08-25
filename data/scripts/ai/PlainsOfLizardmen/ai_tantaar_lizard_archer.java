package ai.PlainsOfLizardmen;

import l2open.config.ConfigSystem;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для лучников в плейнс оф лизардмен.
 */

public class ai_tantaar_lizard_archer extends Fighter
{
	private L2Character myself = null;
	private int TID_SKILL_COOLTIME = 780001;
	private int TIME_SKILL_COOLTIME = 2;
	private L2Character c_ai0;

	public ai_tantaar_lizard_archer(L2Character actor)
	{
		super(actor);
		myself = actor;

	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		AddTimerEx(TID_SKILL_COOLTIME,( TIME_SKILL_COOLTIME * 1000 ));
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
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(6622,1),1);
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_SKILL_COOLTIME )
		{
			if(IsNullCreature(c_ai0) == 0 )
			{
				if(c_ai0.getEffectList().getEffectsBySkillId(101) == null && Rnd.get(100) >= 85)
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(6423,1),1);
				}
				else
				{
					AddUseSkillDesire(c_ai0,SkillTable.getInstance().getInfo(6424,1),1);
				}
			}
			AddTimerEx(TID_SKILL_COOLTIME,( TIME_SKILL_COOLTIME * 1000 ));
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(IsNullCreature(c_ai0) == 0)
		{
			CreateOnePrivateEx(18919,"PlainsOfLizardmen.ai_auragrafter","L2TerrainObject", "ID", c_ai0.getObjectId(), getActor().getX(),getActor().getY(),getActor().getZ(),0);
		}
		if((Rnd.get(1000/(ConfigSystem.getQuestDropRates(423) != 0 ? ConfigSystem.getQuestDropRates(423) : 1)) == 0) && getActor().getNpcId() != 18862 && IsNullCreature(c_ai0) == 0)
		{
			CreateOnePrivateEx(18862,"PlainsOfLizardmen.ai_tantaar_lizard_warrior","L2Monster", "ID", c_ai0.getObjectId(),getActor().getX(),getActor().getY(),getActor().getZ(), 0);
		}
		c_ai0 = null;
		super.MY_DYING(killer);
	}
}
