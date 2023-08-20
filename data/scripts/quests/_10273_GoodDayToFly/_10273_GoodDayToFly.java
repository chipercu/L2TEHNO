package quests._10273_GoodDayToFly;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.SkillTable;

public class _10273_GoodDayToFly extends Quest implements ScriptFile
{
	private final static int Lekon = 32557;
	private final static int VultureRider1 = 22614;
	private final static int VultureRider2 = 22615;

	private final static int Mark = 13856;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_FINAL;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _10273_GoodDayToFly()
	{
		super(false);

		addStartNpc(Lekon);

		addQuestItem(Mark);

		addKillId(VultureRider1, VultureRider2);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();

		if(event.equalsIgnoreCase("32557-06.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32557-09.htm"))
		{
			if(player.getTransformation() != 0 || player.isCastingNow())
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			st.set("transform", "1");
			SkillTable.getInstance().getInfo(5982, 1).getEffects(player, player, false, false);
		}
		else if(event.equalsIgnoreCase("32557-10.htm"))
		{
			if(player.getTransformation() != 0 || player.isCastingNow())
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			st.set("transform", "2");
			SkillTable.getInstance().getInfo(5983, 1).getEffects(player, player, false, false);
		}
		else if(event.equalsIgnoreCase("32557-13.htm"))
		{
			if(player.getTransformation() != 0 || player.isCastingNow())
			{
				player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
				return null;
			}
			if(st.getInt("transform") == 1)
				SkillTable.getInstance().getInfo(5982, 1).getEffects(player, player, false, false);
			else if(st.getInt("transform") == 2)
				SkillTable.getInstance().getInfo(5983, 1).getEffects(player, player, false, false);
		}
		return event;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int transform = st.getInt("transform");

		if(id == COMPLETED)
			htmltext = "32557-0a.htm";
		else if(id == CREATED)
			if(st.getPlayer().getLevel() < 75)
				htmltext = "32557-00.htm";
			else
				htmltext = "32557-01.htm";
		else if(st.getQuestItemsCount(Mark) >= 5)
		{
			htmltext = "32557-14.htm";
			if(transform == 1)
				st.giveItems(13553, 1);
			else if(transform == 2)
				st.giveItems(13554, 1);
			st.takeAllItems(Mark);
			st.giveItems(13857, 1);
			st.addExpAndSp(25160, 2525);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		else if(transform < 1)
			htmltext = "32557-07.htm";
		else
			htmltext = "32557-11.htm";

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(st.getState() != STARTED)
			return null;

		int cond = st.getInt("cond");
		long count = st.getQuestItemsCount(Mark);
		if(cond == 1 && count < 5)
		{
			st.giveItems(Mark, 1);
			if(count == 4)
			{
				st.playSound(SOUND_MIDDLE);
				st.set("cond", "2");
			}
			else
				st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}