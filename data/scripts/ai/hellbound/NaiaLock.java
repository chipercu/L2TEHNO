package ai.hellbound;

import l2open.config.ConfigValue;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.taskmanager.SpawnTaskManager;
import l2open.util.Location;
import l2open.util.GArray;
import l2open.extensions.scripts.Functions;

public class NaiaLock extends Fighter
{
	private static GArray<L2NpcInstance> _spawns = new GArray<L2NpcInstance>();

	public NaiaLock(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
			if(key.getNpcId() == 18492)
			{
				key.doCast(SkillTable.getInstance().getInfo(5527, 1), key.c_ai0, true);
				key.i_ai0 = 0;
				break;
			}

		// Респ 5 минут.
		getActor().onDecay();
		de_spawn_npc();
		SpawnTaskManager.getInstance().addSpawnTask(getActor(), 5*60*1000L);

		super.MY_DYING(killer);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		getActor().i_ai0 = 0;
		getActor().i_ai1 = 0;
		getActor().i_ai2 = 0;
		getActor().i_ai3 = 0;
		getActor().i_ai4 = 0;
		for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
			if(key.getNpcId() == 18492)
			{
				key.i_ai0 = 1;
				break;
			}

		spawn_npc();
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		if(getActor().i_ai4 == 0)
		{
			AddTimerEx(78001,((20 * 60) * 1000)); // деспаун через 20 минут, после первого удара, спаун через 3 минуты после.
			getActor().i_ai4 = 1;
		}
		L2NpcInstance myself = getActor();

		boolean can_say=false;
		if(myself.i_ai0 == 0)
		{
			if(attacker != null)
				for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
					if(key.getNpcId() == 18492)
					{
						key.c_ai0 = attacker;
						break;
					}
			myself.i_ai0 = 1;
		}

		if(myself.i_ai3 == 0 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < myself.getMaxHp())
		{
			can_say = true;
			myself.i_ai3 = 1;
		}
		else if(myself.i_ai3 == 1 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 9))
		{
			can_say = true;
			myself.i_ai3 = 2;
		}
		else if(myself.i_ai3 == 2 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 8))
		{
			can_say = true;
			myself.i_ai3 = 3;
		}
		else if(myself.i_ai3 == 3 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 7))
		{
			can_say = true;
			myself.i_ai3 = 4;
		}
		else if(myself.i_ai3 == 4 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 6))
		{
			can_say = true;
			myself.i_ai3 = 5;
		}
		else if(myself.i_ai3 == 5 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 5))
		{
			spawn_npc();
			can_say = true;
			myself.i_ai3 = 6;
		}
		else if(myself.i_ai3 == 6 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 4))
		{
			can_say = true;
			myself.i_ai3 = 7;
		}
		else if(myself.i_ai3 == 7 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 3))
		{
			can_say = true;
			myself.i_ai3 = 8;
		}
		else if(myself.i_ai3 == 8 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 2))
		{
			can_say = true;
			myself.i_ai3 = 9;
		}
		else if(myself.i_ai3 == 9 && myself.getCurrentHp() > 0 && myself.getCurrentHp() < ((myself.getMaxHp() / 10) * 1))
		{
			spawn_npc();
			spawn_npc();
			can_say = true;
			myself.i_ai3 = 10;
		}
		if(can_say)
			for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
				if(key.getNpcId() == 18492)
				{
					key.Say(null, key, 1800197);
					break;
				}
	}

	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{
		if(script_event_arg1 == 78010026)
		{
			L2NpcInstance myself = getActor();
			if(myself.getCurrentHp() > 0 && myself.getCurrentHp() <= (myself.getMaxHp() / 10))
			{
				for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
					if(key.getNpcId() == 18492)
					{
						key.ShowPage(key.c_ai0.getPlayer(),"naiazma_key005.htm");
						break;
					}
				// Респ 20 минут.
				myself.onDecay();
				de_spawn_npc();
				SpawnTaskManager.getInstance().addSpawnTask(myself, 20*60*1000L);
			}
			else
			{
				for(L2NpcInstance key : L2World.getAroundNpc(getActor(), 300, 50))
					if(key.getNpcId() == 18492)
					{
						key.doCast(SkillTable.getInstance().getInfo(5527, 1), key.c_ai0, true);
						key.i_ai0 = 0;
						break;
					}
				// Респ 5 минут.
				if(ConfigValue.NaiaLockCanFail)
				{
					myself.onDecay();
					de_spawn_npc();
					SpawnTaskManager.getInstance().addSpawnTask(myself, 5*60*1000L);
				}
			}
		}
	}

	private void de_spawn_npc()
	{
		for(L2NpcInstance npc : _spawns)
			if(npc != null && !npc.isDead())
				npc.deleteMe();
	}

	private void spawn_npc()
	{
		try
		{
			L2Spawn sp = new L2Spawn(NpcTable.getTemplate(18493));
			sp.setLoc(Location.findPointToStay(getActor(), 150, 250));
			sp.setReflection(getActor().getReflectionId());
			_spawns.add(sp.doSpawn(true));
			sp.stopRespawn();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == 78001)
		{
			getActor().onDecay();
			de_spawn_npc();
			SpawnTaskManager.getInstance().addSpawnTask(getActor(), 3*1000L);

			getActor().i_ai4 = 0;
		}
	}
}