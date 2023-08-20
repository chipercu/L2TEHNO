package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

public class TeleToMDT extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: Teleport to Race Track");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void toMDT()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}

		player.setVar("backCoords", player.getLoc().toXYZString());
		player.teleToLocation(12661, 181687, -3560);
	}

	public void fromMDT()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		String var = player.getVar("backCoords");
		if(var == null || var.equals(""))
		{
			teleOut();
			return;
		}
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.teleToLocation(new Location(var));
		player.unsetVar("backCoords");
	}

	public void teleOut()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.teleToLocation(12902, 181011, -3563);
		show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить за ограждение." : "I don't know from where you came here, but I can teleport you the another border side.", player, npc);
	}
}