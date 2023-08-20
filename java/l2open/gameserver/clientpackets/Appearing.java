package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;

public class Appearing extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		final L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(System.currentTimeMillis() - activeChar.getLastAppearingPacket() < ConfigValue.AppearingPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastAppearingPacket();

		if(activeChar.isLogoutStarted())
		{
			activeChar.sendActionFailed();
			return;
		}

		//_log.info("Appearing: "+activeChar.getObserverMode());
		if(activeChar.getObserverMode() == 1)
		{
			activeChar.appearObserverMode();
			return;
		}

		if(activeChar.getObserverMode() == 2)
		{
			activeChar.returnFromObserverMode();
			return;
		}

		if(!activeChar.isTeleporting())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.onTeleported();
	}
}