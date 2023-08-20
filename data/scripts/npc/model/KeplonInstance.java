package npc.model;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author Diagod
 */

public final class KeplonInstance extends L2NpcInstance
{
	public KeplonInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("buygreen"))
		{
			if(Functions.getItemCount(player, 57) >= 10000)
			{
				Functions.removeItem(player, 57, 10000);
				Functions.addItem(player, 4401, 1);
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyblue"))
		{
			if(Functions.getItemCount(player, 57) >= 10000)
			{
				Functions.removeItem(player, 57, 10000);
				Functions.addItem(player, 4402, 1);
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyred"))
		{
			if(Functions.getItemCount(player, 57) >= 10000)
			{
				Functions.removeItem(player, 57, 10000);
				Functions.addItem(player, 4403, 1);
				return;
			}
			else
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}