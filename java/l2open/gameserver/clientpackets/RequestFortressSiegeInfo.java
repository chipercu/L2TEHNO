package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.FortressManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Fortress;
import l2open.gameserver.serverpackets.ExShowFortressSiegeInfo;

public class RequestFortressSiegeInfo extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		for(Fortress fort : FortressManager.getInstance().getFortresses().values())
			if(fort != null && fort.getSiege().isInProgress())
				activeChar.sendPacket(new ExShowFortressSiegeInfo(fort));
	}
}