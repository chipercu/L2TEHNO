package npc.model;

import l2open.config.ConfigValue;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.templates.L2NpcTemplate;
import motion.MonasteryOfSilenceGame;

/**
 * Заебашил: Diagod
 * open-team.ru
 ********************************************************************************************
 * Не нравится мой код, иди на 8====> "в лес".
 ********************************************************************************************
 * При первом разговоре выдает эту ШТМЛ "minigame_instructor001.htm".
 * После запуска игры включается таймер на 3 минуты +10 секунд...Если время не вышло то выводим диалог "minigame_instructor008.htm" при попытке поговорить...
 * После 1-го проигрыша выдает эту ШТМЛ "minigame_instructor002.htm".
 * После 2-го проигрыга выдает эту ШТМЛ "minigame_instructor003.htm".
 * Если кто-то уже играет то вот эту ШТМЛ "minigame_instructor004.htm".
 * Если у вас нету факела то выдает вот эту ШТМЛ "minigame_instructor005.htm".
 * Если вы сейчас играете то выдает вот эту ШТМЛ "minigame_instructor007.htm".
 **/
public class MiniGameInstructorInstance extends L2NpcInstance
{
	private MonasteryOfSilenceGame msg = null;

	public MiniGameInstructorInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		msg = new MonasteryOfSilenceGame();
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		int roomId = this.getAI().ROOM_ID;

		if(msg.isPlayer(player.getObjectId(), roomId) && msg.playGame(roomId))
		{
			showHtmlFile(player, "minigame_instructor007.htm");
			return;
		}
		else if(msg.isPlayer(player.getObjectId(), roomId) && !msg.matchEnd(roomId))
		{
			showHtmlFile(player, "minigame_instructor008.htm");
			return;
		}
		else if(msg.freeGame(roomId, this)) // Если никто не играет...
		{
			switch(msg.gameCount(player))
			{
				case 1:
					showHtmlFile(player, "minigame_instructor002.htm");
					break;
				case 2:
					showHtmlFile(player, "minigame_instructor003.htm");
					break;
				default:
					showHtmlFile(player, "minigame_instructor001.htm");
					break;
			}
		}
		else
			showHtmlFile(player, "minigame_instructor004.htm");
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		else if(command.equalsIgnoreCase("Start")) // Начать игру...
		{
			long time = player.getVarLong("MonasteryOfSilenceGameReuse", -1L);
			if(ConfigValue.MonasteryOfSilenceGameReuse > 0 && time > System.currentTimeMillis())
			{
				player.sendMessage("Время до повторной игры "+((time-System.currentTimeMillis())/1000/60)+" минут.");
				return;
			}
			// Проверяем наличие факела, если его нету выдаем HTML "minigame_instructor005".
			if(player.getInventory().getCountOf(15540) > 0)
			{
				L2ItemInstance item = player.getInventory().getItemByItemId(15540);
				player.getInventory().destroyItem(item, 1, true);
				if(player.getInventory().getCountOf(15485) == 0) // На всякий случай...
					player.getInventory().addItem(15485, 1);

				NpcSay cs = new NpcSay(this, Say2C.NPC_ALL, 60000);
				broadcastPacket(cs, this);
				msg.startRoom(this, player.getObjectId());
			}
			else
				showHtmlFile(player, "minigame_instructor005.htm");
		}
		else if(command.equalsIgnoreCase("Start2") || command.equalsIgnoreCase("Start3")) // Начать игру...
		{
			NpcSay cs = new NpcSay(this, Say2C.NPC_ALL, 60000);
			broadcastPacket(cs, this);
			msg.startRoom(this, player.getObjectId());
		}
		else if(command.equalsIgnoreCase("teleTo1"))
			player.teleToLocation(118833, -80589, -2688);
		else if(command.equalsIgnoreCase("teleTo2"))
			player.teleToLocation(118833, -80589, -2688);
		else
			super.onBypassFeedback(player, command);
	}

	public static void broadcastPacket(L2GameServerPacket packets, L2NpcInstance npc)
	{
		for(L2Player player : L2World.getAroundPlayers(npc, 600, 50))
			if(player != null)
				player.sendPacket(packets);
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}
}