package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExUISetting;

public class RequestKeyMapping extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();

		if(player != null)
			player.sendPacket(new ExUISetting(player.getKeyBindings()));
	}
}