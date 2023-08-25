package l2open.gameserver.serverpackets;

import l2open.gameserver.templates.L2PlayerTemplate;
import l2open.util.GArray;

public class NewCharacterSuccess extends L2GameServerPacket
{
	// dddddddddddddddddddd
	private GArray<L2PlayerTemplate> _chars = new GArray<L2PlayerTemplate>();

	public void addChar(L2PlayerTemplate template)
	{
		_chars.add(template);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x0d);
		writeD(_chars.size());

		for(L2PlayerTemplate temp : _chars)
		{
			writeD(temp.race.ordinal());
			writeD(temp.classId.getId());
			writeD(0x46);
			writeD(temp.baseSTR);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseDEX);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseCON);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseINT);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseWIT);
			writeD(0x0a);
			writeD(0x46);
			writeD(temp.baseMEN);
			writeD(0x0a);
		}
	}
}