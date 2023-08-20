package quests._694_BreakThroughTheHallOfSuffering;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.soi.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;

/**
 * <h2>Описание</h2> Поговорите с <b>Офицером Тефиосом</b>, который находится внутри Зала Ожидания <b>Семени Бессмертия</b>
 * <ol>
 * <li>Уничтожьте угрозу в <b>Зале Страданий</b>:
 * <ol>
 * <li>Лидер группы через Рот Экимуса (слева от Тефиоса) заходит в <b>Зал Страданий</b>.</li>
 * <li>Очистите первые 5 комнат от монстров и Колоний Гнилой Плоти.</li>
 * <li>Уничтожьте Рыцарей Смерти — Близнецов (<b>Йохана Клодекуса</b> и <b>Йохана Кланикуса</b>), которые охраняют последнюю комнату.</li>
 * <li>После победы над близнецами лидер группы говорит с <b>Офицером Тефиосом</b> и все участники рейда получают награду. Сундук открывается двойным щелчком по нему в инвентаре.</li>
 * </ol>
 */
public class _694_BreakThroughTheHallOfSuffering extends SeedOfInfinity implements ScriptFile
{
	private static final Location KLODEKUS_LOC = new Location(-173752, 217880, -9582, 31046);
	private static final Location KLANIKUS_LOC = new Location(-173640, 218312, -9584, 35323);

