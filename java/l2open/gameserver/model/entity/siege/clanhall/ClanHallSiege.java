package l2open.gameserver.model.entity.siege.clanhall;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ClanHallSiegeManager;
import l2open.gameserver.instancemanager.SiegeGuardManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.ClanHall;
import l2open.gameserver.model.entity.siege.*;
import l2open.gameserver.model.instances.L2SiegeBossInstance;
import l2open.gameserver.serverpackets.SiegeInfo;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.NpcTable;
import l2open.util.GArray;

import java.util.Calendar;
import java.util.logging.Logger;

// TODO:
// SystemMessage.REGISTRATION_FOR_THE_CLAN_HALL_SIEGE_IS_CLOSED
public class ClanHallSiege extends Siege
{
	private GArray<L2SiegeBossInstance> _siegeBosses = new GArray<L2SiegeBossInstance>();
     protected static Logger _log = Logger.getLogger(ClanHallSiege.class.getName());

	public ClanHallSiege(ClanHall siegeUnit)
	{
		super(siegeUnit);
		_database = new ClanHallSiegeDatabase(this);
		_siegeGuardManager = new SiegeGuardManager(getSiegeUnit());
		_database.loadSiegeClan();
	}

	@Override
	public void startSiege()
	{
		if(!_isInProgress)
		{
			setRegistrationOver(true);

			_database.loadSiegeClan(); // Load siege clan from db

			int oldOwner = getSiegeUnit().getOwnerId();
			if(oldOwner > 0)
			{
				getSiegeUnit().changeOwner(null);
				addSiegeClan(oldOwner, SiegeClanType.ATTACKER);
			}

			if(getAttackerClans().isEmpty())
			{
				if(getSiegeUnit().getOwnerId() <= 0)
					announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addString(getSiegeUnit().getName()), false, false);
				else
					announceToPlayer(new SystemMessage(SystemMessage.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addString(getSiegeUnit().getName()), false, false);
				return;
			}

			getZone().setActive(true);
			//TODO: Включить активацию после описания residence зон кланхоллов
			//getResidenseZone().setActive(true);

			_isInProgress = true; // Flag so that same siege instance cannot be started again
			_isMidVictory = true; // Для того, чтобы атакующие могли атаковать друг друга

			updateSiegeClans();
			updatePlayerSiegeStateFlags(false);

			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town

			spawnSiegeBosses();
			getSiegeUnit().spawnDoor(); // Spawn door
			getSiegeGuardManager().spawnSiegeGuard(); // Spawn siege guard

			// Schedule a task to prepare auto siege end
			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, getSiegeLength());
			ThreadPoolManager.getInstance().schedule(new SiegeEndTask(this), 1000); // Prepare auto end task
			_fameTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SiegeFameTask(), 5 * 60 * 1000L, 5 * 60 * 1000L);

