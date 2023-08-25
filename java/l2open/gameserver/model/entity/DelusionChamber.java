package l2open.gameserver.model.entity;

import javolution.util.FastMap;
import l2open.gameserver.instancemanager.DimensionalRiftManager;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Location;

import java.util.Timer;
import java.util.TimerTask;

public class DelusionChamber extends DimensionalRift
{
	public DelusionChamber(L2Party party, int type, int room)
	{
		super(party, type, room);
	}

	@Override
	public void createNewKillRiftTimer()
	{
		if(killRiftTimerTask != null)
		{
			killRiftTimerTask.cancel();
			killRiftTimerTask = null;
		}

		if(killRiftTimer != null)
		{
			killRiftTimer.cancel();
			killRiftTimer = null;
		}

		killRiftTimer = new Timer();
		killRiftTimerTask = new TimerTask(){
			@Override
			public void run()
			{
				if(getParty() != null && getParty().getPartyMembers() != null)
				{
					for(L2Player p : getParty().getPartyMembers())
						if(p.getReflection() == DelusionChamber.this)
						{
							String var = p.getVar("backCoords");
							if(var == null || var.equals(""))
								continue;
							p.teleToLocation(new Location(var), 0);
							p.unsetVar("backCoords");
						}
					DelusionChamber.this.collapse();
				}
			}
		};

		killRiftTimer.schedule(killRiftTimerTask, 100);
	}

	@Override
	public void partyMemberExited(L2Player player)
	{
		if(getPlayersInside(false) < 2 || getPlayersInside(true) == 0)
		{
			createNewKillRiftTimer();
			return;
		}
	}

	@Override
	public void manualExitRift(L2Player player, L2NpcInstance npc)
	{
		if(!player.isInParty() || player.getParty().getReflection() != this)
			return;

		if(!player.getParty().isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "data/html/rift/NotPartyLeader.htm", npc);
			return;
		}

		createNewKillRiftTimer();
	}

	@Override
	public String getName()
	{
		FastMap<Integer, InstancedZoneManager.InstancedZone> izs = InstancedZoneManager.getInstance().getById(_roomType + 120);
		if (izs != null)
		{
			InstancedZoneManager.InstancedZone iz = izs.get(0);
			if (iz != null)
				return iz.getName();
		}
		return "Delusion Chamber";
	}

	@Override
	protected int getManagerId()
	{
		return 32664;
	}
}