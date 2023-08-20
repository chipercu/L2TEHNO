package l2open.gameserver.handler;

import java.util.Map;
import java.util.TreeMap;

public class CommunityHandler
{
	private static CommunityHandler _instance;

	private static Map<String, ICommunityHandler> _datatable;

	public static CommunityHandler getInstance()
	{
		if(_instance == null)
			_instance = new CommunityHandler();
		return _instance;
	}

	public int size()
	{
		return _datatable.size();
	}

	private CommunityHandler()
	{
		_datatable = new TreeMap<String, ICommunityHandler>();
	}

	public void registerCommunityHandler(ICommunityHandler handler)
	{
		for(Enum<?> e : handler.getCommunityCommandEnum())
			_datatable.put(e.toString().toLowerCase(), handler);
		//int name = handler.getCommunityName();
		//_datatable.put(name, handler);
	}

	public ICommunityHandler getCommunityHandler(String name)
	{
		return _datatable.get(name);
	}

	public void clear()
	{
		_datatable.clear();
	}

	/*public void useCommunityHandler(L2Player activeChar, String command)
	{
		String[] wordList = command.split(" ");
		ICommunityHandler handler = _datatable.get(wordList[0]);
		if(handler != null)
		{
			try
			{
				for(Enum<?> e : handler.getCommunityCommandEnum())
					if(e.toString().equalsIgnoreCase(wordList[0]))
					{
						handler.parsecmd(command, activeChar);
						break;
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}*/
}
