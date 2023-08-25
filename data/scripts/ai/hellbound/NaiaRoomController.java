package ai.hellbound;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.instancemanager.NaiaTowerManager;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.util.Log;

import java.util.List;


public class NaiaRoomController extends DefaultAI
{
	public NaiaRoomController(L2NpcInstance actor)
	{
		super(actor);
		actor.p_block_move(true, null);
		AI_TASK_ACTIVE_DELAY = 1000;
		AI_TASK_DELAY_CURRENT = 1000;
		AI_TASK_ATTACK_DELAY = 1000;
	}

	@Override
	protected void onEvtThink()
	{
		if(NaiaTowerManager.isLockedRoom(getActor().getNpcId()))
		{
			List<L2NpcInstance> _roomMobs = NaiaTowerManager.getRoomMobs(getActor().getNpcId());
			if(_roomMobs == null)
			{
				if(ConfigValue.NaiaRoomControllerDebug > 0 && getActor().getNpcId() == ConfigValue.NaiaRoomControllerDebug)
					Log.add("NaiaRoomController("+getActor().getNpcId()+"): _roomMobs == null", "naia_room_controller");
				return;
			}
			if(!_roomMobs.isEmpty())
				for(L2NpcInstance npc : _roomMobs)
				{
					if(npc == null && ConfigValue.NaiaRoomControllerDebug > 0 && getActor().getNpcId() == ConfigValue.NaiaRoomControllerDebug)
						Log.add("NaiaRoomController("+getActor().getNpcId()+"): npc == null", "naia_room_controller");
					if(npc != null && !npc.isDead())
					{
						if(ConfigValue.NaiaRoomControllerDebug > 0 && getActor().getNpcId() == ConfigValue.NaiaRoomControllerDebug)
							Log.add("NaiaRoomController("+getActor().getNpcId()+"): !isDead", "naia_room_controller");
						return;
					}
				}
			switch(getActor().getNpcId())
			{
				//Room 1
				case 18494:
				{
					DoorTable.getInstance().getDoor(18250002).openMe();
					DoorTable.getInstance().getDoor(18250003).openMe();
					NaiaTowerManager.unlockRoom(18494);
					NaiaTowerManager.removeRoomMobs(18494);
					break;
				}
					//Room 2
				case 18495:
				{
					DoorTable.getInstance().getDoor(18250004).openMe();
					DoorTable.getInstance().getDoor(18250005).openMe();
					NaiaTowerManager.unlockRoom(18495);
					NaiaTowerManager.removeRoomMobs(18495);
					break;
				}
					//Room 3
				case 18496:
				{
					DoorTable.getInstance().getDoor(18250006).openMe();
					DoorTable.getInstance().getDoor(18250007).openMe();
					NaiaTowerManager.unlockRoom(18496);
					NaiaTowerManager.removeRoomMobs(18496);
					break;
				}
					//Room 4
				case 18497:
				{
					DoorTable.getInstance().getDoor(18250008).openMe();
					DoorTable.getInstance().getDoor(18250009).openMe();
					NaiaTowerManager.unlockRoom(18497);
					NaiaTowerManager.removeRoomMobs(18497);
					break;
				}
					//Room 5
				case 18498:
				{
					DoorTable.getInstance().getDoor(18250010).openMe();
					DoorTable.getInstance().getDoor(18250011).openMe();
					NaiaTowerManager.unlockRoom(18498);
					NaiaTowerManager.removeRoomMobs(18498);
					break;
				}
					//Room 6
				case 18499:
				{
					DoorTable.getInstance().getDoor(18250101).openMe();
					DoorTable.getInstance().getDoor(18250013).openMe();
					NaiaTowerManager.unlockRoom(18499);
					NaiaTowerManager.removeRoomMobs(18499);
					break;
				}
					//Room 7
				case 18500:
				{
					DoorTable.getInstance().getDoor(18250014).openMe();
					DoorTable.getInstance().getDoor(18250015).openMe();
					NaiaTowerManager.unlockRoom(18500);
					NaiaTowerManager.removeRoomMobs(18500);
					break;
				}
					//Room 8
				case 18501:
				{
					DoorTable.getInstance().getDoor(18250102).openMe();
					DoorTable.getInstance().getDoor(18250017).openMe();
					NaiaTowerManager.unlockRoom(18501);
					NaiaTowerManager.removeRoomMobs(18501);
					break;
				}
					//Room 9
				case 18502:
				{
					DoorTable.getInstance().getDoor(18250018).openMe();
					DoorTable.getInstance().getDoor(18250019).openMe();
					NaiaTowerManager.unlockRoom(18502);
					NaiaTowerManager.removeRoomMobs(18502);
					break;
				}
					//Room 10
				case 18503:
				{
					DoorTable.getInstance().getDoor(18250103).openMe();
					DoorTable.getInstance().getDoor(18250021).openMe();
					NaiaTowerManager.unlockRoom(18503);
					NaiaTowerManager.removeRoomMobs(18503);
					break;
				}
					//Room 11
				case 18504:
				{
					DoorTable.getInstance().getDoor(18250022).openMe();
					DoorTable.getInstance().getDoor(18250023).openMe();
					NaiaTowerManager.unlockRoom(18504);
					NaiaTowerManager.removeRoomMobs(18504);
					break;
				}
					//Room 12
				case 18505:
				{
					DoorTable.getInstance().getDoor(18250024).openMe();
					ThreadPoolManager.getInstance().schedule(new LastDoorClose(), 300 * 1000L);
					NaiaTowerManager.unlockRoom(18505);
					NaiaTowerManager.removeRoomMobs(18505);
					break;
				}
				default:
					break;
			}
		}
		else
		{
			if(ConfigValue.NaiaRoomControllerDebug2 > 0 && getActor().getNpcId() == ConfigValue.NaiaRoomControllerDebug2)
				Log.add("NaiaRoomController("+getActor().getNpcId()+"): !isLockedRoom", "naia_room_controller");
		}
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	private class LastDoorClose extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			DoorTable.getInstance().getDoor(18250024).closeMe();
		}
	}
}