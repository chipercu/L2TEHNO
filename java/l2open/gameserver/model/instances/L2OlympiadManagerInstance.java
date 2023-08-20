package l2open.gameserver.model.instances;

import l2open.config.*;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.*;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExHeroList;
import l2open.gameserver.serverpackets.ExOlympiadMatchList;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;
import l2open.util.GArray;

import java.util.logging.Logger;

public class L2OlympiadManagerInstance extends L2NpcInstance
{
	private static Logger _log = Logger.getLogger(L2OlympiadManagerInstance.class.getName());

	public L2OlympiadManagerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		if(ConfigValue.EnableOlympiad && template.npcId == 31688)
			Olympiad.addOlympiadNpc(this);
	}

	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(!ConfigValue.EnableOlympiad)
			return;
		if(command.startsWith("OlympiadDesc"))
		{
			int val = Integer.parseInt(command.substring(13, 14));
			String suffix = command.substring(14);
			showChatWindow(player, val, suffix);
		}
		else if(command.startsWith("OlympiadNoble"))
		{
			//if(!ConfigValue.EnableOlympiad || !Olympiad.isNoble(player.getObjectId()) || !player.isNoble() || player.isSubClassActive()/* && !ConfigValue.Multi_Enable*/)
			if(!ConfigValue.EnableOlympiad || !Olympiad.isNoble(player.getObjectId()) || !player.isNoble() || (player.getBaseClassId() != player.getClassId().getId() && !ConfigValue.Multi_Enable))
				return;
			int val = Integer.parseInt(command.substring(14));
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			switch(val)
			{
				case 1:
					Olympiad.unRegisterNoble(player);
					break;
				case 3:
					int points = Olympiad.getNoblePoints(player.getObjectId());
					html.setFile("./data/html/olympiad/noble_points1.htm");
					html.replace("%points%", String.valueOf(points));
					player.sendPacket(html);
					break;
				case 4:
					Olympiad.registerNoble(player, CompType.NON_CLASSED);
					break;
				case 5:
					Olympiad.registerNoble(player, CompType.CLASSED);
					break;
				case 6:
					int passes = Olympiad.getNoblessePasses(player);
					if(passes > 0)
					{
						player.getInventory().addItem(ConfigValue.AltOlyCompRewItem, passes);
						player.sendPacket(SystemMessage.obtainItems(ConfigValue.AltOlyCompRewItem, passes, 0));
					}
					else
						player.sendPacket(html.setFile("./data/html/olympiad/noble_nopoints.htm"));
					break;
				case 7:
					L2Multisell.getInstance().SeparateAndSend(102, player, 0);
					break;
				case 8:
					int point = Olympiad.getNoblePointsPast(player.getObjectId());
					html.setFile("./data/html/olympiad/noble_points2.htm");
					html.replace("%points%", String.valueOf(point));
					player.sendPacket(html);
					break;
				case 9:
					L2Multisell.getInstance().SeparateAndSend(103, player, 0);
					break;
				case 10:
					Olympiad.registerNoble(player, CompType.TEAM_RANDOM);
					break;
				case 11:
					Olympiad.registerNoble(player, CompType.TEAM);
					break;
				case 2:
				default:
					_log.warning("Olympiad System: Couldnt send packet for request " + val);
			}
		}
		else if(command.startsWith("Olympiad"))
		{
			if(!ConfigValue.EnableOlympiad)
				return;
			int val = Integer.parseInt(command.substring(9, 10));
			NpcHtmlMessage reply = new NpcHtmlMessage(player, this);
			switch(val)
			{
				case 1:
					if(!Olympiad.isProgress(player))
						break;
					player.sendPacket(new ExOlympiadMatchList());
					break;
				case 2:
					int classId = Integer.parseInt(command.substring(11));
					if(classId < 88 || classId == 136)
						break;
					reply.setFile("./data/html/olympiad/olympiad_ranking.htm");
					GArray<String> names = OlympiadDatabase.getClassLeaderBoard(classId);
					int index = 1;
					for(String name : names)
					{
						reply.replace("%place" + index + "%", String.valueOf(index));
						reply.replace("%rank" + index + "%", name);
						index++;
						if(index > 10)
							break;
					}
					for(;index <= 10;index++)
					{
						reply.replace("%place" + index + "%", "");
						reply.replace("%rank" + index + "%", "");
					}
					player.sendPacket(reply);
					break;
				case 4:
					player.sendPacket(new ExHeroList());
					break;
				case 5:
					StringBuilder replyMSG = new StringBuilder("<html><body>");
					if(Hero.getInstance().isInactiveHero(player.getObjectId()))
					{
						Hero.getInstance().activateHero(player);
						replyMSG.append("Congratulations! You are a Hero now.");
					}
					else
					{
						replyMSG.append("You cannot be a Hero.");
					}
					replyMSG.append("</body></html>");
					reply.setHtml(replyMSG.toString());
					player.sendPacket(reply);
					break;
				case 3:
				default:
					_log.warning("Olympiad System: Couldnt send packet for request " + val);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void showChatWindow(L2Player player, int val, String suffix)
	{
		String filename = "/data/html/olympiad/";
		if(val == 2 && suffix.equals("a") && Olympiad.isRegistered(player))
			filename = "/data/html/olympiad/noble_cancel.htm";
		else
		{
			filename = filename + "noble_desc" + val;
			filename = filename + (suffix != null ? suffix + ".htm" : ".htm");
			if(filename.equals("./data/html/olympiad/noble_desc0.htm"))
				filename = "/data/html/olympiad/noble_main.htm";
		}
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		String file = Files.read(filename, player);
		if(file != null)
		{
			file = file.replaceAll("%raund%", Integer.toString(Olympiad._currentRound));
			file = file.replaceAll("%circle%", Integer.toString(Olympiad._currentCycle));
			file = file.replaceAll("%participants%", Integer.toString(Olympiad.getCountParticipants()));
		}
		else
			file = "System error!";
		player.sendPacket(html.setHtml(file));
	}
}