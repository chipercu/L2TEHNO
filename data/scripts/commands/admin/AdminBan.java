package commands.admin;

import java.io.*;
import java.util.*;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.ChangeAccessLevel;
import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.L2ObjectTasks.TeleportTask;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.AutoBan;
import l2open.util.Location;
import l2open.util.Log;

public class AdminBan implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_ban,
		admin_unban,
		admin_chatban,
		admin_ckarma,
		admin_cban,
		admin_chatunban,
		admin_acc_ban,
		admin_acc_unban,
		admin_trade_ban,
		admin_trade_unban,
		admin_jail,
		admin_unjail,
		admin_hban
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		StringTokenizer st = new StringTokenizer(fullString);

		if(activeChar.getPlayerAccess().CanTradeBanUnban)
			switch(command)
			{
				case admin_trade_ban:
					return tradeBan(st, activeChar);
				case admin_trade_unban:
					return tradeUnban(st, activeChar);
			}

		if(activeChar.getPlayerAccess().CanBan)
			switch(command)
			{
				case admin_ban:
					ban(st, activeChar);
					break;
				case admin_acc_ban:
					if(st.countTokens() > 1)
					{
						st.nextToken();
						int time = 0;
						String reason = "command by " + activeChar.getName();
						String account = st.nextToken();
						if(account.equals("$target"))
						{
							if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
								return false;
							account = ((L2Player) activeChar.getTarget()).getAccountName();
						}
						if(st.hasMoreTokens())
							time = Integer.parseInt(st.nextToken());
						if(st.hasMoreTokens())
							reason = activeChar.getName() + ": " + st.nextToken();
						activeChar.sendMessage("You banned " + account + ", reason: " + reason);
						L2Player tokick = null;
						for(L2Player p : L2ObjectsStorage.getPlayers())
							if(p.getAccountName().equalsIgnoreCase(account))
							{
								tokick = p;
								break;
							}
						LSConnection.getInstance(tokick != null ? tokick.getNetConnection().getLSId() : activeChar.getNetConnection().getLSId()).sendPacket(new ChangeAccessLevel(account, -100, reason, time));
						if(tokick != null)
						{
							tokick.logout(false, false, true, true);
							activeChar.sendMessage("Player " + tokick.getName() + " kicked.");
						}
					}
					break;
				case admin_trade_ban:
					return tradeBan(st, activeChar);
				case admin_trade_unban:
					return tradeUnban(st, activeChar);
				case admin_chatban:
					try
					{
						st.nextToken();
						String player = st.nextToken();
						String srok = st.nextToken();
						String bmsg = "admin_chatban " + player + " " + srok + " ";
						String msg = fullString.substring(bmsg.length(), fullString.length());

						if(AutoBan.ChatBan(player, Integer.parseInt(srok), msg, activeChar.getName()))
							activeChar.sendMessage("You ban chat for " + player + ".");
						else
							activeChar.sendMessage("Can't find char " + player + ".");
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //chatban char_name period reason");
						//e.printStackTrace();
					}
					break;
				case admin_chatunban:
					try
					{
						st.nextToken();
						String player = st.nextToken();

						if(AutoBan.ChatUnBan(player, activeChar.getName()))
							activeChar.sendMessage("You unban chat for " + player + ".");
						else
							activeChar.sendMessage("Can't find char " + player + ".");
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //chatunban char_name");
						e.printStackTrace();
					}
					break;
				case admin_jail:
					try
					{
						int srok;
						L2Player target;
						String player_name;
						if(wordList.length == 3)
						{
							player_name = wordList[1];
							srok = Integer.parseInt(wordList[2]);
							target = L2World.getPlayer(player_name);
						}
						else if(wordList.length == 2)
						{
							if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
							{
								activeChar.sendMessage("Не верный таргет.");
								return false;
							}
							srok = Integer.parseInt(wordList[1]);
							target = (L2Player)activeChar.getTarget();
							player_name = target.getName();
						}
						else
						{
							activeChar.sendMessage("//jail имя время");
							activeChar.sendMessage("//jail[на таргет] время");
							return false;
						}

						if(target != null)
						{
							target.setVar("jailedFrom", target.getX() + ";" + target.getY() + ";" + target.getZ() + ";" + target.getReflection().getId());
							target._unjailTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(target, target.getLoc(), 0), srok * 1000);
							target.setVar("jailed", srok+";"+(System.currentTimeMillis()/1000));
							target.teleToLocation(-114648, -249384, -2984, -3);
							activeChar.sendMessage("You jailed " + player_name + ".");
						}
						else
							activeChar.sendMessage("Can't find char " + player_name + ".");
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //jail char_name period");
						e.printStackTrace();
					}
					break;
				case admin_unjail:
					try
					{
						L2Player target;
						String player_name;
						if(wordList.length == 2)
						{
							player_name = wordList[1];
							target = L2World.getPlayer(player_name);
						}
						else if(wordList.length == 1)
						{
							if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
							{
								activeChar.sendMessage("Не верный таргет.");
								return false;
							}
							target = (L2Player)activeChar.getTarget();
							player_name = target.getName();
						}
						else
						{
							activeChar.sendMessage("//unjail имя");
							activeChar.sendMessage("//unjail[на таргет]");
							return false;
						}
						if(target != null && target.getVar("jailed") != null)
						{
							String[] re = target.getVar("jailedFrom").split(";");
							target.teleToLocation(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
							target.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);
							target._unjailTask.cancel(true);
							target.unsetVar("jailedFrom");
							target.unsetVar("jailed");
							activeChar.sendMessage("You unjailed " + player_name + ".");
						}
						else
							activeChar.sendMessage("Can't find char " + player_name + ".");
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //unjail char_name");
						e.printStackTrace();
					}
					break;
				case admin_ckarma:
					try
					{
						st.nextToken();
						String player = st.nextToken();
						String srok = st.nextToken();
						String bmsg = "admin_ckarma " + player + " " + srok + " ";
						String msg = fullString.substring(bmsg.length(), fullString.length());

						L2Player plyr = L2World.getPlayer(player);
						if(plyr != null)
						{
							int newKarma = Integer.parseInt(srok) + plyr.getKarma();

							// update karma
							plyr.setKarma(newKarma);

							plyr.sendMessage("You get karma(" + srok + ") by GM " + activeChar.getName());
							AutoBan.Karma(plyr, Integer.parseInt(srok), msg, activeChar.getName());
							activeChar.sendMessage("You set karma(" + srok + ") " + plyr.getName());
						}
						else if(AutoBan.Karma(player, Integer.parseInt(srok), msg, activeChar.getName()))
							activeChar.sendMessage("You set karma(" + srok + ") " + player);
						else
							activeChar.sendMessage("Can't find char: " + player);
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //ckarma char_name karma reason");
					}
					break;
				case admin_cban:
					activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/cban.htm"));
					break;
				case admin_hban:
					if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
						return false;
					st.nextToken();
					String reason = "";
					if(st.hasMoreTokens())
						reason = st.nextToken();
					writeToFile(activeChar.getTarget().getPlayer().getHWIDs()+"\t# baned GM "+activeChar.getName()+" reason: "+reason);
					activeChar.getTarget().getPlayer().logout(false, false, true, true);
					break;
				case admin_unban:
					try
					{
						st.nextToken();
						String player = st.nextToken();
						AutoBan.UnBanned(player);
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //unban char_name");
					}
					break;
				case admin_acc_unban:
					try
					{
						st.nextToken();
						String acc = st.nextToken();
						AutoBan.UnBannedAcc(acc);
					}
					catch(Exception e)
					{
						activeChar.sendMessage("Command syntax: //acc_unban acc_name");
					}
					break;
			}
		return true;
	}

	private boolean tradeBan(StringTokenizer st, L2Player activeChar)
	{
		if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;
		st.nextToken();
		L2Player targ = (L2Player) activeChar.getTarget();
		long days = -1;
		long time = -1;
		if(st.hasMoreTokens())
		{
			days = Long.parseLong(st.nextToken());
			time = days * 24 * 60 * 60 * 1000L + System.currentTimeMillis();
		}
		targ.setVar("tradeBan", String.valueOf(time));

		String msg = activeChar.getName() + " заблокировал торговлю персонажу " + targ.getName() + (days == -1 ? " на бессрочный период." : " на " + days + " дней.");

		Log.add(targ.getName() + ":" + days + tradeToString(targ, targ.getPrivateStoreType()), "tradeBan", activeChar);

		if(targ.isInOfflineMode())
		{
			targ.setOfflineMode(false);
			targ.logout(false, false, true, true);
			if(targ.getNetConnection() != null)
				targ.getNetConnection().disconnectOffline();
		}
		else if(targ.isInStoreMode())
		{
			targ.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
			targ.broadcastUserInfo(true);
			targ.getBuyList().clear();
		}

		if(ConfigValue.MAT_ANNOUNCE_FOR_ALL_WORLD)
			Announcements.getInstance().announceToAll(msg);
		else
			Announcements.shout(activeChar, msg, Say2C.CRITICAL_ANNOUNCEMENT);
		return true;
	}

	@SuppressWarnings("unchecked")
	private static String tradeToString(L2Player targ, int trade)
	{
		String ret;
		Collection list;
		switch(trade)
		{
			case L2Player.STORE_PRIVATE_BUY:
				list = targ.getBuyList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":buy:";
				for(TradeItem i : (Collection<TradeItem>) list)
					ret += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
				return ret;
			case L2Player.STORE_PRIVATE_SELL:
			case L2Player.STORE_PRIVATE_SELL_PACKAGE:
				list = targ.getSellList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":sell:";
				for(TradeItem i : (Collection<TradeItem>) list)
					ret += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
				return ret;
			case L2Player.STORE_PRIVATE_MANUFACTURE:
				list = targ.getCreateList().getList();
				if(list == null || list.isEmpty())
					return "";
				ret = ":mf:";
				for(L2ManufactureItem i : (Collection<L2ManufactureItem>) list)
					ret += i.getRecipeId() + ";" + i.getCost() + ":";
				return ret;
			default:
				return "";
		}
	}

	private boolean tradeUnban(StringTokenizer st, L2Player activeChar)
	{
		if(activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			return false;
		L2Player targ = (L2Player) activeChar.getTarget();

		targ.unsetVar("tradeBan");

		if(ConfigValue.MAT_ANNOUNCE_FOR_ALL_WORLD)
			Announcements.getInstance().announceToAll(activeChar + " разблокировал торговлю персонажу " + targ + ".");
		else
			Announcements.shout(activeChar, activeChar + " разблокировал торговлю персонажу " + targ + ".", Say2C.CRITICAL_ANNOUNCEMENT);

		Log.add(activeChar + " разблокировал торговлю персонажу " + targ + ".", "tradeBan", activeChar);
		return true;
	}

	private boolean ban(StringTokenizer st, L2Player activeChar)
	{
		try
		{
			st.nextToken();

			String player = st.nextToken();

			int time = 0;
			String msg = "";

			if(st.hasMoreTokens())
				time = Integer.parseInt(st.nextToken());

			if(st.hasMoreTokens())
			{
				msg = "admin_ban " + player + " " + time + " ";
				while(st.hasMoreTokens())
					msg += st.nextToken() + " ";
				msg.trim();
			}

			L2Player plyr = L2World.getPlayer(player);
			if(plyr != null)
			{
				//plyr.sendMessage(new CustomMessage("scripts.commands.admin.AdminBan.YoureBannedByGM", plyr).addString(activeChar.getName()));
				plyr.setAccessLevel(-100);
				AutoBan.Banned(plyr, time, msg, activeChar.getName());
				plyr.logout(false, false, true, true);
				activeChar.sendMessage("You banned " + plyr.getName());
			}
			else if(AutoBan.Banned(player, -100, time, msg, activeChar.getName()))
				activeChar.sendMessage("You banned " + player);
			else
				activeChar.sendMessage("Can't find char: " + player);
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Command syntax: //ban char_name days reason");
		}
		return true;
	}

	public synchronized static String writeToFile(String strPage)
	{
		FileWriter save = null;
		try
		{
			File file = new File("./lameguard/banned_hwid.txt");
			if(!file.exists())
				try
				{
					file.createNewFile();
				}
				catch(IOException e)
				{
					e.printStackTrace(System.err);
				}
			save = new FileWriter(file, true);
			save.write("\n");
			save.write(strPage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
				e1.printStackTrace();
			}
		}

	    return strPage;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	public void onLoad()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
		// init jail reflection
		ReflectionTable.getInstance().get(-3, true).setCoreLoc(new Location(-114648, -249384, -2984));
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}