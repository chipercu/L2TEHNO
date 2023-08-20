package ai;

import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

public class Lematan extends Fighter
{
	private static int LEMATAN_FOLLOWER = 18634;
	private boolean _teleported;
	private boolean _onBoat = false;

	public Lematan(L2Character actor)
	{
		super(actor);
		_teleported = false;
	}

	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	public boolean isNotReturnHome()
	{
		return true;
	}

	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2NpcInstance actor = getActor();
		boolean part = actor.getCurrentHp() - damage < actor.getMaxHp() / 2;
		if ((part) && (!_teleported))
		{
			for(L2NpcInstance npc : L2World.getAroundNpc(actor, 5000, 1000))
			{
				if(npc.getNpcId() == 18606 && DistFromMe(npc) <= 800)
					_onBoat = true;
			}
			Location loc = new Location(84968, -208728, -3367);
			actor.setSpawnedLoc(loc);
			actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
			actor.teleToLocation(loc);
			_teleported = true;
			if(!_onBoat)
			{
				actor.doCast(SkillTable.getInstance().getInfo(5756, 1), actor, true);
				try
				{
					L2Spawn spawn1 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn2 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn3 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn4 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn5 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn6 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn7 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					L2Spawn spawn8 = new L2Spawn(NpcTable.getTemplate(LEMATAN_FOLLOWER));
					Location pos1 = GeoEngine.findPointToStay(84712, -208728, -3365, 0, 0, actor.getReflection().getGeoIndex());
					Location pos2 = GeoEngine.findPointToStay(85240, -208744, -3364, 0, 0, actor.getReflection().getGeoIndex());
					Location pos3 = GeoEngine.findPointToStay(84584, -208936, -3361, 0, 0, actor.getReflection().getGeoIndex());
					Location pos4 = GeoEngine.findPointToStay(84552, -208536, -3361, 0, 0, actor.getReflection().getGeoIndex());
					Location pos5 = GeoEngine.findPointToStay(84552, -208728, -3361, 0, 0, actor.getReflection().getGeoIndex());
					Location pos6 = GeoEngine.findPointToStay(85400, -208936, -3361, 0, 0, actor.getReflection().getGeoIndex());
					Location pos7 = GeoEngine.findPointToStay(85400, -208536, -3361, 0, 0, actor.getReflection().getGeoIndex());
					Location pos8 = GeoEngine.findPointToStay(85416, -208776, -3361, 0, 0, actor.getReflection().getGeoIndex());
					spawn1.setReflection(actor.getReflection().getId());
					spawn2.setReflection(actor.getReflection().getId());
					spawn3.setReflection(actor.getReflection().getId());
					spawn4.setReflection(actor.getReflection().getId());
					spawn5.setReflection(actor.getReflection().getId());
					spawn6.setReflection(actor.getReflection().getId());
					spawn7.setReflection(actor.getReflection().getId());
					spawn8.setReflection(actor.getReflection().getId());
					spawn1.setLoc(pos1);
					spawn2.setLoc(pos2);
					spawn3.setLoc(pos3);
					spawn4.setLoc(pos4);
					spawn5.setLoc(pos5);
					spawn6.setLoc(pos6);
					spawn7.setLoc(pos7);
					spawn8.setLoc(pos8);
					spawn1.setRespawnDelay(1);
					spawn2.setRespawnDelay(1);
					spawn3.setRespawnDelay(1);
					spawn4.setRespawnDelay(1);
					spawn5.setRespawnDelay(1);
					spawn6.setRespawnDelay(1);
					spawn7.setRespawnDelay(1);
					spawn8.setRespawnDelay(1);
					spawn1.doSpawn(true);
					spawn2.doSpawn(true);
					spawn3.doSpawn(true);
					spawn4.doSpawn(true);
					spawn5.doSpawn(true);
					spawn6.doSpawn(true);
					spawn7.doSpawn(true);
					spawn8.doSpawn(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		super.ATTACKED(attacker, damage, skill);
	}
}