package l2open;

import l2open.config.*;
import l2open.database.L2DatabaseFactory;
import l2open.gameserver.GameServer;
import l2open.loginserver.L2LoginServer;
import l2open.status.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This class ...
 *
 * @version $Revision: 1.5.4.9 $ $Date: 2005/03/29 23:15:33 $
 */
/***********************************************
 * This class appears to be the starter class. The class the calls upon the
 * login and game server classes. It's purpose is to initiate the log file
 * and start the servers
 * *********************************************
 */
public class Server
{
	private static final Logger _log = Logger.getLogger(Server.class.getName());

	public static final int MODE_GAMESERVER = 1;
	public static final int MODE_LOGINSERVER = 2;
	public static final int MODE_COMBOSERVER = 3;
	public static int SERVER_MODE = 0;
	// Global class instantiations. Used to actually ready the classes
	public static GameServer gameServer;
	public static L2LoginServer loginServer;
	public static Status statusServer;
	public static String PhoenixHomeDir = System.getProperty("user.dir") + "/";

	// Main Method
	public static void main(String[] args) throws Exception
	{
		String PhoenixHome = System.getProperty("Rebellion.home");
		if(PhoenixHome != null)
		{
			File home = new File(PhoenixHome);
			if(!home.isAbsolute())
				try
				{
					PhoenixHome = home.getCanonicalPath();
				}
				catch(IOException e)
				{
					PhoenixHome = home.getAbsolutePath();
				}
			PhoenixHomeDir = PhoenixHome + "/";
			System.setProperty("user.dir", PhoenixHome);
		}

		// Local Constants
		final String LOG_FOLDER = "log"; // Name of folder for log file
		final String LOG_NAME = "/config/log.properties"; // Name of log file
		final String CONF_LOC = "l2open.Config"; // Location of configuration data

		/*** Main ***/
		// Initialize config
		Class.forName(CONF_LOC);

		// Create log folder
		File logFolder = new File("./", LOG_FOLDER);
		logFolder.mkdir();

		// Create input stream for log file -- or store file data into memory
		InputStream is = new FileInputStream(PhoenixHomeDir + LOG_NAME);
		LogManager.getLogManager().readConfiguration(is);
		is.close();
        ConfigSystem.load();

		L2DatabaseFactory.getInstance();

		// Run server threads
		gameServer = new GameServer();
		loginServer = L2LoginServer.getInstance();
		//loginServer = new LoginServer();
		//loginServer.start();

		// run telnet server if enabled

		if(ConfigValue.MAT_BANCHAT)
			_log.warning("MAT AutoBANChat filter enable.");
		else
			_log.warning("MAT AutoBANChat filter disable.");
	}

	public static void exit(int status, String reason)
	{
		_log.warning("Server exiting [status=" + status + "] / Reason: " + reason);
		Runtime.getRuntime().exit(status);
	}

	public static void halt(int status, String reason)
	{
		_log.warning("Server halting [status=" + status + "] / Reason: " + reason);
		Runtime.getRuntime().halt(status);
	}
}