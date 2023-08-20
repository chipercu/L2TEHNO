package l2open.gameserver.model.entity.siege.fortress;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.instancemanager.FortressSiegeManager;
import l2open.gameserver.instancemanager.MercTicketManager;
import l2open.gameserver.instancemanager.SiegeGuardManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.model.entity.residence.FortStatus;
import l2open.gameserver.model.entity.siege.*;
import l2open.gameserver.model.instances.L2CommanderInstance;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2StaticObjectInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.PledgeSkillList;
import l2open.gameserver.serverpackets.SiegeInfo;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.*;

import java.util.Calendar;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

public class FortressSiege extends Siege
{
	private final GArray<L2CommanderInstance> _commanders = new GArray<L2CommanderInstance>();
	private final GArray<L2StaticObjectInstance> _flagPoles = new GArray<L2StaticObjectInstance>();
	private final GArray<L2ItemInstance> _flags = new GArray<L2ItemInstance>();

	private boolean[] barrack__status;

	private ScheduledFuture<?> _commanderRespawnTask = null;

	public L2NpcInstance _mercenary = null;
	public Location _mercenaryLoc = null;
	public int _mercenaryId = 0;

	public FortressSiege(Fortress siegeUnit)
	{
		super(siegeUnit);
		_mercenaryLoc = siegeUnit.mercenaryLoc;
		_mercenaryId = siegeUnit.mercenaryId;

		_database = new FortressSiegeDatabase(this);
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

			if(getAttackerClans().isEmpty())
			{
				if(getSiegeUnit().getOwnerId() <= 0)
					announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addString(getSiegeUnit().getName()), false, true);
				else
					announceToPlayer(new SystemMessage(SystemMessage.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addString(getSiegeUnit().getName()), false, true);
				return;
			}

			getZone().setActive(true);
			//TODO: Включить активацию после описания residence зон крепостей
			//getResidenseZone().setActive(true);

			_isInProgress = true; // Flag so that same siege instance cannot be started again
			_isMidVictory = true; // Для того, чтобы атакующие могли атаковать друг друга
			_ownerBeforeStart = getSiegeUnit().getOwnerId();

			updateSiegeClans();
			updatePlayerSiegeStateFlags(false);

			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town

			// Despawn commanders (Npcs)
			for(L2NpcInstance commanderNpc : FortressSiegeManager.getCommanderNpcsList(getSiegeUnit().getId()))
				if(commanderNpc != null)
					commanderNpc.decayMe();

			// Spawn commanders (Siege guards)
			spawnCommanders();

			getSiegeUnit().spawnDoor(); // Spawn door
			getSiegeGuardManager().spawnSiegeGuard(); // Spawn siege guard
			MercTicketManager.getInstance().deleteTickets(getSiegeUnit().getId()); // remove the tickets from the ground
			_defenderRespawnPenalty = 0; // Reset respawn delay

			// Schedule a task to prepare auto siege end
			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, getSiegeLength());
			ThreadPoolManager.getInstance().schedule(new SiegeEndTask(this), 1000); // Prepare auto end task
			_fameTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SiegeFameTask(), 5 * 60 * 1000L, 5 * 60 * 1000L);

			announceToPlayer(new SystemMessage(SystemMessage.THE_FORTRESS_BATTLE_S1_HAS_BEGAN).addString(getSiegeUnit().getName()), false, true);
			for(int time : FortressManager.getInstance().getMusic().keySet())
				ThreadPoolManager.getInstance().schedule(new PlayMusic(FortressManager.getInstance().getMusic().get(time)), (time * 1000) + 1000);
		}
	}

	@Override
	public void midVictory()
	{
		// Если осада закончилась
		if(!isInProgress() || getSiegeUnit().getOwnerId() <= 0)
			return;

		// Поменять местами атакующих и защитников
		for(SiegeClan sc : getDefenderClans().values())
			if(sc != null)
			{
				removeSiegeClan(sc, SiegeClanType.DEFENDER);
				addSiegeClan(sc, SiegeClanType.ATTACKER);
			}

		SiegeClan sc_newowner = getAttackerClan(getSiegeUnit().getOwner());
		removeSiegeClan(sc_newowner, SiegeClanType.ATTACKER);
		addSiegeClan(sc_newowner, SiegeClanType.OWNER);

		endSiege();
	}

	@Override
	public void endSiege()
	{
		getZone().setActive(false);
		//TODO: Включить деактивацию после описания residence зон крепостей
		//getResidenseZone().setActive(false);

		if(isInProgress())
		{
			announceToPlayer(new SystemMessage(SystemMessage.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED).addString(getSiegeUnit().getName()), false, true);

			if(getSiegeUnit().getOwnerId() <= 0)
				announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addString(getSiegeUnit().getName()), false, true);
			else
			{
				L2Clan oldOwner = null;
				if(_ownerBeforeStart != 0)
					oldOwner = ClanTable.getInstance().getClan(_ownerBeforeStart);
				L2Clan newOwner = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());

				if(oldOwner == null)
				{ // fortress was taken over from scratch
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(200, true, "FortressSiege")));
					SiegeManager.clearFortressRegistrations(newOwner);
				}
				else if(newOwner.equals(oldOwner))
				{ // fortress was defended
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(200, true, "FortressSiege")));
					SiegeManager.clearFortressRegistrations(newOwner);
				}
				else
				{ // fortress was taken over by another clan
					announceToPlayer(new SystemMessage(SystemMessage.S1_CLAN_IS_VICTORIOUS_IN_THE_FORTRESS_BATLE_OF_S2).addString(newOwner.getName()).addString(getSiegeUnit().getName()), false, true);
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(500, true, "FortressSiege")));
					if(oldOwner.getLevel() >= 5)
						oldOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE).addNumber(-oldOwner.incReputation(-500, true, "FortressSiege")));
					SiegeManager.clearFortressRegistrations(newOwner);
				}

				// Spawn envoys
				for(L2NpcInstance envoyNpc : FortressSiegeManager.getEnvoyNpcsList(getSiegeUnit().getId()))
					if(envoyNpc != null)
						envoyNpc.spawnMe();
			}

			// Despawn commanders (Siege guards)
			unspawnCommanders();

			// Spawn commanders (Npcs)
			for(L2NpcInstance commanderNpc : FortressSiegeManager.getCommanderNpcsList(getSiegeUnit().getId()))
				if(commanderNpc != null)
					commanderNpc.spawnMe();

			removeHeadquarters();
			unSpawnFlags();
			ClearSiegeOutpost();
			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			removeSiegeSummons();
			_isInProgress = false; // Flag so that siege instance can be started
			updatePlayerSiegeStateFlags(true);
			saveSiege(); // Save fortress specific data
			_database.clearSiegeClan(); // Clear siege clan from db
			getSiegeGuardManager().unspawnSiegeGuard(); // Remove all spawned siege guard from this fortress
			SiegeGuardManager.removeMercsFromDb(getSiegeUnit().getId());
			getSiegeUnit().spawnDoor(); // Respawn door to fortress
			if(_ownerBeforeStart != getSiegeUnit().getOwnerId())
				getSiegeUnit().setOwnDate((int) (System.currentTimeMillis() / 1000));
			getSiegeUnit().saveOwnDate();
			if(getSiegeUnit().getOwner() != null)
				for(L2Player p : getSiegeUnit().getOwner().getOnlineMembers(0))
				{
					p.setSiegeState(0);
					p.broadcastUserInfo(true);
					p.broadcastRelationChanged();
					p.sendPacket(new PledgeSkillList(getSiegeUnit().getOwner()));
				}
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

			ThreadPoolManager.getInstance().schedule(new SiegeEndSpawn(this), 14400000); // Через 4 часа после осады спауним Торговцев.

			if(getSiegeUnit().getOwnerId() > 0)
			{
				PlaySound ps = new PlaySound(1, "siege_victory", 0);
				L2Clan clan = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());
				for(L2Player member : clan.getOnlineMembers(0))
					if(member != null)
						member.sendPacket(ps);
			}
			//getFort().setFortStatus(FortStatus.ON_FORTRESS_END_SIEGE);
		}
	}

	@Override
	public boolean Engrave(L2Clan clan, int objId)
	{
		if(clan.getHasCastle() > 0)
		{
			getSiegeUnit().changeOwner(null);
			announceToPlayer(Msg.THE_REBEL_ARMY_RECAPTURED_THE_FORTRESS, false, true);
		}
		else
			getSiegeUnit().changeOwner(clan);
		return true;
	}

	@Override
	public boolean registerAttacker(L2Player player, boolean force)
	{
		if(player.getClan() != null)
		{
			if(getAttackerClans().isEmpty())
			{
				if(player.getAdena() < 250000)
				{
					player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return false;
				}
				player.getClan().broadcastToOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1_FORTRESS_BATTLE).addString(getSiegeUnit().getName()));
				player.reduceAdena(250000, true);
			}
			if(super.registerAttacker(player, force))
			{
				startAutoTask(false);
				return true;
			}
		}
		return false;
	}

	/**
	 * Start the auto tasks<BR><BR>
	 */
	@Override
	public void startAutoTask(boolean isServerStarted)
	{
		if(isServerStarted)
			ThreadPoolManager.getInstance().schedule(new SiegeEndSpawn(this), 60000); // При старте сервера сразу спауним Торговцев.

		if(getAttackerClans().isEmpty() || _siegeStartTask != null)
			return;

		_siegeDate.setTimeInMillis(((Fortress) getSiegeUnit()).getSiegeDate() * 1000L);

		setNextSiegeDate();

		_siegeRegistrationEndDate = Calendar.getInstance();
		_siegeRegistrationEndDate.setTimeInMillis(_siegeDate.getTimeInMillis());
		_siegeRegistrationEndDate.add(Calendar.MINUTE, -10);

		// Если сервер только что стартовал, осада начнется не ранее, чем через 20 минут.
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

		ThreadPoolManager.getInstance().schedule(new SiegeStartUnSpawn(this), getSiegeRegistrationEndDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()); // Запускаем таск на деспаун Торговцев за 10 минут до старта осады.

		_siegeStartTask = ThreadPoolManager.getInstance().schedule(new SiegeStartTask(this), 1000);
	}

	/** Set the date for the next siege. */
	@Override
	protected void setNextSiegeDate()
	{
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			_siegeDate = Calendar.getInstance();
			// Осада не чаще, чем каждые 4 часа + 1 час на подготовку.
			if(Calendar.getInstance().getTimeInMillis() - getSiegeUnit().getLastSiegeDate() * 1000L > 14400000)
				_siegeDate.add(Calendar.HOUR_OF_DAY, 1);
			else
			{
				_siegeDate.setTimeInMillis(getSiegeUnit().getLastSiegeDate() * 1000L);
				_siegeDate.add(Calendar.HOUR_OF_DAY, 5);
			}
			_database.saveSiegeDate();
		}
	}

	@Override
	protected void correctSiegeDateTime()
	{}

	@Override
	protected void saveSiege()
	{
		// Выставляем дату прошедшей осады
		getSiegeUnit().setLastSiegeDate((int) (getSiegeDate().getTimeInMillis() / 1000));
		// Сохраняем дату прошедшей осады
		_database.saveLastSiegeDate();
	}

	/** Display list of registered clans */
	@Override
	public void listRegisterClan(L2Player player)
	{
		player.sendPacket(new SiegeInfo(getSiegeUnit()));
	}

	/** Один из командиров убит */
	public void killedCommander(L2CommanderInstance ct)
	{
		_commanders.remove(ct);
		operateGuardDoors(true, _commanders.size());
		if(_commanders.size() == 0)
		{
			spawnFlags();
			operateCommandCenterDoors(true);
			if(_commanderRespawnTask != null)
				_commanderRespawnTask.cancel(true);
			_commanderRespawnTask = null;
		}
		else if(_commanderRespawnTask == null)
			_commanderRespawnTask = ThreadPoolManager.getInstance().schedule(new CommanderRespawnTask(), 600000);
	}

	private class CommanderRespawnTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(isInProgress())
			{
				unspawnCommanders();
				spawnCommanders();
			}
			_commanderRespawnTask = null;
		}
	}

	private void unspawnCommanders()
	{
		for(L2CommanderInstance commander : _commanders)
			if(commander != null)
				commander.deleteMe();
		_commanders.clear();
	}

	private void spawnCommanders()
	{
		for(SiegeSpawn sp : FortressSiegeManager.getCommanderSpawnList(getSiegeUnit().getId()))
		{
			L2CommanderInstance commander = new L2CommanderInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(sp.getNpcId()));
			commander.setCurrentHpMp(commander.getMaxHp(), commander.getMaxMp(), true);
			commander.setXYZInvisible(sp.getLoc().correctGeoZ());
			commander.setSpawnedLoc(commander.getLoc());
			commander.setHeading(sp.getLoc().h);
			commander.spawnMe();
			_commanders.add(commander);
		}
	}

	public void operateGuardDoors(boolean open, int commanders_count)
	{
		for(Entry<Integer, Integer> entry : FortressSiegeManager.getGuardDoors(getSiegeUnit().getId()).entrySet())
		{
			if(entry.getValue() < commanders_count)
				continue;
			L2DoorInstance door = getSiegeUnit().getDoor(entry.getKey());
			if(door != null)
				if(open)
					door.openMe();
				else
					door.closeMe();
		}
	}

	public void operateCommandCenterDoors(boolean open)
	{
		for(Integer doorId : FortressSiegeManager.getCommandCenterDoors(getSiegeUnit().getId()))
		{
			L2DoorInstance door = getSiegeUnit().getDoor(doorId);
			if(door != null)
				if(open)
					door.openMe();
				else
					door.closeMe();
		}
	}

	public void addFlagPole(L2StaticObjectInstance art)
	{
		_flagPoles.add(art);
	}

	private void spawnFlags()
	{
		for(SiegeSpawn sp : FortressSiegeManager.getFlagsList(getSiegeUnit().getId()))
		{
			L2ItemInstance flag = ItemTemplates.getInstance().createItem(sp.getNpcId());
			flag.setCustomFlags(L2ItemInstance.FLAG_EQUIP_ON_PICKUP | L2ItemInstance.FLAG_NO_DESTROY | L2ItemInstance.FLAG_NO_TRADE | L2ItemInstance.FLAG_NO_UNEQUIP, false);
			flag.setXYZInvisible(sp.getLoc().correctGeoZ());
			flag.spawnMe();
			_flags.add(flag);
		}
	}

	private void unSpawnFlags()
	{
		for(L2ItemInstance flag : _flags)
			if(flag != null)
			{
				if(flag.getOwnerId() > 0)
				{
					L2Player owner = L2ObjectsStorage.getPlayer(flag.getOwnerId());
					if(owner != null)
						flag = owner.getInventory().dropItem(flag, flag.getCount(), true);
				}
				flag.deleteMe();
			}
		_flags.clear();
	}

	public GArray<L2CommanderInstance> getCommanders()
	{
		return _commanders;
	}

	private class SiegeEndSpawn extends l2open.common.RunnableImpl
	{
		private FortressSiege _fs = null;
		public SiegeEndSpawn(FortressSiege fs)
		{
			_fs = fs;
		}

		public void runImpl()
		{
			if(_fs != null)
				_fs._mercenary = Functions.spawn(_fs._mercenaryLoc, _fs._mercenaryId, 10);
		}
	}

	private class SiegeStartUnSpawn extends l2open.common.RunnableImpl
	{
		private FortressSiege _fs = null;
		public SiegeStartUnSpawn(FortressSiege fs)
		{
			_fs = fs;
		}

		public void runImpl()
		{
			if(_fs != null && _fs._mercenary != null && _fs._mercenary.getSpawn() != null)
			{
				_fs._mercenary.getSpawn().stopRespawn();
				_fs._mercenary.getSpawn().getLastSpawn().deleteMe();
				_fs._mercenary = null;
			}
		}
	}

	public Fortress getFort()
	{
		return (Fortress)getSiegeUnit();
	}

	public int getBarrackCount()
	{
		int count = 0;
		for(boolean value : barrack__status)
			if(value)
				count++;
		return count;
	}

	public boolean[] getBarracks()
	{
		return barrack__status;
	}

	// После убийства первого командира, есть 10 минут, что бы убить всех остальных, в противном случае отреспаются все...
	public void BarrackCaptured(int barrack_id, int object_id)
	{
		barrack__status[barrack_id] = false;
		// operateGuardDoors(true, _commanders.size());
		if(getBarrackCount() == 0)
		{
			if(protected_npc_leave)
			{
				spawnFlags();
				operateCommandCenterDoors(true);
			}
			getFort().setFortStatus(FortStatus.ON_FORTRESS_START_FLAG_CAPTURE);
			if(_commanderRespawnTask != null)
				_commanderRespawnTask.cancel(true);
			_commanderRespawnTask = null;
			announceToPlayer(new SystemMessage(SystemMessage.ALL_BARRACKS_ARE_OCCUPIED), 0);
			return;
		}
		else if(_commanderRespawnTask == null)
			_commanderRespawnTask = ThreadPoolManager.getInstance().schedule(new CommanderRespawnTask(), 600000);
		announceToPlayer(new SystemMessage(SystemMessage.THE_BARRACKS_HAVE_BEEN_SEIZED), 0);
	}

	public boolean protected_npc_leave = true;
	public void ProtectedNpcDied(int object_id)
	{
		if(getBarrackCount() > 0)
			protected_npc_leave = false;
	}
	/**
	 * +2087	1	u,Крепость атакована!\0	0	79	9B	B0	FF	a,ItemSound3.sys_fortress_underattack02\0	a,	2	0	5	1	0	u,Начало битвы за крепость\0	a,none\0
	 * +2088(10, 5, 1)	1	u,Битва за крепость начинается через $s1 мин.\0	0	79	9B	B0	FF	a,	a,	2	0	5	1	0	u,Начало битвы за крепость: $s1 мин.\0	a,none\0
	 * +2089(30, 10, 5, 1)	1	u,Битва за крепость начинается через $s1 сек.\0	0	79	9B	B0	FF	a,	a,	2	0	5	1	0	u,Начало битвы за крепость: $s1 сек.\0	a,none\0
	 * +2090	1	u,Битва за крепость началась.\0	0	79	9B	B0	FF	a,ItemSound3.sys_fortress_start\0	a,2090\0	2	0	5	1	0	u,Начало битвы за крепость\0	a,none\0
	 * +2183	1	u,$s1: битва закончена.\0	0	79	9B	B0	FF	a,	a,	0	0	0	0	0	a,	a,none\0
	 * +2184	1	u,$s2 достается клану $s1.\0	0	79	9B	B0	FF	a,	a,	0	0	0	0	0	a,	a,none\0
	 * -2084; // Вражеские Заложники Крови ворвались в крепость
	 * -2169; // Ваш клан был заявлен на битву за крепость $s1.
	 **/
	public void announceStartSiege(long time)
	{
		if(time <= 600 && getFort()._fortStatus != FortStatus.ON_FORTRESS_STANDBY_SIEGE)
			getFort().setFortStatus(FortStatus.ON_FORTRESS_STANDBY_SIEGE);
		if(time == 600 || time == 300 || time == 60)
			announceToPlayer(new SystemMessage(SystemMessage.S1_MINUTE_UNTIL_THE_FORTRESS_BATTLE_STARTS).addNumber(Math.round(time / 60)), 0);
		else if(time == 30 || time == 10 || time == 5 || time == 1)
			announceToPlayer(new SystemMessage(SystemMessage.S1_SECOND_UNTIL_THE_FORTRESS_BATTLE_STARTS).addNumber(time), 0);
		else if(time == 0)
		{
			announceToPlayer(new SystemMessage(SystemMessage.THE_FORTRESS_BATTLE_S1_HAS_BEGAN), 1);
			announceToPlayer(new SystemMessage(SystemMessage.A_FORTRESS_IS_UNDER_ATTACK), 2);
		}
	}

	/**
	 * 0 - Всем зарегестрированым.
	 * 1 - Только атакующим.
	 * 2 - Только защитника.
	 **/
	public void announceToPlayer(SystemMessage message, int deff)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getClan() != null && (deff == 0 && isParticipant(player) || deff == 1 && checkIsAttacker(player.getClan()) || deff == 2 && checkIsDefender(player.getClan())))
				player.sendPacket(message);
	}

	public class PlayMusic extends l2open.common.RunnableImpl
	{
		private String	musicName;

		public PlayMusic(String name)
		{
			musicName = name;
		}

		@Override
		public void runImpl()
		{
			if(_isInProgress)
			{
				PlaySound ps = new PlaySound(1, musicName.trim(), 0);

				L2Clan clan;
				if(getSiegeUnit().getOwnerId() > 0)
				{
					clan = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());
					for(L2Player member : clan.getOnlineMembers(0))
						if(member != null)
							member.sendPacket(ps);
				}

				for(SiegeClan siegeClan : getAttackerClans().values())
				{
					clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
					for(L2Player member : clan.getOnlineMembers(0))
						if(member != null)
							member.sendPacket(ps);
				}
			}
		}
	}
	/**
	///// 요새전 /////
	// 요새전 메이커 이벤트
	[ON_FORTRESS_STANDBY_SIEGE] = 0
	[ON_FORTRESS_START_SIEGE] = 1
	[ON_FORTRESS_START_BARRACK_CAPTURE] = 2
	[ON_FORTRESS_START_FLAG_CAPTURE] = 3
	[ON_FORTRESS_END_SIEGE] = 4
	[ON_FORTRESS_NEW_CASTLE_OWNER] = 5
	[ON_FORTRESS_DOOR_BREAK] = 6
	[ON_FORTRESS_SERVER_START_PEACE] = 7

	// 요새 상태
	[FORTRESS_ANNOUNCING] = 0               // 평화 상태
	[FORTRESS_REGISTRATION] = 1             // 등록기간, 50분
	[FORTRESS_STANDBY] = 2                  // 준비기간, 10분
	[FORTRESS_SIEGE] = 3                    // 요새전, 1시간

	// 요새 facility types
	[FORTRESS_GUARD_REINFORCEMENT] = 0      // 수비병 증강, 레벨 1/2
	[FORTRESS_GUARD_POWER_UP] = 1           // 수비병 공방 증가, 레벨 1/2
	[FORTRESS_DOOR_POWER_UP] = 2            // 요새문 강화, 레벨 1
	[FORTRESS_PHOTOCANNON] = 3              // 포토 캐논, 레벨 1
	[FORTRESS_SCOUT] = 4                    // 정찰병, 레벨 1

	// 요새-성 계약 관계
	[FORTRESS_CONTRACT_INDEP] = -1          // 요새 독립상태
	[FORTRESS_CONTRACT_NONE] = 0            // 요새 무관계
	[FORTRESS_CONTRACT_CASTLE] = 1          // 요새 성과 계약 상태

	**/
}