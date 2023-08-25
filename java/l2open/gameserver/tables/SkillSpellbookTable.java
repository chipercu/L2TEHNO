package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkillSpellbookTable
{
	private static Logger _log = Logger.getLogger(SkillTreeTable.class.getName());
	private static final SkillSpellbookTable _instance = new SkillSpellbookTable();

	public static HashMap<Integer, Integer> _skillSpellbooks;
	public static HashMap<Integer, ArrayList<Integer>> _spellbookHandlers;
	public static HashMap<Integer, Integer> _minLevels;

	public static HashMap<Integer, Integer> getSkillSpellbooks()
	{
		return _skillSpellbooks;
	}

	public static HashMap<Integer, ArrayList<Integer>> getSpellbookHandlers()
	{
		return _spellbookHandlers;
	}

	public static SkillSpellbookTable getInstance()
	{
		return _instance;
	}

	public static int getMinLevel(Integer itemId)
	{
		return _minLevels.get(itemId);
	}

	private SkillSpellbookTable()
	{
		_skillSpellbooks = new HashMap<Integer, Integer>();
		_spellbookHandlers = new HashMap<Integer, ArrayList<Integer>>();
		_minLevels = new HashMap<Integer, Integer>();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet spbooks = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT skill_id, level, item_id, item_count, min_level FROM skill_spellbooks");
			spbooks = statement.executeQuery();

			while(spbooks.next())
			{
				int skill_id = spbooks.getInt("skill_id");
				int level = spbooks.getInt("level");
				int item_id = spbooks.getInt("item_id");
				int min_level = spbooks.getInt("min_level");
				_skillSpellbooks.put(hashCode(new int[] { skill_id, level }), item_id);
				_minLevels.put(item_id, min_level);
				if(spbooks.getInt("item_count") == -1)
				{
					ArrayList<Integer> list = _spellbookHandlers.get(item_id);
					if(list == null)
						list = new ArrayList<Integer>();
					list.add(skill_id);
					_spellbookHandlers.put(item_id, list);
				}
			}

			_log.info("SkillSpellbookTable: Loaded " + _skillSpellbooks.size() + " Spellbooks.");
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "error while loading spellbooks	", e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, spbooks);
		}
	}

	public static int hashCode(int a[])
	{
		if(a == null)
			return 0;

		return a[1] + (a[0] << 16);
	}

	public static void unload()
	{
		_skillSpellbooks.clear();
		_spellbookHandlers.clear();
	}
}