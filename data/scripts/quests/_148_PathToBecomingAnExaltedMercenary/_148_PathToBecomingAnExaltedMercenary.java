package quests._148_PathToBecomingAnExaltedMercenary;

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
public class _148_PathToBecomingAnExaltedMercenary extends Quest implements ScriptFile
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

	public _148_PathToBecomingAnExaltedMercenary()
	{
		super(PARTY_ALL);
		addStartNpc(MERCENARY_CAPTAINS);
		addKillId(CATAPULTAS);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gludio_merc_cap_q0148_06.htm"))
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
					return "gludio_merc_cap_q0148_01.htm";
				else if(player.getCastle() != null && player.getCastle().getId() > 0)
					return "gludio_merc_cap_q0148_02.htm";
			}
			else
				; // TODO: должно выдать какую-то поебень...

			if(player.getLevel() < 40 || player.getClassId().getLevel() <= 2)
				htmlText = "gludio_merc_cap_q0148_03.htm";
			else if(st.getQuestItemsCount(13767) < 1)
				htmlText = "gludio_merc_cap_q0148_03a.htm";
			else
				htmlText = "gludio_merc_cap_q0148_04.htm";
		}
		else if(cond == 1 || cond == 2 || cond == 3)
			htmlText = "gludio_merc_cap_q0148_07.htm";
		else if(cond == 4)
		{
			htmlText = "gludio_merc_cap_q0148_08.htm";
			st.takeAllItems(13767);
			st.giveItems(13768, 1);
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
				int maxCount = 30;
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
			int killedCatapultasCount = st.getInt("catapultas");
			int maxCatapultasCount = 2;
			killedCatapultasCount++;
			if(killedCatapultasCount < maxCatapultasCount)
				st.set("catapultas", killedCatapultasCount);
			else
			{
				if(st.getCond() == 1)
					st.setCond(3);
				else if(st.getCond() == 2)
					st.setCond(4);
				st.unset("catapultas");
			}
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
