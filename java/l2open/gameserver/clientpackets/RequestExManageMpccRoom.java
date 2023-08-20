package l2open.gameserver.clientpackets;

public class RequestExManageMpccRoom extends L2GameClientPacket
{
	private int unk, unk2, unk3, unk4, unk5;
	private String unk6;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3 + " :: " + unk4 + " :: " + unk5 + " :: " + unk6);
	}

	/**
	 * format: dddddS
	 */
	@Override
	public void readImpl()
	{
		unk = readD();
		unk2 = readD();
		unk3 = readD();
		unk4 = readD();
		unk5 = readD();
		unk6 = readS();
	}
}