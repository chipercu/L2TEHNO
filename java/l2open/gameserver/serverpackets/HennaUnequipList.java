package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.util.GArray;

public class HennaUnequipList extends L2GameServerPacket
{
	private int HennaEmptySlots;
	private long char_adena;
	private GArray<L2HennaInstance> availHenna = new GArray<L2HennaInstance>();

	public HennaUnequipList(L2Player player, L2HennaInstance[] hennaUnEquipList)
	{
		char_adena = player.getAdena();
		HennaEmptySlots = player.getHennaEmptySlots();
		for(L2HennaInstance element : hennaUnEquipList)
			if(player.getInventory().getItemByItemId(element.getItemIdDye()) != null)
				availHenna.add(element);
	}

	public HennaUnequipList(L2Player player)
	{
		char_adena = player.getAdena();
		HennaEmptySlots = player.getHennaEmptySlots();
		for (int i = 1; i <= 3; i++)
			if (player.getHenna(i) != null)
				availHenna.add(player.getHenna(i));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xE6);

		writeQ(char_adena);
		writeD(HennaEmptySlots);

		writeD(availHenna.size());
		for(L2HennaInstance henna : availHenna)
		{
			writeD(henna.getSymbolId()); //symbolid
			writeD(henna.getItemIdDye()); //itemid of dye

			writeQ(henna.getAmountDyeRequire());
			writeQ(henna.getPrice());
			writeD(1); //meet the requirement or not
		}
	}
}