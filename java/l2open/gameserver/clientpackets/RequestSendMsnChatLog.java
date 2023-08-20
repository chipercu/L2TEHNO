package l2open.gameserver.clientpackets;

@SuppressWarnings("unused")
public class RequestSendMsnChatLog extends L2GameClientPacket
{
	private int unk3;
	private String unk, unk2;

	@Override
	public void runImpl()
	{
	//_log.info(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
	}

	/**
	 * format: SSd
	 */
	@Override
	public void readImpl()
	{
		unk = readS();
		unk2 = readS();
		unk3 = readD();
	}
}