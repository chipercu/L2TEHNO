package npc.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.soi.*;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.tables.ReflectionTable;
import l2open.gameserver.templates.L2NpcTemplate;

public final class MouthOfEkimusInstance extends L2NpcInstance
{
	public MouthOfEkimusInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(command.equalsIgnoreCase("enter_s1"))
		{
			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(cycle == 1 || cycle == 2)
			{
				Quest q = QuestManager.getQuest("_694_BreakThroughTheHallOfSuffering");
				QuestState qs = player.getQuestState(q.getName());
				if(qs == null)
				{
					q.newQuestState(player, Quest.CREATED);
					qs = player.getQuestState(q.getName());
				}
				SeedOfInfinity soi = (SeedOfInfinity)qs.getQuest();
				SystemMessage msg = InstancedZoneManager.checkCondition(ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR1, player, false, player.getName(), null);
				if(msg == null)
				{
					Reflection r = soi.enterPartyInstance(player, ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR1, new HallofSufferingWorld());
					if(r != null)
					{
						L2Party party = player.getParty();
						for(L2Player pl : party.getPartyMembers())
						{
							QuestState qs1 = pl.getQuestState(q.getName());
							if(qs1 == null)
								q.newQuestState(pl, Quest.CREATED);
							pl.setVar("SeedOfInfinityQuest", q.getName());
						}
					}
				}
				else
					player.sendPacket(msg);
			}
			else
				onBypassFeedback(player, "Chat 2");
		}
		else if(command.equalsIgnoreCase("enter_e"))
		{
			Quest q = QuestManager.getQuest("_697_DefendtheHallofErosion");
			QuestState qs = player.getQuestState(q.getName());
			if(qs == null)
			{
				q.newQuestState(player, Quest.CREATED);
				qs = player.getQuestState(q.getName());
			}

			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(cycle == 5)
			{
				SeedOfInfinity soi = (SeedOfInfinity)qs.getQuest();
				SystemMessage msg = InstancedZoneManager.checkCondition(ReflectionTable.SOI_HALL_OF_EROSION_DEFENCE, player, true, player.getName(), null);
				if(msg == null)
				{
					Reflection r = soi.enterCommandChannelInstance(player, ReflectionTable.SOI_HALL_OF_EROSION_DEFENCE, new HallofErosionWorld());
					if(r != null)
					{
						L2CommandChannel commandChannel = getCommandChannel(player);
						// отправляем меседж)
						commandChannel.broadcastToChannelMembers(new ExShowScreenMessage("You will participate in Hall of Erosion Defend shortly. Be prepared for anything.", 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
						// запускаем старт инстанса через 30 секунд
						ThreadPoolManager.getInstance().schedule(new InitialDelayTask(player, soi), 30 * 1000);
					}
				}
				else
					player.sendPacket(msg);
			}
			else
			{
				onBypassFeedback(player, "Chat 2");
			}
		}
		super.onBypassFeedback(player, command);
	}

	protected static L2CommandChannel getCommandChannel(L2Player player)
	{
		return player.getParty() != null ? player.getParty().getCommandChannel() : null;
	}
}