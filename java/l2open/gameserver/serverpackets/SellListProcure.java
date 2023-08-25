package l2open.gameserver.serverpackets;

import javolution.util.FastMap;
import l2open.gameserver.instancemanager.CastleManager;
import l2open.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.util.GArray;

import java.util.Map;

public class SellListProcure extends L2GameServerPacket
{
	private long _money;
	private Map<L2ItemInstance, Long> _sellList = new FastMap<L2ItemInstance, Long>();
	private GArray<CropProcure> _procureList = new GArray<CropProcure>();
	private int _castle;

	public SellListProcure(L2Player player, int castleId)
	{
		_money = player.getAdena();
		_castle = castleId;
		_procureList = CastleManager.getInstance().getCastleByIndex(_castle).getCropProcure(0);
		for(CropProcure c : _procureList)
		{
			L2ItemInstance item = player.getInventory().getItemByItemId(c.getId());
			if(item != null && c.getAmount() > 0)
				_sellList.put(item, c.getAmount());
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xef);
		writeQ(_money);
		writeD(0x00); // lease ?
		writeH(_sellList.size()); // list size

		for(L2ItemInstance item : _sellList.keySet())
		{
			writeH(item.getItem().getType1());
			writeD(item.getObjectId());
			writeD(item.getItemId());
			writeQ(_sellList.get(item));
			writeH(item.getItem().getType2ForPackets());
			writeH(0); // size of [dhhh]
			writeQ(0); // price, u shouldnt get any adena for crops, only raw materials
		}
	}
}