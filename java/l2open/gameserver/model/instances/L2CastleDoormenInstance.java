package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.entity.residence.Castle;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.templates.L2NpcTemplate;

import java.util.StringTokenizer;

public class L2CastleDoormenInstance extends L2NpcInstance
{
	public L2CastleDoormenInstance(int objectID, L2NpcTemplate template)
	{
		super(objectID, template);
	}
}