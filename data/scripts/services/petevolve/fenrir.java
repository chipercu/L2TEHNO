package services.petevolve;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.Files;

/**
 * User: darkevil
 * Date: 02.06.2008
 * Time: 12:19:36
 */
public class fenrir extends Functions implements ScriptFile
{
	private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
	private static final int GREAT_WOLF_NECKLACE = 9882;
	private static final int FENRIR_NECKLACE = 10426;

	public void evolve()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(player.getInventory().getItemByItemId(GREAT_WOLF_NECKLACE) == null)
		{
			show(Files.read("data/scripts/services/petevolve/no_item.htm", player), player, npc);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/evolve_no.htm", player), player, npc);
			return;
		}
		if(pl_pet.getNpcId() != GREAT_WOLF)
		{
			show(Files.read("data/scripts/services/petevolve/no_wolf.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 70)
		{
			show(Files.read("data/scripts/services/petevolve/no_level_gw.htm", player), player, npc);
			return;
		}
		pl_pet.deleteMe();
		addItem(player, FENRIR_NECKLACE, 1);
		show(Files.read("data/scripts/services/petevolve/yes_wolf.htm", player), player, npc);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Evolve Fenrir");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}