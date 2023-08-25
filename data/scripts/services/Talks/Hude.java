package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;

public class Hude extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/hude/";
	private static String RuFilePatch = "data/html-ru/hellbound/hude/";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void getSecondMark()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9676) >= 30 && getItemCount(p, 10012) >= 60)
		{
			removeItem(p, 9676, 30); // Mark of Betrayal
			removeItem(p, 10012, 60); // Scorpion Poison Stingers
			removeItem(p, 9850, 1); // Basic Caravan Certificate
			addItem(p, 9851, 1); // Standard Caravan Certificate
			addItem(p, 9994, 1);

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude004a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude004a.htm", p), p);
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude004b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude004b.htm", p), p);
		}
	}

	public void getThirdMark()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9681) >= 56 && getItemCount(p, 9682) >= 14)
		{
			removeItem(p, 9681, 56); // Life Force
			removeItem(p, 9682, 14); // Contained Life Force
			removeItem(p, 9851, 1); // Standard Caravan Certificate
			addItem(p, 9852, 1); // Premium Caravan Certificate

			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude006a.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude006a.htm", p), p);
		}
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude006b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude006b.htm", p), p);
		}
	}

	public void tradeSpecial()
	{
		final L2Player p = (L2Player) getSelf();
		final L2NpcInstance n = getNpc();
		if(getItemCount(p, 9851) > 0 || getItemCount(p, 9852) > 0)
			n.onBypassFeedback(p, "Multisell 32298002");
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude002b.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude002b.htm", p), p);
		}
	}
	
	public void tradeDynasty()
	{
		final L2Player p = (L2Player) getSelf();
		final L2NpcInstance n = getNpc();
		if(getItemCount(p, 9852) > 0)
			n.onBypassFeedback(p, "Multisell 250980013");
		else
		{
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "caravan_hude007.htm", p), p);
			else
				show(Files.read(EnFilePatch + "caravan_hude007.htm", p), p);
		}
	}
}