package l2open.gameserver.ai;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2RaceManagerInstance;
import l2open.gameserver.serverpackets.MonRaceInfo;

import java.util.*;

public class RaceManager extends DefaultAI
{
	private Boolean thinking = false; // to prevent recursive thinking
	List<L2Player> _knownPlayers = new ArrayList<L2Player>();

	public RaceManager(L2Character actor)
	{
		super(actor);
	}

	@Override
	public void runImpl() throws Exception
	{
		onEvtThink();
	}

	@Override
	protected void onEvtThink()
	{
		L2RaceManagerInstance actor = getActor();
		if(actor == null)
			return;

		MonRaceInfo packet = actor.getPacket();
		if(packet == null)
			return;

		synchronized (thinking)
		{
			if(thinking)
				return;
			thinking = true;
		}

		try
		{
			List<L2Player> newPlayers = new ArrayList<L2Player>();
			for(L2Player player : L2World.getAroundPlayers(actor, 1200, 200))
			{
				if(player == null)
					continue;
				newPlayers.add(player);
				if(!_knownPlayers.contains(player))
					player.sendPacket(packet);
				_knownPlayers.remove(player);
			}

			for(L2Player player : _knownPlayers)
				if(player != null)
					actor.removeKnownPlayer(player);

			_knownPlayers = newPlayers;
		}
		finally
		{
			// Stop thinking action
			thinking = false;
		}
	}

	@Override
	public L2RaceManagerInstance getActor()
	{
		return (L2RaceManagerInstance) super.getActor();
	}
}