package communityboard;

import java.sql.ResultSet;
import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.Files;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.logging.Logger;

public class PremiumLottery extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(General.class.getName());
	private static int total_games;
	private static int day_games;
	private static int jackpot;
	private static int wins;
	private static long win_count;
	private static int loss;
	private static long loss_count;
	private static int jackpot_win;
	private static long jackpot_count;
	private static long result;
	
	private static Winner winner = new Winner();
	private static int CabinetLicense = 1 << 10;

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	private static enum Commands
	{
		_bbsplottery
	}

	@Override
	public void onLoad()
	{
		//if((Functions.script & CabinetLicense) != CabinetLicense)
		//	return;

		_log.info("CommunityBoard: Premium Lottery games loaded.");
		CommunityHandler.getInstance().registerCommunityHandler(this);
		restoreLotteryData();
		_log.info("CommunityBoard: Premium Lottery games played " + Util.formatAdena(getTotalGames()) + ".");
		restoreJackpot();
		_log.info("CommunityBoard: Premium Lottery jackpot is " + Util.formatAdena(jackpot) + " " + DifferentMethods.getItemName(ConfigValue.PLotteryItem) + ".");
		restoreWinnerData();
		restoreStats();
	}

	@Override
	public void onReload()
	{
		store();
	}

	@Override
	public void onShutdown()
	{
		store();
	}

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		//if((Functions.script & CabinetLicense) != CabinetLicense || player.is_block)
		//	return;

		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		boolean win_page = false;
		boolean stats_page = false;
		String html = Files.read(ConfigValue.CommunityBoardHtmlRoot + "cabinet/games/premium_lottery.htm", player);

		if(!ConfigValue.PLotteryAllow)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}
		else if(bypass.startsWith(Commands._bbsplottery.toString()))
		{
			boolean win = false;
			boolean index = false;
			boolean win_jackpot = false;
			String[] task = bypass.split(":");
			if(task[1].equals("play"))
			{
				if(check(player))
				{
					int price = ConfigValue.PLotteryBet[Integer.parseInt(task[2])];
					if(DifferentMethods.getPay(player, ConfigValue.PLotteryItem, price, true))
					{
						UpdateGames();
						if(Rnd.chance(ConfigValue.PLotteryWinChance))
						{
							wins++;
							win_count = win_count + (price * ConfigValue.PLotteryRewardMul);
							win = true;
							Functions.addItem(player, ConfigValue.PLotteryItem, price * ConfigValue.PLotteryRewardMul);
							if(Rnd.chance(ConfigValue.PLotteryJackpotChance))
							{
								win_jackpot = true;
								jackpot_win++;
								jackpot_count = jackpot_count + jackpot;
								Functions.addItem(player, ConfigValue.PLotteryItem, jackpot);
								updateWinner(jackpot, player.getName());
								player.broadcastSkill(new MagicSkillUse(player, player, 6234, 1, 1000, 0), true);

								String[] param = {
										String.valueOf(player.getName()),
										String.valueOf(Util.formatAdena(jackpot)),
										String.valueOf(DifferentMethods.getItemName(ConfigValue.PLotteryItem)) };

								Announcements.getInstance().announceByCustomMessage("communityboard.games.lottery.jackpot.announce", param, Say2C.CRITICAL_ANNOUNCEMENT);

								Log.add(" " + player.getName() + " win jackpot " + jackpot + " " + DifferentMethods.getItemName(ConfigValue.PLotteryItem) + "", "PremiumLottery");
								nulledJackpot();
							}
						}
						else
						{
							loss++;
							loss_count = loss_count + price;
							setJackpot(price * ConfigValue.PLotteryToJacktop / 100);
						}
					}
				}
			}
			else if(task[1].equals("index"))
				index = true;
			else if(task[1].equals("winner"))
			{
				index = true;
				win_page = true;
			}
			else if(task[1].equals("premium_stats"))
			{
				index = true;
				stats_page = true;
				result = win_count - loss_count;
			}

			html = html.replace("<?lottery_result?>", index ? new CustomMessage("communityboard.games.lottery.bet.set", player).toString() : win ? new CustomMessage("communityboard.games.lottery.win", player).toString() : new CustomMessage("communityboard.games.lottery.loose", player).toString());
			html = html.replace("<?lottery_button?>", String.valueOf(button(player)));
			html = html.replace("<?lottery_jackpot?>", win_jackpot ? new CustomMessage("communityboard.games.lottery.jackpot.win", player).toString() : jackpot >= Integer.MAX_VALUE ? Util.formatAdena(jackpot) + " MAX" : Util.formatAdena(jackpot));
			html = html.replace("<?lottery_game_all?>", String.valueOf(Util.formatAdena(getTotalGames())));
			html = html.replace("<?lottery_game_day?>", String.valueOf(Util.formatAdena(getDayGames())));
			html = html.replace("<?lottery_item?>", String.valueOf(ConfigValue.PLotteryItem));
			html = html.replace("<?lottery_max_jackpot?>", String.valueOf(Util.formatAdena(ConfigValue.PLotteryMaxJacktop)));
			html = html.replace("<?lottery_to_jackpot>", String.valueOf(ConfigValue.PLotteryToJacktop));
			html = html.replace("<?lottery_win_mul>", String.valueOf(ConfigValue.PLotteryRewardMul));
			html = html.replace("<?stats?>", player.isGM() ? "<font color=\"FF0000\"><a action=\"bypass -h _bbsslottery:premium_stats\">Статистика</a></font>" : "<br>");

		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);

		separateAndSend(html, player);
		if(win_page)
			Functions.show(showWinnerPage(player), player, null);

		if(stats_page && player.isGM())
			Functions.show(showStats(), player, null);
	}

	private boolean check(L2Player player)
	{
		if(player.getLevel() < ConfigValue.PLotteryLevel[0])
		{
			player.sendMessage(new CustomMessage("common.level.is.low", player).addNumber(ConfigValue.PLotteryLevel[0]));
			return false;
		}
		else if(player.getLevel() > ConfigValue.PLotteryLevel[1])
		{
			player.sendMessage(new CustomMessage("common.level.is.high", player).addNumber(ConfigValue.PLotteryLevel[1]));
			return false;
		}
		else if(ConfigValue.PLotteryOnlyPremium && !player.hasBonus())
		{
			player.sendMessage(new CustomMessage("common.OnlyForPremium", player));
			return false;
		}
		else
			return true;
	}

	public static String button(L2Player player)
	{
		StringBuilder html = new StringBuilder();
		int block = ConfigValue.PLotteryBet.length / 2;
		for(int i = 1; i <= ConfigValue.PLotteryBet.length; i++)
		{
			html.append("<td>");
			html.append("<button action=\"bypass -h _bbsplottery:play:" + (i - 1) + "\" value=\"" + new CustomMessage("communityboard.games.lottery.bet", player).addString(Util.formatAdena(ConfigValue.PLotteryBet[i - 1])).toString() + "\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\">");
			html.append("</td>");
			html.append(i == block ? "</tr><tr>" : "");
		}

		return html.toString();
	}

	private static class Winner
	{
		public int[] count = new int[8];
		public String[] name = new String[8];
	}

	private String showWinnerPage(L2Player player)
	{
		StringBuilder html = new StringBuilder();

		html.append("<html noscrollbar>");
		html.append("<title>" + new CustomMessage("communityboard.games.lottery.top.win.title", player).toString() + "</title>");
		html.append("<body>");
		html.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr>");
		html.append("<td valign=top>");
		html.append("<table width=280 align=center height=25>");
		html.append("<tr>");
		html.append("<td valign=top width=10></td>");
		html.append("<td valign=top width=120><br>");
		html.append("<table height=25 bgcolor=808080>");
		html.append("<tr>");
		html.append("<td width=100 align=center>");
		html.append(new CustomMessage("common.name", player).toString());
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("<td valign=top width=174><br>");
		html.append("<table height=25 bgcolor=808080>");
		html.append("<tr>");
		html.append("<td width=160 align=center>");
		html.append(new CustomMessage("common.currency", player).addItemName(ConfigValue.PLotteryItem).toString());
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");

		int colorN = 0;
		String[] color = new String[] { "333333", "666666" };

		for(int i = 0; i < 8; i++)
		{
			if(winner.name[i] != null)
			{
				if(colorN > 1)
					colorN = 0;

				html.append("<table width=280 align=center height=25>");
				html.append("<tr>");
				html.append("<td valign=top width=10></td>");
				html.append("<td valign=top width=120><br>");
				html.append("<table height=25 bgcolor=" + color[colorN] + ">");
				html.append("<tr>");
				html.append("<td width=100 align=center>");
				html.append("<font color=B59A75>" + winner.name[i] + "</font>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("<td valign=top width=174><br>");
				html.append("<table height=25 bgcolor=" + color[colorN] + ">");
				html.append("<tr>");
				html.append("<td width=160 align=center>");
				html.append("<font color=LEVEL>" + Util.formatAdena(winner.count[i]) + "</font>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				colorN++;
			}
			else
			{
				if(colorN > 1)
					colorN = 0;

				html.append("<table width=280 align=center height=25>");
				html.append("<tr>");
				html.append("<td valign=top width=10></td>");
				html.append("<td valign=top width=120><br>");
				html.append("<table height=25 bgcolor=" + color[colorN] + ">");
				html.append("<tr>");
				html.append("<td width=100 align=center>");
				html.append("<font color=B59A75>...</font>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("<td valign=top width=174><br>");
				html.append("<table height=25 bgcolor=" + color[colorN] + ">");
				html.append("<tr>");
				html.append("<td width=160 align=center>");
				html.append("<font color=LEVEL>...</font>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				html.append("</td>");
				html.append("</tr>");
				html.append("</table>");
				colorN++;
			}
		}

		html.append("<table width=280 align=center height=25>");
		html.append("<tr>");
		html.append("<td valign=top width=10></td>");
		html.append("<td alighn=center valign=top width=270><br><br>");
		html.append(new CustomMessage("communityboard.games.lottery.top.win.info", player).toString());
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");

		return html.toString();
	}
	
	private String showStats()
	{
		StringBuilder html = new StringBuilder();

		html.append("<html noscrollbar>");
		html.append("<html>");
		html.append("<head>");
		html.append("<body scroll=\"no\">");
		html.append("<title>Статистика лотереи</title>");
		html.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		html.append("<tr>");
		html.append("<td valign=\"top\">");
		html.append("<table width=280>");
		html.append("<tr>");
		html.append("<td valign=\"top\" align=\"center\">");
		html.append("<br>");
		html.append("<font color=\"LEVEL\" name=\"hs12\">Премиум Лотерея</font>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32<br>");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" align=\"left\" width=280>");
		html.append("<table width=280>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270>");
		html.append("<font color=\"009900\">Сыграно:</font> " + getTotalGames() + " игр.");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270><br><br>");
		html.append("<font color=\"009900\">Побед:</font> " + wins + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270>");
		html.append("----<font color=\"LEVEL\">На сумму:</font>  " + Util.formatAdena(win_count) + " " + DifferentMethods.getItemName(ConfigValue.SLotteryItem) + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270><br><br>");
		html.append("<font color=\"FF0000\">Проигрышей:</font> " + loss + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270>");
		html.append("----<font color=\"LEVEL\">На сумму:</font> " + Util.formatAdena(loss_count) + " " + DifferentMethods.getItemName(ConfigValue.SLotteryItem) + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270><br><br>");
		html.append("<font color=\"LEVEL\">Джекпотов выиграно:</font> " + jackpot_win + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270>");
		html.append("----<font color=\"LEVEL\">На сумму:</font> " + Util.formatAdena(jackpot_count) + " " + DifferentMethods.getItemName(ConfigValue.SLotteryItem) + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td valign=\"top\" width=10></td>");
		html.append("<td valign=\"top\" align=\"left\" width=270><br><br>");
		html.append("<font color=\"009900\">Итог:</font> " + (result > 0 ? "Игроки в плюсе на " + Util.formatAdena(win_count - loss_count) : "Игроки в минусе на " + Util.formatAdena(loss_count - win_count)) + " " + DifferentMethods.getItemName(ConfigValue.SLotteryItem) + ".");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");

		return html.toString();
	}

	private void restoreStats()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `count`, `type` FROM `bbs_lottery`;");
			rset = statement.executeQuery();

			while(rset.next())
			{
				if(rset.getString("type").equals("premium_wins"))
					wins = rset.getInt("count");
				else if(rset.getString("type").equals("premium_win_count"))
					win_count = rset.getInt("count");
				else if(rset.getString("type").equals("premium_loss"))
					loss = rset.getInt("count");
				else if(rset.getString("type").equals("premium_loss_count"))
					loss_count = rset.getInt("count");
				else if(rset.getString("type").equals("premium_jackpot_win"))
					jackpot_win = rset.getInt("count");
				else if(rset.getString("type").equals("premium_jackpot_count"))
					jackpot_count = rset.getInt("count");
			}
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not restore lottery stats: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private void store()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			_log.info("premiumLottery: Store Jackpot");
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + wins + " WHERE `type`='premium_wins'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + win_count + " WHERE `type`='premium_win_count'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + loss + " WHERE `type`='premium_loss'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + loss_count + " WHERE `type`='premium_loss_count'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + jackpot_win + " WHERE `type`='premium_jackpot_win'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + loss_count + " WHERE `type`='premium_loss_count'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + jackpot + " WHERE `type`='premium_jackpot'");
			statement.execute();
			statement = con.prepareStatement("UPDATE `bbs_lottery` SET `count`=" + getTotalGames() + " WHERE `type`='premium_total_games'");
			statement.execute();
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not store lottery stats: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private void restoreWinnerData()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int counter = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM `bbs_lottery` WHERE `type`='premium_winner' ORDER BY `count` DESC LIMIT 0,8");
			rset = statement.executeQuery();

			while(rset.next())
			{
				winner.count[counter] = rset.getInt("count");
				winner.name[counter] = rset.getString("name");
				counter++;
			}
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not restore lottery winner: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private boolean restoreJackpot()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `count` FROM `bbs_lottery` WHERE `type`='premium_jackpot'");
			rset = statement.executeQuery();

			if(rset.next())
			{
				jackpot = rset.getInt("count");
			}
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not restore lottery jackpot: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return true;
	}

	private boolean restoreLotteryData()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT `count` FROM `bbs_lottery` WHERE `type`='premium_total_games'");
			rset = statement.executeQuery();

			if(rset.next())
			{
				total_games = rset.getInt("count");
			}
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not restore lottery games: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}

		return true;
	}

	private void updateWinner(int count, String name)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO `bbs_lottery` (`count`, `type`, `name`) VALUES (" + count + ", 'premium_winner', '" + name + "');");
			statement.execute();
		}
		catch(SQLException e)
		{
			_log.warning("PremiumLottery: Could not increase current lottery winner: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}
	
	private int getTotalGames()
	{
		return total_games + day_games;
	}

	private int getDayGames()
	{
		return day_games;
	}

	private void UpdateGames()
	{
		day_games++;
	}

	private void setJackpot(int count)
	{
		if((jackpot + count) >= ConfigValue.PLotteryMaxJacktop)
			jackpot = ConfigValue.PLotteryMaxJacktop;
		else if((jackpot + count) >= Integer.MAX_VALUE)
			jackpot = Integer.MAX_VALUE;
		else
			jackpot = jackpot + count;
	}

	private void nulledJackpot()
	{
		jackpot = ConfigValue.PLotteryNullJacktop;
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}
}