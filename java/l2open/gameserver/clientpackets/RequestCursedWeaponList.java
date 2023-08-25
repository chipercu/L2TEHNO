package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.serverpackets.ExCursedWeaponList;
import l2open.util.GArray;

public class RequestCursedWeaponList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Character activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		GArray<Integer> list = new GArray<Integer>();
		for(int id : CursedWeaponsManager.getInstance().getCursedWeaponsIds())
			list.add(id);

		activeChar.sendPacket(new ExCursedWeaponList(list));
	}
}