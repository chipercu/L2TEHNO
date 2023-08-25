package services;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.extensions.scripts.Functions;

public class Misc extends Functions
{
	public void assembleAntharasCrystal()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();

		if(player == null || npc == null || !L2NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		if(Functions.getItemCount(player, 17266) < 1 || Functions.getItemCount(player, 17267) < 1)
		{
			show("data/html/teleporter/32864-2.htm", player);
			return;
		}
		if(Functions.removeItem(player, 17266, 1) > 0 && Functions.removeItem(player, 17267, 1) > 0)
		{
			Functions.addItem(player, 17268, 1);
			show("data/html/teleporter/32864-3.htm", player);
			return;
		}
	}
}