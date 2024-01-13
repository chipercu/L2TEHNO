package l2open;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.1 $ $Date: 2005/03/27 15:30:08 $
 */

public class FileLogFormatter extends Formatter {

    /* (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    private static final String CRLF = "\r\n";
    private static final String tab = "\t";

    @Override
    public String format(LogRecord record) {
        return record.getMillis() +
                tab +
                record.getLevel().getName() +
                tab +
                record.getThreadID() +
                tab +
                record.getLoggerName() +
                tab +
                record.getMessage() +
                CRLF;
    }
}
