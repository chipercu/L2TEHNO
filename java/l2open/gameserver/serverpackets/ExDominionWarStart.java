package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;

public class ExDominionWarStart  extends L2GameServerPacket
{
	private int _objectId;
	private int _territoryId;
	private boolean _isDisguised;

	public ExDominionWarStart(L2Player player)
	{
		_objectId = player.getObjectId();
		_territoryId = player.getTerritorySiege() + 80;
		_isDisguised = TerritorySiege.isDisguised(_objectId);
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(getClient().isLindvior() ? 0xA4 : 0xA3);
		writeD(_objectId);
		writeD(1);
		writeD(_territoryId); //territory Id
		writeD(_isDisguised ? 1 : 0);
		writeD(_isDisguised ? _territoryId : 0); //territory Id
	}
}
