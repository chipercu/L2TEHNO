package services.Hero;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2ObjectTasks;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.serverpackets.SocialAction;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.StatsSet;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Util;

public class BuyHero extends Functions implements ScriptFile
{
	public void list()
	{
		L2Player player = (L2Player) getSelf();
		String html;
		if(!player.isHero() && !player.getVarB("HeroPremium"))
		{
			html = Files.read("data/scripts/services/Hero/index.htm", player);

			String add = new String();
			for(int i = 0; i < ConfigValue.CBHeroItem.length; i++)
				add += "<button value=\"" + new CustomMessage("communityboard.cabinet.hero.button", player).addNumber(ConfigValue.CBHeroTime[i]).addString(DifferentMethods.declension(player, ConfigValue.CBHeroTime[i], "Days")) + "\" action=\"bypass -h scripts_services.Hero.BuyHero:get " + i + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"><br1>" + new CustomMessage("scripts.services.cost", player).addString(String.valueOf(Util.formatAdena(ConfigValue.CBHeroItemPrice[i]))).addItemName(ConfigValue.CBHeroItem[i]) + "";

			html = html.replaceFirst("%toreplace%", add);
		}
		else
		{
			html = Files.read("data/scripts/services/Hero/already.htm", player);
			player.sendMessage(new CustomMessage("scripts.services.Hero.ishero", player));
		}
		show(html, player);
	}

	public void get(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		int var = Integer.parseInt(param[0]);

		if(!player.isHero() && !player.getVarB("HeroPremium"))
		{
			if(DifferentMethods.getPay(player, ConfigValue.CBHeroItem[var], ConfigValue.CBHeroItemPrice[var], true))
			{
				StatsSet hero = new StatsSet();
				hero.set("class_id", player.getBaseClassId());
				hero.set("char_id", player.getObjectId());
				hero.set("char_name", player.getName());

				/*GArray<StatsSet> heroesToBe = new GArray<StatsSet>();
				heroesToBe.add(hero);
				Hero.getInstance().computeNewHeroes(heroesToBe);*/

				player.setHero(true, 2);
				long CBHeroTime = ConfigValue.CBHeroTime[var];
				long expire = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * CBHeroTime);
				player.setVar("HeroPremium", String.valueOf(expire), expire);
				if(ConfigValue.PremiumHeroSetSkill)
				{
					player.addSkill(SkillTable.getInstance().getInfo(395, 1));
					player.addSkill(SkillTable.getInstance().getInfo(396, 1));
					player.addSkill(SkillTable.getInstance().getInfo(1374, 1));
					player.addSkill(SkillTable.getInstance().getInfo(1375, 1));
					player.addSkill(SkillTable.getInstance().getInfo(1376, 1));
					player.sendPacket(new SkillList(player));
				}
				if(player.isHero())
					player.broadcastPacket2(new SocialAction(player.getObjectId(), 16));
				player.broadcastUserInfo(true);
				player._heroTask = ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.UnsetHero(player, 2), 1000L * 60 * 60 * 24 * CBHeroTime);
			}
		}
	}

	public void howtogetcol()
	{
		show("data/scripts/services/howtogetcol.htm", (L2Player) getSelf());
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Rate bonus");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}