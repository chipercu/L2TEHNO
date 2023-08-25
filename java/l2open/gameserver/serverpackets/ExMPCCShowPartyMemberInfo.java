package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.util.GArray;

/**
 * Format: ch d[Sdd]
 * @author SYS
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	private GArray<PartyMemberInfo> members;

	public ExMPCCShowPartyMemberInfo(L2Player partyLeader)
	{
		if(!partyLeader.isInParty())
			return;

		L2Party _party = partyLeader.getParty();
		if(_party == null)
			return;

		if(!_party.isInCommandChannel())
			return;

		members = new GArray<PartyMemberInfo>();
		for(L2Player _member : _party.getPartyMembers())
			members.add(new PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getClassId().getId()));
	}

	@Override
	protected final void writeImpl()
	{
		if(members == null)
			return;

		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x4C : 0x4b);
		writeD(members.size()); // Количество членов в пати

		for(PartyMemberInfo member : members)
		{
			writeS(member.name); // Имя члена пати
			writeD(member.object_id); // object Id члена пати
			writeD(member.class_id); // id класса члена пати
		}

		members.clear();
	}

	static class PartyMemberInfo
	{
		public String name;
		public int object_id, class_id;

		public PartyMemberInfo(String _name, int _object_id, int _class_id)
		{
			name = _name;
			object_id = _object_id;
			class_id = _class_id;
		}
	}
}