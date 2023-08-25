package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Buron extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/buron/";
	private static String RuFilePatch = "data/html-ru/hellbound/buron/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void craftNativeHelmet()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9850) == 0 && getItemCount(p, 9851) == 0 && getItemCount(p, 9852) == 0 && getItemCount(p, 9853) == 0) // нет марки
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002b.htm", p), p);
		}

		if(getItemCount(p, 9674) >= 10)
		{
			removeItem(p, 9674, 10); // Darion's Badge
			addItem(p, 9669, 1); // Native Helmet
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002a.htm", p), p);
		}
	}

	public void craftNativeTunic()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9850) == 0 && getItemCount(p, 9851) == 0 && getItemCount(p, 9852) == 0 && getItemCount(p, 9853) == 0) // нет марки
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002b.htm", p), p);
		}

		if(getItemCount(p, 9674) >= 10)
		{
			removeItem(p, 9674, 10); // Darion's Badge
			addItem(p, 9670, 1); // Native Tunic
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002a.htm", p), p);
		}
	}

	public void craftNativePants()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9850) == 0 && getItemCount(p, 9851) == 0 && getItemCount(p, 9852) == 0 && getItemCount(p, 9853) == 0) // нет марки
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002b.htm", p), p);
		}

		if(getItemCount(p, 9674) >= 10)
		{
			removeItem(p, 9674, 10); // Darion's Badge
			addItem(p, 9671, 1); // Native Pants
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron002a.htm", p), p);
		}
	}

	public void Rumors()
	{
		L2Player p = (L2Player) getSelf();
		int hLevel = HellboundManager.getInstance().getLevel();
		if(hLevel == 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003a.htm", p), p);
		}
		if(hLevel == 2)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003b.htm", p), p);
		}
		if(hLevel == 3)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003c.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003c.htm", p), p);
		}
		if(hLevel == 4)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003h.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003h.htm ", p), p);
		}
		if(hLevel == 5)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003d.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003d.htm", p), p);
		}
		if(hLevel == 6)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003i.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003i.htm", p), p);
		}
		if(hLevel == 7)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003e.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003e.htm", p), p);
		}
		if(hLevel == 8)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003f.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003f.htm", p), p);
		}
		if(hLevel == 9)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003g.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003g.htm", p), p);
		}
		if(hLevel == 10)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003j.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003j.htm", p), p);
		}
		if(hLevel == 11)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "buron003k.htm", p), p);
			else
				show(Files.read(EnFilePatch + "buron003k.htm", p), p);
		}
	}

	public void Back()
	{
		L2Player p = (L2Player) getSelf();
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "buron001.htm", p), p);
		else
			show(Files.read(EnFilePatch + "buron001.htm", p), p);
	}
}