package events.SpooksAndScarecrows;

import l2p.extensions.scripts.Functions;
import l2p.extensions.scripts.ScriptFile;
import l2p.gameserver.Announcements;
import l2p.gameserver.model.L2ObjectsStorage;
import l2p.gameserver.model.L2Player;
import l2p.util.Files;

/**
 *         Development by L2Phoenix
 *         Хэллоуин не за углом и Lineage II дарит вам пару изюминок!
 *         20 октября во вторник все игроки выше 20 уровня найдут у себя на складе
 *         Scarecrow Jack Transformation Stick (Палка трансформации) и
 *         5 Revita Pop (леденцов виталити).
 */

public class SpooksAndScarecrows extends Functions implements ScriptFile
{
	private static final String SpooksAndScarecrows = null;

	private static boolean _active = false;

	// Items
	private static int SHADOW_JACK = 14235;
	private static int REVIVA_POP = 20034;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			System.out.println("Loaded Event: Spooks & Scarecrows [state: activated]");
		}
		else
			System.out.println("Loaded Event: Spooks & Scarecrows [state: deactivated]");
	}

	private static boolean isActive()
	{
		return IsActive("SpooksAndScarecrows");
	}

	/**
	 * Loading Event
	 */
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		System.out.println("Event: 'Spooks & Scarecrows' started.");

		Announcements.getInstance().announceByCustomMessage("scripts.events.SpooksAndScarecrows.SpooksAndScarecrows.AnnounceEventStarted", null);

		for(L2Player p : L2ObjectsStorage.getPlayers())
		{
			int playerLvl = p.getLevel();
			if(playerLvl >= 20)
			{
				addItem(p, SHADOW_JACK, 1);
				addItem(p, REVIVA_POP, 5);
			}
			else if((playerLvl >= 20) && (getItemCount(player, SHADOW_JACK) >= 1) && (getItemCount(player, REVIVA_POP) >= 1)){ return; }
		}
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive(SpooksAndScarecrows, false))
		{
			System.out.println("Event: 'Spooks & Scarecrows' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SpooksAndScarecrows.SpooksAndScarecrows.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event: 'Spooks & Scarecrows' not started.");

		_active = false;
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SpooksAndScarecrows.SpooksAndScarecrows.AnnounceEventStarted", null);
	}
}