package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.communitybbs.CommunityBoard;

public class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	public void readImpl()
	{
		_unknown = readD(); //always 1
	}

	@Override
	public void runImpl()
	{
		CommunityBoard.getInstance().handleCommands(getClient(), ConfigValue.BBSDefault);
	}
}