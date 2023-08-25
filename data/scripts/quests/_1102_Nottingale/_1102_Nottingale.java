package quests._1102_Nottingale;

import quests._10273_GoodDayToFly._10273_GoodDayToFly;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.RadarControl;

public class _1102_Nottingale extends Quest implements ScriptFile
{
	private final static int Nottingale = 32627;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _1102_Nottingale()
	{
		super(false);
		addFirstTalkId(Nottingale);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = st.getPlayer();
		QuestState qs = player.getQuestState(_10273_GoodDayToFly.class);
		if(qs == null || qs.getState() != COMPLETED)
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -184545, 243120, 1581));
			htmltext = "32627.htm";
		}
		else if(event.equalsIgnoreCase("32627-3.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -192361, 254528, 3598));
		}
		else if(event.equalsIgnoreCase("32627-4.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -174600, 219711, 4424));
		}
		else if(event.equalsIgnoreCase("32627-5.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -181989, 208968, 4424));
		}
		else if(event.equalsIgnoreCase("32627-6.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -252898, 235845, 5343));
		}
		else if(event.equalsIgnoreCase("32627-8.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -212819, 209813, 4288));
		}
		else if(event.equalsIgnoreCase("32627-9.htm"))
		{
			player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
			player.sendPacket(new RadarControl(0, 2, -246899, 251918, 4352));
		}
		else if(event.equalsIgnoreCase("32627-1.htm"))
			htmltext = "32627-1.htm";
		else if(event.equalsIgnoreCase("32627-2.htm"))
			htmltext = "32627-2.htm";
		else if(event.equalsIgnoreCase("32627-7.htm"))
			htmltext = "32627-7.htm";
		return htmltext;
	}

	@Override
	public String onFirstTalk(L2NpcInstance npc, L2Player player)
	{
		QuestState qs = player.getQuestState(getClass());
		if(qs == null)
			newQuestState(player, STARTED);
		return "";
	}
}