package l2open.gameserver.instancemanager;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.util.*;

import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantLock;
	
public class BloodAltarManager
{
	private static final Logger _log = Logger.getLogger(BloodAltarManager.class.getName());
	private static BloodAltarManager _instance;
	private static final long delay = 30 * 60 * 1000L;
	private static long bossRespawnTimer = 0;
	private static boolean bossesSpawned = false;
	private static final ReentrantLock lock = new ReentrantLock();
	private static L2NpcInstance bloodaltar_boss[] = new L2NpcInstance[25];
	private static L2NpcInstance bloodaltar_a_npc[] = new L2NpcInstance[51];
	private static L2NpcInstance bloodaltar_d_npc[] = new L2NpcInstance[51];
	private static final Location[] bloodaltar_alive_npc =
	{
		new Location(1880, 21480, -3375, 54112, 4324),
		new Location(-86440, 151624, -3083, 0, 4325),
		new Location(81912, -139288, -2286, 16000, 4325),
		new Location(-86744, 151704, -3083, 32768, 4325),
		new Location(152584, 24840, -2120, 24575, 4325),
		new Location(-45544, -118328, -232, 32768, 4324),
		new Location(130280, -181096, -3312, 57343, 4325),
		new Location(-14376, 120680, -3006, 32768, 4324),
		new Location(119992, 219016, -3384, 52200, 4325),
		new Location(10968, -24248, -3640, 45796, 4325),
		new Location(82072, -139208, -2286, 6000, 4324),
		new Location(119976, 219320, -3384, 32768, 4325),
		new Location(-14280, 120808, -3001, 22973, 4325),
		new Location(152856, 24840, -2128, 8191, 4325),
		new Location(152728, 24920, -2120, 16383, 4324),
		new Location(130136, -181176, -3312, 49151, 4324),
		new Location(16856, 148216, -3276, 3355, 4325),
		new Location(80040, 47176, -3176, 16000, 4325),
		new Location(40280, 53784, -3328, 0, 4325),
		new Location(152296, -57912, -3447, 32768, 4324),
		new Location(16808, 148488, -3279, 14324, 4325),
		new Location(-92648, 244728, -3571, 32768, 4325),
		new Location(28152, -49048, -1312, 8191, 4325),
		new Location(16920, 148360, -3282, 54112, 4324),
		new Location(129992, -181096, -3312, 40959, 4325),
		new Location(80328, 47192, -3176, 53375, 4325),
		new Location(40056, 53944, -3329, 40961, 4325),
		new Location(80184, 47272, -3178, 6000, 4324),
		new Location(10840, -24184, -3640, 40959, 4324),
		new Location(152136, -57848, -3447, 40961, 4325),
		new Location(80952, 142536, -3557, 0, 4324),
		new Location(119896, 219160, -3392, 32768, 4324),
		new Location(-92376, 244648, -3572, 0, 4325),
		new Location(-45448, -118472, -232, 40961, 4325),
		new Location(80824, 142392, -3554, 49152, 4325),
		new Location(152440, -57816, -3447, 0, 4325),
		new Location(10824, -24024, -3640, 29412, 4325),
		new Location(2008, 21592, -3372, 3355, 4325),
		new Location(-45464, -118184, -232, 0, 4325),
		new Location(82216, -139288, -2286, 53375, 4325),
		new Location(27864, -49048, -1312, 24575, 4325),
		new Location(-104184, 45208, -1485, 0, 4325),
		new Location(-92552, 244600, -3571, 17559, 4324),
		new Location(28008, -48984, -1328, 16383, 4324),
		new Location(-104168, 44904, -1483, 40961, 4325),
		new Location(40120, 53784, -3336, 32768, 4324),
		new Location(-104264, 45064, -1489, 32768, 4324),
		new Location(80856, 142680, -3553, 0, 4325),
		new Location(1720, 21560, -3372, 14324, 4325),
		new Location(-86568, 151736, -3083, 17559, 4324),
		new Location(-14296, 120520, -3006, 49152, 4325)
	};

