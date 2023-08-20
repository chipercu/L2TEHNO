package npc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.*;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestEventType;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

/**
 * @author VISTALL
 * @date 6:11/07.06.2011
 */
public class CatapultInstance extends SiegeToggleNpcInstance
{
	public CatapultInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onDeathImpl(L2Character lastAttacker)
	{
		if(!TerritorySiege.isInProgress())
			return;

		//ThreadPoolManager.getInstance().execute(new L2ObjectTasks.NotifyAITask(this, CtrlEvent.EVT_DEAD, lastAttacker, null));

		L2Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		//Map<L2Playable, AggroList.HateInfo> aggroMap = getAggroList().getPlayableMap();
		Map<L2Playable, AggroInfo> aggroMap = getAggroMap();

		Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOBKILLED);
		if(quests != null && quests.length > 0)
		{
			List<L2Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && ConfigValue.NoLasthitOnRaid) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<L2Player>();
				for(L2Playable pl : aggroMap.keySet())
					if(!pl.isDead() && (isInRangeZ(pl, ConfigValue.AltPartyDistributionRange) || killer.isInRangeZ(pl, ConfigValue.AltPartyDistributionRange)))
						players.add(pl.getPlayer());
			}
			else if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<L2Player>(killer.getParty().getMemberCount());
				for(L2Player pl : killer.getParty().getPartyMembers())
					if(!pl.isDead() && (isInRangeZ(pl, ConfigValue.AltPartyDistributionRange) || killer.isInRangeZ(pl, ConfigValue.AltPartyDistributionRange)))
						players.add(pl);
			}

			for(Quest quest : quests)
			{
				L2Player toReward = killer;
				if(quest.getParty() != Quest.PARTY_NONE && players != null)
					if(isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(L2Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if(qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<L2Player> interested = new ArrayList<L2Player>(players.size());
						for(L2Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
								interested.add(pl);
						}

						if(interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
							toReward = killer;
					}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getName());
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
			}
		}
	}
}
