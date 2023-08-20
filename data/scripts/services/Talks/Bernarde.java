package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Bernarde extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/bernarde/";
	private static String RuFilePatch = "data/html-ru/hellbound/bernarde/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void HolyWater()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9674) < 5) // Darion's Badge
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde002c.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde002c.htm", p), p);
		}
		else
		{
			removeItem(p, 9674, 5); // Darion's Badge
			addItem(p, 9673, 1); // Holy Water

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde002b.htm", p), p);
		}
	}

	public void help()
	{
		L2Player p = (L2Player) getSelf();	
		show(Files.read(EnFilePatch + "bernarde003h.htm", p), p);
	}

	public void alreadysaid()
	{
		L2Player p = (L2Player) getSelf();	
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "bernarde003b.htm", p), p);
		else
			show(Files.read(EnFilePatch + "bernarde003b.htm", p), p);
	}

	public void Derek()
	{
		L2Player p = (L2Player) getSelf();
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "bernarde003d.htm", p), p);
		else
			show(Files.read(EnFilePatch + "bernarde003d.htm", p), p);
	}

	public void rumors()
	{
		L2Player p = (L2Player) getSelf();
		int hLevel = HellboundManager.getInstance().getLevel();

		if(hLevel == 6)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde003i.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde003i.htm", p), p);
		}
		else if(hLevel == 7)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde003c.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde003c.htm", p), p);
		}
		else if(hLevel == 8)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde003f.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde003f.htm", p), p);
		}
	}

	public void ruins()
	{
	// TODO
	}

	public void treasure()
	{
		L2Player p = (L2Player) getSelf();
		long treasure = getItemCount(p, 9684);
		boolean condition = Boolean.parseBoolean(p.getVar("bernarde"));

		if(condition)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde003a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde003a.htm", p), p);
		}

		if(treasure < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde002e.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde002e.htm", p), p);
		}
		else
		{
			removeItem(p, 9684, 1); // Native Treasure
			p.setVar("bernarde", "true");
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "bernarde002d.htm", p), p);
			else
				show(Files.read(EnFilePatch + "bernarde002d.htm", p), p);
		}

		boolean condition2 = Boolean.parseBoolean(p.getVar("jude"));
		int hLevel = HellboundManager.getInstance().getLevel();
		
		if(condition && condition2 && HellboundManager.getInstance().getPoints() >= 1000000)
		{
			if(hLevel == 3)
			{
				ServerVariables.set("HellboundCanChangeLevel", true);
				HellboundManager.getInstance().changeLevel(4);
				p.unsetVar("bernarde");
				if(p.getVar("jude") != null)
					p.unsetVar("jude");
			}
		}
	}
}