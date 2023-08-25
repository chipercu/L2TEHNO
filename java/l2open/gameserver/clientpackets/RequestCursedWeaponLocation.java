package l2open.gameserver.clientpackets;

import l2open.gameserver.instancemanager.CursedWeaponsManager;
import l2open.gameserver.model.CursedWeapon;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.serverpackets.ExCursedWeaponLocation;
import l2open.gameserver.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import l2open.util.GArray;
import l2open.util.Location;

public class RequestCursedWeaponLocation extends L2GameClientPacket
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

		GArray<CursedWeaponInfo> list = new GArray<CursedWeaponInfo>();
		for(CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			Location pos = cw.getWorldPosition();
			if(pos != null)
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
		}

		activeChar.sendPacket(new ExCursedWeaponLocation(list));
	}
}