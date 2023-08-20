package services;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.EnterWorld;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;

public class Window extends Functions implements ScriptFile
{
	public void get()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		show("Сервис отключен.", player);

		/*if(!ConfigValue.WindowEnabled || ConfigValue.PROTECT_GS_MAX_SAME_HWIDs == 0 || player.getNetConnection() == null || !player.getNetConnection().protect_used)
		{
			show("Сервис отключен.", player);
			return;
		}

		int size = HWID.getBonus(player, "window", EnterWorld.WindowsBonusComparator);

		if(ConfigValue.PROTECT_GS_MAX_SAME_HWIDs + size >= ConfigValue.SERVICES_WINDOW_MAX)
		{
			player.sendMessage("Already max count.");
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.SERVICES_WINDOW_ITEM);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.SERVICES_WINDOW_PRICE)
		{
			player.getInventory().destroyItem(pay, ConfigValue.SERVICES_WINDOW_PRICE, true);
			HWID.setBonus(player.getHWID(), "window", size + 1);
			player.sendMessage("Max window count is now " + (ConfigValue.PROTECT_GS_MAX_SAME_HWIDs + size + 1));
		}
		else if(ConfigValue.SERVICES_WINDOW_ITEM == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);

		show();*/
	}

	public void show()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		show("Сервис отключен.", player);

		/*if(!ConfigValue.WindowEnabled || ConfigValue.PROTECT_GS_MAX_SAME_HWIDs == 0 || player.getNetConnection() == null || !player.getNetConnection().protect_used)
		{
			show("Сервис отключен.", player);
			return;
		}

		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.SERVICES_WINDOW_ITEM);

		String out = "";

		out += "<html><body>Дополнительные окна:";
		out += "<br><br><table>";
		out += "<tr><td>Текущее число:</td><td>" + (ConfigValue.PROTECT_GS_MAX_SAME_HWIDs + HWID.getBonus(player, "window")) + " окон</td></tr>";
		out += "<tr><td>Максимальное число:</td><td>" + ConfigValue.SERVICES_WINDOW_MAX + " окон</td></tr>";
		out += "<tr><td>Стоимость расширения:</td><td>" + ConfigValue.SERVICES_WINDOW_PRICE + " " + item.getName() + "</td></tr>";
		out += "<tr><td>Время действия:</td><td>" + ConfigValue.SERVICES_WINDOW_DAYS + " дней</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.Window:get\" value=\"Расширить\">";
		out += "</body></html>";

		show(out, player);*/
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Window");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}