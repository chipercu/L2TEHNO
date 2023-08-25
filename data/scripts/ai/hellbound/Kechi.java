package ai.hellbound;

import java.util.HashMap;

import javolution.util.FastMap;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author Diamond
 * 25714(25532)-B
 */
public class Kechi extends DefaultAI
{
	final L2Skill KechiDoubleCutter; // Attack by crossing the sword. Power 2957.
	final L2Skill KechiAirBlade; // Strikes the enemy a blow in a distance using sword energy. Critical enabled. Power 1812

	final L2Skill Invincible; // Invincible against general attack and skill, buff/de-buff.
	final L2Skill NPCparty60ClanHeal; // TODO

	private static final int GUARD1 = 22309;
	private static final int GUARD2 = 22310;
	private static final int GUARD3 = 22417;

	private static final Location guard_spawn_loc = new Location(153384, 149528, -12136);

	private static final int[][] guard_run = new int[][] { { GUARD1, 153384, 149528, -12136 },
			{ GUARD1, 153975, 149823, -12152 }, { GUARD1, 154364, 149665, -12151 }, { GUARD1, 153786, 149367, -12151 },
			{ GUARD2, 154188, 149825, -12152 }, { GUARD2, 153945, 149224, -12151 }, { GUARD3, 154374, 149399, -12152 },
			{ GUARD3, 153796, 149646, -12159 } };

	private static String[] chat = new String[] { "Стража, убейте их!", "Стража!", "Стража, на помощь!", "Добейте их.",
			"Вы все умрете!" };

	private int stage = 0;

	public Kechi(L2Character actor)
	{
		super(actor);

		HashMap<Integer, L2Skill> skills = getActor().getTemplate().getSkills();

		KechiDoubleCutter = skills.get(733);
		KechiAirBlade = skills.get(734);

		Invincible = skills.get(5418);
		NPCparty60ClanHeal = skills.get(5439);
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

		double actor_hp_precent = actor.getCurrentHpPercents();

		switch(stage)
		{
			case 0:
				if(actor_hp_precent < 80)
				{
					spawnMobs();
					return true;
				}
				break;
			case 1:
				if(actor_hp_precent < 60)
				{
					spawnMobs();
					return true;
				}
				break;
			case 2:
				if(actor_hp_precent < 40)
				{
					spawnMobs();
					return true;
				}
				break;
			case 3:
				if(actor_hp_precent < 30)
				{
					spawnMobs();
					return true;
				}
				break;
			case 4:
				if(actor_hp_precent < 20)
				{
					spawnMobs();
					return true;
				}
				break;
			case 5:
				if(actor_hp_precent < 10)
				{
					spawnMobs();
					return true;
				}
				break;
			case 6:
				if(actor_hp_precent < 5)
				{
					spawnMobs();
					return true;
				}
				break;
		}

		int rnd_per = Rnd.get(100);

		if(rnd_per < 5)
		{
			addTaskBuff(actor, Invincible);
			return true;
		}

		double distance = actor.getDistance(target);

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, KechiDoubleCutter);
		addDesiredSkill(d_skill, target, distance, KechiAirBlade);

		L2Skill r_skill = selectTopSkill(d_skill);

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private void spawnMobs()
	{
		stage++;

		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		Functions.npcSay(actor, chat[Rnd.get(chat.length)]);

		for(int[] run : guard_run)
			try
			{
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(run[0]));
				sp.setLoc(guard_spawn_loc);
				sp.setReflection(actor.getReflection().getId());
				L2NpcInstance guard = sp.doSpawn(true);

				Location runLoc = new Location(run[1], run[2], run[3]);

				guard.setRunning();
				guard.getAI().addTaskMove(runLoc, true);
				guard.getAI().setGlobalAggro(0);

				// Выбираем случайную цель
				L2Character hated = actor.getRandomHated();
				if(hated != null)
					// Делаем необходимые приготовления, для атаки в конце движения
					hated.addDamageHate(guard, 0, Rnd.get(1, 100)); // Это нужно, чтобы гвард не перестал атаковать цель после первых ударов
				guard.setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis()); // Это нужно, чтобы не сработал таймаут
				guard.getAI().setAttackTarget(hated); // На всякий случай, не обязательно делать
				guard.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, hated, null); // Переводим в состояние атаки
				guard.getAI().addTaskAttack(hated); // Добавляем отложенное задание атаки, сработает в самом конце движения
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
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
		//myself.InstantZone_MarkRestriction();
		//myself.CreateOnePrivateEx(1032277,"ai_telecube_oracle_raid",0,0,gg.FloatToInt(myself.sm.getX()),gg.FloatToInt(myself.sm.getY()),gg.FloatToInt(myself.sm.getZ()),0,0,0,0);
		if(party0 != null)
		{
			for(L2Player c0 : party0.getPartyMembers())
			{
				if(c0 != null)
				{
					if(c0.getReflectionId() == myself.getReflectionId())
					{
						if(myself.OwnItemCount(c0,9690) >= 1)
						{
							myself.GiveItem1(c0,9696,1);
							myself.GiveItem1(c0,9597,1);
						}
						else
						{
							//myself.InstantZone_Leave(c0);
						}
					}
				}
			}
		}
		super.MY_DYING(last_attacker);
	}
}