package ai.PlainsOfLizardmen;

import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.NpcUtils;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для грибов в плейнс оф лизардмен.
 */

public class ai_tantaar_vegetation extends DefaultAI
{
	private L2Character myself = null;
	private int i_ai1 = 0;
	private int c_ai0 = 0;

	public ai_tantaar_vegetation(L2Character actor)
	{
		super(actor);
		myself = actor;
		myself.setIsInvul(true);
	}

	@Override
	protected void onEvtSpawn()
	{
		i_ai1 = 0;
		c_ai0 = 0;
		super.onEvtSpawn();
		if(getActor().getNpcId() == 18864)
		{
			if(myself != null)
			{
				for(int i = 0; i < 3; i++)
					NpcUtils.spawnSingle(22773, myself.getX() + Rnd.get(200), myself.getY() + Rnd.get(200), myself.getZ());
			}
		}
	}

	@Override
    protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(i_ai1 == 0 && IsNullCreature(attacker) == 0)
		{
			if(attacker.isPlayer() || attacker.isPet() || attacker.isSummon())
			{
				if(attacker.isPlayer())
				{
					c_ai0 = attacker.getObjectId();
				}
				else
				{
					try
					{
						c_ai0 = attacker.getPet().getPlayer().getObjectId();
					}
					catch(NullPointerException e)
					{}
				}
				i_ai1 = 1;
				if(getActor().getNpcId() == 18864)
				{
					BroadcastScriptEvent(78010087, getActor().getObjectId(), c_ai0, 800);
					AddTimerEx(78001,(4 * 1000));
				}
				else if(getActor().getNpcId() == 18865)
				{
					CreateOnePrivateEx("PlainsOfLizardmen.ai_tantaar_vegetation_buffer", "L2TerrainObject", "CharId", attacker.getObjectId(), "CaseId", 0, getActor().getX(), getActor().getY(), getActor().getZ(), 60);
					myself.setIsInvul(false);
					Suicide(myself);
				}
				else if(getActor().getNpcId() == 18868)
				{
					CreateOnePrivateEx("PlainsOfLizardmen.ai_tantaar_vegetation_buffer", "L2TerrainObject", "CharId", attacker.getObjectId(), "CaseId", 1, getActor().getX(), getActor().getY(), getActor().getZ(), 60);
					myself.setIsInvul(false);
					Suicide(myself);
				}
				else if(getActor().getNpcId() == 18867)
				{
					BroadcastScriptEvent(78010085,myself.getObjectId(),5000);
					myself.setIsInvul(false);
					Suicide(myself);
				}
			}
			else if(getActor().getNpcId() == 18867 && attacker.getNpcId() == 18863)
			{
				i_ai1 = 1;
				CreateOnePrivateEx("PlainsOfLizardmen.ai_tantaar_vegetation_buffer", "L2TerrainObject", "CharId", attacker.getObjectId(), "CaseId", 2, getActor().getX(), getActor().getY(), getActor().getZ(), 12);
				myself.setIsInvul(false);
				Suicide(myself);
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character caster, L2Character target)
	{
		myself.setIsInvul(false);
		Suicide(myself);
		super.onEvtFinishCasting(skill, caster,target);
	}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(!getActor().isDead() && script_event_arg1 == 78010080 && script_event_arg2 != 0 && getActor().getNpcId() == 18867)
		{
			L2Character c0 = L2ObjectsStorage.getCharacter(script_event_arg2);
			if(IsNullCreature(c0) == 0 && c0 != getActor())
			{
				SendScriptEvent(c0,78010080,getActor().getObjectId());
			}
		}
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 78001)
		{
			AddUseSkillDesire(myself,SkillTable.getInstance().getInfo(6427, 1), 1);
			for(L2Character cha : L2World.getAroundCharacters(getActor(), 800, 200))
				cha.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, SkillTable.getInstance().getInfo(6427, 1), myself);
			CreateOnePrivateEx("PlainsOfLizardmen.ai_tantaar_vegetation_buffer", "L2TerrainObject", "CharId", c_ai0, "CaseId", 4, getActor().getX(), getActor().getY(), getActor().getZ(), 60);
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		i_ai1 = 0;
		c_ai0 = 0;
		super.MY_DYING(killer);
	}

	public L2NpcInstance CreateOnePrivateEx(String ai_type, String instance, String ai_param_name, int ai_param_value, String ai_param_name2, int ai_param_value2, int x, int y, int z, int max_count)
	{
		int count=0;
		for(L2NpcInstance npc : L2World.getAroundNpc(getActor(), 1000, 100))
			if(npc != null && !npc.isDead())
				count++;
		if(count >= max_count)
			return null;
		StatsSet npcDat = null;

		L2NpcTemplate template = NpcTable.getTemplate(18918);
		if(template == null)
			return null;
		npcDat = template.getSet();
		npcDat.set("displayId", 18918);
		template.setSet(npcDat);
		L2NpcInstance character = null;
		template.ai_type = ai_type;
		template.setInstance(instance);

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLocx(x);
			sp.setLocy(y);
			sp.setLocz(z);
			sp.setAIParam(ai_param_name+"="+ai_param_value+";"+ai_param_name2+"="+ai_param_value2);
			sp.setRespawnDelay(0);
			character = sp.doSpawn(true);
			character.setNpcLeader(getActor());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}
}
