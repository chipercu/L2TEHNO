package quests._336_CoinOfMagic;

import java.io.File;

import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Rnd;

@SuppressWarnings("unused")
public class _336_CoinOfMagic extends Quest implements ScriptFile
{
	public void onLoad()
	{}

	public void onReload()
	{}

	public void onShutdown()
	{}

	private static final int COIN_DIAGRAM = 3811;
	private static final int KALDIS_COIN = 3812;
	private static final int MEMBERSHIP_1 = 3813;
	private static final int MEMBERSHIP_2 = 3814;
	private static final int MEMBERSHIP_3 = 3815;

	private static final int BLOOD_MEDUSA = 3472;
	private static final int BLOOD_WEREWOLF = 3473;
	private static final int BLOOD_BASILISK = 3474;
	private static final int BLOOD_DREVANUL = 3475;
	private static final int BLOOD_SUCCUBUS = 3476;
	private static final int BLOOD_DRAGON = 3477;
	private static final int BELETHS_BLOOD = 3478;
	private static final int MANAKS_BLOOD_WEREWOLF = 3479;
	private static final int NIAS_BLOOD_MEDUSA = 3480;
	private static final int GOLD_DRAGON = 3481;
	private static final int GOLD_WYVERN = 3482;
	private static final int GOLD_KNIGHT = 3483;
	private static final int GOLD_GIANT = 3484;
	private static final int GOLD_DRAKE = 3485;
	private static final int GOLD_WYRM = 3486;
	private static final int BELETHS_GOLD = 3487;
	private static final int MANAKS_GOLD_GIANT = 3488;
	private static final int NIAS_GOLD_WYVERN = 3489;
	private static final int SILVER_UNICORN = 3490;
	private static final int SILVER_FAIRY = 3491;
	private static final int SILVER_DRYAD = 3492;
	private static final int SILVER_DRAGON = 3493;
	private static final int SILVER_GOLEM = 3494;
	private static final int SILVER_UNDINE = 3495;
	private static final int BELETHS_SILVER = 3496;
	private static final int MANAKS_SILVER_DRYAD = 3497;
	private static final int NIAS_SILVER_FAIRY = 3498;

	private static final int SORINT = 30232;
	private static final int BERNARD = 30702;
	private static final int PAGE = 30696;
	private static final int HAGGER = 30183;
	private static final int STAN = 30200;
	private static final int RALFORD = 30165;
	private static final int FERRIS = 30847;
	private static final int COLLOB = 30092;
	private static final int PANO = 30078;
	private static final int DUNING = 30688;
	private static final int LORAIN = 30673;

	private static final int TimakOrcArcher = 20584;
	private static final int TimakOrcSoldier = 20585;
	private static final int TimakOrcShaman = 20587;
	private static final int Lakin = 20604;
	private static final int TorturedUndead = 20678;
	private static final int HatarHanishee = 20663;
	private static final int Shackle = 20235;
	private static final int TimakOrc = 20583;
	private static final int HeadlessKnight = 20146;
	private static final int RoyalCaveServant = 20240;
	private static final int MalrukSuccubusTuren = 20245;
	private static final int Formor = 20568;
	private static final int FormorElder = 20569;
	private static final int VanorSilenosShaman = 20685;
	private static final int TarlkBugbearHighWarrior = 20572;
	private static final int OelMahum = 20161;
	private static final int OelMahumWarrior = 20575;
	private static final int HaritLizardmanMatriarch = 20645;
	private static final int HaritLizardmanShaman = 20644;

	// not spawned
	private static final int Shackle2 = 20279;
	private static final int HeadlessKnight2 = 20280;
	private static final int MalrukSuccubusTuren2 = 20284;
	private static final int RoyalCaveServant2 = 20276;

	// New
	private static final int GraveLich = 21003;
	private static final int DoomServant = 21006;
	private static final int DoomArcher = 21008;
	private static final int DoomKnight = 20674;

	private static final int Kookaburra2 = 21276;
	private static final int Kookaburra3 = 21275;
	private static final int Kookaburra4 = 21274;
	private static final int Antelope2 = 21278;
	private static final int Antelope3 = 21279;
	private static final int Antelope4 = 21280;
	private static final int Bandersnatch2 = 21282;
	private static final int Bandersnatch3 = 21284;
	private static final int Bandersnatch4 = 21283;
	private static final int Buffalo2 = 21287;
	private static final int Buffalo3 = 21288;
	private static final int Buffalo4 = 21286;

