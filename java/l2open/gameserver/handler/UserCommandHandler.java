package l2open.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

public class UserCommandHandler
{
	private static UserCommandHandler _instance;

	private Map<Integer, IUserCommandHandler> _datatable;

	public static UserCommandHandler getInstance()
	{
		if(_instance == null)
			_instance = new UserCommandHandler();
		return _instance;
	}

	private UserCommandHandler()
	{
		_datatable = new HashMap<Integer, IUserCommandHandler>();
	}

	public void registerUserCommandHandler(IUserCommandHandler handler)
	{
		int[] ids = handler.getUserCommandList();
		for(int element : ids)
			_datatable.put(element, handler);
	}

	public IUserCommandHandler getUserCommandHandler(int userCommand)
	{
		return _datatable.get(userCommand);
	}

	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}

	public void clear()
	{
		_datatable.clear();
	}
}
