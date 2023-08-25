package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:21
 */
public class RequestPartyLootModification extends L2GameClientPacket {
    private int mode;

    @Override
    protected void readImpl() {
        mode = readD();
    }

    @Override
    protected void runImpl() {
        L2Player leader = getClient().getActiveChar();
        if (leader == null)
            return;

        L2Party party = leader.getParty();

        if (party == null || !party.isLeader(leader))
            return;

        party.requestLootModification(mode);
    }
}
