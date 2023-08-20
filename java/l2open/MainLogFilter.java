package l2open;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class MainLogFilter implements Filter
{
	@Override
	public boolean isLoggable(LogRecord record)
	{
		return record.getLoggerName().equals("mainlog");
	}
}