	private static final Location[] bloodaltar_dead_npc =
	{
		new Location(1880, 21480, -3375, 54112, 4327),
		new Location(-86440, 151624, -3083, 0, 4328),
		new Location(81912, -139288, -2286, 16000, 4328),
		new Location(-86744, 151704, -3083, 32768, 4328),
		new Location(152584, 24840, -2120, 24575, 4328),
		new Location(-45544, -118328, -232, 32768, 4327),
		new Location(130280, -181096, -3312, 57343, 4328),
		new Location(-14376, 120680, -3006, 32768, 4327),
		new Location(119992, 219016, -3384, 52200, 4328),
		new Location(10968, -24248, -3640, 45796, 4328),
		new Location(82072, -139208, -2286, 6000, 4327),
		new Location(119976, 219320, -3384, 32768, 4328),
		new Location(-14280, 120808, -3001, 22973, 4328),
		new Location(152856, 24840, -2128, 8191, 4328),
		new Location(152728, 24920, -2120, 16383, 4327),
		new Location(130136, -181176, -3312, 49151, 4327),
		new Location(16856, 148216, -3276, 3355, 4328),
		new Location(80040, 47176, -3176, 16000, 4328),
		new Location(40280, 53784, -3328, 0, 4328),
		new Location(152296, -57912, -3447, 32768, 4327),
		new Location(16808, 148488, -3279, 14324, 4328),
		new Location(-92648, 244728, -3571, 32768, 4328),
		new Location(28152, -49048, -1312, 8191, 4328),
		new Location(16920, 148360, -3282, 54112, 4327),
		new Location(129992, -181096, -3312, 40959, 4328),
		new Location(80328, 47192, -3176, 53375, 4328),
		new Location(40056, 53944, -3329, 40961, 4328),
		new Location(80184, 47272, -3178, 6000, 4327),
		new Location(10840, -24184, -3640, 40959, 4327),
		new Location(152136, -57848, -3447, 40961, 4328),
		new Location(80952, 142536, -3557, 0, 4327),
		new Location(119896, 219160, -3392, 32768, 4327),
		new Location(-92376, 244648, -3572, 0, 4328),
		new Location(-45448, -118472, -232, 40961, 4328),
		new Location(80824, 142392, -3554, 49152, 4328),
		new Location(152440, -57816, -3447, 0, 4328),
		new Location(10824, -24024, -3640, 29412, 4328),
		new Location(2008, 21592, -3372, 3355, 4328),
		new Location(-45464, -118184, -232, 0, 4328),
		new Location(82216, -139288, -2286, 53375, 4328),
		new Location(27864, -49048, -1312, 24575, 4328),
		new Location(-104184, 45208, -1485, 0, 4328),
		new Location(-92552, 244600, -3571, 17559, 4327),
		new Location(28008, -48984, -1328, 16383, 4327),
		new Location(-104168, 44904, -1483, 40961, 4328),
		new Location(40120, 53784, -3336, 32768, 4327),
		new Location(-104264, 45064, -1489, 32768, 4327),
		new Location(80856, 142680, -3553, 0, 4328),
		new Location(1720, 21560, -3372, 14324, 4328),
		new Location(-86568, 151736, -3083, 17559, 4327),
		new Location(-14296, 120520, -3006, 49152, 4328)
	};

	private static final Location[][] bossGroups =
	{
		{ //bloodaltar_boss_aden,
			new Location(152936, 25016, -2154, 28318, 25793),
			new Location(153144, 24664, -2139, 32767, 25794),
			new Location(152392, 24712, -2158, 24575, 25797)
		},
		{ //bloodaltar_boss_darkelf
			new Location(2184, 21672, -3375, 63477, 25750)
		},
		{ //bloodaltar_boss_dion
			new Location(16680, 147992, -3267, 63477, 25753),
			new Location(16872, 148680, -3319, 55285, 25754),
			new Location(17000, 148008, -3254, 55285, 25757)
		},
		{ //bloodaltar_boss_dwarw
			new Location(130376, -180664, -3331, 21220, 25782),
			new Location(129800, -180760, -3352, 13028, 25800)
		},
		{ //bloodaltar_boss_giran
			new Location(80888, 142872, -3552, 6133, 25760),
			new Location(80632, 142200, -3559, 8191, 25763),
			new Location(80312, 142568, -3573, 4836, 25766)
		},
		{ //bloodaltar_boss_gludin
			new Location(-86312, 151544, -3083, 4836, 25735),
			new Location(-87000, 151720, -3084, 6133, 25738),
			new Location(-86568, 152040, -3098, 6133, 25741)
		},
		{ //bloodaltar_boss_gludio
			new Location(-14264, 120904, -3008, 27931, 25744),
			new Location(-14440, 120504, -3016, 13028, 25747)
		},
		{ //bloodaltar_boss_goddart
			new Location(152488, -57208, -3431, 47429, 25787),
			new Location(152568, -58008, -3477, 49151, 25790)
		},
		{ //bloodaltar_boss_heine
			new Location(119896, 218872, -3423, 22517, 25773),
			new Location(120296, 219480, -3410, 31287, 25776)
		},
		{ //bloodaltar_boss_orc
			new Location(-45128, -118088, -244, 23095, 25779)
		},
		{ //bloodaltar_boss_oren
			new Location(80328, 46792, -3189, 36123, 25767),
			new Location(80520, 47368, -3193, 36736, 25770)
		},
		{ //bloodaltar_boss_schutgart
			new Location(82168, -139768, -2294, 44315, 25784)
		}
     };

