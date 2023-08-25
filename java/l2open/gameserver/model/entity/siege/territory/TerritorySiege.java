package l2open.gameserver.model.entity.siege.territory;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.database.*;
import l2open.gameserver.GameStart;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.SiegeSpawn;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExDominionWarEnd;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.NpcTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.sql.*;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

public class TerritorySiege
{
	private static final Logger _log = Logger.getLogger(TerritorySiege.class.getName());

	private static boolean _registrationOver = false;
	private static boolean _isInProgress = false;

	private static Calendar _siegeRegEndDate;
	private static Calendar _siegeEndDate;
	private static Calendar _siegeDate;

	private static ScheduledFuture<?> _territoryStartTask;
	private static ScheduledFuture<?> _territoryFameTask;

	private static final int _controlTowerLosePenalty = 150000;
	private static final int _defenderRespawnDelay = 30000;

	private static int SiegeDayOfWeek = ConfigValue.TerritoryWarSiegeDayOfWeek;
	private static int SiegeHourOfDay = ConfigValue.TerritoryWarSiegeHourOfDay;

	private static final FastMap<Integer, Integer> _defenderRespawnPenalty = new FastMap<Integer, Integer>().setShared(true);
	private static final FastList<Integer> _disguisedPlayers = new FastList<Integer>();
	private static final FastMap<Integer, Fortress> _fortress = new FastMap<Integer, Fortress>().setShared(true);
	private static final FastMap<Integer, Integer> _players = new FastMap<Integer, Integer>().setShared(true);
	private static final FastMap<Integer, Castle> _castles = new FastMap<Integer, Castle>().setShared(true);
	private static final FastMap<SiegeClan, Integer> _clans = new FastMap<SiegeClan, Integer>().setShared(true);
	private static final FastMap<Integer, Location> _wardsLoc = new FastMap<Integer, Location>().setShared(true);
	private static final GArray<L2TerritoryFlagInstance> _flags = new GArray<L2TerritoryFlagInstance>();

	private static final FastMap<Integer, Quest> _forSakeQuest = new FastMap<Integer, Quest>().setShared(true);

	public static final int[] TERRITORY_SKILLS = new int[] { 0, 848, 849, 850, 851, 852, 853, 854, 855, 856 };

