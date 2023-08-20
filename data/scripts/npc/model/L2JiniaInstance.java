package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import bosses.FreyaManager;
import quests._10284_AcquisitionOfDivineSword._10284_AcquisitionOfDivineSword;

/**
 * @author Diagod
 * Инстанс для Зинии, Кегора и Сирры...
 */
public class L2JiniaInstance extends L2NpcInstance
{
	public L2JiniaInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(getNpcId() == 32781)
		{
			QuestState _prev = player.getQuestState("_10285_MeetingSirra");
			QuestState _prev2 = player.getQuestState("_10286_ReunionWithSirra");

			if((_prev != null && _prev.getInt("progress") == 2) || (_prev2 != null && (_prev2.getInt("progress") == 2 || _prev2.getInt("progress") == 3)))
				showQuestWindow(player);
			else
			{
				if(player.getLevel() < 82)
					showHtmlFile(player, "jinia_npc2001.htm");
				else
					showHtmlFile(player, "jinia_npc2002.htm");
			}
		}
		else if(getNpcId() == 32762)
		{
			QuestState _prev = player.getQuestState("_10285_MeetingSirra");
			QuestState _prev2 = player.getQuestState("_10286_ReunionWithSirra");
			if((_prev != null && _prev.getInt("progress") == 1) || (_prev2 != null && _prev2.getInt("progress") == 1))
				showQuestWindow(player);
			else
			{
				if(!FreyaManager.isBatle(player))
					showHtmlFile(player, "sirr_npc001.htm");
				else
					showHtmlFile(player, "sirr_npc002.htm");
			}
		}
		else if(getNpcId() == 18846)
		{
			try
			{
				QuestState hostQuest = player.getQuestState("_10284_AcquisitionOfDivineSword");
				if(hostQuest != null && (hostQuest.getInt("progress") == 2 && hostQuest.getInt("cond") == 4) || (hostQuest.getInt("progress") == 3 && hostQuest.getInt("cond") == 6) || (player.getReflection() != null && player.getReflection().getName() != null && player.getReflection().getName().equals("icequeen_kegor")))
				{
					if(getNpcId() == 18846)
					{
						_10284_AcquisitionOfDivineSword.World world = _10284_AcquisitionOfDivineSword.getWorld(player.getReflectionId());
						if(world != null)
						{
							if(world.KEGOR == null)
								world.KEGOR = this;
		
							if(hostQuest.getState() == 3)
							{
								showHtmlFile(player, "kegor_savedun_q10284_04.htm");
								return;
							}

							if(!world.underAttack && hostQuest.getInt("progress") == 2)
							{
								showHtmlFile(player, "kegor_savedun001.htm");
								return;
							}
							else if(hostQuest.getInt("progress") == 3)
							{
								hostQuest.giveItems(57, 296425);
								hostQuest.addExpAndSp(921805, 82230);
								hostQuest.playSound("ItemSound.quest_finish");
								hostQuest.exitCurrentQuest(false);
								showHtmlFile(player, "kegor_savedun_q10284_03.htm");
								return;
							}
							else
							{
								showHtmlFile(player, "kegor_savedun_q10284_02.htm");
								return;
							}
						}
					}
				}
				else
				{
					if(ConfigValue.DEBUG_FREYA || (player.getParty() != null && player.getParty().getCommandChannel() != null && player.getParty().getCommandChannel().getChannelLeader().getObjectId() == player.getObjectId()))
						showHtmlFile(player, "kegor001.htm");
					else
						showHtmlFile(player, "kegor002.htm");
				}
			}
			catch(NullPointerException e)
			{
				if(ConfigValue.DEBUG_FREYA || (player.getParty() != null && player.getParty().getCommandChannel() != null && player.getParty().getCommandChannel().getChannelLeader().getObjectId() == player.getObjectId()))
					showHtmlFile(player, "kegor001.htm");
				else
					showHtmlFile(player, "kegor002.htm");
			}
		}
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(command.equalsIgnoreCase("freezing_core"))
		{
			if(OwnItemCount(player,15469) > 0 || OwnItemCount(player,15470) > 0)
			{
				showHtmlFile(player, "jinia_npc2009.htm");
				return;
			}
			else if(GetOneTimeQuestFlag(player,10286) == 1)
			{
				showHtmlFile(player, "jinia_npc2008.htm");
				GiveItem1(player,15469,1);
			}
			else if(GetOneTimeQuestFlag(player,10286) == 0)
			{
				showHtmlFile(player, "jinia_npc2008.htm");
				GiveItem1(player,15470,1);
			}
		}
		else if(command.equalsIgnoreCase("exit"))
		{
			QuestState hostQuest = player.getQuestState("_10284_AcquisitionOfDivineSword");
			if(hostQuest.getState() == 3)
				player.getReflection().startCollapseTimer(1000);
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}
}