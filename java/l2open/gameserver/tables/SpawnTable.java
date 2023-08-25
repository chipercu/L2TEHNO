package l2open.gameserver.tables;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.instancemanager.CatacombSpawnManager;
import l2open.gameserver.instancemanager.DayNightSpawnManager;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2SiegeGuardInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Rnd;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class SpawnTable
{
	private static final Logger _log = Logger.getLogger(SpawnTable.class.getName());

	private static SpawnTable _instance;

	private List<L2Spawn> _spawntable = new ArrayList<L2Spawn>();
	private int _npcSpawnCount = 0;
	private int _spawnCount = 0;

	public static SpawnTable getInstance()
	{
		if(_instance == null)
			new SpawnTable();
		return _instance;
	}

	private SpawnTable()
	{
		_instance = this;
		if(!ConfigValue.EnablePtsSpawnEngine)
		{
			NpcTable.getInstance().applyServerSideTitle();
			if(!ConfigValue.StartWhisoutSpawn)
				fillSpawnTable(true);
			else
			{
				_log.info("Spawn Correctly Disabled");
				Scripts.getInstance().callOnLoad();
			}
		}
	}

	public List<L2Spawn> getSpawnTable()
	{
		return _spawntable;
	}

	private void fillSpawnTable(boolean scripts)
	{
		
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int npcId = 0;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM spawnlist ORDER by npc_templateid");
			//TODO возможно в будущем понадобится условие: WHERE npc_templateid NOT IN (SELECT bossId FROM epic_boss_spawn)
			rset = statement.executeQuery();

			L2Spawn spawnDat;
			L2NpcTemplate template1;
			StatsSet npcDat;
			_npcSpawnCount = 0;
			_spawnCount = 0;
			StringTokenizer st1;
			StringTokenizer st2;

			while(rset.next())
			{
					
					template1 = NpcTable.getTemplate(npcId = rset.getInt("npc_templateid"));
					//------------------------------------------------------------------------------------
					npcDat = template1.getSet();
					String param = rset.getString("statParam");
					if(!param.startsWith("-1"))
					{
						st1 = new StringTokenizer(param, ";");
						while(st1.hasMoreTokens())
						{
							st2 = new StringTokenizer(st1.nextToken(), "=");
							npcDat.set(st2.nextToken(), st2.nextToken());
						}
					}
					//------------------------------------------------------------------------------------
					StatsSet npcAI = new StatsSet();
					String param2 = rset.getString("aiParam");
					if(!param2.startsWith("-1"))
					{
						st1 = new StringTokenizer(param2, ";");
						while(st1.hasMoreTokens())
						{
							st2 = new StringTokenizer(st1.nextToken(), "=");
							npcAI.set(st2.nextToken(), st2.nextToken());
						}
					}
					npcDat.set("AIparam", npcAI);
					template1.setSet(npcDat);
					//------------------------------------------------------------------------------------
					if(template1 != null)
					{
						if(template1.isInstanceOf(L2SiegeGuardInstance.class))
						{
							// Don't spawn Siege Guard
						}
						else if(ConfigValue.AllowClassMasters[0] == 0 && ConfigValue.AllowClassMasters[1] == 0 && ConfigValue.AllowClassMasters[2] == 0 && template1.name.equalsIgnoreCase("L2ClassMaster"))
						{
							// Dont' spawn class masters
						}
						else
						{
							spawnDat = new L2Spawn(template1);
							spawnDat.setAmount(rset.getInt("count"));
							spawnDat.setLocx(rset.getInt("locx"));
							spawnDat.setLocy(rset.getInt("locy"));
							spawnDat.setLocz(rset.getInt("locz"));
							spawnDat.setHeading(rset.getInt("heading"));
							spawnDat.setRespawnDelay(rset.getInt("respawn_delay"), rset.getInt("respawn_delay_rnd"));
							spawnDat.setLocation2(rset.getString("loc_id"));
							spawnDat.setBanedTerritory(rset.getString("baned_loc_id"));
							spawnDat.setReflection(rset.getInt("reflection"));
							spawnDat.setRespawnTime(0);
							spawnDat.setAIParam(param2);
							if(template1.isInstanceOf(L2MonsterInstance.class))
							{
								if(template1.name.contains("Lilim") || template1.name.contains("Lith"))
									CatacombSpawnManager.getInstance().addDawnMob(spawnDat);
								else if(template1.name.contains("Nephilim") || template1.name.contains("Gigant"))
									CatacombSpawnManager.getInstance().addDuskMob(spawnDat);
								if(CatacombSpawnManager._monsters.contains(template1.getNpcId()))
									spawnDat.setRespawnDelay(Math.round(rset.getInt("respawn_delay") * ConfigValue.AltCatacombMonstersRespawn), Math.round(rset.getInt("respawn_delay_rnd") * ConfigValue.AltCatacombMonstersRespawn));
							}
							if(template1.isRaid || RaidBossSpawnManager.getInstance().isInCustomResp(npcId))
								RaidBossSpawnManager.getInstance().addNewSpawn(spawnDat);
							switch(rset.getInt("periodOfDay"))
							{
								case 0: // default
									if(!ConfigValue.DelayedSpawn)
										_npcSpawnCount += spawnDat.init();
									_spawntable.add(spawnDat);
									break;
								case 1: // Day
									DayNightSpawnManager.getInstance().addDayMob(spawnDat);
									break;
								case 2: // Night
									DayNightSpawnManager.getInstance().addNightMob(spawnDat);
									break;
							}
							_spawnCount++;
							if(_npcSpawnCount % 1000 == 0 && _npcSpawnCount != 0)
								_log.info("Spawned " + _npcSpawnCount + " npc");
						}
					}
			//	}
				else
					_log.warning("mob data for id:" + rset.getInt("npc_templateid") + " missing in npc table");
				template1 = null;
			}
			DayNightSpawnManager.getInstance().notifyChangeMode();
			CatacombSpawnManager.getInstance().notifyChangeMode();
		}
		catch(Exception e1)
		{
			_log.warning("NpcId: " + npcId + " spawn couldnt be initialized:" + e1);
			e1.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_log.info("SpawnTable: Loaded " + _spawnCount + " Npc Spawn Locations. Total NPCs: " + _npcSpawnCount);

		loadInventory();

		if(scripts)
			Scripts.getInstance().callOnLoad();
	}

	public void deleteSpawn(L2Spawn spawn)
	{
		_spawntable.remove(spawn);
	}

	public void reloadAll()
	{
		L2World.deleteVisibleNpcSpawns();
		_spawntable.clear();
		fillSpawnTable(false);
		RaidBossSpawnManager.getInstance().reloadBosses();
	}

	public void loadInventory()
	{
		int count = 0;
		List<L2NpcInstance> temp = null;
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT owner_id, object_id, item_id, count, enchant_level FROM items WHERE loc = 'MONSTER'");
			rset = statement.executeQuery();

			while(rset.next())
			{
				count++;
				temp = L2ObjectsStorage.getAllByNpcId(rset.getInt("owner_id"), false);
				try
				{
					L2ItemInstance item = PlayerData.getInstance().restoreFromDb(rset.getInt("object_id"));
					if(temp.size() > 0)
					{
						L2MonsterInstance monster = (L2MonsterInstance) temp.toArray()[Rnd.get(temp.size())];
						monster.giveItem(item, false);
					}
					else
						NpcTable.getTemplate(rset.getInt("owner_id")).giveItem(item, false);
				}
				catch(Exception e)
				{
					_log.warning("Unable to restore inventory for " + temp.get(0).getNpcId());
				}
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
		_log.info("Monsters inventory loaded, items: " + count);
	}

	public static void unload()
	{
		if(_instance != null)
			_instance = null;
	}
}