	public static boolean[][] protectObjectAtacked = 
	{
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false},
		{false, false, false, false, false}
	};

	public static Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	private static void playersUpdate(boolean end)
	{
		L2Clan clan;

		for(Entry<SiegeClan, Integer> entry : _clans.entrySet())
		{
			SiegeClan siegeClan = entry.getKey();
			clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if(clan == null)
			{
				_log.warning("Siege clan is null!!! id: " + siegeClan.getClanId());
				continue;
			}
			if(end)
			{
				clan.setTerritorySiege(-1);
				refreshTerritorySkills();
			}
			else
			{
				clan.setTerritorySiege(entry.getValue());
				deleteTerritorySkills();
			}
			for(L2Player member : clan.getOnlineMembers(0))
			{
				member.broadcastUserInfo(true);
				member.broadcastRelationChanged();
				if(!end)
					clearReward(member.getObjectId());
				questUpdate(member, !end);
				addReward(member, STATIC_BADGES, 5, entry.getValue());
			}
		}
		for(Entry<Integer, Integer> entry : _players.entrySet())
		{
			L2Player player = L2ObjectsStorage.getPlayer(entry.getKey());
			if(player != null)
			{
				if(end)
					player.setTerritorySiege(-1);
				else
				{
					player.setTerritorySiege(entry.getValue());
					clearReward(player.getObjectId());
				}
				player.broadcastUserInfo(true);
				player.broadcastRelationChanged();
				questUpdate(player, !end);
				addReward(player, STATIC_BADGES, 5, entry.getValue());
			}
		}
		if(end)
		{
			for(Quest q : L2Player.getBreakQuests())
				Quest.deleteAllQuestInDb(q.getName());
			for(Quest q : L2Player.getAllClassQuests().values())
				Quest.deleteAllQuestInDb(q.getName());
		}
	}

	public static void questUpdate(L2Player player, boolean start)
	{
		if(start)
		{
			QuestState sakeQuestState = _forSakeQuest.get(player.getTerritorySiege()).newQuestState(player, Quest.CREATED);
			sakeQuestState.setState(Quest.STARTED);
			sakeQuestState.setCond(1);
		}
		else
		{
			for(Quest q : player.getBreakQuests())
			{
				QuestState questState = player.getQuestState(q.getName());
				if(questState != null)
					questState.abortQuest();
			}

			// сбрасываем классовые квесты...
			for(Quest q : player.getAllClassQuests().values())
			{
				QuestState qs = player.getQuestState(q.getName());
				if(qs != null)
					qs.exitCurrentQuest(true);
			}
		}
	}

	public static SiegeClan getSiegeClan(L2Clan clan)
	{
		if(clan == null)
			return null;
		for(SiegeClan siegeClan : _clans.keySet())
			if(siegeClan.getClan() == clan)
				return siegeClan;
		return null;
	}

	private static void removeHeadquarters()
	{
		for(SiegeClan sc : getClans().keySet())
			if(sc != null)
				sc.removeHeadquarter();
	}

	public static FastMap<Integer, Location> getWardsLoc()
	{
		return _wardsLoc;
	}

	public static void setWardLoc(Integer id, Location loc)
	{
		if(_wardsLoc.get(id) != null)
			_wardsLoc.get(id).set(loc);
		else
			_wardsLoc.put(id, loc);
	}

	public static void guardSpawn(int id, boolean period)
	{
		if(period)
			for(L2Spawn spawn: TerritorySiegeDatabase.getGuardsSpawnList().get(id))
				spawn.doSpawn(true).getSpawn().stopRespawn();
		else
			for(L2Spawn spawn: TerritorySiegeDatabase.getGuardsSpawnList().get(id))
				if(spawn != null)
				{
					spawn.stopRespawn();
					if(spawn.getLastSpawn() != null)
						spawn.getLastSpawn().deleteMe();
				}
	}

	public static void catapultDestroyed(int id)
	{
		Castle castle = CastleManager.getInstance().getCastleByIndex(id);
		if(castle != null)
		{
			for(L2DoorInstance door : castle.getDoors())
				door.openMe();
			guardSpawn(id, false);
		}
	}

	public static void spawnSiegeGuard()
	{
		for(int i = 1;i<10;i++)
		{
			guardSpawn(i, true);
			catapultSpawn(i, true);
		}
	}

	public static void refreshTerritorySkills()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
		{
			L2Clan owner = c.getOwner();
			if(owner == null)
				continue;

			// Удаляем лишние
			L2Skill[] clanSkills = owner.getAllSkills();
			for(L2Skill cs : clanSkills)
			{
				if(!isTerritoriSkill(cs))
					continue;
				if(!c.getTerritorySkills().contains(cs))
					owner.removeSkill(cs);
			}

			// Добавляем недостающие
			clanSkills = owner.getAllSkills();
			boolean exist;
			for(L2Skill cs : c.getTerritorySkills())
			{
				exist = false;
				for(L2Skill clanSkill : clanSkills)
				{
					if(!isTerritoriSkill(clanSkill))
						continue;
					if(clanSkill.getId() == cs.getId())
					{
						exist = true;
						break;
					}
				}
				if(!exist)
					owner.addNewSkill(cs, false);
			}
		}
	}

	public static void removePlayer(L2Player player)
	{
		if(player != null)
		{
			_players.remove(player.getObjectId());
			TerritorySiegeDatabase.changeRegistration(player.getObjectId(), -1, 0, true);
		}
	}

	public static void spawnNpcInTown()
	{
		for(Castle castle : _castles.values())
			if(castle.getOwnerId() != 0)
				for(L2Spawn spawn : TerritorySiegeDatabase.getNpcsSpawnList().get(castle.getId()))
					if(spawn != null)
						spawn.init();
	}

	public static boolean isInProgress()
	{
		return _isInProgress;
	}

	public static int getClansForTerritory(int territoryId)
	{
		int counter = 0;
		for(Entry<SiegeClan, Integer> entry : _clans.entrySet())
			if(entry.getValue() == territoryId)
				counter++;
		return counter;
	}

	public static int getPlayersForTerritory(int territoryId)
	{
		int counter = 0;
		for(Entry<Integer, Integer> entry : _players.entrySet())
			if(entry.getValue() == territoryId)
				counter++;
		return counter;
	}

	public static L2TerritoryFlagInstance getNpcFlagByItemId(int itemId)
	{
		for(L2TerritoryFlagInstance flag : _flags)
			if(flag.getItemId() == itemId)
				return flag;
		return null;
	}

	public static void startSiege()
	{
		if(!_isInProgress)
		{
			_playersRewards.clear();

			for(int unitId : _castles.keySet())
			{
				L2Zone zone = getZone(unitId);
				if(zone != null)
					zone.setActive(true);
				getResidenseZone(unitId).setActive(true);
			}
			for(int unitId : _fortress.keySet())
			{
				L2Zone zone = getZone(unitId);
				if(zone != null)
					zone.setActive(true);
			}
			// Кланы, владеющие замком, автоматически регистрируются за свои земли.
			for(Castle castle : _castles.values())
				if(castle.getOwner() != null)
					getClans().put(new SiegeClan(castle.getOwner().getClanId(), null), castle.getId());
			_isInProgress = true;
			playersUpdate(false);
			clearSiegeFields();
			for(Castle castle : _castles.values())
				castle.spawnDoor();
			for(Fortress fortress : _fortress.values())
				fortress.spawnDoor();
			spawnFlags(-1);
			spawnSiegeGuard();
			// Таймер окончания осады
			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, getSiegeLength());
			ThreadPoolManager.getInstance().schedule(new TerritorySiegeEndTask(), 1000);
			_territoryFameTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TerritorySiegeFameTask(), 5 * 60 * 1000, 5 * 60 * 1000);
			announceToPlayer(Msg.TERRITORY_WAR_HAS_BEGUN, false);
		}
	}

	public static void removeClan(SiegeClan sc)
	{
		if(sc != null)
		{
			_clans.remove(sc);
			TerritorySiegeDatabase.changeRegistration(sc.getClanId(), -1, 1, true);
		}
	}

	public static L2Zone getZone(int unitId)
	{
		return ZoneManager.getInstance().getZoneByIndex(ZoneType.Siege, unitId, false);
	}

	private static void unSpawnFlags()
	{
		for(L2TerritoryFlagInstance flag : _flags)
			if(flag != null)
			{
				L2ItemInstance item = flag.getItem();
				if(item != null)
				{
					if(item.getOwnerId() > 0)
					{
						L2Player owner = L2ObjectsStorage.getPlayer(item.getOwnerId());
						if(owner != null)
							item = owner.getInventory().dropItem(item, item.getCount(), true);
					}
					item.deleteMe();
				}
				flag.deleteMe();
			}
		_flags.clear();
		_wardsLoc.clear();
	}

	public static void setRegistrationOver(boolean value)
	{
		_registrationOver = value;
	}

	public static void registerPlayer(int id, L2Player player)
	{
		if(player != null)
		{
			_players.put(player.getObjectId(), id);
			TerritorySiegeDatabase.changeRegistration(player.getObjectId(), id, 0, false);
		}
	}

	private static void removeSiegeSummons()
	{
		for(L2Player player : getPlayersInZone())
			for(int id : Siege.SIEGE_SUMMONS)
				if(player.getPet() != null && id == player.getPet().getNpcId())
					player.getPet().unSummon();
	}

	public static L2Zone getResidenseZone(int id)
	{
		return ZoneManager.getInstance().getZoneByIndex(ZoneType.siege_residense, id, false);
	}

	public static void registerClan(int id, SiegeClan sc)
	{
		if(sc != null)
		{
			_clans.put(sc, id);
			TerritorySiegeDatabase.changeRegistration(sc.getClanId(), id, 1, false);
		}
	}

	public static boolean checkIfInZone(L2Object object)
	{
		if(!isInProgress() || object.getReflectionId() != 0)
			return false;
		for(int unitId : _castles.keySet())
			for(L2Object obj : getZone(unitId).getObjects())
				if(obj == object)
					return true;
		for(int unitId : _fortress.keySet())
			for(L2Object obj : getZone(unitId).getObjects())
				if(obj == object)
					return true;
		return false;
	}

	public static void clearOutpost()
	{
		for(int unitId : _castles.keySet())
			for(L2Object obj : getZone(unitId).getObjects())
				if(obj.isNpc())
					if(((L2NpcInstance)obj).getNpcId() == 35062 || ((L2NpcInstance)obj).getNpcId() == 36590)
						obj.deleteMe();
		for(int unitId : _fortress.keySet())
			for(L2Object obj : getZone(unitId).getObjects())
				if(obj.isNpc())
					if(((L2NpcInstance)obj).getNpcId() == 35062 || ((L2NpcInstance)obj).getNpcId() == 36590)
						obj.deleteMe();
	}

	public static Calendar getSiegeEndDate()
	{
		return _siegeEndDate;
	}

	public static Calendar getSiegeRegistrationEndDate()
	{
		return _siegeRegEndDate;
	}

	private static void saveSiege()
	{
		setNextSiegeDate(); // Выставляем дату следующей осады
		ServerVariables.set("TerritorySiegeDate", _siegeDate.getTimeInMillis()); // Сохраняем дату следующей осады
		startAutoTask(); // Запускаем таск для следующей осады
	}

	public static void endSiege()
	{
		for(int unitId : _castles.keySet())
		{
			L2Zone zone = getZone(unitId);
			if(zone != null)
				zone.setActive(false);
			getResidenseZone(unitId).setActive(false);
		}

		for(int unitId : _fortress.keySet())
		{
			L2Zone zone = getZone(unitId);
			if(zone != null)
				zone.setActive(false);
		}

		if(_isInProgress)
		{
			announceToPlayer(Msg.TERRITORY_WAR_HAS_ENDED, false);

			// Награда участников в виде exp/sp
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player != null && player.getTerritorySiege() > -1)
				{
					player.getEffectList().stopEffect(L2Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
					player.addExpAndSp(270000, 27000, true, false);
					player.broadcastCharInfo();
					player.sendPacket(ExDominionWarEnd.STATIC);
				}

			// Следующее сообщение должно выводиться через 10 мин после окончания осады.
			// Но поскольку территориальный чат у нас работат только во время осады, выводим сразу 
			announceToPlayer(Msg.THE_TERRITORY_WAR_CHANNEL_AND_FUNCTIONS_WILL_NOW_BE_DEACTIVATED, true);

			removeHeadquarters();

			clearSiegeFields();

			removeSiegeSummons();

			playersUpdate(true);

			saveSiege();

			TerritorySiegeDatabase.clearSiegeMembers();
			getPlayers().clear();
			getClans().clear();

			unSpawnFlags();

			unspawnSiegeGuard();

			clearOutpost();

			for(Castle castle : _castles.values())
				castle.saveFlags();

			for(Castle castle : _castles.values())
				castle.spawnDoor();

			for(Fortress fortress : _fortress.values())
				fortress.spawnDoor();

			if(_territoryStartTask != null)
			{
				_territoryStartTask.cancel(false);
				_territoryStartTask = null;
			}
			if(_territoryFameTask != null)
			{
				_territoryFameTask.cancel(true);
				_territoryFameTask = null;
			}

			setRegistrationOver(false);

			_isInProgress = false;

			insert();
			_defenderRespawnPenalty.clear();
			_disguisedPlayers.clear();
			_fortress.clear();
			_players.clear();
			_castles.clear();
			_clans.clear();
			_wardsLoc.clear();
			_flags.clear();
		}
	}

	/**
	 * Чат доступен за 10 мин до старта и еще 10 мин по окончании ТВ
	 */
	public static boolean isTerritoryChatAccessible()
	{
		return getSiegeDate().getTimeInMillis() - 20 * 60 * 1000 > System.currentTimeMillis() && getSiegeEndDate().getTimeInMillis() + 10 * 60 * 1000 > System.currentTimeMillis();
	}

	private static void correctSiegeDateTime()
	{
		boolean corrected = false;
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			corrected = true;
			setNextSiegeDate();
		}
		if(_siegeDate.get(Calendar.DAY_OF_WEEK) != SiegeDayOfWeek)
		{
			corrected = true;
			_siegeDate.set(Calendar.DAY_OF_WEEK, SiegeDayOfWeek);
		}
		if(_siegeDate.get(Calendar.HOUR_OF_DAY) != SiegeHourOfDay)
		{
			corrected = true;
			_siegeDate.set(Calendar.HOUR_OF_DAY, SiegeHourOfDay);
		}
		_siegeDate.set(Calendar.MINUTE, 0);
		if(corrected)
			ServerVariables.set("TerritorySiegeDate", _siegeDate.getTimeInMillis());
	}

	public static GArray<L2Player> getPlayersInZone()
	{
		GArray<L2Player> players = new GArray<L2Player>();
		if(!isInProgress())
			return players;
		for(int unitId : _castles.keySet())
			players.addAll(getZone(unitId).getInsidePlayers());
		for(int unitId : _fortress.keySet())
			players.addAll(getZone(unitId).getInsidePlayers());
		return players;
	}

	private static void startAutoTask()
	{
		if(_territoryStartTask != null)
			return;
		correctSiegeDateTime();

		_log.info("Territory Siege: " + Util.datetimeFormatter.format(_siegeDate.getTime()));

		_siegeRegEndDate = Calendar.getInstance();
		_siegeRegEndDate.setTimeInMillis(_siegeDate.getTimeInMillis());
		_siegeRegEndDate.add(Calendar.DAY_OF_MONTH, -1);

		_territoryStartTask = ThreadPoolManager.getInstance().schedule(new TerritorySiegeStartTask(), 1000);
	}

	public static boolean isRegistrationOver()
	{
		return _registrationOver;
	}

	public static int getSiegeLength()
	{
		return ConfigValue.WarLength;
	}

	public static FastMap<Integer, Integer> getPlayers()
	{
		return _players;
	}

	private static void setNextSiegeDate()
	{
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()-(86400*ConfigValue.TerritorySiegeDay))
			_siegeDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis()-1000);
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			_siegeDate.add(Calendar.DAY_OF_MONTH, ConfigValue.TerritorySiegeDay);
			if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
				setNextSiegeDate();
		}
	}

	public static void killedCT(int unitId)
	{
		_defenderRespawnPenalty.put(unitId, _defenderRespawnPenalty.get(unitId) + _controlTowerLosePenalty);
	}

	/**
	 * Рассылка бродкастом сообщений всем или только участникам ТВ.<br>
	 * Нельза рассылать только участникам если территориальная война не стартовала. 
	 */
	public static void announceToPlayer(L2GameServerPacket message, boolean participantsOnly)
	{
		GArray<L2Player> players = new GArray<L2Player>();
		if(participantsOnly)
		{
			// Нет смысла перебирать всех игроков, если терриориальная война не началась
			if(!isInProgress())
				return;
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player != null && player.getTerritorySiege() > -1)
					players.add(player);
		}
		else
		{
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(player != null)
					players.add(player);
		}
		for(L2Player player : players)
			player.sendPacket(message);
	}

	public static void load()
	{
		_siegeDate = Calendar.getInstance();
		_siegeDate.setTimeInMillis(ServerVariables.getLong("TerritorySiegeDate", 0));
		_castles.putAll(CastleManager.getInstance().getCastles());
		_fortress.putAll(FortressManager.getInstance().getFortresses());
		TerritorySiegeDatabase.loadSiegeMembers();
		TerritorySiegeDatabase.loadSiegeFlags();
		TerritorySiegeDatabase.loadNpcsSpawnList();
		TerritorySiegeDatabase.loadSiegeGuardsSpawnList();
		TerritorySiegeDatabase.loadSiegeCatapultsSpawnList();
		spawnNpcInTown();
		for(int unitId : _castles.keySet())
		{
			L2Zone zone = getZone(unitId);
			if(zone != null)
				zone.setActive(false);
		}
		for(int unitId : _fortress.keySet())
		{
			L2Zone zone = getZone(unitId);
			if(zone != null)
				zone.setActive(false);
		}
		for(int unitId : _castles.keySet())
			_defenderRespawnPenalty.put(unitId, 0);
		startAutoTask();
		select();
	}

	public static void deleteTerritorySkills()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
		{
			L2Clan clan = c.getOwner();
			if(clan != null)
			{
				L2Skill[] clanSkills = clan.getAllSkills();
				for(L2Skill cs : clanSkills)
					if(isTerritoriSkill(cs))
						clan.removeSkill(cs);
			}
		}
	}

	public static int getDefenderRespawnTotal(int unitId)
	{
		return _defenderRespawnDelay + _defenderRespawnPenalty.get(unitId);
	}

	public static void spawnFlags(int onlyOne)
	{
		for(Castle castle : _castles.values())
		{
			if(castle.getOwner() == null) // Не спауним флаг если замок не имеет владельца
				continue;
				
			GArray<SiegeSpawn> points = TerritorySiegeDatabase.getSiegeFlags().get(castle.getId());
			int i = 0;
			for(int flagCastleId : castle.getFlags())
			{
				if(onlyOne == -1 || flagCastleId == onlyOne)
				{
					SiegeSpawn info = TerritorySiegeDatabase.getSiegeFlags().get(flagCastleId).get(0);
					Location loc = points.get(i).getLoc();

					L2TerritoryFlagInstance flag = new L2TerritoryFlagInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(info.getNpcId()));
					flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp(), true);
					flag.setXYZInvisible(loc.correctGeoZ());
					flag.setSpawnedLoc(flag.getLoc());
					flag.setHeading(loc.h);
					flag.setItemId(info.getValue());
					flag.setBaseTerritoryId(flagCastleId);
					flag.setCurrentTerritoryId(castle.getId());
					flag.spawnMe();
					_flags.add(flag);
					setWardLoc(flagCastleId, flag.getLoc());
				}
				i++;
			}
		}
	}

	public static int getTerritoryForClan(int clanId)
	{
		if(clanId == 0)
			return 0;
		L2Clan clan = ClanTable.getInstance().getClan(clanId);
		if(clan == null)
			return 0;
		if(clan.getHasCastle() > 0)
			return clan.getHasCastle();
		for(Entry<SiegeClan, Integer> entry : TerritorySiege.getClans().entrySet())
			if(entry.getKey().getClanId() == clanId)
				return entry.getValue();
		return 0;
	}

	public static void removeFlag(L2TerritoryFlagInstance flag)
	{
		_flags.remove(flag);
	}

	public static void unspawnSiegeGuard()
	{
		for(int i = 1;i<10;i++)
		{
			guardSpawn(i, false);
			catapultSpawn(i, false);
		}
	}

	public static L2NpcInstance getHeadquarter(L2Clan clan)
	{
		if(clan != null)
		{
			SiegeClan sc = getSiegeClan(clan);
			if(sc != null)
				return sc.getHeadquarter();
		}
		return null;
	}

	public static FastMap<SiegeClan, Integer> getClans()
	{
		return _clans;
	}

	public static boolean isTerritoriSkill(L2Skill skill)
	{
		for(int id : TERRITORY_SKILLS)
			if(id == skill.getId())
				return true;
		return false;
	}

	private static void catapultSpawn(int id, boolean period)
	{
		if(period)
			for(L2Spawn spawn: TerritorySiegeDatabase.getCatapultsSpawnList().get(id))
				spawn.doSpawn(true).getSpawn().stopRespawn();
		else
			for(L2Spawn spawn: TerritorySiegeDatabase.getCatapultsSpawnList().get(id))
				if(spawn != null)
				{
					spawn.stopRespawn();
					if(spawn.getLastSpawn() != null)
						spawn.getLastSpawn().deleteMe();
				}
	}

	private static void clearSiegeFields()
	{
		for(L2Player player : getPlayersInZone())
			if(player != null && !player.isGM())
				player.teleToClosestTown();
	}

	public static int getTerritoryForPlayer(int playerId)
	{
		Integer terrId = _players.get(new Integer(playerId));
		return terrId == null ? -1 : terrId;
	}

	public static class TerritorySiegeFameTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!isInProgress())
				return;
			int bonus = 0;
			for(L2Player player : getPlayersInZone())
				if(player != null && !player.isInOfflineMode() && player.getTerritorySiege() > -1 && player.getReflection().getId() == 0)
				{
					if(player.isInZone(ZoneType.Fortress))
						bonus = 31;
					else if(player.isInZone(ZoneType.Castle))
						bonus = 125;
					if(!ConfigValue.NotSetFameDeadPlayer || !player.isDead())
						player.setFame((int)(player.getFame() + bonus * ConfigValue.RateFameReward * player.getRateFame()), "TerritoryWars");
					if(player.isSitting())
						continue;
					double badgesCount = 0.5;
					if(player.isInCombat())
						badgesCount += 0.5;
					L2Object target = player.getTarget();
					if(target != null && target.isPlayable())
					{
						badgesCount += 0.5;
						L2Player ptarget = target.getPlayer();
						if(ptarget != null && player.getTerritorySiege() != ptarget.getTerritorySiege() && ptarget.getTerritorySiege() > -1)
							badgesCount += 0.5;
					}

					addReward(player, ONLINE_REWARD, (int)badgesCount, player.getTerritorySiege());
				}
		}
	}
	
	public static void addDisguisedPlayer(int playerObjId)
	{
		_disguisedPlayers.add(playerObjId);
	}
	
	public static boolean isDisguised(int playerObjId)
	{
		return _disguisedPlayers.contains(playerObjId);
	}

	public static void setForSakeQuest(Quest forSakeQuest, int terrId)
	{
		_forSakeQuest.put(terrId, forSakeQuest);
	}

	public static Quest getForSakeQuest(int terrId)
	{
		return _forSakeQuest.get(terrId);
	}

	//========================================================================================================================================================================
	//                                                                   Rewards
	//========================================================================================================================================================================
	private static IntObjectMap<int[][]> _playersRewards = new CHashIntObjectMap<int[][]>();

	public static final int KILL_REWARD = 0;
	public static final int ONLINE_REWARD = 1;
	public static final int STATIC_BADGES = 2;
	//
	public static final int REWARD_MAX = 3;

	public static void setReward(int objectId, int type, int v, int terryId)
	{
		int val[][] = _playersRewards.get(objectId);
		if(val == null)
			_playersRewards.put(objectId, val = new int[9][REWARD_MAX]);

		val[terryId-1][type] = v;
	}

	public static void addReward(L2Player player, int type, int v, int terryId)
	{
		if(terryId > 0)
		{
			int val[][] = _playersRewards.get(player.getObjectId());
			if(val == null)
				_playersRewards.put(player.getObjectId(), val = new int[9][REWARD_MAX]);

			val[terryId-1][type] += v;
		}
	}

	public static void clearReward(int objectId)
	{
		if(_playersRewards.containsKey(objectId))
		{
			_playersRewards.remove(objectId);
			delete(objectId);
		}
	}

	public static Collection<IntObjectMap.Entry<int[][]>> getRewards()
	{
		return _playersRewards.entrySet();
	}

	public static int[] calculateReward(L2Player player, int terrId)
	{
		int rewards[][] = _playersRewards.get(player.getObjectId());
		if(rewards == null)
			return null;

		int[] out = new int[3];
		// статичные (старт, стоп, квесты, прочее)
		out[0] += rewards[terrId-1][STATIC_BADGES];
		// если онлайн ревард больше 14(70 мин в зоне) это 7 макс
		out[0] += rewards[terrId-1][ONLINE_REWARD] >= 14 ? 7 : rewards[terrId-1][ONLINE_REWARD] / 2;

		// насчитаем за убийство
		if(rewards[terrId-1][KILL_REWARD] < 50)
			out[0] += rewards[terrId-1][KILL_REWARD] * 0.1;
		else if(rewards[terrId-1][KILL_REWARD] < 120)
			out[0] += (5 + (rewards[terrId-1][KILL_REWARD] - 50) / 14);
		else
			out[0] += 10;

		//TODO [VISTALL] неверно, фейм дается и ниже, нету выдачи адены
		if(out[0] > 90)
		{
			out[0] = 90; // badges
			out[1] = 0; //TODO [VISTALL] adena count
			out[2] = 450; // fame
		}

		return out;
	}

	private static final String INSERT_SQL_QUERY = "INSERT INTO dominion_rewards (id, object_id, static_badges, online_reward, kill_reward) VALUES";
	private static final String SELECT_SQL_QUERY = "SELECT * FROM dominion_rewards";
	private static final String DELETE_SQL_QUERY = "DELETE FROM dominion_rewards WHERE object_id=?";
	private static final String DELETE_SQL_QUERY2 = "DELETE FROM dominion_rewards";

	public static void select()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			rset = statement.executeQuery();
			while(rset.next())
			{
				int id = rset.getInt("id");
				int playerObjectId = rset.getInt("object_id");
				int staticBadges = rset.getInt("static_badges");
				int onlineReward = rset.getInt("online_reward");
				int killReward = rset.getInt("kill_reward");

				setReward(playerObjectId, STATIC_BADGES, staticBadges, id);
				setReward(playerObjectId, KILL_REWARD, killReward, id);
				setReward(playerObjectId, ONLINE_REWARD, onlineReward, id);
			}
		}
		catch(Exception e)
		{
			_log.warning("TerritorySiege:select(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	public static void insert()
	{
		ThreadConnection con = null;
		FiltredStatement pstatement = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY2);
			statement.executeUpdate();

			Collection<IntObjectMap.Entry<int[][]>> rewards = getRewards();
			pstatement = con.createStatement();

			for(IntObjectMap.Entry<int[][]> entry : rewards)
			{
				for(int i=0;i<9;i++)
				{
					StringBuilder sb = new StringBuilder("(");
					sb.append(i+1).append(",");
					sb.append(entry.getKey()).append(",");
					sb.append(entry.getValue()[i][STATIC_BADGES]).append(",");
					sb.append(entry.getValue()[i][ONLINE_REWARD]).append(",");
					sb.append(entry.getValue()[i][KILL_REWARD]).append(")");
					pstatement.executeUpdate(INSERT_SQL_QUERY+sb.toString());
				}
			}
		}
		catch(final Exception e)
		{
			_log.warning("TerritorySiege:insert(): " + e);
		}
		finally
		{
			DatabaseUtils.closeStatement(statement);
			DatabaseUtils.closeDatabaseCS(con, pstatement);
		}
	}

	public static void delete(int objectId)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, objectId);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.warning("TerritorySiege:delete(): " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
}