package l2open.gameserver.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2open.gameserver.model.items.L2ItemInstance;
//import l2open.gameserver.templates.item.support.CrystallizationInfo;
//import l2open.gameserver.templates.item.support.CrystallizationInfo.CrystallizationItem;

/**
 * @author Bonux
**/
public class ExGetCrystalizingEstimation extends L2GameServerPacket
{
	//private List<CrystallizationItem> _crysItems;
	
	int _item_id;
	long _item_count;

	public ExGetCrystalizingEstimation(L2ItemInstance item)
	{
		_item_id = item.getItem().getCrystalType().cry;
		_item_count = item.getItem().getCrystalCount(item.getRealEnchantLevel());
		/*_crysItems = new ArrayList<CrystallizationItem>();

		int crystalId = item.getGrade().getCrystalId();
		int crystalCount = item.getCrystalCountOnCrystallize();
		if(crystalId > 0 && crystalCount > 0)
			_crysItems.add(new CrystallizationItem(crystalId, crystalCount, 100.));

		CrystallizationInfo info = item.getTemplate().getCrystallizationInfo();
		if(info != null)
		{
			_crysItems.addAll(info.getItems());
		}*/
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0xE1);

		writeD(0x01);
		// for
		writeD(_item_id); // итем
		writeQ(_item_count); // количество
		writeF(100F); // шанс
		/*writeD(_crysItems.size());
		for(CrystallizationItem item : _crysItems)
		{
			writeD(item.getItemId());
			writeQ(item.getCount());
			writeF(item.getChance());
		}*/
	}
}