package quests._696_ConquertheHallofErosion;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.SeedOfInfinityManager;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.entity.soi.*;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.tables.ReflectionTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * <h2>Описание задания</h2>
 * <ol>
 * <li>Все участники рейда говорят с Офицером Тефиосом в Зале Ожидания Семени Бессмертия для взятия квеста.</li>
 * <li>Лидер командного канала должен зайти через Рот Экимуса (справа от Тефиоса) в Зал Гибели.</li>
 * <li>Бегая по круговому коридору, забегайте в комнаты, и уничтожайте там 4 Колонны Гнилой Плоти (в первую очередь), Знаки Кохеменеса и монстров (по желанию). После уничтожения всех колонн появится <b>Кохеменес</b>.</li>
 * <li>1 группа уничтожает <b>Кохеменеса</b> в Зале Гибели, остальные продолжают бегать по комнатам и уничтожать возрождающиеся Колонны Гнилой Плоти. Если все колонны возродяться - Кохеменес исчезнет и квест не будет выполнен.</li>
 * <li>Лидер командного канала говорит с Тефиосом и все получают награду.</li>
 * </ol>
 */
public class _696_ConquertheHallofErosion extends SeedOfInfinity implements ScriptFile
{
	// TODO респаун мобов
	// TODO спаун ловушек
	// TODO Cohemenes телепортируется между комнатами, где есть живые символы.

	public _696_ConquertheHallofErosion()
	{
		super(696, PARTY_NONE);
		addTalkId(MOUTH_OF_EKIMUS, TEPIOS_REWARD);
		addKillId(TUMOR_OF_DEATH, SYMBOL_OF_COHEMENES, COHEMENES);
	}

