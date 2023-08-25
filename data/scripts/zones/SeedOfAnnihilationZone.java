package zones;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Zone Manager for location Seed Of Annihilation
 * @author Drizzy
 * @date 15.12.10
 */
public class SeedOfAnnihilationZone extends Functions implements ScriptFile
{
	public void teleport()
	{
		L2Player player = (L2Player) getSelf();
		if(player.getLevel()  >= 80)
			player.teleToLocation(-180218, 185923, -10576);
		else
			player.sendMessage("You level is low. Requirment 80 or high.");

	}

	public void transform()
	{
		L2Player player = (L2Player) getSelf();
		if(player.getTransformation() != 0)
			showHtmlFile(player, "32739-2.htm");
		else
		{
			getNpc().doCast(SkillTable.getInstance().getInfo(6649, 1), player, true);
			showHtmlFile(player, "32739-1.htm");
		}
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, getNpc());
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}		
}
