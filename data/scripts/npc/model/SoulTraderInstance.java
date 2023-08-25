package npc.model;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Multisell;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2MerchantInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class SoulTraderInstance extends L2MerchantInstance
{
	public SoulTraderInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if (command.equalsIgnoreCase("buyspecial"))
			if (Functions.getItemCount(player, 13691) >= 1)
				L2Multisell.getInstance().SeparateAndSend(698, player, 0);
			else
				L2Multisell.getInstance().SeparateAndSend(647, player, 0);
		super.onBypassFeedback(player, command);
	}
}