package l2open.gameserver.serverpackets;

import l2open.gameserver.model.SubPledge;

public class PledgeReceiveSubPledgeCreated extends L2GameServerPacket
{
	private int type;
	private String _name, leader_name;

	public PledgeReceiveSubPledgeCreated(SubPledge subPledge)
	{
		type = subPledge.getType();
		_name = subPledge.getName();
		leader_name = subPledge.getLeaderName();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x40);

		writeD(0x01);
		writeD(type);
		writeS(_name);
		writeS(leader_name);
	}
}