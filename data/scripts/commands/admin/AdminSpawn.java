package commands.admin;

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.ScriptFile;
import l2open.extensions.scripts.Scripts;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.RaidBossSpawnManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.GmListTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Util;
import bosses.FrintezzaManager;

public class AdminSpawn implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_show_spawns,
		admin_spawn,
		admin_spawn_monster,
		admin_spawn_index,
		admin_unspawnall,
		admin_spawn1,
		admin_setheading,
		admin_setai,
		admin_setaiparam,
		admin_dumpaiparams,
		admin_generate_loc,
//		admin_frintezza_start,
//		admin_frintezza_stop
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;
		StringTokenizer st;
		L2NpcInstance target;
		L2Spawn spawn;
		L2NpcInstance npc;

		switch(command)
		{
			case admin_show_spawns:
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/spawns.htm"));
				break;
			case admin_spawn_index:
				try
				{
					String val = fullString.substring(18);
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/spawns/" + val + ".htm"));
				}
				catch(StringIndexOutOfBoundsException e)
				{}
				break;
			case admin_spawn1:
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					String id = st.nextToken();
					int mobCount = 1;
					if(st.hasMoreTokens())
						mobCount = Integer.parseInt(st.nextToken());
					spawnMonster(activeChar, id, 0, mobCount);
				}
				catch(Exception e)
				{
					// Case of wrong monster data
				}
				break;
			case admin_spawn:
			case admin_spawn_monster:
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					String id = st.nextToken();
					int respawnTime = 30;
					int mobCount = 1;
					if(st.hasMoreTokens())
						mobCount = Integer.parseInt(st.nextToken());
					if(st.hasMoreTokens())
						respawnTime = Integer.parseInt(st.nextToken());
					spawnMonster(activeChar, id, respawnTime, mobCount);
				}
				catch(Exception e)
				{
					// Case of wrong monster data
				}
				break;
			case admin_unspawnall:
				for(L2Player player : L2ObjectsStorage.getPlayers())
					player.sendPacket(Msg.THE_NPC_SERVER_IS_NOT_OPERATING);
				L2World.deleteVisibleNpcSpawns();
				GmListTable.broadcastMessageToGMs("NPC Unspawn completed!");
				break;
			case admin_setai:
				if(activeChar.getTarget() == null || !activeChar.getTarget().isNpc())
				{
					activeChar.sendMessage("Please select target NPC or mob.");
					return false;
				}

				st = new StringTokenizer(fullString, " ");
				st.nextToken();
				if(!st.hasMoreTokens())
				{
					activeChar.sendMessage("Please specify AI name.");
					return false;
				}
				String aiName = st.nextToken();
				target = (L2NpcInstance) activeChar.getTarget();

				Constructor<?> aiConstructor = null;
				try
				{
					if(!aiName.equalsIgnoreCase("npc"))
						aiConstructor = Class.forName("l2open.gameserver.ai." + aiName).getConstructors()[0];
				}
				catch(Exception e)
				{
					try
					{
						aiConstructor = Scripts.getInstance().getClasses().get("ai." + aiName).getRawClass().getConstructors()[0];
					}
					catch(Exception e1)
					{
						activeChar.sendMessage("This type AI not found.");
						return false;
					}
				}

				if(aiConstructor != null)
				{
					try
					{
						target.setAI((L2CharacterAI) aiConstructor.newInstance(new Object[] { target }));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					target.getAI().startAITask();
				}
				break;
			case admin_setaiparam:
				if(activeChar.getTarget() == null || !activeChar.getTarget().isNpc())
				{
					activeChar.sendMessage("Please select target NPC or mob.");
					return false;
				}

				st = new StringTokenizer(fullString, " ");
				st.nextToken();

				if(!st.hasMoreTokens())
				{
					activeChar.sendMessage("Please specify AI parameter name.");
					activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
					return false;
				}

				String paramName = st.nextToken();
				if(!st.hasMoreTokens())
				{
					activeChar.sendMessage("Please specify AI parameter value.");
					activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
					return false;
				}
				String paramValue = st.nextToken();
				target = (L2NpcInstance) activeChar.getTarget();
				//((DefaultAI) target.getAI()).set(paramName, paramValue);
				target.decayMe();
				target.spawnMe();
				activeChar.sendMessage("AI parameter " + paramName + " succesfully setted to " + paramValue);
				break;
			case admin_dumpaiparams:
				if(activeChar.getTarget() == null || !activeChar.getTarget().isNpc())
				{
					activeChar.sendMessage("Please select target NPC or mob.");
					return false;
				}
				target = (L2NpcInstance) activeChar.getTarget();
				//StatsSet set = target.getTemplate().getAIParams();
				//if(set != null)
				//	System.out.println("Dump of AI Params:\r\n" + set.dump());
				//else
					System.out.println("AI Params not setted.");
				break;
			case admin_setheading:
				L2Object obj = activeChar.getTarget();
				if(!obj.isNpc())
				{
					activeChar.sendMessage("Target is incorrect!");
					return false;
				}

				npc = (L2NpcInstance) obj;
				npc.setHeading(activeChar.getHeading());
				npc.decayMe();
				npc.spawnMe();
				activeChar.sendMessage("New heading : " + activeChar.getHeading());

				spawn = npc.getSpawn();
				if(spawn == null)
				{
					activeChar.sendMessage("Spawn for this npc == null!");
					return false;
				}

				if(!mysql.set("update spawnlist set heading = " + activeChar.getHeading() //
						+ " where npc_templateid = " + npc.getNpcId() //
						+ " and locx = " + spawn.getLocx() //
						+ " and locy = " + spawn.getLocy() //
						+ " and locz = " + spawn.getLocz() //
						+ " and loc_id = " + spawn.getLocation()))
				{
					activeChar.sendMessage("Error in mysql query!");
					return false;
				}
				break;
			case admin_generate_loc:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Incorrect argument count!");
					return false;
				}

				int id = Integer.parseInt(wordList[1]);
				int id2 = 0;
				if(wordList.length > 2)
					id2 = Integer.parseInt(wordList[2]);

				int min_x = Integer.MIN_VALUE;
				int min_y = Integer.MIN_VALUE;
				int min_z = Integer.MIN_VALUE;
				int max_x = Integer.MAX_VALUE;
				int max_y = Integer.MAX_VALUE;
				int max_z = Integer.MAX_VALUE;

				String name = "";

				for(L2NpcInstance _npc : L2World.getAroundNpc(activeChar))
					if(_npc.getNpcId() == id || _npc.getNpcId() == id2)
					{
						name = _npc.getName();
						min_x = Math.min(min_x, _npc.getX());
						min_y = Math.min(min_y, _npc.getY());
						min_z = Math.min(min_z, _npc.getZ());
						max_x = Math.max(max_x, _npc.getX());
						max_y = Math.max(max_y, _npc.getY());
						max_z = Math.max(max_z, _npc.getZ());
					}

				min_x -= 500;
				min_y -= 500;
				max_x += 500;
				max_y += 500;

				System.out.println("(0,'" + name + "'," + min_x + "," + min_y + "," + min_z + "," + max_z + ",0),");
				System.out.println("(0,'" + name + "'," + min_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
				System.out.println("(0,'" + name + "'," + max_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
				System.out.println("(0,'" + name + "'," + max_x + "," + min_y + "," + min_z + "," + max_z + ",0),");

				System.out.println("delete from spawnlist where npc_templateid in (" + id + ", " + id2 + ")" + //
				" and locx <= " + min_x + //
				" and locy <= " + min_y + //
				" and locz <= " + min_z + //
				" and locx >= " + max_x + //
				" and locy >= " + max_y + //
				" and locz >= " + max_z + //
				";");
				break;
			/*case admin_frintezza_start:
				FrintezzaManager.setScarletSpawnTask(true);
				break;
			case admin_frintezza_stop:
				FrintezzaManager.setUnspawn();
				break;*/
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void spawnMonster(L2Player activeChar, String monsterId, int respawnTime, int mobCount)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher regexp = pattern.matcher(monsterId);
		L2NpcTemplate template;
		if(regexp.matches())
		{
			// First parameter was an ID number
			int monsterTemplate = Integer.parseInt(monsterId);
			template = NpcTable.getTemplate(monsterTemplate);
			if(Util.contains_int(ConfigValue.NotSpawnMob, monsterTemplate))
			{
				activeChar.sendMessage("Этого Моба/НПС/РБ нельзя спаунить через Админку...");
				return;
			}
		}
		else
		{
			// First parameter wasn't just numbers so go by name not ID
			monsterId = monsterId.replace('_', ' ');
			template = NpcTable.getTemplateByName(monsterId);
		}

		if(template == null)
		{
			activeChar.sendMessage("Incorrect monster template.");
			return;
		}

		try
		{
			L2Spawn spawn = new L2Spawn(template);
			spawn.setLoc(target.getLoc());
			spawn.setLocation(0);
			spawn.setAmount(mobCount);
			spawn.setHeading(activeChar.getHeading());
			spawn.setRespawnDelay(respawnTime);
			spawn.setReflection(activeChar.getReflection().getId());

			if(RaidBossSpawnManager.getInstance().isDefined(spawn.getNpcId()))
				activeChar.sendMessage("Raid Boss " + template.name + " already spawned.");
			else
			{
				spawn.init();
				if(respawnTime == 0)
					spawn.stopRespawn();
				activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
			}
			if(ConfigValue.saveAdminSpawn)
				addNewSpawn(spawn, activeChar.getName());
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
	}

	public void addNewSpawn(L2Spawn spawn, String adminName)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO `spawnlist` (location,count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id,aiParam) values(?,?,?,?,?,?,?,?,?,-1)");
			statement.setString(1, "AdminSpawn: " + adminName);
			statement.setInt(2, spawn.getAmount());
			statement.setInt(3, spawn.getNpcId());
			statement.setInt(4, spawn.getLocx());
			statement.setInt(5, spawn.getLocy());
			statement.setInt(6, spawn.getLocz());
			statement.setInt(7, spawn.getHeading());
			statement.setInt(8, spawn.getRespawnDelay());
			statement.setInt(9, spawn.getLocation());
			statement.execute();
		}
		catch(Exception e1)
		{
			_log.warning("spawn couldnt be stored in db:" + e1);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}