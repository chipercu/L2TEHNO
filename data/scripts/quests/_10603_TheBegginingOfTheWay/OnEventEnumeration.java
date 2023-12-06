package quests._10603_TheBegginingOfTheWay;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import quests.common.OnEventInterface;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */
public enum OnEventEnumeration {

    EnterWorld(TheBegginingOfTheWayComponent::enterWorld),
    Start(TheBegginingOfTheWayComponent::start);

    private final OnEventInterface onEventInterface;

    OnEventEnumeration(OnEventInterface onEventInterface) {
        this.onEventInterface = onEventInterface;
    }

    public String onEvent(L2Player player, QuestState st, L2NpcInstance npc) {
        return onEventInterface.onEvent(player, st, npc);
    }

}
