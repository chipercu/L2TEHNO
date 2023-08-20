package quests._692_HowtoOpposeEvil;

import java.io.File;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

public class _692_HowtoOpposeEvil extends Quest implements ScriptFile
{
	private final static int DILIOS = 32549;
	private final static int KUTRAN = 32550;

	private final static int[] DESTRUCTION_MOBS = new int[] {
			22536,
			22537,
			22538,
			22539,
			22540,
			22541,
			22542,
			22543,
			22544,
			22546,
			22547,
			22548,
			22549,
			22550,
			22551,
			22552,
			22593,
			22596,
			22597 };

	private final static int[] IMMORTALITY_MOBS = new int[] { 22509, 22510, 22511, 22512, 22513, 22514, 22515, 22516, 22517, 22518, 22519, 22520, 22521, 22522, 22524, 22526, 22528, 22530, 22532, 22534 };
	private final static int[] ANNIHILATION_MOBS = new int[] { 22750, 22751, 22752, 22753, 22757, 22758, 22759, 22763, 22764, 22765 };
	private final static int[] SEEDS = new int[] { 18678, 18679, 18680, 18681, 18682, 18683 };

	private final static int LEKONS_CERTIFICATE = 13857;
	private final static int NUCLEUS_OF_A_FREED_SOUL = 13796;
	private final static int NUCLEUS_OF_A_INCOMPLETE_SOUL = 13863;
	private final static int FLEET_STEED_TROUPS_CHARM = 13841;
	private final static int FLEET_STEED_TROUPS_TOTEM = 13865;
	private final static int BREATH_OF_TIAT = 13867;
	private final static int SPIRIT_STONE_DUST = 15536;

	public CheckStatus LAST_CHECK_STATUS = CheckStatus.GRACIA_EPILOGUE;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _692_HowtoOpposeEvil()
	{
		super(true);
		addStartNpc(DILIOS);
		addTalkId(KUTRAN);
		addKillId(DESTRUCTION_MOBS);
		addKillId(IMMORTALITY_MOBS);
		addKillId(ANNIHILATION_MOBS);
		addKillId(SEEDS);
		addQuestItem(NUCLEUS_OF_A_FREED_SOUL);
		addQuestItem(NUCLEUS_OF_A_INCOMPLETE_SOUL);
		addQuestItem(FLEET_STEED_TROUPS_CHARM);
		addQuestItem(FLEET_STEED_TROUPS_TOTEM);
		addQuestItem(BREATH_OF_TIAT);
		addQuestItem(SPIRIT_STONE_DUST);
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32549-03.htm"))
		{
			qs.setCond(1);
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32550-04.htm"))
		{
			qs.setCond(3);
			qs.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(st.getState() == CREATED)
			if(st.getPlayer().getLevel() >= 75)
				htmltext = "32549-01.htm";
			else
				return htmltext;//htmltext = "32549-00.htm"; TODO - сделать диалог
		else if(npcId == DILIOS)
		{
			if(cond == 1 && st.getQuestItemsCount(LEKONS_CERTIFICATE) >= 1)
			{
				htmltext = "32549-04.htm";
				st.setCond(2);
			}
			else if(cond == 2)
				htmltext = "32549-05.htm";
		}
		else if(npcId == KUTRAN)
		{
			if(cond == 2)
				htmltext = "32550-01.htm";
			else if(cond == 3)
				htmltext = "32550-04.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(contains(DESTRUCTION_MOBS, npc.getNpcId()))
		{
			if(Rnd.chance(50))
			{
				st.giveItems(FLEET_STEED_TROUPS_TOTEM, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
		}
		else if(contains(IMMORTALITY_MOBS, npc.getNpcId()))
		{
			st.giveItems(NUCLEUS_OF_A_INCOMPLETE_SOUL, 1, true);
			st.playSound(SOUND_ITEMGET);
		}
		else if(contains(ANNIHILATION_MOBS, npc.getNpcId()))
		{
			st.giveItems(SPIRIT_STONE_DUST, 1, true);
			st.playSound(SOUND_ITEMGET);
		}
		else if(contains(SEEDS, npc.getNpcId()))
		{
			st.giveItems(BREATH_OF_TIAT, 1, true);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}