package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.*;

public class Bot extends Functions implements ScriptFile
{
	public void capture(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(var.length != 3)
		{
			player.sendMessage("Не верный ответ.");
			return;
		}

		Integer add1;
		Integer add2;
		Integer summ;
		try
		{
			add1 = Integer.valueOf(var[0]);
			add2 = Integer.valueOf(var[1]);
			summ = Integer.valueOf(var[2]);
		}
		catch(Exception e)
		{
			
			player.sendMessage("Не верный ответ1.");
			return;
		}

		if(add1 + add2 != summ)
		{
			player.sendMessage("Не верный ответ.");
			return;
		}
		player.setVar("admin_bot_check", "true");
		/*if(player._bot_check != null)
		{
			player._bot_check.cancel(false);
			player._bot_check = null;
		}*/
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}