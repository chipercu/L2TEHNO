package commands.admin;

import java.sql.ResultSet;
import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;
import l2open.gameserver.instancemanager.ZoneManager;

public class AdminTeleport implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_show_moves,
		admin_show_moves_other,
		admin_show_teleport,
		admin_teleport_to_character,
		admin_teleportto,
		admin_teleport,
		admin_teleport_to,
		admin_move_to,
		admin_moveto,
		admin_teleport_character,
		admin_recall,
		admin_walk,
		admin_recall_npc,
		admin_gonorth,
		admin_gosouth,
		admin_goeast,
		admin_gowest,
		admin_goup,
		admin_godown,
		admin_tele,
		admin_teleto,
		admin_tele_to,
		admin_failed,
		admin_tonpc,
		admin_to_npc,
		admin_toobject,
		admin_fix_gh_1,
		admin_fix_gh_2,
		admin_fix_gh_r,
		admin_correct_tvt,
		admin_setref,
		admin_getref,
		admin_instant_move,
		admin_tel_menu,
		admin_tp_save,
		admin_tp_list,
		admin_tp_delete,
		admin_tr
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanTeleport)
			return false;

		switch(command)
		{
			case admin_show_moves:
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/teleports.htm"));
				break;
			case admin_show_moves_other:
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/tele/other.htm"));
				break;
			case admin_show_teleport:
				showTeleportCharWindow(activeChar);
				break;
			case admin_teleport_to_character:
				teleportToCharacter(activeChar, activeChar.getTarget());
				break;
			case admin_teleport_to:
			case admin_teleportto:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //teleportto charName");
					return false;
				}
				String chaName = Util.joinStrings(" ", wordList, 1);
				L2Player cha = L2ObjectsStorage.getPlayer(chaName);
				if(cha == null)
				{
					activeChar.sendMessage("Player '" + chaName + "' not found in world");
					return false;
				}
				teleportToCharacter(activeChar, cha);
				break;
			case admin_move_to:
			case admin_moveto:
			case admin_teleport:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //move_to x y z");
					return false;
				}
				teleportTo(activeChar, activeChar, Util.joinStrings(" ", wordList, 1));
				break;
			case admin_walk:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //walk x y z");
					return false;
				}
				try
				{
					activeChar.moveToLocation(new Location(Util.joinStrings(" ", wordList, 1)), 0, true);
				}
				catch(IllegalArgumentException e)
				{
					activeChar.sendMessage("USAGE: //walk x y z");
					return false;
				}
				break;
			case admin_gonorth:
			case admin_gosouth:
			case admin_goeast:
			case admin_gowest:
			case admin_goup:
			case admin_godown:
				int val = wordList.length < 2 ? 150 : Integer.parseInt(wordList[1]);
				int x = activeChar.getX();
				int y = activeChar.getY();
				int z = activeChar.getZ();
				if(command == Commands.admin_goup)
					z += val;
				else if(command == Commands.admin_godown)
					z -= val;
				else if(command == Commands.admin_goeast)
					x += val;
				else if(command == Commands.admin_gowest)
					x -= val;
				else if(command == Commands.admin_gosouth)
					y += val;
				else if(command == Commands.admin_gonorth)
					y -= val;

				activeChar.teleToLocation(x, y, z);
				showTeleportWindow(activeChar);
				break;
			case admin_instant_move:
				activeChar.setTeleMode(1);
				break;
			case admin_tele:
				showTeleportWindow(activeChar);
				break;
			case admin_teleto:
			case admin_tele_to:
				if(wordList.length > 1 && wordList[1].equalsIgnoreCase("r"))
					activeChar.setTeleMode(2);
				else if(wordList.length > 1 && wordList[1].equalsIgnoreCase("end"))
					activeChar.setTeleMode(0);
				else
					activeChar.setTeleMode(1);
				break;
			case admin_failed:
				activeChar.sendMessage("Trying ActionFailed...");
				activeChar.sendActionFailed();
				break;
			case admin_tonpc:
			case admin_to_npc:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //tonpc npcId|npcName");
					return false;
				}
				String npcName = Util.joinStrings(" ", wordList, 1);
				L2NpcInstance npc;
				try
				{
					if((npc = L2ObjectsStorage.getByNpcId(Integer.parseInt(npcName))) != null)
					{
						teleportToCharacter(activeChar, npc);
						return true;
					}
				}
				catch(Exception e)
				{}
				/*if((npc = L2ObjectsStorage.getNpc(npcName)) != null)
				{
					teleportToCharacter(activeChar, npc);
					return true;
				}*/
				activeChar.sendMessage("Npc " + npcName + " not found");
				break;
			case admin_toobject:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //toobject objectId");
					return false;
				}
				Integer target = Integer.parseInt(wordList[1]);
				L2Object obj;
				if((obj = L2ObjectsStorage.getCharacter(target)) != null)
				{
					teleportToCharacter(activeChar, obj);
					return true;
				}
				activeChar.sendMessage("Object " + target + " not found");
				break;
			case admin_tp_save:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //tp_save PointName");
					return false;
				}
				saveTeleport(activeChar, Util.joinStrings(" ", wordList, 1));
				showSavePoint(activeChar);
				break;
			case admin_tp_list:
				showSavePoint(activeChar);
				break;
			case admin_tp_delete:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //tp_delete PointName");
					return false;
				}
				removePoint(activeChar, Util.joinStrings(" ", wordList, 1));
				showSavePoint(activeChar);
				break;
			// tp_radius x y z radius
			case admin_tr:
				if(wordList.length == 5)
				{
					x = Integer.parseInt(wordList[1]);
					y = Integer.parseInt(wordList[2]);
					z = Integer.parseInt(wordList[3]);
					int radius = Integer.parseInt(wordList[4]);
					Location loc = new Location(x, y, z);
					for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
						if(player != null)
							teleportTo(activeChar, player, loc);
				}
				else
				{
					activeChar.sendMessage("USAGE: //tp_radius x y z radius");
					return false;
				}
				break;
		}

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_teleport_character:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //teleport_character x y z");
					return false;
				}
				teleportCharacter(activeChar, Util.joinStrings(" ", wordList, 1));
				showTeleportCharWindow(activeChar);
				break;
			case admin_recall:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //recall charName");
					return false;
				}
				String targetName = Util.joinStrings(" ", wordList, 1);
				L2Player recall_player = L2ObjectsStorage.getPlayer(targetName);
				if(recall_player != null)
				{
					teleportTo(activeChar, recall_player, activeChar.getLoc());
					return true;
				}
				int obj_id = Util.GetCharIDbyName(targetName);
				if(obj_id > 0)
				{
					teleportCharacter_offline(obj_id, activeChar.getLoc());
					activeChar.sendMessage(targetName + " is offline. Offline teleport used...");
				}
				else
					activeChar.sendMessage("->" + targetName + "<- is incorrect.");
				break;
			case admin_fix_gh_1:
				L2Territory gh_spawn_loc = null;

				// Зона крафта
				gh_spawn_loc = new L2Territory(10000001);

				gh_spawn_loc.add(45704, 186617, -3480, -3380);
				gh_spawn_loc.add(46086, 186419, -3488, -3388);
				gh_spawn_loc.add(46733, 187506, -3480, -3380);
				gh_spawn_loc.add(46294, 187709, -3480, -3380);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE && player.getReflection().getId() == -2)
					{
						int[] point = gh_spawn_loc.getRandomPoint();
						player.decayMe();
						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона покупки
				gh_spawn_loc = new L2Territory(10000002);

				gh_spawn_loc.add(46091, 186412, -3488, -3388);
				gh_spawn_loc.add(47218, 185902, -3488, -3388);
				gh_spawn_loc.add(47761, 186929, -3480, -3380);
				gh_spawn_loc.add(46742, 187511, -3480, -3380);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY && player.getReflection().getId() == -2)
					{
						int[] point = gh_spawn_loc.getRandomPoint();
						player.decayMe();
						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона продажи
				gh_spawn_loc = new L2Territory(10000003);

				gh_spawn_loc.add(47665, 186755, -3480, -3380);
				gh_spawn_loc.add(48167, 186488, -3480, -3380);
				gh_spawn_loc.add(48397, 186625, -3480, -3380);
				gh_spawn_loc.add(50156, 184674, -3488, -3388);
				gh_spawn_loc.add(49292, 183916, -3488, -3388);
				gh_spawn_loc.add(47758, 185654, -3488, -3388);
				gh_spawn_loc.add(47244, 185894, -3488, -3388);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if((player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE) && player.getReflection().getId() == -2)
					{
						int[] point = gh_spawn_loc.getRandomPoint();
						player.decayMe();
						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}
				break;
			case admin_fix_gh_2:
				L2Territory gh_spawn_loc2 = null;

				// Зона покупки
				gh_spawn_loc2 = new L2Territory(10000004);

				gh_spawn_loc2.add(46091, 186412, -3488, -3388);
				gh_spawn_loc2.add(47218, 185902, -3488, -3388);
				gh_spawn_loc2.add(47761, 186929, -3480, -3380);
				gh_spawn_loc2.add(46742, 187511, -3480, -3380);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
					{
						int[] point = gh_spawn_loc2.getRandomPoint();

						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона крафта
				gh_spawn_loc2 = new L2Territory(10000005);

				gh_spawn_loc2.add(45704, 186617, -3480, -3380);
				gh_spawn_loc2.add(46086, 186419, -3488, -3388);
				gh_spawn_loc2.add(46733, 187506, -3480, -3380);
				gh_spawn_loc2.add(46294, 187709, -3480, -3380);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
					{
						int[] point = gh_spawn_loc2.getRandomPoint();
						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона продажи
				gh_spawn_loc2 = new L2Territory(10000006);

				gh_spawn_loc2.add(47665, 186755, -3480, -3380);
				gh_spawn_loc2.add(48167, 186488, -3480, -3380);
				gh_spawn_loc2.add(48397, 186625, -3480, -3380);
				gh_spawn_loc2.add(50156, 184674, -3488, -3388);
				gh_spawn_loc2.add(49292, 183916, -3488, -3388);
				gh_spawn_loc2.add(47758, 185654, -3488, -3388);
				gh_spawn_loc2.add(47244, 185894, -3488, -3388);

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
					{
						int[] point = gh_spawn_loc2.getRandomPoint();
						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}
				break;
			case admin_fix_gh_r:
				int radius = 500;
				if(wordList.length > 1)
					radius = Integer.parseInt(wordList[1]);

				L2Territory spawn_loc = null;

				// Зона покупки
				spawn_loc = new L2Territory(10000004);

				spawn_loc.add(46091, 186412, -3488, -3388);
				spawn_loc.add(47218, 185902, -3488, -3388);
				spawn_loc.add(47761, 186929, -3480, -3380);
				spawn_loc.add(46742, 187511, -3480, -3380);

				for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
					{
						int[] point = spawn_loc.getRandomPoint();

						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона крафта
				spawn_loc = new L2Territory(10000005);

				spawn_loc.add(45704, 186617, -3480, -3380);
				spawn_loc.add(46086, 186419, -3488, -3388);
				spawn_loc.add(46733, 187506, -3480, -3380);
				spawn_loc.add(46294, 187709, -3480, -3380);

				for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
					{
						int[] point = spawn_loc.getRandomPoint();
						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}

				// Зона продажи
				spawn_loc = new L2Territory(10000006);

				spawn_loc.add(47665, 186755, -3480, -3380);
				spawn_loc.add(48167, 186488, -3480, -3380);
				spawn_loc.add(48397, 186625, -3480, -3380);
				spawn_loc.add(50156, 184674, -3488, -3388);
				spawn_loc.add(49292, 183916, -3488, -3388);
				spawn_loc.add(47758, 185654, -3488, -3388);
				spawn_loc.add(47244, 185894, -3488, -3388);

				for(L2Player player : L2World.getAroundPlayers(activeChar, radius, 200))
					if(player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
					{
						int[] point = spawn_loc.getRandomPoint();
						player.decayMe();

						if(player.getReflection().getId() != -2)
						{
							player.setVar("backCoords", player.getLoc().toXYZString());
							player.setReflection(-2);
						}

						player.setXYZ(point[0], point[1], point[2]);
						player.spawnMe();
					}
				break;
			case admin_setref:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //setref <reflection>");
					return false;
				}

				int ref_id = Integer.parseInt(wordList[1]);
				if(ref_id != 0 && ReflectionTable.getInstance().get(ref_id) == null)
				{
					activeChar.sendMessage("Reflection <" + ref_id + "> not found.");
					return false;
				}

				L2Object target = activeChar;
				L2Object obj = activeChar.getTarget();
				if(obj != null)
					target = obj;

				target.setReflection(ref_id);
				target.decayMe();
				target.spawnMe();
				break;
			}
			case admin_getref:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //getref <char_name>");
					return false;
				}
				L2Player cha = L2ObjectsStorage.getPlayer(wordList[1]);
				if(cha == null)
				{
					activeChar.sendMessage("Player '" + wordList[1] + "' not found in world");
					return false;
				}
				activeChar.sendMessage("Player '" + wordList[1] + "' in reflection: " + activeChar.getReflection().getId() + ", name: " + activeChar.getReflection().getName());
				break;
			case admin_correct_tvt:
				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null)
						try
						{
							String var = player.getVar("TvT_backCoords");
							if(var == null || var.equals(""))
								continue;
							String[] coords = var.split(" ");
							if(coords.length != 4)
								continue;
							player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), 0);
							player.unsetVar("TvT_backCoords");
						}
						catch(Exception e)
						{}

				for(L2Player player : L2ObjectsStorage.getPlayers())
					if(player != null)
						try
						{
							String var = player.getVar("LastHero_backCoords");
							if(var == null || var.equals(""))
								continue;
							String[] coords = var.split(" ");
							if(coords.length != 4)
								continue;
							player.teleToLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), 0);
							player.unsetVar("LastHero_backCoords");
						}
						catch(Exception e)
						{}
				break;
			case admin_tel_menu:
				if(wordList.length == 2)
				{
					int zoneId = Integer.parseInt(wordList[1]);
					L2Zone zone = ZoneManager.getInstance().getZoneById(ZoneType.Town, zoneId, false);

					NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
					String content = Files.read("data/html/admin/tel_menu2.htm", activeChar);

					StringBuffer replyMSG = new StringBuffer();

					int i = 0;
					for(int[] coords : zone.getSpawns())
					{
						replyMSG.append("<button value=\"(" + i + ")\" action=\"bypass -h admin_move_to " + coords[0] + " " + coords[1] + " " + coords[2] + "\" width=265 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br1>");
						i++;
					}

					content = content.replace("%menu%", replyMSG.toString());
					adminReply.setHtml(content);
					activeChar.sendPacket(adminReply);
				}
				else
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/tel_menu.htm"));
				break;
		}

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_recall_npc:
				recallNPC(activeChar);
				break;
		}

		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void showTeleportWindow(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><title>Teleport Menu</title>");
		replyMSG.append("<body>");

		replyMSG.append("<br>");
		replyMSG.append("<center><table>");

		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

		replyMSG.append("</table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public class Teleport
	{
		public int id = 0;
		public String name = "";
		public int x = 0;
		public int y = 0;
		public int z = 0;
	}

	private void removePoint(L2Player player, String name)
	{
		int id = Integer.parseInt(name);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM comteleport WHERE charId=? AND TpId=?;");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, id);
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

	private void showSavePoint(L2Player player)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer html = new StringBuffer("<html><title>Teleport Menu</title>");
		html.append("<body scroll=no>");
		html.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr>");
		html.append("<td valign=\"top\" align=\"center\">");
		html.append("<br><br>");
		html.append("<font name=hs12>Точки телепорта</font>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");

		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM comteleport WHERE charId=?;");
			statement.setLong(1, player.getObjectId());
			rset = statement.executeQuery();
			html.append("<table border=0 width=280>");

			Teleport teleport = null;
			int count = 0;
			while(rset.next())
			{
				teleport = new Teleport();
				teleport.id = rset.getInt("TpId");
				teleport.name = rset.getString("name");
				teleport.x = rset.getInt("xPos");
				teleport.y = rset.getInt("yPos");
				teleport.z = rset.getInt("zPos");

				html.append("<tr>");
				html.append("<td valign=\"top\" align=\"right\">");
				html.append("<button value=\"" + teleport.name + "\" action=\"bypass -h admin_move_to " + teleport.x + " " + teleport.y + " " + teleport.z + "\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>");
				html.append("<td valign=\"top\" align=\"left\">");
				html.append("<button value=\"X\" action=\"bypass -h admin_tp_delete " + teleport.id + "\" width=25 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
				html.append("</td>)");
				html.append("</tr>");
				count++;
			}
			html.append("</table>");

			html.append("<table border=0 width=280>");
			html.append("<tr>");
			html.append("<td FIXWIDTH=286 valign=\"top\" align=\"center\">");
			html.append(count == 0 ? "<font color=\"FF0000\">Список пуст</font>" : "Всего сохранено точек: " + count + "");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td FIXWIDTH=286 valign=\"top\" align=\"center\">");
			html.append("<edit var=\"name\" width=200>");
			html.append("</td>");
			html.append("</tr>");
			html.append("<tr>");
			html.append("<td FIXWIDTH=286 valign=\"top\" align=\"center\">");
			html.append("<br><button value=\"Сохранить текущую позицию\" action=\"bypass -h admin_tp_save $name\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			html.append("</td>");
			html.append("</tr>");
			html.append("</table>");

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</body></html>");

		adminReply.setHtml(html.toString());
		player.sendPacket(adminReply);
	}

	private void showTeleportCharWindow(L2Player activeChar)
	{
		L2Object target = activeChar.getTarget();
		if(target == null)
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		L2Player player = null;
		if(target.isPlayer())
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><title>Teleport Character</title>");
		replyMSG.append("<body>");
		replyMSG.append("The character you will teleport is " + player.getName() + ".");
		replyMSG.append("<br>");

		replyMSG.append("Co-ordinate x");
		replyMSG.append("<edit var=\"char_cord_x\" width=110>");
		replyMSG.append("Co-ordinate y");
		replyMSG.append("<edit var=\"char_cord_y\" width=110>");
		replyMSG.append("Co-ordinate z");
		replyMSG.append("<edit var=\"char_cord_z\" width=110>");
		replyMSG.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void teleportTo(L2Player activeChar, L2Player target, String Cords)
	{
		try
		{
			teleportTo(activeChar, target, new Location(Cords));
		}
		catch(IllegalArgumentException e)
		{
			activeChar.sendMessage("You must define 3 coordinates required to teleport");
			return;
		}
	}

	private void teleportTo(L2Player activeChar, L2Player target, Location loc)
	{
		if(!target.equals(activeChar))
			target.sendMessage("Admin is teleporting you.");

		target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		target.teleToLocation(loc, activeChar.getReflection().getId());

		if(target.equals(activeChar))
			activeChar.sendMessage("You have been teleported to " + loc);
	}

	private void teleportCharacter(L2Player activeChar, String Cords)
	{
		L2Object target = activeChar.getTarget();
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}
		if(target.getObjectId() == activeChar.getObjectId())
		{
			activeChar.sendMessage("You cannot teleport yourself.");
			return;
		}
		teleportTo(activeChar, (L2Player) target, Cords);
	}

	private void teleportCharacter_offline(int obj_id, Location loc)
	{
		if(obj_id == 0)
			return;

		ThreadConnection con = null;
		FiltredPreparedStatement st = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET x=?,y=?,z=? WHERE obj_Id=? LIMIT 1");
			st.setInt(1, loc.x);
			st.setInt(2, loc.y);
			st.setInt(3, loc.z);
			st.setInt(4, obj_id);
			st.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, st);
		}
	}

	private void teleportToCharacter(L2Player activeChar, L2Object target)
	{
		if(target == null)
			return;

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		activeChar.teleToLocation(target.getLoc().changeZ(25), target.getReflection().getId());

		activeChar.sendMessage("You have teleported to " + target);
	}

	private void saveTeleport(L2Player player, String name)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT COUNT(*) FROM comteleport WHERE charId=? AND name=?;");
			statement.setLong(1, player.getObjectId());
			statement.setString(2, name);
			rset = statement.executeQuery();
			rset.next();

			statement = con.prepareStatement(rset.getInt(1) == 0 ? "INSERT INTO comteleport (charId, xPos, yPos, zPos, name) VALUES(?,?,?,?,?)" : "UPDATE comteleport SET xPos=?, yPos=?, zPos=? WHERE charId=? AND name=?;");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getX());
			statement.setInt(3, player.getY());
			statement.setInt(4, player.getZ());
			statement.setString(5, name);
			statement.execute();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void recallNPC(L2Player activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if(obj != null && obj.isNpc())
		{
			L2NpcInstance target = (L2NpcInstance) obj;
			L2Spawn spawn = target.getSpawn();

			int monsterTemplate = target.getTemplate().npcId;

			L2NpcTemplate template1 = NpcTable.getTemplate(monsterTemplate);

			if(template1 == null)
			{
				activeChar.sendMessage("Incorrect monster template.");
				return;
			}

			int respawnTime = spawn.getRespawnDelay();

			target.deleteMe();
			spawn.stopRespawn();

			try
			{
				// L2MonsterInstance mob = new L2MonsterInstance(monsterTemplate,
				// template1);

				spawn = new L2Spawn(template1);
				spawn.setLoc(activeChar.getLoc());
				spawn.setAmount(1);
				spawn.setReflection(activeChar.getReflection().getId());
				spawn.setRespawnDelay(respawnTime);
				spawn.init();

				activeChar.sendMessage("Created " + template1.name + " on " + target.getObjectId() + ".");
			}
			catch(Exception e)
			{
				activeChar.sendMessage("Target is not in game.");
			}

		}
		else
			activeChar.sendPacket(Msg.INVALID_TARGET);
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