package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.ExWaitWaitingSubStituteInfo;
/**
 * @author ALF
 * @update Darvin
 * @data 20.02.2012
 */
public final class RequestRegistWaitingSubstitute extends L2GameClientPacket
{
    private int _key;

    @Override
    protected void readImpl()
	{
        _key = readD();
    }

    @Override
    protected void runImpl()
	{
        L2Player activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;

        switch (_key)
		{
			case 0:
				activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(ExWaitWaitingSubStituteInfo.WAITING_CANCEL));
				//PartySubstituteManager.getInstance().removePlayerFromParty(activeChar);
				//activeChar.sendPacket(SystemMsg.STOPPED_SEARCHING_THE_PARTY);
				break;
			case 1:
				activeChar.sendPacket(new ExWaitWaitingSubStituteInfo(ExWaitWaitingSubStituteInfo.WAITING_CANCEL));
				//PartySubstituteManager.getInstance().addPlayerToParty(activeChar);
				break;
			default:
				_log.info("RequestRegistWaitingSubstitute: key is " + _key);
				break;
        }
    }
}
