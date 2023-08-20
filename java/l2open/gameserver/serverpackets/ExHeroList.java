package l2open.gameserver.serverpackets;

import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.olympiad.Olympiad;
import l2open.gameserver.templates.StatsSet;

import java.util.Map;

/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public class ExHeroList extends L2GameServerPacket
{
	private Map<Integer, StatsSet> _heroList;

	public ExHeroList()
	{
		_heroList = Hero.getInstance().getHeroes();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x7A : 0x79);

		writeD(_heroList.size());
		for(Integer heroId : _heroList.keySet())
		{
			StatsSet hero = _heroList.get(heroId);
			writeS(hero.getString(Olympiad.CHAR_NAME, ""));
			writeD(hero.getInteger(Olympiad.CLASS_ID,-1));
			writeS(hero.getString(Hero.CLAN_NAME, ""));
			writeD(hero.getInteger(Hero.CLAN_CREST, 0));
			writeS(hero.getString(Hero.ALLY_NAME, ""));
			writeD(hero.getInteger(Hero.ALLY_CREST, 0));
			writeD(hero.getInteger(Hero.COUNT,0));
		}
	}
}