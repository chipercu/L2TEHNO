package l2open.extensions.listeners;

import l2open.extensions.listeners.events.PropertyEvent;
import l2open.gameserver.model.L2Character;

public abstract class CurrentHpChangeListener implements PropertyChangeListener, PropertyCollection
{
	@Override
	public final void propertyChanged(PropertyEvent event)
	{
		onCurrentHpChange((L2Character) event.getObject(), (Double) event.getOldValue(), (Double) event.getNewValue());
	}

	@Override
	public final boolean accept(String property)
	{
		return HitPoints.equals(property);
	}

	@Override
	public final String getPropery()
	{
		return HitPoints;
	}

	public abstract void onCurrentHpChange(L2Character actor, double oldHp, double newHp);
}
