package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javolution.util.FastMap;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.RecipeController;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.geodata.GeoMove;
import l2open.gameserver.geodata.PathFind;
import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2ManufactureList;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Recipe;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.TradeItem;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance.ItemClass;
import l2open.gameserver.serverpackets.ExShowTrace;
import l2open.gameserver.serverpackets.PrivateStoreMsgBuy;
import l2open.gameserver.serverpackets.PrivateStoreMsgSell;
import l2open.gameserver.serverpackets.RecipeShopMsg;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Item.Grade;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Util;
import l2open.util.reference.*;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2TradeList;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.SystemMessage;

public class ItemBroker extends Functions implements ScriptFile
{
	private static FastMap<Integer, NpcInfo> _npcInfos = new FastMap<Integer, NpcInfo>().setShared(true);

	public class NpcInfo
	{
		public long lastUpdate;
		public TreeMap<String, TreeMap<Long, Item>> bestSellItems;
		public TreeMap<String, TreeMap<Long, Item>> bestBuyItems;
		public TreeMap<String, TreeMap<Long, Item>> bestCraftItems;
	}

	public class Item
	{
		public int itemId;
		public int itemObjId;
		public int type;
		public long price;
		public long count;
		public long enchant;
		public String name;
		public String merchantName;
		public List<Location> path;
		public Element element;
		private HardReference<L2Player> merchant = HardReferences.emptyRef();

		public Item(int itemId, int type, long price, long count, int enchant, String itemName, L2Player player, String merchantName, List<Location> path, int itemObjId, Element element)
		{
			this.itemId = itemId;
			this.type = type;
			this.price = price;
			this.count = count;
			this.enchant = enchant;
			this.name = itemName;
			this.merchant = player.getRef();
			this.merchantName = merchantName;
			this.path = path;
			this.itemObjId = itemObjId;
			this.element = element;
		}
	}

	public class Element
	{
		public int[] attackElement;
		public int defenceFire;
		public int defenceWater;
		public int defenceWind;
		public int defenceEarth;
		public int defenceHoly;
		public int defenceUnholy;

		public Element(TradeItem item)
		{
			attackElement = item.getAttackElement();
			defenceEarth = item.getDefenceEarth();
			defenceFire = item.getDefenceFire();
			defenceHoly = item.getDefenceHoly();
			defenceUnholy = item.getDefenceUnholy();
			defenceWater = item.getDefenceWater();
			defenceWind = item.getDefenceWind();
		}
	}

