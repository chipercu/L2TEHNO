package l2open.gameserver.serverpackets;

import javolution.util.FastMap;
import l2open.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2open.gameserver.model.L2Manor;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

/**
 * format
 * dd[dddc[d]c[d]dddcd]
 * dd[dddc[d]c[d]dQQcQ] - Gracia Final
 */
public class ExShowSellCropList extends L2GameServerPacket
{
	private int _manorId = 1;
	private FastMap<Integer, L2ItemInstance> _cropsItems;
	private FastMap<Integer, CropProcure> _castleCrops;

	public ExShowSellCropList(L2Player player, int manorId, GArray<CropProcure> crops)
	{
		_manorId = manorId;
		_castleCrops = new FastMap<Integer, CropProcure>();
		_cropsItems = new FastMap<Integer, L2ItemInstance>();

		GArray<Integer> allCrops = L2Manor.getInstance().getAllCrops();
		for(int cropId : allCrops)
		{
			L2ItemInstance item = player.getInventory().getItemByItemId(cropId);
			if(item != null)
				_cropsItems.put(cropId, item);
		}

		for(CropProcure crop : crops)
			if(_cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0)
				_castleCrops.put(crop.getId(), crop);

	}

	@Override
	public void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x2c);

		writeD(_manorId); // manor id
		writeD(_cropsItems.size()); // size

		for(L2ItemInstance item : _cropsItems.values())
		{
			writeD(item.getObjectId()); // Object id
			writeD(item.getItemId()); // crop id
			writeD(L2Manor.getInstance().getSeedLevelByCrop(item.getItemId())); // seed level

			writeC(1);
			writeD(L2Manor.getInstance().getRewardItem(item.getItemId(), 1)); // reward 1 id

			writeC(1);
			writeD(L2Manor.getInstance().getRewardItem(item.getItemId(), 2)); // reward 2 id

			if(_castleCrops.containsKey(item.getItemId()))
			{
				CropProcure crop = _castleCrops.get(item.getItemId());
				writeD(_manorId); // manor
				writeQ(crop.getAmount()); // buy residual
				writeQ(crop.getPrice()); // buy price
				writeC(crop.getReward()); // reward
			}
			else
			{
				writeD(0xFFFFFFFF); // manor
				writeQ(0); // buy residual
				writeQ(0); // buy price
				writeC(0); // reward
			}
			writeQ(item.getCount()); // my crops
		}
	}
}