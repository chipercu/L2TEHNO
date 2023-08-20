package l2open.gameserver.serverpackets;

public class ExChangeMPCost extends L2GameServerPacket
{
	private final int unk1;
	private final double unk2;
	
	public ExChangeMPCost(int unk1, double unk2)
	{
		this.unk1 = unk1;
		this.unk2 = unk2;
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0xEB);
		writeD(unk1);// TODO unknown
		writeF(unk2);// TODO unknown
	}
}
