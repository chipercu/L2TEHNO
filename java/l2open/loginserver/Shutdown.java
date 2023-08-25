package l2open.loginserver;

import l2open.Server;

import java.util.logging.Logger;

@SuppressWarnings( { "nls", "unqualified-field-access", "boxing" })
public class Shutdown extends Thread
{
	private static final Logger _log = Logger.getLogger(Shutdown.class.getName());

	private static Shutdown _instance;
	private static Shutdown _counterInstance = null;

	private int secondsShut;
	private int shutdownMode;

	public static final int SIGTERM = 0;
	public static final int GM_SHUTDOWN = 1;
	public static final int GM_RESTART = 2;
	public static final int ABORT = 3;
	private static String[] _modeText = { "brought down", "brought down", "restarting", "aborting" };

	public int get_seconds()
	{
		if(_counterInstance != null)
			return _counterInstance.secondsShut;
		return -1;
	}

	public int get_mode()
	{
		if(_counterInstance != null)
			return _counterInstance.shutdownMode;
		return -1;
	}

	/**
	 * This function starts a shutdown countdown from Telnet (Copied from Function startShutdown())
	 *
	 * @param ip		    IP Which Issued shutdown command
	 * @param seconds	   seconds untill shutdown
	 * @param restart	   true if the server will restart after shutdown
	 */
	public void startShutdownH(int hours, boolean restart)
	{
		if(hours < 0)
			return;

		if(_counterInstance != null)
			_counterInstance._abort();

		_counterInstance = new Shutdown(hours * 60, restart);
		_counterInstance.start();
	}

	/**
	 * Default constucter is only used internal to create the shutdown-hook instance
	 *
	 */
	public Shutdown()
	{
		secondsShut = -1;
		shutdownMode = SIGTERM;
	}

	/**
	 * This creates a countdown instance of Shutdown.
	 *
	 * @param seconds	how many seconds until shutdown
	 * @param restart	true is the server shall restart after shutdown
	 *
	 */
	public Shutdown(int seconds, boolean restart)
	{
		if(seconds < 0)
			seconds = 0;
		secondsShut = seconds;

		_log.info("Restarting in " + secondsShut + " sec.");

		if(restart)
			shutdownMode = GM_RESTART;
		else
			shutdownMode = GM_SHUTDOWN;
	}

	/**
	 * get the shutdown-hook instance
	 * the shutdown-hook instance is created by the first call of this function,
	 * but it has to be registrered externaly.
	 *
	 * @return instance of Shutdown, to be used as shutdown hook
	 */
	public static Shutdown getInstance()
	{
		if(_instance == null)
			_instance = new Shutdown();
		return _instance;
	}

	/**
	 * this function is called, when a new thread starts
	 *
	 * if this thread is the thread of getInstance, then this is the shutdown hook
	 * and we save all data and disconnect all clients.
	 *
	 * after this thread ends, the server will completely exit
	 *
	 * if this is not the thread of getInstance, then this is a countdown thread.
	 * we start the countdown, and when we finished it, and it was not aborted,
	 * we tell the shutdown-hook why we call exit, and then call exit
	 *
	 * when the exit status of the server is 1, startServer.sh / startServer.bat
	 * will restart the server.
	 *
	 */
	@Override
	public void run()
	{
		if(this == _instance)
		{
			// server will quit, when this function ends.
			Server.halt(_instance.shutdownMode == GM_RESTART ? 2 : 0, "LS shutdown");
		}
		else
		{
			// gm shutdown: send warnings and then call exit to start shutdown sequence
			countdown();
			// last point where logging is operational :(
			_log.info("Shutdown countdown is over. " + _modeText[shutdownMode] + " NOW!");
			switch(shutdownMode)
			{
				case GM_SHUTDOWN:
					_instance.setMode(GM_SHUTDOWN);
					Server.exit(0, "GM_SHUTDOWN");
					break;
				case GM_RESTART:
					_instance.setMode(GM_RESTART);
					Server.exit(2, "GM_RESTART");
					break;
			}
		}
	}

	/**
	 * This functions starts a shutdown countdown
	 *
	 * @param seconds		seconds until shutdown
	 * @param restart		true if the server will restart after shutdown
	 */
	public void startShutdown(int seconds, boolean restart)
	{
		if(_counterInstance != null)
			_counterInstance._abort();

		//		 the main instance should only run for shutdown hook, so we start a new instance
		_counterInstance = new Shutdown(seconds, restart);
		_counterInstance.start();
	}

	/**
	 * this counts the countdown and reports it to all players
	 * countdown is aborted if mode changes to ABORT
	 */
	private void countdown()
	{
		try
		{
			while(secondsShut > 0)
			{
				secondsShut--;

				int delay = 1000; //milliseconds
				Thread.sleep(delay);

				if(shutdownMode == ABORT)
					break;
			}
		}
		catch(InterruptedException e)
		{
			//this will never happen
		}
	}

	/**
	 * set the shutdown mode
	 * @param mode	what mode shall be set
	 */
	private void setMode(int mode)
	{
		shutdownMode = mode;
	}

	/**
	 * set shutdown mode to ABORT
	 *
	 */
	private void _abort()
	{
		shutdownMode = ABORT;
	}

}
