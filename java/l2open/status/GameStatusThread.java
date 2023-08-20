package l2open.status;

import l2open.config.ConfigValue;
import l2open.status.gshandlers.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Logger;

public class GameStatusThread extends Thread
{
	private static final Logger _log = Logger.getLogger(GameStatusThread.class.getName());

	public boolean LogChat = false, LogTell = false;

	public GameStatusThread next;
	private Socket _csocket;
	private PrintWriter _print;
	private BufferedReader _read;

	private void telnetOutput(int type, String text)
	{
		if(type == 1)
			_log.fine("GSTELNET | " + text);
		else if(type == 2)
			_log.fine("GSTELNET | " + text);
		else if(type == 3)
			_log.fine(text);
		else if(type == 4)
			_log.fine(text);
		else
			_log.fine("GSTELNET | " + text);
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

	private boolean changeEncoding(String charsetName)
	{
		if(charsetName == null || charsetName.isEmpty() || charsetName.equalsIgnoreCase("utf8") || charsetName.equalsIgnoreCase("utf-8"))
		{
			try
			{
				_read = new BufferedReader(new InputStreamReader(_csocket.getInputStream()));
				_print = new PrintWriter(_csocket.getOutputStream(), false);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return true;
		}

		try
		{
			"проверка".getBytes(charsetName);
		}
		catch(UnsupportedEncodingException e)
		{
			return false;
		}

		try
		{
			_read = new BufferedReader(new InputStreamReader(_csocket.getInputStream(), charsetName));
			_print = new PrintWriter(new OutputStreamWriter(_csocket.getOutputStream(), charsetName), false);
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private String readLine() throws IOException
	{
		return _read.readLine();
	}

	public GameStatusThread(Socket client, String StatusPW) throws IOException
	{
		_csocket = client;

		_print = new PrintWriter(_csocket.getOutputStream(), false);
		_read = new BufferedReader(new InputStreamReader(_csocket.getInputStream()));

		if(isValidIP(client))
		{
			telnetOutput(1, client.getInetAddress().getHostAddress() + " accepted.");
			_print.println("Welcome To The L2 GS Telnet Session.");
			if(StatusPW != null && !StatusPW.equalsIgnoreCase(""))
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
					_print.println();
					_print.flush();
					start();
				}
			}
			else
				start();
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
		next = Status.telnetlist;
		Status.telnetlist = this;

		String cmd;
		String[] argv;
		try
		{
			for(;;)
			{
				_print.print("l2pgs> ");
				_print.flush();
				cmd = readLine();

				if(cmd == null)
				{
					_csocket.close();
					break;
				}

				argv = cmd.split(" ");

				try
				{
					if(argv == null || argv.length == 0 || argv[0].isEmpty())
					{ /* do nothing */}
					else if(argv[0].equalsIgnoreCase("?") || argv[0].equalsIgnoreCase("h") || argv[0].equalsIgnoreCase("help"))
						Help(_print);
					else if(argv[0].equalsIgnoreCase("encoding") || argv[0].equalsIgnoreCase("charset") || argv[0].equalsIgnoreCase("enc"))
					{
						if(argv.length < 2 || argv[1] == null || argv[1].isEmpty() || argv[1].equalsIgnoreCase("?"))
							_print.println("USAGE: charset utf8|cp1251|cp866...");
						else if(changeEncoding(argv[1]))
							_print.println("This console charset changed to: " + argv[1]);
						else
							_print.println("Fail to set this console charset: " + argv[1]);
					}

					else if(argv[0].equalsIgnoreCase("status") || argv[0].equalsIgnoreCase("s"))
						HandlerStatus.Status(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("database") || argv[0].equalsIgnoreCase("db"))
						HandlerStatus.Database(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("gmlist"))
						HandlerStatus.GmList(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("ver") || argv[0].equalsIgnoreCase("version"))
						HandlerStatus.Version(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("cfg") || argv[0].equalsIgnoreCase("config"))
						HandlerStatus.Config(cmd, argv, _print);

					else if(argv[0].equalsIgnoreCase("lazyitems"))
						HandlerPerfomance.LazyItems(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("performance") || argv[0].equalsIgnoreCase("p"))
						HandlerPerfomance.ThreadPool(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("garbage") || argv[0].equalsIgnoreCase("gc"))
						HandlerPerfomance.GC(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("give"))
						HandlerPerfomance.GiveItem(cmd, argv, _print);

					else if(argv[0].equalsIgnoreCase("stat"))
						HandlerStats.Stats(cmd, argv, _print);

					else if(argv[0].equalsIgnoreCase("announce") || argv[0].equalsIgnoreCase("!"))
						HandlerSay.Announce(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("message") || argv[0].equalsIgnoreCase("msg"))
						HandlerSay.Message(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("gmchat"))
						HandlerSay.GmChat(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("tell"))
						HandlerSay.TelnetTell(cmd, argv, _print, _csocket);

					else if(argv[0].equalsIgnoreCase("baniplist") || argv[0].equalsIgnoreCase("banip"))
						HandlerBan.BanIP(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("unbanip"))
						HandlerBan.UnBanIP(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("kick"))
						HandlerBan.Kick(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("nochannel") || argv[0].equalsIgnoreCase("nc"))
						HandlerNoChannel.BanChat(cmd, argv, _print);

					else if(argv[0].equalsIgnoreCase("whois"))
						HandlerWorld.Whois(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("enemy"))
						HandlerWorld.ListEnemy(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("reload"))
						HandlerWorld.Reload(cmd, argv, _print);
					else if(argv[0].equalsIgnoreCase("shutdown"))
						HandlerWorld.Shutdown(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("restart"))
						HandlerWorld.Restart(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("abort") || argv[0].equalsIgnoreCase("a"))
						HandlerWorld.AbortShutdown(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("stopLogin"))
						HandlerWorld.StopLogin(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("reloadLogin"))
						HandlerWorld.ReloadLogin(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("debug"))
						HandlerDebug.Debug(cmd, argv, _print, _csocket);
					else if(argv[0].equalsIgnoreCase("dumpmem") || argv[0].equalsIgnoreCase("memdump"))
						HandlerDebug.HprofMemDump(cmd, argv, _print);

					else if(argv[0].equalsIgnoreCase("log_chat"))
					{
						LogChat = !LogChat;
						_print.println("Log chat is turned " + (LogChat ? "on" : "off"));
					}
					else if(argv[0].equalsIgnoreCase("log_tell"))
					{
						LogTell = !LogTell;
						_print.println("Log tell is turned " + (LogTell ? "on" : "off"));
					}
					else if(argv[0].equalsIgnoreCase("quit") || argv[0].equalsIgnoreCase("q") || argv[0].equalsIgnoreCase("exit"))
					{
						_print.println("Bye Bye!");
						_print.flush();
						_csocket.close();
						break;
					}
					else
						_print.println("Unknown command: " + argv[0]);
				}
				catch(Exception e)
				{
					e.printStackTrace(_print);
				}

				_print.println();
				_print.flush();
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			} /* end for */

			telnetOutput(1, "Connection from " + _csocket.getInetAddress().getHostAddress() + " was closed by client.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		//      begin clean telnetlist
		if(this == Status.telnetlist)
			Status.telnetlist = next;
		else
		{
			GameStatusThread temp = Status.telnetlist;
			while(temp.next != this)
				temp = temp.next;
			temp.next = next;
		}
		// finished clean telnetlist
	}

	private static void Help(PrintWriter _print)
	{
		_print.println("encoding(enc,charset)");
		_print.println("version(ver)");
		_print.println("config(cfg)");
		_print.println("status(s)");
		_print.println("database(db)");
		_print.println("lazyitems");
		_print.println("performance(p)");
		_print.println("announce(!)");
		_print.println("message(msg)");
		_print.println("gmlist");
		_print.println("gmchat");
		_print.println("tell");
		_print.println("whois");
		_print.println("enemy");
		_print.println("nochannel(nc)");
		_print.println("baniplist(banip)");
		_print.println("unbanip");
		_print.println("banhwid");
		_print.println("unbanhwid");
		_print.println("kick");
		_print.println("reload");
		_print.println("shutdown");
		_print.println("restart");
		_print.println("abort(a)");
		_print.println("stopLogin");
		_print.println("garbage(gc)");
		_print.println("log_chat");
		_print.println("log_tell");
		_print.println("dumpmem");
		_print.println("exit, quit(q)");
	}

	public void write(String _text)
	{
		_print.println(_text);
		_print.flush();
	}
}