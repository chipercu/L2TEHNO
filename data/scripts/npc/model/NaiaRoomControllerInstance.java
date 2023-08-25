package npc.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.instancemanager.NaiaTowerManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2Territory;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class NaiaRoomControllerInstance extends L2NpcInstance
{
	private static final L2Territory _room1territory = new L2Territory(587400).addR(-46652, 245576, -9175, -9075).addR(-45735, 246648, -9175, -9075);
	private static final L2Territory _room3territory = new L2Territory(587401).addR(-52088, 245667, -10037, -9837).addR(-51159, 246609, -10037, -9837);
	private static final L2Territory _room5territory = new L2Territory(587402).addR(-46652, 245596, -10032, -9832).addR(-45737, 246626, -10032, -9832);
	private static final L2Territory _room6territory = new L2Territory(587403).addR(-49220, 247903, -10027, -9827).addR(-48647, 248543, -10027, -9827);
	private static final L2Territory _room7territory = new L2Territory(587404).addR(-52068, 245575, -10896, -10696).addR(-51195, 246617, -10896, -10696);
	private static final L2Territory _room8territory = new L2Territory(587405).addR(-49284, 243788, -10892, -10692).addR(-48592, 244408, -10892, -10692);
	private static final L2Territory _room9territory = new L2Territory(587406).addR(-46679, 245661, -11756, -11556).addR(-45771, 246614, -11756, -11556);
	private static final L2Territory _room10territory = new L2Territory(587407).addR(-49252, 247894, -11757, -11757).addR(-48587, 248519, -11757, -11757);
	private static final L2Territory _room11territory = new L2Territory(587408).addR(-52080, 245665, -12619, -12419).addR(-51174, 246660, -12619, -12419);
	private static final L2Territory _room12territory = new L2Territory(587409).addR(-48732, 243186, -13423, -13223).addR(-47752, 244097, -13423, -13223);

	private static List<L2NpcInstance> _roomMobList;

	private static final Location[] _room2locs = {
		new Location(-48146, 249597, -9124, -16280),
		new Location(-48144, 248711, -9124, 16368),
		new Location(-48704, 249597, -9104, -16380),
		new Location(-49219, 249596, -9104, -16400),
		new Location(-49715, 249601, -9104, -16360),
		new Location(-49714, 248696, -9104, 15932),
		new Location(-49225, 248710, -9104, 16512),
		new Location(-48705, 248708, -9104, 16576), };

	private static final Location[] _room4locs = {
		new Location(-49754, 243866, -9968, -16328),
		new Location(-49754, 242940, -9968, 16336),
		new Location(-48733, 243858, -9968, -16208),
		new Location(-48745, 242936, -9968, 16320),
		new Location(-49264, 242946, -9968, 16312),
		new Location(-49268, 243869, -9968, -16448),
		new Location(-48186, 242934, -9968, 16576),
		new Location(-48185, 243855, -9968, -16448), };

	public NaiaRoomControllerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		Location kickLoc = new Location(17656, 244328, 11595);

		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}	
		if(command.startsWith("challengeroom"))
		{
			if(!NaiaTowerManager.isLegalGroup(player))
				if(player.isInParty())
				{
					for(L2Player member : player.getParty().getPartyMembers())
						member.teleToLocation(kickLoc);
					return;
				}
				else
				{
					player.teleToLocation(kickLoc);
					return;
				}

			int npcId = getNpcId();
			if(NaiaTowerManager.isRoomDone(npcId, player))
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Ingenious Contraption:<br><br>The room is already challenged."));
				return;
			}

			switch(npcId)
			{
				//Room 1
				case 18494:
				{
					DoorTable.getInstance().getDoor(18250001).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22393, 3, _room1territory, 18494);
					spawnToRoom(22394, 3, _room1territory, 18494);
					NaiaTowerManager.lockRoom(18494);
					NaiaTowerManager.addRoomDone(18494, player);
					NaiaTowerManager.addMobsToRoom(18494, _roomMobList);
					Log.add("ROOM_1: "+player.getName(), "beleth_enter_room");
					//no update for 1st room
					break;
				}
				//Room 2
				case 18495:
				{
					DoorTable.getInstance().getDoor(18250002).closeMe();
					DoorTable.getInstance().getDoor(18250003).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					for(int i = 0; i < _room2locs.length; i++)
						spawnExactToRoom(22439, _room2locs[i], 18495);
					NaiaTowerManager.lockRoom(18495);
					NaiaTowerManager.addRoomDone(18495, player);
					NaiaTowerManager.addMobsToRoom(18495, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_2: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 3
				case 18496:
				{
					DoorTable.getInstance().getDoor(18250004).closeMe();
					DoorTable.getInstance().getDoor(18250005).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22441, 2, _room3territory, 18496);
					spawnToRoom(22442, 2, _room3territory, 18496);
					NaiaTowerManager.lockRoom(18496);
					NaiaTowerManager.addRoomDone(18496, player);
					NaiaTowerManager.addMobsToRoom(18496, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_3: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 4
				case 18497:
				{
					DoorTable.getInstance().getDoor(18250006).closeMe();
					DoorTable.getInstance().getDoor(18250007).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					for(int i = 0; i < _room4locs.length; i++)
						spawnExactToRoom(22440, _room4locs[i], 18497);
					NaiaTowerManager.lockRoom(18497);
					NaiaTowerManager.addRoomDone(18497, player);
					NaiaTowerManager.addMobsToRoom(18497, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_4: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 5
				case 18498:
				{
					DoorTable.getInstance().getDoor(18250008).closeMe();
					DoorTable.getInstance().getDoor(18250009).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22411, 2, _room5territory, 18498);
					spawnToRoom(22393, 2, _room5territory, 18498);
					spawnToRoom(22394, 2, _room5territory, 18498);
					NaiaTowerManager.lockRoom(18498);
					NaiaTowerManager.addRoomDone(18498, player);
					NaiaTowerManager.addMobsToRoom(18498, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_5: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 6
				case 18499:
				{
					DoorTable.getInstance().getDoor(18250010).closeMe();
					DoorTable.getInstance().getDoor(18250011).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22395, 2, _room6territory, 18499);
					NaiaTowerManager.lockRoom(18499);
					NaiaTowerManager.addRoomDone(18499, player);
					NaiaTowerManager.addMobsToRoom(18499, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_6: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 7
				case 18500:
				{
					DoorTable.getInstance().getDoor(18250101).closeMe();
					DoorTable.getInstance().getDoor(18250013).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22393, 3, _room7territory, 18500);
					spawnToRoom(22394, 3, _room7territory, 18500);
					spawnToRoom(22412, 1, _room7territory, 18500);
					NaiaTowerManager.lockRoom(18500);
					NaiaTowerManager.addRoomDone(18500, player);
					NaiaTowerManager.addMobsToRoom(18500, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_7: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 8
				case 18501:
				{
					DoorTable.getInstance().getDoor(18250014).closeMe();
					DoorTable.getInstance().getDoor(18250015).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22395, 2, _room8territory, 18501);
					NaiaTowerManager.lockRoom(18501);
					NaiaTowerManager.addRoomDone(18501, player);
					NaiaTowerManager.addMobsToRoom(18501, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_8: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 9
				case 18502:
				{
					DoorTable.getInstance().getDoor(18250102).closeMe();
					DoorTable.getInstance().getDoor(18250017).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22441, 2, _room9territory, 18502);
					spawnToRoom(22442, 3, _room9territory, 18502);
					NaiaTowerManager.lockRoom(18502);
					NaiaTowerManager.addRoomDone(18502, player);
					NaiaTowerManager.addMobsToRoom(18502, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_9: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 10
				case 18503:
				{
					DoorTable.getInstance().getDoor(18250018).closeMe();
					DoorTable.getInstance().getDoor(18250019).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22395, 2, _room10territory, 18503);
					NaiaTowerManager.lockRoom(18503);
					NaiaTowerManager.addRoomDone(18503, player);
					NaiaTowerManager.addMobsToRoom(18503, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_10: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 11
				case 18504:
				{
					DoorTable.getInstance().getDoor(18250103).closeMe();
					DoorTable.getInstance().getDoor(18250021).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(22413, 6, _room11territory, 18504);
					NaiaTowerManager.lockRoom(18504);
					NaiaTowerManager.addRoomDone(18504, player);
					NaiaTowerManager.addMobsToRoom(18504, _roomMobList);
					NaiaTowerManager.updateGroupTimer(player);
					Log.add("ROOM_11: "+player.getName(), "beleth_enter_room");
					break;
				}
				//Room 12
				//Last special room
				case 18505:
				{
					// 18495
					DoorTable.getInstance().getDoor(18250022).closeMe();
					DoorTable.getInstance().getDoor(18250023).closeMe();
					_roomMobList = new ArrayList<L2NpcInstance>();
					spawnToRoom(18490, 12, _room12territory, 18505);
					NaiaTowerManager.lockRoom(18505);
					NaiaTowerManager.addRoomDone(18505, player);
					NaiaTowerManager.addMobsToRoom(18505, _roomMobList);
					NaiaTowerManager.removeGroupTimer(player);
					Log.add("ROOM_12: "+player.getName(), "beleth_enter_room");
					ThreadPoolManager.getInstance().schedule(new removeGroupTimerPool(), 300000);
					if(player != null && player.getParty() != null)
						for(L2Player p : player.getParty().getPartyMembers())
							if(p != null)
							{
								p.roomDone();
								Log.add("ROOM_DONE: "+p.getName(), "beleth_enter_room");
							}
					break;
				}
				default:
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	public class removeGroupTimerPool extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			for(int j = 18494; j <= 18505; j++)
			{
				NaiaTowerManager.lockedRooms.remove(j);
				NaiaTowerManager.lockedRooms.put(j, false);
				NaiaTowerManager._roomsDone.remove(j);
				NaiaTowerManager._roomMobs.get(j).clear();
			}
		}
	}

	private void spawnToRoom(int mobId, int count, L2Territory territory, int roomId)
	{
		for(int i = 0; i < count; i++)
		{
			try
			{
				L2Spawn sp = new L2Spawn(NpcTable.getTemplate(mobId));
				sp.setLoc(territory.getRandomPoint(), Rnd.get(65535));
				sp.doSpawn(true);
				sp.stopRespawn();
				_roomMobList.add(sp.getLastSpawn());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void spawnExactToRoom(int mobId, Location loc, int roomId)
	{
		try
		{
			L2Spawn sp = new L2Spawn(NpcTable.getTemplate(mobId));
			sp.setLoc(loc);
			sp.doSpawn(true);
			sp.stopRespawn();
			_roomMobList.add(sp.getLastSpawn());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		showHtmlFile(player, "18494.htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}
}