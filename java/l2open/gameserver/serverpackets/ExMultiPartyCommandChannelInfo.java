package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

/**
 * Update a Command Channel
 * @author SYS
 *
 * Format:
 * ch sddd[sdd]
 * Пример пакета с оффа (828 протокол):
 * fe 31 00
 * 62 00 75 00 73 00 74 00 65 00 72 00 00 00 - имя лидера СС
 * 00 00 00 00 - ? Looting type
 * 19 00 00 00 - общее число человек в СС
 * 04 00 00 00 - общее число партий в СС
 * [
 * 62 00 75 00 73 00 74 00 65 00 72 00 00 00  - лидер партии 1
 * 36 46 70 4c - ObjId пати лидера 1
 * 08 00 00 00 - количество мемберов в пати 1
 * 4e 00 31 00 67 00 68 00 74 00 48 00 75
 * ]
 */
public class ExMultiPartyCommandChannelInfo extends L2GameServerPacket
{
	private String ChannelLeaderName;
	private int MemberCount;
	private GArray<ChannelPartyInfo> parties;

	public ExMultiPartyCommandChannelInfo(L2CommandChannel channel)
	{
		if(channel == null)
			return;
		ChannelLeaderName = channel.getChannelLeader().getName();
		MemberCount = channel.getMemberCount();

		parties = new GArray<ChannelPartyInfo>();
		for(L2Party party : channel.getParties())
			if(party != null)
			{
				L2Player leader = party.getPartyLeader();
				if(leader != null)
					parties.add(new ChannelPartyInfo(leader.getName(), leader.getObjectId(), party.getMemberCount()));
			}
	}

	@Override
	protected void writeImpl()
	{
		if(parties == null)
			return;

		writeC(EXTENDED_PACKET);
		writeH(0x31);
		writeS(ChannelLeaderName); // имя лидера CC
		writeD(0); // Looting type?
		writeD(MemberCount); // общее число человек в СС
		writeD(parties.size()); // общее число партий в СС

		for(ChannelPartyInfo party : parties)
		{
			writeS(party.Leader_name); // имя лидера партии
			writeD(party.Leader_obj_id); // ObjId пати лидера
			writeD(party.MemberCount); // количество мемберов в пати
		}

		parties.clear();
	}

	static class ChannelPartyInfo
	{
		public String Leader_name;
		public int Leader_obj_id, MemberCount;

		public ChannelPartyInfo(String _Leader_name, int _Leader_obj_id, int _MemberCount)
		{
			Leader_name = _Leader_name;
			Leader_obj_id = _Leader_obj_id;
			MemberCount = _MemberCount;
		}
	}
}