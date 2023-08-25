package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2ManufactureItem;
import l2open.gameserver.model.L2ManufactureList;
import l2open.gameserver.model.L2Player;

/**
 * dddd d(ddd)
 */
public class RecipeShopSellList extends L2GameServerPacket
{
	private int obj_id, curMp, maxMp;
	private long buyer_adena;
	private L2ManufactureList createList;

	public RecipeShopSellList(L2Player buyer, L2Player manufacturer)
	{
		obj_id = manufacturer.getObjectId();
		curMp = (int) manufacturer.getCurrentMp();
		maxMp = manufacturer.getMaxMp();
		buyer_adena = buyer.getAdena();
		createList = manufacturer.getCreateList();
	}

	@Override
	protected final void writeImpl()
	{
		if(createList == null)
			return;

		writeC(0xdf);
		writeD(obj_id);
		writeD(curMp);//Creator's MP
		writeD(maxMp);//Creator's MP
		writeQ(buyer_adena);
		writeD(createList.size());
		for(L2ManufactureItem temp : createList.getList())
		{
			writeD(temp.getRecipeId());
			writeD(0x00); //unknown
			writeQ(temp.getCost());
		}
	}
}