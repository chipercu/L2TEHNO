package l2open.common;

import l2open.config.ConfigValue;
import java.util.logging.Logger;

/**
 * @author VISTALL
 * @date 19:13/04.04.2011
 */
public abstract class RunnableImpl2 implements Runnable
//public abstract interface RunnableImpl2 implements Runnable
{
	public static final Logger _log = Logger.getLogger(RunnableImpl2.class.getName());

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
			if(ConfigValue.RunnableLog)
				_log.warning("Exception: RunnableImpl2.run(): " + e);
		}
	}
}
