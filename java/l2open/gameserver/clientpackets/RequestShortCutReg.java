package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2ShortCut;
import l2open.gameserver.serverpackets.ShortCutRegister;

public class RequestShortCutReg extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _type, _id, _slot, _page, _lvl, _characterType;

	/**
	 * packet type id 0x3D
	 * format:		cddddd
	 */
	@Override
	public void readImpl()
	{
		_type = readD();
		int slot = readD();
		_id = readD();
		_lvl = readD();
		_characterType = readD(); // UserShortCut

		_slot = slot % 12;
		_page = slot / 12;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_slot < 0 || _slot > 11 || _page < 0 || _page > 10)
		{
			activeChar.sendActionFailed();
			return;
		}

		L2ShortCut sc = new L2ShortCut(_slot, _page, _type, _id, _lvl, _characterType);
		sendPacket(new ShortCutRegister(sc));
		activeChar.registerShortCut(sc);
	}
}