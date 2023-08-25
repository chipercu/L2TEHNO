package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.extensions.listeners.MethodCollection;
import l2open.extensions.listeners.PropertyCollection;
import l2open.extensions.listeners.StatsChangeListener;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.GameServer;
import l2open.gameserver.GameStart;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.ai.L2PlayableAI.nextAction;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.*;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2ObjectTasks.*;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.entity.BlockCheckerEngine;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.entity.Duel.DuelState;
import l2open.gameserver.model.entity.EventMaster;
import l2open.gameserver.model.entity.Town;
import l2open.gameserver.model.entity.vehicle.L2AirShip;
import l2open.gameserver.model.entity.vehicle.L2Ship;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.gameserver.serverpackets.FlyToLocation.FlyType;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.Calculator;
import l2open.gameserver.skills.Formulas.AttackInfo;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.stats.*;
import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.skills.triggers.TriggerType;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.taskmanager.RegenTaskManager;
import l2open.gameserver.templates.*;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;
import l2open.util.reference.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static l2open.gameserver.ai.CtrlEvent.EVT_FORGET_OBJECT;
import static l2open.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

import l2open.gameserver.listener.Listener;
import l2open.gameserver.listener.CharListenerList;

public abstract class L2Character extends L2Object
{
	protected static final Logger _log = Logger.getLogger(L2Character.class.getName());

	private static final double[] POLE_VAMPIRIC_MOD = { 1, 0.5, 0.25, 0.125, 0.06, 0.03, 0.01 };
	public static final double HEADINGS_IN_PI = 10430.378350470452724949566316381;
	public int INTERACTION_DISTANCE2 = 120;
	public int BYPASS_DISTANCE_ADD = 150;
	public int INTERACTION_DISTANCE = INTERACTION_DISTANCE2;

	/** Array containing all clients that need to be notified about hp/mp updates of the L2Character */
	private CopyOnWriteArraySet<L2Character> _statusListener;

	public ScheduledFuture<?> _skillScheduledTask;
	public int _scheduledCastCount;
	public int _scheduledCastInterval;

	public Future<?> _skillTask;
	public Future<?> _skillLaunchedTask;
	public Future<?> _stanceTask;
	public Future<?> _runPetTask;

	private long _stanceInited;

	private double _lastHpUpdate = -99999999;

	protected double _currentCp = 0;
	protected double _currentHp = 1;
	protected double _currentMp = 1;

	public long blockhpdelay = 0;
	public int param1 = 0;
	public int param2 = 0;
	public int param3 = 0;
	public int i_ai0 = 0;
	public int i_ai1 = 0;
	public int i_ai2 = 0;
	public int i_ai3 = 0;
	public int i_ai4 = 0;
	public int i_ai5 = 0;
	public long i_ai6 = 0;
	public int i_ai7 = 0;
	public int i_ai8 = 0;
	public int i_ai9 = 0;
	public int flag;
	private int npcState = 0;
	public L2Character c_ai0 = null;
	public L2Character c_ai1 = null;
	public L2Character c_ai2 = null;
	public L2Character c_ai3 = null;
	public L2Character c_ai4 = null;
	public L2Character[] c_ai5 = null;
	public String s_ai0 = "";
	public String s_ai1 = "";
	public String s_ai2 = "";
	public String s_ai3 = "";
	public int quest_last_reward_time = 0;

	protected boolean _isAttackAborted;
	public long _attackEndTime;
	protected long _attackReuseEndTime;

	protected L2Character _lastAtacker = null;

	/** HashMap(Integer, L2Skill) containing all skills of the L2Character */
	protected final ConcurrentHashMap<Integer, L2Skill> _skills = new ConcurrentHashMap<Integer, L2Skill>();

	private L2Clan _clan;
	private L2Skill _castingSkill;

	public long _castInterruptTime;
	private long _animationEndTime;

	/** Table containing all skillId that are disabled */
	protected GCSArray<Long> _disabledSkills;

	protected volatile EffectList _effectList;

	private boolean _massUpdating;

	/** Map 32 bits (0x00000000) containing all abnormal effect in progress */
	private int[] _abnormal_list = new int[3];
	// для поддержки линдвиора.
	private Set<AbnormalVisualEffect> _abnormalEffects = new CopyOnWriteArraySet<AbnormalVisualEffect>();

	private boolean _flying;
	private boolean _riding;

	private boolean _fakeDeath;
	private boolean _fishing;

	public boolean _isInvul;
	protected boolean _isPendingRevive;
	protected long _isTeleporting;
	protected boolean _overloaded;
	protected boolean _killedAlready;
	protected boolean _killedAlreadyPlayer;
	protected boolean _killedAlreadyPet;

	private long _dropDisabled;

	private volatile HashMap<Integer, Byte> _skillMastery;

	private AtomicState _isBlessedByNoblesse; // Восстанавливает все бафы после смерти
	private AtomicState _isSalvation; // Восстанавливает все бафы после смерти и полностью CP, MP, HP

	public AtomicState _disarm;

	private AtomicState _muted;
	private AtomicState _pmuted;
	private AtomicState _amuted;

	private AtomicState _paralyzed_skill;
	private AtomicState _sleeping;
	private AtomicState _stunned;
	private AtomicState _meditated;
	private AtomicState _petrification;
	private AtomicState _blocked;

	public AtomicState block_hp;
	public AtomicState block_mp;

	public AtomicState p_block_debuff;
	public AtomicState p_block_buff;

	public AtomicState p_block_act;
	public AtomicState p_block_controll;
	public AtomicState p_party_buff;

	private AtomicState p_block_move;

	public boolean _confused;
	private boolean _paralyzed;

	/**
		if(value)
			_stunned.getAndSet(true);
		else
			_stunned.setAndGet(false);
		return _stunned.get();
	**/
	private boolean _running;

	public Future<?> _moveTask;
	public Future<?> _moveWaterTask;
	public MoveNextTask _moveTaskRunnable;
	public boolean isMoving;
	public boolean isFollow;
    public boolean isPathFind;

	protected List<Location> moveList;
	protected Location destination = null;
    protected Location correction;

	/**
	 * при moveToLocation используется для хранения геокоординат в которые мы двигаемся для того что бы избежать повторного построения одного и того же пути
	 * при followToCharacter используется для хранения мировых координат в которых находилась последний раз преследуемая цель для отслеживания необходимости перестраивания пути
	 */
	protected final Location movingDestTempPos = new Location();
	public int _offset;

	protected boolean _forestalling;

	protected final List<List<Location>> _targetRecorder = new ArrayList<List<Location>>();

	protected long _followTimestamp, _startMoveTime, _arriveTime;
	protected double _previousSpeed = -1;

	private int _heading;

	private final Calculator[] _calculators;

	protected L2CharTemplate _template;
	protected L2CharTemplate _baseTemplate;
	protected L2CharacterAI _ai;

	private static final String EMPTY_STRING = new String();
	public String _name;
	public String _title;
	public boolean _showName = false;
	public boolean _showTitle = false;

	protected ConcurrentHashMap<TriggerType, Set<TriggerInfo>> _triggers;

	//private boolean _isCastOk = false;

	protected L2TrapInstance _trap;
	protected final ReentrantLock dieLock = new ReentrantLock(), statusListenerLock = new ReentrantLock(), atackLock = new ReentrantLock();

	private boolean _isRegenerating;
	private final Lock regenLock = new ReentrantLock();
	private final Lock triggerLock = new ReentrantLock();
	private Future<?> _regenTask;
	private Runnable _regenTaskRunnable;

	protected List<Location> _constructMoveList = new ArrayList<Location>();

	protected HardReference<? extends L2Character> reference;
	public List<SkillAbnormalType> _block_buff_slot;
	public List<Integer> _block_skill_id;

	private volatile HardReference<? extends L2Object> my_target = HardReferences.emptyRef();
	private volatile HardReference<? extends L2Character> my_casting_target = HardReferences.emptyRef();
	private volatile HardReference<? extends L2Character> my_follow_target = HardReferences.emptyRef();
	private volatile HardReference<? extends L2Character> my_aggression_target = HardReferences.emptyRef();

	public L2Character(int objectId, L2CharTemplate template)
	{
		this(objectId, template, true);
	}

	public L2Character(int objectId, L2CharTemplate template, boolean put_storage)
	{
		super(objectId, put_storage);

		_isBlessedByNoblesse = new AtomicState(0, objectId, "isBlessedByNoblesse", isPlayer()); // Восстанавливает все бафы после смерти
		_isSalvation = new AtomicState(0, objectId, "isSalvation", isPlayer()); // Восстанавливает все бафы после смерти и полностью CP, MP, HP
		_disarm = new AtomicState(0, objectId, "disarm", isPlayer());
		_muted = new AtomicState(0, objectId, "muted", isPlayer());
		_pmuted = new AtomicState(0, objectId, "pmuted", isPlayer());
		_amuted = new AtomicState(0, objectId, "amuted", isPlayer());
		_paralyzed_skill = new AtomicState(0, objectId, "paralyzed_skill", isPlayer());
		_sleeping = new AtomicState(0, objectId, "sleeping", isPlayer());
		_stunned = new AtomicState(0, objectId, "stunned", isPlayer());
		_meditated = new AtomicState(0, objectId, "meditated", isPlayer());
		_petrification = new AtomicState(0, objectId, "petrification", isPlayer());
		_blocked = new AtomicState(0, objectId, "blocked", isPlayer());
		block_hp = new AtomicState(0, objectId, "block_hp", isPlayer());
		block_mp = new AtomicState(0, objectId, "block_mp", isPlayer());
		p_block_debuff = new AtomicState(0, objectId, "p_block_debuff", isPlayer());
		p_block_buff = new AtomicState(0, objectId, "p_block_buff", isPlayer());
		p_block_act = new AtomicState(0, objectId, "p_block_act", isPlayer());
		p_block_controll = new AtomicState(0, objectId, "p_block_controll", isPlayer());
		p_party_buff = new AtomicState(0, objectId, "p_party_buff", isPlayer());
		p_block_move = new AtomicState(0, objectId, "p_block_move", isPlayer());

		// Set its template to the new L2Character
		_template = template;
		_baseTemplate = template;

		_calculators = new Calculator[Stats.NUM_STATS];
		if(isPlayer())
			for(Stats stat : Stats.values())
				_calculators[stat.ordinal()] = new Calculator(stat, this);

		if(template != null && (isNpc() || this instanceof L2Summon))
			if(((L2NpcTemplate) template).getSkills().size() > 0)
				for(L2Skill skill : ((L2NpcTemplate) template).getSkills().values())
					addSkill(skill);

		reference = new L2Reference<L2Character>(this);
		_moveTaskRunnable = new MoveNextTask(this);
		Formulas.addFuncsToNewCharacter(this);
		INTERACTION_DISTANCE = (int)template.collisionRadius + INTERACTION_DISTANCE2;
	}

	@Override
	public HardReference<? extends L2Character> getRef()
	{
		return reference;
	}
	