			announceToPlayer(Msg.THE_SIEGE_OF_THE_CLAN_HALL_HAS_BEGUN, false, false);
		}
	}

	@Override
	public void midVictory()
	{
		// Если осада закончилась
		if(!isInProgress() || getSiegeUnit().getOwnerId() <= 0)
			return;

		SiegeClan sc_newowner = getAttackerClan(getSiegeUnit().getOwner());
		removeSiegeClan(sc_newowner, SiegeClanType.ATTACKER);
		addSiegeClan(sc_newowner, SiegeClanType.OWNER);

		endSiege();
	}

	@Override
	public void endSiege()
	{
		getZone().setActive(false);

		// TODO: Включить деактивацию после описания residence зон кланхоллов
		// getResidenseZone().setActive(false);

		if(isInProgress())
		{
			announceToPlayer(Msg.THE_SIEGE_OF_THE_CLAN_HALL_IS_FINISHED, false, false);

			if(getSiegeUnit().getOwnerId() <= 0)
				announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addString(getSiegeUnit().getName()), false, false);
			else
			{
				L2Clan newOwner = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());
				// clanhall was taken over from scratch
				if(newOwner.getLevel() >= 5)
					newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_NEWLY_ACQUIRED_CONTESTED_CLAN_HALL_HAS_ADDED_S1_POINTS_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(500, true, "ClanHallSiege")));
			}

			// TODO забрать у проигравших 1000 репутации.
			// _player.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_CAPTURED_YOUR_OPPONENT_CONTESTED_CLAN_HALL_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENT_CLAN_REPUTATION_SCORE).addNumber(500));
			// ClanHallManager.getInstance().getClanHall(_id).getOwner().broadcastToOnlineMembers(new SystemMessage(SystemMessage.AN_OPPOSING_CLAN_HAS_CAPTURED_YOUR_CLAN_CONTESTED_CLAN_HALL_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE).addNumber(300));
			// ClanHallManager.getInstance().getClanHall(_id).getOwner().incReputation(-300, false, "CHSiege");
			// attacker.broadcastToOnlineMembers(new SystemMessage(SystemMessage.AFTER_LOSING_THE_CONTESTED_CLAN_HALL_300_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE));
			// attacker.incReputation(-300, false, "CHSiege");

			unspawnSiegeBosses();
			removeHeadquarters();
			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			removeSiegeSummons();
			_isInProgress = false; // Flag so that siege instance can be started
			updatePlayerSiegeStateFlags(true);
			saveSiege(); // Save clanhall specific data
			_database.clearSiegeClan(); // Clear siege clan from db
			getSiegeGuardManager().unspawnSiegeGuard(); // Remove all spawned siege guard
			getSiegeUnit().spawnDoor(); // Respawn door
			clearSiegeClans();

			if(_siegeStartTask != null)
			{
				_siegeStartTask.cancel(false);
				_siegeStartTask = null;
			}
			if(_fameTask != null)
			{
				_fameTask.cancel(true);
				_fameTask = null;
			}

			setRegistrationOver(false);
		}
	}

	@Override
	public boolean Engrave(L2Clan clan, int objId)
	{
		if(clan != null)
		{
			getSiegeUnit().changeOwner(clan);
			midVictory();
		}
		else
			endSiege();
		return true;
	}

	@Override
	public void addSiegeClan(SiegeClan sc, SiegeClanType type)
	{
		if(type == SiegeClanType.ATTACKER)
			super.addSiegeClan(sc, type);
	}

	@Override
	public void registerDefender(L2Player player, boolean force)
	{}

	/**
	 * Start the auto tasks<BR><BR>
	 */
	@Override
	public void startAutoTask(boolean isServerStarted)
	{
		if(_siegeStartTask != null)
			return;
		
		correctSiegeDateTime();

		_siegeRegistrationEndDate = Calendar.getInstance();
		_siegeRegistrationEndDate.setTimeInMillis(_siegeDate.getTimeInMillis());
		_siegeRegistrationEndDate.add(Calendar.DAY_OF_MONTH, -1);

		// Если сервер только что стартовал, осада начнется не ранее чем через час
		if(isServerStarted)
		{
			Calendar minDate = Calendar.getInstance();
			minDate.add(Calendar.HOUR_OF_DAY, 1);
			_siegeDate.setTimeInMillis(Math.max(minDate.getTimeInMillis(), _siegeDate.getTimeInMillis()));
			_database.saveSiegeDate();

			// Если был рестарт во время осады, даем зарегистрироваться еще раз
			if(_siegeDate.getTimeInMillis() <= minDate.getTimeInMillis())
			{
				setRegistrationOver(false);
				_siegeRegistrationEndDate.setTimeInMillis(_siegeDate.getTimeInMillis());
				_siegeRegistrationEndDate.add(Calendar.MINUTE, -10);
			}
		}

		_log.info("Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());

		_siegeStartTask = ThreadPoolManager.getInstance().schedule(new SiegeStartTask(this), 1000);
	}

	/** Set the date for the next siege. */
	@Override
	protected void setNextSiegeDate()
	{
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()-1209600)
			_siegeDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis()-1000);
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Set next siege date if siege has passed
			_siegeDate.add(Calendar.DAY_OF_MONTH, 14); // Schedule to happen in 14 days
			if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
				setNextSiegeDate(); // Re-run again if still in the pass
		}
	}

	@Override
	protected void correctSiegeDateTime()
	{
		boolean corrected = false;
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Since siege has past reschedule it to the next one (14 days)
			// This is usually caused by server being down
			corrected = true;
			setNextSiegeDate();
		}
		if(_siegeDate.get(Calendar.DAY_OF_WEEK) != getSiegeUnit().getSiegeDayOfWeek())
		{
			corrected = true;
			_siegeDate.set(Calendar.DAY_OF_WEEK, getSiegeUnit().getSiegeDayOfWeek());
		}
		if(_siegeDate.get(Calendar.HOUR_OF_DAY) != getSiegeUnit().getSiegeHourOfDay())
		{
			corrected = true;
			_siegeDate.set(Calendar.HOUR_OF_DAY, getSiegeUnit().getSiegeHourOfDay());
		}
		_siegeDate.set(Calendar.MINUTE, 0);
		if(corrected)
			_database.saveSiegeDate();
	}

	@Override
	protected void saveSiege()
	{
		// Выставляем дату следующей осады
		setNextSiegeDate();
		// Сохраняем дату следующей осады
		_database.saveSiegeDate();
		// Запускаем таск для следующей осады
		startAutoTask(false);
	}

	/** Display list of registered clans */
	@Override
	public void listRegisterClan(L2Player player)
	{
		player.sendPacket(new SiegeInfo(getSiegeUnit()));
	}

	/** Один из боссов убит */
	public void killedSiegeBoss(L2SiegeBossInstance boss)
	{
		if(boss.getNpcId() == 35408)
			Functions.npcSay(boss, "Has once more $$ln the defeat the shame.. But the tragedy had not ended...");
		else if(boss.getNpcId() == 35409)
			Functions.npcSay(boss, "Is this my boundary.. But does not have Gustave's permission, I can die in no way!");
		else if(boss.getNpcId() == 35410)
		{
			Functions.npcSay(boss, "Day.. Unexpectedly is defeated? But I certainly can again come back! Comes back takes your head!");
			Engrave(boss.getWinner(), boss.getObjectId());
		}
		else if(boss.getNpcId() == 35368)
			Engrave(boss.getWinner(), boss.getObjectId());
		else if(boss.getNpcId() == 35629)
			Engrave(boss.getWinner(), boss.getObjectId());
		_siegeBosses.remove(boss);
	}

	private void unspawnSiegeBosses()
	{
		for(L2SiegeBossInstance siegeBoss : _siegeBosses)
			if(siegeBoss != null)
				siegeBoss.deleteMe();
		_siegeBosses.clear();
	}

	private void spawnSiegeBosses()
	{
		for(SiegeSpawn sp : ClanHallSiegeManager.getSiegeBossSpawnList(getSiegeUnit().getId()))
		{
			L2SiegeBossInstance siegeBoss = new L2SiegeBossInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(sp.getNpcId()));
			siegeBoss.setCurrentHpMp(siegeBoss.getMaxHp(), siegeBoss.getMaxMp(), true);
			siegeBoss.setXYZInvisible(sp.getLoc().correctGeoZ());
			siegeBoss.setSpawnedLoc(siegeBoss.getLoc());
			siegeBoss.setHeading(sp.getLoc().h);
			siegeBoss.spawnMe();
			_siegeBosses.add(siegeBoss);
			if(sp.getNpcId() == 35408)
				Functions.npcSay(siegeBoss, "Gustave's soldiers, fight! Delivers the invader to die!");
			if(sp.getNpcId() == 35409)
				Functions.npcSay(siegeBoss, "Qrants kingdom of Aden lion, honorable! Grants does not die $$ln Gustave to be honorable!");
			if(sp.getNpcId() == 35410)
				Functions.npcSay(siegeBoss, "Comes to understand! Your these foreign lands invaders! This fort forever ruler, my Gustave lifts the sword!");
		}
	}

	public void announceStartSiege(long time)
	{
		if(time > 0)
		{
			if(time <= 10)
				announceToPlayer(new SystemMessage(getSiegeUnit().getName() + " siege " + time + " second(s) to start!"), false, false);
			else
				announceToPlayer(new SystemMessage(Math.round(time/60) + " minute(s) until " + getSiegeUnit().getName() + " siege begin."), false, false);
		}
	}
}