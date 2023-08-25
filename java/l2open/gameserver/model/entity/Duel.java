package l2open.gameserver.model.entity;

import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Effect;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.skills.Env;
import l2open.util.reference.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Duel
{
	protected static Logger _log = Logger.getLogger(Duel.class.getName());

	// =========================================================
	// Data Field
	private boolean _isPartyDuel;
	private Calendar _DuelEndTime;
	private int _surrenderRequest = 0;
	private int _countdown = 4;
	private boolean _finished = false;

	List<L2Player> _team1 = new ArrayList<L2Player>(), _team2 = new ArrayList<L2Player>();

	private Map<L2Player, PlayerCondition> _playerConditions = new ConcurrentHashMap<L2Player, PlayerCondition>();

	public static enum DuelResultEnum
	{
		Continue,
		Team1Win,
		Team2Win,
		Team1Surrender,
		Team2Surrender,
		Canceled,
		Timeout
	}

	public static enum DuelState
	{
		Winner,
		Looser,
		Fighting,
		Dead,
		Interrupted
	}

	// =========================================================
	// Constructor
	public Duel(L2Player playerA, L2Player playerB, boolean partyDuel)
	{
		_isPartyDuel = partyDuel;

		_team1.add(playerA);
		_team2.add(playerB);

		_DuelEndTime = Calendar.getInstance();
		if(_isPartyDuel)
			_DuelEndTime.add(Calendar.SECOND, 300);
		else
			_DuelEndTime.add(Calendar.SECOND, 120);

		if(_isPartyDuel)
		{
			//Добавить игроков в списки дуэлянтов
			for(L2Player p : playerA.getParty().getPartyMembers())
				if(p != playerA)
					_team1.add(p);

			for(L2Player p : playerB.getParty().getPartyMembers())
				if(p != playerB)
					_team1.add(p);

			// increase countdown so that start task can teleport players
			_countdown++;
			// inform players that they will be portet shortly
			broadcastToTeam(Msg.IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE, _team1);
			broadcastToTeam(Msg.IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE, _team2);
		}

		// Save player Conditions
		savePlayerConditions();

		// Schedule duel start
		ThreadPoolManager.getInstance().schedule(new ScheduleStartDuelTask(this), 3000, true);
	}

	// ===============================================================
	// Nested Class

	public class PlayerCondition
	{
		private HardReference<? extends L2Player> player_ref;
		private double _hp, _mp, _cp;
		private boolean _paDuel;
		private int _x, _y, _z, _classId, _ref_id;
		private DuelState _duelState;
		private List<L2Effect> _effects;

		public PlayerCondition(L2Player player, boolean partyDuel)
		{
			if(player == null)
				return;
			player_ref = player.getRef();
			_hp = player.getCurrentHp();
			_mp = player.getCurrentMp();
			_cp = player.getCurrentCp();
			_paDuel = partyDuel;
			_classId = player.getActiveClassId();
			_ref_id = player.getReflectionId();

			if(_paDuel)
			{
				_x = player.getX();
				_y = player.getY();
				_z = player.getZ();
			}

			ConcurrentLinkedQueue<L2Effect> effectList = player.getEffectList().getAllEffects();
			_effects = new ArrayList<L2Effect>(effectList.size());
			for(L2Effect effect2 : effectList)
			{
				if(effect2 != null)
				{
					L2Effect effect = effect2.getTemplate().getEffect(new Env(effect2.getEffector(), effect2.getEffected(), effect2.getSkill()));
					if(effect != null)
					{
						effect.setCount(effect2.getCount());
						effect.setPeriod(effect2.getCount() == 1 ? effect2.getPeriod() - effect2.getTime() : effect2.getPeriod());

						_effects.add(effect);
					}
				}
			}
		}

		public void RestoreCondition(boolean abnormalEnd)
		{
			L2Player player = getPlayer();
			if(player == null)
				return;

			// if it is an abnormal DuelEnd do not restore hp, mp, cp
			if(!abnormalEnd && !player.isDead())
			{
				player.getEffectList().stopAllEffects(false);
				if(!player.isInOlympiadMode() && player.getActiveClassId() == _classId && _ref_id == player.getReflectionId())
					for(L2Effect e : _effects)
						player.getEffectList().addEffect(e);
				player.setCurrentHp(_hp, false);
				player.setCurrentMp(_mp);
				player.setCurrentCp(_cp);
				player.sendChanges();
				player.updateEffectIcons();
			}

			_effects.clear();

			if(_paDuel)
				TeleportBack();
		}

		public void TeleportBack()
		{
			L2Player player = getPlayer();
			if(player != null && _paDuel)
				player.teleToLocation(_x, _y, _z);
		}

		public L2Player getPlayer()
		{
			return player_ref.get();
		}

		public void setDuelState(DuelState d)
		{
			_duelState = d;
		}

		public DuelState getDuelState()
		{
			return _duelState;
		}
	}

	// ===============================================================
	// Schedule task
	@SuppressWarnings( { "fallthrough" })
	public class ScheduleDuelTask extends l2open.common.RunnableImpl
	{
		private Duel _duel;

		public ScheduleDuelTask(Duel duel)
		{
			_duel = duel;
		}

		public void runImpl()
		{
			try
			{
				DuelResultEnum status = _duel.checkEndDuelCondition();

				//_log.info("DuelCheck done, result: "+status.toString());

				switch(status)
				{
					case Continue:
						ThreadPoolManager.getInstance().schedule(this, 1000, true);
						break;
					case Team1Win:
					case Team2Win:
					case Team1Surrender:
					case Team2Surrender:
						setFinished(true);
						playKneelAnimation();
					case Canceled:
					case Timeout:
						setFinished(true); //На всякий пожарный, если верхнее не выполнилось.
						stopFighting();

						//Колечка должны сниматся сразу
						for(L2Player p : _team1)
							if(p != null)
								p.setTeam(0, false);
						for(L2Player p : _team2)
							if(p != null)
								p.setTeam(0, false);

						ThreadPoolManager.getInstance().schedule(new ScheduleEndDuelTask(_duel, status), 5000);
						//TODO: hide hp display of opponents (after adding it.. :p )
						break;
					default:
						_log.info("Error with duel end.");
				}
			}
			catch(Throwable t)
			{
				_log.warning("Can't continue duel" + t);
				t.printStackTrace(System.out);
			}
		}
	}

	public class ScheduleStartDuelTask extends l2open.common.RunnableImpl
	{
		private Duel _duel;

		public ScheduleStartDuelTask(Duel duel)
		{
			_duel = duel;
		}

		public void runImpl()
		{
			try
			{
				// start/continue countdown
				int count = _duel.Countdown();

				if(count == 4)
				{
					// players need to be teleportet first
					//TODO: stadia manager needs a function to return an unused stadium for duels
					// currently only teleports to the same stadium
					_duel.teleportPlayers(-102495, -209023, -3326);

					// give players 20 seconds to complete teleport and get ready (its ought to be 30 on offical..)
					ThreadPoolManager.getInstance().schedule(this, 20000, true);
				}
				else if(count > 0)
					ThreadPoolManager.getInstance().schedule(this, 1000, true);
				else
					_duel.startDuel();
			}
			catch(Throwable t)
			{}
		}
	}

	public class ScheduleEndDuelTask extends l2open.common.RunnableImpl
	{
		private Duel _duel;
		private DuelResultEnum _result;

		public ScheduleEndDuelTask(Duel duel, DuelResultEnum result)
		{
			_duel = duel;
			_result = result;
		}

		public void runImpl()
		{
			try
			{
				_duel.endDuel(_result);
			}
			catch(Throwable t)
			{
				_log.warning("Duel: Can't end duel " + t);
			}
		}
	}

	// ========================================================
	// Method - Private

	/**
	 * Stops all players from attacking.
	 * Used for duel timeout.
	 */
	void stopFighting()
	{
		for(L2Player temp : _team1)
			if(temp != null)
			{
				temp.abortAttack(true, true);
				temp.abortCast(true, true);
				if(temp.getPet() != null)
				{
					temp.getPet().abortAttack(true, true);
					temp.getPet().abortCast(true, true);
				}
			}

		for(L2Player temp : _team2)
			if(temp != null)
			{
				temp.abortAttack(true, true);
				temp.abortCast(true, true);
				if(temp.getPet() != null)
				{
					temp.getPet().abortAttack(true, true);
					temp.getPet().abortCast(true, true);
				}
			}
	}

	/**
	 * Прекращает атаку определенного игрока
	 * @param player you wish to stop the attack
	 */
	public void stopFighting(L2Player player)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		player.sendActionFailed();
	}

	// ========================================================
	// Method - Public

	/**
	 * Check if a player engaged in pvp combat (only for 1on1 duels)
	 * @param sendMessage if we need to send message
	 * @return returns true if a duelist is engaged in Pvp combat
	 */
	static String text_duel_canceled = "The duel was canceled because a duelist engaged in PvP combat.";

	public boolean isDuelistInPvp(boolean sendMessage)
	{
		if(_isPartyDuel)
			return false;

		L2Player d1 = getPlayerA();
		L2Player d2 = getPlayerB();
		if(d1 == null || d2 == null || d1.getPvpFlag() != 0 || d2.getPvpFlag() != 0)
		{
			if(sendMessage && d1 != null)
				d1.sendMessage(text_duel_canceled);
			if(sendMessage && d2 != null)
				d2.sendMessage(text_duel_canceled);
			return true;
		}

		return false;
	}

	/**
	 * Starts the duel
	 */
	public void startDuel()
	{
		List<L2Player> t1 = getPlayers(_team1);
		List<L2Player> t2 = getPlayers(_team2);

		// Начало проверки на наличие дуэли
		String name = null;
		for(L2Player temp : t1)
			if(temp.getDuel() != null)
			{
				name = temp.getName();
				break;
			}

		if(name == null)
			for(L2Player temp : t2)
				if(temp.getDuel() != null)
				{
					name = temp.getName();
					break;
				}

		if(name != null)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD);
			sm.addString(name);

			for(L2Player temp : t1)
				temp.sendPacket(sm);
			for(L2Player temp : t2)
				temp.sendPacket(sm);
			return;
		}
		// Конец проверки на наличие дуэли

		// set isInDuel() state
		for(L2Player temp : t1)
		{
			temp.setDuel(this);
			getPlayerCondition(temp).setDuelState(DuelState.Fighting);
			temp.setTeam(1, false);
			temp.broadcastStatusUpdate();
			broadcastToOppositTeam(temp, new ExDuelUpdateUserInfo(temp));
		}
		for(L2Player temp : t2)
		{
			temp.setDuel(this);
			temp.setTeam(2, false);
			getPlayerCondition(temp).setDuelState(DuelState.Fighting);
			temp.broadcastStatusUpdate();
			broadcastToOppositTeam(temp, new ExDuelUpdateUserInfo(temp));
		}

		for(L2Player temp : t1)
			temp.broadcastRelationChanged();
		for(L2Player temp : t2)
			temp.broadcastRelationChanged();
		// Send duel Start packets
		// TODO: verify: is this done correctly?
		ExDuelReady ready = new ExDuelReady(_isPartyDuel ? 1 : 0);
		ExDuelStart start = new ExDuelStart(_isPartyDuel ? 1 : 0);

		broadcastToTeam(ready, _team1);
		broadcastToTeam(ready, _team2);
		broadcastToTeam(start, _team1);
		broadcastToTeam(start, _team2);

		// play duel music
		PlaySound ps = new PlaySound("B04_S01");
		broadcastToTeam(ps, _team1);
		broadcastToTeam(ps, _team2);

		// start duelling task
		ThreadPoolManager.getInstance().schedule(new ScheduleDuelTask(this), 1000, true);
	}

	/**
	 * Save the current player condition: hp, mp, cp, location
	 *
	 */
	public void savePlayerConditions()
	{
		for(L2Player player : _team1)
			if(player != null)
				_playerConditions.put(player, new PlayerCondition(player, _isPartyDuel));
		for(L2Player player : _team2)
			if(player != null)
				_playerConditions.put(player, new PlayerCondition(player, _isPartyDuel));
	}

	/**
	 * Restore player conditions
	 * @param abnormalDuelEnd was the duel canceled?
	 */
	public void restorePlayerConditions(boolean abnormalDuelEnd)
	{
		// update isInDuel() state for all players
		for(L2Player temp : getPlayers(_team1))
		{
			temp.setDuel(null);
			temp.setTeam(0, false);
		}
		for(L2Player temp : getPlayers(_team2))
		{
			temp.setDuel(null);
			temp.setTeam(0, false);
		}

		// restore player conditions
		for(PlayerCondition pc : _playerConditions.values())
			pc.RestoreCondition(abnormalDuelEnd);
	}

	/**
	 * Returns the remaining time
	 * @return remaining time
	 */
	public int getRemainingTime()
	{
		return (int) (_DuelEndTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
	}

	/**
	 * Get the player that requestet the duel
	 * @return duel requester
	 */
	public L2Player getPlayerA()
	{
		return _team1.get(0);
	}

	/**
	 * Get the player that was challenged
	 * @return challenged player
	 */
	public L2Player getPlayerB()
	{
		return _team2.get(0);
	}

	/**
	 * Returns whether this is a party duel or not
	 * @return is party duel
	 */
	public boolean isPartyDuel()
	{
		return _isPartyDuel;
	}

	public void setFinished(boolean mode)
	{
		_finished = mode;
	}

	public boolean isFinished()
	{
		return _finished;
	}

	/**
	 * teleport all players to the given coordinates
	 * @param x coord
	 * @param y coord
	 * @param z coord
	 */
	public void teleportPlayers(int x, int y, int z)
	{
		int instancedZoneId = 1;
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		InstancedZone il = ils.get(0);
		//assert il != null;
		//TODO: adjust the values if needed... or implement something better (especially using more then 1 arena)
		if(!_isPartyDuel)
			return;

		for(L2Player temp : getPlayers(_team1))
			temp.teleToLocation(il.getTeleportCoords());
		for(L2Player temp : getPlayers(_team2))
			temp.teleToLocation(il.getTeleportCoords());
	}

	public void broadcastToTeam(L2GameServerPacket packet, List<L2Player> team)
	{
		for(L2Player temp : team)
			if(temp != null)
				temp.sendPacket(packet);
	}

	public L2Player getWinner()
	{
		if(!isFinished() || _team1.size() == 0 || _team2.size() == 0)
			return null;
		if(_playerConditions.get(_team1.get(0)).getDuelState() == DuelState.Winner)
			return getPlayerA();
		if(_playerConditions.get(_team2.get(0)).getDuelState() == DuelState.Winner)
			return getPlayerB();
		return null;
	}

	public List<L2Player> getLoosers()
	{
		if(!isFinished() || getPlayerA() == null || getPlayerB() == null)
			return null;
		if(_playerConditions.get(_team1.get(0)).getDuelState() == DuelState.Winner)
			return _team2;
		if(_playerConditions.get(_team2.get(0)).getDuelState() == DuelState.Winner)
			return _team1;
		return null;
	}

	/**
	 * Playback the bow animation for all loosers
	 */
	public void playKneelAnimation()
	{
		List<L2Player> loosers = getLoosers();
		if(loosers == null || loosers.size() == 0)
			return;

		for(L2Player looser : loosers)
			if(looser != null)
				looser.broadcastPacket2(new SocialAction(looser.getObjectId(), SocialAction.BOW));
	}

	/**
	 * Do the countdown and send message to players if necessary
	 * @return current count
	 */
	public int Countdown()
	{
		_countdown--;

		if(_countdown > 3)
			return _countdown;

		// Broadcast countdown to duelists
		SystemMessage sm;
		if(_countdown > 0)
		{
			sm = new SystemMessage(SystemMessage.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS);
			sm.addNumber(_countdown);
		}
		else
			sm = Msg.LET_THE_DUEL_BEGIN;

		broadcastToTeam(sm, _team1);
		broadcastToTeam(sm, _team2);

		return _countdown;
	}

	/**
	 * The duel has reached a state in which it can no longer continue
	 * @param result of duel
	 */
	public void endDuel(DuelResultEnum result)
	{
		//_log.info("Executing duel End task.");
		if(getPlayerA() == null || getPlayerB() == null)
		{
			_log.warning("Duel: Duel end with null players.");
			_playerConditions = null;
			return;
		}

		// inform players of the result
		SystemMessage sm;
		switch(result)
		{
			case Team1Win:
				restorePlayerConditions(false);
				// send SystemMessage
				if(_isPartyDuel)
					sm = new SystemMessage(SystemMessage.S1S_PARTY_HAS_WON_THE_DUEL);
				else
					sm = new SystemMessage(SystemMessage.S1_HAS_WON_THE_DUEL);
				sm.addString(getPlayerA().getName());

				broadcastToTeam(sm, _team1);
				broadcastToTeam(sm, _team2);
				break;
			case Team2Win:
				restorePlayerConditions(false);
				// send SystemMessage
				if(_isPartyDuel)
					sm = new SystemMessage(SystemMessage.S1S_PARTY_HAS_WON_THE_DUEL);
				else
					sm = new SystemMessage(SystemMessage.S1_HAS_WON_THE_DUEL);
				sm.addString(getPlayerB().getName());

				broadcastToTeam(sm, _team1);
				broadcastToTeam(sm, _team2);
				break;
			case Team1Surrender:
				restorePlayerConditions(false);
				// send SystemMessage
				if(_isPartyDuel)
					sm = new SystemMessage(SystemMessage.SINCE_S1S_PARTY_WITHDREW_FROM_THE_DUEL_S1S_PARTY_HAS_WON);
				else
					sm = new SystemMessage(SystemMessage.SINCE_S1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON);
				sm.addString(getPlayerA().getName());
				sm.addString(getPlayerB().getName());

				broadcastToTeam(sm, _team1);
				broadcastToTeam(sm, _team2);
				break;
			case Team2Surrender:
				restorePlayerConditions(false);
				// send SystemMessage
				if(_isPartyDuel)
					sm = new SystemMessage(SystemMessage.SINCE_S1S_PARTY_WITHDREW_FROM_THE_DUEL_S1S_PARTY_HAS_WON);
				else
					sm = new SystemMessage(SystemMessage.SINCE_S1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON);
				sm.addString(getPlayerA().getName());
				sm.addString(getPlayerB().getName());

				broadcastToTeam(sm, _team1);
				broadcastToTeam(sm, _team2);
				break;
			case Canceled:
				restorePlayerConditions(true);
				broadcastToTeam(Msg.THE_DUEL_HAS_ENDED_IN_A_TIE, _team1);
				broadcastToTeam(Msg.THE_DUEL_HAS_ENDED_IN_A_TIE, _team2);
				break;
			case Timeout:
				stopFighting();
				// hp,mp,cp seem to be restored in a timeout too...
				restorePlayerConditions(false);
				broadcastToTeam(Msg.THE_DUEL_HAS_ENDED_IN_A_TIE, _team1);
				broadcastToTeam(Msg.THE_DUEL_HAS_ENDED_IN_A_TIE, _team2);
				break;
		}

		for(L2Player pl : getPlayers(_team1))
			pl.broadcastRelationChanged();
		for(L2Player pl : getPlayers(_team2))
			pl.broadcastRelationChanged();
		// Send end duel packet
		//TODO: verify: is this done correctly?
		ExDuelEnd duelEnd = new ExDuelEnd(_isPartyDuel ? 1 : 0);

		broadcastToTeam(duelEnd, _team1);
		broadcastToTeam(duelEnd, _team2);

		//clean up
		_playerConditions.clear();
		_playerConditions = null;

		_team1.clear();
		_team1 = null;

		_team2.clear();
		_team2 = null;
	}

	/**
	 * Did a situation occur in which the duel has to be ended?
	 * @return DuelResultEnum duel status
	 */
	public DuelResultEnum checkEndDuelCondition()
	{
		L2Player p1, p2;
		// one of the players might leave during duel
		if((p1 = getPlayerA()) == null || (p2 = getPlayerB()) == null)
			return DuelResultEnum.Canceled;

		// got a duel surrender request?
		if(_surrenderRequest != 0)
		{
			if(_surrenderRequest == 1)
				return DuelResultEnum.Team1Surrender;
			return DuelResultEnum.Team2Surrender;
		}
		// duel timed out
		else if(getRemainingTime() <= 0)
			return DuelResultEnum.Timeout;
		// Has a player been declared winner yet?
		if(_playerConditions.get(_team1.get(0)).getDuelState() == DuelState.Winner)
			return DuelResultEnum.Team1Win;
		if(_playerConditions.get(_team2.get(0)).getDuelState() == DuelState.Winner)
			return DuelResultEnum.Team2Win;

		// More end duel conditions for 1on1 duels
		else if(!_isPartyDuel)
		{
			// Duel was interrupted e.g.: player was attacked by mobs / other players
			if(_playerConditions.get(_team1.get(0)).getDuelState() == DuelState.Interrupted || _playerConditions.get(_team2.get(0)).getDuelState() == DuelState.Interrupted)
				return DuelResultEnum.Canceled;

			// Are the players too far apart?
			if(p1.getDistance3D(p2) > 1600)
				return DuelResultEnum.Canceled;

			// Did one of the players engage in PvP combat?
			if(isDuelistInPvp(true))
				return DuelResultEnum.Canceled;

			// is one of the players in a Siege, Peace or PvP zone?
			if(p1.isInPeaceZone() || p2.isInPeaceZone() || p1.isOnSiegeField() || p2.isOnSiegeField() || p1.isInCombatZone() || p2.isInCombatZone() || p1.isInWater() || p2.isInWater() || p1.isFishing() || p2.isFishing()) // и рыбку ловить тоже
				return DuelResultEnum.Canceled;
		}

		return DuelResultEnum.Continue;
	}

	/**
	 * Register a surrender request
	 * @param player that had surrender
	 */
	public void doSurrender(L2Player player)
	{
		// already recived a surrender request
		if(_surrenderRequest != 0)
			return;

		// TODO: Can every party member cancel a party duel? or only the party leaders?
		if(getTeamForPlayer(player) == null)
		{
			_log.warning("Error handling duel surrender request by " + player.getName());
			return;
		}

		if(_team1.contains(player))
		{
			_surrenderRequest = 1;
			for(L2Player temp : _team1)
				setDuelState(temp, DuelState.Dead);

			for(L2Player temp : _team2)
				setDuelState(temp, DuelState.Winner);
		}
		else if(_team2.contains(player))
		{
			_surrenderRequest = 2;
			for(L2Player temp : _team2)
				setDuelState(temp, DuelState.Dead);

			for(L2Player temp : _team1)
				setDuelState(temp, DuelState.Winner);
		}
	}

	/**
	 * This function is called whenever a player was defeated in a duel
	 * @param player tat loose the duel
	 */
	public void onPlayerDefeat(L2Player player)
	{
		// Set player as defeated
		setDuelState(player, DuelState.Dead);

		if(_isPartyDuel)
		{
			boolean teamdefeated = true;
			List<L2Player> team = getTeamForPlayer(player);
			for(L2Player temp : getTeamForPlayer(player))
				if(getDuelState(temp) == DuelState.Fighting)
				{
					teamdefeated = false;
					break;
				}

			if(teamdefeated)
			{
				//Установить поьедителем противоположеную команду
				team = team == _team1 ? _team2 : _team1;
				for(L2Player temp : team)
					setDuelState(temp, DuelState.Winner);
			}
		}
		else
		{
			if(player != getPlayerA() && player != getPlayerB())
				_log.warning("Error in onPlayerDefeat(): player is not part of this 1vs1 duel");

			if(getPlayerA() == player)
				setDuelState(_team2.get(0), DuelState.Winner);
			else
				setDuelState(_team1.get(0), DuelState.Winner);
		}
	}

	/**
	 * This function is called whenever a player leaves a party
	 * @param player leaving player
	 */
	public void onRemoveFromParty(L2Player player)
	{
		// if it isnt a party duel ignore this
		if(!_isPartyDuel)
			return;

		// this player is leaving his party during party duel
		// if hes either playerA or playerB cancel the duel and port the players back
		if(player == _team1.get(0) || player == _team2.get(0))
			for(PlayerCondition pc : _playerConditions.values())
			{
				pc.TeleportBack();
				pc.getPlayer().setDuel(null);
			}
		else
		// teleport the player back & delete his PlayerCondition record
		{
			PlayerCondition pc = _playerConditions.get(player);

			if(pc == null)
			{
				_log.warning("Duel: Error, can't get player condition from list.");
				return;
			}

			pc.TeleportBack();
			_playerConditions.remove(player);

			//Удалить игрока со списков учасников
			if(_team1.contains(player))
				_team1.remove(player);
			else if(_team2.contains(player))
				_team2.remove(player);

			player.setDuel(null);
		}
	}

	/**
	 * Получаем playerCondition
	 * @param player у которого мы хотим получить
	 * @return либо playerCondition либо null
	 */
	public PlayerCondition getPlayerCondition(L2Player player)
	{
		return _playerConditions == null ? null : _playerConditions.get(player);
	}

	/**
	 * Получить состояние дуэли
	 * @param player игрок состояние кого пытаемся получить.
	 * @return состояние дуели
	 */

	public DuelState getDuelState(L2Player player)
	{
		if(_playerConditions == null)
			return null;
		PlayerCondition cond = _playerConditions.get(player);
		return cond == null ? null : cond.getDuelState();
	}

	/**
	 * Устанавливает состояние дуели
	 * @param player кому устанавливать
	 * @param state что устанавливать
	 */
	public void setDuelState(L2Player player, DuelState state)
	{
		if(_playerConditions == null)
			return;
		PlayerCondition cond = _playerConditions.get(player);
		if(cond != null)
			cond.setDuelState(state);
	}

	/**
	 * Broadcasts a packet to the team opposing the given player.
	 * @param player to whos opponents you wish to send packet
	 * @param packet what you wish to send
	 */
	public void broadcastToOppositTeam(L2Player player, L2GameServerPacket packet)
	{
		if(_team1.contains(player))
			broadcastToTeam(packet, _team2);
		else if(_team2.contains(player))
			broadcastToTeam(packet, _team1);
		else
			_log.warning("Duel: Broadcast by player who is not in duel");
	}

	public List<L2Player> getTeamForPlayer(L2Player p)
	{
		if(_team1.contains(p))
			return _team1;
		if(_team2.contains(p))
			return _team2;
		_log.warning("Duel: got request for player team who is not duel participant");
		return null;
	}

	/**
	 * Посколько мы снесли к чертям мэнеджер дуэлей (Нафига оно надо???)
	 * мы должны запускать дуэли как-то по другому.
	 * Статический метод вполне устроит, т.к. все нужное находится в инстансе.
	 * @param playerA бросающий вызов
	 * @param playerB кто бросает вызов
	 * @param partyDuel партийная или нет
	 */
	public static void createDuel(L2Player playerA, L2Player playerB, int partyDuel)
	{
		if(playerA == null || playerB == null || playerA.getDuel() != null || playerB.getDuel() != null)
			return;

		// return if a player has PvPFlag
		String engagedInPvP = "The duel was canceled because a duelist engaged in PvP combat.";
		if(partyDuel == 1)
		{
			boolean playerInPvP = false;
			for(L2Player temp : playerA.getParty().getPartyMembers())
				if(temp.getPvpFlag() != 0)
				{
					playerInPvP = true;
					break;
				}
			if(!playerInPvP)
				for(L2Player temp : playerB.getParty().getPartyMembers())
					if(temp.getPvpFlag() != 0)
					{
						playerInPvP = true;
						break;
					}
			// A player has PvP flag
			if(playerInPvP)
			{
				for(L2Player temp : playerA.getParty().getPartyMembers())
					temp.sendMessage(engagedInPvP);
				for(L2Player temp : playerB.getParty().getPartyMembers())
					temp.sendMessage(engagedInPvP);
				return;
			}
		}
		else if(playerA.getPvpFlag() != 0 || playerB.getPvpFlag() != 0)
		{
			playerA.sendMessage(engagedInPvP);
			playerB.sendMessage(engagedInPvP);
			return;
		}

		//запуск дуэли происходит в ее конструкторе
		new Duel(playerA, playerB, partyDuel == 1);
	}

	public static boolean checkIfCanDuel(L2Player requestor, L2Player target, boolean sendMessage)
	{
		int _noDuelReason = 0;
		if(target.isInCombat())
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
		else if(target.isDead() || target.isAlikeDead() || target.getCurrentHpPercents() < 50 || target.getCurrentMpPercents() < 50 || target.getCurrentCpPercents() < 50)
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1S_HP_OR_MP_IS_BELOW_50_PERCENT;
		else if(target.getDuel() != null)
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_ALREADY_ENGAGED_IN_A_DUEL;
		else if(target.isInOlympiadMode())
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD;
		else if(target.isCursedWeaponEquipped() || target.getKarma() > 0 || target.getPvpFlag() > 0)
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_IN_A_CHAOTIC_STATE;
		else if(target.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
		else if(target.isMounted() || target.isInVehicle())
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_RIDING_A_BOAT_WYVERN_OR_STRIDER;
		else if(target.isFishing())
			_noDuelReason = SystemMessage.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_FISHING;
		else if(target.isInCombatZone() || target.isInPeaceZone() || target.isOnSiegeField() || target.isInWater())
			_noDuelReason = SystemMessage.S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA;
		else if(requestor.getDistance3D(target) > 1200)
			_noDuelReason = SystemMessage.S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY;
		else if(target.getTransformation() != 0)
			_noDuelReason = SystemMessage.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED;
		else if(!ConfigValue.CanStartDuelInReflect && target.getReflectionId() > 0)
			_noDuelReason = SystemMessage.S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA;

		if(sendMessage && _noDuelReason != 0)
			if(requestor != target)
				requestor.sendPacket(new SystemMessage(_noDuelReason).addString(target.getName()));
			else
				requestor.sendPacket(Msg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
		return _noDuelReason == 0;
	}

	private List<L2Player> getPlayers(List<L2Player> team)
	{
		return team;
	}
}