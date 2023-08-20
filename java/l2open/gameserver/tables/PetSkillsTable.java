package l2open.gameserver.tables;

import javolution.util.FastList;
import javolution.util.FastMap;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.model.L2Summon;
import l2open.util.GArray;

import java.sql.ResultSet;
import java.util.logging.Logger;

public class PetSkillsTable
{
	private static Logger _log = Logger.getLogger(PetSkillsTable.class.getName());
	private FastMap<Integer, GArray<L2PetSkillLearn>> _skillTrees;

	private static PetSkillsTable _instance = new PetSkillsTable();

	public static PetSkillsTable getInstance()
	{
		return _instance;
	}

	public void reload()
	{
		_instance = new PetSkillsTable();
	}

	private PetSkillsTable()
	{
		_skillTrees = new FastMap<Integer, GArray<L2PetSkillLearn>>().setShared(true);
		load();
	}

	private void load()
	{
		int npcId = 0;
		int count = 0;
		int id = 0;
		int lvl = 0;
		int minLvl = 0;

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		FiltredPreparedStatement statement2 = null;
		ResultSet petlist = null;
		ResultSet skilltree = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id, name FROM npc WHERE type = 'L2Pet' ORDER BY id");
			petlist = statement.executeQuery();
			GArray<L2PetSkillLearn> map;
			L2PetSkillLearn skillLearn;
			while(petlist.next())
			{
				map = new GArray<L2PetSkillLearn>();
				npcId = petlist.getInt("id");

				try
				{
					statement2 = con.prepareStatement("SELECT minLvl, skillId, skillLvl FROM pets_skills where templateId=? ORDER BY skillId, skillLvl");
					statement2.setInt(1, npcId);
					skilltree = statement2.executeQuery();

					while(skilltree.next())
					{
						id = skilltree.getInt("skillId");
						lvl = skilltree.getInt("skillLvl");
						minLvl = skilltree.getInt("minLvl");

						skillLearn = new L2PetSkillLearn(id, lvl, minLvl);
						map.add(skillLearn);
					}

					_skillTrees.put(npcId, map);
				}
				catch(Exception e)
				{
					_log.severe("Error while creating pet skill tree (Pet ID: " + npcId + ", skillId: " + id + ", level: " + lvl + ")");
					e.printStackTrace();
				}
				finally
				{
					DatabaseUtils.closeDatabaseSR(statement2, skilltree);
				}

				count += map.size();
				_log.fine("PetSkillsTable: skill tree for pet " + petlist.getString("name") + " has " + map.size() + " skills");
			}
		}
		catch(Exception e)
		{
			_log.severe("Error while creating pet skill tree (Pet ID " + npcId + ")");
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, petlist);
		}
		_log.info("PetSkillsTable: Loaded " + count + " skills.");
	}

	public int getAvailableLevel(L2Summon cha, int skillId)
	{
		int lvl = 0;
		if(!_skillTrees.containsKey(cha.getNpcId()))
			return lvl;
		GArray<L2PetSkillLearn> skills = _skillTrees.get(cha.getNpcId());
		for(L2PetSkillLearn temp : skills)
		{
			if(temp.getId() != skillId)
				continue;
			if(temp.getLevel() == 0)
			{
				if(cha.getLevel() < 70)
				{
					lvl = cha.getLevel() / 10;
					if(lvl <= 0)
						lvl = 1;
				}
				else
					lvl = 7 + (cha.getLevel() - 70) / 5;

				// formula usable for skill that have 10 or more skill levels
				int maxLvl = SkillTable.getInstance().getMaxLevel(temp.getId());
				if(lvl > maxLvl)
					lvl = maxLvl;
				break;
			}
			else if(temp.getMinLevel() <= cha.getLevel() && temp.getLevel() > lvl)
				lvl = temp.getLevel();
		}
		return lvl;
	}

	public FastList<Integer> getAvailableSkills(L2Summon cha)
	{
		FastList<Integer> skillIds = new FastList<Integer>();
		if(!_skillTrees.containsKey(cha.getNpcId()))
			return null;
		GArray<L2PetSkillLearn> skills = _skillTrees.get(cha.getNpcId());
		for(L2PetSkillLearn temp : skills)
		{
			if(skillIds.contains(temp.getId()))
				continue;
			skillIds.add(temp.getId());
		}
		return skillIds;
	}

	public final class L2PetSkillLearn
	{
		private final int _id;
		private final int _level;
		private final int _minLevel;

		public L2PetSkillLearn(int id, int lvl, int minLvl)
		{
			_id = id;
			_level = lvl;
			_minLevel = minLvl;
		}

		public int getId()
		{
			return _id;
		}

		public int getLevel()
		{
			return _level;
		}

		public int getMinLevel()
		{
			return _minLevel;
		}
	}
}