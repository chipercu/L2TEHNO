package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2open.gameserver.model.L2Manor;
import l2open.util.GArray;

/**
 * Format:
 * cddd[ddddcdc[d]c[d]]
 * cddd[dQQQcdc[d]c[d]] - Gracia Final
 *
 */

public class ExShowCropInfo extends L2GameServerPacket
{
	private GArray<CropProcure> _crops;
	private int _manorId;

	public ExShowCropInfo(int manorId, GArray<CropProcure> crops)
	{
		_manorId = manorId;
		_crops = crops;
		if(_crops == null)
			_crops = new GArray<CropProcure>();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET); // Id
		writeH(0x24); // SubId
		writeC(0);
		writeD(_manorId); // Manor ID
		writeD(0);
		writeD(_crops.size());
		for(CropProcure crop : _crops)
		{
			writeD(crop.getId()); // Crop id
			writeQ(crop.getAmount()); // Buy residual
			writeQ(crop.getStartAmount()); // Buy
			writeQ(crop.getPrice()); // Buy price
			writeC(crop.getReward()); // Reward
			writeD(L2Manor.getInstance().getSeedLevelByCrop(crop.getId())); // Seed Level

			writeC(1); // rewrad 1 Type
			writeD(L2Manor.getInstance().getRewardItem(crop.getId(), 1)); // Rewrad 1 Type Item Id

			writeC(1); // rewrad 2 Type
			writeD(L2Manor.getInstance().getRewardItem(crop.getId(), 2)); // Rewrad 2 Type Item Id
		}
	}
}