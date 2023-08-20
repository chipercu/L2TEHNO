package l2open.status;

import javolution.util.FastList;
import l2open.Server;
import l2open.config.ConfigValue;
import l2open.loginserver.GameServerTable;
import l2open.loginserver.IpManager;
import l2open.loginserver.L2LoginServer;
import l2open.util.BannedIp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;

public class LoginStatusThread extends Thread
{
	private static final Logger _log = Logger.getLogger(LoginStatusThread.class.getName());

	private Socket _csocket;

	private final PrintWriter _print;
	private final BufferedReader _read;

	private boolean _redirectLogger;

	private void telnetOutput(int type, String text)
	{
		if(type == 1)
			System.out.println("LSTELNET | " + text);
		else if(type == 2)
			System.out.print("LSTELNET | " + text);
		else if(type == 3)
			System.out.print(text);
		else if(type == 4)
			System.out.println(text);
		else
			System.out.println("LSTELNET | " + text);
	}

	private boolean isValidIP(Socket client)
	{
		boolean result = false;
		InetAddress ClientIP = client.getInetAddress();

		// convert IP to String, and compare with list
		String clientStringIP = ClientIP.getHostAddress();

		telnetOutput(1, "Connection from: " + clientStringIP);

		// read and loop thru list of IPs, compare with newIP
		try
		{
			// compare
			String ipToCompare;
			for(String ip : ConfigValue.ListOfHosts.split(","))
				if(!result)
				{
					ipToCompare = InetAddress.getByName(ip).getHostAddress();
					if(clientStringIP.equals(ipToCompare))
						result = true;
				}
		}
		catch(IOException e)
		{
			telnetOutput(1, "Error: " + e);
		}

		return result;
	}

	public LoginStatusThread(Socket client, String StatusPW) throws IOException
	{
		_csocket = client;

		_print = new PrintWriter(_csocket.getOutputStream());
		_read = new BufferedReader(new InputStreamReader(_csocket.getInputStream()));

		if(isValidIP(client))
		{
			telnetOutput(1, client.getInetAddress().getHostAddress() + " accepted.");
			_print.println("Welcome To The L2 LS Telnet Session.");
			_print.flush();
			if(StatusPW == null || StatusPW.isEmpty())
				start();
			else
			{
				_print.println("Please Insert Your Password!");
				_print.print("Password: ");
				_print.flush();
				String tmpLine = _read.readLine();
				if(tmpLine == null)
				{
					_print.println("Error.");
					_print.println("Disconnected...");
					_print.flush();
					_csocket.close();
				}
				else if(tmpLine.compareTo(StatusPW) != 0)
				{
					_print.println("Incorrect Password!");
					_print.println("Disconnected...");
					_print.flush();
					_csocket.close();
				}
				else
				{
					_print.println("Password Correct!");
					_print.println("[L2LS]");
					_print.print("");
					_print.flush();
					start();
				}
			}
		}
		else
		{
			telnetOutput(1, "Connection attempt from " + client.getInetAddress().getHostAddress() + " rejected.");
			_csocket.close();
		}
	}

