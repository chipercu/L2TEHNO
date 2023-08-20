package l2open.gameserver.idfactory;

import l2open.config.ConfigValue;
import l2open.Server;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Tasks
{
	private static Logger _log = Logger.getLogger(IdFactory.class.getName());

	public static final String[][] objTables = 
	{ 
		{ "characters", "obj_id" }, 
		{ "items", "object_id" },
		{ "clan_data", "clan_id" }, 
		{ "ally_data", "ally_id" }, 
		{ "pets", "objId" }, 
		{ "couples", "id" } 
	};

	public static enum ClearQuery implements Runnable
	{
		character_variables("DELETE FROM character_variables WHERE character_variables.obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_friends("DELETE FROM character_friends WHERE character_friends.char_id NOT IN (SELECT obj_Id FROM characters) OR character_friends.friend_id NOT IN (SELECT obj_Id FROM characters);"),
		couples("DELETE FROM couples WHERE couples.player1Id NOT IN (SELECT obj_Id FROM characters) OR couples.player2Id NOT IN (SELECT obj_Id FROM characters);"),
		character_blocklist("DELETE FROM character_blocklist WHERE character_blocklist.obj_Id NOT IN (SELECT obj_Id FROM characters) OR character_blocklist.target_Id NOT IN (SELECT obj_Id FROM characters);"),
		character_hennas("DELETE FROM character_hennas WHERE character_hennas.char_obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_macroses("DELETE FROM character_macroses WHERE character_macroses.char_obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_quests("DELETE FROM character_quests WHERE character_quests.char_id NOT IN (SELECT obj_Id FROM characters);"),
		character_shortcuts("DELETE FROM character_shortcuts WHERE character_shortcuts.char_obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_effects_save("DELETE FROM character_effects_save WHERE character_effects_save.char_obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_skills_save("DELETE FROM character_skills_save WHERE character_skills_save.char_obj_id NOT IN (SELECT obj_Id FROM characters);"),
		character_recipebook("DELETE FROM character_recipebook WHERE character_recipebook.char_id NOT IN (SELECT obj_Id FROM characters);", true),
		character_skills("DELETE FROM character_skills WHERE character_skills.char_obj_id NOT IN (SELECT obj_Id FROM characters);", true),
		character_subclasses("DELETE FROM character_subclasses WHERE character_subclasses.char_obj_id NOT IN (SELECT obj_Id FROM characters);", true),
		clan_data("DELETE FROM clan_data WHERE clan_data.leader_id NOT IN (SELECT obj_Id FROM characters WHERE characters.clanid=clan_data.clan_id);", true),
		clan_subpledges("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);", false, clan_data),
		ally_data("DELETE FROM ally_data WHERE (ally_data.leader_id NOT IN (SELECT clan_id FROM clan_data)) OR (ally_id NOT IN (SELECT ally_id FROM clan_data));", false, clan_data),
		siege_clans("DELETE FROM siege_clans WHERE siege_clans.clan_id NOT IN (SELECT clan_id FROM clan_data);", false, clan_data),
		clan_wars("DELETE FROM clan_wars where clan1 not in (select clan_id FROM clan_data) or clan2 not in (select clan_id FROM clan_data);", false, clan_data),
		items(ConfigValue.HardDbCleanUpOnStart ? "DELETE FROM items WHERE (count = 0) OR (owner_id NOT IN (SELECT obj_Id FROM characters) AND owner_id NOT IN (SELECT clan_id FROM clan_data) AND owner_id NOT IN (SELECT objId FROM pets) AND owner_id NOT IN (SELECT id FROM npc));" : "DELETE FROM items WHERE count = 0;", false, ConfigValue.HardDbCleanUpOnStart ? clan_data : null),
		pets("DELETE FROM pets WHERE pets.item_obj_id NOT IN (SELECT object_id FROM items);", true, items),
		item_attributes("DELETE FROM item_attributes where itemId not in (select object_id FROM items);", false, items),
		update_characters("UPDATE characters SET clanid=0,pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0 WHERE clanid!=0 AND clanid NOT IN (SELECT clan_id FROM clan_data);", false, clan_data),
		update_clan_data("UPDATE clan_data SET ally_id=0 WHERE ally_id!=0 AND ally_id NOT IN (SELECT ally_id FROM ally_data);", false, ally_data);

		public static int totalDeleted = 0, totalUpdated = 0;
		private static ReentrantLock totalLock = new ReentrantLock();
		private final ClearQuery _parent;
		public final String _query, _table;
		public final boolean _hard, _update;
		public boolean compleated;

		private ClearQuery(String query, boolean hard, ClearQuery parent)
		{
			compleated = false;
			_query = query;
			_hard = hard;
			_parent = parent;
			_update = this.name().startsWith("update_");
			_table = _update ? name().replaceFirst("update_", "") : name();
		}

		private ClearQuery(String query, boolean hard)
		{
			this(query, hard, null);
		}

		private ClearQuery(String query)
		{
			this(query, false);
		}

		@Override
		public void run()
		{
			ThreadConnection con = null;
			FiltredStatement s = null;
			ResultSet rs = null;

			try
			{
				if(!ConfigValue.HardDbCleanUpOnStart)
					return;
				if(_parent != null)
				{
					while(!_parent.compleated)
						synchronized (_parent)
						{
							_parent.wait();
						}
				}
				con = L2DatabaseFactory.getInstance().getConnection();
				s = con.createStatement();
				int currCount = s.executeUpdate(_query);
				if(currCount > 0)
				{
					totalLock.lock();
					if(_update)
						totalUpdated += currCount;
					else
						totalDeleted += currCount;
					totalLock.unlock();
					if(_update)
						_log.info("Updated " + currCount + " elements in table " + _table + ".");
					else
						_log.info("Cleaned " + currCount + " elements from table " + _table + ".");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Server.exit(0, "IdFactory::DBCleaner::" + _query);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, s, rs);
				compleated = true;
				synchronized (this)
				{
					notifyAll();
				}
			}
		}
	}

	public static class CountObjectIds implements Runnable
	{
		final String[] _objTable;
		final int[] _objCountPut;

		public CountObjectIds(String[] objTable, int[] objCountPut)
		{
			super();
			_objTable = objTable;
			_objCountPut = objCountPut;
		}

		@Override
		public void run()
		{
			ThreadConnection con = null;
			FiltredStatement s = null;
			ResultSet rs = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				s = con.createStatement();
				rs = s.executeQuery("SELECT COUNT(*) FROM " + _objTable[0]);
				if(!rs.next())
					throw new Exception("IdFactory: can't extract count ids :: " + _objTable[0]);
				_objCountPut[0] = rs.getInt(1);
				_log.info("IdFactory: Table " + _objTable[0] + " contains " + _objCountPut[0] + " rows");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Server.exit(0, "IdFactory::CountObjectIds::" + _objTable[0]);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, s, rs);
			}
		}
	}

	public static class ExtractObjectIds implements Runnable
	{
		final String[] _objTable;
		final int[] _resultArray;
		int startIdx;

		public ExtractObjectIds(String[] objTable, int[] objCount, int[] resultArray)
		{
			super();
			_objTable = objTable;
			_resultArray = resultArray;
			startIdx = objCount[1];
		}

		@Override
		public void run()
		{
			ThreadConnection con = null;
			FiltredStatement s = null;
			ResultSet rs = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				s = con.createStatement();
				rs = s.executeQuery("SELECT " + _objTable[1] + " FROM " + _objTable[0]);
				int idx = 0;
				while(rs.next())
					_resultArray[startIdx + idx++] = rs.getInt(1);
				_log.info("IdFactory: Extracted " + idx + " used id's from " + _objTable[0]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Server.exit(0, "IdFactory::ExtractObjectIds::" + _objTable[0] + "::" + startIdx);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, s, rs);
			}
		}
	}
}