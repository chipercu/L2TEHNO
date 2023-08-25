package l2open.gameserver.skills.skillclasses;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.serverpackets.L2GameServerPacket;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Location;

public class Teleport extends L2Skill
{
	private final Location loc;
	private final String target;

	public Teleport(StatsSet set)
	{
		super(set);
		String[] coords = set.getString("teleCoords", "").split(";");
		target = set.getString("target", "NUN");
		loc = new Location(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		L2Player player = activeChar.getPlayer();
		if(player == null)
			return;
		if(player.isCombatFlagEquipped() || player.isTerritoryFlagEquipped())
		{
			player.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
			return;
		}
		if(player.isFestivalParticipant())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Recall.Festival", activeChar));
			return;
		}
		if(player.isInOlympiadMode())
		{
			player.sendPacket(Msg.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		if(player.getDuel() != null || player.getTeam() != 0)
		{
			player.sendMessage(new CustomMessage("common.RecallInDuel", activeChar));
			return;
		}
		if(player.getVar("jailed") != null)
			return;
		if(target.contains("TARGET_OWNER_PET") || target.contains("TARGET_PARTY"))
			for(L2Character targ : targets)
				targ.teleToLocation(loc);
		else
			activeChar.teleToLocation(loc);
	}
}