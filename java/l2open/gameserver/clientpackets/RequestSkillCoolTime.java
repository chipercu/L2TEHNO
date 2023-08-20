package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.network.L2GameClient;
import l2open.gameserver.serverpackets.SkillCoolTime;

public class RequestSkillCoolTime extends L2GameClientPacket
{
	L2GameClient _client;

	@Override
	public void readImpl()
	{
		_client = getClient();
	}

	@Override
	public void runImpl()
	{
		L2Player pl = _client.getActiveChar();
		if(pl != null)
			pl.sendPacket(new SkillCoolTime(pl));
	}

	public boolean isFilter()
	{
		return false;
	}
}