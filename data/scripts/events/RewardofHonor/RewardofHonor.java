package events.RewardofHonor;

import java.util.logging.Logger;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.Announcements;
import l2open.gameserver.common.DifferentMethods;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2ObjectsStorage;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExShowScreenMessage;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.Files;
import l2open.util.Util;

/**
 * <b>Author:</b> L2CCCP<br>
 * <b>Date:</b> 10.12.2013<br>
 * <b>Time:</b> 07:50:00<br>
 * <b>Description:</b> Event Reward of Honor, order by client Auri.
 */
public class RewardofHonor extends Functions implements ScriptFile
{
	private static final Logger _log = Logger.getLogger(RewardofHonor.class.getName());

	private static boolean _active = false;

	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			spawn();
			_log.info("Loaded Event: Reward of Honor [state: activated]");
		}
		else
			_log.info("Loaded Event: Reward of Honor [state: deactivated]");
	}

	/**
	 * Читает статус ивента из базы.
	 */
	private static boolean isActive()
	{
		return IsActive("RewardofHonorEvent");
	}

	/**
	 * Запускает ивент
	 */
	public void startEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("RewardofHonorEvent", true))
		{
			spawn();
			_log.info("Event 'Reward of Honor' started.");
			Announcements.getInstance().announceByCustomMessage("events.RewardofHonor.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Reward of Honor' already started.");

		_active = true;

		show(Files.read("data/html/admin/events/events.htm", player), player);
	}

	/**
	 * Останавливает ивент
	 */
	public void stopEvent()
	{
		L2Player player = (L2Player) getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive("RewardofHonorEvent", false))
		{
			_log.info("Event 'Reward of Honor' stopped.");
			Announcements.getInstance().announceByCustomMessage("events.RewardofHonor.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Reward of Honor' not started.");

		_active = false;

		show(Files.read("data/html/admin/events/events.htm", player), player);
	}

	/**
	 * Спавнит ивент менеджеров
	 */
	private void spawn()
	{
		L2NpcInstance npc = L2ObjectsStorage.getByNpcId(ConfigValue.ERewardofHonorManager);

		if(npc == null)
		{
			spawn(ConfigValue.ERewardofHonorCords[0], ConfigValue.ERewardofHonorCords[1], ConfigValue.ERewardofHonorCords[2], ConfigValue.ERewardofHonorManager);
			_log.info("SpawnManager: spawned npc for event: Reward of Honor");
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	/**
	 * Обработчик смерти мобов, управляющий ивентовым дропом
	 */
	public static void OnDie(L2Character cha, L2Character killer)
	{
		if(!_active)
			return;

		if(cha == null || killer == null || killer.getPlayer() == null) //Для избежания нпе.
			return;

		if(cha.isPlayer() || cha.isPet() || cha.isSummon() || cha.isDoor()) //Исключаем другие бредовые смерти.
			return;

		if(cha.isRaid() || cha.isBoss() || cha.isEpicRaid()) //Проверка рб.
			return;

		if(!Util.contains_int(ConfigValue.ERewardofHonorMonster, cha.getNpcId())) //Проверка одобренных id монстров.
			return;

		L2Player player = killer.getPlayer();

		if(player.getVarInt("RewardofHonorStatus") == 0)
			return;

		int kills = player.getVarInt("RewardofHonorKills");

		player.setVar("RewardofHonorKills", String.valueOf(kills + 1));
		kills++;

		if(kills >= ConfigValue.ERewardofHonorKills)
		{
			player.setVar("RewardofHonorStatus", "2");
			DifferentMethods.sendMessage(player, new CustomMessage("events.RewardofHonor.lastkill", player).addString(DifferentMethods.getNpcName(ConfigValue.ERewardofHonorManager)));
		}
		else
		{
			if(ConfigValue.ERewardofHonorMessage)
				sendMessage(player, kills);
		}
	}

	/**
	 * Вывод сообщения сколько осталось убить.
	 */
	public static void sendMessage(L2Player player, int kills)
	{
		int count = ConfigValue.ERewardofHonorKills - kills;
		player.sendPacket(new ExShowScreenMessage(new CustomMessage("events.RewardofHonor.needkill", player).addString(Util.formatAdena(count)).addString(DifferentMethods.declension(player, count, "Monster")).toString(), 3000, ScreenMessageAlign.TOP_CENTER, true));
		player.sendMessage(new CustomMessage("events.RewardofHonor.needkill", player).addString(Util.formatAdena(count)).addString(DifferentMethods.declension(player, count, "Monster")));
	}

	public static void OnPlayerEnter(L2Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "events.RewardofHonor.AnnounceEventStarted", null);
	}
}