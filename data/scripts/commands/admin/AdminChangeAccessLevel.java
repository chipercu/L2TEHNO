package commands.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import l2open.config.*;
import l2open.database.mysql;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Files;
import l2open.util.Util;

/**
 * changelvl - Изменение уровня доступа
 * moders - Панель управления модераторами
 * moders_add - Добавление модератора
 * moders_del - Удаление модератора
 * moders_log - Просмотр логов бана чата модераторов
 * penalty - Штраф за некорректное модерирование
 */
public class AdminChangeAccessLevel implements IAdminCommandHandler, ScriptFile
{
	private static final String MODERATORS_LOG_FILE = "./log/game/banchat.txt";

	private static enum Commands
	{
		admin_changelvl,
		admin_moders,
		admin_moders_add,
		admin_moders_del,
		admin_moders_log,
		admin_penalty
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanGmEdit)
			return false;

		switch(command)
		{
			case admin_changelvl:
				if(wordList.length == 2)
				{
					int lvl = Integer.parseInt(wordList[1]);
					if(activeChar.getTarget().isPlayer())
						((L2Player) activeChar.getTarget()).setAccessLevel(lvl);
				}
				else if(wordList.length == 3)
				{
					int lvl = Integer.parseInt(wordList[2]);
					L2Player player = L2ObjectsStorage.getPlayer(wordList[1]);
					if(player != null)
						player.setAccessLevel(lvl);
				}
				break;
			case admin_moders:
				// Панель управления модераторами
				showModersPannel(activeChar);
				break;
			case admin_moders_add:
				if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()/* || activeChar.getTarget() == activeChar*/)
				{
					activeChar.sendMessage("Incorrect target. Please select a player.");
					showModersPannel(activeChar);
					return false;
				}

				L2Player modAdd = activeChar.getTarget().getPlayer();
				if(ConfigSystem.gmlist.containsKey(modAdd.getObjectId()))
				{
					activeChar.sendMessage("Error: Moderator " + modAdd.getName() + " already in server access list.");
					showModersPannel(activeChar);
					return false;
				}

				// Копируем файл с привилегиями модератора
				String newFName = "m" + modAdd.getObjectId() + ".xml";
				if(!Files.copyFile("./config/GMAccess.d/template/moderator.xml", "./config/GMAccess.d/" + newFName))
				{
					activeChar.sendMessage("Error: Failed to copy access-file.");
					showModersPannel(activeChar);
					return false;
				}

				// Замена objectId
				String res = "";
				try
				{
					BufferedReader in = new BufferedReader(new FileReader("./config/GMAccess.d/" + newFName));
					String str;
					while((str = in.readLine()) != null)
						res += str + "\n";
					in.close();

					res = res.replaceFirst("ObjIdPlayer", "" + modAdd.getObjectId());
					Files.writeFile("./config/GMAccess.d/" + newFName, res);
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Error: Failed to modify object ID in access-file.");
					File fDel = new File("./config/GMAccess.d/" + newFName);
					if(fDel.exists())
						fDel.delete();
					showModersPannel(activeChar);
					return false;
				}

				// Устанавливаем права модератору
				File af = new File("./config/GMAccess.d/" + newFName);
				if(!af.exists())
				{
					activeChar.sendMessage("Error: Failed to read access-file for " + modAdd.getName());
					showModersPannel(activeChar);
					return false;
				}

				ConfigSystem.loadGMAccess(af);
				modAdd.setPlayerAccess(ConfigSystem.gmlist.get(modAdd.getObjectId()));

