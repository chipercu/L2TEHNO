package l2open.gameserver.serverpackets;

import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2StaticObjectInstance;

public class StaticObject extends L2GameServerPacket
{
	private int _id, obj_id;
	private int p1, p2, p3, p4, p5, p6, p7, p8, p9;

	public StaticObject(L2StaticObjectInstance StaticObject)
	{
		_id = StaticObject.getStaticObjectId();
		obj_id = StaticObject.getObjectId();
		p1 = 2; //Kamael (0x02 - throne and map board)
		p2 = 1; //Kamael
		p3 = 0; //Kamael
		p4 = 0; //Kamael
		p5 = 0; //Kamael
		p6 = 0; //Kamael
		p7 = 0; //Kamael
		p8 = 0; //Kamael
		p9 = 0; //Kamael
	}

	public StaticObject(L2DoorInstance door)
	{
		_id = door.getDoorId();
		obj_id = door.getObjectId();
		p1 = 1;
		p2 = 1;
		p3 = 1;
		p4 = door.isOpen() ? 0 : 1; //opened 0 /closed 1
		p5 = 0;
		p6 = (int) door.getCurrentHp();
		p7 = door.getMaxHp();
		p8 = door.isHPVisible() ? 1 : 0;
		p9 = door.getDamage();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9f);
		writeD(_id);
		writeD(obj_id);
		writeD(p1);
		writeD(p2);
		writeD(p3);
		writeD(p4);
		writeD(p5);
		writeD(p6);
		writeD(p7);
		writeD(p8);
		writeD(p9);
	}
}