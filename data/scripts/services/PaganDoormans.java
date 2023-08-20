package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Files;

/**
 * Используется в локации Pagan Temple
 * @Author: SYS
 */
public class PaganDoormans extends Functions implements ScriptFile
{
	private static final int MainDoorId = 19160001;
	private static final int SecondDoor1Id = 19160011;
	private static final int SecondDoor2Id = 19160010;

	public final int VISITOR_MARK = 8064;
	public final int FADED_MARK = 8065;
	public final int PERMANENT_MARK = 8067;

	public void onLoad()
	{
		_log.info("Loaded Service: Pagan Doormans");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void openMainDoor()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		long visitorMarkCount = getItemCount(player, VISITOR_MARK);
		if(visitorMarkCount == 0 && getItemCount(player, FADED_MARK) == 0 && getItemCount(player, PERMANENT_MARK) == 0)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		if(visitorMarkCount > 0)
		{
			removeItem(player, VISITOR_MARK, visitorMarkCount);
			addItem(player, FADED_MARK, 1);
		}

		openDoor(MainDoorId);
		show(Files.read("data/html/default/32034-1.htm", player), player, npc);
	}

	public void openSecondDoor()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		if(getItemCount(player, PERMANENT_MARK) == 0)
		{
			show(Files.read("data/html/default/32036-2.htm", player), player, npc);
			return;
		}

		openDoor(SecondDoor1Id);
		openDoor(SecondDoor2Id);
		show(Files.read("data/html/default/32036-1.htm", player), player, npc);
	}

	public void pressSkull()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		openDoor(MainDoorId);
		show(Files.read("data/html/default/32035-1.htm", player), player, npc);
	}

	public void press2ndSkull()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		openDoor(SecondDoor1Id);
		openDoor(SecondDoor2Id);
		show(Files.read("data/html/default/32037-1.htm", player), player, npc);
	}

	private static void openDoor(int doorId)
	{
		final int CLOSE_TIME = 10000; // 10 секунд
		L2DoorInstance door = DoorTable.getInstance().getDoor(doorId);
		if(!door.isOpen())
		{
			door.openMe();
			door.scheduleCloseMe(CLOSE_TIME);
		}
	}
}