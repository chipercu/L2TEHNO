package l2open.gameserver.serverpackets;

public class ExPartyMemberRenamed extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xA6);
		//writeD(_objectId);
		//writeD(_isDisguised);
		//writeD(_isDisguised ? _dominionId : 0);
	}
}