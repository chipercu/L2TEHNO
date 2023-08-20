package services;

import l2open.config.ConfigValue;

import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.barahlo.academ.dao.*;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.base.ClassType2;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

import java.util.Map;
import java.util.HashMap;

public class RecruitmentPanel extends Functions implements ScriptFile
{
	public static Map<String, Integer> _items;
	//@Bypass("services.RecruitmentPanel:editDescription")
	// bypass -h scripts_services.RecruitmentPanel:editDescription
	public void editDescription()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		L2Clan clan = player.getClan();

		if(clan == null || clan.getLeader().getPlayer() != player)
			return;

		player.sendPacket(new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/edit.htm"));
	}

	//@Bypass("services.RecruitmentPanel:doEdit")
	public void doEdit(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		L2Clan clan = player.getClan();
		String description = Util.ArrayToString(arg, 0);

		if(checkDescription(player, clan, description))
		{
			AcademyRequestDAO.getInstance().updateDescription(clan.getClanId(), description);
			clan.setDescription(description);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + clan.getClanId());
		}
	}

	//@Bypass("services.RecruitmentPanel:addDescription")
	public void addDescription()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		L2Clan clan = player.getClan();

		if(clan == null || clan.getLeader().getPlayer() != player)
			return;

		player.sendPacket(new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/add.htm"));
	}

	//@Bypass("services.RecruitmentPanel:doAdd")
	public void doAdd(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		L2Clan clan = player.getClan();
		String description = Util.ArrayToString(arg, 0);

		if(checkDescription(player, clan, description))
		{
			AcademyRequestDAO.getInstance().insertDescription(clan.getClanId(), description);
			clan.setDescription(description);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + clan.getClanId());
		}
	}

	private boolean checkDescription(L2Player player, L2Clan clan, String description)
	{
		if(player == null || clan == null || clan.getLeader().getPlayer() != player)
			return false;

		int min = ConfigValue.RecruitmentDescription;
		if(description.length() < min)
		{
			player.sendMessage("Descriptions size must be minimum " + min + " symbols!");
			return false;
		}

		return true;
	}

	//@Bypass("services.RecruitmentPanel:addRequest")
	public void addRequest(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.getClan() != null)
			return;
		if(arg[0].isEmpty() || !Util.isNumber(arg[0]))
			return;

		int clan = Integer.parseInt(arg[0]);

		if(clan == -1)
			return;

		NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/request.htm");
		html.replace("%clan%", String.valueOf(clan));

		player.sendPacket(html);
	}

	public void addAcademy()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!checkAcademy(player))
			return;

		NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/academy.htm");
		html.replace("%time%", parseTime());
		html.replace("%items%", parseItems());
		html.replace("%size%", parseSize(player));
		player.sendPacket(html);
	}

	//@Bypass("services.RecruitmentPanel:addAcademy")
	public void addAcademy(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(arg.length < 4)
		{
			if(!checkAcademy(player))
				return;

			NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/academy.htm");
			html.replace("%time%", parseTime());
			html.replace("%items%", parseItems());
			html.replace("%size%", parseSize(player));
			player.sendPacket(html);
		}
		else
		{
			if(!checkAcademy(player))
				return;

			int seats = Integer.parseInt(arg[0]);
			int time = Integer.parseInt(arg[1]);
			long price = Long.parseLong(arg[2].replace(",", ""));
			String item_name = Util.ArrayToString(arg, 3);
			int item = _items.get(item_name);
			if(Util.getClanPay(player, item, (price * seats), true))
			{
				int id = player.getClan().getClanId();
				AcademyRequest request = new AcademyRequest(time, id, seats, price, item); // Создаем набор
				AcademyRequestDAO.getInstance().insert(request);
			}

			DifferentMethods.communityNextPage(player, "_bbsrecruitment:list:academy:1");
		}
	}

	//@Bypass("services.RecruitmentPanel:online")
	public void online(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(arg[0].isEmpty())
			return;

		int clan_id = Integer.parseInt(arg[0]);

		int page = 1;

		if(arg.length > 1)
			page = Integer.parseInt(arg[1]);

		L2Clan clan = ClanTable.getInstance().getClan(clan_id);

		if(clan != null)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("data/scripts/services/RecruitmentPanel/clan_online.htm");
			String template = Files.read("data/scripts/services/RecruitmentPanel/clan_online_template.htm", player);
			String list = "";
			String data = "";

			int current = 1;
			int start = (page - 1) * 10;
			int end = Math.min(page * 10, clan.getOnlineMembers(0).length);
			for(int i = start; i < end; i++)
			{
				L2Player member = clan.getOnlineMembers(0)[i];
				if(member != null)
				{
					list = template;
					list = list.replace("<?name?>", member.getName());
					list = list.replace("<?level?>", String.valueOf(member.getLevel()));
					list = list.replace("<?color?>", (current % 2 == 0 ? "666666" : "999999"));
					ClassId classId = member.getClassId();
					list = list.replace("<?icon?>", getClanClassIcon(classId, classId.getType2()));
					String unity = member.getPledgeType() == 0 ? member.getClan().getName() : member.getClan().getSubPledge(member.getPledgeType()).getName();
					list = list.replace("<?unity?>", (unity.length() > 10 ? (unity.substring(0, 8) + "...") : unity));
					data += list;
					current++;
				}
			}

			html.replace("%navigate%", parseNavigate(clan, page));
			html.replace("%data%", data);
			html.replace("%name%", clan.getName());
			html.replace("%count%", String.valueOf(clan.getOnlineMembers(0).length));
			player.sendPacket(html);
		}
	}

	private String parseNavigate(L2Clan clan, int page)
	{
		StringBuilder pg = new StringBuilder();

		double size = clan.getOnlineMembers(0).length;
		double inpage = 10;

		if(size > inpage)
		{
			double max = Math.ceil(size / inpage);

			pg.append("<center><table width=25 border=0><tr>");
			int line = 1;

			for(int current = 1; current <= max; current++)
			{
				if(page == current)
					pg.append("<td width=25 align=center><button value=\"[").append(current).append("]\" width=38 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
				else
					pg.append("<td width=25 align=center><button value=\"").append(current).append("\" action=\"bypass -h htmbypass_services.ClanPanel:online " + clan.getClanId() + " ").append(current).append("\" width=28 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");

				if(line == 22)
				{
					pg.append("</tr><tr>");
					line = 0;
				}
				line++;
			}

			pg.append("</tr></table></center>");
		}

		return pg.toString();
	}

	private String getClanClassIcon(ClassId classId, ClassType2 type)
	{
		String icon = "L2UI_CH3.party_styleicon1_" + (classId.isMage() ? "2" : "1");
		if(type != null)
			switch(type)
			{
				case Enchanter:
					icon = "L2UI_CH3.party_styleicon5";
					break;
				case Healer:
					icon = "L2UI_CH3.party_styleicon6";
					break;
				case Knight:
					icon = "L2UI_CH3.party_styleicon3";
					break;
				case Rogue:
					icon = "L2UI_CH3.party_styleicon2";
					break;
				case Summoner:
					icon = "L2UI_CH3.party_styleicon7";
					break;
				case Warrior:
					icon = "L2UI_CH3.party_styleicon1";
					break;
				case Wizard:
					icon = "L2UI_CH3.party_styleicon5";
					break;
				default:
					icon = "L2UI_CH3.party_styleicon1_" + (classId.isMage() ? "2" : "1");
					break;
			}

		if(classId.getLevel() == 4)
			icon += "_3";

		return icon;
	}

	private boolean checkAcademy(L2Player player)
	{
		L2Clan clan;
		if((clan = player.getClan()) == null)
			return false;
		else if(clan.getSubPledge(L2Clan.SUBUNIT_ACADEMY) == null)
			return false;
		else if(clan.getSubPledgeMembersCount(L2Clan.SUBUNIT_ACADEMY) >= 20)
			return false;
		else if(AcademyStorage.getInstance().getReguest(clan.getClanId()) != null)
			return false;

		return true;
	}

	//@Bypass("services.RecruitmentPanel:sendRequest")
	public void sendRequest(String[] arg)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.getClan() != null)
			return;

		int clan = Integer.parseInt(arg[0]);
		String note = arg.length > 1 ? Util.ArrayToString(arg, 1) : "...";
		DifferentMethods.communityNextPage(player, "_bbsrecruitment:invite:" + clan + " " + note);
	}

	//@Bypass("services.RecruitmentPanel:removeAcademy")
	public void removeAcademy()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		L2Clan clan;

		if((clan = player.getClan()) == null)
			return;

		AcademyRequest request = AcademyStorage.getInstance().getReguest(clan.getClanId());

		if(request == null)
		{
			player.sendMessage("Request not found!");
			return;
		}

		if(!AcademiciansStorage.getInstance().clanCheck(clan.getClanId()))
		{
			clan.getWarehouse().addItem(request.getItem(), (request.getPrice() * request.getSeats()), "RecruitmentPanel.removeAcademy");
			AcademyRequestDAO.getInstance().delete(request.getClanId());
			AcademyStorage.getInstance().get().remove(request);
			AcademyStorage.getInstance().updateList();
			player.sendMessage("Your acceptance to the Academy was successfully deleted! Payment is sent to the warehouse!");
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:list:academy:1");
		}
		else
			player.sendMessage("You can not remove the acceptance to the Academy until all Academics do not finish passage!");
	}

	/**
	 * @return "20;19;18;17;16";
	 */
	private String parseSize(L2Player player)
	{
		String list = "";
		int count = 1;
		L2Clan clan = player.getClan();
		int max = clan.getSubPledgeLimit(L2Clan.SUBUNIT_ACADEMY);
		int size = clan.getSubPledgeMembersCount(L2Clan.SUBUNIT_ACADEMY);
		if(size < max)
		{
			for(int i = max; i > size; i--)
			{
				if(count > 1 && count <= max)
					list += ";";
				list += ((max + 1) - i);
				count++;
			}
		}
		else
			list = "0";

		return list;
	}

	/**
	 * @return "Adena;Coin of Luck";
	 */
	private String parseItems()
	{
		String items = "";
		int count = 1;
		int size = _items.size();
		for(String item : _items.keySet())
		{
			if(count > 1 && count <= size)
				items += ";";
			items += item;
			count++;
		}
		return items;
	}

	/**
	 * @return "1;2;3;4;5;6;7";
	 */
	private String parseTime()
	{
		String time = "";
		int count = 1;
		int size = ConfigValue.RecruitmentTime.length;
		for(int hour : ConfigValue.RecruitmentTime)
		{
			if(count > 1 && count <= size)
				time += ";";
			time += hour;
			count++;
		}
		return time;
	}

	private boolean inviteAcademy(AcademyRequest request, L2Player player)
	{
		if(request == null)
			return false;

		L2Clan clan = ClanTable.getInstance().getClan(request.getClanId());

		if(clan == null)
			return false;
		else if(clan.getSubPledge(L2Clan.SUBUNIT_ACADEMY) == null)
			return false;
		else if(clan.getSubPledgeMembersCount(L2Clan.SUBUNIT_ACADEMY) >= clan.getSubPledgeLimit(L2Clan.SUBUNIT_ACADEMY))
			return false;
		else if(request.getSeats() <= 0)
			return false;

		return true;
	}

	public void sayYes(Integer clanId)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(RecruitmentData.getInstance().checkAcademyInvite(player))
		{
			AcademyRequest request = AcademyStorage.getInstance().getReguest(clanId);
			if(inviteAcademy(request, player))
			{
				Academicians academic = new Academicians(System.currentTimeMillis() + (request.getTime() * 60 * 60 * 1000), player.getObjectId(), request.getClanId());
				AcademiciansDAO.getInstance().insert(academic);

				request.reduceSeats();
				Recruitment.doInvite(request.getClanId(), player.getObjectId(), L2Clan.SUBUNIT_ACADEMY);
				AcademyStorage.getInstance().updateList();
				DifferentMethods.communityNextPage(player, "_bbsrecruitment:list:academy:1");
			}
		}
	}

	public void onLoad()
	{
		if(ConfigValue.RecruitmentAllow)
		{
			_items = new HashMap<String, Integer>();
			for(int item_id : ConfigValue.RecruitmentItems)
			{
				String name = ItemTemplates.getInstance().getTemplate(item_id).getName();
				_items.put(name, item_id);
			}
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}
