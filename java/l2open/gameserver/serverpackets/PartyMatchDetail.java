package l2open.gameserver.serverpackets;

import l2open.gameserver.instancemanager.PartyRoomManager;
import l2open.gameserver.model.*;
import l2open.util.GArray;

import java.util.*;

/**
 * Format:(c) dddddds
 */
public class PartyMatchDetail extends L2GameServerPacket
{
	private Collection<PartyRoom> _rooms;
	private int _fullSize;

	public PartyMatchDetail(L2Player player)
	{
		this(player.getPartyMatchingRegion(), player.getPartyMatchingLevels(), 1, player);
	}

	public PartyMatchDetail(int region, int lvlRst, int page, L2Player activeChar)
	{
		int first = (page - 1) * 64;
		int firstNot = page * 64;
		_rooms = new GArray<PartyRoom>();

		int i = 0;
		GArray<PartyRoom> temp = PartyRoomManager.getInstance().getRooms(region, lvlRst, activeChar);
		_fullSize = temp.size();
		for(PartyRoom room : temp)
		{
			if(i < first || i >= firstNot)
				continue;
			_rooms.add(room);
			i++;
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9c);
		writeD(_fullSize); // unknown
		writeD(_rooms.size()); // room count

		for(PartyRoom room : _rooms)
		{
			writeD(room.getId()); //room id
			writeS(room.getLeader() == null ? "None" : room.getLeader().getName());
			writeD(room.getLocation());
			writeD(room.getMinLevel()); //min level
			writeD(room.getMaxLevel()); //max level
			writeD(room.getMaxMembers()); //max members coun
			writeS(room.getTitle()); // room name

			writeD(room.getMembers().size()); //members count
			for(L2Player player : room.getMembers())
			{
				writeD(player.getClassId().getId());
				writeS(player.getName());
			}
		}
		/*	Talking Island - 1
			Gludio - 2
			Dark Elven Ter. - 3
			Elven Territory - 4
			Dion - 5
			Giran - 6
			Neutral Zone - 7
			Schuttgart - 9
			Oren - 10
			Hunters Village - 11
			Innadril - 12
			Aden - 13
			Rune - 14
			Goddard - 15 */
	}
}