	private static final int ClawsofSplendor = 21521;
	private static final int WisdomofSplendor = 21526;
	private static final int PunishmentofSplendor = 21531;
	private static final int WailingofSplendor = 21539;

	private static final int HungeredCorpse = 20954;
	private static final int BloodyGhost = 20960;
	private static final int NihilInvader = 20957;
	private static final int DarkGuard = 20959;

	private static final int[][] PROMOTE = { {}, {},
			{ SILVER_DRYAD, BLOOD_BASILISK, BLOOD_SUCCUBUS, SILVER_UNDINE, GOLD_GIANT, GOLD_WYRM },
			{ BLOOD_WEREWOLF, GOLD_DRAKE, SILVER_FAIRY, BLOOD_DREVANUL, GOLD_KNIGHT, SILVER_GOLEM } };

	private static final int[][] EXCHANGE_LEVEL = { { PAGE, 3 }, { LORAIN, 3 }, { HAGGER, 3 }, { RALFORD, 2 },
			{ STAN, 2 }, { DUNING, 2 }, { FERRIS, 1 }, { COLLOB, 1 }, { PANO, 1 }, };

	private static final int[][] DROPLIST = 
	{		
		{ PunishmentofSplendor, 160, BLOOD_MEDUSA },
		{ 21658, 160, BLOOD_MEDUSA },
		{ WisdomofSplendor, 160, BLOOD_MEDUSA },
		{ DoomArcher, 160, BLOOD_MEDUSA },
		{ TimakOrcArcher, 140, BLOOD_MEDUSA },
		{ TimakOrcSoldier, 140, BLOOD_MEDUSA },
		{ TimakOrcShaman, 130, BLOOD_MEDUSA },
		{ NihilInvader, 110, BLOOD_MEDUSA },
		{ Bandersnatch2, 90, BLOOD_MEDUSA },
		{ Bandersnatch4, 90, BLOOD_MEDUSA },
		{ Bandersnatch3, 90, BLOOD_MEDUSA },
		{ GraveLich, 90, BLOOD_MEDUSA },
		{ TorturedUndead, 80, BLOOD_MEDUSA },
		{ HatarHanishee, 70, BLOOD_MEDUSA },
		{ Lakin, 60, BLOOD_MEDUSA },
		{ DoomKnight, 210, GOLD_WYVERN },
		{ DoomServant, 140, GOLD_WYVERN },
		{ Kookaburra4, 110, GOLD_WYVERN },
		{ Kookaburra3, 110, GOLD_WYVERN },
		{ Buffalo4, 110, GOLD_WYVERN },
		{ Buffalo2, 110, GOLD_WYVERN },
		{ Buffalo3, 110, GOLD_WYVERN },
		{ Antelope2, 100, GOLD_WYVERN },
		{ Antelope3, 100, GOLD_WYVERN },
		{ Antelope4, 100, GOLD_WYVERN },
		{ Kookaburra2, 100, GOLD_WYVERN },
		{ MalrukSuccubusTuren, 100, GOLD_WYVERN },
		{ RoyalCaveServant, 100, GOLD_WYVERN },
		{ RoyalCaveServant2, 100, GOLD_WYVERN },
		{ MalrukSuccubusTuren2, 100, GOLD_WYVERN },
		{ HeadlessKnight2, 85, GOLD_WYVERN },
		{ HeadlessKnight, 80, GOLD_WYVERN },
		{ TimakOrc, 80, GOLD_WYVERN },
		{ Shackle, 70, GOLD_WYVERN },
		{ Shackle2, 70, GOLD_WYVERN },
		{ WailingofSplendor, 210, SILVER_UNICORN },
		{ 21540, 210, SILVER_UNICORN },
		{ 20576, 200, SILVER_UNICORN },
		{ ClawsofSplendor, 150, SILVER_UNICORN },
		{ 21522, 150, SILVER_UNICORN },
		{ DarkGuard, 150, SILVER_UNICORN },
		{ FormorElder, 120, SILVER_UNICORN },
		{ Formor, 110, SILVER_UNICORN },
		{ HungeredCorpse, 100, SILVER_UNICORN },
		{ OelMahumWarrior, 90, SILVER_UNICORN },
		{ OelMahum, 80, SILVER_UNICORN },
		{ BloodyGhost, 80, SILVER_UNICORN },
		{ TarlkBugbearHighWarrior, 80, SILVER_UNICORN },
		{ VanorSilenosShaman, 70, SILVER_UNICORN },
	};

