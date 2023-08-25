package services.petevolve;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Util;

public class exchange extends Functions implements ScriptFile
{
	/** Билеты для обмена **/
	private static final int PEticketB = 7583;
	private static final int PEticketC = 7584;
	private static final int PEticketK = 7585;

	/** Дудки для вызова петов **/
	private static final int BbuffaloP = 6648;
	private static final int BcougarC = 6649;
	private static final int BkookaburraO = 6650;

	public void exch_1()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(getItemCount(player, PEticketB) >= 1)
		{
			removeItem(player, PEticketB, 1);
			addItem(player, BbuffaloP, 1);
			return;
		}
		show(Files.read("data/scripts/services/petevolve/exchange_no.htm", player), player);
	}

	public void exch_2()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(getItemCount(player, PEticketC) >= 1)
		{
			removeItem(player, PEticketC, 1);
			addItem(player, BcougarC, 1);
			return;
		}
		show(Files.read("data/scripts/services/petevolve/exchange_no.htm", player), player);
	}

	public void exch_3()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;

		if(getItemCount(player, PEticketK) >= 1)
		{
			removeItem(player, PEticketK, 1);
			addItem(player, BkookaburraO, 1);
			return;
		}
		show(Files.read("data/scripts/services/petevolve/exchange_no.htm", player), player);
	}

	public void showBabyPetExchange()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.BabyPetExchangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.BabyPetExchangeItem);
		String out = "";
		out += "<html><body>Вы можете в любое время обменять вашего Improved Baby пета на другой вид, без потери опыта. Пет при этом должен быть вызван.";
		out += "<br>Стоимость обмена: " + Util.formatAdena(ConfigValue.BabyPetExchangePrice) + " " + item.getName();
		out += "<br><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToCougar\" value=\"Обменять на Improved Cougar\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToBuffalo\" value=\"Обменять на Improved Buffalo\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToKookaburra\" value=\"Обменять на Improved Kookaburra\">";
		out += "</body></html>";
		show(out, player);
	}

	public void showErasePetName()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.PetNameChangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.PetNameChangeItem);

		String html = Files.read("data/scripts/services/petevolve/exchange.htm", player);
		html = html.replace("<?price?>", Util.formatAdena(ConfigValue.PetNameChangePrice));
		html = html.replace("<?price_name?>", item.getName());
		show(html, player);
	}

	public void erasePetName()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.PetNameChangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || !pl_pet.isPet())
		{
			show(Files.read("data/scripts/services/petevolve/exchange_no_s.htm", player), player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.PetNameChangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.PetNameChangePrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.PetNameChangePrice, true);
			pl_pet.setName(pl_pet.getTemplate().name);
			pl_pet.broadcastPetInfo();

			L2PetInstance _pet = (L2PetInstance) pl_pet;
			L2ItemInstance controlItem = _pet.getControlItem();
			if(controlItem != null)
			{
				controlItem.setCustomType2(1);
				controlItem.setPriceToSell(0);
				controlItem.updateDatabase();
				_pet.updateControlItem();
			}
			show(Files.read("data/scripts/services/petevolve/exchange_ok_clear.htm", player), player);
		}
		else if(ConfigValue.PetNameChangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public void exToCougar()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.BabyPetExchangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			show(Files.read("data/scripts/services/petevolve/exchange_no_s.htm", player), player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.BabyPetExchangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.BabyPetExchangePrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.BabyPetExchangePrice, true);
			L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(10312);
			control.updateDatabase(true, true);
			player.sendPacket(new ItemList(player, false));
			player.getPet().unSummon();
			show(Files.read("data/scripts/services/petevolve/exchange_ok_ch.htm", player), player);
		}
		else if(ConfigValue.BabyPetExchangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public void exToBuffalo()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.BabyPetExchangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			show(Files.read("data/scripts/services/petevolve/exchange_no_s.htm", player), player);
			return;
		}
		if(ConfigValue.ImprovedPetsLimitedUse && player.isMageClass())
		{
			show(Files.read("data/scripts/services/petevolve/exchange_err1.htm", player), player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.BabyPetExchangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.BabyPetExchangePrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.BabyPetExchangePrice, true);
			L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(10311);
			control.updateDatabase(true, true);
			player.sendPacket(new ItemList(player, false));
			player.getPet().unSummon();
			show(Files.read("data/scripts/services/petevolve/exchange_ok_ch.htm", player), player);
		}
		else if(ConfigValue.BabyPetExchangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public void exToKookaburra()
	{
		L2Player player = (L2Player) getSelf();
		if(player == null)
			return;
		if(!ConfigValue.BabyPetExchangeEnabled)
		{
			show("Сервис отключен.", player);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID))
		{
			show(Files.read("data/scripts/services/petevolve/exchange_no_s.htm", player), player);
			return;
		}
		if(ConfigValue.ImprovedPetsLimitedUse && !player.isMageClass())
		{
			show(Files.read("data/scripts/services/petevolve/exchange_err2.htm", player), player);
			return;
		}
		L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.BabyPetExchangeItem);
		L2ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if(pay != null && pay.getCount() >= ConfigValue.BabyPetExchangePrice)
		{
			player.getInventory().destroyItem(pay, ConfigValue.BabyPetExchangePrice, true);
			L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(10313);
			control.updateDatabase(true, true);
			player.sendPacket(new ItemList(player, false));
			player.getPet().unSummon();
			show(Files.read("data/scripts/services/petevolve/exchange_ok_ch.htm", player), player);
		}
		else if(ConfigValue.BabyPetExchangeItem == 57)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(Msg.INCORRECT_ITEM_COUNT);
	}

	public static String DialogAppend_30731(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30827(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30828(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30829(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30830(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30831(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30869(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31067(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31265(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31309(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31954(Integer val)
	{
		return getHtmlAppends(val);
	}

	private static String getHtmlAppends(Integer val)
	{
		String ret = "";
		if(val != 0)
			return ret;
		if(ConfigValue.PetNameChangeEnabled)
			ret = "<br>[scripts_services.petevolve.exchange:showErasePetName|Обнулить имя у пета]";
		if(ConfigValue.BabyPetExchangeEnabled)
			ret += "<br>[scripts_services.petevolve.exchange:showBabyPetExchange|Обменять Improved Baby пета]";
		return ret;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}