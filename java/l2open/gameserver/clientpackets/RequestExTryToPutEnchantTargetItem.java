package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.serverpackets.ExPutEnchantTargetItemResult;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.Log;
import l2open.util.Util;

public class RequestExTryToPutEnchantTargetItem extends L2GameClientPacket
{

	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar._catalystId=0; // скидываем каталист...

		if(activeChar.isOutOfControl() || activeChar.isActionsDisabled())
		{
			activeChar.sendPacket(new ExPutEnchantTargetItemResult(0, 0, 0));
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
		L2ItemInstance scroll = activeChar.getEnchantScroll();

		if(itemToEnchant == null || scroll == null)
		{
			activeChar.sendPacket(new ExPutEnchantTargetItemResult(0, 0, 0));
			return;
		}

		// Этими скролами можно точить только PC-Club Weapon
		if(scroll.isPcClubEnchantScroll() && !itemToEnchant.isPcClubWeapon())
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}
		
		boolean olf = Util.contains(ConfigValue.ItemsEnchantAsOlf, itemToEnchant.getItemId());

		if((scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707) && !olf || olf && scroll.getItemId() != 21581 && scroll.getItemId() != 21582 && scroll.getItemId() != 21707)
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}

		// С помощью Master Yogi's Scroll: Enchant Weapon можно точить только Staff of Master Yogi
		if(scroll.getItemId() == 13540 && itemToEnchant.getItemId() != 13539 || itemToEnchant.getItemId() == 13539 && scroll.getItemId() != 13540)
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}

		Log.add(activeChar.getName() + "|Trying to put enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getRealEnchantLevel() + "|" + itemToEnchant.getObjectId()+"|scrol="+scroll.getItemId(), "enchants");

		// Затычка, разрешающая точить Staff of Master Yogi
		if(!itemToEnchant.canBeEnchanted() && !itemToEnchant.isPcClubWeapon() && itemToEnchant.getItemId() != 13539 && !olf || itemToEnchant.isStackable())
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			return;
		}

		if(itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.PAPERDOLL)
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			return;
		}

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(new ExPutEnchantTargetItemResult(0, 0, 0));
			activeChar.sendPacket(Msg.YOU_CANNOT_PRACTICE_ENCHANTING_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_MANUFACTURING_WORKSHOP);
			return;
		}

		if((scroll = inventory.getItemByObjectId(scroll.getObjectId())) == null)
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendPacket(new ExPutEnchantTargetItemResult(0, 0, 0));
			return;
		}

		int crystalId = itemToEnchant.getEnchantCrystalId(scroll, null);

		// Затычка, разрешающая точить Staff of Master Yogi
		if(crystalId == 0 && itemToEnchant.getItemId() != 13539 && !olf)
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			return;
		}
		
		if(!scroll.isPcClubEnchantScroll() && itemToEnchant.isPcClubWeapon())
		{
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			return;
		}
		

		if((scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707) && olf && itemToEnchant.getRealEnchantLevel() >= ConfigValue.MaxEnchantLevelOlf)
		{
			activeChar.sendActionFailed();
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestEnchantItem.MaxLevel", activeChar));
			return;
		}

		// Staff of Master Yogi можно точить до 23
		if(!isYogiStaffEnchanting(scroll, itemToEnchant) && !isValid(itemToEnchant) || isYogiStaffEnchanting(scroll, itemToEnchant) && itemToEnchant.getRealEnchantLevel() >= ConfigValue.EnchantMaxMasterYogi)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestEnchantItem.MaxLevel", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		// Древними точкми можно точить только до +16.
		if(scroll.isAncientEnchantScroll() && itemToEnchant.getRealEnchantLevel() >= 16)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestEnchantItem.MaxLevel", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		int itemType = itemToEnchant.getItem().getType2();
		// Свитками разрушения можно точить Оружие до +15, а Армор до +6.
		if(scroll.isDestructionEnchantScroll() && itemToEnchant.getRealEnchantLevel() >= (itemType == L2Item.TYPE2_WEAPON ? ConfigValue.EnchantMaxDestructionWeapon : ConfigValue.EnchantMaxDestructionArmor))
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestEnchantItem.MaxLevel", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != activeChar.getObjectId())
		{
			activeChar.sendPacket(new ExPutEnchantTargetItemResult(0, 0, 0));
			return;
		}

		if(itemToEnchant.getRealEnchantLevel() >= (itemToEnchant.isWeapon() ? ConfigValue.EnchantDecriseLevelWeapon : ConfigValue.EnchantDecriseLevelArmor))
		{
			activeChar.sendMessage("Божественно усиление : Вы точно хотите временно улучшить "+itemToEnchant.getName()+" до +"+(itemToEnchant.getRealEnchantLevel()+1)+"?");
			activeChar.sendPacket(new ExShowScreenMessage("Божественно усиление : Вы точно хотите временно улучшить "+itemToEnchant.getName()+" до +"+(itemToEnchant.getRealEnchantLevel()+1)+"?", 5000, ScreenMessageAlign.TOP_CENTER, true));
		}

		if(ConfigValue.EnchantProtectEnable)
			activeChar.increasEnchantCount();
		activeChar.sendPacket(new ExPutEnchantTargetItemResult(1, 0, 0));
		activeChar._enchant_time = System.currentTimeMillis() + ConfigValue.ItemEnchantDelay;
	}

	private final boolean isValid(L2ItemInstance enchantItem)
    {
        if(enchantItem == null)
            return false;
        int type2 = enchantItem.getItem().getType2();
        switch(type2)
		{
            case L2Item.TYPE2_WEAPON:
                if(ConfigValue.EnchantMaxWeapon > 0 && enchantItem.getRealEnchantLevel() >= (ConfigValue.EnchantBotFail > -1 ? ConfigValue.EnchantMaxWeapon+1 : ConfigValue.EnchantMaxWeapon))
                    return false;
                break;
            case L2Item.TYPE2_SHIELD_ARMOR:
                if(ConfigValue.EnchantMaxArmor > 0 && enchantItem.getRealEnchantLevel() >= (ConfigValue.EnchantBotFail > -1 ? ConfigValue.EnchantMaxArmor+1 : ConfigValue.EnchantMaxArmor))
                    return false;
                break;
            case L2Item.TYPE2_ACCESSORY:
                if(ConfigValue.EnchantMaxJewelry > 0 && enchantItem.getRealEnchantLevel() >= (ConfigValue.EnchantBotFail > -1 ? ConfigValue.EnchantMaxJewelry+1 : ConfigValue.EnchantMaxJewelry))
                    return false;
                break;
            default:
                return false;
        }
        return true;
    }

	private static boolean isYogiStaffEnchanting(L2ItemInstance scroll, L2ItemInstance itemToEnchant)
	{
		if(scroll.getItemId() == 13540 && itemToEnchant.getItemId() == 13539)
			return true;
		else if((scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707) && Util.contains(ConfigValue.ItemsEnchantAsOlf, itemToEnchant.getItemId()))
			return true;
		return false;
	}
}