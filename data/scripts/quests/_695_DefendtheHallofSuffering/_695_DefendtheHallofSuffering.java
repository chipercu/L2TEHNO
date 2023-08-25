package quests._695_DefendtheHallofSuffering;

import l2open.extensions.listeners.CurrentHpChangeListener;
import l2open.extensions.scripts.ScriptFile;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.soi.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Util;

/**
 * <h2>Описание</h2> Поговорите с <b>Офицером Тефиосом</b>, который находится внутри Зале Ожидания <b>Семени Бессмертия</b>.
 * <ol>
 * <li>Защитите Зал Страданий
 * <ol>
 * <li>Лидер группы через Рот Экимуса (слева от Тефиоса) заходит в <b>Зал Страданий</b>.</li>
 * <li>В центре комнаты уничтожаете Колонию Гнилой Плоти и появляющихся монстров. Когда Колония восстанавливается - снова убиваете ее.</li>
 * <li>Из червяков на площадке рядом с колонной можно выбивать настойки с маной, но чем больше убито червяков - тем больше появляется монстров.</li>
 * <li>После появления уничтожьте Близнецов Рыцарей Тьмы - <b>Йохана Клодекуса</b> и <b>Йохана Кланикуса</b>, не забывая уничтожать появляющихся монстров и Колонию.</li>
 * <li>После победы над близнецами лидер группы говорит с <b>Офицером Тефиосом</b> и все участники рейда получают награду. Сундук открывается двойным щелчком по нему в инвентаре.</li>
 * </ol>
 */
public class _695_DefendtheHallofSuffering extends SeedOfInfinity implements ScriptFile
{
	private class UpdateTumor extends l2open.common.RunnableImpl
	{
		private HallofSufferingWorld world;

		private UpdateTumor(HallofSufferingWorld w)
		{
			world = w;
		}

		@Override
		public void runImpl()
		{
			world.destroyedTumor.deleteMe();
			world.tumor = addSpawnToInstance(TUMOR_OF_DEATH, spawn_location, 0, world.instanceId);
			world.tumor.addPropertyChangeListener(new CurrentHpListener());
			// FIXME ThreadPoolManager.getInstance().schedule(new BuffMobs(world.tumor, 1000, 5000), 5000);
			ThreadPoolManager.getInstance().schedule(new RunNewWaveDefence(world), waves_time[world.status]);
		}
	}

	private class RunNewWaveDefence extends l2open.common.RunnableImpl
	{
		private HallofSufferingWorld world;

		private RunNewWaveDefence(HallofSufferingWorld w)
		{
			world = w;
		}

		@Override
		public void runImpl()
		{
			if(world.status < 5)
				doSpawn(); // спаунит монстров
			else
				spawnBrothers(world); // после 5 волн идёт спаун РБ
		}

		private void doSpawn()
		{
			// ппц, спауним монстров)
			int[][] spawns = waves_spawn[world.status];
			for(int[] monster : spawns)
			{
				int count = monster[1];
				while (count-- > 0)
					world.add(addSpawnToInstance(monster[0], spawn_location.rnd(300, 600, false), 0, world.instanceId), false);
			}

			// TODO чем больше убито червяков - тем больше появляется монстров
			// TODO так же червяки влияют на ХП/Регенирацию опухолей
			for(int i = 0; i < 5; i++)
				addSpawnToInstance(SOUL_COFFIN, spawn_location.rnd(400, 750, false), 0, world.instanceId);
		}
	}

	private class RespawnTumor extends l2open.common.RunnableImpl
	{
		private HallofSufferingWorld world;

		private RespawnTumor(HallofSufferingWorld w)
		{
			world = w;
		}

		@Override
		public void runImpl()
		{
			world.tumor = addSpawnToInstance(TUMOR_OF_DEATH, spawn_location, 0, world.instanceId);
			world.tumor.addPropertyChangeListener(new CurrentHpListener());
			// FIXME ThreadPoolManager.getInstance().schedule(new BuffMobs(world.tumor, 1000, 5000), 5000);
		}
	}

	// TODO проверить) похоже не будет работать
	public class CurrentHpListener extends CurrentHpChangeListener
	{
		@Override
		public void onCurrentHpChange(L2Character actor, double oldHp, double newHp)
		{
			if(actor == null || actor.isDead())
				return;
			L2Party party = getParty(actor.getReflectionId());

			int hp = (int) actor.getCurrentHpPercents();
			if(hp == 85)
				showMessageToParty(party, "You can feel the surging energy of death from the tumor.");
			else if(hp == 95)
				showMessageToParty(party, "The area near the tumor is full of ominous energy");
		}
	}

	// локации для спауна рб
	private static final Location clodecus = new Location(-173727, 218169, -9582, -16384);
	private static final Location clanicus = new Location(-173727, 218049, -9584, 16360);

	// локации для спауна монстров
	private static final Location spawn_location = new Location(-173704, 218092, -9562);

	private static final int SOUL_COFFIN = 18706;

