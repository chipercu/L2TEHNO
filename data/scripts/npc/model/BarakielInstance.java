package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.instances.L2NpcInstance.AggroInfo;
import l2open.gameserver.model.instances.L2RaidBossInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.*;

import java.util.HashMap;

public class BarakielInstance extends L2RaidBossInstance
{
	public BarakielInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(final L2Character killer)
	{
		HashMap<L2Playable, AggroInfo> aggroList = getAggroMap();

		if(aggroList != null && !aggroList.isEmpty())
		{
			Quest quest = QuestManager.getQuest(246);
			for(L2Playable pl : aggroList.keySet())
				if(pl.isPlayer() && pl.getReflectionId() == getReflectionId() && (pl.isInRange(this, ConfigValue.AltPartyDistributionRange) || pl.isInRange(killer, ConfigValue.AltPartyDistributionRange)) && Math.abs(pl.getZ() - getZ()) < 400)
				{
					QuestState qs = pl.getPlayer().getQuestState(quest.getName());
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
		}
		super.doDie(killer);
		
	}
}