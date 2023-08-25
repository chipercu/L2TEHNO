package l2open.gameserver.cache;

import javolution.util.FastMap;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class CrestCache
{
	private static final Logger _log = Logger.getLogger(CrestCache.class.getName());

	// Требуется для получения ID значка по ID клана
	private static FastMap<Integer, Integer> _cachePledge = new FastMap<Integer, Integer>().setShared(true);
	private static FastMap<Integer, Integer> _cachePledgeLarge = new FastMap<Integer, Integer>().setShared(true);
	private static FastMap<Integer, Integer> _cacheAlly = new FastMap<Integer, Integer>().setShared(true);

	// Integer - ID значка, byte[] - сам значек
	private static FastMap<Integer, byte[]> _cachePledgeHashed = new FastMap<Integer, byte[]>().setShared(true);
	private static FastMap<Integer, byte[]> _cachePledgeLargeHashed = new FastMap<Integer, byte[]>().setShared(true);
	private static FastMap<Integer, byte[]> _cacheAllyHashed = new FastMap<Integer, byte[]>().setShared(true);
	public static byte[] header_ally_crest = 		new byte[]{0x44, 0x44, 0x53, 0x20, 0x7C, 0x00, 0x00, 0x00, 0x07, 0x10, 0x08, 0x00, 0x10, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, (byte)0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x44, 0x58, 0x54, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static byte[] header_clan_crest = 		new byte[]{0x44, 0x44, 0x53, 0x20, 0x7C, 0x00, 0x00, 0x00, 0x07, 0x10, 0x08, 0x00, 0x10, 0x00, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, (byte)0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x44, 0x58, 0x54, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static byte[] header_clan_larg_crest = 	new byte[]{0x44, 0x44, 0x53, 0x20, 0x7C, 0x00, 0x00, 0x00, 0x07, 0x10, 0x08, 0x00, 0x40, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x44, 0x58, 0x54, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	private static List<Integer> _pledgeIds = new ArrayList<Integer>();
	private static List<Integer> _largePledgeIds = new ArrayList<Integer>();
	private static List<Integer> _allyIds = new ArrayList<Integer>();

	public static void load()
	{
		_cachePledge.clear();
		_cachePledgeLarge.clear();
		_cacheAlly.clear();
		_cachePledgeHashed.clear();
		_cachePledgeLargeHashed.clear();
		_cacheAllyHashed.clear();

		int counter = 0;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet list = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("SELECT `clan_id`,`crest` FROM `clan_data` WHERE `crest` IS NOT NULL");
			list = statement.executeQuery();
			while(list.next())
			{
				counter++;
				int hash = mhash(list.getBytes("crest"));
				_cachePledge.put(list.getInt("clan_id"), hash);
				_cachePledgeHashed.put(hash, list.getBytes("crest"));
				//_log.info("CrestCache: hash="+hash+" clan_id="+list.getInt("clan_id"));
			}
			DatabaseUtils.closeDatabaseSR(statement, list);

			statement = con.prepareStatement("SELECT `clan_id`,`largecrest` FROM `clan_data` WHERE `largecrest` IS NOT NULL");
			list = statement.executeQuery();
			while(list.next())
			{
				counter++;
				int hash = mhash(list.getBytes("largecrest"));
				_cachePledgeLarge.put(list.getInt("clan_id"), hash);
				_cachePledgeLargeHashed.put(hash, list.getBytes("largecrest"));
			}
			DatabaseUtils.closeDatabaseSR(statement, list);

			statement = con.prepareStatement("SELECT `ally_id`,`crest` FROM `ally_data` WHERE `crest` IS NOT NULL");
			list = statement.executeQuery();
			while(list.next())
			{
				counter++;
				int hash = mhash(list.getBytes("crest"));
				_cacheAlly.put(list.getInt("ally_id"), hash);
				_cacheAllyHashed.put(hash, list.getBytes("crest"));
			}
			DatabaseUtils.closeDatabaseSR(statement, list);
			statement = null;
			list = null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, list);
		}
		_log.info("CrestCache: Loaded " + counter + " crests");
	}

	public static byte[] getPledgeCrest(int id)
	{
		byte[] crest = _cachePledgeHashed.get(id);
		if(crest == null)
		{
			if(id < 10)
			{
				int image_id = ImagesChache.getInstance().getImageId(id+".bmp");
				byte[] image = ImagesChache.getInstance().getImage(image_id);
				return image; 
			}
			return new byte[0];
		}
		return crest;
	}

	public static byte[] getPledgeCrestLarge(int id)
	{
		byte[] crest = _cachePledgeLargeHashed.get(id);
		return crest != null ? crest : new byte[0];
	}

	public static byte[] getAllyCrest(int id)
	{
		byte[] crest = _cacheAllyHashed.get(id);
		return crest != null ? crest : new byte[0];
	}

	public static int getPledgeCrestId(int clan_id)
	{
		Integer crest = _cachePledge.get(clan_id);
		return crest != null ? crest : 0;
	}

	public static int getPledgeCrestLargeId(int clan_id)
	{
		Integer crest = _cachePledgeLarge.get(clan_id);
		return crest != null ? crest : 0;
	}

	public static int getAllyCrestId(int ally_id)
	{
		Integer crest = _cacheAlly.get(ally_id);
		return crest != null ? crest : 0;
	}

	public static void removePledgeCrest(L2Clan clan)
	{
		clan.setCrestId(0);
		_cachePledge.remove(clan.getClanId());
		_cachePledgeHashed.remove(clan.getCrestId());
		clan.broadcastClanStatus(false, true, false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?");
			statement.setNull(1, -3);
			statement.setInt(2, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void removePledgeCrestLarge(L2Clan clan)
	{
		clan.setCrestLargeId(0);
		_cachePledgeLarge.remove(clan.getClanId());
		_cachePledgeLargeHashed.remove(clan.getCrestLargeId());
		clan.broadcastClanStatus(false, true, false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?");
			statement.setNull(1, -3);
			statement.setInt(2, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void removeAllyCrest(L2Alliance ally)
	{
		ally.setAllyCrestId(0);
		_cacheAlly.remove(ally.getAllyId());
		_cacheAllyHashed.remove(ally.getAllyCrestId());
		ally.broadcastAllyStatus(false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?");
			statement.setNull(1, -3);
			statement.setInt(2, ally.getAllyId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void savePledgeCrest(L2Clan clan, byte[] data)
	{
		int hash = mhash(data);
		clan.setCrestId(hash);
		_cachePledgeHashed.put(hash, data);
		_cachePledge.put(clan.getClanId(), hash);
		clan.broadcastClanStatus(false, true, false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?");
			statement.setBytes(1, data);
			statement.setInt(2, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void savePledgeCrestLarge(L2Clan clan, byte[] data)
	{
		int hash = mhash(data);
		clan.setCrestLargeId(hash);
		_cachePledgeLargeHashed.put(hash, data);
		_cachePledgeLarge.put(clan.getClanId(), hash);
		clan.broadcastClanStatus(false, true, false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?");
			statement.setBytes(1, data);
			statement.setInt(2, clan.getClanId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static void saveAllyCrest(L2Alliance ally, byte[] data)
	{
		int hash = mhash(data);
		ally.setAllyCrestId(hash);
		_cacheAllyHashed.put(hash, data);
		_cacheAlly.put(ally.getAllyId(), hash);
		ally.broadcastAllyStatus(false);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?");
			statement.setBytes(1, data);
			statement.setInt(2, ally.getAllyId());
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public static int mhash(byte[] data)
	{
		int ret = 0;
		if(data != null)
			for(byte element : data)
				ret = 7 * ret + element;
		return Math.abs(ret);
	}
}
