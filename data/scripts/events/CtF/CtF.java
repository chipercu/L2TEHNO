package events.CtF;

import gnu.trove.list.array.TIntArrayList;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.Revive;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;
import l2open.util.reference.*;

public class CtF extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(CtF.class.getName());
	private static Reflection reflection = null;

	public class StartTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!_active)
			{
				startTimerTask();
				return;
			}

			if(isPvPEventStarted())
			{
				_log.info("CtF not started: another event is already running");
				startTimerTask();
				return;
			}

			if(TerritorySiege.isInProgress())
			{
				_log.info("CtF not started: TerritorySiege in progress");
				startTimerTask();
				return;
			}

			for(Castle c : CastleManager.getInstance().getCastles().values())
			{
				if(c.getSiege() != null && c.getSiege().isInProgress())
				{
					_log.info("CtF not started: CastleSiege in progress");
					startTimerTask();
					return;
				}
			}

			if(ConfigValue.CaptureTheFlagCategories)
				start(new String[] { "1", "1" });
			else
				start(new String[] { "-1", "-1" });
		}
	}

	private static ScheduledFuture<?> _startTask;

	private static GCSArray<HardReference<L2Player>> players_list1 = new GCSArray<HardReference<L2Player>>();
	private static GCSArray<HardReference<L2Player>> players_list2 = new GCSArray<HardReference<L2Player>>();

	private static L2NpcInstance redFlag = null;
	private static L2NpcInstance blueFlag = null;

	private static boolean _isRegistrationActive = false;
	private static int _status = 0;
	private static int _time_to_start;
	private static int _category;
	private static int _minLevel;
	private static int _maxLevel;
	private static int _autoContinue = 0;

	private static ScheduledFuture<?> _endTask;

	private static L2Zone _zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 4, true);
	private static L2Zone _blueBaseZone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 5, true);
	private static L2Zone _redBaseZone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 6, true);

	ZoneListener _zoneListener = new ZoneListener();
	RedBaseZoneListener _redBaseZoneListener = new RedBaseZoneListener();
	BlueBaseZoneListener _blueBaseZoneListener = new BlueBaseZoneListener();

	private static L2Territory team1loc = new L2Territory(11000003);
	private static L2Territory team2loc = new L2Territory(11000004);

	private static Location blueFlagLoc = new Location(150760, 45848, -3408);
	private static Location redFlagLoc = new Location(148232, 47688, -3408);

	private static List<Long> time2 = new ArrayList<Long>();

	private void initTimer(boolean new_day)
	{
		time2.clear();
		if(ConfigValue.CaptureTheFlagStartTime[0] == -1)
			return;
		long cur_time = System.currentTimeMillis();
		for(int i = 0; i < ConfigValue.CaptureTheFlagStartTime.length; i += 2)
		{
			Calendar ci = Calendar.getInstance();
			if(new_day)
				ci.add(Calendar.HOUR_OF_DAY, 12);
			ci.set(Calendar.HOUR_OF_DAY, ConfigValue.CaptureTheFlagStartTime[i]);
			ci.set(Calendar.MINUTE, ConfigValue.CaptureTheFlagStartTime[i + 1]);
			ci.set(Calendar.SECOND, 00);

			long delay = ci.getTimeInMillis();
			if(delay - cur_time > 0)
				time2.add(delay);
			ci = null;
		}
		Collections.sort(time2);
		long delay = 0;
		while(time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0);
		if(_startTask != null)
			_startTask.cancel(true);
		if(delay - cur_time > 0)
			_startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay - cur_time);
	}

	public void onLoad()
	{
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		_blueBaseZone.getListenerEngine().addMethodInvokedListener(_blueBaseZoneListener);
		_redBaseZone.getListenerEngine().addMethodInvokedListener(_redBaseZoneListener);

		team1loc.add(149878, 47505, -3408, -3308);
		team1loc.add(150262, 47513, -3408, -3308);
		team1loc.add(150502, 47233, -3408, -3308);
		team1loc.add(150507, 46300, -3408, -3308);
		team1loc.add(150256, 46002, -3408, -3308);
		team1loc.add(149903, 46005, -3408, -3308);

		team2loc.add(149027, 46005, -3408, -3308);
		team2loc.add(148686, 46003, -3408, -3308);
		team2loc.add(148448, 46302, -3408, -3308);
		team2loc.add(148449, 47231, -3408, -3308);
		team2loc.add(148712, 47516, -3408, -3308);
		team2loc.add(149014, 47527, -3408, -3308);

		initTimer(false);

		_active = ServerVariables.getString("CtF", "on").equalsIgnoreCase("on");

		if(!_active)
			_log.info("Loaded Event: CtF not active.");
		else
			_log.info("Loaded Event: CtF active.");
	}

	public void onReload()
	{
		if(_zone != null)
			_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
		if(_startTask != null)
			_startTask.cancel(true);
	}

	public void onShutdown()
	{
		onReload();
	}

	private static boolean _active = false;

	public static boolean isActive()
	{
		return _active;
	}

	public void activateEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(!isActive())
		{
			if(_startTask == null)
				initTimer(false);
			ServerVariables.set("CtF", "on");
			_log.info("Event 'CtF' activated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CtF.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'CtF' already active.");

		_active = true;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void deactivateEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(isActive())
		{
			if(_startTask != null)
			{
				_startTask.cancel(true);
				_startTask = null;
			}
			ServerVariables.unset("CtF");
			_log.info("Event 'CtF' deactivated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.CtF.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'CtF' not active.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public static boolean isRunned()
	{
		return _isRegistrationActive || _status > 0;
	}

	public String DialogAppend_31225(Integer val)
	{
		if(val == 0)
		{
			L2Player player = (L2Player) getSelf();
			return Files.read("data/scripts/events/CtF/31225.html", player);
		}
		return "";
	}

	// Red flag
	public String DialogAppend_35423(Integer val)
	{
		if(_status < 2)
			return "";
		L2Player player = (L2Player) getSelf();
		if(player.getTeam() != 1)
			return "";
		if(val == 0)
			return Files.read("data/scripts/events/CtF/35423.html", player).replaceAll("n1", "" + Rnd.get(100, 999)).replaceAll("n2", "" + Rnd.get(100, 999));
		return "";
	}

	// Blue flag
	public String DialogAppend_35426(Integer val)
	{
		if(_status < 2)
			return "";
		L2Player player = (L2Player) getSelf();
		if(player.getTeam() != 2)
			return "";
		if(val == 0)
			return Files.read("data/scripts/events/CtF/35426.html", player).replaceAll("n1", "" + Rnd.get(100, 999)).replaceAll("n2", "" + Rnd.get(100, 999));
		return "";
	}

	public void capture(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(var.length != 4)
		{
			player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		L2NpcInstance npc = getNpc();

		if(player.isDead() || npc == null || !player.isInRange(npc, 200) || npc.getNpcId() != (player.getTeam() == 1 ? 35423 : 35426))
		{
			player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		Integer base;
		Integer add1;
		Integer add2;
		Integer summ;
		try
		{
			base = Integer.valueOf(var[0]);
			add1 = Integer.valueOf(var[1]);
			add2 = Integer.valueOf(var[2]);
			summ = Integer.valueOf(var[3]);
		}
		catch(Exception e)
		{
			player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		if(add1 + add2 != summ)
		{
			player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		if(base == 1 && blueFlag != null && blueFlag.isVisible()) // Синяя база
		{
			blueFlag.decayMe();
			addFlag(player, 13561);
		}

		if(base == 2 && blueFlag != null && redFlag.isVisible()) // Красная база
		{
			redFlag.decayMe();
			addFlag(player, 13560);
		}

		if(player.isInvisible() && player.getEffectList().getEffectByType(EffectType.p_hide) != null)
			player.getEffectList().stopAllSkillEffects(EffectType.p_hide);
	}

	public void start(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(var.length != 2)
		{
			if(player != null)
				player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		Integer category;
		Integer autoContinue;
		try
		{
			category = Integer.valueOf(var[0]);
			autoContinue = Integer.valueOf(var[1]);
		}
		catch(Exception e)
		{
			if(player != null)
				player.sendMessage(new CustomMessage("common.Error", player));
			return;
		}

		_category = category;
		_autoContinue = autoContinue;

		if(_category == -1)
		{
			_minLevel = 1;
			_maxLevel = 85;
		}
		else
		{
			_minLevel = ConfigValue.CaptureTheFlagMinLevelForCategory[_category-1];
			_maxLevel = ConfigValue.CaptureTheFlagMaxLevelForCategory[_category-1];
		}

		if(_endTask != null)
		{
			if(player != null)
				player.sendMessage(new CustomMessage("common.TryLater", player));
			return;
		}
		reflection = new Reflection("CtFInstances");
		reflection.setGeoIndex(GeoEngine.NextGeoIndex(24, 19, reflection.getId()));
		reflection.addDoor(24190002);
		reflection.addDoor(24190003);

		for(L2DoorInstance d : reflection.getDoors())
		{
			d.setReflection(reflection);
			d.spawnMe();
			d.closeMe();
		}

		_status = 0;
		_isRegistrationActive = true;
		_time_to_start = ConfigValue.CaptureTheFlagTime;

		players_list1 = new GCSArray<HardReference<L2Player>>();
		players_list2 = new GCSArray<HardReference<L2Player>>();

		if(redFlag != null)
			redFlag.deleteMe();
		if(blueFlag != null)
			blueFlag.deleteMe();

		redFlag = spawn(redFlagLoc, 35423, 0, reflection.getId());
		blueFlag = spawn(blueFlagLoc, 35426, 0, reflection.getId());

		redFlag.decayMe();
		blueFlag.decayMe();

		String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
		sayToAll("scripts.events.CtF.AnnouncePreStart", param);

		executeTask("events.CtF.CtF", "question", new Object[0], 10000);
		executeTask("events.CtF.CtF", "announce", new Object[0], 60000);
	}

	public static void sayToAll(String address, String[] replacements)
	{
		Announcements.getInstance().announceByCustomMessage(address, replacements, Say2C.CRITICAL_ANNOUNCEMENT);
	}

	public static void question()
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().getId() <= 0 && !player.isInOlympiadMode() && !player.isDead() && !player.isInZone(ZoneType.epic) && !player.isFlying() && player.getVar("jailed") == null && player.getVarB("event_invite", true))
				player.scriptRequest(new CustomMessage("scripts.events.CtF.AskPlayer", player).toString(), "events.CtF.CtF:addPlayer", new Object[0]);
	}

	public static void announce()
	{
		if(players_list1.isEmpty() || players_list2.isEmpty())
		{
			sayToAll("scripts.events.CtF.AnnounceEventCancelled", null);
			_isRegistrationActive = false;
			_status = 0;
			executeTask("events.CtF.CtF", "autoContinue", new Object[0], 10000);
			if(!players_list1.isEmpty())
			{
				for(HardReference<L2Player> ref : players_list1)
				{
					L2Player p = ref.get();
					if(p == null)
						continue;
					p.setEventReg(false);
				}
			}
			if(!players_list2.isEmpty())
			{
				for(HardReference<L2Player> ref : players_list2)
				{
					L2Player p = ref.get();
					if(p == null)
						continue;
					p.setEventReg(false);
				}
			}
			players_list1.clear();
			players_list2.clear();
			return;
		}

		if(_time_to_start > 1)
		{
			_time_to_start--;
			String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
			sayToAll("scripts.events.CtF.AnnouncePreStart", param);
			executeTask("events.CtF.CtF", "announce", new Object[0], 60000);
		}
		else
		{
			_status = 1;
			_isRegistrationActive = false;
			sayToAll("scripts.events.CtF.AnnounceEventStarting", null);
			executeTask("events.CtF.CtF", "prepare", new Object[0], 5000);
		}
	}

	public void un_reg()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		else if(!_isRegistrationActive)
		{
			player.sendMessage("Вы не можете снять регистрацию с ивента.");
			return;
		}
		removePlayer(player);
		player.sendMessage("Вы сняли регистрацию с CtF.");
	}

	public void addPlayer()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || !checkPlayer(player, true))
			return;

		if(ConfigValue.CaptureTheFlagIP)
		{
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getIP().equals(player.getIP()))
				{
					player.sendMessage("Игрок с данным IP уже зарегистрирован.");
					return;
				}
			}
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getIP().equals(player.getIP()))
				{
					player.sendMessage("Игрок с данным IP уже зарегистрирован.");
					return;
				}
			}
		}

		if(ConfigValue.CaptureTheFlagHWID)
		{
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getHWIDs().equals(player.getHWIDs()))
				{
					player.sendMessage("С данного компьютера уже зарегистрирован 1 игрок.");
					return;
				}
			}
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player p = ref.get();
				if(p == null)
					continue;
				if(p.getHWIDs().equals(player.getHWIDs()))
				{
					player.sendMessage("С данного компьютера уже зарегистрирован 1 игрок.");
					return;
				}
			}
		}

		int team = 0, size1 = players_list1.size(), size2 = players_list2.size();

		if(size1 > size2)
			team = 2;
		else if(size1 < size2)
			team = 1;
		else
			team = Rnd.get(1, 2);

		if(team == 1)
		{
			players_list1.add(player.getRef());
			player.setEventReg(true);
			player.sendMessage(new CustomMessage("scripts.events.CtF.Registered", player));
		}
		else if(team == 2)
		{
			players_list2.add(player.getRef());
			player.setEventReg(true);
			player.sendMessage(new CustomMessage("scripts.events.CtF.Registered", player));
		}
		else
			_log.info("WTF??? Command id 0 in CtF...");
	}

	public static boolean is_reg(L2Player player)
	{
		return players_list1.contains(player.getRef()) || players_list2.contains(player.getRef());
	}

	public static boolean checkPlayer(L2Player player, boolean first)
	{
		if(first && !_isRegistrationActive)
		{
			player.sendMessage(new CustomMessage("scripts.events.Late", player));
			return false;
		}
		else if(first && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())))
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.Cancelled", player));
			return false;
		}
		else if(first && (player.isInEvent() != 0 || player.isEventReg()))
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.OtherEvent", player).addString(player.getEventName(player.isInEvent())));
			return false;
		}
		else if(player.getLevel() < _minLevel || player.getLevel() > _maxLevel)
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledLevel", player));
			return false;
		}
		else if(player.isMounted())
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.Cancelled", player));
			return false;
		}
		else if(player.getDuel() != null)
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledDuel", player));
			return false;
		}
		else if(player.getTeam() != 0)
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledOtherEvent", player));
			return false;
		}
		else if(player.getOlympiadGame() != null || player.isInZoneOlympiad() || first && Olympiad.isRegistered(player))
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledOlympiad", player));
			return false;
		}
		else if(player.isInParty() && player.getParty().isInDimensionalRift())
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledOtherEvent", player));
			return false;
		}
		else if(player.isTeleporting())
		{
			player.sendMessage(new CustomMessage("scripts.events.CtF.CancelledTeleport", player));
			return false;
		}
		else if(player.isCursedWeaponEquipped())
		{
			player.sendMessage("С проклятым оружием на эвент нельзя.");
			return false;
		}
		else if(player.isInOfflineMode() || player.inObserverMode() || player.isLogout())// Если игрок в обсерве то удаляем его с ивента...нехуй было туда заходить)))
			return false;
		else if(player.isInStoreMode())
		{
			player.sendMessage("Во время торговли на эвент нельзя.");
			return false;
		}
		else if(player.getVar("jailed") != null)
		{
			player.sendMessage("В тюрьме на эвент нельзя");
			return false;
		}
		else if(player.getReflection().getId() > 0)
		{
			player.sendMessage("Регистрация отменена, нельзя находится во временной зоне.");
			return false;
		}
		else if(player.isSubClassActive() && !ConfigValue.CaptureTheFlagSub)
		{
			player.sendMessage("Принимать участие в ивенте, можно только основным классом!");
			return false;
		}
		return true;
	}

	public static void prepare()
	{
		cleanPlayers();
		clearArena();
		ressurectPlayers();

		redFlag.spawnMe();
		blueFlag.spawnMe();

		paralyzePlayers();

		executeTask("events.CtF.CtF", "teleportPlayersToColiseum", new Object[0], 4000);
		executeTask("events.CtF.CtF", "healPlayers", new Object[0], 6000);
		executeTask("events.CtF.CtF", "go", new Object[0], 64000);

		sayToAll("scripts.events.CtF.AnnounceFinalCountdown", null);
	}

	public static void go()
	{
		if(ConfigValue.CaptureTheFlagCancel)
			removeBuff();
		_status = 2;
		upParalyzePlayers();
		clearArena();
		sayToAll("scripts.events.CtF.AnnounceFight", null);
		_endTask = executeTask("events.CtF.CtF", "endOfTime", new Object[0], 300000);
	}

	public static void removeBuff()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player != null)
			{
				try
				{
					if(player.isCastingNow())
						player.abortCast(true);
					player.getEffectList().stopAllEffects();
					if(player.getPet() != null)
					{
						L2Summon summon = player.getPet();
						summon.getEffectList().stopAllEffects();
						if(summon.isPet())
							summon.unSummon();
					}
					if(player.getAgathion() != null)
						player.setAgathion(0);
					player.sendPacket(new SkillList(player));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				buffPlayer(player);
			}
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player != null)
			{
				try
				{
					if(player.isCastingNow())
						player.abortCast(true);
					player.getEffectList().stopAllEffects();
					if(player.getPet() != null)
					{
						L2Summon summon = player.getPet();
						summon.getEffectList().stopAllEffects();
						if(summon.isPet())
							summon.unSummon();
					}
					if(player.getAgathion() != null)
						player.setAgathion(0);
					player.sendPacket(new SkillList(player));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				buffPlayer(player);
			}
		}
	}

	public static void endOfTime()
	{
		endBattle(3); // ничья
	}

	public static void endBattle(int win)
	{
		if(_endTask != null)
		{
			_endTask.cancel(false);
			_endTask = null;
		}

		removeFlags();

		if(redFlag != null)
		{
			redFlag.deleteMe();
			redFlag = null;
		}

		if(blueFlag != null)
		{
			blueFlag.deleteMe();
			blueFlag = null;
		}

		_status = 0;

		switch(win)
		{
			case 1:
				sayToAll("scripts.events.CtF.AnnounceFinishedRedWins", null);
				giveItemsToWinner(false, true, 1);
				break;
			case 2:
				sayToAll("scripts.events.CtF.AnnounceFinishedBlueWins", null);
				giveItemsToWinner(true, false, 1);
				break;
			case 3:
				sayToAll("scripts.events.CtF.AnnounceFinishedDraw", null);
				giveItemsToWinner(true, true, 0.5);
				break;
		}

		sayToAll("scripts.events.CtF.AnnounceEnd", null);
		executeTask("events.CtF.CtF", "end", new Object[0], 30000);
		_isRegistrationActive = false;
	}

	public static void end()
	{
		executeTask("events.CtF.CtF", "ressurectPlayers", new Object[0], 1000);
		executeTask("events.CtF.CtF", "healPlayers", new Object[0], 2000);
		executeTask("events.CtF.CtF", "teleportPlayersToSavedCoords", new Object[0], 3000);
		executeTask("events.CtF.CtF", "autoContinue", new Object[0], 10000);
	}

	public void autoContinue()
	{
		if(reflection != null)
		{
			reflection.startCollapseTimer(1);
			reflection = null;
		}
		players_list1.clear();
		players_list2.clear();

		if(_autoContinue > 0)
		{
			if(_autoContinue >= ConfigValue.CaptureTheFlagMinLevelForCategory.length)
			{
				_autoContinue = 0;
				startTimerTask();
				return;
			}
			start(new String[] { "" + (_autoContinue + 1), "" + (_autoContinue + 1) });
		}
		else
			startTimerTask();
	}

	public void startTimerTask()
	{
		long delay = 0;
		long cur_time = System.currentTimeMillis();

		while(time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0);
		if(_startTask != null)
			_startTask.cancel(true);
		if(delay - cur_time > 0)
			_startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay - cur_time);
		else
			initTimer(true);
	}

	public static void giveItemsToWinner(boolean team1, boolean team2, double rate)
	{
		if(!ConfigValue.CaptureTheFlagAddItemDraw && team1 && team2)
			return;
		if(team1)
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				if(player.getAttainment() != null)
					player.getAttainment().event_battle_end(0, true);
				addItem(player, ConfigValue.CaptureTheFlagBonusID, Math.round((ConfigValue.CaptureTheFlagRate ? player.getLevel() : 1) * ConfigValue.CaptureTheFlagBonusCount * rate));
			}
		if(team2)
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				if(player.getAttainment() != null)
					player.getAttainment().event_battle_end(0, true);
				addItem(player, ConfigValue.CaptureTheFlagBonusID, Math.round((ConfigValue.CaptureTheFlagRate ? player.getLevel() : 1) * ConfigValue.CaptureTheFlagBonusCount * rate));
			}
	}

	public static void teleportPlayersToColiseum()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			unRide(player);
			unSummonPet(player, true);
			int[] pos = team1loc.getRandomPoint();
			player.setIsInEvent((byte) 3);
			if(ConfigValue.CaptureTheFlagNoParty)
				player.can_create_party = false;
			lockItems(player);
			player.setIsInvul(false);

			player.setVar("backCoords", player.getLoc().toXYZString());
			player.setReflection(reflection);
			player.teleToLocation(pos[0], pos[1], pos[2]);
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			unRide(player);
			unSummonPet(player, true);
			int[] pos = team2loc.getRandomPoint();
			player.setIsInEvent((byte) 3);
			if(ConfigValue.CaptureTheFlagNoParty)
				player.can_create_party = false;
			lockItems(player);
			player.setIsInvul(false);
			player.setVar("backCoords", player.getLoc().toXYZString());
			player.setReflection(reflection);
			player.teleToLocation(pos[0], pos[1], pos[2]);

		}
	}

	private static void lockItems(L2Player player)
	{
		if(ConfigValue.CaptureTheFlagOlympiadItems || ConfigValue.CaptureTheFlagForbiddenItems.length > 0)
		{
			TIntArrayList items = new TIntArrayList();

			if(ConfigValue.CaptureTheFlagForbiddenItems.length > 0)
			{
				for(int i = 0; i < ConfigValue.CaptureTheFlagForbiddenItems.length; i++)
				{
					items.add(ConfigValue.CaptureTheFlagForbiddenItems[i]);
				}
			}

			if(ConfigValue.CaptureTheFlagOlympiadItems)
			{
				for(L2ItemInstance item : player.getInventory().getItems())
				{
					if(!item.getOlympiadUse())
						items.add(item.getItemId());
				}
			}

			player.getInventory().lockItems(LockType.INCLUDE, items.toArray());
		}
	}

	public static void teleportPlayersToSavedCoords()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			try
			{
				if(ConfigValue.CaptureTheFlagOlympiadItems || ConfigValue.CaptureTheFlagForbiddenItems.length > 0)
					player.getInventory().unlock();
				player.setTeam(0, true);
				player.setEventReg(false);
				player.setIsInEvent((byte) 0);
				player.getEffectList().stopAllEffects();
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.getEffectList().stopAllEffects();
				}
				if(ConfigValue.CaptureTheFlagNoParty)
					player.can_create_party = true;
				player.setIsInvul(false);

				String back = player.getVar("backCoords");
				if(back != null)
				{
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					player.teleToLocation(new Location(back), 0);
				}
			}
			catch(Exception e)
			{
				player.setIsInvul(false);
				player.teleToLocation(147800, -55320, -2728, 0);
				player.unsetVar("backCoords");
				player.unsetVar("reflection");
				e.printStackTrace();
			}
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			try
			{
				if(ConfigValue.CaptureTheFlagOlympiadItems || ConfigValue.CaptureTheFlagForbiddenItems.length > 0)
					player.getInventory().unlock();
				player.setTeam(0, true);
				player.setEventReg(false);
				player.setIsInEvent((byte) 0);
				player.getEffectList().stopAllEffects();
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.getEffectList().stopAllEffects();
				}
				if(ConfigValue.CaptureTheFlagNoParty)
					player.can_create_party = true;
				player.setIsInvul(false);

				String back = player.getVar("backCoords");
				if(back != null)
				{
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					player.teleToLocation(new Location(back), 0);
				}

			}
			catch(Exception e)
			{
				player.setIsInvul(false);
				player.teleToLocation(147800, -55320, -2728, 0);
				player.unsetVar("backCoords");
				player.unsetVar("reflection");
				e.printStackTrace();
			}
		}
	}

	public static void paralyzePlayers()
	{
		if(ConfigValue.CaptureTheFlagCancel)
			removeBuff();
		L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
			player.getEffectList().stopEffect(1540);
			player.getEffectList().stopEffect(1418);
			player.getEffectList().stopEffect(396);
			player.getEffectList().stopEffect(914);
			player.stopAbnormalEffect(AbnormalVisualEffect.ave_ultimate_defence);
			player.stopAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			player.setIsInvul(false);
			player.p_block_move(true, null);
			player.block_hp_mp(true);
			player.startAMuted();
			player.startPMuted();
			//player.setParalyzedSkill(true);
			if(player.getPet() != null)
			{
				player.getPet().p_block_move(true, null);
				player.getPet().block_hp_mp(true);
				player.getPet().startAMuted();
				player.getPet().startPMuted();
				//player.getPet().setParalyzedSkill(true);
			}

			/*revengeSkill.getEffects(player, player, false, false);
			if(player.getPet() != null)
				revengeSkill.getEffects(player, player.getPet(), false, false);*/
			if(player.getParty() != null)
				player.getParty().oustPartyMember(player);
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
			player.getEffectList().stopEffect(1540);
			player.getEffectList().stopEffect(1418);
			player.getEffectList().stopEffect(396);
			player.getEffectList().stopEffect(914);
			player.stopAbnormalEffect(AbnormalVisualEffect.ave_ultimate_defence);
			player.stopAbnormalEffect(AbnormalVisualEffect.ave_invincibility);
			player.setIsInvul(false);
			/*revengeSkill.getEffects(player, player, false, false);
			if(player.getPet() != null)
				revengeSkill.getEffects(player, player.getPet(), false, false);*/
			player.p_block_move(true, null);
			player.block_hp_mp(true);
			player.startAMuted();
			player.startPMuted();
			//player.setParalyzedSkill(true);
			if(player.getPet() != null)
			{
				player.getPet().p_block_move(true, null);
				player.getPet().block_hp_mp(true);
				player.getPet().startAMuted();
				player.getPet().startPMuted();
				//player.getPet().setParalyzedSkill(true);
			}

			if(player.getParty() != null)
				player.getParty().oustPartyMember(player);
		}
	}

	public static void upParalyzePlayers()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			/*player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
			if(player.getPet() != null)
				player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);*/
			player.p_block_move(false, null);
			player.unblock_hp_mp(true);
			player.stopAMuted();
			player.stopPMuted();
			//player.setParalyzedSkill(false);
			if(player.getPet() != null)
			{
				player.getPet().p_block_move(false, null);
				player.getPet().unblock_hp_mp(true);
				player.getPet().stopAMuted();
				player.getPet().stopPMuted();
				//player.getPet().setParalyzedSkill(false);
			}
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			/*player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
			if(player.getPet() != null)
				player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);*/
			player.p_block_move(false, null);
			player.unblock_hp_mp(true);
			player.stopAMuted();
			player.stopPMuted();
			//player.setParalyzedSkill(false);
			if(player.getPet() != null)
			{
				player.getPet().p_block_move(false, null);
				player.getPet().unblock_hp_mp(true);
				player.getPet().stopAMuted();
				player.getPet().stopPMuted();
				//player.getPet().setParalyzedSkill(false);
			}
		}
	}

	public static void ressurectPlayers()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(player.isDead())
			{
				player.restoreExp();
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp(), true);
				player.setCurrentMp(player.getMaxMp());
				player.broadcastPacket(new Revive(player));
			}
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(player.isDead())
			{
				player.restoreExp();
				player.setCurrentCp(player.getMaxCp());
				player.setCurrentHp(player.getMaxHp(), true);
				player.setCurrentMp(player.getMaxMp());
				player.broadcastPacket(new Revive(player));
			}
		}
	}

	public static void healPlayers()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
	}

	public static void cleanPlayers()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(!checkPlayer(player, false))
				removePlayer(player);
			else
				player.setTeam(1, true);
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(!checkPlayer(player, false))
				removePlayer(player);
			else
				player.setTeam(2, true);
		}
	}

	public static void clearArena()
	{
		for(L2Object obj : _zone.getObjects())
			if(obj != null)
			{
				L2Player player = obj.getPlayer();
				if(player != null && !players_list1.contains(player.getRef()) && !players_list2.contains(player.getRef()) && player.getReflection() == reflection)
				{
					player.teleToLocation(147451, 46728, -3410);
					player.setIsInvul(false);
				}
			}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(_status > 1 && self != null && self.isPlayer() && self.getTeam() > 0 && (players_list1.contains(self.getRef()) || players_list2.contains(self.getRef())))
		{
			dropFlag((L2Player) self);
			executeTask("events.CtF.CtF", "resurrectAtBase", new Object[] { (L2Player) self }, 10000);
		}
	}

	public static void resurrectAtBase(L2Player player)
	{
		if(player.getTeam() <= 0)
			return;
		player.setIsInvul(false);
		if(player.isDead())
		{
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp(), true);
			player.setCurrentMp(player.getMaxMp());
			player.broadcastPacket(new Revive(player));
		}
		int[] pos;
		if(player.getTeam() == 1)
			pos = team1loc.getRandomPoint();
		else
			pos = team2loc.getRandomPoint();
		player.teleToLocation(pos[0], pos[1], pos[2]);
	}

	public static Location OnEscape(L2Player player)
	{
		if(player != null)
			player.setEventReg(false);
		if(_status > 1 && player != null && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())))
			removePlayer(player);
		return null;
	}

	public static void OnPlayerExit(L2Player player)
	{
		if(player != null)
			player.setEventReg(false);
		if(player != null && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())))
		{
			// Вышел или вылетел во время регистрации
			if(_status == 0 && _isRegistrationActive && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())))
			{
				removePlayer(player);
				return;
			}

			// Вышел или вылетел во время телепортации
			if(_status == 1 && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())))
			{
				removePlayer(player);

				try
				{
					player.setIsInvul(false);
					String back = player.getVar("backCoords");
					if(back != null)
					{
						player.unsetVar("backCoords");
						player.unsetVar("reflection");
						player.teleToLocation(new Location(back), 0);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				return;
			}

			// Вышел или вылетел во время эвента
			OnEscape(player);
		}
	}

	private class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(object == null)
				return;
			L2Player player = object.getPlayer();
			if(_status > 0 && player != null && !players_list1.contains(player.getRef()) && !players_list2.contains(player.getRef()) && player.getReflection() == reflection)
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(147451, 46728, -3410)), 3000);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(object == null)
				return;
			L2Player player = object.getPlayer();
			if(_status > 1 && player != null && player.getTeam() > 0 && (players_list1.contains(player.getRef()) || players_list2.contains(player.getRef())) && player.getReflection() == reflection)
			{
				double angle = Util.convertHeadingToDegree(object.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				int x = (int) (object.getX() + 50 * Math.sin(radian));
				int y = (int) (object.getY() - 50 * Math.cos(radian));
				int z = object.getZ();
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(x, y, z)), 3000);
			}
		}
	}

	private class RedBaseZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(object == null || !object.isPlayer())
				return;
			L2Player player = object.getPlayer();
			if(_status > 0 && player != null && players_list1.contains(player.getRef()) && player.isTerritoryFlagEquipped())
				endBattle(2);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{}
	}

	private class BlueBaseZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(object == null || !object.isPlayer())
				return;
			L2Player player = object.getPlayer();
			if(_status > 0 && player != null && players_list2.contains(player.getRef()) && player.isTerritoryFlagEquipped())
				endBattle(1);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{}
	}

	public class TeleportTask extends l2open.common.RunnableImpl
	{
		Location loc;
		L2Character target;

		public TeleportTask(L2Character target, Location loc)
		{
			this.target = target;
			this.loc = loc;
			target.startStunning();
		}

		public void runImpl()
		{
			target.stopStunning();
			target.teleToLocation(loc);
		}
	}

	private static void removePlayer(L2Player player)
	{
		if(player != null)
		{
			players_list1.remove(player.getRef());
			players_list2.remove(player.getRef());
			player.setTeam(0, true);
			player.setEventReg(false);
			player.setIsInEvent((byte) 0);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}
			if(ConfigValue.CaptureTheFlagNoParty)
				player.can_create_party = true;
			dropFlag(player);

			if(ConfigValue.CaptureTheFlagOlympiadItems || ConfigValue.CaptureTheFlagForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
	}

	private static void addFlag(L2Player player, int flagId)
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(flagId);
		item.setCustomType1(77);
		item.setCustomFlags(L2ItemInstance.FLAG_EQUIP_ON_PICKUP | L2ItemInstance.FLAG_NO_DESTROY | L2ItemInstance.FLAG_NO_TRADE | L2ItemInstance.FLAG_NO_UNEQUIP, false);
		player.getInventory().addItem(item);
		player.getInventory().equipItem(item, false);
		player.sendChanges();
		player.sendPacket(Msg.YOU_VE_ACQUIRED_THE_WARD_MOVE_QUICKLY_TO_YOUR_FORCES__OUTPOST);
	}

	private static void removeFlags()
	{
		for(HardReference<L2Player> ref : players_list1)
			removeFlag(ref.get());
		for(HardReference<L2Player> ref : players_list2)
			removeFlag(ref.get());
	}

	private static void removeFlag(L2Player player)
	{
		if(player != null && player.isTerritoryFlagEquipped())
		{
			L2ItemInstance flag = player.getActiveWeaponInstance();
			if(flag != null && flag.getCustomType1() == 77) // 77 это эвентовый флаг
			{
				flag.setCustomFlags(0, false);
				player.getInventory().destroyItem(flag, 1, false);
				player.broadcastUserInfo(true);
			}
		}
	}

	private static void dropFlag(L2Player player)
	{
		if(player != null && player.isTerritoryFlagEquipped())
		{
			L2ItemInstance flag = player.getActiveWeaponInstance();
			if(flag != null && flag.getCustomType1() == 77) // 77 это эвентовый флаг
			{
				flag.setCustomFlags(0, false);
				player.getInventory().destroyItem(flag, 1, false);
				player.broadcastUserInfo(true);
				if(flag.getItemId() == 13560)
				{
					redFlag.setXYZInvisible(player.getLoc());
					redFlag.spawnMe();
				}
				else if(flag.getItemId() == 13561)
				{
					blueFlag.setXYZInvisible(player.getLoc());
					blueFlag.spawnMe();
				}
			}
		}
	}

	private static synchronized void buffPlayer(L2Player player)
	{
		if(player != null && ConfigValue.CaptureTheFlagBuff)
		{
			int[][] buff;
			if(player.getClassId().isMage())
				buff = ConfigValue.CaptureTheFlagMagicBuff;
			else
				buff = ConfigValue.CaptureTheFlagPhisicBuff;
			for(int[] sk : buff)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(sk[0], sk[1]);
				int buffTime = skill.isMusic() ? ConfigValue.CaptureTheFlagDanceAndSongTime : ConfigValue.CaptureTheFlagBuffTime;
				if(!skill.checkSkillAbnormal(player) && !skill.isBlockedByChar(player, skill))
					for(EffectTemplate et : skill.getEffectTemplates())
					{
						Env env = new Env(player, player, skill);
						L2Effect effect = et.getEffect(env);
						if(effect != null && effect.getCount() == 1 && effect.getTemplate()._instantly && !effect.getSkill().isToggle())
						{
							// Эффекты однократного действия не шедулятся, а применяются немедленно
							// Как правило это побочные эффекты для скиллов моментального действия
							effect.onStart();
							effect.onActionTime();
							effect.onExit();
						}
						else if(effect != null)
						{
							if(buffTime > 0)
								effect.setPeriod(buffTime * 1000);
							player.getEffectList().addEffect(effect);
						}
					}
			}
			player.updateEffectIcons();
			player.setCurrentCp(player.getMaxCp());
			player.setCurrentHp(player.getMaxHp(), false);
			player.setCurrentMp(player.getMaxMp());
		}
	}
}