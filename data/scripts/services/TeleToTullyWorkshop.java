package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

/**
 * User: Drizzy
 * Date: 06.04.11
 * Time: 21:33
 * Service for teleport on the floor Tully Workshop (1-5).
 */
public class TeleToTullyWorkshop  extends Functions implements ScriptFile
{
	private final static Location[]	teleports	=
	{
		new Location(-12904, 273848, -15332), //1 этаж
		new Location(-12700, 273340, -13600), //2 этаж
		new Location(-13246, 275740, -11936), //3 этаж
		new Location(-12798, 273458, -10496), //4 этаж
        new Location(-13500, 275912, -9032), //5 этаж
	};

	public void teleTo(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(args.length != 1)
			return;

		Location loc = teleports[Integer.parseInt(args[0]) - 1];
		L2Party party = player.getParty();
		if(party == null)
			npc.ShowPage(player,"teleporter_a01001a.htm");
		else if(!party.isLeader(player))
			npc.ShowPage(player,"teleporter_a01001b.htm");
		else
		{
			if(player.isInOlympiadMode())
			{
				player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
				return;
			}
			else if(npc.DistFromMe(player) >= 1000)
				return;

			for(L2Player member : party.getPartyMembers())
				if((player.getZ() - member.getZ()) > 10 || (member.getZ() - player.getZ()) > 100)
				{
					npc.ShowPage(player,"teleporter_a01001b.htm");
					return;
				}

			for(L2Player member : party.getPartyMembers())
				if(member != null && npc.isInRange(member, 4000/* 2000 */) && !member.isCursedWeaponEquipped() && !member.isTerritoryFlagEquipped())
					member.teleToLocation(loc);
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
