package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2HennaInstance;
import l2open.gameserver.serverpackets.HennaItemInfo;
import l2open.gameserver.tables.HennaTable;
import l2open.gameserver.templates.L2Henna;

public class RequestHennaItemInfo extends L2GameClientPacket
{
	// format  cd
	private int SymbolId;

	@Override
	public void readImpl()
	{
		SymbolId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2Henna template = HennaTable.getInstance().getTemplate(SymbolId);
		if(template != null)
			activeChar.sendPacket(new HennaItemInfo(new L2HennaInstance(template), activeChar));
	}
}