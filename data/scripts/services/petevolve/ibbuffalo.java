package services.petevolve;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.Files;

/**
 * User: darkevil
 * Date: 04.06.2008
 * Time: 1:06:26
 */
public class ibbuffalo extends Functions implements ScriptFile
{
	private static final int BABY_BUFFALO = PetDataTable.BABY_BUFFALO_ID;
	private static final int BABY_BUFFALO_PANPIPE = 6648;
	private static final int IN_BABY_BUFFALO_NECKLACE = 10311;

	public void evolve()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		L2Summon pl_pet = player.getPet();
		if(player.getInventory().getItemByItemId(BABY_BUFFALO_PANPIPE) == null)
		{
			show(Files.read("data/scripts/services/petevolve/no_item.htm", player), player, npc);
			return;
		}
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/evolve_no.htm", player), player, npc);
			return;
		}
		if(pl_pet.getNpcId() != BABY_BUFFALO)
		{
			show(Files.read("data/scripts/services/petevolve/no_pet.htm", player), player, npc);
			return;
		}
		if(ConfigValue.ImprovedPetsLimitedUse && player.isMageClass())
		{
			show(Files.read("data/scripts/services/petevolve/no_class_w.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show(Files.read("data/scripts/services/petevolve/no_level.htm", player), player, npc);
			return;
		}
		pl_pet.deleteMe();
		addItem(player, IN_BABY_BUFFALO_NECKLACE, 1);
		show(Files.read("data/scripts/services/petevolve/yes_pet.htm", player), player, npc);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Evolve Improved Baby Buffalo");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}