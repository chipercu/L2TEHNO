package l2open.gameserver.instancemanager;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.net.Socket;

import java.sql.ResultSet;
import java.sql.SQLException;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.ThreadConnection;
import l2open.database.L2DatabaseFactory;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;

public class L2VoteManager 
{
	private static Logger _log = Logger.getLogger(L2VoteManager.class.getName());

	private final static String voteWeb = ConfigValue.DatapackRoot + "/data/vote-web.txt";
	private final static String voteSms = ConfigValue.DatapackRoot + "/data/vote-sms.txt";

	private static L2VoteManager _instance;

	public static L2VoteManager getInstance()
	{
		if(_instance == null && ConfigValue.L2VoteManagerEnabled)
			_instance = new L2VoteManager();
		return _instance;
	}

	public L2VoteManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ConnectAndUpdate(), ConfigValue.L2VoteManagerInterval, ConfigValue.L2VoteManagerInterval);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Clean(), ConfigValue.L2VoteManagerInterval, ConfigValue.L2VoteManagerInterval);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new GiveReward(), ConfigValue.L2VoteManagerInterval, ConfigValue.L2VoteManagerInterval);
		_log.info("L2VoteManager: loaded sucesfully");
	}

	private void update()
	{
		String out_sms = getPage(ConfigValue.L2VoteSmsAddress);
		String out_web = getPage(ConfigValue.L2VoteWebAddress);
		
		File sms = new File(voteSms);
		File web = new File(voteWeb);
		FileWriter SaveWeb = null;
		FileWriter SaveSms = null;

		try
		{
			SaveSms = new FileWriter(sms);
			SaveSms.write(out_sms);
			SaveWeb = new FileWriter(web);
			SaveWeb.write(out_web);
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	
		finally
		{
			try
			{
				if(SaveSms != null)
					SaveSms.close();
				if(SaveWeb != null)
					SaveWeb.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	private static String getPage(String address)
	{
		StringBuffer buf = new StringBuffer();
		Socket s;
		try
		{
			s = new Socket("l2vote.com", 80);

			s.setSoTimeout(30000); //Таймут 30 секунд
			String request = "GET " + address + " HTTP/1.1\r\n" + "User-Agent: http:\\" + ConfigValue.L2VoteServerAddress + " server\r\n" + "Host: http:\\" + ConfigValue.L2VoteServerAddress + " \r\n" + "Accept: */*\r\n" + "Connection: close\r\n" + "\r\n";
			s.getOutputStream().write(request.getBytes());
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "Cp1251"));

			for(String line = in.readLine(); line != null; line = in.readLine())
			{
				buf.append(line);
				buf.append("\r\n");
			}
			s.close();
		}
		catch(Exception e)
		{
			buf.append("Connection error");
		}
		return buf.toString();
	}

	private void parse(boolean sms) 
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(sms? voteSms : voteWeb));
			String line = in.readLine();
			while(line != null)
			{
				Calendar cal = Calendar.getInstance();
				int year = cal.get(Calendar.YEAR);
				
				if(line.startsWith(String.valueOf(year)))
				{
					try
					{
						StringTokenizer st = new StringTokenizer(line, "\t -:");
						cal.set(Calendar.YEAR, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.MONTH, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.SECOND, Integer.parseInt(st.nextToken()));
						cal.set(Calendar.MILLISECOND, 0);
						String server_prefix = st.nextToken().trim();
						String real_nick = server_prefix;
						if(!sms && st.hasMoreTokens())
							real_nick = st.nextToken().trim();
						else if(sms && st.countTokens() > 1)
							real_nick = st.nextToken().trim();

						int mult = 1;
						if(sms)
							mult = Integer.parseInt(new StringBuffer(st.nextToken()).delete(0, 1).toString());
						if((ConfigValue.L2VoteNamePrefix.isEmpty() || ConfigValue.L2VoteNamePrefix.equalsIgnoreCase(server_prefix)) && cal.getTimeInMillis() + ConfigValue.L2VoteSaveDays * 86400000 > System.currentTimeMillis())
							checkAndSaveFromDb(cal.getTimeInMillis(), real_nick, mult);
					}
					catch(NoSuchElementException nsee)
					{
						nsee.printStackTrace();
						continue;
					}
				}
				line = in.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private synchronized void clean()
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, - ConfigValue.L2VoteSaveDays);
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_vote WHERE date<? AND type='4'");
			statement.setLong(1, cal.getTimeInMillis());
			statement.execute();
		}	
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	private synchronized void checkAndSaveFromDb(long date, String nick, int mult)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, nick);
			rset = statement.executeQuery();
			int objId = 0;
			if(rset.next()) // чар существует и проверка успешна
			{
				objId = rset.getInt("obj_Id");
			}
			if(objId > 0)
			{
				DatabaseUtils.closeDatabaseSR(statement, rset);
				statement = con.prepareStatement("SELECT * FROM character_vote WHERE id=? AND date=? AND multipler=? AND type='4'");
				statement.setInt(1, objId);
				statement.setLong(2, date);
				statement.setInt(3, mult);
				rset = statement.executeQuery();
				if(!rset.next())
				{
					DatabaseUtils.closeDatabaseSR(statement, rset);
					statement = con.prepareStatement("INSERT INTO character_vote (type, date, id, nick, multipler) values (4,?,?,?,?)");
					statement.setLong(1, date);
					statement.setInt(2, objId);
					statement.setString(3, nick);
					statement.setInt(4, mult);
					statement.execute();
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private synchronized void giveReward()
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				int objId = player.getObjectId();
				int mult = 0;
				statement = con.prepareStatement("SELECT multipler FROM character_vote WHERE id=? AND has_reward='0' AND type='4'");
				statement.setInt(1, objId);
				rset = statement.executeQuery();
				while(rset.next())
				{
					mult += rset.getInt("multipler");
				}
				
				DatabaseUtils.closeDatabaseSR(statement, rset);
				statement = con.prepareStatement("UPDATE character_vote SET has_reward=1 WHERE id=? AND type='4'");
				statement.setInt(1, objId);
				statement.executeUpdate();
				if(mult > 0)
				{
					if(player.getVar("lang@").equalsIgnoreCase("ru"))
						player.sendMessage("Администрация сервера " + ConfigValue.L2VoteServerAddress + " благодарит вас за голосование");
					else
						player.sendMessage("The administration server " + ConfigValue.L2VoteServerAddress + " thank you for your vote");
                    for(int i=0; i < ConfigValue.L2VoteReward.length; i+=2)
                        player.getInventory().addItem((int)ConfigValue.L2VoteReward[i], ConfigValue.L2VoteReward[i+1]*mult);
				}
				DatabaseUtils.closeStatement(statement);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}

	private class ConnectAndUpdate extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			update();
			parse(true);
			parse(false);
		}
	}

	private class Clean extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			clean();
		}
	}

	private class GiveReward extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			giveReward();
		}
	}
}