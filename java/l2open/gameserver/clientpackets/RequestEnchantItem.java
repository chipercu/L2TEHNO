package l2open.gameserver.clientpackets;

import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.items.PcInventory;
import l2open.gameserver.serverpackets.EnchantResult;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.Util;

import org.apache.commons.lang3.ArrayUtils;

public class RequestEnchantItem extends L2GameClientPacket
{
	protected static Logger _log = Logger.getLogger(RequestEnchantItem.class.getName());

	// Format: cd
	private int _objectId, _catalystObjId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
		_catalystObjId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
		L2ItemInstance catalyst = _catalystObjId > 0 && activeChar._catalystId == _catalystObjId ? inventory.getItemByObjectId(_catalystObjId) : null;
		L2ItemInstance scroll = activeChar.getEnchantScroll();
		activeChar.setEnchantScroll(null);

		// Затычка, ибо клиент криво обрабатывает RequestExTryToPutEnchantSupportItem
		if(!RequestExTryToPutEnchantSupportItem.checkCatalyst(itemToEnchant, catalyst))
			catalyst = null;

		if(activeChar.isOutOfControl() || activeChar.isActionsDisabled() || itemToEnchant == null || scroll == null || activeChar._enchant_time > System.currentTimeMillis())
		{
			activeChar.sendActionFailed();
			return;
		}

