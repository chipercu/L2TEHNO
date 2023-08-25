package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;

/**
 * dddddQ
 */
public class RecipeShopItemInfo extends L2GameServerPacket
{
	private int _recipeId, _shopId, curMp, maxMp;
	private int _success = 0xFFFFFFFF;
	private long _price;
	private boolean can_writeImpl = false;

	public RecipeShopItemInfo(int shopId, int recipeId, long price, int success, L2Player activeChar)
	{
		_recipeId = recipeId;
		_shopId = shopId;
		_price = price;
		_success = success;

		L2Object manufacturer = activeChar.getVisibleObject(_shopId);

		if(manufacturer == null)
			return;

		if(!manufacturer.isPlayer())
			return;

		curMp = (int) ((L2Player) manufacturer).getCurrentMp();
		maxMp = ((L2Player) manufacturer).getMaxMp();
		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeC(0xe0);
		writeD(_shopId);
		writeD(_recipeId);
		writeD(curMp);
		writeD(maxMp);
		writeD(_success);
		writeQ(_price);
	}
}