	public _336_CoinOfMagic()
	{
		super(true);
		addStartNpc(SORINT);

		addTalkId(new int[] { SORINT, BERNARD, PAGE, HAGGER, STAN, RALFORD, FERRIS, COLLOB, PANO, DUNING, LORAIN });

		for(int mob[] : DROPLIST)
			addKillId(mob[0]);

		addKillId(HaritLizardmanMatriarch);
		addKillId(HaritLizardmanShaman);

		addQuestItem(new int[] { COIN_DIAGRAM, KALDIS_COIN, MEMBERSHIP_1, MEMBERSHIP_2, MEMBERSHIP_3 });
	}

	@Override
	public String onEvent(String event, QuestState st, L2NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getInt("cond");
		if(event.equalsIgnoreCase("30702-06.htm"))
		{
			if(cond < 7)
			{
				st.set("cond", "7");
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if(event.equalsIgnoreCase("30232-22.htm"))
		{
			if(cond < 6)
				st.set("cond", "6");
		}
		else if(event.equalsIgnoreCase("30232-23.htm"))
		{
			if(cond < 5)
				st.set("cond", "5");
		}
		else if(event.equalsIgnoreCase("30702-02.htm"))
			st.set("cond", "2");
		else if(event.equalsIgnoreCase("30232-05.htm"))
		{
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(COIN_DIAGRAM, 1);
			st.set("cond", "1");
		}
		else if(event.equalsIgnoreCase("30232-04.htm") || event.equalsIgnoreCase("30232-18a.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_GIVEUP);
		}
		else if(event.equalsIgnoreCase("raise"))
			htmltext = promote(st);
		return htmltext;
	}

	private String promote(QuestState st)
	{
		int grade = st.getInt("grade");
		String html;
		if(grade == 1)
			html = "30232-15.htm";
		else
		{
			int h = 0;
			for(int i : PROMOTE[grade])
				if(st.getQuestItemsCount(i) > 0)
					h += 1;
			if(h == 6)
			{
				for(int i : PROMOTE[grade])
					st.takeItems(i, 1);
				html = "30232-" + str(19 - grade) + ".htm";
				st.takeItems(3812 + grade, -1);
				st.giveItems(3811 + grade, 1);
				st.set("grade", str(grade - 1));
				if(grade == 3)
					st.set("cond", "9");
				else if(grade == 2)
					st.set("cond", "11");
				st.playSound(SOUND_FANFARE_MIDDLE);
			}
			else
			{
				html = "30232-" + str(16 - grade) + ".htm";
				if(grade == 3)
					st.set("cond", "8");
				else if(grade == 2)
					st.set("cond", "9");
			}
		}
		return html;
	}

	@Override
	public String onTalk(L2NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int grade = st.getInt("grade");
		if(npcId == SORINT)
		{
			if(id == CREATED)
			{
				if(st.getPlayer().getLevel() < 40)
				{
					htmltext = "30232-01.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "30232-02.htm";
			}
			else if(st.getQuestItemsCount(COIN_DIAGRAM) > 0)
			{
				if(st.getQuestItemsCount(KALDIS_COIN) > 0)
				{
					st.takeItems(KALDIS_COIN, -1);
					st.takeItems(COIN_DIAGRAM, -1);
					st.giveItems(MEMBERSHIP_3, 1);
					st.set("grade", "3");
					st.set("cond", "4");
					st.playSound(SOUND_FANFARE_MIDDLE);
					htmltext = "30232-07.htm";
				}
				else
					htmltext = "30232-06.htm";
			}
			else if(grade == 3)
				htmltext = "30232-12.htm";
			else if(grade == 2)
				htmltext = "30232-11.htm";
			else if(grade == 1)
				htmltext = "30232-10.htm";
		}
		else if(npcId == BERNARD)
		{
			if(st.getQuestItemsCount(COIN_DIAGRAM) > 0 && grade == 0)
				htmltext = "30702-01.htm";
			else if(grade == 3)
				htmltext = "30702-05.htm";
		}
		else
			for(int e[] : EXCHANGE_LEVEL)
				if(npcId == e[0] && grade <= e[1])
					htmltext = npcId + "-01.htm";
		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		int cond = st.getInt("cond");
		int npcId = npc.getNpcId();
		if(npcId == HaritLizardmanMatriarch || npcId == HaritLizardmanShaman)
		{
			if(cond == 2)
				if(Rnd.get(100) < 63)
				{
					st.giveItems(KALDIS_COIN, 1);
					st.set("cond", "3");
				}
			return null;
		}
		for(int[] e : DROPLIST)
			if(e[0] == npcId)
			{
				if(Rnd.get(1000) < e[1])
					st.giveItems(e[2], 1);
				return null;
			}
		return null;
	}
}