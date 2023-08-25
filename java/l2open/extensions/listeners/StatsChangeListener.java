package l2open.extensions.listeners;

import l2open.gameserver.skills.Calculator;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

 /**
 * НЕ ИСПОЛЬЗУЕТСЯ!
 **/
public abstract class StatsChangeListener
{
	public final Stats _stat;
	protected Calculator _calculator;

	public StatsChangeListener(Stats stat)
	{
		_stat = stat;
	}

	public void setCalculator(Calculator calculator)
	{
		_calculator = calculator;
	}

	public abstract void statChanged(Double oldValue, double newValue, double baseValue, Env env);
}