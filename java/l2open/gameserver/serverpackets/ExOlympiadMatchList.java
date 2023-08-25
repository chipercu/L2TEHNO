package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.olympiad.CompType;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.model.entity.olympiad.OlympiadGame;
import l2open.gameserver.model.entity.olympiad.OlympiadTeam;
import l2open.util.GArray;

public class ExOlympiadMatchList extends L2GameServerPacket
{
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeHG(0xD4);
		writeD(0x00);
		GArray<OlympiadGame> games = new GArray<OlympiadGame>();
		if(Olympiad.getOlympiadGames() != null)
			for(OlympiadGame game : Olympiad.getActiveGames())
				games.add(game);
		writeD(games != null ? games.size() : 0);
		writeD(0x00);

		if(games != null)
			for(OlympiadGame curGame : games)
			{
				writeD(curGame.getId());
				if(curGame.getType().equals(CompType.NON_CLASSED))
					writeD(0x01);
				else if(curGame.getType().equals(CompType.CLASSED))
					writeD(0x02);
				else if(curGame.getType().equals(CompType.TEAM))
					writeD(-1);
				else
					writeD(0x00);
				writeD(curGame.getState());
				writeS(curGame.getTeam(1).getName());
				writeS(curGame.getTeam(2).getName());
			}
	}
}