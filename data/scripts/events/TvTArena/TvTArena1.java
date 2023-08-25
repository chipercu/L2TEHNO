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

public class TvTArena1 extends TvTTemplate
{
	private static TvTArena1 _instance;

	public static TvTArena1 getInstance()
	{
		if(_instance == null)
			_instance = new TvTArena1();
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
		_managerId = 31390;
		_className = "TvTArena1";
		_status = 0;

		_zoneListener = new ZoneListener();
		_zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 9000, true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		_team1points = new GArray<Location>();
		_team2points = new GArray<Location>();

		_team1points.add(new Location(-81806, -44865, -11418));
		_team1points.add(new Location(-81617, -44893, -11418));
		_team1points.add(new Location(-81440, -44945, -11418));
		_team1points.add(new Location(-81301, -48066, -11418));
		_team1points.add(new Location(-81168, -45208, -11418));
		_team1points.add(new Location(-81114, -46379, -11418));
		_team1points.add(new Location(-81068, -45570, -11418));
		_team1points.add(new Location(-81114, -45728, -11418));
		_team1points.add(new Location(-81162, -45934, -11418));
		_team1points.add(new Location(-81280, -46045, -11418));
		_team1points.add(new Location(-81424, -46196, -11418));
		_team1points.add(new Location(-81578, -46238, -11418));
		_team2points.add(new Location(-81792, -46299, -11418));
		_team2points.add(new Location(-81959, -46247, -11418));
		_team2points.add(new Location(-82147, -46206, -11418));
		_team2points.add(new Location(-82256, -46093, -11418));
		_team2points.add(new Location(-82418, -45940, -11418));
		_team2points.add(new Location(-82455, -45779, -11418));
		_team2points.add(new Location(-82513, -45573, -11418));
		_team2points.add(new Location(-82464, -45499, -11418));
		_team2points.add(new Location(-82421, -45215, -11418));
		_team2points.add(new Location(-82308, -45106, -11418));
		_team2points.add(new Location(-82160, -44948, -11418));
		_team2points.add(new Location(-81978, -44904, -11418));
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
			_log.info("Loaded Event: TvT Arena 1 [state: activated]");
		}
		if(!isActive())
			_log.info("Loaded Event: TvT Arena 1 [state: deactivated]");
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

	public String DialogAppend_31390(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			if(player.isGM())
				return Files.read("data/scripts/events/TvTArena/31390.html", player) + Files.read("data/scripts/events/TvTArena/31390-4.html", player);
			return Files.read("data/scripts/events/TvTArena/31390.html", player);
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
		return IsActive("TvT Arena 1");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("TvT Arena 1", true))
		{
			spawnEventManagers();
			_log.info("Event: TvT Arena 1 started.");
			Announcements.getInstance().announceToAll("Начался TvT Arena 1 эвент.");
		}
		else
			player.sendMessage("TvT Arena 1 Event already started.");

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

		if(SetActive("TvT Arena 1", false))
		{
			ServerVariables.unset("TvT Arena 1");
			unSpawnEventManagers();
			stop();
			_log.info("TvT Arena 1 Event stopped.");
			Announcements.getInstance().announceToAll("TvT Arena 1 эвент окончен.");
		}
		else
			player.sendMessage("TvT Arena 1 Event not started.");

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 82840, 149167, -3495, 0 } };

		L2NpcTemplate template = NpcTable.getTemplate(31390);
		template.displayId = 31390;
		for(int[] element : EVENT_MANAGERS)
			try
			{
				L2Spawn sp = new L2Spawn(template);
				sp.setLocx(element[0]);
				sp.setLocy(element[1]);
				sp.setLocz(element[2]);
				sp.setHeading(element[3]);
				L2NpcInstance npc = sp.doSpawn(true);
				npc.setName("Arena 1");
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