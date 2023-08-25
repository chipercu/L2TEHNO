package services.Talks;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;

public class HellboundNpc extends Functions implements ScriptFile
{
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public void buyMagicBottle()
	{
		final L2Player p = (L2Player) getSelf();
		final L2NpcInstance n = getNpc();
		if(getItemCount(p, 9851) == 0 && getItemCount(p, 9852) == 0 && getItemCount(p, 9853) == 0) // нет второй или выше марки
		{
			n.onBypassFeedback(p, "Chat 1");
			return;
		}

		if(getItemCount(p, 10012) >= 20)
		{
			removeItem(p, 10012, 20); // Scorpion Poison Stingers
			addItem(p, 9672, 1); // Magic Bottle
		}
		else
			n.onBypassFeedback(p, "Chat 1");
	}
}