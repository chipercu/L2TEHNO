package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.extensions.listeners.MethodCollection;
import l2open.extensions.listeners.MethodInvokeListener;
import l2open.extensions.listeners.events.MethodEvent;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.serverpackets.AbnormalStatusUpdate;
import l2open.gameserver.serverpackets.ExOlympiadSpelledInfo;
import l2open.gameserver.serverpackets.PartySpelled;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.AbnormalVisualEffect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.SkillTrait;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.taskmanager.DotTaskManager;
import l2open.gameserver.taskmanager.EffectTaskManager;
import l2open.util.ArrayMap;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public abstract class L2Effect
{
	protected static final Logger _log = Logger.getLogger(L2Effect.class.getName());
	//private final Lock lock = new ReentrantLock();

	public static int CREATED = -2;
	public static int ACTING = 2;
	public static int FINISHING = 3;
	public static int FINISHED = 4;

	private static final Func[] _emptyFunctionSet = new Func[0];

	/** Накладывающий эффект */
	protected final L2Character _effector;
	/** Тот, на кого накладывают эффект */
	protected final L2Character _effected;

	protected final L2Skill _skill;
	protected final int _displayId;
	protected final int _displayLevel;

	// the value of an update
	private final double _value;

	private final AtomicInteger _state;

	// period, milliseconds
	private long _period;
	private long _periodStartTime;

	// function templates
	private final FuncTemplate[] _funcTemplates;

	private final EffectType _effectType;

	// counter
	protected int _count;

	public boolean _inUse = false;
	private boolean _active = false;
	private boolean _canDelay = true;

	private boolean _skillMastery = false;

	public final EffectTemplate _template;

	public int _tick_time = -1;
	public boolean isDot = false; // Затычка на время!!!
	public boolean add_action_timer = false; // Затычка на время!!!
	public boolean _instantly = false;
	public boolean update_effect_list = false;
	public Env _env;

	public int _eff_id;

	public int _obj_id;

	
	protected L2Effect(Env env, EffectTemplate template)
	{
		_env = env;
		_template = template;
		_state = new AtomicInteger(CREATED);
		_skill = env.skill;
		_effector = env.character;
		_effected = env.target;
		_value = template._value;
		_funcTemplates = template._funcTemplates;
		_effectType = template._effectType;

		_count = template._counter;
		_period = _count > 1 ? template.getPeriod() : _skill.getAbnormalTime();
		_displayId = template._displayId != 0 ? template._displayId : _skill.getDisplayId();
		_displayLevel = template._displayLevel != 0 ? template._displayLevel : _skill.getDisplayLevel();
		if(!_instantly)
			_instantly = template._instantly;

		// Check for skill mastery duration time increase
		if(env.skill_mastery == 2)
		{
			if(_count > 1)
				_count *= 2;
			else
				_period *= 2;
			_skillMastery = true;
		}

		// Считаем влияние резистов
		if(!template._applyOnCaster && _skill.isOffensive() && !_effector.isRaid())
		{
			double res = 0;
			SkillTrait trait = _skill.getTraitType();
			if(trait != null && trait != SkillTrait.trait_none)
				res = trait.calcResist(_effected) - trait.calcPower(_effector); // Трейт Резист

			double mod=0;
			if(res != 0)
			{
				mod = 1 + Math.abs(0.01 * res);
				if(res > 0)
					mod = 1. / mod;
				mod = Math.max(mod, 0.5);
				if(mod < 1)
				{
					if(_count > 1)
						_count = (int) Math.floor(Math.max(_count * mod, 1));
					else
						_period = (long) Math.floor(Math.max(_period * mod, 1));
				}
			}
		}

		if(_skill.getSkillType() == SkillType.BUFF && _period > 119000 && _skill.getId() != 396 && _skill.getId() != 1374)
			_period *= ConfigValue.BuffTimeModifier;
		if(_skill.isMusic())
			_period *= ConfigValue.SongDanceTimeModifier;
		if(_skill.getId() >= 4342 && _skill.getId() <= 4360)
			_period *= ConfigValue.ClanHallBuffTimeModifier;
		_periodStartTime = System.currentTimeMillis();
	}

	public long getPeriod()
	{
		return _period;
	}

	public void setPeriod(long time)
	{
		_period = time;
	}

	public int getCount()
	{
		return _count;
	}

	public void setCount(int newcount)
	{
		_count = newcount;
	}

	public long getTime()
	{
		return System.currentTimeMillis() - _periodStartTime;
	}

	public long getPeriodStartTime()
	{
		return _periodStartTime;
	}

	/** Возвращает оставшееся время в миллисекундах. */
	public long getTimeLeft()
	{
		return getPeriod() * getCount() - getTime();
	}

	public boolean isInUse()
	{
		return _inUse;
	}

	public void setInUse(boolean inUse)
	{
		_inUse = inUse;
		if(_inUse)
			scheduleEffect(true, false, false);
		else if(getState() != FINISHED)
			setState(FINISHING);
	}

	public boolean isActive()
	{
		return _active;
	}

	/**
	 * true означает что эффект зашедулен и сейчас не считается активным. Для неактивных эфектов не вызывается onActionTime.
	 */
	public void setActive(boolean set)
	{
		_active = set;
	}

	public L2Skill getSkill()
	{
		return _skill;
	}

	public L2Character getEffector()
	{
		return _effector;
	}

	public L2Character getEffected()
	{
		return _effected;
	}

	public double calc()
	{
		return _value;
	}

	public void exit(boolean update, boolean replace)
	{
		/*if(_effected.isPlayer())
		{
			_log.info("Effect exit("+update+"): "+getSkill().getId());
			l2open.util.Util.test();
		}*/
		if(getState() == FINISHED)
			return;
		if(getState() != CREATED)
		{
			setState(FINISHING);
			scheduleEffect(update, true, replace);
		}
		else if(getState() != FINISHED) // Ява, я тебя люблю
			setState(FINISHING);
	}

	public boolean isEnded()
	{
		return getState() == FINISHED || getState() == FINISHING;
	}

	public boolean isFinishing()
	{
		return getState() == FINISHING;
	}

	public boolean isFinished()
	{
		return getState() == FINISHED;
	}

	/**
	 * Stop the task of the L2Effect, remove it and update client magic icon.<BR><BR>
	 *
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Cancel the task </li>
	 * <li>Stop and remove L2Effect from L2Character and update client magic icon </li><BR><BR>
	 *
	 */
	private void stopEffectTask(boolean update, boolean remove, boolean replace)
	{
		_effected.getEffectList().removeEffect(this, update, remove, replace);
		updateEffects();
	}

	private ActionDispelListener _listener;

	// TODO: Придумать, что-то более удачное.
	// TODO: !!! Сделать сбивание при включении сосок, на ПТСке при юзе соски кастуется скил:)
	private class ActionDispelListener implements MethodInvokeListener, MethodCollection
	{
		@Override
		public boolean accept(MethodEvent event)
		{
			return event.getMethodName().equals(onStartAttack) || event.getMethodName().equals(onStartCast) || event.getMethodName().equals(onStartAltCast);
		}

		@Override
		public void methodInvoked(MethodEvent e)
		{
			exit(true, false);
		}
	}

	public boolean checkCondition()
	{
		if(getEffected().getSkillLevel(4045) > 0 && (getSkill().getTraitType() == SkillTrait.trait_hold || getSkill().getTraitType() == SkillTrait.trait_sleep || getSkill().getTraitType() == SkillTrait.trait_derangement || getSkill().getTraitType() == SkillTrait.trait_paralyze || getSkill().getTraitType() == SkillTrait.trait_shock || getSkill().getTraitType() == SkillTrait.trait_boss || getSkill().getTraitType() == SkillTrait.trait_physical_blockade))
			return false;
		return true;
	}
	
	/** Notify started */
	public void onStart()
	{
		if(_instantly)
			return;
		getEffected().addTriggers(getTemplate());
		//if(getTemplate()._is_first == 0)
		{
			if(getSkill().abnormal_visual_effect != AbnormalVisualEffect.ave_none)
				getEffected().startAbnormalEffect(getSkill().abnormal_visual_effect);
			if(getSkill().abnormal_visual_effect2 != AbnormalVisualEffect.ave_none)
				getEffected().startAbnormalEffect(getSkill().abnormal_visual_effect2);
		}
		if(_template._cancelOnAction)
			getEffected().addMethodInvokeListener(_listener = new ActionDispelListener());
	}

	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
	 */
	public void onExit()
	{
		if(_instantly)
			return;
		getEffected().removeTriggers(getTemplate());
		//if(getTemplate()._is_first == 0)
		{
			if(getSkill().abnormal_visual_effect != AbnormalVisualEffect.ave_none)
				getEffected().stopAbnormalEffect(getSkill().abnormal_visual_effect);
			if(getSkill().abnormal_visual_effect2 != AbnormalVisualEffect.ave_none)
				getEffected().stopAbnormalEffect(getSkill().abnormal_visual_effect2);
		}
		if(_template._cancelOnAction)
			getEffected().removeMethodInvokeListener(_listener);
	}

	/** Return true for continuation of this effect */
	public abstract boolean onActionTime();

	public final void scheduleEffect(boolean update, boolean remove, boolean replace)
	{
		L2Character effected = getEffected();
		if(effected == null)
			return;

		//if(effected.isPlayer())
		//	l2open.util.Util.test();
		// Если персонаж выходит (или уже вышел) из игры, просто останавливаем эффект
		if(getState() != FINISHED && effected.isPlayer() && ((L2Player) effected).isDeleting())
		{
			setState(FINISHED);
			
			//synchronized (this)
			//{
				setInUse(false);
				onExit();
				stopEffectTask(true, false, false);
			//}
			return;
		}

		if(getState() == CREATED)
		{
			if(!checkCondition())
			{
				// TODO Переделать так, чтобы проверка вызывалась до owner.addStatFuncs(newEffect.getStatFuncs()); и эффект вообще не добавлялся игроку, если условие не прошло.
				// Учесть случаи, когда эффект ставится в очередь, либо вынимается из очереди. Лучше всего делать проверку еще до постановки в очередь.
				// Но не забыть, что условия могут подойти вначале, но не подойти после вынимания из очереди.
				// Сейчас вся очередь уничтожается, если условие не подошло. Это может произойти, только если этот эффект уже сам в очереди, что конечно маловероятно :)
				exit(true, false); // Т.к. getState() CREATED, никаких действий не выполнится
				_effected.getEffectList().removeEffect(this, true, false, false); // Удаляем эффект у игрока
				return;
			}

			setState(ACTING);

			//synchronized (this)
			//{
				onStart();
			//}

			// Fake Death и Silent Move не отображаются
			// Отображать сообщение только для первого эффекта скилла
			if(!getSkill().isHideStartMessage() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1 && !getSkill().isToggle())
				getEffected().sendPacket(new SystemMessage(SystemMessage.S1_S2S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));

			updateEffects(); // Обрабатываем отображение статов

			if(isDot)
				DotTaskManager.getInstance().addDispelTask(this, _tick_time);
			if(!_skill.isPassive() && (!_skill.isToggle() || add_action_timer) && _skill.getAbnormalTime() != -1000)
				EffectTaskManager.getInstance().addDispelTask(this, (int) (_period / 1000));

			_periodStartTime = System.currentTimeMillis();

			return;
		}

		if(getState() == ACTING)
		{
			if(_count > 0)
			{
				_count--;
				if((!isActive() || onActionTime()) && _count > 0 || getSkill().isToggle() || getSkill().getAbnormalTime() == -1000)
					return;
			}
			setState(FINISHING);
		}
		finish(update, remove, replace);
	}

	public void finish(boolean update, boolean remove, boolean replace)
	{
		if(_state.compareAndSet(FINISHING, FINISHED))
		{
			
			setInUse(false);
			//synchronized (this)
			//{
				onExit();
			//}
			if(!_skill.isHideStopMessage() && _count <= 0 && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1)
				getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HAS_WORN_OFF).addSkillName(_displayId, _displayLevel));
			stopEffectTask(update, remove, replace);
			if(getSkill().getDelayedEffect() > 0 && _canDelay)
				SkillTable.getInstance().getInfo(getSkill().getDelayedEffect(), getSkill().getDelayedEffectLevel()).getEffects(_effected, _effected, false, false);
		}
	}

	public void updateEffects()
	{
		_effected.updateStats();
	}

	public Func[] getStatFuncs()
	{
		if(_funcTemplates == null)
			return _emptyFunctionSet;
		Func[] funcs = new Func[_funcTemplates.length];
		for(int i = 0; i < funcs.length; i++)
		{
			Func f = _funcTemplates[i].getFunc(this); // effect is owner
			funcs[i] = f;
		}
		return funcs;
	}

	public void addIcon(AbnormalStatusUpdate mi)
	{
		if(getState() != ACTING || _displayId < 0)
			return;
		int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : (int) (getTimeLeft() / 1000);
		mi.addEffect(_displayId, _displayLevel, duration);
	}

	public void addPartySpelledIcon(PartySpelled ps)
	{
		if(getState() != ACTING || _displayId < 0 || _skill.isToggle())
			return;
		int duration = (int) (getTimeLeft() / 1000);
		ps.addPartySpelledEffect(_displayId, _displayLevel, duration);
	}

	public void addOlympiadSpelledIcon(L2Player player, ExOlympiadSpelledInfo os)
	{
		if(getState() != ACTING || _displayId < 0 || _skill.isToggle())
			return;
		int duration = (int) (getTimeLeft() / 1000);
		os.addSpellRecivedPlayer(player);
		os.addEffect(_displayId, _displayLevel, duration);
	}

	protected int getLevel()
	{
		return _skill.getLevel();
	}

	public boolean containsStat(Stats stat)
	{
		if(_funcTemplates != null)
			for(int i = 0; i < _funcTemplates.length; i++)
				if(_funcTemplates[i]._stat == stat)
					return true;
		return false;
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public boolean isSkillMasteryEffect()
	{
		return _skillMastery;
	}

	public boolean isSaveable()
	{
		return getTimeLeft() > ConfigValue.SaveableEffectTime && getSkill().isSaveable();
	}

	public int getDisplayId()
	{
		return _displayId;
	}

	public void setCanDelay(boolean i)
	{
		_canDelay = i;
	}

	public int getDisplayLevel()
	{
		return _displayLevel;
	}

	@Override
	public String toString()
	{
		return "Skill: " + _skill + ", state: " + getState(getState()) + ", inUse: " + _inUse;
	}

	public EffectTemplate getTemplate()
	{
		return _template;
	}

	public boolean isOffensive()
	{
		Boolean template = _template.getParam().getBool("isOffensive", null);
		if(template != null)
			return template;
		return getSkill().isOffensive();
	}

	public SkillAbnormalType getAbnormalType()
	{
		return getSkill().getAbnormalType();
	}

	public int getAbnormalLv()
	{
		return getSkill().getAbnormalLv();
	}

	public int getState()
	{
		return _state.get();
	}

	private void setState(int newState)
	{
		_state.getAndSet(newState);
	}

	private final static String getState(final int state)
	{
		switch (state)
		{
			case -2:
				return "CREATED";
			case 2:
				return "ACTING";
			case 3:
				return "FINISHING";
			case 4:
				return "FINISHED";
		}
		return "UNKNOW";
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		
		final L2Effect other = (L2Effect) obj;

		if(other._skill == null || _skill == null)
			return false;
		else if(!_skill.equals(other._skill))
			return false;
		else if(getTemplate()._is_first != other.getTemplate()._is_first)
			return false;
		return true;
	}
}