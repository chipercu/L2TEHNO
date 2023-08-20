package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.util.GArray;

public class HennaEquipList extends L2GameServerPacket
{
	private int HennaEmptySlots;
	private long char_adena;
	private GArray<L2HennaInstance> availHenna = new GArray<L2HennaInstance>();

	public HennaEquipList(L2Player player, L2HennaInstance[] hennaEquipList)
	{
		char_adena = player.getAdena();
		HennaEmptySlots = player.getHennaEmptySlots();
		for(L2HennaInstance element : hennaEquipList)
		{
		//	_log.info("element: "+element.getSymbolId()+" item="+element.getItemIdDye()+" inv="+player.getInventory().getItemByItemId(element.getItemIdDye()));
			if(player.getInventory().getItemByItemId(element.getItemIdDye()) != null)
				availHenna.add(element);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xee);

		writeQ(char_adena);
		writeD(HennaEmptySlots);
		if(availHenna.size() != 0)
		{
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
		else
		{
			writeD(0x01);
			writeD(0x00);
			writeD(0x00);
			writeQ(0x00);
			writeQ(0x00);
			writeD(0x00);
		}
	}
}