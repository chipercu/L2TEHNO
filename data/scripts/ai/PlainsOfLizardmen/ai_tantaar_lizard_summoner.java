package ai.PlainsOfLizardmen;

import l2open.config.ConfigSystem;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для самонеров в плейнс оф лизардмен
 */

public class ai_tantaar_lizard_summoner extends ai_tantaar_lizard_wizard
{
	private int Self_Debuff = 6425;
	private int i_ai3 = 0;
	private L2Character c_ai0 = null;

	public ai_tantaar_lizard_summoner(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		i_ai3 = 0;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			c_ai0 = attacker;
		}
		if(getActor().getCurrentHp() - damage <= (getActor().getMaxHp() * 0.600000) && i_ai3 == 0 && IsNullCreature(attacker) == 0)
		{
			i_ai3 = 1;
			AddUseSkillDesire(getActor(), SkillTable.getInstance().getInfo(Self_Debuff,1),1);
			if(c_ai0 != null)
			{
				L2NpcInstance npc = CreateOnePrivateEx(22768,"PlainsOfLizardmen.ai_tantaar_lizard_warrior","L2Monster", "ID", c_ai0.getObjectId(), getActor().getX(),getActor().getY(),getActor().getZ(),0);
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10);
				npc.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
				L2NpcInstance npc1 = CreateOnePrivateEx(22768,"PlainsOfLizardmen.ai_tantaar_lizard_warrior","L2Monster", "ID", c_ai0.getObjectId(), getActor().getX(),getActor().getY(),getActor().getZ(),0);
				npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10);
				npc1.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		i_ai3 = 0;
		if(IsNullCreature(c_ai0) == 0)
		{
			CreateOnePrivateEx(18919,"PlainsOfLizardmen.ai_auragrafter","L2TerrainObject", "ID", c_ai0.getObjectId(), getActor().getX(),getActor().getY(),getActor().getZ(),0);
		}
		if((Rnd.get(1000 / (ConfigSystem.getQuestDropRates(423) != 0 ? ConfigSystem.getQuestDropRates(423) : 1)) == 0) && getActor().getNpcId() != 18862)
		{
			CreateOnePrivateEx(18862,"PlainsOfLizardmen.ai_tantaar_lizard_warrior","L2Monster", "ID", c_ai0.getObjectId(),getActor().getX(),getActor().getY(),getActor().getZ(), 0);
		}
		super.MY_DYING(killer);
	}
}
