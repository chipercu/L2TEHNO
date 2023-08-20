package l2open.gameserver.serverpackets;

/**
Tèï: 0xFE (ExEventMatchFenceInfo)
Pàçìåð: 31+2
Âðåìÿ ïðèõîäà: 14:03:23:310
0002 h  subID: 3 (0x0003)
0004 d  unk: 1477445156 (0x58100624)
0008 d  unk: 1 (0x00000001)
000C d  unk: 72835 (0x00011C83)
0010 d  unk: 142653 (0x00022D3D)
0014 d  unk: -3768 (0xFFFFF148)
0018 d  unk: 200 (0x000000C8)
001C d  unk: 100 (0x00000064)
**/
// FE 03 00 24 06 10 58 01 00 00 00 83 1C 01 00 3D 2D 02 00 48 F1 FF FF C8 00 00 00 64 00 00 00 
public class ExEventMatchFenceInfo extends L2GameServerPacket
{
	int obj_id;
	int type;
	int x;
	int y;
	int z;
	int min;
	int max;

	public ExEventMatchFenceInfo(int _obj_id, int _type, int _x, int _y, int _z, int _min, int _max)
	{
		obj_id = _obj_id;
		type = _type;
		x = _x;
		y = _y;
		z = _z;
		min = _min;
		max = _max;
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x03);
		writeD(obj_id); // obj_id
		writeD(type); // 0 - убрать ограждение, 1 - поставить столбы, 2 - поднять стены
		writeD(x); // x
		writeD(y); // y
		writeD(z); // z
		writeD(max); // max
		writeD(min); // min
	}
}