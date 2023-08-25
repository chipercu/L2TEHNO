package commands.voiced;

import java.sql.ResultSet;
import java.sql.SQLException;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.IVoicedCommandHandler;
import l2open.gameserver.handler.VoicedCommandHandler;
import l2open.gameserver.loginservercon.LSConnection;
import l2open.gameserver.loginservercon.gspackets.ChangePassword;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;
import l2open.util.Log;
import l2open.util.Util;

import l2open.loginserver.crypt.Crypt;
import l2open.loginserver.LoginController;

public class Activation extends Functions implements IVoicedCommandHandler, ScriptFile
{
	private String[] _commandList = new String[] { "activ_new_info" };

	public static Crypt DEFAULT_CRYPT;

	static
	{
		try
		{
			DEFAULT_CRYPT = (Crypt) Class.forName("l2open.loginserver.crypt." + ConfigValue.DefaultPasswordEncoding).getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void activation_page(L2Player player)
	{
		if(player == null)
			return;
		player.block();
		player.setFlying(true); // хак позволяющий сделать логаут
		show(Files.read("data/scripts/services/change_data.htm", player), player);
	}

	public boolean useVoicedCommand(String command, L2Player activeChar, String args)
	{
		command = command.intern();
		if(command.startsWith("activ_new_info"))
		{
			if(!activation(activeChar, args.trim().split(":")))
				activation_page(activeChar);
		}
		return false;
	}

	public boolean activation(L2Player player, String[] args)
	{
		if(player == null)
			return false;
		else if(args.length != 8)
		{
			sendMessage(new CustomMessage("scripts.services.Activation.InvalidArguments", player), player);
			return false;
		}

		// $newmail11 : $newmail12 : $newmail21 : $newmail22 : $newpass1 : $newpass2 : $l2question : $l2answer

		String t1 = args[0].trim();
		String t2 = args[1].trim();
		String t3 = args[2].trim();
		String t4 = args[3].trim();

		String email1 = t1 + "@" + t2;
		String email2 = t3 + "@" + t4;
		String newpass1 = args[4].trim();
		String newpass2 = args[5].trim();
		String l2question = args[6].trim();
		String l2answer = args[7].trim();
		String dbPassword = "";

		if(ConfigValue.ActivationCanNewQuestionAndAnswer)
		{
			if(l2question == null || l2answer == null || l2question.isEmpty() || l2answer.isEmpty())
			{
				player.sendMessage("Введите секретный вопрос/ответ.");
				return false;
			}
			try
			{
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE accounts SET l2question='"+l2question+"',l2answer='"+l2answer+"' WHERE login=?", player.getAccountName());
			}
			catch(SQLException e)
			{
				sendMessage(new CustomMessage("scripts.services.Activation.SomethingIsWrongTryAgain", player), player);
				e.printStackTrace();
				return false;
			}
		}
		if(ConfigValue.ActivationCanNewPass)
		{
			if(!newpass1.equals(newpass2))
			{
				sendMessage(new CustomMessage("scripts.services.Activation.NewPasswordAndConfirmationMustMatch", player), player);
				return false;
			}
			else if(!Util.isMatchingRegexp(newpass1, ConfigValue.ApasswdTemplate))
			{
				sendMessage(new CustomMessage("scripts.services.Activation.InvalidNewPassword", player), player);
				return false;
			}

			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = L2DatabaseFactory.getInstanceLogin().getConnection();
				statement = con.prepareStatement("SELECT password FROM accounts WHERE login = ?");
				statement.setString(1, player.getAccountName());
				rset = statement.executeQuery();
				if(rset.next())
					dbPassword = rset.getString("password");
			}
			catch(Exception e)
			{
				// log.warning("Can't recive old password for account " + accname + ", exciption :" + e);
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			}
			if(DEFAULT_CRYPT.compare(newpass1, dbPassword))
			{
				player.sendMessage("Новый пароль, не должен совпадать со старым!");
				return false;
			}
			try
			{
				newpass1 = DEFAULT_CRYPT.encrypt(newpass1);
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET password='"+newpass1+"' WHERE `login`=?", player.getAccountName());
			}
			catch(Exception e)
			{
				sendMessage(new CustomMessage("scripts.services.Activation.SomethingIsWrongTryAgain", player), player);
				e.printStackTrace();
				return false;
			}
		}
		if(ConfigValue.ActivationCanNewMail)
		{
			if(!email1.equals(email2))
			{
				sendMessage(new CustomMessage("scripts.services.Activation.EmailAndConfirmationMustMatch", player), player);
				return false;
			}
			else if(!args[0].matches("[-a-zA-Z0-9_\\.]+") || !args[1].matches("[-a-zA-Z0-9_\\.]+"))
			{
				sendMessage(new CustomMessage("scripts.services.Activation.InvalidNewMail", player), player);
				return false;
			}
			ThreadConnection con = null;
			FiltredPreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = L2DatabaseFactory.getInstanceLogin().getConnection();
				statement = con.prepareStatement("SELECT * FROM `accounts` WHERE l2email=?");
				statement.setString(1, email1);
				rset = statement.executeQuery();
				if(rset.next())
				{
					sendMessage(new CustomMessage("scripts.services.Activation.EmailAlreadyExists", player), player);
					return false;
				}
			}
			catch(SQLException e)
			{
				sendMessage(new CustomMessage("scripts.services.Activation.SomethingIsWrongTryAgain", player), player);
				e.printStackTrace();
				return false;
			}
			finally
			{
				DatabaseUtils.closeDatabaseCSR(con, statement, rset);
			}
			try
			{
				Log.add("ACT_MOD["+player.getAccountName()+"]: email1["+t1+"]["+t2+"]["+t3+"]["+t4+"]='"+email1+"'", "enter_mail");
				mysql.setEx(L2DatabaseFactory.getInstanceLogin(), "UPDATE `accounts` SET l2email='"+email1+"' WHERE `login`=?", player.getAccountName());
			}
			catch(SQLException e)
			{
				sendMessage(new CustomMessage("scripts.services.Activation.SomethingIsWrongTryAgain", player), player);
				e.printStackTrace();
				return false;
			}
		}
		player.unblock();
		player.setFlying(false); // хак позволяющий сделать логаут

		//player.setVar("activation_acc", "ok");
		//player.logout(false, false, false, true);
		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public void onLoad()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}