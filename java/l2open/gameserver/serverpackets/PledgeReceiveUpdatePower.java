package l2open.gameserver.serverpackets;

public class PledgeReceiveUpdatePower extends L2GameServerPacket
{
	private int _privs;

	public PledgeReceiveUpdatePower(int privs)
	{
		_privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x42);
		writeD(_privs); //Filler??????
	}
}