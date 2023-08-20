package communityboard;

import java.sql.ResultSet;
import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.communitybbs.Manager.BaseBBSManager;
import l2open.gameserver.handler.CommunityHandler;
import l2open.gameserver.handler.ICommunityHandler;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

/**
 * @author Powered by L2CCCP
 */

public class CommunityBoardShortStats extends BaseBBSManager implements ICommunityHandler, ScriptFile
{
	static final Logger _log = Logger.getLogger(CommunityBoardShortStats.class.getName());

	@Override
	public void onLoad()
	{
		CommunityHandler.getInstance().registerCommunityHandler(this);
		selectTopPK();
		selectTopPVP();
		_log.info("Short statistics in the commynity board has been updated.");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getCommunityCommandEnum()
	{
		return Commands.values();
	}

	private static enum Commands
	{
		_bbsstats
	}

	public static class Manager
	{
		public String[] TopPvPName = new String[10];
		public int[] TopPvPOnline = new int[10];
		public int[] TopPvPCount = new int[10];

		public String[] TopPkName = new String[10];
		public int[] TopPkOnline = new int[10];
		public int[] TopPkCount = new int[10];
	}

	static Manager ManagerStats = new Manager();

	public long update = System.currentTimeMillis() / 1000;
	public int number = 0;

	@Override
	public void parsecmd(String bypass, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(player.is_block || player.isInEvent() > 0)
			return;
		if(!ConfigValue.StatsAllow)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}
		else if(bypass.equals(Commands._bbsstats.toString()))
		{
			if(update + ConfigValue.StatsUpdate * 60 < System.currentTimeMillis() / 1000)
			{
				selectTopPK();
				selectTopPVP();
				update = System.currentTimeMillis() / 1000;
				_log.info("Short statistics in the commynity board has been updated.");
			}
			show(player);
		}
		else
			separateAndSend(DifferentMethods.getErrorHtml(player, bypass), player);
	}

	private void show(L2Player player)
	{
		number = 0;
		String content;

		content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "statistic/short.htm", player);
		content = content.replace("<?update?>", String.valueOf(ConfigValue.StatsUpdate));

		CustomMessage No = new CustomMessage("common.result.no", player);
		CustomMessage Yes = new CustomMessage("common.result.yes", player);

		while(number < 10)
		{
			if(ManagerStats.TopPvPName[number] != null)
			{
				content = content.replace("<?pvp_name_" + number + "?>", ManagerStats.TopPvPName[number]);
				content = content.replace("<?pvp_count_" + number + "?>", Integer.toString(ManagerStats.TopPvPCount[number]));

				if(ManagerStats.TopPvPOnline[number] == 1)
					content = content.replace("<?pvp_online_" + number + "?>", "<font color=\"66FF33\">" + Yes + "</font>");
				else
					content = content.replace("<?pvp_online_" + number + "?>", "<font color=\"B59A75\">" + No + "</font>");
			}
			else
			{
				content = content.replace("<?pvp_name_" + number + "?>", "...");
				content = content.replace("<?pvp_online_" + number + "?>", "...");
				content = content.replace("<?pvp_count_" + number + "?>", "");
			}

			if(ManagerStats.TopPkName[number] != null)
			{
				content = content.replace("<?pk_name_" + number + "?>", ManagerStats.TopPkName[number]);
				content = content.replace("<?pk_count_" + number + "?>", Integer.toString(ManagerStats.TopPkCount[number]));

				if(ManagerStats.TopPkOnline[number] == 1)
					content = content.replace("<?pk_online_" + number + "?>", "<font color=\"66FF33\">" + Yes + "</font>");
				else
					content = content.replace("<?pk_online_" + number + "?>", "<font color=\"B59A75\">" + No + "</font>");
			}
			else
			{
				content = content.replace("<?pk_name_" + number + "?>", "...");
				content = content.replace("<?pk_online_" + number + "?>", "...");
				content = content.replace("<?pk_count_" + number + "?>", "");
			}

			number++;
		}

		separateAndSend(addCustomReplace(content), player);
	}

	public void selectTopPVP()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		number = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name, pvpkills, online FROM characters ORDER BY pvpkills DESC LIMIT 10;");
			rset = statement.executeQuery();

			while(rset.next())
			{
				if(!rset.getString("char_name").isEmpty())
				{
					ManagerStats.TopPvPName[number] = rset.getString("char_name");
					ManagerStats.TopPvPOnline[number] = rset.getInt("online");
					ManagerStats.TopPvPCount[number] = rset.getInt("pvpkills");
				}
				else
				{
					ManagerStats.TopPvPName[number] = "...";
					ManagerStats.TopPvPOnline[number] = 0;
					ManagerStats.TopPvPCount[number] = 0;
				}
				number++;
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

		return;
	}

	public void selectTopPK()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		number = 0;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name, pkkills, online FROM characters ORDER BY pkkills DESC LIMIT 10;");
			rset = statement.executeQuery();
			while(rset.next())
			{
				if(!rset.getString("char_name").isEmpty())
				{
					ManagerStats.TopPkName[number] = rset.getString("char_name");
					ManagerStats.TopPkOnline[number] = rset.getInt("online");
					ManagerStats.TopPkCount[number] = rset.getInt("pkkills");
				}
				else
				{
					ManagerStats.TopPkName[number] = "...";
					ManagerStats.TopPkOnline[number] = 0;
					ManagerStats.TopPkCount[number] = 0;
				}
				number++;
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

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{}
}