package ai;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.NpcTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author Diamond
 * 25716(25717/25534/25535)-C
 */
public class Tears extends DefaultAI
{
	private class DeSpawnTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			for(L2NpcInstance npc : spawns)
				if(npc != null)
					npc.deleteMe();
			spawns.clear();
			despawnTask = null;
		}
	}

	private class SpawnMobsTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			spawnMobs();
			spawnTask = null;
		}
	}

	final L2Skill Invincible;
	final L2Skill Freezing;

	private static final int Water_Dragon_Scale = 2369;
	private static final int Tears_Copy = 25535;

	ScheduledFuture<?> spawnTask;
	ScheduledFuture<?> despawnTask;

	GArray<L2NpcInstance> spawns = new GArray<L2NpcInstance>();

	private boolean _isUsedInvincible = false;

	private int _scale_count = 0;
	private long _last_scale_time = 0;

	public Tears(L2Character actor)
	{
		super(actor);

		HashMap<Integer, L2Skill> skills = getActor().getTemplate().getSkills();

		Invincible = skills.get(5420);
		Freezing = skills.get(5238);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead() || skill == null || caster == null)
			return;

		if(System.currentTimeMillis() - _last_scale_time > 5000)
			_scale_count = 0;

		if(skill.getId() == Water_Dragon_Scale)
		{
			_scale_count++;
			_last_scale_time = System.currentTimeMillis();
		}

		L2Player player = caster.getPlayer();
		if(player == null)
			return;

		int count = 1;
		L2Party party = player.getParty();
		if(party != null)
			count = party.getMemberCount();

		// Снимаем неуязвимость
		if(_scale_count >= count)
		{
			_scale_count = 0;
			actor.getEffectList().stopEffect(Invincible);
		}
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		L2Character target;
		if((target = prepareTarget()) == null)
			return false;

		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return false;

		double distance = actor.getDistance(target);
		double actor_hp_precent = actor.getCurrentHpPercents();
		int rnd_per = Rnd.get(100);

		if(actor_hp_precent < 15 && !_isUsedInvincible)
		{
			_isUsedInvincible = true;
			addTaskBuff(actor, Invincible);
			Functions.npcSay(actor, "Готовьтесь к смерти!!!");
			return true;
		}

		if(rnd_per < 5 && spawnTask == null && despawnTask == null)
		{
			actor.broadcastSkill(new MagicSkillUse(actor, actor, 5441, 1, 3000, 0));
			spawnTask = ThreadPoolManager.getInstance().schedule(new SpawnMobsTask(), 3000);
			return true;
		}

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		return chooseTaskAndTargets(Freezing, target, distance);
	}

	private void spawnMobs()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		Location pos;
		L2Character hated;

		// Спавним 9 копий
		for(int i = 0; i < 9; i++)
			try
			{
				pos = GeoEngine.findPointToStay(144298, 154420, -11854, 300, 320, actor.getReflection().getGeoIndex());
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(Tears_Copy));
				sp.setLoc(pos);
				sp.setReflection(actor.getReflection().getId());
				L2NpcInstance copy = sp.doSpawn(true);
				spawns.add(copy);

				// Атакуем случайную цель
				hated = actor.getRandomHated();
				if(hated != null)
					copy.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		// Прячемся среди них
		pos = GeoEngine.findPointToStay(144298, 154420, -11854, 300, 320, actor.getReflection().getGeoIndex());
		actor.teleToLocation(pos);

		// Атакуем случайную цель
		hated = actor.getRandomHated();
		if(hated != null)
			actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, Rnd.get(1, 100));

		if(despawnTask != null)
			despawnTask.cancel(false);
		despawnTask = ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 30000);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public void MY_DYING(L2Character last_attacker)
	{
		L2NpcInstance myself = getActor();
		L2Party party0 = last_attacker.getPlayer().getParty();
		//myself.CreateOnePrivateEx(1032278,"ai_telecube_oracle_raid",0,0,myself.getX(),myself.getY(),myself.getZ(),0,0,0,0);
		if(party0 != null)
		{
			for(L2Player c0 : party0.getPartyMembers())
			{
				if(c0 != null)
				{
					if(c0.getReflectionId() == myself.getReflectionId())
					{
						myself.GiveItem1(c0,9697,1);
						myself.GiveItem1(c0,9598,1);
					}
				}
			}
		}
		//myself.InstantZone_MarkRestriction();
		super.MY_DYING(last_attacker);
	}
}