package l2open.gameserver.clientpackets;

import l2open.gameserver.RecipeController;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2Player;

public class RequestRecipeShopMakeDo extends L2GameClientPacket
{
	private int _id;
	private int _recipeId;
	private long _price;

	/**
	 * packet type id 0xBF
	 * format:		cddd
	 * format:		cddQ - Gracia Final
	 */
	@Override
	public void readImpl()
	{
		_id = readD();
		_recipeId = readD();
		_price = readQ();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getDuel() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Player manufacturer = (L2Player) activeChar.getVisibleObject(_id);
		if(manufacturer == null || manufacturer.getPrivateStoreType() != L2Player.STORE_PRIVATE_MANUFACTURE || manufacturer.getDistance(activeChar) > manufacturer.INTERACTION_DISTANCE+manufacturer.BYPASS_DISTANCE_ADD)
		{
			activeChar.sendActionFailed();
			return;
		}

		for(L2ManufactureItem i : manufacturer.getCreateList().getList())
			if(i.getRecipeId() == _recipeId)
				if(_price != i.getCost())
				{
					activeChar.sendActionFailed();
					return;
				}
				else
					break;

		RecipeController.getInstance().requestManufactureItem(manufacturer, activeChar, _recipeId);
	}
}