		String tradeBan = activeChar.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			activeChar.sendMessage("Your trade is banned! Expires: " + (tradeBan.equals("-1") ? "never" : Util.formatTime((Long.parseLong(tradeBan) - System.currentTimeMillis()) / 1000)) + ".");
			activeChar.sendActionFailed();
			return;
		}

		// Этими скролами можно точить только PC-Club Weapon
		if(scroll.isPcClubEnchantScroll() && !itemToEnchant.isPcClubWeapon())
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		boolean olf = Util.contains(ConfigValue.ItemsEnchantAsOlf, itemToEnchant.getItemId());
		// Этими скролами можно точить только рубаху Olf's T-shirt.
		if((scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707) && !olf || olf && scroll.getItemId() != 21581 && scroll.getItemId() != 21582 && scroll.getItemId() != 21707)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		// С помощью Master Yogi's Scroll: Enchant Weapon можно точить только Staff of Master Yogi
		if(scroll.getItemId() == 13540 && itemToEnchant.getItemId() != 13539 || itemToEnchant.getItemId() == 13539 && scroll.getItemId() != 13540)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		if(ConfigValue.EnableItemEnchantLog)
			Log.add(activeChar.getName() + "|Trying to enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getRealEnchantLevel() + "|" + itemToEnchant.getObjectId(), "enchants");

		// Затычка, разрешающая точить Staff of Master Yogi
		if(!itemToEnchant.canBeEnchanted() && !itemToEnchant.isPcClubWeapon() && !isYogiStaffEnchanting(scroll, itemToEnchant))
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		if(itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != L2ItemInstance.ItemLocation.PAPERDOLL)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.YOU_CANNOT_PRACTICE_ENCHANTING_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_MANUFACTURING_WORKSHOP);
			activeChar.sendActionFailed();
			return;
		}

		if(itemToEnchant.isStackable() || (scroll = inventory.getItemByObjectId(scroll.getObjectId())) == null)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendActionFailed();
			return;
		}

		int crystalId = itemToEnchant.getEnchantCrystalId(scroll, catalyst);

		// Затычка, разрешающая точить Staff of Master Yogi
		if(crystalId == 0 && !isYogiStaffEnchanting(scroll, itemToEnchant))
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		if(!scroll.isPcClubEnchantScroll() && itemToEnchant.isPcClubWeapon())
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		int safe_enchant_level = olf ? ConfigValue.SafeEnchantValueOlf : ConfigValue.SafeEnchant;
		int itemType = itemToEnchant.getItem().getType2();

		if(itemType == L2Item.TYPE2_WEAPON)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercent > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercent+.5);
			else if(ConfigValue.SafeEnchantPercent > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercent+.5);
		}
		else if(itemType == L2Item.TYPE2_ACCESSORY)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercentAccessory > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercentAccessory+.5);
			else if(ConfigValue.SafeEnchantPercentAccessory > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercentAccessory+.5);
		}
		else if(!olf)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercentArmor > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercentArmor+.5);
			else if(ConfigValue.SafeEnchantPercentArmor > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercentArmor+.5);
		}

		//_log.info("safe_enchant_level: "+safe_enchant_level+" Config1: "+(olf ? ConfigValue.SafeEnchantValueOlf : ConfigValue.SafeEnchant)+" SafeBlessedEnchantPercent="+ConfigValue.SafeBlessedEnchantPercent+" SafeEnchantPercent="+ConfigValue.SafeEnchantPercent);
		// Staff of Master Yogi можно точить до 23
		if(!isValid(scroll, itemToEnchant))
		{
			if(!isYogiStaffEnchanting(scroll, itemToEnchant))
			{
				switch(ConfigValue.EnchantBotFail)
				{
					case 0:
					{
						if(itemToEnchant.isEquipped())
							inventory.unEquipItemInSlot(itemToEnchant.getEquipSlot());

						L2ItemInstance destroyedItem = inventory.destroyItem(itemToEnchant.getObjectId(), 1, true);
						if(destroyedItem == null)
						{
							_log.warning("failed to destroy " + itemToEnchant.getObjectId() + " after unsuccessful enchant attempt by char " + activeChar.getName());
							activeChar.sendActionFailed();
							break;
						}

						if(ConfigValue.EnableItemEnchantLog)
							Log.LogItem(activeChar, Log.EnchantItemFail, itemToEnchant);

						if(crystalId > 0 && destroyedItem.getItem().getCrystalCount() > 0)
						{
							L2ItemInstance crystalsToAdd = ItemTemplates.getInstance().createItem(crystalId);

							int count = destroyedItem.getItem().getCrystalCount(itemToEnchant.getRealEnchantLevel()) - (itemToEnchant.getItem().getCrystalCount() + 1) / 2;
							if(count < 1)
								count = 1;
							crystalsToAdd.setCount(count);

							inventory.addItem(crystalsToAdd);
							if(ConfigValue.EnableItemEnchantLog)
								Log.LogItem(activeChar, Log.Sys_GetItem, crystalsToAdd);

							activeChar.sendPacket(new EnchantResult(1, crystalsToAdd.getItemId(), count, 0), SystemMessage.obtainItems(crystalId, count, 0)); // FAILED
						}
						else
							activeChar.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);
						synchronized (inventory)
						{
							inventory.destroyItem(scroll.getObjectId(), 1, true);
							if(catalyst != null)
								inventory.destroyItem(catalyst.getObjectId(), 1, true);
						}

						activeChar.validateItemExpertisePenalties(false, destroyedItem.getItem().getType2() == L2Item.TYPE2_SHIELD_ARMOR || destroyedItem.getItem().getType2() == L2Item.TYPE2_ACCESSORY, destroyedItem.getItem().getType2() == L2Item.TYPE2_WEAPON);
						break;
					}
					case 1:
					{
						//_log.info("setEnchantLevel1: "+safe_enchant_level);
						if(itemToEnchant.isEquipped())
							activeChar.getInventory().refreshListeners(itemToEnchant, safe_enchant_level);
						else
						{
							itemToEnchant.setEnchantLevel(safe_enchant_level);
							activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
						}
						if(safe_enchant_level == 0)
							activeChar.sendPacket(Msg.FAILED_IN_BLESSED_ENCHANT_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);
						else
							activeChar.sendMessage("Благословенное улучшение не удалось. Заточка предмета стала +"+safe_enchant_level+".");
						activeChar.sendPacket(EnchantResult.BLESSED_FAILED);
						synchronized (inventory)
						{
							inventory.destroyItem(scroll.getObjectId(), 1, true);
							if(catalyst != null)
								inventory.destroyItem(catalyst.getObjectId(), 1, true);
						}
						break;
					}
				}
			}
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.clientpackets.RequestEnchantItem.MaxLevel", activeChar));
			activeChar.sendActionFailed();
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != activeChar.getObjectId())
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendPacket(Msg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			activeChar.sendActionFailed();
			return;
		}

		// Если игрок олень, наказываем его)
		if(activeChar.isEnchantDisable(itemToEnchant, scroll))
			return;

		int prev_level = itemToEnchant.getRealEnchantLevel();
		int ench_count=0;
		while(startEnchant(safe_enchant_level, activeChar, itemToEnchant, scroll, crystalId, catalyst))
		{
			catalyst = null;
			if(++ench_count >= activeChar.getVarInt("AutoEnchantItemScrolCount", 1))
				break;
		}
		int cur_level = itemToEnchant.getRealEnchantLevel();

		if(cur_level > prev_level)
		{
			activeChar.sendPacket(new EnchantResult(0, 0, 0, cur_level)); // SUCESS
			if(ConfigValue.EnchantUseAnimSkill && cur_level <= 16)
				activeChar.broadcastSkill(new MagicSkillUse(activeChar, activeChar, cur_level+23096, 1, 1, 0), true);

			else if(ConfigValue.Enchant6AnnounceToAll && (cur_level == (itemType == L2Item.TYPE2_WEAPON ? 7 : 6) || (cur_level == (itemType == L2Item.TYPE2_WEAPON ? 15 : 12))))
			{
				activeChar.broadcastSkill(new MagicSkillUse(activeChar, activeChar, 21006, 1, 1, 0), true);
				activeChar.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_SUCCESSFULY_ENCHANTED_A__S2_S3).addName(activeChar).addNumber(cur_level).addItemName(itemToEnchant.getItemId()));
			}
		}
		/*else if(prev_level > cur_level)
		{
			if(ConfigValue.EnchantUseAnimSkill)
				activeChar.broadcastSkill(new MagicSkillUse(activeChar, activeChar, 23121, 1, 1, 0), true);
		}*/

		activeChar.refreshOverloaded();
		activeChar.sendChanges();
	}

	private boolean startEnchant(int safe_enchant_level1, L2Player activeChar, L2ItemInstance itemToEnchant, L2ItemInstance scroll, int crystalId, L2ItemInstance catalyst)
	{
		boolean olf = Util.contains(ConfigValue.ItemsEnchantAsOlf, itemToEnchant.getItemId());
		int itemType = itemToEnchant.getItem().getType2();
		PcInventory inventory = activeChar.getInventory();
		int safe_enchant_level = olf ? ConfigValue.SafeEnchantValueOlf : ConfigValue.SafeEnchant;

		if(itemType == L2Item.TYPE2_WEAPON)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercent > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercent+.5);
			else if(ConfigValue.SafeEnchantPercent > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercent+.5);
		}
		else if(itemType == L2Item.TYPE2_ACCESSORY)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercentAccessory > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercentAccessory+.5);
			else if(ConfigValue.SafeEnchantPercentAccessory > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercentAccessory+.5);
		}
		else if(!olf)
		{
			if(scroll.isBlessedEnchantScroll() && ConfigValue.SafeBlessedEnchantPercentArmor > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeBlessedEnchantPercentArmor+.5);
			else if(ConfigValue.SafeEnchantPercentArmor > 0)
				safe_enchant_level = (int)(itemToEnchant.getRealEnchantLevel()/100f*ConfigValue.SafeEnchantPercentArmor+.5);
		}


		L2ItemInstance removedScroll, removedCatalyst = null;
		synchronized (inventory)
		{
			removedScroll = inventory.destroyItem(scroll.getObjectId(), 1, true);
			if(catalyst != null)
				removedCatalyst = inventory.destroyItem(catalyst.getObjectId(), 1, true);
		}

		//tries enchant without scrolls
		if(removedScroll == null || catalyst != null && removedCatalyst == null)
		{
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendActionFailed();
			return false;
		}

		int safeEnchantLevel = itemToEnchant.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR ? ConfigValue.SafeEnchantFullBody : olf ? ConfigValue.SafeEnchantOlf : ConfigValue.SafeEnchantCommon;
		if(itemToEnchant.getItemId() == 13539)
			safeEnchantLevel = ConfigValue.SafeEnchantMasterYogi;

		
		//_log.info("RequestEnchantItem: startEnchant["+removedScroll+"]["+itemToEnchant.getRealEnchantLevel()+"]");
		if(removedScroll == null && inventory.destroyItem(scroll.getObjectId(), 1, true) == null)
			return false;

		double chance;
		boolean premium = activeChar.getNetConnection().getBonus() > 1;

		if(itemType == L2Item.TYPE2_WEAPON)
		{
			if(ConfigValue.OfflikeEnchant)
			{
				if(premium)
				{
					if(itemToEnchant.getItemId() == 13539)
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantMasterYogi.length ? ConfigValue.OfflikePremiumEnchantMasterYogi[ConfigValue.OfflikePremiumEnchantMasterYogi.length - 1] : ConfigValue.OfflikePremiumEnchantMasterYogi[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isCrystallEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantCrystalWeapon.length ? ConfigValue.OfflikePremiumEnchantCrystalWeapon[ConfigValue.OfflikePremiumEnchantCrystalWeapon.length - 1] : ConfigValue.OfflikePremiumEnchantCrystalWeapon[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isBlessedEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantBlessedWeapon.length ? ConfigValue.OfflikePremiumEnchantBlessedWeapon[ConfigValue.OfflikePremiumEnchantBlessedWeapon.length - 1] : ConfigValue.OfflikePremiumEnchantBlessedWeapon[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isAncientEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantAncientWeapon.length ? ConfigValue.OfflikePremiumEnchantAncientWeapon[ConfigValue.OfflikePremiumEnchantAncientWeapon.length - 1] : ConfigValue.OfflikePremiumEnchantAncientWeapon[itemToEnchant.getRealEnchantLevel()];
					else
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantSimpleWeapon.length ? ConfigValue.OfflikePremiumEnchantSimpleWeapon[ConfigValue.OfflikePremiumEnchantSimpleWeapon.length - 1] : ConfigValue.OfflikePremiumEnchantSimpleWeapon[itemToEnchant.getRealEnchantLevel()];
				}
				else
				{
					if(itemToEnchant.getItemId() == 13539)
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantMasterYogi.length ? ConfigValue.OfflikeEnchantMasterYogi[ConfigValue.OfflikeEnchantMasterYogi.length - 1] : ConfigValue.OfflikeEnchantMasterYogi[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isCrystallEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantCrystalWeapon.length ? ConfigValue.OfflikeEnchantCrystalWeapon[ConfigValue.OfflikeEnchantCrystalWeapon.length - 1] : ConfigValue.OfflikeEnchantCrystalWeapon[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isBlessedEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantBlessedWeapon.length ? ConfigValue.OfflikeEnchantBlessedWeapon[ConfigValue.OfflikeEnchantBlessedWeapon.length - 1] : ConfigValue.OfflikeEnchantBlessedWeapon[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isAncientEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantAncientWeapon.length ? ConfigValue.OfflikeEnchantAncientWeapon[ConfigValue.OfflikeEnchantAncientWeapon.length - 1] : ConfigValue.OfflikeEnchantAncientWeapon[itemToEnchant.getRealEnchantLevel()];
					else
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantSimpleWeapon.length ? ConfigValue.OfflikeEnchantSimpleWeapon[ConfigValue.OfflikeEnchantSimpleWeapon.length - 1] : ConfigValue.OfflikeEnchantSimpleWeapon[itemToEnchant.getRealEnchantLevel()];
				}
				if(ConfigValue.OfflikeEnchantMage && itemToEnchant.getItem().isMage() && chance != 100)
					chance *= ConfigValue.OfflikeEnchantMageChance;
			}
			else if(itemToEnchant.getItemId() == 13539)
				chance = ConfigValue.EnchantChanceMasterYogi;
			else
				chance = removedScroll.isCrystallEnchantScroll() ? ConfigValue.EnchantChanceCrystal : removedScroll.isBlessedEnchantScroll() ? ConfigValue.EnchantChanceBlessed : ConfigValue.EnchantChance;
		}
		else if(itemType == L2Item.TYPE2_SHIELD_ARMOR)
		{
			if(olf)
			{
				if(ConfigValue.OfflikeEnchant)
				{
					if(!removedScroll.isBlessedEnchantScroll())
					{
						if(premium)
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumSimpleEnchantOlf.length ? ConfigValue.OfflikePremiumSimpleEnchantOlf[ConfigValue.OfflikePremiumSimpleEnchantOlf.length - 1] : ConfigValue.OfflikePremiumSimpleEnchantOlf[itemToEnchant.getRealEnchantLevel()];
						else
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeSimpleEnchantOlf.length ? ConfigValue.OfflikeSimpleEnchantOlf[ConfigValue.OfflikeSimpleEnchantOlf.length - 1] : ConfigValue.OfflikeSimpleEnchantOlf[itemToEnchant.getRealEnchantLevel()];
					}
					else
					{
						if(premium)
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumBlessedEnchantOlf.length ? ConfigValue.OfflikePremiumBlessedEnchantOlf[ConfigValue.OfflikePremiumBlessedEnchantOlf.length - 1] : ConfigValue.OfflikePremiumBlessedEnchantOlf[itemToEnchant.getRealEnchantLevel()];
						else
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeBlessedEnchantOlf.length ? ConfigValue.OfflikeBlessedEnchantOlf[ConfigValue.OfflikeBlessedEnchantOlf.length - 1] : ConfigValue.OfflikeBlessedEnchantOlf[itemToEnchant.getRealEnchantLevel()];
					}
				}
				else
					chance = removedScroll.isBlessedEnchantScroll() ? ConfigValue.EnchantChanceOlfsBlessed : ConfigValue.EnchantChanceOlfs;
			}
			else
			{
				if(ConfigValue.OfflikeEnchant)
				{
					if(premium)
					{
						if(removedScroll.isCrystallEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantCrystalArmor.length ? ConfigValue.OfflikePremiumEnchantCrystalArmor[ConfigValue.OfflikePremiumEnchantCrystalArmor.length - 1] : ConfigValue.OfflikePremiumEnchantCrystalArmor[itemToEnchant.getRealEnchantLevel()];
						else if(removedScroll.isBlessedEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantBlessedArmor.length ? ConfigValue.OfflikePremiumEnchantBlessedArmor[ConfigValue.OfflikePremiumEnchantBlessedArmor.length - 1] : ConfigValue.OfflikePremiumEnchantBlessedArmor[itemToEnchant.getRealEnchantLevel()];
						else if(removedScroll.isAncientEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantAncientArmor.length ? ConfigValue.OfflikePremiumEnchantAncientArmor[ConfigValue.OfflikePremiumEnchantAncientArmor.length - 1] : ConfigValue.OfflikePremiumEnchantAncientArmor[itemToEnchant.getRealEnchantLevel()];
						else
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantSimpleArmor.length ? ConfigValue.OfflikePremiumEnchantSimpleArmor[ConfigValue.OfflikePremiumEnchantSimpleArmor.length - 1] : ConfigValue.OfflikePremiumEnchantSimpleArmor[itemToEnchant.getRealEnchantLevel()];
					}
					else
					{
						if(removedScroll.isCrystallEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantCrystalArmor.length ? ConfigValue.OfflikeEnchantCrystalArmor[ConfigValue.OfflikeEnchantCrystalArmor.length - 1] : ConfigValue.OfflikeEnchantCrystalArmor[itemToEnchant.getRealEnchantLevel()];
						else if(removedScroll.isBlessedEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantBlessedArmor.length ? ConfigValue.OfflikeEnchantBlessedArmor[ConfigValue.OfflikeEnchantBlessedArmor.length - 1] : ConfigValue.OfflikeEnchantBlessedArmor[itemToEnchant.getRealEnchantLevel()];
						else if(removedScroll.isAncientEnchantScroll())
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantAncientArmor.length ? ConfigValue.OfflikeEnchantAncientArmor[ConfigValue.OfflikeEnchantAncientArmor.length - 1] : ConfigValue.OfflikeEnchantAncientArmor[itemToEnchant.getRealEnchantLevel()];
						else
							chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantSimpleArmor.length ? ConfigValue.OfflikeEnchantSimpleArmor[ConfigValue.OfflikeEnchantSimpleArmor.length - 1] : ConfigValue.OfflikeEnchantSimpleArmor[itemToEnchant.getRealEnchantLevel()];
					}
				}
				else
					chance = removedScroll.isCrystallEnchantScroll() ? ConfigValue.EnchantChanceCrystalArmor : removedScroll.isBlessedEnchantScroll() ? ConfigValue.EnchantChanceArmorBlessed : ConfigValue.EnchantChanceArmor;
			}
		}
		else if(itemType == L2Item.TYPE2_ACCESSORY)
		{
			if(ConfigValue.OfflikeEnchant)
			{
				if(premium)
				{
					if(removedScroll.isCrystallEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantCrystalAccessory.length ? ConfigValue.OfflikePremiumEnchantCrystalAccessory[ConfigValue.OfflikePremiumEnchantCrystalAccessory.length - 1] : ConfigValue.OfflikePremiumEnchantCrystalAccessory[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isBlessedEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantBlessedAccessory.length ? ConfigValue.OfflikePremiumEnchantBlessedAccessory[ConfigValue.OfflikePremiumEnchantBlessedAccessory.length - 1] : ConfigValue.OfflikePremiumEnchantBlessedAccessory[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isAncientEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantAncientAccessory.length ? ConfigValue.OfflikePremiumEnchantAncientAccessory[ConfigValue.OfflikePremiumEnchantAncientAccessory.length - 1] : ConfigValue.OfflikePremiumEnchantAncientAccessory[itemToEnchant.getRealEnchantLevel()];
					else
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikePremiumEnchantSimpleAccessory.length ? ConfigValue.OfflikePremiumEnchantSimpleAccessory[ConfigValue.OfflikePremiumEnchantSimpleAccessory.length - 1] : ConfigValue.OfflikePremiumEnchantSimpleAccessory[itemToEnchant.getRealEnchantLevel()];
				}
				else
				{
					if(removedScroll.isCrystallEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantCrystalAccessory.length ? ConfigValue.OfflikeEnchantCrystalAccessory[ConfigValue.OfflikeEnchantCrystalAccessory.length - 1] : ConfigValue.OfflikeEnchantCrystalAccessory[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isBlessedEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantBlessedAccessory.length ? ConfigValue.OfflikeEnchantBlessedAccessory[ConfigValue.OfflikeEnchantBlessedAccessory.length - 1] : ConfigValue.OfflikeEnchantBlessedAccessory[itemToEnchant.getRealEnchantLevel()];
					else if(removedScroll.isAncientEnchantScroll())
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantAncientAccessory.length ? ConfigValue.OfflikeEnchantAncientAccessory[ConfigValue.OfflikeEnchantAncientAccessory.length - 1] : ConfigValue.OfflikeEnchantAncientAccessory[itemToEnchant.getRealEnchantLevel()];
					else
						chance = itemToEnchant.getRealEnchantLevel() > ConfigValue.OfflikeEnchantSimpleAccessory.length ? ConfigValue.OfflikeEnchantSimpleAccessory[ConfigValue.OfflikeEnchantSimpleAccessory.length - 1] : ConfigValue.OfflikeEnchantSimpleAccessory[itemToEnchant.getRealEnchantLevel()];
				}
			}
			else
				chance = removedScroll.isCrystallEnchantScroll() ? ConfigValue.EnchantChanceCrystalAccessory : removedScroll.isBlessedEnchantScroll() ? ConfigValue.EnchantChanceAccessoryBlessed : ConfigValue.EnchantChanceAccessory;
		}
		else
		{
			_log.info("WTF? Request to enchant " + itemToEnchant.getItemId());
			activeChar.sendPacket(EnchantResult.CANCEL);
			activeChar.sendActionFailed();
			activeChar.sendPacket(Msg.SYSTEM_ERROR);
			inventory.addItem(removedScroll);
			return false;
		}

		if(scroll.isDivineEnchantScroll()) // Item Mall divine
			chance = 100;
		else if(scroll.isItemMallEnchantScroll()) // Item Mall normal/ancient
			chance += 10;
		else if(scroll.getItemId() == 32415 || scroll.getItemId() == 32416) // аффорд
			chance += 2;

		if(removedCatalyst != null)
			chance += removedCatalyst.getCatalystPower();

		if(ConfigValue.AltEnchantFormulaForPvPServers)
		{
			int enchlvl = itemToEnchant.getRealEnchantLevel();
			L2Item.Grade crystaltype = itemToEnchant.getItem().getCrystalType();

			//для уровнения шансов дуальщиков и остальных на победу в PvP вставка SA в дули халявная
			if(itemType == L2Item.TYPE2_WEAPON && itemToEnchant.getItemType() == L2Weapon.WeaponType.DUAL)
				safeEnchantLevel += 1;

			if(enchlvl < safeEnchantLevel)
				chance = 100;
			else if(enchlvl > 11)
				chance = ConfigValue.EChanceHigh;
			else
			{
				// Выборка базового шанса
				if(itemType == L2Item.TYPE2_WEAPON)
				{
					boolean magewep = itemToEnchant.getItem().isMage();
					chance = !magewep ? ConfigValue.EChanceWeapon : ConfigValue.EChanceMageWeapon;

					// Штраф на двуручное оружие(немагическое)
					if(itemToEnchant.getItem().getBodyPart() == L2Item.SLOT_LR_HAND && itemToEnchant.getItem().getItemType() == L2Weapon.WeaponType.BLUNT && !magewep)
						chance -= ConfigValue.PenaltyforEChanceToHandBlunt;
				}
				else if(olf)
					chance = ConfigValue.EnchantChanceOlfs;
				else
					chance = ConfigValue.EChanceArmor;

				int DeltaChance = 15;

				// Основная прогрессия
				for(int i = safeEnchantLevel; i < enchlvl; i++)
				{
					if(i == safeEnchantLevel + 2)
						DeltaChance -= 5;
					if(i == safeEnchantLevel + 6)
						DeltaChance -= 5;
					chance -= DeltaChance;
				}

				// Учёт грейда
				int Delta2 = 5;
				for(int in = 0x00; in < crystaltype.ordinal(); in++)
				{
					if(in == L2Item.CRYSTAL_C)
						Delta2 -= 5;
					if(in == L2Item.CRYSTAL_B)
						Delta2 -= 5;
					if(in == L2Item.CRYSTAL_A)
						Delta2 -= 2;
					if(in == L2Item.CRYSTAL_S)
						Delta2 -= 1;
				}
				chance += Delta2;

				if(scroll.isBlessedEnchantScroll())
					chance += 2;
				if(chance < 1)
					chance = 1;
				if(scroll.isCrystallEnchantScroll())
				{
					if(itemType == L2Item.TYPE2_WEAPON)
						chance = ConfigValue.EnchantChanceCrystal;
					if(itemType == L2Item.TYPE2_SHIELD_ARMOR)
						chance = ConfigValue.EnchantChanceCrystalArmor;
					if(itemType == L2Item.TYPE2_ACCESSORY)
						chance = ConfigValue.EnchantChanceCrystalAccessory;
				}
			}
			if(scroll.isDivineEnchantScroll()) // Item Mall divine
				chance = 100;
			if(scroll.isItemMallEnchantScroll()) // Item Mall normal/ancient
				chance += 10;
			if(scroll.getItemId() == 32415 || scroll.getItemId() == 22416) // аффорд
				chance += 2;
			else if(scroll.getItemId() == 22428 || scroll.getItemId() == 22429) // аффорд
				chance += 4;
		}

		if(scroll.isBlessedEnchantScroll())
		{
			chance *= activeChar.getBonus().RATE_ENCHANT_BLESSED_MUL;
			chance += activeChar.getBonus().RATE_ENCHANT_BLESSED;
		}
		else
		{
			chance *= activeChar.getBonus().RATE_ENCHANT_MUL;
			chance += activeChar.getBonus().RATE_ENCHANT;
		}

		if(itemToEnchant.getRealEnchantLevel() < safeEnchantLevel || Rnd.chance(chance))
		{
			int value = itemToEnchant.getRealEnchantLevel() + (itemToEnchant.isWeapon() ? ConfigValue.EnchantWeaponLevel : itemToEnchant.isArmor() ? ConfigValue.EnchantArmorLevel : itemToEnchant.isAccessory() ? ConfigValue.EnchantAccessoryLevel : 1);

			if(itemToEnchant.isWeapon() && value > ConfigValue.EnchantMaxWeapon)
				value = ConfigValue.EnchantMaxWeapon;
			else if(itemToEnchant.isArmor() && value > ConfigValue.EnchantMaxArmor)
				value = ConfigValue.EnchantMaxArmor;
			else if(itemToEnchant.isAccessory() && value > ConfigValue.EnchantMaxJewelry)
				value = ConfigValue.EnchantMaxJewelry;

			if(activeChar.getAttainment() != null)
				activeChar.getAttainment().enchant_sucess(value, safeEnchantLevel, scroll.getItemId(), itemToEnchant.isWeapon());

			//_log.info("setEnchantLevel2: "+value);
			if(itemToEnchant.isEquipped())
				activeChar.getInventory().refreshListeners(itemToEnchant, value);
			else
			{
				itemToEnchant.setEnchantLevel(value);
				//if(activeChar.getVarInt("AutoEnchantItemEnable", -1) <= value)
					activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
			}

			itemToEnchant.updateDatabase();

			if(ConfigValue.EnableItemEnchantLog)
			{
				Log.add(activeChar.getName() + "|Successfully enchanted|" + itemToEnchant.getItemId() + "|to+" + itemToEnchant.getRealEnchantLevel() + "|" + chance+"|scroll="+scroll.getItemId()+"|Catalyst="+(catalyst == null ? -1 : catalyst.getItemId()), "enchants");
				Log.LogItem(activeChar, Log.EnchantItem, itemToEnchant);
			}

			if(ConfigValue.EnchantBonus)
			{
				if(ConfigValue.EnchantBonusRandom && Rnd.chance(ConfigValue.EnchantBonusChance))
				{
					int rnd = Rnd.get(0, ConfigValue.EnchantBonusRandomList.length);

					if(ConfigValue.EnchantBonusRandomList[rnd][1] > 0)
					{
						if(ConfigValue.EnchantBonusRandomList[rnd][0] == L2Item.ITEM_ID_FAME)
							activeChar.setFame(activeChar.getFame() + ConfigValue.EnchantBonusRandomList[rnd][1], "Enchant");
						else if(ConfigValue.EnchantBonusRandomList[rnd][0] == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE && activeChar.getClan() != null)
							activeChar.getClan().incReputation(ConfigValue.EnchantBonusRandomList[rnd][1], true, "Enchant");
						else if(ConfigValue.EnchantBonusRandomList[rnd][0] == L2Item.ITEM_ID_PC_BANG_POINTS)
							activeChar.addPcBangPoints(ConfigValue.EnchantBonusRandomList[rnd][1], false, 1);
						else
							activeChar.getInventory().addItem(ConfigValue.EnchantBonusRandomList[rnd][0], ConfigValue.EnchantBonusRandomList[rnd][1]);
					}
				}
				else
				{
					if(itemToEnchant.getRealEnchantLevel() > safeEnchantLevel)
					{
						int number = itemToEnchant.getRealEnchantLevel() - safeEnchantLevel;

						if(ConfigValue.EnchantBonusList.length < number && ConfigValue.EnchantBonusList[number][1] > 0)
						{
							if(ConfigValue.EnchantBonusList[number][0] == L2Item.ITEM_ID_FAME)
								activeChar.setFame(activeChar.getFame() + ConfigValue.EnchantBonusList[number][1], "Enchant");
							else if(ConfigValue.EnchantBonusList[number][0] == L2Item.ITEM_ID_CLAN_REPUTATION_SCORE && activeChar.getClan() != null)
								activeChar.getClan().incReputation(ConfigValue.EnchantBonusList[number][1], true, "Enchant");
							else if(ConfigValue.EnchantBonusList[number][0] == L2Item.ITEM_ID_PC_BANG_POINTS)
								activeChar.addPcBangPoints(ConfigValue.EnchantBonusList[number][1], false, 1);
							else
								activeChar.getInventory().addItem(ConfigValue.EnchantBonusList[number][0], ConfigValue.EnchantBonusList[number][1]);
						}
					}
				}
			}

			if(!isValid(scroll, itemToEnchant))
				return false;
			return ConfigValue.EnableAutoEnchant && activeChar.getVarInt("AutoEnchantItemEnable", -1) > value;
		}
		else
		{
			boolean AutoEnchantAfterFailBlessed = activeChar.getVarB("AutoEnchantAfterFailBlessed", false);
			if(activeChar.getAttainment() != null)
				activeChar.getAttainment().enchant_fail(itemToEnchant.getRealEnchantLevel(), safeEnchantLevel, scroll.getItemId());

			if(ConfigValue.EnchantUseAnimSkill)
				activeChar.broadcastPacket2(new MagicSkillUse(activeChar, activeChar, 23121, 1, 1, 0));

			if(ConfigValue.EnableItemEnchantLog)
				Log.add(activeChar.getName() + "|Failed to enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getRealEnchantLevel() + "|" + chance+"|"+safeEnchantLevel, "enchants");

			if(scroll.isBlessedEnchantScroll() || (ConfigValue.SafeEnchantPercent > 0 && itemType == L2Item.TYPE2_WEAPON) || (ConfigValue.SafeEnchantPercentAccessory > 0 && itemType == L2Item.TYPE2_ACCESSORY) || (ConfigValue.SafeEnchantPercentArmor > 0 && !olf)) // фейл, но заточка блесед
			{
				//_log.info("setEnchantLevel3: "+safe_enchant_level);
				if(itemToEnchant.isEquipped())
					activeChar.getInventory().refreshListeners(itemToEnchant, safe_enchant_level);
				else
				{
					itemToEnchant.setEnchantLevel(safe_enchant_level);
					//if(!AutoEnchantAfterFailBlessed)
						activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
				}
				if(!AutoEnchantAfterFailBlessed)
				{
					if(safe_enchant_level == 0)
						activeChar.sendPacket(Msg.FAILED_IN_BLESSED_ENCHANT_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);
					else
						activeChar.sendMessage("Благословенное улучшение не удалось. Заточка предмета стала +"+safe_enchant_level+".");

					activeChar.sendPacket(new EnchantResult(3, 0, 0, safe_enchant_level)); // BLESSED_FAILED
				}
				return AutoEnchantAfterFailBlessed;
			}
			else if(scroll.isDestructionEnchantScroll())
			{
				boolean AutoEnchantAfterFailDestract = activeChar.getVarB("AutoEnchantAfterFailDestract", false);
				if(!AutoEnchantAfterFailDestract)
					activeChar.sendPacket(EnchantResult.FAILED);
				return AutoEnchantAfterFailDestract;
			}
			else if(scroll.isCrystallEnchantScroll() && ConfigValue.AlternativeCrystalScroll) // фейл, но если конфиг включён то не ломаем и не сбрасываем точку.
			{
				boolean AutoEnchantAfterFailCrystal = activeChar.getVarB("AutoEnchantAfterFailCrystal", false);
				if(!AutoEnchantAfterFailCrystal)
					activeChar.sendPacket(EnchantResult.FAILED);
				return AutoEnchantAfterFailCrystal;
			}
			else if(scroll.isAncientEnchantScroll()) // фейл, но заточка ancient
			{
				boolean AutoEnchantAfterFailAncient = activeChar.getVarB("AutoEnchantAfterFailAncient", false);
				if(!AutoEnchantAfterFailAncient)
					activeChar.sendPacket(EnchantResult.ANCIENT_FAILED);
				return AutoEnchantAfterFailAncient;
			}
			else
			// фейл, разбиваем вещь
			{
				if(itemToEnchant.isEquipped())
					inventory.unEquipItemInSlot(itemToEnchant.getEquipSlot());

				int itemid = itemToEnchant._visual_item_id;
				if(itemid > 0)
				{
					activeChar.getInventory().addItem(itemid, 1);
					itemToEnchant.setVisualItemId(0);
				}

				L2ItemInstance destroyedItem = inventory.destroyItem(itemToEnchant.getObjectId(), 1, true);
				if(destroyedItem == null)
				{
					_log.warning("failed to destroy " + itemToEnchant.getObjectId() + " after unsuccessful enchant attempt by char " + activeChar.getName());
					activeChar.sendActionFailed();
					return false;
				}

				if(ConfigValue.EnableItemEnchantLog)
					Log.LogItem(activeChar, Log.EnchantItemFail, itemToEnchant);

				if(crystalId > 0 && destroyedItem.getItem().getCrystalCount() > 0)
				{
					L2ItemInstance crystalsToAdd = ItemTemplates.getInstance().createItem(crystalId);

					int count = destroyedItem.getItem().getCrystalCount(itemToEnchant.getRealEnchantLevel()) - (itemToEnchant.getItem().getCrystalCount() + 1) / 2;
					if(count < 1)
						count = 1;
					crystalsToAdd.setCount(count);

					inventory.addItem(crystalsToAdd);
					if(ConfigValue.EnableItemEnchantLog)
						Log.LogItem(activeChar, Log.Sys_GetItem, crystalsToAdd);

					activeChar.sendPacket(new EnchantResult(1, crystalsToAdd.getItemId(), count, 0), SystemMessage.obtainItems(crystalId, count, 0)); // FAILED
				}
				else
					activeChar.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);

				activeChar.validateItemExpertisePenalties(false, destroyedItem.getItem().getType2() == L2Item.TYPE2_SHIELD_ARMOR || destroyedItem.getItem().getType2() == L2Item.TYPE2_ACCESSORY, destroyedItem.getItem().getType2() == L2Item.TYPE2_WEAPON);
			}
		}
		return false;
	}

	private static boolean isYogiStaffEnchanting(L2ItemInstance scroll, L2ItemInstance itemToEnchant)
	{
		if(scroll.getItemId() == 13540 && itemToEnchant.getItemId() == 13539)
			return true;
		else if((scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707) && Util.contains(ConfigValue.ItemsEnchantAsOlf, itemToEnchant.getItemId()))
			return true;
		return false;
	}

	private final boolean isValid(L2ItemInstance scroll, L2ItemInstance enchantItem)
	{
		if(enchantItem == null)
			return false;
		if(isYogiStaffEnchanting(scroll, enchantItem))
		{
			if(enchantItem.getRealEnchantLevel() >= ConfigValue.EnchantMaxMasterYogi)
				return false;
		}
		else
		{
			int type2 = enchantItem.getItem().getType2();
			int max_value = 35565;
			if(scroll.isAncientEnchantScroll())
				max_value = 16;
			if(scroll.isDestructionEnchantScroll())
				max_value = (type2 == L2Item.TYPE2_WEAPON ? ConfigValue.EnchantMaxDestructionWeapon : ConfigValue.EnchantMaxDestructionArmor);
			else if(scroll.getItemId() == 21581 || scroll.getItemId() == 21582 || scroll.getItemId() == 21707)
				max_value = ConfigValue.MaxEnchantLevelOlf;
			switch(type2)
			{
				case L2Item.TYPE2_WEAPON:
					if(ConfigValue.EnchantMaxWeapon > 0 && enchantItem.getRealEnchantLevel() >= Math.min(ConfigValue.EnchantMaxWeapon, max_value))
						return false;
					break;
				case L2Item.TYPE2_SHIELD_ARMOR:
					if(ConfigValue.EnchantMaxArmor > 0 && enchantItem.getRealEnchantLevel() >= Math.min(ConfigValue.EnchantMaxArmor, max_value))
						return false;
					break;
				case L2Item.TYPE2_ACCESSORY:
					if(ConfigValue.EnchantMaxJewelry > 0 && enchantItem.getRealEnchantLevel() >= Math.min(ConfigValue.EnchantMaxJewelry, max_value))
						return false;
					break;
				default:
					return false;
			}
		}
		return true;
	}
}