	public final void abortAttack(boolean force, boolean message)
	{
		if(isAttackingNow())
		{
			if(force)
				_isAttackAborted = true;
			getAI().setIntention(AI_INTENTION_ACTIVE);
			if(isPlayer())
			{
				sendActionFailed();
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_FAILED).addName(this));
			}
		}
	}

	public final void abortCast(boolean force)
	{
		abortCast(force, true);
	}

	public final void abortCast(boolean force, boolean set_ai_active)
	{
		if(isCastingNow() && (force || canAbortCast()))
		{
			L2Skill castingSkill = _castingSkill;
			Future skillTask = _skillTask;
			Future skillLaunchedTask = _skillLaunchedTask;

			finishFly(); 
			clearCastVars(); 
			if(skillTask != null)
			{
				skillTask.cancel(false); // cancels the skill hit scheduled tas
			}
			if(skillLaunchedTask != null)
			{
				skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task
			}
			if(castingSkill != null)
			{
				if(castingSkill.isUsingWhileCasting())
				{
					L2Character target = getAI().getAttackTarget();
					if(target != null)
						target.getEffectList().stopEffect(castingSkill.getId());
				}
				HashMap<Integer, Byte> skillMastery = _skillMastery;
				if(skillMastery != null)
					skillMastery.remove(castingSkill.getId());
			}

			broadcastSkill(new MagicSkillCanceled(_objectId), true); // broadcast packet to stop animations client-side
			if(set_ai_active)
				getAI().setIntention(AI_INTENTION_ACTIVE);

			if(isPlayer())
			{
				sendPacket(Msg.CASTING_HAS_BEEN_INTERRUPTED);
				//Util.test();
			}
		}
	}

	public final boolean canAbortCast()
	{
		return _castInterruptTime > System.currentTimeMillis();
	}

	private boolean absorbAndReflect(L2Character target, L2Skill skill, double damage, boolean bow)
	{
		int poleAttackCount=0;
		if(target.isDead())
			return false;
		//Заглушка от развода на пк.
		if(getDuel() != null)
			if(target.getDuel() != getDuel())
				return false;

		double targetHp = target.getCurrentHp();
		double targetMp = target.getCurrentMp();
		double targetCp = target.getCurrentCp();

		double value = 0;

		if(skill != null && skill.isMagic() && (!target.isPet() && !target.isSummon() || ConfigValue.p_reflect_dd_use_pet) && (ConfigValue.CanMagicReflectOverDamage || damage < targetHp+targetCp))
			value = target.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0, this, skill);
		/*else if(skill != null && skill.getCastRange() <= 200 && (!target.isPet() && !target.isSummon()))
			value = target.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0, this, skill);
		else if(skill == null && !bow && (!target.isPet() && !target.isSummon()))
			value = target.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0, this, null);*/


		//Цель отразила весь урон
		if(value > 0)
		{
			if(Rnd.chance(value))
			{
				reduceCurrentHp(damage, target, null, true, true, false, false, false, damage, true, false, false, false);
				target.sendHDmgMsg(target, this, skill, (int)damage, false, false);
				return true;
			}
			else
			{
				if(skill != null || bow)
					return false;

				// вампирик
				damage = (int) (damage - targetCp);

				if(damage <= 0)
					return false;

				final double poleMod = poleAttackCount < POLE_VAMPIRIC_MOD.length ? POLE_VAMPIRIC_MOD[poleAttackCount] : 0;
				double absorb = poleMod*calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
				if(absorb > 0 && !(target.isDoor()))
				{
					double limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100;
					if(getCurrentMp() < limit)
					{
						double mp = Math.min(damage * absorb * ConfigValue.AbsorbDamageModifier / 100, limit);
						// Нельзя восстановить больше mp, чем есть у цели.
						if(mp > targetMp)
							mp = targetMp;
						setCurrentMp(_currentMp + mp);
					}
				}		

				absorb = poleMod*calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, target, null);
				if(absorb > 0 && !target.isHealBlocked(true, true))
				{
					double limit = calcStat(Stats.p_limit_hp, null, null) * getMaxHp() / 100;
					if(getCurrentHp() < limit)
					{
						double hp = Math.min(damage * absorb * ConfigValue.AbsorbDamageModifier / 100, limit);
						// Нельзя восстановить больше hp, чем есть у цели.
						if(hp > targetHp)
							hp = targetHp;
						setCurrentHp(_currentHp + hp, false);
					}
				}
				return false;
			}
		}
		else if(skill != null && skill.isMagic())
			value = target.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0, this, skill);
		else if(skill != null && skill.getCastRange() <= 200)
			value = target.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0, this, skill);
		else if(skill == null && !bow)
			value = target.calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, this, null);

		//Цель в состоянии отразить часть урона 
		if(value > 0 && target.getCurrentHp() + target.getCurrentCp() > damage)
		{
			double dam = value / 100. * damage;
			reduceCurrentHp(dam, target, null, true, true, false, false, false, dam, true, false, false, false);
			target.sendHDmgMsg(target, this, skill, (int)dam, false, false);
		}

		if(skill != null || bow)
			return false;

		// вампирик
		damage = (int) (damage - target.getCurrentCp());

		if(damage <= 0)
			return false;

		final double poleMod = poleAttackCount < POLE_VAMPIRIC_MOD.length ? POLE_VAMPIRIC_MOD[poleAttackCount] : 0;
		double absorb = poleMod*calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
		if(absorb > 0 && !target.isDoor())
		{
			double limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100;
			if(getCurrentMp() < limit)
			{
				double mp = Math.min(damage * absorb * ConfigValue.AbsorbDamageModifier / 100, limit);
				// Нельзя восстановить больше mp, чем есть у цели.
				if(mp > targetMp)
					mp = targetMp;
				setCurrentMp(_currentMp + mp);
			}
		}		

 		absorb = poleMod*calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, target, null);
		if(absorb > 0 && !target.isHealBlocked(true, true))
		{
			double limit = calcStat(Stats.p_limit_hp, null, null) * getMaxHp() / 100;
			if(getCurrentHp() < limit)
			{
				double hp = Math.min(damage * absorb * ConfigValue.AbsorbDamageModifier / 100, limit);
				// Нельзя восстановить больше hp, чем есть у цели.
				if(hp > targetHp)
					hp = targetHp;
				setCurrentHp(_currentHp + hp, false);
			}
		}
		return false;
	}

	public L2Skill addSkill(L2Skill newSkill)
	{
		if(newSkill == null)
			return null;

		L2Skill oldSkill = _skills.get(newSkill.getId());

		if(oldSkill != null && (oldSkill.getLevel() == newSkill.getLevel() || ConfigValue.MultiProfa && oldSkill.getLevel() > newSkill.getLevel()))
			return newSkill;

		// Replace oldSkill by newSkill or Add the newSkill
		_skills.put(newSkill.getId(), newSkill);

		// If an old skill has been replaced, remove all its Func objects
		if(oldSkill != null)
		{
			removeStatsOwner(oldSkill);
			removeTriggers(oldSkill);
			if(oldSkill.isPassive())
			{
				GArray<L2Effect> effects = getEffectList().getEffectsBySkill(oldSkill);
				if(effects != null)
				{
					for(L2Effect effect : effects)
						effect.exit(false, false);
					//updateEffectIcons();
				}
			}
		}

		addTriggers(newSkill);

		// Add Func objects of newSkill to the calculator set of the L2Character
		addStatFuncs(newSkill.getStatFuncs());
		if(newSkill.isPassive() && newSkill.getEffectTemplates() != null)
		{
			for(EffectTemplate et : newSkill.getEffectTemplates())
			{
				Env env = new Env(this, this, newSkill);
				L2Effect effect = et.getEffect(env);
				if(effect == null)
					continue;
				getEffectList().addEffect(effect);
			}
		}

		return oldSkill;
	}

	public final synchronized void addStatFunc(Func f)
	{
		if(f == null)
			return;
		int stat = f._stat.ordinal();
		if(_calculators[stat] == null)
			_calculators[stat] = new Calculator(f._stat, this);
		_calculators[stat].addFunc(f);
	}

	public final synchronized void addStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			addStatFunc(f);
	}

	public void altOnMagicUseTimer(L2Character aimingTarget, L2Skill skill)
	{
		if(isAlikeDead())
			return;
		int magicId = skill.getDisplayId();
		int level = Math.max(1, getSkillDisplayLevel(skill.getId()));
		GArray<L2Character> targets = skill.getTargets(this, aimingTarget, true);
		broadcastSkill(new MagicSkillLaunched(_objectId, magicId, level, targets, skill.isOffensive()), true);
		double mpConsume2 = skill.getMpConsume2();
		if(mpConsume2 > 0)
		{
			if(_currentMp < mpConsume2)
			{
				sendPacket(Msg.NOT_ENOUGH_MP);
				return;
			}
			if(skill.isMagic())
				reduceCurrentMp(calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill), null);
			else
				reduceCurrentMp(calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill), null);
		}
		callSkill(skill, targets, false);
	}

	public void altUseSkill(L2Skill skill, L2Character target)
	{
		altUseSkill(skill, target, true);
	}

	public void altUseSkill(L2Skill skill, L2Character target, boolean isNoHandler)
	{
		if(skill == null)
			return;
		int magicId = skill.getId();
		if(isNoHandler && isSkillDisabled(ConfigValue.SkillReuseType == 0 ? magicId*65536L+skill.getLevel() : magicId))
		{
			sendReuseMessage(skill);
			return;
		}
		if(target == null)
		{
			target = skill.getAimingTarget(this, getTarget());
			if(target == null)
				return;
		}
		else if(getCurrentMp() < (skill.isMagic() ? (skill.getMpConsume1()+calcStat(Stats.MP_MAGIC_SKILL_CONSUME, skill.getMpConsume2(), target, skill)) : (skill.getMpConsume1()+calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, skill.getMpConsume2(), target, skill))))
		{
			sendPacket(Msg.NOT_ENOUGH_MP);
			return;
		}
		else if(getCurrentHp() < skill.getHpConsume() + 1)
		{
			sendPacket(Msg.NOT_ENOUGH_HP);
			return;
		}

		getListeners().onMagicUse(skill, target, true);

		if(skill.getReferenceItemId() > 0)
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceConsume()))
				return;

		int itemConsume[] = skill.getItemConsume();

		if(itemConsume[0] > 0)
			for(int i = 0; i < itemConsume.length; i++)
				if(!consumeItem(skill.getItemConsumeId()[i], itemConsume[i]))
				{
					sendPacket(Msg.INCORRECT_ITEM_COUNT);
					sendChanges(); // Мне кажется здесь это лишнее...TODO: тест
					return;
				}
		double mpConsume1 = skill.isUsingWhileCasting() ? skill.getMpConsume() : skill.getMpConsume1();
		if(mpConsume1 > 0)
			reduceCurrentMp(mpConsume1, null);

		int level = Math.max(1, getSkillDisplayLevel(magicId));
		Formulas.calcSkillMastery(skill, this);
		long reuseDelay = isNoHandler ? Formulas.calcSkillReuseDelay(this, skill) : skill.getReuseDelay();
		if(!skill.isToggle())
			broadcastSkill(new MagicSkillUse(this, target, skill.getDisplayId(), level, skill.getHitTime(), reuseDelay), true);
		// Не показывать сообщение для хербов и кубиков
		if(!(skill.getId() >= 4049 && skill.getId() <= 4055 || skill.getId() >= 4164 && skill.getId() <= 4166 || skill.getId() >= 2278 && skill.getId() <= 2285 || skill.getId() >= 2512 && skill.getId() <= 2514 || skill.getId() == 5115 || skill.getId() == 5116 || skill.getId() == 2580))
			if(!skill.isHandler())
				sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addSkillName(magicId, level));
			else
				sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(skill.getItemConsumeId()[0]));
		// Skill reuse check
		if(isNoHandler && reuseDelay > 10)
			disableSkill(skill.getId(), skill.getLevel(), reuseDelay);

		if(!isNoHandler && isInvul())
			ThreadPoolManager.getInstance().execute(new AltMagicUseTask(this, target, skill));
		else
		{
			fireMethodInvoked(MethodCollection.onStartAltCast, new Object[] { skill, target });
			ThreadPoolManager.getInstance().execute(new AltMagicUseTask(this, target, skill));
		}
	}

	public void sendReuseMessage(L2Skill skill)
	{
		if(isPet() || isSummon())
		{
			L2Player player = getPlayer();
			if(player != null && isSkillDisabled(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId()) && skill.getId() != 23237)
				player.sendPacket(new SystemMessage(SystemMessage.THAT_PET_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING));
			return;
		}
		if(!isPlayer() || isCastingNow())
			return;
		SkillTimeStamp sts = ((L2Player) this).getSkillReuseTimeStamps().get(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536+skill.getLevel() : skill.getId());
		if(sts == null || !sts.hasNotPassed())
			return;
		long timeleft = sts.getReuseCurrent();
		if(!ConfigValue.AltShowSkillReuseMessage && timeleft < 10000 || timeleft < 500)
			return;
		long hours = timeleft / 3600000;
		long minutes = (timeleft - hours * 3600000) / 60000;
		long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
		if(hours > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
		else if(minutes > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
		else
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
	}

	public void broadcastPacket(L2GameServerPacket... packets)
	{
		sendPacket(packets);
		broadcastPacketToOthers(packets);
	}

	public void broadcastPacket2(L2GameServerPacket... packets)
	{
		/*if(ConfigValue.AaaFalse)
		{
			broadcastPacket(packets);
			return;
		}*/
		sendPacket(packets);
		
		if(!isVisible() || packets.length == 0)
			return;

		for(L2Player target : L2World.getAroundPlayers(this, 3000, 300, true))
			if(target != null && _objectId != target.getObjectId())
				target.sendPacket(packets);
	}

	public void broadcastPacketToOthers(L2GameServerPacket... packets)
	{
		if(!isVisible() || packets.length == 0)
			return;
		for(L2Player target : L2World.getAroundPlayers(this))
			if(target != null && _objectId != target.getObjectId())
				target.sendPacket(packets);
	}

	public void broadcastPacketToOthers2(L2GameServerPacket... packets)
	{
		if(!isVisible() || packets.length == 0)
			return;

		for(L2Player target : L2World.getAroundPlayers(this, 3000, 300, true))
			if(target != null && _objectId != target.getObjectId())
				target.sendPacket(packets);
	}

	public void broadcastAttack(Attack packets)
	{
		if(getPlayer() != null)
			getPlayer().sendPacket(packets);

		if(!isVisible())
			return;

		for(L2Player target : L2World.getAroundPlayers(this, ConfigValue.BroadcastAttackRadius, ConfigValue.BroadcastAttackHeight+1000, true))
			if(target != null && _objectId != target.getObjectId() && (target.inObserverMode() || target.getObjectId() == packets.hits[0]._targetId || (target.show_attack_dist() > 10 && getDistance(target) <= target.show_attack_dist())))
				target.sendPacket(packets);
	}

	public void broadcastSkill(L2GameServerPacket packets)
	{
		broadcastSkill(packets, false);
	}

	public void broadcastSkill(L2GameServerPacket packets, boolean to_my)
	{
		if(to_my && getPlayer() != null)
			getPlayer().sendPacket(packets);

		if(!isVisible())
			return;

		for(L2Player target : L2World.getAroundPlayers(this, 1000))
			if(target != null && _objectId != target.getObjectId() && target.show_buff_anim_dist() > 10 && getDistance(target) <= target.show_buff_anim_dist())
				target.sendPacket(packets);
		if(isPlayable() /*&& !isPlayer()*/ && ConfigValue.EnableOlympiad && getPlayer().getOlympiadGame() != null)
			getPlayer().getOlympiadGame().broadcastPacket(packets, false, true);
	}

	public void addStatusListener(L2Character object)
	{
		if(object == this)
			return;
		statusListenerLock.lock();
		try
		{
			if(_statusListener == null)
				_statusListener = new CopyOnWriteArraySet<L2Character>();
			_statusListener.add(object);
		}
		finally
		{
			statusListenerLock.unlock();
		}
	}

	public void removeStatusListener(L2Character object)
	{
		statusListenerLock.lock();
		try
		{
			if(_statusListener == null)
				return;
			_statusListener.remove(object);
			if(_statusListener.isEmpty())
				_statusListener = null;
		}
		finally
		{
			statusListenerLock.unlock();
		}
	}

	public StatusUpdate makeStatusUpdate(int... fields)
	{
		StatusUpdate su = new StatusUpdate(getObjectId());
		for(int field : fields)
			switch(field)
			{
				case StatusUpdate.CUR_HP:
					su.addAttribute(field, (int) getCurrentHp());
					break;
				case StatusUpdate.p_max_hp:
					su.addAttribute(field, getMaxHp());
					break;
				case StatusUpdate.CUR_MP:
					su.addAttribute(field, (int) getCurrentMp());
					break;
				case StatusUpdate.p_max_mp:
					su.addAttribute(field, getMaxMp());
					break;
				case StatusUpdate.KARMA:
					su.addAttribute(field, getKarma());
					break;
				case StatusUpdate.CUR_CP:
					su.addAttribute(field, (int) getCurrentCp());
					break;
				case StatusUpdate.p_max_cp:
					su.addAttribute(field, getMaxCp());
					break;
				default:
					System.out.println("unknown StatusUpdate field: " + field);
					Thread.dumpStack();
					break;
			}
		return su;
	}

	public void broadcastStatusUpdate()
	{
		CopyOnWriteArraySet<L2Character> list = _statusListener;

		if(list == null || list.isEmpty())
			return;

		if(!needStatusUpdate())
			return;

		StatusUpdate su = makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP);

		for(L2Character temp : list)
			if(!ConfigValue.ForceStatusUpdate)
			{
				if(temp.getTarget() == this)
					temp.sendPacket(su);
			}
			else
				temp.sendPacket(su);
	}

	public int calcHeading(Location dest)
	{
		if(dest == null)
			return 0;
		if(Math.abs(getX() - dest.x) == 0 && Math.abs(getY() - dest.y) == 0)
			return _heading;
		return calcHeading(dest.x, dest.y);
	}

	public int calcHeading(int x_dest, int y_dest)
	{
		return (int) (Math.atan2(getY() - y_dest, getX() - x_dest) * HEADINGS_IN_PI) + 32768;
	}

	public final double calcStat(Stats stat, double init)
	{
		return calcStat(stat, init, null, null);
	}

	public final double calcStat(Stats stat, double init, L2Character object, L2Skill skill)
	{
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c == null)
			return init;
		Env env = new Env();
		env.character = this;
		env.target = object;
		env.skill = skill;
		env.value = init;
		c.calc(env);
		return env.value;
	}

	public final double calcStat(Stats stat, L2Character object, L2Skill skill)
	{
		if(stat == null)
			return 0;
		Env env = new Env(this, object, skill);
		stat.getInit().calc(env);
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c != null)
			c.calc(env);
		return env.value;
	}

	/**
	 * Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
	 */
	public int calculateAttackDelay()
	{
		 return (int)(ConfigValue.NextAtackDelayMod / getPAtkSpd()); // в миллисекундах поэтому 500*1000
	}

	public void callSkill(L2Skill skill, GArray<L2Character> targets, boolean useActionSkills)
	{
		try
		{
			if(useActionSkills && !skill.isUsingWhileCasting() && _triggers != null)
			{
				if(skill.isOffensive())
				{
					if(skill.isMagic())
						useTriggers(getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, skill, 0, true);
					else
						useTriggers(getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, skill, 0, true);
				}
				else if(skill.isMagic())
				{
					boolean targetSelf = skill.isAoE() || skill.isNotTargetAoE(this) || skill.getTargetType() == L2Skill.SkillTargetType.TARGET_SELF;
					useTriggers(targetSelf ? this : getTarget(), TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, skill, 0., true);
				}
			}

			if(isPlayer())
			{
				L2Player pl = (L2Player) this;
				for(L2Character target : targets)
					if(target != null && target.isNpc())
					{
						L2NpcInstance npc = (L2NpcInstance) target;
						List<QuestState> ql = pl.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL);
						if(ql != null)
							for(QuestState qs : ql)
								qs.getQuest().notifySkillUse(npc, skill, qs);
					}
			}
			if(isNpc() || isMonster())
			{
				for(L2Character target : targets)
					if(target != null && target.isNpc() || target != null && target.isMonster())
						target.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
			}

			if(skill.getNegateSkill() > 0)
				for(L2Character target : targets)
				{
					boolean update=false;
					for(L2Effect e : target.getEffectList().getAllEffects())
					{
						L2Skill efs = e.getSkill();
						if(efs.getId() == skill.getNegateSkill() && efs.isCancelable() && (skill.getNegatePower() <= 0 || efs.getPower() <= skill.getNegatePower()))
						{
							update=true;
							e.exit(false, false);
						}
					}
					if(update)
						target.updateEffectIcons();
				}

			if(skill.getCancelTarget() > 0)
				for(L2Character target : targets)
					if(target != null && skill != null && Rnd.chance(skill.getCancelTarget()))
					{
						if(target.getCastingSkill() != null && (target.getCastingSkill().getSkillType() == SkillType.TAKECASTLE || target.getCastingSkill().getSkillType() == SkillType.TAKEFORTRESS || target.getCastingSkill().getSkillType() == SkillType.TAKEFLAG))
							continue;
						if(!target.isPlayable())
						{
							target.getAI().setAttackTarget(null);
							target.stopMove(true, true);
							//target.getAI().clearTasks();
						}
						if(target.getTarget() != null)
						{
							target.stopMove(true, true);
							target.abortAttack(true, true);
							target.abortCast(true);
						}
						target.setTarget(null);
					}

			if(skill.isSkillInterrupt())
				for(L2Character target : targets)
					if(!target.isRaid() && !target.isEpicRaid())
					{
						if(target.getCastingSkill() != null && !target.getCastingSkill().isMagic())
							target.abortCast(false);
						target.abortAttack(true, true);
					}

			if(skill.isOffensive())
			{
				startAttackStanceTask();
				for(L2Character target : targets)
					target.startAttackStanceTask();
			}
			// Скилы A3 налкладывают на кастующего p_ эффекты, но если не было целей, то кастующий не получит эффект.
			//if(skill.getId() != 368 || targets.size() != 0)
			if(skill.getOperateType() != OperateType.A3 || targets.size() != 0)
				skill.getEffects(this, this, true, true);

			//new l2open.test.EffectInstance(this, targets, skill, 0);
			skill.useSkill(this, targets);
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "", e);
		}
	}

	public boolean checkReflectSkill(L2Character attacker, L2Skill skill)
	{
		if(isInvul() || attacker.isInvul() || !skill.isOffensive() || attacker.block_hp.get() || block_hp.get()) // Не отражаем, если есть неуязвимость, иначе она может отмениться
			return false;
		// Из магических скилов отражаются только скилы наносящие урон по ХП.
		if(skill.isMagic() && skill.getSkillType() != SkillType.MDAM)
			return false;
		if(Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0, attacker, skill)))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(attacker));
			attacker.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(this));
			return true;
		}
		return false;
	}

	public void doCounterAttack(L2Skill skill, L2Character attacker)
	{
		if(isDead()) // Не отражаем, если персонаж уже мёртв.
			return;
		else if(isInvul() || attacker.isInvul() || attacker.block_hp.get() || block_hp.get()) // Не отражаем, если есть неуязвимость, иначе она может отмениться
			return;
		else if(skill == null || skill.hasEffects() || skill.isMagic() || !skill.isOffensive() || skill.getCastRange() > 200)
			return;
		else if(Rnd.chance(calcStat(Stats.COUNTER_ATTACK, 0, attacker, skill)))
		{
			// Вообщим, 704 получается каким-то не понятным образом...Зависит от разници п.деф/п.атк
			double damage = (704. * getPAtk(attacker)) / Math.max(attacker.getPDef(this), 1.);
			attacker.sendPacket(new SystemMessage(SystemMessage.C1S_IS_PERFORMING_A_COUNTERATTACK).addName(this));
			sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(attacker));

			/**
			 * 1 раз отражаются: 16, 4067, 4709, 4170, 4729, 4730, 4731, 4732, 4733, 4181, 4749, 4750, 4751, 4752, 4753, 5049, 5143, 6096
			 **/
			// Заточеные на пвп скилы не дают дамаг.
			if(!skill.isPowerPvP() || ConfigValue.CounterAttackPvp)
			{
				sendHDmgMsg(this, attacker, skill, (int)damage, false, false);
				attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, damage, true, false, false, false);
			}
			/**
			 * 2 раза отражаются: 263, 344, 409, 580, 689, 928
			 **/
			// Если у нас бекстаб, то он в любом случае отражается, даже точеный на пвп, остальные блоу скилы, отражаются в двойном количестве, при условии, что они не точеные на пвп.
			if(skill.getId() == 30 || skill.getLethal1() > 0 && (!skill.isPowerPvP() || ConfigValue.CounterAttackPvp)) 
			{
				sendHDmgMsg(this, attacker, skill, (int)damage, false, false);
				attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, damage, true, false, false, false);
			}
			// Blinding Blow у нас отражается 3 раза:)
			if(skill.getId() == 321 || skill.getId() == 5084)
			{
				sendHDmgMsg(this, attacker, skill, (int)damage, false, false);
				attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, damage, true, false, false, false);
			}
		}
	}

	public final void disableDrop(int time)
	{
		_dropDisabled = System.currentTimeMillis() + time;
	}

	/**
	 * Disable this skill id for the duration of the delay in milliseconds.
	 *
	 * @param skillId
	 * @param delay (seconds * 1000)
	 */
	public void disableSkill(int skillId, int level, long delay)
	{
		if(delay > 10)
		{
			if(_disabledSkills == null)
				_disabledSkills = new GCSArray<Long>();
			_disabledSkills.add(ConfigValue.SkillReuseType == 0 ? skillId*65536L+level : skillId);
			ThreadPoolManager.getInstance().schedule(new EnableSkillTask(this, ConfigValue.SkillReuseType == 0 ? skillId*65536L+level : skillId), delay, isPlayable());
		}
	}

	public void disableItem(int handler, int handler_lvl, int itemId, int grp_id, long timeTotal, long timeLeft)
	{
		if(timeLeft > 0)
			if(!isSkillDisabled(ConfigValue.SkillReuseType == 0 ? handler*65536L+handler_lvl : handler))
				disableSkill(handler, handler_lvl, timeLeft);
	}

	public void doAttack(L2Character target, boolean force)
	{
		//if(isPlayer())
		//	_log.info("L2Character: doAttack->: [1142]");
		if(target == null || isAMuted() || isAttackingNow() || isDead() || target.isDead() || !isInRangeZ(target, 2000))
		{
			if(ConfigValue.DebugOnAction)
				_log.info("DebugOnAction: L2CHAR:doAttack->Err1");
			return;
		}
		
		getListeners().onAttack(target);

		fireMethodInvoked(MethodCollection.onStartAttack, new Object[] { this, target });

		// Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
		//int sAtk = Math.max(350000 / getPAtkSpd(), ConfigValue.MinPhisAttackSpeed);
		int sAtk = Math.max(calculateAttackDelay(), ConfigValue.MinPhisAttackSpeed);
		int ssGrade = 0;

		if(isPlayer() && target.isPlayer())
			force = !target.checkTarget(force, this);
		else if(isPlayer() && (target.isNpc() || target.isSummon() || target.isPet()) || isSummon() || isPet()) // на мобов всегда есть авто атака и у сумонов тоже она всегда...
			force = false;

		boolean notify = !force;
		L2Weapon weaponItem = getActiveWeaponItem();
		if(weaponItem != null)
		{
			if(isPlayer() && weaponItem.getAttackReuseDelay() > 0 && (getPlayer().getTransformation() == 0 || getPlayer().isTransformLalka()) && (getPlayer().getEventMaster() == null || !getPlayer().getEventMaster().attackFirst(getPlayer())))
			{
				int reuse = 0;
				if(ConfigValue.OldTimeToReuseAttack)
					reuse = (int) (weaponItem.getAttackReuseDelay() * getReuseModifier(target) * 666 * calcStat(Stats.ATK_BASE, 0, target, null) / 293. / getPAtkSpd());
				else // так по офу
					reuse = (int) (weaponItem.getAttackReuseDelay() * getReuseModifier(target) * 333 / getPAtkSpd())+sAtk;

				if(reuse > 0)
				{
					sendPacket(new SetupGauge(getObjectId(), SetupGauge.RED, reuse, reuse));
					_attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
					if(reuse > sAtk && (!force || ConfigValue.CanAutoAtackPvpOnBow))
					{
						ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT, null, null), reuse, isPlayable());
						notify=false;
					}
				}
			}

			ssGrade = weaponItem.getCrystalType().externalOrdinal;
		}

		_attackEndTime = sAtk + System.currentTimeMillis() - 20;
		_isAttackAborted = false;

		Attack attack = new Attack(this, target, getChargedSoulShot(), ssGrade);

		setHeading(Util.calculateHeadingFrom(this, target));

		// Select the type of attack to start
		boolean tranform = isPlayer() && getPlayer().isTransformed() && !getPlayer().isTransformLalka();
		if(weaponItem == null || tranform || (getPlayer() != null && getPlayer().getEventMaster() != null && getPlayer().getEventMaster().attackFirst(getPlayer())))
			doAttackHitSimple(attack, target, 1., !isPlayer() || tranform, sAtk/2, notify);
		else
			switch(weaponItem.getItemType())
			{
				case BOW:
				case CROSSBOW:
					doAttackHitByBow(attack, target, sAtk/2, notify);
					break;
				case POLE:
					doAttackHitByPole(attack, target, sAtk/2, notify);
					break;
				case DUAL:
				case DUALFIST:
				case DUALDAGGER:
					doAttackHitByDual(attack, target, sAtk/2, notify);
					break;
				default:
					doAttackHitSimple(attack, target, 1., true, sAtk/2, notify);
			}

		if(attack.hasHits())
			broadcastAttack(attack);
		if(ConfigValue.DebugOnAction && isPlayer())
			_log.info("DebugOnAction: L2CHAR:doAttack->Attack");
		//if(isPlayer())
		//	_log.info("L2Character: doAttack->: [1207]");
	}

	private static void println(String text)
	{
		_log.info("L2Character: "+text);
	}

	public boolean checkTarget(boolean force, L2Character attacker)
	{
		L2Player player = getPlayer();
		if(attacker == null || player == null || attacker == this || isDead() || attacker.isAlikeDead() || isInVehicle() || isInvisible())
			return false;

		L2Player pcAttacker = attacker.getPlayer();
		L2Clan clan1 = player.getClan();
		if(pcAttacker != null)
		{
			Duel duel1 = player.getDuel();
			Duel duel2 = pcAttacker.getDuel();
			if(player != pcAttacker && duel1 != null && duel1 == duel2)
			{
				if(duel1.getTeamForPlayer(pcAttacker) == duel1.getTeamForPlayer(player))
					return false;
				else if(duel1.getDuelState(player) != Duel.DuelState.Fighting)
					return false;
				else if(duel1.getDuelState(pcAttacker) != Duel.DuelState.Fighting)
					return false;
				return true;
			}
			else if(pcAttacker.atMutualWarWith(player) || pcAttacker.isFactionWar(player))
				return true;
			else if(player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcAttacker.getOlympiadSide())
				return true;
			else if(pcAttacker.getTeam() > 0 && player.getTeam() > 0 && player.getTeam() != pcAttacker.getTeam())
				return true;
			else if(pcAttacker.getTeam() > 0 && pcAttacker.isChecksForTeam() > 0 && player.getTeam() == 0)
				return false;
			else if(player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcAttacker.getTeam() == 0)
				return false;
			else if(player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcAttacker.getTeam() > 0 && pcAttacker.isChecksForTeam() > 0 && player.getTeam() == pcAttacker.getTeam() && (pcAttacker.isChecksForTeam() > 1 || !force))
				return false;
			else if(isInZoneBattle())
				return true;
			else if(isInZone(L2Zone.ZoneType.Siege) && attacker.isInZone(L2Zone.ZoneType.Siege))
			{
				if(player.getTerritorySiege() > -1 && player.getTerritorySiege() == pcAttacker.getTerritorySiege())
					return false;
				L2Clan clan2 = pcAttacker.getClan();
				if(clan1 == null || clan2 == null)
					return true;
				else if(clan1.getSiege() == null || clan2.getSiege() == null)
					return true;
				else if(clan1.getSiege() != clan2.getSiege())
					return true;
				else if(clan1.isDefender() && clan2.isDefender())
					return false;
				else if(clan1.getSiege().isMidVictory())
					return true;
				else if(clan1.isAttacker() && clan2.isAttacker()) 
					return false;
				return true; 
			}
			else if(ConfigValue.CanAutoAtackPvp || attacker.isBot() || attacker.getKarma() > 0 || getKarma() > 0)
				return true;
			else if(pcAttacker.getPvpFlag() != 0 || player.getPvpFlag() != 0 || force)
				return false;
		}
		return true; 
	}

	protected boolean doAttackHitSimple(Attack attack, L2Character target, double multiplier, boolean unchargeSS, int sAtk, boolean notify)
	{
		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;
		boolean miss1 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false, false, false);
			damage1 = (int) (info.damage * multiplier);
			shld1 = info.shld;
			crit1 = info.crit;
			if(getPlayer() != null)
				for (L2Cubic cubic : getPlayer().getCubics()) 
					cubic.startAttack(target);
		}
		else if(target.isPlayer() && !target.isInvul() && !target.block_hp.get())
			target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(this));
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, unchargeSS, notify, false, sAtk), sAtk, isPlayable());

		attack.addHit(target, damage1, miss1, crit1, shld1);
		//if(isPlayer())
		//	_log.info("L2Character: doAttackHitSimple->: [1300]");
		return !miss1;
	}

	protected void doAttackHitByBow(Attack attack, L2Character target, int sAtk, boolean notify)
	{
		if(getActiveWeaponItem() == null)
			return;

		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;

		// Calculate if hit is missed or not
		boolean miss1 = Formulas.calcHitMiss(this, target);

		reduceArrowCount();

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false, false, true);
			damage1 = (int) info.damage;
			shld1 = info.shld;
			crit1 = info.crit;
			if(getPlayer() != null)
				for(L2Cubic cubic : getPlayer().getCubics())
					cubic.startAttack(target);	
		}
		attack.addHit(target, damage1, miss1, crit1, shld1);
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, notify, true, sAtk), sAtk, isPlayable());
	}

	protected void doAttackHitByDual(Attack attack, L2Character target, int sAtk, boolean notify)
	{
		int damage1 = 0;
		int damage2 = 0;
		boolean shld1 = false;
		boolean shld2 = false;
		boolean crit1 = false;
		boolean crit2 = false;

		boolean miss1 = Formulas.calcHitMiss(this, target);
		boolean miss2 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false, false, false);
			damage1 = (int) info.damage;
			shld1 = info.shld;
			crit1 = info.crit;
			if(getPlayer() != null)
				for (L2Cubic cubic : getPlayer().getCubics())
				{
					cubic.startAttack(target);
				}
		}

		if(!miss2)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false, false, false);
			damage2 = (int) info.damage;
			shld2 = info.shld;
			crit2 = info.crit;
			if(getPlayer() != null)
				for (L2Cubic cubic : getPlayer().getCubics())
				{
					cubic.startAttack(target);
				}
		}

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, false, false, sAtk/2), sAtk / 2, isPlayable());
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage2, crit2, miss2, attack._soulshot, shld2, false, notify, false, sAtk), sAtk, isPlayable());

		attack.addHit(target, damage1, miss1, crit1, shld1);
		attack.addHit(target, damage2, miss2, crit2, shld2);
	}

	private void doAttackHitByPole(Attack attack, L2Character target, int sAtk, boolean notify)
	{
		int angle = (int) calcStat(Stats.POLE_ATTACK_ANGLE, 90, target, null);
		int range = (int) calcStat(Stats.POWER_ATTACK_RANGE, getTemplate().baseAtkRange, target, null);

		// Используем Math.round т.к. обычный кастинг обрезает к меньшему
		// double d = 2.95. int i = (int)d, выйдет что i = 2
		// если 1% угла или 1 дистанции не играет огромной роли, то для
		// количества целей это критично
		int attackcountmax = (int) Math.round(calcStat(Stats.POLE_TARGERT_COUNT, 3, target, null));

		if(isBoss())
			attackcountmax += 27;
		else if(isRaid())
			attackcountmax += 12;
		else if(isMonster() && getLevel() > 0)
			attackcountmax += getLevel() / 7.5;

		double mult = 1;
		int attackcount = 1;
		int poleHitCount = 0;

		if(doAttackHitSimple(attack, target, 1., true, sAtk, notify))
			poleHitCount++;

		for(L2Character t : getAroundCharacters(range, 200))
			if(attackcount < attackcountmax)
			{
				if(t != null && !t.isDead() && t.isAutoAttackable(this))
				{
					if(t == getAI().getAttackTarget() || !Util.isFacing(this, t, angle) || isPlayable() && !isPlayer() && t.isPlayable() && !isPlayer() && !target.isPlayable() && !isPlayer() && t.getKarma() <= 0) // Если цель моб, то даже флаганутых чаров пика не бьет, но ПК получают по шапке...
						continue;
					if(doAttackHitSimple(attack, t, mult, false, sAtk, false))
						poleHitCount++;
					mult *= ConfigValue.HitByPoleNextTargetMod;
					attackcount++;
				}
			}
			else
				break;

		//sendMessage("doAttackHitByPole: max_count="+attackcountmax+" attackcount="+attackcount+" poleHitCount="+poleHitCount);
	}

	public long getAnimationEndTime()
	{
		return _animationEndTime;
	}
	
	public void doCast(L2Skill skill, L2Character target, boolean forceUse)
	{
		doCast(skill, target, forceUse, false);
	}

	public void doCast(L2Skill skill, L2Character target, boolean forceUse, boolean nextAction)
	{
		if(skill == null)
		{
			sendActionFailed();
			return;
		}

		// Временная затычка, что бы мобы кастовали селфы на себя.
		if(skill.getTargetType() == SkillTargetType.TARGET_SELF)
			target = this;
		// Прерывать дуэли если цель не дуэлянт
		if(getDuel() != null)
			if(target.getDuel() != getDuel())
				getDuel().setDuelState(getPlayer(), DuelState.Interrupted);
			else if(isPlayer() && getDuel().getDuelState(getPlayer()) == DuelState.Interrupted)
			{
				sendPacket(Msg.INVALID_TARGET());
				return;
			}

		int itemConsume[] = skill.getItemConsume();

		if(itemConsume[0] > 0 && !isBot())
			for(int i = 0; i < itemConsume.length; i++)
				if(!consumeItem(skill.getItemConsumeId()[i], itemConsume[i]))
				{
					sendPacket(Msg.INCORRECT_ITEM_COUNT);
					sendChanges();// Мне кажется здесь это лишнее...TODO: тест
					return;
				}
				
		if(skill.getReferenceItemId() > 0)
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceConsume()))
				return;
				
		int magicId = skill.getId();

		if(target == null)
			target = skill.getAimingTarget(this, getTarget());
		if(target == null)
			return;

		getListeners().onMagicUse(skill, target, false);
		fireMethodInvoked(MethodCollection.onStartCast, new Object[] { skill, target, forceUse });

		if(this != target)
			setHeading(Util.calculateHeadingFrom(this, target));

		int level = Math.max(1, getSkillDisplayLevel(magicId));
		int skillTime = skill.isSkillTimePermanent() ? Math.max(skill.getHitTime(), skill.getSkillInterruptTime()) : Formulas.calcMAtkSpd(this, skill, Math.max(skill.getHitTime(), skill.getSkillInterruptTime()));
		int skillInterruptTime = skill.isSkillTimePermanent() ? skill.getSkillInterruptTime() : Formulas.calcMAtkSpd(this, skill, skill.getSkillInterruptTime());

		if(ConfigValue.OlympiadBreakCastMod && isPlayable() && getPlayer() != null && (getPlayer().isInOlympiadMode() || getPlayer().isInEvent() == 1))
			skillInterruptTime = skillTime-(int)(skillTime*ConfigValue.OlympiadBreakCastModValue);
		else
			skillInterruptTime = skillTime-Math.max(skillInterruptTime, ConfigValue.SkillsCastTimeMin);

		if(skillTime < ConfigValue.SkillsCastTimeMin)
			skillTime = ConfigValue.SkillsCastTimeMin;
		if(skillInterruptTime < 0)
			skillInterruptTime = 0;

		_animationEndTime = System.currentTimeMillis() + skillTime;

		if(skill.isMagic() && !skill.isSkillTimePermanent() && getChargedSpiritShot() > 0 && !nextAction)
		{
			skillTime = (int) (0.70 * skillTime);
			skillInterruptTime = (int) (0.70 * skillInterruptTime);
		}
		Formulas.calcSkillMastery(skill, this); // Calculate skill mastery for current cast
		long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skill));

		broadcastSkill(new MagicSkillUse(this, target, skill.getDisplayId(), level, skillTime, reuseDelay), true);

		disableSkill(skill.getId(), skill.getLevel(), reuseDelay);

		if(isPlayer())
			if(!skill.isHandler())
				sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addSkillName(magicId, level));
			else
				sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(skill.getItemConsumeId()[0]));

		if(skill.getTargetType() == SkillTargetType.TARGET_HOLY)
			target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);
		double mpConsume1 = skill.isUsingWhileCasting() ? skill.getMpConsume() : skill.getMpConsume1();
		if(mpConsume1 > 0)
		{
			if(skill.isMusic())
				mpConsume1 += getEffectList().getActiveMusicCount(0)*mpConsume1/2;
			reduceCurrentMp(mpConsume1, null);
		}
		
		_castingSkill = skill;
		_castInterruptTime = System.currentTimeMillis() + skillInterruptTime;
		setCastingTarget(target);

		if(skill.isUsingWhileCasting())
			callSkill(skill, skill.getTargets(this, target, forceUse), true);
		final boolean checks = isPlayer() && !skill.isUsingWhileCasting();
		if(isPlayer())
			sendPacket(new SetupGauge(getObjectId(), SetupGauge.BLUE, skillTime, skillTime));

		if(!isPlayer() && ConfigValue.ShowNpcCastSkill)
		{
			for(L2Player pl : L2World.getAroundPlayers(this, 1500, 50, true))
				if(pl.getTarget() != null && pl.getTarget().getObjectId() == getObjectId())
					pl.sendPacket(new ExShowScreenMessage(getName()+" use skill: "+skill.getName(), skill.getHitTime(), ScreenMessageAlign.TOP_CENTER, false));
			//target.sendPacket(new SetupGauge(getActor().getObjectId(), 1, skill.getHitTime(), skill.getHitTime()));
		}

		_scheduledCastCount = skill.getCastCount();
		_scheduledCastInterval = skill.getCastCount() > 0 ? skillTime / _scheduledCastCount : skillTime;
		// Create a task MagicUseTask with Medium priority to launch the MagicSkill at the end of the casting time
		_skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, skill, target, forceUse), skillInterruptTime, isPlayable());
		_skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, skill, forceUse), skill.getCastCount() > 0 ? skillTime / skill.getCastCount() : skillTime);
		//if(_isCastOk)
		//	onCastEndTime();
		/*if(isPlayer())
		{
			_log.info("skillTime="+skillTime+" skillInterruptTime="+skillInterruptTime);
			_log.info("skillTime="+skillTime+" skillInterruptTime="+(skillTime-Formulas.calcMAtkSpd(this, skill, skill.getSkillInterruptTime())));
			_log.info("ClearskillTime="+skill.getHitTime()+" getSkillInterruptTime="+(skill.getHitTime()-skill.getSkillInterruptTime()));
		}*/
	}
	private Location _flyLoc;

	public void setFlyLoc(Location loc)
	{
		_flyLoc = loc;
	}
	
	public Location getFlyLocation(L2Object target, L2Skill skill)
	{
		if(target != null && target != this)
		{
			Location loc;

			double radian = Util.convertHeadingToRadian(target.getHeading());
			if(skill.isFlyToBack())
				//loc = new Location(target.getX() + (int) (Math.sin(radian) * 40), target.getY() - (int) (Math.cos(radian) * 40), target.getZ());
				loc = GeoEngine.moveCheck(getX(), getY(), getZ(), target.getX() + (int) (Math.sin(radian) * 40), target.getY() - (int) (Math.cos(radian) * 40), getReflection().getGeoIndex());
			else
				loc = GeoEngine.moveCheck(getX(), getY(), getZ(), target.getX() - (int) (Math.sin(radian) * 40), target.getY() + (int) (Math.cos(radian) * 40), getReflection().getGeoIndex());
				//loc = new Location(target.getX() - (int) (Math.sin(radian) * 40), target.getY() + (int) (Math.cos(radian) * 40), target.getZ());

			if(isFlying())
			{
				if(isPlayer() && ((L2Player) this).isInFlyingTransform() && (loc.z <= 0 || loc.z >= 6000))
					return null;
				if(GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getColRadius(), getReflection().getGeoIndex()) == null)
					return null;
			}
			else
			{
				loc.correctGeoZ();

				if(skill.getFlyType() == FlyType.CHARGE && !GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getReflection().getGeoIndex()))
				{
					loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
					if(!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getReflection().getGeoIndex()))
						return null;
				}
			}

			return loc;
		}

		double radian = Util.convertHeadingToRadian(getHeading());
		int x1 = -(int) (Math.sin(radian) * skill.getFlyRadius());
		int y1 = (int) (Math.cos(radian) * skill.getFlyRadius());

		if(isFlying())
			return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ(), getColRadius(), getReflection().getGeoIndex());
		return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, true, getReflection().getGeoIndex());
	}
	/*public Location getFlyLocation(L2Object target, L2Skill skill)
	{
		if(target != null && target != this)
		{
			Location loc;

			double radian = Util.convertHeadingToRadian(target.getHeading());
			if(skill.isFlyToBack())
				loc = GeoEngine.moveCheck(getX(), getY(), getZ(), target.getX() + (int) (Math.sin(radian) * 40), target.getY() - (int) (Math.cos(radian) * 40), getReflection().getGeoIndex());
			else
			{
				loc = GeoEngine.moveCheck(getX(), getY(), getZ(), target.getX(), target.getY(), getReflection().getGeoIndex());
				loc = applyOffset(loc, 40);
			}

			if(isFlying())
			{
				//if(isPlayer())
				//	sendMessage("getFlyLocation: isFlying");
				if(isPlayer() && ((L2Player) this).isInFlyingTransform() && (loc.z <= 0 || loc.z >= 6000))
					return null;
				if(GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getColRadius(), getReflection().getGeoIndex()) == null)
					return null;
			}
			else
			{
				//if(isPlayer())
				//	sendMessage("getFlyLocation: canMoveToCoord="+GeoEngine.canMoveToCoord(getX(), getY(), getZ(), target.getX(), target.getY(), target.getZ(), getReflection().getGeoIndex()));
				loc.correctGeoZ();
				//if(skill.getFlyType() == FlyType.CHARGE && !GeoEngine.canMoveToCoord(getX(), getY(), getZ(), target.getX(), target.getY(), target.getZ(), getReflection().getGeoIndex()))
				//	return null;
			}

			return loc;
		}

		double radian = Util.convertHeadingToRadian(getHeading());
		int x1 = -(int) (Math.sin(radian) * skill.getFlyRadius());
		int y1 = (int) (Math.cos(radian) * skill.getFlyRadius());

		if(isFlying())
			return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ(), getColRadius(), getReflection().getGeoIndex());
		return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, getReflection().getGeoIndex());
	}*/

	public void doDie(L2Character killer)
	{
		// killing is only possible one time
		dieLock.lock();
		try
		{
			if(_killedAlready)
				return;
			_killedAlready = true;
		}
		finally
		{
			dieLock.unlock();
		}

		// TODO: мб убрать?
		fireMethodInvoked(MethodCollection.doDie, new Object[] { killer });

		if(killer != null)
		{
			L2Player killerPlayer = killer.getPlayer();
			if(killerPlayer != null)
				killerPlayer.getListeners().onKillIgnorePetOrSummon(this);

			killer.getListeners().onKill(this);

			if(isPlayer() && killer.isPlayable())
				_currentCp = 0;
		}

		setTarget(null);
		stopMove();
		stopRegeneration();

		_currentHp = 0;

		if(getEventMaster() != null)
			getEventMaster().doDie(this, killer);

		// Stop all active skills effects in progress on the L2Character
		setMassUpdating(true);
		if(isBlessedByNoblesse() || isSalvation())
		{
			if(isPlayer())
			{
				if(isSalvation() && !getPlayer().isInOlympiadMode())
					getPlayer().reviveRequest(getPlayer(), 100, false);
				else if(isBlessedByNoblesse())
					_blessed = true;
				for(L2Effect e : getEffectList().getAllEffects())
					// Noblesse Blessing Buff/debuff effects are retained after
					// death. However, Noblesse Blessing and Lucky Charm are lost as normal.
					if(e.getEffectType() == EffectType.BlessNoblesse || e.getSkill().getId() == L2Skill.SKILL_FORTUNE_OF_NOBLESSE || e.getSkill().getId() == L2Skill.SKILL_RAID_BLESSING)
						e.exit(true, false);
					else if(e.getEffectType() == EffectType.AgathionResurrect)
					{
						if(isPlayer())
							getPlayer().setAgathionRes(true);
						e.exit(true, false);
					}
			}
			else
			{
				if(isBlessedByNoblesse())
					_blessed = true;
				for(L2Effect e : getEffectList().getAllEffects())
					if(e.getEffectType() == EffectType.BlessNoblesse || e.getSkill().getId() == L2Skill.SKILL_FORTUNE_OF_NOBLESSE || e.getSkill().getId() == L2Skill.SKILL_RAID_BLESSING || e.getEffectType() == EffectType.Salvation)
						e.exit(true, false);
			}
		}
		else
		{
			for(L2Effect e : getEffectList().getAllEffects())
				// Battlefield Death Syndrome при смерти не слетают.
				// Charm of Courage тоже, он удаляется позже
				if(e.getEffectType() == EffectType.Transformation && e.getSkill().getAbnormalTime() != -1000)
				{
					// TODO: !!! Трансформа должна уходить сразу, но пакет о инфе чара, через определенное время...
					ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							getEffectList().stopAllSkillEffects(EffectType.Transformation);
						}
					}, (e.getSkill().getId() == 9159 || e.getSkill().getId() == 664 || e.getSkill().getId() == 22205) ? 10800 : 3000);
				}
				else if(e.getSkill().getId() != L2Skill.SKILL_CHARM_OF_COURAGE && e.getSkill().getId() != L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME && e.getSkill().getAbnormalTime() != -1000)
					e.exit(false, false);
		}
		updateEffectIcons();

		setMassUpdating(false);

		Object[] script_args = new Object[] { this, killer };
		for(ScriptClassAndMethod handler : Scripts.onDie)
			callScripts(handler.scriptClass, handler.method, script_args);

		getListeners().onDeath(killer);

		ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer, null));

		// нах???
		updateEffectIcons();
		updateStats();
		broadcastStatusUpdate();
	}

	/** Sets HP, MP and CP and revives the L2Character. */
	public void doRevive()
	{
		if(!isTeleporting())
		{
			setIsPendingRevive(false);
			setNonAggroTime(System.currentTimeMillis() + 15000);

			if(isSalvation())
			{
				for(L2Effect e : getEffectList().getAllEffects())
					if(e.getEffectType() == EffectType.Salvation)
					{
						e.exit(true, false);
						break;
					}
				setCurrentCp(getMaxCp());
				setCurrentHp(getMaxHp(), true);
				setCurrentMp(getMaxMp());
			}
			else
			{
				if(isPlayer() && ConfigValue.RespawnRestoreCP >= 0)
					setCurrentCp(getMaxCp() * (ConfigValue.RespawnRestoreCP/100));

				setCurrentHp(Math.max(1, getMaxHp() * (ConfigValue.RespawnRestoreHP/100)), true);

				if(ConfigValue.RespawnRestoreMP >= 0)
					setCurrentMp(getMaxMp() * (ConfigValue.RespawnRestoreMP/100));
			}

			broadcastPacket(new Revive(this));
			broadcastUserInfo(true);
			if(getEventMaster() != null)
				getEventMaster().doRevive(this);
		}
		else
			setIsPendingRevive(true);
	}

	public void enableSkill(Long skillId)
	{
		if(_disabledSkills == null)
			return;
		_disabledSkills.remove(skillId);
	}

	public AbnormalVisualEffect[] getAbnormalEffectsArray()
	{
		return _abnormalEffects.toArray(new AbnormalVisualEffect[_abnormalEffects.size()]);
	}

	public int[] getAbnormalEffectList()
	{
		return _abnormal_list;
	}
	/**
	 * Return a map of 32 bits (0x00000000) containing all abnormal effects
	 */
	public int getAbnormalEffect()
	{
		return _abnormal_list[0];
	}

	/**
	 * Return a map of 32 bits (0x00000000) containing all special effects
	 */
	public int getAbnormalEffect2()
	{
		return _abnormal_list[1];
	}

	public int getAbnormalEffect3()
	{
		return _abnormal_list[2];
	}

	public int getAccuracy()
	{
		return (int) (calcStat(Stats.p_hit, 0, null, null) / getWeaponExpertisePenalty());
	}

	/**
	 * Возвращает тип атакующего элемента и его силу.
	 * @return массив, в котором:
	 * <li>[0]: тип элемента,
	 * <li>[1]: его сила
	 */
	public int[] getAttackElement()
	{
		return Formulas.calcAttackElement(this);
	}

	public int[] getAttackElementValue()
	{
		if(getActiveWeaponInstance() == null || getActiveWeaponInstance().getAttackElement() == -2)
			return new int[]{-2, 0};
		return new int[]{getActiveWeaponInstance().getAttackElement(), getActiveWeaponInstance().getAttackElementValue()};
	}

	/**
	 * Возвращает защиту от элемента: огонь.
	 * @return значение защиты
	 */
	public int getDefenceFire()
	{
		return (int) -calcStat(Stats.FIRE_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает защиту от элемента: вода.
	 * @return значение защиты
	 */
	public int getDefenceWater()
	{
		return (int) -calcStat(Stats.WATER_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает защиту от элемента: воздух.
	 * @return значение защиты
	 */
	public int getDefenceWind()
	{
		return (int) -calcStat(Stats.WIND_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает защиту от элемента: земля.
	 * @return значение защиты
	 */
	public int getDefenceEarth()
	{
		return (int) -calcStat(Stats.EARTH_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает защиту от элемента: свет.
	 * @return значение защиты
	 */
	public int getDefenceHoly()
	{
		return (int) -calcStat(Stats.SACRED_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает защиту от элемента: тьма.
	 * @return значение защиты
	 */
	public int getDefenceUnholy()
	{
		return (int) -calcStat(Stats.UNHOLY_RECEPTIVE, 0, null, null);
	}

	/**
	 * Возвращает коллекцию скиллов для быстрого перебора
	 */
	public Collection<L2Skill> getAllSkills()
	{
		return _skills.values();
	}

	/**
	 * Возвращает массив скиллов для безопасного перебора
	 */
	public final L2Skill[] getAllSkillsArray()
	{
		Collection<L2Skill> vals = _skills.values();
		return vals.toArray(new L2Skill[vals.size()]);
	}

	public float getArmourExpertisePenalty()
	{
		return 1.f;
	}

	public final float getAttackSpeedMultiplier()
	{
		return (float) (1.1 * getPAtkSpd() / getTemplate().basePAtkSpd);
	}

	public int getBuffLimit()
	{
		return (int) calcStat(Stats.BUFF_LIMIT, ConfigValue.BuffLimit, null, null);
	}
	
	public int getSongLimit()
	{
		return (int) calcStat(Stats.SONG_LIMIT, ConfigValue.SongLimit, null, null);
	}

	public L2Skill getCastingSkill()
	{
		return _castingSkill;
	}

	public final L2Character getCharTarget()
	{
		L2Object target = getTarget();
		if(target == null || !target.isCharacter())
			return null;
		return (L2Character) target;
	}

	public byte getCON()
	{
		return (byte) calcStat(Stats.STAT_CON, _template.baseCON, null, null);
	}

	/**
	 * Возвращает шанс физического крита (1000 == 100%)
	 */
	public int getCriticalHit(L2Character target, L2Skill skill)
	{
		if(isPlayer())
			return (int) calcStat(Stats.CRITICAL_BASE, getCritRate(), target, skill);
		return (int) calcStat(Stats.CRITICAL_BASE, _template.baseCritRate, target, skill);
	}

	public int getCritRate()
	{
		//Formulas.DEXbonus[getDEX()];
		if(getActiveWeaponItem() == null)
			return 40;
		return getActiveWeaponItem().getCritical();
	}
	/**
	 * Возвращает шанс магического крита в процентах
	 */
	public double getMagicCriticalRate(L2Character target, L2Skill skill)
	{
		return calcStat(Stats.MCRITICAL_RATE, target, skill);
	}
	
	public double getCriticalRate(L2Character target, L2Skill skill)
	{
		return calcStat(Stats.CRITICAL_RATE, target, skill);
	}
	
	public double getCriticalDamage(L2Character target, L2Skill skill)
	{
		return calcStat(Stats.CRITICAL_DAMAGE, target, skill);
	}

	/**
	 * Return the current CP of the L2Character.
	 * 
	 */
	public final double getCurrentCp()
	{
		return _currentCp;
	}

	public final double getCurrentCpRatio()
	{
		return getCurrentCp() / getMaxCp();
	}

	public final double getCurrentCpPercents()
	{
		return getCurrentCpRatio() * 100f;
	}

	public final boolean isCurrentCpFull()
	{
		return getCurrentCp() >= getMaxCp();
	}

	public final boolean isCurrentCpZero()
	{
		return getCurrentCp() < 1;
	}

	public final double getCurrentHp()
	{
		return _currentHp;
	}

	public double getHpReg()
	{
		return 1;
	}

	public double getMpReg()
	{
		return 1;
	}

	public final double getCurrentHpRatio()
	{
		return getCurrentHp() / getMaxHp();
	}

	public final double getCurrentHpPercents()
	{
		return getCurrentHpRatio() * 100f;
	}

	public final boolean isCurrentHpFull()
	{
		return getCurrentHp() >= getMaxHp();
	}

	public final boolean isCurrentHpZero()
	{
		return getCurrentHp() < 1;
	}

	public final double getCurrentMp()
	{
		return _currentMp;
	}

	public final double getCurrentMpRatio()
	{
		return getCurrentMp() / getMaxMp();
	}

	public final double getCurrentMpPercents()
	{
		return getCurrentMpRatio() * 100f;
	}

	public final boolean isCurrentMpFull()
	{
		return getCurrentMp() >= getMaxMp();
	}

	public final boolean isCurrentMpZero()
	{
		return getCurrentMp() < 1;
	}

	public Location getDestination()
	{
		return destination;
	}

	public byte getDEX()
	{
		return (byte) calcStat(Stats.STAT_DEX, _template.baseDEX, null, null);
	}

	public int getEvasionRate(L2Character target)
	{
		return (int) (calcStat(Stats.EVASION_RATE, 0, target, null) / getArmourExpertisePenalty());
	}

	/**
	 * If <b>boolean toChar is true heading calcs this->target, else target->this.
	 */
	public int getHeadingTo(L2Object target, boolean toChar)
	{
		if(target == null || target == this)
			return -1;

		int dx = target.getX() - getX();
		int dy = target.getY() - getY();
		int heading = (int) (Math.atan2(-dy, -dx) * HEADINGS_IN_PI + 32768);

		heading = toChar ? target.getHeading() - heading : getHeading() - heading;

		if(heading < 0)
			heading = heading + 1 + Integer.MAX_VALUE & 0xFFFF;
		else if(heading > 0xFFFF)
			heading &= 0xFFFF;

		return heading;
	}

	public byte getINT()
	{
		return (byte) calcStat(Stats.STAT_INT, _template.baseINT, null, null);
	}

	public GArray<L2Character> getAroundCharacters(int radius, int height)
	{
		if(!isVisible())
			return new GArray<L2Character>(0);
		return L2World.getAroundCharacters(this, radius, height);
	}

	public GArray<L2NpcInstance> getAroundNpc(int range, int height)
	{
		if(!isVisible())
			return new GArray<L2NpcInstance>(0);
		return L2World.getAroundNpc(this, range, height);
	}

	public boolean knowsObject(L2Object obj)
	{
		return L2World.getAroundObjectById(this, obj.getObjectId()) != null;
	}

	public final L2Skill getKnownSkill(int skillId)
	{
		return _skills.get(skillId);
	}

	public final int getMagicalAttackRange(L2Skill skill)
	{
		if(skill != null)
			return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
		return getTemplate().baseAtkRange;
	}

	public int getMAtk(L2Character target, L2Skill skill)
	{
		if(skill != null && skill.getMatak() > 0)
			return skill.getMatak();
		return (int) calcStat(Stats.p_magical_attack, _template.baseMAtk, target, skill);
	}

	public double getMAtkSpd()
	{
		return (calcStat(Stats.p_magic_speed, _template.baseMAtkSpd, null, null) / getArmourExpertisePenalty());
	}

	public int getMaxCp()
	{
		return (int) calcStat(Stats.p_max_cp, _template.baseCpMax, null, null);
	}

	public int getMaxHp()
	{
		return (int) calcStat(Stats.p_max_hp, _template.baseHpMax, null, null);
	}

	public int getMaxMp()
	{
		return (int) calcStat(Stats.p_max_mp, _template.baseMpMax, null, null);
	}

	public int getMDef(L2Character target, L2Skill skill)
	{
		return Math.max((int) calcStat(Stats.p_magical_defence, _template.baseMDef, target, skill), 1);
	}

	public byte getMEN()
	{
		return (byte) calcStat(Stats.STAT_MEN, _template.baseMEN, null, null);
	}

	public float getMinDistance(L2Object obj)
	{
		float distance = getTemplate().collisionRadius;

		if(obj != null && obj.isCharacter())
			distance += ((L2Character) obj).getTemplate().collisionRadius;

		return distance;
	}

	public float getMovementSpeedMultiplier()
	{
		return getRunSpeed() * 1f / _template.baseRunSpd;
	}

	@Override
	public float getMoveSpeed()
	{
		if(isRunning())
			return getRunSpeed();

		return getWalkSpeed();
	}

	@Override
	public String getName()
	{
		return _name == null ? EMPTY_STRING : _name;
	}

	public int getPAtk(L2Character target)
	{
		return (int) calcStat(Stats.p_physical_attack, _template.basePAtk, target, null);
	}

	public double getPAtkSpd()
	{
		return Math.max(1, (calcStat(Stats.p_attack_speed, _template.basePAtkSpd, null, null) / getArmourExpertisePenalty()));
	}

	public int getPDef(L2Character target)
	{
		return (int) calcStat(Stats.p_physical_defence, _template.basePDef, target, null);
	}

	public final int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, getTemplate().baseAtkRange, null, null);
	}

	public final int getRandomDamage()
	{
		if(ConfigValue.DisableRndDamage)
			return 0;
		L2Weapon weaponItem = getActiveWeaponItem();
		if(weaponItem == null)
			return 5 + (int) Math.sqrt(getLevel());
		return weaponItem.getRandomDamage();
	}

	public double getReuseModifier(L2Character target)
	{
		return calcStat(Stats.ATK_REUSE, 1, target, null);
	}

	public int getRunSpeed()
	{
		return getSpeed(_template.baseRunSpd);
	}

	public final double getMoveMultiplier()
	{
		return 1.100000023841858; // Formulas.DEXbonus[_template.baseDEX];
	}

	public final int getShldDef()
	{
		if(isPlayer())
			return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
		return (int) calcStat(Stats.SHIELD_DEFENCE, _template.baseShldDef, null, null);
	}

	public final short getSkillDisplayLevel(Integer skillId)
	{
		L2Skill skill = _skills.get(skillId);
		if(skill == null)
			return -1;
		return skill.getDisplayLevel();
	}

	public final short getSkillLevel(Integer skillId)
	{
		L2Skill skill = _skills.get(skillId);
		if(skill == null)
			return -1;
		return skill.getLevel();
	}

	public byte getSkillMastery(Integer skillId)
	{
		if(_skillMastery == null)
			return 0;
		Byte val = _skillMastery.get(skillId);
		return val == null ? 0 : val;
	}

	public void removeSkillMastery(Integer skillId)
	{
		if(_skillMastery != null)
			_skillMastery.remove(skillId);
	}

	public final GArray<L2Skill> getSkillsByType(SkillType type)
	{
		GArray<L2Skill> result = new GArray<L2Skill>();
		for(L2Skill sk : _skills.values())
			if(sk.getSkillType() == type)
				result.add(sk);
		return result;
	}

	public int getSpeed(int baseSpeed)
	{
		if(isInWater())
			return getSwimSpeed();
		return (int) (calcStat(Stats.p_speed, baseSpeed, null, null) / getArmourExpertisePenalty());
	}

	public byte getSTR()
	{
		return (byte) calcStat(Stats.STAT_STR, _template.baseSTR, null, null);
	}

	public int getSwimSpeed()
	{
		return (int) calcStat(Stats.p_speed, ConfigValue.SwimingSpeedTemplate, null, null);
	}

	public L2Object getTarget()
	{
		return my_target.get();
	}

	public final int getTargetId()
	{
		L2Object target = getTarget();
		return target == null ? -1 : target.getObjectId();
	}

	public L2CharTemplate getTemplate()
	{
		return _template;
	}

	public L2CharTemplate getBaseTemplate()
	{
		return _baseTemplate;
	}

	public String getTitle()
	{
		if(_title != null && _title.length() > 16)
			return _title.substring(0, 16);
		return _title;
	}
	
	public int getWalkSpeed()
	{
		if(isInWater())
			return getSwimSpeed();
		return getSpeed(_template.baseWalkSpd);
	}

	public float getWeaponExpertisePenalty()
	{
		return 1.f;
	}

	public byte getWIT()
	{
		return (byte) calcStat(Stats.STAT_WIT, _template.baseWIT, null, null);
	}

	public double headingToRadians(int heading)
	{
		return (heading - 32768) / HEADINGS_IN_PI;
	}

	public final boolean isAlikeDead()
	{
		return _fakeDeath || _currentHp < 0.5;
	}

	public boolean isAttackAborted()
	{
		return _isAttackAborted;
	}

	// Остановился тут!!!Поставить false и смотреть где дальше блочится атака!!!
	public final boolean isAttackingNow()
	{
		return _attackEndTime > System.currentTimeMillis();
	}

	public boolean isBehindTarget()
	{
		if(getTarget() != null && getTarget().isCharacter())
		{
			int head = getHeadingTo(getTarget(), true);
			return head != -1 && (head <= 10430 || head >= 55105);
		}
		return false;
	}

	public boolean isToSideOfTarget()
	{
		if(getTarget() != null && getTarget().isCharacter())
		{
			int head = getHeadingTo(getTarget(), true);
			return head != -1 && (head <= 22337 || head >= 43197);
		}
		return false;
	}

	public boolean isToSideOfTarget(L2Object target)
	{
		if(target != null && target.isCharacter())
		{
			int head = getHeadingTo(target, true);
			return head != -1 && (head <= 22337 || head >= 43197);
		}
		return false;
	}

	public boolean isBehindTarget(L2Object target)
	{
		if(target != null && target.isCharacter())
		{
			int head = getHeadingTo(target, true);
			return head != -1 && (head <= 10430 || head >= 55105);
		}
		return false;
	}

	public final boolean isBlessedByNoblesse()
	{
		return _isBlessedByNoblesse.get();
	}

	public final boolean isSalvation()
	{
		return _isSalvation.get();
	}

	public boolean isDead()
	{
		return _currentHp < 0.5;
	}

	public final boolean isDropDisabled()
	{
		return _dropDisabled > System.currentTimeMillis();
	}

	@Override
	public final boolean isFlying()
	{
		return _flying;
	}

	public final boolean isInCombat()
	{
		return _stanceTask != null;
	}

	public boolean isInvul()
	{
		if(ConfigValue.InvullOnlyTest && isPlayable())
			return getEffectList().getEffectByType(EffectType.Petrification) != null;
		return _isInvul || _isInvul_skill != null;
	}

	/** Отображение значка клана у НПЦ */
	public boolean isCrestEnable()
	{
		return true;
	}

	public boolean isMageClass()
	{
		return getTemplate().basePAtk == 3;
	}

	public final boolean isRiding()
	{
		return _riding;
	}

	public final boolean isRunning()
	{
		return _running;
	}

	public boolean isSkillDisabled(L2Skill skill)
	{
		return _disabledSkills != null && _disabledSkills.contains(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId());
	}

	public boolean isSkillDisabled(Long skillId)
	{
		return _disabledSkills != null && _disabledSkills.contains(skillId);
	}

	public final boolean isTeleporting()
	{
		return _isTeleporting > System.currentTimeMillis();
	}

	/**
	 * Возвращает позицию цели, в которой она будет через пол секунды.
	 */
	/*public Location getIntersectionPoint(L2Character target)
	{
		if(!Util.isFacing(this, target, 90))
			return new Location(target.getX(), target.getY(), target.getZ());
		double angle = Util.convertHeadingToDegree(target.getHeading()); // угол в градусах
		double radian = Math.toRadians(angle - 90); // угол в радианах
		double range = target.getMoveSpeed() / 2; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
		if(isPlayable() && !isPlayer() && !isPlayer()) // Ебать, че за хуйню я сделал?
			return new Location((int) (target.getX() - range * Math.sin(radian)) + Rnd.get(-40, 40), (int) (target.getY() + range * Math.cos(radian)) + Rnd.get(-40, 40), target.getZ());
		return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
	}

	public Location applyOffset2(Location point, int offset)
	{
		if(offset <= 0)
		{
			//if(!isFlying() && !isInVehicle() && !isSwimming() && !isVehicle())
			//	point.correctGeoZ();
			return point;
		}
		Location current = getLoc();

		double distance = current.distance(point);

		double cos = (point.x - current.x) / distance;
		double sin = (point.y - current.y) / distance;

		point.x = current.x + (int)((distance - offset + 10) * cos);
		point.y = current.y + (int)((distance - offset + 10) * sin);

		if(!isFlying() && !isInVehicle() && !isSwimming() && !isVehicle())
			point.correctGeoZ();

		return point;
	}
	public Location applyOffset(Location point, int offset)
	{
		if(offset <= 0)
		{
			//if(!isFlying() && !isInVehicle() && !isSwimming() && !isVehicle())
			//	point.correctGeoZ();
			return point;
		}

		long dx = point.x - getX();
		long dy = point.y - getY();
		long dz = point.z - getZ();

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if(distance <= offset)
		{
			point.set(getX(), getY(), getZ());
			return point;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;

			// возможно ceil
			point.x -= (int) Math.round(dx * cut);
			point.y -= (int) Math.round(dy * cut);
			point.z -= (int) Math.round(dz * cut);

			if(!isFlying() && !isInVehicle() && !isSwimming() && !isVehicle())
				point.correctGeoZ();
		}

		return point;
	}

	public void setNewPath(Location dest)
	{
		_move_data._targetRecorder.clear();
		_move_data._targetRecorder.add(dest);
	}

	public void setNewPath(List<Location> dest)
	{
		_move_data._targetRecorder.clear();
		_move_data._targetRecorder.addAll(dest);
	}

	public boolean buildPathToNew(int dest_x, int dest_y, int dest_z, int offset, boolean pathFind, boolean follow)
	{
		int geoIndex = getReflection().getGeoIndex();
		Location dest;

		**
		* 1. @_move_data._forestalling если можно менять направление.
		* 2. @ isFollow если мы в режиме следования
		* 3. @getFollowTarget().isMoving если наша цель передвигается
		* --------------------------------
		* Берем координаты, в которых будет наша цель через пол секунды, возможно лучше брать следующую точку из мове листа цели.
		**
		//if(_move_data._forestalling && isFollow && getFollowTarget() != null && getFollowTarget().isMoving)
		//	dest = getIntersectionPoint(getFollowTarget());
		//else if(isPlayable() && !isPlayer() && !isPlayer()) // для петов/суммонов идет разброс, хуйню я тут если честно сделал, но уже не помню для чего так что пускай пока побудет так...
		//	dest = new Location(dest_x+Rnd.get(-40, 40), dest_y+Rnd.get(-40, 40), dest_z);
		//else
			dest = new Location(dest_x, dest_y, dest_z);

		//if(isPlayer())
		//	_log.info("dest_z1="+dest.z);
		applyOffset(dest, offset);
		//if(isPlayer())
		//	_log.info("dest_z2="+dest.z);
		if(isInVehicle() || isVehicle() || !ConfigValue.GeodataEnabled)
		{
			// DELL
			//applyOffset(dest, offset);
			setNewPath(dest);
			return true;
		}
		else if(isFlying())
		{
			// DELL
			//applyOffset(dest, offset);

			Location nextloc;

			if(isFlying())
				nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
			else
				nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getWaterZ(), geoIndex, isPlayer());
			if(nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
			{
				setNewPath(nextloc);// .correctGeoZ()
				return true;
			}
			return false;
		}

		boolean isWater = isInWater() || L2World.isWater(dest.x, dest.y, dest.z+8);

		Location move_loc[] = GeoEngine.MoveLoc(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, isPlayer(), 0, isWater ? getWaterZ() : Integer.MIN_VALUE, true**возвращаем координаты в world представлении**); // onlyFullPath = true - проверяем весь путь до конца

		if(isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
		{
			_log.info("L2Character: buildPathToNew0->: pathFind="+pathFind+" moveList2= move_loc0="+move_loc[0]+" move_loc1="+move_loc[1]+" dest="+dest+" dest_z="+dest_z);
			if(ConfigValue.DebugMoveStackIsPlayer)
				Util.test();
		}

		if(move_loc[0] != null) // null - до конца пути дойти нельзя
		{
			setNewPath(move_loc[0]);
			if(isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
				_log.info("L2Character: buildPathToNew1->: move_loc="+move_loc[0]+" dest="+dest);
			return true;
		}

		// если нужно искать путь к конечной точке, строим его.
		// пока уберем, нужно переделать поиск пути, на возвращение только крайних точек...
		if(pathFind)
		{
			List<Location> targets = GeoMove.findMovePathNew(getX(), getY(), getZ(), dest.x, dest.y, dest.z, this, geoIndex);
			if(!targets.isEmpty())
			{
				Location move = targets.remove(targets.size() - 1);
				targets.add(move);
				if(!targets.isEmpty())
				{
					setNewPath(targets);

					if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
						_log.info("L2Character: buildPathToNew2->: targets="+targets);
					return true;
				}
			}
		}

		// если следовали, то возвращаем фалсе.
		if(follow && pathFind && !isPlayable())
			return false;

		if(move_loc[1] != null) // null - нет геодаты
		{
			Location loc = move_loc[1];
			
			if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
				_log.info("L2Character: buildPathToNew->: add0="+loc+" move_loc2=");
			_move_data._targetRecorder.clear();

			// Если режим преследования без поиска пути, то за ~200 едениц до преграды, с MoveToPawn идет переключение на MoveToLocation
			if(follow && !pathFind)
			{
				long dx = loc.x - getX();
				long dy = loc.y - getY();
				long dz = loc.z - getZ();

				double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

				//@offset=200
				if(distance > 200)
				{
					//@offset=200
					double cut = 200 / distance;

					// возможно ceil
					_move_data._x_follow_break = loc.x - (int) Math.round(dx * cut);
					_move_data._y_follow_break = loc.y - (int) Math.round(dy * cut);
					_move_data._z_follow_break = loc.z - (int) Math.round(dz * cut);

					Location loc1 = new Location(_move_data._x_follow_break, _move_data._y_follow_break, _move_data._z_follow_break);
					if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
						_log.info("L2Character: buildPathToNew->: add1="+loc1);
					_move_data._targetRecorder.add(loc1);
				}
				else
				{
					_move_data._x_follow_break = getX();
					_move_data._y_follow_break = getX();
					_move_data._z_follow_break = getX();
				}
				loc.type = 1;
				if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
					_log.info("L2Character: buildPathToNew->: add2="+loc);
				_move_data._targetRecorder.add(loc);
			}
			else
			{
				if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
					_log.info("L2Character: buildPathToNew->: add3="+loc);
				_move_data._targetRecorder.add(loc);
			}			

			if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
				_log.info("L2Character: buildPathToNew3->: move_loc="+loc);
			if(!pathFind && loc.distance(getLoc()) <= offset && getFollowTarget() != null)
			{
				//_log.info("L2Character: buildPathToNew3->: id["+getNpcId()+"]["+getFollowTarget().getNpcId()+"]["+getObjectId()+"] offset="+offset);
				//if(getNpcId() == 22323 || getNpcId() == 22659 || getNpcId() == 22658)
				//	Util.test();
			}
			return true;
		}
		if(follow && pathFind)
		{
			isMoving = false;
			//isFollow = false;
			ThreadPoolManager.getInstance().execute(new L2ObjectTasks.NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 1, null));
			return true;
		}
		return false;
	}

	public boolean followToCharacter(L2Character target, int offset, boolean forestalling, boolean path_find)
	{
		if(isPlayable())
		{
			if(ConfigValue.DEbug1)
				_log.info("L2Character: followToCharacter offset="+offset);
			if(ConfigValue.DEbug2)
				Util.test();
		}
		//if(target.isPlayer())
		//	_log.info("L2Character: followToCharacter->: steep 1");

		if(_move_data == null)
			_move_data = new MoveData(this);

		Location dest = target.getLoc();
		synchronized (_move_data._targetRecorder)
		{
			offset = Math.max(offset, 10);
			if(isFollow && target == getFollowTarget() && offset == _move_data._offset)
			{
				if(!path_find)
				{
					//if(target.isPlayer())
					//	_log.info("L2Character: followToCharacter->: steep 2");

					*broadcastPacket(new CharMoveToLocation(getObjectId(), getLoc(), getFollowTarget().getLoc()));	
					sendActionFailed();
					stopMove(false, false, true);*

					isMoving = false;
					isFollow = false;
					ThreadPoolManager.getInstance().execute(new L2ObjectTasks.NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 2, null));
				}
				sendActionFailed();
				return true;
			}
			if(!path_find)
			{
				*if(target == getFollowTarget() && _move_data.movingDestTempPos.equals(dest))
				{
					broadcastPacket(new MoveToPawn(this, getFollowTarget(), offset));
					sendActionFailed();
					stopMove(false, false, true);
					isFollow = false;
					ThreadPoolManager.getInstance().execute(new L2ObjectTasks.NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 3, null));
					return true;
				}*
				//if(forestalling && isFollow && target.isMoving)
				//	dest = getIntersectionPoint(target);
			}
			//if(isPlayable())
			//	_log.info("L2Character: followToCharacter->: asd["+isAttackingNow()+"]["+(target == null)+"]["+isInVehicle()+"]");

			getAI().clearNextAction();
			if(isMovementDisabled() || target == null || isInVehicle())
			{
				stopMove();
				return false;
			}
				//		if(isPlayable())
				//_log.info("L2Character: followToCharacter->: asd 2");

			if(Math.abs(getZ() - target.getZ()) > 1000 && !isFlying())
			{
				stopMove();
				sendPacket(Msg.CANNOT_SEE_TARGET());
				return false;
			}
				//		if(isPlayable())
				//_log.info("L2Character: followToCharacter->: asd 3");

			_move_data.stopMove();
			if(_moveWaterTask != null)
			{
				_moveWaterTask.cancel(false);
				_moveWaterTask = null;
			}
			isFollow = true;
			setFollowTarget(target);
			_move_data._forestalling = forestalling;
			if(isPlayable() && !isPlayer() && ConfigValue.DEbug3)
				_log.info("L2Character: followToCharacter->: dest1=" + dest);

		*	if(!path_find)
			{
				applyOffset(dest, offset);

				boolean isWater = isInWater() || L2World.isWater(dest.x, dest.y, dest.z);*/
				//dest = GeoEngine.MoveCheck(getX(), getY(), getZ(), dest.x, dest.y, false, false, false, getReflection().getGeoIndex());
				//dest = GeoEngine.MoveLoc(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getReflection().getGeoIndex(), false/** идем до куда можем **/, isPlayer(), 0, isWater ? getWaterZ() : Integer.MIN_VALUE, true/**возвращаем координаты в world представлении**/); // onlyFullPath = true - проверяем весь путь до конца

				/*if(target.isPlayer() && ConfigValue.DEbug4)
					_log.info("L2Character: followToCharacter->: dest2=" + dest);

				setNewPath(dest);

				_move_data.movingDestTempPos.set(target.getX(), target.getY(), target.getZ());
			}
			else*
			{
				if(buildPathToNew(target.getX(), target.getY(), target.getZ(), offset, path_find, !target.isDoor()))
					_move_data.movingDestTempPos.set(target.getX(), target.getY(), target.getZ());
				else if(path_find)
				{
				//				if(isPlayable())
				//_log.info("L2Character: followToCharacter->: asd 4");

					isMoving = false;
					isFollow = false;
					return false;
				}
			}

			//if(target.isPlayer())
			//	_log.info("L2Character: followToCharacter->: steep 3");
			//if(isPlayable())
			//	_log.info("L2Character: followToCharacter->: asd 5");

			_move_data._offset = offset;
			//_move_data.path_find = path_find;
			moveNextNew(true, target.getX(), target.getY(), target.getZ(), _move_data._offset, true, path_find);
			return true;
		}
	}

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding)
	{
		return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, false);
	}
  
	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding)
	{
		return moveToLocation(x_dest, y_dest, z_dest, offset, pathfinding, false);
	}
  
	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean follow)
	{
		// не забыть убрать...
		//if(!isPlayer())
		//	return true;
		*if(isPlayer())
		{
			sendMessage("-- moveToLocation["+x_dest+":"+y_dest+":"+z_dest+":"+offset+"] --");
			_log.info("------ moveToLocation["+x_dest+":"+y_dest+":"+z_dest+":"+offset+"] ------");
		}*
		*if(isPlayable() && !isPlayer() && !isPlayer() && p_block_controll)
			Util.test();*
		//if(isPlayer())
		//	GeoEditorConnector.getInstance().getGMs().add(getPlayer());
		if(_move_data == null)
			_move_data = new MoveData(this);

		synchronized (_move_data._targetRecorder)
		{
			//offset = Math.max(offset, 10);
			Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
			if(isMoving && !isFollow && _move_data.movingDestTempPos.equals(dst_geoloc))
			{
				sendActionFailed();
				return true;
			}

			getAI().clearNextAction();

			if(isMovementDisabled())
			{
				getAI().setNextAction(nextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
				sendActionFailed();
				return false;
			}

			// isFollow = follow;
			isFollow = false;

			_move_data.stopMove();
			if(_moveWaterTask != null)
			{
				_moveWaterTask.cancel(false);
				_moveWaterTask = null;
			}

			if(isPlayer() && !follow)
				getAI().changeIntention(AI_INTENTION_ACTIVE, null, null);

			if(buildPathToNew(x_dest, y_dest, z_dest, offset, pathfinding, false))
				_move_data.movingDestTempPos.set(dst_geoloc);
			else
			{
				isMoving = false;
				sendActionFailed();
				return false;
			}
		}

		//_move_data.path_find = pathfinding;
		moveNextNew(true, x_dest, y_dest, z_dest, offset, follow, pathfinding);
		return true;
	}

	**
	 * должно вызыватся только из synchronized(_move_data._targetRecorder)
	 * @param firstMove
	 *
	public void moveNextNew(boolean firstMove, int x_dest, int y_dest, int z_dest, int offset, boolean follow, boolean path_find)
	{
		_move_data._previous_speed = firstMove ? getWalkSpeed() : getMoveSpeed();
		if(_move_data._previous_speed <= 0)
		{
			stopMove(false, false);
			return;
		}

		//if(!firstMove)
		//	setXYZ(_move_data._x_destination, _move_data._y_destination, _move_data._z_destination, true);
		if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
			_log.info("L2Character: moveNextNew->: intention="+getAI().getIntention()+" steep 1["+isFollow+"]");

		Location end;
		synchronized(_move_data._targetRecorder)
		{
			// Список движения пустой.
			if(_move_data._targetRecorder.isEmpty())
			{
				isMoving = false;
				if(isFollow)
				{
					if(isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
						_log.info("L2Character: moveNextNew->: intention="+getAI().getIntention()+" steep 2");
					if(getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
						broadcastMove(false, 0, 0, 0, 0, false, false);
					//sendActionFailed();
					//stopMove(false, false, true);
					isFollow = false;
					ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 4, null));
				}
				else
					ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED, null, null));
				//if(isPlayer())
				//	GeoEditorConnector.getInstance().getGMs().remove(getPlayer());
				return;
			}

			// Текущая позиция чара.
			Location begin = getLoc();

			// Следующая точка, в которую нам необходимо двигатся.
			end = _move_data._targetRecorder.remove(0);

			// Клиент при передвижении не учитывает поверхность
			double distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end);

			isMoving = true;
		
			try
			{
				setHeading(Util.calculateHeadingFrom(getX(), getY(), end.x, end.y));
			}
			catch(Exception e)
			{
				setHeading(Util.calculateHeadingFrom(getX(), getY(), 0, 0));
			}

			_move_data.startMove(end, path_find);
		}
		// Броадкастим мувинг...
		broadcastMove(firstMove, x_dest, y_dest, z_dest, offset, end.type == 1 && follow || path_find, follow);
		_move_data._followTimestamp = System.currentTimeMillis();

		boolean isWater = isInWater();
		if(isWater && firstMove && z_dest >= getZ()-16)
		{
			if(_moveWaterTask != null)
			{
				_moveWaterTask.cancel(false);
				_moveWaterTask = null;
			}
			_moveWaterTask = ThreadPoolManager.getInstance().scheduleMV(new WaterTaskZ(this, z_dest), 800);
		}
	}

	public void broadcastMove(boolean firstMove, int x_dest, int y_dest, int z_dest, int pawn_dist, boolean path_find, boolean follow)
	{
		//if(isPlayable())
		//	_log.info("L2Character: broadcastMove->: firstMove="+firstMove+" path_find="+path_find+" follow="+follow);
		//if(isPlayable())
		//	_log.info("L2Character: broadcastMove->: "+((!path_find && follow && getFollowTarget() != null && firstMove) ? "MoveToPawn" : "CharMoveToLocation"));

		//if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveStackIsPlayer)
		//	Util.test();
		if(isAirShip())
			broadcastPacket(new ExMoveToLocationAirShip((L2AirShip) this));
		else if(isShip())
			broadcastPacket(new VehicleDeparture((L2Ship) this));
		else
		{
			//validateLocation(isPlayer() ? 2 : 1);
			// TODO: REV
			//if(!path_find && isFollow && getFollowTarget() != null && firstMove)
			if(!path_find && follow && getFollowTarget() != null && firstMove)
			{
				//if(firstMove)
					broadcastPacket(new MoveToPawn(this, getFollowTarget(), pawn_dist));
			}
			else
				broadcastPacket(new CharMoveToLocation(this, z_dest, firstMove));
			//if(isPlayer())
			//	System.out.println("broadcastMove: " + getName());
		}
	}

	**
	 * Останавливает движение и рассылает ValidateLocation
	 *
	public void stopMove()
	{
		stopMove(true, false);
	}

	public void stopMove(boolean validate, boolean set_intention)
	{
		stopMove(validate, set_intention, false);
	}

	**
	 * Останавливает движение
	 * @param validate - рассылать ли ValidateLocation
	 *
	public void stopMove(boolean validate, boolean set_intention, boolean force)
	{
		if(isMoving || force)
		{
			synchronized (_move_data._targetRecorder)
			{
				isMoving = false;
				_move_data.stopMove();
				if(_moveWaterTask != null)
				{
					_moveWaterTask.cancel(false);
					_moveWaterTask = null;
				}
				_move_data._targetRecorder.clear();
			}

			// вроде бы каждый стопМуве сопровождается актионФаил
			// sendActionFailed();
			broadcastPacket(new StopMove(this));
			//if(validate)
			//	validateLocation(1);
			if(set_intention)
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			if(*getNpcId() == 31360 || *isPlayable() && isPlayer() && ConfigValue.DebugMoveIsPlayer)
				Util.test();
		}

		isFollow = false;
	}*/

	protected boolean needStatusUpdate()
	{
		if(ConfigValue.ForceStatusUpdate)
			return true;

		if(!isNpc())
			return true;

		double _intervalHpUpdate = getMaxHp() / 352;

		if(_lastHpUpdate == -99999999)
		{
			_lastHpUpdate = -9999999;
			return true;
		}

		if(getCurrentHp() <= 0 || getMaxHp() < 352)
			return true;

		if(_lastHpUpdate + _intervalHpUpdate < getCurrentHp() && getCurrentHp() > _lastHpUpdate)
		{
			_lastHpUpdate = getCurrentHp();
			return true;
		}

		if(_lastHpUpdate - _intervalHpUpdate > getCurrentHp() && getCurrentHp() < _lastHpUpdate)
		{
			_lastHpUpdate = getCurrentHp();
			return true;
		}
		return false;
	}

	public void onDecay()
	{
		decayMe();
	}

	@Override
	public void onForcedAttack(L2Player player, boolean shift)
	{
		player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));

		if(!isAttackable(player) || player.isConfused() || player.isBlocked())
		{
			player.sendActionFailed();
			return;
		}

		player.getAI().Attack(this, true, shift);
	}

	public void onHitTimer(L2Character target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS, boolean bow)
	{
		if(isAlikeDead())
		{
			sendActionFailed();
			return;
		}

		if(target.isDead() || !isInRangeZ(target, 2000))
		{
			sendActionFailed();
			return;
		}

		if(isPlayable() && !isPlayer() && target.isPlayable() && !isPlayer() && isInZoneBattle() != target.isInZoneBattle())
		{
			L2Player player = getPlayer();
			if(player != null)
			{
				player.sendPacket(Msg.INVALID_TARGET());
				player.sendActionFailed();
			}
			return;
		}

		// if hitted by a cursed weapon, Cp is reduced to 0, if a cursed weapon is hitted by a Hero, Cp is reduced to 0
		if(!miss && target.isPlayer() && (isCursedWeaponEquipped() || getActiveWeaponInstance() != null && getActiveWeaponInstance().isHeroWeapon() && target.isCursedWeaponEquipped()) && !target.block_hp.get())
			target.setCurrentCp(0);

		if(isPlayer())
		{
			if(crit)
				sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this).addDamage(target, target, damage));
			if(miss)
			{
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this).addDamage(target, target, damage));
				if(ConfigValue.EnableDamageOnScreenOld && show_damage)
					sendPacket(new DamageTextPacket(target.getObjectId(), damage, crit, miss, false, false, ConfigValue.DamageOnScreenFontId, ConfigValue.DamageOnScreenColorHDmgMsg, "", "", ConfigValue.DamageOnScreenHPosX, ConfigValue.DamageOnScreenHPosY, ConfigValue.DamageOnScreenHSizeX, ConfigValue.DamageOnScreenHSizeY));
			}
			else if(!target.isInvul() && !target.block_hp.get())
			{
				double trans = target.calcStat(Stats.TRANSFER_PET_DAMAGE_PERCENT, 0, this, null);
				if(trans >= 1 && target.getPet() != null && !target.getPet().isDead() && target.getPet().isSummon() && target.getPet().isInRangeZ(target, 1200) && !target.getPet().isInZonePeace() && (trans = (damage / 100d * trans)) < target.getPet().getCurrentHp()-1 && trans > 0)
				{
					//sendMessage("(0)Пизданул "+((long) (damage - trans))+" лоху и "+((long) trans)+" его носкам.");
					sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_GIVEN_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR).addNumber((int) (damage - trans)).addNumber((int) trans));
				}
				else
					sendHDmgMsg(this, target, null, damage, crit, miss);
			}
		}
		else if(this instanceof L2Summon)
			((L2Summon) this).displayHitMessage(target, damage, crit, miss);

		if(target.isPlayer())
		{
			L2Player enemy = (L2Player) target;

			if(shld && damage > 1)
				enemy.sendPacket(Msg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
			else if(shld && damage == 1)
				enemy.sendPacket(Msg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
		}

		if(target.isMonster() && !target.isRaid() && !target.isBoss() && !target.isMinion() && ((L2MonsterInstance)target).getChampion() == 0 && target.getReflection().getId() == 0 && (target.getLevel() >= 21 && target.getLevel() <= 78) && (target.getLevel() - getLevel() <= 9 && getLevel() - target.getLevel() <= 9) && ((L2MonsterInstance)target).getChampion() == 0 && !(target instanceof L2ChestInstance))
		{
			if(target.getRealDistance(this) >= 150)
			{
				if(Rnd.get(100) < ConfigValue.MonsterChanceUseUltimateDefence && !target.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? 5044*65536L+3 : 5044))
					target.altUseSkill(SkillTable.getInstance().getInfo(5044, 3), target);
			}
			else
			{
				for(L2Effect e : target.getEffectList().getAllEffects())
					if(e.getSkill().getId() == 5044)
						e.exit(true, false);
			}
		}
		
		if(checkPvP(target, null))
			startPvPFlag(target);

		// Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
		if(!miss && damage > 0)
		{
			target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, new Object[] { this, damage, null });
			target.reduceCurrentHp(damage, this, null, true, true, false, true, false, damage, true, bow, crit, false);
			target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 0);

			// Скиллы, кастуемые при физ атаке
			if(!target.isDead())
			{
				if(crit)
					useTriggers(target, TriggerType.CRIT, null, null, damage, false);

				useTriggers(target, TriggerType.ATTACK, null, null, damage, false);

				if(!ConfigValue.AltFormulaCastBreak && Formulas.calcCastBreak(target, crit))
					target.abortCast(false);
				else if(ConfigValue.AltFormulaCastBreak && Formulas.calcCastBreakAlt(target, damage))
					target.abortCast(false);
			}

			if(soulshot && unchargeSS)
				unChargeShots(false);
		}

		if(miss)
			target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, damage, true);
			
		/*if(target.isSummon() || target.isPet())
		{
			if(_runPetTask != null)
				_runPetTask.cancel(true);
			_runPetTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunOnAttackPet(this, target), 1000, 1000);
		//	_log.info("RunOnAttackPet: Start");
		}*/

		startAttackStanceTask();
	}

	// разобратся, почему сыпится НПЕ...Толи таргет, то ли скилл == null
	public void onMagicUseTimer(final L2Character aimingTarget, final L2Skill skill, boolean forceUse)
	{
		if(skill == null)
		{
			onCastEndTime(skill, this, aimingTarget, forceUse);
			sendPacket(Msg.ActionFail);
			return;
		}
		_castInterruptTime = 0;

		if(skill.isUsingWhileCasting())
		{
			aimingTarget.getEffectList().stopEffect(skill.getId());
			onCastEndTime(skill, this, aimingTarget, forceUse);
			return;
		}
		// Только DUMMY.
		if(skill.getFlyType() == FlyToLocation.FlyType.DUMMY)
		{
			setFlyLoc(null);
			if(skill.getSkillType() == SkillType.PDAM && Rnd.chance(aimingTarget.calcStat(Stats.PSKILL_EVASION, 0, this, skill)))
			{
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
				aimingTarget.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(aimingTarget).addName(this));
			}
			else
			{
				Location flyLoc = getFlyLocation(aimingTarget, skill);
				if(flyLoc != null)
				{
					setFlyLoc(flyLoc);
					broadcastPacket(new FlyToLocation(this, flyLoc, skill.getFlyType()));
					setLoc(flyLoc);
					setHeading(aimingTarget.getHeading());
				}
				else
				{
					sendPacket(Msg.CANNOT_SEE_TARGET());
					//sendMessage("CANNOT_SEE_TARGET() 7");
					return;
				}
			}
		}
		if(!skill.isOffensive() && getAggressionTarget() != null)
			forceUse = true;

		int level = getSkillDisplayLevel(skill.getId());
		if(level < 1)
			level = 1;
		GArray<L2Character> targets = skill.getTargets(this, aimingTarget, forceUse);

		callSkill(skill, targets, true);

		if(aimingTarget != null && aimingTarget.isMonster() && !aimingTarget.isRaid() && !aimingTarget.isBoss() && ((L2MonsterInstance)aimingTarget).getChampion() == 0 && !aimingTarget.isMinion() && aimingTarget.getReflection().getId() == 0 && (aimingTarget.getLevel() >= 21 && aimingTarget.getLevel() <= 76))
		{
			if(skill.getId() != 28 && skill.getId() != 680 && skill.getId() != 51 && skill.getId() != 511 && skill.getId() != 15 && skill.getId() != 254 && skill.getId() != 1069 && skill.getId() != 1097 && skill.getId() != 1042 && skill.getId() != 1072 && skill.getId() != 1170 && skill.getId() != 352 && skill.getId() != 358 && skill.getId() != 1394 && skill.getId() != 695 && skill.getId() != 115 && skill.getId() != 1083 && skill.getId() != 1160 && skill.getId() != 1164 && skill.getId() != 1201 && skill.getId() != 1206 && skill.getId() != 1222 && skill.getId() != 1223 && skill.getId() != 1224 && skill.getId() != 1092 && skill.getId() != 65 && skill.getId() != 106 && skill.getId() != 122 && skill.getId() != 127 && skill.getId() != 1049 && skill.getId() != 1064 && skill.getId() != 1071 && skill.getId() != 1074 && skill.getId() != 1169 && skill.getId() != 1263 && skill.getId() != 1269 && skill.getId() != 352 && skill.getId() != 353 && skill.getId() != 1336 && skill.getId() != 1337 && skill.getId() != 1338 && skill.getId() != 1358 && skill.getId() != 1359 && skill.getId() != 402 && skill.getId() != 403 && skill.getId() != 412 && skill.getId() != 1386 && skill.getId() != 1394 && skill.getId() != 1396 && skill.getId() != 485 && skill.getId() != 501 && skill.getId() != 1445 && skill.getId() != 1446 && skill.getId() != 1447 && skill.getId() != 522 && skill.getId() != 531 && skill.getId() != 1481 && skill.getId() != 1482 && skill.getId() != 1483 && skill.getId() != 1484 && skill.getId() != 1485 && skill.getId() != 1486 && skill.getId() != 695 && skill.getId() != 696 && skill.getId() != 716 && skill.getId() != 775 && skill.getId() != 1511 && skill.getId() != 792 && skill.getId() != 1524 && skill.getId() != 1529)
			{
				if(aimingTarget.getRealDistance(this) >= 150)
				{
					if(Rnd.get(100) < ConfigValue.MonsterChanceUseUltimateDefence)
					{
						aimingTarget.altUseSkill(SkillTable.getInstance().getInfo(5044, 3), aimingTarget);
					}
				}
				else
				{
					for(L2Effect e : aimingTarget.getEffectList().getAllEffects())
						if(e.getSkill().getId() == 5044)
							e.exit(true, false);
				}
			}
		}
		if(_scheduledCastCount > 0)
		{
			_scheduledCastCount--;
			_skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, skill, getCastingTarget(), forceUse), _scheduledCastInterval, isPlayable());
			_skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, skill, forceUse), _scheduledCastInterval, isPlayable());
			return;
		}
		int skillCoolTime = Formulas.calcMAtkSpd(this, skill, skill.getCoolTime());
		if(skillCoolTime > 0)
			ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(skill, this, aimingTarget, forceUse), skillCoolTime, isPlayable());
		else if(skill.hasEffects())
			ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(skill, this, aimingTarget, forceUse), 20, isPlayable());
		else
			onCastEndTime(skill, this, aimingTarget, forceUse);
	}

	private void finishFly() 
	{ 
		Location flyLoc = _flyLoc; 
		_flyLoc = null; 
		if(flyLoc == null) 
			return; 
		setLoc(flyLoc); 
		validateLocation(1); 
	}

	public void onCastEndTime(L2Skill skill, L2Character actor, L2Character target, boolean forceUse)
	{
		// Уёбищно, но хуй с ним...Если наложили дисарм в момент каста скила, оружее снимаем после его окончания...
		if(_disarm.get() && isPlayer())
		{
			L2ItemInstance weapon = getPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			if(weapon != null)
				getPlayer().getInventory().unEquipItemInBodySlotAndNotify(weapon.getBodyPart(), weapon, false);
			_disarm.setAndGet(false);
		}
		else if(skill != null && actor != null && actor.getAI() != null && skill.getNextAction() == L2Skill.NextAction.ATTACK && !actor.equals(actor.getAI().getAttackTarget()) && (actor.getPlayer() != null && actor.getPlayer().getAI().getNextAction() == null) && !forceUse || (isSummon() || isPet()) && ((L2Summon)this)._actionAtack == 1 && skill.isOffensive() && actor.getAI().getAttackTarget() != this)
			actor.getAI().setNextAction(nextAction.ATTACK, actor.getAI().getAttackTarget(), null, isSummon() || isPet(), false);
		//else
			//actor.getAI().clearNextAction();
		finishFly();
		clearCastVars();
		getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, new Object[] { skill, actor, target });
	}

	public void clearCastVars()
	{
		_animationEndTime = 0; 
		_castInterruptTime = 0; 
		_scheduledCastCount = 0; 
		_castingSkill = null;
		_skillTask = null;
		_skillLaunchedTask = null;
		_flyLoc = null;
	}

	public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{
		fireMethodInvoked(MethodCollection.ReduceCurrentHp, new Object[] { i, attacker, skill, awake, standUp, directHp});

		if(((attacker == null || attacker.isDead()) && !isDot) || isDead())
			return;

		_lastAtacker = attacker;

		if(attacker != null && (isInvul() || block_hp.get()) && attacker != this && !isDot)
		{
			attacker.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
			if(ConfigValue.TestAtackFail && attacker.isPlayer() && attacker.getPlayer().isGM())
			{
				attacker.sendMessage("L2CharacterHP: ["+getName()+"]["+_isInvul+"]["+_isInvul_skill+"]["+block_hp.get()+"]");
				_log.info("L2CharacterHP: ["+getName()+"]["+_isInvul+"]["+_isInvul_skill+"]["+block_hp.get()+"]");
				Util.test();
			}
			return;
		}

		// 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
		if(attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			// ПК не может нанести урон чару с блессингом
			if(attacker.getKarma() > 0 && getEffectList().getEffectsBySkillId(5182) != null && !isInZone(ZoneType.Siege))
				return;
			// чар с блессингом не может нанести урон ПК
			if(getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.Siege))
				return;
			if ((!isPlayable()) && (attacker.getPlayer() != null))
			{
				attacker.getPlayer().getNevitBlessing().startBonus();
				attacker.getPlayer().getRecommendation().startRecBonus();
			}
		}

		if(awake && !canReflect)
			DeleteAbnormalStatus2(true, false);

		if(standUp && isPlayer())
		{
			standUp();
			//synchronized(getEffectList())
			{
				if(isFakeDeath())
				{
					L2Effect fakeDeath = getEffectList().getEffectByType(EffectType.c_fake_death);
					if(fakeDeath == null)
						stopFakeDeath();
					else if(fakeDeath.getTime() > 2000)
						getEffectList().stopAllSkillEffects(EffectType.c_fake_death);
				}
			}
		}

		if(attacker != this && (skill == null || skill.isOffensive()))
		{
			startAttackStanceTask();
			//synchronized(getEffectList())
			{
				if(isInvisible() && getEffectList().getEffectByType(EffectType.p_hide) != null)
					getEffectList().stopAllSkillEffects(EffectType.p_hide);
			}
		}

		// ...
		if((skill == null || skill.isOffensive() && !isDot) && getCurrentHp() - i > 0.5D)
			useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, null, null, i, true);

		if(attacker != null && canReflect && attacker.absorbAndReflect(this, skill, i2, bow))
			return;

		if(canReflect || tp)
			DeleteAbnormalStatus2(false, crit);

		if(attacker != null && attacker.isPlayable())
		{
			L2Playable pAttacker = (L2Playable) attacker;

			// Flag the attacker if it's a L2Player outside a PvP area
			//if(!isDead() && pAttacker.checkPvP(this, null))
			//	pAttacker.startPvPFlag(this);

			if(isMonster() && skill != null && skill.isOverhit())
			{
				// Calculate the over-hit damage
				// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
				double overhitDmg = (_currentHp - i) * -1;
				if(overhitDmg <= 0)
				{
					setOverhitDamage(0);
					setOverhitAttacker(null);
				}
				else
				{
					setOverhitDamage(overhitDmg);
					setOverhitAttacker(attacker);
				}
			}

			double ii;
			if(!directHp && _currentCp > 0)
			{
				i = _currentCp - i;
				ii = i;

				if(ii < 0)
					ii *= -1;

				if(i < 0)
					i = 0;

				setCurrentCp(i);
			}
			else
				ii = i;

			if(_currentCp == 0 || directHp)
			{
				ii = _currentHp - ii;

				if(ii < 0)
					ii = 0;

				if(isNpc())
					pAttacker.addDamage((L2NpcInstance) this, (int) (_currentHp - ii));

				setCurrentHp(ii, false);
			}
		}
		else
		{
			if(getCurrentHp() - i < 0.5)
				useTriggers(attacker, TriggerType.DIE, null, null, i, true);

			setCurrentHp(Math.max(_currentHp - i, 0), false);
		}

		if(isDead())
			doDie(attacker);
	}

	public void reduceCurrentMp(double i, L2Character attacker)
	{
		if(attacker != null && attacker != this)
			DeleteAbnormalStatus2(true, false);

		if((isInvul() || block_mp.get()) && attacker != null && attacker != this)
		{
			attacker.sendPacket(Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
			if(ConfigValue.TestAtackFail && attacker.isPlayer() && attacker.getPlayer().isGM())
			{
				attacker.sendMessage("L2CharacterMP: ["+getName()+"]["+_isInvul+"]["+_isInvul_skill+"]["+block_hp.get()+"]");
				_log.info("L2CharacterMP: ["+getName()+"]["+_isInvul+"]["+_isInvul_skill+"]["+block_hp.get()+"]");
				Util.test();
			}
			return;
		}

		// 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
		if(attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			// ПК не может нанести урон чару с блессингом
			if(attacker.getKarma() > 0 && getEffectList().getEffectsBySkillId(5182) != null && !isInZone(ZoneType.Siege))
				return;
			// чар с блессингом не может нанести урон ПК
			if(getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.Siege))
				return;
		}

		i = _currentMp - i;

		if(i < 0)
			i = 0;

		setCurrentMp(i);

		if(attacker != null && attacker != this)
			startAttackStanceTask();
	}

	public double relativeSpeed(L2Object target)
	{
		return getMoveSpeed() - target.getMoveSpeed() * Math.cos(headingToRadians(getHeading()) - headingToRadians(target.getHeading()));
	}

	public void removeAllSkills(boolean update_icon)
	{
		for(L2Skill s : getAllSkillsArray())
			removeSkill(s, false, false);
		if(update_icon)
			updateEffectIcons();
	}

	public L2Skill removeSkill(L2Skill skill, boolean n, boolean update_icon)
	{
		if(skill == null)
			return null;
		return removeSkillById(skill.getId(), update_icon);
	}

	public L2Skill removeSkillById(Integer id)
	{
		return removeSkillById(id, true);
	}

	public L2Skill removeSkillById(Integer id, boolean update_icon)
	{
		// Remove the skill from the L2Character _skills
		L2Skill oldSkill = _skills.remove(id);

		// Remove all its Func objects from the L2Character calculator set
		if(oldSkill != null)
		{
			removeTriggers(oldSkill);
			removeStatsOwner(oldSkill);
			if(oldSkill.isPassive())
			{
				GArray<L2Effect> effects = getEffectList().getEffectsBySkill(oldSkill);
				if(effects != null)
					for(L2Effect effect : effects)
						effect.exit(false, false);
			}
			else if(ConfigValue.AltDeleteSABuffs && (oldSkill.isItemSkill() || oldSkill.isHandler()))
			{
				// Завершаем все эффекты, принадлежащие старому скиллу
				GArray<L2Effect> effects = getEffectList().getEffectsBySkill(oldSkill);
				if(effects != null)
					for(L2Effect effect : effects)
						effect.exit(false, false);
				// И с петов тоже
				L2Summon pet = getPet();
				if(pet != null)
				{
					effects = pet.getEffectList().getEffectsBySkill(oldSkill);
					if(effects != null)
					{
						for(L2Effect effect : effects)
							effect.exit(false, false);
						if(update_icon)
							pet.updateEffectIcons();
					}
				}
			}
			if(update_icon)
				updateEffectIcons();
		}

		return oldSkill;
	}

	public void addTriggers(StatTemplate f)
	{
		if(f.getTriggerList().isEmpty())
			return;
		for(TriggerInfo t : f.getTriggerList())
		{
			addTrigger(t);
		}
	}

	public void addTrigger(TriggerInfo[] trigger)
	{
		for(TriggerInfo t : trigger)
		{
			if(_triggers == null)
			{
				_triggers = new ConcurrentHashMap<TriggerType, Set<TriggerInfo>>();
			}
			Set<TriggerInfo> hs = _triggers.get(t.getType());
			if(hs == null)
			{
				hs = new CopyOnWriteArraySet<TriggerInfo>();
				_triggers.put(t.getType(), hs);
			}

			hs.add(t);

			if (t.getType() == TriggerType.ADD)
				useTriggerSkill(this, null, t, null, 0.0, true);
		}
	}

	public void addTrigger(TriggerInfo t)
	{
		if(_triggers == null)
		{
			_triggers = new ConcurrentHashMap<TriggerType, Set<TriggerInfo>>();
		}
		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
		{
			hs = new CopyOnWriteArraySet<TriggerInfo>();
			_triggers.put(t.getType(), hs);
		}

		hs.add(t);

		if (t.getType() == TriggerType.ADD)
			useTriggerSkill(this, null, t, null, 0.0D, true);
	}

	public void removeTriggers(StatTemplate f)
	{
		if(_triggers == null || f.getTriggerList().isEmpty())
		{
			return;
		}
		for(TriggerInfo t : f.getTriggerList())
			removeTrigger(t);
	}

	public void useTriggers(L2Object target, TriggerType type, L2Skill ex, L2Skill owner, double damage, boolean is_d)
	{
		if(_triggers == null)
			return;
		Set<TriggerInfo> SkillsOnSkillAttack = _triggers.get(type);
		if(SkillsOnSkillAttack != null)
			for(TriggerInfo t : SkillsOnSkillAttack)
				if(t.getSkill() != ex)
					useTriggerSkill(target == null ? getTarget() : target, null, t, owner, damage, is_d);
	}

	// TODO: переделать как должно быть...тип таргета и объекты действия, не учитываются от скила, который мы накладываем, таргет берёться от настроек тригера.
	// {{p_trigger_skill_by_dmg;{enemy_all;1;99};{100;30;diff};[s_trigger_curse_of_life_flow2];target;{all}}}
	// target
	public void useTriggerSkill(L2Object target, GArray<L2Character> targets, TriggerInfo trigger, L2Skill owner, double damage, boolean is_d)
	{
		triggerLock.lock();
		try
		{
			L2Skill skill = trigger.getSkill();
			if(skill.getReuseDelay() > 0 && isSkillDisabled(ConfigValue.SkillReuseType == 0 ? skill.getId()*65536L+skill.getLevel() : skill.getId()))
				return;
			L2Character aimTarget = skill.getAimingTarget(this, target);

			L2Character realTarget = ((target != null) && (target.isCharacter())) ? (L2Character)target : null;
			if(!Rnd.chance(trigger.getChance()) || !trigger.checkCondition(this, realTarget, aimTarget, owner, damage) || !skill.checkCondition(this, aimTarget, (skill.getId() == 5682 || skill.getId() == 3592), true, true))
				return;
			else if(!is_d && trigger._is_item == 1 && !target.isAutoAttackable(this))
				return;
			else if(targets == null)
				targets = skill.getTargets(this, aimTarget, false);

			long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skill));
			disableSkill(skill.getId(), skill.getLevel(), reuseDelay);

			int displayId = 0; int displayLevel = 0;

			if(skill.hasEffects())
			{
				displayId = skill.getEffectTemplates()[0]._displayId;
				displayLevel = skill.getEffectTemplates()[0]._displayLevel;
			}

			if(displayId == 0)
				displayId = skill.getDisplayId();
			if(displayLevel == 0)
				displayLevel = skill.getDisplayLevel();

			if(trigger.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE)
				for(L2Character cha : targets)
					broadcastSkill(new MagicSkillUse(this, cha, displayId, displayLevel, 0, 0), true);
			Formulas.calcSkillMastery(skill, this);

			callSkill(skill, targets, false);
		}
		finally
		{
			triggerLock.unlock();
		}
	}

	public void removeTrigger(TriggerInfo t)
	{
		if(_triggers == null)
			return;
		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
			return;
		hs.remove(t);
	}

	public void removeTrigger(TriggerInfo[] triggers)
	{
		for(TriggerInfo t : triggers)
		{
			if(_triggers == null)
				return;
			Set<TriggerInfo> hs = _triggers.get(t.getType());
			if(hs == null)
				return;
			hs.remove(t);
		}
	}

	public final synchronized void removeStatFunc(Func f)
	{
		if(f == null)
			return;

		int stat = f._stat.ordinal();
		if(_calculators.length > stat && _calculators[stat] != null)
			_calculators[stat].removeFunc(f);
	}

	public final synchronized void removeStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			removeStatFunc(f);
	}

	public final void removeStatsOwner(Object owner)
	{
		for(int i = 0; i < _calculators.length; i++)
			if(_calculators[i] != null)
				_calculators[i].removeOwner(owner);
	}

	public void sendActionFailed()
	{
		if(ConfigValue.TestActionFail && isPlayer() && getPlayer().isGM())
		{
			Util.test();
		}
		sendPacket(Msg.ActionFail);
	}

	@Override
	public boolean hasAI()
	{
		return _ai != null;
	}

	@Override
	public L2CharacterAI getAI()
	{
		if(_ai == null)
			_ai = new L2CharacterAI(this);
		return _ai;
	}

	public L2CharacterAI setAI(L2CharacterAI new_ai)
	{
		if(new_ai == null)
			return _ai = null;
		if(_ai != null)
			_ai.stopAITask();
		_ai = new_ai;
		return _ai;
	}

	public final double setCurrentHp(double newHp, boolean canRessurect)
	{
		newHp = Math.min(getMaxHp(), Math.max(0, newHp));

		if(_currentHp == newHp || newHp >= 0.5 && isDead() && !canRessurect)
			return 0;

		double limit = calcStat(Stats.p_limit_hp, null, null) * getMaxHp() / 100;
		_blessed = false;

		double hpStart = _currentHp;

		if(_currentHp < newHp && newHp > limit)
		{
			if(_currentHp < limit)
				newHp = limit;
			else
				return 0;
		}

		dieLock.lock();
		
		if(newHp < 0.5 && isUnDying() == 1)
			newHp = 0.5;

		_currentHp = newHp;
		if(!isDead())
		{
			_killedAlready = false;
			_killedAlreadyPlayer = false;
			_killedAlreadyPet = false;
		}

		dieLock.unlock();

		if(_currentHp < getMaxHp())
			startRegeneration(0);

		firePropertyChanged(PropertyCollection.HitPoints, hpStart, _currentHp);

		checkHpMessages(hpStart, newHp);
		broadcastStatusUpdate();
		sendChanges();//1
		return _currentHp-hpStart;
	}

	public final double setCurrentMp(double newMp)
	{
		newMp = Math.min(getMaxMp(), Math.max(0, newMp));

		if(_currentMp == newMp)
			return 0;

		double limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100;

		double mpStart = _currentMp;
		
		if(_currentMp < newMp && newMp > limit)
		{
			if(_currentMp < limit)
				newMp = limit;
			else
				return 0;
		}

		_currentMp = newMp;

		if(_currentMp < getMaxMp())
			startRegeneration(1);

		broadcastStatusUpdate();
		sendChanges();//1
		return _currentMp-mpStart;
	}

	public final double setCurrentCp(double newCp)
	{
		if(!isPlayer())
			return 0;

		newCp = Math.min(getMaxCp(), Math.max(0, newCp));

		if(_currentCp == newCp)
			return 0;

		double limit = calcStat(Stats.CP_LIMIT, null, null) * getMaxCp() / 100;
		double cpStart = _currentCp;

		if(_currentCp < newCp && newCp > limit)
		{
			if(_currentCp < limit)
				newCp = limit;
			else
				return 0;
		}

		_currentCp = newCp;

		if(_currentCp < getMaxCp())
			startRegeneration(2);
		broadcastStatusUpdate();
		sendChanges();//1
		return _currentCp-cpStart;
	}

	public void setCurrentHpMp(double newHp, double newMp, boolean canRessurect)
	{
		newHp = Math.min(getMaxHp(), Math.max(0, newHp));
		newMp = Math.min(getMaxMp(), Math.max(0, newMp));

		if(_currentHp == newHp && _currentMp == newMp)
			return;

		if(newHp >= 0.5 && isDead() && !canRessurect)
			return;

		double limit_hp = calcStat(Stats.p_limit_hp, null, null) * getMaxHp() / 100;
		double limit_mp = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100;
		_blessed = false;

		double hpStart = _currentHp;

		if(_currentHp <= newHp && newHp > limit_hp)
		{
			if(_currentHp < limit_hp)
				newHp = limit_hp;
			else
				newHp = _currentHp;
		}
		if(_currentMp <= newMp && newMp > limit_mp)
		{
			if(_currentMp < limit_mp)
				newMp = limit_mp;
			else
				newMp = _currentMp;
		}

		dieLock.lock();

		if(newHp < 0.5 && isUnDying() == 1)
			newHp = 0.5;

		_currentHp = newHp;
		_currentMp = newMp;

		if(!isDead())
			_killedAlready = false;

		dieLock.unlock();

		if(_currentHp < getMaxHp() || _currentMp < getMaxMp())
			startRegeneration(-1);
		firePropertyChanged(PropertyCollection.HitPoints, hpStart, _currentHp);
		checkHpMessages(hpStart, newHp);
		broadcastStatusUpdate();
		sendChanges();//1
	}

	public void setCurrentHpMp(double newHp, double newMp)
	{
		setCurrentHpMp(newHp, newMp, false);
	}

	public final void setFlying(boolean mode)
	{
		_flying = mode;
	}

	@Override
	public final int getHeading()
	{
		return _heading;
	}

	@Override
	public void setHeading(int heading)
	{
		_heading = heading;
	}

	public final void setIsBlessedByNoblesse(boolean value)
	{
		if(value)
			_isBlessedByNoblesse.getAndSet(true);
		else
			_isBlessedByNoblesse.setAndGet(false);
	}

	public final void setIsSalvation(boolean value)
	{
		if(value)
			_isSalvation.getAndSet(true);
		else
			_isSalvation.setAndGet(false);
	}

	public void setIsInvul(boolean b)
	{
		if(ConfigValue.DebugParam_Invul && isPlayer())
			Util.test(getName(), null, String.valueOf(b), "debug_param_invul_other");
		_isInvul = b;
	}

	public L2Skill _isInvul_skill = null;
	public void setIsInvul(boolean b, L2Skill skill)
	{
		if(ConfigValue.DebugParam_Invul && isPlayer())
			Util.test(getName(), skill, String.valueOf(b), "debug_param_invul_skill");
		if(b)
			_isInvul_skill = skill;
		else
			_isInvul_skill = null;

		//_isInvul = b;
	}

	public final void setIsPendingRevive(boolean value)
	{
		_isPendingRevive = value;
	}

	public final void setIsTeleporting(long time)
	{
		_isTeleporting = System.currentTimeMillis() + time;
	}

	public final void setName(String name)
	{
		_name = name;
		if(!name.isEmpty() && isNpc())
			_showName = true;
	}

	public final void setNameCreate(String name)
	{
		_name = name;
	}

	public L2Character getCastingTarget()
	{
		return my_casting_target.get();
	}

	public void setCastingTarget(L2Character target)
	{
		if(target == null)
			my_casting_target = HardReferences.emptyRef();
		else
			my_casting_target = target.getRef();
	}

	public final void setRiding(boolean mode)
	{
		_riding = mode;
	}

	public final void setRunning()
	{
		if(!_running)
		{
			_running = true;
			broadcastPacket(new ChangeMoveType(this));
		}
	}

	public void setSkillMastery(Integer skill, byte mastery)
	{
		if(_skillMastery == null)
			_skillMastery = new HashMap<Integer, Byte>();
		_skillMastery.put(skill, mastery);
	}

	public void setAggressionTarget(L2Character target)
	{
		if(target == null)
			my_aggression_target = HardReferences.emptyRef();
		else
			my_aggression_target = target.getRef();
	}

	public L2Character getAggressionTarget()
	{
		return my_aggression_target.get();
	}

	public L2Character getFollowTarget()
	{
		return my_follow_target.get();
	}

	public void setFollowTarget(L2Character target)
	{
		if(target == null)
			my_follow_target = HardReferences.emptyRef();
		else
			my_follow_target = target.getRef();
	}

	public void setTarget(L2Object object)
	{
		if(object != null && !object.isVisible())
		{
			object = null;
			//if(isPlayer())
			//_log.info("setTarget: no ok");
		}
		/*if(object == null)
		{
			if(isAttackingNow() && getAI().getAttackTarget() == getTarget())
				abortAttack(false, true);
			if(isCastingNow() && getAI().getAttackTarget() == getTarget())
				abortCast(false);
		}*/

		if(object == null)
			my_target = HardReferences.emptyRef();
		else
			my_target = object.getRef();

	//	if(isPlayer())
		//_log.info("setTarget: targetStoreId="+targetStoreId);
	}

	public void setTemplate(L2CharTemplate template)
	{
		_template = template;
	}

	public void setBaseTemplate(L2CharTemplate template)
	{
		_baseTemplate = template;
	}

	public void setTitle(String title)
	{
		_title = title;
		if(title == null)
			_title = "";
		if(title != null && !title.isEmpty() && isNpc())
			_showTitle = true;
	}

	public void setTitleCreate(String title)
	{
		_title = title;
	}

	public void setTitle(String title, boolean save)
	{
		_title = title;
		if(save)
			PlayerData.getInstance().setTitle(this, title);
	}

	public void setWalking()
	{
		if(_running)
		{
			_running = false;
			broadcastPacket(new ChangeMoveType(this));
		}
	}

	public void startAbnormalEffect(AbnormalVisualEffect ae)
	{
		//_log.info("startAbnormalEffect: "+ae);
		if(ae == AbnormalVisualEffect.ave_none)
		{
			_abnormal_list[0] = AbnormalVisualEffect.ave_none.getMask();
			_abnormal_list[1] = AbnormalVisualEffect.ave_none.getMask();
			_abnormal_list[2] = AbnormalVisualEffect.ave_none.getMask();
			_abnormalEffects.clear();
		}
		else
		{
			if(!_abnormalEffects.contains(ae))
			{
				if(ae.isSpecial() && (_abnormal_list[1] & ae.getMask()) == 0)
					_abnormal_list[1] |= ae.getMask();
				else if(ae.isEvent() && (_abnormal_list[2] & ae.getMask()) == 0)
					_abnormal_list[2] |= ae.getMask();
				else if((_abnormal_list[0] & ae.getMask()) == 0)
					_abnormal_list[0] |= ae.getMask();

				_abnormalEffects.add(ae);
			}
		}
		updateAbnormalEffect();
		//sendChanges();//1
	}

	@Override
	public void startAttackStanceTask()
	{
		if(System.currentTimeMillis() < _stanceInited + 10000)
			return;

		_stanceInited = System.currentTimeMillis();
		atackLock.lock();
		try
		{
			// Бесконечной рекурсии не будет, потому что выше проверка на _stanceInited
			if(this instanceof L2Summon && getPlayer() != null)
				getPlayer().startAttackStanceTask();
			else if(isPlayer() && getPet() != null)
				getPet().startAttackStanceTask();

			if(_stanceTask != null)
				_stanceTask.cancel(false);
			else
				broadcastPacket(new AutoAttackStart(getObjectId()));
		}
		catch(Exception e)
		{
			atackLock.unlock();
		}
		finally
		{
			atackLock.unlock();
		}
		_stanceTask = ThreadPoolManager.getInstance().schedule(new CancelAttackStanceTask(this), 15000, isPlayable());
	}

	public void stopAttackStanceTask()
	{
		broadcastPacket(new AutoAttackStop(getObjectId()));
		if(_stanceTask != null)
		{
			_stanceTask.cancel(false);
			_stanceTask = null;
		}
		try
		{
			if(_runPetTask != null)
			{
				//_log.info("RunOnAttackPet: Stop");
				_runPetTask.cancel(true);
				_runPetTask = null;
			}
		}
		catch(Exception e)
		{}
		if(isBot() && getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	public long getRegenTick()
	{
		return 3333L;
	}

	/**
	 * Остановить регенерацию
	 */
	protected void stopRegeneration()
	{
		regenLock.lock();
		try
		{
			if(_isRegenerating)
			{
				_isRegenerating = false;

				if(_regenTask != null)
				{
					_regenTask.cancel(false);
					_regenTask = null;
				}
			}
		}
		finally
		{
			regenLock.unlock();
		}
	}

	/**
	 * Запустить регенерацию
	 */
	public void startRegeneration(int type)
	{
		if(!isDead() && (_currentHp < getMaxHp() || _currentMp < getMaxMp() || _currentCp < getMaxCp()))
		{
			if(ConfigValue.AutoCpEnable && isPlayer())
				try
				{
					if((type == 0 || type == -1) && (getPlayer()._next_use_auto_cp_hp == null || getPlayer()._next_use_auto_cp_hp.isDone()))
						getPlayer()._next_use_auto_cp_hp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(getPlayer(), type), 200);
					else if((type == 1 || type == -1) && (getPlayer()._next_use_auto_cp_mp == null || getPlayer()._next_use_auto_cp_mp.isDone()))
						getPlayer()._next_use_auto_cp_mp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(getPlayer(), type), 200);
					else if(type == 2 && (getPlayer()._next_use_auto_cp_cp == null || getPlayer()._next_use_auto_cp_cp.isDone()))
						getPlayer()._next_use_auto_cp_cp = ThreadPoolManager.getInstance().schedule(new AutoCpTask(getPlayer(), type), 200);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			if(!_isRegenerating)
			{
				regenLock.lock();
				try
				{
					if(!_isRegenerating)
					{
						_isRegenerating = true;
						_regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(_regenTaskRunnable == null ? _regenTaskRunnable = new RegenTask() : _regenTaskRunnable, getRegenTick(), getRegenTick());
					}
				}
				finally
				{
					regenLock.unlock();
				}
			}
		}
	}

	private class RegenTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(isDead() || isHealBlocked(false, true))
				return;

			double hpStart = _currentHp;

			regenLock.lock();
			try
			{
				double addHp = 0;
				double addMp = 0;

				int maxHp = getMaxHp();
				int maxMp = getMaxMp();
				int maxCp = isPlayer() ? getMaxCp() : 0;

				if(_currentHp < maxHp)
					addHp += Formulas.calcHpRegen(L2Character.this);

				if(_currentMp < maxMp)
					addMp += Formulas.calcMpRegen(L2Character.this);

				// Added regen bonus when character is sitting
				if(isPlayer() && ConfigValue.RegenSitWait)
				{
					L2Player pl = (L2Player) L2Character.this;
					if(pl.isSitting())
					{
						pl.updateWaitSitTime();
						if(pl.getWaitSitTime() > 5)
						{
							addHp += pl.getWaitSitTime();
							addMp += pl.getWaitSitTime();
						}
					}
				}
				else if(isRaid())
				{
					addHp *= ConfigValue.RateRaidRegen;
					addMp *= ConfigValue.RateRaidRegen;
				}

				_currentHp += Math.max(0, Math.min(addHp, calcStat(Stats.p_limit_hp, null, null) * maxHp / 100. - _currentHp));
				_currentMp += Math.max(0, Math.min(addMp, calcStat(Stats.MP_LIMIT, null, null) * maxMp / 100. - _currentMp));

				_currentHp = Math.min(maxHp, _currentHp);
				_currentMp = Math.min(maxMp, _currentMp);

				if(isPlayer())
				{
					_currentCp += Math.max(0, Math.min(Formulas.calcCpRegen(L2Character.this), calcStat(Stats.CP_LIMIT, null, null) * maxCp / 100. - _currentCp));
					_currentCp = Math.min(maxCp, _currentCp);
				}

				//отрегенились, останавливаем задачу
				if(_currentHp == maxHp && _currentMp == maxMp && _currentCp == maxCp)
					stopRegeneration();
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			finally
			{
				regenLock.unlock();
			}
			firePropertyChanged(PropertyCollection.HitPoints, hpStart, _currentHp);
			checkHpMessages(hpStart, _currentHp);

			broadcastStatusUpdate();
			sendChanges();//1
		}
	}

	public void stopAbnormalEffect(AbnormalVisualEffect ae)
	{
		if(ae.isSpecial() && (_abnormal_list[1] & ae.getMask()) == ae.getMask())
			_abnormal_list[1] &= ~ae.getMask();
		else if(ae.isEvent() && (_abnormal_list[2] & ae.getMask()) == ae.getMask())
			_abnormal_list[2] &= ~ae.getMask();
		else if((_abnormal_list[0] & ae.getMask()) == ae.getMask())
			_abnormal_list[0] &= ~ae.getMask();
		_abnormalEffects.remove(ae);
		updateAbnormalEffect();
		//sendChanges();//1
	}

	public void block_hp_mp(boolean block_hp_)
	{
		block_hp_mp(-1, null, block_hp_);
	}

	public void block_hp_mp(int obj_id, L2Skill skill, boolean block_hp_)
	{
		if(block_hp_)
		{
			//if(System.currentTimeMillis() - blockhpdelay > 1000)
			{
				block_hp.setAndGet(true);
				blockhpdelay = System.currentTimeMillis();
				if(ConfigValue.DebugParam_Invul && isPlayer())
					Util.test(getName(), skill, "block["+obj_id+"]:"+block_hp.get(), "debug_param_invul2_skill");
			}
		}
		else
			block_mp.setAndGet(true);
	}

	public void unblock_hp_mp(boolean block_hp_)
	{
		unblock_hp_mp(-1, null, block_hp_);
	}

	public void unblock_hp_mp(int obj_id, L2Skill skill, boolean unblock_hp)
	{
		if(unblock_hp)
		{
			block_hp.setAndGet(false);
			blockhpdelay = System.currentTimeMillis();
			if(ConfigValue.DebugParam_Invul && isPlayer())
				Util.test(getName(), skill, "unblock["+obj_id+"]:"+block_hp.get(), "debug_param_invul2_skill");
		}
		else
			block_mp.setAndGet(false);
	}

	public void block()
	{
		_blocked.getAndSet(true);
	}

	public void unblock()
	{
		_blocked.setAndGet(false);
	}

	public void startConfused()
	{
		if(!_confused)
		{
			_confused = true;
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopConfused()
	{
		if(_confused)
		{
			_confused = false;
			updateAbnormalEffect();

			abortAttack(true, true);
			abortCast(true);
			stopMove();
			getAI().setAttackTarget(null);
		}
	}

	public void startFakeDeath()
	{
		if(!_fakeDeath)
		{
			if(isPlayer())
				((L2Player) this).clearHateList(true);
			_fakeDeath = true;
			getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH, null, null);
			broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_START_FAKEDEATH));
			updateAbnormalEffect();
		}
	}

	public void stopFakeDeath()
	{
		if(_fakeDeath)
		{
			_fakeDeath = false;
			broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STOP_FAKEDEATH));
			broadcastPacket(new Revive(this));
			updateAbnormalEffect();
		}
	}

	public void breakFakeDeath()
	{
		getEffectList().stopAllSkillEffects(EffectType.c_fake_death);
		stopFakeDeath();
	}

	public void startFear()
	{
		if(!p_block_controll.getAndSet(true))
		{
			abortAttack(true, true);
			abortCast(true);
			sendActionFailed();
			stopMove();
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopFear()
	{
		if(p_block_controll.setAndGet(false))
			updateAbnormalEffect();
	}

	public void startMuted()
	{
		if(!_muted.getAndSet(true))
		{
			if(getCastingSkill() != null && getCastingSkill().isMagic())
				abortCast(true);
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopMuted()
	{
		if(_muted.setAndGet(false))
			updateAbnormalEffect();
	}

	public void startPMuted()
	{
		if(!_pmuted.getAndSet(true))
		{
			if(getCastingSkill() != null && !getCastingSkill().isMagic())
				abortCast(true);
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopPMuted()
	{
		if(_pmuted.setAndGet(false))
			updateAbnormalEffect();
	}

	public void startAMuted()
	{
		if(!_amuted.getAndSet(true))
		{
			abortCast(true);
			abortAttack(true, true);
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopAMuted()
	{
		if(_amuted.setAndGet(false))
			updateAbnormalEffect();
	}

	public void startSleeping(L2Skill skill)
	{
		if(!_sleeping.getAndSet(true))
		{
			abortAttack(true, true);
			abortCast(true);
			sendActionFailed();
			stopMove();
			startAttackStanceTask();
			updateAbnormalEffect();
		}
		if(ConfigValue.DebugParam_Sleeping && isPlayer())
			Util.test(getName(), skill, "block:"+_sleeping.get(), "debug_param_sleeping");
	}

	public void stopSleeping(L2Skill skill)
	{
		if(!_sleeping.setAndGet(false))
			updateAbnormalEffect();
		if(ConfigValue.DebugParam_Sleeping && isPlayer())
			Util.test(getName(), skill, "unblock:"+_sleeping.get(), "debug_param_sleeping");
	}

	public void startStunning()
	{
		p_block_act.getAndSet(true);
		if(!_stunned.getAndSet(true))
		{
			abortAttack(true, true);
			abortCast(true);
			sendActionFailed();
			stopMove();
			startAttackStanceTask();
			updateAbnormalEffect();
		}
	}

	public void stopStunning()
	{
		p_block_act.setAndGet(false);
		if(!_stunned.setAndGet(false))
			updateAbnormalEffect();
	}

	public void setMeditated(boolean meditated)
	{
		if(meditated)
			_meditated.getAndSet(true);
		else
			_meditated.setAndGet(false);
	}

	public void setParalyzed(boolean paralyzed)
	{
		if(_paralyzed != paralyzed)
		{
			_paralyzed = paralyzed;
			if(paralyzed)
			{
				abortAttack(true, true);
				abortCast(true);
				sendActionFailed();
				stopMove();
			}
		}
	}

	public void setParalyzedSkill(boolean paralyzed)
	{
		if(paralyzed)
		{
			if(!_paralyzed_skill.getAndSet(true)) // тест
			{
				abortAttack(true, true);
				abortCast(true);
				sendActionFailed();
				stopMove();
			}
		}
		else
			_paralyzed_skill.setAndGet(false);
	}

	public void setPetrification(boolean petrification)
	{
		if(petrification)
			_petrification.getAndSet(true);
		else
			_petrification.setAndGet(false);
	}

	public void p_block_move(boolean imobilised, L2Skill skill)
	{
		if(imobilised)
		{
			if(!p_block_move.getAndSet(true))
			{
				stopMove();
				startAttackStanceTask();
				setFollowTarget(null);
			}
		}
		else
			p_block_move.setAndGet(false);

		if(ConfigValue.DebugParam_p_block_move && isPlayer())
			Util.test(getName(), skill, imobilised+":"+p_block_move.get(), "debug_param_p_block_move");

		updateAbnormalEffect();
	}

	/**
	 * if True, the L2Player can't take more item
	 */
	public void setOverloaded(boolean overloaded)
	{
		_overloaded = overloaded;
	}

	public boolean isConfused()
	{
		return _confused;
	}

	public boolean isFakeDeath()
	{
		return _fakeDeath;
	}

	public boolean isAfraid()
	{
		return p_block_controll.get();
	}

	public boolean isBlocked()
	{
		return _blocked.get() || is_block;
	}

	public boolean isMuted(L2Skill skill)
	{
		if(skill == null || skill.isNotAffectedByMute())
			return false;
		return _muted.get() && skill.getMagic() == 1 || _pmuted.get() && skill.getMagic() == 0 || ConfigValue.MutedIfEquipTerFlag && isPlayer() && skill.getId() != 847 && getPlayer().isTerritoryFlagEquipped();
	}

	public boolean isPMuted()
	{
		return _pmuted.get();
	}

	public boolean isMMuted()
	{
		return _muted.get();
	}

	public boolean isAMuted()
	{
		return _amuted.get();
	}

	public boolean isSleeping()
	{
		return _sleeping.get();
	}

	public boolean isStunned()
	{
		return _stunned.get();
	}

	public boolean isActionBlock()
	{
		return p_block_act.get();
	}

	public boolean isMeditated()
	{
		return _meditated.get();
	}

	public boolean isPetrification()
	{
		return _petrification.get();
	}

	public boolean isParalyzed()
	{
		return _paralyzed_skill.get() || _paralyzed;
	}

	public boolean is_block_move()
	{
		return p_block_move.get() || getRunSpeed() < 1;
	}

	public boolean isHealBlocked(boolean check_invul, boolean check_ref)
	{
		return check_invul && (isInvul() || !check_ref && ConfigValue.BloackHealRb && (isRaid() || isBoss() || isEpicRaid()));
	}

	public boolean isCastingNow()
	{
		return _skillTask != null;
	}

	public boolean isMovementDisabled()
	{
		return isSitting() || isActionBlock() || isStunned() || isSleeping() || isParalyzed() || is_block_move() || isAlikeDead() || isAttackingNow() || isCastingNow() || _overloaded || _fishing || isPlayer() && (isTeleporting() || ((L2Player) this).isLogoutStarted());
	}

	public boolean isActionsDisabled()
	{
		//_log.info("isActionsDisabled: isAttackingNow="+isAttackingNow());
		return !GameServer.isLoaded() || isActionBlock() || isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isAlikeDead() || isPlayer() && (isTeleporting() || ((L2Player) this).isLogoutStarted());
	}

	public boolean isPotionsDisabled()
	{
		return isStunned() || isActionBlock() || isSleeping() || isParalyzed() || isAlikeDead() || isPlayer() && (isTeleporting() || ((L2Player) this).isLogoutStarted());
	}

	public boolean isToggleDisabled()
	{
		return isStunned() || isActionBlock() || isSleeping() || isParalyzed() || isPlayer() && (isTeleporting() || ((L2Player) this).isLogoutStarted());
	}

	public final boolean isAttackingDisabled()
	{
		return _attackReuseEndTime > System.currentTimeMillis();
	}

	public boolean isOutOfControl()
	{
		return isConfused() || isAfraid() || isBlocked() || isPlayer() && (isTeleporting() || ((L2Player) this).isLogoutStarted());
	}

	public void teleToLocation(Location loc)
	{
		teleToLocation(loc.x, loc.y, loc.z, getReflection().getId(), 0);
	}

	public void teleToLocation(Location loc, int ref)
	{
		teleToLocation(loc.x, loc.y, loc.z, ref, 0);
	}

	public void teleToLocation(int x, int y, int z)
	{
		teleToLocation(x, y, z, getReflection().getId(), 0);
	}

	public void teleToLocation(int x, int y, int z, int ref)
	{
		teleToLocation(x, y, z, ref, 0);
	}

	public void teleToLocation(int x, int y, int z, int ref, int valid)
	{
		/*if(i_ai0 == 1994576)
		{
			_log.info("L2Character: teleToLocation -> "+toString()+" "+getTeam()+" ["+x+","+y+","+z+","+ref+"]");
			Util.test();
		}*/
		if(isFakeDeath())
			breakFakeDeath();

		if(isTeleporting() || inObserverMode())
			return;

		abortCast(true);

		if(isPlayable())
			clearHateList(true);

		if(!isVehicle() && !isFlying() && !L2World.isWater(x, y, z))
			z = GeoEngine.getHeight(x, y, z, getReflection().getGeoIndex());

		if(isPlayer() && DimensionalRiftManager.getInstance().checkIfInRiftZone(getLoc(), true))
		{
			L2Player player = (L2Player) this;
			if(player.isInParty() && player.getParty().isInDimensionalRift())
			{
				Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
				x = newCoords.x;
				y = newCoords.y;
				z = newCoords.z;
				player.getParty().getDimensionalRift().usedTeleport(player);
			}
		}

		setTarget(null);

		if(isPlayer())
		{
			L2Player player = (L2Player) this;
			if(player.isLogoutStarted())
				return;

			if(player.entering)
			{
				setXYZInvisible(x, y, z);
				return;
			}

			setIsTeleporting(60000);

			decayMe();
			setXYZInvisible(x, y, z);
			if(ref != getReflection().getId())
				setReflection(ref);

			// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
			setLastClientPosition(null);
			setLastServerPosition(null);
			//Util.test();
			player.getListeners().onTeleport(x, y, z, ref);

			player.sendPacket(new TeleportToLocation(player, x, y, z, valid));
			//player.broadcastRelationChanged();

			if(player.getEventMaster() != null)
				player.getEventMaster().onTeleportPlayer(player, x, y, z, ref);

			Object[] script_args = new Object[] { this, new Location(x, y, z) };
			for(ScriptClassAndMethod handler : Scripts.onPlayerTeleport)
				callScripts(handler.scriptClass, handler.method, script_args);
		}
		else
		{
			setXYZ(x, y, z);
			broadcastPacket(new TeleportToLocation(this, x, y, z, valid));
		}
	}

	public void teleToClosestTown()
	{
		teleToLocation(MapRegion.getTeleToClosestTown(this), 0);
	}

	public void teleToSecondClosestTown()
	{
		teleToLocation(MapRegion.getTeleToSecondClosestTown(this), 0);
	}

	public void teleToCastle()
	{
		teleToLocation(MapRegion.getTeleToCastle(this), 0);
	}

	public void teleToFortress()
	{
		teleToLocation(MapRegion.getTeleToFortress(this), 0);
	}

	public void teleToClanhall()
	{
		teleToLocation(MapRegion.getTeleToClanHall(this), 0);
	}

	public void teleToHeadquarter()
	{
		teleToLocation(MapRegion.getTeleToHeadquarter(this), 0);
	}

	public void sendMessage(CustomMessage message)
	{
		sendMessage(message.toString());
	}

	private long _nonAggroTime;

	public long getNonAggroTime()
	{
		return _nonAggroTime;
	}

	public void setNonAggroTime(long time)
	{
		_nonAggroTime = time;
	}

	@Override
	public String toString()
	{
		return "mob " + getObjectId()+" "+getNpcId()+" "+this.getClass();
	}

	@Override
	public float getColRadius()
	{
		return getTemplate().collisionRadius;
	}

	@Override
	public float getColHeight()
	{
		return getTemplate().collisionHeight;
	}

	public boolean canAttackCharacter(L2Character target)
	{
		return target.getPlayer() != null;
	}

	public class HateInfo
	{
		public L2NpcInstance npc;
		public int hate;
		public int damage;

		HateInfo(L2NpcInstance attacker)
		{
			npc = attacker;
		}
	}

	private ConcurrentHashMap<L2NpcInstance, HateInfo> _hateList = null;

	public void addDamage(L2NpcInstance npc, int damage)
	{
		addDamageHate(npc, damage, damage);

		// Добавляем хейта к хозяину саммона
		if(npc.hasAI() && npc.getAI() instanceof DefaultAI && (isSummon() || isPet()) && getPlayer() != null)
			getPlayer().addDamageHate(npc, damage, npc.getTemplate().searchingMaster && getPlayer().isInRange(npc, 2000) ? damage : 1);
	}

	public void addDamageHate(L2NpcInstance npc, int damage, int aggro)
	{
	/*	if(isPlayer())
		{
			_log.info("addDamageHate: damage="+damage+" aggro="+aggro);
			Util.test();
		}*/
		if(npc == null)
			return;

		if(damage < 0 && aggro <= 0)
			return;

		if(damage > 0 && aggro <= 0)
			aggro = damage;

		if(_hateList == null)
			_hateList = new ConcurrentHashMap<L2NpcInstance, HateInfo>();

		HateInfo ai = _hateList.get(npc);

		if(ai != null)
		{
			ai.damage += damage;
			ai.hate += aggro;
			ai.hate = Math.max(ai.hate, 0);
		}
		else if(aggro > 0)
		{
			ai = new HateInfo(npc);
			ai.damage = damage;
			ai.hate = aggro;
			_hateList.put(npc, ai);
		}
	}

	public ConcurrentHashMap<L2NpcInstance, HateInfo> getHateList()
	{
		if(_hateList == null)
			return new ConcurrentHashMap<L2NpcInstance, HateInfo>();
		return _hateList;
	}

	public void removeFromHatelist(L2NpcInstance npc, boolean onlyHate)
	{
		if(npc != null && _hateList != null)
			if(onlyHate)
			{
				HateInfo i = _hateList.get(npc);
				if(i != null)
					i.hate = 0;
			}
			else
				_hateList.remove(npc);
	}

	public void clearHateList(boolean onlyHate)
	{
		if(_hateList != null)
			if(onlyHate)
				for(HateInfo i : _hateList.values())
				{
					L2NpcInstance npc = i.npc;
					if(npc != null && npc.getAI().getAttackTarget() != null && npc.getAI().getAttackTarget().getObjectId() == getObjectId())
					{
						npc.abortAttack(true, true);
						npc.abortCast(true);
						npc.setTarget(null);

						npc.getAI().nextAttack();
					}
					i.hate = 0;
				}
			else
				_hateList = null;
	}

	public EffectList getEffectList()
	{
		if(_effectList == null)
		{
			//synchronized(this)
			{
				if(_effectList == null)
					_effectList = new EffectList(this);
			}
		}
		return _effectList;
	}

	public void setEffectList(EffectList el)
	{
		_effectList = el;
	}

	public boolean isMassUpdating()
	{
		return _massUpdating;
	}

	public void setMassUpdating(boolean updating)
	{
		_massUpdating = updating;
	}

	public void addTrap(L2TrapInstance trap)
	{
		if(_trap != null)
			_trap.destroy();
		_trap = trap;
		_trap.getAI().startAITask();
	}

	public void removeTrap()
	{
		_trap = null;
	}

	public void destroyTrap()
	{
		if(_trap != null)
			_trap.destroy();
		_trap = null;
	}

	public boolean paralizeOnAttack(L2Character attacker)
	{
		// Mystic Immunity Makes a target temporarily immune to raid curce
		if(attacker.getEffectList().getEffectsBySkillId(L2Skill.SKILL_MYSTIC_IMMUNITY) != null || getNpcId() == 29021)
			return false;

		int max_attacker_level = 0xFFFF;

		L2MonsterInstance leader;
		if(isRaid() || (isMinion() && (leader = ((L2MinionInstance) this).getLeader()) != null && leader.isRaid()))
			max_attacker_level = getLevel() + ConfigValue.RaidMaxLevelDiff;
		else if(getAI() instanceof DefaultAI)
		{
			int max_level_diff = ((DefaultAI) getAI()).ParalizeOnAttack;
			if(max_level_diff != -1000)
				max_attacker_level = getLevel() + max_level_diff;
		}

		if(attacker.getLevel() > max_attacker_level)
		{
			if(max_attacker_level > 0)
				attacker.sendMessage(new CustomMessage("l2open.gameserver.model.L2Character.ParalizeOnAttack", attacker).addCharName(this).addNumber(max_attacker_level));
			return true;
		}

		return false;
	}

	public Calculator[] getCalculators()
	{
		return _calculators;
	}

	@Override
	public void deleteMe()
	{
		setTarget(null);
		stopMove();
		stopRegeneration();
		super.deleteMe();
	}

	// ---------------------------- Not Implemented -------------------------------

	public void addExpAndSp(long addToExp, long addToSp)
	{}

	public void addExpAndSp(long addToExp, long addToSp, boolean applyBonus, boolean appyToPet)
	{}

	public void addExpAndSp(long addToExp, long addToSp, boolean applyBonus, boolean appyToPet, long addToExpExVit, long AddToSpExVit, L2MonsterInstance monster)
	{}

	public void broadcastUserInfo(boolean force)
	{}

	public void checkHpMessages(double currentHp, double newHp)
	{}

	public boolean checkPvP(L2Character target, L2Skill skill)
	{
		return false;
	}

	public boolean consumeItem(int itemConsumeId, int itemCount)
	{
		return true;
	}
	
	public boolean consumeItemMp(int itemId, int mp)
	{
		return true;
	}

	public void doPickupItem(L2Object object)
	{}

	public boolean isFearImmune()
	{
		return false;
	}

	public boolean isLethalImmune()
	{
		if(isEpicRaid() || Util.contains(ConfigValue.LethalNoImmune, getNpcId()))
			return true;
		if(ConfigValue.LethalImmuneHp > 0)
			return getMaxHp() >= ConfigValue.LethalImmuneHp;
		else
			return false;
	}

	public boolean getChargedSoulShot()
	{
		return false;
	}

	public int getChargedSpiritShot()
	{
		return 0;
	}

	public Duel getDuel()
	{
		return null;
	}

	public int getIncreasedForce()
	{
		return 0;
	}

	public int getConsumedSouls()
	{
		return 0;
	}

	public int getKarma()
	{
		return 0;
	}

	public double getLevelMod()
	{
		return 1;
	}

	public int getNpcId()
	{
		return 0;
	}

	public L2Summon getPet()
	{
		return null;
	}

	public int getPvpFlag()
	{
		return 0;
	}

	public int getTeam()
	{
		return 0;
	}

	public boolean isSitting()
	{
		return false;
	}

	public boolean isUndead()
	{
		return false;
	}

	public boolean isUsingDualWeapon()
	{
		return false;
	}

	public boolean isParalyzeImmune()
	{
		return false;
	}

	public void reduceArrowCount()
	{}

	public void sendChanges()
	{}

	public void sendMessage(String message)
	{}

	public void sendGMMessage(String message)
	{}

	public void sendPacket(L2GameServerPacket... mov)
	{}

	public void setIncreasedForce(int i)
	{}

	public void setConsumedSouls(int i, L2NpcInstance monster)
	{}

	public void sitDown(boolean force)
	{}

	public void standUp()
	{}

	public void startPvPFlag(L2Character target)
	{}

	public boolean unChargeShots(boolean spirit)
	{
		return false;
	}

	public void updateEffectIcons()
	{}

	public void updateStats()
	{}

	public void callMinionsToAssist(L2Character attacker)
	{}

	public void setOverhitAttacker(L2Character attacker)
	{}

	public void setOverhitDamage(double damage)
	{}

	public boolean hasMinions()
	{
		return false;
	}

	public boolean isCursedWeaponEquipped()
	{
		return false;
	}

	public boolean isHero()
	{
		return false;
	}

	public int isHeroType()
	{
		return -1;
	}

	public int getAccessLevel()
	{
		return 0;
	}

	public void spawnWayPoints(Vector<Location> recorder)
	{}

	public void setFollowStatus(boolean state, boolean changeIntention)
	{}

	public void setLastClientPosition(Location charPosition)
	{}

	public void setLastServerPosition(Location charPosition)
	{}

	public boolean hasRandomAnimation()
	{
		return true;
	}

	public boolean hasRandomWalk()
	{
		return true;
	}

	public int getClanCrestId()
	{
		Integer result = 0;
		if(isCrestEnable())
		{
			Town town = TownManager.getInstance().getClosestTown(this);
			if(town != null && town.getCastle() != null && town.getCastle().getOwner() != null && (town.getCastle().getDominionLord() != 0 || ConfigValue.ShowClanCrestWithoutQuest))
				result = town.getCastle().getOwner().getCrestId();
		}
		return result;
	}

	public int getClanCrestLargeId()
	{
		Integer result = 0;
		if(isCrestEnable())
		{
			Town town = TownManager.getInstance().getClosestTown(this);
			if(town != null && town.getCastle() != null && town.getCastle().getOwner() != null && (town.getCastle().getDominionLord() != 0 || ConfigValue.ShowClanCrestWithoutQuest))
				result = town.getCastle().getOwner().getCrestLargeId();
		}
		return result;
	}

	public int getAllyCrestId()
	{
		Integer result = 0;
		L2Alliance ally;
		if(isCrestEnable())
		{
			Town town = TownManager.getInstance().getClosestTown(this);
			if(town != null && town.getCastle() != null && town.getCastle().getOwner() != null)
			{
				ally = town.getCastle().getOwner().getAlliance();
				if(ally != null)
					result = town.getCastle().getOwner().getAlliance().getAllyCrestId();
			}
		}
		return result;
	}

	public void disableItem(L2Skill handler, long timeTotal, long timeLeft)
	{}

	public double getRateAdena()
	{
		return 1.0d;
	}

	public double getRateItems()
	{
		return 1.0d;
	}

	public float getRateFame()
	{
		return 1.0f;
	}

	public float getRateEpaulette()
	{
		return 1.0f;
	}

	public float getRateMaxLoad()
	{
		return 1.0f;
	}

	public double getRateExp()
	{
		return 1.;
	}

	public double getRateSp()
	{
		return 1.;
	}

	public double getRateSpoil()
	{
		return 1.0d;
	}

	public float getRateChest()
	{
		return 1.0f;
	}

	public int getFormId()
	{
		return 0;
	}

	public boolean isNameAbove()
	{
		return true;
	}

	@Override
	public void setXYZInvisible(int x, int y, int z)
	{
		stopMove();
		super.setXYZInvisible(x, y, z);
	}

	@Override
	public void setLoc(Location loc, boolean MoveTask)
	{
		/*if(isPlayer() && getName().equals("Diagod"))
		{
			_log.info("L2Character: setLoc["+loc.x+":"+loc.y+":"+loc.z+":"+MoveTask+"]");
			//sendMessage("setLoc: "+loc.x+":"+loc.y+":"+loc.z+":"+MoveTask);
			//Util.test();
		}*/

		super.setLoc(loc, MoveTask);
	}

	@Override
	public void setXYZ(int x, int y, int z, boolean MoveTask)
	{
		//if(isPlayer())
		//	sendMessage("setXYZ: "+x+":"+y+":"+z+":"+MoveTask);

		if(!MoveTask)
			stopMove();

		moveLock.lock();
		try
		{
			super.setXYZ(x, y, z, MoveTask);
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public void validateLocation(int broadcast)
	{
		if(isVehicle() || isInVehicle() || isInWater()) // FIXME для кораблей что-то иное
			return;
		L2GameServerPacket sp = new ValidateLocation(this);
		if(broadcast == 0)
			sendPacket(sp);
		else if(broadcast == 1)
			broadcastPacket(sp);
		else
			broadcastPacketToOthers(sp);
	}

	public int getClanId()
	{
		return _clan == null ? 0 : _clan.getClanId();
	}

	// --------------------------- End Of Not Implemented ------------------------------

	// --------------------------------- Abstract --------------------------------------

	public abstract byte getLevel();

	public abstract void updateAbnormalEffect();

	public abstract L2ItemInstance getActiveWeaponInstance();

	public abstract L2Weapon getActiveWeaponItem();

	public abstract L2ItemInstance getSecondaryWeaponInstance();

	public abstract L2Weapon getSecondaryWeaponItem();

	public abstract WeaponType getFistWeaponType();
	// ----------------------------- End Of Abstract -----------------------------------
	// ----------------------------------- Crypt -----------------------------------
	public static class RunOnAttackPet extends l2open.common.RunnableImpl
	{
		private final L2Character character;  //Атакующий
		private final L2Character pet;	//Пет

		public RunOnAttackPet(L2Character cha, L2Character _pet)
		{
			character = cha;
			pet = _pet;
		}

		public void runImpl()
		{
			if(character == null || character.isDead() || pet == null || pet.isDead())
				return;

			L2Character owner = pet.getPlayer(); //Владелец пета.
			if(owner == null)
				return;

			// Бегаем вокруг гозяйна на дистанции 100...
			/*if(pet.getDistance(character) <= 100)
			{
				double angle = Math.toRadians(Rnd.get(130, 170)+Location.calculateAngleFrom(owner, pet));
				int oldX = pet.getX();
				int oldY = pet.getY();
				int x = oldX + (int)(150 * Math.cos(angle));
				int y = oldY + (int)(150 * Math.sin(angle));
				pet.setRunning();
				pet.moveToLocation(GeoEngine.moveCheck(oldX, oldY, pet.getZ(), x, y, pet.getReflection().getGeoIndex()), 0, false);
				//_log.info("RunOnAttackPet: "+GeoEngine.moveCheck(oldX, oldY, pet.getZ(), x, y, pet.getReflection().getGeoIndex()));
			}*/
		}
	}

	public L2Character GetLastAttacker()
	{
		return _lastAtacker;
	}

	public int isUnDying()
	{
		return 0;
	}

	public boolean isShowName()
	{
		return _showName;
	}

	public boolean isShowTitle()
	{
		return _showTitle;
	}

	public void addBlockBuffSlot(SkillAbnormalType[] sat)
	{
		if(_block_buff_slot == null)
			_block_buff_slot = new ArrayList<SkillAbnormalType>();
		for(SkillAbnormalType s : sat)
			_block_buff_slot.add(s);
		for(L2Effect e : getEffectList().getAllEffects())
			for(SkillAbnormalType s : sat)
				if(e.getAbnormalType() == s)
				{
					e.setCanDelay(false);
					e.exit(false, false);
				}
		updateEffectIcons();
	}

	public void removeBlockBuffSlot(SkillAbnormalType[] sat)
	{
		if(_block_buff_slot != null)
			for(SkillAbnormalType s : sat)
				_block_buff_slot.remove(s);
	}

	public List<SkillAbnormalType> getListBlockBuffSlot()
	{
		return _block_buff_slot;
	}

	public void addBlockSkill(Integer[] sat)
	{
		if(_block_skill_id == null)
			_block_skill_id = new ArrayList<Integer>();
		for(Integer s : sat)
			_block_skill_id.add(s);
	}

	public void removeBlockSkill(Integer[] sat)
	{
		if(_block_skill_id != null)
		{
			for(Integer s : sat)
				if(!_block_skill_id.remove(s));
			if(_block_skill_id.size() == 0)
				_block_skill_id = null;
		}
	}

	public List<Integer> getListBlockSkill()
	{
		return _block_skill_id;
	}

	public L2Party getParty()
	{
		return null;
	}

	public int alive()
	{
		return isDead() ? 0 : 1;
	}

	public int class_id()
	{
		return getNpcId();
	}

	public int getActiveClassId()
	{
		return -1;
	}

	public Race getRace()
	{
		return isNpc() ? ((L2NpcTemplate)getBaseTemplate())._race : ((L2PlayerTemplate)getBaseTemplate()).race;
	}

	public int builder_level = 0;
	
	private boolean _bot = false;
	public void setBot(boolean isBot)
	{
		_bot = isBot;
	}
	public boolean isBot()
	{
		return _bot;
	}
	// ----
	private boolean _bot2 = false;
	public void setBot2(boolean isBot)
	{
		_bot2 = isBot;
	}
	public boolean isBot2()
	{
		return _bot2;
	}

	// ----
	public int _botAtack1 = 0;
	public int _botAtack2 = 0;

	public boolean p_ignore_skill_freya = false;

	public void setNpcState(int i)
	{
		if(npcState != i)
		{
			npcState = i;
			broadcastPacketToOthers(new ExChangeNpcState(getObjectId(), i));
		}
	}

	public int getNpcState()
	{
		return npcState;
	}

	private TraitStat _trait_stat;
	public TraitStat getTraitStat()
	{
		if(_trait_stat == null)
			_trait_stat = new TraitStat();
		return _trait_stat;
	}

	private EventMaster _event_master;
	public EventMaster getEventMaster()
	{
		return _event_master;
	}
	public void setEventMaster(EventMaster em)
	{
		_event_master = em;
	}

	public int getDisplayId()
	{
		return 0;
	}

	public int getAgathionEnergy()
	{
		return 0;
	}

	public void setAgathionEnergy(int val)
	{}

	public boolean _blessed = false;
	public boolean is_block = false;

	public boolean old_dam_message = false;
	public boolean show_damage = ConfigValue.EnableDamageOnScreenOld;

	// public DamageTextNewPacket(int victim_id, int skill_id, int skill_lvl, int color1, int color2, String message)
	public void sendRDmgMsg(L2Character target, L2Character attacker, L2Skill skill, long damage, boolean crit, boolean miss)
	{
		if(ConfigValue.EnableDamageOnScreen)
		{
			int skill_id = skill == null ? 0 : skill.getId();
			int skill_lvl = skill == null ? 0 : skill.getLevel();
			sendPacket(new DamageTextNewPacket(target.getObjectId(), skill_id, skill_lvl, ConfigValue.DamageOnScreenColorRSkillName, ConfigValue.DamageOnScreenColorRDmgMsg, String.valueOf(damage)));
		}
		else if(ConfigValue.EnableDamageOnScreenOld && show_damage)
		{
			sendPacket(new DamageTextPacket(target.getObjectId(), damage, crit, miss, false, false, ConfigValue.DamageOnScreenFontId, ConfigValue.DamageOnScreenColorRDmgMsg, skill == null || ConfigValue.DamageOnScreenOldShowNotSkillIcon ? "" : skill.getName(), skill == null || ConfigValue.DamageOnScreenOldShowNotSkillIcon ? "" : skill.getIcon(), ConfigValue.DamageOnScreenRPosX, ConfigValue.DamageOnScreenRPosY, ConfigValue.DamageOnScreenRSizeX, ConfigValue.DamageOnScreenRSizeY));
		}
		if(old_dam_message)
			sendPacket(new SystemMessage(SystemMessage.C1_HIT_YOU_FOR_S2_DAMAGE).addName(attacker).addNumber(damage));
		else
			sendPacket(new SystemMessage(SystemMessage.C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2).addName(target).addName(attacker).addNumber(damage).addDamage(attacker, attacker, damage));
	}

	public void sendHDmgMsg(L2Character attacker, L2Character target, L2Skill skill, long damage, boolean crit, boolean miss)
	{
		if(ConfigValue.EnableDamageOnScreen)
		{
			int skill_id = skill == null ? 0 : skill.getId();
			int skill_lvl = skill == null ? 0 : skill.getLevel();
			sendPacket(new DamageTextNewPacket(target.getObjectId(), skill_id, skill_lvl, ConfigValue.DamageOnScreenColorHSkillName, ConfigValue.DamageOnScreenColorHDmgMsg, String.valueOf(damage)));
		}
		else if(ConfigValue.EnableDamageOnScreenOld && show_damage)
		{
			sendPacket(new DamageTextPacket(target.getObjectId(), damage, crit, miss, false, false, ConfigValue.DamageOnScreenFontId, ConfigValue.DamageOnScreenColorHDmgMsg, skill == null || ConfigValue.DamageOnScreenOldShowNotSkillIcon ? "" : skill.getName(), skill == null || ConfigValue.DamageOnScreenOldShowNotSkillIcon ? "" : skill.getIcon(), ConfigValue.DamageOnScreenHPosX, ConfigValue.DamageOnScreenHPosY, ConfigValue.DamageOnScreenHSizeX, ConfigValue.DamageOnScreenHSizeY));
		}
		if(old_dam_message)
			sendPacket(new SystemMessage(SystemMessage.YOU_HIT_FOR_S1_DAMAGE).addNumber(damage));
		else
			sendPacket(new SystemMessage(SystemMessage.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(attacker).addName(target).addNumber(damage).addDamage(target, target, damage));
	}

	@Override
	public boolean isCharacter()
	{
		return true;
	}

	private boolean fake=false;
	public boolean isFake()
	{
		return fake;
	}

	public void setIsFake()
	{
		fake=true;
	}

	public final Lock moveLock = new ReentrantLock();

	public void DeleteAbnormalStatus2(boolean break_, boolean crit)
	{
		if(isSleeping())
			getEffectList().stopEffect(SkillAbnormalType.sleep);

		if(isMeditated())
			getEffectList().stopEffect(SkillAbnormalType.force_meditation);

		if(break_)
			return;

		if(isStunned() && Formulas.calcStunBreak(crit))
			getEffectList().stopEffect(SkillAbnormalType.stun);
		if(ConfigValue.BreakRealTarget && Rnd.get(100) < 3)
			getEffectList().stopEffect(SkillAbnormalType.real_target);
	}
	// ----
	public Location getIntersectionPoint(L2Character target)
	{
		if(!Util.isFacing(this, target, 90))
			return new Location(target.getX(), target.getY(), target.getZ());
		double angle = Util.convertHeadingToDegree(target.getHeading()); // угол в градусах
		double radian = Math.toRadians(angle - 90); // угол в радианах
		double range = target.getMoveSpeed() / 2; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
		if(isPlayable() && !isPlayer()) // Ебать, че за хуйню я сделал?
			return new Location((int) (target.getX() - range * Math.sin(radian)) + Rnd.get(-40, 40), (int) (target.getY() + range * Math.cos(radian)) + Rnd.get(-40, 40), target.getZ());
		return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
	}

	public Location applyOffset(Location point, int offset)
	{
		if(offset <= 0)
			return point;

		long dx = point.x - getX();
		long dy = point.y - getY();
		long dz = point.z - getZ();

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if(distance <= offset)
		{
			point.set(getX(), getY(), getZ());
			return point;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			point.x -= (int) Math.ceil(dx * cut);
			point.y -= (int) Math.ceil(dy * cut);
			point.z -= (int) Math.ceil(dz * cut);

			if(!isFlying() && !isInVehicle() && !isSwimming() && !isVehicle())
				point.correctGeoZ();
		}

		return point;
	}

	public List<Location> applyOffset(List<Location> points, int offset)
	{
		offset = offset >> 4;
		if(offset <= 0)
			return points;

		long dx = points.get(points.size() - 1).x - points.get(0).x;
		long dy = points.get(points.size() - 1).y - points.get(0).y;
		long dz = points.get(points.size() - 1).z - points.get(0).z;

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if(distance <= offset)
		{
			Location point = points.get(0);
			points.clear();
			points.add(point);
			return points;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			int num = (int) (points.size() * cut + 0.5f);
			for(int i = 1; i <= num && points.size() > 0; i++)
				points.remove(points.size() - 1);
			//if(isPlayer())
			//	_log.info("distance="+distance+" offset="+offset+" cut="+cut+" num="+num+" points="+points.size());
		}

		return points;
	}

	public boolean setSimplePath(Location dest)
	{
		_constructMoveList.clear();
		GeoMove.constructMoveList(getLoc(), dest, _constructMoveList);
		if(_constructMoveList.isEmpty())
			return false;
		_targetRecorder.clear();
		_targetRecorder.add(_constructMoveList);
		/*if(isPlayer())
		{
			//Util.test();
			_log.info("target_recorder_add1: "+_constructMoveList);
		}*/
		return true;
	}

	public boolean notFullPath(int x, int y, int z, int offset)
	{
        Location dest = new Location(x, y, z);
        int geoIndex = getGeoIndex();
        applyOffset(dest, offset);
		List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, false, isPlayer(), 0, /*isWater ? getWaterZ() : */Integer.MIN_VALUE);
        if(moveList != null && !moveList.isEmpty())
		{
            _targetRecorder.clear();
            _targetRecorder.add(moveList);
            return true;
        }
        return false;
    }

	// пиздец как грузит ЦП...this.moveToLocation - > this.buildPathTo -> GeoMove.findMovePath -> GeoMove.findPath -> PathFind<init> PathFind.findPath -> PathFind.handleNode -> PathFind.getNeighbour -> -> ->
	public boolean buildPathTo(int dest_x, int dest_y, int dest_z, int offset, boolean pathFind, boolean _follow, boolean forestalling, L2Character follow)
	{
		if(ConfigValue.NewGeoEngine)
			return buildPathTo_v2(dest_x, dest_y, dest_z, offset, follow, forestalling, pathFind);
		return buildPathTo_v1(dest_x, dest_y, dest_z, offset, pathFind, _follow);
	}

	public boolean buildPathTo_v2(int x, int y, int z, int offset, L2Character follow, boolean forestalling, boolean pathFind)
	{
		int geoIndex = getGeoIndex();
		Location dest = forestalling && follow != null && follow.isMoving ? getIntersectionPoint(follow) : new Location(x, y, z);
		if(isInVehicle() || isVehicle())
		{
			applyOffset(dest, offset);
			return setSimplePath(dest);
		}
		/*if (isFlying() || isInWater())
		{
            applyOffset(dest, offset);
            Location waterloc = null;
            if (isFlying())
			{
                if (GeoEngine.canSeeCoord(this, dest.x, dest.y, dest.z, true))
                    return setSimplePath(dest);

                Location nextloc = isObservePoint() ? GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, 15000, geoIndex) : GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
                if (nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
                    return setSimplePath(nextloc);

            }
			else
			{
                int waterZ = getWaterZ();
                Location nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, waterZ, geoIndex);
                if (nextloc == null)
                    return false;

                int waterBottomZ = getWaterBottomZ();
                if (dest.z < waterBottomZ)
                    return false;

                List<Location> moveList = GeoMove.constructMoveList(getLoc(), nextloc.clone());
                _targetRecorder.clear();
                if (!moveList.isEmpty())
                    _targetRecorder.add(moveList);

                int earthZ = GeoEngine.getHeight(dest, geoIndex);
                int dz = Math.max(dest.z - waterZ, nextloc.z - waterZ);
                if (dz > 0)
				{
                    waterloc = GeoEngine.MoveWaterCheck(nextloc.x, nextloc.y, waterZ, dest.x, dest.y, geoIndex);
                    if (waterloc == null)
                        return false;

                    moveList = GeoMove.constructMoveList(nextloc, waterloc.clone());
                    if (!moveList.isEmpty())
                        _targetRecorder.add(moveList);
                }
                if(waterloc == null)
                    waterloc = nextloc.clone();
                if(!(earthZ <= waterloc.z && Math.abs(dest.z - earthZ) >= 80 || (moveList = GeoEngine.MoveList(waterloc.x, waterloc.y, waterloc.z, dest.x, dest.y, geoIndex, false)) == null || moveList.isEmpty()))
                    _targetRecorder.add(moveList);
                return !_targetRecorder.isEmpty();
            }
            return false;
		}*/
		boolean isWater = isInWater() || L2World.isWater(dest.x, dest.y, dest.z);
		if(isFlying())
		{
			applyOffset(dest, offset);
			Location nextloc;
			if(isFlying())
				nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
			else
				nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getWaterZ(), geoIndex, isPlayer());
			if(nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
			{
				//if(isPlayer())
				//	_log.info("moveInWaterCheck["+isInWater()+"]["+L2World.isWater(dest.x, dest.y, dest.z)+"]["+isSwimming()+"]: getZ="+getZ()+" dest.z="+dest.z+" getWaterZ"+getWaterZ()+" nextloc.z="+nextloc.z);
				return setSimplePath(nextloc);
			}
			return false;
		}
		List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, true, isPlayer(), 0, isWater ? getWaterZ() : Integer.MIN_VALUE); // onlyFullPath = true - проверяем весь путь до конца
        if (moveList != null && (/*!Config.PATHFIND_ONLY && !isPlayer() || */isPlayer() && !isPathFind || !pathFind))
		{
            if (moveList.isEmpty())
                return false;

            applyOffset(moveList, offset);
            if (moveList.isEmpty())
                return false;

            _targetRecorder.clear();
            _targetRecorder.add(moveList);
            correction = /*Config.PRECISE_COORD && */isPlayer() && moveList != null && !moveList.isEmpty() ? (offset > 0 ? moveList.get(moveList.size() - 1).clone().correctNoffset(dest, getLoc(), offset) : moveList.get(moveList.size() - 1).clone().correctNz(dest)) : null;
            isPathFind = false;
            return true;
        }

		List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.x, dest.y, dest.z, this, geoIndex);
        if (pathFind && !targets.isEmpty())
		{
            moveList = targets.remove(targets.size() - 1);
            applyOffset(moveList, offset);
            if (!moveList.isEmpty())
                targets.add(moveList);

            if (!targets.isEmpty())
			{
                _targetRecorder.clear();
                _targetRecorder.addAll(targets);
                correction = /*Config.PRECISE_COORD && */isPlayer() && moveList != null && !moveList.isEmpty() ? moveList.get(moveList.size() - 1).clone().correctNz(dest) : null;
                isPathFind = true;
                return true;
            }
        }
        if (isPlayable())
		{
            applyOffset(dest, offset);
			moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, false, isPlayer(), 1, isWater ? getWaterZ() : Integer.MIN_VALUE); // onlyFullPath = false - идем до куда можем
            if (moveList != null && !moveList.isEmpty())
			{
                _targetRecorder.clear();
                _targetRecorder.add(moveList);
                correction = /*Config.PRECISE_COORD && */isPlayer() && moveList != null && !moveList.isEmpty() ? moveList.get(moveList.size() - 1).clone().correctNworld(dest) : null;
                isPathFind = false;
                return true;
            }
        }
        return false;
    }

	public boolean buildPathTo_v1(int dest_x, int dest_y, int dest_z, int offset, boolean pathFind, boolean _follow)
	{
		int geoIndex = getReflection().getGeoIndex();

		Location dest;

		if(_forestalling && isFollow && getFollowTarget() != null && getFollowTarget().isMoving)
			dest = getIntersectionPoint(getFollowTarget());
		else if(isPlayable() && !isPlayer())
			dest = new Location(dest_x+Rnd.get(-40, 40), dest_y+Rnd.get(-40, 40), dest_z);
		else
			dest = new Location(dest_x, dest_y, dest_z);

		if(isInVehicle() || isVehicle())
		{
			applyOffset(dest, offset);
			return setSimplePath(dest);
		}

		boolean isWater = isInWater() || L2World.isWater(dest.x, dest.y, dest.z);
		
		//if(isFlying() || isWater && ConfigValue.WaterTest)
		if(isFlying())
		{
			applyOffset(dest, offset);

			Location nextloc;

			if(isFlying())
				nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
			else
				nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getWaterZ(), geoIndex, isPlayer());
			if(nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
			{
				//if(isPlayer())
				//	_log.info("moveInWaterCheck["+isInWater()+"]["+L2World.isWater(dest.x, dest.y, dest.z)+"]["+isSwimming()+"]: getZ="+getZ()+" dest.z="+dest.z+" getWaterZ"+getWaterZ()+" nextloc.z="+nextloc.z);
				return setSimplePath(nextloc);
			}

			return false;
		}
		// Если мы в воде и подымаемся вверх, то каждые 800мс, нужно отправлять пакет МовеТуЛокатион
		//else if(isWater)
		//{
		//	Location nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getWaterZ(), geoIndex, isPlayer());
		//	if(nextloc == null)
		//		return false;

		//	setSimplePath(nextloc.clone());

		//	int dz = dest.z - nextloc.z;
			// если пытаемся выбратся на берег, считаем путь с точки выхода до точки назначения
		//	if(dz > 0 && dz < 128)
		//	{
				//moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
		//		moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, false, isPlayer(), 0, isWater ? getWaterZ() : Integer.MIN_VALUE); // onlyFullPath = true - проверяем весь путь до конца
		//		if(moveList != null) // null - до конца пути дойти нельзя
		//		{
		//			if(!moveList.isEmpty()) // уже стоим на нужной клетке
		//				_targetRecorder.add(moveList);
		//		}
		//	}
		//	return !_targetRecorder.isEmpty();
		//}
		List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, true, isPlayer(), 0, isWater ? getWaterZ() : Integer.MIN_VALUE); // onlyFullPath = true - проверяем весь путь до конца
		if(moveList != null) // null - до конца пути дойти нельзя
		{
			//if(isPlayer())
			//{
			//	if(moveList.size() > 0)
			//	{
			//		//Util.test();
			//		for(Location l : moveList)
			//			_log.info("l1: "+l);
			//	}
			//}
			if(moveList.isEmpty()) // уже стоим на нужной клетке
			{
				//if(isPlayer())
				//	_log.info("buildPathTo 1");
				return false;
			}
			applyOffset(moveList, offset);
			if(moveList.isEmpty()) // уже стоим на нужной клетке
			{
				//if(isPlayer())
				//	_log.info("buildPathTo 2");
				return false;
			}
			//if(isPlayer())
			//if(getNpcId() == 31360)
			//	for(Location c : moveList)
			//		_log.info("L2Character: MoveList->: moveList1="+c.clone().geo2world());

			_targetRecorder.clear();
			_targetRecorder.add(moveList);
			//if(isPlayer())
			//	_log.info("target_recorder_add2: "+moveList);
			return true;
		}
		//else if(isPlayer())
		//	_log.info("buildPathTo: moveList == null");
		if(!ConfigValue.GeodataEnabled)
        {
			applyOffset(dest, offset);
			setSimplePath(dest);
			//if(isPlayer())
			//	_log.info("buildPathTo 3");
            return true;
		}
		if(pathFind)
		{
            //List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.clone(), this, true, geoIndex);
			List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.x, dest.y, dest.z, this, geoIndex);
			if(!targets.isEmpty())
			{
				moveList = targets.remove(targets.size() - 1);
				//if(isPlayer())
				//{
				//	for(Location l : moveList)
				//		_log.info("l2: "+l);
				//}
				applyOffset(moveList, offset);
				if(!moveList.isEmpty())
					targets.add(moveList);
				if(!targets.isEmpty())
				{
					_targetRecorder.clear();
					_targetRecorder.addAll(targets);
					//if(isPlayer())
					//	_log.info("target_recorder_add_all: "+targets);
					return true;
				}
			}
		}

		if(_follow)
			return false;

		applyOffset(dest, offset);

		moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, dest.z, geoIndex, false, isPlayer(), 1, isWater ? getWaterZ() : Integer.MIN_VALUE); // onlyFullPath = false - идем до куда можем
		//if(isPlayer())
		//{
		//	for(Location l : moveList)
		//		_log.info("l3: "+l);
		//}

		if(moveList != null && !moveList.isEmpty()) // null - нет геодаты, empty - уже стоим на нужной клетке
		{
			//if(isPlayer())
			//if(getNpcId() == 31360)
			//for(Location c : moveList)
			//	_log.info("L2Character: MoveList->: moveList2="+c.clone().geo2world());

			//if(isPlayer())
			//{
			//	if(moveList.size() > 0)
			//	{
			//		//Util.test();
			//		for(Location l : moveList)
			//			_log.info("l2: "+l);
			//	}
			//}
			_targetRecorder.clear();
			_targetRecorder.add(moveList);
			//if(isPlayer())
			//	_log.info("target_recorder_add3: "+moveList);
			return true;
		}

		//if(isPlayer())
		//	_log.info("buildPathTo 4");
		return false;
	}

	public boolean followToCharacter(L2Character target, int offset, boolean forestalling, boolean path_find)
	{
		if(ConfigValue.NewGeoEngine)
			return followToCharacter_v2(target, offset, forestalling);	
		return followToCharacter_v1(target, offset, forestalling, path_find);
	}

	public boolean followToCharacter_v2(L2Character target, int offset, boolean forestalling)
	{
        return followToCharacter_v2(target.getLoc(), target, offset, forestalling);
    }

    public boolean followToCharacter_v2(Location loc, L2Character target, int offset, boolean forestalling)
	{
        moveLock.lock();
        try
		{
            if(isMovementDisabled() || target == null || isInVehicle())
                return false;

			offset = Math.max(offset, 10);

            if(isFollow && target == getFollowTarget() && offset == _offset)
                return false;

			 getAI().clearNextAction(); // выше???

            if(Math.abs(getZ() - target.getZ()) > 1000 && !isFlying())
			{
               sendPacket(Msg.CANNOT_SEE_TARGET());
                return false;
            }

			stopMove(false, false, false, false);

			if(!buildPathTo(loc.x, loc.y, loc.z, offset, (!isPlayer() || ConfigValue.AllowFollowAttack), !target.isDoor(), forestalling, target))
                return false;

            movingDestTempPos.set(loc.x, loc.y, loc.z);
            isMoving = true;
            isFollow = true;
            _forestalling = forestalling;
            _offset = offset;
            setFollowTarget(target);
            moveNext(true);
            return true;
        }
        finally
		{
            moveLock.unlock();
        }
    }
	private boolean followToCharacter_v1(L2Character target, int offset, boolean forestalling, boolean path_find)
	{
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 1");
		//if(isPlayer())
		//	Util.test();

		moveLock.lock();
		try
		{
			offset = Math.max(offset, 10);
			if(isFollow && target == getFollowTarget() && offset == _offset)
				return true;

			getAI().clearNextAction();
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 2");

			if(isMovementDisabled() || target == null || isInVehicle())
			{
				stopMove();
				return false;
			}
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 3");

			if(Math.abs(getZ() - target.getZ()) > 1000 && !isFlying())
			{
				stopMove();
				sendPacket(Msg.CANNOT_SEE_TARGET());
				//sendMessage("CANNOT_SEE_TARGET() 6");
				return false;
			}
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 4");

			//stopMove(false, false);
			if(_moveTask != null)
			{
				_moveTask.cancel(false);
				_moveTask = null;
			}
			if(_moveWaterTask != null)
			{
				_moveWaterTask.cancel(false);
				_moveWaterTask = null;
			}

			//TODO сравнить с ним и без 
			//broadcastPacket(new StopMove(this));
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 5");

			isFollow = true;
			setFollowTarget(target);
			_forestalling = forestalling;

			if(buildPathTo(target.getX(), target.getY(), target.getZ(), offset, (!isPlayer() || ConfigValue.AllowFollowAttack), !target.isDoor(), true, target))
				movingDestTempPos.set(target.getX(), target.getY(), target.getZ());
			else
			{
				isFollow = false;
				return false;
			}
		//if(isPlayer())
		//	_log.info("L2Character: followToCharacter 6");

			_offset = offset;
			moveNext(true);
			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding)
	{
		return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, false);
	}

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding)
	{
		return moveToLocation(x_dest, y_dest, z_dest, offset, pathfinding, false);
	}

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean follow)
	{
		if(ConfigValue.NewGeoEngine)
			return moveToLocation_v2(x_dest, y_dest, z_dest, offset, pathfinding);
		return moveToLocation_v1(x_dest, y_dest, z_dest, offset, pathfinding, follow);
	}

	public boolean moveToLocation_v2(Location loc, int offset, boolean pathfinding)
	{
		return moveToLocation_v2(loc.x, loc.y, loc.z, offset, pathfinding);
	}

    public boolean moveToLocation_v2(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding)
	{
        moveLock.lock();
        try
		{
            offset = Math.max(offset, 0);
            Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
            if(isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc))
			{
                sendActionFailed();
                return true;
            }

			getAI().clearNextAction(); // выше???

            if(isMovementDisabled())
			{
				getAI().setNextAction(nextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
                sendActionFailed();
                return false;
            }

            if (isPlayer())
                getAI().changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);

			stopMove(false, false, false, false);

			if(!buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding, false, false, null))
			{
                if(isNpc() && notFullPath(x_dest, y_dest, z_dest, offset))
				{
                    movingDestTempPos.set(dst_geoloc);
					getListeners().onMove(dst_geoloc.geo2world());
                    isMoving = true;
                    moveNext(true);
                    return false;
                }
                sendActionFailed();
                return false;
            }
            movingDestTempPos.set(dst_geoloc);
			getListeners().onMove(dst_geoloc.geo2world());
            isMoving = true;
            moveNext(true);
            return true;
        }
        finally
		{
            moveLock.unlock();
        }
    }

	private boolean moveToLocation_v1(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean follow)
	{
		//if(isPlayer())
		//	_log.info("L2Character: moveToLocation 1");
		//if(isPlayer())
		//	Util.test();

		//if(isPlayer())
		//{
		//	sendMessage("-- moveToLocation["+x_dest+":"+y_dest+":"+z_dest+":"+offset+"] --");
		//	_log.info("------ moveToLocation["+x_dest+":"+y_dest+":"+z_dest+":"+offset+"] ------");
		//}
		//if(isPlayable() && !isPlayer() && p_block_controll)
		//	Util.test();
		//if(isPlayer())
		//	GeoEditorConnector.getInstance().getGMs().add(getPlayer());
		moveLock.lock();
		try
		{
			offset = Math.max(offset, 0);
			Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
			if(isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc))
			{
				sendActionFailed();
				return true;
			}

			getAI().clearNextAction();

			if(isMovementDisabled())
			{
				getAI().setNextAction(nextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
				sendActionFailed();
				return false;
			}

			//isFollow = false;

			//if(_moveTask != null)
			//{
			//	_moveTask.cancel(false);
			//	_moveTask = null;
			//}
			//if(_moveWaterTask != null)
			//{
			//	_moveWaterTask.cancel(false);
			//	_moveWaterTask = null;
			//}

			//TODO сравнить с ним и без 
			//broadcastPacket(new StopMove(this));

			if(isPlayer())
				getAI().changeIntention(AI_INTENTION_ACTIVE, null, null);
			
			stopMove(false, false, false, false);

			if(buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding, false, false, null))
			{
				movingDestTempPos.set(dst_geoloc);
				getListeners().onMove(dst_geoloc.geo2world());
			}
			else
			{
				isMoving = false;
				sendActionFailed();
				return false;
			}

			// ADD_FF
			//if(isPlayer())
			//	fantoms.FantomsManager.getInstance().writePlayerAI(getPlayer(), new fantoms.ai.externalizable.MoveToSerializable(x_dest, y_dest, z_dest));
			isMoving = true;
			moveNext(true, x_dest, y_dest, z_dest);
			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public void moveNext(boolean firstMove)
	{
		if(ConfigValue.NewGeoEngine)
			moveNext_v2(firstMove);
		else
			moveNext_v1(firstMove);
	}

	private void moveNext(final boolean firstMove, int x_dest, int y_dest, final int z_dest)
	{
		if(ConfigValue.NewGeoEngine)
			moveNext_v2(firstMove);
		else
			moveNext_v1(firstMove, x_dest, y_dest, z_dest);
	}

	private void moveNext_v2(boolean firstMove)
	{
		/*if(firstMove)
		{
			if(getNpcId() == 22851 || isPlayer())
				_log.info("L2Character: start->: "+getLoc());
		}*/
        if(!isMoving || isMovementDisabled())
		{
            stopMove();
            return;
        }

        _previousSpeed = getMoveSpeed();
        if (_previousSpeed <= 0)
		{
            stopMove();
            return;
        }

		if(!firstMove)
		{
			Location dest = destination;
			if(dest != null)
				setLoc(dest, true);
		}

        if(_targetRecorder.isEmpty())
		{
			//if(getNpcId() == 22851 || isPlayer())
			//	_log.info("L2Character: EVT_ARRIVED->: ["+destination.x+"]["+destination.y+"]["+destination.z+"]");

			if(isFollow)
				ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 1, null));
			else
			{
				ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED, null, null));
			}
			stopMove(isPlayer(), false, false, false);
            return;
        }
		double distance;
        moveList = _targetRecorder.remove(0);
        Location begin = getLoc();
        Location end = moveList.get(moveList.size() - 1).clone().geo2world();
        destination = GeoEngine.isTheSameBlock(correction, end) ? correction : end;
        double d = distance = isFlying() || isInWater() ? begin.distance3D(destination) : begin.distance(destination);

		if(distance != 0)
			try
			{
				if(isFollow && getFollowTarget() != null)
					setHeading(Util.calculateHeadingFrom(this, getFollowTarget()));
				else
					setHeading(Util.calculateHeadingFrom(getX(), getY(), destination.x, destination.y));
			}
			catch(Exception e)
			{
				setHeading(Util.calculateHeadingFrom(getX(), getY(), 0, 0));
			}
			
		broadcastMove(firstMove, 0, 0, 0, 0, false, false);
        _startMoveTime = _followTimestamp = System.currentTimeMillis();
		_moveTask = ThreadPoolManager.getInstance().scheduleMV(_moveTaskRunnable.setDist(distance, begin), getMoveTickInterval());
		/*boolean isWater = isSwimming() || isInWater();
		if(isWater && firstMove && z_dest >= getZ()-16)
		{
			_moveWaterTask = ThreadPoolManager.getInstance().scheduleMV(new WaterTaskZ(this, z_dest), 800);
		}*/
    }

	public void moveNext_v1(boolean firstMove)
	{
		moveNext_v1(firstMove, 0, 0, 0);
	}
	/**
		* должно вызыватся только из synchronized(_targetRecorder)
	 * @param firstMove
	 */
	private void moveNext_v1(final boolean firstMove, int x_dest, int y_dest, final int z_dest)
	{
		_previousSpeed = getMoveSpeed();
		if(_previousSpeed <= 0)
		{
			stopMove();
			return;
		}

		if(!firstMove)
		{
			Location dest = destination;
			if(dest != null)
				setLoc(dest, true);
		}

		double distance;

		synchronized (_targetRecorder)
		{
			if(_targetRecorder.isEmpty())
			{
				isMoving = false;
				destination = null;
				if(isFollow)
				{
					isFollow = false;
					ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED_TARGET, 1, null));
				}
				else
				{
					ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_ARRIVED, null, null));
				}

				//if(isBot())
				//	_log.info("moveNext: "+isFollow);
				validateLocation(isPlayer() ? 2 : 1);
				//if(isPlayer())
				//	GeoEditorConnector.getInstance().getGMs().remove(getPlayer());
				return;
			}
			//if(isBot())
			//	_log.info("moveNext: 2 "+isFollow);

			moveList = _targetRecorder.remove(0);
			if(isPlayer())
			{
				//if(moveList.size() > 0)
				//{
					//Util.test();
				//	for(Location l : moveList)
				//		_log.info("l4: "+l);
				//}
			}
			Location begin = moveList.get(0).clone().geo2world();
			Location end = moveList.get(moveList.size() - 1).clone().geo2world();
			destination = end;
			// distance = begin.distance3D(end);
			distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end); //клиент при передвижении не учитывает поверхность

			isMoving = true;
		}
		
		if(distance != 0)
			try
			{
				setHeading(Util.calculateHeadingFrom(getX(), getY(), destination != null ? destination.x : 0, destination != null ? destination.y : 0));
			}
			catch(Exception e)
			{
				setHeading(Util.calculateHeadingFrom(getX(), getY(), 0, 0));
			}
			
		broadcastMove(firstMove, x_dest, y_dest, z_dest, 0, false, false);
		_startMoveTime = _followTimestamp = System.currentTimeMillis();
		_moveTask = ThreadPoolManager.getInstance().scheduleMV(_moveTaskRunnable.setDist(distance, null), getMoveTickInterval());
		boolean isWater = isSwimming() || isInWater();
		if(isWater && firstMove && z_dest >= getZ()-16)
		{
			_moveWaterTask = ThreadPoolManager.getInstance().scheduleMV(new WaterTaskZ(this, z_dest), 800);
		}
	}
	public int getMoveTickInterval()
	{
		if(ConfigValue.NewGeoEngine)
			return (int) ((isPlayer() ? 8000 : 4000) / Math.max(getMoveSpeed(), 1));
		return (int) ((isPlayer() ? 16000 : 32000) / Math.max(getMoveSpeed(), 1));
	}

	public void broadcastMove(boolean firstMove, int x_dest, int y_dest, int z_dest, int pawn_dist, boolean path_find, boolean follow)
	{
		/*if(i_ai0 == 1994576)
		{
			_log.info("L2Character: broadcastMove["+firstMove+"] -> "+toString()+" ["+x_dest+","+y_dest+","+z_dest+","+pawn_dist+"]");
			Util.test();
		}*/
		//if(isPlayer())
		//	Util.test();
		if(isAirShip())
			broadcastPacket(new ExMoveToLocationAirShip((L2AirShip) this, getLoc(), getDestination()));
		else if(isShip())
			broadcastPacket(new VehicleDeparture((L2Ship) this));
		else
		{
			validateLocation(isPlayer() ? 2 : 1);
			//if(firstMove)
			//	broadcastPacket(new CharMoveToLocation(getObjectId(), getX(), getY(), getZ(), x_dest, y_dest, z_dest));
			//else
			CharMoveToLocation cmtl = new CharMoveToLocation(this, z_dest, firstMove);

			if(ConfigValue.TestMoveInHide && isInvisible())
				sendPacket(cmtl);
			else
				broadcastPacket(cmtl);
			//if(isPlayer())
			//	System.out.println("broadcastMove: " + getName());
		}
	}

	public void stopMove()
	{
		stopMove(true, false, false, true);
	}

	public void stopMove(boolean validate, boolean set_intention)
	{
		stopMove(validate, set_intention, false, true);
	}

	public void stopMove(boolean validate, boolean set_intention, boolean force, boolean send)
	{
		if(!isMoving)
			return;
		/*if(i_ai0 == 1994576)
		{
			_log.info("L2Character: stopMove["+validate+"] -> "+toString());
			Util.test();
		}*/
		moveLock.lock();
		try
		{
			if(isMoving || force)
			{
				isMoving = false;
				destination = null;
				moveList = null;
				correction = null;
				if(_moveTask != null)
				{
					_moveTask.cancel(false);
					_moveTask = null;
				}
				if(_moveWaterTask != null)
				{
					_moveWaterTask.cancel(false);
					_moveWaterTask = null;
				}
				_targetRecorder.clear();
				if(validate || send)
					isPathFind = false;
			}

			if(send)
				broadcastPacket(new StopMove(this));
			if(validate)
				validateLocation(isPlayer() && ConfigValue.NewGeoEngine ? 2 : 1);
			if(set_intention)
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

			isFollow = false;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public void endDecayTask()
	{}
	//----------------------------
	protected volatile CharListenerList listeners;
	public CharListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new CharListenerList(this);
			}
		return listeners;
	}

	public <T extends Listener<L2Character>> boolean addListener(T listener)
	{
		return getListeners().add(listener);
	}

	public <T extends Listener<L2Character>> boolean removeListener(T listener)
	{
		return getListeners().remove(listener);
	}
}