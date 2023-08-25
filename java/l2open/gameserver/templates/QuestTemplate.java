package l2open.gameserver.templates;

import java.util.List;
import java.util.Map;

import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestType;

/**
 * @author L2CCCP
 * @site http://l2cccp.com/
 */
public class QuestTemplate
{
	public final static int DROP = 0;
	public final static int REWARD = 1;
	public final static int EXP = 2;
	public final static int SP = 3;

	private final int _id;
	private final QuestType _type;
	private Map<String, String> _name;
	private Map<QuestEventType, List<Integer>> _events;
	private List<Integer> _items;
	private double[] _rates;

	public QuestTemplate(final int id, final QuestType type)
	{
		_id = id;
		_type = type;
	}

	public int getId()
	{
		return _id;
	}

	public QuestType getType()
	{
		return _type;
	}

	public String getName(final String lang)
	{
		return _name.get(lang);
	}

	public void setNames(final Map<String, String> name)
	{
		_name = name;
	}

	public Map<QuestEventType, List<Integer>> getEvents()
	{
		return _events;
	}

	public void setEvents(final Map<QuestEventType, List<Integer>> events)
	{
		_events = events;
	}

	public List<Integer> getItems()
	{
		return _items;
	}

	public void setItems(final List<Integer> items)
	{
		_items = items;
	}

	public double getDrop()
	{
		return _rates[DROP];
	}

	public double getReward()
	{
		return _rates[REWARD];
	}

	public double getExp()
	{
		return _rates[EXP];
	}

	public double getSp()
	{
		return _rates[SP];
	}

	public void setRates(final double[] rates)
	{
		_rates = rates;
	}
}