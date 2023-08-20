package commands.voiced;

import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.L2DatabaseFactory;
import l2open.database.mysql;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.tables.player.PlayerData;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Log;
import l2open.util.Util;
import l2open.extensions.multilang.CustomMessage;
/**
 * @author: Diagod
 * open-team.ru
 **/
public class AccLock extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "hwidlock", "hwidunlock", "hwidinfo", "enter_mail"};

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		if(activeChar.is_block)
			return false;
		command = command.intern();
		if(command.startsWith("enter_mail"))
		{
			String[] arg = args.trim().split(":");
			if(arg.length != 6)
			{
				activeChar.sendMessage(new CustomMessage("scripts.services.Activation.InvalidArguments", activeChar));
				activation_page(activeChar);
				return true;
			}
			String t1 = arg[0].trim();
			String t2 = arg[1].trim();
			String t3 = arg[2].trim();
			String t4 = arg[3].trim();

			String email1 = t1 + "@" + t2;
			String email2 = t3 + "@" + t4;
			String l2question = arg[4].trim();
			String l2answer = arg[5].trim();

			if(!email1.equals(email2))
			{
				activeChar.sendMessage(new CustomMessage("scripts.services.Activation.EmailAndConfirmationMustMatch", activeChar));
				activation_page(activeChar);
				return true;
			}
			
			else if(!Util.isMatchingRegexp(l2question, "([\\s++=#!@%^$&?~.0-9A-Za-z\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9\\s.]{4,16})|([\\s++=#!@%^$&?~.0-9A-Za-z\u0410-\u044f\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9\\s.]{4,16})") || !Util.isMatchingRegexp(l2answer, "([\\s++=#!@%^$&?~.0-9A-Za-z\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9\\s.]{4,16})|([\\s++=#!@%^$&?~.0-9A-Za-z\u0410-\u044f\u003c\u003e\u0028\u0029\u007b\u007d\u005f\u007c\u002d\u00ae\u00a9\\s.]{4,16})"))
			{
				activeChar.sendMessage("Не допустимые символы в поле вопрос/ответ. Можно использовать только буквы и цифры.");
				activation_page(activeChar);
				return true;
			}
			/*else if(!arg[0].trim().matches("[-a-zA-Z0-9_\\.]+") || !arg[1].trim().matches("[-a-zA-Z0-9_\\.]+"))
			{
				activeChar.sendMessage(new CustomMessage("scripts.services.Activation.InvalidNewMail", activeChar));
				activation_page(activeChar);
				return true;
			}*/
			else if(ConfigValue.AccHwidLockPriceCount > 0)
			{
				if(Functions.getItemCount(activeChar, ConfigValue.AccHwidLockPriceId) >= ConfigValue.AccHwidLockPriceCount && Functions.removeItem(activeChar, ConfigValue.AccHwidLockPriceId, ConfigValue.AccHwidLockPriceCount) == ConfigValue.AccHwidLockPriceCount)
				{
					activeChar.addAccLock(activeChar.getHWIDs());
					activeChar.sendMessage("Вы успешно привязали свой аккаунт, к данному компьютеру.");
				}
				else
				{
					activeChar.sendMessage("У вас не достаточно предметов.");
					return true;
				}
			}
			else
			{
				activeChar.addAccLock(activeChar.getHWIDs());
				activeChar.sendMessage("Вы успешно привязали свой аккаунт, к данному компьютеру.");
			}
			try
			{
				Log.add("HWID_MOD["+activeChar.getAccountName()+"]: email1["+t1+"]["+t2+"]["+t3+"]["+t4+"]='"+email1+"' l2question='"+l2question+"' l2answer='"+l2answer+"'", "enter_mail");
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE accounts SET l2email='"+email1+"',l2question='"+l2question+"',l2answer='"+l2answer+"' WHERE login='"+activeChar.getAccountName()+"'");
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if(command.equalsIgnoreCase("hwidlock"))
		{
			if(ConfigValue.AccHwidLockEnable && ConfigValue.ProtectEnable)
			{
				if(activeChar.getAccLock() != null && Util.contains(activeChar.getAccLock(), activeChar.getHWIDs()))
				{
					activeChar.sendMessage("Ваш аккаунт, уже привязан к данному компьютеру.");
					return true;
				}
				else if(ConfigValue.AccHwidLockOnlyFullInfo && PlayerData.getInstance().canEnterMailOrQuestion(activeChar, false))
				{
					activeChar.sendMessage("Привязка доступна только для полных регистраций.");
					activation_page(activeChar);
					return true;
				}
				else if(ConfigValue.AccHwidLockOnlyFullInfo && PlayerData.getInstance().canEnterMailOrQuestion(activeChar, true))
				{
					activeChar.sendMessage("Привязка доступна только для полных регистраций.");
					activation_page(activeChar);
					return true;
				}
				else if(ConfigValue.AccHwidLockPriceCount > 0)
				{
					if(Functions.getItemCount(activeChar, ConfigValue.AccHwidLockPriceId) >= ConfigValue.AccHwidLockPriceCount && Functions.removeItem(activeChar, ConfigValue.AccHwidLockPriceId, ConfigValue.AccHwidLockPriceCount) == ConfigValue.AccHwidLockPriceCount)
					{
						activeChar.addAccLock(activeChar.getHWIDs());
						activeChar.sendMessage("Вы успешно привязали свой аккаунт, к данному компьютеру.");
					}
					else
						activeChar.sendMessage("У вас не достаточно предметов.");
				}
				else
				{
					activeChar.addAccLock(activeChar.getHWIDs());
					activeChar.sendMessage("Вы успешно привязали свой аккаунт, к данному компьютеру.");
				}
			}
			else
				activeChar.sendMessage("В данный момент функция не доступна. Обратитесь за помощью к Администрации.");
			return true;
		}
		else if(command.equalsIgnoreCase("hwidunlock") && activeChar.getAccLock() != null && Util.contains(activeChar.getAccLock(), activeChar.getHWIDs()))
		{
			activeChar.removeAccLock(activeChar.getHWIDs());
			activeChar.sendMessage("Вы упешно сняли привязку с аккаунта по HWID.");
			return true;
		}
		else if(command.equalsIgnoreCase("hwidinfo"))
		{
			if(activeChar.getAccLock() != null && Util.contains(activeChar.getAccLock(), activeChar.getHWIDs()))
				activeChar.sendMessage("В данный момент ваш аккаунт привязан по HWID, что бы его отвязать введите в общий чат команду .hwidunlock");
			else
			{
				activeChar.sendMessage("В данный момент ваш аккаунт не привязан по HWID, что бы его привязать введите в общий чат команду .hwidlock");
				activeChar.sendMessage("Стоимость привязки: " + ConfigValue.AccHwidLockPriceCount + " " + getName(ConfigValue.AccHwidLockPriceId));
			}
			return true;
		}
		return false;
	}

	public void activation_page(L2Player player)
	{
		if(player == null)
			return;
		//player.block();
		//player.setFlying(true); // хак позволяющий сделать логаут
		show(Files.read("data/scripts/services/enter_new_mail.htm", player), player);
	}

	private static String getName(int itemId)
	{
		return ItemTemplates.getInstance().getTemplate(itemId).getName();
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}