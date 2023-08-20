package npc.model;

import bosses.ZakenManager;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;

/**
 * @author : Ragnarok
 * @date : 13.01.11    18:32
 */
public class ZakenKandleInstance extends L2NpcInstance
{
    public ZakenKandleInstance(int objectId, L2NpcTemplate template)
	{
        super(objectId, template);
    }

    @Override
    public void onAction(L2Player player, boolean shift, int addDist)
	{
        if (!isInRange(player, INTERACTION_DISTANCE+addDist))
		{
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
            return;
        }
        if (ZakenManager.instances.get(getReflectionId()) != null)
		{
            ZakenManager.ZakenInstanceInfo instanceInfo = ZakenManager.instances.get(getReflectionId());
            int instanceId = getReflection().getInstancedZoneId();
            if(player.getParty() == null)
			{
                player.sendActionFailed();
                return;
            }

            int id = 0;
            if (instanceInfo.getZakenLoc().z == getLoc().z && instanceInfo.getZakenLoc().distance(getLoc()) < 1200)
			{
                if (!instanceInfo.getBlueKandles().contains(getObjectId()))
				{
                    id = 15302;
                    instanceInfo.getBlueKandles().add(getObjectId());
                    if (instanceInfo.getBlueKandles().size() == 4)
					{
                        try
						{
                            L2Spawn spawn = new L2Spawn(instanceInfo.getZakenId());
                            getReflection().addSpawn(spawn);
                            spawn.setReflection(getReflectionId());
                            spawn.setRespawnDelay(0, 0);
                            spawn.setLocation(0);
                            spawn.setLoc(instanceInfo.getZakenLoc());
                            L2NpcInstance zaken = spawn.doSpawn(true);
                            ZakenManager.calcZakenStat(zaken, getReflectionId());
                            spawn.stopRespawn();
                        }
						catch (ClassNotFoundException e)
						{
                            e.printStackTrace();
                        }
                    }
                }
            }
			else if (!instanceInfo.getRedKandles().contains(getObjectId()))
			{
                id = 15281;
                instanceInfo.getRedKandles().add(getObjectId());
                // Спаун охраны в ближайшей комнате
                int room = Integer.MAX_VALUE;
                double distance = Double.MAX_VALUE;
                for (int i = 0; i < ZakenManager.ROOM_CENTER_COORDS.length; i++)
				{
                    if (getLoc().z == ZakenManager.ROOM_CENTER_COORDS[i][2] && getLoc().distance(new Location((ZakenManager.ROOM_CENTER_COORDS[i]))) < distance)
					{
                        room = i;
                        distance = getLoc().distance(new Location((ZakenManager.ROOM_CENTER_COORDS[i])));
                    }
                }
                if(instanceId == 114 || instanceId == 133)
                    spawnGuards(room, 29023, 29023, 29024, 29024, 29024, 29026, 29026, 29026, 29027, 29027);

                if(instanceId == 135)
                    spawnGuards(room, 29182, 29182, 29183, 29183, 29183, 29184, 29184, 29184, 29185, 29185);
            }

            if (id != 0)
			{
				if(ConfigValue.EnableLindvior)
					setNpcState(1);
				setRHandId(15280);//Анимация зажигания
                updateAbnormalEffect();// отсылка NpcInfo всем вокруг
                ThreadPoolManager.getInstance().schedule(new KandleFireTask(this, id), 3000);
            }
        }
        player.sendActionFailed();
    }

	public void spawnGuards(int room, int... ids)
	{
		Location loc = new Location(ZakenManager.ROOM_CENTER_COORDS[room]);
        for(int id : ids)
		{
            Location pos = GeoEngine.findPointToStay(loc.x, loc.y, loc.z, 200, 200, getReflection().getGeoIndex());
            try
            {
                L2Spawn spawn = new L2Spawn(id);
                getReflection().addSpawn(spawn);
                spawn.setReflection(getReflectionId());
                spawn.setRespawnDelay(0, 0);
                spawn.setLocation(0);
                spawn.setLoc(pos);
                spawn.doSpawn(true);
                spawn.stopRespawn();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
	}

    class KandleFireTask extends l2open.common.RunnableImpl
	{
        ZakenKandleInstance npc;
        int id;

        public KandleFireTask(ZakenKandleInstance npc, int id)
		{
            this.npc = npc;
            this.id = id;
        }

        @Override
        public void runImpl()
		{
			if(ConfigValue.EnableLindvior)
			{
				if(id == 15281)
					npc.setNpcState(2);
				else
					npc.setNpcState(3);
			}

			npc.setRHandId(id);		
			npc.updateAbnormalEffect();
        }
    }
}
