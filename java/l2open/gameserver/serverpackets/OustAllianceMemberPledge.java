package l2open.gameserver.serverpackets;

public class OustAllianceMemberPledge extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xAC);
		//TODO d
	}
}