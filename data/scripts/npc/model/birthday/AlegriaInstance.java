package npc.model.birthday;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

public class AlegriaInstance extends L2NpcInstance
{
	private static final int EXPLORERHAT = 10250;
	private static final int HAT = 13488; // Birthday Hat

 	public AlegriaInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("exchangeHat"))
		{
			if(Functions.getItemCount(player, EXPLORERHAT) < 1)
			{
				showChatWindow(player, "default/32600-nohat.htm");
				return;
			}

			Functions.removeItem(player, EXPLORERHAT, 1);
			Functions.addItem(player, HAT, 1);

			showChatWindow(player, "default/32600-successful.htm");

			deleteMe();
		}
		else
			super.onBypassFeedback(player, command);
	}
}
