package quests.common;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;

/**
 * Created by a.kiperku
 * Date: 06.12.2023
 */
@FunctionalInterface
public interface OnEventInterface {
    String onEvent(L2Player player, QuestState st, L2NpcInstance npc);
}
