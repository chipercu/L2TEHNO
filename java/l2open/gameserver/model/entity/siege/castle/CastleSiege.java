package l2open.gameserver.model.entity.siege.castle;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.MercTicketManager;
import l2open.gameserver.instancemanager.SiegeGuardManager;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.residence.Residence;
import l2open.gameserver.model.entity.siege.*;
import l2open.gameserver.model.instances.L2ArtefactInstance;
import l2open.gameserver.model.instances.L2ControlTowerInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.MailParcelController;
import l2open.gameserver.model.items.MailParcelController.Letter;
import l2open.gameserver.serverpackets.ExNoticePostArrived;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.serverpackets.PledgeSkillList;
import l2open.gameserver.serverpackets.SiegeInfo;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.serverpackets.Revive;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.MapRegion;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.util.Calendar;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class CastleSiege extends Siege
{
	private final GArray<L2ControlTowerInstance> _controlTowers = new GArray<L2ControlTowerInstance>();
	private final GArray<L2ArtefactInstance> _artifacts = new GArray<L2ArtefactInstance>();
	private final FastMap<Integer, Integer> _engrave = new FastMap<Integer, Integer>().setShared(true);
	private int[] _nextSiegeTimes = new int[0];
	public boolean _notNextSet = true;
	private Future<?> _setNewSiegeData = null;

    protected static Logger _log = Logger.getLogger(CastleSiege.class.getName());

	protected TrapPacketSender trapListener = new TrapPacketSender();

	public CastleSiege(Residence castle)
	{
		super(castle);
		_database = new CastleSiegeDatabase(this);
		_siegeGuardManager = new SiegeGuardManager(getSiegeUnit());
		_database.loadSiegeClan();
	}

	@Override
	public void startSiege()
	{
		if(!_isInProgress)
		{
			((Castle) getSiegeUnit()).setDominionLord(0, true);
			setRegistrationOver(true);

			_database.loadSiegeClan(); // Load siege clan from db

			if(getAttackerClans().isEmpty())
			{
				if(getSiegeUnit().getOwnerId() <= 0)
					announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addString(getSiegeUnit().getName()), false, false);
				else
					announceToPlayer(new SystemMessage(SystemMessage.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addString(getSiegeUnit().getName()), false, false);
				return;
			}

			// Слушатель добавляется перед активацией т.к. зона выполнит вход
			getZone().getListenerEngine().addMethodInvokedListener(trapListener);
			getZone().setActive(true);
			getResidenseZone().setActive(true);

			_isInProgress = true; // Flag so that same siege instance cannot be started again
			_isMidVictory = false;
			_ownerBeforeStart = getSiegeUnit().getOwnerId();

			updateSiegeClans();
			updatePlayerSiegeStateFlags(false);

			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			respawnControlTowers(); // Respawn control towers
			getSiegeUnit().spawnDoor(); // Spawn door
			getSiegeGuardManager().spawnSiegeGuard(); // Spawn siege guard
			MercTicketManager.getInstance().deleteTickets(getSiegeUnit().getId()); // remove the tickets from the ground
			_defenderRespawnPenalty = 0; // Reset respawn delay

			// Schedule a task to prepare auto siege end
			_siegeEndDate = Calendar.getInstance();
			_siegeEndDate.add(Calendar.MINUTE, getSiegeLength());
			ThreadPoolManager.getInstance().schedule(new SiegeEndTask(this), 1000); // Prepare auto end task
			_fameTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SiegeFameTask(), 5 * 60 * 1000L, 5 * 60 * 1000L);

			announceToPlayer(Msg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT_IT_WILL_BE_DISSOLVED_WHEN_THE_CASTLE_LORD_IS_REPLACED, false, true);
			announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_STARTED).addString(getSiegeUnit().getName()), false, false);

			for(int time : CastleManager.getInstance().getMusic().keySet())
				ThreadPoolManager.getInstance().schedule(new PlayMusic(CastleManager.getInstance().getMusic().get(time)), (time * 1000) + 1000);
		}
	}

	@Override
	public void midVictory()
	{
		// Если осада закончилась
		if(!isInProgress())
			return;

		// Если атакуется замок, принадлежащий NPC, и только 1 атакующий - закончить осаду
		if(getDefenderClans().isEmpty() && getAttackerClans().size() == 1)
		{
			SiegeClan sc_newowner = getAttackerClan(getSiegeUnit().getOwner());
			removeSiegeClan(sc_newowner, SiegeClanType.ATTACKER);
			addSiegeClan(sc_newowner, SiegeClanType.OWNER);
			endSiege();
			return;
		}

		int allyId = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId()).getAllyId();

		// Если атакуется замок, принадлежащий NPC, и все атакующие в одном альянсе - закончить осаду
		if(allyId != 0 && getDefenderClans().isEmpty())
		{
			boolean allinsamealliance = true;
			for(SiegeClan sc : getAttackerClans().values())
				if(sc != null && sc.getClan() != null && sc.getClan().getAllyId() != allyId)
					allinsamealliance = false;
			if(allinsamealliance)
			{
				SiegeClan sc_newowner = getAttackerClan(getSiegeUnit().getOwner());
				removeSiegeClan(sc_newowner, SiegeClanType.ATTACKER);
				addSiegeClan(sc_newowner, SiegeClanType.OWNER);
				endSiege();
				return;
			}
		}

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

		_isMidVictory = true;

		clearSiegeClans();
		updateSiegeClans();
		updatePlayerSiegeStateFlags(false);

		announceToPlayer(Msg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, false, true);

		teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown);
		teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown);

		_defenderRespawnPenalty = 0; // Reset respawn delay

		getSiegeGuardManager().unspawnSiegeGuard(); // Remove all spawned siege guard from this castle
		getSiegeUnit().removeUpgrade(); // Remove all castle upgrade
		respawnControlTowers(); // Respawn control towers
		getSiegeUnit().spawnDoor(true); // Respawn door to castle but make them weaker (50% hp)
	}

	@Override
	public void endSiege()
	{
		getZone().setActive(false);
		getZone().getListenerEngine().removeMethodInvokedListener(trapListener); // Слушаетель убирается после деактивации т.к. зона выполнит выход
		getResidenseZone().setActive(false);

		if(isInProgress())
		{
			announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_FINISHED).addString(getSiegeUnit().getName()), false, false);

			if(getSiegeUnit().getOwnerId() <= 0)
				announceToPlayer(new SystemMessage(SystemMessage.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addString(getSiegeUnit().getName()), false, false);
			else
			{
				L2Clan oldOwner = null;
				if(_ownerBeforeStart != 0)
					oldOwner = ClanTable.getInstance().getClan(_ownerBeforeStart);
				L2Clan newOwner = ClanTable.getInstance().getClan(getSiegeUnit().getOwnerId());

				if(ConfigValue.AllowAddCastleRewards)
					setReward(newOwner);

				if(oldOwner == null)
				{ // castle was taken over from scratch
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(1500, true, "CastleSiege")));
					SiegeManager.clearCastleRegistrations(newOwner);
					SiegeManager.clearFortressRegistrations(newOwner); // TODO убрать
				}
				else if(newOwner.equals(oldOwner))
				{ // castle was defended
					((Castle) getSiegeUnit()).setDominionLord(newOwner.getLeaderId(), true);
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(1500, true, "CastleSiege")));
					SiegeManager.clearCastleRegistrations(newOwner);
					SiegeManager.clearFortressRegistrations(newOwner); // TODO убрать
				}
				else
				{ // castle was taken over by another clan
					announceToPlayer(new SystemMessage(SystemMessage.CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE).addString(newOwner.getName()).addString(getSiegeUnit().getName()), false, false);
					if(newOwner.getLevel() >= 5)
						newOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(newOwner.incReputation(3000, true, "CastleSiege")));
					if(oldOwner.getLevel() >= 5)
						oldOwner.broadcastToOnlineMembers(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLAN_REPUTATION_SCORE).addNumber(-oldOwner.incReputation(-3000, true, "CastleSiege")));
					SiegeManager.clearCastleRegistrations(newOwner);
					SiegeManager.clearFortressRegistrations(newOwner); // TODO убрать
					for(L2ClanMember member : newOwner.getMembers())
					{
						L2Player player = member.getPlayer();
						if(player != null)
						{
							if(player.isOnline() && player.isNoble())
								Hero.getInstance().addHeroDiary(player.getObjectId(), 3, getSiegeUnit().getId());
						}
					}
				}
			}

			removeHeadquarters();
			teleportPlayer(TeleportWhoType.Attacker, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			teleportPlayer(TeleportWhoType.Spectator, MapRegion.TeleportWhereType.ClosestTown); // Teleport to the closest town
			removeSiegeSummons();
			ClearSiegeOutpost();
			_isInProgress = false; // Flag so that siege instance can be started
			updatePlayerSiegeStateFlags(true);
			startDataTask(ConfigValue.CastleSelectHours.length == 0 ? 10000 : ConfigValue.CastleSelectHoursTime);
			saveSiege();
			_database.clearSiegeClan(); // Clear siege clan from db
			respawnControlTowers(); // Remove all control tower from this castle
			getSiegeGuardManager().unspawnSiegeGuard(); // Remove all spawned siege guard from this castle
			SiegeGuardManager.removeMercsFromDb(getSiegeUnit().getId());
			getSiegeUnit().spawnDoor(); // Respawn door to castle
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
		}
	}

	@Override
	public boolean Engrave(L2Clan clan, int objId)
	{
		if(_artifacts.size() == 1)
		{
			getSiegeUnit().changeOwner(clan);
			return true;
		}
		else if(_artifacts.size() == 2)
		{
			if(_engrave.containsKey(clan.getClanId()))
			{
				Integer arg = _engrave.get(clan.getClanId());
				if(arg == objId)
					return false;
				_engrave.clear();
				getSiegeUnit().changeOwner(clan);
				return true;
			}
			else
				_engrave.put(clan.getClanId(), objId);
		}
		return false;
	}

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
			if(((Castle) getSiegeUnit())._setNewData == 1)
			{
				//minDate.add(Calendar.MINUTE, 3);
				minDate.add(Calendar.HOUR_OF_DAY, 1);
				_siegeDate.setTimeInMillis(Math.max(minDate.getTimeInMillis(), _siegeDate.getTimeInMillis()));
				_database.saveSiegeDate();
			}

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
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()-(172800*ConfigValue.CastleSiegeDay))
			_siegeDate.setTimeInMillis(Calendar.getInstance().getTimeInMillis()-172800000);
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			// Set next siege date if siege has passed
			_siegeDate.add(Calendar.DAY_OF_MONTH, ConfigValue.CastleSiegeDay); // Schedule to happen in 14 days
			if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
				setNextSiegeDate(); // Re-run again if still in the pass
		}
		_log.info("\nsetNextSiegeDate["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		//l2open.util.Util.test();
	}

	@Override
	protected void correctSiegeDateTime()
	{
		_log.info("correctSiegeDateTime[START]["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		boolean corrected = false;
		if(_siegeDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
		{
			//corrected = true;
			setNextSiegeDate();
			_log.info("correctSiegeDateTime[NEXT_DATE]["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		}
		if(_siegeDate.get(Calendar.DAY_OF_WEEK) != getSiegeUnit().getSiegeDayOfWeek())
		{
			corrected = true;
			_siegeDate.set(Calendar.DAY_OF_WEEK, getSiegeUnit().getSiegeDayOfWeek());
			_log.info("correctSiegeDateTime[DAY_OF_WEEK]["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		}
		if(_siegeDate.get(Calendar.HOUR_OF_DAY) != getSiegeUnit().getSiegeHourOfDay())
		{
			corrected = true;
			_siegeDate.set(Calendar.HOUR_OF_DAY, getSiegeUnit().getSiegeHourOfDay());
			_log.info("correctSiegeDateTime[HOUR_OF_DAY]["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		}
		_siegeDate.set(Calendar.MINUTE, 0);
		_siegeDate.set(Calendar.SECOND, 0);
		if(corrected)
			_database.saveSiegeDate();

		_nextSiegeTimes = new int[ConfigValue.CastleSelectHours.length];

		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(_siegeDate.getTimeInMillis());
		for(int i=0;i<ConfigValue.CastleSelectHours.length;i++)
		{
			calendar.set(Calendar.HOUR_OF_DAY, ConfigValue.CastleSelectHours[i]);
			_nextSiegeTimes[i] = (int)(calendar.getTimeInMillis() / 1000L);
		}
		_log.info("correctSiegeDateTime[FINISH]["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
	}

	protected void saveSiegePersonal()
	{
		if(_notNextSet)
		{
			((Castle) getSiegeUnit())._setNewData = 1;
			// Сохраняем дату следующей осады
			_database.saveSiegeDate();
			// Запускаем таск для следующей осады
			startAutoTask(false);
			_log.info("saveSiegePersonal["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		}
	}

	@Override
	protected void saveSiege()
	{
		if(_notNextSet)
		{
			// Выставляем дату следующей осады
			setNextSiegeDate();
			if(_siegeDate.get(Calendar.DAY_OF_WEEK) != getSiegeUnit().getSiegeDayOfWeek())
				_siegeDate.set(Calendar.DAY_OF_WEEK, getSiegeUnit().getSiegeDayOfWeek());
			if(_siegeDate.get(Calendar.HOUR_OF_DAY) != getSiegeUnit().getSiegeHourOfDay())
				_siegeDate.set(Calendar.HOUR_OF_DAY, getSiegeUnit().getSiegeHourOfDay());
			_siegeDate.set(Calendar.MINUTE, 0);
			_siegeDate.set(Calendar.SECOND, 0);
			// Сохраняем дату следующей осады
			_database.saveSiegeDate();
			// Запускаем таск для следующей осады
			//startAutoTask(false);
			_nextSiegeTimes = new int[ConfigValue.CastleSelectHours.length];

			final Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(_siegeDate.getTimeInMillis());
			for(int i=0;i<ConfigValue.CastleSelectHours.length;i++)
			{
				calendar.set(Calendar.HOUR_OF_DAY, ConfigValue.CastleSelectHours[i]);
				_nextSiegeTimes[i] = (int)(calendar.getTimeInMillis() / 1000L);
			}
			_log.info("saveSiege["+Calendar.getInstance().getTimeInMillis()+"]["+System.currentTimeMillis()+"]: Siege of " + getSiegeUnit().getName() + ": " + _siegeDate.getTime());
		}
	}

	/** Display list of registered clans */
	@Override
	public void listRegisterClan(L2Player player)
	{
		player.sendPacket(new SiegeInfo(getSiegeUnit()));
	}

	/** Remove all control tower spawned. */
	private void respawnControlTowers()
	{
		// Remove all instance of control tower for this castle
		for(L2ControlTowerInstance ct : _controlTowers)
			if(ct != null)
			{
				DecayTaskManager.getInstance().cancelDecayTask(ct);
				ct.decayMe();
				ct.setCurrentHpMp(ct.getMaxHp(), ct.getMaxMp(), true);
				ct.spawnMe();
			}
	}

	public void addControlTower(L2ControlTowerInstance tower)
	{
		_controlTowers.add(tower);
	}
	
	public void deleteControlTower(L2ControlTowerInstance tower)
	{
		_controlTowers.remove(tower);
	}

	public boolean isAllTowersDead()
	{
		boolean allDead = true;
		for(L2ControlTowerInstance ct : _controlTowers)
			if(ct != null && !ct.isDead())
				allDead = false;

		return allDead;
	}

	public void addArtifact(L2ArtefactInstance art)
	{
		_artifacts.add(art);
	}

	/**
	 * Обновляет статус ловушек у текущей осады.
	 * Если игрок входит(enter == true), то будет отослано состояние трэпов.
	 * Если выходит, то трэпы будут простро выключены
	 * Если осада не запущена, то трепы выключатся.
	 * @param player игрок
	 * @param enter вход или выход игрока
	 * <p>
	 * TODO: обработка
	 */
	@Override
	public void sendTrapStatus(L2Player player, boolean enter)
	{
		if(enter)
		{
			// TODO: player.sendPacket(new EventTrigger(...));
		}
		else
		{
			// TODO: player.sendPacket(new EventTrigger(...));
		}
	}

	/**
	 * Осадной зоне добавляется слушатель для входа/выхода объекта
	 */
	private class TrapPacketSender extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			if(object.isPlayer())
			{
				L2Player player = (L2Player) object;
				sendTrapStatus(player, true);
				//System.out.println(player.getName() + " -> enter; " + zone);
			}
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			if(object.isPlayer())
			{
				L2Player player = (L2Player) object;
				sendTrapStatus(player, false);
				//System.out.println(player.getName() + " -> exit; " + zone);
			}
		}
	}

	public static class SetNewSiegeData extends l2open.common.RunnableImpl
	{
		private CastleSiege _cs;

		public SetNewSiegeData(CastleSiege cs)
		{
			_cs = cs;
		}

		public void runImpl()
		{
			if(_cs == null)
			{
				_log.info("CastleSiege(489): SetNewSiegeData cs == null");
				return;
			}
			_cs.saveSiegePersonal();
		}
	}

	public int[] getNextSiegeTimes()
	{
		return _nextSiegeTimes;
	}

	public void setNextSiegeTime(int id)
	{
		boolean _break = true;
		for(int data : _nextSiegeTimes)
			if(data == id)	
				_break = false;
		if(_break || _setNewSiegeData == null)
			return;

		_nextSiegeTimes = new int[0];
		_setNewSiegeData.cancel(false);
		_setNewSiegeData = null;

		_siegeDate.setTimeInMillis(id*1000L);
		((Castle) getSiegeUnit())._SiegeHourOfDay = _siegeDate.get(Calendar.HOUR_OF_DAY);
		((Castle) getSiegeUnit())._setNewData = 1;
		// Сохраняем дату следующей осады
		_database.saveSiegeDate();
		// Запускаем таск для следующей осады
		startAutoTask(false);

		_notNextSet = false;
	}

	public void startDataTask(long time)
	{
		_setNewSiegeData = ThreadPoolManager.getInstance().schedule(new SetNewSiegeData(this), time); // Запускаем таск авто-установки времени осады если КЛ не установил сам новую дату осады.
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
				for(SiegeClan siegeClan : getDefenderClans().values())
				{
					clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
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

	private void setReward(L2Clan newOwner)
	{
		long[][] reward1 = new long[0][];
		long[][] reward2 = new long[0][];
		switch(getSiegeUnit().getId())
		{
			case 1:
				reward1 = ConfigValue.GludioCastleRewards;
				reward2 = ConfigValue.GludioLeadCastleRewards;
				break;
			case 2:
				reward1 = ConfigValue.DionCastleRewards;
				reward2 = ConfigValue.DionLeadCastleRewards;
				break;
			case 3:
				reward1 = ConfigValue.GiranCastleRewards;
				reward2 = ConfigValue.GiranLeadCastleRewards;
				break;
			case 4:
				reward1 = ConfigValue.OrenCastleRewards;
				reward2 = ConfigValue.OrenLeadCastleRewards;
				break;
			case 5:
				reward1 = ConfigValue.AdenCastleRewards;
				reward2 = ConfigValue.AdenLeadCastleRewards;
				break;
			case 6:
				reward1 = ConfigValue.InnadrilCastleRewards;
				reward2 = ConfigValue.InnadrilLeadCastleRewards;
				break;
			case 7:
				reward1 = ConfigValue.GoddardCastleRewards;
				reward2 = ConfigValue.GoddardLeadCastleRewards;
				break;
			case 8:
				reward1 = ConfigValue.RuneCastleRewards;
				reward2 = ConfigValue.RuneLeadCastleRewards;
				break;
			case 9:
				reward1 = ConfigValue.ShuttgartCastleRewards;
				reward2 = ConfigValue.ShuttgartLeadCastleRewards;
				break;
		}
		for(L2ClanMember member : newOwner.getMembers())
		{
			if(member.isClanLeader())
				sendReward(member.getObjectId(), reward2);
			else
				sendReward(member.getObjectId(), reward1);
		}
	}

	private void sendReward(int obj_id, long[][] rewards)
	{
		GArray<L2ItemInstance> attachments = new GArray<L2ItemInstance>();
		for(long[] reward : rewards)
			if(Rnd.chance(reward[3]))
			{
				long count = Rnd.get(reward[1], reward[2]);

				L2ItemInstance reward1 = ItemTemplates.getInstance().createItem((int)reward[0]);
				reward1.setCount(count);
				attachments.add(reward1);
			}

		if(attachments.size() > 0)
		{
			Letter letter = new Letter();
			letter.receiverId = obj_id;
			letter.receiverName = "";
			letter.senderId = 1;
			letter.senderName = "";
			letter.topic = "Siege reward";
			letter.body = "Congratulations!";
			letter.price = 0;
			letter.unread = 1;
			letter.system = 1;
			letter.hideSender = 2;
			letter.validtime = 1296000 + (int) (System.currentTimeMillis() / 1000); // 14 days

			MailParcelController.getInstance().sendLetter(letter, attachments);

			L2Player player = L2ObjectsStorage.getPlayer(obj_id);
			if(player != null)
				player.sendPacket(new ExNoticePostArrived(1));
		}
	}
}