	public _695_DefendtheHallofSuffering()
	{
		super(695, PARTY_NONE);
		addTalkId(MOUTH_OF_EKIMUS);
		addTalkId(TEPIOS_REWARD);
		addKillId(TUMOR_OF_DEATH, YEHAN_KLODEKUS, YEHAN_KlANIKUS);
		addKillId(MOBS);
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		L2Player player = qs.getPlayer();
		if(event.equals("32603-05.htm"))
		{
			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(cycle == 4 || cycle == 5)
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
		else if(event.equals("ai_is_time_attack_reward_q0695_12.htm"))
		{
			L2Party party = player.getParty();
			HallofSufferingWorld world = getHallofSufferingWorld(npc.getReflectionId());
			if(world != null && party != null)
			{
				if(party.isLeader(player) && world.status >= 5)
				{
					QuestState uqs;
					for(L2Player p : player.getParty().getPartyMembers())
						if((uqs = getPlayerQuestState(p, getName())) != null)
						{
							uqs.giveItems(SOE, 1);
							uqs.giveItems(SUPPLIES[world.rewardType], 1);
							uqs.exitCurrentQuest(true);
						}

					htmltext = "ai_is_time_attack_reward_q0695_13.htm";
					endInstance(player);
				}
				else
					htmltext = "ai_is_time_attack_reward_q0695_12.htm";
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
				htmltext = "32603-06.htm";
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
			if(cond == 1 && (cycle == 4 || cycle == 5))
			{
				if(checkCondition(player, ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR2))
				{
					Reflection r = enterPartyInstance(player, ReflectionTable.SOI_HALL_OF_SUFFERING_SECTOR2, new HallofSufferingWorld());
					if(r != null)
					{
						L2Party party = player.getParty();
						if(party != null)
						{
							startInstance(r.getId());
							for(L2Player pl : player.getParty().getPartyMembers())
								pl.setVar("SeedOfInfinityQuest", getName());
						}
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
				// 0 мин - 20 мин
				if(world.timer < 20 * 60)
					world.rewardType = 1;
				// 20 мин - 23 мин
				else if(world.timer >= 20 * 60 && world.timer <= 21 * 60)
					world.rewardType = 2;
				// 21 мин - 24 мин
				else if(world.timer > 21 * 60 && world.timer <= 22 * 60)
					world.rewardType = 3;
				// 22 мин - 25 мин
				else if(world.timer > 22 * 60 && world.timer <= 23 * 60)
					world.rewardType = 4;
				// 23 мин - 26 мин
				else if(world.timer > 23 * 60 && world.timer <= 24 * 60)
					world.rewardType = 5;
				// 24 мин - 60 мин
				else if(world.timer > 24 * 60 && world.timer <= 25 * 60)
					world.rewardType = 6;
				// 25 мин - 60 мин
				else if(world.timer > 25 * 60 && world.timer <= 26 * 60)
					world.rewardType = 7;
				// 26 мин - 60 мин
				else if(world.timer > 26 * 60 && world.timer <= 27 * 60)
					world.rewardType = 8;
				// 27 мин - 60 мин
				else if(world.timer > 27 * 60 && world.timer <= 38 * 60)
					world.rewardType = 9;
				// 28 мин - 60 мин
				else if(world.timer > 28 * 60)
					world.rewardType = 10;

				htmltext = "ai_is_time_attack_reward_q0695_0" + world.rewardType + ".htm";
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
		L2Party party = player.getParty();
		int npcId = npc.getNpcId();
		if(party == null)
		{
			System.out.println(getName() + ": player: " + player.getName() + " Account: " + player.getAccountName() + " Have no party in party-instance, mb cheater?");
			return super.onKill(npc, qs);
		}

		if(npcId == TUMOR_OF_DEATH)
			ThreadPoolManager.getInstance().schedule(new RespawnTumor(world), 1 * 60 * 1000);
		else if(Util.isInArray(npcId, MOBS))
		{
			if(checkKillProgress(npc, world))
			{
				world.status++;
				ThreadPoolManager.getInstance().schedule(new RunNewWaveDefence(world), waves_time[world.status]);
			}
		}
		else if((npcId == YEHAN_KLODEKUS || npcId == YEHAN_KlANIKUS) && checkKillProgress(npc, world) && qs.getCond() == 1)
		{
			world.timer = System.currentTimeMillis() / 1000 - world.timer;

			addSpawnToInstance(TEPIOS_REWARD, spawn_location, 0, player.getReflectionId());
			QuestState st;
			for(L2Player pl : player.getParty().getPartyMembers())
				if((st = getPlayerQuestState(pl, getName())) != null)
					st.setCond(2);
		}

		return super.onKill(npc, qs);
	}

	private void startInstance(int reflectionId)
	{
		HallofSufferingWorld world = getHallofSufferingWorld(reflectionId);
		if(world == null)
			return;
		world.destroyedTumor = addSpawnToInstance(DESTROYED_TUMOR, spawn_location, 0, reflectionId);
		ThreadPoolManager.getInstance().schedule(new UpdateTumor(world), 60000);
	}

	private void spawnBrothers(HallofSufferingWorld world)
	{
		world.tumor.deleteMe();
		world.createNpcList();
		world.add(addSpawnToInstance(YEHAN_KLODEKUS, clodecus, 0, world.instanceId), false);
		world.add(addSpawnToInstance(YEHAN_KlANIKUS, clanicus, 0, world.instanceId), false);
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
