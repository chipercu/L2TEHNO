package l2open.extensions.listeners;

import l2open.extensions.listeners.events.PropertyEvent;

public interface PropertyChangeListener
{
	/**
	 * Вызывается при смене состояния
	 * @param event передаваемое событие
	 */
	public void propertyChanged(PropertyEvent event);

	/**
	 * Возвращает свойство даного листенера
	 * @return свойство
	 */
	public String getPropery();

	/**
	 * Простенький фильтр, если лень добавлят по группам.
	 *
	 * Фильтр применяется только в общем хранилеще, тоесть при:
	 *
	 * addPropertyChangeListener(слушатель) - обработается этот метод
	 *
	 * а при
	 *
	 * addPropertyChangeListener(параметр, слушатель) не обработает
	 *
	 *
	 * @param property свойство
	 * @return принимать ли
	 */
	public boolean accept(String property);
}
