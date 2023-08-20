package npc.model;

import bosses.BelethManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2CommandChannel;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public final class BelethCoffinInstance extends L2NpcInstance
{
	private static final int RING = 10314;

	public BelethCoffinInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command);
		if(st.nextToken().equals("request_ring"))
		{
			/*if(BelethManager.isRingAvailable() != player.getObjectId())
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>Ring is not available. Get lost!"));
				return;
			}
			else */if(player.getParty() == null || player.getParty().getCommandChannel() == null)
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not allowed to take the ring. Are are not the group or Command Channel."));
				return;
			}
			else if(player.getParty().getCommandChannel().getChannelLeader() != player)
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not leader or the Command Channel."));
				return;
			}

			L2CommandChannel channel = player.getParty().getCommandChannel();

			Functions.addItem(player, RING, 1);

			SystemMessage smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_S2);
			smsg.addString(player.getName());
			smsg.addItemName(RING);
			channel.broadcastToChannelMembers(smsg);

			BelethManager.setRingAvailable(0);
			deleteMe();

		}
		else
			super.onBypassFeedback(player, command);
	}
}