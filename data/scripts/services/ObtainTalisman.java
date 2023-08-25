package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;
import l2open.util.Rnd;

/**
 * Используется для выдачи талисманов в крепостях и замках за Knight's Epaulette.
 * @Author: SYS
 */
public class ObtainTalisman extends Functions implements ScriptFile
{
	public void onLoad()
	{
		_log.info("Loaded Service: Obtain Talisman");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public void Obtain()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!L2NpcInstance.canBypassCheck(player, npc))
			return;

		if(!player.isQuestContinuationPossible(false))
		{
			player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		if(getItemCount(player, 9912) < 10)
		{
			show(Files.read("data/scripts/services/ObtainTalisman-no.htm", player), player, npc);
			return;
		}

		final GArray<Integer> talismans = new GArray<Integer>();

		//9914-9965
		for(int i = 9914; i <= 9965; i++)
			if(i != 9923)
				talismans.add(i);
		//10416-10424
		for(int i = 10416; i <= 10424; i++)
			talismans.add(i);
		//10518-10519
		for(int i = 10518; i <= 10519; i++)
			talismans.add(i);
		//10533-10543
		for(int i = 10533; i <= 10543; i++)
			talismans.add(i);

		removeItem(player, 9912, 10);
		addItem(player, talismans.get(Rnd.get(talismans.size())), 1);
		show(Files.read("data/scripts/services/ObtainTalisman.htm", player), player, npc);
	}
}