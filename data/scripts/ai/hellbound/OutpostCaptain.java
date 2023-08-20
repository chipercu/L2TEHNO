package ai.hellbound;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.ai.L2CharacterAI;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.HellboundManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.tables.DoorTable;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;

public class OutpostCaptain extends Fighter
{
	private int EnceinteDefender = 22358;
	private static boolean _say = false;
	private static boolean _say1 = false;
	private static boolean _spawn = false;

	public OutpostCaptain(L2Character actor)
	{
		super(actor);
	}

	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
			return;
		
		_say = false;
		_say1 = false;
		super.thinkAttack();	
	}
	
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
		{
			return true;
		}

		for (L2Player pc : L2World.getAroundPlayers(actor, 2000, 2000))
		{
			if (_say == false)
			{
				if (actor.getDistance(pc) >= 510 && actor.getDistance(pc) <= 1000)
				{
					Functions.npcSay(actor, "Please help me... Come to me!");
					_say = true;
				}
			}
			if (actor.getDistance(pc) <= 200)
			{
				actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
				if (_say1 == false)
				{
					Functions.npcSay(actor, "I have deceived you! Now you will die!");
					actor.doCast(SkillTable.getInstance().getInfo(5306, 4), pc, true);
					if(_spawn == false)
					{
						try
						{
							Location pos1 = GeoEngine.findPointToStay(5231, 243750, -1925, 100, 120, actor.getReflection().getGeoIndex());
							Location pos2 = GeoEngine.findPointToStay(4866, 244463, -1925, 100, 120, actor.getReflection().getGeoIndex());
							Location pos3 = GeoEngine.findPointToStay(4680, 244813, -1593, 100, 120, actor.getReflection().getGeoIndex());
							Location pos4 = GeoEngine.findPointToStay(4788, 244792, -1589, 100, 120, actor.getReflection().getGeoIndex());
							Location pos5 = GeoEngine.findPointToStay(5453, 243425, -1586, 100, 120, actor.getReflection().getGeoIndex());
							Location pos6 = GeoEngine.findPointToStay(5496, 243558, -1588, 100, 120, actor.getReflection().getGeoIndex());
							L2Spawn sp1 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							L2Spawn sp2 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							L2Spawn sp3 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							L2Spawn sp4 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							L2Spawn sp5 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							L2Spawn sp6 = new L2Spawn(NpcTable.getTemplate(EnceinteDefender));
							sp1.setLoc(pos1);
							sp2.setLoc(pos2);
							sp3.setLoc(pos3);
							sp4.setLoc(pos4);
							sp5.setLoc(pos5);
							sp6.setLoc(pos6);
							L2NpcInstance npc1 = sp1.doSpawn(true);
							L2NpcInstance npc2 = sp2.doSpawn(true);
							L2NpcInstance npc3 = sp3.doSpawn(true);
							L2NpcInstance npc4 = sp4.doSpawn(true);
							L2NpcInstance npc5 = sp5.doSpawn(true);
							L2NpcInstance npc6 = sp6.doSpawn(true);
							npc1.setRunning();
							npc2.setRunning();
							npc3.setRunning();
							npc4.setRunning();
							npc5.setRunning();
							npc6.setRunning();
							npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							npc2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							npc3.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							npc4.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							npc5.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							npc6.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, pc, 5000);
							_spawn = true;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					_say1 = true;
				}
			}
		}
		if (_def_think)
		{
			doTask();
			return true;
		}

		return false;
	}

	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		int curHBLevel = HellboundManager.getInstance().getLevel();
		if (curHBLevel == 8)
		{
			HellboundManager.getInstance().changeLevel(9);
			DoorTable.getInstance().getDoor(20250001).openMe();
		}
		super.MY_DYING(killer);
	}
}