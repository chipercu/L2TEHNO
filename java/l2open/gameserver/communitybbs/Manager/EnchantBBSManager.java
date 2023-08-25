package l2open.gameserver.communitybbs.Manager;

import l2open.config.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.Log;

import java.util.StringTokenizer;

public class EnchantBBSManager extends BaseBBSManager
{
	private static EnchantBBSManager _Instance = null;

	public static EnchantBBSManager getInstance()
	{
		if (_Instance == null)
			_Instance = new EnchantBBSManager();
		return _Instance;
	}

	public void parsecmd(String command, L2Player activeChar)
	{
		if(activeChar.getEventMaster() != null && activeChar.getEventMaster().blockBbs())
			return;
		if (command.equals("_bbsechant"))
		{
			String name = "None Name";
			name = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem).getName();
			StringBuilder sb = new StringBuilder();
			sb.append("<table width=400>");
			L2ItemInstance[] arr = activeChar.getInventory().getItems();
			int len = arr.length;
			for (int i = 0; i < len; i++)
			{
				L2ItemInstance _item = arr[i];
				if (_item == null || _item.getItem() instanceof L2EtcItem || !_item.isEquipped() || _item.isHeroWeapon() || _item.getItem().getCrystalType() == L2Item.Grade.NONE || _item.getItemId() >= 7816 && _item.getItemId() <= 7831 || _item.isShadowItem() || _item.isCommonItem() || _item.getItem().isCloak() || _item.isWear() || _item.getRealEnchantLevel() >= (ConfigValue.CBMaxEnchant + 1) || _item.getBodyPart() == L2Item.SLOT_R_BRACELET || _item.getBodyPart() == L2Item.SLOT_L_BRACELET)
					continue;
				sb.append(new StringBuilder("<tr><td><img src=icon." + _item.getItem().getIcon() + " width=32 height=32></td><td>"));
				sb.append(new StringBuilder("<font color=\"LEVEL\">" + _item.getItem().getName() + " " + (_item.getRealEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + _item.getRealEnchantLevel())) + "</font><br1>"));

				sb.append(new StringBuilder("Заточка за: <font color=\"LEVEL\">" + name + "</font>"));
				sb.append("<img src=\"l2ui.squaregray\" width=\"170\" height=\"1\">");
				sb.append("</td><td>");
				sb.append(new StringBuilder("<button value=\"Обычная\" action=\"bypass -h _bbsechant;enchlistpage;" + _item.getObjectId() + "\" width=75 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"));
				sb.append("</td>");

				if(_item.getItemType() != WeaponType.NONE && _item.getItemType() != ArmorType.SIGIL)
				{
					sb.append("<td>");
					sb.append(new StringBuilder("<button value=\"Аттрибут\" action=\"bypass -h _bbsechant;enchlistpageAtrChus;" + _item.getObjectId() + "\" width=75 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"));
					sb.append("</td>");
				}
				sb.append("</tr>");
			}

