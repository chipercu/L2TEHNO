package l2open.gameserver.clientpackets;

import l2open.gameserver.communitybbs.CommunityBoard;

/**
 * Format cSSSSSS
 */
public class RequestBBSwrite extends L2GameClientPacket
{
	private String _url;
	private String _arg1;
	private String _arg2;
	private String _arg3;
	private String _arg4;
	private String _arg5;

	@Override
	public void readImpl()
	{
		_url = readS();
		_arg1 = readS();
		_arg2 = readS();
		_arg3 = readS();
		_arg4 = readS();
		_arg5 = readS();
	}

	@Override
	public void runImpl()
	{
		CommunityBoard.getInstance().handleWriteCommands(getClient(), _url, _arg1, _arg2, _arg3, _arg4, _arg5);
	}
}