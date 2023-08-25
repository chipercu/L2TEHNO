package l2open.gameserver.clientpackets;

import l2open.config.*;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.ExAttributeEnchantResult;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Armor.ArmorType;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;

/**
 * @author SYS
 * Format: d
 */
public class RequestEnchantItemAttribute extends L2GameClientPacket
{
	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_objectId == -1)
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendPacket(Msg.ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED);
			return;
		}

		String tradeBan = activeChar.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			activeChar.setEnchantScroll(null);
			activeChar.sendActionFailed();
			return;
		}
		else if(activeChar.isOutOfControl() || activeChar.isActionsDisabled())
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendActionFailed();
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
		L2ItemInstance stone = activeChar.getEnchantScroll();
		if(itemToEnchant == null || stone == null)
		{
			activeChar.sendActionFailed();
			activeChar.setEnchantScroll(null);
			return;
		}

		/*int bodyPart = itemToEnchant.getBodyPart();

		if(!itemToEnchant.canBeEnchanted() || itemToEnchant.getItem().getCrystalType().cry < L2Item.CRYSTAL_S || bodyPart != L2Item.SLOT_LR_HAND && bodyPart != L2Item.SLOT_L_HAND && bodyPart != L2Item.SLOT_R_HAND)
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, Msg.ActionFail);
			activeChar.setEnchantScroll(null);
			return;
		}*/
		if(!itemToEnchant.canBeEnchanted() || itemToEnchant.getItem().getCrystalType().cry < L2Item.CRYSTAL_S || itemToEnchant.getItem().isAccessory() || itemToEnchant.getItem().getItemType() == WeaponType.NONE || itemToEnchant.getItem().getItemType() == ArmorType.SIGIL || (!itemToEnchant.getItem().isArmor() && !itemToEnchant.getItem().isWeapon()) || itemToEnchant.getBodyPart() == L2Item.SLOT_UNDERWEAR)
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, Msg.ActionFail);
			activeChar.setEnchantScroll(null);
			return;
		}

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP, Msg.ActionFail);
			activeChar.setEnchantScroll(null);
			return;
		}

		if(itemToEnchant.getItem().isUnderwear() || itemToEnchant.getItem().isCloak() || itemToEnchant.getItem().isBracelet() || itemToEnchant.getItem().isBelt() || !ConfigValue.AltAttributePvPItem && itemToEnchant.getItem().isPvP())
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, Msg.ActionFail);
			activeChar.setEnchantScroll(null);
			return;
		}

		if(ConfigValue.EnableAutoAttribute && activeChar.getVarB("EnableAutoAttribute"))
			while(enchant_ett(activeChar, inventory, itemToEnchant, stone));
		else
			enchant_ett(activeChar, inventory, itemToEnchant, stone);

		activeChar.sendChanges();
	}

	private boolean enchant_ett(L2Player activeChar, PcInventory inventory, L2ItemInstance itemToEnchant, L2ItemInstance stone)
	{
		byte stoneElement = stone.getEnchantAttributeStoneElement(false);
		byte oppElement = stone.getEnchantAttributeStoneElement(true);
		activeChar.setEnchantScroll(null);

		L2Item item = itemToEnchant.getItem();

		if(itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.PAPERDOLL)
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, Msg.ActionFail);
			return false;
		}

		if(itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			activeChar.sendActionFailed();
			return false;
		}

		/*int itemType = item.getType2();

		if(itemToEnchant.getAttackAttributeElement() != L2Item.ATTRIBUTE_NONE && itemToEnchant.getAttackAttributeElement() != stone.getEnchantAttributeStoneElement(itemType == L2Item.TYPE2_SHIELD_ARMOR))
		{
			activeChar.sendPacket(Msg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED, Msg.ActionFail);
			return false;
		}*/

		if(item.isWeapon() && itemToEnchant.getWeaponElementLevel() > stone.getAttributeElementLevel() || item.isArmor() && itemToEnchant.getArmorAttributeLevel()[oppElement] > stone.getAttributeElementLevel())
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.ELEMENTAL_POWER_ENCHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT));
			activeChar.sendActionFailed();
			return false;
		}

		//int minValue = 0; WTF???? TODO
		int maxValue = item.isWeapon() ? ConfigValue.MaxAttributeWeapon : ConfigValue.MaxAttributeArmor;
		int maxValueCrystal = item.isWeapon() ? ConfigValue.MaxAttributeWeaponCrystal : ConfigValue.MaxAttributeArmorCrystal;
		int maxValueJewel = item.isWeapon() ? ConfigValue.MaxAttributeWeaponJewel : ConfigValue.MaxAttributeArmorJewel;
		int maxValueEnergy = item.isWeapon() ? ConfigValue.MaxAttributeWeaponEnergy : ConfigValue.MaxAttributeArmorEnergy;

		int[] deffAttr = itemToEnchant.getDeffAttr();

		if(item.isArmor() && deffAttr[stoneElement] != 0) // проверка на энчат противоположных элементов
		{
			activeChar.sendPacket(new SystemMessage(3117));
			activeChar.sendActionFailed();
			return false;
		}

		int attrValue = item.isWeapon() ? itemToEnchant.getAttackElementValue() : itemToEnchant.getElementDefAttr(oppElement);

		/**
		 * Если не подходят условия то посылаем...
		 **/
		if(attrValue >= maxValueEnergy || (stone.isAttributeCrystal() && (/*attrValue < maxValue || */attrValue >= maxValueCrystal)) || (stone.isAttributeJewel() && (attrValue < maxValueCrystal || attrValue >= maxValueJewel)) || (stone.isAttributeEnergy() && (attrValue < maxValueJewel || attrValue >= maxValueEnergy)))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.ELEMENTAL_POWER_ENCHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT));
			activeChar.sendActionFailed();
			return false;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != activeChar.getObjectId())
		{
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS, Msg.ActionFail);
			return false;
		}

		Log.add(activeChar.getName() + "|Trying to attribute enchant|" + itemToEnchant.getItemId() + "|attribute:" + stone.getEnchantAttributeStoneElement(item.getType2() == L2Item.TYPE2_SHIELD_ARMOR) + "|" + itemToEnchant.getObjectId(), "enchants");

		L2ItemInstance removedStone;
		synchronized (inventory)
		{
			removedStone = inventory.destroyItem(stone.getObjectId(), 1, true);
		}

		if(removedStone == null)
		{
			activeChar.sendActionFailed();
			return false;
		}

		int chance;
		if(stone.isAttributeCrystal())
			chance = ConfigValue.EnchantAttributeCrystalChance;
		else if(stone.isAttributeJewel())
			chance = ConfigValue.EnchantAttributeJewelChance;
		else if(stone.isAttributeEnergy())
			chance = ConfigValue.EnchantAttributeEnergyChance;
		else
			chance = ConfigValue.EnchantAttributeChance;

		if(Rnd.chance(chance))
		{
			if(item.isWeapon())
			{
				if(itemToEnchant.getRealEnchantLevel() == 0)
				{
					SystemMessage sm = new SystemMessage(SystemMessage.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
					sm.addItemName(item.getItemId());
					sm.addElementName(stoneElement);
					activeChar.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(SystemMessage.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO__S1S2);
					sm.addNumber(itemToEnchant.getRealEnchantLevel());
					sm.addItemName(item.getItemId());
					sm.addElementName(stoneElement);
					activeChar.sendPacket(sm);
				}
			}
			else
			{
				if(itemToEnchant.getRealEnchantLevel() == 0)
				{
					SystemMessage sm = new SystemMessage(3144);
					sm.addItemName(item.getItemId());
					sm.addElementName(stoneElement);
					sm.addElementName(oppElement);
					activeChar.sendPacket(sm);
				}
				else
				{
					SystemMessage sm = new SystemMessage(3163);
					sm.addNumber(itemToEnchant.getRealEnchantLevel());
					sm.addItemName(item.getItemId());
					sm.addElementName(stoneElement);
					sm.addElementName(oppElement);
					activeChar.sendPacket(sm);
				}
			}

			int value = item.isWeapon() ? ConfigValue.AttributeWeaponLevel : ConfigValue.AttributeArmorLevel;

			// Для оружия 1й камень дает +20 атрибута
			if(itemToEnchant.getAttackElementValue() == 0 && item.isWeapon())
				value = ConfigValue.AttributeStartWeapon;

			byte attackElement = itemToEnchant.getAttackAttributeElement();
			int attackElementValue = itemToEnchant.getAttackElementValue();
			if(item.isArmor())
				deffAttr[oppElement] = itemToEnchant.getElementDefAttr(oppElement) + value;
			else if(item.isWeapon())
			{
				attackElement = stoneElement;
				attackElementValue += value;
			}
			itemToEnchant.setAttributeElement(attackElement, attackElementValue, deffAttr, true);
			if(itemToEnchant.isEquipped())
				activeChar.getInventory().refreshListeners(itemToEnchant, -1);
			else
				activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
			activeChar.sendPacket(new ExAttributeEnchantResult(value));

			//Log.add(player.getName() + "|Successfully enchanted by attribute|" + item.getItemId() + "|to+" + item.getAttackElementValue() + "|" + Config.ENCHANT_ATTRIBUTE_CHANCE, "enchants");
			Log.LogItem(activeChar, Log.EnchantItem, itemToEnchant);
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER));
			//Log.add(player.getName() + "|Failed to enchant attribute|" + item.getItemId() + "|+" + item.getAttackElementValue() + "|" + Config.ENCHANT_ATTRIBUTE_CHANCE, "enchants");
		}
		return true;
	}
}