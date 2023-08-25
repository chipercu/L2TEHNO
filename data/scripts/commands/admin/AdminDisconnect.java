package commands.admin;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.*;
import l2open.database.L2DatabaseFactory;

public class AdminDisconnect implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_disconnect,
		admin_kick,
		admin_kickip,
		admin_kick_bot,
		admin_db
	}

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanKick)
			return false;

		switch(command)
		{
			case admin_db:
				/*try
				{
					L2DatabaseFactory.getInstance().shutdown();

					L2DatabaseFactory a = L2DatabaseFactory.getInstance();
					L2DatabaseFactory a2 = L2DatabaseFactory.getInstanceLogin();

					java.lang.reflect.Field f = L2DatabaseFactory.class.getDeclaredField("_instance");
					f.setAccessible(true); 
					f.set(a, new L2DatabaseFactory());

					java.lang.reflect.Field f2 = L2DatabaseFactory.class.getDeclaredField("_instanceLogin");
					f2.setAccessible(true); 

					if(ConfigValue.URL.equalsIgnoreCase(ConfigValue.Accounts_URL))
						f2.set(a2, L2DatabaseFactory.getInstance());
					else
					{
						f2.set(a2, new L2DatabaseFactory(ConfigValue.Accounts_URL, ConfigValue.Accounts_Login, ConfigValue.Accounts_Password, ConfigValue.MaximumDbConnections/2, ConfigValue.MaxIdleConnectionTimeout));

					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				try
				{
					L2DatabaseFactory.getInstance().shutdown();
					L2DatabaseFactory._instance = new L2DatabaseFactory();
					if(ConfigValue.URL.equalsIgnoreCase(ConfigValue.Accounts_URL))
						L2DatabaseFactory.getInstance()._instanceLogin = L2DatabaseFactory.getInstance();
					else
					{
						L2DatabaseFactory.getInstance()._instanceLogin = new L2DatabaseFactory(ConfigValue.Accounts_URL, ConfigValue.Accounts_Login, ConfigValue.Accounts_Password, ConfigValue.MaximumDbConnections/2, ConfigValue.MaxIdleConnectionTimeout);

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				break;
			case admin_kickip:
				if(wordList.length > 1)
				{
					int online_kick=0;
					int offline_kick=0;
					for(final L2Player player : L2ObjectsStorage.getPlayers())
						if(player != null && wordList[1].equalsIgnoreCase(player.getIP()))
						{
							if(player.isInOfflineMode())
							{
								player.setOfflineMode(false);
								player.logout(false, false, true, true);
								if(player.getNetConnection() != null)
									player.getNetConnection().disconnectOffline();
								offline_kick++;
								continue;
							}

							//player.sendMessage(new CustomMessage("scripts.commands.admin.AdminDisconnect.YoureKickedByGM", player));
							player.sendPacket(Msg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN);
							ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
								public void runImpl()
								{
									player.logout(false, false, true, true);
								}
							}, 500);
							online_kick++;
						}
					activeChar.sendMessage("Disconnected from server online char: "+online_kick+" and offline: "+offline_kick);
				}
				break;
			case admin_disconnect:
			case admin_kick:
				final L2Player player;
				if(wordList.length == 1)
				{
					// Обработка по таргету
					L2Object target = activeChar.getTarget();
					if(target == null)
					{
						activeChar.sendMessage("Select character or specify player name.");
						break;
					}
					if(!target.isPlayer())
					{
						activeChar.sendPacket(Msg.INVALID_TARGET);
						break;
					}
					player = (L2Player) target;
				}
				else
				{
					// Обработка по нику
					player = L2World.getPlayer(wordList[1]);
					if(player == null)
					{
						activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
						break;
					}
				}

				activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");

				if(player.isInOfflineMode())
				{
					player.setOfflineMode(false);
					player.logout(false, false, true, true);
					if(player.getNetConnection() != null)
						player.getNetConnection().disconnectOffline();
					return true;
				}

				//player.sendMessage(new CustomMessage("scripts.commands.admin.AdminDisconnect.YoureKickedByGM", player));
				player.sendPacket(Msg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN);
				ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl(){
					public void runImpl()
					{
						player.logout(false, false, true, true);
					}
				}, 500);
				break;
			case admin_kick_bot:
				player = L2ObjectsStorage.getPlayer(Integer.parseInt(wordList[1]));
				if(player != null)
				{
					activeChar.sendMessage("Character(bot) " + player.getName() + " disconnected from server.");
					ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
					{
						public void runImpl()
						{
							player.logout(false, false, true, true);
						}
					}, 500);
				}
				break;
		}
		return true;
	}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
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