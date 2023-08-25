package l2open.gameserver.model.entity.siege.clanhall;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ClanHallManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2SiegeHeadquarterInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.ExclusiveTask;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class WildBeastFarmSiege extends CHSiege
{
	private static final Logger _log = Logger.getLogger(WildBeastFarmSiege.class.getName());
	private static WildBeastFarmSiege _instance;
	private boolean _registrationPeriod = false;
	private int _clanCounter = 0;
	private Map<Integer, clanPlayersInfo> _clansInfo = new HashMap<Integer, clanPlayersInfo>();
	private L2Zone zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.battle_zone, 21750001, false);
	public ClanHall clanhall = ClanHallManager.getInstance().getClanHall(63);
	private clanPlayersInfo _ownerClanInfo = new clanPlayersInfo();
	private boolean _finalStage = false;
	private ScheduledFuture<?> _midTimer;
	private ScheduledFuture<?> _startTimer;
	private static Map<Integer, L2Clan> clansUnreg = new HashMap<Integer, L2Clan>();

	private final ExclusiveTask _startSiegeTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			if(getIsInProgress())
			{
				cancel();
				return;
			}
			Calendar siegeStart = Calendar.getInstance();
			siegeStart.setTimeInMillis(getSiegeDate().getTimeInMillis());
			long registerTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis() - 3600000;
			long siegeTimeRemaining = siegeStart.getTimeInMillis() - System.currentTimeMillis();
			long remaining = registerTimeRemaining;
			if(registerTimeRemaining <= 0 && !isRegistrationPeriod())
			{
				if(clanhall.getOwner() != null)
					_ownerClanInfo._clanName = clanhall.getOwner().getName();
				else
					_ownerClanInfo._clanName = "";
				setRegistrationPeriod(true);
				anonce("Внимание!!! Начался период регистрации на осаду Холл Клана, Ферма Диких Зверей.", 2);
				remaining = siegeTimeRemaining;
			}
			if (siegeTimeRemaining <= 0)
			{
				startSiege();
				cancel();
				return;
			}
			schedule(remaining);
		}
	};

	private final ExclusiveTask _endSiegeTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			if(!getIsInProgress())
			{
				cancel();
				return;
			}
			long timeRemaining = _siegeEndDate.getTimeInMillis() - System.currentTimeMillis();
			if(timeRemaining <= 0)
			{
				endSiege(true);
				cancel();
				return;
			}
			schedule(timeRemaining);
		}
	};

	private final ExclusiveTask _mobControlTask = new ExclusiveTask(true)
	{
		protected void onElapsed()
		{
			int mobCount = 0;
			for(clanPlayersInfo cl : _clansInfo.values())
				if(cl._mob.isDead())
				{
					L2Clan clan = ClanTable.getInstance().getClanByName(cl._clanName);
					clansUnreg.put(clan.getClanId(), clan);
				}
				else
				{
					mobCount++;
				}
			for(L2Clan cl : clansUnreg.values())
			{
				unRegisterClan(cl);
			}
			teleportPlayers();
			if(mobCount < 2)
				if(_finalStage)
				{
					_siegeEndDate = Calendar.getInstance();
					_endSiegeTask.cancel();
					_endSiegeTask.schedule(5000);
				}
			else
			{
				_midTimer.cancel(false);
				ThreadPoolManager.getInstance().schedule(new midSiegeStep(), 5000);
			}
			else
				schedule(3000);
		}
	};

	public static WildBeastFarmSiege getInstance()
	{
		if(_instance == null)
			_instance = new WildBeastFarmSiege();
		return _instance;
	}

	private WildBeastFarmSiege()
	{
		long siegeDate = restoreSiegeDate(63);
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTimeInMillis(siegeDate);
		setSiegeDate(tmpDate);
		setNewSiegeDate(siegeDate, 63, ConfigValue.BeastFarmSiegeHour, ConfigValue.BeastFarmSiegeDay);
		_startSiegeTask.schedule(1000);
		_log.info("Siege of Wild Beasts Farm : " + tmpDate.getTime());
	}

	public void startSiege()
	{
		setRegistrationPeriod(false);
		if(_clansInfo.size() == 0)
		{
			endSiege(false);
			return;
		}
		if(_clansInfo.size() == 1 && clanhall.getOwner() == null)
		{
			endSiege(false);
			return;
		}
		if(_clansInfo.size() == 1 && clanhall.getOwner() != null)
		{
			L2Clan clan = null;
			for(clanPlayersInfo a : _clansInfo.values())
				clan = ClanTable.getInstance().getClanByName(a._clanName);
			setIsInProgress(true);
			zone.setActive(true);

			startSecondStep(clan);

			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(12, 30);
			_endSiegeTask.schedule(1000);
			return;
		}
		setIsInProgress(true);
		zone.setActive(true);
		spawnFlags();
		gateControl(1);
		anonce("Займите места у своих осадных штабов.", 1);
		_startTimer = ThreadPoolManager.getInstance().schedule(new startFirstStep(), 300000);
		_midTimer = ThreadPoolManager.getInstance().schedule(new midSiegeStep(), 1500000);
		_siegeEndDate = Calendar.getInstance();
		_siegeEndDate.add(12, 60);
		_endSiegeTask.schedule(1000);
	}

	//checking
	public void startSecondStep(L2Clan winner)
	{
		ArrayList<String> winPlayers = getInstance().getRegisteredPlayers(winner);
		unSpawnAll();
		_clansInfo.clear();
		clanPlayersInfo regPlayers = new clanPlayersInfo();
		regPlayers._clanName = winner.getName();
		regPlayers._players = winPlayers;
		_clansInfo.put(winner.getClanId(), regPlayers);
		_clansInfo.put(clanhall.getOwner().getClanId(), _ownerClanInfo);
		spawnFlags();
		gateControl(1);
		_finalStage = true;
		anonce("Займите места у своих осадных штабов.", 1);
		_startTimer = ThreadPoolManager.getInstance().schedule(new startFirstStep(), 300000);
	}

	public void endSiege(boolean par)
	{
		_mobControlTask.cancel();
		_finalStage = false;
		if(par)
		{
			L2Clan winner = checkHaveWinner();
			if(winner != null)
			{
				clanhall.changeOwner(winner);
				anonce("Внимание!!! Холл Клана, Ферма Диких Зверей был завоеван кланом " + winner.getName(), 2);
			}
			else
			{
				anonce("Внимание!!! Холл Клана, Ферма Диких Зверей не получил нового владельца", 2);
			}
		}
		setIsInProgress(false);
		unSpawnAll();
		_clansInfo.clear();
		_clanCounter = 0;
		teleportPlayers();
		zone.setActive(false);
		setNewSiegeDate(getSiegeDate().getTimeInMillis(), 63, ConfigValue.BeastFarmSiegeHour, ConfigValue.BeastFarmSiegeDay);
		_startSiegeTask.schedule(1000);
	}

	public void unSpawnAll()
	{
		for(String clanName : getRegisteredClans())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			L2MonsterInstance mob = getQuestMob(clan);
			L2SiegeHeadquarterInstance flag = getSiegeFlag(clan);
			if(mob != null)
				mob.deleteMe();
			if(flag != null)
				flag.deleteMe();
		}
	}

	public void gateControl(int val)
	{
		if (val == 1)
		{
			DoorTable.getInstance().getDoor(21150003).openMe();
			DoorTable.getInstance().getDoor(21150004).openMe();
			DoorTable.getInstance().getDoor(21150001).closeMe();
			DoorTable.getInstance().getDoor(21150002).closeMe();
		}
		else
		{
			if(val != 2)
				return;
			DoorTable.getInstance().getDoor(21150001).closeMe();
			DoorTable.getInstance().getDoor(21150002).closeMe();
			DoorTable.getInstance().getDoor(21150003).closeMe();
			DoorTable.getInstance().getDoor(21150004).closeMe();
		}
	}

	public void teleportPlayers()
	{
		for (L2Player cha : zone.getInsidePlayers())
		{
			if(cha.isPlayer())
			{
				L2Clan clan = cha.getClan();

				if(!isPlayerRegister(clan, cha.getName()))
					cha.teleToLocation(53468, -94092, -1634);
			}
		}
	}

	public L2Clan checkHaveWinner()
	{
		L2Clan res = null;
		int questMobCount = 0;
		for(String clanName : getRegisteredClans())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if(getQuestMob(clan) != null)
			{
				res = clan;
				questMobCount++;
			}
		}
		if(questMobCount > 1)
			return null;
		return res;
	}

	public void spawnFlags()
	{
		int flagCounter = 1;
		for(String clanName : getRegisteredClans())
		{
			L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
			L2NpcTemplate template;
			if(clan == clanhall.getOwner())
				template = NpcTable.getTemplate(35422);
			else
				template = NpcTable.getTemplate(35422 + flagCounter);
			L2SiegeHeadquarterInstance flag = new L2SiegeHeadquarterInstance(clan.getLeader().getPlayer(), IdFactory.getInstance().getNextId(), template);
			Location loc = new Location();

			flag.setTitle(clan.getName());
			flag.setHeading(100);
			flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());

			if(flagCounter == 1)
			{
				loc.set(59138, -92532, -1354);
				flag.spawnMe(loc);
			}
			else if(flagCounter == 2)
			{
				loc.set(56769, -92097, -1360);
				flag.spawnMe(loc);
			}
			else if(flagCounter == 3)
			{
				loc.set(57027, -93673, -1365);
				flag.spawnMe(loc);
			}
			else if(flagCounter == 4)
			{
				loc.set(58120, -91440, -1354);
				flag.spawnMe(loc);
			}
			else if(flagCounter == 5)
			{
				loc.set(58428, -93787, -1360);
				flag.spawnMe(loc);
			}
			clanPlayersInfo regPlayers = _clansInfo.get(clan.getClanId());
			regPlayers._flag = flag;
			flagCounter++;
		}
	}

	public void setRegistrationPeriod(boolean par)
	{
		_registrationPeriod = par;
	}

	public boolean isRegistrationPeriod()
	{
		return _registrationPeriod;
	}

	public boolean isPlayerRegister(L2Clan playerClan, String playerName)
	{
		if(playerClan == null)
			return false;
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		return (regPlayers != null && regPlayers._players.contains(playerName));
	}

	public boolean isClanOnSiege(L2Clan playerClan)
	{
		if(playerClan == clanhall.getOwner())
			return true;
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		return regPlayers != null;
	}

	public synchronized int registerClanOnSiege(L2Player player, L2Clan playerClan)
	{
		if(_clanCounter == 5)
		{
			return 2;
		}
		L2ItemInstance item = player.getInventory().getItemByItemId(8293);
		if(item != null)
		{
			player.getInventory().destroyItemByItemId(8293, 1, true);
			player.sendPacket(SystemMessage.removeItems(8293, 1));
			QuestState st = player.getQuestState("_655_AGrandPlanforTamingWildBeasts");
			if(st != null)
				st.exitCurrentQuest(true);

			_clanCounter += 1;
			clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
			if(regPlayers == null)
			{
				regPlayers = new clanPlayersInfo();
				regPlayers._clanName = playerClan.getName();
				_clansInfo.put(playerClan.getClanId(), regPlayers);
			}
		}
		else
		{
			return 1;
		}
		return 0;
	}

	public boolean unRegisterClan(L2Clan playerClan)
	{
		if(_clansInfo.remove(playerClan.getClanId()) != null)
		{
			_clanCounter -= 1;
			return true;
		}
		return false;
	}

	public ArrayList<String> getRegisteredClans()
	{
		ArrayList<String> clans = new ArrayList<String>();
		for(clanPlayersInfo a : _clansInfo.values())
		{
			clans.add(a._clanName);
		}
		return clans;
	}

	public ArrayList<String> getRegisteredPlayers(L2Clan playerClan)
	{
		if(playerClan == clanhall.getOwner())
			return _ownerClanInfo._players;
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if(regPlayers != null)
			return regPlayers._players;
		return null;
	}

	public L2SiegeHeadquarterInstance getSiegeFlag(L2Clan playerClan)
	{
		clanPlayersInfo clanInfo = _clansInfo.get(playerClan.getClanId());
		if(clanInfo != null)
			return clanInfo._flag;
		return null;
	}

	public L2MonsterInstance getQuestMob(L2Clan clan)
	{
		clanPlayersInfo clanInfo = _clansInfo.get(clan.getClanId());
		if(clanInfo != null)
			return clanInfo._mob;
		return null;
	}

	public int getPlayersCount(String playerClan)
	{
		for(clanPlayersInfo a : _clansInfo.values())
			if(a._clanName.equals(playerClan))
				return a._players.size();
		return 0;
	}

	public void addPlayer(L2Clan playerClan, String playerName)
	{
		if(playerClan == clanhall.getOwner() && _ownerClanInfo._players.size() < 18 && !_ownerClanInfo._players.contains(playerName))
		{
			_ownerClanInfo._players.add(playerName);
			return;
		}
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if(regPlayers == null || regPlayers._players.size() >= 18 || regPlayers._players.contains(playerName))
			return;
		regPlayers._players.add(playerName);
	}

	public void removePlayer(L2Clan playerClan, String playerName)
	{
		if(playerClan == clanhall.getOwner() && _ownerClanInfo._players.contains(playerName))
		{
			_ownerClanInfo._players.remove(playerName);
			return;
		}
		clanPlayersInfo regPlayers = _clansInfo.get(playerClan.getClanId());
		if(regPlayers == null || !regPlayers._players.contains(playerName))
			return;
		regPlayers._players.remove(playerName);
	}

	public void anonce(String text, int type)
	{
		NpcSay cs = new NpcSay(L2ObjectsStorage.getByNpcId(35627), Say2C.NPC_SHOUT, text);
		if(type == 1)
		{
			for(String clanName : getRegisteredClans())
			{
				L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				for(String playerName : getRegisteredPlayers(clan))
				{
					L2Player cha = L2World.getPlayer(playerName);
					if(cha != null)
						cha.sendPacket(cs);
				}
			}
		}
		else
		{
			GArray<L2Player> res = L2World.getAroundPlayers(L2ObjectsStorage.getByNpcId(35627), 15000, 5000);
			if(res != null)
				for(L2Player player : res)
				{
					player.sendPacket(cs);
				}
		}
	}

	private class clanPlayersInfo
	{
		public String _clanName;
		public L2SiegeHeadquarterInstance _flag;
		public L2MonsterInstance _mob;
		public ArrayList<String> _players;

		private clanPlayersInfo()
		{
			_flag = null;
			_mob = null;
			_players = new ArrayList<String>();
		}
	}

	private class startFirstStep extends l2open.common.RunnableImpl
	{
		private startFirstStep()
		{
		}

		public void runImpl()
		{
			teleportPlayers();
			gateControl(2);
			int mobCounter = 1;
			for(String clanName : getRegisteredClans())
			{
				L2Clan clan = ClanTable.getInstance().getClanByName(clanName);
				L2NpcTemplate template = NpcTable.getTemplate(35617 + Rnd.get(1,5));
				L2MonsterInstance questMob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), template);
				Location loc = new Location();

				questMob.setTitle(clan.getName());
				questMob.setHeading(100);
				questMob.setCurrentHpMp(questMob.getMaxHp(), questMob.getMaxMp());
				if(mobCounter == 1)
				{
					loc.set(58838, -92232, -1354);
					questMob.spawnMe(loc);
				}
				else if(mobCounter == 2)
				{
					loc.set(57069, -91797, -1360);
					questMob.spawnMe(loc);
				}
				else if(mobCounter == 3)
				{
					loc.set(57327, -93373, -1365);
					questMob.spawnMe(loc);
				}
				else if(mobCounter == 4)
				{
					loc.set(57820, -91740, -1354);
					questMob.spawnMe(loc);
				}
				else if(mobCounter == 5)
				{
					loc.set(58728, -93487, -1360);
					questMob.spawnMe(loc);
				}
				clanPlayersInfo regPlayers = _clansInfo.get(clan.getClanId());
				regPlayers._mob = questMob;
				mobCounter++;
			}
			_mobControlTask.schedule(3000);
			anonce("Бой начался. Убейте НПЦ противника", 1);
		}
	}

	private class midSiegeStep extends l2open.common.RunnableImpl
	{
		private midSiegeStep()
		{
		}

		public void runImpl()
		{
			_mobControlTask.cancel();
			L2Clan winner = checkHaveWinner();
			if(winner != null)
			{
				if(clanhall.getOwner() == null)
				{
					clanhall.changeOwner(winner);

					anonce("Внимание!!! Холл Клана, Ферма Диких Зверей был завоеван кланом " + winner.getName(), 2);
					endSiege(false);
				}
				else
				{
					startSecondStep(winner);
				}
			}
			else
				endSiege(true);
		}
	}
}
