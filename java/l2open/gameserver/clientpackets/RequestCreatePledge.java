package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

public class RequestCreatePledge extends L2GameClientPacket
{
	//Format: cS
	private String _pledgename;

	@Override
	public void readImpl()
	{
		_pledgename = readS(64);
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		_log.info("Unfinished packet: " + getType() + " // S: " + _pledgename);
	}
}