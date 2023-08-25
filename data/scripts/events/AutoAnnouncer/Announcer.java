package events.AutoAnnouncer;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Announcer extends Functions implements ScriptFile
{
	private static boolean _active = false;
	private static String[][] text = 
	{
		{ "Test announce 1", "120000" },
		{ "Test anounce 2", "60000" }
	};

	private static boolean isActive()
	{
		return ServerVariables.getString("event_Announcer", "off").equalsIgnoreCase("on");
	}

	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(!isActive())
		{
			ServerVariables.set("event_Announcer", "on");
			announce_run();
			_log.info("Event: AutoAnnouncer started.");
		}
		else
			player.sendMessage("Event 'AutoAnnouncer' already started.");

		_active = true;
		show(Files.read("data/html/admin/events.htm", player), player, null);
	}

	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(isActive())
		{
			ServerVariables.unset("event_Announcer");
			_log.info("Event: AutoAnnouncer stopped.");
		}
		else
			player.sendMessage("Event 'AutoAnnouncer' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player, null);
	}

	public static void announce_run()
	{
		if(_active)
			for(String[] element : text)
				executeTask("events.AutoAnnouncer.Announcer", "announce", new Object[] { element[0],
						Integer.valueOf(element[1]) }, Integer.valueOf(element[1]));
	}

	public static void announce(String text, Integer inter)
	{
		if(_active)
		{
			Announcements.getInstance().announceToAll(text);
			executeTask("events.AutoAnnouncer.Announcer", "announce", new Object[] { text, inter }, inter);
		}
	}

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			announce_run();
			_log.info("Loaded Event: AutoAnnouncer [state: activated]");
		}
		else
			_log.info("Loaded Event: AutoAnnouncer [state: deactivated]");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}