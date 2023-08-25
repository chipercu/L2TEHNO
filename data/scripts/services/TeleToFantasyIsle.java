package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * Используется для телепорта на Fantasy Isle и обратно.
 *
 * @Author: SYS
 * @Date: 01/07/2008
 */
public class TeleToFantasyIsle extends Functions implements ScriptFile
{
	private static final Location[] POINTS = { new Location(-60695, -56896, -2032), new Location(-59716, -55920, -2032),
			new Location(-58752, -56896, -2032), new Location(-59716, -57864, -2032) };

	public void onLoad()
	{
		_log.info("Loaded Service: Teleport to Fantasy Isle");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void toFantasyIsle()
	{
		L2Player player = (L2Player) getSelf();

		if(!L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		player.setVar("backCoords", player.getLoc().toXYZString());
		player.teleToLocation(POINTS[Rnd.get(POINTS.length)]);
	}

	public void fromFantasyIsle()
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
		player.teleToLocation(-44316, -113136, -80); //Orc Village
		show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить в ближайший город." : "I don't know from where you came here, but I can teleport you the nearest town.", player, npc);
	}
}