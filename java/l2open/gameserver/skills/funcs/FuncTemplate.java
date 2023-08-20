package l2open.gameserver.skills.funcs;

import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.conditions.Condition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FuncTemplate
{
	public Condition _applyCond;
	public Class<?> _func;
	public Constructor<?> _constructor;
	public Stats _stat;
	public int _order;
	public double _value;

	public FuncTemplate(Condition applyCond, String func, Stats stat, int order, double value)
	{
		_applyCond = applyCond;
		_stat = stat;
		_order = order;
		_value = value;
		try
		{
			_func = Class.forName("l2open.gameserver.skills.funcs.Func" + func);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		try
		{
			_constructor = _func.getConstructor(new Class[] { Stats.class, // stats to update
					Integer.TYPE, // order of execution
					Object.class, // owner
					Double.TYPE // value for function
			});
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Func getFunc(Object owner)
	{
		try
		{
			Func f = (Func) _constructor.newInstance(_stat, _order, owner, _value);
			if(_applyCond != null)
				f.setCondition(_applyCond);
			return f;
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
			return null;
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}