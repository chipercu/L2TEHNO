package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.database.*;
import l2open.util.Log;
import l2open.util.Util;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.*;

import java.sql.ResultSet;

/**
 * Запиздовал Diagod...
 * open-team.ru
 **/
public class MMOVoteManager
{
	private static Logger _log = Logger.getLogger(MMOVoteManager.class.getName());

	private static BufferedReader reader;
	private static int vote_id=-1;

	public static void main(String[] args) throws Exception
	{
		start();
	}

	public static void start() throws Exception
	{
		if(!ConfigValue.MmoVoteStartWithServer)
		{
			InputStream is = new FileInputStream(new File("./config/log.properties"));
			LogManager.getLogManager().readConfiguration(is);
			is.close();

			ConfigSystem.load();

			L2DatabaseFactory.getInstance();
			Log.InitGSLoggers();
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ConnectAndUpdate(), ConfigValue.MmoVoteManagerInterval, ConfigValue.MmoVoteManagerInterval);
		_log.info("MMOVoteManager: Start sucesfully.");
	}

	private static void getPage(String address)
	{
		_log.info("MMOVoteManager: Start load page...");
		try
		{
			URL url = new URL(address);
			HttpURLConnection connect = (HttpURLConnection)url.openConnection();
			connect.setRequestProperty("User-Agent", "server: open-team.ru");
			connect.getContent();

			reader = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF8"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	4186
	2015-03-01
	06:01:09
	UTC
	95.154.100.213
	builder
	1
	**/
	private static void parse()
	{
		try
		{
			String line;
			_log.info("MMOVoteManager: Start parse page...");
			read_line : while((line = reader.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\t- :");
				if(st.countTokens() == 11)
					try
					{
						int vote_id_ = Integer.parseInt(st.nextToken());
						if(vote_id_ <= vote_id && vote_id != -1)
							continue read_line;
						vote_id = Math.max(vote_id_, vote_id);
						int year = Integer.parseInt(st.nextToken());
						int month = Integer.parseInt(st.nextToken()) - 1;
						int day = Integer.parseInt(st.nextToken());
						int hour = Integer.parseInt(st.nextToken());
						int minute = Integer.parseInt(st.nextToken());
						int second = Integer.parseInt(st.nextToken());
						st.nextToken();
						st.nextToken();
						String charName = st.nextToken();

						while(st.countTokens() > 1)
							st.nextToken();
						int voteType = Integer.parseInt(st.nextToken());

						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, month);
						calendar.set(Calendar.DAY_OF_MONTH, day);
						calendar.set(Calendar.HOUR_OF_DAY, hour);
						calendar.set(Calendar.MINUTE, minute);
						calendar.set(Calendar.SECOND, second);
						calendar.set(Calendar.MILLISECOND, 0);

						checkAndSave(vote_id_, calendar.getTimeInMillis() / 1000L, charName, voteType);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		}
		catch (Exception e) 
		{
			_log.warning("MMOVoteManager: Cant store MMOVote data.");
			e.printStackTrace();
		}
	}

	private static void set_vote_id()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;

		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT MAX(vote_id) FROM character_vote where type='2'");
			rset = statement.executeQuery();

			if(rset.next())
				vote_id = rset.getInt(1);
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

	private static void checkAndSave(int vote_id, long voteTime, String charName, int voteType)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, charName);
			rset = statement.executeQuery();

			int player_id = 0;
			if(rset.next())
				player_id = rset.getInt("obj_Id");
			if(player_id > 0)
			{
				statement = con.prepareStatement("INSERT INTO character_vote (type, vote_id, date, id, nick, multipler, has_reward) values (2,?,?,?,?,?,1)");
				statement.setInt(1, vote_id);
				statement.setLong(2, voteTime);
				statement.setInt(3, player_id);
				statement.setString(4, charName);
				statement.setInt(5, voteType);
				statement.execute();

				DatabaseUtils.closeDatabaseSR(statement, rset);

				String insert_item = "";
				for(int i = 0; i < ConfigValue.MmoVoteReward.length; i += 2)
					insert_item+="("+player_id+","+ConfigValue.MmoVoteReward[i]+","+(ConfigValue.MmoVoteReward[i + 1]*voteType)+",'MMOVOTE_REWARD_"+vote_id+"')"+(ConfigValue.MmoVoteReward.length == i+2 ? "" : ",");

				statement = con.prepareStatement("INSERT INTO items_delayed (owner_id,item_id,`count`,description) VALUES "+insert_item);
				statement.executeUpdate();
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

	private static class ConnectAndUpdate implements Runnable
	{
		public void run()
		{
			try
			{
				getPage(ConfigValue.MmoVoteWebAddress);
			}
			finally
			{
				//set_vote_id();
				parse();
				_log.info("MMOVoteManager: Finish give reward...");
			}
		}
	}
}