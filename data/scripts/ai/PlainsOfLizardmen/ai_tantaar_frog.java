package ai.PlainsOfLizardmen;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Rnd;

/**
 * @author: Drizzy
 * АИ для фрога :D
 */

public class ai_tantaar_frog extends Fighter
{
	private L2Character myself = null;
	private L2Character c_ai0;
	private int i_ai1 = 0;

	public ai_tantaar_frog(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		i_ai1 = 0;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(IsNullCreature(attacker) == 0)
		{
			c_ai0 = attacker;
		}
		if(i_ai1 == 0)
		{
			if(IsNullCreature(c_ai0) == 0 && (attacker.isPlayer() || attacker.isPet() || attacker.isSummon()))
			{
				CreateOnePrivateEx("PlainsOfLizardmen.ai_tantaar_vegetation_buffer", "L2TerrainObject", "CharId", attacker.getObjectId(), "CaseId", 3, getActor().getX(), getActor().getY(), getActor().getZ());
			}
			i_ai1 = 1;
			Suicide(myself);
		}
		super.ATTACKED(attacker, damage, skill);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		if(skill.getId() == 6427)
		{
			AddUseSkillDesire(myself, SkillTable.getInstance().getInfo(6622,1),1);
		}
		super.onEvtSeeSpell(skill, caster);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		i_ai1 = 0;
		super.MY_DYING(killer);
	}

	public L2NpcInstance CreateOnePrivateEx(String ai_type, String instance, String ai_param_name, int ai_param_value, String ai_param_name2, int ai_param_value2, int x, int y, int z)
	{
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
			sp.setRespawnDelay(0);
			sp.setAIParam(ai_param_name+"="+ai_param_value+";"+ai_param_name2+"="+ai_param_value2);
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
