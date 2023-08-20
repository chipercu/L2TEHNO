package l2open.gameserver.instancemanager;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.Announcements;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.SpawnTable;
import l2open.util.Location;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Logger;

public class HellboundManager
{
	protected static Logger _log = Logger.getLogger(HellboundManager.class.getName());
	private boolean _megalithsCompleted = false;
	private static HellboundManager _instance;
	private int _level = 0;
	private long _currentPoints = 0;
	private long _tempPoints = 0;
	private int tempLevel = 0;
    private boolean unlocked = false;

	private static int ghostofderek = 18465;
	private static int Captain = 18466;
	private static int celtus = 22353;
	private static int Solomon = 32355;
	private static int Traitor = 32364;
	private static int Kief = 32354;
	private static int Falk = 32297;
	private static int Buron = 32345;
	private static int Native = 32362;
	private static int Insurgent = 32363;
	private static int Hellinark = 22326;
	private static int MB_GUARD = 18467;

	private static int Keltas = 22341;
	private static Location KeltasSpawn = new Location(-29569, 252834, -3518);
	private static int[] KeltasMinions = { 22342, 22343 };
	private static Location[] LOCS = { new Location(4276, 237245, -3310), new Location(11437, 236788, -1949), new Location(7647, 235672, -1977), new Location(1882, 233520, -3315) };

	private static Location[] KeltasMinionsSpawns = { new Location(-23553, 251677, -3361), new Location(-24381, 251555, -3351), new Location(-25367, 252178, -3252), new Location(-25390, 252405, -3252), new Location(-24222, 252270, -3140), new Location(-24441, 252295, -3085), new Location(-24106, 252711, -3054), new Location(-26930, 251951, -3518), new Location(-27918, 251498, -3518), new Location(-27764, 251124, -3521), new Location(-28415, 251995, -3521), new Location(-28640, 250998, -3518), new Location(-28389, 250050, -3473), new Location(-28610, 250034, -3473), new Location(-28719, 252281, -3518), new Location(-29477, 253089, -3518), new Location(-29360, 252953, -3518), new Location(-29689, 252685, -3518), new Location(-29117, 252925, -3518), new Location(-23496, 251924, -3364), new Location(-23812, 251522, -3368), new Location(-25035, 251961, -3295), new Location(-23869, 252040, -3306), new Location(-24689, 252583, -3043), new Location(-23887, 253034, -3038), new Location(-27273, 251871, -3518), new Location(-27211, 251139, -3518), new Location(-28683, 251590, -3518), new Location(-29615, 253045, -3518), new Location(-29345, 252660, -3518), new Location(-29145, 252709, -3518) };

	private static Location[] QuarrySlavesSpawns = { new Location(-6781, 241040, -1845, 43629), new Location(-6656, 241013, -1838, 42932), new Location(-6337, 242017, -2035, 16865), new Location(-7311, 242725, -2030, 62145), new Location(-6630, 242634, -2085, 32348), new Location(-9115, 243232, -1845, 40387), new Location(-9230, 243278, -1848, 36315), new Location(-9384, 244176, -1859, 33839), new Location(-9367, 244281, -1854, 32725), new Location(-9260, 244864, -1845, 31809), new Location(-9121, 245029, -1850, 9081), new Location(-7540, 244000, -2042, 48351), new Location(-6533, 243238, -2079, 62208), new Location(-7063, 244310, -2044, 64625), new Location(-5090, 243213, -2037, 62334), new Location(-5935, 241772, -2013, 12068), new Location(-5413, 243548, -2055, 34599), new Location(-6382, 244647, -2047, 50680), new Location(-6228, 245361, -2072, 2078), new Location(-4202, 243779, -2018, 19948), new Location(-4712, 244319, -2080, 51707), new Location(-4466, 245445, -2018, 47512), new Location(-4323, 245393, -2022, 46990), new Location(-5567, 245504, -2049, 38083), new Location(-5108, 243938, -2008, 33115), new Location(-3684, 246080, -1864, 39254), new Location(-4663, 242664, -2080, 20560), new Location(-3567, 241491, -1847, 55775), new Location(-3659, 241318, -1849, 58964), new Location(-4674, 240783, -1872, 50670), new Location(-4767, 240878, -1880, 24465) };

	private static Location[] MegalithSpawns = { new Location(-24542, 245792, -3133), new Location(-23839,246056,-3133), new Location(-23713,244358,-3133), new Location(-23224,244524,-3133), new Location(-24709,245186,-3133), new Location(-24394,244379,-3133) };

	public static HellboundManager getInstance()
	{
		if (_instance == null)
			_instance = new HellboundManager();
		return _instance;
	}

