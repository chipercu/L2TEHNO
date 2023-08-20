package events.arena;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;

public class DionArena extends ArenaTemplate
{
	private static DionArena _instance;

	public static DionArena getInstance()
	{
		if(_instance == null)
			_instance = new DionArena();
		return _instance;
	}

	public void OnDie(L2Character cha, L2Character killer)
	{
		getInstance().onDie(cha, killer);
	}

	public void OnPlayerExit(L2Player player)
	{
		getInstance().onPlayerExit(player);
	}

	public Location OnEscape(L2Player player)
	{
		return getInstance().onEscape(player);
	}

	public void loadArena()
	{
		_managerId = 20230001;
		_className = "DionArena";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 1, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1points = new GArray<Location>();
		_team2points = new GArray<Location>();

		_team1points.add(new Location(12053, 183101, -3563));
		_team1points.add(new Location(12253, 183101, -3563));
		_team1points.add(new Location(12459, 183101, -3563));
		_team1points.add(new Location(12659, 183101, -3563));
		_team1points.add(new Location(12851, 183101, -3563));
		_team2points.add(new Location(12851, 183941, -3563));
		_team2points.add(new Location(12659, 183941, -3563));
		_team2points.add(new Location(12459, 183941, -3563));
		_team2points.add(new Location(12253, 183941, -3563));
		_team2points.add(new Location(12053, 183941, -3563));

		_log.info("Loaded Event: Dion Arena");
	}

	public void unLoadArena()
	{
		if(_status > 0)
			stop();
		_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
		_instance = null;
	}

	public void onLoad()
	{
		getInstance().loadArena();
	}

	public void onReload()
	{
		getInstance().unLoadArena();
	}

	public void onShutdown()
	{
		onReload();
	}

	public String DialogAppend_20230001(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/arena/20230001.html", player) + Files.read("data/scripts/events/arena/20230001-4.html", player);
			return Files.read("data/scripts/events/arena/20230001.html", player);
		}
		return "";
	}

	public String DialogAppend_20230002(Integer val)
	{
		return DialogAppend_20230001(val);
	}

	public void create1()
	{
		getInstance().template_create1((L2Player) getSelf());
	}

	public void create2()
	{
		getInstance().template_create2((L2Player) getSelf());
	}

	public void register()
	{
		getInstance().template_register((L2Player) getSelf());
	}

	public void check1(String[] var)
	{
		getInstance().template_check1((L2Player) getSelf(), getNpc(), var);
	}

	public void check2(String[] var)
	{
		getInstance().template_check2((L2Player) getSelf(), getNpc(), var);
	}

	public void register_check(String[] var)
	{
		getInstance().template_register_check((L2Player) getSelf(), var);
	}

	public void stop()
	{
		getInstance().template_stop();
	}

	public void announce()
	{
		getInstance().template_announce();
	}

	public void prepare()
	{
		getInstance().template_prepare();
	}

	public void start()
	{
		getInstance().template_start();
	}

	public static void timeOut()
	{
		getInstance().template_timeOut();
	}
}