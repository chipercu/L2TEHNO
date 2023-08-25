package events.arena;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;

public class GiranArena extends ArenaTemplate
{
	private static GiranArena _instance;

	public static GiranArena getInstance()
	{
		if(_instance == null)
			_instance = new GiranArena();
		return _instance;
	}

	public static void OnDie(L2Character cha, L2Character killer)
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
		_managerId = 22220001;
		_className = "GiranArena";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 3, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1points = new GArray<Location>();
		_team2points = new GArray<Location>();

		_team1points.add(new Location(72609, 142346, -3798));
		_team1points.add(new Location(72809, 142346, -3798));
		_team1points.add(new Location(73015, 142346, -3798));
		_team1points.add(new Location(73215, 142346, -3798));
		_team1points.add(new Location(73407, 142346, -3798));
		_team2points.add(new Location(73407, 143186, -3798));
		_team2points.add(new Location(73215, 143186, -3798));
		_team2points.add(new Location(73015, 143186, -3798));
		_team2points.add(new Location(72809, 143186, -3798));
		_team2points.add(new Location(72609, 143186, -3798));

		_log.info("Loaded Event: Giran Arena");
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

	public String DialogAppend_22220001(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/arena/22220001.html", player) + Files.read("data/scripts/events/arena/22220001-4.html", player);
			return Files.read("data/scripts/events/arena/22220001.html", player);
		}
		return "";
	}

	public String DialogAppend_22220002(Integer val)
	{
		return DialogAppend_22220001(val);
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