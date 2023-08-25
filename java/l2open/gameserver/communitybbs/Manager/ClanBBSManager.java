package l2open.gameserver.communitybbs.Manager;

import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.ClanTable;
import l2open.gameserver.tables.player.PlayerData;

import java.util.StringTokenizer;

public class ClanBBSManager extends BaseBBSManager
{
	private static ClanBBSManager _Instance = new ClanBBSManager();

	public static ClanBBSManager getInstance()
	{
		return _Instance;
	}

	/**
	 * @param command
	 * @param activeChar
	 */
	@Override
	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if(command.equals("_bbsclan"))
		{
			if(activeChar.getClan() == null || activeChar.getClan().getLevel() < 2)
				clanlist(activeChar, 1, 0, "");
			else
				clanhome(activeChar);
		}
		else if(command.startsWith("_bbsclan_clanlist"))
		{
			if(command.equals("_bbsclan_clanlist"))
			{
				clanlist(activeChar, 1, 0, "");
			}
			else if(command.startsWith("_bbsclan_clanlist;"))
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				int index = Integer.parseInt(st.nextToken());
				clanlist(activeChar, index, 0, "");
			}
		}
		else if(command.startsWith("_bbsclan_clanhome"))
		{
			if(command.equals("_bbsclan_clanhome"))
			{
				clanhome(activeChar);
			}
			else if(command.startsWith("_bbsclan_clanhome;"))
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				int index = Integer.parseInt(st.nextToken());
				clanhome(activeChar, index);
			}
		}
		else if(command.startsWith("_bbsclan_clannotice_edit;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			int index = Integer.parseInt(st.nextToken());
			if(activeChar.getClanId() == index && activeChar.isClanLeader())
			{
				if(activeChar.getClan() != null)
					clanNotice(activeChar, activeChar.getClanId());
			}
			else
				activeChar.sendPacket(new SystemMessage(SystemMessage.ONLY_THE_CLAN_LEADER_IS_ENABLED));
		}
		else if(command.startsWith("_bbsclan_clannotice_enable"))
		{
			PlayerData.getInstance().setNoticeEnabled(activeChar.getClan(), true);
			clanNotice(activeChar, activeChar.getClanId());
		}
		else if(command.startsWith("_bbsclan_clannotice_disable"))
		{
			PlayerData.getInstance().setNoticeEnabled(activeChar.getClan(), false);
			clanNotice(activeChar, activeChar.getClanId());
		}
		else
		{
			separateAndSend("<html><body><br><br><center>Command : " + command + " needs core development</center><br><br></body></html>", activeChar);

		}
	}

	private void clanNotice(L2Player activeChar, int clanId)
	{
		L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if(cl != null)
		{
			if(cl.getLevel() < 2)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER));
				parsecmd("_bbsclan_clanlist", activeChar);
			}
			else
			{
				StringBuilder html = new StringBuilder("<html><body><center>");
				html.append("<br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr><td FIXWIDTH=15>&nbsp;</td>");
				html.append("<td width=755 height=30 align=left>");
				if(!activeChar.isLangRus())
					html.append("<a action=\"bypass -h _bbshome\">Home</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanlist\">Clan Community</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanhome;" + clanId + "\">&$802;</a>&nbsp;&nbsp;&gt;&nbsp;Notice");
				else
					html.append("<a action=\"bypass -h _bbshome\">Главная</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanlist\">Сообщество Клана</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanhome;" + clanId + "\">&$802;</a>&nbsp;&nbsp;&gt;&nbsp;Новости");
				html.append("</td></tr></table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr><td height=5></td></tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0 width=755 height=25 bgcolor=A7A19A>");
				html.append("<tr><td height=10></td></tr>");
				html.append("<tr>");
				html.append("<td fixWIDTH=5></td>");
				if(!activeChar.isLangRus())
					html.append("<td fixwidth=600><a action=\"\">Announcements</a>&nbsp;&nbsp;<a action=\"\">Community Board</a>&nbsp;&nbsp;<a action=\"\">Management</a>&nbsp;&nbsp;<a action=\"\">Mail</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Notice</a></td>");
				else
					html.append("<td fixwidth=600><a action=\"\">Объявления</a>&nbsp;&nbsp;<a action=\"\">Доска Объявлений</a>&nbsp;&nbsp;<a action=\"\">Управление</a>&nbsp;&nbsp;<a action=\"\">Почта</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Новости</a></td>");
				html.append("<td fixWIDTH=5></td>");
				html.append("</tr>");
				html.append("<tr><td height=10></td></tr>");
				html.append("</table>");
				html.append("<br>");
				html.append("<table width=755 border=0 cellspacing=0 cellpadding=0>");
				if(!activeChar.isLangRus())
					html.append("<tr><td fixwidth=610><font color=\"AAAAAA\">With the \"Clan Notice\" function can send a message, which will be received by all members of the clan when entering the game in a pop-up window.</font> </td></tr>");
				else
					html.append("<tr><td fixwidth=610><font color=\"AAAAAA\">С помощью функции \"Новости Клана\" можно отправить сообщение, которое будет получено всеми членами клана при входе в игру в виде всплывающего окна.</font> </td></tr>");
				html.append("<tr><td height=20></td></tr>");
				if(!activeChar.isLangRus())
					if(PlayerData.getInstance().isNoticeEnabled(activeChar.getClan()))
						html.append("<tr><td fixwidth=610> Clan Notice: &nbsp;on &nbsp; <a action=\"bypass -h _bbsclan_clannotice_disable\">off</a>");
					else
						html.append("<tr><td fixwidth=610> Clan Notice: &nbsp;off &nbsp; <a action=\"bypass -h _bbsclan_clannotice_enable\">on</a>");
				else if(PlayerData.getInstance().isNoticeEnabled(activeChar.getClan()))
					html.append("<tr><td fixwidth=610> Новости Клана: &nbsp;вкл &nbsp; <a action=\"bypass -h _bbsclan_clannotice_disable\">выкл</a>");
				else
					html.append("<tr><td fixwidth=610> Новости Клана: &nbsp;выкл &nbsp; <a action=\"bypass -h _bbsclan_clannotice_enable\">вкл</a>");
				html.append("</td></tr>");
				html.append("</table>");
				html.append("<img src=\"L2UI.Squaregray\" width=\"750\" height=\"1\">");
				html.append("<br><br>");
				html.append("<table width=755 border=0 cellspacing=2 cellpadding=0>");
				if(!activeChar.isLangRus())
					html.append("<tr><td>Изменить содержание сообщения:</td></tr>");
				else
					html.append("<tr><td>Change the content of the message:</td></tr>");
				html.append("<tr><td height=5></td></tr>");
				html.append("<tr><td><MultiEdit var=\"Content\" width=750 height=100></td></tr>");
				html.append("</table>");
				html.append("<br>");
				html.append("<table width=755 border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr><td height=5></td></tr>");
				html.append("<tr>");
				html.append("<td align=center FIXWIDTH=65><button value=\"&$140;\" action=\"Write Notice Set _ Content Content Content\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\" ></td>");
				html.append("<td align=center FIXWIDTH=45></td>");
				html.append("<td align=center FIXWIDTH=500></td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</center></body></html>");
				send1001(html.toString(), activeChar);
				send1002(activeChar, PlayerData.getInstance().getNotice(activeChar.getClan()), " ", "0");
			}
		}
	}

	/**
	 * @param activeChar
	 */
	private void clanlist(L2Player activeChar, int page, int type, String search)
	{
		int clancount = ClanTable.getInstance().getClans().length;
		int maxpage = getMaxPageId(clancount);
		if(page > maxpage)
			page = maxpage;
		if(page < 1)
			page = 1;
		int index = 0, minIndex = 0, maxIndex = 0;
		maxIndex = (page == 1 ? page * 9 : (page * 10) - 1);
		minIndex = maxIndex - 9;

		StringBuilder html = new StringBuilder("<html><body><center>");
		html.append("<br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td FIXWIDTH=15>&nbsp;</td>");
		html.append("<td width=755 height=30 align=left>");
		if(!activeChar.isLangRus())
			html.append("<a action=\"bypass -h _bbshome\">Home</a>&nbsp;&nbsp;&gt;&nbsp;Clan Community");
		else
			html.append("<a action=\"bypass -h _bbshome\">Главная</a>&nbsp;&nbsp;&gt;&nbsp;Сообщество Клана");
		html.append("</td></tr></table>");
		if(activeChar.getClanId() != 0)
		{
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=755 height=30 bgcolor=A7A19A>");
			html.append("<tr><td height=10></td></tr>");
			html.append("<tr>");
			html.append("<td fixWIDTH=5></td>");
			if(!activeChar.isLangRus())
				html.append("<td fixWIDTH=750><a action=\"bypass -h _bbsclan_clanhome;" + (activeChar.getClanId() != 0 ? activeChar.getClanId() : 0) + "\">My clan: " + activeChar.getClan().getName() + "</a></td>");
			else
				html.append("<td fixWIDTH=750><a action=\"bypass -h _bbsclan_clanhome;" + (activeChar.getClanId() != 0 ? activeChar.getClanId() : 0) + "\">Мой клан: " + activeChar.getClan().getName() + "</a></td>");
			html.append("<td fixWIDTH=5></td>");
			html.append("</tr>");
			html.append("<tr><td height=10></td></tr>");
			html.append("</table>");
			html.append("<br>");
		}
		html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=A7A19A width=755><tr>");
		html.append("<td FIXWIDTH=5></td>");
		if(!activeChar.isLangRus())
			html.append("<td FIXWIDTH=300 align=center>Name</td><td FIXWIDTH=300 align=center>Leader</td><td FIXWIDTH=100 align=center>Level</td><td FIXWIDTH=100 align=center>Members</td>");
		else
			html.append("<td FIXWIDTH=300 align=center>Название</td><td FIXWIDTH=300 align=center>Глава</td><td FIXWIDTH=100 align=center>Уровень</td><td FIXWIDTH=100 align=center>Состав</td>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("</tr></table>");
		html.append("<img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");

		for(L2Clan cl : ClanTable.getInstance().getClans())
		{
			if(!search.isEmpty() && (type == 0 ? !cl.getName().toLowerCase().contains(search) : !cl.getLeaderName().toLowerCase().contains(search)))
				continue;
			if(index < minIndex)
			{
				index++;
				continue;
			}
			if(index > maxIndex)
				break;

			html.append("<img src=\"L2UI.SquareBlank\" width=\"755\" height=\"3\">");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=755>");
			html.append("<tr><td FIXWIDTH=5></td>");
			html.append("<td FIXWIDTH=300 align=center><a action=\"bypass -h _bbsclan_clanhome;" + cl.getClanId() + "\">" + cl.getName() + "</a></td>");
			html.append("<td FIXWIDTH=300 align=center>" + cl.getLeaderName() + "</td>");
			html.append("<td FIXWIDTH=100 align=center>" + cl.getLevel() + "</td>");
			html.append("<td FIXWIDTH=100 align=center>" + cl.getMembersCount() + "</td>");
			html.append("<td FIXWIDTH=5></td></tr><tr><td height=5></td></tr></table>");
			html.append("<img src=\"L2UI.SquareBlank\" width=\"755\" height=\"3\"><img src=\"L2UI.SquareGray\" width=\"755\" height=\"1\">");
			index++;
		}

		html.append("<img src=\"L2UI.SquareBlank\" width=\"755\" height=\"2\">");
		html.append("<table cellpadding=0 cellspacing=2 border=0><tr>");
		html.append("<td><table><tr><td></td></tr><tr><td>");
		html.append("<button action=\"bypass -h _bbsclan_clanlist;" + (page == 1 ? page : page - 1) + "\" back=\"L2UI_CT1.Button_DF_Left_Down\" fore=\"L2UI_CT1.Button_DF_Left\" width=15 height=15 >");
		html.append("</td></tr></table></td>");

		int i = 0;
		if(maxpage > 21)
		{
			if(page <= 11)
			{
				for(i = 1; i <= (10 + page); i++)
				{
					if(i == page)
						html.append("<td> " + i + " </td>");
					else
						html.append("<td><a action=\"bypass -h _bbsclan_clanlist;" + i + "\"> " + i + " </a></td>");
				}
			}
			else if(page > 11 && (maxpage - page) > 10)
			{
				for(i = (page - 10); i <= (page - 1); i++)
				{
					if(i == page)
						continue;
					else
						html.append("<td><a action=\"bypass -h _bbsclan_clanlist;" + i + "\"> " + i + " </a></td>");
				}
				for(i = page; i <= (page + 10); i++)
				{
					if(i == page)
						html.append("<td> " + i + " </td>");
					else
						html.append("<td><a action=\"bypass -h _bbsclan_clanlist;" + i + "\"> " + i + " </a></td>");
				}
			}
			else if((maxpage - page) <= 10)
			{
				for(i = (page - 10); i <= maxpage; i++)
				{
					if(i == page)
						html.append("<td> " + i + " </td>");
					else
						html.append("<td><a action=\"bypass -h _bbsclan_clanlist;" + i + "\"> " + i + " </a></td>");
				}
			}
		}
		else
		{
			for(i = 1; i <= maxpage; i++)
			{
				if(i == page)
					html.append("<td> " + i + " </td>");
				else
					html.append("<td><a action=\"bypass -h _bbsclan_clanlist;" + i + "\"> " + i + " </a></td>");
			}
		}

		html.append("<td><table><tr><td></td></tr><tr><td>");
		html.append("<button action=\"bypass -h _bbsclan_clanlist;" + (page == maxpage ? page : page + 1) + "\" back=\"L2UI_CT1.Button_DF_Right_Down\" fore=\"L2UI_CT1.Button_DF_Right\" width=15 height=15 >");
		html.append("</td></tr></table></td>");
		html.append("</tr></table>");
		html.append("<table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td width=755 height=20></td></tr>");
		html.append("</table>");
		html.append("<table border=0><tr><td>");
		if(!activeChar.isLangRus())
			html.append("<combobox width=65 var=keyword list=\"Name;Ruler\">");
		else
			html.append("<combobox width=65 var=keyword list=\"Название;Глава\">");
		html.append("</td><td><edit var=text width=130 height=15 length=\"16\"></td>");
		html.append("<td><button value=\"&$420;\" action=\"Write Notice Search _ text keyword keyword\" back=\"l2ui_ct1.button.button_df_small_down\" width=70 height=25 fore=\"l2ui_ct1.button.button_df_small\"></td></tr></table>");
		html.append("</center></body></html>");
		separateAndSend(html.toString(), activeChar);
	}

	/**
	 * @param activeChar
	 */
	private void clanhome(L2Player activeChar)
	{
		clanhome(activeChar, activeChar.getClanId());
	}

	/**
	 * @param activeChar
	 * @param clanId
	 */
	private void clanhome(L2Player activeChar, int clanId)
	{
		L2Clan cl = ClanTable.getInstance().getClan(clanId);
		if(cl != null)
		{
			if(cl.getLevel() < 2)
			{
				activeChar.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER));
				parsecmd("_bbsclan_clanlist", activeChar);
			}
			else
			{
				StringBuilder html = new StringBuilder("<html><body><center>");
				html.append("<br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0>");
				html.append("<tr><td FIXWIDTH=15>&nbsp;</td>");
				html.append("<td width=755 height=30 align=left>");
				if(!activeChar.isLangRus())
					html.append("<a action=\"bypass -h _bbshome\">Home</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanlist\">Clan Community</a>");
				else
					html.append("<a action=\"bypass -h _bbshome\">Главная</a>&nbsp;&nbsp;&gt;&nbsp;<a action=\"bypass -h _bbsclan_clanlist\">Сообщество Клана</a>");
				if(activeChar.getClanId() == cl.getClanId())
					html.append("&nbsp;&nbsp;&gt;&nbsp;&$802;");
				else
					html.append("&nbsp;&nbsp;&gt;&nbsp;" + cl.getName());
				html.append("</td></tr></table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0 width=755 height=25 bgcolor=A7A19A>");
				html.append("<tr><td height=10></td></tr>");
				html.append("<tr>");
				html.append("<td fixWIDTH=5></td>");
				if(activeChar.getClanId() == cl.getClanId() && activeChar.isClanLeader())
				{
					if(!activeChar.isLangRus())
						html.append("<td fixwidth=600><a action=\"\">Announcements</a>&nbsp;&nbsp;<a action=\"\">Community Board</a>&nbsp;&nbsp;<a action=\"\">Management</a>&nbsp;&nbsp;<a action=\"\">Mail</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Notice</a></td>");
					else
						html.append("<td fixwidth=600><a action=\"\">Объявления</a>&nbsp;&nbsp;<a action=\"\">Доска Объявлений</a>&nbsp;&nbsp;<a action=\"\">Управление</a>&nbsp;&nbsp;<a action=\"\">Почта</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Новости</a></td>");
				}
				else
				{
					if(!activeChar.isLangRus())
						html.append("<td fixwidth=600><a action=\"\">Announcements</a>&nbsp;&nbsp;<a action=\"\">Community Board</a>&nbsp;&nbsp;<a action=\"\">Mail</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Notice</a></td>");
					else
						html.append("<td fixwidth=600><a action=\"\">Объявления</a>&nbsp;&nbsp;<a action=\"\">Доска Объявлений</a>&nbsp;&nbsp;<a action=\"\">Почта</a>&nbsp;&nbsp;<a action=\"bypass -h _bbsclan_clannotice_edit;" + clanId + ";cnotice\">Новости</a></td>");
				}
				html.append("<td fixWIDTH=5></td>");
				html.append("</tr>");
				html.append("<tr><td height=10></td></tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0 width=750>");
				html.append("<tr><td height=10></td></tr>");
				html.append("<tr><td fixWIDTH=5></td>");
				if(!activeChar.isLangRus())
					html.append("<td fixwidth=290 valign=top>Welome to clan community!</td>");
				else
					html.append("<td fixwidth=290 valign=top>Приветствуем вас в клановом сообществе!</td>");
				html.append("<td fixWIDTH=5></td>");
				html.append("<td fixWIDTH=5 align=center valign=top><img src=\"l2ui.squaregray\" width=2  height=128></td>");
				html.append("<td fixWIDTH=5></td>");
				html.append("<td fixwidth=350>");
				html.append("<table border=0 cellspacing=0 cellpadding=0 width=350>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left>Name:</td>");
				else
					html.append("<td fixWIDTH=100 align=left>Название:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + cl.getName() + "</td>");
				html.append("</tr>");
				html.append("<tr><td height=7></td></tr>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left >Level:</td>");
				else
					html.append("<td fixWIDTH=100 align=left >Уровень:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + cl.getLevel() + "</td>");
				html.append("</tr>");
				html.append("<tr><td height=7></td></tr>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left >Members:</td>");
				else
					html.append("<td fixWIDTH=100 align=left >Состав:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + cl.getMembersCount() + "</td>");
				html.append("</tr>");
				html.append("<tr><td height=7></td></tr>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left >Leader:</td>");
				else
					html.append("<td fixWIDTH=100 align=left >Глава:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + cl.getLeaderName() + "</td>");
				html.append("</tr>");
				html.append("<tr><td height=7></td></tr>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left >Administrator:</td>");
				else
					html.append("<td fixWIDTH=100 align=left >Администратор:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + cl.getLeaderName() + "</td>");
				html.append("</tr>");
				html.append("<tr><td height=7></td></tr>");
				html.append("<tr>");
				if(!activeChar.isLangRus())
					html.append("<td fixWIDTH=100 align=left >Alliance:</td>");
				else
					html.append("<td fixWIDTH=100 align=left >Альянс:</td>");
				html.append("<td fixWIDTH=250 align=left height=20>" + (cl.getAlliance() != null ? cl.getAlliance().getAllyName() : "") + "</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("<td fixWIDTH=5></td>");
				html.append("</tr>");
				html.append("<tr><td height=10></td></tr>");
				html.append("</table>");
				html.append("<table border=0 cellspacing=0 cellpadding=0 width=750  bgcolor=A7A19A>");
				/** html.append("<!--TEMPLET1<tr><td height=10></td></tr><tr><td fixWIDTH=50 align=center valign=top>[&$429;]</td><td fixWIDTH=470 align=left valign=top><?CLICK_LINK?></td><td fixWIDTH=100 align=right valign=top>&$418; :</td><td fixWIDTH=120 align=right valign=top><?POST_DATE?></td><td fixWIDTH=10></td></tr><tr><td height=2></td></tr>TEMPLET-->");
				html.append("<?ANN_LIST?>"); **/
				html.append("<tr><td height=10>&nbsp;</td></tr>");
				html.append("</table>");
				html.append("<br>");
				html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=A7A19A width=750>");
				html.append("<tr>");
				html.append("<td FIXWIDTH=80 align=center></td>");
				html.append("<td FIXWIDTH=370 align=center>&$413;</td>");
				html.append("<td FIXWIDTH=110 align=center>&$417;</td>");
				html.append("<td FIXWIDTH=110 align=center>&$418;</td>");
				html.append("<td FIXWIDTH=80 align=center>&$419;</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"2\">");
				/** html.append("<!--TEMPLET2<table border=0 cellspacing=0 cellpadding=0 width=750><tr><td height=8></td></tr><tr><td FIXWIDTH=80 align=center>[Ранг]</td><td FIXWIDTH=370><?title?></td><td FIXWIDTH=110 align=center><?writer?></td><td FIXWIDTH=110 align=center><?post_date?></td><td FIXWIDTH=80 align=center><?read_count?></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"5\"><img src=\"L2UI.squaregray\" width=\"750\" height=\"1\">TEMPLET-->");
				html.append("<?THREAD_LIST?>"); **/
				html.append("</center></body></html>");
				separateAndSend(html.toString(), activeChar);
			}
		}
	}

	private int getMaxPageId(int ClanCount)
	{
		if(ClanCount < 1)
			return 1;
		if(ClanCount % 10 == 0)
			return ClanCount / 10;
		else
			return (ClanCount / 10) + 1;
	}

	// Write Notice Set _ Content Content Content
	/* (non-Javadoc)
	 * @see ru.l2f.gameserver.communitybbs.Manager.BaseBBSManager#parsewrite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, net.sf.l2j.gameserver.model.actor.instance.L2Player)
	 */
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
		if(ar1.equals("Set"))
		{
			PlayerData.getInstance().setNotice(activeChar.getClan(), ar4);
			activeChar.sendPacket(new SystemMessage(SystemMessage.NOTICE_HAS_BEEN_SAVED));
		}
		else if(ar1.equals("Search"))
		{
			int type = (ar4.equals("Name") || ar4.equals("Название")) ? 0 : 1;
			clanlist(activeChar, 1, type, ar3.toLowerCase().trim());
		}
	}
}