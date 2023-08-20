package commands.admin;

import java.util.StringTokenizer;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.util.Location;

@SuppressWarnings("unused")
public class AdminMenu implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_char_manage,
		admin_teleport_character_to_menu,
		admin_recall_char_menu,
		admin_goto_char_menu,
		admin_kick_menu,
		admin_kill_menu,
		admin_ban_menu,
		admin_unban_menu
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Menu)
			return false;

		if(fullString.startsWith("admin_teleport_character_to_menu"))
		{
			String[] data = fullString.split(" ");
			if(data.length == 5)
			{
				String playerName = data[1];
				L2Player player = L2World.getPlayer(playerName);
				if(player != null)
					teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), activeChar, 0);
				if(player.isBot())
				{
					AdminEditChar.showCharacterList(activeChar, player);
					return true;
				}
			}
		}
		else if(fullString.startsWith("admin_recall_char_menu"))
			try
			{
				String targetName = fullString.substring(23);
				L2Player player = L2World.getPlayer(targetName);
				teleportCharacter(player, activeChar.getLoc(), activeChar, activeChar.getReflection().getId());
				if(player.isBot())
				{
					AdminEditChar.showCharacterList(activeChar, player);
					return true;
				}
			}
			catch(StringIndexOutOfBoundsException e)
			{}
		else if(fullString.startsWith("admin_goto_char_menu"))
			try
			{
				String targetName = fullString.substring(21);
				L2Player player = L2World.getPlayer(targetName);
				teleportToCharacter(activeChar, player);
				if(player.isBot())
				{
					AdminEditChar.showCharacterList(activeChar, player);
					return true;
				}
			}
			catch(StringIndexOutOfBoundsException e)
			{}
		else if(fullString.equals("admin_kill_menu"))
		{
			L2Object obj = activeChar.getTarget();
			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2Player plyr = L2World.getPlayer(player);
				if(plyr != null)
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
				else
					activeChar.sendMessage("Player " + player + " not found in game.");
				obj = plyr;
			}
			if(obj != null && obj.isCharacter())
			{
				L2Character target = (L2Character) obj;
				target.reduceCurrentHp(target.getMaxHp() + 1, activeChar, null, true, true, true, false, true, target.getMaxHp() + 1, true, false, false, false);
			}
			else
				activeChar.sendPacket(Msg.INVALID_TARGET);
		}
		else if(fullString.startsWith("admin_kick_menu"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2Player plyr = L2World.getPlayer(player);
				if(plyr != null)
					plyr.logout(false, false, true, true);
				if(plyr != null)
					activeChar.sendMessage("You kicked " + plyr.getName() + " from the game.");
				else
					activeChar.sendMessage("Player " + player + " not found in game.");
			}
		}
		else if(fullString.startsWith("admin_ban_menu"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2Player plyr = L2World.getPlayer(player);
				if(plyr != null)
				{
					plyr.setAccountAccesslevel(-100, "admin_ban_menu", -1);
					plyr.logout(false, false, true, true);
				}
			}
		}
		else if(fullString.startsWith("admin_unban_menu"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			if(st.countTokens() > 1)
			{
				st.nextToken();
				String player = st.nextToken();
				L2Player plyr = L2World.getPlayer(player);
				if(plyr != null)
					plyr.setAccountAccesslevel(0, "admin_unban_menu", 0);
			}
		}

		activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/charmanage.htm"));
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void teleportCharacter(L2Player player, Location loc, L2Player activeChar, int ref)
	{
		if(player != null)
		{
			player.sendMessage("Admin is teleporting you.");
			player.teleToLocation(loc, ref);
		}
	}

	private void teleportToCharacter(L2Player activeChar, L2Object target)
	{
		L2Player player;
		if(target != null && target.isPlayer())
			player = (L2Player) target;
		else
		{
			activeChar.sendPacket(Msg.INVALID_TARGET);
			return;
		}

		if(player.getObjectId() == activeChar.getObjectId())
			activeChar.sendMessage("You cannot self teleport.");
		else
		{
			activeChar.teleToLocation(player.getLoc(), player.getReflection().getId());
			activeChar.sendMessage("You have teleported to character " + player.getName() + ".");
		}
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}