package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;

/**
 * @author : Ragnarok
 * @date : 19.12.10    13:38
 */
public class AnswerPartyLootModification extends L2GameClientPacket {
    int answer;

    @Override
    protected void readImpl() {
        answer = readD();// 0 - не принял, 1 - принял
    }

    @Override
    protected void runImpl() {
        L2Player player = getClient().getActiveChar();
        if (player == null || player.getParty() == null)
            return;
        player.getParty().answerLootModification(player, answer);
    }
}
