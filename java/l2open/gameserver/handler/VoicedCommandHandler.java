package l2open.gameserver.handler;

import javolution.util.FastMap;

import java.util.Map;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.5 $ $Date: 2005/03/27 15:30:09 $
 */
public class VoicedCommandHandler
{
	private static VoicedCommandHandler _instance;

	private Map<String, IVoicedCommandHandler> _datatable;

	public static VoicedCommandHandler getInstance()
	{
		if(_instance == null)
			_instance = new VoicedCommandHandler();
		return _instance;
	}

	private VoicedCommandHandler()
	{
		_datatable = new FastMap<String, IVoicedCommandHandler>().setShared(true);
		Status s = new Status();
		for(String e : s.getVoicedCommandList())
			_datatable.put(e, s);
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for(String element : ids)
			_datatable.put(element, handler);
	}

	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if(voicedCommand.indexOf(" ") != -1)
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		return _datatable.get(command);
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
