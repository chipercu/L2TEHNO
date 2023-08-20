package ai.SeedOfAnnihilation;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Location;
import l2open.util.Rnd;

public class AnnihilationFighter extends Fighter
{
	private L2Character io1;

	public AnnihilationFighter(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		int i0 = 0;
		if(killer.isPlayer())
			if(killer.getPlayer().getParty() != null)
			{
				i0 = (30 + (10 * killer.getPlayer().getParty().getMemberCount()));
				io1 = killer.getPlayer().getParty().getRandomMember();
			}
			else
			{
				io1 = killer;
				i0 = 40;
			}

		if(Rnd.get(1000) < i0)
			spawnSingle(18839, getActor(), io1);

		super.MY_DYING(killer);
	}

	public void spawnSingle(int npcId, L2NpcInstance actor, L2Character killer)
	{
		L2Spawn spawn = null;
		StatsSet npcDat = null;
		L2NpcTemplate template = null;
		try
		{
			template = NpcTable.getTemplate(npcId);
			npcDat = template.getSet();
			npcDat.set("displayId", npcId);
			template.setSet(npcDat);

			spawn = new L2Spawn(template);
			spawn.setAmount(1);
			spawn.setRespawnDelay(0, 0);
			spawn.setAIParam("SPAWN="+killer.getName());
			Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
			spawn.setLoc(pos);
			spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean canSeeInSilentMove(L2Playable target)
	{
		return true;
	}

	@Override
	public boolean canSeeInInvis(L2Playable target)
	{
		return true;
	}
}
/**
package ai.pts;

import l2open.gameserver.ai.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.pts.*;
import l2open.util.*;


// null

public class ai_a_seed_elite_monster extends combat_monster
{
	public ai_a_seed_elite_monster(L2Character self)
	{
		super(self);
	}

	public int FieldCycle_ID = 0;
	public int FieldCycle_point = 0;
	public int max_desire = 10000000;

	@Override
	public void NO_DESIRE()
	{
		myself.AddMoveAroundDesire(5,5);
	}

	@Override
	public void PARTY_ATTACKED(L2Character attacker, NpcAi _private, int damage, int skill_name_id)
	{
		int i10;
		if(myself.i_ai0 == 0)
		{
			myself.i_ai0 = 1;
		}
		if(attacker.is_pc() == 1)
		{
			myself.AddHateInfo(attacker,1,0,1,1);
		}
		else if(attacker.is_pc() == 0 && myself.IsInCategory(12,attacker.class_id()) == 1)
		{
			if(attacker.getPlayer().alive() == 1)
			{
				if(babble_mode == 1)
				{
					myself.Say("???? ?????. ??? ?? ?? ?? ????? ????.");
				}
				myself.AddHateInfo(attacker,2,0,1,1);
				myself.AddHateInfo(attacker.getPlayer(),1,0,1,1);
				i10 = myself.GetHateInfoCount();
				if(babble_mode == 1)
				{
					myself.Say("??? ??? ?? ??? ? " + i10 + "? ???.");
				}
			}
			else if(attacker.getPlayer().alive() == 0)
			{
				myself.AddAttackDesire(attacker,1,100);
			}
		}
		super.PARTY_ATTACKED(attacker, _private, damage, skill_name_id);
	}

	@Override
	public void CREATED()
	{
		myself.c_ai0 = gg.GetNullCreature();
		myself.c_ai1 = gg.GetNullCreature();
		super.CREATED();
	}

	@Override
	public void ATTACKED(L2Character attacker, int damage, int skill_name_id, int skill_id)
	{
		int i0;
		if(FieldCycle_ID == 4)
		{
			i0 = gg.GetDBSavingMap(34);
			if(i0 == 2 || i0 == 3)
			{
				if(attacker.is_pc() == 1)
				{
					if(skill_name_id != 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 30)),0,1,1);
					}
					else if(skill_name_id == 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 10)),0,1,1);
					}
				}
			}
		}
		else if(FieldCycle_ID == 5)
		{
			i0 = gg.GetDBSavingMap(36);
			if(i0 == 2 || i0 == 3)
			{
				if(attacker.is_pc() == 1)
				{
					if(skill_name_id != 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 30)),0,1,1);
					}
					else if(skill_name_id == 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 10)),0,1,1);
					}
				}
			}
		}
		else if(FieldCycle_ID == 6)
		{
			i0 = gg.GetDBSavingMap(35);
			if(i0 == 2 || i0 == 3)
			{
				if(attacker.is_pc() == 1)
				{
					if(skill_name_id != 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 30)),0,1,1);
					}
					else if(skill_name_id == 0 && myself.IsInCategory(5,attacker.getActiveClassId()) == 1)
					{
						myself.AddHateInfo(attacker,gg.FloatToInt((damage * 10)),0,1,1);
					}
				}
			}
		}
		if(myself.IsNullCreature(myself.c_ai0) == 1)
		{
			if(myself.IsInCategory(2,attacker.getActiveClassId()) == 1)
			{
				myself.c_ai0 = attacker;
			}
		}
		else if(myself.IsNullCreature(myself.c_ai1) == 1)
		{
			if(myself.IsInCategory(2,attacker.getActiveClassId()) == 1)
			{
				myself.c_ai1 = attacker;
			}
		}
		super.ATTACKED(attacker, damage, skill_name_id, skill_id);
	}

	@Override
	public void MY_DYING(L2Character last_attacker, L2Party lparty)
	{
		Location pos0;
		L2Character c0;
		L2Party party0;
		L2Character c1;
		int i0;
		int i1;
		i0 = gg.GetStep_FieldCycle(FieldCycle_ID);
		i1 = FieldCycle_point;
		if(i0 == 1)
		{
			gg.AddPoint_FieldCycle(FieldCycle_ID,FieldCycle_point,1,myself.sm);
		}
		i0 = 0;
		if(myself.IsNullCreature(last_attacker) == 0)
		{
			if(last_attacker.is_pc() == 1)
			{
				c0 = last_attacker;
			}
			else if(myself.IsInCategory(12,last_attacker.class_id()) == 1)
			{
				c0 = last_attacker.getPlayer();
			}
			else
			{
				c0 = gg.GetNullCreature();
			}
			if(myself.IsNullCreature(c0) == 0)
			{
				party0 = gg.GetParty(c0);
				if(myself.IsNullParty(party0) == 0)
				{
					c1 = myself.GetMemberOfParty(party0,Rnd.get(party0.getMemberCount()));
					i0 = (10 + (10 * party0.getMemberCount()));
					if(gg.GetStep_FieldCycle(FieldCycle_ID) == 2)
					{
						i0 = (i0 * 2);
					}
				}
			}
			if(myself.DistFromMe(c0) < 2000 && myself.DistFromMe(c1) < 2000 && Rnd.get(1000) < i0)
			{
				pos0 = gg.GetRandomPosInCreature(c1,10,40);
				myself.CreateOnePrivateEx(1018839,"ai_marguene",0,0,pos0.getX(),pos0.getY(),pos0.getY(),0,0,0,gg.GetIndexFromCreature(c1));
			}
		}
		if(FieldCycle_ID == 4)
		{
			i0 = gg.GetDBSavingMap(34);
			if(i0 == 3)
			{
				if(myself.IsNullCreature(myself.c_ai0) == 0 && myself.IsNullCreature(myself.c_ai1) == 0)
				{
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) > 70)
					{
						myself.DropItem1(myself.sm,8604,1);
					}
				}
			}
		}
		else if(FieldCycle_ID == 5)
		{
			i0 = gg.GetDBSavingMap(36);
			if(i0 == 3)
			{
				if(myself.IsNullCreature(myself.c_ai0) == 0 && myself.IsNullCreature(myself.c_ai1) == 0)
				{
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) > 70)
					{
						myself.DropItem1(myself.sm,8604,1);
					}
				}
			}
		}
		else if(FieldCycle_ID == 6)
		{
			i0 = gg.GetDBSavingMap(35);
			if(i0 == 3)
			{
				if(myself.IsNullCreature(myself.c_ai0) == 0 && myself.IsNullCreature(myself.c_ai1) == 0)
				{
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) < 70)
					{
						myself.DropItem1(myself.sm,8603,1);
					}
					if(Rnd.get(100) > 70)
					{
						myself.DropItem1(myself.sm,8604,1);
					}
				}
			}
		}
		super.MY_DYING(last_attacker, lparty);
	}

	@Override
	public void USE_SKILL_FINISHED(L2Character target, int skill_name_id, int success)
	{
		int skill_id;
		int i1;
		int i2;
		int i3;
		int i4;
		int i5;
		super.USE_SKILL_FINISHED(target, skill_name_id, success);
	}

}

**/