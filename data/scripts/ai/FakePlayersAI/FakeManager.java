package ai.FakePlayersAI;

import ai.FakePlayersAI.ClassAI.DoombringerAI;
import ai.FakePlayersAI.ClassAI.FemaleSoulHoundAI;
import ai.FakePlayersAI.ClassAI.MaleSoulHoundAI;
import ai.FakePlayersAI.ClassAI.TricksterAI;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.ai.L2PlayerAI;
import l2open.gameserver.model.L2Player;

public class FakeManager {


    public static L2CharacterAI getAIbyClassId(L2Player player) {
        L2CharacterAI characterAI;

        switch (player.getClassId()) {
            case femaleSoulhound:
                characterAI = new FemaleSoulHoundAI(player);
                break;
            case maleSoulhound:
                characterAI = new MaleSoulHoundAI(player);
                break;
            case doombringer:
                characterAI = new DoombringerAI(player);
                break;
            case trickster:
                characterAI = new TricksterAI(player);
                break;
            default:
                characterAI = new L2PlayerAI(player);
        }
        return characterAI;
    }
}
