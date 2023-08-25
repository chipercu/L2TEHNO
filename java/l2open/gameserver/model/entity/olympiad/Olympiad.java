package l2open.gameserver.model.entity.olympiad;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.OlympiadHistoryManager;
import l2open.gameserver.instancemanager.PlayerRewardManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2OlympiadManagerInstance;
import l2open.gameserver.serverpackets.ExOlympiadMatchList;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Crontab;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Location;
import l2open.util.MultiValueIntegerMap;
import l2open.util.Util;

public class Olympiad
{
	public static final Logger _log = Logger.getLogger(Olympiad.class.getName());
	public static Map<Integer, StatsSet> _nobles = new ConcurrentHashMap<Integer, StatsSet>();
	public static Map<Integer, Integer> _noblesRank;
	public static GArray<StatsSet> _heroesToBe;
	public static GArray<String> _hwidRegistered = new GArray<String>();
	public static GCSArray<Integer> _nonClassBasedRegisters = new GCSArray<Integer>();
	public static MultiValueIntegerMap _classBasedRegisters = new MultiValueIntegerMap();
	public static GCSArray<Integer> _teamRandomBasedRegisters = new GCSArray<Integer>();
	public static MultiValueIntegerMap _teamBasedRegisters = new MultiValueIntegerMap();
	private static final int WEEKLY_POINTS = 10;
	public static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
	public static final String OLYMPIAD_LOAD_NOBLES = "SELECT * FROM `olympiad_nobles`";
	public static final String OLYMPIAD_SAVE_NOBLES = "REPLACE INTO `olympiad_nobles` (`char_id`, `class_id`, `char_name`, `olympiad_points`, `olympiad_points_past`, `olympiad_points_past_static`, `competitions_done`, `competitions_win`, `competitions_loose`, `noneclass_competitions`, `class_competitions`, `team_competitions`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String OLYMPIAD_GET_HEROS = "SELECT `char_id`, `char_name` FROM `olympiad_nobles` WHERE `class_id` = ? AND `competitions_done` >= 15 AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String OLYMPIAD_GET_HEROS_SOULHOUND = "SELECT `char_id`, `char_name` FROM `olympiad_nobles` WHERE `class_id` IN (?, 133) AND `competitions_done` >= 15 AND `competitions_win` > 0 ORDER BY `olympiad_points` DESC, `competitions_win` DESC, `competitions_done` DESC";
	public static final String GET_ALL_CLASSIFIED_NOBLESS = "SELECT `char_id` FROM `olympiad_nobles` ORDER BY olympiad_points_past_static DESC";
	public static final String GET_EACH_CLASS_LEADER = "SELECT `char_name` FROM `olympiad_nobles` WHERE `class_id` = ? AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	public static final String GET_EACH_CLASS_LEADER_SOULHOUND = "SELECT `char_name` FROM `olympiad_nobles` WHERE `class_id` IN (?, 133) AND `olympiad_points_past_static` != 0 ORDER BY `olympiad_points_past_static` DESC LIMIT 10";
	public static final String OLYMPIAD_CALCULATE_LAST_PERIOD = "UPDATE `olympiad_nobles` SET `olympiad_points_past` = `olympiad_points`, `olympiad_points_past_static` = `olympiad_points` WHERE `competitions_done` >= 15";
	public static final String OLYMPIAD_CLEANUP_NOBLES = "UPDATE `olympiad_nobles` SET `olympiad_points` = "+ConfigValue.OlympiadSetStartPoint+", `competitions_done` = 0, `competitions_win` = 0, `competitions_loose` = 0";
	public static final String OLYMPIAD_CLEANUP_COMPETITIONS = "UPDATE `olympiad_nobles` SET `noneclass_competitions` = 0, `class_competitions` = 0, `team_competitions` = 0";
	public static final String CHAR_ID = "char_id";
	public static final String CLASS_ID = "class_id";
	public static final String CHAR_NAME = "char_name";
	public static final String POINTS = "olympiad_points";
	public static final String POINTS_PAST = "olympiad_points_past";
	public static final String POINTS_PAST_STATIC = "olympiad_points_past_static";
	public static final String COMP_DONE = "competitions_done";
	public static final String COMP_WIN = "competitions_win";
	public static final String COMP_LOOSE = "competitions_loose";
	public static final String NONECLASS_COMP_COUNT = "noneclass_competitions";
	public static final String CLASS_COMP_COUNT = "class_competitions";
	public static final String TEAM_COMP_COUNT = "team_competitions";
	public static long _olympiadEnd;
	public static long _validationEnd;
	public static int _period;
	public static long _nextWeeklyChange;
	public static int _currentCycle;
	public static long _compEnd;
	private static Calendar _compStart;
	public static boolean _inCompPeriod;
	public static boolean _isOlympiadEnd;
	public static int _currentRound;
	public static ScheduledFuture<?> _scheduledOlympiadEnd;
	public static ScheduledFuture<?> _scheduledManagerTask;
	public static ScheduledFuture<?> _scheduledWeeklyTask;
	public static ScheduledFuture<?> _scheduledValdationTask;
	public static Stadia[] STADIUMS;
	public static OlympiadGame[] _games;
	public static OlympiadManager _manager;
	private static GArray<L2OlympiadManagerInstance> _npcs = new GArray<L2OlympiadManagerInstance>();
	public static long _fake_olympiad_start;
	public static long _fake_olympiad_end;
	public static long _custom_olympiad_start;
	public static long _custom_olympiad_end;
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	public static void load()
	{
		_games = new OlympiadGame[ConfigValue.OlympiadStadiasCount];
		_currentCycle = ServerVariables.getInt("Olympiad_CurrentCycle", 1);
		_period = ServerVariables.getInt("Olympiad_Period", 0);
		_olympiadEnd = ServerVariables.getLong("Olympiad_End", 0);
		_validationEnd = ServerVariables.getLong("Olympiad_ValdationEnd", 0) > System.currentTimeMillis() ? ServerVariables.getLong("Olympiad_ValdationEnd", 0) : 10;
		_nextWeeklyChange = ServerVariables.getLong("Olympiad_NextWeeklyChange", 0);
		_currentRound = ServerVariables.getInt("Olympiad_Round", 1);

		initStadiums();

		OlympiadHistoryManager.getInstance();

		switch(_period)
		{
			case 0:
				if(_olympiadEnd == 0 || _olympiadEnd < Calendar.getInstance().getTimeInMillis())
					OlympiadDatabase.setNewOlympiadEnd();
				else
					_isOlympiadEnd = false;
				break;
			case 1:
				_isOlympiadEnd = true;
				_scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), getMillisToValidationEnd());
				break;
			default:
				_log.log(Level.WARNING, "Olympiad System: Omg something went wrong in loading!! Period = {0}", _period);
				return;
		}

		_log.info("Olympiad System: Loading Olympiad System....");
		if(_period == 0)
			_log.info("Olympiad System: Currently in Olympiad Period");
		else
			_log.info("Olympiad System: Currently in Validation Period");

		long milliToEnd;
		if(_period == 0)
			milliToEnd = getMillisToOlympiadEnd();
		else
			milliToEnd = getMillisToValidationEnd();
		double numSecs = milliToEnd / 1000L % 60;
		double countDown = (milliToEnd / 1000 - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);

		_log.info("Olympiad System: In " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		if(_period == 0)
		{
			_log.info("Olympiad System: Next Weekly Change is in....");

			milliToEnd = getMillisToWeekChange();

			double numSecs2 = milliToEnd / 1000L % 60;
			double countDown2 = (milliToEnd / 1000 - numSecs2) / 60;
			int numMins2 = (int) Math.floor(countDown2 % 60);
			countDown2 = (countDown2 - numMins2) / 60;
			int numHours2 = (int) Math.floor(countDown2 % 24);
			int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);
			_log.info("Olympiad System: In " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");
		}
		_log.info("Olympiad System: Loaded " + _nobles.size() + " Noblesses");
		if(_period == 0)
			init();

		if(ConfigValue.AltFakeOlyStartTime > -1)
		{
			Calendar call = Calendar.getInstance();
			call.set(Calendar.HOUR_OF_DAY, ConfigValue.AltFakeOlyStartTime);
			call.set(Calendar.MINUTE, 00);
			call.set(Calendar.SECOND, 00);
			call.set(Calendar.MILLISECOND, 00);

			_fake_olympiad_start = call.getTimeInMillis();
			_fake_olympiad_end = _fake_olympiad_start + ConfigValue.AltFakeOlyEndTime*60*60*1000L;

			ThreadPoolManager.getInstance().schedule(new FakeStartTask(), _fake_olympiad_start-Calendar.getInstance().getTimeInMillis());
		}

		_custom_olympiad_start = new Crontab(ConfigValue.CustomOlyCron).timeNextUsage(System.currentTimeMillis());
		_custom_olympiad_end = _custom_olympiad_start + ConfigValue.CustomOlyEndTime*60*1000L;

		_log.info("Olympiad System:["+(ConfigValue.CustomOlyEnable ? "Enable" : "Disable")+"] Start custom reward " + SIMPLE_FORMAT.format(_custom_olympiad_start) + ".");
		_log.info("Olympiad System: End custom reward " + SIMPLE_FORMAT.format(_custom_olympiad_end) + ".");
	}

	private static void initStadiums()
	{
		STADIUMS = new Stadia[ConfigValue.OlympiadStadiums.length];
		for(int i=0;i<ConfigValue.OlympiadStadiums.length;i++)
			STADIUMS[i] = new Stadia(ConfigValue.OlympiadStadiums[i]);

	}

	public static void init()
	{
		if(_period == 1)
			return;

		_compStart = Calendar.getInstance();
		_compStart.set(Calendar.HOUR_OF_DAY, ConfigValue.AltOlyStartTime);
		_compStart.set(Calendar.MINUTE, ConfigValue.AltOlyMin);
		_compStart.set(Calendar.SECOND, 00);
		_compStart.set(Calendar.MILLISECOND, 00);
		_compEnd = _compStart.getTimeInMillis() + ConfigValue.AltOlyCPeriod;

		if(_scheduledOlympiadEnd != null)
			_scheduledOlympiadEnd.cancel(true);
		_scheduledOlympiadEnd = ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), getMillisToOlympiadEnd());

		updateCompStatus();

		if(_scheduledWeeklyTask != null)
			_scheduledWeeklyTask.cancel(true);
		_scheduledWeeklyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WeeklyTask(), getMillisToWeekChange(), ConfigValue.AltOlyWPeriod);
	}

	public static boolean isFakeOly()
	{
		long time = System.currentTimeMillis();
		return (ConfigValue.FakeOlyEnable && _fake_olympiad_start < time && _fake_olympiad_end > time || ConfigValue.FakeOlyForceEnable) && !isProgress(null);
	}

	public static boolean isCustomReward()
	{
		long time = System.currentTimeMillis();
		return (ConfigValue.CustomOlyEnable && _custom_olympiad_start < time && _custom_olympiad_end > time || ConfigValue.CustomOlyForceEnable);
	}

	public static synchronized boolean registerNoble(L2Player noble, CompType type)
	{
		if(noble.isEventReg())
			return false;
		else if(!isFakeOly())
		{
			if(ConfigValue.MaxCompForAll <= getAllCompetitionCount(noble.getObjectId()))
			{
				noble.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_FOR_ALL_COMP_70);
				return false;
			}
			else if(getMaxForCompType(type) <= getCompetitionCount(noble.getObjectId(), type))
			{
				noble.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_IN_THE_60_INDEPENDENT_FROM_THE_CLASS_OF_EVENTS);
				return false;
			}
			else if(!isProgress(noble))
				return false;
		}
		else if(noble.isCursedWeaponEquipped())
		{
			noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.Cursed", noble));
			return false;
		}
		else if(noble.getClassId().getLevel() != 4 || noble.isCombatFlagEquipped() || noble.isTerritoryFlagEquipped())
			return false;
		else if(noble.getVar("jailed") != null)
			return false;
		StatsSet nobleInfo = _nobles.get(noble.getObjectId());

		if(nobleInfo == null || !noble.isNoble())
		{
			noble.sendPacket(Msg.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			return false;
		}
		//else if(noble.isSubClassActive()/* && !ConfigValue.Multi_Enable*/)
		else if(noble.getClassId() == ClassId.judicator || noble.getBaseClassId() != noble.getClassId().getId() && !ConfigValue.Multi_Enable)
		{
			noble.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			return false;
		}
		else if(!isFakeOly() && getNoblePoints(noble.getObjectId()) < 1)
		{
			noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.LessPoints", noble));
			return false;
		}
		else if(noble.getOlympiadGame() != null)
		{
			noble.sendPacket(Msg.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT.addName(noble));
			return false;
		}
		else if(isHWIDRegistered(noble))
		{
			noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.HWID", noble));
			return false;
		}

		int classId = nobleInfo.getInteger("class_id");

		switch(type)
		{
			case CLASSED:
			{
				if(_classBasedRegisters.containsValue(noble.getObjectId()))
				{
					noble.sendPacket(Msg.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS);
					return false;
				}
				setHwid(noble);
				_classBasedRegisters.put(classId, noble.getObjectId());
				noble.sendPacket(Msg.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
				break;
			}
			case NON_CLASSED:
			{
				if(_nonClassBasedRegisters.contains(noble.getObjectId()))
				{
					noble.sendPacket(Msg.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME);
					return false;
				}

				setHwid(noble);
				_nonClassBasedRegisters.add(noble.getObjectId());
				noble.sendPacket(Msg.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES);
				break;
			}
			case TEAM_RANDOM:
			{
				if(_teamRandomBasedRegisters.contains(noble.getObjectId()))
				{
					noble.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_MATCH_EVENT).addName(noble));
					return false;
				}

				setHwid(noble);
				_teamRandomBasedRegisters.add(noble.getObjectId());
				noble.sendPacket(Msg.YOU_HAVE_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_TEAM_MATCH_EVENT);
				break;
			}
			case TEAM:
			{
				if(_teamBasedRegisters.containsValue(noble.getObjectId()))
				{
					noble.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_MATCH_EVENT).addName(noble));
					return false;
				}

				L2Party party = noble.getParty();
				if(party == null || !party.isLeader(noble))
				{
					noble.sendPacket(new SystemMessage(SystemMessage.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH));
					return false;
				}
				if(party.getMemberCount() != 3)
				{
					noble.sendPacket(new SystemMessage(SystemMessage.THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM));
					return false;
				}

				for(L2Player member : party.getPartyMembers())
				{
					if(!member.isNoble())
					{
						noble.sendPacket(Msg.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
						return false;
					}
					else if(member.isEventReg() || member.isCombatFlagEquipped() || member.isTerritoryFlagEquipped())
						return false;
					else if(isRegistered(member))
					{
						noble.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_MATCH_EVENT).addName(member));
						member.sendPacket(new SystemMessage(SystemMessage.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_MATCH_EVENT).addName(member));
						return false;
					}
					else if(isHWIDRegistered(member))
					{
						noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.HWID", noble));
						return false;
					}
					else if(member.getOlympiadGame() != null)
					{
						noble.sendPacket(Msg.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT.addName(member));
						return false;
					}
					//--
					else if(!isFakeOly())
					{
						if(ConfigValue.MaxCompForAll <= getAllCompetitionCount(member.getObjectId()))
						{
							noble.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_FOR_ALL_COMP_70);
							member.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_FOR_ALL_COMP_70);
							return false;
						}
						else if(getMaxForCompType(type) <= getCompetitionCount(member.getObjectId(), type))
						{
							noble.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_IN_THE_60_INDEPENDENT_FROM_THE_CLASS_OF_EVENTS);
							member.sendPacket(Msg.FOR_ONE_WEEK_YOU_CAN_TAKE_PART_IN_THE_60_INDEPENDENT_FROM_THE_CLASS_OF_EVENTS);
							return false;
						}
					}
					else if(member.isCursedWeaponEquipped())
					{
						noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.Cursed", noble));
						member.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.Cursed", noble));
						return false;
					}
					//else if(member.isSubClassActive()/* && !ConfigValue.Multi_Enable*/)
					else if(noble.getBaseClassId() != noble.getClassId().getId() && !ConfigValue.Multi_Enable)
					{
						noble.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
						member.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
						return false;
					}
					else if(!isFakeOly() && getNoblePoints(member.getObjectId()) < 3)
					{
						noble.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.LessPoints", noble));
						member.sendMessage(new CustomMessage("l2open.gameserver.model.entity.Olympiad.LessPoints", noble));
						return false;
					}
				}

				for(L2Player member : party.getPartyMembers())
				{
					setHwid(member);
					noble.sendPacket(Msg.YOU_HAVE_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_NON_CLASS_LIMITED_TEAM_MATCH_EVENT);
				}

				_teamBasedRegisters.putAll(noble.getObjectId(), party.getPartyMembersObjIds());
				break;
			}
		}
		return true;
	}

	public static synchronized void logoutPlayer(L2Player player)
	{
		OlympiadGame game = player.getOlympiadGame();
		if(game != null)
			try
			{
				game.endGame(5000, true, player);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		removeRegistration(player.getObjectId(), false);
	}

	public static synchronized boolean unRegisterNoble(L2Player noble)
	{
		if((!inCompPeriod() || isOlympiadEnd()) && !isFakeOly())
		{
			noble.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}

		if(!noble.isNoble())
		{
			noble.sendPacket(Msg.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			return false;
		}
		L2Party party = noble.getParty();
		if(isRegisteredTeam(noble) && party != null && !party.isLeader(noble))
			return false;
		

		OlympiadGame game = noble.getOlympiadGame();
		if(game != null)
		{
			try
			{
				game.endGame(5000, true, noble);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if(!isRegistered(noble))
		{
			noble.sendPacket(Msg.YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME);
			return false;
		}
		removeRegistration(noble.getObjectId(), true);

		noble.sendPacket(Msg.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
		return true;
	}

	private static synchronized void updateCompStatus()
	{
		long milliToStart = getMillisToCompBegin();
		double numSecs = milliToStart / 1000 % 60;
		double countDown = (milliToStart / 1000 - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);

		_log.info("Olympiad System: Competition Period Starts in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
		_log.info("Olympiad System: Event starts/started: " + _compStart.getTime());

		ThreadPoolManager.getInstance().schedule(new CompStartTask(), getMillisToCompBegin());
	}

	private static long getMillisToOlympiadEnd()
	{
		return _olympiadEnd - System.currentTimeMillis();
	}

	static long getMillisToValidationEnd()
	{
		if(_validationEnd > System.currentTimeMillis())
			return _validationEnd - System.currentTimeMillis();
		return 10;
	}

	public static boolean isOlympiadEnd()
	{
		return _isOlympiadEnd;
	}

	public static boolean inCompPeriod()
	{
		return _inCompPeriod;
	}

	private static long getMillisToCompBegin()
	{
		if(_compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && _compEnd > Calendar.getInstance().getTimeInMillis())
			return 10;
		if(_compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
			return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		return setNewCompBegin();
	}

	private static long setNewCompBegin()
	{
		_compStart = Calendar.getInstance();
		_compStart.set(Calendar.HOUR_OF_DAY, ConfigValue.AltOlyStartTime);
		_compStart.set(Calendar.MINUTE, ConfigValue.AltOlyMin);
		_compStart.set(Calendar.SECOND, 00);
		_compStart.set(Calendar.MILLISECOND, 00);
		_compStart.add(Calendar.HOUR_OF_DAY, 24);
		_compEnd = _compStart.getTimeInMillis() + ConfigValue.AltOlyCPeriod;

		_log.info("Olympiad System: New Schedule @ " + _compStart.getTime());

		return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
	}

	public static long getMillisToCompEnd()
	{
		return _compEnd - Calendar.getInstance().getTimeInMillis();
	}

	public static long getMillisToWeekChange()
	{
		if(_nextWeeklyChange > Calendar.getInstance().getTimeInMillis())
			return _nextWeeklyChange - Calendar.getInstance().getTimeInMillis();
		return 10;
	}

	public static synchronized void addWeeklyPoints()
	{
		if(_period == 1)
			return;
		for(StatsSet nobleInfo : _nobles.values())
			if(nobleInfo != null)
				nobleInfo.set(POINTS, nobleInfo.getInteger(POINTS) + WEEKLY_POINTS);
	}

	public static int getCurrentCycle()
	{
		return _currentCycle;
	}

	public static synchronized void addSpectator(OlympiadGame game, L2Player spectator)
	{
		if(spectator.getVar("jailed") != null || spectator.isCombatFlagEquipped() || spectator.isTerritoryFlagEquipped() || spectator.getDuel() != null || spectator.getTeam() != 0 || spectator.isFlying() || spectator.isCursedWeaponEquipped() || spectator.isMounted() || !spectator.isInPeaceZone())
			return;
		else if(spectator.getOlympiadGame() != null || isRegisteredInComp(spectator))
		{
			spectator.sendPacket(Msg.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
			return;
		}
		else if(game.getStatus() == BattleStatus.Begining || game.getStatus() == BattleStatus.Begin_Countdown)
		{
			spectator.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}
		else if(spectator.getPet() != null)
			spectator.getPet().unSummon();

		int[] c = STADIUMS[game.getOllyId()].getZone().getSpawns().get(2);
		spectator.enterObserverMode(new Location(c[0], c[1], c[2]), game);
		game.addSpectator(spectator);
	}

	public static synchronized void removeSpectator(OlympiadGame game, L2Player spectator)
	{
		if(_manager == null || game == null)
			return;
		game.removeSpectator(spectator);
	}

	public static OlympiadGame getGameBySpectator(L2Player player)
	{
		if(_games != null)
			for(OlympiadGame game : _games)
				if(game != null && game.containsSpectator(player))
					return game;
		return null;
	}

	public static OlympiadGame[] getOlympiadGames()
	{
		return _games;
	}

	public static synchronized int[] getWaitingList()
	{
		if(!inCompPeriod() && !isFakeOly())
			return null;

		int[] array = new int[4];
		array[0] = _classBasedRegisters.totalSize();
		array[1] = _nonClassBasedRegisters.size();
		array[2] = _teamRandomBasedRegisters.size();
		array[3] = _teamBasedRegisters.totalSize();

		return array;
	}

	public static void removeRegistration(Integer objId)
	{
		removeRegistration(objId, false);
	}

	public static void removeRegistration(Integer objId, boolean unreg)
	{
		if(ConfigValue.OlympiadDebug1)
			_log.info("Olympiad: removeRegistration["+objId+"]["+L2ObjectsStorage.getPlayer(objId)+"]");
		_classBasedRegisters.removeValue(objId);
		_nonClassBasedRegisters.remove(objId);
		_teamRandomBasedRegisters.remove(objId);

		L2Player player = L2ObjectsStorage.getPlayer(objId);
		if(player != null)
			player.setOlympiadGame(null);
		if(player == null)
		{
			System.out.println("Olympiad if(player == null): " + objId);
			Util.test();
			return;
		}
		if(_teamBasedRegisters.containsValue(objId))
		{
			if(player.getParty() != null)
				for(L2Player member : player.getParty().getPartyMembers())
					if(member != null)
					{
						member.setOlympiadGame(null);
						removeHwid(member);
						if(unreg && objId != member.getObjectId())
							member.sendPacket(Msg.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
					}
		}
		_teamBasedRegisters.removeValue(objId);
		removeHwid(player);
	}

	private static void removeHwid(L2Player player)
	{
		if(ConfigValue.Olympiad_HWID_LOG)
			System.out.println("Olympiad removeRegistration1: " + player.getHWIDs() + " char name: " + player.getName());
		if(ConfigValue.Olympiad_HWID)
		{
			try
			{
				_hwidRegistered.remove(_hwidRegistered.indexOf(player.getHWIDs()));
				if(ConfigValue.Olympiad_HWID_LOG)
					System.out.println("Olympiad removeRegistration2: " + player.getHWIDs() + " char name: " + player.getName());
			}
			catch(NullPointerException e)
			{
				//System.out.println(e);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				//System.out.println("Olympiad(546) removeHWID error: " + _hwidRegistered.size() + "  HWID: "+player.getHWIDs());
			}
		}
	}

	private static void setHwid(L2Player player)
	{
		if(player != null)
			if(ConfigValue.Olympiad_HWID)
				_hwidRegistered.add(player.getHWIDs());
			if(ConfigValue.Olympiad_HWID_LOG)
				System.out.println("Olympiad setHwid: " + player.getHWIDs() + " char name: " + player.getName());
	}

	public static synchronized int getNoblessePasses(L2Player player)
	{
		int objId = player.getObjectId();

		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;

		int points = noble.getInteger(POINTS_PAST);
		if(points == 0) // Уже получил бонус
			return 0;

		int rank = _noblesRank.get(objId);

		try
		{
			points = ConfigValue.class.getField("AltOlyRank" + rank + "Points").getInt("");
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		if(player.isHeroType() == 0 || Hero.getInstance().isInactiveHero(player.getObjectId()))
			points += ConfigValue.AltOlyHeroPoints;

		noble.set(POINTS_PAST, 0);
		OlympiadDatabase.saveNobleData(objId);

		return points * ConfigValue.AltOlyGPPerPoint;
	}

	public static synchronized boolean isRegistered(L2Player noble)
	{
		if(noble == null)
			return false;
		return (_classBasedRegisters.containsValue(noble.getObjectId()) || _nonClassBasedRegisters.contains(noble.getObjectId()) || _teamRandomBasedRegisters.contains(noble.getObjectId()) || _teamBasedRegisters.containsValue(noble.getObjectId()));
	}

	public static synchronized boolean isRegisteredTeam(L2Player noble)
	{
		return _teamRandomBasedRegisters.contains(noble.getObjectId()) || _teamBasedRegisters.containsValue(noble.getObjectId());
	}

	public static synchronized boolean isHWIDRegistered(L2Player noble)
	{
		if(ConfigValue.Olympiad_HWID)
			for(String _hwid : _hwidRegistered)
				if(_hwid.equals(noble.getHWIDs()))
					return true;
		return false;
	}

	public static synchronized boolean isRegisteredInComp(L2Player player)
	{
		if(isRegistered(player))
			return true;
		if(_manager == null || _games == null)
			return false;
		for(OlympiadGame g : _games)
			if(g != null && g.isRegistered(player.getObjectId()))
				return true;
		return false;
	}

	/**
	 * Возвращает олимпийские очки за текущий период
	 * @param objId
	 * @return
	 */
	public static synchronized int getNoblePoints(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(POINTS);
	}

	/**
	 * Возвращает олимпийские очки за прошлый период
	 * @param objId
	 * @return
	 */
	public static synchronized int getNoblePointsPast(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(POINTS_PAST);
	}

	public static synchronized int getCompetitionDone(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(COMP_DONE);
	}

	public static synchronized int getCompetitionWin(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(COMP_WIN);
	}

	public static synchronized int getCompetitionLoose(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(COMP_LOOSE);
	}

	public static synchronized int getNoneClassCompetition(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(NONECLASS_COMP_COUNT);
	}

	public static synchronized int getClassCompetition(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(CLASS_COMP_COUNT);
	}

	public static synchronized int getTeamCompetition(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(TEAM_COMP_COUNT);
	}

	public static synchronized int getCompetitionCount(int objId, CompType type)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		switch(type)
		{
			case NON_CLASSED:
				return noble.getInteger(NONECLASS_COMP_COUNT);
			case CLASSED:
				return noble.getInteger(CLASS_COMP_COUNT);
			case TEAM:
			case TEAM_RANDOM: // Уточнить!!!
				return noble.getInteger(TEAM_COMP_COUNT);
		}
		return 0;
	}

	public static synchronized int getAllCompetitionCount(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(NONECLASS_COMP_COUNT) + noble.getInteger(CLASS_COMP_COUNT) + noble.getInteger(TEAM_COMP_COUNT);
	}

	public static synchronized void incCompetitionCount(int objId, CompType type)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
		{
			_log.warning("Nooble is null, for objId: " + objId);
			return;
		}

		int count = getCompetitionCount(objId, type) + 1;

		switch(type)
		{
			case NON_CLASSED:
				noble.set(NONECLASS_COMP_COUNT, count);
				break;
			case CLASSED:
				noble.set(CLASS_COMP_COUNT, count);
				break;
			case TEAM:
			case TEAM_RANDOM: // Уточнить!!!
				noble.set(TEAM_COMP_COUNT, count);
		}
	}

	public static GArray<L2OlympiadManagerInstance> getNpcs()
	{
		return _npcs;
	}

	public static void addOlympiadNpc(L2OlympiadManagerInstance npc)
	{
		_npcs.add(npc);
	}

	public static void changeNobleName(int objId, String newName)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return;
		noble.set(CHAR_NAME, newName);
		OlympiadDatabase.saveNobleData(objId);
	}

	public static void changeNobleClass(int objId, int newClass)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return;
		noble.set(CLASS_ID, newClass);
		OlympiadDatabase.saveNobleData(objId);
	}

	public static void changeNobleId(int objId, int newId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return;
		_nobles.remove(objId);
		_nobles.put(newId, noble);
		OlympiadDatabase.saveNobleData(objId);
		OlympiadDatabase.saveNobleData(newId);
	}

	public static String getNobleName(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return null;
		return noble.getString(CHAR_NAME);
	}

	public static int getNobleClass(int objId)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return 0;
		return noble.getInteger(CLASS_ID);
	}

	public static void manualSetNoblePoints(int objId, int points)
	{
		StatsSet noble = _nobles.get(objId);
		if(noble == null)
			return;
		noble.set(POINTS, points);
		OlympiadDatabase.saveNobleData(objId);
	}

	public static synchronized boolean isNoble(int objId)
	{
		return _nobles.get(objId) != null;
	}

	public static synchronized void addNoble(L2Player noble)
	{
		if(!_nobles.containsKey(noble.getObjectId()))
		{
			int classId = noble.getBaseClassId();
			if(classId < 88)
			{
				for(ClassId id : ClassId.values())
				{
					if(id.level() != 3 || id.getParent((byte)0).getId() != classId)
						continue;
					classId = id.getId();
					break;
				}
			}
			StatsSet statDat = new StatsSet();
			statDat.set(CLASS_ID, classId);
			statDat.set(CHAR_NAME, noble.getName());
			statDat.set(POINTS, ConfigValue.OlympiadSetStartPoint);
			statDat.set(POINTS_PAST, 0);
			statDat.set(POINTS_PAST_STATIC, 0);
			statDat.set(COMP_DONE, 0);
			statDat.set(COMP_WIN, 0);
			statDat.set(COMP_LOOSE, 0);
			statDat.set(NONECLASS_COMP_COUNT, 0);
			statDat.set(CLASS_COMP_COUNT, 0);
			statDat.set(TEAM_COMP_COUNT, 0);
			_nobles.put(noble.getObjectId(), statDat);
			OlympiadDatabase.saveNobleData();
			PlayerRewardManager.getInstance().set_noble(noble);
		}
	}

	public static synchronized void removeNoble(L2Player noble)
	{
		_nobles.remove(noble.getObjectId());
		OlympiadDatabase.saveNobleData();
	}

	public static int getCountAcitveGames()
	{
		int count = 0;
		if(_games != null)
			for(OlympiadGame game : _games)
				if(game != null && game.getState() > 0)
					count++;
		return count;
	}

	public static GArray<OlympiadGame> getActiveGames()
	{
		GArray<OlympiadGame> activeGames = new GArray<OlympiadGame>();
		if(_games != null)
			for(OlympiadGame game : _games)
				if(game != null && game.getState() > 0)
					activeGames.add(game);
		return activeGames;
	}

	public static synchronized int getFreeGameId()
	{
		if(_games != null)
			for(int i = 0; i <= _games.length - 1; i++)
				if(_games[i] == null)
					return i;
		return -1;
	}

	public static void useCommand(L2Player player, String command, String value)
	{
		if(command.startsWith("move_op_field"))
		{
			try
			{
				int id = Integer.parseInt(value) - 1;
				if(_games[id] != null && _games[id].getState() > 0)
				{
					if(player.getOlympiadObserveId() == -1)
						addSpectator(_games[id], player);
					else
					{
						player.leaveObserverMode(Olympiad.getGameBySpectator(player));
						addSpectator(_games[id], player);
					}
				}
				else
					player.sendPacket(new ExOlympiadMatchList());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
			_log.info("Command for '_olympiad' not found: " + command + " and value: " + value);
	}

	public static int getCountParticipants()
	{
		return _classBasedRegisters.size() + _nonClassBasedRegisters.size() + _teamRandomBasedRegisters.size() + _teamBasedRegisters.size();
	}

	public static int getMaxForCompType(CompType type)
	{
		switch(type)
		{
			case NON_CLASSED:
				return ConfigValue.MaxCompForNonClassed;
			case CLASSED:
				return ConfigValue.MaxCompForClassed;
			case TEAM_RANDOM:
			case TEAM:
				return ConfigValue.MaxCompForTeam;
		}
		return 0;
	}

	public static void removeBattlesCount()
	{
		for(StatsSet set : _nobles.values())
			if(set != null)
			{
				set.set(NONECLASS_COMP_COUNT, 0);
				set.set(CLASS_COMP_COUNT, 0);
				set.set(TEAM_COMP_COUNT, 0);
			}

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(OLYMPIAD_CLEANUP_COMPETITIONS);
			statement.execute();
		}
		catch(Exception e)
		{
			Olympiad._log.warning("Olympiad System: Couldn't removeBattlesCount!");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static boolean isProgress(L2Player noble)
	{
		if(!inCompPeriod() || isOlympiadEnd() || getMillisToOlympiadEnd() <= 600000 || getMillisToCompEnd() <= 600000)
		{
			if(noble != null)
				noble.sendPacket(Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		return true;
	}
}