	private String parseElement(Item item)
	{
		String element = "";
		if(item.element != null)
			if(item.element.attackElement != null && item.element.attackElement[0] != L2Item.ATTRIBUTE_NONE)
			{
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.attackElement[1];
				switch(item.element.attackElement[0])
				{
					case L2Item.ATTRIBUTE_FIRE:
						element += " Fire";
						break;
					case L2Item.ATTRIBUTE_WATER:
						element += " Water";
						break;
					case L2Item.ATTRIBUTE_WIND:
						element += " Wind";
						break;
					case L2Item.ATTRIBUTE_EARTH:
						element += " Earth";
						break;
					case L2Item.ATTRIBUTE_HOLY:
						element += " Holy";
						break;
					case L2Item.ATTRIBUTE_DARK:
						element += " Unholy";
						break;
				}
				element += "</font>";
			}
			else if(item.element.defenceFire > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceFire + " Fire</font>";
			else if(item.element.defenceWater > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceWater + " Water</font>";
			else if(item.element.defenceWind > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceWind + " Wind</font>";
			else if(item.element.defenceEarth > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceEarth + " Earth</font>";
			else if(item.element.defenceHoly > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceHoly + " Holy</font>";
			else if(item.element.defenceUnholy > 0)
				element = " &nbsp;<font color=\"7CFC00\">+" + item.element.defenceUnholy + " Unholy</font>";
		return element;
	}

	private TreeMap<String, TreeMap<Long, Item>> getItems(int type)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null/* || npc == null*/)
			return null;
		updateInfo(player, npc);
		NpcInfo info = _npcInfos.get(npc == null ? 0 : npc.getObjectId());
		if(info == null)
			return null;
		switch(type)
		{
			case L2Player.STORE_PRIVATE_SELL:
				return info.bestSellItems;
			case L2Player.STORE_PRIVATE_BUY:
				return info.bestBuyItems;
			case L2Player.STORE_PRIVATE_MANUFACTURE:
				return info.bestCraftItems;
		}
		return null;
	}

	public String DialogAppend_32320(Integer val)
	{
		return call_item_list(new String[]{String.valueOf(val)}, true);
	}

	public String DialogAppend_32321(Integer val)
	{
		return call_item_list(new String[]{String.valueOf(val)}, true);
	}

	public String DialogAppend_32322(Integer val)
	{
		return call_item_list(new String[]{String.valueOf(val)}, true);
	}

	public void call_item_list(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		call_item_list(Integer.parseInt(var[0]), player);
	}

	public static void call_item_list(int val, L2Player player)
	{
		StringBuffer append = new StringBuffer();
		int type = 0;
		String typeNameRu = "";
		String typeNameEn = "";

		switch(val)
		{
			case 0:
				if(player.isLangRus())
				{
					append.append("<br><font color=\"LEVEL\">Поиск торговцев:</font><br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 11|<font color=\"FF9900\">Список продаваемых товаров</font>]<br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 13|<font color=\"FF9900\">Список покупаемых товаров</font>]<br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 15|<font color=\"FF9900\">Список создаваемых товаров</font>]<br1>");
				}
				else
				{
					append.append("<br><font color=\"LEVEL\">Search for dealers:</font><br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 11|<font color=\"FF9900\">The list of goods for sale</font>]<br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 13|<font color=\"FF9900\">The list of goods to buy</font>]<br1>");
					append.append("[scripts_services.ItemBroker:call_item_list 15|<font color=\"FF9900\">The list of goods to craft</font>]<br1>");
				}
				break;
			case 11:
				type = L2Player.STORE_PRIVATE_SELL;
				typeNameRu = "продаваемых";
				typeNameEn = "sell";
				break;
			case 13:
				type = L2Player.STORE_PRIVATE_BUY;
				typeNameRu = "покупаемых";
				typeNameEn = "buy";
				break;
			case 15:
				type = L2Player.STORE_PRIVATE_MANUFACTURE;
				typeNameRu = "создаваемых";
				typeNameEn = "craft";
				break;
			case 20 + L2Player.STORE_PRIVATE_SELL:
			case 20 + L2Player.STORE_PRIVATE_BUY:
			case 20 + L2Player.STORE_PRIVATE_MANUFACTURE:
				// Обычное снаряжение
				type = val - 20;
				if(player.isLangRus())
				{
					append.append("!Список снаряжения:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 0 0|<font color=\"FF9900\">Оружие</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 0 0|<font color=\"FF9900\">Броня</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 0 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 0 0|<font color=\"FF9900\">Украшения</font>]<br1>");

					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
				}
				else
				{
					append.append("!The list of equipment:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 0 0|<font color=\"FF9900\">Weapons</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 0 0|<font color=\"FF9900\">Armors</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 0 0|<font color=\"FF9900\">Jewels</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 0 0|<font color=\"FF9900\">Accessories</font>]<br1>");

					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
				}
				show(append.toString(), player, null);
				return;
			case 30 + L2Player.STORE_PRIVATE_SELL:
			case 30 + L2Player.STORE_PRIVATE_BUY:
			case 30 + L2Player.STORE_PRIVATE_MANUFACTURE:
				// Заточенное снаряжение
				type = val - 30;
				if(player.isLangRus())
				{
					append.append("!Список снаряжения, заточенного на +1 и выше:<br>");

					
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 1 0|<font color=\"FF9900\">Оружие</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 1 0|<font color=\"FF9900\">Броня</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 1 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 1 0|<font color=\"FF9900\">Украшения</font>]<br1>");

					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
				}
				else
				{
					append.append("!The list of equipment, enchanted to +4 and more:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 1 0|<font color=\"FF9900\">Weapons+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 1 0|<font color=\"FF9900\">Armors+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 1 0|<font color=\"FF9900\">Jewels+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 1 0|<font color=\"FF9900\">Accessories+</font>]<br1>");

					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
				}
				show(append.toString(), player, null);
				return;
		}

		if(type > 0)
			if(player.isLangRus())
			{
				append.append("!Список ").append(typeNameRu).append(" товаров:<br>");

				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 1 0 0|<font color=\"FF9900\">Весь список</font>]<br1>");

				append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(20 + type).append("|<font color=\"FF9900\">Снаряжение</font>]<br1>");

				if(type == L2Player.STORE_PRIVATE_SELL)
				{
					append.append("[scripts_services.ItemBroker:call_item_list ").append(30 + type).append("|<font color=\"FF9900\">Снаряжение+</font>]<br1>");
				}

				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 1 0 1|<font color=\"FF9900\">Редкое снаряжение</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 1 0 0|<font color=\"FF9900\">Расходные материалы</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 1 0 0|<font color=\"FF9900\">Ингредиенты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 1 0 0|<font color=\"FF9900\">Ключевые ингредиенты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 5 1 1 0 0|<font color=\"FF9900\">Рецепты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 6 1 1 0 0|<font color=\"FF9900\">Книги и амулеты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 7 1 1 0 0|<font color=\"FF9900\">Предметы для улучшения</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 8 1 1 0 0|<font color=\"FF9900\">Разное</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 9 1 1 0 0|<font color=\"FF9900\">Стандартные предметы</font>]<br1>");

				append.append("<edit var=\"tofind\" width=100><br1>");

				append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 $tofind|<font color=\"FF9900\">Найти</font>]<br1>");
			}
			else
			{
				append.append("!The list of goods to ").append(typeNameEn).append(":<br>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 1 0 0|<font color=\"FF9900\">List all</font>]<br1>");
				append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(20 + type).append("|<font color=\"FF9900\">Equipment</font>]");
				if(type == L2Player.STORE_PRIVATE_SELL)
				{
					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(30 + type).append("|<font color=\"FF9900\">Equipment+</font>]");
				}
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 1 0 1|<font color=\"FF9900\">Rare equipment</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 1 0 0|<font color=\"FF9900\">Consumable</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 1 0 0|<font color=\"FF9900\">Matherials</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 1 0 0|<font color=\"FF9900\">Key matherials</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 5 1 1 0 0|<font color=\"FF9900\">Recipies</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 6 1 1 0 0|<font color=\"FF9900\">Books and amulets</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 7 1 1 0 0|<font color=\"FF9900\">Enchant items</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 8 1 1 0 0|<font color=\"FF9900\">Other</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 9 1 1 0 0|<font color=\"FF9900\">Commons</font>]<br1>");

				append.append("<edit var=\"tofind\" width=100><br1>");

