package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.HennaUnequipInfo;
import l2open.gameserver.tables.HennaTable;
import l2open.gameserver.templates.L2Henna;

public class RequestHennaUnequipInfo extends L2GameClientPacket
{
	private int _symbolId;

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;

		L2Henna henna = HennaTable.getInstance().getTemplate(_symbolId);
		if (henna != null)
			player.sendPacket(new HennaUnequipInfo(henna, player));
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		_symbolId = readD();
	}
}