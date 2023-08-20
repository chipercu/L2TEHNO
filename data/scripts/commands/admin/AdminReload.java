package commands.admin;

import services.VoteManager;
import l2open.config.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.extensions.scripts.Scripts;
import l2open.extensions.scripts.Scripts.ScriptClassAndMethod;
import l2open.gameserver.TradeController;
import l2open.gameserver.cache.*;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.FishTable;
import l2open.gameserver.tables.GmListTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SpawnTable;
import l2open.gameserver.tables.StaticObjectsTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.util.Files;
import l2open.util.Strings;

public class AdminReload implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_reload,
		admin_reload_fstring,
		admin_reload_multisell,
		admin_reload_gmaccess,
		admin_reload_htm,
		admin_reload_qs,
		admin_reload_qs_help,
		admin_reload_loc,
		admin_reload_skills,
		admin_reload_npc,
		admin_reload_spawn,
		admin_reload_fish,
		admin_reload_abuse,
		admin_reload_translit,
		admin_reload_shops,
		admin_reload_static,
		admin_reload_doors,
		admin_reload_pkt_logger,
		admin_reload_pets,
		admin_reload_locale,
		admin_reload_instances,
		admin_reload_nobles,
		admin_reload_vote,
		admin_reload_locali,
		admin_reload_config,
		admin_reload_image
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanReload)
			return false;

		switch(command)
		{
			case admin_reload:
				break;
			case admin_reload_image:
				ImagesChache.getInstance().load();
				break;
			case admin_reload_fstring:
				FStringCache.reload();
				break;
			case admin_reload_multisell:
			{
				try
				{
					L2Multisell.getInstance().reload();
				}
				catch(Exception e)
				{
					return false;
				}
				for(ScriptClassAndMethod handler : Scripts.onReloadMultiSell)
					activeChar.callScripts(handler.scriptClass, handler.method);
				activeChar.sendMessage("Multisell list reloaded!");
				break;
			}
			case admin_reload_gmaccess:
			{
				try
				{
					ConfigSystem.loadGMAccess();
					for(L2Player player : L2ObjectsStorage.getPlayers())
						if(!ConfigValue.EverybodyHasAdminRights)
							player.setPlayerAccess(ConfigSystem.gmlist.get(player.getObjectId()));
						else
						{
							if(ConfigSystem.gmlist.containsKey(player.getObjectId()))
								player.setPlayerAccess(ConfigSystem.gmlist.get(player.getObjectId()));
							else
								player.setPlayerAccess(ConfigSystem.gmlist.get(new Integer(0)));
						}
				}
				catch(Exception e)
				{
					return false;
				}
				activeChar.sendMessage("GMAccess reloaded!");
				break;
			}
			case admin_reload_htm:
			{
				Files.cacheClean();
				Files.loadPtsHtml(".data/html-ru.zip");
				Files.loadPtsHtml(".data/html-en.zip");
				activeChar.sendMessage("HTML cache clearned.");
				break;
			}
			case admin_reload_qs:
			{
				if(fullString.endsWith("all"))
					for(L2Player p : L2ObjectsStorage.getPlayers())
						reloadQuestStates(p);
				else
				{
					L2Object t = activeChar.getTarget();

					if(t != null && t.isPlayer())
					{
						L2Player p = (L2Player) t;
						reloadQuestStates(p);
					}
					else
						reloadQuestStates(activeChar);
				}
				break;
			}
			case admin_reload_qs_help:
			{
				activeChar.sendMessage("");
				activeChar.sendMessage("Quest Help:");
				activeChar.sendMessage("reload_qs_help - This Message.");
				activeChar.sendMessage("reload_qs <selected target> - reload all quest states for target.");
				activeChar.sendMessage("reload_qs <no target or target is not player> - reload quests for self.");
				activeChar.sendMessage("reload_qs all - reload quests for all players in world.");
				activeChar.sendMessage("");
				break;
			}
			case admin_reload_loc:
			{
				TerritoryTable.getInstance().reloadData();
				ZoneManager.getInstance().reload();
				GmListTable.broadcastMessageToGMs("Locations and zones reloaded.");
				break;
			}
			case admin_reload_skills:
			{
				SkillTable.getInstance().reload();
				GmListTable.broadcastMessageToGMs("Skill table reloaded by " + activeChar.getName() + ".");
				_log.info("Skill table reloaded by " + activeChar.getName() + ".");
				break;
			}
			case admin_reload_npc:
			{
				NpcTable.getInstance().reloadAllNpc();
				GmListTable.broadcastMessageToGMs("Npc table reloaded.");
				break;
			}
			case admin_reload_spawn:
			{
				SpawnTable.getInstance().reloadAll();
				GmListTable.broadcastMessageToGMs("All npc respawned.");
				break;
			}
			case admin_reload_fish:
			{
				FishTable.getInstance().reload();
				GmListTable.broadcastMessageToGMs("Fish table reloaded.");
				break;
			}
			case admin_reload_abuse:
			{
				ConfigSystem.abuseLoad();
				GmListTable.broadcastMessageToGMs("Abuse reloaded.");
				break;
			}
			case admin_reload_translit:
			{
				Strings.reload();
				GmListTable.broadcastMessageToGMs("Translit reloaded.");
				break;
			}
			case admin_reload_shops:
			{
				TradeController.reload();
				GmListTable.broadcastMessageToGMs("Shops reloaded.");
				break;
			}
			case admin_reload_static:
			{
				StaticObjectsTable.getInstance().reloadStaticObjects();
				GmListTable.broadcastMessageToGMs("Static objects table reloaded.");
				break;
			}
			case admin_reload_doors:
			{
				DoorTable.getInstance().respawn();
				GmListTable.broadcastMessageToGMs("Door table reloaded.");
				break;
			}
			case admin_reload_pkt_logger:
			{
				try
				{
					//Config.reloadPacketLoggerConfig();
					activeChar.sendMessage("Packet Logger setting reloaded");
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Failed reload Packet Logger setting. Check stdout for error!");
				}
				break;
			}
			case admin_reload_pets:
			{
				PetDataTable.reload();
				GmListTable.broadcastMessageToGMs("PetDataTable reloaded");
				break;
			}
			case admin_reload_locale:
			{
				CustomMessage.reload();
				GmListTable.broadcastMessageToGMs("Localization reloaded");
				break;
			}
			case admin_reload_instances:
			{
				InstancedZoneManager.getInstance().reload();
				DimensionalRiftManager.getInstance().reload();
				GmListTable.broadcastMessageToGMs("Instanced zones reloaded");

				Reflection r = ReflectionTable.SOD_REFLECTION_ID == 0 ? null : ReflectionTable.getInstance().get(ReflectionTable.SOD_REFLECTION_ID);
				if(r != null)
					r.collapse();
				ServerVariables.unset("SoD_id");
				break;
			}
			case admin_reload_nobles:
			{
				OlympiadDatabase.loadNobles();
				OlympiadDatabase.loadNoblesRank();
				break;
			}
			case admin_reload_vote:
			{
				VoteManager.load();
				break;
			}
			case admin_reload_locali:
			{
				CustomMessage.reload();
				break;
			}
			case admin_reload_config:
			{
				try
				{
					ConfigSystem.reload();
				}
				catch(Exception e) 
				{
                    e.printStackTrace();
					activeChar.sendMessage("Config reloaded Error!!!");
                } 
				GmListTable.broadcastMessageToGMs("Config reloaded");
				break;
			}
		}
		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/reload.htm"));
		return true;
	}

	private void reloadQuestStates(L2Player p)
	{
		for(QuestState qs : p.getAllQuestsStates())
			p.delQuestState(qs.getQuest().getName());
		Quest.playerEnter(p);
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