package l2open.gameserver.instancemanager;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Zone Manager for location Seed Of Annihilation
 * @author Drizzy
 * @date 15.12.10
 */
public class SeedOfAnnihilationZone extends Functions
{
	protected static Logger _log = Logger.getLogger(SeedOfAnnihilationZone.class.getName());
	// Buffs
	private static final int[] ZONE_BUFFS = { 0, 6443, 6444, 6442 };
	private static final int[] ZONE_BUFFS_NPC = { 0, 6440, 6441, 6439 };
	private static final int[][] ZONE_BUFFS_LIST = { {1,2,3},{1,3,2},{2,1,3},{2,3,1},{3,2,1},{3,1,2} };
	// Region = 0: Bistakon, 1: Reptilikon, 2: Cokrakon
	private static SeedRegion[] _regionsData = new SeedRegion[3];
	// Timer
	private static Long _seedsNextStatusChange;
	// Npc
	private static final int ANNIHILATION_FURNACE = 18928;	
	// Other
	private static final FastMap<Integer, int[]> _teleportZones = new FastMap<Integer, int[]>();
	private static ZoneListener _zoneListener = new ZoneListener();
	private static final ReentrantLock lock = new ReentrantLock();

	public static void init()
	{
		lock.lock();
		try
		{
			loadSeed(); //Load Region
			loadEffectZone(); //Load Zone Buffs
			for(int i : _teleportZones.keySet()) // add zone teleport
			{
				L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.other, i, true);
				_zone.deleteSkill();	// Delete old skill for zone.
				_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
			}
			_log.info("SeedOfAnnihilationManager: Load Manager.");
		}
		finally
		{
			lock.unlock();
		}
	}
	
	// class for Region.
	private static class SeedRegion
	{
		public int[] elite_mob_ids;
		public int buff_zone;
		public int[][] af_spawns;
		public L2NpcInstance[] af_npcs = new L2NpcInstance[2];
		public int activeBuff = 0;
		
		public SeedRegion(int[] emi, int bz, int[][] as)
		{
			elite_mob_ids = emi;
			buff_zone = bz;
			af_spawns = as;
		}
	}	

	static
	{
		_teleportZones.put(60002, new int[]{ -213175, 182648, -11020 });
		_teleportZones.put(60003, new int[]{ -181217, 186711, -10562 });
		_teleportZones.put(60004, new int[]{ -180211, 182984, -15186 });
		_teleportZones.put(60005, new int[]{ -179275, 186802, -10748 });
	}

	public static void loadSeed() // Load Region
	{
		// 14_23_beastacon_for_melee 34
		// Bistakon data
		_regionsData[0] = new SeedRegion(new int[]{ 22750, 22751, 22752, 22753 },
				60006, new int[][]{ {-180450,185507,-10544,11632},{-180005,185489,-10544,11632} });

		// 13_23_cocracon_for_archer 35
		// Reptilikon data
		_regionsData[1] = new SeedRegion(new int[]{ 22757, 22758, 22759 },
				60007, new int[][]{ {-180971,186361,-10528,11632},{-180758,186739,-10528,11632} });

		// 14_23_raptilicon_for_mage 36
		// Cokrakon data
		_regionsData[2] = new SeedRegion(new int[]{ 22763, 22764, 22765 },
				60008, new int[][]{ {-179600,186998,-10704,11632},{-179295,186444,-10704,11632} });

		/**
		// Reptilikon data
		_regionsData[1] = new SeedRegion(new int[]{ 22757, 22758, 22759 },
				60007, new int[][]{ {-179600,186998,-10704,11632},{-179295,186444,-10704,11632} });
		
		// Cokrakon data
		_regionsData[2] = new SeedRegion(new int[]{ 22763, 22764, 22765 },
				60008, new int[][]{ {-180971,186361,-10528,11632},{-180758,186739,-10528,11632} });
		**/
		int buffsNow = ServerVariables.getInt("SeedBuffsList", -1);
		//Get change timer
		String var = ServerVariables.getString("SeedNextStatusChange", "");
		if (var.equalsIgnoreCase("") || Long.parseLong(var) < System.currentTimeMillis())
		{
			do
			{
				buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
			}
			while(buffsNow == ServerVariables.getInt("SeedBuffsList", -1));

			ServerVariables.set("SeedBuffsList", String.valueOf(buffsNow));
			_seedsNextStatusChange = getNextSeedsStatusChangeTime();
			ServerVariables.set("SeedNextStatusChange", String.valueOf(_seedsNextStatusChange));
		}
		else
			_seedsNextStatusChange = Long.parseLong(var);
		for(int i = 0; i < _regionsData.length; i++)
			_regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];
	}

	//Get time for change region buffs.
	private static Long getNextSeedsStatusChangeTime()
	{
		Calendar reenter = Calendar.getInstance();
		reenter.set(Calendar.SECOND, 0);
		reenter.set(Calendar.MINUTE, 0);
		reenter.set(Calendar.HOUR_OF_DAY, 13);
		reenter.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		if (reenter.getTimeInMillis() <= System.currentTimeMillis())
			reenter.add(Calendar.DAY_OF_MONTH, 7);
		return reenter.getTimeInMillis();
	}	
	
	//Apply effect to zone
	private static void loadEffectZone()
	{
		for(int i = 0; i < _regionsData.length; i++)
		{
			for(int j = 0; j < _regionsData[i].af_spawns.length; j++)
			{
                _regionsData[i].af_npcs[j] = spawn(new Location(_regionsData[i].af_spawns[j][0], _regionsData[i].af_spawns[j][1], _regionsData[i].af_spawns[j][2], _regionsData[i].af_spawns[j][3]),  ANNIHILATION_FURNACE);
                _regionsData[i].af_npcs[j].setNpcState(_regionsData[i].activeBuff);
			}
			L2Skill skill = SkillTable.getInstance().getInfo(ZONE_BUFFS[_regionsData[i].activeBuff], 1);
			L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.other, _regionsData[i].buff_zone, true);
			_zone.deleteSkill();
			_zone.setSkill(skill);

			L2Zone zone2 = ZoneManager.getInstance().getZoneById(ZoneType.other, _regionsData[i].buff_zone + 3, true);	
			L2Skill skill2 = SkillTable.getInstance().getInfo(ZONE_BUFFS_NPC[_regionsData[i].activeBuff], 1);
			zone2.deleteSkill();
			zone2.setSkill(skill2);
		}
		ThreadPoolManager.getInstance().schedule(new ChangeSeedsStatus(), _seedsNextStatusChange - System.currentTimeMillis());
	}

	public static class ZoneListener extends L2ZoneEnterLeaveListener
	{
		public ZoneListener()
		{}

		public void objectEntered(L2Zone zone, L2Object object)
		{
			teleportTo((L2Character) object, zone);		
		}

		public void objectLeaved(L2Zone zone, L2Object object)
		{}
	}

	public static void teleportTo(L2Character cha, L2Zone zone)
	{
		if (cha != null)
		{
			if (_teleportZones.containsKey(zone.getId()))
			{
				int[] teleLoc = _teleportZones.get(zone.getId());
				//Заглушка для 454 квеста.
				GArray<L2NpcInstance> around = cha.getAroundNpc(500, 300);
				if(around != null && !around.isEmpty())
					for(L2NpcInstance npc : around)
						if(npc.getNpcId() == 32738 && npc.getFollowTarget() != null)
						{
							if(npc.getFollowTarget().getObjectId() == cha.getObjectId())
							{
								npc.teleToLocation(teleLoc[0],teleLoc[1],teleLoc[2]);
								npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, cha, ConfigValue.FollowRange);
							}
						}

				cha.teleToLocation(teleLoc[0],teleLoc[1],teleLoc[2]);
			}
		}
	}
	private static class ChangeSeedsStatus extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			int buffsNow = -1;
			do
			{
				buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
			}
			while(buffsNow == ServerVariables.getInt("SeedBuffsList", -1));

			ServerVariables.set("SeedBuffsList", String.valueOf(buffsNow));
			_seedsNextStatusChange = getNextSeedsStatusChangeTime();
			ServerVariables.set("SeedNextStatusChange", String.valueOf(_seedsNextStatusChange));
			for(int i = 0; i < _regionsData.length; i++)
			{
				_regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];
				
				for(L2NpcInstance af : _regionsData[i].af_npcs)
					af.setNpcState(_regionsData[i].activeBuff);
				
				L2Zone zone = ZoneManager.getInstance().getZoneById(ZoneType.other, _regionsData[i].buff_zone, true);
				zone.deleteSkill();
				L2Skill skill = SkillTable.getInstance().getInfo(ZONE_BUFFS[_regionsData[i].activeBuff], 1);
				zone.setSkill(skill);

				L2Zone zone2 = ZoneManager.getInstance().getZoneById(ZoneType.other, _regionsData[i].buff_zone + 3, true);
				zone2.deleteSkill();
				L2Skill skill2 = SkillTable.getInstance().getInfo(ZONE_BUFFS_NPC[_regionsData[i].activeBuff], 1);
				zone2.setSkill(skill2);
			}
			ThreadPoolManager.getInstance().schedule(new ChangeSeedsStatus(), _seedsNextStatusChange - System.currentTimeMillis());
		}
	}
}
