package l2open.extensions.listeners;

import l2open.extensions.listeners.events.PropertyEvent;
import l2open.gameserver.GameTimeController;

/**
 * @Author: Death
 * @Date: 23/11/2007
 * @Time: 8:56:19
 */
public abstract class DayNightChangeListener implements PropertyChangeListener, PropertyCollection
{

	/**
	 * Вызывается при смене состояния
	 *
	 * @param event передаваемое событие
	 */
	@Override
	public final void propertyChanged(PropertyEvent event)
	{
		if(((GameTimeController) event.getObject()).isNowNight())
			switchToNight();
		else
			switchToDay();
	}

	@Override
	public final boolean accept(String property)
	{
		return GameTimeControllerDayNightChange.equals(property);
	}

	/**
	 * Возвращает свойство даного листенера
	 *
	 * @return свойство
	 */
	@Override
	public final String getPropery()
	{
		return GameTimeControllerDayNightChange;
	}

	/**
	 * Вызывается когда на сервер наступает ночь
	 */
	public abstract void switchToNight();

	/**
	 * Вызывается когда на сервере наступает день
	 */
	public abstract void switchToDay();
}
