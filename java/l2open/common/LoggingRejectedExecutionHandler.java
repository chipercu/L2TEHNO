package l2open.common;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * @author NB4L1
 */
public final class LoggingRejectedExecutionHandler implements RejectedExecutionHandler
{
	private static final Logger _log = Logger.getLogger(LoggingRejectedExecutionHandler.class.getName());

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
	{
		if(executor.isShutdown())
			return;

		_log.warning(r + " from " + executor);
	}
}
