package services.Level;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2SkillLearn;
import l2open.gameserver.model.base.Experience;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.SkillTreeTable;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Util;

public class Level extends Functions implements ScriptFile
{
	public void show()
	{
		L2Player player = (L2Player) getSelf();
		String html = Files.read("data/scripts/services/Level/index.htm", player);

		html = html.replace("%up_price%", Util.formatAdena(ConfigValue.LevelUp[1]));
		html = html.replace("%up_item%", DifferentMethods.getItemName(ConfigValue.LevelUp[0]));
		html = html.replace("%de_price%", Util.formatAdena(ConfigValue.DeLevel[1]));
		html = html.replace("%de_item%", DifferentMethods.getItemName(ConfigValue.DeLevel[0]));

		show(html, player);
	}

	public void calc(String[] param)
	{
		L2Player player = (L2Player) getSelf();
		String html = Files.read("data/scripts/services/Level/calc.htm", player);

		int price = 0, item = 57;

		try
		{
			byte level = Byte.parseByte(param[0]);

			if(level == player.getLevel())
				return;
			else if(level > player.getLevel())
			{
				price = (level - player.getLevel()) * ConfigValue.LevelUp[1];
				item = ConfigValue.LevelUp[0];
				html = html.replace("%price%", "(" + level + " - " + player.getLevel() + ") * " + Util.formatAdena(ConfigValue.LevelUp[1]) + " = " + Util.formatAdena(price) + " " + DifferentMethods.getItemName(item));
			}
			else if(level < player.getLevel())
			{
				price = (player.getLevel() - level) * ConfigValue.DeLevel[1];
				item = ConfigValue.DeLevel[0];
				html = html.replace("%price%", "(" + player.getLevel() + " - " + level + ") * " + Util.formatAdena(ConfigValue.DeLevel[1]) + " = " + Util.formatAdena(price) + " " + DifferentMethods.getItemName(item));
			}
		}
		catch(Exception e)
		{
			show();
			return;
		}

		html = html.replace("%level%", param[0]);
		show(html, player);
	}

	public void levelup(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		if(!ConfigValue.LevelManipulationEnable)
		{
			player.sendMessage(new CustomMessage("scripts.services.off", player));
			return;
		}

		byte level = Byte.parseByte(param[0]);
		int max = player.isSubClassActive() ? ConfigValue.AltMaxSubLevel : 85;

		if(level > max)
			return;

		int price = 0, item = 57;
		if(level == player.getLevel())
			return;
		else if(level > player.getLevel())
		{
			price = (level - player.getLevel()) * ConfigValue.LevelUp[1];
			item = ConfigValue.LevelUp[0];
		}
		else if(level < player.getLevel())
		{
			price = (player.getLevel() - level) * ConfigValue.DeLevel[1];
			item = ConfigValue.DeLevel[0];
		}

		if(DifferentMethods.getPay(player, item, price, true))
		{
			Long exp_add = Experience.LEVEL[level] - player.getExp();
			player.addExpAndSp(exp_add, 0, false, false);
			resetSkill(player);
			
			if(ConfigValue.AutoLearnSkills)
				giveSkill(player);
			return;
		}

	}

	private void giveSkill(L2Player player)
	{
		int count = 0;
		GArray<L2SkillLearn> skills = player.getAvailableSkills(player.getClassId());
		while(skills.size() > count)
		{
			count = 0;
			for(L2SkillLearn s : skills)
			{
				L2Skill sk = SkillTable.getInstance().getInfo(s.id, s.skillLevel);
				if(sk == null || !sk.getCanLearn(player.getClassId()))
				{
					count++;
					continue;
				}
				player.addSkill(sk, true);
			}
			skills = player.getAvailableSkills(player.getClassId());
		}

		player.sendPacket(new SkillList(player));
	}

	private void resetSkill(L2Player player)
	{
		if(!ConfigValue.OldSkillDelete)
		{
			L2Skill[] skills = player.getAllSkillsArray();
			for(L2Skill element : skills)
				if(!element.isCommon() && !SkillTreeTable.getInstance().isSkillPossible(player, element.getId(), element.getLevel()))
				{
					player.removeSkill(element, true, true);
				}
			player.checkSkills(10);
			player.sendPacket(new SkillList(player));
		}
	}

	public void add()
	{}

	public void do_add(String[] param)
	{}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: Level manipulation");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
