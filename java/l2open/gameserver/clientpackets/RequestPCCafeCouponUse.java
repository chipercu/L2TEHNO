package l2open.gameserver.clientpackets;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket
{
	// format: (ch)S
	private String _code;

	@Override
	public void readImpl()
	{
		_code = readS().toUpperCase();
	}

	@Override
	public void runImpl()
	{
	}
}