	@Override
	public String onEvent(String event, QuestState qs, L2NpcInstance npc)
	{
		String htmltext = event;
		int cycle = SeedOfInfinityManager.getCurrentCycle();
		if(event.equals("32603-05.htm"))
			if(cycle == 1)
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
			else if(st.getQuestItemsCount(13691) == 0)
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
			if(cond == 1 && cycle == 1)
			{
				if(checkCondition(player, ReflectionTable.SOI_HALL_OF_EROSION_ATTACK))
				{
					Reflection r = enterCommandChannelInstance(player, ReflectionTable.SOI_HALL_OF_EROSION_ATTACK, new HallofErosionWorld());
					if(r != null)
					{
						L2CommandChannel commandChannel = getCommandChannel(player);
						// отправляем меседж)
						showMessageToCommandChannel(commandChannel, PREPARE_HALL_OF_EROSION_ATTACK);
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
			L2CommandChannel commandChannel = getCommandChannel(player);
			HallofErosionWorld world = getHallofErosionWorld(npc.getReflectionId());
			if(world != null)
				if(commandChannel != null && commandChannel.getChannelLeader().getObjectId() == player.getObjectId())
				{
					for(L2Player p : commandChannel.getMembers())
					{
						QuestState pqs = getPlayerQuestState(p, getName());
						if(pqs != null)
						{
							 // Mark stage 2
							if(pqs.getQuestItemsCount(MARK_OF_KEUCEREUS_STAGE_2) == 0)
								pqs.giveItems(MARK_OF_KEUCEREUS_STAGE_2, 1);
							
							pqs.giveItems(SOE, 1);
							pqs.exitCurrentQuest(true);
						}
					}

					htmltext = "32603-07.htm";
					endInstance(player);
				}
		}

		return htmltext;
	}
	
	@Override
	public void initialInstance(L2Player player)
	{
		HallofErosionWorld world = getHallofErosionWorld(player.getReflectionId());
		L2CommandChannel commandChannel = getCommandChannel(player);
		
		// при 0 спауним РБ, при 4 фейл, все труморы реснулись
		world.status = 4; // количество живых
		world.mark_cohemenes = new int[] { 1, 1, 1, 1 };

		world.timer = 25; // 25 мин даётся на убийство всех труморов
		// стартуем таск на проверку лимита по времени
		world.remainingTimeTask = ThreadPoolManager.getInstance().schedule(new TimeRemaining(world, commandChannel, "The Hall of Erosion attack will fail unless you make a quick attack against the tumor! \nHall of Erosion Attack %time% minute(s) are remaining."), time_limit);

		for(L2Player pl : commandChannel.getMembers())
			pl.setVar("SeedOfInfinityQuest", getName());
		
		spawnMobsAroundTumors(world); // спаун монстров и труморов
		spawnSymbols(world);

		// отправляем меседж)
		showMessageToCommandChannel(commandChannel, START_HALL_OF_EROSION_ATTACK);
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState qs)
	{
		if(!checkQuest(qs))
			return super.onKill(npc, qs);

		HallofErosionWorld world = getHallofErosionWorld(npc.getReflectionId());
		if(world == null)
			return null;

		L2Player player = qs.getPlayer();
		int npcId = npc.getNpcId();
		L2CommandChannel commandChannel = getCommandChannel(player);
		if(commandChannel == null)
			return null;

		if(npcId == TUMOR_OF_DEATH)
		{
			world.status--;

			// если убили 4 Tumor'а - спауним РБ
			if(world.status == 0 && !world.raidboss_spawned)
			{
				// отменяем таск на проверку лимита по времени
				if(world.remainingTimeTask != null)
				{
					world.remainingTimeTask.cancel(false);
					world.remainingTimeTask = null;
				}

				//спаун РБ
				world.raidboss = addSpawnToInstance(COHEMENES, mark_cohemenes_loactions[Rnd.get(mark_cohemenes_loactions.length)], 51, world.instanceId);
				Functions.npcShout(world.raidboss, COHEMENES_START);

				world.raidboss_spawned = true;
				// все Tumor'ы убиты
				showMessageToCommandChannel(commandChannel, DESTROY_ALL_TRUMOR);
			}
			else if(!world.raidboss_spawned)
				showMessageToCommandChannel(commandChannel, DESTROY_MORE_TRUMOR);

			// если РБ ещё не убит, то спауним Труморы
			if(!world.raidboss_dead)
				ThreadPoolManager.getInstance().schedule(new RespawnTumor(world, npc.getLoc(), commandChannel), 300 * 1000);
		}
		else if(npcId == SYMBOL_OF_COHEMENES)
		{
			// ищём какой символ убили
			int n = findNumber(npc.getSpawnedLoc());
			world.mark_cohemenes[n] = 0;
			ThreadPoolManager.getInstance().schedule(new RespawnSymbol(world, n), 180 * 1000);
		}
		else if(npcId == COHEMENES)
		{
			// предсмертные крики)
			Functions.npcShout(npc, COHEMENES_DIE);

			world.raidboss_dead = true;
			
			SeedOfInfinityManager.addAttackErrosion();
			addSpawnToInstance(TEPIOS_REWARD, world.raidboss.getLoc(), 0, world.instanceId);
			if(commandChannel != null)
			{
				QuestState uqs;
				for(L2Player p : commandChannel.getMembers())
					if((uqs = getPlayerQuestState(p, getName())) != null)
						uqs.setCond(2);
			}

			showMessageToCommandChannel(commandChannel, SUCCEEDED_HALL_OF_EROSION_ATTACK);
		}
		return super.onKill(npc, qs);
	}

	private int findNumber(Location loc)
	{
		for(int i = 0; i < mark_cohemenes_loactions.length; i++)
			if(loc.equals(mark_cohemenes_loactions[i]))
				return i;
		return -1;
	}

	private void spawnMobsAroundTumors(HallofErosionWorld world)
	{
		for(Location loc : tumor_death_locations)
			spawnMobsAroundTumor(loc, world);
	}

	private void spawnMobsAroundTumor(Location location, HallofErosionWorld world)
	{
		//FIXME L2NpcInstance tumor = 
		addSpawnToInstance(TUMOR_OF_DEATH, location, 0, world.instanceId);
		//FIXME ThreadPoolManager.getInstance().schedule(new BuffMobs(tumor, 1000, 5 * 1000), 5 * 1000);

		for(int i = 0; i < max_npc_count; i++)
			addSpawnToInstance(monsters[Rnd.get(monsters.length)], location, 500, world.instanceId);
	}

	private void spawnSymbols(HallofErosionWorld world)
	{
		for(int i = 0; i < mark_cohemenes_loactions.length; i++)
			addSpawnToInstance(SYMBOL_OF_COHEMENES, mark_cohemenes_loactions[i], 0, world.instanceId);
	}

	private class RespawnTumor extends l2open.common.RunnableImpl
	{
		private HallofErosionWorld world;
		private Location location;
		private L2CommandChannel commandChannel;

		private RespawnTumor(HallofErosionWorld w, Location loc, L2CommandChannel channel)
		{
			world = w;
			location = loc;
			commandChannel = channel;
		}

		@Override
		public void runImpl()
		{
			world.status++;

			// если отресались 4 Tumor'а заново после спауна рб
			if(world.status == 4 && world.raidboss_spawned && !world.raidboss_dead)
			{
				Functions.npcShout(world.raidboss, "Kyahaha! Since the tumor has been resurrected, I no longer need to waste my time on you!"); //1800235
				showMessageToCommandChannel(commandChannel, REVIVED_ALL_TRUMOR);
				
				// удаляем РБ
				world.raidboss.deleteMe();
				world.raidboss = null;
				
				// отменяем таск
				if(world.remainingTimeTask != null)
				{
					world.remainingTimeTask.cancel(false);
					world.remainingTimeTask = null;
				}
			}
			else
				spawnMobsAroundTumor(location, world);
		}
	}

	private class RespawnSymbol extends l2open.common.RunnableImpl
	{
		private HallofErosionWorld world;
		private int number;

		private RespawnSymbol(HallofErosionWorld w, int num)
		{
			world = w;
			number = num;
		}

		@Override
		public void runImpl()
		{
			world.mark_cohemenes[number] = 1;
			addSpawnToInstance(SYMBOL_OF_COHEMENES, mark_cohemenes_loactions[number], 0, world.instanceId);
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
