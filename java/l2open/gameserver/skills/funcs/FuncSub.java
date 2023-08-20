package l2open.gameserver.skills.funcs;

import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.Stats;

public class FuncSub extends Func
{
	public FuncSub(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner, value);
	}

	@Override
	public void calc(Env env)
	{
		env.value -= _value;
	}
}
