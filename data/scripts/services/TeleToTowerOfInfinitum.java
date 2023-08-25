package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.Location;

/**
 * @author Drizzy
 * @date 06.04.11
 * Service for teleport on the floor of Tower of Infinitum.
 */
public class TeleToTowerOfInfinitum extends Functions implements ScriptFile
{
	private final static Location[]	teleports	=
	{
		new Location(-22208, 277056, -15045), //1 этаж
		new Location(-22208, 277122, -13376), //1-2 этаж
		new Location(-22208, 277106, -11648), //2-3 этаж
		new Location(-22208, 277120, -9920), //3-4 этаж
		new Location(-19024, 277126, -8256), //4-6 этаж + РБ(142 инстант)
		new Location(-19024, 277106, -9920), //6-7 этаж
		new Location(-19008, 277100, -11648), //7-8 этаж
		new Location(-19008, 277100, -13376), //8-9 этаж
		new Location(14602, 283179, -7500), //9-10 этаж + РБ(143 инстант) + тулли
	};

	public void teleTo(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(args.length != 1)
			return;
		if(Integer.parseInt(args[0]) == 5)
			if(player.getReflection() != null && player.getReflection().getId() != 0)
				player.getReflection().startCollapseTimer(1);
		if(Integer.parseInt(args[0]) == 9)
			if(player.getReflection() != null && player.getReflection().getId() != 0)
				player.getReflection().startCollapseTimer(1);

		Location loc = teleports[Integer.parseInt(args[0]) - 1];
		L2Party party = player.getParty();
		if(party == null)
			show(Files.read("data/scripts/services/TeleToTowerOfInfinitumNoParty.htm", player), player);
		else
		{
			if(npc.isInRange(player, 1000))
			{
				for(L2Player member : party.getPartyMembers())
					if((player.getZ() - member.getZ()) > 10 || (member.getZ() - player.getZ()) > 100)
					{
						npc.ShowPage(player,"teleporter_a01001b.htm");
						return;
					}

				for(L2Player member : party.getPartyMembers())
					if(member != null && npc.isInRange(member, 4000) && !member.isCursedWeaponEquipped() && !member.isTerritoryFlagEquipped() && !member.isInOlympiadMode())
						member.teleToLocation(loc);
			}
		}
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}