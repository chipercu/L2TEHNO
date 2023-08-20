package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;

public class ExVoteSystemInfo extends L2GameServerPacket
{
	private final int _recHave;
	private final int _recLeft;
	private final int _time;
	private final int _bonus;
	private final int _paused;

	public ExVoteSystemInfo(L2Player player)
	{
		_recHave = player.getRecommendation().getRecomHave();
		_recLeft = player.getRecommendation().getRecomLeft();
		_time = player.getRecommendation().isHourglassBonusActive() > 0 ? (int)player.getRecommendation().isHourglassBonusActive()/1000 : player.getRecommendation().getRecomTimeLeft();
		_bonus = player.getRecommendation().getRecomExpBonus();
		_paused = (_time == 0 || player.getRecommendation().isRecBonusActive() || player.getRecommendation().isHourglassBonusActive() > 0 ? 0 : 1);
	}

	@Override
	public void writeImpl()
	{
        writeC(0xFE);
        writeH(getClient().isLindvior() ? 0xCA : 0xC9);
        writeD(_recLeft); // отданые реки
        writeD(_recHave); // полученые реки
        writeD(_time); // таймер скок секунд по времени осталось
        writeD(_bonus); // процент бонуса к опыту
        writeD(_paused); // 0 - обычный отсчет времени, 1 - поддержка
    }
}
