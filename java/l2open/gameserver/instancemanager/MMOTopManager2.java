package l2open.gameserver.instancemanager;

import l2open.common.ThreadPoolManager;
import l2open.config.*;
import l2open.database.*;
import l2open.util.Log;
import l2open.util.Util;
import l2open.util.Rnd;

import java.io.*;
import java.net.URL;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.*;

import java.sql.ResultSet;

/**
INSERT INTO items_delayed (owner_id,item_id,count,description) SELECT id, 10639, 5*multipler, 'MMOTOP_REWARD' FROM character_vote where has_reward='0' AND type='1';
UPDATE character_vote SET has_reward='1' WHERE type='1';
**/
public class MMOTopManager2
{
	private static Logger _log = Logger.getLogger(MMOTopManager2.class.getName());

	private static BufferedReader reader;
	private static int vote_id=0;

	public static void main(String[] args) throws Exception
	{
		start();
	}

	public static void start() throws Exception
	{
		if(!ConfigValue.MmoTopStartWithServer2)
		{
			InputStream is = new FileInputStream(new File("./config/log.properties"));
			LogManager.getLogManager().readConfiguration(is);
			is.close();

			ConfigSystem.load();

			L2DatabaseFactory.getInstance();
			Log.InitGSLoggers();
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ConnectAndUpdate(), ConfigValue.MmoTopManagerInterval2, ConfigValue.MmoTopManagerInterval2);
		_log.info("MMOTopManager2: Start sucesfully.");
	}

	private static void getPage()
	{
		if(ConfigValue.MmoTopShowConsoleInfo2)
			_log.info("MMOTopManager2: Start load page...");
		try
		{
			URL url = new URL(ConfigValue.MmoTopWebAddress2);
			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF8"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// 27966239	
	// 01.06.2014 
	// 00:18:43	
	// 178.44.2.197	
	// Perdynec	
	// 1
	private static void parse()
	{
		try
		{
			String line;
			if(ConfigValue.MmoTopShowConsoleInfo2)
				_log.info("MMOTopManager2: Start parse page...");
			read_line : while((line = reader.readLine()) != null)
			{
				/*String[] val = line.split(" ");
				val[0]; // VoteId
				val[1]; // Data
				val[2]; // Time
				val[3]; // IP
				val[4]; // Nick
				val[5]; // Type*/

				StringTokenizer st = new StringTokenizer(line, "\t. :");
				//while(st.hasMoreTokens())
				if(st.countTokens() == 13)
					try
					{
						int vote_id_ = Integer.parseInt(st.nextToken());
						if(vote_id_ <= vote_id)
							continue read_line;
						int day = Integer.parseInt(st.nextToken());
						int month = Integer.parseInt(st.nextToken()) - 1;
						int year = Integer.parseInt(st.nextToken());
						int hour = Integer.parseInt(st.nextToken());
						int minute = Integer.parseInt(st.nextToken());
						int second = Integer.parseInt(st.nextToken());
						st.nextToken();
						st.nextToken();
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
					}
			}
		}
		catch (Exception e) 
		{
			_log.warning("MMOTopManager2: Cant store MMOTop data.");
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
			statement = con.prepareStatement("SELECT MAX(vote_id) FROM character_vote where type='10'");
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
				statement = con.prepareStatement("INSERT INTO character_vote (type, vote_id, date, id, nick, multipler, has_reward) values (10,?,?,?,?,?,1)");
				statement.setInt(1, vote_id);
				statement.setLong(2, voteTime);
				statement.setInt(3, player_id);
				statement.setString(4, charName);
				statement.setInt(5, voteType);
				statement.execute();

				DatabaseUtils.closeDatabaseSR(statement, rset);

				String insert_item = "";

				if(ConfigValue.MmoTopReward2.length >= 2)
					for(int i = 0; i < ConfigValue.MmoTopReward2.length; i += 2)
						insert_item+="("+player_id+","+ConfigValue.MmoTopReward2[i]+","+(ConfigValue.MmoTopReward2[i + 1]*voteType)+",'MMOTOP2_REWARD_"+vote_id+"')"+(ConfigValue.MmoTopReward2.length == i+2 ? "" : ",");
				else if(ConfigValue.MmoTopRewardRnd2.length >= 3)
					for(int i = 0; i < ConfigValue.MmoTopRewardRnd2.length; i += 3)
						insert_item+="("+player_id+","+ConfigValue.MmoTopRewardRnd2[i]+","+((Rnd.get(ConfigValue.MmoTopRewardRnd2[i + 1],ConfigValue.MmoTopRewardRnd2[i + 2]))*voteType)+",'MMOTOP2_REWARD_"+vote_id+"')"+(ConfigValue.MmoTopRewardRnd2.length == i+3 ? "" : ",");

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
				getPage();
			}
			finally
			{
				set_vote_id();
				parse();
				if(ConfigValue.MmoTopShowConsoleInfo2)
					_log.info("MMOTopManager2: Finish give reward...");
			}
		}
	}
}