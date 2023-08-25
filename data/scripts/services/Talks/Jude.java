package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Jude extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/jude/";
	private static String RuFilePatch = "data/html-ru/hellbound/jude/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void Treasure()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9684) < 40)
		{
			p.setVar("jude", "true");
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "jude002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "jude002a.htm", p), p);
		}
		else
		{
			removeItem(p, 9684, 40);

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "jude002.htm", p), p);
			else
				show(Files.read(EnFilePatch + "jude002.htm", p), p);
		}

		boolean condition = Boolean.parseBoolean(p.getVar("bernarde"));
		boolean condition2 = Boolean.parseBoolean(p.getVar("jude"));
		int hLevel = HellboundManager.getInstance().getLevel();

		if(condition && condition2 && HellboundManager.getInstance().getPoints() >= 1000000)
		{
			if(hLevel == 3)
			{
				ServerVariables.set("HellboundCanChangeLevel", true);
				HellboundManager.getInstance().changeLevel(4);
				if(p.getVar("bernarde") != null)
					p.unsetVar("bernarde");
				p.unsetVar("jude");
			}
		}
	}
}