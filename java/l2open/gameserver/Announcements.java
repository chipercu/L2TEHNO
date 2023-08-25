package l2open.gameserver;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.Say2;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.MapRegion;
import l2open.util.GArray;

import java.io.*;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Announcements
{
	private static Logger _log = Logger.getLogger(Announcements.class.getName());

	private static final Announcements _instance = new Announcements();
	private GArray<String> _announcements = new GArray<String>();
	private static final int _type = Say2C.ANNOUNCEMENT;

	private Announcements()
	{
		loadAnnouncements();
	}

	public static Announcements getInstance()
	{
		return _instance;
	}

	public void loadAnnouncements()
	{
		_announcements.clear();
		File file = new File("./", "config/announcements.txt");
		if(file.exists())
			readFromDisk(file);
		else
			_log.info("config/announcements.txt doesn't exist");
	}

	public void showAnnouncements(L2Player activeChar)
	{
		for(String _announcement : _announcements)
		{
			Say2 cs = new Say2(0, _type, activeChar.getName(), _announcement);
			activeChar.sendPacket(cs);
		}
	}

	public void listAnnouncements(L2Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuffer replyMSG = new StringBuffer("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Announcement Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Add or announce a new announcement:</center>");
		replyMSG.append("<center><multiedit var=\"new_announcement\" width=240 height=30></center><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"Add\" action=\"bypass -h admin_add_announcement $new_announcement\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Announce\" action=\"bypass -h admin_announce_menu $new_announcement\" width=64 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Reload\" action=\"bypass -h admin_reload_announcements\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Broadcast\" action=\"bypass -h admin_announce_announcements\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td></tr></table></center>");
		replyMSG.append("<br>");
		for(int i = 0; i < _announcements.size(); i++)
		{
			replyMSG.append("<table width=260><tr><td width=220>" + _announcements.get(i) + "</td><td width=40>");
			replyMSG.append("<button value=\"Delete\" action=\"bypass -h admin_del_announcement " + i + "\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		}
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public void addAnnouncement(String text)
	{
		_announcements.add(text);
		saveToDisk();
	}

	public void delAnnouncement(int line)
	{
		_announcements.remove(line);
		saveToDisk();
	}

	private void readFromDisk(File file)
	{
		LineNumberReader lnr = null;
		try
		{
			String line;
			lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			while((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				if(st.hasMoreTokens())
				{
					String announcement = st.nextToken().trim();
					if(announcement.length() > 1)
						_announcements.add(announcement);
				}
			}
		}
		catch(IOException e1)
		{
			_log.log(Level.SEVERE, "Error reading announcements", e1);
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e2)
			{
				// nothing
			}
		}
	}

	private void saveToDisk()
	{
		File file = new File("config/announcements.txt");
		FileWriter save = null;

		try
		{
			save = new FileWriter(file);
			for(String _announcement : _announcements)
			{
				save.write(_announcement);
				save.write("\r\n");
			}
		}
		catch(IOException e)
		{
			_log.warning("saving the announcements file has failed: " + e);
		}
		finally
		{
			try
			{
				if(save != null)
					save.close();
			}
			catch(Exception e1)
			{
				// nothing
			}
		}
	}

	public void announceToAll(String text)
	{
		announceToAll(text, _type);
	}

	public static void shout(L2Character activeChar, String text, int type)
	{
		Say2 cs = new Say2(activeChar.getObjectId(), type, activeChar.getName(), text);
		int mapregion = MapRegion.getInstance().getMapRegion(activeChar.getX(), activeChar.getY());
		if(ConfigValue.ShoutChatMode == 1)
			for(L2Player player : L2World.getAroundPlayers(activeChar))
				player.sendPacket(cs);
		else
			for(L2Player player : L2ObjectsStorage.getPlayers())
				if(MapRegion.getInstance().getMapRegion(player.getX(), player.getY()) == mapregion && player != activeChar)
					player.sendPacket(cs);
		activeChar.sendPacket(cs);
	}

	public void announceToAll(String text, int type)
	{
		Say2 cs = new Say2(0, type, "", text);
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.sendPacket(cs);
	}

	public void announceToAllC(String text)
	{
		Say2 cs = null; 
		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			cs = new Say2(0, _type, "", new CustomMessage(text, player).toString());
			player.sendPacket(cs);
		}
	}

	public void announceToAllC(String text, String add, String add2)
	{
		Say2 cs = null; 
		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			cs = new Say2(0, _type, "", new CustomMessage(text, player).addString(add).addString(add2).toString());
			player.sendPacket(cs);
		}
	}

	/**
	 * Отправляет анонсом CustomMessage, приминимо к примеру в шатдауне.
	 * @param address адрес в {@link l2open.extensions.multilang.CustomMessage}
	 * @param replacements массив String-ов которые атоматически добавятся в сообщения
	 */
	public void announceByCustomMessage(String address, String[] replacements)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			announceToPlayerByCustomMessage(player, address, replacements);
	}

	public void announceByCustomMessage(String address, String[] replacements, int type)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			announceToPlayerByCustomMessage(player, address, replacements, type);
	}

	public void announceToPlayerByCustomMessage(L2Player player, String address, String[] replacements)
	{
		CustomMessage cm = new CustomMessage(address, player);
		if(replacements != null)
			for(String s : replacements)
				cm.addString(s);
		player.sendPacket(new Say2(0, _type, "", cm.toString()));
	}

	public void announceToPlayerByCustomMessage(L2Player player, String address, String[] replacements, int type)
	{
		CustomMessage cm = new CustomMessage(address, player);
		if(replacements != null)
			for(String s : replacements)
				cm.addString(s);
		player.sendPacket(new Say2(0, type, "", cm.toString()));
	}

	public void announceToAll(SystemMessage sm)
	{
		for(L2Player player : L2ObjectsStorage.getPlayers())
			player.sendPacket(sm);
	}

	public void handleAnnounce(String command, int lengthToTrim)
	{
		handleAnnounce(command, lengthToTrim, _type);
	}

	// Method for handling announcements from admin
	public void handleAnnounce(String command, int lengthToTrim, int type)
	{
		try
		{
			// Announce string to everyone on server
			String text = command.substring(lengthToTrim);
			Announcements.getInstance().announceToAll(text, type);
		}

		// No body cares!
		catch(StringIndexOutOfBoundsException e)
		{
			// empty message.. ignore
		}
	}
}