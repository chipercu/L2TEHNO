package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestRecipeShopMessageSet extends L2GameClientPacket
{
	// format: cS
	private String _name;

	@Override
	public void readImpl()
	{
		_name = readS(16);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _name.length() > 16)
			return;

		if(activeChar.getDuel() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getCreateList() != null)
			activeChar.getCreateList().setStoreName(_name);
	}
}