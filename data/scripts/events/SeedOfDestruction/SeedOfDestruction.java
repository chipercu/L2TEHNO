package events.SeedOfDestruction;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Files;

public class SeedOfDestruction extends Functions implements ScriptFile
{
	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(ServerVariables.getLong("SoD_opened", 0) * 1000L + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L < System.currentTimeMillis())
		{
			ServerVariables.set("SoD_opened", System.currentTimeMillis() / 1000L);
			_log.info("Seed Of Destruction opened for next "+ConfigValue.SeedofDestructionOpenTime+"h.");
			Announcements.getInstance().announceToAll("Seed Of Destruction opened for next "+ConfigValue.SeedofDestructionOpenTime+"h.");
		}
		else
			player.sendMessage("Seed Of Destruction already opened for "+ConfigValue.SeedofDestructionOpenTime+"h.");
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Останавливает эвент
	*/
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		long sodOpened = ServerVariables.getLong("SoD_opened", 0) * 1000L;
		if(sodOpened < System.currentTimeMillis() && sodOpened + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L > System.currentTimeMillis())
		{
			ServerVariables.unset("SoD_opened");
			Reflection r = ReflectionTable.SOD_REFLECTION_ID == 0 ? null : ReflectionTable.getInstance().get(ReflectionTable.SOD_REFLECTION_ID);
			if(r != null)
				r.startCollapseTimer(0);
			else
				new Exception("Failed to collapse Seed Of Destruction").printStackTrace();

			Announcements.getInstance().announceToAll("Seed Of Destruction closed now.");
			_log.info("Seed Of Destruction closed manually.");
		}
		else
			player.sendMessage("Seed Of Destruction not opened.");
		show(Files.read("data/html/admin/events.htm", player), player);
	}

	public void onLoad()
	{
		long timelimit = ServerVariables.getLong("SoD_opened", 0) * 1000L + ConfigValue.SeedofDestructionOpenTime * 60 * 60 * 1000L - System.currentTimeMillis();
		if(timelimit > 0)
		{
			int h = (int) Math.ceil(timelimit / 3600000);
			_log.info("Seed Of Destruction will closed in " + h + "h " + (timelimit - h * 3600000) / 60000 + "min");
		}
		else
			_log.info("Seed Of Destruction closed.");
	}

	public void onReload()
	{
		onLoad();
	}

	public void onShutdown()
	{}
}