			sb.append("</table>");
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "804.htm", activeChar);
			content = content.replace("%enchanter%", sb.toString());
			separateAndSend(content, activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpage;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			String name = "None Name";
			name = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);

			StringBuilder sb = new StringBuilder();
			sb.append("Для обычной заточки выбрана вещь:<br1><table width=300>");
			sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
			sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getItem().getName() + " " + (EhchantItem.getRealEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getRealEnchantLevel())) + "</font><br1>"));

			sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			sb.append("<table border=0 width=400><tr><td width=200>");

			if(EhchantItem.isWeapon())
			{
				for(int i = 0; i < ConfigValue.CBEnchantLvl.length; i++)
				{
					sb.append(new StringBuilder("<button value=\"На +" + ConfigValue.CBEnchantLvl[i] + " (Цена:" + ConfigValue.CBEnchantPrice[i] + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;" + ConfigValue.CBEnchantLvl[i] + ";" + ConfigValue.CBEnchantPrice[i] + ";" + ItemForEchantObjID + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
					sb.append("<br1>");
				}
			}
			else
			{
				for(int i = 0; i < ConfigValue.CBEnchantArmorLvl.length; i++)
				{
					sb.append(new StringBuilder("<button value=\"На +" + ConfigValue.CBEnchantArmorLvl[i] + " (Цена:" + ConfigValue.CBEnchantArmorPrice[i] + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;" + ConfigValue.CBEnchantArmorLvl[i] + ";" + ConfigValue.CBEnchantArmorPrice[i] + ";" + ItemForEchantObjID + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
					sb.append("<br1>");
				}
			}

			sb.append("</td></tr></table><br1><button value=\"Назад\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "804.htm", activeChar);
			content = content.replace("%enchanter%", sb.toString());
			separateAndSend(content, activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpageAtrChus;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			String name = "None Name";
			name = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);

			StringBuilder sb = new StringBuilder();
			sb.append("Для заточки на атрибут выбрана вещь:<br1><table width=300>");
			sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
			sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getItem().getName() + " " + (EhchantItem.getRealEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getRealEnchantLevel())) + "</font><br1>"));

			sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			sb.append("<table border=0 width=400><tr><td width=200>");
			sb.append("<center><img src=icon.etc_wind_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Wind \" action=\"bypass -h _bbsechant;enchlistpageAtr;2;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("<br1><center><img src=icon.etc_earth_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Earth \" action=\"bypass -h _bbsechant;enchlistpageAtr;3;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("<br1><center><img src=icon.etc_fire_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Fire \" action=\"bypass -h _bbsechant;enchlistpageAtr;0;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("</td><td width=200>");
			sb.append("<center><img src=icon.etc_water_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Water \" action=\"bypass -h _bbsechant;enchlistpageAtr;1;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("<br1><center><img src=icon.etc_holy_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Divine \" action=\"bypass -h _bbsechant;enchlistpageAtr;4;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("<br1><center><img src=icon.etc_unholy_stone_i00 width=32 height=32></center><br1>");
			sb.append(new StringBuilder("<button value=\"Dark \" action=\"bypass -h _bbsechant;enchlistpageAtr;5;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
			sb.append("</td></tr></table><br1><button value=\"Назад\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "804.htm", activeChar);
			content = content.replace("%enchanter%", sb.toString());
			separateAndSend(content, activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpageAtr;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int AtributType = Integer.parseInt(st.nextToken());
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			String ElementName = "";
			if (AtributType == 0)
				ElementName = "Fire";
			else if (AtributType == 1)
				ElementName = "Water";
			else if (AtributType == 2)
				ElementName = "Wind";
			else if (AtributType == 3)
				ElementName = "Earth";
			else if (AtributType == 4)
				ElementName = "Divine";
			else if (AtributType == 5)
				ElementName = "Dark";
			String name = "None Name";
			name = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);
			StringBuilder sb = new StringBuilder();
			sb.append(new StringBuilder("Выбран элемент: <font color=\"LEVEL\">" + ElementName + "</font><br1> Для заточки выбрана вещь:<br1><table width=300>"));
			sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
			sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getItem().getName() + " " + (EhchantItem.getRealEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getRealEnchantLevel())) + "</font><br1>"));

			sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getItem().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			if (!EhchantItem.getItem().isPvP() && (EhchantItem.getItem().getCrystalType() == L2Item.Grade.S || EhchantItem.getItem().getCrystalType() == L2Item.Grade.S80 || EhchantItem.getItem().getCrystalType() == L2Item.Grade.S84))
			{
				sb.append("<table border=0 width=400><tr><td width=200>");
				
				if(EhchantItem.isWeapon())
				{
					for(int i = 0; i < ConfigValue.CBEnchantAtributeLvl.length; i++)
					{
						sb.append(new StringBuilder("<button value=\"На +" + ConfigValue.CBEnchantAtributeLvl[i] + " (Цена:" + ConfigValue.CBEnchantAtributePrice[i] + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;" + ConfigValue.CBEnchantAtributeLvl[i] + ";" + AtributType + ";" + ConfigValue.CBEnchantAtributePrice[i] + ";" + ItemForEchantObjID + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
						sb.append("<br1>");
					}
				}
				else
				{
					for(int i = 0; i < ConfigValue.CBEnchantArmorAtributeLvl.length; i++)
					{
						sb.append(new StringBuilder("<button value=\"На +" + ConfigValue.CBEnchantArmorAtributeLvl[i] + " (Цена:" + ConfigValue.CBEnchantArmorAtributePrice[i] + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;" + ConfigValue.CBEnchantArmorAtributeLvl[i] + ";" + AtributType + ";" + ConfigValue.CBEnchantArmorAtributePrice[i] + ";" + ItemForEchantObjID + "\" width=300 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
						sb.append("<br1>");
					}
				}
				
				sb.append("</td></tr></table><br1>");
			}
			else
			{
				sb.append("<table border=0 width=400><tr><td width=200>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<center><font color=\"LEVEL\">Заточка данной вещи не возможна!</font></center>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("</td></tr></table><br1>");
			}
			sb.append("<button value=\"Назад\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			String content = Files.read(ConfigValue.CommunityBoardHtmlRoot + "804.htm", activeChar);
			content = content.replace("%enchanter%", sb.toString());
			separateAndSend(content, activeChar);
		}
		if (command.startsWith("_bbsechant;enchantgo;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int EchantVal = Integer.parseInt(st.nextToken());
			int EchantPrice = Integer.parseInt(st.nextToken());
			int EchantObjID = Integer.parseInt(st.nextToken());
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem);
			L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
			if (pay != null && pay.getCount() >= EchantPrice)
			{
				activeChar.getInventory().destroyItem(pay, EchantPrice, true);
				activeChar.getInventory().unEquipItemInSlot(EhchantItem.getEquipSlot());
				EhchantItem.setEnchantLevel(EchantVal);
				activeChar.getInventory().equipItem(EhchantItem, false);
				activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
				activeChar.broadcastUserInfo(true);
				activeChar.sendMessage(new StringBuilder(EhchantItem.getItem().getName() + " было заточено до " + EchantVal + ". Спасибо.").toString());
				Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getItem().getName() + " val: " + EchantVal + "").toString(), "wmzSeller");
				parsecmd("_bbsechant", activeChar);
			}
			else
				activeChar.sendPacket(Msg.INCORRECT_ITEM_COUNT);
		}

		if (command.startsWith("_bbsechant;enchantgoAtr;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int EchantVal = Integer.parseInt(st.nextToken());
			int AtrType = Integer.parseInt(st.nextToken());
			int EchantPrice = Integer.parseInt(st.nextToken());
			int EchantObjID = Integer.parseInt(st.nextToken());
			L2Item item = ItemTemplates.getInstance().getTemplate(ConfigValue.CBEnchantItem);
			L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
			L2Item items = EhchantItem.getItem();
			if(EhchantItem.canBeEnchanted() && !EhchantItem.isStackable() && !items.isUnderwear() && !items.isCloak() && !items.isBracelet() && !items.isBelt() && !(!ConfigValue.AltAttributePvPItem && items.isPvP()))
			{
				if(EhchantItem.isWeapon()) 
				{
					if (pay != null && pay.getCount() >= EchantPrice) 
					{
						activeChar.getInventory().destroyItem(pay, EchantPrice, true);
						activeChar.getInventory().unEquipItemInSlot(EhchantItem.getEquipSlot());
						EhchantItem.setAttributeElement((byte)AtrType, EchantVal, new int[] {0,0,0,0,0,0}, true);
						activeChar.getInventory().equipItem(EhchantItem, false);
						activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
						activeChar.broadcastUserInfo(true);
						activeChar.sendMessage(new StringBuilder(EhchantItem.getItem().getName() + " было заточено до " + EchantVal + ". Спасибо.").toString());
						Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getItem().getName() + " val: " + EchantVal + " AtributType:" + AtrType).toString(), "wmzSeller");
						parsecmd("_bbsechant", activeChar);
					}
					else
						activeChar.sendPacket(Msg.INCORRECT_ITEM_COUNT);
				}
				else if(EhchantItem.isArmor()) 
				{
					if(!canEnchantArmorAttribute(AtrType, EhchantItem)) 
					{
						activeChar.sendMessage("Невозможно вставить аттрибут в броню, не соблюдены условия");
						return;
					}
					if (pay != null && pay.getCount() >= EchantPrice) 
					{
						activeChar.getInventory().destroyItem(pay, EchantPrice, true);
						activeChar.getInventory().unEquipItemInSlot(EhchantItem.getEquipSlot());
						int[] deffAttr = EhchantItem.getDeffAttr();
						deffAttr[AtrType] = EchantVal;
						EhchantItem.setAttributeElement((byte)-2, 0, deffAttr, true);
						activeChar.getInventory().equipItem(EhchantItem, false);
						activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
						activeChar.broadcastUserInfo(true);
						activeChar.sendMessage(new StringBuilder(EhchantItem.getItem().getName() + " было заточено до " + EchantVal + ". Спасибо.").toString());
						Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getItem().getName() + " val: " + EchantVal + " AtributType:" + AtrType).toString(), "wmzSeller");
						parsecmd("_bbsechant", activeChar);
					}
				}
			}
		}
	}

	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player activeChar)
	{
	}

    private boolean canEnchantArmorAttribute(int attr, L2ItemInstance item)
	{
        switch(attr)
		{
            case 0:
                if(item.getDeffAttr()[1] != 0)
                    return false;
                break;
            case 1:
                if(item.getDeffAttr()[0] != 0)
                    return false;
                break;
            case 2:
                if(item.getDeffAttr()[3] != 0)
                    return false;
                break;
            case 3:
                if(item.getDeffAttr()[2] != 0)
                    return false;
                break;
            case 4:
                if(item.getDeffAttr()[5] != 0)
                    return false;
                break;
            case 5:
                if(item.getDeffAttr()[4] != 0)
                    return false;
                break;
        }
        return true;
    }
}