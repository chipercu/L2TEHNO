package l2open.gameserver.skills.effects;

import l2open.gameserver.model.*;
import l2open.gameserver.skills.AbnormalEffect;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Env;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.skills.StatTemplate;
import l2open.gameserver.skills.conditions.Condition;
import l2open.gameserver.skills.funcs.Func;
import l2open.gameserver.skills.funcs.FuncTemplate;
import l2open.gameserver.templates.StatsSet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public final class EffectTemplate extends StatTemplate
{
	static Logger _log = Logger.getLogger(EffectTemplate.class.getName());

	public Condition _attachCond;
	public final double _value;
	public final int _counter;
	public final long _period; // in milliseconds
	public int _is_first = -1; // Потом удалить эту поебень!!!

	public FuncTemplate[] _funcTemplates;
	public final EffectType _effectType;

	public final int _displayId;
	public final int _displayLevel;

	public final boolean _applyOnCaster;
	public final boolean _cancelOnAction;
	public final boolean _instantly;

	public final StatsSet _paramSet;
	public final int _level_min;
	public final int _level_max;

	public final String[] _effect_param;

	public EffectTemplate(StatsSet set)
	{
		_value = set.getDouble("value");
		_counter = set.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("count", 1);
		_period = Math.min(Integer.MAX_VALUE, 1000 * (set.getInteger("time", 0) < 0 ? Integer.MAX_VALUE : set.getInteger("time", 0)));
		_applyOnCaster = set.getBool("applyOnCaster", false);
		_cancelOnAction = set.getBool("cancelOnAction", false);
		_displayId = set.getInteger("displayId", 0);
		_displayLevel = set.getInteger("displayLevel", 0);
		_effectType = set.getEnum("name", EffectType.class);
		_effect_param = set.getString("param", "none").split(";");
		_level_min = set.getInteger("level_min", 0);
		_level_max = set.getInteger("level_max", Integer.MAX_VALUE);
		_instantly = set.getBool("instantly", _effectType.name().startsWith("i_"));
		_paramSet = set;
	}

	public L2Effect getEffect(Env env)
	{
		if(_attachCond != null && !_attachCond.test(env))
			return null;
		return _effectType.makeEffect(env, this);
	}

	public void attachCond(Condition c)
	{
		_attachCond = c;
	}

	public void attachFunc(FuncTemplate f)
	{
		if(_funcTemplates == null)
			_funcTemplates = new FuncTemplate[] { f };
		else
		{
			int len = _funcTemplates.length;
			FuncTemplate[] tmp = new FuncTemplate[len + 1];
			System.arraycopy(_funcTemplates, 0, tmp, 0, len);
			tmp[len] = f;
			_funcTemplates = tmp;
		}
	}

	public FuncTemplate[] getAttachedFuncs()
	{
		return _funcTemplates;
	}

	public Func[] getStatFuncs(Object owner)
	{
		if(_funcTemplates.length == 0)
			return Func.EMPTY_FUNC_ARRAY;

		Func[] funcs = new Func[_funcTemplates.length];
		for(int i = 0; i < funcs.length; i++)
		{
			funcs[i] = _funcTemplates[i].getFunc(owner);
		}
		return funcs;
	}

	public long getPeriod()
	{
		return _period;
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public L2Effect getSameByStackType(ConcurrentLinkedQueue<L2Effect> ef_list, SkillAbnormalType abnormalType)
	{
		for(L2Effect ef : ef_list)
			if(ef != null && EffectList.checkStackType(ef.getAbnormalType(), abnormalType))
				return ef;
		return null;
	}

	public StatsSet getParam()
	{
		return _paramSet;
	}
}