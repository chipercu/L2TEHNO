package events.SavingSnowman;

import java.util.concurrent.ScheduledFuture;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Drop;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.CharMoveToLocation;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.RadarControl;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.tables.TerritoryTable;
import l2open.gameserver.taskmanager.DecayTaskManager;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SavingSnowman extends Functions implements ScriptFile
{

	private static GArray<L2Spawn> _spawns = new GArray<L2Spawn>();

	private static ScheduledFuture<?> _snowmanShoutTask;
	private static ScheduledFuture<?> _saveTask;
	private static ScheduledFuture<?> _sayTask;
	private static ScheduledFuture<?> _eatTask;

	public static SnowmanState _snowmanState;

	private static L2NpcInstance _snowman;
	private static L2Character _thomas;

	public static enum SnowmanState
	{
		CAPTURED,
		KILLED,
		SAVED;
	}

	private static final int INITIAL_SAVE_DELAY = 10 * 60 * 1000; // 10 мин
	private static final int SAVE_INTERVAL = 60 * 60 * 1000; // 60 мин
	private static final int SNOWMAN_SHOUT_INTERVAL = 1 * 60 * 1000; // 1 мин
	private static final int THOMAS_EAT_DELAY = 10 * 60 * 1000; // 10 мин
	private static final int SATNA_SAY_INTERVAL = 5 * 60 * 1000; // 5 мин
	private static final int EVENT_MANAGER_ID = 13184;
	private static final int CTREE_ID = 13006;
	private static final int EVENT_REWARDER_ID = 13186;
	private static final int SNOWMAN_ID = 13160;
	private static final int THOMAS_ID = 13183;

	// Оружие для обмена купонов
	private static final int WEAPONS[][] = {
			{ 20109, 20110, 20111, 20112, 20113, 20114, 20115, 20116, 20117, 20118, 20119, 20120, 20121, 20122 }, // D
			{ 20123, 20124, 20125, 20126, 20127, 20128, 20129, 20130, 20131, 20132, 20133, 20134, 20135, 20136 }, // C
			{ 20137, 20138, 20139, 20140, 20141, 20142, 20143, 20144, 20145, 20146, 20147, 20148, 20149, 20150 }, // B
			{ 20151, 20152, 20153, 20154, 20155, 20156, 20157, 20158, 20159, 20160, 20161, 20162, 20163, 20164 }, // A
			{ 20165, 20166, 20167, 20168, 20169, 20170, 20171, 20172, 20173, 20174, 20175, 20176, 20177, 20178 } // S
	};

	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: SavingSnowman [state: activated]");
			_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			_sayTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			_log.info("Loaded Event: SavingSnowman [state: deactivated]");
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("SavingSnowman");
	}

	/**
	* Запускает эвент
	*/
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("SavingSnowman", true))
		{
			spawnEventManagers();
			_log.info("Event 'SavingSnowman' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStarted", null);
			if(_saveTask == null)
				_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
			if(_sayTask == null)
				_sayTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SavingSnowman' already started.");

		_active = true;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	* Останавливает эвент
	*/
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("SavingSnowman", false))
		{
			unSpawnEventManagers();
			if(_snowman != null)
				_snowman.deleteMe();
			if(_thomas != null)
				_thomas.deleteMe();
			_log.info("Event 'SavingSnowman' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStoped", null);
			if(_saveTask != null)
			{
				_saveTask.cancel(true);
				_saveTask = null;
			}
			if(_sayTask != null)
			{
				_sayTask.cancel(true);
				_sayTask = null;
			}
			if(_eatTask != null)
			{
				_eatTask.cancel(true);
				_eatTask = null;
			}
			_snowmanState = SnowmanState.SAVED;
		}
		else
			player.sendMessage("Event 'SavingSnowman' not started.");

		_active = false;

		show(Files.read("data/html/admin/events.htm", player), player);
	}

	/**
	 * Спавнит эвент менеджеров и рядом ёлки
	 */
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 81921, 148921, -3467, 16384 }, { 146405, 28360, -2269, 49648 },
				{ 19319, 144919, -3103, 31135 }, { -82805, 149890, -3129, 16384 }, { -12347, 122549, -3104, 16384 },
				{ 110642, 220165, -3655, 61898 }, { 116619, 75463, -2721, 20881 }, { 85513, 16014, -3668, 23681 },
				{ 81999, 53793, -1496, 61621 }, { 148159, -55484, -2734, 44315 }, { 44185, -48502, -797, 27479 },
				{ 86899, -143229, -1293, 8192 } };

		final int CTREES[][] = { { 81961, 148921, -3467, 0 }, { 146445, 28360, -2269, 0 }, { 19319, 144959, -3103, 0 },
				{ -82845, 149890, -3129, 0 }, { -12387, 122549, -3104, 0 }, { 110602, 220165, -3655, 0 },
				{ 116659, 75463, -2721, 0 }, { 85553, 16014, -3668, 0 }, { 81999, 53743, -1496, 0 },
				{ 148199, -55484, -2734, 0 }, { 44185, -48542, -797, 0 }, { 86859, -143229, -1293, 0 } };

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
		SpawnNPCs(CTREE_ID, CTREES, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	public void onReload()
	{
		unSpawnEventManagers();
		if(_saveTask != null)
			_saveTask.cancel(true);
		if(_sayTask != null)
			_sayTask.cancel(true);
		_snowmanState = SnowmanState.SAVED;
	}

	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/**
	 * Обработчик смерти мобов
	 */
	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(_active && killer != null)
		{
			L2Player pKiller = killer.getPlayer();
			if(pKiller != null && SimpleCheckDrop(cha, killer) && (pKiller.isGM() || Rnd.get(1000) < ConfigValue.SavingSnowmanRewarderChance))
			{
				List<L2Player> players = new ArrayList<L2Player>();
				if(pKiller.isInParty())
					players = pKiller.getParty().getPartyMembers();
				else
					players.add(pKiller);

				spawnRewarder(players.get(Rnd.get(players.size())));
			}
		}
	}

	public static void spawnRewarder(L2Player rewarded)
	{
		// Два санты рядом не должно быть
		for(L2NpcInstance npc : rewarded.getAroundNpc(1500, 300))
			if(npc.getNpcId() == EVENT_REWARDER_ID)
				return;

		// Санта появляется в зоне прямой видимости
		Location spawnLoc = Location.getAroundPosition(rewarded, rewarded, 300, 400, 10);
		for(int i = 0; i < 20 && !GeoEngine.canSeeCoord(rewarded.getX(), rewarded.getY(), rewarded.getZ() + 32, spawnLoc.x, spawnLoc.y, spawnLoc.z, false, rewarded.getReflection().getGeoIndex()); i++)
			spawnLoc = Location.getAroundPosition(rewarded, rewarded, 300, 400, 10);

		// Спауним
		L2NpcTemplate template = NpcTable.getTemplate(EVENT_REWARDER_ID);
		if(template == null)
		{
			_log.info("WARNING! events.SavingSnowman.spawnRewarder template is null for npc: " + EVENT_REWARDER_ID);
			Thread.dumpStack();
			return;
		}

		L2NpcInstance rewarder = new L2NpcInstance(IdFactory.getInstance().getNextId(), template);
		rewarder.setXYZInvisible(spawnLoc);
		rewarder.setHeading((int) (Math.atan2(spawnLoc.y - rewarded.getY(), spawnLoc.x - rewarded.getX()) * L2Character.HEADINGS_IN_PI) + 32768); // Лицом к игроку
		rewarder.spawnMe();

		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase1");

		Location targetLoc = Location.getAroundPosition(rewarded, rewarded, 40, 50, 10);
		rewarder.setSpawnedLoc(targetLoc);
		rewarder.broadcastPacket(new CharMoveToLocation(rewarder.getObjectId(), rewarder.getLoc(), targetLoc));

		executeTask("events.SavingSnowman.SavingSnowman", "reward", new Object[] { rewarder, rewarded }, 5000);
	}

	public static void reward(L2NpcInstance rewarder, L2Player rewarded)
	{
		if(!_active || rewarder == null || rewarded == null)
			return;
		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase2", rewarded.getName());
		Functions.addItem(rewarded, 14616, 1); // Gift from Santa Claus
		executeTask("events.SavingSnowman.SavingSnowman", "removeRewarder", new Object[] { rewarder }, 5000);
	}

	public static void removeRewarder(L2NpcInstance rewarder)
	{
		if(!_active || rewarder == null)
			return;

		Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase3");

		Location loc = rewarder.getSpawnedLoc();

		double radian = Util.convertHeadingToRadian(rewarder.getHeading());
		int x = loc.x - (int) (Math.sin(radian) * 300);
		int y = loc.y + (int) (Math.cos(radian) * 300);
		int z = loc.z;

		rewarder.broadcastPacket(new CharMoveToLocation(rewarder.getObjectId(), loc, new Location(x, y, z)));

		executeTask("events.SavingSnowman.SavingSnowman", "unspawnRewarder", new Object[] { rewarder }, 2000);
	}

	public static void unspawnRewarder(L2NpcInstance rewarder)
	{
		if(!_active || rewarder == null)
			return;
		rewarder.deleteMe();
	}

	public void buff()
	{
		L2Player player = (L2Player) getSelf();
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(_snowmanState != SnowmanState.SAVED)
		{
			show(Files.read("data/html/default/13184-3.htm", player), player);
			return;
		}

		player.broadcastSkill(new MagicSkillUse(player, player, 23017, 1, 0, 0), true);
		player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(23017, 1));

		L2Summon pet = player.getPet();
		if(pet != null)
		{
			pet.broadcastSkill(new MagicSkillUse(pet, pet, 23017, 1, 0, 0), true);
			pet.altOnMagicUseTimer(pet, SkillTable.getInstance().getInfo(23017, 1));
		}
	}

	public void locateSnowman()
	{
		L2Player player = (L2Player) getSelf();
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(_snowman != null)
		{
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControl(2, 2, _snowman.getLoc()), new RadarControl(0, 1, _snowman.getLoc()));
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(_snowman.getLoc()).addString("Ищите Снеговика в "));
		}
		else
			player.sendPacket(Msg.YOUR_TARGET_CANNOT_BE_FOUND);
	}

	public void coupon(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(getItemCount(player, 20107) < 1)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		int num = Integer.parseInt(var[0]);
		if(num < 0 || num > 13)
			return;

		int expertise = Math.min(player.expertiseIndex, 5);
		expertise = Math.max(expertise, 1);
		expertise--;

		removeItem(player, 20107, 1);

		int item_id = WEAPONS[expertise][num];
		int enchant = Rnd.get(4, 16);
		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		item.setEnchantLevel(enchant);
		player.getInventory().addItem(item);
		player.sendPacket(SystemMessage.obtainItems(item_id, 1, enchant));
	}

	public void lotery()
	{
		L2Player player = (L2Player) getSelf();
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		if(!player.isQuestContinuationPossible(true))
			return;

		if(getItemCount(player, 57) < ConfigValue.SavingSnowmanLoteryPrice)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		removeItem(player, 57, ConfigValue.SavingSnowmanLoteryPrice);

		double chance = Rnd.get(L2Drop.MAX_CHANCE);

		// Special Christmas Tree            30%
		if(chance < 300000)
			addItem(player, 5561, 1);
		// Christmas Red Sock                18%
		else if(chance < 480000)
			addItem(player, 14612, 1);
		// Santa Claus' Weapon Exchange Ticket - 12 Hour Expiration Period      15%
		else if(chance < 630000)
			addItem(player, 20107, 1);
		// Gift from Santa Claus             5%
		else if(chance < 680000)
			addItem(player, 14616, 1);
		// Rudolph's Nose                    5%
		else if(chance < 730000 && getItemCount(player, 14611) == 0)
			addItem(player, 14611, 1);
		// Santa's Hat                       5%
		else if(chance < 780000 && getItemCount(player, 7836) == 0)
			addItem(player, 7836, 1);
		// Santa's Antlers                   5%
		else if(chance < 830000 && getItemCount(player, 8936) == 0)
			addItem(player, 8936, 1);
		// Agathion Seal Bracelet - Rudolph (постоянный предмет)                5%
		else if(chance < 880000 && getItemCount(player, 10606) == 0)
			addItem(player, 10606, 1);
		// Agathion Seal Bracelet: Rudolph - 30 дней со скилом на виталити      5%
		else if(chance < 930000 && getItemCount(player, 20094) == 0)
			addItem(player, 20094, 1);
		// Chest of Experience (Event)       3%
		else if(chance < 960000)
			addItem(player, 20575, 1);
		// Призрачные аксессуары             2.5%
		else if(chance < 985000)
			addItem(player, Rnd.get(9177, 9204), 1);
		// BOSE или BRES                     1.2%
		else if(chance < 997000)
			addItem(player, Rnd.get(9156, 9157), 1);
		// 14 lvl cry                        0.2%
		else if(chance < 999000)
		{
			player.altUseSkill(SkillTable.getInstance().getInfo(21006, 1), player);
			addItem(player, Rnd.get(9570, 9572), 1);
		}
		// 15 lvl cry                        0.1%
		else if(chance <= 1000000)
		{
			player.altUseSkill(SkillTable.getInstance().getInfo(21006, 1), player);
			addItem(player, Rnd.get(10480, 10482), 1);
		}
	}

	public String DialogAppend_13184(Integer val)
	{
		if(val != 0)
			return "";

		return " (" + Util.formatAdena(ConfigValue.SavingSnowmanLoteryPrice) + " adena)";
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceEventStarted", null);
	}

	private static Location getRandomSpawnPoint()
	{
		List<Integer> locIds = new ArrayList<Integer>();
		for(int locId : TerritoryTable.getInstance().getLocations().keySet())
			locIds.add(locId);
		L2Territory terr = TerritoryTable.getInstance().getLocation(locIds.get(Rnd.get(locIds.size())));
		return new Location(terr.getRandomPoint());
	}

	// Индюк захватывает снеговика
	public void captureSnowman()
	{
		Location spawnPoint = getRandomSpawnPoint();

		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanCaptured", null, Say2C.CRITICAL_ANNOUNCEMENT);
			player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(spawnPoint).addString("Ищите Снеговика в "));
			// Убираем и ставим флажок на карте и стрелку на компасе
			player.sendPacket(new RadarControl(2, 2, spawnPoint), new RadarControl(0, 1, spawnPoint));
		}

		// Спауним снеговика
		L2NpcTemplate template = NpcTable.getTemplate(SNOWMAN_ID);
		if(template == null)
		{
			_log.info("WARNING! events.SavingSnowman.captureSnowman template is null for npc: " + SNOWMAN_ID);
			Thread.dumpStack();
			return;
		}

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLoc(spawnPoint);
			sp.setAmount(1);
			sp.setRespawnDelay(0);
			_snowman = sp.doSpawn(true);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		if(_snowman == null)
			return;

		// Спауним Томаса
		template = NpcTable.getTemplate(THOMAS_ID);
		if(template == null)
		{
			_log.info("WARNING! events.SavingSnowman.captureSnowman template is null for npc: " + THOMAS_ID);
			Thread.dumpStack();
			return;
		}

		Location pos = GeoEngine.findPointToStay(_snowman.getX(), _snowman.getY(), _snowman.getZ(), 100, 120, _snowman.getReflection().getGeoIndex());

		try
		{
			L2Spawn sp = new L2Spawn(template);
			sp.setLoc(pos);
			sp.setAmount(1);
			sp.setRespawnDelay(0);
			_thomas = sp.doSpawn(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(_thomas == null)
			return;

		_snowmanState = SnowmanState.CAPTURED;

		// Если по каким-то причинам таск существует, останавливаем его
		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(true);
			_snowmanShoutTask = null;
		}
		_snowmanShoutTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShoutTask(), 1, SNOWMAN_SHOUT_INTERVAL);

		if(_eatTask != null)
		{
			_eatTask.cancel(true);
			_eatTask = null;
		}
		_eatTask = executeTask("events.SavingSnowman.SavingSnowman", "eatSnowman", new Object[0], THOMAS_EAT_DELAY);
	}

	// Индюк захавывает снеговика
	public static void eatSnowman()
	{
		if(_snowman == null || _thomas == null)
			return;

		for(L2Player player : L2ObjectsStorage.getPlayers())
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanKilled", null, Say2C.CRITICAL_ANNOUNCEMENT);

		_snowmanState = SnowmanState.KILLED;

		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(true);
			_snowmanShoutTask = null;
		}

		_snowman.deleteMe();
		_thomas.deleteMe();
	}

	// Индюк умер, освобождаем снеговика
	public static void freeSnowman(L2Character topDamager)
	{
		if(_snowman == null || topDamager == null || !topDamager.isPlayable())
			return;

		for(L2Player player : L2ObjectsStorage.getPlayers())
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanSaved", null, Say2C.CRITICAL_ANNOUNCEMENT);

		_snowmanState = SnowmanState.SAVED;

		if(_snowmanShoutTask != null)
		{
			_snowmanShoutTask.cancel(true);
			_snowmanShoutTask = null;
		}
		if(_eatTask != null)
		{
			_eatTask.cancel(true);
			_eatTask = null;
		}

		L2Player player = topDamager.getPlayer();
		Functions.npcSayCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanSayTnx", player.getName());
		addItem(player, 20034, 3); // Revita-Pop
		addItem(player, 20338, 1); // Rune of Experience Points 50%	10 Hour Expiration Period
		addItem(player, 20344, 1); // Rune of SP 50% 10 Hour Expiration Period

		DecayTaskManager.getInstance().addDecayTask(_snowman);
	}

	public class SayTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!_active)
				return;

			for(L2Spawn s : _spawns)
				if(s.getNpcId() == EVENT_MANAGER_ID)
					Functions.npcSayCustomMessage(s.getLastSpawn(), "scripts.events.SavingSnowman.SantaSay");
		}
	}

	public class ShoutTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!_active || _snowman == null || _snowmanState != SnowmanState.CAPTURED)
				return;

			Functions.npcShoutCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanShout");
		}
	}

	public class SaveTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			if(!_active || _snowmanState == SnowmanState.CAPTURED)
				return;

			captureSnowman();
		}
	}
}