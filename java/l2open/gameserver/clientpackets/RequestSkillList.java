package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.serverpackets.SkillList;

public final class RequestSkillList extends L2GameClientPacket
{
	private static final String _C__50_REQUESTSKILLLIST = "[C] 50 RequestSkillList";

	@Override
	protected void readImpl()
	{
	// this is just a trigger packet. it has no content
	}

	@Override
	protected void runImpl()
	{
		L2Player cha = getClient().getActiveChar();

		if(cha != null)
			cha.sendPacket(new SkillList(cha));
	}

	@Override
	public String getType()
	{
		return _C__50_REQUESTSKILLLIST;
	}
}