	public _694_BreakThroughTheHallOfSuffering()
	{
		super(694, PARTY_NONE);
		addTalkId(MOUTH_OF_EKIMUS); // MOUTH_OF_EKIMUS = 32537;
		addTalkId(TEPIOS_REWARD); // TEPIOS_REWARD = 32530;
		addKillId(TUMOR_OF_DEATH, YEHAN_KLODEKUS, YEHAN_KlANIKUS); // TUMOR_OF_DEATH = 18704;YEHAN_KLODEKUS = 25665;YEHAN_KlANIKUS = 25666;
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = qs.getPlayer();
		if(event.equals("32603-05.htm"))
		{
			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(cycle == 1 || cycle == 2)
			{
				qs.setState(STARTED);
				qs.setCond(1);
				qs.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "32603-03.htm";
				qs.exitCurrentQuest(true);
			}
		}
		else if(event.equals("ai_is_time_attack_reward_q0694_12.htm"))
		{
			L2Party party = player.getParty();
			HallofSufferingWorld world = getHallofSufferingWorld(npc.getReflectionId());
			if(world != null && party != null)
			{
				if(party.isLeader(player) && world.status == 5)
				{
					QuestState uqs;
					for(L2Player p : player.getParty().getPartyMembers())
						if((uqs = getPlayerQuestState(p, getName())) != null)
						{
							if(uqs.getQuestItemsCount(MARK_OF_KEUCEREUS_STAGE_1) == 0)
								uqs.giveItems(MARK_OF_KEUCEREUS_STAGE_1, 1);

							uqs.giveItems(SOE, 1);
							uqs.giveItems(SUPPLIES[world.rewardType], 1);
							uqs.exitCurrentQuest(true);
						}

					htmltext = "ai_is_time_attack_reward_q0694_13.htm";
					endInstance(player, 60);
				}
				else
					// дам награду только Вашему лидеру
					htmltext = "ai_is_time_attack_reward_q0694_12.htm";
			}
			else
				htmltext = "ai_is_time_attack_reward001.htm";
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
		if(npcId == TEPIOS)
		{
			if(player.getLevel() < 75)
			{
				htmltext = "32603-02.htm";
				st.exitCurrentQuest(true);
			}
			else if(player.getLevel() > 82)
			{
				htmltext = "32603-02a.htm";
				st.exitCurrentQuest(true);
			}
			else if(cond == 0)
				htmltext = "32603-01.htm";
			else if(cond == 1)
				htmltext = "32603-07.htm";
			else if(cond == 2)
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
				htmltext = "32603-05.htm";
			}
		}
		else if(npcId == MOUTH_OF_EKIMUS)
		{
			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(cond == 1 && (cycle == 1 || cycle == 2))
			{
				if(checkCondition(player, ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR1, false))
				{
					Reflection r = enterPartyInstance(player, ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR1, new HallofSufferingWorld());
					if(r != null)
					{
						L2Party party = player.getParty();
						for(L2Player pl : party.getPartyMembers())
							pl.setVar("SeedOfInfinityQuest", getName());
					}
					return null;
				}
				else
					htmltext = "Mouth of Ekimus:<br>Your team does not qualify for entry";
			}
			else
			{
				npc.onBypassFeedback(player, "Chat 2");
				return null;
			}
		}
		else if(npcId == TEPIOS_REWARD && cond == 2)
		{
			HallofSufferingWorld world = getHallofSufferingWorld(npc.getReflectionId());
			if(world != null)
			{
				// 0 мин - 22 мин
				if(world.timer < 22 * 60)
					world.rewardType = 1;
				// 22 мин - 23 мин
				else if(world.timer >= 22 * 60 && world.timer <= 23 * 60)
					world.rewardType = 2;
				// 23 мин - 24 мин
				else if(world.timer > 23 * 60 && world.timer <= 24 * 60)
					world.rewardType = 3;
				// 24 мин - 25 мин
				else if(world.timer > 24 * 60 && world.timer <= 25 * 60)
					world.rewardType = 4;
				// 25 мин - 26 мин
				else if(world.timer > 25 * 60 && world.timer <= 26 * 60)
					world.rewardType = 5;
				// 26 мин - 60 мин
				else if(world.timer > 26 * 60 && world.timer <= 27 * 60)
					world.rewardType = 6;
				// 27 мин - 60 мин
				else if(world.timer > 27 * 60 && world.timer <= 28 * 60)
					world.rewardType = 7;
				// 28 мин - 60 мин
				else if(world.timer > 28 * 60 && world.timer <= 29 * 60)
					world.rewardType = 8;
				// 29 мин - 60 мин
				else if(world.timer > 29 * 60 && world.timer <= 30 * 60)
					world.rewardType = 9;
				// 30 мин - 60 мин
				else if(world.timer > 30 * 60)
					world.rewardType = 10;

				htmltext = "ai_is_time_attack_reward_q0694_0" + world.rewardType + ".htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		if(!checkQuest(qs))
			return super.onKill(npc, qs);

		HallofSufferingWorld world = getHallofSufferingWorld(npc.getReflectionId());
		if(world == null)
			return null;

		L2Player player = qs.getPlayer();
		int npcId = npc.getNpcId();
		if(!player.isInParty())
		{
			System.out.println(getName() + ": player: " + player.getName() + " Account: " + player.getAccountName() + " Have no party in party-instance, mb cheater?");
			return super.onKill(npc, qs);
		}

		if(npcId == TUMOR_OF_DEATH)
		{
			addSpawnToInstance(DESTROYED_TUMOR, npc.getLoc(), 0, world.instanceId);
			world.status++;
			if(world.status == 5)
				spawnBrothers(world);
		}
		else if((npcId == YEHAN_KLODEKUS || npcId == YEHAN_KlANIKUS) && checkKillProgress(npc, world) && (qs.getCond() == 1 || !ConfigValue.NeedQuestsSoiHallOfSuffering))
		{
			addSpawnToInstance(TEPIOS_REWARD, new Location(-173704, 218088, -9528, 0), 0, world.instanceId);

			world.timer = System.currentTimeMillis() / 1000 - world.timer;

			SeedOfInfinityManager.addAttackSuffering();
			QuestState uqs;
			for(L2Player pl : player.getParty().getPartyMembers())
				if((uqs = getPlayerQuestState(pl, getName())) != null)
					uqs.setCond(2);
			if(!ConfigValue.NeedQuestsSoiHallOfSuffering)
			{
				if(player.getParty() == null)
					endInstance(player, 60);
				else
					endInstance(player.getParty(), 60);
			}
		}

		return super.onKill(npc, qs);
	}

	private void spawnBrothers(HallofSufferingWorld world)
	{
		world.createNpcList();
		world.add(addSpawnToInstance(YEHAN_KLODEKUS, KLODEKUS_LOC, 0, world.instanceId), false);
		world.add(addSpawnToInstance(YEHAN_KlANIKUS, KLANIKUS_LOC, 0, world.instanceId), false);
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
