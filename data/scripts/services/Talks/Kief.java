package services.Talks;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.instancemanager.ServerVariables;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;

public class Kief extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/kief/";
	private static String RuFilePatch = "data/html-ru/hellbound/kief/";

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
		if(badges < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief010a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief010a.htm", p), p);
		}
		else
		{
			long points = 10 * badges;
			HellboundManager.getInstance().addPoints(points);

			removeItem(p, 9674, badges);

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief010.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief010.htm", p), p);
		}
	}

	public void Bottle()
	{
		L2Player p = (L2Player) getSelf();
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "kief011g.htm", p), p);
		else
			show(Files.read(EnFilePatch + "kief011g.htm", p), p);
	}

	public void getBottle()
	{
		L2Player p = (L2Player) getSelf();
		long stinger = getItemCount(p, 10012);
		if(stinger == 0)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011f.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011f.htm", p), p);
		}
		else if(stinger < 20)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011i.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011i.htm", p), p);
		}
		else
		{
			removeItem(p, 10012, 20);
			addItem(p, 9672, 1);

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011h.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011h.htm", p), p);
		}
	}

	public void dlf()
	{
		L2Player p = (L2Player) getSelf();
		long dimlf = getItemCount(p, 9680);
		if(dimlf < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011b.htm", p), p);
		}
		else
		{
			long points = 10 * dimlf * ConfigValue.RateHbPoints;
			removeItem(p, 9680, dimlf);
			changePoints(points);
			checklvlup();

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011c.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011c.htm", p), p);
		}
	}

	public void lf()
	{
		L2Player p = (L2Player) getSelf();
		long lifef = getItemCount(p, 9681);
		if(lifef < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011b.htm", p), p);
		}
		else
		{
			long points = 20 * lifef * ConfigValue.RateHbPoints;
			removeItem(p, 9681, lifef);
			changePoints(points);
			checklvlup();

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011e.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011e.htm", p), p);
		}
	}

	public void clf()
	{
		L2Player p = (L2Player) getSelf();
		long conlf = getItemCount(p, 9682);
		if(conlf < 1)
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011d.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011d.htm", p), p);
		}
		else
		{
			long points = 50 * conlf * ConfigValue.RateHbPoints;
			removeItem(p, 9682, conlf);
			changePoints(points);
			checklvlup();

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "kief011a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "kief011a.htm", p), p);
		}
	}
	
	public static void changePoints(long mod)
	{
		long curr = getPoints();
		long n = Math.max(0, mod + curr);
		if(curr != n)
		{
			ServerVariables.set("life_points", n);
		}
	}

	public static long getPoints()
	{
		return ServerVariables.getInt("life_points", 0);
	}
	
	public void checklvlup()
	{
		int curHBLevel = HellboundManager.getInstance().getLevel();
		long curr = getPoints();
		if(curr >= 1000000)
			if (curHBLevel == 7)
				HellboundManager.getInstance().changeLevel(8);
	}
}