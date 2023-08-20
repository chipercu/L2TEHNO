package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Falk extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/falk/";
	private static String RuFilePatch = "data/html-ru/hellbound/falk/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void Go()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9850) < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "falk002.htm", p), p);
			else
				show(Files.read(EnFilePatch + "falk002.htm", p), p);
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "falk001a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "falk001a.htm", p), p);
		}
	}

	public void getFirstMark()
	{
		L2Player p = (L2Player) getSelf();	
		if(getItemCount(p, 9850) > 0 || getItemCount(p, 9851) > 0 || getItemCount(p, 9852) > 0 || getItemCount(p, 9853) > 0) // уже есть
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "falk002d.htm", p), p);
			else
				show(Files.read(EnFilePatch + "falk002d.htm", p), p);
		}
		else if(getItemCount(p, 9674) >= 20)
		{
			removeItem(p, 9674, 20); // Darion's Badge
			addItem(p, 9850, 1); // Basic Caravan Certificate

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "falk002a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "falk002a.htm", p), p);
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "falk002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "falk002b.htm", p), p);
		}
	}

	public void Back1a()
	{
		L2Player p = (L2Player) getSelf();	
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "falk001a.htm", p), p);
		else
			show(Files.read(EnFilePatch + "falk001a.htm", p), p);
	}

	public void Back2()
	{
		L2Player p = (L2Player) getSelf();	
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "falk002.htm", p), p);
		else
			show(Files.read(EnFilePatch + "falk002.htm", p), p);
	}
}