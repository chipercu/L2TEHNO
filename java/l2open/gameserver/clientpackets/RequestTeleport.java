package l2open.gameserver.clientpackets;

public class RequestTeleport extends L2GameClientPacket
{
	private int unk, _type, unk2, unk3, unk4;

	@Override
	public void runImpl()
	{}

	@Override
	public void readImpl()
	{
		unk = readD();
		_type = readD();
		if(_type == 2)
		{
			unk2 = readD();
			unk3 = readD();
			_log.info(getType() + " [2] :: " + unk + " :: " + unk2 + " :: " + unk3);
		}
		else if(_type == 3)
		{
			unk2 = readD();
			unk3 = readD();
			unk4 = readD();
			_log.info(getType() + " [3] :: " + unk + " :: " + unk2 + " :: " + unk3 + " :: " + unk4);
		}
	}
}