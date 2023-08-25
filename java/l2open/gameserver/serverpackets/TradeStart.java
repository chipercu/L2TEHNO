package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;

import java.util.*;

//0x2e TradeStart   d h (h dddhh dhhh)
public class TradeStart extends L2GameServerPacket
{
	private List<L2ItemInstance> _tradelist = new ArrayList<L2ItemInstance>();
	private boolean can_writeImpl = false;
	private int requester_obj_id;

	public TradeStart(L2Player me, L2Player other)
	{
		if(me == null)
			return;

		requester_obj_id = other.getObjectId();

		L2ItemInstance[] inventory = me.getInventory().getItems();
		for(L2ItemInstance item : inventory)
			if(item != null && item.canBeTraded(me))
				_tradelist.add(item);
		Collections.sort(_tradelist, Inventory.OrderComparator);

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeC(0x14);
		writeD(requester_obj_id);
		int count = _tradelist.size();
		writeH(count);//count??

		for(L2ItemInstance temp : _tradelist)
			writeItemInfo(temp);
	}
}