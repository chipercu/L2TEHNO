package quests._003_WilltheSealbeBroken;

// version = Unknown

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Рейты учтены
 */
public class _003_WilltheSealbeBroken extends Quest implements ScriptFile
{
	int StartNpc = 30141;
	int[] Monster = { 20031, 20041, 20046, 20048, 20052, 20057 };

	int OnyxBeastEye = 1081;
	int TaintStone = 1082;
	int SuccubusBlood = 1083;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _003_WilltheSealbeBroken()
	{
		super(false);
		addStartNpc(StartNpc);

		for(int npcId : Monster)
			addKillId(npcId);

		addQuestItem(new int[] { OnyxBeastEye, TaintStone, SuccubusBlood });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "redry_q0003_03.htm";
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		if(id == CREATED)
			if(st.getPlayer().getRace() != Race.darkelf)
			{
				htmltext = "redry_q0003_00.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() >= 16)
			{
				htmltext = "redry_q0003_02.htm";
				return htmltext;
			}
			else
			{
				htmltext = "redry_q0003_01.htm";
				st.exitCurrentQuest(true);
			}
		else if(id == STARTED)
			if(st.getQuestItemsCount(OnyxBeastEye) > 0 && st.getQuestItemsCount(TaintStone) > 0 && st.getQuestItemsCount(SuccubusBlood) > 0)
			{
				htmltext = "redry_q0003_06.htm";
				st.takeItems(OnyxBeastEye, -1);
				st.takeItems(TaintStone, -1);
				st.takeItems(SuccubusBlood, -1);
				st.giveItems(956, 1, true);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
				htmltext = "redry_q0003_04.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();
		if(id == STARTED)
		{
			if(npcId == Monster[0] && st.getQuestItemsCount(OnyxBeastEye) == 0)
			{
				st.giveItems(OnyxBeastEye, 1, false);
				st.playSound(SOUND_ITEMGET);
			}
			else if((npcId == Monster[1] || npcId == Monster[2]) && st.getQuestItemsCount(TaintStone) == 0)
			{
				st.giveItems(TaintStone, 1, false);
				st.playSound(SOUND_ITEMGET);
			}
			else if((npcId == Monster[3] || npcId == Monster[4] || npcId == Monster[5]) && st.getQuestItemsCount(SuccubusBlood) == 0)
			{
				st.giveItems(SuccubusBlood, 1, false);
				st.playSound(SOUND_ITEMGET);
			}
			if(st.getQuestItemsCount(OnyxBeastEye) > 0 && st.getQuestItemsCount(TaintStone) > 0 && st.getQuestItemsCount(SuccubusBlood) > 0)
			{
				st.set("cond", "2");
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}