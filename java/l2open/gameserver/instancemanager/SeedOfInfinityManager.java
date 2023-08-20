package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class SeedOfInfinityManager
{
	private static final Logger _log = Logger.getLogger(SeedOfInfinityManager.class.getName());
	private static final Location[] MouthLocs = new Location[] { new Location(-183002, 205944, -12896), new Location(-183586, 205947, -12896), new Location(-183589, 206510, -12896), new Location(-182996, 206510, -12896) };
	private static ArrayList<L2NpcInstance> Energies = new ArrayList<L2NpcInstance>();
	private static ScheduledFuture<?> thrid = null;
	private static final Location[] gatherTPLoc = new Location[] { new Location(-179537, 209551, -15504), new Location(-179779, 212540, -15520), new Location(-177028, 211135, -15520), new Location(-176355, 208043, -15520), new Location(-179284, 205990, -15520), new Location(-182268, 208218, -15520), new Location(-182069, 211140, -15520), new Location(-176036, 210002, -11948), new Location(-176039, 208203, -11949), new Location(-183288, 208205, -11939), new Location(-183290, 210004, -11939), new Location(-187776, 205696, -9536), new Location(-186327, 208286, -9536), new Location(-184429, 211155, -9536), new Location(-182811, 213871, -9504), new Location(-180921, 216789, -9536), new Location(-177264, 217760, -9536), new Location(-173727, 218169, -9536) };
	private static final ArrayList<L2NpcInstance> npcs = new ArrayList<L2NpcInstance>();
	private static SeedOfInfinityManager _instance;

	public static SeedOfInfinityManager getInstance()
	{
		if (_instance == null)
			_instance = new SeedOfInfinityManager();
		return _instance;
	}

	private SeedOfInfinityManager()
	{
		init();
		_log.info("Seed of Infinity Manager: Current Cycle is " + getCurrentCycle());
	}
	
	public static void enterToGathering(L2Player player)
	{
		player.teleToLocation(gatherTPLoc[Rnd.get(gatherTPLoc.length)]);
	}

	private static void setCycle(int cycle)
	{
		ServerVariables.set("SeedOfInfinity_cycle", cycle);
		SwitchCond(cycle);
	}

	public static void addAttackSuffering()
	{
		if (getCurrentCycle() == 1)
		{
			AddAttackPoints(1);
			CheckStage();
		}
	}

	protected static L2NpcInstance spawn(Location loc, int npcId)
	{
		L2NpcTemplate templ = NpcTable.getTemplate(npcId);
		try
		{
			L2Spawn spawn = new L2Spawn(templ);
			spawn.setRespawnDelay(Rnd.get(300000, 300000));
			spawn.setAmount(1);
			spawn.setLoc(loc);
			spawn.startRespawn();
			return spawn.doSpawn(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static void DeleteItem(Inventory inv, int count)
	{
		long counts = inv.getCountOf(count);
		if(counts > 0)
			inv.destroyItemByItemId(count, counts, true);
	}

	private static void CheckStage()
	{
		switch (getCurrentCycle())
		{
			case 1:
				if(getAttackPoints() < 20)
					break;
				setCycle(2);
				SetAttackPoints(0);
				break;
			case 2:
				if (getAttackPoints() < ConfigValue.SeedofInfinityEkimusKill)
					break;
				SetAttackPoints(0);
				Util.cancelFuture(false, new Future[] { thrid });
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(3), 3600000);
				break;
			case 3:
				ServerVariables.set("SeedOfInfinity_nextCycleTime", System.currentTimeMillis() + 86400000);
				Util.cancelFuture(false, new Future[] { thrid });
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(4), 86400000);
				break;
			case 4:
				int i = Rnd.get(172800000, 345600000);
				ServerVariables.set("SeedOfInfinity_nextCycleTime", System.currentTimeMillis() + i);
				Util.cancelFuture(false, new Future[] { thrid });
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(5), i);
				break;
			default:
				int j = Rnd.get(172800000, 345600000);
				SetAttackPoints(0);
				ServerVariables.set("SeedOfInfinity_nextCycleTime", System.currentTimeMillis() + j);
				Util.cancelFuture(false, new Future[] { thrid });
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(1), j);
				break;
		}
	}

	public static void deleteEnergies()
    {
        for (L2NpcInstance npc : Energies)
            npc.deleteMe();
        Energies.clear();
    }
	private static void SetAttackPoints(int a)
	{
		ServerVariables.set("SeedOfInfinity_attackPoints", a);
	}

	public static void init()
	{
		long l = ServerVariables.getLong("SeedOfInfinity_nextCycleTime", 0);
		if (getCurrentCycle() > 2)
		{
			if (l < System.currentTimeMillis())
			{
				if (getCurrentCycle() >= 5)
					thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(1), 500);
				else
					thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(getCurrentCycle() + 1), 500);
			}
			else if (getCurrentCycle() >= 5)
			{
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(1), l - System.currentTimeMillis());
			}
			else
			{
				SwitchCond(getCurrentCycle());
				thrid = ThreadPoolManager.getInstance().schedule(new SwitchStage(getCurrentCycle() + 1), l - System.currentTimeMillis());
			}
			SpawnNpcs(getCurrentCycle());
			Date data = new Date(ServerVariables.getLong("SeedOfInfinity_nextCycleTime", 0));
			_log.info("SeedOfInfinityManager: cycle: " + getCurrentCycle() + " / next switch:" + Util.datetimeFormatter.format(data));
		}
		else
		{
			SpawnNpcs(getCurrentCycle());
		}
	}

	public static int getCurrentCycle()
	{
		return Math.min(5, ServerVariables.getInt("SeedOfInfinity_cycle", 1));
	}

	public static void addEkimusKill()
	{
		if(getCurrentCycle() == 2)
		{
			AddAttackPoints(1);
			CheckStage();
		}
	}

	public static void spawnEnergies()
    {
        deleteEnergies();
        int x, y;
        for (Location l : gatherTPLoc)
            for (int i = 0; i < 4; i++)
            {
                x = Rnd.get(-500, 500);
                y = Rnd.get(-500, 500);
                Energies.add(spawn(new Location(l.x + x, l.y + y, l.z), Rnd.get(18678, 18683)));
            }
    }

	public static void addAttackErrosion()
	{
		if(getCurrentCycle() == 1)
		{
			AddAttackPoints(2);
			CheckStage();
		}
	}

    private static final int KECERUS_MARK_1 = 13691;
    private static final int KECERUS_MARK_2 = 13692;

    private static void ClearItems()
    {
        for (L2Player player : L2ObjectsStorage.getPlayers())
        {
            DeleteItem(player.getInventory(), KECERUS_MARK_1);
            DeleteItem(player.getInventory(), KECERUS_MARK_2);
        }

        ThreadConnection con = null;
        FiltredPreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT DISTINCT owner_id FROM items WHERE item_id IN(13691,13692)");
            rset = statement.executeQuery();

            FiltredPreparedStatement statement2 = null;
            while (rset.next())
            {
                statement2 = con.prepareStatement("INSERT INTO character_variables VALUES(?, 'user-var', 'NeedToDeleteSOIItems', 0, 'true', -1)");
                statement2.setInt(1, rset.getInt(1));
                statement2.execute();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            DatabaseUtils.closeDatabaseCSR(con, statement, rset);
        }
    }

    private static void AddAttackPoints(int n)
    {
        int k = getAttackPoints();
        SetAttackPoints(k + n);
    }

	private static void SwitchCond(int n)
	{
		switch (n)
		{
			case 1:
				ClearItems();
				break;
			case 3:
				spawnEnergies();
				break;
			case 4:
				deleteEnergies();
		}
		SpawnNpcs(n);
	}

	private static int getAttackPoints()
	{
		return ServerVariables.getInt("SeedOfInfinity_attackPoints", 0);
	}

	private static void SpawnNpcs(int stage)
    {
        for (L2NpcInstance n : npcs)
            n.deleteMe();
        npcs.clear();

        int MouthNpcId = 32537; //Opened
        if (stage == 3)
            MouthNpcId = 32538;
        for (Location loc : MouthLocs)
            npcs.add(Functions.spawn(loc, MouthNpcId));
    }
  	public static StringBuilder startAppend(final int sizeHint, final String... strings)
	{
		final int length = getLength(strings);
		final StringBuilder sbString = new StringBuilder(sizeHint > length ? sizeHint : length);
		for (final String string : strings)
			sbString.append(string);
		return sbString;
	}

	public static void append(final StringBuilder sbString, final String... strings)
	{
		sbString.ensureCapacity(sbString.length() + getLength(strings));
		for (final String string : strings)
			sbString.append(string);
	}

	private static int getLength(final String[] strings)
	{
		int length = 0;
		for (final String string : strings)
			length += string.length();
		return length;
	}

	private static class SwitchStage extends l2open.common.RunnableImpl
    {
        int newStage;

        SwitchStage(int n)
        {
            newStage = n;
        }

        @Override
        public void runImpl()
        {
            setCycle(newStage);
            CheckStage();
        }
    }
}