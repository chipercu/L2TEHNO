package l2open.gameserver.ai;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Character.HateInfo;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.SevenSigns;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.taskmanager.AiTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.templates.StatsSet;
import l2open.util.*;
import l2open.util.reference.*;


import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class DefaultAI extends L2CharacterAI
{
	protected static Logger _log = Logger.getLogger(DefaultAI.class.getName());
	private int DELAY = 2;//ConfigValue.UseSkillDelay;
	private long TIMER = System.currentTimeMillis();

	/**************** AI Params ****************/
	public int ParalizeOnAttack = -1000;
	public int MaxPursueRange = -1;
	public int FactionNotifyInterval = 500;
	public int SelfAggressive = 1;
	public int transformOnDead = 0;
	public int transformChance = 0;
	public int transformOnUnderAttack = 0;
	public int spawnOtherOnDead = 0;
	public int spawnOtherChance = 100;
	public int spawnOtherCount = 1;
	public int isMadness = 1;
	public float DebuffIntention = 1.0f;
	public boolean searchingMaster = false;
	public boolean canSeeInSilentMove = false;
	public boolean canSeeInHide = false;
	public boolean noRandomWalk = false;
	public boolean isMobile = false;

	public static enum TaskType
	{
		MOVE,
		ATTACK,
		CAST,
		BUFF
	}

	public static class Task
	{
		public TaskType type;
		public L2Skill skill;
		public HardReference<? extends L2Character> task_target = HardReferences.emptyRef();
		public Location loc;
		public boolean pathfind;
		public boolean checkTarget = true;
		public int weight = TaskDefaultWeight;
	}

	@Override
	public void addTaskCast(L2Character target, L2Skill skill)
	{
		Task task = new Task();
		task.type = TaskType.CAST;
		if(target != null)
			task.task_target = target.getRef();
		task.skill = skill;
		_task_list.add(task);
		_def_think = true;
	}

	@Override
	public void addTaskBuff(L2Character target, L2Skill skill)
	{
		Task task = new Task();
		task.type = TaskType.BUFF;
		if(target != null)
			task.task_target = target.getRef();
		task.skill = skill;
		_task_list.add(task);
		_def_think = true;
	}

	@Override
	public void addTaskAttack(L2Character target)
	{
		setLog("			addTaskAttack Start");
		Task task = new Task();
		task.type = TaskType.ATTACK;
		if(target != null)
			task.task_target = target.getRef();
		_task_list.add(task);
		_def_think = true;
	}

	@Override
	public void addTaskMove(Location loc, boolean pathfind)
	{
		/*if(getActor().getNpcId() == 22323 || getActor().getNpcId() == 22659 || getActor().getNpcId() == 22658)
		{
			_log.info("addTaskMove["+getActor().getNpcId()+"]["+getActor().getObjectId()+"]: "+loc+" pathfind="+pathfind);
			Util.test();
		}*/
		Task task = new Task();
		task.type = TaskType.MOVE;
		task.loc = loc;
		task.pathfind = pathfind;
		_task_list.add(task);
		_def_think = true;
	}

	public void addTaskMove(int locX, int locY, int locZ, boolean pathfind)
	{
		addTaskMove(new Location(locX, locY, locZ), pathfind);
	}

	private static class TaskComparator implements Comparator<Task>
	{
		public int compare(Task o1, Task o2)
		{
			if(o1 == null || o2 == null)
				return 0;
			return o2.weight - o1.weight;
		}
	}

	private static final int TaskDefaultWeight = 10000;
	private static final TaskComparator task_comparator = new TaskComparator();

	public class Teleport extends RunnableImpl
	{
		Location _destination;

		public Teleport(Location destination)
		{
			_destination = destination;
		}

		@Override
		public void runImpl() throws Exception
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.teleToLocation(_destination);
		}
	}

	public class RunningTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.setRunning();
			_runningTask = null;
		}
	}

	public class MadnessTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.stopConfused();
			_madnessTask = null;
		}
	}

	protected int AI_TASK_DELAY = ConfigValue.AiTaskDelay; // потом снести...

	protected long AI_TASK_ATTACK_DELAY = ConfigValue.AiTaskDelay;
	protected long AI_TASK_ACTIVE_DELAY = ConfigValue.AiTaskActiveDelay;
	protected long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;

	protected int MAX_PURSUE_RANGE;
	protected int MAX_Z_AGGRO_RANGE = 200;
	protected int MAX_AGGRO_RANGE = 500;

	/** The L2NpcInstance AI task executed every 1s (call onEvtThink method)*/
	protected ScheduledFuture<?> _aiTask;

	protected ScheduledFuture<?> _runningTask;
	protected ScheduledFuture<?> _madnessTask;

	/** The flag used to indicate that a thinking action is in progress */
	protected boolean _thinking = false;

	/** The L2NpcInstance aggro counter */
	protected long _globalAggro;

	protected long _randomAnimationEnd;
	protected int _pathfind_fails;

	/** Список заданий */
	protected ConcurrentSkipListSet<Task> _task_list = new ConcurrentSkipListSet<Task>(task_comparator);

	protected GCArray<L2Character> _see_creature_list = new GCArray<L2Character>();

	/** Показывает, есть ли задания */
	protected boolean _def_think = false;

	public final L2Skill[] _dam_skills, _dot_skills, _debuff_skills, _heal, _buff, _stun;

	private long _lastActiveCheck;
	
	public DefaultAI(L2Character actor)
	{
		super(actor);

		setGlobalAggro(System.currentTimeMillis() + 10000); // 10 seconds timeout of ATTACK after respawn
		L2NpcInstance thisActor = (L2NpcInstance) actor;
		thisActor.setAttackTimeout(Long.MAX_VALUE);

		_dam_skills = thisActor.getTemplate().getDamageSkills();
		_dot_skills = thisActor.getTemplate().getDotSkills();
		_debuff_skills = thisActor.getTemplate().getDebuffSkills();
		_buff = thisActor.getTemplate().getBuffSkills();
		_stun = thisActor.getTemplate().getStunSkills();
		_heal = thisActor.getTemplate().getHealSkills();

		// Preload some AI params
		
		if(MaxPursueRange == -1)
			MaxPursueRange = actor.isRaid() ? ConfigValue.MaxPursueRangeRaid : thisActor.isUnderground() ? ConfigValue.MaxPursueUndergroundRange : ConfigValue.MaxPursueRange;
		
		setMaxPursueRange(MaxPursueRange);
		thisActor.minFactionNotifyInterval = FactionNotifyInterval;
		
		if(NpcTable.getInstance().getAIParams().containsKey(thisActor.getNpcId()))
		{
			HashMap<String, Object> set = NpcTable.getInstance().getAIParams().get(thisActor.getNpcId()).getSet();
			String name = "";
			String values = "";
			for(Object obj : set.keySet())
			{
				name = (String) obj;
				setAIField(this, name, String.valueOf(set.get(name)));
			}
		}
	}

	private static void setAIField(DefaultAI templ, String fieldName, String value)
	{
		try
		{
			Field f = templ.getClass().getField(fieldName);
			setToType(templ, fieldName, value, "NpcTable(363): TODO::Warning text...");
			f = null;
			fieldName = null;
		}
		catch(Exception e)
		{
			if(!fieldName.equals("isFlying") && !fieldName.equals("chatWindowDisabled") && !fieldName.equals("searchingMaster") && !fieldName.equals("spawnAnimationDisabled") && !fieldName.equals("randomAnimationDisabled"))
				e.printStackTrace();
		}
	}

	private static void setToType(DefaultAI nem, String fieldName, String value, String text)
	{
		try
		{
			Field f = nem.getClass().getField(fieldName);
			if(f.getType().getName().equals("int"))
				f.setInt(nem, Integer.parseInt(value));
			else if(f.getType().getName().equals("boolean"))
				f.setBoolean(nem, Boolean.parseBoolean(value));
			else if(f.getType().getName().equals("byte"))
				f.setByte(nem, Byte.parseByte(value));
			else if(f.getType().getName().equals("double"))
				f.setDouble(nem, Double.parseDouble(value));
			else if(f.getType().getName().equals("float"))
				f.setFloat(nem, Float.parseFloat(value));
			else if(f.getType().getName().equals("long"))
				f.setLong(nem, Long.parseLong(value));
			else if(f.getType().getName().equals("short"))
				f.setShort(nem, Short.parseShort(value));
			else if(f.getType().getName().equals("java.lang.String"))
				f.set(nem, value);
			else if(f.getType().getName().equals("[J"))
				f.set(f, Util.parseCommaSeparatedLongArray(value.replace(" ", "")));
			else if(f.getType().getName().equals("[I"))
				f.set(nem, Util.parseCommaSeparatedIntegerArray(value));
			else if(f.getType().getName().equals("[D"))
				f.set(nem, Util.parseCommaSeparatedDoubleArray(value));
			else if(f.getType().getName().startsWith("[F"))
				f.set(nem, Util.parseCommaSeparatedFloatArray(value));
			else if(f.getType().getName().startsWith("[Ljava.lang.String"))
				f.set(nem, value);
		}
		catch(Exception e)
		{
			_log.warning(text);
			e.printStackTrace();
		}
	}

	@Override
	public void runImpl() throws Exception
	{
		try
		{
		setLog("AI run Start");
		if(_aiTask == null)
			return;
		if(!isGlobalAI() && System.currentTimeMillis() - _lastActiveCheck > 60000)
		{
			_lastActiveCheck = System.currentTimeMillis();
			L2NpcInstance actor = getActor();
			L2WorldRegion region = actor == null ? null : actor.getCurrentRegion();
			if(region == null || region.areNeighborsEmpty())
			{
				stopAITask();
				return;
			}
		}
		onEvtThink();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void startAITask()
	{
		setLog("AI startAITask");
		if(_aiTask == null)
		{
			AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(getActor(), this, 0, AI_TASK_DELAY_CURRENT);
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			setLog("AI startAITask ok="+AI_TASK_DELAY_CURRENT);
		}
		_see_creature_list.clear();
	}

	protected void switchAITask(long NEW_DELAY)
	{
		if(_aiTask == null)
			return;

		if(AI_TASK_DELAY_CURRENT != NEW_DELAY)
		{
			_aiTask.cancel(false);
			AI_TASK_DELAY_CURRENT = NEW_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(getActor(), this, 0L, AI_TASK_DELAY_CURRENT);
		}
		_see_creature_list.clear();
	}

	@Override
	public void stopAITask()
	{
		setLog("AI stopAITask");
		//if(getActor() != null && getActor().getNpcId() == 31360)
		//	Util.test();
		try
		{
			if(_aiTask != null)
			{
				setIntention(CtrlIntention.AI_INTENTION_IDLE);
				_aiTask.cancel(false);
				_aiTask = null;
			}
		}
		catch(Exception e)
		{}
		_see_creature_list.clear();
	}

	/**
	 * Определяет, может ли этот тип АИ видеть персонажей в режиме Silent Move.
	 * @param target L2Playable цель
	 * @return true если цель видна в режиме Silent Move
	 */
	@Override
	public boolean canSeeInSilentMove(L2Playable target)
	{
		if(canSeeInSilentMove)
			return true;
		return !target.isSilentMoving();
	}
	
	/**
	 * Определяет, может ли этот тип АИ видеть персонажей в режиме p_hide (Hide).
	 * @param target L2Playable цель
	 * @return true если цель видна в режиме p_hide
	 */
	@Override
	public boolean canSeeInInvis(L2Playable target)
	{
		if(canSeeInHide)
			return true;
		return !target.isInvisible();
	}


	@Override
	public void checkAggression(L2Character target)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
			return;
		if(actor instanceof L2FestivalMonsterInstance && target.getPlayer() != null && !target.getPlayer().isFestivalParticipant())
			return;
		if(!actor.isAggressive() || actor.getDistance(target) > MAX_AGGRO_RANGE/*actor.getAgroRange()*/ || Math.abs(actor.getZ() - target.getZ()) > MAX_Z_AGGRO_RANGE)
			return;
		if(target.isPlayable() && !canSeeInSilentMove((L2Playable) target))
			return;
		if(actor.getFactionId().equalsIgnoreCase("varka_silenos_clan") && target.getPlayer() != null && target.getPlayer().getVarka() > 0)
			return;
		if(actor.getFactionId().equalsIgnoreCase("ketra_orc_clan") && target.getPlayer() != null && target.getPlayer().getKetra() > 0)
			return;
		if(target.isInZonePeace())
			return;
		if(target.isFollow && !target.isPlayer() && target.getFollowTarget() != null && target.getFollowTarget().isPlayer())
			return;
		if(target.isPlayable() && !canSeeInInvis((L2Playable) target))
			return;
		if(target.isPlayer() && ((L2Player)target).isGM() && !canSeeInInvis((L2Playable) target))
			return;
		if(target.getNonAggroTime() > System.currentTimeMillis())
			return;
		if(target.isPlayer() && !target.getPlayer().isActive())
			return;

		// Если таргет это игрок, а так же на таргете весит Рейд Курсе, то мы не агримся...
		if(target.isPlayer() && target._isInvul_skill != null && (target._isInvul_skill.getId() == 4515 || target._isInvul_skill.getId() == 4215))
			return;

		if(!actor.canAttackCharacter(target))
			return;
		if(!GeoEngine.canAttacTarget(actor, target, false))
			return;

		target.addDamageHate(actor, 0, 2);

		if((target.isSummon() || target.isPet()) && target.getPlayer() != null)
			target.getPlayer().addDamageHate(actor, 0, 1);

		startRunningTask(AI_TASK_ATTACK_DELAY);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}

	public void setIsInRandomAnimation(long time)
	{
		_randomAnimationEnd = System.currentTimeMillis() + time;
	}

	protected boolean randomAnimation()
	{
		L2NpcInstance actor = getActor();
		if(actor != null && !actor.isMoving && actor.hasRandomAnimation() && Rnd.chance(ConfigValue.RndAnimationRate))
		{
			setIsInRandomAnimation(3000);
			actor.onRandomAnimation();
			return true;
		}
		return false;
	}

	protected boolean randomWalk()
	{
		if(noRandomWalk)
			return false;
		L2Character actor = getActor();
		return actor != null && !actor.isMoving && maybeMoveToHome();
	}

	/**
	 * @return true если действие выполнено, false если нет
	 */
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(actor.isBlocked() || _randomAnimationEnd > System.currentTimeMillis())
			return true;

		if(_def_think)
		{
			if(doTask())
				clearTasks();
			return true;
		}
		else if(_no_desair + _no_desair_time < System.currentTimeMillis())
		{
			_no_desair = System.currentTimeMillis();
			NO_DESIRE();
		}

		/**
		 * HalfAggressive: 0 - нету агра, 1 - агрится только до 05.00, 2 - агрится только после 05.00
		 * RandomAggressive: 1 - агрится рандомно.
		 * IsAggressive: - агрится всегда.
		 * Aggressive_Time: - время, через которое после респа моба он может агрится.
		 **/
		// Аггрится даже на неподвижных игроков
		if(actor.isAggressive() && Rnd.chance(SelfAggressive))
			//for(L2Playable obj : L2World.getAroundPlayables(actor, actor.getAggroRange(), 150))
			for(L2Playable obj : L2World.getAroundPlayables(actor, MAX_AGGRO_RANGE, MAX_Z_AGGRO_RANGE))
				if(obj != null && !obj.isAlikeDead() && !obj.isInvul() && obj.isVisible())
					checkAggression(obj);

		// If this is a festival monster or chest, then it remains in the same location
		if(actor instanceof L2FestivalMonsterInstance || actor instanceof L2ChestInstance || actor instanceof L2TamedBeastInstance)
			return false;

		if(actor.isMinion() && actor.getNpcId() != 29002)
		{
			L2MonsterInstance leader = ((L2MinionInstance) actor).getLeader();
			if(leader == null)
				return false;
			double distance = actor.getDistance(leader.getX(), leader.getY());
			if(distance > 1000)
				actor.teleToLocation(leader.getMinionPosition());
			else if(distance > 200)
				addTaskMove(leader.getMinionPosition(), false);
			return false;
		}

		if(randomAnimation())
			return true;

		if(randomWalk())
			return true;

		return false;
	}

	@Override
	protected void onIntentionActive()
	{
		L2NpcInstance actor = getActor();
		if(actor != null)
			actor.setAttackTimeout(Long.MAX_VALUE);

		clientStopMoving();

		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
		{
			switchAITask(AI_TASK_ACTIVE_DELAY);
			changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		}

		onEvtThink();
	}

	protected boolean checkTarget(L2Character target, boolean canSelf, int range)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || target == null || target == actor && !canSelf || target.isAlikeDead() || !actor.isInRange(target, range))
		{
			setLog("checkTarget Error 1 target["+target+"] isAlikeDead["+(target != null ? target.isAlikeDead() : "null")+"] isInRange["+(actor != null ? !actor.isInRange(target, range) : "null")+"]["+(actor != null ? actor.getDistance(target) : "null")+"]!=["+range+"]");
			return true;
		}

		final boolean hided = target.isPlayable() && !canSeeInInvis((L2Playable)target);

		if(!hided && actor.isConfused())
		{
			setLog("checkTarget Error 2");
			return true;
		}

		if(actor.isConfused() || target instanceof L2DecoyInstance)
		{
			setLog("checkTarget Ok 1");
			return false;
		}

		if(target.isInvisible() || !canSelf && !target.isPlayable() && target.getHateList().get(actor) == null)
		{
			setLog("checkTarget Error 3: target["+target+"]");
			return true;
		}
		//if(!canSelf && target.getHateList().get(actor) == null && getActor().getNpcId() != 18660)
		//	return true;

		/**
		if(actor.getAttackTimeout() < System.currentTimeMillis())
		{
			if(actor.isRunning() && actor.getAggroListSize() == 1)
			{
				actor.setWalking();
				actor.setAttackTimeout(MAX_ATTACK_TIMEOUT / 4 + System.currentTimeMillis());
				return false;
			}
			target.removeFromHatelist(actor);
			return true;
		}
		 */

		setLog("checkTarget Ok");
		return false;
	}

	protected void thinkAttack()
	{
		setLog("	thinkAttack Start");
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		Location loc = actor.getSpawnedLoc();
		if(loc != null && loc.x != 0 && loc.y != 0 && !actor.isInRange(loc, getMaxPursueRange()))
		{
			teleportHome(true);
			return;
		}

		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			setLog("		thinkAttack createNewTask");
			createNewTask();
		}
		setLog("	thinkAttack Finish");
	}

	@Override
	protected void onEvtReadyToAct()
	{
		onEvtThink();
	}

	@Override
	protected void onEvtArrivedTarget(int i)
	{
		L2Character actor = getActor();
		/*if(actor.getNpcId() > 0)
		{
			_log.info("DefaultAI: onEvtArrivedTarget["+i+"]->:"+actor.getNpcId()+"|"+actor.getObjectId());
			//Util.test();
		}*/
		onEvtThink();
	}

	@Override
	protected void onEvtArrived()
	{
		onEvtThink();
	}
	protected void tryMoveToTarget(L2Character target)
	{
		tryMoveToTarget(target, 0);
	}

	protected void tryMoveToTarget(L2Character target, int range)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		range = range == 0 ? actor.getPhysicalAttackRange() : Math.max(0, range);
		if(!actor.followToCharacter(target, range, !ConfigValue.NewGeoEngine, true))
			_pathfind_fails++;

		if(_pathfind_fails >= getMaxPathfindFails() && System.currentTimeMillis() - (actor.getAttackTimeout() - getMaxAttackTimeout()) < getTeleportTimeout() && actor.isInRange(target, 2000))
		{
			_pathfind_fails = 0;
			HateInfo hate = target.getHateList().get(actor);
			if(hate == null || hate.damage < 100 && hate.hate < 100)
			{
				returnHome(true);
				return;
			}
			Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex());
			if(!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getReflection().getGeoIndex())) // Р”Р»СЏ РїРѕРґСЃС‚СЂР°С…РѕРІРєРё
				loc = target.getLoc();
			actor.teleToLocation(loc);
			//actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
			//ThreadPoolManager.getInstance().scheduleAI(new Teleport(GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex())), 500);
		}
	}
	/*protected void tryMoveToTarget(final L2Character target, final int range)
	{
		final L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(actor._move_data == null)
			actor._move_data = new MoveData(actor);

		actor._move_data._moveTask = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
		{
			public void runImpl()
			{
				int offset = Math.max(0, range);
				if(range == 0)
				{
					if(actor.getPhysicalAttackRange() <= 300)
						offset = (int)(actor.getPhysicalAttackRange()*.67);
					else
						offset = (int)(actor.getPhysicalAttackRange()-100f);
					// на ПТСке еще +10 к offset
				}
				//new L2ObjectTasks.ExecuteFollow(actor, attack_target, (int)Math.ceil(offset+actor.getMinDistance(attack_target)), ConfigValue.FollowFindPathType == 0 || !actor.isPlayer())
				//actor.followToCharacter(target, offset, true, true);

				if(!actor.followToCharacter(target, (int)Math.ceil(offset+actor.getMinDistance(target)), true, true))
					_pathfind_fails++;

				if(_pathfind_fails >= getMaxPathfindFails() && System.currentTimeMillis() - (actor.getAttackTimeout() - getMaxAttackTimeout()) < getTeleportTimeout() && actor.isInRange(target, 2000))
				{
					_pathfind_fails = 0;
					HateInfo hate = target.getHateList().get(actor);
					if(hate == null || hate.damage < 100 && hate.hate < 100)
					{
						returnHome(true);
						return;
					}
					Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex());
					if(!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getReflection().getGeoIndex())) // Для подстраховки
						loc = target.getLoc();
					actor.teleToLocation(loc);
					//actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
					//ThreadPoolManager.getInstance().scheduleAI(new Teleport(GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex())), 500);
				}
			}
		}, 500);

		*int offset = Math.max(0, range);
		if(range == 0)
		{
			if(actor.getPhysicalAttackRange() <= 300)
				offset = (int)(actor.getPhysicalAttackRange()*.67);
			else
				offset = (int)(actor.getPhysicalAttackRange()-100f);
			// на ПТСке еще +10 к offset
		}
		if(!actor.followToCharacter(target, (int)Math.ceil(offset+actor.getMinDistance(target)), true, true))
			_pathfind_fails++;

		if(_pathfind_fails >= getMaxPathfindFails() && System.currentTimeMillis() - (actor.getAttackTimeout() - getMaxAttackTimeout()) < getTeleportTimeout() && actor.isInRange(target, 2000))
		{
			_pathfind_fails = 0;
			HateInfo hate = target.getHateList().get(actor);
			if(hate == null || hate.damage < 100 && hate.hate < 100)
			{
				returnHome(true);
				return;
			}
			Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex());
			if(!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getReflection().getGeoIndex())) // Для подстраховки
				loc = target.getLoc();
			actor.teleToLocation(loc);
			//actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
			//ThreadPoolManager.getInstance().scheduleAI(new Teleport(GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getReflection().getGeoIndex())), 500);
		}*
	}*/

	protected boolean maybeNextTask(Task currentTask)
	{
		// Следующее задание
		_task_list.remove(currentTask);
		// Если заданий больше нет - определить новое
		if(_task_list.size() == 0)
		{
			//NO_DESIRE();
			return true;
		}
		return false;
	}

	protected boolean doTask()
	{
		setLog("		doTask Start");
		L2NpcInstance actor = getActor();
		if(actor == null)
		{
			setLog("		doTask Error 1");
			return false;
		}

		if(_task_list.size() == 0)
		{
			setLog("		doTask Error 2");
			clearTasks();
			return true;
		}

		Task currentTask = null;
		try
		{
			currentTask = _task_list.first();
		}
		catch(Exception e)
		{
			setLog("		doTask Error 3");
		}

		if(currentTask == null)
			clearTasks();

		if(!_def_think)
		{
			setLog("		doTask Error 4");
			return true;
		}

		//assert currentTask != null;
		L2Character temp_attack_target = currentTask.task_target.get();

		if(actor.isAttackingNow() || actor.isCastingNow())
		{
			setLog("		doTask Error 5");
			return false;
		}

		switch(currentTask.type)
		{
			// Задание "прибежать в заданные координаты"
			case MOVE:
			{
				if(actor.isMovementDisabled() || !getIsMobile())
					return true;

				if(actor.isInRange(currentTask.loc, 100))
					return maybeNextTask(currentTask);

				if(actor.isMoving)
					return false;

				if(!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind))
				{
					//if(actor.getNpcId() == 31360)
					//	_log.info("doTask MOVE["+actor.getNpcId()+"]["+actor.getObjectId()+"]: "+currentTask.loc+" pathfind="+currentTask.pathfind);
					/*if(actor.getNpcId() == 22323 || actor.getNpcId() == 22659 || actor.getNpcId() == 22658)
					{
						_log.info("doTask MOVE["+actor.getNpcId()+"]["+actor.getObjectId()+"]: "+currentTask.loc+" pathfind="+currentTask.pathfind);
						Util.test();
					}*/
					clientStopMoving();
					_pathfind_fails = 0;
					actor.teleToLocation(currentTask.loc);
					//actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
					//ThreadPoolManager.getInstance().scheduleAI(new Teleport(currentTask.loc), 500);
					return maybeNextTask(currentTask);
				}
			}
				break;
			// Задание "добежать - ударить"
			case ATTACK:
			{
				setLog("		doTask ATTACK Start");
				if(currentTask.checkTarget && checkTarget(temp_attack_target, false, 4500))
				{
					setLog("		doTask ATTACK Error 1");
					return true;
				}

				if(actor.getEffectList().getEffectsCountForSkill(5044) > 0)
					return true;

				setAttackTarget(temp_attack_target);

				if(actor.isMoving)
				{
					setLog("		doTask ATTACK Error 2");
					return Rnd.chance(25) && currentTask.checkTarget;
				}

				L2Weapon weaponItem = actor.getActiveWeaponItem();
				WeaponType w_type = weaponItem != null ? weaponItem.getItemType() : null;
				if(w_type == null)
					w_type = actor.getFistWeaponType();
				boolean bow_eq = w_type != null && (w_type == WeaponType.BOW || w_type == WeaponType.CROSSBOW);
				boolean canSee = ConfigValue.AttackInBarrierNpc && !bow_eq || GeoEngine.canAttacTarget(actor, temp_attack_target, false);

				if(actor.getRealDistance3D(temp_attack_target, ConfigValue.NpcRealDistance3D) <= actor.getPhysicalAttackRange() + 40 && canSee)
				{
					clientStopMoving();
					_pathfind_fails = 0;
					actor.setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
					setLog("		doTask doAttack Ok");
					actor.doAttack(temp_attack_target, true);
					return maybeNextTask(currentTask);
				}

				if(actor.isMovementDisabled() || !getIsMobile())
					return true;

				setLog("		doTask ATTACK Finish");
				tryMoveToTarget(temp_attack_target, 0);
				break;
			}
			// Задание "добежать - атаковать скиллом"
			case CAST:
			{
				if(actor.isMuted(currentTask.skill) || actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? currentTask.skill.getId()*65536L+currentTask.skill.getLevel() : currentTask.skill.getId()))
					return true;

				boolean isAoE = currentTask.skill.getTargetType() == L2Skill.SkillTargetType.TARGET_AURA;

				if(checkTarget(temp_attack_target, isAoE ? true : false, 4500))
					return true;

				if(actor.getEffectList().getEffectsCountForSkill(5044) > 0)
					return true;

				setAttackTarget(temp_attack_target);

				int castRange = currentTask.skill.getAOECastRange();

				if(actor.getRealDistance3D(temp_attack_target, ConfigValue.NpcRealDistance3D) <= castRange + 60 && GeoEngine.canAttacTarget(actor, temp_attack_target, false))
				{
					clientStopMoving();
					_pathfind_fails = 0;
					actor.setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
					actor.doCast(currentTask.skill, isAoE ? actor : temp_attack_target, !temp_attack_target.isPlayable());
					return maybeNextTask(currentTask);
				}

				if(actor.isMoving)
					return Rnd.chance(10);

				if(actor.isMovementDisabled() || !getIsMobile())
					return true;

				tryMoveToTarget(temp_attack_target, castRange);
				break;
			}
			// Задание "добежать - применить скилл"
			case BUFF:
			{
				if(actor.isMuted(currentTask.skill) || actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? currentTask.skill.getId()*65536L+currentTask.skill.getLevel() : currentTask.skill.getId()))
					return true;

				if(temp_attack_target == null || temp_attack_target.isAlikeDead() || !actor.isInRange(temp_attack_target, 3000))
					return true;

				if(actor.getEffectList().getEffectsCountForSkill(5044) > 0)
					return true;

				boolean isAoE = currentTask.skill.getTargetType() == L2Skill.SkillTargetType.TARGET_AURA;
				int castRange = currentTask.skill.getAOECastRange();

				if(actor.isMoving)
					return Rnd.chance(10);

				if(actor.getRealDistance3D(temp_attack_target, ConfigValue.NpcRealDistance3D) <= castRange + 60 && GeoEngine.canAttacTarget(actor, temp_attack_target, false))
				{
					clientStopMoving();
					_pathfind_fails = 0;
					actor.doCast(currentTask.skill, isAoE ? actor : temp_attack_target, !temp_attack_target.isPlayable());
					return maybeNextTask(currentTask);
				}

				if(actor.isMovementDisabled() || !getIsMobile())
					return true;

				tryMoveToTarget(temp_attack_target, castRange);
				break;
			}
		}
		return false;
	}

	protected boolean createNewTask()
	{
		return false;
	}

	protected boolean defaultNewTask()
	{
		clearTasks();

		L2NpcInstance actor = getActor();
		L2Character target;
		if(actor == null || (target = prepareTarget()) == null)
			return false;

		double distance = actor.getDistance(target);
		return chooseTaskAndTargets(null, target, distance);
	}

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		// Удаляем все задания
		clearTasks();
		setAttackTarget(target);
		clientStopMoving();
		if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
			switchAITask(AI_TASK_ATTACK_DELAY);
		}

		onEvtThink();
	}

	@Override
	protected void onEvtThink()
	{
		setLog("onEvtThink Start");
		L2NpcInstance actor = getActor();
		if(actor == null)
			stopAITask();
		if(_thinking || actor == null || actor.isActionsDisabled() || actor.isAfraid()  || actor.isDead())
		{
			//setLog("onEvtThink: _thinking="+_thinking+" actor="+(actor == null)+" isActionsDisabled="+actor.isActionsDisabled()+" isAfraid="+actor.isAfraid()+" isDead="+actor.isDead());
			//if(getActor().i_ai0 == 1994575)
			//	Util.test();
			return;
		}

		if(actor.isBlocked() || _randomAnimationEnd > System.currentTimeMillis())
		{
			setLog("onEvtThink: isBlocked="+actor.isBlocked()+" _randomAnimationEnd="+(_randomAnimationEnd > System.currentTimeMillis())+" _randomAnimationEnd="+_randomAnimationEnd+" System.currentTimeMillis="+System.currentTimeMillis());
			return;
		}

		if(actor.isRaid() && (actor.isInZonePeace() || actor.isInZone(ZoneType.battle_zone) || actor.isInZone(ZoneType.Siege)))
			teleportHome(true);

		_thinking = true;
		try
		{
			if(actor.getEventFlag() > 0)
			{
				for(L2Playable obj : L2World.getAroundPlayables(actor, MAX_AGGRO_RANGE, MAX_Z_AGGRO_RANGE)) // 450 ренж
					if(obj != null && !obj.isAlikeDead() && obj.isVisible() && !_see_creature_list.contains(obj))
					{
						SEE_CREATURE(obj);
						checkAggression(obj);
						if(actor.getDistance(obj) <= actor.getAgroRange())
							_see_creature_list.add(obj);
					}
				if(actor == null)
					_see_creature_list.clear();
				for(L2Character obj : _see_creature_list)
				{
					try
					{
						if(obj == null || obj.isDead() || actor.getDistance(obj) > 600)
							_see_creature_list.remove(obj);
					}
					catch(Exception e)
					{}
				}
				if(_see_creature_list.size() == 0)
					_see_creature_list.clear();
			}
			if(!ConfigValue.BlockActiveTasks && (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE))
			{
				setLog("onEvtThink thinkActive");
				thinkActive();
				setLog("onEvtThink thinkActive 2");
			}
			else if(getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
			{
				setLog("onEvtThink thinkAttack");
				thinkAttack();
				setLog("onEvtThink thinkAttack 2");
				//if(getActor().i_ai0 == 1994575)
				//	Util.test();
			}
		}
		catch(Exception e)
		{
			setLog("onEvtThink Exception: "+e);
			_see_creature_list.clear();
			e.printStackTrace();
		}
		finally
		{
			setLog("onEvtThink Finish");
			_thinking = false;
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();

		if(actor != null)
			actor.callFriends(killer, true);

		if(transformChance == 0)
			transformChance = 100;
		if(transformOnDead > 0 && Rnd.chance(transformChance))
		{
			try
			{
				if(actor != null)
				{
					Reflection r = actor.getReflection();
					L2MonsterInstance npc = (L2MonsterInstance) NpcTable.getTemplate(transformOnDead).getNewInstance();
					npc.setSpawnedLoc(actor.getLoc());
					npc.setReflection(r);
					npc.onSpawn();
					npc.spawnMe(npc.getSpawnedLoc());
					if(r.getId() > 0)
						r.addSpawn(npc.getSpawn());
					if(killer != null && killer.isPlayable())
					{
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
						killer.setTarget(npc);
						killer.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

        if(spawnOtherOnDead > 0 && Rnd.chance(spawnOtherChance))
		{
			for(int i = 0; i < spawnOtherCount; i++)
				try
				{
					if(actor != null)
					{
						Reflection r = actor.getReflection();
						L2MonsterInstance npc = (L2MonsterInstance)NpcTable.getTemplate(spawnOtherOnDead).getNewInstance();
						Location pos = GeoEngine.findPointToStay(actor.getX(), actor.getY(), actor.getZ(), 100, 120, actor.getReflection().getGeoIndex());
						npc.setSpawnedLoc(pos);
						npc.setReflection(r);
						npc.onSpawn();
						npc.spawnMe(npc.getSpawnedLoc());
						if(r.getId() > 0)
							r.addSpawn(npc.getSpawn());
						if(killer != null && killer.isPlayable())
						{
							npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
							killer.setTarget(npc);
							killer.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
						}
					}
				}
            catch(Exception e)
            {
                e.printStackTrace();
            }
		}

		stopAITask();

		if(actor != null)
			actor.clearAggroList(false);

		if(actor != null)
			actor.setAttackTimeout(Long.MAX_VALUE);

		// Удаляем все задания
		clearTasks();

		if(actor != null && (actor.getNpcId() == 18329 || actor.getNpcId() == 18335 || actor.getNpcId() == 18336 || actor.getNpcId() == 18332 || actor.getNpcId() == 18331) && Rnd.get(100) < 5)
			DropItem1(actor,8556,1);

		super.MY_DYING(killer);
	}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
			return;
		L2NpcInstance actor = getActor();
		if(actor == null || !actor.isInRange(attacked_member, actor.getFactionRange()))
			return;
		if(Math.abs(attacker.getZ() - actor.getZ()) > MAX_Z_AGGRO_RANGE)
			return;

		if(GeoEngine.canAttacTarget(actor, attacked_member, false))
			notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[] { attacker, attacker.isSummon() ? damage : 3 });
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		setLog("ATTACKED Start");
		L2NpcInstance actor = getActor();
		if(attacker == null || actor == null)
			return;

		if(!actor.isDead())
		{
			if(transformOnUnderAttack > 0)
				try
				{
					if(transformChance == 0)
						transformChance = 5;
					if(transformChance == 100 || ((L2MonsterInstance) actor).getChampion() == 0 && actor.getCurrentHpPercents() > 50 && Rnd.chance(transformChance))
					{
						Reflection r = actor.getReflection();
						L2MonsterInstance npc = (L2MonsterInstance) NpcTable.getTemplate(transformOnUnderAttack).getNewInstance();
						npc.setSpawnedLoc(actor.getLoc());
						npc.setReflection(r);
						npc.onSpawn();
						npc.setChampion(((L2MonsterInstance) actor).getChampion());
						npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
						npc.spawnMe(npc.getSpawnedLoc());
						if(r.getId() > 0)
							r.addSpawn(npc.getSpawn());
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
						actor.decayMe();
						actor.doDie(actor);
						attacker.setTarget(npc);
						attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.p_max_hp));
						return;
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
		}

		if(!actor.canAttackCharacter(attacker))
			return;

		L2Player player = attacker.getPlayer();
		if(player != null)
		{
			List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.MOBGOTATTACKED);
			if(quests != null)
				for(QuestState qs : quests)
					qs.getQuest().notifyAttack(actor, qs);
		}

		if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if(!actor.isRunning())
				startRunningTask(AI_TASK_ATTACK_DELAY);
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}

		actor.callFriends(attacker, damage);
		setLog("ATTACKED Finish");
	}

	@Override
	protected void onEvtAggression(L2Character attacker, int aggro)
	{
		if(attacker == null || attacker.getPlayer() == null)
			return;

		L2NpcInstance actor = getActor();
		if(actor == null || !actor.canAttackCharacter(attacker))
			return;

		L2Player player = attacker.getPlayer();

		if(!ConfigValue.SevenSignsAll && getIntention() != CtrlIntention.AI_INTENTION_ATTACK && (SevenSigns.getInstance().isSealValidationPeriod() || SevenSigns.getInstance().isCompResultsPeriod()) && actor.isSevenSignsMonster())
		{
			int pcabal = SevenSigns.getInstance().getPlayerCabal(player);
			int wcabal = SevenSigns.getInstance().getCabalHighestScore();
			if(pcabal != wcabal && wcabal != SevenSigns.CABAL_NULL)
			{
				player.sendMessage("You have been teleported to the nearest town because you not signed for winning cabal.");
				player.teleToClosestTown();
				return;
			}
		}

		actor.setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
		setGlobalAggro(0);

		attacker.addDamageHate(actor, 0, aggro);

		// Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
		if(attacker.getPlayer() != null && aggro > 0 && (attacker.isSummon() || attacker.isPet()))
			attacker.getPlayer().addDamageHate(actor, 0, searchingMaster && attacker.isInRange(actor, 2000) ? aggro : 1);

		if(!actor.isRunning())
			startRunningTask(AI_TASK_ATTACK_DELAY);

		if(getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			// Показываем анимацию зарядки шотов, если есть таковые.
			switch(actor.getTemplate().shots)
			{
				case SOUL:
					actor.unChargeShots(false);
					break;
				case SPIRIT:
				case BSPIRIT:
					actor.unChargeShots(true);
					break;
				case SOUL_SPIRIT:
				case SOUL_BSPIRIT:
					actor.unChargeShots(false);
					actor.unChargeShots(true);
					break;
			}

			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}

	protected boolean maybeMoveToHome()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;

		boolean randomWalk = actor.hasRandomWalk();
		Location sloc = actor.getSpawnedLoc();
		if(sloc == null)
			return false;

		// Моб попал на другой этаж
		if(Math.abs(sloc.z - actor.getZ()) > 128 && !isGlobalAI())
		{
			teleportHome(true);
			return true;
		}

		// Random walk or not?
		if(randomWalk && (!ConfigValue.RndWalk || !Rnd.chance(ConfigValue.RndWalkRate)))
			return false;

		boolean isInRange = actor.isInRangeZ(sloc, ConfigValue.MaxDriftRange);

		if(!randomWalk && isInRange)
			return false;

		int x = sloc.x + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int y = sloc.y + Rnd.get(-ConfigValue.MaxDriftRange, ConfigValue.MaxDriftRange);
		int z = GeoEngine.getHeight(x, y, sloc.z, actor.getReflection().getGeoIndex());

		if(Math.abs(sloc.z - z) > 64)
			return false;

		L2Spawn spawn = actor.getSpawn();
		if(spawn != null && spawn.getLocation() > 0 && !TerritoryTable.getInstance().getLocation(spawn.getLocation()).isInside(x, y))
			return false;

		if(spawn != null && spawn.getLocation2() != null)
			for(L2Territory terr : spawn.getLocation2())
				if(!terr.isInside(x, y))
					return false;

		actor.setWalking();

		// Телепортируемся домой, только если далеко от дома
		if(!actor.moveToLocation(x, y, z, 0, false) && !isInRange)
			teleportHome(true);

		return true;
	}

	public void returnHome(boolean clearAggro)
	{
		if(isNotReturnHome())
			return;
		if(ConfigValue.AlwaysTeleportHome)
		{
			teleportHome(clearAggro);
			return;
		}

		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character most_hated = actor.getMostHated();
		if(most_hated != null)
			actor.getAI().notifyEvent(CtrlEvent.EVT_FINISH_ATTACK, most_hated);
		
		if(clearAggro)
			actor.clearAggroList(true);

		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

		// Удаляем все задания
		clearTasks();

		Location sloc = actor.getSpawnedLoc();
		if(sloc == null)
			return;

		if(!clearAggro)
			actor.setRunning();

		addTaskMove(sloc, false);
	}

	@Override
	public void teleportHome(boolean clearAggro)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(clearAggro)
			actor.clearAggroList(true);

		setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

		// Удаляем все задания
		clearTasks();

		Location sloc = actor.getSpawnedLoc();
		if(sloc == null)
			return;

		actor.broadcastSkill(new MagicSkillUse(actor, actor, 2036, 1, 500, 0), false);
		actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getReflection().getGeoIndex()));
	}

	protected L2Character prepareTarget()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return null;

		// Новая цель исходя из агрессивности
		L2Character hated = actor.isConfused() && getAttackTarget() != actor ? getAttackTarget() : actor.getMostHated();

		// Для "двинутых" боссов, иногда, выбираем случайную цель
		if(!actor.isConfused() && Rnd.chance(isMadness))
		{
			L2Character randomHated = actor.getRandomHated();
			if(randomHated != null && randomHated != hated && randomHated != actor)
			{
				setAttackTarget(randomHated);
				if(_madnessTask == null && !actor.isConfused())
				{
					actor.startConfused();
					_madnessTask = ThreadPoolManager.getInstance().scheduleAI(new MadnessTask(), 10000);
				}
				return randomHated;
			}
		}

		if(hated != null && hated != actor && !hated.isAlikeDead())
		{
			setAttackTarget(hated);
			return hated;
		}

		returnHome(false);
		return null;
	}

	protected boolean canUseSkill(L2Skill sk, L2Character target, double distance)
	{
		L2NpcInstance actor = getActor();

		if(actor == null || sk == null || sk.isNotUsedByAI() || actor.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? sk.getId()*65536L+sk.getLevel() : sk.getId()))
			return false;

		double mpConsume2 = sk.getMpConsume2();
		if(actor.getCurrentMp() < mpConsume2)
			return false;
		if(actor.isMuted(sk))
			return false;

		int castRange = sk.getAOECastRange();
		/*
				if(distance > 0)
				{
					if(castRange > 200)
					{
						if(distance <= 200 && !actor.isRaid())
							return false;
					}
					else if(distance > 200)
						return false;
				}
		*/
		if(castRange <= 200 && distance > 200)
			return false;
		if(target.getEffectList().getEffectsBySkill(sk) != null)
			return false;
		return true;
	}

	protected boolean canUseSkill(L2Skill sk, L2Character target)
	{
		return canUseSkill(sk, target, 0);
	}

	protected L2Skill[] selectUsableSkills(L2Character target, double distance, L2Skill... skills)
	{
		if(skills == null || skills.length == 0 || target == null)
			return null;

		L2Skill[] ret = null;
		int usable = 0;

		for(L2Skill skill : skills)
			if(canUseSkill(skill, target, distance))
			{
				if(ret == null)
					ret = new L2Skill[skills.length];
				ret[usable++] = skill;
			}

		if(ret == null || usable == skills.length)
			return ret;

		L2Skill[] ret_resized = new L2Skill[usable];
		System.arraycopy(ret, 0, ret_resized, 0, usable);
		return ret_resized;
	}

	protected static L2Skill selectTopSkillByDamage(L2Character actor, L2Character target, double distance, L2Skill... skills)
	{
		if(skills == null || skills.length == 0)
			return null;

		RndSelector<L2Skill> rnd = new RndSelector<L2Skill>(skills.length);
		double weight;
		for(L2Skill skill : skills)
		{
			weight = skill.getSimpleDamage(actor, target) * skill.getAOECastRange() / distance;
			if(weight <= 0)
				weight = 1;
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static L2Skill selectTopSkillByDebuff(L2Character actor, L2Character target, double distance, L2Skill... skills) //FIXME
	{
		if(skills == null || skills.length == 0)
			return null;

		RndSelector<L2Skill> rnd = new RndSelector<L2Skill>(skills.length);
		double weight;
		for(L2Skill skill : skills)
		{
			if(skill.getSameByStackType(target) != null)
				continue;
			if((weight = 100f * skill.getAOECastRange() / distance) <= 0)
				weight = 1;
			if(skill.isCancel() && Rnd.get(100) < 60)
				continue;
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static L2Skill selectTopSkillByBuff(L2Character target, L2Skill... skills)
	{
		if(skills == null || skills.length == 0)
			return null;

		RndSelector<L2Skill> rnd = new RndSelector<L2Skill>(skills.length);
		double weight;
		for(L2Skill skill : skills)
		{
			if(skill.getSameByStackType(target) != null)
				continue;
			if((weight = skill.getPower()) <= 0)
				weight = 1;
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected static L2Skill selectTopSkillByHeal(L2Character target, L2Skill... skills)
	{
		if(skills == null || skills.length == 0)
			return null;

		double hp_reduced = target.getMaxHp() - target.getCurrentHp();
		if(hp_reduced < 1)
			return null;

		RndSelector<L2Skill> rnd = new RndSelector<L2Skill>(skills.length);
		double weight;
		for(L2Skill skill : skills)
		{
			if((weight = Math.abs(skill.getPower() - hp_reduced)) <= 0)
				weight = 1;
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}

	protected void addDesiredSkill(Map<L2Skill, Integer> skill_list, L2Character target, double distance, L2Skill[] skills_for_use)
	{
		if(skills_for_use == null || skills_for_use.length == 0 || target == null)
			return;
		for(L2Skill sk : skills_for_use)
			addDesiredSkill(skill_list, target, distance, sk);
	}

	protected void addDesiredSkill(Map<L2Skill, Integer> skill_list, L2Character target, double distance, L2Skill skill_for_use)
	{
		if(skill_for_use == null || target == null || !canUseSkill(skill_for_use, target))
			return;
		int weight = (int) -Math.abs(skill_for_use.getAOECastRange() - distance);
		if(skill_for_use.getAOECastRange() >= distance)
			weight += 1000000;
		else if(skill_for_use.isNotTargetAoE(getActor()) && skill_for_use.getTargets(getActor(), target, false).size() == 0)
			return;
		skill_list.put(skill_for_use, weight);
	}

	protected void addDesiredHeal(Map<L2Skill, Integer> skill_list, L2Skill[] skills_for_use)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || skills_for_use == null || skills_for_use.length == 0)
			return;
		double hp_reduced = actor.getMaxHp() - actor.getCurrentHp();
		double hp_precent = actor.getCurrentHpPercents();
		if(hp_reduced < 1)
			return;
		int weight;
		for(L2Skill sk : skills_for_use)
			if(canUseSkill(sk, actor) && sk.getPower() <= hp_reduced)
			{
				weight = (int) sk.getPower();
				if(hp_precent < 50)
					weight += 1000000;
				skill_list.put(sk, weight);
			}
	}

	protected void addDesiredBuff(Map<L2Skill, Integer> skill_list, L2Skill[] skills_for_use)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || skills_for_use == null || skills_for_use.length == 0)
			return;
		for(L2Skill sk : skills_for_use)
			if(canUseSkill(sk, actor))
				skill_list.put(sk, 1000000);
	}

	protected L2Skill selectTopSkill(Map<L2Skill, Integer> skill_list)
	{
		if(skill_list == null || skill_list.isEmpty())
			return null;
		int next_weight, top_weight = Integer.MIN_VALUE;
		for(L2Skill next : skill_list.keySet())
			if((next_weight = skill_list.get(next)) > top_weight)
				top_weight = next_weight;
		if(top_weight == Integer.MIN_VALUE)
			return null;
		for(L2Skill next : skill_list.keySet())
			if(skill_list.get(next) < top_weight)
				skill_list.remove(next);
		next_weight = skill_list.size();
		return skill_list.keySet().toArray(new L2Skill[next_weight])[Rnd.get(next_weight)];
	}

	protected boolean chooseTaskAndTargets(L2Skill r_skill, L2Character target, double distance)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;
		// Использовать скилл если можно, иначе атаковать
		if(r_skill != null && !actor.isMuted(r_skill)/* && System.currentTimeMillis() >= TIMER + DELAY*/ || r_skill != null && !actor.isMuted(r_skill) && actor.isSiegeGuard() || actor.getNpcId() == 36564)
		{
			if(r_skill != null)
			{
				// Проверка цели, и смена если необходимо
				if(r_skill.getTargetType() != null)
					if(r_skill.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF)
						target = actor;
				else if(actor.isMovementDisabled() && r_skill.isOffensive() && distance > r_skill.getAOECastRange() + 60)
				{
					GArray<L2Playable> targets = new GArray<L2Playable>();
					for(AggroInfo ai : actor.getAggroList())
						if(ai.attacker != null && actor.getDistance(ai.attacker) <= r_skill.getAOECastRange() + 60)
							targets.add(ai.attacker);
					if(targets.size() > 0)
						target = targets.get(Rnd.get(targets.size()));
				}

				if(actor.isAMuted())
					return false;

				/*if(ConfigValue.ShowNpcCastSkill && target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
				{
					target.sendPacket(new ExShowScreenMessage(getActor().getName()+" use skill: "+r_skill.getName(), r_skill.getHitTime(), ScreenMessageAlign.TOP_CENTER, false));
					//target.sendPacket(new SetupGauge(getActor().getObjectId(), 1, r_skill.getHitTime(), r_skill.getHitTime()));
				}*/

				// Добавить новое задание
				Task task = new Task();
				task.type = r_skill.isOffensive() ? TaskType.CAST : TaskType.BUFF;
				if(target != null)
					task.task_target = target.getRef();
				task.skill = r_skill;
				_task_list.add(task);
				_def_think = true;
				return true;
			}
		}

		// Смена цели, если необходимо
		if(actor.isMovementDisabled() && distance > actor.getPhysicalAttackRange() + 40)
		{
			setLog("		chooseTaskAndTargets 2");
			GArray<L2Playable> targets = new GArray<L2Playable>();
			for(AggroInfo ai : actor.getAggroList())
				if(ai.attacker != null && actor.getDistance(ai.attacker) <= actor.getPhysicalAttackRange() + 40)
					targets.add(ai.attacker);
			if(targets.size() > 0)
				target = targets.get(Rnd.get(targets.size()));
		}

		// Добавить новое задание
		addTaskAttack(target);
		setLog("		chooseTaskAndTargets Finish");
		return true;
	}

	@Override
	public boolean isActive()
	{
		return _aiTask != null;
	}

	@Override
	public void clearTasks()
	{
		setLog("clearTasks");
		//if(getActor().getNpcId() == 31360)
		//	Util.test();

		_def_think = false;
		_task_list.clear();
	}

	@Override
	public void clearAttackTasks()
	{
		for(Task task : _task_list)
			if(task.type == TaskType.ATTACK)
				_task_list.remove(task);
	}

	/** переход в режим бега через определенный интервал времени */
	public void startRunningTask(long interval)
	{
		L2NpcInstance actor = getActor();
		if(actor != null && _runningTask == null && !actor.isRunning())
			_runningTask = ThreadPoolManager.getInstance().scheduleAI(new RunningTask(), interval);
	}

	@Override
	public boolean isGlobalAggro()
	{
		if(_globalAggro == 0)
			return true;
		if(_globalAggro < System.currentTimeMillis())
		{
			_globalAggro = 0;
			return true;
		}
		return false;
	}

	@Override
	public void setGlobalAggro(long value)
	{
		_globalAggro = value;
	}

	@Override
	public void AddAttackDesire(L2Character target, int arg1, int weight)
	{
		Task task = new Task();
		task.type = TaskType.ATTACK;
		if(target != null)
			task.task_target = target.getRef();
		task.weight = weight;
		_task_list.add(task);
		_def_think = true;
	}

	public void AddAttackDesireEx(int target, int arg1, int arg2, int weight)
	{
		L2Character tar = L2ObjectsStorage.getCharacter(target);
		/*Task task = new Task();
		task.type = TaskType.ATTACK;
		if(target != null)
			task.task_target = target.getRef();
		task.weight = weight;
		task.checkTarget = false;
		_task_list.add(task);
		_def_think = true;*/
		//tar.addDamageHate(getActor(), 10, 100);
		
		tar.addDamageHate(getActor(), arg1, arg2);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, tar);
	}

	@Override
	public void AddUseSkillDesire(L2Character target, L2Skill skill, int weight)
	{
		Task task = new Task();
		task.type = skill.isOffensive() ? TaskType.CAST : TaskType.BUFF;
		if(target != null)
			task.task_target = target.getRef();
		task.skill = skill;
		task.weight = weight;
		_task_list.add(task);
		_def_think = true;
	}

	public static void DebugTask(Task task)
	{
		if(task == null)
			_log.warning("NULL");
		else
		{
			System.out.print("Weight=" + task.weight);
			System.out.print("; Type=" + task.type);
			System.out.print("; Skill=" + task.skill);
			System.out.print("; Target=" + task.task_target.get());
			System.out.print("; Loc=" + task.loc);
			System.out.println();
		}
	}

	public void DebugTasks()
	{
		if(_task_list.size() == 0)
		{
			_log.warning("No Tasks");
			return;
		}

		int i = 0;
		for(Task task : _task_list)
		{
			System.out.print("Task [" + i + "]: ");
			DebugTask(task);
			i++;
		}
	}

	@Override
	public L2NpcInstance getActor()
	{
		return (L2NpcInstance) super.getActor();
	}

	protected boolean defaultThinkBuff(int rateSelf)
	{
		return defaultThinkBuff(rateSelf, 0);
	}

	protected boolean defaultThinkBuff(int rateSelf, int rateFriends)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		//TODO сделать более разумный выбор баффа, сначала выбирать подходящие а потом уже рандомно 1 из них
		if(_buff.length > 0)
		{
			if(Rnd.chance(rateSelf))
			{
				L2Skill r_skill = _buff[Rnd.get(_buff.length)];
				if(actor.getEffectList().getEffectsBySkill(r_skill) == null)
				{
					addTaskBuff(actor, r_skill);
					return true;
				}
			}

			if(Rnd.chance(rateFriends))
			{
				L2Skill r_skill = _buff[Rnd.get(_buff.length)];
				double bestDistance = 1000000;
				L2NpcInstance target = null;

				L2WorldRegion region = L2World.getRegion(actor);
				if(region != null && region.getObjectsSize() > 0)
					for(L2Object obj : region.getObjects())
						if(obj != null && obj.isNpc() && obj.getObjectId() != actor.getObjectId() && (actor.getReflection().getId() == -1 || obj.getReflection().getId() == actor.getReflection().getId()) && !((L2NpcInstance)obj).isDead() && GeoEngine.canAttacTarget(actor, obj, false) && (obj.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK || obj.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) && ((L2NpcInstance)obj).getEffectList().getEffectsBySkill(r_skill) == null)
						{
							double distance = actor.getDistance(obj);
							if(target == null || bestDistance > distance)
							{
								target = (L2NpcInstance)obj;
								bestDistance = distance;
							}
						}

				if(target != null)
				{
					addTaskBuff(target, r_skill);
					return true;
				}
			}
		}

		return false;
	}

	protected boolean defaultFightTask()
	{
		setLog("		defaultFightTask Start");
		clearTasks();
		L2Character target;
		if((target = prepareTarget()) == null)
		{
			setLog("		defaultFightTask Error 1");
			return false;
		}

		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
		{
			setLog("		defaultFightTask Error 2");
			return false;
		}

		// Если таргет это игрок, а так же на таргете весит Рейд Курсе, то мы не атакуем...
		if(target.isPlayer() && target._isInvul_skill != null && (target._isInvul_skill.getId() == 4515 || target._isInvul_skill.getId() == 4215))
		{
			if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
			{
				switchAITask(AI_TASK_ACTIVE_DELAY);
				changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
			}
			return false;
		}

		double distance = actor.getDistance(target);

		if(!actor.isAMuted() && Rnd.get(100) < getRatePHYS())
		{
			setLog("		defaultFightTask chooseTaskAndTargets 1");
			return chooseTaskAndTargets(null, target, distance);
		}

		double target_hp_precent = target.getCurrentHpPercents();
		double actor_hp_precent = actor.getCurrentHpPercents();

		// Изначально все дружественные цели живые
		double frendly_target_hp_precent = 100;
		L2MonsterInstance targetToHeal = null;
		if(actor.isMinion())
		{
			// Ищем самую дохлую дружественную цель
			L2MonsterInstance master = ((L2MinionInstance) actor).getLeader();
			if(master != null && !master.isDead() && master.isInCombat())
			{
				if(frendly_target_hp_precent > master.getCurrentHpPercents())
				{
					targetToHeal = master;
					frendly_target_hp_precent = master.getCurrentHpPercents();
				}

				MinionList list = master.getMinionList();
				if(list != null)
					for(L2MinionInstance m : list.getSpawnedMinions())
						if(m != actor && frendly_target_hp_precent > m.getCurrentHpPercents())
						{
							targetToHeal = m;
							frendly_target_hp_precent = m.getCurrentHpPercents();
						}
			}
		}

		L2Skill[] dam_skills = getRateDAM() > 0 ? selectUsableSkills(target, distance, _dam_skills) : null;
		L2Skill[] dot_skills = getRateDOT() > 0 && target_hp_precent > 10 ? selectUsableSkills(target, distance, _dot_skills) : null;
		L2Skill[] debuff_skills = getRateDEBUFF() > 0 && target_hp_precent > 10 ? selectUsableSkills(target, distance, _debuff_skills) : null;
		L2Skill[] stun = getRateSTUN() > 0 && distance < 200 ? selectUsableSkills(target, distance, _stun) : null;
		L2Skill[] heal = getRateHEAL() > 0 && (actor_hp_precent < 65 || frendly_target_hp_precent < 95) ? selectUsableSkills(actor, 0, _heal) : null;
		L2Skill[] buff = getRateBUFF() > 0 ? selectUsableSkills(actor, 0, _buff) : null;

		int chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//	_log.warning("chance getRateDAM("+getRateDAM()+"): "+chance);
		if(dam_skills != null && dam_skills.length > 0 && chance < getRateDAM())
			return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, dam_skills), target, distance);
		chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//_log.warning("chance getRateDOT("+getRateDOT()+"): "+chance);
		if(dot_skills != null && dot_skills.length > 0 && chance < getRateDOT())
			return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, dot_skills), target, distance);
		chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//_log.warning("chance getRateDEBUFF("+getRateDEBUFF()+"): "+chance);
		if(debuff_skills != null && debuff_skills.length > 0 && chance < getRateDEBUFF())
		{
			return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, debuff_skills), target, distance);
			
		}
		chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//_log.warning("chance getRateSTUN("+getRateSTUN()+"): "+chance);
		if(stun != null && stun.length > 0 && chance < getRateSTUN())
			return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, stun), target, distance);
		chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//_log.warning("chance getRateBUFF("+getRateBUFF()+"): "+chance);
		// TODO сделать баф дружественных целей
		if(buff != null && buff.length > 0 && chance < getRateBUFF())
			return chooseTaskAndTargets(selectTopSkillByBuff(actor, buff), actor, distance);
		chance = Rnd.get(100);
		//if(target.getTarget() != null && target.getTarget().getObjectId() == getActor().getObjectId())
		//_log.warning("chance getRateHEAL("+getRateHEAL()+"): "+chance);
		// TODO сделать хил дружественный целей для обычных мобов
		if(chance < getRateHEAL())
		{
			if(actor_hp_precent < frendly_target_hp_precent)
				return chooseTaskAndTargets(selectTopSkillByHeal(actor, heal), actor, distance);
			else if(actor_hp_precent > frendly_target_hp_precent)
			{
				distance = actor.getDistance(targetToHeal);
				return chooseTaskAndTargets(selectTopSkillByHeal(targetToHeal, heal), targetToHeal, distance);
			}
		}
		return chooseTaskAndTargets(null, target, distance);
	}

	public int getRatePHYS()
	{
		return 100;
	}

	public int getRateDOT()
	{
		return 0;
	}

	public int getRateDEBUFF()
	{
		return 0;
	}

	public int getRateDAM()
	{
		return 0;
	}

	public int getRateSTUN()
	{
		return 0;
	}

	public int getRateBUFF()
	{
		return 0;
	}

	public int getRateHEAL()
	{
		return 0;
	}

	public boolean getIsMobile()
	{
		if(isMobile)
			return false;
		return true;
	}

	protected int getMaxPursueRange()
	{
		return MaxPursueRange;
	}

	@Override
	public void setMaxPursueRange(int range)
	{
		MaxPursueRange = range;
	}

	public int getMaxPathfindFails()
	{
		return 3;
	}

	public int getMaxAttackTimeout()
	{
		return 20000;
	}

	public int getTeleportTimeout()
	{
		return 10000;
	}

	public void setLog(String text)
	{
		/*L2NpcInstance actor = getActor();
		if(actor != null && (actor.i_ai0 == 1994575 || actor.getNpcId() == 31360))
		{
			_log.info(text);
			Say2 cs = new Say2(0, 0, "DEBUG", text);
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player.isGM())
					player.sendPacket(cs);
		}*/
	}

	public synchronized L2NpcInstance CreateOnePrivateEx(int npc_id, String npc_ai, int arg1, int arg2, int x, int y, int z, int heading, int param1, int param2, int param3)
	{
		return CreateOnePrivateEx(npc_id, npc_ai, arg1, arg2, x, y, z, heading, param1, param2, param3, false);
	}

	public synchronized L2NpcInstance CreateOnePrivateEx(int npc_id, String npc_ai, int arg1, int arg2, int x, int y, int z, int heading, int param1, int param2, int param3, boolean myreflect)
	{
		if(npc_id > 1000000)
			npc_id -= 1000000;
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
		{
			_log.warning("CreateOnePrivateEx: Error!!! -> "+npc_id+" :"+npc_ai);
			return null;
		}
		String instance = (arg2 == 1 ? "L2Minion" : "L2Monster"); // L2Minion
		L2NpcInstance character = null;
		template.ai_type = npc_ai;
		template.setInstance(instance);

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLocx(x);
			sp.setLocy(y);
			sp.setLocz(z);
			sp.setHeading(heading);
			sp.setRespawnDelay(arg1);
			if(myreflect)
				sp.setReflection(getActor().getReflection().getId());
			if(arg1 > 0)
				sp.startRespawn();
			character = sp.doSpawn(true, arg2 == 1, param1, param2, param3, getActor());
			if(character == null)
				_log.warning("L2NpcInstance " + getActor().getNpcId() + " WTF???");
			if(arg2 == 1)
			{
				((L2MinionInstance)character).setLeader((L2MonsterInstance)getActor());
				if(((L2MonsterInstance)getActor())._minionList == null)
					((L2MonsterInstance)getActor()).setNewMinionList();
				MinionList list = ((L2MonsterInstance)getActor()).getMinionList();
				if(list != null)
					list.addSpawnedMinion((L2MinionInstance)character);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}

	/**
	 * CreateOnePrivateEx - спаунит моба который привязан к вызывающему...
	 * npc_id - ID моба которого вызываем...
	 * ai_type - AI вызываемого моба...
	 * instance - инстанс(L2Npc, L2Monster, L2Raid и т.д.) вызываемого моба...
	 * x, y, z - координаты спауна...
	 **/
	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, int x, int y, int z)
	{
		return CreateOnePrivateEx(npc_id, ai_type, "L2Monster", x, y, z, 0);
	}

	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, String instance, int x, int y, int z, long despawnTime)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
			return null;
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
			sp.stopRespawn();
			character = sp.doSpawn(true, false, 0, 0, 0, getActor());
			if(despawnTime > 0)
				ThreadPoolManager.getInstance().scheduleAI(new L2ObjectTasks.DeleteTask(character), despawnTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}

	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, String instance, String ai_param_name, int ai_param_value, int x, int y, int z, long despawnTime)
	{
		return CreateOnePrivateEx(npc_id, ai_type, instance, ai_param_name, ai_param_value, x, y, z, despawnTime, false);
	}

	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, String instance, String ai_param_name, int ai_param_value, int x, int y, int z, long despawnTime, boolean myreflect)
	{
		StatsSet npcDat = null;
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
			return null;
		
		npcDat = template.getSet();
		npcDat.set("displayId", npc_id);
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
			sp.setAIParam(ai_param_name+"="+ai_param_value);
			sp.stopRespawn();
			if(myreflect)
				sp.setReflection(getActor().getReflection().getId());

			character = sp.doSpawn(true, false, 0, 0, 0, getActor());
			if(despawnTime > 0)
				ThreadPoolManager.getInstance().scheduleAI(new L2ObjectTasks.DeleteTask(character), despawnTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}
	
	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, String instance, int loc, long despawnTime)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
			return null;
		L2NpcInstance character = null;
		template.ai_type = ai_type;
		template.setInstance(instance);

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLocation(loc);
			sp.setRespawnDelay(0);
			sp.stopRespawn();
			character = sp.doSpawn(true, false, 0, 0, 0, getActor());
			if(despawnTime > 0)
				ThreadPoolManager.getInstance().scheduleAI(new L2ObjectTasks.DeleteTask(character), despawnTime);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}
	
	public L2NpcInstance CreateOnePrivateEx(int npc_id, String ai_type, String instance, int x, int y, int z, GArray<L2Spawn> spawn)
	{
		L2NpcTemplate template = NpcTable.getTemplate(npc_id);
		if(template == null)
			return null;
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
			sp.stopRespawn();
			character = sp.doSpawn(true, false, 0, 0, 0, getActor());

			if(spawn != null)
				spawn.add(sp);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return character;
	}

	/**
	 * Отправляет игрокам в радиусе мессагу в указаной точке экрана.
	 */
	public void BroadcastOnScreenMsgStr(L2Character actor, int radius, ExShowScreenMessage.ScreenMessageAlign text_align, int time, boolean bigfont, boolean bool, int msgId)
	{
		for(L2Player player : L2World.getAroundPlayers(actor, radius, 500))
			player.sendPacket(new ExShowScreenMessage(msgId, time, text_align, true, 1, -1, bool));
	}

	// Возвращает внутриигровое время суток. Если type == 0, то результатом функции будут часы, если type == 1, то минуты, type == 2, то день\ночь.
	public int GetL2Time(int type)
	{
		int t = GameTimeController.getInstance().getGameTime();
		switch(type)
		{
			case 0:
				return t / 60;
			case 1:
				return t % 60;
			case 2:
				return GameTimeController.getInstance().isNowNight() ? 1 : 0;
			default:
				return -1;
		}
	}

	public int GetDateTime(int type1, int type2)
	{
		Calendar c = Calendar.getInstance();
		switch(type2)
		{
			case 0:
				return c.get(1); // gg::GetDateTime(0, 0) возвращает текущий год
			case 1:
				return c.get(2); // gg::GetDateTime(0, 1) возвращает текущий месяц
			case 2:
				return c.get(5); // gg::GetDateTime(0, 2) возвращает текущий день месяца
			case 3:
				return c.get(11); // gg::GetDateTime(0, 3) возвращает текущий час
			case 4:
				return c.get(12); // gg::GetDateTime(0, 4) возвращает текущую минуту
			case 5:
				return c.get(13); // gg::GetDateTime(0, 5) возвращает текущую секунду
			case 6:
				return c.get(7)+1; // gg::GetDateTime(0, 6) возвращает день недели. Понедельник - 1, пятница - 5. Выходные - другие цифры, но почему-то не 6 и 7 (хотя может я просто где-то тупил).
			default:
				return -1;
		}
	}

	public void Area_SetOnOff(String areaName, int action)
	{
		try
		{
			if(action == 1)
				ZoneManager.getInstance().getZoneByName(areaName).setActive(true);
			else
				ZoneManager.getInstance().getZoneByName(areaName).setActive(false);
		}
		catch(Exception e)
		{}
	}

	public void ShowMsgInTerritory(int unk, String ter_name, int msgId)
	{
		for(L2Player player : ZoneManager.getInstance().getZoneByName(ter_name).getInsidePlayers())
			player.sendPacket(new SystemMessage(msgId));
	}

	public void InstantTeleport(L2Character myself, int x, int y, int z)
	{
		if(myself != null)
			myself.teleToLocation(x, y, z);
	}

	public void InstantTeleportInMyTerritory(int x, int y, int z, int rnd)
	{
		for(L2Playable obj : L2World.getAroundPlayables(getActor(), MAX_AGGRO_RANGE, MAX_Z_AGGRO_RANGE))
			if(obj != null)
				obj.teleToLocation(Location.coordsRandomize(x, y, z, 0, rnd, 0)); 
	}

	public int Skill_GetEffectPoint(int skill_id)
	{
		return SkillTable.getInstance().getInfo(skill_id/65536, skill_id%65536).getEffPoint(); // getEffectPoint()
	}

	public void AddEffectActionDesire(L2Character target, int arg1, int arg2, float value)
	{
		getActor().broadcastPacket2(new SocialAction(target.getObjectId(), arg1));
	}

	public void EffectMusic(L2Character actor, int unk, String name)
	{
		actor.broadcastPacketToOthers(new PlaySound(unk, name, 1, 0, actor.getLoc()));
	}

	protected final int CanAttack(L2Character target)
	{
		if (getActor().isAlikeDead() || target.isAlikeDead() || getActor().isAttackingDisabled())
			return 0;
		float offset = getActor().getPhysicalAttackRange() + getActor().getTemplate().collisionRadius + target.getTemplate().collisionRadius;
		return getActor().isInRangeZ(target, (long)offset) && GeoEngine.canAttacTarget(getActor(), target, false) ? 1 : 0;
	}

	long _no_desair = 0;
	long _no_desair_time = 1000;
	// Короче, просто делает запрет на вызов но_десайт...
	// что означают параметры не известно.
	// Если изменить первый на любое число, то но_десайр вызываеться раз в 6с, если изменить второй параметр, то но_десайр вызываеться вообще только 1 раз.
	public void AddDoNothingDesire(int time, int value)
	{
		if(time > 0)
			_no_desair_time = 6000;
		if(value > 0)
			_no_desair_time = Integer.MAX_VALUE*1000;
	}

	@Override
	protected void onEvtSpawn()
	{
		L2NpcInstance actor = getActor();
		if(ConfigValue.SetOverAggrToRb && actor != null && (actor.isRefRaid() || actor.isEpicRaid() || actor.isBoss() || actor.isRaid()) && !Util.contains(ConfigValue.NoSetOverAggrToRb, actor.getNpcId()))
		{
			MAX_Z_AGGRO_RANGE = 1000;
			MAX_AGGRO_RANGE = 4096;
		}
	}

	public void nextAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		L2Character target = actor.getMostHated();
		if(target == null)
			return;

		if(target != null && target != actor)
		{
			actor.getAI().setAttackTarget(target);
			actor.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
			actor.getAI().addTaskAttack(target);
		}
	}
}