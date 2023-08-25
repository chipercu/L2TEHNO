package ai;

import bosses.AntharasManager;
import javolution.util.FastMap;
import l2open.extensions.scripts.Functions;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Playable;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.List;

public class Antharas extends DefaultAI
{
	// debuffs
	final L2Skill s_fear = getSkill(4108, 1), s_fear2 = getSkill(5092, 1), s_curse = getSkill(4109, 1), s_paralyze = getSkill(4111, 1);
	// damage skills
	final L2Skill s_shock = getSkill(4106, 1), s_shock2 = getSkill(4107, 1), s_antharas_ordinary_attack = getSkill(4112, 1), s_antharas_ordinary_attack2 = getSkill(4113, 1), s_meteor = getSkill(5093, 1), s_breath = getSkill(4110, 1);
	// regen skills
	final L2Skill s_regen1 = getSkill(4239, 1), s_regen2 = getSkill(4240, 1), s_regen3 = getSkill(4241, 1);

	// Vars
	private int _hpStage = 0;
	private List<L2NpcInstance> minions = new ArrayList<L2NpcInstance>();

	private static int ultra_respawn_time = 3;
	public static int TID_ULTRALISK_COOLTIME = 1100;
	public static int TID_TARASQUE_COOLTIME = 1101;
	public static int TIME_TARASQUE_COOLTIME = 5;

	private L2Character myself = null;

	public Antharas(L2Character actor)
	{
		super(actor);
		myself = actor;
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		AntharasManager.setLastAttackTime();
		for(L2Playable p : AntharasManager.getZone().getInsidePlayers())
			if(p != null && !p.isDead())
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 1);
		super.ATTACKED(attacker, damage, skill);
	}
	
	@Override
	protected void onEvtArrived()
	{
		if(getActor().getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
		{
			for(L2Player p : AntharasManager.getZone().getInsidePlayers())
			{
				if(!p.isDead())
					notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
			}
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, AntharasManager.getRandomPlayer(), null);
		}
		super.onEvtArrived();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		if(timer_id == TID_ULTRALISK_COOLTIME)
		{
			if(myself.i_ai0 >= 4 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-4)
			{
				spawnMinions(29069);
				spawnMinions(29069);
				spawnMinions(29069);
				spawnMinions(29069);
			}
			else if(myself.i_ai0 >= 3 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-3)
			{
				spawnMinions(29069);
				spawnMinions(29069);
				spawnMinions(29069);
			}
			else if(myself.i_ai0 >= 2 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-2)
			{
				spawnMinions(29069);
				spawnMinions(29069);
			}
			else if(getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-1)
				spawnMinions(29069);
			if(Rnd.get(100) > 10 && myself.i_ai0 < 4)
				myself.i_ai0++;
			AddTimerEx(TID_ULTRALISK_COOLTIME,((ultra_respawn_time * 60) * 1000));
		}
		else if(timer_id == TID_TARASQUE_COOLTIME)
		{
			if(myself.i_ai0 >= 4 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-4)
			{
				spawnMinions(29190);
				spawnMinions(29190);
				spawnMinions(29190);
				spawnMinions(29190);
			}
			else if(myself.i_ai0 >= 3 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-3)
			{
				spawnMinions(29190);
				spawnMinions(29190);
				spawnMinions(29190);
			}
			else if(myself.i_ai0 >= 2 && getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-2)
			{
				spawnMinions(29190);
				spawnMinions(29190);
			}
			else if(getAliveMinionsCount() < ConfigValue.MaxMinionOfAntharas-1)
				spawnMinions(29190);
			AddTimerEx(TID_TARASQUE_COOLTIME,((TIME_TARASQUE_COOLTIME * 60) * 1000));
		}
	}

	private void spawnMinions(int npcId)
	{
		L2NpcInstance minion = Functions.spawn(myself.getLoc(), npcId);
		minion.c_ai0 = getActor();
		minions.add(minion);
		AntharasManager.addSpawnedMinion(minion);
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		L2Character target;
		if((target = prepareTarget()) == null || target.isDead())
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			return false;
		}

		L2NpcInstance actor = getActor();
		if(actor.isDead())
			return false;

		double distance = actor.getDistance(target);

		// Buffs and stats
		double chp = actor.getCurrentHpPercents();
		if(_hpStage == 0)
		{
			actor.altOnMagicUseTimer(actor, s_regen1);
			_hpStage = 1;
		}
		else if(chp < 75 && _hpStage == 1)
		{
			actor.altOnMagicUseTimer(actor, s_regen2);
			_hpStage = 2;
		}
		else if(chp < 50 && _hpStage == 2)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 3;
		}
		else if(chp < 30 && _hpStage == 3)
		{
			actor.altOnMagicUseTimer(actor, s_regen3);
			_hpStage = 4;
		}

		// Basic Attack
		if(Rnd.chance(50))
			return chooseTaskAndTargets(Rnd.chance(50) ? s_antharas_ordinary_attack : s_antharas_ordinary_attack2, target, distance);

		// Stage based skill attacks
		FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();
		switch(_hpStage)
		{
			case 1:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				break;
			case 2:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				break;
			case 3:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				addDesiredSkill(d_skill, target, distance, s_shock2);
				addDesiredSkill(d_skill, target, distance, s_breath);
				break;
			case 4:
				addDesiredSkill(d_skill, target, distance, s_curse);
				addDesiredSkill(d_skill, target, distance, s_paralyze);
				addDesiredSkill(d_skill, target, distance, s_meteor);
				addDesiredSkill(d_skill, target, distance, s_fear2);
				addDesiredSkill(d_skill, target, distance, s_shock2);
				addDesiredSkill(d_skill, target, distance, s_fear);
				addDesiredSkill(d_skill, target, distance, s_shock);
				addDesiredSkill(d_skill, target, distance, s_breath);
				break;
			default:
				break;
		}

		L2Skill r_skill = selectTopSkill(d_skill);
		if(r_skill != null && !r_skill.isOffensive())
			target = actor;

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	private int getAliveMinionsCount()
	{
		int i = 0;
		for(L2NpcInstance n : minions)
			if(n != null && !n.isDead())
				i++;
		return i;
	}

	private L2Skill getSkill(int id, int level)
	{
		return SkillTable.getInstance().getInfo(id, level);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		if(minions != null && !minions.isEmpty())
			for(L2NpcInstance n : minions)
				n.deleteMe();
		//mysql.set("UPDATE `bos_debug` SET `attacked`=0 where bos_id=29068");
		super.MY_DYING(killer);
	}
	
	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}
	
	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}