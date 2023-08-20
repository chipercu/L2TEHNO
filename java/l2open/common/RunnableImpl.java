package l2open.common;

import l2open.config.ConfigValue;
import l2open.util.Log;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author VISTALL
 * @date 19:13/04.04.2011
 */
public abstract class RunnableImpl implements Runnable
{
	public static final Logger _log = Logger.getLogger(RunnableImpl.class.getName());

	public abstract void runImpl() throws Exception;

	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(ConfigValue.RunnableLog)
				Log.logTrace2(e, "Exception: RunnableImpl.run", "RunnableImpl", "RunnableImpl");
				//_log.log(Level.WARNING, "Exception: RunnableImpl.run("+_log.getLevel()+"): ", e);
		}
	}
}
