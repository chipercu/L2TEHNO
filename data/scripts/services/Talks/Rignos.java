package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Files;
import l2open.util.GArray;

public class Rignos extends Functions implements ScriptFile
{
	private static String EnFilePatch = "data/html/hellbound/rignos/32349";
	private static String RuFilePatch = "data/html/hellbound/rignos/32349/"; // TODO

	public void Info()
	{
		L2Player p = (L2Player) getSelf();
		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "-1.htm", p), p);
		else
			show(Files.read(EnFilePatch + "-1.htm", p), p);
	}

	public void getTask()
	{
		L2Player p = (L2Player) getSelf();
		boolean canTask = true;

		if(p.getLevel() < 78)
		{
			p.sendMessage("level < 78");
			if(p.getVar("lang@").equalsIgnoreCase("ru"))
				show(Files.read(RuFilePatch + "-2.htm", p), p);
			else
				show(Files.read(EnFilePatch + "-2.htm", p), p);
		}
		else
		{
			p.sendMessage("level > 78");
			if(canTask)
			{
				p.sendMessage("canTask != null");
				startRace();
			}
			else
			{
				p.sendMessage("canTask == null");
				if(p.getVar("lang@").equalsIgnoreCase("ru"))
					show(Files.read(RuFilePatch + "-2.htm", p), p);
				else
					show(Files.read(EnFilePatch + "-2.htm", p), p);
			}
		}
	}

	private void startRace()
	{
		L2Player p = (L2Player) getSelf();
		L2Skill skill = SkillTable.getInstance().getInfo(5239, 5);
		if(skill != null)
		{
			GArray<L2Character> targets = new GArray<L2Character>();
			targets.add(p);
			if(p.getPet() != null)
				targets.add(p);
			p.callSkill(skill, targets, true);
			p.sendPacket(new MagicSkillUse(p, 5239, 5, skill.getHitTime(), 0));
			p.setVar("RaceStarted", "started");
		}
	}

	public void finishRace()
	{
		L2Player p = (L2Player) getSelf();
		if(getItemCount(p, 9850) < 4)
			return;

		// removeItem(p, 10013, -1);
		addItem(p, 9694, 3);

		p.getEffectList().stopEffectByDisplayId(5239);

		if(p.getPet() != null)
			p.getPet().getEffectList().stopEffectByDisplayId(5239);

		if(p.getVar("lang@").equalsIgnoreCase("ru"))
			show(Files.read(RuFilePatch + "-5.htm", p), p);
		else
			show(Files.read(EnFilePatch + "-5.htm", p), p);
		p.unsetVar("RaceStarted");
	}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: Isle of Player Race");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}