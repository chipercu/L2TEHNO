package bosses;

import bosses.EpicBossState.State;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.instances.L2BossInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Crontab;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Util;
import l2open.util.Rnd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;


public class AntharasManager extends Functions implements ScriptFile
{
	private static final Logger _log = Logger.getLogger(AntharasManager.class.getName());
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");
	// Constants
	private static final int _teleportCubeId = 31859;
	private static final int ANTHARAS_STRONG = 29068;
	private static final int PORTAL_STONE = 3865;
	private static final Location TELEPORT_POSITION = new Location(179892, 114915, -7704);
	private static final Location _teleportCubeLocation = new Location(177615, 114941, -7709, 0);
	private static final Location _antharasLocation = new Location(181911, 114835, -7678, 32542);

	// Models
	public static L2BossInstance _antharas;
	private static L2NpcInstance _teleCube;
	private static List<L2NpcInstance> _spawnedMinions = new ArrayList<L2NpcInstance>();

	// tasks.
	private static ScheduledFuture<?> _monsterSpawnTask;
	private static ScheduledFuture<?> _intervalEndTask;
	private static ScheduledFuture<?> _socialTask;
	private static ScheduledFuture<?> _moveAtRandomTask;
	private static ScheduledFuture<?> _sleepCheckTask;
	private static ScheduledFuture<?> _onAnnihilatedTask;

	// Vars
	public static EpicBossState _state;
	private static L2Zone _zone;
	private static long _lastAttackTime = 0;
	private static int _count1 = 0;
	private static boolean Dying = false;
	private static L2CommandChannel _channel = null;

	private static class AntharasSpawn extends RunnableImpl
	{
		private int _distance = 2550;
		private int _taskId = 0;
		private GArray<L2Player> _players = getPlayersInside();

		AntharasSpawn(int taskId)
		{
			_taskId = taskId;
		}

		@Override
		public void runImpl()
		{
			switch(_taskId)
			{
				case 1:
					_antharas = (L2BossInstance) Functions.spawn(_antharasLocation, ANTHARAS_STRONG);
					_antharas.setAggroRange(0);
					_state.setRespawnDate2(getRespawnInterval(false, "SPAWN"));
					_state.setState(EpicBossState.State.ALIVE);
					_state.update();
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(2), 2000);
					break;
				case 2:
					// set camera.
					broadcastPacket(new SpecialCamera(_antharas, 700, 13, -19, 0, 10000, 20000, 0, 0, 0, 0, 0));
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(3), 3000);
					break;
				case 3:
					// do social.
					_antharas.broadcastPacket(new SocialAction(_antharas.getObjectId(), 1));
					_antharas.MPCC_SetMasterPartyRouting(_channel, 1);

