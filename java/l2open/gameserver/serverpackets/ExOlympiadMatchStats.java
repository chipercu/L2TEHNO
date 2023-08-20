package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.olympiad.OlympiadTeam;

public class ExOlympiadMatchStats extends L2GameServerPacket
{
	private boolean _win;
	private OlympiadTeam _oneTeam;
	private OlympiadTeam _twoTeam;
	private int _pointDiff;

	public ExOlympiadMatchStats(boolean win, OlympiadTeam oneTeam, OlympiadTeam twoTeam, int pointDiff)
	{
		_win = win;
		_oneTeam = oneTeam;
		_twoTeam = twoTeam;
		_pointDiff = pointDiff;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0xD4);
		writeD(0x01);

		writeD(_win ? 0x00 : 0x01); 
		writeS(_win ? _oneTeam.getName() : "");

		addTeam(_oneTeam, 0x00, _win ? 1 : -1);
		addTeam(_twoTeam, 0x01, -1);
	}

	public void addTeam(OlympiadTeam team, int id, int pointMod)
	{
		writeD(id);
		writeD(team.getPlayers().size());

		for(L2Player player : team.getPlayers())
		{
			writeS(player.getName());
			writeS(player.getClan() != null ? player.getClan().getName() : "");
			writeD(player.getClanId());
			writeD(player.getClassId().getId());
			writeD(player.getDamageMy());
			writeD(team.getScore(player.getObjectId()));
			writeD(_pointDiff * pointMod);
		}
	}
}