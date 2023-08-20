package events.underground;

import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.util.Location;

public class UndergroundArena1 extends UndergroundTemplate
{
	private static UndergroundArena1 _instance;

	public static UndergroundArena1 getInstance()
	{
		if(_instance == null)
			_instance = new UndergroundArena1();
		return _instance;
	}

	/*
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
	*/

	public void loadArena()
	{
		_managerId = 32377;
		_manager = "Underground Coliseum (unlimited): ";
		_className = "UndergroundArena1";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 10, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1point = new Location(-85765, -52450, -11496);
		_team2point = new Location(-83917, -53761, -11496);
		_team3point = new Location(-82640, -51367, -11504);
		_team4point = new Location(-83675, -50542, -11504);

		_log.info("Loaded Event: Underground Arena #1");
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

	public String DialogAppend_32377(Integer val)
	{
		/*
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/underground/32377.html", player) + Files.read("data/scripts/events/underground/32377-4.html", player);
			return Files.read("data/scripts/events/underground/32377.html", player);
		}
		*/
		return "";
	}

	public void create()
	{
		getInstance().template_create((L2Player) getSelf());
	}

	public void register()
	{
		getInstance().template_register((L2Player) getSelf());
	}

	public void check(String[] var)
	{
		getInstance().template_check((L2Player) getSelf(), var);
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