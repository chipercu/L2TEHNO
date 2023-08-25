package l2open.gameserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.items.L2ItemInstance;

import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
public class ExRefundList extends L2GameServerPacket
{
	private ConcurrentLinkedQueue<L2ItemInstance> _RefundList;
	long _adena;

	public ExRefundList(L2Player cha)
	{
		_adena = cha.getAdena();
		_RefundList = cha.getInventory().getRefundItemsList();
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xA8);

		writeQ(_adena);

		// hx[ddQhhhQhhhhhhhh]
		if(_RefundList == null)
			writeH(0);
		else
		{
			writeH(_RefundList.size());
			for(L2ItemInstance item : _RefundList)
			{
				writeD(item.getObjectId());
				writeD(item.getItemId());
				writeQ(item.getCount());
				writeH(item.getItem().getType2ForPackets());
				writeH(item.getRealEnchantLevel());
				writeH(0x00); // unknown
				if(ConfigValue.SellItemOneAdena)
					writeQ(1);
				else
				{
					long price = item.getItem().getReferencePrice() / ConfigValue.SellItemDiv;
					price -= price*ConfigValue.SellITaxPer/100;

					writeQ(price);
				}
				writeItemElements(item);
			}
		}
	}
}