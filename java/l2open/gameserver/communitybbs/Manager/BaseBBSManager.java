package l2open.gameserver.communitybbs.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.sql.ResultSet;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.Constants;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.*;
import l2open.gameserver.model.barahlo.CBBuffSch;
import l2open.gameserver.model.barahlo.CBTpSch;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.serverpackets.ShowBoard;
import l2open.gameserver.serverpackets.NetPing;
import l2open.gameserver.tables.CharNameTable;
import l2open.util.*;

import static l2open.gameserver.model.base.Race.*;

public abstract class BaseBBSManager
{
	static final Logger _log = Logger.getLogger(BaseBBSManager.class.getName());

	public abstract void parsecmd(String command, L2Player activeChar);

	public abstract void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar);

	protected void separateAndSend(String html, L2Player player)
	{
		//_log.info("html: "+html);
		int online = Integer.parseInt(DifferentMethods.getOnline());
		for(int i=0;i<ConfigValue.BbsOnlineMsg.length;i++)
			if(online < ConfigValue.BbsOnlineMsg[i])
			{
				html = html.replace("<?cb_online_players_name?>", new CustomMessage("BbsOnlineMsg"+i, player).toString());
				html = html.replace("<?cb_online_players_color?>", ConfigValue.BbsOnlineColor[i]);
				break;
			}
		CustomMessage No = new CustomMessage("common.result.no", player);
		CustomMessage Yes = new CustomMessage("common.result.yes", player);

		html = html.replace("<?cb_time?>", String.valueOf(Util.time()));
		html = html.replace("<?cb_online_players?>", String.valueOf(online));
		html = html.replace("<?cb_offtrade_players?>", String.valueOf(DifferentMethods.getOffline()));

		String[] ip_data = Util.checkIP(player);
		html = html.replace("<?allow_ip?>", String.valueOf(ip_data[0]));
		html = html.replace("<?bind_ip?>", String.valueOf(ip_data[1]));

		String[] hwid_data = Util.checkHWID(player);
		html = html.replace("<?allow_hwid?>", String.valueOf(hwid_data[0]));
		html = html.replace("<?bind_hwid?>", String.valueOf(hwid_data[1]));

		html = html.replace("<?player_premium?>", String.valueOf(DifferentMethods.consider(player)));
		html = html.replace("<?player_name?>", String.valueOf(player.getName()));
		html = html.replace("<?player_clan?>", String.valueOf(player.getClan() != null ? new CustomMessage("communityboard.clan.info", player).addString(player.getClan().getName()).addNumber(player.getClan().getLevel()) : "<font color=\"FF0000\">" + No + "</font>"));
		html = html.replace("<?online_time?>", String.valueOf(player.getOnlineTime(player)));
		html = html.replace("<?pvp_count?>", String.valueOf(player.getPvpKills()));
		html = html.replace("<?pc_count?>", String.valueOf(player.getPkKills()));

		ShowBoard.separateAndSend(html, player);
	}

	protected void send1001(String html, L2Player activeChar)
	{
		ShowBoard.send1001(html, activeChar);
	}

	protected void send1002(L2Player activeChar)
	{
		ShowBoard.send1002(activeChar, " ", " ", "0");
	}

	protected void send1002(L2Player activeChar, String string, String string2, String string3)
	{
		ShowBoard.send1002(activeChar, string, string2, string3);
	}

	static long updateTopPVP = 0;
	static long updateTopHg = 0;
	
	static List<PvPInfo> _pvp_stat = new ArrayList<PvPInfo>();
	static List<PvPInfo> _hg_stat = new ArrayList<PvPInfo>();

	static class PvPInfo
	{
		public String name;
		public int pvp;

		public PvPInfo(String n, int p)
		{
			name = n;
			pvp = p;
		}
	}

	//Используется для _bbstop и _bbshome. (Для снижения нагрузки не использовать для других байпасов)
	public static String readHtml(String file, L2Player player)
	{
		String html = Files.read(file, player);
		if(html == null)
			return "";

		player.sendPacket(new NetPing((int)(System.currentTimeMillis() - l2open.gameserver.GameStart.serverUpTime())));

		CustomMessage No = new CustomMessage("common.result.no", player);
		CustomMessage Yes = new CustomMessage("common.result.yes", player);

		if(ConfigValue.NewsAllow)
		{
			if(NewsBBSManager.getInstance().lUpdateTime + ConfigValue.NewsUpdate * 60 < System.currentTimeMillis() / 1000)
			{
				NewsBBSManager.getInstance().selectPortalNews();
				NewsBBSManager.getInstance().selectServerNews();
				NewsBBSManager.getInstance().lUpdateTime = System.currentTimeMillis() / 1000;
				//_log.info("News in the commynity board has been updated.");
			}
			String server = "<table border=0 cellspacing=0 cellpadding=0><tr><td width=345 align=center valign=top><font color=\"B59A75\" name=\"hs12\">"+new CustomMessage("communityboard.info1", player)+"</font></td></tr></table><br><table border=0 cellspacing=0 cellpadding=0><tr><td width=345 height=\"6\"><img src=l2ui.squaregray width=345 height=1></td></tr></table>";
			String project = "<table border=0 cellspacing=0 cellpadding=0><tr><td width=345 align=center valign=top><font color=\"B59A75\" name=\"hs12\">"+new CustomMessage("communityboard.info2", player)+"</font></td></tr></table><br><table border=0 cellspacing=0 cellpadding=0><tr><td width=345 height=\"6\"><img src=l2ui.squaregray width=345 height=1></td></tr></table>";

			for(int i = 0; i < ConfigValue.NewsCount; i++)
			{
				project += NewsBBSManager.getInstance().parse(player, "project", i);
				server += NewsBBSManager.getInstance().parse(player, "server", i);
			}

			html = html.replace("<?project_news?>", project);
			html = html.replace("<?server_news?>", server);
		}
		else
		{
			html = html.replace("<?project_news?>", "<center><font color=\"FF0000\">" + new CustomMessage("scripts.services.off", player) + "</font></center>");
			html = html.replace("<?server_news?>", "<center><font color=\"FF0000\">" + new CustomMessage("scripts.services.off", player) + "</font></center>");
		}

		html = html.replace("<?player_noobless?>", String.valueOf(player.isNoble() ? "<font color=\"18FF00\">" + Yes + "</font>" : player.getSubLevel() > 75 ? "<font color=\"FF0000\">" + No + "</font>" : new CustomMessage("communityboard.noble.info", player)));
		html = html.replace("<?player_hero?>", String.valueOf(player.isHero() ? "<font color=\"18FF00\">" + Yes + "</font>" : "<font color=\"FF0000\">" + No + "</font>"));
		html = html.replace("<?player_level?>", String.valueOf(player.getLevel()));
		html = html.replace("<?player_ip?>", String.valueOf(player.getIP()));
		html = html.replace("<?raid_points?>", String.valueOf(player.getRaidPoints()));
		html = html.replace("<?player_class?>", String.valueOf(DifferentMethods.htmlClassNameNonClient(player, player.getClassId().getId())));
		//html = html.replace("<?player_status?>", String.valueOf(player.getStatusName())); TODO: написать метод присвоения Статуса персонажа: Vagabond\Vassal\Heir итд. и запиздячить в блок с инфо.

		//html = html.replace("<?player_rank?>", String.valueOf(1385+player.getPledgeClass()));

		html = html.replace("<?rate_xp?>", String.valueOf(RateService.getRateXp(player) * player.getBonus().RATE_XP*player.getAltBonus()));
		html = html.replace("<?rate_sp?>", String.valueOf(RateService.getRateSp(player) * player.getBonus().RATE_SP*player.getAltBonus()));
		html = html.replace("<?rate_adena?>", String.valueOf(RateService.getRateDropAdena(player) * player.getBonus().RATE_DROP_ADENA*player.getAltBonus()));
		html = html.replace("<?rate_items?>", String.valueOf(RateService.getRateDropItems(player) * player.getBonus().RATE_DROP_ITEMS*player.getAltBonus()));
		html = html.replace("<?rate_spoil?>", String.valueOf(RateService.getRateDropSpoil(player) * player.getBonus().RATE_DROP_SPOIL*player.getAltBonus()));
		html = html.replace("<?rate_quests_reward?>", String.valueOf(player.getBonus().RATE_QUESTS_REWARD*player.getAltBonus()));
		html = html.replace("<?rate_quests_drop?>", String.valueOf(player.getBonus().RATE_QUESTS_DROP*player.getAltBonus()));

		// Не выводить в общак...
		html = html.replace("<?rate_epaulette?>", String.valueOf(ConfigValue.RateDropEpaulette * player.getBonus().RATE_EPAULETTE*player.getAltBonus()));
		html = html.replace("<?rate_fame?>", String.valueOf(ConfigValue.RateFameReward * player.getBonus().RATE_FAME*player.getAltBonus()));

		html = html.replace("<?player_ally?>", String.valueOf(player.getClan() != null && player.getClan().getAlliance() != null ? player.getClan().getAlliance().getAllyName() : "<font color=\"FF0000\">" + No + "</font>"));

		html = html.replace("<?ping?>", String.valueOf(player.getNetConnection().getPing()));
		if(ConfigValue.ShowBuffType > -1)
			html = html.replace("<?show_buff_list?>", ConfigValue.ShowBuffType == 0 ? РазноеГовно.слепить_список_бафов(player) : РазноеГовно.слепить_список_бафов2(player));
		if(ConfigValue.AffordShowTp)
			html = html.replace("<?show_tp_list?>", РазноеГовно.слепить_список_точек_тп(player));

		if(player.getAttainment() != null)
		{
			if(ConfigValue.AttainmentType == 0x100)
			{
				int state = 0;
				if(player.getAttainment()._pvp_count > ConfigValue.Attainment1_count)
				{
					state = player.getAttainment().getAttainmentState(2);
					html = html.replace("%attaintment1%", "back=\"icons.2_"+state+"_Over\" fore=\"icons.2_"+state+"\"");
					html = html.replace("%attaintment1_stat%", Math.min(player.getAttainment()._pvp_count, ConfigValue.Attainment2_count)+"/"+ConfigValue.Attainment2_count);
				}
				else
				{
					state = player.getAttainment().getAttainmentState(1);
					html = html.replace("%attaintment1%", "back=\"icons.1_"+state+"_Over\" fore=\"icons.1_"+state+"\"");
					html = html.replace("%attaintment1_stat%", Math.min(player.getAttainment()._pvp_count, ConfigValue.Attainment1_count)+"/"+ConfigValue.Attainment1_count);
				}
				if(player.getAttainment()._pk_count > ConfigValue.Attainment3_count)
				{
					state = player.getAttainment().getAttainmentState(4);
					html = html.replace("%attaintment2%", "back=\"icons.4_"+state+"_Over\" fore=\"icons.4_"+state+"\"");
					html = html.replace("%attaintment2_stat%", Math.min(player.getAttainment()._pk_count, ConfigValue.Attainment4_count)+"/"+ConfigValue.Attainment4_count);
				}
				else
				{
					state = player.getAttainment().getAttainmentState(3);
					html = html.replace("%attaintment2%", "back=\"icons.3_"+state+"_Over\" fore=\"icons.3_"+state+"\"");
					html = html.replace("%attaintment2_stat%", Math.min(player.getAttainment()._pk_count, ConfigValue.Attainment3_count)+"/"+ConfigValue.Attainment3_count);
				}

				html = html.replace("%attaintment6_stat%", player.getVarInt("Attainment6_kill", 0)+"/"+ConfigValue.Attainment6_count);
				html = html.replace("%attaintment7_stat%", player.getVarInt("Attainment7_win", 0)+"/"+ConfigValue.Attainment7_count);
				html = html.replace("%attaintment8_stat%", (player.getClan() == null ? 0 : player.getClan().getLevel())+"/"+ConfigValue.Attainment8_count);
				html = html.replace("%attaintment11_stat%", player.getVarInt("Attainment11_res", 0)+"/"+ConfigValue.Attainment11_count);
				html = html.replace("%attaintment12_stat%", player.getVarInt("Attainment12_win", 0)+"/"+ConfigValue.Attainment12_count);
				html = html.replace("%attaintment13_stat%", player.getVarInt("Attainment13_time", 0)+"/"+ConfigValue.Attainment13_count);
				html = html.replace("%attaintment14_stat%", player.getVarInt("Attainment14_die", 0)+"/"+ConfigValue.Attainment14_count);

				for(int id=5;id<=18;id++)
				{
					state = player.getAttainment().getAttainmentState(id);
					html = html.replace("%attaintment"+id+"%", "back=\"icons."+id+"_"+state+"_Over\" fore=\"icons."+id+"_"+state+"\"");
				}
			}
			else if(ConfigValue.AttainmentType == 0x560)
			{
				for(int i=1;i<=10;i++)
				{
					int[] stats = player.getAttainment().getAttainmentStats(i);
					int level = stats[0];
					int cur_count = stats[1];
					int need_count = stats[2];

					html = html.replace("<?attaintment_"+i+"_cur?>", String.valueOf(Math.min(cur_count, need_count)));
					html = html.replace("<?attaintment_"+i+"_need?>", String.valueOf(need_count));
					html = html.replace("<?attaintment_"+i+"_level?>", String.valueOf(level));
					html = html.replace("<?attaintment_"+i+"_icon?>", player.getAttainment().getIcon(i));
				}
			}
		}

		String competition_done = "0";
		String competition_win = "0";
		String competition_loose = "0";
		String noble_points = "0";

		if(player.isNoble())
		{
			competition_done = String.valueOf(Olympiad.getCompetitionDone(player.getObjectId()));
			competition_win = String.valueOf(Olympiad.getCompetitionWin(player.getObjectId()));
			competition_loose = String.valueOf(Olympiad.getCompetitionLoose(player.getObjectId()));
			noble_points = String.valueOf(Olympiad.getNoblePoints(player.getObjectId()));
		}
		html = html.replace("<?competition_done?>", competition_done);
		html = html.replace("<?competition_win?>", competition_win);
		html = html.replace("<?competition_loose?>", competition_loose);
		html = html.replace("<?noble_points?>", noble_points);

		String type = "";
		switch(player.getRace())
		{
				case darkelf:
					if(player.getSex() == 1)
						type = "delf_f";
					else
						type = "delf_m";
					break;
				case elf:
					if(player.getSex() == 1)
						type = "elf_f";
					else
						type = "elf_m";
					break;
				case dwarf:
					if(player.getSex() == 1)
						type = "gnom_f";
					else
						type = "gnom_m";
					break;
				case human:
					if(player.getSex() == 1)
					{
						if(player.getClassId().isMage())
							type = "magik_f";
						else
							type = "human_f";
					}
					else
					{
						if(player.getClassId().isMage())
							type = "magik_m";
						else
							type = "human_m";
					}
					break;
				case kamael:
					if(player.getSex() == 1)
						type = "kama_f";
					else
						type = "kama_m";
					break;
				case orc:
					if(player.getSex() == 1)
					{
						if(player.getClassId().isMage())
							type = "saman_f";
						else
							type = "ork_f";
					}
					else
					{
						if(player.getClassId().isMage())
							type = "saman_m";
						else
							type = "ork_m";
					}
					break;
		}
		html = html.replace("%src_race%", "icons."+type);

		//_log.info("html: "+html);
		return addCustomReplace(html);
	}

	@SuppressWarnings("unchecked")
	public static String addCustomReplace(String html)
	{
		if(ConfigValue.TheHungerGames_CbInfo)
		{
			html = html.replace("<?hg_winner_tur?>", ServerVariables.getString("TheHungerGames_TurnirWinner", "победителя нет"));

			if(updateTopPVP + ConfigValue.StatisticUpdateTopPVP * 60 < System.currentTimeMillis() / 1000)
			{
				selectTopPVP();
				updateTopPVP = System.currentTimeMillis() / 1000;
			}
			if(updateTopHg + ConfigValue.StatisticUpdateTopPVP * 60 < System.currentTimeMillis() / 1000)
			{
				Constants.points_list = ValueSortMap.sortMapByValue(Constants.points_list, false);

				_hg_stat.clear();
				for(Entry<Integer, Integer> entry : Constants.points_list.entrySet())
					_hg_stat.add(new PvPInfo(CharNameTable.getInstance().getNameByObjectId(entry.getKey()), entry.getValue()));

				updateTopHg = System.currentTimeMillis() / 1000;
			}
			for(int i=0;i<5;i++)
			{
				if(_hg_stat.size() > i)
				{
					PvPInfo pi = _hg_stat.get(i);
					html = html.replace("<?hg_stat_name_"+(i+1)+"?>", pi.name);
					html = html.replace("<?hg_stat_point_"+(i+1)+"?>", String.valueOf(pi.pvp));
				}
				else
				{
					html = html.replace("<?hg_stat_name_"+(i+1)+"?>", "-");
					html = html.replace("<?hg_stat_point_"+(i+1)+"?>", "");
				}
			}

			for(int i=0;i<5;i++)
			{
				if(_pvp_stat.size() > i)
				{
					PvPInfo pi = _pvp_stat.get(i);
					html = html.replace("<?hg_pvp_stat_name_"+(i+1)+"?>", pi.name);
					html = html.replace("<?hg_pvp_stat_pvp_"+(i+1)+"?>", String.valueOf(pi.pvp));
				}
				else
				{
					html = html.replace("<?hg_pvp_stat_name_"+(i+1)+"?>", "-");
					html = html.replace("<?hg_pvp_stat_pvp_"+(i+1)+"?>", "");
				}
			}
		}
		return html;
	}

	private static void selectTopPVP()
	{
		_pvp_stat.clear();
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name, pvpkills FROM characters ORDER BY pvpkills DESC LIMIT 5");
			rset = statement.executeQuery();

			while(rset.next())
			{
				if(!rset.getString("char_name").isEmpty())
					_pvp_stat.add(new PvPInfo(rset.getString("char_name"), rset.getInt("pvpkills")));
			}
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
}