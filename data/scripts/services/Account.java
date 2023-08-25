package services;

import l2open.config.ConfigValue;
import l2open.database.*;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;

import java.sql.ResultSet;

/**
 * @author PaInKiLlEr
 */
public class Account extends Functions implements ScriptFile
{
	public void CharToAcc()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		String append = "Перенос персонажей между аккаунтами.<br>";
		append += "Цена: " + ConfigValue.CharToAccPrice + " " + ItemTemplates.getInstance().getTemplate(ConfigValue.CharToAccItem).getName() + ".<br>";
		append += "Внимание !!! При переносе персонажа на другой аккаунт, убедитесь что персонажей там меньше чем 7, иначе могут возникнуть непредвиденные ситуации за которые Администрация не отвечает.<br>";
		append += "Внимательно вводите логин куда переносите, администрация не возвращает персонажей.";
		append += "Вы переносите персонажа " + player.getName() + ", на какой аккаунт его перенести ?";
		append += "<edit var=\"new_acc\" width=150>";
		append += "<button value=\"Перенести\" action=\"bypass -h scripts_services.Account:NewAccount $new_acc\" width=150 height=15><br>";
		show(append, player, null);
	}

	public void NewAccount(String[] name)
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(player.getInventory().getCountOf(ConfigValue.CharToAccItem) < ConfigValue.CharToAccPrice)
		{
			player.sendMessage("У вас нету " + ConfigValue.CharToAccPrice + " " + ItemTemplates.getInstance().getTemplate(ConfigValue.CharToAccItem));
			CharToAcc();
			return;
		}
		String _name = name[0];
		ThreadConnection con = null;
        ThreadConnection conGS = null;
		FiltredPreparedStatement offline = null;
        FiltredStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstanceLogin().getConnection();
			offline = con.prepareStatement("SELECT `login` FROM `accounts` WHERE `login` = ?");
			offline.setString(1, _name);
			rs = offline.executeQuery();
			if(rs.next())
			{
				removeItem(player, ConfigValue.CharToAccItem, ConfigValue.CharToAccPrice);
                conGS = L2DatabaseFactory.getInstance().getConnection();
			    statement = conGS.createStatement();
				statement.executeUpdate("UPDATE `characters` SET `account_name` = '" + _name + "' WHERE `char_name` = '" + player.getName() + "'");
				player.sendMessage("Персонаж успешно перенесен.");
				Log.add("Персонаж " + player.getName() + " перенесён на акаунт " + _name + " с акаунта " + player.getAccountName(), "services");
				player.logout(false, false, false, true);
			}
			else
			{
				player.sendMessage("Введенный аккаунт не найден.");
				CharToAcc();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, offline, rs);
            DatabaseUtils.closeDatabaseCS(conGS, statement);
		}
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}