package quests._274_SkirmishWithTheWerewolves;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.base.Race;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _274_SkirmishWithTheWerewolves extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int MARAKU_WEREWOLF_HEAD = 1477;
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	private static final int MARAKU_WOLFMEN_TOTEM = 1501;

	public _274_SkirmishWithTheWerewolves()
	{
		super(false);
		addStartNpc(30569);

		addKillId(20363);
		addKillId(20364);

		addQuestItem(MARAKU_WEREWOLF_HEAD);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("prefect_brukurse_q0274_03.htm"))
		{
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
		int cond = st.getInt("cond");
		if(id == CREATED)
			if(st.getPlayer().getRace() != Race.orc)
			{
				htmltext = "prefect_brukurse_q0274_00.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getPlayer().getLevel() < 9)
			{
				htmltext = "prefect_brukurse_q0274_01.htm";
				st.exitCurrentQuest(true);
			}
			else if(st.getQuestItemsCount(NECKLACE_OF_VALOR) > 0 || st.getQuestItemsCount(NECKLACE_OF_COURAGE) > 0)
			{
				htmltext = "prefect_brukurse_q0274_02.htm";
				return htmltext;
			}
			else
				htmltext = "prefect_brukurse_q0274_07.htm";
		else if(cond == 1)
			htmltext = "prefect_brukurse_q0274_04.htm";
		else if(cond == 2)
			if(st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 40)
				htmltext = "prefect_brukurse_q0274_04.htm";
			else
			{
				st.takeItems(MARAKU_WEREWOLF_HEAD, -1);
				st.giveItems(ADENA_ID, 3500, true);
				if(st.getQuestItemsCount(MARAKU_WOLFMEN_TOTEM) >= 1)
				{
					st.giveItems(ADENA_ID, st.getQuestItemsCount(MARAKU_WOLFMEN_TOTEM) * 600, true);
					st.takeItems(MARAKU_WOLFMEN_TOTEM, -1);
				}
				htmltext = "prefect_brukurse_q0274_05.htm";
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getInt("cond") == 1 && st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 40)
		{
			if(st.getQuestItemsCount(MARAKU_WEREWOLF_HEAD) < 39)
				st.playSound(SOUND_ITEMGET);
			else
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
			}
			st.giveItems(MARAKU_WEREWOLF_HEAD, 1);
		}
		if(Rnd.chance(5))
			st.giveItems(MARAKU_WOLFMEN_TOTEM, 1);
		return null;
	}
}