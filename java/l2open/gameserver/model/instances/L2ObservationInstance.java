package l2open.gameserver.model.instances;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;

import java.util.StringTokenizer;

public final class L2ObservationInstance extends L2NpcInstance
{
	public L2ObservationInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		// first do the common stuff and handle the commands that all NPC classes know
		super.onBypassFeedback(player, command);

		if(player.getOlympiadGame() != null || player.isTerritoryFlagEquipped())
			return;

		if(command.startsWith("observeSiege"))
		{
			String val = command.substring(13);
			StringTokenizer st = new StringTokenizer(val);
			st.nextToken(); // Bypass cost

			if(ZoneManager.getInstance().checkIfInZone(ZoneType.Siege, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 0))
				doObserve(player, val);
			else
				player.sendPacket(Msg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
		}
		else if(command.startsWith("observe"))
			doObserve(player, command.substring(8));
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		return "data/html/observation/" + pom + ".htm";
	}

	private void doObserve(L2Player player, String val)
	{
		if(player.getPet() != null)
			player.getPet().unSummon();
		StringTokenizer st = new StringTokenizer(val);
		int cost = Integer.parseInt(st.nextToken());
		if(player.getAdena() < cost)
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else if(player.enterObserverMode(new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())), null))
			player.reduceAdena(cost, true);
		player.sendActionFailed();
	}
}