	private HellboundManager()
	{
		SQLLoad();
		checkLevel();
		checkSpawn();
		_log.info("Hellbound Manager: Current Level is " + _level);
		_log.info("Hellbound Manager: Current Points is " + _currentPoints);
	}

	public boolean checkIsOpen()
	{
		return unlocked;
	}

	public int getLevel()
	{
		return _level;
	}

	public long getPoints()
	{
		return _currentPoints;
	}

	public void addPoints(long points)
	{
		_currentPoints = _currentPoints + points * ConfigValue.RateHbPoints;
		if(_currentPoints < 0)
			_currentPoints = 0;
		updatePoints(_currentPoints);
	}

	public void updatePoints(long newPoints)
	{
		_currentPoints = newPoints;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE hellbound SET hb_points=? WHERE name=8000");
			statement.setLong(1, _currentPoints);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warning("Hellbound Manager Error: Could not save Hellbound points: "+_currentPoints);
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		checkLevel();
	}

	public void checkLevel()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT hb_points, hb_level, unlocked FROM hellbound");
			rset = statement.executeQuery();
			while (rset.next())
			{
				_tempPoints = rset.getInt("hb_points");
				tempLevel = rset.getInt("hb_level");
                unlocked = rset.getInt("unlocked") == 1;
				_currentPoints = rset.getInt("hb_points");
				if (_tempPoints >= 0 && _currentPoints < 300000) // 1 lvl
					_level = 1;
				if (_tempPoints >= 300000 && _currentPoints < 600000) // 2 lvl
					_level = 2;
				if (_tempPoints >= 600000 && _currentPoints < 1000000) // 3 lvl
					_level = 3;
				if (_tempPoints >= 1000000 && getCanChange()) // 4 lvl
				{
					_level = 4;
					ServerVariables.unset("HellboundCanChangeLevel");
				}
				if (tempLevel == 5) // 5 lvl
					_level = 5;
				if (tempLevel == 6) // 6 lvl ok
					_level = 6;
				if (tempLevel == 7)
					_level = 7;
				if (tempLevel == 8) // 8 lvl ok
					_level = 8;
				if (tempLevel == 9)
					_level = 9;
				if (tempLevel == 10)
					_level = 10;
				if (_tempPoints >= 2600000 && tempLevel >= 10)
					_level = 11;
			}
		}
		catch (SQLException e)
		{
			_log.warning("Hellbound Manager Error: Could not load the hellbound table");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		switch(ConfigValue.GetHellboundLevel)
		{
			case 1:
				_level = 1;
				_tempPoints = 0;
				_currentPoints = 0;
				unlocked = true;
				break;
			case 2:
				_level = 2;
				_tempPoints = 300000;
				_currentPoints = 300000;
				unlocked = true;
				break;
			case 3:
				_level = 3;
				_tempPoints = 600000;
				_currentPoints = 600000;
				unlocked = true;
				break;
			case 4:
				_level = 4;
				_tempPoints = 1000000;
				_currentPoints = 1000000;
				unlocked = true;
				break;
			case 5:
				_level = 5;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 6:
				_level = 6;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 7:
				_level = 7;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 8:
				_level = 8;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 9:
				_level = 9;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 10:
				_level = 10;
				_tempPoints = 2000000;
				_currentPoints = 2000000;
				unlocked = true;
				break;
			case 11:
				_level = 11;
				_tempPoints = 2600000;
				_currentPoints = 2600000;
				unlocked = true;
				break;
		}
		if(tempLevel != _level)
			changeLevel(_level);
	}

	public void changeLevel(int newLevel)
	{
		_level = newLevel;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE hellbound SET hb_level=? WHERE name=8000");
			statement.setInt(1, _level);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warning("Hellbound Manager Error: Could not save Hellbound level");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
		_log.info("Hellbound Manager: Hellbound Level set: " + _level);
		String announce = "The level of Hellbound increased and now: " + _level;
		Announcements.getInstance().announceToAll(announce);

		checkLevel();
		checkSpawn();
	}

	private void checkSpawn()
	{
		int curHBLevel = getLevel();

		if (curHBLevel < 2)
		{
			DoorTable.getInstance().getDoor(19250001).closeMe();
			DoorTable.getInstance().getDoor(19250002).closeMe();
		}
		if (curHBLevel >= 2)
		{
			DoorTable.getInstance().getDoor(19250001).openMe();
			DoorTable.getInstance().getDoor(19250002).openMe();
		}

		if (curHBLevel < 7)
			DoorTable.getInstance().getDoor(20250002).closeMe();
		if (curHBLevel >= 7)
			DoorTable.getInstance().getDoor(20250002).openMe();
		if (curHBLevel < 9)
			DoorTable.getInstance().getDoor(20250001).closeMe();
		if (curHBLevel >= 9)
			DoorTable.getInstance().getDoor(20250001).openMe();
		if (curHBLevel >= 2)
		{
			unSpawn(Falk);
			Functions.spawn(new Location(-19904, 250016, -3240, 12288), Falk);
		}
		if (curHBLevel >= 3 && curHBLevel < 5)
		{
			int resp = Rnd.get(36, 72) * 100;
			if (getLevel() == 4)
				resp *= 4;
			Functions.spawn(KeltasSpawn, Keltas, resp);

			for (Location loc : KeltasMinionsSpawns)
			{
				int RndMobSpawn = KeltasMinions[Rnd.get(KeltasMinions.length)];
				Functions.spawn(loc, RndMobSpawn);
			}
		}
		if (curHBLevel == 4)
			Functions.spawn(new Location(-28058, 256885, -1934, 0), ghostofderek);
		if (curHBLevel > 4)
			unSpawn(ghostofderek);		
		if (curHBLevel < 5)
		{
			unSpawn(Kief);
			unSpawn(Buron);

			Functions.spawn(new Location(-21271, 250238, -3314, 16384), Kief);
			Functions.spawn(new Location(-11954, 236171, -3272, 16380), Buron);
		}

		if (curHBLevel >= 5)
		{
			unSpawn(Kief);
			unSpawn(Buron);

			unSpawn(Keltas);
			unSpawn(22342);
			unSpawn(22343);
			unSpawn(32299);
			unSpawn(32307);

			Functions.spawn(new Location(-28916, 249381, -3472), Solomon);
			Functions.spawn(new Location(-27352, 252387, -3520, 5416), Traitor);
			Functions.spawn(new Location(-28357, 248993, -3472, 16384), Kief);
			Functions.spawn(new Location(-28567, 248994, -3472, 16384), Buron);

			if (curHBLevel >= 5)
			{
				Functions.spawn(new Location(-28619,249254,-3472), Native);
				Functions.spawn(new Location(-26837,251044,-3520), Native);
				Functions.spawn(new Location(-27865,251575,-3520), Native);
				Functions.spawn(new Location(-28678,250618,-3520), Native);
				Functions.spawn(new Location(-28472,251972,-3520), Native);
				Functions.spawn(new Location(-27247,251891,-3520), Native);
				Functions.spawn(new Location(-28141,251949,-3520), Native);
				Functions.spawn(new Location(-27114,252005,-3520), Native);
				Functions.spawn(new Location(-27469,252074,-3520), Native);
				Functions.spawn(new Location(-28715,250461,-3520), Native);
				Functions.spawn(new Location(-29537,252963,-3520), Native);
				Functions.spawn(new Location(-28858,249258,-3472), Native);
				Functions.spawn(new Location(-29724,253033,-3520), Native);
				Functions.spawn(new Location(-25376,252208,-3256), Insurgent);
				Functions.spawn(new Location(-25376,252368,-3256), Insurgent);
				Functions.spawn(new Location(-28310,250388,-3472), Insurgent);
				Functions.spawn(new Location(-28400,249977,-3472), Insurgent);
				Functions.spawn(new Location(-28592,249976,-3472), Insurgent);
				Functions.spawn(new Location(-29123,250387,-3472), Insurgent);
			}

			if (curHBLevel == 5)
			{
				Functions.spawn(new Location(-6072, 249304, -3120, 21052), 32307);

				for (Location loc : QuarrySlavesSpawns)
					Functions.spawn(loc, 32299, 180);
			}
		}

		if (curHBLevel < 6)
			changeBoxesSpawnState(false);
		if (curHBLevel >= 6)
		{
			ServerVariables.unset("HellboundRabInstance");		
			changeBoxesSpawnState(true);
		}
		if (curHBLevel == 6)
		{
			Functions.spawn(new Location(-24430,244764,-3133), Hellinark, 10800);
			int curr = getMegalith(); 
			if(curr < 6)
			for (Location loc : MegalithSpawns)
				Functions.spawn(loc, 18484);
		}
		if (curHBLevel == 7)
		{
			boolean curr = getmbguard();
			if(curr)
			{
				Functions.spawn(new Location(-410, 234647, -3248), MB_GUARD, 300);
				Functions.spawn(new Location(-320, 234737, -3251), MB_GUARD, 300);
				Functions.spawn(new Location(-234, 234818, -3259), MB_GUARD, 300);
				Functions.spawn(new Location(-175, 234921, -3256), MB_GUARD, 300);
				Functions.spawn(new Location(-164, 235023, -3270), MB_GUARD, 300);
				Functions.spawn(new Location(-213, 235116, -3264), MB_GUARD, 300);
				Functions.spawn(new Location(-341, 235117, -3248), MB_GUARD, 300);
				Functions.spawn(new Location(-472, 235038, -3248), MB_GUARD, 300);
			}
			if(!curr)
			{
				ServerVariables.unset("mb_guard");
				unSpawn(MB_GUARD);
			}
		}
		
		if (curHBLevel < 7)
			changeChimeraSpawnState(false);
			
		if (curHBLevel == 7)
		{
			ServerVariables.unset("MegalithsCompleted");
			ServerVariables.unset("megaliths_killed");
			ServerVariables.unset("megaliths_portals");
			unSpawn(Hellinark);
			unSpawn(18484);
			changeChimeraSpawnState(true);
			int CeltrespTime = Rnd.get(18, 36) * 100;
			Functions.spawn(LOCS[Rnd.get(LOCS.length)], celtus, CeltrespTime);
		}

		if (curHBLevel >= 7)
		{
			Collection<L2Spawn> worldObjects = SpawnTable.getInstance().getSpawnTable();
			for (L2Spawn i : worldObjects)
			{
				int npcId = i.getNpcId();
				if (npcId == 18467)
				{
					i.stopRespawn();
					L2NpcInstance curNpc = i.getLastSpawn();
					curNpc.onDecay();
				}
			}
		}
		if (curHBLevel == 8)
		{
			ServerVariables.unset("life_points");
			Functions.spawn(new Location(4912, 244032, -1933, 36561), Captain, 86400);
		}
		
		if (curHBLevel >= 9)
			unSpawn(Captain);
	}

	private void SQLLoad()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement trigger = null;
		FiltredPreparedStatement insertion = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			trigger = con.prepareStatement("SELECT name FROM hellbound WHERE dummy=0");
			ResultSet trigger1 = trigger.executeQuery();
			int ZoneName = 100;
			while (trigger1.next())
				ZoneName = trigger1.getInt("name");
			con.close();

			if (ZoneName != 8000)
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				insertion = con.prepareStatement("INSERT INTO hellbound (name,hb_points,hb_level,unlocked,dummy) VALUES (?,?,?,?,?)");
				insertion.setInt(1, 8000);
				insertion.setInt(2, 0);
				insertion.setInt(3, 0);
				insertion.setInt(4, 0);
				insertion.setInt(5, 0);
				insertion.executeUpdate();
				insertion.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private void changeChimeraSpawnState(boolean spawning)
	{
		int curHBLevel = getLevel();
	
		if (curHBLevel >= 7)
		{
			Collection<L2Spawn> worldObjects = SpawnTable.getInstance().getSpawnTable();
			for (L2Spawn i : worldObjects)
			{
				int npcId = i.getNpcId();
				if (npcId == 22349 || npcId == 22350 || npcId == 22351 || npcId == 22352)
				{
					Location loc = new Location(i.getLocx(), i.getLocy(), i.getLocz(), i.getHeading());

					if (spawning)
					{
						int resp = i.getRespawnDelay();
						Functions.spawn(loc, npcId, resp);
					}
					else
					{
						i.stopRespawn();
						L2NpcInstance curNpc = i.getLastSpawn();
						curNpc.onDecay();
					}
				}
			}
		}
	}

	private void changeBoxesSpawnState(boolean spawning)
	{
		Collection<L2Spawn> worldObjects = SpawnTable.getInstance().getSpawnTable();
		for (L2Spawn i : worldObjects)
		{
			int npcId = i.getNpcId();
			if (npcId == 32361)
			{
				Location loc = new Location(i.getLocx(), i.getLocy(), i.getLocz(), i.getHeading());

				if (spawning)
				{
					int resp = i.getRespawnDelay();
					Functions.spawn(loc, npcId, resp);
				}
				else
				{
					i.stopRespawn();
					L2NpcInstance curNpc = i.getLastSpawn();
					curNpc.onDecay();
				}
			}
		}
	}

	public void unSpawn(int id)
	{
		for(L2NpcInstance npc : L2ObjectsStorage.getAllByNpcId(id, true))
			npc.deleteMe();
	}

	public boolean getCanChange()
	{
		return ServerVariables.getBool("HellboundCanChangeLevel", true);
	}
	
	public boolean checkMegalithsCompleted()
	{
		return _megalithsCompleted;
	}

	public void setMegalithsCompleted(boolean value)
	{
		_megalithsCompleted = value;
	}
	
	public static int getMegalith()
	{
		return ServerVariables.getInt("megaliths_portals", 0);
	}
	
	public static boolean getmbguard()
	{
		return ServerVariables.getBool("mbguard", true);
	}
}