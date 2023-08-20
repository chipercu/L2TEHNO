package l2open.gameserver.model.entity.siege;

import javolution.util.FastMap;
import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.SiegeGuardManager;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.model.entity.siege.clanhall.ClanHallSiege;
import l2open.gameserver.model.entity.siege.fortress.FortressSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.MapRegion;
import l2open.util.GArray;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

public abstract class Siege
{
	private int _defenderRespawnDelay = 30000;
	private int _siegeClanMinLevel = 5;
	private int _siegeLength = 120;
	private int _controlTowerLosePenalty = 150000;

	protected FastMap<SiegeClanType, FastMap<Integer, SiegeClan>> _siegeClans = new FastMap<SiegeClanType, FastMap<Integer, SiegeClan>>().setShared(true);

	protected Residence _siegeUnit;
	protected SiegeDatabase _database;
	protected SiegeGuardManager _siegeGuardManager;

	protected boolean _isInProgress = false;
	protected boolean _isMidVictory = false;
	protected boolean _isRegistrationOver = false;

	protected int _ownerBeforeStart;
	public int _defenderRespawnPenalty;

	protected Calendar _siegeDate;
	protected Calendar _siegeEndDate;
	protected Calendar _siegeRegistrationEndDate;

	protected ScheduledFuture<?> _siegeStartTask;
	protected ScheduledFuture<?> _fameTask;

	public Siege(Residence siegeUnit)
	{
		_siegeUnit = siegeUnit;
		_siegeDate = Calendar.getInstance();
	}

	public L2Zone getZone()
	{
		return ZoneManager.getInstance().getZoneByIndex(ZoneType.Siege, getSiegeUnit().getId(), false);
	}

	public L2Zone getResidenseZone()
	{
		return ZoneManager.getInstance().getZoneByIndex(ZoneType.siege_residense, getSiegeUnit().getId(), false);
	}

	/**
	 * When siege starts<BR><BR>
	 */
	public abstract void startSiege();

	/**
	 * When control of castle changed during siege<BR><BR>
	 */
	public abstract void midVictory();

	/** Display list of registered clans */
	public abstract void listRegisterClan(L2Player player);

	public abstract void endSiege();

	public abstract boolean Engrave(L2Clan clan, int objId);

	public abstract void startAutoTask(boolean isServerStarted);

	protected abstract void setNextSiegeDate();

	protected abstract void correctSiegeDateTime();

	protected abstract void saveSiege();
	
	public abstract void announceStartSiege(long time);

	public Residence getSiegeUnit()
	{
		return _siegeUnit;
	}

	public SiegeGuardManager getSiegeGuardManager()
	{
		return _siegeGuardManager;
	}

	public SiegeDatabase getDatabase()
	{
		return _database;
	}

	/** Return true if object is inside the zone */
	public boolean checkIfInZone(int x, int y, boolean onlyActive)
	{
		if(onlyActive && !isInProgress())
			return false;
		if(getSiegeUnit() != null && getSiegeUnit().checkIfInZone(x, y))
			return true;
		L2Zone zone = getZone();
		return zone != null && zone.checkIfInZone(x, y);
	}

	public void ClearSiegeOutpost()
	{
		for(L2Object ob : getZone().getObjects())
			if(ob.isNpc())
				if(((L2NpcInstance)ob).getNpcId() == 35062 || ((L2NpcInstance)ob).getNpcId() == 36590)
					ob.deleteMe();
	}

