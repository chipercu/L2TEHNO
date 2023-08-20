package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Cubic;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;

public class RequestTargetCanceld extends L2GameClientPacket
{
    private int _unselect;

    /**
     * packet type id 0x48
     * format:		ch
     */
    @Override
    public void readImpl()
	{
        _unselect = readH();
    }

    @Override
    public void runImpl()
	{
        L2Player activeChar = getClient().getActiveChar();
        if(activeChar == null)
            return;

        for(L2Cubic cubic : activeChar.getCubics())
            cubic.stopAttack();

        if(activeChar.getAgathion() != null)
            activeChar.getAgathion().stopAction();

		if(activeChar.getAggressionTarget() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

        if(_unselect == 0)
		{
            if(activeChar.isCastingNow())
				activeChar.abortCast(false);
			else if(activeChar.getTarget() != null)
                activeChar.setTarget(null);
        }
		else if(activeChar.getTarget() != null)
            activeChar.setTarget(null);
    }
}