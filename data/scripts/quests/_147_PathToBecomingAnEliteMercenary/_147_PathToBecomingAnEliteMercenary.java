package quests._147_PathToBecomingAnEliteMercenary;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;

/**
 * @author pchayka
 */
public class _147_PathToBecomingAnEliteMercenary extends Quest implements ScriptFile
{
	private final int[] MERCENARY_CAPTAINS = {
			36481,
			36482,
			36483,
			36484,
			36485,
			36486,
			36487,
			36488,
			36489
	};

	private final int[] CATAPULTAS = {
			36499,
			36500,
			36501,
			36502,
			36503,
			36504,
			36505,
			36506,
			36507
	};

	public _147_PathToBecomingAnEliteMercenary()
	{
		super(PARTY_ALL);
		addStartNpc(MERCENARY_CAPTAINS);
		addKillId(CATAPULTAS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gludio_merc_cap_q0147_04b.htm"))
			st.giveItems(13766, 1);
		else if(event.equalsIgnoreCase("gludio_merc_cap_q0147_07.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		L2Player player = st.getPlayer();
		Castle castle = TownManager.getInstance().getClosestTown(npc).getCastle();
		String htmlText = "noquest";

		int cond = st.getCond();
		if(cond == 0)
		{
			if(player.getClan() != null)
			{
				if(player.getCastle() != null && player.getCastle().getId() == castle.getId())
					return "gludio_merc_cap_q0147_01.htm";
				else if(player.getCastle() != null && player.getCastle().getId() > 0)
					return "gludio_merc_cap_q0147_02.htm";
			}

			if(player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
				htmlText = "gludio_merc_cap_q0147_03.htm";
			else if(st.getQuestItemsCount(13766) < 1)
				htmlText = "gludio_merc_cap_q0147_04a.htm";
			else
				htmlText = "gludio_merc_cap_q0147_04.htm";
		}
		else if(cond == 1 || cond == 2 || cond == 3)
			htmlText = "gludio_merc_cap_q0147_08.htm";
		else if(cond == 4)
		{
			htmlText = "gludio_merc_cap_q0147_09.htm";
			st.takeAllItems(13766);
			st.giveItems(13767, 1);
			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}

		return htmlText;
	}

	@Override
	public String onPlayerKill(L2Player killed, QuestState st)
	{
		if(st.getCond() == 1 || st.getCond() == 3)
		{
			if(isValidKill(killed, st.getPlayer()))
			{
				int killedCount = st.getInt("enemies");
				int maxCount = 10;
				killedCount++;
				if(killedCount < maxCount)
				{
					st.set("enemies", killedCount);
					st.getPlayer().sendPacket(new ExShowScreenMessage(73661, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false, String.valueOf(maxCount), String.valueOf(killedCount)));
				}
				else
				{
					if(st.getCond() == 1)
						st.setCond(2);
					else if(st.getCond() == 3)
						st.setCond(4);
					st.unset("enemies");
					st.getPlayer().sendPacket(new ExShowScreenMessage(73562, 4000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false));
				}
			}
		}
		return null;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(isValidNpcKill(st.getPlayer(), npc))
		{
			if(st.getCond() == 1)
				st.setCond(3);
			else if(st.getCond() == 2)
				st.setCond(4);
		}
		return null;
	}

	private boolean isValidKill(L2Player killed, L2Player killer)
	{
		if(killed.getTerritorySiege() == -1 || killer.getTerritorySiege() == -1)
			return false;
		if(killed.getTerritorySiege() == killer.getTerritorySiege())
			return false;
		if(killed.getLevel() < 61)
			return false;
		return true;
	}

	private boolean isValidNpcKill(L2Player killer, L2NpcInstance npc)
	{
		if(36499 > npc.getNpcId() || 36507 < npc.getNpcId())
			return false;
		if(killer.getTerritorySiege() == -1)
			return false;
		if(killer.getTerritorySiege() == npc.getNpcId() - 36498)
			return false;
		return true;
	}

	@Override
	public void onPlayerEnter(QuestState st)
	{
		if(st.getState() != COMPLETED)
			st.addNotifyOfPlayerKill();
	}

	@Override
	public void onLoad()
	{

	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
