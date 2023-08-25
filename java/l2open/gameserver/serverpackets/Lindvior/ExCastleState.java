package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.residence.Castle;
//import l2open.gameserver.model.entity.residence.ResidenceSide;

public class ExCastleState extends L2GameServerPacket
{
	private final int _id;
	//private final ResidenceSide _side;
	
	public ExCastleState(Castle castle)
	{
		_id = castle.getId();
		//_side = castle.getResidenceSide();
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x133);
		writeD(_id);
		writeD(0x00);
	}
}