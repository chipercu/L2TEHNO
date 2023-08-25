package l2open;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MainLogFormatter extends Formatter
{
	private static final String CRLF = "\r\n";

	@Override
	public String format(LogRecord record)
	{
		StringBuffer output = new StringBuffer();
		output.append(record.getMessage());
		output.append(CRLF);
		return output.toString();
	}
}