	@Override
	public void run()
	{
		String _usrCommand = "";
		try
		{
			while(_usrCommand.compareTo("quit") != 0 && _usrCommand.compareTo("exit") != 0)
			{
				_usrCommand = _read.readLine();
				if(_usrCommand == null)
				{
					_csocket.close();
					break;
				}
				if(_usrCommand.equals("help"))
				{
					_print.println("The following is a list of all available commands: ");
					_print.println("help                         - shows this help.");
					_print.println("status                       - displays basic server statistics.");
					_print.println("unblockip <ip>               - removes <ip> from hacking protection list.");
					_print.println("baniplist                    - display banip list.");
					_print.println("banip <ip>                   - ban <ip>.");
					_print.println("unban <ip>                   - unban <ip>.");
					_print.println("setpass <account> <password> - set new password.");
					_print.println("shutdown			         - shuts down server.");
					_print.println("restart				         - restarts the server.");
					_print.println("RedirectLogger		         - Telnet will give you some info about server in real time.");
					_print.println("quit                         - closes telnet session.");
					_print.println("");
				}
				else if(_usrCommand.equals("status") || _usrCommand.equals("s"))
					for(String str : GameServerTable.getInstance().status())
						_print.println(str);
				else if(_usrCommand.startsWith("unblock"))
					try
					{
						_usrCommand = _usrCommand.substring(8);
						if(L2LoginServer.getInstance().unblockIp(_usrCommand))
						{
							_log.warning("IP removed via LSTELNET by host: " + _csocket.getInetAddress().getHostAddress());
							_print.println("The IP " + _usrCommand + " has been removed from the hack protection list!");
						}
						else
							_print.println("IP not found in hack protection list...");
					}
					catch(StringIndexOutOfBoundsException e)
					{
						_print.println("Please Enter the IP to Unblock!");
					}
				else if(_usrCommand.equals("baniplist"))
					try
					{
						FastList<BannedIp> baniplist = IpManager.getInstance().getBanList();
						_print.println("Ban IP List:");
						for(BannedIp temp : baniplist)
							_print.println("Ip:" + temp.ip + " banned by " + temp.admin);
						_print.flush();
					}
					catch(StringIndexOutOfBoundsException e)
					{
						_print.println("Please enter ip to ban");
					}
				else if(_usrCommand.startsWith("banip"))
					try
					{
						_usrCommand = _usrCommand.substring(6);
						IpManager.getInstance().BanIp(_usrCommand, "Telnet: " + _csocket.getInetAddress().getHostAddress(), 0, "");
					}
					catch(StringIndexOutOfBoundsException e)
					{
						_print.println("Please enter ip to unban");
					}
				else if(_usrCommand.startsWith("unbanip"))
					try
					{
						_usrCommand = _usrCommand.substring(8);
						IpManager.getInstance().UnbanIp(_usrCommand);
					}
					catch(StringIndexOutOfBoundsException e)
					{
						_print.println("Please enter ip to unban");
					}
				else if(_usrCommand.startsWith("shutdown"))
				{
					System.out.println("Shutdowning from telnet @ " + _csocket.getInetAddress().getHostAddress());
					_print.println("Bye Bye!");
					_print.flush();
					_csocket.close();
					Server.exit(0, "Shutdowning from telnet @ " + _csocket.getInetAddress().getHostAddress());
				}
				else if(_usrCommand.startsWith("restart"))
				{
					System.out.println("Restarting from telnet @ " + _csocket.getInetAddress().getHostAddress());
					_print.println("Bye Bye!");
					_print.flush();
					_csocket.close();
					Server.exit(2, "Restarting from telnet @ " + _csocket.getInetAddress().getHostAddress());
				}
				else if(_usrCommand.startsWith("setpass"))
					try
					{
						_usrCommand = _usrCommand.substring(8);
						String[] data = _usrCommand.split(" ");
						boolean result = L2LoginServer.getInstance().setPassword(data[0], data[1]);
						if(result)
							_print.print("Password successfully changed.");
						else
							_print.print("Error while set new password.");
					}
					catch(StringIndexOutOfBoundsException e)
					{
						_print.println("Usage: setpass <account> <password>");
					}
				else if(_usrCommand.equals("RedirectLogger"))
					_redirectLogger = true;
				else if(_usrCommand.equals("quit"))
				{ /* Do Nothing :p - Just here to save us from the "Command Not Understood" Text */}
				else if(_usrCommand.length() == 0)
				{ /* Do Nothing Again - Same reason as the quit part */}
				else
					_print.println("Invalid Command");
				_print.print("");
				_print.flush();
			}
			if(!_csocket.isClosed())
			{
				_print.println("Bye Bye!");
				_print.flush();
				_csocket.close();
			}
			telnetOutput(1, "Connection from " + _csocket.getInetAddress().getHostAddress() + " was closed by client.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void printToTelnet(String msg)
	{
		synchronized (_print)
		{
			_print.println(msg);
			_print.flush();
		}
	}

	/**
	 * @return Returns the redirectLogger.
	 */
	public boolean isRedirectLogger()
	{
		return _redirectLogger;
	}
}
