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

public class TvTArena3 extends TvTTemplate
{
	private static TvTArena3 _instance;

	public static TvTArena3 getInstance()
	{
		if(_instance == null)
			_instance = new TvTArena3();
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
		_managerId = 31392;
		_className = "TvTArena3";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 9002, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1points = new GArray<Location>();
		_team2points = new GArray<Location>();

		_team1points.add(new Location(-79383, -52724, -11518, -11418));
		_team1points.add(new Location(-79558, -52793, -11518, -11418));
		_team1points.add(new Location(-79726, -52867, -11518, -11418));
		_team1points.add(new Location(-79911, -52845, -11518, -11418));
		_team1points.add(new Location(-80098, -52822, -11518, -11418));
		_team1points.add(new Location(-80242, -52714, -11518, -11418));
		_team1points.add(new Location(-80396, -52597, -11518, -11418));
		_team1points.add(new Location(-80466, -52422, -11518, -11418));
		_team1points.add(new Location(-80544, -52250, -11518, -11418));
		_team1points.add(new Location(-80515, -52054, -11518, -11418));
		_team1points.add(new Location(-80496, -51878, -11518, -11418));
		_team1points.add(new Location(-80386, -51739, -11518, -11418));
		_team2points.add(new Location(-80270, -51582, -11518, -11418));
		_team2points.add(new Location(-80107, -51519, -11518, -11418));
		_team2points.add(new Location(-79926, -51435, -11518, -11418));
		_team2points.add(new Location(-79739, -51465, -11518, -11418));
		_team2points.add(new Location(-79554, -51482, -11518, -11418));
		_team2points.add(new Location(-79399, -51600, -11518, -11418));
		_team2points.add(new Location(-79254, -51711, -11518, -11418));
		_team2points.add(new Location(-79181, -51884, -11518, -11418));
		_team2points.add(new Location(-79114, -52057, -11518, -11418));
		_team2points.add(new Location(-79133, -52246, -11518, -11418));
		_team2points.add(new Location(-79156, -52427, -11518, -11418));
		_team2points.add(new Location(-79275, -52583, -11518, -11418));
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
			_log.info("Loaded Event: TvT Arena 3 [state: activated]");
		}
		if(!isActive())
			_log.info("Loaded Event: TvT Arena 3 [state: deactivated]");
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

	public String DialogAppend_31392(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/TvTArena/31392.html", player) + Files.read("data/scripts/events/TvTArena/31392-4.html", player);
			return Files.read("data/scripts/events/TvTArena/31392.html", player);
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
		return IsActive("TvT Arena 3");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("TvT Arena 3", true))
		{
			spawnEventManagers();
			_log.info("Event: TvT Arena 3 started.");
			Announcements.getInstance().announceToAll("Начался TvT Arena 3 эвент.");
		}
		else
			player.sendMessage("TvT Arena 3 Event already started.");

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

		if(SetActive("TvT Arena 3", false))
		{
			ServerVariables.unset("TvT Arena 3");
			unSpawnEventManagers();
			stop();
			_log.info("TvT Arena 3 Event stopped.");
			Announcements.getInstance().announceToAll("TvT Arena 3 эвент окончен.");
		}
		else
			player.sendMessage("TvT Arena 3 Event not started.");

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 82840, 148936, -3472, 0 } };

		L2NpcTemplate template = NpcTable.getTemplate(31392);
		template.displayId = 31392;
		for(int[] element : EVENT_MANAGERS)
			try
			{
				L2Spawn sp = new L2Spawn(template);
				sp.setLocx(element[0]);
				sp.setLocy(element[1]);
				sp.setLocz(element[2]);
				sp.setHeading(element[3]);
				L2NpcInstance npc = sp.doSpawn(true);
				npc.setName("Arena 3");
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