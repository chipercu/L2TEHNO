package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Slave extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/slave/";
	private static String RuFilePatch = "data/html-ru/hellbound/slave/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void Badges()
	{
		L2Player p = (L2Player) getSelf();
		long badges = getItemCount(p, 9674);
		if(badges < 5)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "incastle_native002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "incastle_native002a.htm", p), p);
		}
		else
		{
			removeItem(p, 9674, 5);
			if(p.getVar("badgesamount") == null)
				p.setVar("badgesamount", "1");
			else
			{
				int badgesamount = Integer.parseInt(p.getVar("badgesamount"));
				badgesamount++;
				if(badgesamount < 6)
					p.setVar("badgesamount", String.valueOf(badgesamount));
				else
				{
					HellboundManager.getInstance().changeLevel(10);
					p.unsetVar("badgesamount");

					if(p.getVar("lang@").equalsIgnoreCase("ru"))
						show(Files.read(RuFilePatch + "incastle_native002.htm", p), p);
					else
						show(Files.read(EnFilePatch + "incastle_native002.htm", p), p);
				}
			}
		}
	}
}