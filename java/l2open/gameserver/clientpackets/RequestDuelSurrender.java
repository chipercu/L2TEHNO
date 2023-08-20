package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.Duel;

public class RequestDuelSurrender extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player p = getClient().getActiveChar();

		if(p == null)
			return;

		Duel d = p.getDuel();

		if(d == null)
			return;

		d.doSurrender(p);
	}
}