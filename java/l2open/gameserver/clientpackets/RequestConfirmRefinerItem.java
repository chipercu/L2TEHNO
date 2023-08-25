package l2open.gameserver.clientpackets;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ExPutIntensiveResultForVariationMake;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2Item.Grade;

public class RequestConfirmRefinerItem extends L2GameClientPacket
{
	private static final int GEMSTONE_D = 2130;
	private static final int GEMSTONE_C = 2131;
	private static final int GEMSTONE_B = 2132;

	// format: (ch)dd
	private int _targetItemObjId;
	private int _refinerItemObjId;

	@Override
	public void readImpl()
	{
		_targetItemObjId = readD();
		_refinerItemObjId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);

		if(targetItem == null || refinerItem == null)
		{
			activeChar.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		int refinerItemId = refinerItem.getItem().getItemId();

		boolean isAccessoryLifeStone = refinerItem.isAccessoryLifeStone();
		if(!targetItem.canBeAugmented(activeChar, isAccessoryLifeStone))
		{
			activeChar.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		if(!isAccessoryLifeStone && !refinerItem.isLifeStone())
		{
			activeChar.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}

		Grade itemGrade = targetItem.getItem().getItemGrade();

		int gemstoneCount = 0;
		int gemstoneItemId = 0;

		if(isAccessoryLifeStone)
			switch(itemGrade)
			{
				case C:
					gemstoneCount = 200;
					gemstoneItemId = GEMSTONE_D;
					break;
				case B:
					gemstoneCount = 300;
					gemstoneItemId = GEMSTONE_D;
					break;
				case A:
					gemstoneCount = 200;
					gemstoneItemId = GEMSTONE_C;
					break;
				case S:
					if(targetItem.getItem().getName().contains("Dynasty"))
					{
						gemstoneCount = 360;
						gemstoneItemId = GEMSTONE_B;
					}
					else
					{
						gemstoneCount = 250;
						gemstoneItemId = GEMSTONE_C;
					}
					break;
				case S80:
					gemstoneCount = 360;
					gemstoneItemId = GEMSTONE_B;
					break;
				case S84:
					gemstoneCount = 480;
					gemstoneItemId = GEMSTONE_B;
					break;
			}
		else
			switch(itemGrade)
			{
				case C:
					gemstoneCount = 20;
					gemstoneItemId = GEMSTONE_D;
					break;
				case B:
					gemstoneCount = 30;
					gemstoneItemId = GEMSTONE_D;
					break;
				case A:
					gemstoneCount = 20;
					gemstoneItemId = GEMSTONE_C;
					break;
				case S:
					if(targetItem.getItem().getName().contains("Dynasty"))
					{
						gemstoneCount = 36;
						gemstoneItemId = GEMSTONE_B;
					}
					else
					{
						gemstoneCount = 25;
						gemstoneItemId = GEMSTONE_C;
					}
					break;
				case S80:
					gemstoneCount = 36;
					gemstoneItemId = GEMSTONE_B;
					break;
				case S84:
					if(targetItem.getItem().getCrystalCount() == 7050)
					{
						gemstoneCount = 48;
						gemstoneItemId = GEMSTONE_B;
					}
					else
					{
						gemstoneCount = 58;
						gemstoneItemId = GEMSTONE_B;
					}
					break;
			}

		SystemMessage sm = new SystemMessage(SystemMessage.REQUIRES_S1_S2).addNumber(gemstoneCount).addItemName(gemstoneItemId);
		activeChar.sendPacket(new ExPutIntensiveResultForVariationMake(_refinerItemObjId, refinerItemId, gemstoneItemId, gemstoneCount), sm);
	}
}