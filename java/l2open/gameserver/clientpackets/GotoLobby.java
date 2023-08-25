package l2open.gameserver.clientpackets;

import l2open.gameserver.serverpackets.CharacterSelectionInfo;

public class GotoLobby extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		sendPacket(new CharacterSelectionInfo(getClient().getLoginName(), getClient().getSessionId().playOkID1));
	}
}