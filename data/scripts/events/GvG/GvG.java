package events.GvG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.*;
import l2open.util.*;
import l2open.util.reference.*;

import javolution.util.FastMap;

/**
 * Глобальный класс предварительного этапа GvG турнира
 * @author pchayka
 */
public class GvG extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(GvG.class.getName());

	public static final Location TEAM1_LOC = new Location(139736, 145832, -15264); // Team location after teleportation
	public static final Location TEAM2_LOC = new Location(139736, 139832, -15264);
	public static final Location RETURN_LOC = new Location(43816, -48232, -822);

	private static boolean _active = false;
	private static boolean _isRegistrationActive = false;

	private static ScheduledFuture<?> _globalTask;
	private static ScheduledFuture<?> _regTask;
	private static ScheduledFuture<?> _countdownTask1;
	private static ScheduledFuture<?> _countdownTask2;
	private static ScheduledFuture<?> _countdownTask3;

	private static List<Long> time2 = new ArrayList<Long>();

	private static List<HardReference<L2Player>> leaderList = new CopyOnWriteArrayList<HardReference<L2Player>>();
	public static GArray<String> _hwidRegistered = new GArray<String>();

	private static List<GvGInstance> gvgInst = new ArrayList<GvGInstance>();

	public static class RegTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			prepare();
		}
	}

	public static class Countdown extends l2open.common.RunnableImpl
	{
		int _timer;

		public Countdown(int timer)
		{
			_timer = timer;
		}

		@Override
		public void runImpl()
		{
			Announcements.getInstance().announceToAll("GvG: До конца приема заявок на турнир осталось " + Integer.toString(_timer) + " мин.");
		}
	}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Event: GvG");
		if(ConfigValue.GvG_Enable)
			initTimer(false);
	}

	@Override
	public void onReload()
	{
		gvgInst.clear();
	}

	@Override
	public void onShutdown()
	{}

	private static void initTimer(boolean new_day)
	{
		time2.clear();
		if(ConfigValue.GvG_StartTimeList[0] == -1)
			return;
		long cur_time = System.currentTimeMillis();
		for(int i=0;i<ConfigValue.GvG_StartTimeList.length;i+=2)
		{
			Calendar ci = Calendar.getInstance();
			if(new_day)
				ci.add(Calendar.HOUR_OF_DAY, 12);
			ci.set(Calendar.HOUR_OF_DAY, ConfigValue.GvG_StartTimeList[i]);
			ci.set(Calendar.MINUTE, ConfigValue.GvG_StartTimeList[i+1]);
			ci.set(Calendar.SECOND, 00);

			long delay = ci.getTimeInMillis();
			if(delay - cur_time > 0)
				time2.add(delay);
			ci = null;
		}
		Collections.sort(time2);
		long delay = 0;
		while(time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0);
		if(_globalTask != null)
			_globalTask.cancel(true);
		if(delay - cur_time > 0)
			_globalTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay-cur_time);
	}

	public static class StartTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(!ConfigValue.GvG_Enable || !_active)
			{
				startTimerTask();
				return;
			}

			if(isPvPEventStarted())
			{
				_log.info("GvG not started: another event is already running");
				startTimerTask();
				return;
			}

			if(TerritorySiege.isInProgress())
			{
				_log.info("GvG not started: TerritorySiege in progress");
				startTimerTask();
				return;
			}

			for(Castle c : CastleManager.getInstance().getCastles().values())
			{
				if(c.getSiege() != null && c.getSiege().isInProgress())
				{
					_log.info("GvG not started: CastleSiege in progress");
					startTimerTask();
					return;
				}
			}

			start();
		}
	}

	private static void start()
	{
		if(!isActive() && canBeStarted() && ConfigValue.GvG_Enable)
		{
			executeTask("events.GvG.GvG", "question", new Object[0], 10000);
			_regTask = ThreadPoolManager.getInstance().schedule(new RegTask(), ConfigValue.GvG_RegTime * 60 * 1000L);
			if(ConfigValue.GvG_RegTime * 60 * 1000L > 2 * 60 * 1000L) //display countdown announcements only when timelimit for registration is more than 3 mins
			{
				if(ConfigValue.GvG_RegTime * 60 * 1000L > 5 * 60 * 1000L)
					_countdownTask3 = ThreadPoolManager.getInstance().schedule(new Countdown(5), ConfigValue.GvG_RegTime * 60 * 1000L - 300 * 1000);

				_countdownTask1 = ThreadPoolManager.getInstance().schedule(new Countdown(2), ConfigValue.GvG_RegTime * 60 * 1000L - 120 * 1000);
				_countdownTask2 = ThreadPoolManager.getInstance().schedule(new Countdown(1), ConfigValue.GvG_RegTime * 60 * 1000L - 60 * 1000);
			}
			ServerVariables.set("GvG", "on");
			_log.info("Event 'GvG' activated.");
			Announcements.getInstance().announceToAll("Регистрация на GvG турнир началась! Community Board(Alt+B) -> Эвенты -> Группа на Группу (Регистрация в эвенте)");
			Announcements.getInstance().announceToAll("Заявки принимаются в течение " + ConfigValue.GvG_RegTime * 60 * 1000L / 60000 + " минут");
			_active = true;
			_isRegistrationActive = true;
		}
	}

	private static boolean canBeStarted()
	{
		for(Castle c : CastleManager.getInstance().getCastles().values())
			if(c.getSiege() != null && c.getSiege().isInProgress())
				return false;
		return true;
	}

	public static boolean isActive()
	{
		return _active;
	}

	public static void startTimerTask()
	{
		long delay = 0;
		long cur_time = System.currentTimeMillis();

		while(time2.size() != 0 && (delay = time2.remove(0)) - cur_time <= 0);
		if(_globalTask != null)
			_globalTask.cancel(true);
		if(delay - cur_time > 0)
			_globalTask = ThreadPoolManager.getInstance().schedule(new StartTask(), delay-cur_time);
		else
			initTimer(true);
	}

	/**
	 * Cancels the event during registration time
	 */
	public static void deactivateEvent()
	{
		if(isActive())
		{
			stopTimers();
			ServerVariables.unset("GvG");
			_log.info("Event 'GvG' canceled.");
			Announcements.getInstance().announceToAll("GvG: Турнир отменен");
			_active = false;
			_isRegistrationActive = false;
			leaderList.clear();
			gvgInst.clear();
			_hwidRegistered.clear();
		}
	}

	/**
	 * Shows groups and their leaders who's currently in registration list
	 */
	public void showStats()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(!isActive())
		{
			player.sendMessage("GvG event is not launched");
			return;
		}

		StringBuilder string = new StringBuilder();
		String refresh = "<button value=\"Refresh\" action=\"bypass -h scripts_events.GvG.GvG:showStats\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
		String start = "<button value=\"Start Now\" action=\"bypass -h scripts_events.GvG.GvG:startNow\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
		int i = 0;

		if(!leaderList.isEmpty())
		{
			for(L2Player leader : HardReferences.unwrap(leaderList))
			{
				if(!leader.isInParty())
					continue;
				string.append("*").append(leader.getName()).append("*").append(" | group members: ").append(leader.getParty().getMemberCount()).append("\n\n");
				i++;
			}
			show("There are " + i + " group leaders who registered for the event:\n\n" + string + "\n\n" + refresh + "\n\n" + start, player, null);
		}
		else
			show("There are no participants at the time\n\n" + refresh, player, null);
	}

	public void startNow()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(!isActive() || !canBeStarted())
		{
			player.sendMessage("GvG event is not launched");
			return;
		}

		prepare();
	}

	public static void question()
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			if(player != null && player.getLevel() >= ConfigValue.GvG_MinLevel && player.getLevel() <= ConfigValue.GvG_MaxLevel && player.getReflection().getId() <= 0 && !player.isInOlympiadMode() && !player.isDead())
				if(!player.isInZone(ZoneType.epic) && !player.isFlying() && player.getVar("jailed") == null)
					player.scriptRequest(new CustomMessage("scripts.events.GvG.AskPlayer", player).toString(), "events.GvG.GvG:addGroup", new Object[0]);
	}

	/**
	 * Handles the group applications and apply restrictions
	 */
	public void addGroup()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!_isRegistrationActive)
		{
			player.sendMessage("GvG турнир неактивен.");
			return;
		}

		if(ConfigValue.GvG_HWID && _hwidRegistered.contains(player.getHWIDs()))
		{
			player.sendMessage("С вашего ПК уже зарегистрировались на GvG турнир");
			return;
		}

		if(leaderList.contains(player.getRef()))
		{
			player.sendMessage("Вы уже зарегистрировались на GvG турнир");
			return;
		}

		if(!player.isInParty())
		{
			player.sendMessage("Вы не состоите в группе и не можете подать заявку");
			return;
		}

		if(!player.getParty().isLeader(player))
		{
			player.sendMessage("Только лидер группы может подать заявку");
			return;
		}
		if(player.getParty().isInCommandChannel())
		{
			player.sendMessage("Чтобы участвовать в турнире вы должны покинуть Командный Канал");
			return;
		}

		if(leaderList.size() >= ConfigValue.GvG_GroupLimit)
		{
			player.sendMessage("Достигнут лимит количества групп для участия в турнире. Заявка отклонена");
			return;
		}

		String[] abuseReason = {
				"не находится в игре",
				"не находится в группе",
				"состоит в неполной группе. Минимальное кол-во членов группы - 6.",
				"не является лидером группы, подававшей заявку",
				"не соответствует требованиям уровней для турнира",
				"использует ездовое животное, что противоречит требованиям турнира",
				"находится в дуэли, что противоречит требованиям турнира",
				"принимает участие в другом эвенте, что противоречит требованиям турнира",
				"находится в списке ожидания Олимпиады или принимает участие в ней",
				"находится в состоянии телепортации, что противоречит требованиям турнира",
				"находится в Dimensional Rift, что противоречит требованиям турнира",
				"обладает Проклятым Оружием, что противоречит требованиям турнира",
				"не находится в мирной зоне",
				"находится в режиме обозревания",
				"во время торговли на эвент нельзя",
				"в тюрьме на эвент нельзя", };

		for(L2Player eachmember : player.getParty().getPartyMembers())
		{
			int abuseId = checkPlayer(eachmember, false);
			if(abuseId != 0)
			{
				player.sendMessage("Игрок " + eachmember.getName() + " " + abuseReason[abuseId - 1]);
				return;
			}
		}

		_hwidRegistered.add(player.getHWIDs());
		leaderList.add(player.getRef());
		player.getParty().broadcastMessageToPartyMembers("Ваша группа внесена в список ожидания. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях до начала турнира. Полный список требований турнира в Community Board (Alt+B)");
	}

	private static void stopTimers()
	{
		if(_regTask != null)
		{
			_regTask.cancel(false);
			_regTask = null;
		}
		if(_countdownTask1 != null)
		{
			_countdownTask1.cancel(false);
			_countdownTask1 = null;
		}
		if(_countdownTask2 != null)
		{
			_countdownTask2.cancel(false);
			_countdownTask2 = null;
		}
		if(_countdownTask3 != null)
		{
			_countdownTask3.cancel(false);
			_countdownTask3 = null;
		}
	}

	private static void prepare()
	{
		checkPlayers();
		shuffleGroups();

		if(isActive())
		{
			stopTimers();
			ServerVariables.unset("GvG");
			//_active = false;
			_isRegistrationActive = false;
		}

		if(leaderList.size() < 2)
		{
			leaderList.clear();
			_hwidRegistered.clear();
			Announcements.getInstance().announceToAll("GvG: Турнир отменен из-за недостатка участников");
			_active = false;
			startTimerTask();
			return;
		}

		Announcements.getInstance().announceToAll("GvG: Прием заявок завершен. Запуск турнира.");
		go();
	}

	/**
	 * @param player
	 * @param doCheckLeadership
	 * @return
	 * Handles all limits for every group member. Called 2 times: when registering group and before sending it to the instance
	 */
	private static int checkPlayer(L2Player player, boolean doCheckLeadership)
	{
		if(!player.isOnline())
			return 1;

		if(!player.isInParty())
			return 2;

		if(doCheckLeadership && (player.getParty() == null || !player.getParty().isLeader(player)))
			return 4;

		if(player.getParty() == null || player.getParty().getMemberCount() < ConfigValue.GvG_MinPatyMembers)
			return 3;

		if(player.getLevel() < ConfigValue.GvG_MinLevel || player.getLevel() > ConfigValue.GvG_MaxLevel)
			return 5;

		if(player.isMounted())
			return 6;

		if(player.isInDuel())
			return 7;

		if(player.getTeam() != 0)
			return 8;

		if(player.getOlympiadGame() != null || player.isInZoneOlympiad() || Olympiad.isRegistered(player))
			return 9;

		if(player.isTeleporting())
			return 10;

		if(player.getParty().isInDimensionalRift())
			return 11;

		if(player.isCursedWeaponEquipped())
			return 12;

		if(!player.isInPeaceZone())
			return 13;

		if(player.inObserverMode())
			return 14;
		if(player.isInOfflineMode())
            return 13;
        if(player.isInStoreMode())
            return 15;
		if(player.getVar("jailed") != null) 
			return 16; 
		return 0;
	}

	/**
	 * @return
	 * Shuffles groups to separate them in two lists of equals size
	 */
	private static void shuffleGroups()
	{
		if(leaderList.size() % 2 != 0) // If there are odd quantity of groups in the list we should remove one of them to make it even
		{
			int rndindex = Rnd.get(leaderList.size());
			L2Player expelled = leaderList.remove(rndindex).get();
			_hwidRegistered.remove(expelled.getHWIDs());
			if(expelled != null)
				expelled.sendMessage("При формировании списка участников турнира ваша группа была отсеяна. Приносим извинения, попробуйте в следующий раз.");
		}

		//Перемешиваем список
		for(int i = 0; i < leaderList.size(); i++)
		{
			int rndindex = Rnd.get(leaderList.size());
			leaderList.set(i, leaderList.set(rndindex, leaderList.get(i)));
		}
	}

	private static void checkPlayers()
	{
		for(L2Player player : HardReferences.unwrap(leaderList))
		{
			if(checkPlayer(player, true) != 0)
			{
				leaderList.remove(player.getRef());
				_hwidRegistered.remove(player.getHWIDs());
				continue;
			}

			for(L2Player partymember : player.getParty().getPartyMembers())
			{
				if(checkPlayer(partymember, false) != 0)
				{
					player.sendMessage("Ваша группа была дисквалифицирована и снята с участия в турнире так как один или более членов группы нарушил условия участия");
					leaderList.remove(player.getRef());
					_hwidRegistered.remove(player.getHWIDs());
					break;
				}
			}
		}
	}

	public static void updateWinner(L2Player winner)
	{
		/*Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO event_data(charId, score) VALUES (?,1) ON DUPLICATE KEY UPDATE score=score+1");
			statement.setInt(1, winner.getObjectId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}*/
	}

	private static void go()
	{
		int instancedZoneId = 504;
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(instancedZoneId);
		InstancedZoneManager izm = InstancedZoneManager.getInstance();
		if(izs == null)
			return;
		InstancedZone iz = izs.get(0);
		if(iz == null)
		{
			_log.warning("GvG: InstanceZone : " + instancedZoneId + " not found!");
			return;
		}

		for(int i = 0; i < leaderList.size(); i += 2)
		{
			L2Player team1Leader = leaderList.get(i).get();
			L2Player team2Leader = leaderList.get(i + 1).get();
			
			GvGInstance r = new GvGInstance(iz);
			r.setTeam1(team1Leader.getParty());
			r.setTeam2(team2Leader.getParty());
			//r.init(iz);
			r.setReturnLoc(GvG.RETURN_LOC);
			
			for(InstancedZone is : izs.values())
				r.FillDoors(is.getDoors());
		
			team1Leader.getParty().setReflection(r);
			team2Leader.getParty().setReflection(r);
			
			for(L2Player member : team1Leader.getParty().getPartyMembers())
			{
				Functions.unRide(member);
				Functions.unSummonPet(member, true);
				member.setTransformation(0);
				dispelBuffs(member);

				member.setRestartPoint(1);
				member.setTeam(1, true);
				member.setVar("backCoords", member.getLoc().toXYZString());
				member.setVar("reflection", ""+r.getId());
				member.setReflection(r);
				member.teleToLocation(Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, r.getGeoIndex()));
			}

			for(L2Player member : team2Leader.getParty().getPartyMembers())
			{
				Functions.unRide(member);
				Functions.unSummonPet(member, true);
				member.setTransformation(0);
				dispelBuffs(member);

				member.setRestartPoint(1);
				member.setTeam(2, true);
				member.setVar("backCoords", member.getLoc().toXYZString());
				member.setVar("reflection", ""+r.getId());
				member.setReflection(r);
				member.teleToLocation(Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, r.getGeoIndex()));
			}
			gvgInst.add(r);
			r.start();
		}

		leaderList.clear();
		_hwidRegistered.clear();
		_log.info("GvG: Event started successfuly.");
	}

	public static void dispelBuffs(L2Player player)
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
	}

	public static synchronized void deleteArena(GvGInstance inst)
	{
		gvgInst.remove(inst);
	}

	public void OnPlayerExit(L2Player player)
	{
		if(isActive())
		{
			/*if(_regList.contains(player.getObjectId()) || _playerList.contains(player.getObjectId()))
				removeReg(player);
			if(player != null && player.isPlayer() && player.getTeam() > 0)
				for(GvGInstance fca : gvgInst)
					if(fca != null)
						fca.OnPlayerExit(player);*/
		}
	}

	public void onPlayerTeleport(L2Character player, Location loc)
	{
		if(isActive())
		{
			if(player != null && player.isPlayer() && player.getTeam() > 0)
				for(GvGInstance fca : gvgInst)
					fca.onPlayerTeleport(player, loc);
		}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(isActive())
		{
			if((self != null && self.isPlayer()) || (self != null && (self.getNpcId() == 18822 || self.getNpcId() == 25655)))
				for(GvGInstance fca : gvgInst)
					fca.OnDie(self, killer);
		}
	}

	public static void activateEvent()
	{
		if(!isActive() && canBeStarted() && ConfigValue.GvG_Enable)
		{
			executeTask("events.GvG.GvG", "question", new Object[0], 10000);
			_regTask = ThreadPoolManager.getInstance().schedule(new RegTask(), ConfigValue.GvG_RegTime * 60 * 1000L);
			if(ConfigValue.GvG_RegTime * 60 * 1000L > 2 * 60 * 1000L) //display countdown announcements only when timelimit for registration is more than 3 mins
			{
				if(ConfigValue.GvG_RegTime * 60 * 1000L > 5 * 60 * 1000L)
					_countdownTask3 = ThreadPoolManager.getInstance().schedule(new Countdown(5), ConfigValue.GvG_RegTime * 60 * 1000L - 300 * 1000);

				_countdownTask1 = ThreadPoolManager.getInstance().schedule(new Countdown(2), ConfigValue.GvG_RegTime * 60 * 1000L - 120 * 1000);
				_countdownTask2 = ThreadPoolManager.getInstance().schedule(new Countdown(1), ConfigValue.GvG_RegTime * 60 * 1000L - 60 * 1000);
			}
			ServerVariables.set("GvG", "on");
			_log.info("Event 'GvG' activated.");
			Announcements.getInstance().announceToAll("Регистрация на GvG турнир началась! Community Board(Alt+B) -> Эвенты -> Группа на Группу (Регистрация в эвенте)");
			Announcements.getInstance().announceToAll("Заявки принимаются в течение " + ConfigValue.GvG_RegTime * 60 * 1000L / 60000 + " минут");
			_active = true;
			_isRegistrationActive = true;
		}
	}
}