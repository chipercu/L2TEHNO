package commands.admin;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.serverpackets.ExShowTrace;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;

public class AdminZone extends Functions implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_zone_check,
		admin_region,
		admin_loc,
		admin_xloc,
		admin_pos,
		admin_showloc,
		admin_location,
		admin_loc_begin,
		admin_loc_add,
		admin_loc_reset,
		admin_loc_end,
		admin_loc_remove,
		admin_vis_count,
		admin_show_locations,
		admin_zonec,
		admin_zone_tp
	}

	private static GArray<int[]> create_loc;
	private static int create_loc_id;
	private static int _loc_id = 900521;

	private static void locationMenu(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><body><title>Location Create</title>");

		replyMSG.append("<center><table width=260><tr>");
		replyMSG.append("<td width=70>Location:</td>");
		replyMSG.append("<td width=50><edit var=\"loc\" width=50 height=12></td>");
		replyMSG.append("<td width=50><button value=\"Show\" action=\"bypass -h admin_showloc $loc\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"New Location\" action=\"bypass -h admin_loc_begin $loc\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"New Location End\" action=\"bypass -h admin_loc_begin "+_loc_id+"\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br></center>");

		if(create_loc != null)
		{
			replyMSG.append("<center><table width=260><tr>");
			replyMSG.append("<td width=80><button value=\"Add Point\" action=\"bypass -h admin_loc_add menu\" width=80 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=90><button value=\"Reset Points\" action=\"bypass -h admin_loc_reset menu\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=90><button value=\"End Location\" action=\"bypass -h admin_loc_end menu\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr></table></center>");

			replyMSG.append("<center><button value=\"Show\" action=\"bypass -h admin_loc_showloc " + create_loc_id + " menu\" width=80 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");

			replyMSG.append("<br><br>");

			int i = 0;
			for(int[] loc : create_loc)
			{
				replyMSG.append("<button value=\"Remove\" action=\"bypass -h admin_loc_remove " + i + "\" width=80 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
				replyMSG.append("&nbsp;&nbsp;(" + loc[0] + ", " + loc[1] + ", " + loc[2] + ")<br1>");
				i++;
			}
		}

		replyMSG.append("</body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(activeChar == null || !activeChar.getPlayerAccess().CanTeleport)
			return false;

		switch(command)
		{
			case admin_zone_tp:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //zone_tp <zone_id>");
					return false;
				}

				String zone_id = wordList[1];
				L2Zone zone = ZoneManager.getInstance().getZoneById(Integer.parseInt(zone_id));
				if(zone == null)
				{
					activeChar.sendMessage("Zone <" + zone_id + "> undefined.");
					return false;
				}
				//if(!zone.getLoc().isInside(activeChar.getX(), activeChar.getY()))
				{
					int[] _loc = zone.getLoc().getRandomPoint();
					activeChar.teleToLocation(_loc[0], _loc[1], _loc[2]);
				}
				activeChar.sendPacket(Points2Trace(zone.getLoc().getCoords(), 50, true, true));
				break;
			}
			case admin_zonec:
				L2Territory territory = new L2Territory(899999);
				territory.add(70600, 130952, -3696, 3696);
				territory.add(128440, 130616, -2160, 2160);
				territory.add(133560, 98216, -736, 736);
				territory.add(65032, 98760, -3552, 3552);
				territory.validate();
				TerritoryTable.getInstance().getLocations().put(899999, territory);
				L2World.addTerritory(territory);
				for(L2Territory terr : TerritoryTable.getInstance().getLocations().values())
				{
					if(terr == null)
						continue;
					if(terr.getId() >=900000)
						if(territory.isInside(terr.getXmax(), terr.getYmax()))
						{
							boolean isSpawn = true;
							for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
							{
								int location = spawn.getLocation();
								if(location == 0)
									continue;
								if(location == terr.getId())
									isSpawn= false;
							}
							if(isSpawn)
							{
								activeChar.sendMessage("Territory: " + terr.getId());
								System.out.println("Territory: " + terr.getId());
							}
						}
				}
				break;
			case admin_zone_check:
			{
				activeChar.sendMessage("===== Active Territories =====");
				GArray<L2Territory> territories = L2World.getTerritories(activeChar.getX(), activeChar.getY(), activeChar.getZ());
				if(territories != null)
					for(L2Territory terr : territories)
					{
						activeChar.sendMessage("Territory: " + terr.getId());
						if(terr.getZone() != null)
							activeChar.sendMessage("Zone: " + terr.getZone().getType().toString() + ", id: " + terr.getZone().getId() + ", state: " + (terr.getZone().isActive() ? "active" : "not active"));
					}
				activeChar.sendMessage("======= Mob Spawns =======");
				for(L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
				{
					int location = spawn.getLocation();
					if(location == 0)
						continue;
					if(location < 0)
						location*=-1;
					L2Territory terr = TerritoryTable.getInstance().getLocation(location);
					if(terr == null)
						continue;
					if(terr.isInside(activeChar.getX(), activeChar.getY()))
						activeChar.sendMessage("Territory: " + terr.getId());
				}
				break;
			}
			case admin_region:
			{
				activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion().getName());
				activeChar.sendMessage("Objects list:");
				int players=0;
				int pet=0;
				int npc=0;
				for(L2Object o : activeChar.getCurrentRegion().getObjectsList(new GArray<L2Object>(activeChar.getCurrentRegion().getObjectsSize()), 0, activeChar.getReflection()))
					if(o != null)
					{
						activeChar.sendMessage(o.toString());
						if(o.isPlayable())
						{
							if(o.isPlayer())
								players++;
							else
								pet++;
						}
						else
							npc++;
					}
				activeChar.sendMessage("Object counts: npc="+npc+" player="+players+" pet="+pet);
				break;
			}
			case admin_vis_count:
			{
				activeChar.sendMessage("Players count: " + L2World.getAroundPlayers(activeChar).size());
				break;
			}
				/*
				* Пишет в консоль текущую точку для локации, оформляем в виде SQL запроса
				* пример: (8699,'loc_8699',111104,-112528,-1400,-1200),
				* Удобно для рисования локаций под спавн, разброс z +100/-10
				* необязательные параметры: id локации и название локации
				* Бросает бутылку, чтобы не запутаццо :)
				*/
			case admin_loc:
			{
				String loc_id = "0";
				String loc_name;
				if(wordList.length > 1)
					loc_id = wordList[1];
				if(wordList.length > 2)
					loc_name = wordList[2];
				else
					loc_name = "loc_" + loc_id;
				System.out.println("	(" + loc_id + ",'" + loc_name + "'," + activeChar.getX() + "," + activeChar.getY() + "," + activeChar.getZ() + "," + (activeChar.getZ() + 100) + ",0),");
				activeChar.sendMessage("Point saved.");
				L2ItemInstance temp = ItemTemplates.getInstance().createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
			case admin_xloc:
			{
				System.out.println("			<coords loc=\"" + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + " 0\" />");
				activeChar.sendMessage("Point saved.");
				L2ItemInstance temp = ItemTemplates.getInstance().createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				break;
			}
			case admin_pos:
				String pos = activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading() + " Geo [" + (activeChar.getX() - L2World.MAP_MIN_X >> 4) + ", " + (activeChar.getY() - L2World.MAP_MIN_Y >> 4) + "] Ref " + activeChar.getReflection().getId();
				System.out.println(activeChar.getName() + "'s position: " + pos);
				activeChar.sendMessage("Pos: " + pos);
				break;
			case admin_location:
				locationMenu(activeChar);
				break;
			case admin_loc_begin:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //loc_begin <location_id>");
					locationMenu(activeChar);
					return false;
				}
				try
				{
					create_loc_id = Integer.valueOf(wordList[1]);
					_loc_id = create_loc_id;
				}
				catch(Exception E)
				{
					activeChar.sendMessage("location_id should be integer");
					create_loc = null;
					locationMenu(activeChar);
					return false;
				}

				create_loc = new GArray<int[]>();
				create_loc.add(new int[] { activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getZ() + 100 });
				L2ItemInstance temp = ItemTemplates.getInstance().createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());
				activeChar.sendMessage("Location("+_loc_id+"): Now you can add points...");
				activeChar.sendPacket(new ExShowTrace(60000));
				locationMenu(activeChar);
				break;
			}
			case admin_loc_add:
			{
				if(create_loc == null)
				{
					activeChar.sendMessage("Location not started");
					locationMenu(activeChar);
					return false;
				}

				create_loc.add(new int[] { activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getZ() + 100 });
				L2ItemInstance temp = ItemTemplates.getInstance().createItem(1060);
				temp.dropMe(activeChar, activeChar.getLoc());

				if(create_loc.size() > 1)
					activeChar.sendPacket(Points2Trace(create_loc, 50, false, false));
				if(wordList.length > 1 && wordList[1].equals("menu"))
					locationMenu(activeChar);
				break;
			}
			case admin_loc_reset:
			{
				if(create_loc == null)
				{
					activeChar.sendMessage("Location not started");
					locationMenu(activeChar);
					return false;
				}

				create_loc.clear();
				activeChar.sendPacket(new ExShowTrace(60000));
				locationMenu(activeChar);
				break;
			}
			case admin_loc_end:
			{
				if(create_loc == null)
				{
					activeChar.sendMessage("Location not started");
					locationMenu(activeChar);
					return false;
				}
				if(create_loc.size() < 3)
				{
					activeChar.sendMessage("Minimum location size 3 points");
					locationMenu(activeChar);
					return false;
				}
				_loc_id++;

				//String prefix = "(" + create_loc_id + ",'loc_" + create_loc_id + "',";
				String prefix = "(" + create_loc_id + ",'dragon_valley',";
				
				for(int[] _p : create_loc)
					System.out.println(prefix + _p[0] + "," + _p[1] + "," + _p[2] + "," + _p[3] + ", 0),");
				System.out.println("");

				activeChar.sendPacket(Points2Trace(create_loc, 50, true, false));
				create_loc = null;
				create_loc_id = 0;
				activeChar.sendMessage("Location Created, check stdout");
				if(wordList.length > 1 && wordList[1].equals("menu"))
					locationMenu(activeChar);
				break;
			}
			case admin_showloc:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //showloc <location>");
					return false;
				}

				String loc_id = wordList[1];
				L2Territory terr = TerritoryTable.getInstance().getLocations().get(Integer.parseInt(loc_id));
				if(terr == null)
				{
					activeChar.sendMessage("Location <" + loc_id + "> undefined.");
					return false;
				}
				if(!terr.isInside(activeChar.getX(), activeChar.getY()))
				{
					int[] _loc = terr.getRandomPoint();
					activeChar.teleToLocation(_loc[0], _loc[1], _loc[2]);
				}
				activeChar.sendPacket(Points2Trace(terr.getCoords(), 50, true, false));

				if(wordList.length > 2 && wordList[2].equals("menu"))
					locationMenu(activeChar);
				break;
			}
			case admin_loc_remove:
			{
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Usage: //showloc <location>");
					return false;
				}

				if(create_loc == null)
				{
					activeChar.sendMessage("Location not started");
					locationMenu(activeChar);
					return false;
				}

				int point_id = Integer.parseInt(wordList[1]);

				create_loc.remove(point_id);

				if(create_loc.size() > 1)
					activeChar.sendPacket(Points2Trace(create_loc, 50, false, false));

				locationMenu(activeChar);
				break;
			}
			case admin_show_locations:
			{
				for(L2Territory terr : TerritoryTable.getInstance().getLocations().values())
					if(activeChar.isInRange(terr.getCenter(), 2000))
						activeChar.sendPacket(Points2Trace(terr.getCoords(), 50, true, false));
				for(L2Territory terr : TerritoryTable.getInstance().getLocations().values())
					if(activeChar.isInRange(terr.getCenter(), 2000))
						activeChar.sendPacket(Points2Trace(terr.getCoords(), 50, true, true));
				break;
			}
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
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