	/**
	 * Announce to player.<BR><BR>
	 * @param message The String of the message to send to player
	 * @param inAreaOnly The boolean flag to show message to players in area only.
	 * @param participantsOnly TODO
	 */
	public void announceToPlayer(SystemMessage message, boolean inAreaOnly, boolean participantsOnly)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && (!inAreaOnly || player.isInZone(ZoneType.Siege)))
			{
				if(_siegeUnit.getType() == ResidenceType.Fortress && !isParticipant(player))
					continue;
				if(participantsOnly && !isParticipant(player))
					continue;
				player.sendPacket(message);
			}
	}

	public void updatePlayerSiegeStateFlags(boolean clear)
	{
		L2Clan clan;
		for(SiegeClan siegeClan : getAttackerClans().values())
		{
			clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if(clan != null)
				for(L2Player member : clan.getOnlineMembers(0))
					member.setSiegeState(clear ? 0 : 1);
		}
		for(SiegeClan siegeclan : getDefenderClans().values())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
			if(clan != null)
				for(L2Player member : clan.getOnlineMembers(0))
					member.setSiegeState(clear ? 0 : 2);
		}
		for(SiegeClan siegeClan : getAttackerClans().values())
		{
			clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
			if(clan != null)
				for(L2Player member : clan.getOnlineMembers(0))
				{
					member.sendPacket(new PledgeSkillList(clan));
					member.broadcastUserInfo(true);
					member.broadcastRelationChanged();
				}
		}
		for(SiegeClan siegeclan : getDefenderClans().values())
		{
			clan = ClanTable.getInstance().getClan(siegeclan.getClanId());
			if(clan != null)
				for(L2Player member : clan.getOnlineMembers(0))
				{
					member.sendPacket(new PledgeSkillList(clan));
					member.broadcastUserInfo(true);
					member.broadcastRelationChanged();
				}
		}
	}

	/** Return list of L2Player in the zone. */
	public GArray<L2Player> getPlayersInZone()
	{
		GArray<L2Player> players = new GArray<L2Player>();
		for(L2Object object : getZone().getObjects())
			if(object.isPlayer() && object.getReflection().getId() == 0)
				players.add((L2Player) object);
		return players;
	}

	/**
	 * Teleport players
	 */
	public void teleportPlayer(TeleportWhoType teleportWho, MapRegion.TeleportWhereType teleportWhere)
	{
		GArray<L2Player> players = new GArray<L2Player>();
		int ownerId = getSiegeUnit().getOwnerId();
		switch(teleportWho)
		{
			case Owner:
				if(ownerId > 0)
					for(L2Player player : getPlayersInZone())
						if(player.getClan() != null && player.getClan().getClanId() == ownerId)
							players.add(player);
				break;
			case Attacker:
				for(L2Player player : getPlayersInZone())
					if(player.getClan() != null && checkIsAttacker(player.getClan()))
					{
						players.add(player);
					}
				break;
			case Defender:
				for(L2Player player : getPlayersInZone())
					if(player.getClan() != null && player.getClan().getClanId() != ownerId && checkIsDefender(player.getClan()))
						players.add(player);
				break;
			case Spectator:
				for(L2Player player : getPlayersInZone())
					if(player.getClan() == null || !checkIsAttacker(player.getClan()) && !checkIsDefender(player.getClan()))
						players.add(player);
				break;
			default:
				players = getPlayersInZone();
		}
		for(L2Player player : players)
			if(player != null && !player.isGM())
			{
				if(player.getCastingSkill() != null && player.getCastingSkill().getId() == 246)
					player.abortCast(true);
				if(teleportWho == TeleportWhoType.Defender && teleportWhere == MapRegion.TeleportWhereType.Castle)
				{
					player.teleToLocation(getSiegeUnit().getZone().getSpawn(), 0);
					continue;
				}
				else if(player.getClan() == null || (player.getClan() != null && player.getClan().getClanId() != ownerId))
					player.teleToLocation(MapRegion.getTeleTo(player, teleportWhere), 0);
			}
	}

	/**
	 * Set siege date time<BR><BR>
	 * @param siegeDateTime The long of date time in millisecond
	 */
	public void setSiegeDateTime(long siegeDateTime)
	{
		_siegeDate.setTimeInMillis(siegeDateTime); // Set siege date
	}

	/**
	 * Return true if the player can register.<BR><BR>
	 * @param player The L2Player of the player trying to register
	 * @return true if the player can register.
	 */
	private boolean checkIfCanRegister(L2Player player)
	{
		if(player.getClan() == null || player.getClan().getLevel() < getSiegeClanMinLevel())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.ClanLevelToSmall", player).addNumber(getSiegeClanMinLevel()));
			return false;
		}

		if(player.getClan().getHasCastle() > 0 && getSiegeUnit().getType() == ResidenceType.Castle)
		{
			player.sendPacket(Msg.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
			return false;
		}

		if(player.getClan().getClanId() == getSiegeUnit().getOwnerId())
		{
			player.sendPacket(Msg.THE_CLAN_THAT_OWNS_THE_CASTLE_IS_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
			return false;
		}

		if(SiegeDatabase.checkIsRegistered(player.getClan(), getSiegeUnit().getId()))
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.AlreadyRegistered", player));
			return false;
		}

		if(isRegistrationOver())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.DeadlinePassed", player).addString(getSiegeUnit().getName()));
			return false;
		}

		if(isInProgress())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.NotTimeToCancel", player));
			return false;
		}

		if(getSiegeUnit().getType() == ResidenceType.Fortress)
		{
			// Нельзя регистрироваться на осаду фортов, если прошло менее двух часов после начала последней его осады
			if(getSiegeUnit().getLastSiegeDate() * 1000 + 2 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.DeadlinePassed", player).addString(getSiegeUnit().getName()));
				return false;
			}
			if(TerritorySiege.isInProgress())
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.DeadlinePassed", player).addString(getSiegeUnit().getName()));
				return false;
			}
			// Нельзя регистрироваться на осаду фортов за 2 часа до битв за земли
			if(TerritorySiege.getSiegeDate().getTimeInMillis() > System.currentTimeMillis() && TerritorySiege.getSiegeDate().getTimeInMillis() - System.currentTimeMillis() < 2 * 60 * 60 * 1000)
			{
				player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.DeadlinePassed", player).addString(getSiegeUnit().getName()));
				return false;
			}
			// Владельцам замка нельзя атаковать присягнувшие крепости.
			if(player.getClan().getHasCastle() > 0 && ((Fortress) getSiegeUnit()).getCastleId() == player.getClan().getHasCastle() && ((Fortress) getSiegeUnit()).getFortState() == 2)
			{
				player.sendPacket(Msg.SIEGE_REGISTRATION_IS_NOT_POSSIBLE_DUE_TO_A_CONTRACT_WITH_A_HIGHER_CASTLE);
				return false;
			}
		}

		return true;
	}

	/**
	 * Register clan as attacker<BR><BR>
	 * @param player The L2Player of the player trying to register
	 */
	public boolean registerAttacker(L2Player player)
	{
		return registerAttacker(player, false);
	}

	public boolean registerAttacker(L2Player player, boolean force)
	{
		if(player.getClan() == null)
			return false;
		int allyId = 0;
		if(getSiegeUnit().getOwnerId() != 0)
		{
			L2Clan castleClan = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());
			if(castleClan != null)
				allyId = castleClan.getAllyId();
			//При записи первого атакующего, записываем овнеров на защиту.
			if(getDefenderClans().get(getSiegeUnit().getOwnerId()) == null)
				if(!(getSiegeUnit() instanceof ClanHall))
					addSiegeClan(getSiegeUnit().getOwnerId(), SiegeClanType.OWNER);
		}
		if(allyId != 0)
			if(player.getClan().getAllyId() == allyId && !force)
			{
				player.sendPacket(Msg.YOU_CANNOT_REGISTER_ON_THE_ATTACKING_SIDE_BECAUSE_YOU_ARE_PART_OF_AN_ALLIANCE_WITH_THE_CLAN_THAT_OWNS_THE_CASTLE);
				return false;
			}

		if(force || checkIfCanRegister(player))
			_database.saveSiegeClan(player.getClan(), 1); // Save to database
		return true;
	}

	/**
	 * Register clan as defender<BR><BR>
	 * @param player The L2Player of the player trying to register
	 */
	public void registerDefender(L2Player player)
	{
		registerDefender(player, false);
	}

	public void registerDefender(L2Player player, boolean force)
	{
		if(getSiegeUnit().getOwnerId() <= 0)
			player.sendMessage(new CustomMessage("l2open.gameserver.model.entity.siege.Siege.OwnedByNPC", player).addString(getSiegeUnit().getName()));
		else if(force || checkIfCanRegister(player))
			_database.saveSiegeClan(player.getClan(), 2); // Save to database
	}

	public void clearSiegeClan(L2Clan clan, boolean force)
	{
		if(clan == null || !force && clan.getHasCastle() == getSiegeUnit().getId() || !SiegeDatabase.checkIsRegistered(clan, getSiegeUnit().getId()))
			return;
		_database.removeSiegeClan(clan.getClanId());
		for(FastMap<Integer, SiegeClan> siegeClans : _siegeClans.values())
			siegeClans.remove(clan.getClanId());
	}

	public FastMap<SiegeClanType, FastMap<Integer, SiegeClan>> getSiegeClanList()
	{
		return _siegeClans;
	}

	public void addSiegeClan(SiegeClan sc, SiegeClanType type)
	{
		if(sc == null)
			return;
		sc.setTypeId(type);
		_siegeClans.get(type.simple()).put(sc.getClanId(), sc);
	}

	public void addSiegeClan(int clanId, SiegeClanType type)
	{
		addSiegeClan(new SiegeClan(clanId, type), type);
	}

	public void removeSiegeClan(int clanId, SiegeClanType type)
	{
		_siegeClans.get(type.simple()).remove(clanId);
	}

	public void removeSiegeClan(SiegeClan sc, SiegeClanType type)
	{
		if(sc != null)
			removeSiegeClan(sc.getClanId(), type);
	}

	public SiegeClan getSiegeClan(int clanId, SiegeClanType type)
	{
		return _siegeClans.get(type.simple()).get(clanId);
	}

	public SiegeClan getSiegeClan(L2Clan clan, SiegeClanType type)
	{
		if(clan == null)
			return null;
		return getSiegeClan(clan.getClanId(), type);
	}

	public FastMap<Integer, SiegeClan> getSiegeClans(SiegeClanType type)
	{
		return _siegeClans.get(type.simple());
	}

	public boolean isParticipant(L2Player player)
	{
		L2Clan clan = player.getClan();
		return clan != null && (checkIsAttacker(clan) || checkIsDefender(clan));
	}

	public boolean checkIsAttacker(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.ATTACKER) != null;
	}

	public boolean checkIsDefender(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.DEFENDER) != null;
	}

	public boolean checkIsDefenderWaiting(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.DEFENDER_WAITING) != null;
	}

	public boolean checkIsDefenderRefused(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.DEFENDER_REFUSED) != null;
	}

	public boolean checkIsClanRegistered(L2Clan clan)
	{
		return checkIsAttacker(clan) || checkIsDefender(clan) || checkIsDefenderWaiting(clan) || checkIsDefenderRefused(clan);
	}

	public FastMap<Integer, SiegeClan> getAttackerClans()
	{
		return getSiegeClans(SiegeClanType.ATTACKER);
	}

	public FastMap<Integer, SiegeClan> getDefenderClans()
	{
		return getSiegeClans(SiegeClanType.DEFENDER);
	}

	public FastMap<Integer, SiegeClan> getDefenderRefusedClans()
	{
		return getSiegeClans(SiegeClanType.DEFENDER_REFUSED);
	}

	public FastMap<Integer, SiegeClan> getDefenderWaitingClans()
	{
		return getSiegeClans(SiegeClanType.DEFENDER_WAITING);
	}

	public SiegeClan getAttackerClan(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.ATTACKER);
	}

	public SiegeClan getDefenderClan(L2Clan clan)
	{
		return getSiegeClan(clan, SiegeClanType.DEFENDER);
	}

	/**
	 * Approve clan as defender for siege<BR><BR>
	 * @param clanId The int of player's clan id
	 */
	public void approveSiegeDefenderClan(int clanId)
	{
		if(clanId <= 0)
			return;
		_database.saveSiegeClan(ClanTable.getInstance().getClan(clanId), 0);
		_database.loadSiegeClan();
	}

	public void refuseSiegeDefenderClan(int clanId)
	{
		if(clanId <= 0)
			return;
		_database.saveSiegeClan(ClanTable.getInstance().getClan(clanId), 3);
		_database.loadSiegeClan();
	}

	protected void updateSiegeClans()
	{
		for(SiegeClan clan : getDefenderClans().values())
			if(clan != null && clan.getClan() != null)
			{
				clan.getClan().setSiege(this);
				clan.getClan().setDefender(true);
				clan.getClan().setAttacker(false);
			}
		for(SiegeClan clan : getAttackerClans().values())
			if(clan != null && clan.getClan() != null)
			{
				clan.getClan().setSiege(this);
				clan.getClan().setDefender(false);
				clan.getClan().setAttacker(true);
			}
	}

	protected void clearSiegeClans()
	{
		for(L2Clan clan : ClanTable.getInstance().getClans())
			if(clan.getSiege() == this)
			{
				clan.setSiege(null);
				clan.setDefender(false);
				clan.setAttacker(false);
			}
	}

	public int getDefenderRespawnTotal()
	{
		return _defenderRespawnDelay + _defenderRespawnPenalty;
	}

	public boolean isInProgress()
	{
		return _isInProgress;
	}

	public boolean isMidVictory()
	{
		return _isMidVictory;
	}

	public boolean isRegistrationOver()
	{
		return _isRegistrationOver;
	}

	public void setRegistrationOver(boolean value)
	{
		_isRegistrationOver = value;
	}

	public Calendar getSiegeDate()
	{
		return _siegeDate;
	}

	public Calendar getSiegeEndDate()
	{
		return _siegeEndDate;
	}

	public long getTimeRemaining()
	{
		return getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
	}

	public static final int[] SIEGE_SUMMONS = {
			1459,
			14768,
			14769,
			14770,
			14771,
			14772,
			14773,
			14774,
			14775,
			14776,
			14777,
			14778,
			14779,
			14780,
			14781,
			14782,
			14783,
			14784,
			14785,
			14786,
			14787,
			14788,
			14789,
			14790,
			14791,
			14792,
			14793,
			14794,
			14795,
			14796,
			14797,
			14798,
			14839 };

	protected void removeSiegeSummons()
	{
		for(L2Player player : getPlayersInZone())
			for(int id : SIEGE_SUMMONS)
				if(player.getPet() != null && id == player.getPet().getNpcId())
					player.getPet().unSummon();
	}

	/** Remove all Headquarters */
	protected void removeHeadquarters()
	{
		for(SiegeClan sc : getAttackerClans().values())
			if(sc != null)
				sc.removeHeadquarter();
		for(SiegeClan sc : getDefenderClans().values())
			if(sc != null)
				sc.removeHeadquarter();
	}

	public L2NpcInstance getHeadquarter(L2Clan clan)
	{
		if(clan != null)
		{
			SiegeClan sc = getSiegeClan(clan, SiegeClanType.ATTACKER);
			if(sc != null)
				return sc.getHeadquarter();
		}
		return null;
	}

	/**
	 * Control Tower was killed
	 * Add respawn penalty to defenders for each control tower lose
	 */
	public void killedCT()
	{
		_defenderRespawnPenalty += getControlTowerLosePenalty();
	}

	public void sendTrapStatus(L2Player player, boolean enter)
	{}

	public Calendar getSiegeRegistrationEndDate()
	{
		return _siegeRegistrationEndDate;
	}

	public int getSiegeClanMinLevel()
	{
		return _siegeClanMinLevel;
	}

	public void setSiegeClanMinLevel(int siegeClanMinLevel)
	{
		_siegeClanMinLevel = siegeClanMinLevel;
	}

	public int getSiegeLength()
	{
		return _siegeLength;
	}

	public void setSiegeLength(int siegeLength)
	{
		_siegeLength = siegeLength;
	}

	public int getControlTowerLosePenalty()
	{
		return _controlTowerLosePenalty;
	}

	public void setControlTowerLosePenalty(int controlTowerLosePenalty)
	{
		_controlTowerLosePenalty = controlTowerLosePenalty;
	}

	public void setDefenderRespawnDelay(int respawnDelay)
	{
		_defenderRespawnDelay = respawnDelay;
	}

	public class SiegeFameTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!isInProgress())
				return;

			int bonus = 0;
			if(Siege.this instanceof CastleSiege)
				bonus = 125;
			else if(Siege.this instanceof ClanHallSiege)
				bonus = 75;
			else if(Siege.this instanceof FortressSiege)
				bonus = 31;

			for(L2Player player : getPlayersInZone())
				if(player != null && !player.isInOfflineMode() && player.getClan() != null && player.getClan().getSiege() == Siege.this && player.getReflection().getId() == 0 && (!ConfigValue.NotSetFameDeadPlayer || !player.isDead()))
					player.setFame((int)(player.getFame() + bonus * ConfigValue.RateFameReward * player.getRateFame()), "Siege");
		}
	}
}