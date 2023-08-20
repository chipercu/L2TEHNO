package npc.model;

import javolution.util.FastMap;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author pchayka
 */

public final class GruffManInstance extends L2NpcInstance
{
	private static final int elcardiaIzId = 158;

	public GruffManInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("elcardia_enter"))
		{
			enterInstance(player, elcardiaIzId);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void enterInstance(L2Player player, int instancedZoneId)
	{
	
		InstancedZoneManager ilm = InstancedZoneManager.getInstance();
		FastMap<Integer, InstancedZone> ils = ilm.getById(instancedZoneId);
		
		if(ils == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}

		InstancedZone il = ils.get(0);

		assert il != null;

		Reflection r = new Reflection(il.getName());
		r.setInstancedZoneId(instancedZoneId);
		for(InstancedZone i : ils.values())
		{
			if(r.getReturnLoc() == null)
				r.setReturnLoc(i.getReturnCoords());
			if(r.getTeleportLoc() == null)
				r.setTeleportLoc(i.getTeleportCoords());
			r.FillSpawns(i.getSpawnsInfo());
		}

		int timelimit = il.getTimelimit();

		player.setReflection(r);
		player.teleToLocation(89807, -238074, -9632);
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.startCollapseTimer(timelimit * 60 * 1000L);
	}
}