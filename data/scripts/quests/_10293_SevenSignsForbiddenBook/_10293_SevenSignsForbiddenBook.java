package quests._10293_SevenSignsForbiddenBook;

import javolution.util.FastMap;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author pchayka
 */
public class _10293_SevenSignsForbiddenBook extends Quest implements ScriptFile
{
	private static final int Elcardia = 32784;
	private static final int Sophia = 32596;

	private static final int SophiaInzone1 = 32861;
	private static final int ElcardiaInzone1 = 32785;
	private static final int SophiaInzone2 = 32863;

	private static final int SolinasBiography = 17213;

	private static final int[] books = {32809, 32810, 32811, 32812, 32813};

	public _10293_SevenSignsForbiddenBook()
	{
		super(false);
		addStartNpc(Elcardia);
		addTalkId(Sophia, SophiaInzone1, ElcardiaInzone1, SophiaInzone2);
		addTalkId(books);
		addQuestItem(SolinasBiography);
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		L2Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("elcardia_q10293_3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("enter_library"))
		{
			enterInstance(player, 156);
			return null;
		}
		else if(event.equalsIgnoreCase("sophia2_q10293_4.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("sophia2_q10293_8.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("elcardia2_q10293_4.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("sophia2_q10293_10.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("teleport_in"))
		{
			Location loc = new Location(37348, -50383, -1168);
			st.getPlayer().teleToLocation(loc);
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("teleport_out"))
		{
			Location loc = new Location(37205, -49753, -1128);
			st.getPlayer().teleToLocation(loc);
			teleportElcardia(player);
			return null;
		}
		else if(event.equalsIgnoreCase("book_q10293_3a.htm"))
		{
			st.giveItems(SolinasBiography, 1);
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("elcardia_q10293_7.htm"))
		{
			st.addExpAndSp(15000000, 1500000);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		L2Player player = st.getPlayer();
		if(player.isSubClassActive())
			return "no_subclass_allowed.htm";
		switch(npcId)
		{
			case Elcardia:
				if(cond == 0)
				{
					QuestState qs = player.getQuestState("_10292_SevenSignsGirlofDoubt");
					if(player.getLevel() >= 81 && qs != null && qs.isCompleted())
						htmltext = "elcardia_q10293_1.htm";
					else
					{
						htmltext = "elcardia_q10293_0.htm";
						st.exitCurrentQuest(true);
					}
				}
				else if(cond >= 1 && cond < 8)
					htmltext = "elcardia_q10293_4.htm";
				else if(cond == 8)
					htmltext = "elcardia_q10293_5.htm";
				break;
			case Sophia:
				if(cond >= 1 && cond <= 7)
					htmltext = "sophia_q10293_1.htm";
				break;
			case SophiaInzone1:
				if(cond == 1)
					htmltext = "sophia2_q10293_1.htm";
				else if(cond == 2 || cond == 4 || cond == 7 || cond == 8)
					htmltext = "sophia2_q10293_5.htm";
				else if(cond == 3)
					htmltext = "sophia2_q10293_6.htm";
				else if(cond == 5)
					htmltext = "sophia2_q10293_9.htm";
				else if(cond == 6)
					htmltext = "sophia2_q10293_11.htm";
				break;
			case ElcardiaInzone1:
				if(cond == 1 || cond == 3 || cond == 5 || cond == 6)
					htmltext = "elcardia2_q10293_1.htm";
				else if(cond == 2)
				{
					st.setCond(3);
					htmltext = "elcardia2_q10293_2.htm";
				}
				else if(cond == 4)
					htmltext = "elcardia2_q10293_3.htm";
				else if(cond == 7)
				{
					st.setCond(8);
					htmltext = "elcardia2_q10293_5.htm";
				}
				else if(cond == 8)
					htmltext = "elcardia2_q10293_5.htm";

				break;
			case SophiaInzone2:
				if(cond == 6 || cond == 7)
					htmltext = "sophia3_q10293_1.htm";
				else if(cond == 8)
					htmltext = "sophia3_q10293_4.htm";
				break;
			// Books
			case 32809:
				htmltext = "book_q10293_3.htm";
				break;
			case 32811:
				htmltext = "book_q10293_1.htm";
				break;
			case 32812:
				htmltext = "book_q10293_2.htm";
				break;
			case 32810:
				htmltext = "book_q10293_4.htm";
				break;
			case 32813:
				htmltext = "book_q10293_5.htm";
				break;

		}
		return htmltext;
	}

	private void enterInstance(L2Player player, int instancedZoneId)
	{
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		Reflection r = new Reflection(il.getName());
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
		}

		int timelimit = il.getTimelimit();

		player.setReflection(r);
		player.teleToLocation(37205, -49753, -1128);
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.startCollapseTimer(timelimit * 60 * 1000L);
	}

	private void teleportElcardia(L2Player player)
	{
		L2NpcInstance n = player.getReflection().findFirstNPC(ElcardiaInzone1);
		n.teleToLocation(Location.findPointToStay(player, 60));
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