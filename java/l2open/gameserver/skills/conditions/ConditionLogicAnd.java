package l2open.gameserver.skills.conditions;

import l2open.gameserver.skills.Env;

public class ConditionLogicAnd extends Condition
{
	private final static Condition[] emptyConditions = new Condition[0];

	public Condition[] _conditions = emptyConditions;

	public ConditionLogicAnd()
	{
		super();
	}

	public void add(Condition condition)
	{
		if(condition == null)
			return;
		if(getListener() != null)
			condition.setListener(this);
		final int len = _conditions.length;
		final Condition[] tmp = new Condition[len + 1];
		System.arraycopy(_conditions, 0, tmp, 0, len);
		tmp[len] = condition;
		_conditions = tmp;
	}

	@Override
	public void setListener(ConditionListener listener)
	{
		if(listener != null)
			for(Condition c : _conditions)
				c.setListener(this);
		else
			for(Condition c : _conditions)
				c.setListener(null);
		super.setListener(listener);
	}

	@Override
	protected boolean testImpl(Env env)
	{
		for(Condition c : _conditions)
			if(!c.test(env))
				return false;
		return true;
	}
}
