package quests._697_DefendtheHallofErosion;

import ai.NpcAttacker;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.soi.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

public class _697_DefendtheHallofErosion extends SeedOfInfinity implements ScriptFile
{
	// в течении 20 минут нужно защитать Seed'ы
	// если хотя бы 1 умрёт то всё.
	// TODO через 15 мин после старта появляются РБ около Seed (или около Опухоли, хз) и нападают на игроков вроде как, если те убивают Опухоли
	// через 5 мин, если ни одного не убили, то победа.

	private static final int[] monster_count = { 10, 12, 14, 16, 18 };

	public _697_DefendtheHallofErosion()
	{
		super(PARTY_NONE, 697);
		addTalkId(MOUTH_OF_EKIMUS, TEPIOS_REWARD);
		addKillId(TUMOR_OF_DEATH, RAVENOUS_SOUL_DEVOURER);
		addKillId(monsters);
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		int cycle = SeedOfInfinityManager.getCurrentCycle();
		if(event.equals("32603-05.htm"))
			if(cycle == 5)
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
			if(cond == 1 && cycle == 5)
			{
				if(checkCondition(player, ReflectionTable.SOI_HALL_OF_EROSION_DEFENCE, ConfigValue.NeedQuestsSoiHallOfErosionDefence ? "_10268_ToTheSeedOfInfinity" : null))
				{
					Reflection r = enterCommandChannelInstance(player, ReflectionTable.SOI_HALL_OF_EROSION_DEFENCE, new HallofErosionWorld());
					if(r != null)
					{
						L2CommandChannel commandChannel = getCommandChannel(player);
						// отправляем меседж)
						showMessageToCommandChannel(commandChannel, PREPARE_HALL_OF_EROSION_DEFEND);
						// запускаем старт инстанса через 30 секунд
						ThreadPoolManager.getInstance().schedule(new InitialDelayTask(player, this), 30 * 1000);
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
			HallofErosionWorld world = getHallofErosionWorld(npc.getReflectionId());
			if(world != null)
			{
				htmltext = "32603-07.htm";
				L2CommandChannel commandChannel = getCommandChannel(player);
				if(commandChannel != null && commandChannel.getChannelLeader().getObjectId() == player.getObjectId())
				{
					QuestState qs;
					for(L2Player p : commandChannel.getMembers())
						if((qs = getPlayerQuestState(p, getName())) != null)
						{
							qs.giveItems(VESPER_STONE, Rnd.get(5, 20));
							qs.exitCurrentQuest(true);
						}
					endInstance(player);
				}
			}
		}

		return htmltext;
	}

	@Override
	public void initialInstance(L2Player player)
	{
		HallofErosionWorld world = getHallofErosionWorld(player.getReflectionId());
		L2CommandChannel commandChannel = getCommandChannel(player);

		world.status = 0; // количество волн атаки на Сиды

		// Все Unstable Seed живы
		world.mark_cohemenes = new int[] { 1, 1, 1, 1 }; // TODO переделать на статусы Опухолей

		// стартуем таск на проверку лимита по времени
		world.timer = 25; // 20 мин даётся
		world.remainingTimeTask = ThreadPoolManager.getInstance().schedule(new TimeRemaining(world, commandChannel, "Hall of Erosion Defend %time% minute(s) are remaining."), 1000);

		spawnMobsAroundTumors(world); // спаун монстров и труморов
		spawnSeeds(world);

		for(L2Player pl : commandChannel.getMembers())
			pl.setVar("SeedOfInfinityQuest", getName());

		// запускаем новую волну через 2 мин
		ThreadPoolManager.getInstance().schedule(new RunNewWaveDefence(world), 2 * 60 * 1000);

		showMessageToCommandChannel(commandChannel, START_HALL_OF_EROSION_DEFEND);
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		if(!checkQuest(qs))
			return super.onKill(npc, qs);

		HallofErosionWorld world = getHallofErosionWorld(npc.getReflectionId());
		if(world == null)
			return null;

		int npcId = npc.getNpcId();
		L2Player player = qs.getPlayer();
		L2CommandChannel channel = getCommandChannel(player);

		if(npcId == TUMOR_OF_DEATH)
		{
			// TODO при разрушении Опухолей монстры атакующие Seed'ы теряют свою силу
			// при смерти Опухоли: The tumor inside %s has been destroyed! \nThe nearby Undead that were attacking Seed of Life start losing their energy and run away!
			// при респауне Опухоли: The tumor inside %s has completely revived. \nRecovered nearby Undead are swarming toward Seed of Life...
			ThreadPoolManager.getInstance().schedule(new RespawnTumor(world, npc.getLoc(), addSpawnToInstance(DESTROYED_TUMOR, npc.getLoc(), 0, world.instanceId)), 300 * 1000);
		}
		else if(Util.isInArray(npcId, monsters))
		{
			if(channel != null && checkKillProgress(npc, world))
			{
				world.status++; // увеличиваем счётчик волн
				ThreadPoolManager.getInstance().schedule(new RunNewWaveDefence(world), 60000); // запускаем новую волну
			}
		}
		return super.onKill(npc, qs);
	}

	private void spawnMobsAroundTumors(HallofErosionWorld world)
	{
		for(Location loc : tumor_death_locations)
			ThreadPoolManager.getInstance().schedule(new RespawnTumor(world, loc, addSpawnToInstance(DESTROYED_TUMOR, loc, 0, world.instanceId)), 60 * 1000);
	}

	private void spawnMobsAroundTumor(Location loc, HallofErosionWorld world)
	{
		// FIXME L2NpcInstance tumor =
		addSpawnToInstance(TUMOR_OF_DEATH, loc, 0, world.instanceId);
		// FIXME ThreadPoolManager.getInstance().schedule(new BuffMobs(tumor, 1000, 5000), 5000);

		for(int i = 0; i < 10; i++)
			addSpawnToInstance(monsters[Rnd.get(monsters.length)], loc, 500, world.instanceId);
	}

	private void spawnSeeds(HallofErosionWorld world)
	{
		for(Location loc : mark_cohemenes_loactions)
			addSpawnToInstance(LIFE_SEED, loc, 0, world.instanceId);
	}

	private class RespawnTumor extends l2open.common.RunnableImpl
	{
		private HallofErosionWorld world;
		private Location location;
		private L2NpcInstance destroyedTumor;

		private RespawnTumor(HallofErosionWorld w, Location loc, L2NpcInstance deadTumor)
		{
			world = w;
			location = loc;
			destroyedTumor = deadTumor;
		}

		@Override
		public void runImpl()
		{
			destroyedTumor.deleteMe();
			spawnMobsAroundTumor(location, world);
		}
	}

	private class RunNewWaveDefence extends l2open.common.RunnableImpl
	{
		private HallofErosionWorld world;

		private RunNewWaveDefence(HallofErosionWorld w)
		{
			world = w;
		}

		@Override
		public void runImpl()
		{
			if(world.status < 5)
				doSpawn(); // спаунит монстров
			else
			// успешно отбили всё волны
			{
				world.raidboss_spawned = true; // чтоб отключить таймер
				// отменяем таск
				if(world.remainingTimeTask != null)
				{
					world.remainingTimeTask.cancel(false);
					world.remainingTimeTask = null;
				}

				L2CommandChannel channel = getCommandChannel(world.instanceId);
				showMessageToCommandChannel(channel, SUCCEEDED_HALL_OF_EROSION_DEFEND);
				QuestState st;
				for(L2Player player : channel.getMembers())
					if((st = getPlayerQuestState(player, getName())) != null)
						st.setCond(2);
			}
		}

		private void doSpawn()
		{
			L2NpcInstance monster;
			NpcAttacker ai;
			for(Location loc : mark_cohemenes_loactions)
			{
				// количество зависит от номера волны
				for(int i = 0; i < monster_count[world.status]; i++)
				{
					monster = addSpawnToInstance(monsters[Rnd.get(monsters.length)], loc.rnd(150, 300, false), 0, world.instanceId);
					ai = new NpcAttacker(monster, LIFE_SEED);
					monster.setAI(ai);
					ai.startAITask();
					world.add(monster, false);
				}
			}
		}
	}

	/**
	 * Обработка смерти Unstable Seed
	 */
	public void OnDie(L2Character self, L2Character killer)
	{
		if(self == null || killer == null)
			return;

		if(self.getNpcId() == LIFE_SEED)
		{
			L2CommandChannel channel = getCommandChannel(killer.getReflectionId());
			HallofErosionWorld world = getHallofErosionWorld(killer.getReflectionId());
			if(world == null || channel == null)
				return;

			// иначе fail, квест провален
			if(!world.raidboss_spawned)
			{
				showMessageToCommandChannel(channel, FAILED_HALL_OF_EROSION_DEFEND);
				QuestState st;
				for(L2Player member : channel.getMembers())
					if((st = getPlayerQuestState(member, getName())) != null)
						st.exitCurrentQuest(true);
			}
		}
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
