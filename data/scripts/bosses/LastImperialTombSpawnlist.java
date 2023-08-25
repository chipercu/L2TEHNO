package bosses;

import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.GArray;

import java.sql.ResultSet;

public class LastImperialTombSpawnlist extends Functions implements ScriptFile
{
	private static GArray<L2Spawn> _Room1SpawnList1st = new GArray<L2Spawn>();
	private static GArray<L2Spawn> _Room2InsideSpawnList = new GArray<L2Spawn>();
	private static GArray<L2Spawn> _Room2OutsideSpawnList = new GArray<L2Spawn>();

	public static void fill()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM lastimperialtomb_spawnlist");
			rset = statement.executeQuery();

			int npcTemplateId;
			L2Spawn spawnDat;
			L2NpcTemplate npcTemplate;

			while(rset.next())
			{
				npcTemplateId = rset.getInt("npc_templateid");
				npcTemplate = NpcTable.getTemplate(npcTemplateId);
				if(npcTemplate != null)
				{
					spawnDat = new L2Spawn(npcTemplate);
					spawnDat.setAmount(rset.getInt("count"));
					spawnDat.setLocx(rset.getInt("locx"));
					spawnDat.setLocy(rset.getInt("locy"));
					// мини костыль...
					spawnDat.setLocz(GeoEngine.getHeight(rset.getInt("locx"), rset.getInt("locy"), rset.getInt("locz"), 0));
					//spawnDat.setLocz(rset.getInt("locz"));
					spawnDat.setHeading(rset.getInt("heading"));
					spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));

					switch(npcTemplateId)
					{
						case 18328:
						case 18330:
						case 18329:
						case 18331:
							_Room1SpawnList1st.add(spawnDat);
							break;
						case 18339:
							/**
							territory_begin	[godard32_1713_021]	{{-88059;-147379;-9228;-9028};{-87699;-147371;-9228;-9028};{-87707;-146959;-9228;-9028};{-88075;-146975;-9228;-9028}}	territory_end	
							npcmaker_ex_begin	[godard32_1713_021]	name=[godard32_1713_021m1]	ai=[hall_b_controller_undeadband_player_maker]	ai_parameters={[inzone_type_param]=136}	maximum_npc=4	
							npc_ex_begin	[undeadband_player]	pos=anywhere	total=4	respawn=no	npc_ex_end
							npcmaker_ex_end	
							**/
							_Room2InsideSpawnList.add(spawnDat);
							break;
						case 18334:
						case 18335:
						case 18336:
						case 18337:
						case 18338:
							_Room2OutsideSpawnList.add(spawnDat);
							break;
					}
				}
				else
					_log.info("LastImperialTombSpawnlist: Data missing in NPC table for ID: " + npcTemplateId + ".");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		_log.info("LastImperialTombSpawnlist: Loaded " + _Room1SpawnList1st.size() + " Room1 1st Npc Spawn Locations.");
		_log.info("LastImperialTombSpawnlist: Loaded " + _Room2InsideSpawnList.size() + " Room2 Inside Npc Spawn Locations.");
		_log.info("LastImperialTombSpawnlist: Loaded " + _Room2OutsideSpawnList.size() + " Room2 Outside Npc Spawn Locations.");
	}

	public static void clear()
	{
		_Room1SpawnList1st.clear();
		_Room2InsideSpawnList.clear();
		_Room2OutsideSpawnList.clear();
	}

	public static GArray<L2Spawn> getRoom1SpawnList1st()
	{
		return _Room1SpawnList1st;
	}

	public static GArray<L2Spawn> getRoom2InsideSpawnList()
	{
		return _Room2InsideSpawnList;
	}

	public static GArray<L2Spawn> getRoom2OutsideSpawnList()
	{
		return _Room2OutsideSpawnList;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}