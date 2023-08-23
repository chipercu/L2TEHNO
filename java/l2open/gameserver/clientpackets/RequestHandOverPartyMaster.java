package l2open.gameserver.clientpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;

public class RequestHandOverPartyMaster extends L2GameClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readS(ConfigValue.cNameMaxLen);
    }

    @Override
    public void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isInParty() && activeChar.getParty().isLeader(activeChar)) {
            if (Olympiad.isRegistered(activeChar) || activeChar.getOlympiadGame() != null || activeChar.isInOlympiadMode())
                return;
            activeChar.getParty().changePartyLeader(_name);
        }
    }
}