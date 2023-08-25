package l2open.gameserver.serverpackets;

import l2open.util.GArray;

public class ExCursedWeaponList extends L2GameServerPacket
{
	private int[] cursedWeapon_ids;

	public ExCursedWeaponList(GArray<Integer> cursedWeaponIds)
	{
		cursedWeapon_ids = new int[cursedWeaponIds.size()];
		for(int i = 0; i < cursedWeaponIds.size(); i++)
			cursedWeapon_ids[i] = cursedWeaponIds.get(i);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x47 : 0x46);

		writeD(cursedWeapon_ids.length);
		for(int element : cursedWeapon_ids)
			writeD(element);
		cursedWeapon_ids = null;
	}
}