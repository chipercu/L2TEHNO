package services;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javolution.util.FastMap;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.*;
import l2open.gameserver.communitybbs.Manager.*;
import l2open.gameserver.handler.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.barahlo.academ.dao.*;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.*;
import l2open.gameserver.tables.player.PlayerData;
import l2open.util.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.sql.ResultSet;

public class Recruitment extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	public static final Logger _log = Logger.getLogger(Recruitment.class.getName());
	
	private static enum Commands
	{
		_bbsrecruitment
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(!ConfigValue.RecruitmentAllow)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player).toString());
			return;
		}
		bypass = bypass.replace(" ", ":"); // для избежания создания лишнего массива!

		String content = "";
		if(bypass.equals("_bbsrecruitment:academy:add"))
			content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/academy-add.htm", player);
		else if(bypass.equals("_bbsrecruitment:invitelist"))
			content = inviteList(player);
		else if(bypass.startsWith("_bbsrecruitment:invite:sub"))
		{
			String[] data = bypass.split(":");
			int clanId = player.getClanId();
			int obj = Integer.parseInt(data[3]);
			int unity = Integer.parseInt(data[4]);
			if(RecruitmentData.getInstance().checkClanInvite(player, clanId, obj, unity))
				doInvite(clanId, obj, unity);

			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + player.getClanId());
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:academy:invite"))
		{
			String[] data = bypass.split(":");
			askBefor(player, data[3]);

			DifferentMethods.communityNextPage(player, "_bbsrecruitment:list:academy:1");
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:invite:page"))
		{
			String[] data = bypass.split(":");
			content = invitePlayer(player, data[3]);
		}
		else if(bypass.startsWith("_bbsrecruitment:invite:remove"))
		{
			String[] data = bypass.split(":");
			RecruitmentData.getInstance().inviteRemove(player, data[3]);
			if(player.getClan().getInviteList().size() > 0)
				DifferentMethods.communityNextPage(player, "_bbsrecruitment:invitelist");
			else
				DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + player.getClanId());
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:list"))
		{
			String[] data = bypass.split(":");
			String page = data.length < 4 ? "1" : data[3];
			if(data[2].equals("clan"))
				content = buildClanList(page, player);
			else if(data[2].equals("academy"))
				content = buildAcademyList(page, player);
		}
		else if(bypass.startsWith("_bbsrecruitment:clan:id"))
			content = buildClanPage(bypass.split(":")[3], player);
		else if(bypass.startsWith("_bbsrecruitment:invite"))
		{
			String[] data = bypass.split(":");
			String clanId = data[2];
			String note = Util.ArrayToString(data, 3);
			RecruitmentData.getInstance().sendInviteTask(player, clanId, note, true);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + clanId);
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:warclan"))
		{
			String[] data = bypass.split(":");
			int war = Integer.parseInt(data[2]);
			RecruitmentData.getInstance().checkAndStartWar(player, war);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + war);
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:unwarclan"))
		{
			String[] data = bypass.split(":");
			int war = Integer.parseInt(data[2]);
			RecruitmentData.getInstance().checkAndStopWar(player, war);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + war);
			return;
		}
		else if(bypass.startsWith("_bbsrecruitment:removeinvite"))
		{
			String clanId = bypass.split(":")[2];
			RecruitmentData.getInstance().sendInviteTask(player, clanId, null, false);
			DifferentMethods.communityNextPage(player, "_bbsrecruitment:clan:id:" + clanId);
			return;
		}

		ShowBoard.separateAndSend(addCustomReplace(content), player);
	}

	private String buildAcademyList(String page_num, L2Player player)
	{
		if(page_num == null)
			return null;

		int page = Integer.parseInt(page_num);
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/academy-list.htm", player);
		int start = (page - 1) * 10;
		int end = Math.min(page * 10, AcademyStorage.getInstance().getAcademyList().size());

		String template = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/academy-list-template.htm", player);
		String body = "";
		for(int i = start; i < end; i++)
		{
			L2Clan clan = AcademyStorage.getInstance().getAcademyList().get(i);
			AcademyRequest request = AcademyStorage.getInstance().getReguest(clan.getClanId());
			String academytpl = template;
			academytpl = academytpl.replace("<?action?>", "bypass -h _bbsrecruitment:academy:invite:" + clan.getClanId());
			academytpl = academytpl.replace("<?color?>", i % 2 == 1 ? "333333" : "A7A19A");
			academytpl = academytpl.replace("<?position?>", String.valueOf(i + 1));
			academytpl = academytpl.replace("<?clan_name?>", clan.getName().length() > 12 ? clan.getName().substring(0, 11) + "..." : clan.getName());
			academytpl = academytpl.replace("<?clan_owner?>", clan.getLeaderName());
			academytpl = academytpl.replace("<?seats?>", String.valueOf(request.getSeats() + " мест"));
			academytpl = academytpl.replace("<?price?>", Util.formatPay(player, request.getPrice(), request.getItem()));
			academytpl = academytpl.replace("<?time?>", String.valueOf(request.getTime() + " часов"));
			body += academytpl;
		}

		content = content.replace("<?add?>", AddAcademy(player));
		content = content.replace("<?navigate?>", parseNavigate(page, false));
		content = content.replace("<?body?>", body);

		return content;
	}

	public String AddAcademy(L2Player player)
	{
		L2Clan clan = player.getClan();
		if(clan == null || clan.getLeader().getPlayer() != player)
			return "<font color=\"FF0000\">Добавить набор в Академию могут только Клан Лидеры!</font>";
		else if(clan.getSubPledge(L2Clan.SUBUNIT_ACADEMY) == null)
			return "<font color=\"FF0000\">Вы не можете добавить набор, так как у Вашего клана нет Академии!</font>";
		else if(AcademyStorage.getInstance().getReguest(clan.getClanId()) != null)
			return GenerateElement.button("Удалить набор в Академию", "_bbsscripts; ;services.RecruitmentPanel:removeAcademy", 180, 28, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF");
		else
			return GenerateElement.button("Добавить набор в Академию", "_bbsscripts; ;services.RecruitmentPanel:addAcademy", 180, 28, "L2UI_CT1.Button_DF_Down", "L2UI_CT1.Button_DF");
	}

	private String invitePlayer(L2Player player, String string)
	{
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-player-info.htm", player);

		L2Clan clan = player.getClan();
		if(clan == null)
			return null;

		int obj = Integer.parseInt(string);

		ClanRequest request = ClanRequest.getClanInvitePlayer(clan.getClanId(), obj);
		if(request == null)
			return null;

		String No = new CustomMessage("common.result.no", player).toString();
		String Yes = new CustomMessage("common.result.yes", player).toString();

		L2Player invited = request.getPlayer();

		if(!invited.isOnline())
		{
			L2Player restore = RecruitmentData.getInstance().restore(obj);
			if(restore != null)
				invited = restore;
		}

		html = html.replace("<?name?>", invited.getName());
		html = html.replace("<?online?>", String.valueOf(invited.isOnline() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
		html = html.replace("<?noblesse?>", String.valueOf(invited.isNoble() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
		html = html.replace("<?hero?>", String.valueOf(invited.isHero() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
		html = html.replace("<?level?>", String.valueOf(invited.getLevel()));
		html = html.replace("<?time?>", toSimpleFormat(request.getTime()));
		html = html.replace("<?class?>", DifferentMethods.htmlClassNameNonClient(player, player.getBaseClassId()).toString());
		html = html.replace("<?remove?>", "bypass -h _bbsrecruitment:invite:remove:" + invited.getObjectId());
		html = html.replace("<?note?>", request.getNote());
		if(!invited.isOnline())
			html = html.replace("<?button?>", Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-player-info-button-off.htm", player));
		else
		{
			String button = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-player-info-button.htm", player);
			String block = "";
			String list = "";

			int i = 1;
			final int[] unity = new int[] { 0, 100, 200, 1001, 1002, 2001, 2002 };
			for(int id : unity)
			{
				SubPledge sub = clan.getSubPledge(id);
				if(sub != null && clan.getSubPledgeMembersCount(id) < clan.getSubPledgeLimit(id))
				{
					block = button;
					block = block.replace("<?color?>", i % 2 == 1 ? "99CC33" : "669933");
					block = block.replace("<?action?>", "bypass -h _bbsrecruitment:invite:sub:" + invited.getObjectId() + ":" + id);
					block = block.replace("<?unity?>", takeFullSubName(id, sub.getName()));
					list += block;
					i++;
				}
			}

			if(list.isEmpty())
				list = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-player-info-button-limit.htm", player);

			html = html.replace("<?button?>", list);
		}

		return html;
	}

	private String takeFullSubName(int id, String name)
	{
		String type = null;
		switch(id)
		{
			case 0:
				type = "Main Clan";
				break;
			case 100:
				type = "1st Royal Guard";
				break;
			case 200:
				type = "2nd Royal Guard";
				break;
			case 1001:
				type = "1st Order of Knights";
				break;
			case 1002:
				type = "2nd Order of Knights";
				break;
			case 2001:
				type = "3rd Order of Knights";
				break;
			case 2002:
				type = "4th Order of Knights";
				break;
		}

		type += ": " + name;

		return type;
	}

	private String inviteList(L2Player player)
	{
		L2Clan clan = player.getClan();

		if(clan == null)
			return null;

		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-list.htm", player);
		String template = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/invite-list-template.htm", player);
		String block = "";
		String list = "";
		String No = new CustomMessage("common.result.no", player).toString();
		String Yes = new CustomMessage("common.result.yes", player).toString();
		int i = 1;
		for(ClanRequest request : clan.getInviteList())
		{
			L2Player invited = request.getPlayer();

			if(!invited.isOnline())
			{
				L2Player restore = RecruitmentData.getInstance().restore(invited.getObjectId());
				if(restore != null)
					invited = restore;
			}

			long time = request.getTime();

			block = template;
			block = block.replace("<?color?>", i % 2 == 1 ? "333333" : "A7A19A");
			block = block.replace("<?name?>", invited.getName());
			block = block.replace("<?online?>", String.valueOf(invited.isOnline() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
			block = block.replace("<?noblesse?>", String.valueOf(invited.isNoble() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
			block = block.replace("<?level?>", String.valueOf(invited.getLevel()));
			block = block.replace("<?time?>", toSimpleFormat(time));
			block = block.replace("<?class?>", DifferentMethods.htmlClassNameNonClient(invited, invited.getBaseClassId()).toString());
			block = block.replace("<?action?>", "bypass -h _bbsrecruitment:invite:page:" + invited.getObjectId());
			block = block.replace("<?remove?>", "bypass -h _bbsrecruitment:invite:remove:" + invited.getObjectId());
			list += block;
			i++;
		}
		html = html.replace("<?list?>", list);
		html = html.replace("<?action?>", "bypass -h _bbsrecruitment:clan:id:" + player.getClanId());
		return html;
	}

	private String buildClanList(String page_num, L2Player player)
	{
		if(page_num == null)
			return null;

		int page = Integer.parseInt(page_num);
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-list.htm", player);
		int start = (page - 1) * 10;
		int end = Math.min(page * 10, ClanRequest.getClanList().size());

		String template = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-list-template.htm", player);
		String body = "";
		String No = new CustomMessage("common.result.no", player).toString();
		for(int i = start; i < end; i++)
		{
			L2Clan clan = ClanRequest.getClanList().get(i);
			String clantpl = template;
			clantpl = clantpl.replace("<?action?>", "bypass -h _bbsrecruitment:clan:id:" + clan.getClanId());
			clantpl = clantpl.replace("<?color?>", i % 2 == 1 ? "333333" : "A7A19A");
			clantpl = clantpl.replace("<?position?>", String.valueOf(i + 1));
			clantpl = clantpl.replace("<?ally_crest?>", clan.getAlliance() != null && clan.getAlliance().getAllyCrestId() > 0 ? "Crest.crest_" + ConfigValue.RequestServerID + "_" + clan.getAlliance().getAllyCrestId() : "L2UI_CH3.ssq_bar2back");
			clantpl = clantpl.replace("<?clan_name?>", clan.getName().length() > 12 ? clan.getName().substring(0, 11) + "..." : clan.getName());
			clantpl = clantpl.replace("<?clan_crest?>", clan.hasCrest() ? "Crest.crest_" + ConfigValue.RequestServerID + "_" + clan.getCrestId() : "L2UI_CH3.ssq_bar1back");
			clantpl = clantpl.replace("<?clan_owner?>", clan.getLeaderName());
			clantpl = clantpl.replace("<?level_color?>", clan.getLevel() == 11 ? "FFCC33" : (clan.getLevel() <= 10 && clan.getLevel() >= 8 ? "66CCCC" : "339933"));
			clantpl = clantpl.replace("<?clan_level?>", String.valueOf(clan.getLevel()));
			clantpl = clantpl.replace("<?clan_base?>", String.valueOf(clan.getHasCastle() != 0 ? RecruitmentData.getInstance().name(clan.getHasCastle()) : clan.getHasFortress() != 0 ? RecruitmentData.getInstance().name(clan.getHasFortress()) : No));
			clantpl = clantpl.replace("<?clan_hall?>", clan.getHasHideout() != 0 ? RecruitmentData.getInstance().name(clan.getHasHideout()) : No);
			clantpl = clantpl.replace("<?member_count?>", String.valueOf(clan.getMembersCount()));
			body += clantpl;
		}

		L2Clan my_clan = player.getClan();
		if(my_clan != null && my_clan.getLevel() > 0)
		{
			String clantpl = template;
			clantpl = clantpl.replace("<?action?>", "bypass -h _bbsrecruitment:clan:id:" + my_clan.getClanId());
			clantpl = clantpl.replace("<?color?>", "669933");
			clantpl = clantpl.replace("<?position?>", "MY");
			clantpl = clantpl.replace("<?ally_crest?>", my_clan.getAlliance() != null && my_clan.getAlliance().getAllyCrestId() > 0 ? "Crest.crest_" + ConfigValue.RequestServerID + "_" + my_clan.getAlliance().getAllyCrestId() : "L2UI_CH3.ssq_bar2back");
			clantpl = clantpl.replace("<?clan_name?>", my_clan.getName().length() > 12 ? my_clan.getName().substring(0, 11) + "..." : my_clan.getName());
			clantpl = clantpl.replace("<?clan_crest?>", my_clan.hasCrest() ? "Crest.crest_" + ConfigValue.RequestServerID + "_" + my_clan.getCrestId() : "L2UI_CH3.ssq_bar1back");
			clantpl = clantpl.replace("<?clan_owner?>", my_clan.getLeaderName());
			clantpl = clantpl.replace("<?level_color?>", my_clan.getLevel() == 11 ? "FFCC33" : (my_clan.getLevel() <= 10 && my_clan.getLevel() >= 8 ? "66CCCC" : "339933"));
			clantpl = clantpl.replace("<?clan_level?>", String.valueOf(my_clan.getLevel()));
			clantpl = clantpl.replace("<?clan_base?>", String.valueOf(my_clan.getHasCastle() != 0 ? RecruitmentData.getInstance().name(my_clan.getHasCastle()) : my_clan.getHasFortress() != 0 ? RecruitmentData.getInstance().name(my_clan.getHasFortress()) : No));
			clantpl = clantpl.replace("<?clan_hall?>", my_clan.getHasHideout() != 0 ? RecruitmentData.getInstance().name(my_clan.getHasHideout()) : No);
			clantpl = clantpl.replace("<?member_count?>", String.valueOf(my_clan.getMembersCount()));
			body += clantpl;
		}

		content = content.replace("<?navigate?>", parseNavigate(page, true));
		content = content.replace("<?body?>", body);

		return content;
	}

	private String parseNavigate(int page, boolean clan)
	{
		StringBuilder pg = new StringBuilder();

		double size = clan ? ClanRequest.getClanList().size() : AcademyStorage.getInstance().get().size();
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
					pg.append("<td width=25 align=center><button value=\"").append(current).append("\" action=\"bypass -h _bbsrecruitment:list:" + (clan ? "clan" : "academy") + ":").append(current).append("\" width=28 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");

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

	private String buildClanPage(String string, L2Player player)
	{
		if(string == null)
			return null;

		int clanId = Integer.parseInt(string);

		L2Clan clan = ClanTable.getInstance().getClan(clanId);

		if(clan == null)
			return null;

		String No = new CustomMessage("common.result.no", player).toString();
		String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page.htm", player);
		content = content.replace("<?clan_name?>", clan.getName());
		content = content.replace("<?clan_ally?>", clan.getAlliance() != null ? clan.getAlliance().getAllyName() : No);
		content = content.replace("<?clan_owner?>", clan.getLeaderName());
		content = content.replace("<?clan_id?>", String.valueOf(clan.getClanId()));
		content = content.replace("<?clan_level?>", String.valueOf(clan.getLevel()));
		content = content.replace("<?clan_base?>", clan.getHasCastle() != 0 ? RecruitmentData.getInstance().name(clan.getHasCastle()) : clan.getHasFortress() != 0 ? RecruitmentData.getInstance().name(clan.getHasFortress()) : No);
		//if(ConfigValue.IS_MY)
		//	content = content.replace("<?clan_base_img?>", clan.getHasCastle() != 0 ? "IMG_ClanCastle" : clan.getHasFortress() != 0 ? "IMG_ClanFortess" : "IMG_ClanNone");
		content = content.replace("<?clan_hall?>", clan.getHasHideout() != 0 ? RecruitmentData.getInstance().name(clan.getHasHideout()) : No);
		content = content.replace("<?clan_members?>", String.valueOf(clan.getMembersCount()));
		content = content.replace("<?clan_point?>", Util.formatAdena(clan.getReputationScore()));
		content = content.replace("<?clan_avarage_level?>", Util.formatAdena(clan.getAverageLevel()));
		content = content.replace("<?clan_online?>", Util.formatAdena(clan.getOnlineMembers(0).length));

		int pvp = 0;
		int pvp_total = 0;
		int pk = 0;
		String top_pk = "---";
		String top_pvp = "---";
		for(L2ClanMember pl : clan._members.values())
		{
			final int _pvp = pl.getPvp();
			final int _pk = pl.getPk();

			if(_pvp > pvp)
				top_pvp = pl.getName();
			if(_pk > pk)
				top_pk = pl.getName();

			pvp = _pvp;
			pk = _pk;
			pvp_total += _pvp;
		}

		content = content.replace("<?clan_pvp?>", Util.formatAdena(pvp_total));
		//content = content.replace("<?clan_today_pvp?>", Util.formatAdena(clan.getPvP()));

		content = content.replace("<?clan_top_pvp?>", top_pvp);
		content = content.replace("<?clan_top_pk?>", top_pk);

		L2Clan myclan = player.getClan();
		if(myclan == null)
		{
			String page = null;
			if(clan.checkInviteList(player.getObjectId()))
			{
				page = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-remove_invite.htm", player);
				page = page.replace("<?bypass?>", "bypass -h _bbsrecruitment:removeinvite:" + clan.getClanId());
			}
			else
			{
				page = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-invite.htm", player);
				page = page.replace("<?bypass?>", "bypass -h _bbsscripts; ;services.RecruitmentPanel:addRequest " + clan.getClanId() + ";_bbsrecruitment:clan:id:" + clan.getClanId());
			}

			content = content.replace("<?container?>", page);
		}
		else if(myclan == clan && clan.getLeader().getPlayer() == player && clan.getInviteList().size() > 0)
		{
			String page = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-inviteinfo.htm", player);
			page = page.replace("<?bypass?>", "bypass -h _bbsrecruitment:invitelist");
			page = page.replace("<?invite_count?>", String.valueOf(clan.getInviteList().size()));
			content = content.replace("<?container?>", page);
		}
		else
			content = content.replace("<?container?>", "");

		if(RecruitmentData.getInstance().checkClanWar(clan, myclan, player, false))
		{
			if(myclan.isAtWarWith(clan.getClanId()))
			{
				String page = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-unwar.htm", player);
				page = page.replace("<?bypass?>", "bypass -h _bbsrecruitment:unwarclan:" + clan.getClanId());
				content = content.replace("<?war?>", page);
			}
			else
			{
				String page = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-war.htm", player);
				page = page.replace("<?bypass?>", "bypass -h _bbsrecruitment:warclan:" + clan.getClanId());
				content = content.replace("<?war?>", page);
			}
		}
		else
			content = content.replace("<?war?>", "");

		if(RecruitmentData.getInstance().haveWars(clan))
		{
			String war_body = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-war-list.htm", player);
			String war_temp = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-war-list-template.htm", player);
			String block = "";
			String list = "";
			int num = 0;
			for(L2Clan war : clan.getEnemyClans())
			{
				num++;
				if(num <= 6 && war.isAtWarWith(clan.getClanId()) && clan.isAtWarWith(war.getClanId()))
				{
					block = war_temp;
					block = block.replace("<?color?>", num % 2 == 1 ? "333333" : "A7A19A");
					block = block.replace("<?clan_name?>", war.getName());
					block = block.replace("<?clan_leader?>", war.getLeaderName());
					block = block.replace("<?clan_level?>", String.valueOf(war.getLevel()));
					block = block.replace("<?clan_member?>", String.valueOf(war.getMembersCount()));
					block = block.replace("<?war_link?>", "bypass -h _bbsrecruitment:clan:id:" + war.getClanId());
					list += block;
				}
				else
					num--;
			}

			war_body = war_body.replace("<?war_list?>", list);
			war_body = war_body.replace("<?war_count?>", String.valueOf(num));
			content = content.replace("<?clan_warlist?>", war_body);
		}
		else
			content = content.replace("<?clan_warlist?>", Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-war-list-empty.htm", player));

		if(clan._skills.size() > 0)
		{
			String skill_body = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-skills.htm", player);
			String skill_temp = Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-skills-template.htm", player);
			String block = "";
			String list = "";
			int count = 1;
			for(L2Skill skill : clan.getAllSkills())
			{
				block = skill_temp;
				block = block.replace("<?skill_level?>", String.valueOf(skill.getLevel()));
				block = block.replace("<?skill_icon?>", skill.getIcon());
				list += block;

				if(count % 9 == 0)
					list += "</tr><tr>";

				count++;
			}

			final int[] unity = new int[] { 0, 100, 200, 1001, 1002, 2001, 2002 };
			List<L2Skill> unity_list = new ArrayList<L2Skill>();
			for(int id : unity)
			{
				FastMap<Integer, L2Skill> sub_skill = clan.getSquadSkills().get(id);
				if(sub_skill != null)
				{
					for(L2Skill skill : sub_skill.values())
					{
						unity_list.add(skill);
					}
				}
			}

			for(L2Skill skill : unity_list)
			{
				block = skill_temp;
				block = block.replace("<?skill_level?>", String.valueOf(skill.getLevel()));
				block = block.replace("<?skill_icon?>", skill.getIcon());
				list += block;

				if(count % 9 == 0)
					list += "</tr><tr>";

				count++;
			}

			skill_body = skill_body.replace("<?skill_list?>", list);
			content = content.replace("<?clan_skill?>", skill_body);
		}
		else
			content = content.replace("<?clan_skill?>", Files.read(ConfigValue.CommunityBoardHtmlRoot + "recruitment/clan-page-skills-empty.htm", player));

		String description = clan.getDescription();
		final boolean isnull = description == null || description.isEmpty();
		content = content.replace("<?clan_notice?>", isnull ? "<center><font color=\"FF0000\">Empty</font></center>" : description);
		content = content.replace("<?edit_notice?>", clan.getLeader().getPlayer() == player ? ("<button " + (isnull ? "action=\"bypass -h _bbsscripts; ;services.RecruitmentPanel:addDescription;_bbsrecruitment:clan:id:" + clan.getClanId() + "\" value=\"Add\"" : "action=\"bypass -h _bbsscripts; ;services.RecruitmentPanel:editDescription;_bbsrecruitment:clan:id:" + clan.getClanId() + "\" value=\"Edit\"") + " width=120 height=24 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/>") : "");

		return content;
	}

	public static void doInvite(int clanId, int obj, int unity)
	{
		L2Player player = L2ObjectsStorage.getPlayer(obj);

		if(player == null || !player.isOnline())
			return;

		L2Clan clan = ClanTable.getInstance().getClan(clanId);

		player.sendPacket(new JoinPledge(clan.getClanId()));
		SubPledge subUnit = clan.getSubPledge(unity);
		if(subUnit == null)
			return;

		player.setPledgeType(unity);
		clan.addClanMember(player);
		player.setClan(clan);
		player.setVar("join_clan", String.valueOf(System.currentTimeMillis()));
		clan.getClanMember(player.getName()).setPlayerInstance(player, false);

		if(clan.isAcademy(player.getPledgeType()))
			player.setLvlJoinedAcademy(player.getLevel());
		clan.getClanMember(player.getName()).setPowerGrade(clan.getAffiliationRank(player.getPledgeType()));

		clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(clan.getClanMember(player.getName())), player);
		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdate(clan));

		player.sendPacket(Msg.ENTERED_THE_CLAN, new PledgeShowMemberListAll(clan, player));
		player.setLeaveClanTime(0);
		player.updatePledgeClass();
		clan.addAndShowSkillsToPlayer(player);
		player.broadcastUserInfo(true);
		player.broadcastRelationChanged();
		
		player.sendPacket(new PledgeSkillList(clan));
		player.sendPacket(new SkillList(player));

		PlayerData.getInstance().store(player, false);

		if(unity != L2Clan.SUBUNIT_ACADEMY)
			ClanRequest.removeClanInvitePlayer(clan.getClanId(), player.getObjectId());
	}

	private void askBefor(L2Player player, String clanId)
	{
		int id = Integer.parseInt(clanId);
		AcademyRequest request;

		if((request = AcademyStorage.getInstance().getReguest(id)) == null)
			return;

		L2Clan clan;

		if((clan = ClanTable.getInstance().getClan(id)) == null)
			return;

		String msg = String.valueOf(new CustomMessage("academy.ask", player).addString(clan.getName()).addString(Util.formatPay(player, request.getPrice(), 57)).addNumber(request.getTime()).toString());
		player.scriptRequest(msg, "services.RecruitmentPanel:sayYes", new Object[]{id});
	}

	public static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	public static String toSimpleFormat(Calendar cal)
	{
		return SIMPLE_FORMAT.format(cal.getTime());
	}

	public static String toSimpleFormat(long cal)
	{
		return SIMPLE_FORMAT.format(cal);
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{}

	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		if(ConfigValue.RecruitmentAllow)
		{
			ClanRequest.updateList();
			ThreadPoolManager.getInstance().schedule(new AcademicCheck(), 30000L);
			AcademyRequestDAO.getInstance().load(); // Загружаем Академии!
			AcademiciansDAO.getInstance().load(); // Загружаем Академиков!
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}
}
