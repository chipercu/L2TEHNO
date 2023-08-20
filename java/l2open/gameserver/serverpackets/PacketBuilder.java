package l2open.gameserver.serverpackets;

import javolution.util.FastMap;
import java.util.Map.Entry;
import l2open.util.GArray;
public class PacketBuilder extends L2GameServerPacket
{
	private FastMap<Integer, Object>  packets2 = null;
	public PacketBuilder(GArray<FastMap<Integer, Object>> packets)
	{
		packets2 = packets.get(0);
	}

	private void writePacket(int opc, Object arg)
	{
		switch(opc)
		{
			case 0:
				writeC((Integer)arg);
				break;
			case 1:
				writeH((Integer)arg);
				break;
			case 2:
				writeD((Integer)arg);
				break;
			case 3:
				writeQ((Long)arg);
				break;
			case 4:
				writeF((Double)arg);
				break;
			case 5:
				writeS((String)arg);
				break;
		}
	}

	@Override
	protected final void writeImpl()
	{
		for(Entry<Integer, Object> e : packets2.entrySet())
			writePacket(e.getKey(), e.getValue());
	}
}