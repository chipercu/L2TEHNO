package quests._312_TakeAdvantageOfTheCrisis;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _312_TakeAdvantageOfTheCrisis extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static int FILAUR = 30535;

	private static int MINERAL_FRAGMENT = 14875;
	private static int DROP_CHANCE = 60;

	private static int[] MINE_MOBS = new int[] { 22678, 22679, 22680, 22681, 22682, 22683, 22684, 22685, 22686, 22687,
			22688, 22689, 22690 };

	public _312_TakeAdvantageOfTheCrisis()
	{
		super(false);

		addStartNpc(FILAUR);
		addKillId(MINE_MOBS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30535-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("30535-09.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		else
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(event);
			}
			catch(Exception e)
			{}

			if(id > 0)
			{
				int count = 0;
				switch(id)
				{
					case 9487:
						count = 244;
						break;
					case 9488:
						count = 153;
						break;
					case 9489:
						count = 122;
						break;
					case 9490:
					case 9491:
						count = 82;
						break;
					case 9497:
						count = 86;
						break;
					case 9625:
						count = 667;
						break;
					case 9626:
						count = 1000;
						break;
					case 9628:
						count = 24;
						break;
					case 9629:
						count = 43;
						break;
					case 9630:
						count = 36;
						break;
				}
				if(count > 0)
				{
					if(st.getQuestItemsCount(MINERAL_FRAGMENT) >= count)
					{
						st.giveItems(id, 1);
						st.takeItems(MINERAL_FRAGMENT, count);
						st.playSound(SOUND_MIDDLE);
						return "30535-16.htm";
					}
					return "30535-15.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		if(npcId == FILAUR)
			if(cond == 0)
			{
				if(st.getPlayer().getLevel() >= 80)
					htmltext = "30535-01.htm";
				else
				{
					htmltext = "30535-00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(id == STARTED)
			{
				if(st.getQuestItemsCount(MINERAL_FRAGMENT) >= 1)
					htmltext = "30535-10.htm";
				else
					htmltext = "30535-07.htm";
			}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && contains(MINE_MOBS, npcId))
			if(Rnd.chance(DROP_CHANCE))
			{
				st.giveItems(MINERAL_FRAGMENT, (int)ConfigValue.RateQuestsDrop);
				st.playSound(SOUND_ITEMGET);
			}
		return null;
	}
}