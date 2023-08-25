package l2open.extensions.listeners;

import l2open.extensions.listeners.events.MethodEvent;

/**
 * @author Death
 */
public interface MethodInvokeListener
{
	public void methodInvoked(MethodEvent e);

	/**
	 * Простенький фильтр. Фильтрирует по названии метода и аргументам.
	 * Ничто не мешает переделать при нужде :)
	 * @param event событие с аргументами
	 * @return true если все ок ;)
	 */
	public boolean accept(MethodEvent event);
}
