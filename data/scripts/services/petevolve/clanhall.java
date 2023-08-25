package services.petevolve;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Summon;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.ItemList;
import l2open.gameserver.tables.PetDataTable;
import l2open.util.Files;

/**
 * User: Keiichi
 * Date: 09.08.2008
 * Time: 15:36:59
 */
public class clanhall extends Functions implements ScriptFile
{
	// -- Pet ID --
	private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
	private static final int WHITE_WOLF = PetDataTable.WGREAT_WOLF_ID;
	private static final int FENRIR = PetDataTable.FENRIR_WOLF_ID;
	private static final int WHITE_FENRIR = PetDataTable.WFENRIR_WOLF_ID;
	private static final int WIND_STRIDER = PetDataTable.STRIDER_WIND_ID;
	private static final int RED_WIND_STRIDER = PetDataTable.RED_STRIDER_WIND_ID;
	private static final int STAR_STRIDER = PetDataTable.STRIDER_STAR_ID;
	private static final int RED_STAR_STRIDER = PetDataTable.RED_STRIDER_STAR_ID;
	private static final int TWILING_STRIDER = PetDataTable.STRIDER_TWILIGHT_ID;
	private static final int RED_TWILING_STRIDER = PetDataTable.RED_STRIDER_TWILIGHT_ID;

	// -- First Item ID --
	private static final int GREAT_WOLF_NECKLACE = 9882;
	private static final int FENRIR_NECKLACE = 10426;
	private static final int WIND_STRIDER_ITEM = 4422;
	private static final int STAR_STRIDER_ITEM = 4423;
	private static final int TWILING_STRIDER_ITEM = 4424;

	// -- Second Item ID --
	private static final int WHITE_WOLF_NECKLACE = 10307;
	private static final int WHITE_FENRIR_NECKLACE = 10611;
	private static final int RED_WS_ITEM = 10308;
	private static final int RED_SS_ITEM = 10309;
	private static final int RED_TW_ITEM = 10310;

	public void evolve()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		show(Files.read("data/scripts/services/petevolve/chamberlain.htm", player), player, npc);
	}

	public void greatsw(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/error_1.htm", player), player, npc);
			return;
		}
		if(pl_pet.getNpcId() != (fwd ? GREAT_WOLF : WHITE_WOLF))
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show(Files.read("data/scripts/services/petevolve/error_lvl_greatw.htm", player), player, npc);
			return;
		}
		if(player.getInventory().getItemByItemId((fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE)) == null)
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/end_msg3_gwolf.htm", player), player, npc);
	}

	public void fenrir(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		if(player.getInventory().getItemByItemId(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE) == null)
		{
			show(Files.read("data/scripts/services/petevolve/no_item.htm", player), player, npc);
			return;
		}
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/error_1.htm", player), player, npc);
			return;
		}
		if(pl_pet.getNpcId() != (fwd ? FENRIR : WHITE_FENRIR))
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 70)
		{
			show(Files.read("data/scripts/services/petevolve/error_lvl_fenrir.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/end_msg2_fenrir.htm", player), player, npc);
	}

	public void fenrirW(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		if(player.getInventory().getItemByItemId(fwd ? WHITE_WOLF_NECKLACE : WHITE_FENRIR_NECKLACE) == null)
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
		if(pl_pet.getNpcId() != (fwd ? WHITE_WOLF : WHITE_FENRIR))
		{
			show(Files.read("data/scripts/services/petevolve/no_wolf.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 70)
		{
			show(Files.read("data/scripts/services/petevolve/no_level_gw.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? WHITE_FENRIR_NECKLACE : WHITE_WOLF_NECKLACE);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/yes_wolf.htm", player), player, npc);
	}

	public void wstrider(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/error_1.htm", player), player, npc);
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		if(pl_pet.getNpcId() != (fwd ? WIND_STRIDER : RED_WIND_STRIDER))
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show(Files.read("data/scripts/services/petevolve/error_lvl_strider.htm", player), player, npc);
			return;
		}
		if(player.getInventory().getItemByItemId(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM) == null)
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/end_msg_strider.htm", player), player, npc);
	}

	public void sstrider(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/error_1.htm", player), player, npc);
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		if(pl_pet.getNpcId() != (fwd ? STAR_STRIDER : RED_STAR_STRIDER))
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show(Files.read("data/scripts/services/petevolve/error_lvl_strider.htm", player), player, npc);
			return;
		}
		if(player.getInventory().getItemByItemId(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM) == null)
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/end_msg_strider.htm", player), player, npc);
	}

	public void tstrider(String[] direction)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		L2Summon pl_pet = player.getPet();
		if(pl_pet == null || pl_pet.isDead())
		{
			show(Files.read("data/scripts/services/petevolve/error_1.htm", player), player, npc);
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;
		if(pl_pet.getNpcId() != (fwd ? TWILING_STRIDER : RED_TWILING_STRIDER))
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show(Files.read("data/scripts/services/petevolve/error_lvl_strider.htm", player), player, npc);
			return;
		}
		if(player.getInventory().getItemByItemId(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM) == null)
		{
			show(Files.read("data/scripts/services/petevolve/error_2.htm", player), player, npc);
			return;
		}

		L2ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
		control.setItemId(fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM);
		control.updateDatabase(true, true);
		player.sendPacket(new ItemList(player, false));
		player.getPet().unSummon();

		show(Files.read("data/scripts/services/petevolve/end_msg_strider.htm", player), player, npc);
	}

	public void onLoad()
	{
		_log.info("Loaded Service: ClanHall Pet Evolution");
	}

	public void onReload()
	{}

	public void onShutdown()
	{}
}