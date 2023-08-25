package npc.model;

import java.util.StringTokenizer;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Files;
import l2open.util.Location;

import java.util.List;

/**
 * Данный инстанс используется в городе-инстансе на Hellbound как точка выхода
 * @author SYS
 */
public final class MoonlightTombstoneInstance extends L2NpcInstance
{
	private static final int KEY_ID = 9714;
	private final static long COLLAPSE_TIME = 5; // 5 мин
	private boolean _activated = false;

	public MoonlightTombstoneInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("insertKey") && player.getReflection() != null)
		{
			if(player.getParty() == null)
			{
				player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return;
			}

			if(!player.getParty().isLeader(player))
			{
				player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
				return;
			}

			List<L2Player> partyMembers = player.getParty().getPartyMembers();
			for(L2Player partyMember : partyMembers)
				if(!isInRange(partyMember, INTERACTION_DISTANCE * 2))
				{
					// Члены партии слишком далеко
					Functions.show(Files.read("data/html/default/32343-3.htm", player), player, this);
					return;
				}

			if(_activated)
			{
				// Уже активировано
				Functions.show(Files.read("data/html/default/32343-1.htm", player), player, this);
				return;
			}

			if(Functions.getItemCount(player, KEY_ID) > 0)
			{
				Functions.removeItem(player, KEY_ID, 1);
				player.getReflection().startCollapseTimer(COLLAPSE_TIME * 60 * 1000L);
				_activated = true;
				broadcastPacketToOthers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(COLLAPSE_TIME));
				player.getReflection().setCoreLoc(player.getReflection().getReturnLoc());
				player.getReflection().setReturnLoc(new Location(16280, 283448, -9704));
				Functions.show(Files.read("data/html/default/32343-1.htm", player), player, this);
				return;
			}
			// Нет ключа
			Functions.show(Files.read("data/html/default/32343-2.htm", player), player, this);
			return;
		}
		super.onBypassFeedback(player, command);
	}
}