					// set camera.
					broadcastPacket(new SpecialCamera(_antharas, 700, 13, 0, 6000, 10000, 20000, 0, 0, 0, 0, 0));
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(4), 10000);
					break;
				case 4:
					_antharas.broadcastPacket(new SocialAction(_antharas.getObjectId(), 2));
					// set camera.
					broadcastPacket(new SpecialCamera(_antharas, 3700, 0, -3, 0, 10000, 10000, 0, 0, 0, 0, 0));
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(5), 200);
					break;
				case 5:
					// set camera.
					broadcastPacket(new SpecialCamera(_antharas, 1100, 0, -3, 22000, 10000, 30000, 0, 0, 0, 0, 0));
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(6), 10800);
					break;
				case 6:
					// set camera.
					broadcastPacket(new SpecialCamera(_antharas, 1100, 0, -3, 300, 10000, 7000, 0, 0, 0, 0, 0));
					_antharas.AddTimerEx(1100,((3 * 60) * 1000)); // Таск на спаун миньенов...
					_antharas.AddTimerEx(1101,((5 * 60) * 1000)); // Таск на спаун миньенов...
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(7), 7000);
					break;
				case 7:
					// reset camera.
					for(L2Player pc : _players)
						pc.leaveMovieMode();

					broadcastScreenMessage(1000520);
					_antharas.broadcastPacket(new PlaySound(1, "BS02_A", 1, _antharas.getObjectId(), _antharas.getLoc()));
					_antharas.setAggroRange(_antharas.getTemplate().aggroRange);
					_antharas.setRunning();
					_antharas.moveToLocation(new Location(179011, 114871, -7704), 0, false);
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 600000);
					break;
				case 8:
					broadcastPacket(new SpecialCamera(_antharas, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
					_antharas.broadcastPacket(new PlaySound(1, "BS01_D", 1, _antharas.getObjectId(), _antharas.getLoc()));
					_socialTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(9), 13000);
					break;
				case 9:
					for(L2Player pc : _players)
					{
						pc.altOnMagicUseTimer(pc, SkillTable.getInstance().getInfo(23312, 1));
					}
					broadcastScreenMessage(1900150);
					onAntharasDie();
					break;
			}
		}
	}

	private static class CheckLastAttack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(_state.getState() == EpicBossState.State.ALIVE)
				if(_lastAttackTime + (ConfigValue.AntharasWaitingToSleep*60000) < System.currentTimeMillis())
					sleep();
				else
					_sleepCheckTask = ThreadPoolManager.getInstance().schedule(new CheckLastAttack(), 60000);
		}
	}

	// at end of interval.
	private static class IntervalEnd extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			Announcements.getInstance().announceToAll("Антарас Возродился!");
		}
	}

	private static class onAnnihilated extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			sleep();
		}
	}

	private static void banishForeigners()
	{
		//Util.test();
		for(L2Player player : getPlayersInside())
			player.teleToClosestTown();
	}

	private synchronized static void checkAnnihilated()
	{
		if(_onAnnihilatedTask == null && isPlayersAnnihilated())
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
	}

	public static GArray<L2Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}

	private static long getRespawnInterval(boolean inc, String txt)
	{
		long time = (long) (ConfigValue.AltRaidRespawnMultiplier * (ConfigValue.FixIntervalOfAntharas + Rnd.get(0,  ConfigValue.RandomIntervalOfAantaras)));
		int index = ServerVariables.getInt("FixRespHourOfAntharas", 0);
		// Это не нужно в силу наличия крона, но лень всем клиентам объяснять что и как
		if(ConfigValue.FixRespHourOfAntharas.length > index && ConfigValue.FixRespHourOfAntharas[index] > -1 || ConfigValue.FixRespDayOfWeekAntharas > -1)
		{
			if(ConfigValue.FixRespDayOfWeekAntharas > -1)
				time = 7*24*60*60000L;
			final Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis()+time);
			if(ConfigValue.FixRespHourOfAntharas.length > index && ConfigValue.FixRespHourOfAntharas[index] > -1)
			{
				c.set(Calendar.HOUR_OF_DAY, ConfigValue.FixRespHourOfAntharas[index]);
				c.set(Calendar.MINUTE, 00);
				c.set(Calendar.SECOND, 00);
				if(inc)
				{
					index++;
					if(ConfigValue.FixRespHourOfAntharas.length <= index)
						index=0;
					ServerVariables.set("FixRespHourOfAntharas", String.valueOf(index));
				}
				Log.add("Antharas getRespawnInterval["+index+"]["+inc+"]["+txt+"]", "bosses");
			}
			if(ConfigValue.FixRespDayOfWeekAntharas > -1)
				c.set(Calendar.DAY_OF_WEEK, ConfigValue.FixRespDayOfWeekAntharas);
			return c.getTimeInMillis()+(Rnd.get(ConfigValue.AddRndRespHourOfAntharas)*60000);
		}

		return new Crontab(ConfigValue.AntharasCronResp).timeNextUsage(System.currentTimeMillis())+time;
	}

	public static L2Zone getZone()
	{
		return _zone;
	}

	private static boolean isPlayersAnnihilated()
	{
		for(L2Player pc : getPlayersInside())
			if(!pc.isDead())
				return false;
		return true;
	}

	private static void onAntharasDie()
	{
		if(Dying)
			return;

		Dying = true;
		_state.setRespawnDate2(getRespawnInterval(true, "DIE"));
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();

		_teleCube = Functions.spawn(_teleportCubeLocation, _teleportCubeId);
		Log.add("Antharas died", "bosses");

		if(ConfigValue.AntharasCustomRewardPlayerCount > 0)
		{
			GArray<L2Player> _players = new GArray<L2Player>();
			_players.addAll(getPlayersInside());

			Announcements.getInstance().announceToAll("Поздравляем они получили частичку души Антараса: ");
			for(int i=0;i<ConfigValue.AntharasCustomRewardPlayerCount && _players.size() > 0;i++)
			{
				L2Player player = _players.remove(Rnd.get(_players.size()));
				if(player != null)
				{
					Announcements.getInstance().announceToAll(player.getName());
					if(ConfigValue.AntharasCustomReward.length > 0)
						for(int i2=0;i2<ConfigValue.AntharasCustomReward.length;i2+=2)
							Functions.addItem(player, (int)ConfigValue.AntharasCustomReward[i2], ConfigValue.AntharasCustomReward[i2+1]);

					int rnd_size = ConfigValue.AntharasCustomRewardRnd.length;
					if(rnd_size > 0)
					{
						long[] reward = ConfigValue.AntharasCustomRewardRnd[Rnd.get(rnd_size)];
						Functions.addItem(player, (int)reward[0], reward[1]);
					}
				}
			}
		}
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self.isPlayer() && _state != null && _state.getState() == State.ALIVE && _zone != null && _zone.checkIfInZone(self.getX(), self.getY()))
			checkAnnihilated();
		else if(self.isNpc() && self.getNpcId() == ANTHARAS_STRONG)
			ThreadPoolManager.getInstance().schedule(new AntharasSpawn(8), 10);
	}

	private static void setIntervalEndTask()
	{
		setUnspawn();

		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			return;
		}

		if(!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate2(getRespawnInterval(false,"INTERVAL"));
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}

		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}

	// clean Antharas's lair.
	private static void setUnspawn()
	{
		// eliminate players.
		banishForeigners();

		if(_antharas != null)
		{
			_antharas.MPCC_SetMasterPartyRouting(null, 0);
			_antharas.deleteMe();
		}
		_channel = null;
		for(L2NpcInstance npc : _spawnedMinions)
			npc.deleteMe();
		if(_teleCube != null)
			_teleCube.deleteMe();


		// not executed tasks is canceled.
		if(_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = null;
		}
		if(_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if(_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if(_moveAtRandomTask != null)
		{
			_moveAtRandomTask.cancel(false);
			_moveAtRandomTask = null;
		}
		if(_sleepCheckTask != null)
		{
			_sleepCheckTask.cancel(false);
			_sleepCheckTask = null;
		}
		if(_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(false);
			_onAnnihilatedTask = null;
		}
		//mysql.set("UPDATE `bos_debug` SET `attacked`=0 where bos_id=29068");
	}

	private void init()
	{
		_state = new EpicBossState(ANTHARAS_STRONG);
		communityboard.CommunityBoardFullStats.addEpic(_state);
		_zone = ZoneManager.getInstance().getZoneById(L2Zone.ZoneType.epic, 702002, false);
		_log.info("AntharasManager: State of Antharas is " + _state.getState() + ".");
		if(!_state.getState().equals(EpicBossState.State.NOTSPAWN))
			setIntervalEndTask();

		_log.info("AntharasManager: Next spawn date of Antharas is " + toSimpleFormat(_state.getRespawnDate()) + ".");
	}

	private static void sleep()
	{
		setUnspawn();
		if(_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}

	public static void setLastAttackTime()
	{
		//if(_lastAttackTime + 600000 < System.currentTimeMillis())
		//	mysql.set("UPDATE `bos_debug` SET `attacked`=1 where bos_id=29068");
		_lastAttackTime = System.currentTimeMillis();
	}

	// setting Antharas spawn task.
	public synchronized static void setAntharasSpawnTask()
	{
		if(_monsterSpawnTask == null)
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new AntharasSpawn(1), ConfigValue.AntharasWaitingToSpawn * 60000);
	}

	public static void broadcastScreenMessage(int id)
	{
		for(L2Player p : getPlayersInside())
			p.sendPacket(new ExShowScreenMessage(id, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
	}

	public static void addSpawnedMinion(L2NpcInstance npc)
	{
		_spawnedMinions.add(npc);
	}

	public static void enterTheLair(L2Player ccleader)
	{
		if(ccleader == null)
			return;

		if(ccleader.isGM() && ConfigValue.AntharasDebug)
		{
			if(ccleader.getParty() != null)
				for(L2Player p : ccleader.getParty().getPartyMembers())
					p.teleToLocation(TELEPORT_POSITION);
			else
				ccleader.teleToLocation(TELEPORT_POSITION);
			setAntharasSpawnTask();
			return;
		}
		Location loc = ccleader.getLoc();
		int _count = 0;

		if(_state.getState() != EpicBossState.State.NOTSPAWN)
		{
			ccleader.sendMessage("Antharas is still reborning. You cannot invade the nest now");
			return;
		}
		if(_state.getState() == EpicBossState.State.ALIVE)
		{
			ccleader.sendMessage("Antharas has already been reborned and is being attacked. The entrance is sealed.");
			return;
		}
		if(ccleader.getInventory().getCountOf(PORTAL_STONE) < 1)
		{
			ccleader.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}
		if(!ConfigValue.AntharasNeedCC)
		{
			if(ccleader.getParty() == null)
			{
				if(!ccleader.isFlying() && !ccleader.isCursedWeaponEquipped() && !ccleader.isDead())
				{
					ccleader.teleToLocation(TELEPORT_POSITION);
					Log.add("Enter["+_count1+"]: "+ccleader.getName(), "antharas_enter");
				}
				else
					Log.add("NO enter1["+(!ccleader.isFlying())+"]["+(!ccleader.isCursedWeaponEquipped())+"]["+(!ccleader.isDead())+"]: "+ccleader.getName(), "antharas_enter");
				_count1++;
				setAntharasSpawnTask();
				return;
			}
			else if(!ccleader.getParty().isInCommandChannel())
			{
				for(L2Player p : ccleader.getParty().getPartyMembers())
					if(!p.isFlying() && !p.isCursedWeaponEquipped() && !p.isDead() && p.isInRange(loc, 500))
					{
						p.teleToLocation(TELEPORT_POSITION);
						_count++;
						Log.add("Enter["+_count+"]["+_count1+"]: "+p.getName(), "antharas_enter");
					}
					else
						Log.add("NO enter1["+(!p.isFlying())+"]["+(!p.isCursedWeaponEquipped())+"]["+(!p.isDead())+"]["+(p.isInRange(loc, 500))+"]: "+p.getName(), "antharas_enter");

				_count1 = _count1 + _count;
				setAntharasSpawnTask();
				return;
			}
		}
		if(ccleader.getParty() == null || !ccleader.getParty().isInCommandChannel())
		{
			ccleader.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
			return;
		}
		L2CommandChannel cc = ccleader.getParty().getCommandChannel();
		if(cc.getChannelLeader() != ccleader)
		{
			ccleader.sendMessage("Only the alliance channel leader can try to entry");
			return;
		}
		if(_count1 >= ConfigValue.AntharasMaxPlayer || cc.getMemberCount() >= ConfigValue.AntharasMaxPlayer)
		{
			ccleader.sendMessage("The maximum of "+ConfigValue.AntharasMaxPlayer+" players can invade the Antharas Nest");
			return;
		}
		// checking every member of CC for the proper conditions

		/*for(L2Player p : cc.getMembers())
			if(p.isDead() || p.isFlying() || p.isCursedWeaponEquipped() || !p.isInRange(ccleader, 500))
			{
				ccleader.sendMessage("Command Channel member " + p.getName() + " doesn't meet the requirements to enter the nest");
				return;
			}*/

		Log.add("-----------------------------------------------------------------\nEnter Leader: "+ccleader.getName(), "antharas_enter");
		for(L2Player p : cc.getMembers())
			//if(p.getInventory().getCountOf(PORTAL_STONE) >= 1)
			{
				if(!p.isFlying() && !p.isCursedWeaponEquipped() && !p.isDead() && p.isInRange(loc, 500))
				{
					p.teleToLocation(TELEPORT_POSITION);
					_count++;
					Log.add("Enter["+_count+"]["+_count1+"]: "+p.getName(), "antharas_enter");
				}
				else
					Log.add("NO enter1["+(!p.isFlying())+"]["+(!p.isCursedWeaponEquipped())+"]["+(!p.isDead())+"]["+(p.isInRange(loc, 500))+"]: "+p.getName(), "antharas_enter");
			}
			/*else
			{
				Log.add("NO enter2[No Item]: "+p.getName(), "antharas_enter");
				p.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			}*/

		if(_count >= ConfigValue.MinChannelMembersAntharas && _channel == null)
			_channel = cc;
		if(_count > 0)
			_count1 = _count1 + _count;

		setAntharasSpawnTask();
	}

	public static String toSimpleFormat(long cal)
	{
		return SIMPLE_FORMAT.format(cal);
	}

	public static L2Player getRandomPlayer()
	{
		GArray<L2Player> list = getZone().getInsidePlayers();
		if(list.isEmpty())
			return null;
		return list.get(Rnd.get(list.size()));
	}

	protected static void broadcastPacket(L2GameServerPacket mov)
	{
		if (getZone() != null)
		{
			for (L2Character characters : getZone().getInsidePlayers())
			{
				if (characters.isPlayer())
				{
					characters.sendPacket(mov);
				}
			}
		}
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{
		sleep();
	}

	@Override
	public void onShutdown()
	{
	}
}