	public static BloodAltarManager getInstance()
	{
		if(_instance == null)
			_instance = new BloodAltarManager();
		return _instance;
	}

	public BloodAltarManager()
	{
		_log.info("Blood Altar Manager: Initializing...");
		if(ConfigValue.EnableBloodAltarManager)
		{
			manageNpcs(true);
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new l2open.common.RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if(Rnd.chance(30) && bossRespawnTimer < System.currentTimeMillis())
						if(!bossesSpawned)
						{
							manageNpcs(false);
							manageBosses(true);
							bossesSpawned = true;
						}
						else
						{
							manageBosses(false);
							manageNpcs(true);
							bossesSpawned = false;
						}
				}
			}, delay, delay);
		}
	}

	private static void manageNpcs(boolean spawnAlive)
	{
		if(ConfigValue.EnableSpawnBloodAltarNpc)
		{
			if(spawnAlive)
			{
				int i = 0;
				for(Location loc : bloodaltar_alive_npc)
				{
					bloodaltar_a_npc[i] = spawn(loc.id, loc, 60);
					i++;
				}
				//-----------------------------------------------------
				for(L2NpcInstance npc : bloodaltar_d_npc)
					if(npc != null)
						npc.deleteMe();
			}
			else
			{
				int i = 0;
				for(Location loc : bloodaltar_dead_npc)
				{
					bloodaltar_d_npc[i] = spawn(loc.id, loc, 60);
					i++;
				}
				//-----------------------------------------------------
				for(L2NpcInstance npc : bloodaltar_a_npc)
					if(npc != null)
						npc.deleteMe();
			}
		}
	}

	private static void manageBosses(boolean spawn)
	{
		if(spawn)
		{
			int i = 0;
			for(Location[] locs : bossGroups)
				for(Location loc : locs)
				{
					if(i == 0)
						bloodaltar_boss[i] = spawnRB(loc.id, loc, 0);
					else
						bloodaltar_boss[i] = spawn(loc.id, loc, 0);
					i++;
				}
		}
		else
		{
			bossRespawnTimer = System.currentTimeMillis() + 4 * 3600 * 1000;
			for(L2NpcInstance npc : bloodaltar_boss)
				if(npc != null)
					npc.deleteMe();
		}
	}

	private static L2NpcInstance spawn(int npcId, Location loc, int resp)
	{
		L2NpcInstance npc = null;
		lock.lock();
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setAmount(1);
			spawn.setRespawnDelay(resp, 0);
			if(resp > 0)
				spawn.startRespawn();
			spawn.setLoc(loc);
			npc = spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		return npc;
	}

	private static L2RaidBossInstance spawnRB(int npcId, Location loc, int resp)
	{
		L2RaidBossInstance npc = null;
		lock.lock();
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(npcId));
			spawn.setAmount(1);
			spawn.setRespawnDelay(resp, 0);
			spawn.setLoc(loc);
			if(resp > 0)
				spawn.startRespawn();
			if(NpcTable.getTemplate(npcId).type.equals("L2RaidBoss"))
				npc = (L2RaidBossInstance) spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		return npc;
	}
}
