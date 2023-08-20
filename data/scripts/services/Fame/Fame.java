package services.Fame;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Player;
import l2open.util.Files;
import l2open.util.Util;

public class Fame extends Functions implements ScriptFile
{
	public void list()
	{
		L2Player player = (L2Player) getSelf();
		String html = Files.read("data/scripts/services/Fame/index.htm", player);

		String add = new String();
		for(int i = 0; i < ConfigValue.CBFameItem.length; i++)
			add += "<button value=\"Купить "+ConfigValue.CBFamePoint[i]+" очков\" action=\"bypass -h scripts_services.Fame.Fame:get " + i + "\" width=200 height=29 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"><br1>" + new CustomMessage("scripts.services.cost", player).addString(String.valueOf(Util.formatAdena(ConfigValue.CBFameItemPrice[i]))).addItemName(ConfigValue.CBFameItem[i]) + "";

		html = html.replaceFirst("%toreplace%", add);
		show(html, player);
	}

	public void get(String[] param)
	{
		L2Player player = (L2Player) getSelf();

		int var = Integer.parseInt(param[0]);

		if(DifferentMethods.getPay(player, ConfigValue.CBFameItem[var], ConfigValue.CBFameItemPrice[var], true))
		{
			player.setFame(player.getFame() + ConfigValue.CBFamePoint[var], "buy");
		}
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}