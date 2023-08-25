package npc.model;

import javolution.util.FastMap;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Zone;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.EventTrigger;
import l2open.gameserver.serverpackets.ExStartScenePlayer;
import l2open.gameserver.templates.L2NpcTemplate;

/**
 * @author pchayka
 */

public final class OddGlobeInstance extends L2NpcInstance
{
	private static final int instancedZoneId = 155;

	public OddGlobeInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("monastery_enter"))
		{
			enterInstance(player, instancedZoneId);

			ZoneListener zoneL = new ZoneListener();
			L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.dummy, 800000);
			if(_zone != null)
				_zone.getListenerEngine().addMethodInvokedListener(zoneL);

			ZoneListener2 zoneL2 = new ZoneListener2();
			L2Zone _zone2 = ZoneManager.getInstance().getZoneById(ZoneType.dummy, 800001);
			if(_zone2 != null)
				_zone2.getListenerEngine().addMethodInvokedListener(zoneL2);
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
			r.FillDoors(i.getDoors());
		}

		int timelimit = il.getTimelimit();

		player.setReflection(r);
		player.teleToLocation(120664, -86968, -3392);
		player.setVar("backCoords", r.getReturnLoc().toXYZString());

		r.startCollapseTimer(timelimit * 60 * 1000L);
	}

	public class ZoneListener extends L2ZoneEnterLeaveListener
	{
		private boolean done = false;

		@Override
		public void objectEntered(L2Zone zone, L2Object cha)
		{
			L2Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer() || done)
				return;
			done = true;
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_HOLY_BURIAL_GROUND_OPENING);
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object cha)
		{
		}
	}

	public class ZoneListener2 extends L2ZoneEnterLeaveListener
	{
		private boolean done = false;

		@Override
		public void objectEntered(L2Zone zone, L2Object cha)
		{
			L2Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer())
				return;
			player.broadcastPacket(new EventTrigger(21100100, true));
			if(!done)
			{
				done = true;
				player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_OPENING);
			}
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object cha)
		{
		}
	}
}