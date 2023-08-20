package l2open.extensions.listeners.engine;

import l2open.extensions.listeners.MethodInvokeListener;
import l2open.extensions.listeners.PropertyChangeListener;
import l2open.extensions.listeners.events.MethodEvent;
import l2open.extensions.listeners.events.PropertyEvent;

/**
 * Интерфейс для движка слушателей.
 * Идея заключается в том что для каждого объекта можно добавить свой движок слушателей.
 * В результате мы получаем гибкую систему для управления событиями в сервере.
 *
 * @author Death
 */
public interface ListenerEngine<T>
{
	/**
	 * Добавляет слушатель свойсв в общую коллекцию.
	 * @param listener слушатель
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Убирает слушаеть свойств с общей коллекции.
	 * @param listener слушатель
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Добавляет слушатель свойств определенному свойсву
	 * @param value свойство
	 * @param listener слушатель
	 */
	public void addPropertyChangeListener(String value, PropertyChangeListener listener);

	/**
	 * Убирает слушатель свойств у определенного свойства
	 * @param value свойство
	 * @param listener слушатель
	 */
	public void removePropertyChangeListener(String value, PropertyChangeListener listener);

	/**
	 * Запускает уведомление всех слушателей об изменении свойства
	 * Используется стандартный класс PropertyChangeEvent
	 * @param value свойство
	 * @param source объект от которого запущено
	 * @param oldValue старое значение
	 * @param newValue новое значение
	 */
	public void firePropertyChanged(String value, T source, Object oldValue, Object newValue);

	/**
	 * Запускает уведомление всех слушателей об изменении свойства
	 * @param event Определенный ивенд для передачи.
	 */
	public void firePropertyChanged(PropertyEvent event);

	/**
	 * Добавляет свойство в коллекцию.
	 * @param property свойство
	 * @param value значение
	 */
	public void addProperty(String property, Object value);

	/**
	 * Возвращает значение свойства
	 * @param property свойство
	 * @return значение
	 */
	public Object getProperty(String property);

	/**
	 * Возвращает обьект - владельца даного движка слушателей
	 * @return владелец инстанса слушаетелей
	 */
	public T getOwner();

	/**
	 * Добавляет слушатель на вызов определенный метод
	 * @param listener слушатель
	 */
	public void addMethodInvokedListener(MethodInvokeListener listener);

	/**
	 * Убирает определенный слушатель методов
	 * @param listener слушатель
	 */
	public void removeMethodInvokedListener(MethodInvokeListener listener);

	/**
	 * Добавляет слушатель на вызов определенный метод
	 * @param listener слушатель
	 * @param methodName имя метода
	 */
	public void addMethodInvokedListener(String methodName, MethodInvokeListener listener);

	/**
	 * Убирает определенный слушатель методов
	 * @param listener слушатель
	 * @param methodName имя метода
	 */
	public void removeMethodInvokedListener(String methodName, MethodInvokeListener listener);

	/**
	 * Вызывает слушатели и делает им нотифай события
	 * @param event событие
	 */
	public void fireMethodInvoked(MethodEvent event);

	/**
	 * Запускает нотифай слушателям что был вызван метод.
	 * @param methodName имя метода
	 * @param source источник у кого он был вызван
	 * @param args аргументы метода
	 */
	public void fireMethodInvoked(String methodName, T source, Object[] args);
}
