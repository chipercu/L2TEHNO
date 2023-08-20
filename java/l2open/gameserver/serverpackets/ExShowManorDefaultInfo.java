package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Manor;
import l2open.util.GArray;

/**
 * format(packet 0xFE)
 * ch cd [ddddcdcd]
 * c  - id
 * h  - sub id
 *
 * c
 * d  - size
 *
 * [
 * d  - level
 * d  - seed price
 * d  - seed level
 * d  - crop price
 * c
 * d  - reward 1 id
 * c
 * d  - reward 2 id
 * ]
 *
 */
public class ExShowManorDefaultInfo extends L2GameServerPacket
{
	private GArray<Integer> _crops = null;

	public ExShowManorDefaultInfo()
	{
		_crops = L2Manor.getInstance().getAllCrops();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x25);
		writeC(0);
		writeD(_crops.size());
		for(int cropId : _crops)
		{
			writeD(cropId); // crop Id
			writeD(L2Manor.getInstance().getSeedLevelByCrop(cropId)); // level
			writeD(L2Manor.getInstance().getSeedBasicPriceByCrop(cropId)); // seed price
			writeD(L2Manor.getInstance().getCropBasicPrice(cropId)); // crop price
			writeC(1); // rewrad 1 Type
			writeD(L2Manor.getInstance().getRewardItem(cropId, 1)); // Rewrad 1 Type Item Id
			writeC(1); // rewrad 2 Type
			writeD(L2Manor.getInstance().getRewardItem(cropId, 2)); // Rewrad 2 Type Item Id
		}
	}
}