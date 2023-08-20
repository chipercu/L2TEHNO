package quests._283_TheFewTheProudTheBrave;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.Rnd;

public class _283_TheFewTheProudTheBrave extends Quest implements ScriptFile
{
	//NPCs
	private static int PERWAN = 32133;
	//Mobs
	private static int CRIMSON_SPIDER = 22244;
	//Quest Items
	private static int CRIMSON_SPIDER_CLAW = 9747;
	//Chances
	private static int CRIMSON_SPIDER_CLAW_CHANCE = 34;

	public _283_TheFewTheProudTheBrave()
	{
		super(false);
		addStartNpc(PERWAN);
		addKillId(CRIMSON_SPIDER);
		addQuestItem(CRIMSON_SPIDER_CLAW);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		int _state = st.getState();
		if(event.equalsIgnoreCase("subelder_perwan_q0283_0103.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.set("cond", "1");
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("subelder_perwan_q0283_0203.htm") && _state == STARTED)
		{
			long count = st.getQuestItemsCount(CRIMSON_SPIDER_CLAW);
			if(count > 0)
			{
				st.takeItems(CRIMSON_SPIDER_CLAW, -1);
				st.giveItems(ADENA_ID, 45 * count);

				if(st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q4"))
				{
					st.getPlayer().setVar("p1q4", "1");
					st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				}

				st.playSound(SOUND_MIDDLE);
			}
		}
		else if(event.equalsIgnoreCase("subelder_perwan_q0283_0204.htm") && _state == STARTED)
		{
			st.takeItems(CRIMSON_SPIDER_CLAW, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if(npc.getNpcId() != PERWAN)
			return htmltext;
		int _state = st.getState();

		if(_state == CREATED)
		{
			if(st.getPlayer().getLevel() >= 15)
			{
				htmltext = "subelder_perwan_q0283_0101.htm";
				st.set("cond", "0");
			}
			else
			{
				htmltext = "subelder_perwan_q0283_0102.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if(_state == STARTED)
			htmltext = st.getQuestItemsCount(CRIMSON_SPIDER_CLAW) > 0 ? "subelder_perwan_q0283_0105.htm" : "subelder_perwan_q0283_0106.htm";

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		if(qs.getState() != STARTED)
			return null;

		if(Rnd.chance(CRIMSON_SPIDER_CLAW_CHANCE))
		{
			qs.giveItems(CRIMSON_SPIDER_CLAW, 1);
			qs.playSound(SOUND_ITEMGET);
		}
		return null;
	}

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}
}