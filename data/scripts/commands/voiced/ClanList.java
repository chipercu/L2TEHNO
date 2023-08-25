package commands.voiced;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.academ.*;
import l2open.gameserver.model.barahlo.academ.dao.AcademiciansDAO;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.ClanTable;
import l2open.util.*;
/**
 * @author: Diagod
 * open-team.ru
 **/
public class ClanList extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "clanlist", "clan_kick"};

	private static SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy:HH.mm");
	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("clanlist"))
		{
			int page = 0;
			if(!args.isEmpty())
				page = Integer.parseInt(args);
			showList(activeChar, page);
		}
		else if(command.startsWith("clan_kick"))
		{
			int page = 0;
			if(!args.isEmpty())
			{
				String[] arg = args.split(":");
				_log.info("args["+arg.length+"]='"+args+"'");
				if(arg.length == 2)
				{
					page = Integer.parseInt(arg[1]);
					kickClanMember(activeChar, Integer.parseInt(arg[0]));
				}
			}
			showList(activeChar, page);
		}
		return false;
	}

	public void kickClanMember(L2Player activeChar, int obj_id)
	{
		if(activeChar == null || !((activeChar.getClanPrivileges() & L2Clan.CP_CL_DISMISS) == L2Clan.CP_CL_DISMISS) || activeChar.is_block)
			return;

		L2Clan clan = activeChar.getClan();
		L2ClanMember member = clan.getClanMember(obj_id);
		if(member == null)
		{
			activeChar.sendPacket(Msg.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
			return;
		}
		else if(member.isOnline() && member.getPlayer().isInCombat())
		{
			activeChar.sendPacket(Msg.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
			return;
		}
		else if(member.isClanLeader())
		{
			activeChar.sendMessage("A clan leader may not be dismissed.");
			return;
		}
		else if(member.isOnline() && member.getPlayer().isTerritoryFlagEquipped())
		{
			activeChar.sendMessage("Нельзя изгнать чара с флагом в руках.");
			return;
		}

		boolean clanPenalty = member.getPledgeType() != L2Clan.SUBUNIT_ACADEMY;
		clan.removeClanMember(member.getObjectId());
		clan.broadcastToOnlineMembers(new SystemMessage(SystemMessage.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED).addString(member.getName()), new PledgeShowMemberListDelete(member.getName()));
		if(clanPenalty)
			clan.setExpelledMember();
		else if(ConfigValue.AcademicEnable)
		{
			l2open.gameserver.model.barahlo.academ2.Academicians academic = l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().getAcademicMap().get(member.getObjectId());
			if(academic != null)
				l2open.gameserver.model.barahlo.academ2.AcademiciansStorage.getInstance().delAcademic(academic, true);
		}
		else if(ConfigValue.RecruitmentAllow)
		{
			Academicians academic = AcademiciansStorage.getInstance().get(member.getObjectId());

			if(academic != null)
			{
				AcademyRequest academy = AcademyStorage.getInstance().getReguest(academic.getClanId());
				AcademiciansDAO.getInstance().delete(academic);
				AcademiciansStorage.getInstance().get().remove(academic);
				AcademyStorage.getInstance().updateList();
				academy.updateSeats();
			}
			else
				_log.info("RequestWithdrawalPledge: Academicians ERROR 1.");
		}

		L2Player player = member.getPlayer();
		if(member.isOnline() || player != null && player.isInOfflineMode())
		{
			if(player.getPledgeType() == L2Clan.SUBUNIT_ACADEMY)
				player.setLvlJoinedAcademy(0);
			player.setClan(null);
			if(!player.isNoble())
				player.setTitle("");
			player.setLeaveClanCurTime();
			Log.add("EXPELLED: clan="+clan.getName()+" member="+player.getName(), "clan_info");

			player.broadcastUserInfo(true);
			player.broadcastRelationChanged();

			player.sendPacket(Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS, Msg.PledgeShowMemberListDeleteAll);
		}
	}

	private void showList(L2Player activeChar, int page)
	{
		if(activeChar.getClan() == null || ((activeChar.getClanPrivileges() & L2Clan.CP_CL_DISMISS) != L2Clan.CP_CL_DISMISS && (activeChar.getClanPrivileges() & L2Clan.CP_CL_INVITE_CLAN) != L2Clan.CP_CL_INVITE_CLAN))
		{
			activeChar.sendMessage("У вас нет прав для использования команды .clanlist");
			return;
		}

		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "clan_list.htm", activeChar);

		StringBuffer online_list = new StringBuffer();
		StringBuffer offline_list = new StringBuffer();
		StringBuffer pages = new StringBuffer();

		
		GArray<L2ClanMember> online = new GArray<L2ClanMember>();
		GArray<L2ClanMember> offline = new GArray<L2ClanMember>();

		
		for(L2ClanMember temp : activeChar.getClan()._members.values())
			if(temp.isOnline())
				online.add(temp);
			else
				offline.add(temp);

		int size = online.size() > offline.size() ? online.size() : offline.size();

		int MaxCharactersPerPage = 20;
		int MaxPages = size / MaxCharactersPerPage;

		if(size > MaxCharactersPerPage * MaxPages)
			MaxPages++;

		if(page > MaxPages)
			page = MaxPages;

		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = online.size();
		if(CharactersEnd - CharactersStart > MaxCharactersPerPage)
			CharactersEnd = CharactersStart + MaxCharactersPerPage;

		for(int i = CharactersStart; i < CharactersEnd; i++)
		{
			L2ClanMember cm = online.get(i);
			online_list.append("<tr>");
			online_list.append("	<td width=130 align=center valign=top>");
			online_list.append("		<font color=00FF00>"+cm.getName()+"</font>");
			online_list.append("	</td>");
			online_list.append("	<td width=120 align=center valign=top>");
			online_list.append("		<font color=0CEEAA>"+String.valueOf(DifferentMethods.htmlClassNameNonClient(activeChar, cm.getClassId()))+"</font>");
			online_list.append("	</td>");
			online_list.append("	<td width=30 align=center valign=top>");
			online_list.append("		<font color=FFFF00>"+cm.getLevel()+"</font>");
			online_list.append("	</td>");
			online_list.append("	<td width=120 align=center valign=top>");
			online_list.append("		<font color=00FF00>"+(cm.getPlayer().getParty() == null ? "Не в группе" : cm.getPlayer().getParty().getPartyLeader().getName())+"</font>");
			online_list.append("	</td>");
			if(cm.getObjectId() != activeChar.getObjectId() && !cm.isClanLeader())
			{
				online_list.append("	<td width=15 align=center valign=top>");
				online_list.append("		<button action=\"bypass -h user_clan_kick "+cm.getObjectId()+":" + page + "\" value=\"\" width=15 height=15 back=L2UI_CT1.Button_DF_Delete_Down fore=L2UI_CT1.Button_DF_Delete>");
				online_list.append("	</td>");
			}
			else
				online_list.append("	<td width=15 align=center valign=top></td>");
			online_list.append("</tr>");
		}

		int CharactersStart2 = MaxCharactersPerPage * page;
		int CharactersEnd2 = offline.size();
		if(CharactersEnd2 - CharactersStart2 > MaxCharactersPerPage)
			CharactersEnd2 = CharactersStart2 + MaxCharactersPerPage;

		for(int i = CharactersStart2; i < CharactersEnd2; i++)
		{
			L2ClanMember cm = offline.get(i);
			offline_list.append("<tr>");
			offline_list.append("	<td width=130 align=center valign=top>");
			offline_list.append("		<font color=FF0000>"+cm.getName()+"</font>");
			offline_list.append("	</td>");
			offline_list.append("	<td width=120 align=center valign=top>");
			offline_list.append("		<font color=FFFF00>"+String.valueOf(DifferentMethods.htmlClassNameNonClient(activeChar, cm.getClassId()))+"</font>");
			offline_list.append("	</td>");
			offline_list.append("	<td width=30 align=center valign=top>");
			offline_list.append("		<font color=0CEEAA>"+cm.getLevel()+"</font>");
			offline_list.append("	</td>");
			offline_list.append("	<td width=105 align=center valign=top>");
			offline_list.append("		<font color=FF0000>"+date.format(new Date(cm.getLastAccess()*1000))+"</font>");
			offline_list.append("	</td>");
			offline_list.append("	<td width=15 align=center valign=top>");
			offline_list.append("		<button action=\"bypass -h user_clan_kick "+cm.getObjectId()+":" + page + "\" value=\"\" width=15 height=15 back=L2UI_CT1.Button_DF_Delete_Down fore=L2UI_CT1.Button_DF_Delete>");
			offline_list.append("	</td>");
			offline_list.append("</tr>");
		}

		for(int x = 0; x < MaxPages; x++)
		{
			pages.append("<td align=center valign=top>");
			pages.append("	<button action=\"bypass -h user_clanlist " + x + "\" value=\""+(x + 1)+"\" width=25 height=20 back=\"L2UI_CT1.button_df\" fore=\"L2UI_CT1.button_df\">");
			pages.append("</td>");
		}

		html = html.replace("<?online_list?>", online_list.toString());
		html = html.replace("<?offline_list?>", offline_list.toString());
		html = html.replace("<?pages?>", pages.toString());

		ShowBoard.separateAndSend(html, activeChar);
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}