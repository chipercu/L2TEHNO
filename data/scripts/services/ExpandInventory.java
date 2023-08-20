package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;

public class ExpandInventory extends Functions implements ScriptFile
{
	public void get()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.ExpandInventoryEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}

		if(player.getInventoryLimit() >= ConfigValue.ExpandInventoryMax)
		{
			player.sendMessage("Already max count.");
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandInventoryItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.ExpandInventoryPrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.ExpandInventoryPrice, true);
			player.setExpandInventory(player.getExpandInventory() + 1);
			player.setVar("ExpandInventory", String.valueOf(player.getExpandInventory()));
			player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
		}
		else if(ConfigValue.ExpandInventoryItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);

		show();
	}

	public void show()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(!ConfigValue.ExpandInventoryEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.ExpandInventoryItem);

		String html = Files.read("data/scripts/services/ExpandInventory.htm", player);
		html = html.replace("<?arg1?>", String.valueOf(player.getInventoryLimit()));
		html = html.replace("<?arg2?>", String.valueOf(ConfigValue.ExpandInventoryMax));
		html = html.replace("<?arg3?>", String.valueOf(ConfigValue.ExpandInventoryPrice + " " + item.getName()));
		show(html, player);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Expand Inventory");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}