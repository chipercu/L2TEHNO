package quests._213_TrialOfSeeker;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * Квест на вторую профессию Trial Of Seeker
 * @author Sergey Ibryaev aka Artful
 */

public class _213_TrialOfSeeker extends Quest implements ScriptFile
{
	//NPC
	private static final int Dufner = 30106;
	private static final int Terry = 30064;
	private static final int Viktor = 30684;
	private static final int Marina = 30715;
	private static final int Brunon = 30526;
	//Quest Item
	private static final int DufnersLetter = 2647;
	private static final int Terrys1stOrder = 2648;
	private static final int Terrys2ndOrder = 2649;
	private static final int TerrysLetter = 2650;
	private static final int ViktorsLetter = 2651;
	private static final int HawkeyesLetter = 2652;
	private static final int MysteriousRunestone = 2653;
	private static final int OlMahumRunestone = 2654;
	private static final int TurekRunestone = 2655;
	private static final int AntRunestone = 2656;
	private static final int TurakBugbearRunestone = 2657;
	private static final int TerrysBox = 2658;
	private static final int ViktorsRequest = 2659;
	private static final int MedusaScales = 2660;
	private static final int ShilensRunestone = 2661;
	private static final int AnalysisRequest = 2662;
	private static final int MarinasLetter = 2663;
	private static final int ExperimentTools = 2664;
	private static final int AnalysisResult = 2665;
	private static final int Terrys3rdOrder = 2666;
	private static final int ListOfHost = 2667;
	private static final int AbyssRunestone1 = 2668;
	private static final int AbyssRunestone2 = 2669;
	private static final int AbyssRunestone3 = 2670;
	private static final int AbyssRunestone4 = 2671;
	private static final int TerrysReport = 2672;
	private static final int MarkofSeeker = 2673;
	//MOBs
	private static final int NeerGhoulBerserker = 20198;
	private static final int OlMahumCaptain = 20211;
	private static final int TurekOrcWarlord = 20495;
	private static final int AntCaptain = 20080;
	private static final int TurakBugbearWarrior = 20249;
	private static final int Medusa = 20158;
	private static final int MarshStakatoDrone = 20234;
	private static final int BrekaOrcOverlord = 20270;
	private static final int AntWarriorCaptain = 20088;
	private static final int LetoLizardmanWarrior = 20580;
	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]	
	private static final int[][] DROPLIST_COND = {
			{ 2, 3, NeerGhoulBerserker, Terrys1stOrder, MysteriousRunestone, 1, 10, 1 },
			{ 4, 0, OlMahumCaptain, Terrys2ndOrder, OlMahumRunestone, 1, 20, 1 },
			{ 4, 0, TurekOrcWarlord, Terrys2ndOrder, TurekRunestone, 1, 20, 1 },
			{ 4, 0, AntCaptain, Terrys2ndOrder, AntRunestone, 1, 20, 1 },
			{ 4, 0, TurakBugbearWarrior, Terrys2ndOrder, TurakBugbearRunestone, 1, 20, 1 },
			{ 9, 10, Medusa, ViktorsRequest, MedusaScales, 10, 30, 1 },
			{ 16, 0, MarshStakatoDrone, ListOfHost, AbyssRunestone1, 1, 25, 1 },
			{ 16, 0, BrekaOrcOverlord, ListOfHost, AbyssRunestone2, 1, 25, 1 },
			{ 16, 0, AntWarriorCaptain, ListOfHost, AbyssRunestone3, 1, 25, 1 },
			{ 16, 0, LetoLizardmanWarrior, ListOfHost, AbyssRunestone4, 1, 25, 1 } };

	private static boolean QuestProf = true;

	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public _213_TrialOfSeeker()
	{
		super(false);

		addStartNpc(Dufner);

		addTalkId(Dufner);
		addTalkId(Terry);
		addTalkId(Viktor);
		addTalkId(Marina);
		addTalkId(Brunon);
		//Mob Drop
		for(int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][2]);

		addQuestItem(new int[] { DufnersLetter, Terrys1stOrder, Terrys2ndOrder, TerrysLetter, TerrysBox, ViktorsLetter,
				ViktorsRequest, HawkeyesLetter, ShilensRunestone, AnalysisRequest, MarinasLetter, ExperimentTools,
				AnalysisResult, ListOfHost, Terrys3rdOrder, TerrysReport, MysteriousRunestone, OlMahumRunestone,
				TurekRunestone, AntRunestone, TurakBugbearRunestone, MedusaScales, AbyssRunestone1, AbyssRunestone2,
				AbyssRunestone3, AbyssRunestone4 });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dufner_q0213_05a.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(DufnersLetter, 1, false);
			if(!st.getPlayer().getVarB("dd1"))
			{
				st.giveItems(7562, 64, false);
				st.getPlayer().setVar("dd1", "1");
			}
		}
		else if(event.equalsIgnoreCase("terry_q0213_03.htm"))
		{
			st.giveItems(Terrys1stOrder, 1, false);
			st.takeItems(DufnersLetter, -1);
			st.set("cond", "2");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("terry_q0213_07.htm"))
		{
			st.takeItems(Terrys1stOrder, -1);
			st.takeItems(MysteriousRunestone, -1);
			st.giveItems(Terrys2ndOrder, 1, false);
			st.set("cond", "4");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("terry_q0213_10.htm"))
		{
			st.takeItems(Terrys2ndOrder, -1);
			st.takeItems(OlMahumRunestone, -1);
			st.takeItems(TurekRunestone, -1);
			st.takeItems(AntRunestone, -1);
			st.takeItems(TurakBugbearRunestone, -1);
			st.giveItems(TerrysLetter, 1, false);
			st.giveItems(TerrysBox, 1, false);
			st.set("cond", "6");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_05.htm"))
		{
			st.takeItems(TerrysLetter, -1);
			st.giveItems(ViktorsLetter, 1, false);
			st.set("cond", "7");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_11.htm"))
		{
			st.takeItems(TerrysLetter, -1);
			st.takeItems(TerrysBox, -1);
			st.takeItems(HawkeyesLetter, -1);
			st.giveItems(ViktorsRequest, 1, false);
			st.set("cond", "9");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_15.htm"))
		{
			st.takeItems(ViktorsRequest, -1);
			st.takeItems(MedusaScales, -1);
			st.giveItems(ShilensRunestone, 1, false);
			st.giveItems(AnalysisRequest, 1, false);
			st.set("cond", "11");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("magister_marina_q0213_02.htm"))
		{
			st.takeItems(ShilensRunestone, -1);
			st.takeItems(AnalysisRequest, -1);
			st.giveItems(MarinasLetter, 1, false);
			st.set("cond", "12");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("magister_marina_q0213_05.htm"))
		{
			st.takeItems(ExperimentTools, 1);
			st.giveItems(AnalysisResult, 1, false);
			st.set("cond", "14");
			st.setState(STARTED);
		}
		else if(event.equalsIgnoreCase("terry_q0213_18.htm"))
			if(st.getPlayer().getLevel() < 36)
			{
				htmltext = "terry_q0213_17.htm";
				st.takeItems(AnalysisResult, -1);
				st.giveItems(Terrys3rdOrder, 1, false);
			}
			else
			{
				htmltext = "terry_q0213_18.htm";
				st.giveItems(ListOfHost, 1, false);
				st.takeItems(AnalysisResult, -1);
				st.set("cond", "16");
				st.setState(STARTED);
			}
		return htmltext;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getInt("cond");
		if(npcId == Dufner)
		{
			if(st.getQuestItemsCount(MarkofSeeker) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else if(cond == 0 && st.getQuestItemsCount(TerrysReport) == 0)
			{
				if(st.getPlayer().getClassId().ordinal() == 0x07 || st.getPlayer().getClassId().ordinal() == 0x16 || st.getPlayer().getClassId().ordinal() == 0x23)
					if(st.getPlayer().getLevel() < 35)
					{
						htmltext = "dufner_q0213_02.htm";
						st.exitCurrentQuest(true);
					}
					else
						htmltext = "dufner_q0213_03.htm";
				else
				{
					htmltext = "dufner_q0213_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if(st.getQuestItemsCount(DufnersLetter) == 1 && st.getQuestItemsCount(TerrysReport) == 0)
				htmltext = "dufner_q0213_06.htm";
			else if(st.getQuestItemsCount(DufnersLetter) == 0 && st.getQuestItemsCount(TerrysReport) == 0)
				htmltext = "dufner_q0213_07.htm";
			else if(st.getQuestItemsCount(TerrysReport) != 0)
			{
				if(!st.getPlayer().getVarB("prof2.1"))
				{
					st.addExpAndSp(514739, 33384, true);
					st.giveItems(ADENA_ID, 93803, ConfigValue.RateQuestsRewardOccupationChange);
					st.getPlayer().setVar("prof2.1", "1");
				}
				htmltext = "dufner_q0213_08.htm";
				st.playSound(SOUND_FINISH);
				st.takeItems(TerrysReport, -1);
				st.giveItems(MarkofSeeker, 1, false);
				st.exitCurrentQuest(true);
			}
		}
		else if(npcId == Terry)
		{
			if(cond == 1)
				htmltext = "terry_q0213_01.htm";
			else if(cond == 2)
				htmltext = "terry_q0213_04.htm";
			else if(cond == 3 && st.getQuestItemsCount(MysteriousRunestone) == 0)
			{
				htmltext = "terry_q0213_04.htm";
				st.set("cond", "2");
			}
			else if(cond == 3)
				htmltext = "terry_q0213_05.htm";
			else if(cond == 4)
				htmltext = "terry_q0213_07.htm";
			else if(cond == 5 && st.getQuestItemsCount(OlMahumRunestone) != 0 && st.getQuestItemsCount(TurekRunestone) != 0 && st.getQuestItemsCount(AntRunestone) != 0 && st.getQuestItemsCount(TurakBugbearRunestone) != 0)
				htmltext = "terry_q0213_09.htm";
			else if(cond == 5)
			{
				htmltext = "terry_q0213_07.htm";
				st.set("cond", "4");
			}
			else if(cond == 6)
				htmltext = "terry_q0213_11.htm";
			else if(cond == 7)
			{
				st.takeItems(ViktorsLetter, -1);
				st.giveItems(HawkeyesLetter, 1, false);
				htmltext = "terry_q0213_12.htm";
				st.set("cond", "8");
				st.setState(STARTED);
			}
			else if(cond == 8)
				htmltext = "terry_q0213_13.htm";
			else if(cond > 8 && cond < 14)
				htmltext = "terry_q0213_14.htm";
			else if(cond == 14 && st.getQuestItemsCount(AnalysisResult) > 0)
				htmltext = "terry_q0213_15.htm";
			else if((cond == 14 || cond == 15) && st.getQuestItemsCount(Terrys3rdOrder) > 0)
			{
				if(st.getPlayer().getLevel() < 36)
					htmltext = "terry_q0213_20.htm";
				else
				{
					htmltext = "terry_q0213_21.htm";
					st.takeItems(Terrys3rdOrder, -1);
					st.giveItems(ListOfHost, 1, false);
					st.set("cond", "16");
					st.setState(STARTED);
				}
			}
			else if(cond == 15 || cond == 16) //15 конда пока нету, и хз как он получается
				htmltext = "terry_q0213_22.htm";
			else if(cond == 17)
				if(st.getQuestItemsCount(AbyssRunestone1) != 0 && st.getQuestItemsCount(AbyssRunestone2) != 0 && st.getQuestItemsCount(AbyssRunestone3) != 0 && st.getQuestItemsCount(AbyssRunestone4) != 0)
				{
					htmltext = "terry_q0213_23.htm";
					st.takeItems(ListOfHost, -1);
					st.takeItems(AbyssRunestone1, -1);
					st.takeItems(AbyssRunestone2, -1);
					st.takeItems(AbyssRunestone3, -1);
					st.takeItems(AbyssRunestone4, -1);
					st.giveItems(TerrysReport, 1, false);
					st.set("cond", "0"); //Тут непонятки, в клиенте нету когда 18, а последний 17 говорит что нада идти к Терри, но после него есть еще один пункт.  Нужна проверка с офф сервера.
				}
				else
				{
					htmltext = "terry_q0213_22.htm";
					st.set("cond", "16");
				}
		}
		else if(npcId == Viktor)
		{
			if(cond == 6)
				htmltext = "trader_viktor_q0213_01.htm";
			else if(cond == 8)
				htmltext = "trader_viktor_q0213_12.htm";
			else if(cond == 9)
				htmltext = "trader_viktor_q0213_13.htm";
			else if(cond == 10 && st.getQuestItemsCount(MedusaScales) >= 10)
				htmltext = "trader_viktor_q0213_14.htm";
			else if(cond == 10)
			{
				st.set("cond", "9");
				htmltext = "trader_viktor_q0213_13.htm";
			}
		}
		else if(npcId == Marina)
		{
			if(cond == 11)
				htmltext = "magister_marina_q0213_01.htm";
			else if(cond == 12)
				htmltext = "magister_marina_q0213_03.htm";
			else if(cond == 13)
				htmltext = "magister_marina_q0213_04.htm";
			else if(cond > 13)
				htmltext = "magister_marina_q0213_06.htm";

		}
		else if(npcId == Brunon)
			if(cond == 12)
			{
				htmltext = "blacksmith_bronp_q0213_01.htm";
				st.takeItems(MarinasLetter, 1);
				st.giveItems(ExperimentTools, 1, false);
				st.set("cond", "13");
				st.setState(STARTED);
			}
			else if(cond == 13)
				htmltext = "blacksmith_bronp_q0213_02.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		for(int i = 0; i < DROPLIST_COND.length; i++)
			if(cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
				if(DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
					if(DROPLIST_COND[i][5] == 0)
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6], QuestProf);
					else if(st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6], QuestProf))
						if(DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.set("cond", String.valueOf(DROPLIST_COND[i][1]));
							st.setState(STARTED);
						}
		if(cond == 4 && st.getQuestItemsCount(OlMahumRunestone) != 0 && st.getQuestItemsCount(TurekRunestone) != 0 && st.getQuestItemsCount(AntRunestone) != 0 && st.getQuestItemsCount(TurakBugbearRunestone) != 0)
		{
			st.set("cond", "5");
			st.setState(STARTED);
		}
		else if(cond == 16 && st.getQuestItemsCount(AbyssRunestone1) != 0 && st.getQuestItemsCount(AbyssRunestone2) != 0 && st.getQuestItemsCount(AbyssRunestone3) != 0 && st.getQuestItemsCount(AbyssRunestone4) != 0)
		{
			st.set("cond", "17");
			st.setState(STARTED);
		}
		return null;
	}
}