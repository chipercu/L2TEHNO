package l2open.gameserver.skills;

import l2open.config.*;
import l2open.debug.StatsLimitDebugger;
import l2open.extensions.listeners.StatsChangeListener;
import l2open.extensions.listeners.StorageSizeListener;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.serverpackets.ExStorageMaxCount;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : p_max_hp, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 *
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 *
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 *
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 *
 */
public final class Calculator
{
	/** Empty Func table definition */
	private static final Func[] emptyFuncs = new Func[0];

	/** Table of Func object */
	private Func[] _functions;

	private static final StatsChangeListener[] emptyListeners = new StatsChangeListener[0];
	private StatsChangeListener[] _listeners = emptyListeners;

	private Double _base = null;
	private Double _last = null;

	public final Stats _stat;
	public final L2Character _character;

	/**
	 * Constructor<?> of Calculator (Init value : emptyFuncs).<BR><BR>
	 */
	public Calculator(Stats stat, L2Character character)
	{
		_stat = stat;
		_character = character;
		_functions = emptyFuncs;
	}

	/**
	 * Check if 2 calculators are equals.<BR><BR>
	 */
	public static boolean equalsCals(Calculator c1, Calculator c2)
	{
		if(c1 == c2)
			return true;

		if(c1 == null || c2 == null)
			return false;

		Func[] funcs1 = c1.getFunctions();
		Func[] funcs2 = c2.getFunctions();

		if(funcs1.length != funcs2.length)
			return false;

		if(funcs1 == funcs2)
			return true;

		if(funcs1.length == 0)
			return true;

		for(int i = 0; i < funcs1.length; i++)
			if(funcs1[i] != funcs2[i])
				return false;
		return true;

	}

	/**
	 * Return the number of Funcs in the Calculator.<BR><BR>
	 */
	public int size()
	{
		return _functions.length;
	}

	/**
	 * Add a Func to the Calculator.<BR><BR>
	 */
	public synchronized void addFunc(Func f)
	{
		Func[] funcs = _functions;
		Func[] tmp = new Func[funcs.length + 1];

		final int order = f._order;
		int i;

		for(i = 0; i < funcs.length && order >= funcs[i]._order; i++)
			tmp[i] = funcs[i];

		tmp[i] = f;

		for(; i < funcs.length; i++)
			tmp[i + 1] = funcs[i];

		_functions = tmp;
	}

	/**
	 * Remove a Func from the Calculator.<BR><BR>
	 */
	public synchronized void removeFunc(Func f)
	{
		Func[] funcs = _functions;
		if(funcs.length == 0)
			return;

		if(funcs.length == 1)
		{
			if(funcs[0] == f)
				_functions = emptyFuncs;
			return;
		}

		int size = funcs.length;
		for(Func func : funcs)
			if(func == f)
				size--;

		if(size == funcs.length)
			return;
		if(size <= 0)
		{
			_functions = emptyFuncs;
			return;
		}

		Func[] tmp = new Func[size];

		int j = 0;

		for(int i = 0; i < funcs.length; i++)
			if(tmp.length > j && f != funcs[i])
				tmp[j++] = funcs[i];

		_functions = tmp;
	}

	/**
	 * Remove each Func with the specified owner of the Calculator.<BR><BR>
	 */
	public synchronized void removeOwner(Object owner)
	{
		Func[] funcs = _functions;
		for(Func element : funcs)
			if(element._funcOwner == owner)
				removeFunc(element);
		if(_stat == Stats.p_max_mp)
		{
			double hp = _character.getMaxHp();
			if(_character.getCurrentHp() > hp)
				_character.setCurrentHp(hp, false);
		}
		else if(_stat == Stats.p_max_hp)
		{
			double mp = _character.getMaxMp();
			if(_character.getCurrentMp() > mp)
				_character.setCurrentMp(mp);
		}
		else if(_stat == Stats.p_max_cp)
		{
			double cp = _character.getMaxCp();
			if(_character.getCurrentCp() > cp)
				_character.setCurrentCp(cp);
		}
	}

	/**
	 * Run each Func of the Calculator.<BR><BR>
	 */
	public void calc(Env env)
	{
		Func[] funcs = _functions;
		_base = env.value;

		for(Func func : funcs)
			if(func.getCondition() == null || func.getCondition().test(env))
				func.calc(env);

		if(_stat._min != null && env.value < _stat._min)
			env.value = _stat._min;
	/*	if(_character.isPlayer())
		{
			if(_stat == Stats.p_attack_speed && _character.getPlayer().getStatModifiers().MaxPAtkSpeed > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxPAtkSpeed);
			else if(_stat == Stats.p_magic_speed && _character.getPlayer().getStatModifiers().MaxMAtkSpeed > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxMAtkSpeed);
			else if(_stat == Stats.p_physical_defence && _character.getPlayer().getStatModifiers().MaxPDef > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxPDef);
			else if(_stat == Stats.p_magical_defence && _character.getPlayer().getStatModifiers().MaxMDef > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxMDef);
			else if(_stat == Stats.p_physical_attack && _character.getPlayer().getStatModifiers().MaxPAtk > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxPAtk);
			else if(_stat == Stats.p_magical_attack && _character.getPlayer().getStatModifiers().MaxMAtk > 1)
				env.value = Math.min(env.value, _character.getPlayer().getStatModifiers().MaxMAtk);
			else if(_stat._max != null && env.value > (_stat == Stats.p_speed && _character.getPlayer().isInMountTransform() ? ConfigValue.LimitMoveHourse : _stat._max) && (!_character.getPlayer().isGM() || !_stat.isLimitOnlyPlayable()))
				env.value = _stat._max;
		}
		else if(_stat._max != null && env.value > (_stat == Stats.p_speed && _character.isPlayer() && _character.getPlayer().isInMountTransform() ? ConfigValue.LimitMoveHourse : _stat._max) && ((_character.isPlayer() && !_character.getPlayer().isGM()) || !_stat.isLimitOnlyPlayable()))
			env.value = _stat._max;*/
		if(_stat._max != null && env.value > (_stat == Stats.p_speed && _character.isPlayer() && _character.getPlayer().isInMountTransform() ? ConfigValue.LimitMoveHourse : _stat._max) && ((_character.isPlayer() && !_character.getPlayer().isGM()) || !_stat.isLimitOnlyPlayable()))
			env.value = _stat._max;


		if(_last == null || _last != env.value)
		{
			Double last = _last;
			_last = env.value;
			if(_stat == Stats.p_max_mp || _stat == Stats.p_max_hp || _stat == Stats.p_max_cp) 
				_character.startRegeneration(-3); 
			if(_character.isPlayer() && (_stat == Stats.INVENTORY_LIMIT || _stat == Stats.STORAGE_LIMIT || _stat == Stats.TRADE_LIMIT || _stat == Stats.COMMON_RECIPE_LIMIT || _stat == Stats.DWARVEN_RECIPE_LIMIT))
				_character.sendPacket(new ExStorageMaxCount((L2Player) _character));
		}
	}

	/**
	 * Для отладки
	 */
	public Func[] getFunctions()
	{
		return _functions;
	}

	public Double getBase()
	{
		return _base;
	}

	public Double getLast()
	{
		return _last;
	}
}