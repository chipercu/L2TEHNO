package npc.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;


public class TullyDoorInstance extends L2NpcInstance
{
	private static long doorRecharge = 0;
	private static final IntObjectMap<IntSet> doors = new HashIntObjectMap<IntSet>();

	static
	{
		IntSet list;

		list = new HashIntSet();
		list.add(19260001);
		list.add(19260002);
		doors.put(18445, list);

		list = new HashIntSet();
		list.add(19260003);
		doors.put(18446, list);

		list = new HashIntSet();
		list.add(19260003);
		list.add(19260004);
		list.add(19260005);
		doors.put(18447, list);

		list = new HashIntSet();
		list.add(19260006);
		list.add(19260007);
		doors.put(18448, list);

		list = new HashIntSet();
		list.add(19260007);
		list.add(19260008);
		doors.put(18449, list);

		list = new HashIntSet();
		list.add(19260010);
		doors.put(18450, list);

		list = new HashIntSet();
		list.add(19260011);
		list.add(19260012);
		doors.put(18451, list);

		list = new HashIntSet();
		list.add(19260009);
		list.add(19260011);
		doors.put(18452, list);

		list = new HashIntSet();
		list.add(19260014);
		list.add(19260023);
		list.add(19260013);
		doors.put(18453, list);

		list = new HashIntSet();
		list.add(19260015);
		list.add(19260023);
		doors.put(18454, list);

		list = new HashIntSet();
		list.add(19260016);
		list.add(19260019); //coded hard
		doors.put(18455, list);

		list = new HashIntSet();
		list.add(19260017);
		list.add(19260018);
		doors.put(18456, list);

		list = new HashIntSet();
		list.add(19260021);
		list.add(19260020);
		doors.put(18457, list);

		list = new HashIntSet();
		list.add(19260022);
		doors.put(18458, list);

		list = new HashIntSet();
		list.add(19260018);
		doors.put(18459, list);

		list = new HashIntSet();
		list.add(19260051);
		doors.put(18460, list);

		list = new HashIntSet();
		list.add(19260052);
		doors.put(18461, list);
	}

	public TullyDoorInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("trydoor"))
		{
			int npcId = getNpcId();
			if(doorRecharge == 0 || doorRecharge <= System.currentTimeMillis())
			{
				if(player.getClassId() == ClassId.maestro)
				{
					openDoor(npcId);
					player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Tully Gatekeeper:<br><br>Doors are opened."));
				}
				else if(Rnd.chance(60)) //unknown
				{
					openDoor(npcId);
					player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Tully Gatekeeper:<br><br>Doors are opened."));
				}
				else
				{
					doorRecharge = System.currentTimeMillis() + 120 * 1000L; // 120 sec retail
					player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Tully Gatekeeper:<br><br>The attempt has failed. Recharching..."));
				}
			}
			else
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Tully Gatekeeper:<br><br>The time needed for the recharge has not elapsed yet"));
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		showHtmlFile(player, "18445.htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}

	private void openDoor(int npcId)
	{
		IntSet set = doors.get(npcId);
		if(set != null)
		{
			for(int i : set.toArray())
			{
				DoorTable.getInstance().getDoor(i).openMe();
				ThreadPoolManager.getInstance().schedule(new DoorClose(DoorTable.getInstance().getDoor(i)), 120 * 1000L);
			}
		}
	}

	private class DoorClose extends l2open.common.RunnableImpl
	{
		L2DoorInstance _door;

		public DoorClose(L2DoorInstance door)
		{
			_door = door;
		}

		@Override
		public void runImpl()
		{
			_door.closeMe();
		}
	}
}