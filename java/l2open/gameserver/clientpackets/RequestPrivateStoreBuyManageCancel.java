package l2open.gameserver.clientpackets;

public class RequestPrivateStoreBuyManageCancel extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
		_log.info(getType());
	}

	@Override
	public void readImpl()
	{}
}