				append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 $tofind|<font color=\"FF9900\">Find</font>]<br1>");
			}
			show(append.toString(), player, null);
	}
	
	public String call_item_list(String[] var, boolean npc)
	{
		StringBuffer append = new StringBuffer();
		int type = 0;
		String typeNameRu = "";
		String typeNameEn = "";
		int val = Integer.parseInt(var[0]);
		L2Player player = (L2Player) getSelf();

		switch(val)
		{
			case 0:
				if(player.isLangRus())
				{
					append.append("<br><font color=\"LEVEL\">Поиск торговцев:</font><br1>");
					append.append("[npc_%objectId%_Chat 11|<font color=\"FF9900\">Список продаваемых товаров</font>]<br1>");
					append.append("[npc_%objectId%_Chat 13|<font color=\"FF9900\">Список покупаемых товаров</font>]<br1>");
					append.append("[npc_%objectId%_Chat 15|<font color=\"FF9900\">Список создаваемых товаров</font>]<br1>");
				}
				else
				{
					append.append("<br><font color=\"LEVEL\">Search for dealers:</font><br1>");
					append.append("[npc_%objectId%_Chat 11|<font color=\"FF9900\">The list of goods for sale</font>]<br1>");
					append.append("[npc_%objectId%_Chat 13|<font color=\"FF9900\">The list of goods to buy</font>]<br1>");
					append.append("[npc_%objectId%_Chat 15|<font color=\"FF9900\">The list of goods to craft</font>]<br1>");
				}
				break;
			case 11:
				type = L2Player.STORE_PRIVATE_SELL;
				typeNameRu = "продаваемых";
				typeNameEn = "sell";
				break;
			case 13:
				type = L2Player.STORE_PRIVATE_BUY;
				typeNameRu = "покупаемых";
				typeNameEn = "buy";
				break;
			case 15:
				type = L2Player.STORE_PRIVATE_MANUFACTURE;
				typeNameRu = "создаваемых";
				typeNameEn = "craft";
				break;
			case 20 + L2Player.STORE_PRIVATE_SELL:
			case 20 + L2Player.STORE_PRIVATE_BUY:
			case 20 + L2Player.STORE_PRIVATE_MANUFACTURE:
				// Обычное снаряжение
				type = val - 20;
				if(player.isLangRus())
				{
					append.append("!Список снаряжения:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 0 0|<font color=\"FF9900\">Оружие</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 0 0|<font color=\"FF9900\">Броня</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 0 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 0 0|<font color=\"FF9900\">Украшения</font>]<br1>");

					if(npc)
						append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
					else
						append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
				}
				else
				{
					append.append("!The list of equipment:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 0 0|<font color=\"FF9900\">Weapons</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 0 0|<font color=\"FF9900\">Armors</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 0 0|<font color=\"FF9900\">Jewels</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 0 0|<font color=\"FF9900\">Accessories</font>]<br1>");

					if(npc)
						append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
					else
						append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
				}
				if(!npc)
					show(append.toString(), player, null);
				return append.toString();
			case 30 + L2Player.STORE_PRIVATE_SELL:
			case 30 + L2Player.STORE_PRIVATE_BUY:
			case 30 + L2Player.STORE_PRIVATE_MANUFACTURE:
				// Заточенное снаряжение
				type = val - 30;
				if(player.isLangRus())
				{
					append.append("!Список снаряжения, заточенного на +1 и выше:<br>");

					
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 1 0|<font color=\"FF9900\">Оружие</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 1 0|<font color=\"FF9900\">Броня</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 1 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 1 0|<font color=\"FF9900\">Украшения</font>]<br1>");

					if(npc)
						append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
					else
						append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Назад</font>]");
				}
				else
				{
					append.append("!The list of equipment, enchanted to +4 and more:<br>");

					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 21 1 1 1 0|<font color=\"FF9900\">Weapons+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 22 1 1 1 0|<font color=\"FF9900\">Armors+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 23 1 1 1 0|<font color=\"FF9900\">Jewels+</font>]<br1>");
					append.append("[scripts_services.ItemBroker:list ").append(type).append(" 24 1 1 1 0|<font color=\"FF9900\">Accessories+</font>]<br1>");

					if(npc)
						append.append("<br>[npc_%objectId%_Chat ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
					else
						append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(10 + type).append("|<font color=\"FF9900\">Back</font>]");
				}
				if(!npc)
					show(append.toString(), player, null);
				return append.toString();

		}

		if(type > 0)
			if(player.isLangRus())
			{
				append.append("!Список ").append(typeNameRu).append(" товаров:<br>");

				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 1 0 0|<font color=\"FF9900\">Весь список</font>]<br1>");
				
				
				if(npc)
					append.append("<br>[npc_%objectId%_Chat ").append(20 + type).append("|<font color=\"FF9900\">Снаряжение</font>]<br1>");
				else
					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(20 + type).append("|<font color=\"FF9900\">Снаряжение</font>]<br1>");

				if(type == L2Player.STORE_PRIVATE_SELL)
				{
					if(npc)
						append.append("[npc_%objectId%_Chat ").append(30 + type).append("|<font color=\"FF9900\">Снаряжение+</font>]<br1>");
					else
						append.append("[scripts_services.ItemBroker:call_item_list ").append(30 + type).append("|<font color=\"FF9900\">Снаряжение+</font>]<br1>");
				}

				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 1 0 1|<font color=\"FF9900\">Редкое снаряжение</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 1 0 0|<font color=\"FF9900\">Расходные материалы</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 1 0 0|<font color=\"FF9900\">Ингредиенты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 1 0 0|<font color=\"FF9900\">Ключевые ингредиенты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 5 1 1 0 0|<font color=\"FF9900\">Рецепты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 6 1 1 0 0|<font color=\"FF9900\">Книги и амулеты</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 7 1 1 0 0|<font color=\"FF9900\">Предметы для улучшения</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 8 1 1 0 0|<font color=\"FF9900\">Разное</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 9 1 1 0 0|<font color=\"FF9900\">Стандартные предметы</font>]<br1>");

				append.append("<edit var=\"tofind\" width=100><br1>");

				if(npc)
				{
					append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 \\$tofind|<font color=\"FF9900\">Найти</font>]<br1>");
					append.append("<br>[npc_%objectId%_Chat 0").append("|<font color=\"FF9900\">Назад</font>]");
				}
				else
					append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 $tofind|<font color=\"FF9900\">Найти</font>]<br1>");
			}
			else
			{
				append.append("!The list of goods to ").append(typeNameEn).append(":<br>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 0 1 1 0 0|<font color=\"FF9900\">List all</font>]<br1>");
				if(npc)
					append.append("<br>[npc_%objectId%_Chat ").append(20 + type).append("|<font color=\"FF9900\">Equipment</font>]");
				else
					append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(20 + type).append("|<font color=\"FF9900\">Equipment</font>]");
				if(type == L2Player.STORE_PRIVATE_SELL)
				{
					if(npc)
						append.append("<br>[npc_%objectId%_Chat ").append(30 + type).append("|<font color=\"FF9900\">Equipment+</font>]");
					else
						append.append("<br>[scripts_services.ItemBroker:call_item_list ").append(30 + type).append("|<font color=\"FF9900\">Equipment+</font>]");
				}
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 1 1 1 0 1|<font color=\"FF9900\">Rare equipment</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 2 1 1 0 0|<font color=\"FF9900\">Consumable</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 3 1 1 0 0|<font color=\"FF9900\">Matherials</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 4 1 1 0 0|<font color=\"FF9900\">Key matherials</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 5 1 1 0 0|<font color=\"FF9900\">Recipies</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 6 1 1 0 0|<font color=\"FF9900\">Books and amulets</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 7 1 1 0 0|<font color=\"FF9900\">Enchant items</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 8 1 1 0 0|<font color=\"FF9900\">Other</font>]<br1>");
				append.append("[scripts_services.ItemBroker:list ").append(type).append(" 9 1 1 0 0|<font color=\"FF9900\">Commons</font>]<br1>");

				append.append("<edit var=\"tofind\" width=100><br1>");

				if(npc)
				{
					append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 \\$tofind|<font color=\"FF9900\">Find</font>]<br1>");
					append.append("<br>[npc_%objectId%_Chat 0").append("|<font color=\"FF9900\">Back</font>]");
				}
				else
					append.append("[scripts_services.ItemBroker:find ").append(type).append(" 1 1 $tofind|<font color=\"FF9900\">Find</font>]<br1>");
			}

		if(!npc)
			show(append.toString(), player, null);
		return append.toString();
	}

	public void list(String[] var)
	{
		int countPerPage = 9;

		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		//L2NpcInstance npc = null;
		if(player == null/* || npc == null*/)
			return;

		if(var.length != 6)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		int type;
		int itemType;
		int min;
		int max;
		int minEnchant;
		int rare;

		try
		{
			type = Integer.valueOf(var[0]);
			itemType = Integer.valueOf(var[1]);
			min = Integer.valueOf(var[2]);
			max = Integer.valueOf(var[3]);
			minEnchant = Integer.valueOf(var[4]);
			rare = Integer.valueOf(var[5]);
		}
		catch(Exception e)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		if(max < countPerPage)
			max = countPerPage;

		ItemClass itemClass = null;
		if(itemType > 20)
			itemClass = ItemClass.values()[1];
		else if(itemType == 9);
		else if(itemType > 8)
			itemClass = ItemClass.values()[0];
		else
			itemClass = ItemClass.values()[itemType];

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if(allItems == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		GArray<Item> items = new GArray<Item>();
		for(TreeMap<Long, Item> tempItems : allItems.values())
		{
			TreeMap<Long, Item> tempItems2 = new TreeMap<Long, Item>();
			for(Entry<Long, Item> entry : tempItems.entrySet())
			{
				Item tempItem = entry.getValue();
				if(tempItem == null)
					continue;
				else if(tempItem.enchant < minEnchant)
					continue;
				L2Item temp = ItemTemplates.getInstance().getTemplate(tempItem.itemId);
				if(temp == null || rare > 0 && !temp.isRare())
					continue;
				else if(itemClass == null ? !temp.isCommonItem() : temp.isCommonItem())
					continue;
				else if(itemClass != null && itemClass != ItemClass.ALL && temp.getItemClass() != itemClass)
					continue;
				else if(itemType == 7 && temp.getCrystalType() != Grade.NONE)
					continue;
				else if(itemType > 20)
				{
					if(itemType == 21 && !temp.isWeapon() || itemType == 22 && (temp.getBodyPart() & L2Item.SLOTS_ARMOR) <= 0 || itemType == 23 && (temp.getBodyPart() & L2Item.SLOTS_JEWELRY) <= 0 || itemType == 24 && (!temp.isAccessory() || (temp.getBodyPart() & L2Item.SLOTS_JEWELRY) > 0))
						continue;
				}
				tempItems2.put(entry.getKey(), tempItem);
			}
			if(tempItems2.isEmpty())
				continue;

			Item item = type == L2Player.STORE_PRIVATE_BUY ? tempItems2.lastEntry().getValue() : tempItems2.firstEntry().getValue();
			if(item != null)
				items.add(item);
		}
		StringBuffer out = new StringBuffer(npc != null ? ("[npc_%objectId%_Chat " + ("1" + type) + "|««]&nbsp;") : ("[scripts_services.ItemBroker:call_item_list " + ("1" + type) + "|««]&nbsp;"));

		int pages = Math.max(1, items.size() / countPerPage + 1);
		if(pages > 1)
			for(int j = 1; j <= pages; j++)
				if(min == (j - 1) * countPerPage + 1)
					out.append(j).append("&nbsp;");
				else
					out.append("[scripts_services.ItemBroker:list ").append(type).append(" ").append(itemType).append(" ").append(((j - 1) * countPerPage + 1)).append(" ").append((j * countPerPage)).append(" ").append(minEnchant).append(" ").append(rare).append("|").append(j).append("]&nbsp;");

		out.append("<table width=100%>");

		int i = 0;
		for(Item item : items)
		{
			i++;
			if(i < min || i > max)
				continue;
			L2Item temp = ItemTemplates.getInstance().getTemplate(item.itemId);
			if(temp == null)
				continue;

			String icon = "<img src=" + temp.getIcon() + " width=32 height=32>";

			String color = "<font color=\"LEVEL\">";
			if(item.enchant > 0)
				color = "<font color=\"7CFC00\">+" + item.enchant + " ";
			if(temp.isRare())
				color = "<font color=\"0000FF\">Rare ";
			if(temp.isRare() && item.enchant > 0)
				color = "<font color=\"FF0000\">+" + item.enchant + " Rare ";

			out.append("<tr><td>").append(icon);
			out.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ").append(type).append(" ").append(item.itemId).append(" ").append(minEnchant).append(" ").append(rare).append(" ").append(itemType).append(" ").append(min).append(" ").append(max).append("|");
			out.append(color).append(item.name).append("</font>]").append(parseElement(item)).append("</td></tr><tr><td>price: ").append(Util.formatAdena(item.price));
			if(temp.isStackable())
				out.append(", count: ").append(Util.formatAdena(item.count));
			out.append("</td></tr></table></td></tr>");
		}
		out.append("</table><br>&nbsp;");

		show(out.toString(), player, npc);
	}

	public void listForItem(String[] var)
	{
		int maxItems = 20;

		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		//L2NpcInstance npc = null;
		if(player == null/* || npc == null*/)
			return;

		if(var.length != 7)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		int type;
		int itemId;
		int minEnchant;
		int rare;
		// нужны только для запоминания, на какую страницу возвращаться
		int itemType;
		int min;
		int max;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			minEnchant = Integer.valueOf(var[2]);
			rare = Integer.valueOf(var[3]);
			itemType = Integer.valueOf(var[4]);
			min = Integer.valueOf(var[5]);
			max = Integer.valueOf(var[6]);
		}
		catch(Exception e)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
		if(template == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if(allItems == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		TreeMap<Long, Item> items = allItems.get(template.getName());
		if(items == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		StringBuffer out = new StringBuffer("[scripts_services.ItemBroker:list " + type + " " + itemType + " " + min + " " + max + " " + minEnchant + " " + rare + "|««]");

		out.append("<table width=100%>");

		NavigableMap<Long, Item> sortedItems = type == L2Player.STORE_PRIVATE_BUY ? items.descendingMap() : items;
		if(sortedItems == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		int i = 0;
		for(Item item : sortedItems.values())
		{
			if(item.enchant < minEnchant)
				continue;
			L2Item temp = ItemTemplates.getInstance().getTemplate(item.itemId);
			if(temp == null || rare > 0 && !temp.isRare())
				continue;

			i++;
			if(i > maxItems)
				break;

			String icon = "<img src=" + temp.getIcon() + " width=32 height=32>";

			String color = "<font color=\"LEVEL\">";
			if(item.enchant > 0)
				color = "<font color=\"7CFC00\">+" + item.enchant + " ";
			if(temp.isRare())
				color = "<font color=\"0000FF\">Rare ";
			if(temp.isRare() && item.enchant > 0)
				color = "<font color=\"FF0000\">+" + item.enchant + " Rare ";

			out.append("<tr><td>").append(icon);
			out.append("</td><td><table border=0 width=100%><tr><td>");
			if(npc == null)
				out.append(color).append(item.name).append("</font>");
			else
				out.append("[scripts_services.ItemBroker:path ").append(type).append(" ").append(item.itemId).append(" ").append(item.itemObjId).append("|").append(color).append(item.name).append("</font>]");
			out.append(parseElement(item)).append("</td></tr><tr><td>price: ").append(Util.formatAdena(item.price));
			if(temp.isStackable())
				out.append(", count: ").append(Util.formatAdena(item.count));
			out.append(", owner: ").append(item.merchantName);
			out.append("</td></tr></table>");
			if(npc == null && item.merchant.get() != null)
			{
				out.append("<table border=0 width=100%><tr>");
				if(item.count == 1)
					out.append("<td valign=top align=center width=140></td>");
				else
					out.append("<td valign=top align=center width=140><edit var=\"count_items"+i+"\" type=text width=140 length=25 height=16></td>");
				out.append("<td><button value=Купить action=\"bypass -h scripts_services.ItemBroker:call_buy_item ");
				out.append(item.merchant.get().getObjectId()).append(" ");
				out.append(1).append(" ");
				out.append(item.itemObjId).append(" "); // object id
				if(item.count == 1)
					out.append(item.count).append(" "); // count
				else
					out.append("$count_items"+i).append(" "); // count
				out.append(item.price); // price
				out.append("\" width=50 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
				out.append("</table></td></tr>");
			}
			else
				out.append("</td></tr>");
		}
		out.append("</table><br>&nbsp;");

		show(out.toString(), player, npc);
	}

	public void path(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		//L2NpcInstance npc = null;
		if(player == null/* || npc == null*/)
			return;

		if(var.length != 3)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		int type;
		int itemId;
		int itemObjId;

		try
		{
			type = Integer.valueOf(var[0]);
			itemId = Integer.valueOf(var[1]);
			itemObjId = Integer.valueOf(var[2]);
		}
		catch(Exception e)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		L2Item temp = ItemTemplates.getInstance().getTemplate(itemId);
		if(temp == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if(allItems == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		TreeMap<Long, Item> items = allItems.get(temp.getName());
		if(items == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		Item item = null;
		for(Item i : items.values())
			if(i.itemObjId == itemObjId)
			{
				item = i;
				break;
			}

		if(item == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		if(item.path != null && !item.path.isEmpty())
			player.sendPacket(Points2Trace(player, item.path, 50, 60000));

        L2Player trader = item.merchant.get();
		if(trader != null)
		{
			// Показываем игроку торговца, если тот скрыт
			if(player.getVarB("notraders"))
			{
				if(trader != null)
				{
					player.sendPacket(trader.newCharInfo());
					if(trader.getPrivateStoreType() == L2Player.STORE_PRIVATE_BUY)
						player.sendPacket(new PrivateStoreMsgBuy(trader));
					else if(trader.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL)
						player.sendPacket(new PrivateStoreMsgSell(trader, false));
					else if(trader.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
						player.sendPacket(new PrivateStoreMsgSell(trader, true));
					else if(trader.getPrivateStoreType() == L2Player.STORE_PRIVATE_MANUFACTURE)
						player.sendPacket(new RecipeShopMsg(trader));
				}
			}
			
			//Ставим таргет на торговца
			player.setTarget(trader);
			//Указыавем стрелкой на троговца + ставим отметку на карте
			player.sendPacket(new RadarControl(2, 2,  trader.getX(), trader.getY(), trader.getZ()), new RadarControl(0, 1,  trader.getX(), trader.getY(), trader.getZ()));
		}
	}

	public void updateInfo(L2Player player, L2NpcInstance npc)
	{
		NpcInfo info = _npcInfos.get(npc == null ? 0 : npc.getObjectId());
		if(info == null || info.lastUpdate < System.currentTimeMillis() - l2open.config.ConfigValue.ItemBrokerUpdateTime * 1000)
		{
			info = new NpcInfo();
			info.lastUpdate = System.currentTimeMillis();
			info.bestBuyItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestSellItems = new TreeMap<String, TreeMap<Long, Item>>();
			info.bestCraftItems = new TreeMap<String, TreeMap<Long, Item>>();

			int itemObjId = 0; // Обычный objId не подходит для покупаемых предметов

			Collection<L2Player> player_list = npc == null ? L2ObjectsStorage.getPlayers() : L2World.getAroundPlayers(npc, 4000, 400);
			for(L2Player pl : player_list)
			{
				int type = pl.getPrivateStoreType();
				if(type == L2Player.STORE_PRIVATE_SELL || type == L2Player.STORE_PRIVATE_BUY || type == L2Player.STORE_PRIVATE_MANUFACTURE)
				{
					List<Location> path = new ArrayList<Location>();
					if(npc != null)
					{
						//if(GeoEngine.canMoveToCoord(npc.getX(), npc.getY(), npc.getZ(), pl.getX(), pl.getY(), pl.getZ(), npc.getReflection().getGeoIndex()))
						//{
							path.add(npc.getLoc());
							path.add(pl.getLoc());
						//}
						//else
							//path = GeoMove.findPath(npc.getX(), npc.getY(), npc.getZ(), new Location(pl), player, false, npc.getReflection().getGeoIndex());
							//path = PathFind.findPath(npc.getX(), npc.getY(), npc.getZ(), pl.getX(), pl.getY(), pl.getZ(), true, npc.getReflection().getGeoIndex());
					}
					TreeMap<String, TreeMap<Long, Item>> items = null;
					ConcurrentLinkedQueue<TradeItem> tradeList = null;

					switch(type)
					{
						case L2Player.STORE_PRIVATE_SELL:
							items = info.bestSellItems;
							tradeList = pl.getSellList();

							for(TradeItem item : tradeList)
							{
								L2Item temp = ItemTemplates.getInstance().getTemplate(item.getItemId());
								if(temp == null)
									continue;
								TreeMap<Long, Item> oldItems = items.get(temp.getName());
								if(oldItems == null)
								{
									oldItems = new TreeMap<Long, Item>();
									items.put(temp.getName(), oldItems);
								}
								Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl, pl.getName(), path, item.getObjectId(), new Element(item));
								long key = newItem.price * 100;
								while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
									// До 100 предметов с одинаковыми ценами
									key++;
								oldItems.put(key, newItem);
							}
							break;
						case L2Player.STORE_PRIVATE_BUY:
							items = info.bestBuyItems;
							tradeList = pl.getBuyList();

							for(TradeItem item : tradeList)
							{
								L2Item temp = ItemTemplates.getInstance().getTemplate(item.getItemId());
								if(temp == null)
									continue;
								TreeMap<Long, Item> oldItems = items.get(temp.getName());
								if(oldItems == null)
								{
									oldItems = new TreeMap<Long, Item>();
									items.put(temp.getName(), oldItems);
								}
								Item newItem = new Item(item.getItemId(), type, item.getOwnersPrice(), item.getCount(), item.getEnchantLevel(), temp.getName(), pl, pl.getName(), path, itemObjId++, new Element(item));
								long key = newItem.price * 100;
								while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
									// До 100 предметов с одинаковыми ценами
									key++;
								oldItems.put(key, newItem);
							}
							break;
						case L2Player.STORE_PRIVATE_MANUFACTURE:
							items = info.bestCraftItems;
							L2ManufactureList createList = pl.getCreateList();
							if(createList == null)
								continue;

							for(L2ManufactureItem mitem : createList.getList())
							{
								int recipeId = mitem.getRecipeId();
								L2Recipe recipe = RecipeController.getInstance().getRecipeByRecipeId(recipeId);
								if(recipe == null)
									continue;

								L2Item temp = ItemTemplates.getInstance().getTemplate(recipe.getItemId());
								if(temp == null)
									continue;
								TreeMap<Long, Item> oldItems = items.get(temp.getName());
								if(oldItems == null)
								{
									oldItems = new TreeMap<Long, Item>();
									items.put(temp.getName(), oldItems);
								}
								Item newItem = new Item(recipe.getItemId(), type, mitem.getCost(), recipe.getCount(), 0, temp.getName(), pl, pl.getName(), path, itemObjId++, null);
								long key = newItem.price * 100;
								while(key < newItem.price * 100 + 100 && oldItems.containsKey(key))
									// До 100 предметов с одинаковыми ценами
									key++;
								oldItems.put(key, newItem);
							}
							break;
						default:
							continue;
					}
				}
			}
			_npcInfos.put(npc == null ? 0 : npc.getObjectId(), info);
		}
	}

	public void find(String[] var)
	{
		int countPerPage = 9;

		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		//L2NpcInstance npc = null;

		if(player == null/* || npc == null*/)
			return;

		if(var.length < 4 || var.length > 8)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		int type;
		int min;
		int max;
		String str = "";

		try
		{
			type = Integer.valueOf(var[0]);
			min = Integer.valueOf(var[1]);
			max = Integer.valueOf(var[2]);
			for(int i = 3; i < var.length; i++)
				str += var[i];
		}
		catch(Exception e)
		{
			show("Некорректные данные", player, npc);
			return;
		}

		if(max < countPerPage)
			max = countPerPage;

		TreeMap<String, TreeMap<Long, Item>> allItems = getItems(type);
		if(allItems == null)
		{
			show("Неизвестная ошибка", player, npc);
			return;
		}

		GArray<Item> items = new GArray<Item>();
		mainLoop: for(Entry<String, TreeMap<Long, Item>> entry : allItems.entrySet())
		{
			for(int i = 3; i < var.length; i++)
				if(entry.getKey().toLowerCase().indexOf(var[i].toLowerCase()) == -1)
					continue mainLoop;
			Item item = type == L2Player.STORE_PRIVATE_BUY ? entry.getValue().lastEntry().getValue() : entry.getValue().firstEntry().getValue();
			if(item != null && ItemTemplates.getInstance().getTemplate(item.itemId) != null)
				items.add(item);
		}

		StringBuffer out = new StringBuffer(npc != null ? ("[npc_%objectId%_Chat " + ("1" + type) + "|««]&nbsp;") : ("[scripts_services.ItemBroker:call_item_list " + ("1" + type) + "|««]&nbsp;"));

		int pages = Math.min(10, Math.max(1, items.size() / countPerPage + 1));

		if(pages > 1)
			for(int j = 1; j <= pages; j++)
				if(min == (j - 1) * countPerPage + 1)
					out.append(j).append("&nbsp;");
				else
					out.append("[scripts_services.ItemBroker:find ").append(type).append(" ").append(((j - 1) * countPerPage + 1)).append(" ").append((j * countPerPage)).append(" ").append(str).append("|").append(j).append("]&nbsp;");

		out.append("<table width=100%>");

		int i = 0;
		for(Item item : items)
		{
			i++;
			if(i < min || i > max)
				continue;
			L2Item temp = ItemTemplates.getInstance().getTemplate(item.itemId);
			if(temp == null)
				continue;

			out.append("<tr><td>").append("<img src=").append(temp.getIcon()).append(" width=32 height=32>");
			out.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ").append(type).append(" ").append(item.itemId).append(" ").append(0).append(" ").append(0).append(" ").append(0).append(" ").append(min).append(" ").append(max).append("|");
			out.append("<font color=\"LEVEL\">").append(item.name).append("</font>]").append("</td></tr>");
			out.append("</table></td></tr>");
		}

		out.append("</table><br>&nbsp;");
		
		//player.sendMessage("find: var["+str+"]");

		show(out.toString(), player, npc);
	}
	
	public static ExShowTrace Points2Trace(L2Player player, List<Location> points, int step, int time)
	{
		ExShowTrace result = new ExShowTrace(time);
		Location _prev = null;
		for(Location p : points)
		{
			if(player.isGM())
				player.sendMessage(p.toString());
			if(_prev != null)
				result.addLine(_prev, p, step);
			_prev = p;
		}
		return result;
	}	

	public void onLoad()
	{
		_log.info("Loaded Service: Item Broker");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void call_buy_item(String[] var)
	{
		L2Player buyer = (L2Player) getSelf();
		if(buyer == null || var.length < 5)
		{
			for(String v : var)
				_log.info("-"+v+"-");
			return;
		}

		int _sellerID=Integer.parseInt(var[0]);
		int _count=Integer.parseInt(var[1]);
		long[] _items = new long[_count * 3];

		for(int i = 0; i < _count; i++)
		{
			_items[i * 3 + 0] = Long.parseLong(var[2]); // object id
			_items[i * 3 + 1] = Long.parseLong(var[3]); // count
			_items[i * 3 + 2] = Long.parseLong(var[4]); // price

			if(_items[i * 3 + 0] < 1 || _items[i * 3 + 1] < 1 || _items[i * 3 + 2] < 1)
			{
				_items = null;
				break;
			}
		}
		if(_items == null)
			return;

		if(!buyer.getPlayerAccess().UseTrade)
		{
			buyer.sendPacket(Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
			return;
		}

		ConcurrentLinkedQueue<TradeItem> buyerlist = new ConcurrentLinkedQueue<TradeItem>();

		L2Player seller = L2ObjectsStorage.getPlayer(_sellerID);
		if(seller == null || seller.getPrivateStoreType() != L2Player.STORE_PRIVATE_SELL && seller.getPrivateStoreType() != L2Player.STORE_PRIVATE_SELL_PACKAGE)
		{
			buyer.sendActionFailed();
			return;
		}

		if(seller.getTradeList() == null)
		{
			L2TradeList.cancelStore(seller);
			return;
		}

		if(!L2TradeList.validateList(seller))
		{
			buyer.sendPacket(new SystemMessage(SystemMessage.CANNOT_PURCHASE));
			return;
		}

		ConcurrentLinkedQueue<TradeItem> sellerlist = seller.getSellList();
		double cost = 0;

		if(seller.getPrivateStoreType() == L2Player.STORE_PRIVATE_SELL_PACKAGE)
		{
			buyerlist = new ConcurrentLinkedQueue<TradeItem>();
			buyerlist.addAll(sellerlist);
			for(TradeItem ti : buyerlist)
				cost += 1d * ti.getOwnersPrice() * ti.getCount();
		}
		else
			for(int i = 0; i < _count; i++)
			{
				int objectId = (int) _items[i * 3 + 0];
				long count = _items[i * 3 + 1];
				long price = _items[i * 3 + 2];

				for(TradeItem si : sellerlist)
					if(si.getObjectId() == objectId)
					{
						if(count > si.getCount() || price != si.getOwnersPrice())
						{
							buyer.sendActionFailed();
							buyer.sendMessage("У продавца не достаточно товара.");
							return;
						}

						L2ItemInstance sellerItem = seller.getInventory().getItemByObjectId(objectId);
						if(sellerItem == null || sellerItem.getCount() < count)
						{
							buyer.sendActionFailed();
							buyer.sendMessage("У продавца не достаточно товара.");
							return;
						}

						TradeItem temp = new TradeItem();
						temp.setObjectId(si.getObjectId());
						temp.setItemId(sellerItem.getItemId());
						temp.setCount(count);
						temp.setOwnersPrice(si.getOwnersPrice());
						temp.setAttackElement(sellerItem.getAttackElementAndValue());
						temp.setDefenceFire(sellerItem.getDefenceFire());
						temp.setDefenceWater(sellerItem.getDefenceWater());
						temp.setDefenceWind(sellerItem.getDefenceWind());
						temp.setDefenceEarth(sellerItem.getDefenceEarth());
						temp.setDefenceHoly(sellerItem.getDefenceHoly());
						temp.setDefenceUnholy(sellerItem.getDefenceUnholy());
						temp.setAugmentationId(sellerItem.getAugmentationId());
						temp.setEnchantOptions(sellerItem.getEnchantOptions());
						cost += 1d * temp.getOwnersPrice() * temp.getCount();
						buyerlist.add(temp);
					}
			}

		L2ItemInstance _cost = buyer.getInventory().getItemByItemId(ConfigValue.TradeItemId);
		if(_cost == null || _cost.getCount() < cost || cost > Long.MAX_VALUE || cost < 0)
		{
			buyer.sendPacket(ConfigValue.TradeItemId == 57 ? Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			buyer.sendActionFailed();
			return;
		}

		byte validate = L2TradeList.validateTrade(seller, buyer, buyerlist);
		if(validate == 1)
		{
			buyer.sendPacket(Msg.INCORRECT_ITEM_COUNT);
			buyer.sendActionFailed();
			L2TradeList.validateList(seller);
			return;
		}
		else if(validate != 0)
			return;

		seller.getTradeList().buySellItems(buyer, buyerlist, seller, sellerlist);
		buyer.sendChanges();

		seller.saveTradeList();

		// на всякий случай немедленно сохраняем все изменения
		for(L2ItemInstance i : buyer.getInventory().getItemsList())
			i.updateDatabase(true, true);

		for(L2ItemInstance i : seller.getInventory().getItemsList())
			i.updateDatabase(true, true);

		if(seller.getSellList().isEmpty())
			L2TradeList.cancelStore(seller);

		seller.sendChanges();
		buyer.sendActionFailed();
	}
	/***
		public boolean isWeapon()
	{
		return getType2() == L2Item.TYPE2_WEAPON;
	}

	public boolean isArmor()
	{
		return getType2() == L2Item.TYPE2_SHIELD_ARMOR;
	}

	public boolean isAccessory()
	{
		return getType2() == L2Item.TYPE2_ACCESSORY;
	}
	**/
}