package l2open.util;

import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogChat
{
	private static final Logger _logChat = Logger.getLogger("chat");
	private static final Logger _logMail = Logger.getLogger("mail_chat");
	private static final Logger _log = Logger.getLogger(LogChat.class.getName());

	public static void add(String text, String type, String from, String to)
	{
		if(ConfigValue.LogChat)
		{
			LogRecord record = new LogRecord(Level.INFO, text);
			record.setLoggerName("chat");
			if(to != null && !to.isEmpty())
				record.setParameters(new Object[] { type, "[" + from + " to " + to + "]" });
			else
				record.setParameters(new Object[] { type, "[" + from + "]" });
			_logChat.log(record);
		}

		if(ConfigValue.LogChatDB != null && !ConfigValue.LogChatDB.isEmpty())
		{
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT DELAYED INTO " + ConfigValue.LogChatDB + " (`type`,`text`,`from`,`to`) VALUES(?,?,?,?);");
				statement.setString(1, type.trim());
				statement.setString(2, text);
				statement.setString(3, from);
				statement.setString(4, to != null ? to : "");
				statement.execute();
			}
			catch(Exception e)
			{
				_log.warning("fail to sql log chat[" + type + "|" + text + "|" + from + "|" + to + "]: " + e);
				e.printStackTrace();
			}
			finally
			{
				DatabaseUtils.closeDatabaseCS(con, statement);
			}
		}
	}

	public static void addMail(String text, String from, String to)
	{
		if(ConfigValue.LogChat)
		{
			LogRecord record = new LogRecord(Level.INFO, text);
			record.setLoggerName("mail_chat");
			if(to != null && !to.isEmpty())
				record.setParameters(new Object[] { "[" + from + " to " + to + "]" });
			else
				record.setParameters(new Object[] { "[" + from + "]" });
			_logMail.log(record);
		}
	}
}