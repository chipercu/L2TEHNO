package l2open.gameserver.skills;

import l2open.gameserver.skills.triggers.TriggerInfo;
import l2open.gameserver.skills.funcs.*;
import l2open.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatTemplate
{
	protected List<TriggerInfo> _triggerList;
	protected FuncTemplate[] _funcTemplates = new FuncTemplate[0];

	public StatTemplate()
	{
		_triggerList = Collections.emptyList();
	}

	public List<TriggerInfo> getTriggerList()
	{
		return _triggerList;
	}

	public void addTrigger(TriggerInfo f)
	{
		if(_triggerList.isEmpty())
			_triggerList = new ArrayList<TriggerInfo>(4);
		_triggerList.add(f);
	}

	public void attachFunc(FuncTemplate f)
	{
		_funcTemplates = ArrayUtils.add(_funcTemplates, f);
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
}
