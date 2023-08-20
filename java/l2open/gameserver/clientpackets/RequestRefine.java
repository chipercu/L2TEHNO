package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExVariationResult;
import l2open.gameserver.serverpackets.InventoryUpdate;
import l2open.gameserver.tables.AugmentationData;
import l2open.gameserver.templates.L2Item.Grade;

public final class RequestRefine extends L2GameClientPacket
{
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	private static final int GEMSTONE_B = 2132;

	// format: (ch)dddd
	private int _targetItemObjId, _refinerItemObjId, _gemstoneItemObjId;
	private long _gemstoneCount;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
		_gemstoneItemObjId = readD();
		_gemstoneCount = readQ();
	}

	@Override
	protected void runImpl()
	{
		// Под большим вопросом!!!
		if(_gemstoneCount <= 0 || getClient().getActiveChar().is_block)
			return;

		L2Player activeChar = getClient().getActiveChar();
		L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
		L2ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(_gemstoneItemObjId);

		if(targetItem == null || refinerItem == null || gemstoneItem == null || targetItem.getOwnerId() != activeChar.getObjectId() || refinerItem.getOwnerId() != activeChar.getObjectId() || gemstoneItem.getOwnerId() != activeChar.getObjectId() || activeChar.getLevel() < 46)
		{
			activeChar.sendPacket(new ExVariationResult(0, 0, 0), Msg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
			return;
		}

		if(TryAugmentItem(activeChar, targetItem, refinerItem, gemstoneItem))
		{
			int stat12 = 0x0000FFFF & targetItem.getAugmentation().getAugmentationId();
			int stat34 = targetItem.getAugmentation().getAugmentationId() >> 16;
			activeChar.sendPacket(new ExVariationResult(stat12, stat34, 1), Msg.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
		}
		else
			activeChar.sendPacket(new ExVariationResult(0, 0, 0), Msg.AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS);
	}

	boolean TryAugmentItem(L2Player player, L2ItemInstance targetItem, L2ItemInstance refinerItem, L2ItemInstance gemstoneItem)
	{
		Grade itemGrade = targetItem.getItem().getItemGrade();
		int lifeStoneId = refinerItem.getItemId();
		int gemstoneItemId = gemstoneItem.getItemId();

		boolean isAccessoryLifeStone = refinerItem.isAccessoryLifeStone();
		if(!targetItem.canBeAugmented(player, isAccessoryLifeStone))
			return false;

		if(!isAccessoryLifeStone && !refinerItem.isLifeStone())
			return false;

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(targetItem.getOwnerId() != player.getObjectId())
		{
			player.sendActionFailed();
			return false;
		}

		long modifyGemstoneCount = _gemstoneCount;
		int lifeStoneLevel = isAccessoryLifeStone ? getAccessoryLifeStoneLevel(lifeStoneId) : getLifeStoneLevel(lifeStoneId);
		int lifeStoneGrade = isAccessoryLifeStone ? 0 : getLifeStoneGrade(lifeStoneId);

		if(isAccessoryLifeStone)
			switch(itemGrade)
			{
				case C:
					if(player.getLevel() < 46 || gemstoneItemId != GEMSTONE_D)
						return false;
					modifyGemstoneCount = 200;
					break;
				case B:
					if(player.getLevel() < 52 || gemstoneItemId != GEMSTONE_D)
						return false;
					modifyGemstoneCount = 300;
					break;
				case A:
					if(player.getLevel() < 61 || gemstoneItemId != GEMSTONE_C)
						return false;
					modifyGemstoneCount = 200;
					break;
				case S:
					if(player.getLevel() < 76 || (targetItem.getItem().getName().contains("Dynasty") && gemstoneItemId != GEMSTONE_B || !targetItem.getItem().getName().contains("Dynasty") && gemstoneItemId != GEMSTONE_C))
						return false;
					if(targetItem.getItem().getName().contains("Dynasty"))
						modifyGemstoneCount = 360;
					else
						modifyGemstoneCount = 250;
					break;
				case S80:
					if(player.getLevel() < 80 || gemstoneItemId != GEMSTONE_B)
						return false;
					modifyGemstoneCount = 360;
					break;
				case S84:
					if(player.getLevel() < 84 || gemstoneItemId != GEMSTONE_B)
						return false;
					modifyGemstoneCount = 480;
					break;
			}
		else
			switch(itemGrade)
			{
				case C:
					if(player.getLevel() < 46 || gemstoneItemId != GEMSTONE_D)
						return false;
					modifyGemstoneCount = 20;
					break;
				case B:
					if(player.getLevel() < 52 || gemstoneItemId != GEMSTONE_D)
						return false;
					modifyGemstoneCount = 30;
					break;
				case A:
					if(player.getLevel() < 61 || gemstoneItemId != GEMSTONE_C)
						return false;
					modifyGemstoneCount = 20;
					break;
				case S:
					if(player.getLevel() < 76 || (targetItem.getItem().getName().contains("Dynasty") && gemstoneItemId != GEMSTONE_B || !targetItem.getItem().getName().contains("Dynasty") && gemstoneItemId != GEMSTONE_C))
						return false;
					if(targetItem.getItem().getName().contains("Dynasty"))
						modifyGemstoneCount = 36;
					else
						modifyGemstoneCount = 25;
					break;
				case S80:
					if(player.getLevel() < 80 || gemstoneItemId != GEMSTONE_B)
						return false;
					modifyGemstoneCount = 36;
					break;
				case S84:
					if(player.getLevel() < 84 || gemstoneItemId != GEMSTONE_B)
						return false;
					if(targetItem.getItem().getCrystalCount() == 7050)
						modifyGemstoneCount = 48;
					else
						modifyGemstoneCount = 58;
					break;
			}

		// check if the lifestone is appropriate for this player
		switch(lifeStoneLevel)
		{
			case 1:
				if(player.getLevel() < 46)
					return false;
				break;
			case 2:
				if(player.getLevel() < 49)
					return false;
				break;
			case 3:
				if(player.getLevel() < 52)
					return false;
				break;
			case 4:
				if(player.getLevel() < 55)
					return false;
				break;
			case 5:
				if(player.getLevel() < 58)
					return false;
				break;
			case 6:
				if(player.getLevel() < 61)
					return false;
				break;
			case 7:
				if(player.getLevel() < 64)
					return false;
				break;
			case 8:
				if(player.getLevel() < 67)
					return false;
				break;
			case 9:
				if(player.getLevel() < 70)
					return false;
				break;
			case 10:
				if(player.getLevel() < 76)
					return false;
				break;
			case 11:
				if(player.getLevel() < 80)
					return false;
				break;
			case 12:
				if(player.getLevel() < 82)
					return false;
				break;
			case 13:
				if(player.getLevel() < 84)
					return false;
				break;
			case 14:
				if(player.getLevel() < 85)
					return false;
				break;
		}

		if(gemstoneItem.getCount() < modifyGemstoneCount)
			return false;
		player.getInventory().destroyItem(_gemstoneItemObjId, modifyGemstoneCount, true);

		// consume the life stone
		player.getInventory().destroyItem(refinerItem, 1, true);

		// generate augmentation
		lifeStoneLevel = Math.min(lifeStoneLevel, 10) - 1; // 10 уровней (0-9)

		targetItem.setAugmentation(AugmentationData.getInstance().generateRandomAugmentation(lifeStoneLevel, lifeStoneGrade, targetItem.getItem().getBodyPart()));

		if(targetItem.isEquipped())
			targetItem.getAugmentation().applyBoni(player, true);

		player.updateStats();

		player.sendPacket(new InventoryUpdate().addModifiedItem(targetItem));
		player.sendUserInfo(false);

		return true;
	}

	private int getLifeStoneGrade(int itemId)
	{
		switch(itemId)
		{
			case 8723:
			case 8724:
			case 8725:
			case 8726:
			case 8727:
			case 8728:
			case 8729:
			case 8730:
			case 8731:
			case 8732:
			case 9573:
			case 10483:
			case 14166:
			case 16160:
			case 16164:
				return 0;
			case 8733:
			case 8734:
			case 8735:
			case 8736:
			case 8737:
			case 8738:
			case 8739:
			case 8740:
			case 8741:
			case 8742:
			case 9574:
			case 10484:
			case 14167:
			case 16161:
			case 16165:
				return 1;
			case 8743:
			case 8744:
			case 8745:
			case 8746:
			case 8747:
			case 8748:
			case 8749:
			case 8750:
			case 8751:
			case 8752:
			case 9575:
			case 10485:
			case 14168:
			case 16162:
			case 16166:
				return 2;
			case 8753:
			case 8754:
			case 8755:
			case 8756:
			case 8757:
			case 8758:
			case 8759:
			case 8760:
			case 8761:
			case 8762:
			case 9576:
			case 10486:
			case 14169:
			case 16163:
			case 16167:
				return 3;
			default:
				return 0;
		}
	}

	private int getLifeStoneLevel(int itemId)
	{
		switch(itemId)
		{
			case 8723:
			case 8733:
			case 8743:
			case 8753:
				return 1;
			case 8724:
			case 8734:
			case 8744:
			case 8754:
				return 2;
			case 8725:
			case 8735:
			case 8745:
			case 8755:
				return 3;
			case 8726:
			case 8736:
			case 8746:
			case 8756:
				return 4;
			case 8727:
			case 8737:
			case 8747:
			case 8757:
				return 5;
			case 8728:
			case 8738:
			case 8748:
			case 8758:
				return 6;
			case 8729:
			case 8739:
			case 8749:
			case 8759:
				return 7;
			case 8730:
			case 8740:
			case 8750:
			case 8760:
				return 8;
			case 8731:
			case 8741:
			case 8751:
			case 8761:
				return 9;
			case 8732:
			case 8742:
			case 8752:
			case 8762:
				return 10;
			case 9573:
			case 9574:
			case 9575:
			case 9576:
				return 11;
			case 10483:
			case 10484:
			case 10485:
			case 10486:
				return 12;
			case 14166:
			case 14167:
			case 14168:
			case 14169:
				return 13;
			case 16160:
			case 16161:
			case 16162:
			case 16163:
			case 16164:
			case 16165:
			case 16166:
			case 16167:
				return 14;
			default:
				return 1;
		}
	}

	private int getAccessoryLifeStoneLevel(int itemId)
	{
		switch(itemId)
		{
			case 12754:
			case 12840:
				return 1;
			case 12755:
			case 12841:
				return 2;
			case 12756:
			case 12842:
				return 3;
			case 12757:
			case 12843:
				return 4;
			case 12758:
			case 12844:
				return 5;
			case 12759:
			case 12845:
				return 6;
			case 12760:
			case 12846:
				return 7;
			case 12761:
			case 12847:
				return 8;
			case 12762:
			case 12848:
				return 9;
			case 12763:
			case 12849:
				return 10;
			case 12821:
			case 12850:
				return 11;
			case 12822:
			case 12851:
				return 12;
			case 14008:
				return 13;
			case 16177:
			case 16178:
				return 14;
			default:
				return 1;
		}
	}
}