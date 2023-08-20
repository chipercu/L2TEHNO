package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.serverpackets.Ex2ndPasswordCheck;

/**
 * Format: (ch)
 */
public class RequestEx2ndPasswordCheck extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		//if(getClient().getSecondaryAuth() == null)
		//	return;

		if(!ConfigValue.SAEnabled || getClient().getSecondaryAuth().isAuthed())
		{
			sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_OK));
			return;
		}
		getClient().getSecondaryAuth().openDialog();
	}
}