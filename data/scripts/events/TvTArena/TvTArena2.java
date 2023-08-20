package events.TvTArena;

import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;

public class TvTArena2 extends TvTTemplate
{
	private static TvTArena2 _instance;

	public static TvTArena2 getInstance()
	{
		if(_instance == null)
			_instance = new TvTArena2();
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
		_managerId = 31391;
		_className = "TvTArena2";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 9001, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1points = new GArray<Location>();
		_team2points = new GArray<Location>();

		_team1points.add(new Location(-77724, -47901, -11518, -11418));
		_team1points.add(new Location(-77718, -48080, -11518, -11418));
		_team1points.add(new Location(-77699, -48280, -11518, -11418));
		_team1points.add(new Location(-77777, -48442, -11518, -11418));
		_team1points.add(new Location(-77863, -48622, -11518, -11418));
		_team1points.add(new Location(-78002, -48714, -11518, -11418));
		_team1points.add(new Location(-78168, -48835, -11518, -11418));
		_team1points.add(new Location(-78353, -48851, -11518, -11418));
		_team1points.add(new Location(-78543, -48864, -11518, -11418));
		_team1points.add(new Location(-78709, -48784, -11518, -11418));
		_team1points.add(new Location(-78881, -48702, -11518, -11418));
		_team1points.add(new Location(-78981, -48555, -11518, -11418));
		_team2points.add(new Location(-79097, -48400, -11518, -11418));
		_team2points.add(new Location(-79107, -48214, -11518, -11418));
		_team2points.add(new Location(-79125, -48027, -11518, -11418));
		_team2points.add(new Location(-79047, -47861, -11518, -11418));
		_team2points.add(new Location(-78965, -47689, -11518, -11418));
		_team2points.add(new Location(-78824, -47594, -11518, -11418));
		_team2points.add(new Location(-78660, -47474, -11518, -11418));
		_team2points.add(new Location(-78483, -47456, -11518, -11418));
		_team2points.add(new Location(-78288, -47440, -11518, -11418));
		_team2points.add(new Location(-78125, -47515, -11518, -11418));
		_team2points.add(new Location(-77953, -47599, -11518, -11418));
		_team2points.add(new Location(-77844, -47747, -11518, -11418));
	}

	public void unLoadArena()
	{
		if(_status > 0)
			stop();
		unSpawnEventManagers();
		_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
		_instance = null;
	}

	public void onLoad()
	{
		if(isActive())
		{
			spawnEventManagers();
			_log.info("Loaded Event: TvT Arena 2 [state: activated]");
		}
		if(!isActive())
			_log.info("Loaded Event: TvT Arena 2 [state: deactivated]");
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

	public String DialogAppend_31391(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/TvTArena/31391.html", player) + Files.read("data/scripts/events/TvTArena/31391-4.html", player);
			return Files.read("data/scripts/events/TvTArena/31391.html", player);
		}
		return "";
	}

	public void create1()
	{
		getInstance().template_create1((L2Player) getSelf());
	}

	public void register()
	{
		getInstance().template_register((L2Player) getSelf());
	}

	public void check1(String[] var)
	{
		getInstance().template_check1((L2Player) getSelf(), getNpc(), var);
	}

	public void register_check()
	{
		getInstance().template_register_check((L2Player) getSelf());
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

	public void timeOut()
	{
		getInstance().template_timeOut();
	}

	private static GArray<L2NpcInstance> _spawns = new GArray<L2NpcInstance>();

	/**
	 * Читает статус эвента из базы.
	 */
	private boolean isActive()
	{
		return IsActive("TvT Arena 2");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("TvT Arena 2", true))
		{
			spawnEventManagers();
			_log.info("Event: TvT Arena 2 started.");
			Announcements.getInstance().announceToAll("Начался TvT Arena 2 эвент.");
		}
		else
			player.sendMessage("TvT Arena 2 Event already started.");

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Останавливает эвент
	*/
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("TvT Arena 2", false))
		{
			ServerVariables.unset("TvT Arena 2");
			unSpawnEventManagers();
			stop();
			_log.info("TvT Arena 2 Event stopped.");
			Announcements.getInstance().announceToAll("TvT Arena 2 эвент окончен.");
		}
		else
			player.sendMessage("TvT Arena 2 Event not started.");

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 82840, 149048, -3472, 0 } };

		L2NpcTemplate template = NpcTable.getTemplate(31391);
		template.displayId = 31391;
		for(int[] element : EVENT_MANAGERS)
			try
			{
				L2Spawn sp = new L2Spawn(template);
				sp.setLocx(element[0]);
				sp.setLocy(element[1]);
				sp.setLocz(element[2]);
				sp.setHeading(element[3]);
				L2NpcInstance npc = sp.doSpawn(true);
				npc.setName("Arena 2");
				npc.setTitle("TvT Event");
				npc.updateAbnormalEffect();
				_spawns.add(npc);
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		for(L2NpcInstance npc : _spawns)
			npc.deleteMe();
		_spawns.clear();
	}
}