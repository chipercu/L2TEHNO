package l2open.gameserver.serverpackets;

import java.util.*;

import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.PartyRoom;

public class ExListMpccWaiting extends L2GameServerPacket
{
	private static final int PAGE_SIZE = 10;
	private int _fullSize;
	private List<PartyRoom> _list;

	public ExListMpccWaiting(L2Player player, int page, int location, boolean allLevels)
	{
		int first = (page - 1) * PAGE_SIZE;
		int firstNot = page * PAGE_SIZE;
		int i = 0;
		Collection<PartyRoom> all = PartyRoomManager.getInstance().getRooms(location, allLevels ? 1 : 0, player); // TODO: CC_MATCHING
		_fullSize = all.size();
		_list = new ArrayList<PartyRoom>(PAGE_SIZE);
		for(PartyRoom c : all)
		{
			if(i < first || i >= firstNot)
				continue;
			_list.add(c);
			i++;
		}
	}

	@Override
	protected void writeImpl()
	{
	  	writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x9D : 0x9C);
		writeD(_fullSize);
		writeD(_list.size());
		for(PartyRoom room : _list)
		{
			writeD(room.getId());
			writeS(room.getTitle());
			writeD(room.getMembers().size());
			writeD(room.getMinLevel());
			writeD(room.getMaxLevel());
			writeD(1);  //min group
			writeD(room.getMaxMembers());   //max group
			L2Player leader = room.getLeader();
			writeS(leader == null ? "" : leader.getName());
		}
	}
}