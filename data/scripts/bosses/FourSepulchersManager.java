package bosses;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l2open.common.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import npc.model.L2SepulcherNpcInstance;
import bosses.FourSepulchersSpawn.GateKeeper;

public class FourSepulchersManager extends Functions implements ScriptFile
{
	private static Logger _log = Logger.getLogger(FourSepulchersManager.class.getName());

	public static final String QUEST_ID = "_620_FourGoblets";

	private static L2Zone[] _zone = new L2Zone[4];

	private static final int ENTRANCE_PASS = 7075;
	private static final int USED_PASS = 7261;
	private static final int CHAPEL_KEY = 7260;
	private static final int ANTIQUE_BROOCH = 7262;

	private static boolean _inEntryTime = false;
	private static boolean _inAttackTime = false;

	private static ScheduledFuture<?> _changeCoolDownTimeTask = null, _changeEntryTimeTask = null,
			_changeWarmUpTimeTask = null, _changeAttackTimeTask = null;

	private static long _coolDownTimeEnd = 0;
	private static long _entryTimeEnd = 0;
	private static long _warmUpTimeEnd = 0;
	private static long _attackTimeEnd = 0;

	private static byte _newCycleMin = 55;

	private static boolean _firstTimeRun;

	public static void init()
	{
		_zone[0] = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702110, false);
		_zone[1] = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702111, false);
		_zone[2] = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702112, false);
		_zone[3] = ZoneManager.getInstance().getZoneById(ZoneType.epic, 702113, false);

		if(_changeCoolDownTimeTask != null)
			_changeCoolDownTimeTask.cancel(true);
		if(_changeEntryTimeTask != null)
			_changeEntryTimeTask.cancel(true);
		if(_changeWarmUpTimeTask != null)
			_changeWarmUpTimeTask.cancel(true);
		if(_changeAttackTimeTask != null)
			_changeAttackTimeTask.cancel(true);

		_changeCoolDownTimeTask = null;
		_changeEntryTimeTask = null;
		_changeWarmUpTimeTask = null;
		_changeAttackTimeTask = null;

		_inEntryTime = false;
		_inAttackTime = false;

		_firstTimeRun = true;

		FourSepulchersSpawn.init();

		timeSelector();
	}

	// phase select on server launch
	private static void timeSelector()
	{
		timeCalculator();
		long currentTime = System.currentTimeMillis();
		// if current time >= time of entry beginning and if current time < time of entry beginning + time of entry end
		if(currentTime >= _coolDownTimeEnd && currentTime < _entryTimeEnd) // entry time check
		{
			cleanUp();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeEntryTime(), 0);
			_log.warning("FourSepulchersManager: Beginning in Entry time");
		}
		else if(currentTime >= _entryTimeEnd && currentTime < _warmUpTimeEnd) // warmup time check
		{
			cleanUp();
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeWarmUpTime(), 0);
			_log.warning("FourSepulchersManager: Beginning in WarmUp time");
		}
		else if(currentTime >= _warmUpTimeEnd && currentTime < _attackTimeEnd) // attack time check
		{
			cleanUp();
			_changeAttackTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeAttackTime(), 0);
			_log.warning("FourSepulchersManager: Beginning in Attack time");
		}
		else
		// else cooldown time and without cleanup because it's already implemented
		{
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeCoolDownTime(), 0);
			_log.warning("FourSepulchersManager: Beginning in Cooldown time");
		}
	}

	//phase end times calculator
	private static void timeCalculator()
	{
		Calendar tmp = Calendar.getInstance();
		if(tmp.get(Calendar.MINUTE) < _newCycleMin)
			tmp.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) - 1);
		tmp.set(Calendar.MINUTE, _newCycleMin);
		_coolDownTimeEnd = tmp.getTimeInMillis();
		_entryTimeEnd = _coolDownTimeEnd + 3 * 60000;
		_warmUpTimeEnd = _entryTimeEnd + 2 * 60000;
		_attackTimeEnd = _warmUpTimeEnd + 50 * 60000;
	}

	private static void cleanUp()
	{
		for(L2Player player : getPlayersInside())
			player.teleToClosestTown();

		FourSepulchersSpawn.deleteAllMobs();

		FourSepulchersSpawn.closeAllDoors();

		FourSepulchersSpawn._hallInUse.clear();
		FourSepulchersSpawn._hallInUse.put(31921, false);
		FourSepulchersSpawn._hallInUse.put(31922, false);
		FourSepulchersSpawn._hallInUse.put(31923, false);
		FourSepulchersSpawn._hallInUse.put(31924, false);

		if(!FourSepulchersSpawn._archonSpawned.isEmpty())
		{
			Set<Integer> npcIdSet = FourSepulchersSpawn._archonSpawned.keySet();
			for(int npcId : npcIdSet)
				FourSepulchersSpawn._archonSpawned.put(npcId, false);
		}
	}

	public static boolean isEntryTime()
	{
		return _inEntryTime;
	}

	public static boolean isAttackTime()
	{
		return _inAttackTime;
	}

	public static synchronized void tryEntry(L2NpcInstance npc, L2Player player)
	{
		int npcId = npc.getNpcId();
		switch(npcId)
		{
			// ID ok
			case 31921:
			case 31922:
			case 31923:
			case 31924:
				break;
			// ID not ok
			default:
				return;
		}

		if(FourSepulchersSpawn._hallInUse.get(npcId))
		{
			showHtmlFile(player, npcId + "-FULL.htm", npc, null);
			return;
		}

		if(!player.isInParty() || player.getParty().getMemberCount() < 4)
		{
			showHtmlFile(player, npcId + "-SP.htm", npc, null);
			return;
		}

		if(!player.getParty().isLeader(player))
		{
			showHtmlFile(player, npcId + "-NL.htm", npc, null);
			return;
		}

		for(L2Player mem : player.getParty().getPartyMembers())
		{
			QuestState qs = mem.getQuestState(QUEST_ID);
			if(qs == null || !qs.isStarted() && !qs.isCompleted())
			{
				showHtmlFile(player, npcId + "-NS.htm", npc, mem);
				return;
			}

			if(mem.getInventory().getItemByItemId(ENTRANCE_PASS) == null)
			{
				showHtmlFile(player, npcId + "-SE.htm", npc, mem);
				return;
			}

			if(!mem.isQuestContinuationPossible(true))
				return;

			if(mem.isDead() || !mem.isInRange(player, 700))
				return;
		}

		if(!isEntryTime())
		{
			showHtmlFile(player, npcId + "-NE.htm", npc, null);
			return;
		}

		showHtmlFile(player, npcId + "-OK.htm", npc, null);

		entry(npcId, player);
	}

	private static void entry(int npcId, L2Player player)
	{
		Location loc = FourSepulchersSpawn._startHallSpawns.get(npcId);
		for(L2Player mem : player.getParty().getPartyMembers())
		{
			mem.teleToLocation(loc.rnd(0, 80, false));
			Functions.removeItem(mem, ENTRANCE_PASS, 1);
			if(mem.getInventory().getItemByItemId(ANTIQUE_BROOCH) == null)
				Functions.addItem(mem, USED_PASS, 1);
			Functions.removeItem(mem, CHAPEL_KEY, 999999);
		}
		FourSepulchersSpawn._hallInUse.put(npcId, true);
	}

	public static void OnDie(L2Character self, L2Character killer)
	{
		if(self != null && self.isPlayer() && self.getZ() >= -7250 && self.getZ() <= -6841 && checkIfInZone(self))
			checkAnnihilated((L2Player) self);
	}

	public static void checkAnnihilated(final L2Player player)
	{
		if(isPlayersAnnihilated())
			ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
				public void runImpl()
				{
					if(player.getParty() != null)
						for(L2Player mem : player.getParty().getPartyMembers())
						{
							if(!mem.isDead())
								break;
							mem.teleToLocation(169589 + Rnd.get(-80, 80), -90493 + Rnd.get(-80, 80), -2914);
						}
					else
						player.teleToLocation(169589 + Rnd.get(-80, 80), -90493 + Rnd.get(-80, 80), -2914);
				}
			}, 5000);
	}

	private static byte minuteSelect(byte min)
	{
		switch(min % 5)
		{
			case 0:
				return min;
			case 1:
				return (byte) (min - 1);
			case 2:
				return (byte) (min - 2);
			case 3:
				return (byte) (min + 2);
			default:
				return (byte) (min + 1);
		}
	}

	public static void managerSay(byte min)
	{
		// for attack phase, sending message every 5 minutes
		if(_inAttackTime)
		{
			// do not shout when < 5 minutes
			if(min < 5)
				return;
			min = minuteSelect(min);
			String msg = min + " minute(s) have passed."; // now this is a proper message^^
			if(min == 90)
				msg = "Game over. The teleport will appear momentarily";
			for(L2SepulcherNpcInstance npc : FourSepulchersSpawn._managers)
			{
				// hall not used right now, so its manager will not tell you anything :)
				// if you don't need this - delete next two lines.
				if(!FourSepulchersSpawn._hallInUse.get(npc.getNpcId()))
					continue;
				npc.sayInShout(msg);
			}
		}
		else if(_inEntryTime)
		{
			String msg1 = "You may now enter the Sepulcher";
			String msg2 = "If you place your hand on the stone statue in front of each sepulcher," + " you will be able to enter";
			for(L2SepulcherNpcInstance npc : FourSepulchersSpawn._managers)
			{
				npc.sayInShout(msg1);
				npc.sayInShout(msg2);
			}
		}
	}

	private static class ManagerSay extends RunnableImpl
	{
		public void runImpl()
		{
			if(_inAttackTime)
			{
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(System.currentTimeMillis() - _warmUpTimeEnd);
				if(tmp.get(Calendar.MINUTE) + 5 < 50)
				{
					managerSay((byte) tmp.get(Calendar.MINUTE)); //byte because minute cannot be more than 59
					ThreadPoolManager.getInstance().schedule(new ManagerSay(), 5 * 60000);
				}
				// attack time ending chat
				else if(tmp.get(Calendar.MINUTE) + 5 >= 50)
					managerSay((byte) 90); //sending a unique id :D
			}
			else if(_inEntryTime)
				managerSay((byte) 0);
		}
	}

	private static class ChangeEntryTime extends RunnableImpl
	{
		public void runImpl()
		{
			_inEntryTime = true;
			_inAttackTime = false;

			long interval = 0;
			// if this is first launch - search time when entry time will be ended:
			// counting difference between time when entry time ends and current time and then launching change time task
			if(_firstTimeRun)
				interval = _entryTimeEnd - System.currentTimeMillis();
			else
				interval = 3 * 60000; // else use stupid method
			// launching saying process...
			ThreadPoolManager.getInstance().execute(new ManagerSay());
			_changeWarmUpTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeWarmUpTime(), interval);
			if(_changeEntryTimeTask != null)
			{
				_changeEntryTimeTask.cancel(true);
				_changeEntryTimeTask = null;
			}
		}
	}

	private static class ChangeWarmUpTime extends RunnableImpl
	{
		public void runImpl()
		{
			_inEntryTime = true;
			_inAttackTime = false;

			long interval = 0;
			// searching time when warmup time will be ended:
			// counting difference between time when warmup time ends and current time and then launching change time task
			if(_firstTimeRun)
				interval = _warmUpTimeEnd - System.currentTimeMillis();
			else
				interval = 2 * 60000;
			_changeAttackTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeAttackTime(), interval);

			if(_changeWarmUpTimeTask != null)
			{
				_changeWarmUpTimeTask.cancel(true);
				_changeWarmUpTimeTask = null;
			}
		}
	}

	private static class ChangeAttackTime extends RunnableImpl
	{
		public void runImpl()
		{
			_inEntryTime = false;
			_inAttackTime = true;

			for(GateKeeper gk : FourSepulchersSpawn._GateKeepers)
			{
				L2SepulcherNpcInstance npc = new L2SepulcherNpcInstance(IdFactory.getInstance().getNextId(), gk.template);
				npc.spawnMe(gk);
				FourSepulchersSpawn._allMobs.add(npc);
			}

			FourSepulchersSpawn.locationShadowSpawns();

			FourSepulchersSpawn.spawnMysteriousBox(31921);
			FourSepulchersSpawn.spawnMysteriousBox(31922);
			FourSepulchersSpawn.spawnMysteriousBox(31923);
			FourSepulchersSpawn.spawnMysteriousBox(31924);

			if(!_firstTimeRun)
				_warmUpTimeEnd = System.currentTimeMillis();

			long interval = 0;
			//say task
			if(_firstTimeRun)
			{
				for(double min = Calendar.getInstance().get(Calendar.MINUTE); min < _newCycleMin; min++)
					// looking for next shout time....
					if(min % 5 == 0) //check if min can be divided by 5
					{
						_log.warning(Calendar.getInstance().getTime() + " Atk announce scheduled to " + min + " minute of this hour.");
						Calendar inter = Calendar.getInstance();
						inter.set(Calendar.MINUTE, (int) min);
						ThreadPoolManager.getInstance().schedule(new ManagerSay(), inter.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
						break;
					}
			}
			else
				ThreadPoolManager.getInstance().schedule(new ManagerSay(), 5 * 60400);

			// searching time when attack time will be ended:
			// counting difference between time when attack time ends and current time and then launching change time task
			if(_firstTimeRun)
				interval = _attackTimeEnd - System.currentTimeMillis();
			else
				interval = 50 * 60000;
			_changeCoolDownTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeCoolDownTime(), interval);

			if(_changeAttackTimeTask != null)
			{
				_changeAttackTimeTask.cancel(true);
				_changeAttackTimeTask = null;
			}
		}
	}

	private static class ChangeCoolDownTime extends RunnableImpl
	{
		public void runImpl()
		{
			_inEntryTime = false;
			_inAttackTime = false;

			cleanUp();

			Calendar time = Calendar.getInstance();
			// one hour = 55th min to 55 min of next hour, so we check for this, also check for first launch
			if(Calendar.getInstance().get(Calendar.MINUTE) > _newCycleMin && !_firstTimeRun)
				time.set(Calendar.HOUR, Calendar.getInstance().get(Calendar.HOUR) + 1);
			time.set(Calendar.MINUTE, _newCycleMin);
			_log.warning("FourSepulchersManager: Entry time: " + time.getTime());
			if(_firstTimeRun)
				_firstTimeRun = false; // cooldown phase ends event hour, so it will be not first run

			long interval = time.getTimeInMillis() - System.currentTimeMillis();
			_changeEntryTimeTask = ThreadPoolManager.getInstance().schedule(new ChangeEntryTime(), interval);

			if(_changeCoolDownTimeTask != null)
			{
				_changeCoolDownTimeTask.cancel(true);
				_changeCoolDownTimeTask = null;
			}
		}
	}

	public static GateKeeper getHallGateKeeper(int npcId)
	{
		for(GateKeeper gk : FourSepulchersSpawn._GateKeepers)
			if(gk.template.npcId == npcId)
				return gk;
		return null;
	}

	public static void showHtmlFile(L2Player player, String file, L2NpcInstance npc, L2Player member)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile("data/html/SepulcherNpc/" + file);
		if(member != null)
			html.replace("%member%", member.getName());
		player.sendPacket(html);
	}

	private static boolean isPlayersAnnihilated()
	{
		for(L2Player pc : getPlayersInside())
			if(!pc.isDead())
				return false;
		return true;
	}

	private static GArray<L2Player> getPlayersInside()
	{
		GArray<L2Player> result = new GArray<L2Player>();
		for(L2Zone zone : getZones())
			result.addAll(zone.getInsidePlayers());
		return result;
	}

	private static boolean checkIfInZone(L2Object obj)
	{
		for(L2Zone zone : getZones())
			if(zone.checkIfInZone(obj))
				return true;
		return false;
	}

	public static L2Zone[] getZones()
	{
		return _zone;
	}

	public void onLoad()
	{
		init();
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}