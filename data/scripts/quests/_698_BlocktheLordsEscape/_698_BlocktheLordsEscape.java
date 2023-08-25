package quests._698_BlocktheLordsEscape;

import ai.SoulDevourer;

import l2open.extensions.scripts.ScriptFile;
import l2open.common.ThreadPoolManager;
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

/**
 * Не позволяйте <b>Soul Devourer</b> (81 ур.) захватить центр Heart of Death.<br>
 * В Источнике Бессмертия невозможно воскрешаться обычным способом, для этого необходимо покупать у Торговца Душами Асъятэ (в Зале Ожидания Семени) специальные предметы - <br>
 * Скарабей Помощи за Слезы Освобожденной Души
 */
public class _698_BlocktheLordsEscape extends SeedOfInfinity implements ScriptFile
{
	public _698_BlocktheLordsEscape()
	{
		super(PARTY_NONE, 698);
		addTalkId(GATEKEEPER);
		addKillId(SOUL_DEVOURER, TUMOR_OF_DEATH2);
		addKillId(monsters);
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		int Stage = SeedOfInfinityManager.getCurrentCycle();
		if(event.equals("32603-05.htm"))
			if(Stage == 5)
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
			if(player.getLevel() < 80)
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
				L2CommandChannel commandChannel = getCommandChannel(player);
				if(commandChannel != null)
				{
					htmltext = "32603-07.htm";
					if(commandChannel.getChannelLeader().getObjectId() == player.getObjectId())
					{
						QuestState qs;
						for(L2Player p : commandChannel.getMembers())
							if((qs = getPlayerQuestState(p, getName())) != null)
							{
								qs.giveItems(VESPER_STONE, Rnd.get(5, 20));
								qs.exitCurrentQuest(true);
							}
						endInstance(player); // исправить
					}
				}
			}
		}
		else if(npcId == GATEKEEPER && cond == 1)
		{
			int cycle = SeedOfInfinityManager.getCurrentCycle();
			if(checkCondition(player, ReflectionTable.SOI_HEART_OF_INFINITY_DEFENCE) && cycle == 5)
			{
				Reflection r = enterCommandChannelInstance(player, ReflectionTable.SOI_HEART_OF_INFINITY_DEFENCE, new HeartOfInfinityWorld());
				if(r != null)
				{
					L2CommandChannel commandChannel = getCommandChannel(player);
					// отправляем меседж)
					showMessageToCommandChannel(commandChannel, PREPARE_HEART_OF_INFINITY_DEFEND);
					// запускаем старт инстанса через 30 секунд
					ThreadPoolManager.getInstance().schedule(new InitialDelayTask(player, this), 30 * 1000);
				}
				return null;
			}
			else
				htmltext = "Gatekeeper of Abyss:<br>Your team does not qualify for entry";
		}
		return htmltext;
	}

	@Override
	public void initialInstance(L2Player player)
	{
		HeartOfInfinityWorld world = getHeartOfInfinityWorld(player.getReflectionId());
		L2CommandChannel commandChannel = getCommandChannel(player);

		// вначале надо заспаунить:

		// все Опухоли
		for(Location loc : tumors_locations)
		{
			addSpawnToInstance(TUMOR_OF_DEATH2, loc, 0, world.instanceId);
			// всех мобов
			for(int i = 0; i < 12; i++)
				addSpawnToInstance(monsters[Rnd.get(monsters.length)], loc.rnd(150, 300, false), 0, world.instanceId);
		}

		// октрываем дверь 14240102
		player.getReflection().openDoor(14240102);

		// спауним [npc_echimus]
		world.center_tumor = addSpawnToInstance(CENTER_TUMOR, center_tumor_loction, 0, world.instanceId);

		// спауним одного РБ, первоначально через TIME_BOSS_DELAY_DEFAULT
		world.bossSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnBoss(world), world.boss_respawn_time * 1000);

		// стартуем таск на проверку лимита по времени
		world.timer = 25; // 25 мин даётся
		world.remainingTimeTask = ThreadPoolManager.getInstance().schedule(new TimeLimitCheck(world, commandChannel, "Heart of Infinity Defend %time% minute(s) are remaining."), time_limit);

		for(L2Player pl : commandChannel.getMembers())
			pl.setVar("SeedOfInfinityQuest", getName());

		showMessageToCommandChannel(commandChannel, START_HEART_OF_INFINITY_DEFEND);
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		if(!checkQuest(qs))
			return super.onKill(npc, qs);

		HeartOfInfinityWorld world = getHeartOfInfinityWorld(npc.getReflectionId());
		if(world == null)
			return null;

		int npcId = npc.getNpcId();
		L2Player player = qs.getPlayer();

		if(npcId == TUMOR_OF_DEATH2)
		{
			// увеличиваем время спауна РБ
			if(world.boss_respawn_time + 5 < TIME_BOSS_DELAY_MAX)
				world.boss_respawn_time = world.boss_respawn_time + 5;
			else
				world.boss_respawn_time = TIME_BOSS_DELAY_MAX;

			// запускаем респаун Опухоли
			L2CommandChannel channel = getCommandChannel(player);
			if(channel != null)
				showMessageToCommandChannel(channel, TUMOR_DIE);

			ThreadPoolManager.getInstance().schedule(new RespawnTumor(world, npc.getLoc(), addSpawnToInstance(DESTROYED_TUMOR2, npc.getLoc(), 0, world.instanceId)), TIME_TUMOR_RESPAWN * 1000);
		}
		else if(Util.isInArray(npcId, monsters))
		{
			// просто считаем сколько надо заспаунить потом
			world.status++;
		}

		return super.onKill(npc, qs);
	}

	/**
	 * Обработка смерти Soul Devourer после того как он дошёл до точки
	 */
	public void OnDie(L2Character self, L2Character killer)
	{
		if(self == null || killer == null || killer.isPlayable())
			return;

		if(self.getNpcId() == SOUL_DEVOURER && killer.getNpcId() == CENTER_TUMOR)
		{
			L2CommandChannel channel = getCommandChannel(killer.getReflectionId());
			HeartOfInfinityWorld world = getHeartOfInfinityWorld(killer.getReflectionId());
			if(world == null || channel == null)
				return;

			// иначе fail, квест провален
			if(!world.successfully)
			{
				world.raidbossCount++;

				if(world.raidbossCount >= BOSS_LIMIT_COUNT) // обрабатываем фейл квеста
				{
					showMessageToCommandChannel(channel, FAILED_HEART_OF_INFINITY_DEFEND);
					QuestState st;
					for(L2Player member : channel.getMembers())
						if((st = getPlayerQuestState(member, getName())) != null)
							st.exitCurrentQuest(true);

					if(world.bossSpawnTask != null)
					{
						world.bossSpawnTask.cancel(false);
						world.bossSpawnTask = null;
					}
					// отменяем таск
					if(world.remainingTimeTask != null)
					{
						world.remainingTimeTask.cancel(false);
						world.remainingTimeTask = null;
					}
				}
				else
				// отправляем сообщение с предупреждением
				{
					showMessageToCommandChannel(channel, "The Soul Coffin has awakened Ekimus. If " + (BOSS_LIMIT_COUNT - world.raidbossCount) + " more are created, the defense of the Heart of Infinity will fail...");

					if(world.raidbossCount == 5 && world.warning == 0 || world.raidbossCount == 15 && world.warning == 1)
						if(world.raidbossCount == 5)
						{
							showMessageToCommandChannel(channel, FIRST_FERAL);
							world.warning = 1;
							addSpawnToInstance(FERAL_HOUND, boss_spawn_location, 0, world.instanceId);
						}
						else
						{
							showMessageToCommandChannel(channel, SECOND_FERAL);
							world.warning = 2;
							// FIXME addSpawnToInstance(FERAL_HOUND, boss_spawn_location, 0, world.instanceId); надо их спаунить за ограждением похоже
						}
				}
			}
		}
	}

	private class SpawnBoss extends l2open.common.RunnableImpl
	{
		private HeartOfInfinityWorld world;

		public SpawnBoss(HeartOfInfinityWorld w)
		{
			world = w;
		}

		@Override
		public void runImpl()
		{
			L2NpcInstance boss = addSpawnToInstance(SOUL_DEVOURER, boss_spawn_location, 0, world.instanceId);
			SoulDevourer ai = new SoulDevourer(boss, world.center_tumor, Rnd.get(2));
			boss.setAI(ai);
			ai.startAITask();
			world.bossSpawnTask = ThreadPoolManager.getInstance().schedule(new SpawnBoss(world), world.boss_respawn_time * 1000);
		}
	}

	private class RespawnTumor extends l2open.common.RunnableImpl
	{
		private HeartOfInfinityWorld world;
		private Location location;
		private L2NpcInstance destroyedTumor;

		private RespawnTumor(HeartOfInfinityWorld w, Location loc, L2NpcInstance deadTumor)
		{
			world = w;
			location = loc;
			destroyedTumor = deadTumor;
		}

		@Override
		public void runImpl()
		{
			// уменьшаяем время спауна РБ
			if(world.boss_respawn_time - 5 > TIME_BOSS_DELAY_MIN)
				world.boss_respawn_time = world.boss_respawn_time - 5;
			else
				world.boss_respawn_time = TIME_BOSS_DELAY_MIN;

			destroyedTumor.deleteMe();
			L2CommandChannel channel = getCommandChannel(world.instanceId);
			if(channel != null)
				showMessageToCommandChannel(channel, TUMOR_RESPAWN);

			addSpawnToInstance(TUMOR_OF_DEATH2, location, 0, world.instanceId);

			// спауним монстров
			if(world.status > 0)
			{
				while (world.status-- > 0)
					addSpawnToInstance(monsters[Rnd.get(monsters.length)], tumors_locations[Rnd.get(tumors_locations.length)].rnd(150, 300, false), 0, world.instanceId);

				// на всякий случай)
				world.status = 0;
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