				activeChar.sendMessage("Moderator " + modAdd.getName() + " added.");
				showModersPannel(activeChar);
				break;
			case admin_moders_del:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Please specify moderator object ID to delete moderator.");
					showModersPannel(activeChar);
					return false;
				}

				int oid = Integer.parseInt(wordList[1]);

				// Удаляем права из серверного списка
				if(ConfigSystem.gmlist.containsKey(oid))
					ConfigSystem.gmlist.remove(oid);
				else
				{
					activeChar.sendMessage("Error: Moderator with object ID " + oid + " not found in server access lits.");
					showModersPannel(activeChar);
					return false;
				}

				// Если удаляемый модератор онлайн, то отбираем у него права на ходу
				L2Player modDel = L2ObjectsStorage.getPlayer(oid);
				if(modDel != null)
					modDel.setPlayerAccess(null);

				// Удаляем файл с правами
				String fname = "m" + oid + ".xml";
				File f = new File("./config/GMAccess.d/" + fname);
				if(!f.exists() || !f.isFile() || !f.delete())
				{
					activeChar.sendMessage("Error: Can't delete access-file: " + fname);
					showModersPannel(activeChar);
					return false;
				}

				if(modDel != null)
					activeChar.sendMessage("Moderator " + modDel.getName() + " deleted.");
				else
					activeChar.sendMessage("Moderator with object ID " + oid + " deleted.");

				showModersPannel(activeChar);
				break;
			case admin_moders_log:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("Please specify moderator object ID to show logs.");
					showModersPannel(activeChar);
					return false;
				}

				int objId = Integer.parseInt(wordList[1]);

				NpcHtmlMessage reply = new NpcHtmlMessage(5);
				String html = "Moderators managment panel.<br>";
				html += "<p align=right>";
				html += "<button width=120 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders\" value=\"Managment\">";
				html += "</p><br>";

				String name = Util.getPlayerNameByObjId(objId);
				if(name == null || name.isEmpty())
				{
					html += "<center><font color=LEVEL>Unknown moderator for '" + objId + "' object Id.</font></center>";
					reply.setHtml(html);
					activeChar.sendPacket(reply);
					return false;
				}

				int entrys = getEntrysCountForModerator(objId);
				final int showEntrys = 15; // показывать последние n записей лога
				try
				{
					html += "<center><font color=LEVEL>Logs for moderator '" + name + "':</font></center>";
					html += "Total records: <font color=LEVEL>" + entrys + "</font>";
					html += "<table width=285 border=1 cellspacing=0>";
					html += "<tr>";
					html += "<td width=50>Banned</td>";
					html += "<td width=50>Duration</td>";
					html += "<td width=130 align=\"center\">Reason</td>";
					html += "</tr>";
					BufferedReader log = new BufferedReader(new FileReader(MODERATORS_LOG_FILE));
					String str;
					int counter = 0;
					while((str = log.readLine()) != null)
					{
						String splited[] = str.split(" ");
						if(splited.length < 1 || objId != Integer.parseInt(splited[5].substring(5, 14)))
							continue;
						if(counter < entrys - showEntrys)
							continue;
						counter++;
						html += "<tr>";
						html += "<td><font color=LEVEL>" + splited[12] + "</font><br1>"; // ник
						html += splited[0].substring(1) + " " + splited[1].substring(0, 5) + "</td>"; // дата, время
						html += "<td>" + splited[14] + "</td>"; // длительность
						html += "<td>";
						// причина
						for(int i = 17; i < splited.length; i++)
							html += splited[i] + " ";
						html += "</td>";
						html += "</tr>";
					}
					log.close();
					html += "</table>";
					reply.setHtml(html);
					activeChar.sendPacket(reply);
				}
				catch(Exception e)
				{
					html += "<center><font color=LEVEL>Error: Failed to open moderators log.</font></center>";
					reply.setHtml(html);
					activeChar.sendPacket(reply);
					e.printStackTrace();
					return false;
				}
				break;
			case admin_penalty:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //penalty charName [count] [reason]");
					return false;
				}

				int count = 1;
				if(wordList.length > 2)
					count = Integer.parseInt(wordList[2]);

				String reason = "не указана";

				if(wordList.length > 3)
					reason = wordList[3];

				int oId = 0;

				L2Player player = L2ObjectsStorage.getPlayer(wordList[1]);
				if(player != null && player.getPlayerAccess().CanBanChat)
				{
					oId = player.getObjectId();
					int oldPenaltyCount = 0;
					String oldPenalty = player.getVar("penaltyChatCount");
					if(oldPenalty != null)
						oldPenaltyCount = Integer.parseInt(oldPenalty);

					player.setVar("penaltyChatCount", "" + (oldPenaltyCount + count));
				}
				else
				{
					// TODO: Не плохо было бы сделать сперва проверку, модератор это или нет.
					oId = mysql.simple_get_int("obj_Id", "characters", "`char_name`='" + wordList[1] + "'");
					if(oId > 0)
					{
						Integer oldCount = (Integer) mysql.get("SELECT `value` FROM character_variables WHERE `obj_id` = " + oId + " AND `name` = 'penaltyChatCount'");
						mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (" + oId + ",'user-var','penaltyChatCount','" + (oldCount + count) + "',-1)");
					}
				}

				if(oId > 0)
					if(ConfigValue.MAT_ANNOUNCE_FOR_ALL_WORLD)
						Announcements.getInstance().announceToAll(activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + ".");
					else
						Announcements.shout(activeChar, activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + ".", Say2C.CRITICAL_ANNOUNCEMENT);

				break;
		}

		return true;
	}

	private static int getEntrysCountForModerator(int objId)
	{
		try
		{
			BufferedReader log = new BufferedReader(new FileReader(MODERATORS_LOG_FILE));
			String str;
			int counter = 0;
			while((str = log.readLine()) != null)
			{
				String splited[] = str.split(" ");
				if(splited.length < 1 || objId != Integer.parseInt(splited[5].substring(5, 14)))
					continue;
				counter++;
			}
			log.close();
			return counter;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	// Панель управления модераторами
	private static void showModersPannel(L2Player activeChar)
	{
		NpcHtmlMessage reply = new NpcHtmlMessage(5);
		String html = "Moderators managment panel.<br>";

		File dir = new File("./config/GMAccess.d/");
		if(!dir.exists() || !dir.isDirectory())
		{
			html += "Error: Can't open permissions folder.";
			reply.setHtml(html);
			activeChar.sendPacket(reply);
			return;
		}

		html += "<p align=right>";
		html += "<button width=120 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_add\" value=\"Add modrator\">";
		html += "</p><br>";

		html += "<center><font color=LEVEL>Moderators:</font></center>";
		html += "<table width=285>";
		for(File f : dir.listFiles())
		{
			if(f.isDirectory() || !f.getName().startsWith("m") || !f.getName().endsWith(".xml"))
				continue;

			// Для файлов модераторов префикс m
			int oid = Integer.parseInt(f.getName().substring(1, 10));
			String pName = Util.getPlayerNameByObjId(oid);
			boolean on = false;

			if(pName == null || pName.isEmpty())
				pName = "" + oid;
			else
				on = L2ObjectsStorage.getPlayer(pName) != null;

			html += "<tr>";
			html += "<td width=140>" + pName;
			html += on ? " <font color=\"33CC66\">(on)</font>" : "";
			html += "</td>";
			html += "<td width=45><button width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_log " + oid + "\" value=\"Logs\"></td>";
			html += "<td width=45><button width=20 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_del " + oid + "\" value=\"X\"></td>";
			html += "</tr>";
		}
		html += "</table>";

		reply.setHtml(html);
		activeChar.sendPacket(reply);
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