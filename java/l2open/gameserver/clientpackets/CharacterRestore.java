package l2open.gameserver.clientpackets;

import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.CharacterSelectionInfo;

public class CharacterRestore extends L2GameClientPacket
{
	// cd
	private int _charSlot;

	@Override
	public void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	public void runImpl()
	{
		L2GameClient client = getClient();
		try
		{
			client.markRestoredChar(_charSlot);
		}
		catch(Exception e)
		{}
		CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLoginName(), client.getSessionId().playOkID1);
		sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}