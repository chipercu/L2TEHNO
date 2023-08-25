package l2open.gameserver.serverpackets;

public class ExPVPMatchRecord extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x7E);
		// TODO ddddd d[Sdd] d[Sdd]	(currentState:%d blueTeamTotalKillCnt:%d, redTeamTotalKillCnt:%d)
	}
}