package l2open.gameserver.clientpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.serverpackets.ExOlympiadMatchList;

public class RequestOlympiadMatchList extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if((!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd()) && !Olympiad.isFakeOly())
		{
			_log.info("RequestOlympiadMatchList: THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS");
			//player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
			return;
		}
		player.sendPacket(new ExOlympiadMatchList());
		//player.sendPacket(new ExReceiveOlympiad.MatchList());
	}
}