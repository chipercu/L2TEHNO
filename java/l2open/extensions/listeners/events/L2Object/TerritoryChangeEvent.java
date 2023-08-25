package l2open.extensions.listeners.events.L2Object;

import l2open.extensions.listeners.PropertyCollection;
import l2open.extensions.listeners.events.PropertyEvent;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Territory;

import java.util.Collection;

/**
 * @author Death
 * Date: 22/8/2007
 * Time: 12:57:10
 */
public class TerritoryChangeEvent implements PropertyEvent
{
	private final Collection<L2Territory> enter;
	private final Collection<L2Territory> exit;
	private final L2Object object;

	public TerritoryChangeEvent(Collection<L2Territory> enter, Collection<L2Territory> exit, L2Object object)
	{
		this.enter = enter;
		this.exit = exit;
		this.object = object;
	}

	@Override
	public L2Object getObject()
	{
		return object;
	}

	/**
	 * Возврщает список территорий с которых вышел объект
	 * @return список удаленных территорий
	 */
	@Override
	public Collection<L2Territory> getOldValue()
	{
		return exit;
	}

	/**
	 * Возвразает список территорой в которые вошел объект
	 * @return список добавленых территорий
	 */
	@Override
	public Collection<L2Territory> getNewValue()
	{
		return enter;
	}

	@Override
	public String getProperty()
	{
		return PropertyCollection.TerritoryChanged;
	}
}
