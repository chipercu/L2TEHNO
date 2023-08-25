package l2open.gameserver.serverpackets;

import l2open.gameserver.model.L2Alliance;
import l2open.gameserver.model.L2Clan;

public class PledgeShowInfoUpdate extends L2GameServerPacket
{
	private int clan_id, clan_level, clan_rank, clan_rep, crest_id, ally_id, ally_crest, atwar;
	private String ally_name = "";
	private int HasCastle, HasHideout, HasFortress;

	public PledgeShowInfoUpdate(final L2Clan clan)
	{
		clan_id = clan.getClanId();
		clan_level = clan.getLevel();
		HasCastle = clan.getHasCastle();
		HasHideout = clan.getHasHideout();
		HasFortress = clan.getHasFortress();
		clan_rank = clan.getRank();
		clan_rep = clan.getReputationScore();
		crest_id = clan.getCrestId();
		ally_id = clan.getAllyId();
		L2Alliance ally = clan.getAlliance();
		if(ally != null)
		{
			ally_name = ally.getAllyName();
			ally_crest = ally.getAllyCrestId();
			atwar = clan.isAtWar();
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x8e);
		//sending empty data so client will ask all the info in response ;)
		writeD(clan_id);
		writeD(crest_id);
		writeD(clan_level);
		writeD(HasCastle);
		if(getClient().isLindvior())
			writeD(0x00);
		writeD(HasHideout);
		writeD(HasFortress);
		writeD(clan_rank);// displayed in the "tree" view (with the clan skills)
		writeD(clan_rep);
		writeD(0);
		writeD(0);
		writeD(ally_id); //c5
		writeS(ally_name); //c5
		writeD(ally_crest); //c5
		writeD(atwar); //c5

		// isGraciaFinal
		writeD(0x00); // Territory castle ID ?
		writeD(0x00); //?
	}
}