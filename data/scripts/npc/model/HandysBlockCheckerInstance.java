package npc.model;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.instancemanager.HandysBlockCheckerManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExCubeGameChangeTimeToStart;
import l2open.gameserver.serverpackets.ExCubeGameRequestReady;
import l2open.gameserver.serverpackets.ExCubeGameTeamList;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

public class HandysBlockCheckerInstance extends L2NpcInstance
{
	public HandysBlockCheckerInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		if(!ConfigValue.EnableBlockCheckerEvent)
			return;
		HandysBlockCheckerManager.getInstance().startUpParticipantsQueue();
	}

	// Arena Managers
	private static final int A_MANAGER_1 = 32521;
	private static final int A_MANAGER_2 = 32522;
	private static final int A_MANAGER_3 = 32523;
	private static final int A_MANAGER_4 = 32524;

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		if(player == null || !ConfigValue.EnableBlockCheckerEvent)
			return;
		int npcId = getNpcId();

		int arena = -1;
		switch(npcId)
		{
			case A_MANAGER_1:
				arena = 0;
				break;
			case A_MANAGER_2:
				arena = 1;
				break;
			case A_MANAGER_3:
				arena = 2;
				break;
			case A_MANAGER_4:
				arena = 3;
				break;
		}

		if(arena != -1)
		{
			if(eventIsFull(arena))
			{
				player.sendPacket(new SystemMessage(SystemMessage.YOU_CANNOT_REGISTER_BECAUSE_CAPACITY_HAS_BEEN_EXCEEDED));
				return;
			}
			if(HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(arena))
			{
				player.sendPacket(new SystemMessage(SystemMessage.THE_MATCH_IS_BEING_PREPARED_PLEASE_TRY_AGAIN_LATER));
				return;
			}
			if(HandysBlockCheckerManager.getInstance().addPlayerToArena(player, arena))
			{
				HandysBlockCheckerManager.ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);

				final ExCubeGameTeamList tl = new ExCubeGameTeamList(holder.getRedPlayers(), holder.getBluePlayers(), arena);

				player.sendPacket(tl);

				int countBlue = holder.getBlueTeamSize();
				int countRed = holder.getRedTeamSize();
				int minMembers = ConfigValue.BlockCheckerMinTeamMembers;

				if(countBlue >= minMembers && countRed >= minMembers)
				{
					holder.updateEvent();
					holder.broadCastPacketToTeam(new ExCubeGameRequestReady());
					holder.broadCastPacketToTeam(new ExCubeGameChangeTimeToStart(10));
					ThreadPoolManager.getInstance().schedule(holder.getEvent().new StartEvent(), 10100L);
				}
			}
		}
	}

	private boolean eventIsFull(int arena)
	{
		if(HandysBlockCheckerManager.getInstance().getHolder(arena).getPlayers().size() == 12)
			return true;
		return false;
	}
}