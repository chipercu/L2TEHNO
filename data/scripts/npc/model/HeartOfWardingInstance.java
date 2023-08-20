package npc.model;

import bosses.AntharasManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public final class HeartOfWardingInstance extends L2NpcInstance
{
	public HeartOfWardingInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("enter_lair"))
		{
			AntharasManager.enterTheLair(player);
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}
}