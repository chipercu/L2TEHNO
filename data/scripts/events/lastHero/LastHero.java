package events.lastHero;

import gnu.trove.list.array.TIntArrayList;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

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
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.LockType;
import l2open.gameserver.serverpackets.Revive;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.skills.*;
import l2open.gameserver.skills.effects.EffectTemplate;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.reference.*;

public class LastHero extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(LastHero.class.getName());
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
				_log.info("Last Hero not started: another event is already running");
				startTimerTask();
				return;
			}

			if(TerritorySiege.isInProgress())
			{
				_log.info("LastHero not started: TerritorySiege in progress");
				startTimerTask();
				return;
			}

			for(Castle c : CastleManager.getInstance().getCastles().values())
			{
				if(c.getSiege() != null && c.getSiege().isInProgress())
				{
					_log.info("LastHero not started: CastleSiege in progress");
					startTimerTask();
					return;
				}
			}

			if(ConfigValue.LastHeroCategories)
				start(new String[] { "1", "1" });
			else
				start(new String[] { "-1", "-1" });
		}
	}

	private static ScheduledFuture<?> _startTask;

	private static GCSArray<HardReference<L2Player>> players_list = new GCSArray<HardReference<L2Player>>();
	private static GCSArray<HardReference<L2Player>> live_list = new GCSArray<HardReference<L2Player>>();

	private static boolean _isRegistrationActive = false;
	private static int _status = 0;
	private static int _time_to_start;
	private static int _category;
	private static int _minLevel;
	private static int _maxLevel;
	private static int _autoContinue = 0;

	private static List<Long> time2 = new ArrayList<Long>();

	private static ScheduledFuture<?> _endTask;

	private static L2Zone _zone = ZoneManager.getInstance().getZoneByIndex(ZoneType.battle_zone, 4, true);
	ZoneListener _zoneListener = new ZoneListener();

	private void initTimer(boolean new_day)
	{
		time2.clear();
		if(ConfigValue.LastHeroStartTime[0] == -1)
			return;
		long cur_time = System.currentTimeMillis();
		for(int i = 0; i < ConfigValue.LastHeroStartTime.length; i += 2)
		{
			Calendar ci = Calendar.getInstance();
			if(new_day)
				ci.add(Calendar.HOUR_OF_DAY, 12);
			ci.set(Calendar.HOUR_OF_DAY, ConfigValue.LastHeroStartTime[i]);
			ci.set(Calendar.MINUTE, ConfigValue.LastHeroStartTime[i + 1]);
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
		initTimer(false);
		_active = ServerVariables.getString("LastHero", "on").equalsIgnoreCase("on");
		_log.info("Loaded Event: Last Hero");
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
			ServerVariables.set("LastHero", "on");
			_log.info("Event 'Last Hero' activated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Last Hero' already active.");

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
			ServerVariables.unset("LastHero");
			_log.info("Event 'Last Hero' deactivated.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.LastHero.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'LastHero' not active.");

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
			return Files.read("data/scripts/events/lastHero/31225.html", player);
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
			_minLevel = ConfigValue.LastHeroMinLevelForCategory[_category-1];
			_maxLevel = ConfigValue.LastHeroMaxLevelForCategory[_category-1];
		}

		if(_endTask != null)
		{
			if(player != null)
				player.sendMessage(new CustomMessage("common.TryLater", player));
			return;
		}
		reflection = new Reflection("LHInstances");
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
		_time_to_start = ConfigValue.LastHeroTime;

		players_list = new GCSArray<HardReference<L2Player>>();
		live_list = new GCSArray<HardReference<L2Player>>();

		String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
		sayToAll("scripts.events.LastHero.AnnouncePreStart", param);

		executeTask("events.lastHero.LastHero", "question", new Object[0], 10000);
		executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000);
	}

	public static void sayToAll(String address, String[] replacements)
	{
		Announcements.getInstance().announceByCustomMessage(address, replacements, Say2C.CRITICAL_ANNOUNCEMENT);
	}

	public static void question()
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().getId() <= 0 && !player.isInOlympiadMode() && !player.isDead() && !player.isInZone(ZoneType.epic) && !player.isFlying() && player.getVar("jailed") == null && player.getVarB("event_invite", true))
				player.scriptRequest(new CustomMessage("scripts.events.LastHero.AskPlayer", player).toString(), "events.lastHero.LastHero:addPlayer", new Object[0]);
	}

	public static void announce()
	{
		if(players_list.size() < 2)
		{
			sayToAll("scripts.events.LastHero.AnnounceEventCancelled", null);
			_isRegistrationActive = false;
			_status = 0;
			executeTask("events.lastHero.LastHero", "autoContinue", new Object[0], 10000);
			if(!players_list.isEmpty())
			{
				for(HardReference<L2Player> ref : players_list)
				{
					L2Player p = ref.get();
					if(p == null)
						continue;
					p.setEventReg(false);
				}
			}
			players_list.clear();
			return;
		}

		if(_time_to_start > 1)
		{
			_time_to_start--;
			String[] param = { String.valueOf(_time_to_start), String.valueOf(_minLevel), String.valueOf(_maxLevel) };
			sayToAll("scripts.events.LastHero.AnnouncePreStart", param);
			executeTask("events.lastHero.LastHero", "announce", new Object[0], 60000);
		}
		else
		{
			_status = 1;
			_isRegistrationActive = false;
			sayToAll("scripts.events.LastHero.AnnounceEventStarting", null);
			executeTask("events.lastHero.LastHero", "prepare", new Object[0], 5000);
		}
	}

	public void addPlayer()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null || !checkPlayer(player, true))
			return;

		if(ConfigValue.LastHeroIP)
		{
			for(HardReference<L2Player> ref : players_list)
			{
				L2Player p = ref.get();
				if(p != null && p.getIP().equals(player.getIP()))
				{
					player.sendMessage("Игрок с данным IP уже зарегистрирован.");
					return;
				}
			}
		}

		if(ConfigValue.LastHeroHWID)
		{
			for(HardReference<L2Player> ref : players_list)
			{
				L2Player p = ref.get();
				if(p != null && p.getHWIDs().equals(player.getHWIDs()))
				{
					player.sendMessage("С данного компьютера уже зарегистрирован 1 игрок.");
					return;
				}
			}
		}
		player.setEventReg(true);
		players_list.add(player.getRef());
		live_list.add(player.getRef());

		player.sendMessage(new CustomMessage("scripts.events.LastHero.Registered", player));
	}

	public static boolean is_reg(L2Player player)
	{
		return players_list.contains(player.getRef());
	}

	public static boolean checkPlayer(L2Player player, boolean first)
	{
		if(first && !_isRegistrationActive)
		{
			player.sendMessage(new CustomMessage("scripts.events.Late", player));
			return false;
		}
		else if(first && players_list.contains(player.getRef()))
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.Cancelled", player));
			return false;
		}
		else if(first && (player.isInEvent() != 0 || player.isEventReg()))
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.OtherEvent", player).addString(player.getEventName(player.isInEvent())));
			return false;
		}
		else if(player.getLevel() < _minLevel || player.getLevel() > _maxLevel)
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledLevel", player));
			return false;
		}
		else if(player.isMounted())
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.Cancelled", player));
			return false;
		}
		else if(player.getDuel() != null)
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledDuel", player));
			return false;
		}
		else if(player.getTeam() != 0)
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player));
			return false;
		}
		else if(player.getOlympiadGame() != null || player.isInZoneOlympiad() || first && Olympiad.isRegistered(player))
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOlympiad", player));
			return false;
		}
		else if(player.isInParty() && player.getParty().isInDimensionalRift())
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledOtherEvent", player));
			return false;
		}
		else if(player.isTeleporting())
		{
			player.sendMessage(new CustomMessage("scripts.events.LastHero.CancelledTeleport", player));
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
		else if(player.isSubClassActive())
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
		paralyzePlayers();
		changeStyle();

		executeTask("events.lastHero.LastHero", "teleportPlayersToColiseum", new Object[0], 4000);
		executeTask("events.lastHero.LastHero", "healPlayers", new Object[0], 6000);
		executeTask("events.lastHero.LastHero", "go", new Object[0], 64000);

		sayToAll("scripts.events.LastHero.AnnounceFinalCountdown", null);
	}

	public static void go()
	{
		if(ConfigValue.LastHeroCancel)
			removeBuff();
		_status = 2;
		upParalyzePlayers();
		checkLive();
		clearArena();
		sayToAll("scripts.events.LastHero.AnnounceFight", null);
		_endTask = executeTask("events.lastHero.LastHero", "endBattle", new Object[0], ConfigValue.LastHeroEndTime * 1000);
	}

	public static void removeBuff()
	{
		for(HardReference<L2Player> ref : players_list)
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

	public static void endBattle()
	{
		_status = 0;
		if(live_list.size() == 1)
		{
			for(HardReference<L2Player> ref : live_list)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				String[] repl = { player.getName() };
				sayToAll("scripts.events.LastHero.AnnounceWiner", repl);
				addItem(player, ConfigValue.LastHeroBonusID, Math.round(ConfigValue.LastHeroFinalRate ? player.getLevel() * ConfigValue.LastHeroFinalBonus : 1 * ConfigValue.LastHeroFinalBonus));
				if(ConfigValue.LastHeroSetHero)
					setHero(player);
				if(player.getAttainment() != null)
					player.getAttainment().event_battle_end(1, true);
				break;
			}
		}
		else if(ConfigValue.LastHeroWinMaxDamager)
		{
			int max_damage = 0;
			L2Player max_damager = null;
			for(HardReference<L2Player> ref : live_list)
			{
				L2Player player = ref.get();
				if(player == null)
					continue;
				if(max_damage < player.getDamageMy())
				{
					max_damage = player.getDamageMy();
					max_damager = ref.get();
				}
			}
			if(max_damager != null)
			{
				String[] repl = { max_damager.getName() };
				sayToAll("scripts.events.LastHero.AnnounceWiner", repl);
				addItem(max_damager, ConfigValue.LastHeroBonusID, Math.round(ConfigValue.LastHeroFinalRate ? max_damager.getLevel() * ConfigValue.LastHeroFinalBonus : 1 * ConfigValue.LastHeroFinalBonus));
				if(ConfigValue.LastHeroSetHero)
					setHero(max_damager);
				if(max_damager.getAttainment() != null)
					max_damager.getAttainment().event_battle_end(1, true);

			}
		}
		sayToAll("scripts.events.LastHero.AnnounceEnd", null);
		executeTask("events.lastHero.LastHero", "end", new Object[0], 30000);
		_isRegistrationActive = false;
		if(_endTask != null)
		{
			_endTask.cancel(false);
			_endTask = null;
		}
	}

	public static void setHero(L2Player player)
	{
		// Статус меняется только на текущую логон сессию
		if(!player.isHero())
		{
			if(true)
			{
				player.setHero(true, 1);
				player.updatePledgeClass();
				Hero.addSkills(player);
			}
			else
			{
				long expire = System.currentTimeMillis() + (1000 * 60 * 60 * 1);
				player.setVar("HeroEvent", String.valueOf(expire), expire);
				player.setHero(true, 1);
				player.updatePledgeClass();
				Hero.addSkills(player);
				player._heroTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.UnsetHero(player, 1), 1000 * 60 * 60 * 1);
			}
		
			player.sendPacket(new SkillList(player));
			if(player.isHero())
			{
				player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
				Announcements.getInstance().announceToAll(player.getName() + " has become a hero.");
			}
			player.broadcastUserInfo(true);
		}
	}

	public static void end()
	{
		ressurectPlayers();
		healPlayers();
		executeTask("events.lastHero.LastHero", "teleportPlayersToSavedCoords", new Object[0], 3000);
		executeTask("events.lastHero.LastHero", "autoContinue", new Object[0], 10000);
	}

	public void autoContinue()
	{
		if(reflection != null)
		{
			reflection.startCollapseTimer(1);
			reflection = null;
		}
		if(_autoContinue > 0)
		{
			if(_autoContinue >= ConfigValue.LastHeroMinLevelForCategory.length)
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

	public static void teleportPlayersToColiseum()
	{
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			unRide(player);
			unSummonPet(player, true);
			Location pos = Rnd.coordsRandomize(149505, 46719, -3417, 0, 0, 500);
			player.setIsInEvent((byte) 2);
			if(ConfigValue.LastHeroNoParty)
				player.can_create_party = false;
			lockItems(player);

			player.setVar("backCoords", player.getLoc().toXYZString());
			player.setReflection(reflection);
			player.teleToLocation(pos.x, pos.y, pos.z);
		}
	}

	public static void changeStyle()
	{
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			switch(ConfigValue.LastHeroArmor)
			{
				case 1:
					player.startAbnormalEffect(AbnormalVisualEffect.br_ave_vesper1);
					break;
				case 2:
					player.startAbnormalEffect(AbnormalVisualEffect.br_ave_vesper2);
					break;
				case 3:
					player.startAbnormalEffect(AbnormalVisualEffect.br_ave_vesper3);
					break;
				case 4:
					player.startAbnormalEffect(AbnormalVisualEffect.ave_unk22);
					break;
			}
		}
	}

	public static void returnStyle(L2Player player)
	{
		if(player == null)
			return;

		switch(ConfigValue.LastHeroArmor)
		{
			case 1:
				player.stopAbnormalEffect(AbnormalVisualEffect.br_ave_vesper1);
				break;
			case 2:
				player.stopAbnormalEffect(AbnormalVisualEffect.br_ave_vesper2);
				break;
			case 3:
				player.stopAbnormalEffect(AbnormalVisualEffect.br_ave_vesper3);
				break;
			case 4:
				player.stopAbnormalEffect(AbnormalVisualEffect.ave_unk22);
				break;
		}
	}

	private static void lockItems(L2Player player)
	{
		if(ConfigValue.LastHeroOlympiadItems || ConfigValue.LastHeroForbiddenItems.length > 0)
		{
			TIntArrayList items = new TIntArrayList();

			if(ConfigValue.LastHeroForbiddenItems.length > 0)
			{
				for(int i = 0; i < ConfigValue.LastHeroForbiddenItems.length; i++)
				{
					items.add(ConfigValue.LastHeroForbiddenItems[i]);
				}
			}

			if(ConfigValue.LastHeroOlympiadItems)
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
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			try
			{
				player.setTeam(0, false);
				player._poly_id = 0 << 24;
				player.setIsInEvent((byte) 0);
				player.setEventReg(false);
				if(ConfigValue.LastHeroNoParty)
					player.can_create_party = true;
				returnStyle(player);
				if(ConfigValue.LastHeroOlympiadItems || ConfigValue.LastHeroForbiddenItems.length > 0)
					player.getInventory().unlock();
				player.setIsInvul(false);
				player.getEffectList().stopAllEffects();
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.getEffectList().stopAllEffects();
				}
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
				player.teleToLocation(147800, -55320, -2728, 0);
				player.unsetVar("backCoords");
				player.unsetVar("reflection");
				e.printStackTrace();
			}
		}
	}

	public static void paralyzePlayers()
	{
		if(ConfigValue.LastHeroCancel)
			removeBuff();
		L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player == null)
				continue;
			player.getEffectList().stopEffect(L2Skill.SKILL_MYSTIC_IMMUNITY);
			player.getEffectList().stopEffect(1540);
			player.getEffectList().stopEffect(1418);
			player.getEffectList().stopEffect(396);
			player.getEffectList().stopEffect(914);
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
			player.setInvisible(true);
			player.sendUserInfo(true);
			if(player.getCurrentRegion() != null)
				for(L2WorldRegion neighbor : player.getCurrentRegion().getNeighbors())
					neighbor.removePlayerFromOtherPlayers(player);
			player.clearHateList(true);
		}
	}

	public static void upParalyzePlayers()
	{
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player != null)
			{
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

				if(player.getParty() != null)
					player.getParty().oustPartyMember(player);
				player.setInvisible(false);
				player.broadcastUserInfo(true);
				if(player.getPet() != null)
					player.getPet().broadcastPetInfo();
				player.broadcastRelationChanged();
			}
		}
	}

	public static void ressurectPlayers()
	{
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player != null && player.isDead())
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
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player != null)
			{
				player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
				player.setCurrentCp(player.getMaxCp());
			}
		}
	}

	public static void cleanPlayers()
	{
		for(HardReference<L2Player> ref : players_list)
		{
			L2Player player = ref.get();
			if(player != null && !checkPlayer(player, false))
				removePlayer(player);
		}
	}

	public static void checkLive()
	{
		GCSArray<HardReference<L2Player>> new_live_list = new GCSArray<HardReference<L2Player>>();

		for(HardReference<L2Player> ref : live_list)
		{
			L2Player player = ref.get();
			if(player != null)
				new_live_list.add(ref);
		}

		live_list = new_live_list;

		for(HardReference<L2Player> ref : live_list)
		{
			L2Player player = ref.get();
			if(player != null)
			{
				if(player.isInZone(_zone) && !player.isDead() && player.isConnected() && !player.isLogoutStarted())
				{
					player.setTeam(2, false);
					if(ConfigValue.LastHeroPolymorphId > 0)
						player.setPolyInfo(1, ConfigValue.LastHeroPolymorphId);
				}
				else
					loosePlayer(player);
			}
		}

		if(live_list.size() <= 1)
			endBattle();
	}

	public static void clearArena()
	{
		for(L2Object obj : _zone.getObjects())
			if(obj != null)
			{
				L2Player player = obj.getPlayer();
				if(player != null && !live_list.contains(player.getRef()) && player.getReflection() == reflection)
					player.teleToLocation(147451, 46728, -3410);
			}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(_status > 1 && self != null && self.isPlayer() && self.getTeam() > 0 && live_list.contains(self.getRef()))
		{
			L2Player player = (L2Player) self;
			loosePlayer(player);
			checkLive();
			if(killer != null && killer.isPlayer() && killer.getPlayer().expertiseIndex - player.expertiseIndex > 2 && !killer.getPlayer().getIP().equals(player.getIP()))
				addItem((L2Player) killer, ConfigValue.LastHeroBonusID, Math.round(ConfigValue.LastHeroRate ? player.getLevel() * ConfigValue.LastHeroBonusCount : 1 * ConfigValue.LastHeroBonusCount));
		}
	}

	public static Location OnEscape(L2Player player)
	{
		if(_status > 1 && player != null && live_list.contains(player.getRef()))
		{
			removePlayer(player);
			checkLive();
		}
		return null;
	}

	public static void OnPlayerExit(L2Player player)
	{
		if(player != null && live_list.contains(player.getRef()))
		{
			// Вышел или вылетел во время регистрации
			if(_status == 0 && _isRegistrationActive && live_list.contains(player.getRef()))
			{
				removePlayer(player);
				return;
			}

			// Вышел или вылетел во время телепортации
			if(_status == 1 && live_list.contains(player.getRef()))
			{
				removePlayer(player);

				try
				{
					player.setIsInvul(false);
					player.getEffectList().stopAllEffects();
					if(player.getPet() != null)
					{
						L2Summon summon = player.getPet();
						summon.getEffectList().stopAllEffects();
					}
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
					player.teleToLocation(147800, -55320, -2728, 0);
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					// e.printStackTrace();
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
			if(_status > 0 && player != null && !live_list.contains(player.getRef()) && player.getReflection() == reflection)
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(147451, 46728, -3410)), 3000);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(object == null)
				return;
			L2Player player = object.getPlayer();
			if(_status > 1 && player != null && player.getTeam() > 0 && live_list.contains(player.getRef()) && player.getReflection() == reflection)
			{
				double angle = Util.convertHeadingToDegree(object.getHeading()); // угол
				// в
				// градусах
				double radian = Math.toRadians(angle - 90); // угол в радианах
				int x = (int) (object.getX() + 50 * Math.sin(radian));
				int y = (int) (object.getY() - 50 * Math.cos(radian));
				int z = object.getZ();
				ThreadPoolManager.getInstance().schedule(new TeleportTask((L2Character) object, new Location(x, y, z)), 3000);
			}
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

	private static void loosePlayer(L2Player player)
	{
		if(player != null)
		{
			live_list.remove(player.getRef());
			player.setTeam(0, false);
			player._poly_id = 0 << 24;
			player.setIsInEvent((byte) 0);
			player.setEventReg(false);
			if(ConfigValue.LastHeroNoParty)
				player.can_create_party = true;
			returnStyle(player);
			player.sendMessage(new CustomMessage("scripts.events.LastHero.YouLose", player));
			try
			{
				player.setIsInvul(false);
				player.getEffectList().stopAllEffects();
				if(player.getPet() != null)
				{
					L2Summon summon = player.getPet();
					summon.getEffectList().stopAllEffects();
				}
				String back = player.getVar("backCoords");
				if(back != null)
				{
					player.unsetVar("backCoords");
					player.unsetVar("reflection");
					player.teleToLocation(new Location(back), 0);
				}
				removePlayer(player);
				if(player.isDead())
				{
					player.restoreExp();
					player.setCurrentCp(player.getMaxCp());
					player.setCurrentHp(player.getMaxHp(), true);
					player.setCurrentMp(player.getMaxMp());
					player.broadcastPacket(new Revive(player));
				}
			}
			catch(Exception e)
			{
				player.teleToLocation(147800, -55320, -2728, 0);
				player.unsetVar("backCoords");
				player.unsetVar("reflection");
				// e.printStackTrace();
			}
		}
	}

	private static void removePlayer(L2Player player)
	{
		if(player != null)
		{
			live_list.remove(player.getRef());
			players_list.remove(player.getRef());
			player.setTeam(0, false);
			player._poly_id = 0 << 24;
			player.broadcastUserInfo(true);
			player.setIsInEvent((byte) 0);
			player.getEffectList().stopAllEffects();
			if(player.getPet() != null)
			{
				L2Summon summon = player.getPet();
				summon.getEffectList().stopAllEffects();
			}
			player.setEventReg(false);
			if(ConfigValue.LastHeroNoParty)
				player.can_create_party = true;
			returnStyle(player);

			if(ConfigValue.LastHeroOlympiadItems || ConfigValue.LastHeroForbiddenItems.length > 0)
				player.getInventory().unlock();
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
		player.sendMessage("Вы сняли регистрацию с Last Hero.");
	}

	private static synchronized void buffPlayer(L2Player player)
	{
		if(player != null && ConfigValue.LastHeroBuff)
		{
			int[][] buff;
			if(player.getClassId().isMage())
				buff = ConfigValue.LastHeroMagicBuff;
			else
				buff = ConfigValue.LastHeroPhisicBuff;
			for(int[] sk : buff)
			{
				L2Skill skill = SkillTable.getInstance().getInfo(sk[0], sk[1]);
				int buffTime = skill.isMusic() ? ConfigValue.LastHeroDanceAndSongTime : ConfigValue.LastHeroBuffTime;
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