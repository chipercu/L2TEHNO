package events.TvT;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.EventMaster;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.*;
import l2open.util.reference.*;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class TvT extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(TvT.class.getName());
	private static Reflection tvt_reflection = null;
	private static GameInstances game = new GameInstances();

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
				_log.info("TvT not started: another event is already running");
				startTimerTask();
				return;
			}

			if(TerritorySiege.isInProgress())
			{
				_log.info("TvT not started: TerritorySiege in progress");
				startTimerTask();
				return;
			}

			for(Castle c : CastleManager.getInstance().getCastles().values())
				if(c.getSiege() != null && c.getSiege().isInProgress())
				{
					_log.info("TvT not started: CastleSiege in progress");
					startTimerTask();
					return;
				}

			if(ConfigValue.TeamvsTeamCategories)
				start(new String[] { "1", "1" });
			else
				start(new String[] { "-1", "-1" });
		}
	}

	private static ScheduledFuture<?> _startTask;

	public static GCSArray<HardReference<L2Player>> players_list1 = new GCSArray<HardReference<L2Player>>();
	public static GCSArray<HardReference<L2Player>> players_list2 = new GCSArray<HardReference<L2Player>>();
	public static GCSArray<HardReference<L2Player>> live_list1 = new GCSArray<HardReference<L2Player>>();
	public static GCSArray<HardReference<L2Player>> live_list2 = new GCSArray<HardReference<L2Player>>();

	private static boolean _isRegistrationActive = false;
	private static int _status = 0;
	private static int _time_to_start;
	private static int _category;
	private static int _minLevel;
	private static int _maxLevel;
	private static int _autoContinue = 0;

	private static long startTime;

	private static int _teamRedKill = 0;
	private static int _teamBlueKill = 0;

	private static ScheduledFuture<?> _endTask;

	private static L2Zone _zone;
	ZoneListener _zoneListener = new ZoneListener();

	private static int loc_id = 0;
	private static int[] loc_list = {8000}; // {7015, 7016, 7017};
	private static Location[][] team_loc_list = 
	{
		{new Location(150008,46712,-3408), new Location(148888,46744,-3408)},
		/*{new Location(113368,14792,10080), new Location(115672,17096,10080)},
		{new Location(17992,213144,-9352), new Location(14584,213112,-9352)},
		{new Location(-248856,210152,-11968), new Location(-252024,206328,-11960)}*/
	};

	private static List<Long> time2 = new ArrayList<Long>();

	private void initTimer(boolean new_day)
	{
		time2.clear();
		if(ConfigValue.TeamvsTeamStartTime[0] == -1)
			return;
		long cur_time = System.currentTimeMillis();
		for(int i = 0; i < ConfigValue.TeamvsTeamStartTime.length; i += 2)
		{
			Calendar ci = Calendar.getInstance();
			if(new_day)
				ci.add(Calendar.HOUR_OF_DAY, 12);
			ci.set(Calendar.HOUR_OF_DAY, ConfigValue.TeamvsTeamStartTime[i]);
			ci.set(Calendar.MINUTE, ConfigValue.TeamvsTeamStartTime[i + 1]);
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
		initTimer(false);

		_active = ServerVariables.getString("TvT", "on").equalsIgnoreCase("on");

		_log.info("Loaded Event: TvT");
	}

	public void onReload()
	{
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
			ServerVariables.set("TvT", "on");
			_log.info("Event 'TvT' activated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'TvT' already active.");

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
			ServerVariables.unset("TvT");
			_log.info("Event 'TvT' deactivated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'TvT' not active.");

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
			return Files.read("data/scripts/events/TvT/31225.html", player);
		}
		return "";
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
			_minLevel = ConfigValue.TeamvsTeamMinLevelForCategory[_category-1];
			_maxLevel = ConfigValue.TeamvsTeamMaxLevelForCategory[_category-1];
		}

		if(_endTask != null)
		{
			if(player != null)
				player.sendMessage(new CustomMessage("common.TryLater", player));
			return;
		}
		tvt_reflection = new Reflection("TvTInstances");
		tvt_reflection.setGeoIndex(GeoEngine.NextGeoIndex(24, 19, tvt_reflection.getId()));
		tvt_reflection.addDoor(24190002);
		tvt_reflection.addDoor(24190003);

		for(L2DoorInstance d : tvt_reflection.getDoors())
		{
			d.setReflection(tvt_reflection);
			d.spawnMe();
			d.closeMe();
		}

		_status = 0;
		_isRegistrationActive = true;
		_time_to_start = ConfigValue.TeamvsTeamTime;

		String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
		sayToAll("scripts.events.TvT.AnnouncePreStart", param);

		executeTask("events.TvT.TvT", "question", new Object[0], 10000);
		executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
	}

	public static void sayToAll(String address, String[] replacements)
	{
		Announcements.getInstance().announceByCustomMessage(address, replacements, Say2C.CRITICAL_ANNOUNCEMENT);
	}

	public static void question()
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().getId() <= 0 && !player.isInOlympiadMode() && !player.isDead())
				if(!player.isInZone(ZoneType.epic) && !player.isFlying() && player.getVar("jailed") == null)
					player.scriptRequest(new CustomMessage("scripts.events.TvT.AskPlayer", player).toString(), "events.TvT.TvT:addPlayer", new Object[0]);
	}

	public static void announce()
	{
		if(players_list1.isEmpty() || players_list2.isEmpty())
		{
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				player.setEventReg(false);
			}
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				player.setEventReg(false);
			}
			sayToAll("scripts.events.TvT.AnnounceEventCancelled", null);
			_isRegistrationActive = false;
			_status = 0;
			executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
			players_list1.clear();
			players_list2.clear();
			live_list1.clear();
			live_list2.clear();
			return;
		}

		if(_time_to_start > 1)
		{
			_time_to_start--;
			String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
			sayToAll("scripts.events.TvT.AnnouncePreStart", param);
			executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
		}
		else
		{
			_status = 1;
			_isRegistrationActive = false;
			sayToAll("scripts.events.TvT.AnnounceEventStarting", null);
			executeTask("events.TvT.TvT", "prepare", new Object[0], 5000);
		}
	}

	public static void addPlayer(L2Player player)
	{
		int team = 0, size1 = players_list1.size(), size2 = players_list2.size();

		if(size1 > size2)
			team = 2;
		else if(size1 < size2)
			team = 1;
		else
			team = Rnd.get(1, 2);

		player.s_ai0 = player.getTitle();
		if(team == 1)
		{
			player.setEventReg(true);
			players_list1.add(player.getRef());
			live_list1.add(player.getRef());
			player.sendMessage(new CustomMessage("scripts.events.TvT.Registered", player));
		}
		else if(team == 2)
		{
			player.setEventReg(true);
			players_list2.add(player.getRef());
			live_list2.add(player.getRef());
			player.sendMessage(new CustomMessage("scripts.events.TvT.Registered", player));
		}
		else
			_log.info("WTF??? Command id 0 in TvT...");
	}

	public void addPlayer()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || !checkPlayer(player, true))
			return;

		if(ConfigValue.TeamvsTeamIP)
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

		if(ConfigValue.TeamvsTeamHWID)
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

		player.s_ai0 = player.getTitle();
		if(team == 1)
		{
			player.setEventReg(true);
			players_list1.add(player.getRef());
			live_list1.add(player.getRef());
			player.sendMessage(new CustomMessage("scripts.events.TvT.Registered", player));
		}
		else if(team == 2)
		{
			player.setEventReg(true);
			players_list2.add(player.getRef());
			live_list2.add(player.getRef());
			player.sendMessage(new CustomMessage("scripts.events.TvT.Registered", player));
		}
		else
			_log.info("WTF??? Command id 0 in TvT...");
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
			player.sendMessage(new CustomMessage("scripts.events.TvT.Cancelled", player));
			return false;
		}
		else if(first && (player.isInEvent() != 0 || player.isEventReg()))
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.OtherEvent", player).addString(player.getEventName(player.isInEvent())));
			return false;
		}
		else if(player.getLevel() < _minLevel || player.getLevel() > _maxLevel)
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledLevel", player));
			return false;
		}
		else if(player.isMounted())
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.Cancelled", player));
			return false;
		}
		else if(player.getDuel() != null)
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledDuel", player));
			return false;
		}
		else if(player.getTeam() != 0)
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player));
			return false;
		}
		else if(player.getOlympiadGame() != null || player.isInZoneOlympiad() || first && Olympiad.isRegistered(player))
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledOlympiad", player));
			return false;
		}
		else if(player.isInParty() && player.getParty().isInDimensionalRift())
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player));
			return false;
		}
		else if(player.isTeleporting())
		{
			player.sendMessage(new CustomMessage("scripts.events.TvT.CancelledTeleport", player));
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
		else if(player.isSubClassActive() && !ConfigValue.TeamvsTeamSub)
		{
			player.sendMessage("Принимать участие в ивенте, можно только основным классом!");
			return false;
		}
		return true;
	}

	public void prepare()
	{
		loc_id = Rnd.get(loc_list.length);
		_zone = ZoneManager.getInstance().getZoneById(ZoneType.battle_zone, loc_list[loc_id], false);
		_zone.reflection = tvt_reflection.getId();
		_zone.setActive(true);
		_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);

		cleanPlayers();
		clearArena();
		_teamRedKill = 0;
		_teamBlueKill = 0;
		ressurectPlayers();
		paralyzePlayers();

		executeTask("events.TvT.TvT", "teleportPlayersToColiseum", new Object[0], 4000);
		executeTask("events.TvT.TvT", "healPlayers", new Object[0], 6000);
		executeTask("events.TvT.TvT", "go", new Object[0], 64000);

		sayToAll("scripts.events.TvT.AnnounceFinalCountdown", null);
	}

	public static void go()
	{
		startTime = System.currentTimeMillis() + ConfigValue.TeamvsTeamBattleTime * 60 * 1000L;

		_status = 2;
		upParalyzePlayers();
		checkLive();
		//removeBuff();
		clearArena();
		sayToAll("scripts.events.TvT.AnnounceFight", null);
		_endTask = executeTask("events.TvT.TvT", "endBattle", new Object[0], ConfigValue.TeamvsTeamBattleTime * 60 * 1000L);
		updateStartCubeIndicator();
	}

	public static void removeBuff()
	{
		if(ConfigValue.TeamvsTeamBuff || ConfigValue.TeamvsTeamCancel)
		{
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player player = ref.get();
				if(player != null)
				{
					if(ConfigValue.TeamvsTeamCancel)
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
					if(ConfigValue.TeamvsTeamBuff)
						buffPlayer(player);
				}
			}
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player player = ref.get();
				if(player != null)
				{
					if(ConfigValue.TeamvsTeamCancel)
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
					if(ConfigValue.TeamvsTeamBuff)
						buffPlayer(player);
				}
			}
		}
	}

	public static void endBattle()
	{
		_status = 0;
		removeAura();
		boolean _isRedWinner = true;

		if(ConfigValue.TeamvsTeamBattleCount)
		{
			if(getTeamRedKill() < getTeamBlueKill())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
				giveItemsToWinner(false, true, 1);
				_isRedWinner = false;
			}
			else if(getTeamBlueKill() < getTeamRedKill())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
				giveItemsToWinner(true, false, 1);
			}
			else if(getTeamBlueKill() == getTeamRedKill())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedDraw", null);
				giveItemsToWinner(true, true, 0.5);
			}
		}
		else
		{
			if(live_list1.isEmpty())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
				giveItemsToWinner(false, true, 1);
				_isRedWinner = false;
			}
			else if(live_list2.isEmpty())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
				giveItemsToWinner(true, false, 1);
			}
			else if(live_list1.size() < live_list2.size())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
				giveItemsToWinner(false, true, 1);
				_isRedWinner = false;
			}
			else if(live_list1.size() > live_list2.size())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
				giveItemsToWinner(true, false, 1);
			}
			else if(live_list1.size() == live_list2.size())
			{
				sayToAll("scripts.events.TvT.AnnounceFinishedDraw", null);
				giveItemsToWinner(true, true, 0.5);
			}
		}

		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.sendPacket(new ExCubeGameEnd(_isRedWinner));
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.sendPacket(new ExCubeGameEnd(_isRedWinner));
		}

		sayToAll("scripts.events.TvT.AnnounceEnd", null);
		executeTask("events.TvT.TvT", "end", new Object[0], 30000);
		_isRegistrationActive = false;
		if(_endTask != null)
		{
			_endTask.cancel(false);
			_endTask = null;
		}
	}

	public static void end()
	{
		executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000);
		executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000);
		executeTask("events.TvT.TvT", "teleportPlayersToSavedCoords", new Object[0], 3000);
		executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
	}

	public void autoContinue()
	{
		if(tvt_reflection != null)
		{
			tvt_reflection.startCollapseTimer(1);
			tvt_reflection = null;
		}
		live_list1.clear();
		live_list2.clear();
		players_list1.clear();
		players_list2.clear();

		if(_autoContinue > 0)
		{
			if(_autoContinue >= ConfigValue.TeamvsTeamMinLevelForCategory.length)
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
		if(team1)
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				if(player.getAttainment() != null)
					player.getAttainment().event_battle_end(2, true);
				addItem(player, ConfigValue.TeamvsTeamBonusID, Math.round((ConfigValue.TeamvsTeamRate ? player.getLevel() : 1) * ConfigValue.TeamvsTeamBonusCount * rate));
			}
		if(team2)
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				if(player.getAttainment() != null)
					player.getAttainment().event_battle_end(2, true);
				addItem(player, ConfigValue.TeamvsTeamBonusID, Math.round((ConfigValue.TeamvsTeamRate ? player.getLevel() : 1) * ConfigValue.TeamvsTeamBonusCount * rate));
			}
	}

	public static void teleportPlayersToColiseum()
	{
		List<L2Player> team_list1 = new ArrayList<L2Player>();
		List<L2Player> team_list2 = new ArrayList<L2Player>();
		
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null || player.isLogout())
			{
				players_list1.remove(ref);
				players_list2.remove(ref);
				live_list1.remove(ref);
				live_list2.remove(ref);
				continue;
			}
			team_list1.add(player);
		}

		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null || player.isLogout())
			{
				players_list1.remove(ref);
				players_list2.remove(ref);
				live_list1.remove(ref);
				live_list2.remove(ref);
				continue;
			}
			team_list2.add(player);
		}

		for(L2Player player : team_list1)
		{
			if(player == null)
				continue;
			player.sendPacket(new ExCubeGameTeamList(team_list1, team_list2, 1));
			unRide(player);
			unSummonPet(player, true);

			Location zone1 = team_loc_list[loc_id][0];
			Location pos = Rnd.coordsRandomize(zone1.x, zone1.y, zone1.z, 0, 0, 500);

			player.setIsInEvent((byte) 4);
			player.setEventMaster(game);
			lockItems(player);
			player.setIsInvul(false);

			player.setVar("backCoords", player.getLoc().toXYZString());
			player.setReflection(tvt_reflection);
			player.teleToLocation(pos.x, pos.y, pos.z);
		}
		for(L2Player player : team_list2)
		{
			if(player == null)
				continue;
			player.sendPacket(new ExCubeGameTeamList(team_list1, team_list2, 1));
			unRide(player);
			unSummonPet(player, true);

			Location zone2 = team_loc_list[loc_id][1];
			Location pos = Rnd.coordsRandomize(zone2.x, zone2.y, zone2.z, 0, 0, 500);

			player.setIsInEvent((byte) 4);
			player.setEventMaster(game);
			lockItems(player);
			player.setIsInvul(false);

			player.setVar("backCoords", player.getLoc().toXYZString());
			player.setReflection(tvt_reflection);
			player.teleToLocation(pos.x, pos.y, pos.z);
		}
	}

	private static void lockItems(L2Player player)
	{
		if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
		{
			TIntArrayList items = new TIntArrayList();

			if(ConfigValue.TeamvsTeamForbiddenItems.length > 0)
			{
				for(int i = 0; i < ConfigValue.TeamvsTeamForbiddenItems.length; i++)
				{
					items.add(ConfigValue.TeamvsTeamForbiddenItems[i]);
				}
			}

			if(ConfigValue.TeamvsTeamOlympiadItems)
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

	public void teleportPlayersToSavedCoords()
	{
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			try
			{
				if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
					player.getInventory().unlock();
				//broadcastPacketBattle(new ExCubeGameRemovePlayer(player, false));
				player.setTitle(player.s_ai0 == null ? "" : player.s_ai0);
				player.i_ai0 = 0;
				player.s_ai0 = null;
				player.setIsInvul(false);

				String back = player.getVar("backCoords");
				if(back != null)
				{
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					player.teleToLocation(new Location(back), 0);
				}
			}
			catch(NumberFormatException e)
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
				if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
					player.getInventory().unlock();
				//broadcastPacketBattle(new ExCubeGameRemovePlayer(player, true));
				player.i_ai0 = 0;
				player.setTitle(player.s_ai0 == null ? "" : player.s_ai0);
				player.s_ai0 = null;
				player.setIsInvul(false);

				String back = player.getVar("backCoords");
				if(back != null)
				{
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					player.teleToLocation(new Location(back), 0);
				}
			}
			catch(NumberFormatException e)
			{
				player.setIsInvul(false);
				player.teleToLocation(147800, -55320, -2728, 0);
				player.unsetVar("backCoords");
				player.unsetVar("reflection");
				e.printStackTrace();
			}
		}
		if(_zone != null)
		{
			_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
			_zone.setActive(false);
			_zone.reflection = -1;
		}
	}

	public static void paralyzePlayers()
	{
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

			/*player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
			if(player.getPet() != null)
				player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);*/
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
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

			/*player.getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);
			if(player.getPet() != null)
				player.getPet().getEffectList().stopEffect(L2Skill.SKILL_RAID_CURSE);*/
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
				player.setTeam(2, true);
		}
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(!checkPlayer(player, false))
				removePlayer(player);
			else
				player.setTeam(1, true);
		}
	}

	public static void checkLive()
	{
		GCSArray<HardReference<L2Player>> new_live_list1 = new GCSArray<HardReference<L2Player>>();
		GCSArray<HardReference<L2Player>> new_live_list2 = new GCSArray<HardReference<L2Player>>();

		for(HardReference<L2Player> ref : live_list1)
		{
			L2Player player = ref.get();
			if(player != null)
				new_live_list1.add(ref);
		}

		for(HardReference<L2Player> ref : live_list2)
		{
			L2Player player = ref.get();
			if(player != null)
				new_live_list2.add(ref);
		}

		live_list1 = new_live_list1;
		live_list2 = new_live_list2;

		for(HardReference<L2Player> ref : live_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(player.isInZone(_zone) && (!player.isDead() || ConfigValue.TeamvsTeamBattleCount) && player.isConnected() && !player.isLogoutStarted())
				player.setTeam(2, true); // Ставим Красную команду.
			else
				loosePlayer(player);
		}

		for(HardReference<L2Player> ref : live_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			if(player.isInZone(_zone) && (!player.isDead() || ConfigValue.TeamvsTeamBattleCount) && player.isConnected() && !player.isLogoutStarted())
				player.setTeam(1, true); // Ставим Голубую команду.
			else
				loosePlayer(player);
		}

		if(live_list1.size() < 1 || live_list2.size() < 1)
			endBattle();
	}

	public static void clearArena()
	{
		for(L2Object obj : _zone.getObjects())
			if(obj != null)
			{
				L2Player player = obj.getPlayer();
				if(player != null && !live_list1.contains(player.getRef()) && !live_list2.contains(player.getRef()) && player.getReflection() == tvt_reflection)
				{
					player.setIsInvul(false);
					player.teleToLocation(147451, 46728, -3410);
				}
			}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(_status > 1 && self != null && self.isPlayer() && self.getTeam() > 0 && (live_list1.contains(self.getRef()) || live_list2.contains(self.getRef())))
		{
			onKill(killer);
			if(!ConfigValue.TeamvsTeamBattleCount)
				loosePlayer((L2Player) self);
			checkLive();
			
			if(ConfigValue.TeamvsTeamBattleCount)
			{
				Location zone1;

				if(self.getTeam() == 1)
					zone1 = team_loc_list[loc_id][0];
				else
					zone1 = team_loc_list[loc_id][1];

				Location pos = Rnd.coordsRandomize(zone1.x, zone1.y, zone1.z, 0, 0, 500);
				self.getPlayer().doRevive();

				self.setReflection(tvt_reflection);
				self.getPlayer().teleToLocation(pos.x, pos.y, pos.z);
				buffPlayer(self.getPlayer());
			}
		}
	}

	public static void onKill(L2Character killer)
	{
		if(killer.isPlayable())
		{
			killer.i_ai0++;
			killer.setTitle("Kills:" + killer.i_ai0);
			killer.broadcastUserInfo(true);

			if(killer.getTeam() == 1)
				addTeamBlueKill();
			else if(killer.getTeam() == 2)
				addTeamRedKill();

			int waitTime = (int) ((startTime - System.currentTimeMillis()) / 1000);
			broadcastPacketBattle(new ExCubeGameExtendedChangePoints(waitTime, getTeamBlueKill(), getTeamRedKill(), true, killer.getPlayer(), killer.i_ai0));
			broadcastPacketBattle(new ExCubeGameExtendedChangePoints(waitTime, getTeamBlueKill(), getTeamRedKill(), false, killer.getPlayer(), killer.i_ai0));
		}
	}

	public static int getTeamBlueKill()
	{
		return _teamBlueKill;
	}

	public static int getTeamRedKill()
	{
		return _teamRedKill;
	}

	public static void addTeamBlueKill()
	{
		_teamBlueKill++;
	}

	public static void addTeamRedKill()
	{
		_teamRedKill++;
	}

	public static Location OnEscape(L2Player player)
	{
		if(_status > 1 && player != null && (live_list1.contains(player.getRef()) || live_list2.contains(player.getRef())))
		{
			player.setTitle(player.s_ai0 == null ? "" : player.s_ai0);
			player.i_ai0 = 0;
			removePlayer(player);
			checkLive();
		}
		return null;
	}

	public static void OnPlayerExit(L2Player player)
	{
		if(player != null && (live_list1.contains(player.getRef()) || live_list2.contains(player.getRef())))
		{
			player.setTitle(player.s_ai0 == null ? "" : player.s_ai0);
			player.i_ai0 = 0;
			// Вышел или вылетел во время регистрации
			if(_status == 0 && _isRegistrationActive)
			{
				removePlayer(player);
				return;
			}

			// Вышел или вылетел во время телепортации
			if(_status == 1)
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
			if(_status > 0 && player != null && !live_list1.contains(player.getRef()) && !live_list2.contains(player.getRef()) && player.getReflection() == tvt_reflection)
				_log.warning("TvT-> objectEntered: "+player.getName());//ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(147451, 46728, -3410)), 3000);
			player.setRestartPoint(false);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(object == null)
				return;
			L2Player player = object.getPlayer();
			if(_status > 1 && player != null && player.getTeam() > 0 && (live_list1.contains(player.getRef()) || live_list2.contains(player.getRef())) && player.getReflection() == tvt_reflection)
			{
				double angle = Util.convertHeadingToDegree(object.getHeading()); // угол в градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				int x = (int) (object.getX() + 50 * Math.sin(radian));
				int y = (int) (object.getY() - 50 * Math.cos(radian));
				int z = object.getZ();
				ThreadPoolManager.getInstance().execute(new TeleportTask((L2Character) object, new Location(x, y, z)));
			}
			player.setRestartPoint(true);
		}
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

	public static void removeAura()
	{
		for(HardReference<L2Player> ref : live_list1)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.setTeam(0, true);
			player.setIsInEvent((byte) 0);
			player.setEventMaster(null);
			player.setEventReg(false);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}
			if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
		for(HardReference<L2Player> ref : live_list2)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.setTeam(0, true);
			player.setIsInEvent((byte) 0);
			player.setEventMaster(null);
			player.setEventReg(false);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}
			if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
	}

	private static void loosePlayer(L2Player player)
	{
		if(player != null)
		{
			live_list1.remove(player.getRef());
			live_list2.remove(player.getRef());
			player.setTeam(0, true);
			player.setIsInEvent((byte) 0);
			player.setEventMaster(null);
			player.setEventReg(false);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}
			player.sendMessage(new CustomMessage("scripts.events.TvT.YouLose", player));
			if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
	}

	private static void removePlayer(L2Player player)
	{
		if(player != null)
		{
			if(_status == 1)
			{
				player.p_block_move(false, null);
				player.unblock_hp_mp(true);
				player.setParalyzedSkill(false);
				if(player.getPet() != null)
				{
					player.getPet().p_block_move(false, null);
					player.getPet().unblock_hp_mp(true);
					player.getPet().setParalyzedSkill(false);
				}
			}
			broadcastPacketBattle(new ExCubeGameRemovePlayer(player, player.getTeam() == 2));
			live_list1.remove(player.getRef());
			live_list2.remove(player.getRef());
			players_list1.remove(player.getRef());
			players_list2.remove(player.getRef());
			player.setTeam(0, true);
			player.setIsInEvent((byte) 0);
			player.setEventMaster(null);
			
			player.setEventReg(false);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}

			if(ConfigValue.TeamvsTeamOlympiadItems || ConfigValue.TeamvsTeamForbiddenItems.length > 0)
				player.getInventory().unlock();
		}
	}

	private static void updateStartCubeIndicator()
	{
		for(HardReference<L2Player> ref : players_list2)
		{
			L2Player ePlayer = ref.get();
			if(ePlayer != null)
			{
				ePlayer.sendPacket(new ExCubeGameExtendedChangePoints(ConfigValue.TeamvsTeamBattleTime*60, 0, 0, false, ePlayer, 0));
				ePlayer.sendPacket(new ExCubeGameChangePoints(ConfigValue.TeamvsTeamBattleTime*60, 0, 0));
			}
		}
		for(HardReference<L2Player> ref : players_list1)
		{
			L2Player ePlayer = ref.get();
			if(ePlayer != null)
			{
				ePlayer.sendPacket(new ExCubeGameExtendedChangePoints(ConfigValue.TeamvsTeamBattleTime*60, 0, 0, true, ePlayer, 0));
				ePlayer.sendPacket(new ExCubeGameChangePoints(ConfigValue.TeamvsTeamBattleTime*60, 0, 0));
			}
		}
	}

	public static void broadcastPacketBattle(L2GameServerPacket packet)
	{
		try
		{
			for(HardReference<L2Player> ref : players_list1)
			{
				L2Player player = ref.get();
				if(player != null)
					player.sendPacket(packet);
			}
		}
		catch(Exception e)
		{}
		try
		{
			for(HardReference<L2Player> ref : players_list2)
			{
				L2Player player = ref.get();
				if(player != null)
					player.sendPacket(packet);
			}
		}
		catch(Exception e)
		{}
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
		player.sendMessage("Вы сняли регистрацию с TvT.");
	}

	private static synchronized void buffPlayer(L2Player player)
	{
		if(player != null)
		{
			int[][] buff;
			if(player.getClassId().isMage())
				buff = ConfigValue.TeamvsTeamMagicBuff;
			else
				buff = ConfigValue.TeamvsTeamPhisicBuff;
			for(int[] sk : buff)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(sk[0], sk[1]);
				int buffTime = skill.isMusic() ? ConfigValue.TeamvsTeamDanceAndSongTime : ConfigValue.TeamvsTeamBuffTime;
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

	private static class GameInstances extends EventMaster
	{
		public boolean canUseSkill(L2Player player, L2Skill skill)
		{
			if((skill.getId() == 628 || skill.getId() == 885 || skill.getId() == 1448) && _status < 2)
				return false;
			return true;
		}
	}
}