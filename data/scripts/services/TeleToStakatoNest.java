package services;

import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.util.Files;
import l2open.util.Location;

public class TeleToStakatoNest extends Functions implements ScriptFile
{
	private final static Location[]	teleports	=
	{
		new Location(80456, -52322, -5640),
		new Location(88718, -46214, -4640),
		new Location(87464, -54221, -5120),
		new Location(80848, -49426, -5128),
		new Location(87682, -43291, -4128)
	};

	public void list()
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		QuestState qs = player.getQuestState("_240_ImTheOnlyOneYouCanTrust");
		if(qs == null || !qs.isCompleted())
		{
			show(Files.read("data/scripts/services/TeleToStakatoNest-no.htm", player), player);
			return;
		}

		show(Files.read("data/scripts/services/TeleToStakatoNest.htm", player), player);
	}

	public void teleTo(String[] args)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		if(args.length != 1)
			return;

		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		else if(npc.DistFromMe(player) >= 1000)
			return;
		Location loc = teleports[Integer.parseInt(args[0]) - 1];
		L2Party party = player.getParty();
		if(party == null)
			player.teleToLocation(loc);
		else
			for(L2Player member : party.getPartyMembers())
				if(member != null && member.isInRange(npc, 2000) && !member.isInOlympiadMode())
					member.teleToLocation(loc);
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
