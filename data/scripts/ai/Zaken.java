package ai;

import bosses.ZakenManager;
import javolution.util.FastMap;
import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.database.mysql;
import l2open.extensions.listeners.DayNightChangeListener;
import l2open.extensions.listeners.L2ZoneEnterLeaveListener;
import l2open.extensions.listeners.PropertyCollection;
import l2open.gameserver.GameTimeController;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.InstancedZoneManager;
import l2open.gameserver.instancemanager.InstancedZoneManager.InstancedZone;
import l2open.gameserver.instancemanager.ZoneManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.ExSendUIEvent;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.tables.NpcTable;
import l2open.util.Location;
import l2open.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Индивидуальное АИ эпик боса Zaken.
 * - Спаунит каждые 2 минуты, 3 моба (охрану).
 * - имеет усиленный реген ночью<BR>
 * - получает 25% пенальти на реген в солнечной комнате
 * - после смерти проигрывает музыку<BR>
 */
public class Zaken extends DefaultAI
{
	//Npc Day and Night
	private static final int[] mobs = { 29023, 29024, 29026, 29027 };
	//Npc Day High
	private static final int[] _mobs = { 29182, 29183, 29184, 29185 };
	//Skills
	private final int FaceChanceNightToDay = 4223;
	private final int FaceChanceDayToNight = 4224;
	private final L2Skill AbsorbHPMP;
	private final L2Skill Hold;
	private final L2Skill DeadlyDualSwordWeapon;
	private final L2Skill DeadlyDualSwordWeaponRangeAttack;
	//Other
	private final float _baseHpReg;
	private final float _baseMpReg;
	private boolean _isInLightRoom = false;	
	//Listener
	private static final L2Zone _zone = ZoneManager.getInstance().getZoneById(ZoneType.no_restart, 1335, true);
	private ZoneListener _zoneListener = new ZoneListener();
	private NightInvulDayNightListener _timeListener = new NightInvulDayNightListener();
    private boolean spawn = false;

    public Zaken(L2Character actor)
	{
		super(actor);

		HashMap<Integer, L2Skill> skills = getActor().getTemplate().getSkills();

		AbsorbHPMP = skills.get(4218);
		Hold = skills.get(4219);
		DeadlyDualSwordWeapon = skills.get(4220);
		DeadlyDualSwordWeaponRangeAttack = skills.get(4221);

		_baseHpReg = actor.getTemplate().baseHpReg;
		_baseMpReg = actor.getTemplate().baseMpReg;
	}

    @Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

        int instanceId = actor.getReflection().getInstancedZoneId();
        if(!spawn)
        {
            if(instanceId == 114 || instanceId == 133)
            {
                spawnGuards(mobs[Rnd.get(mobs.length)]);
				spawnGuards(mobs[Rnd.get(mobs.length)]);
				spawnGuards(mobs[Rnd.get(mobs.length)]);
                spawn = true;
                ThreadPoolManager.getInstance().schedule(new SpawnTask(), 120000);
            }
            if(instanceId == 135)
            {
                spawnGuards(_mobs[Rnd.get(_mobs.length)]);
				spawnGuards(_mobs[Rnd.get(mobs.length)]);
				spawnGuards(_mobs[Rnd.get(mobs.length)]);
                spawn = true;
                ThreadPoolManager.getInstance().schedule(new SpawnTask(), 120000);
            }
        }
		super.thinkAttack();
	}

    private void spawnGuards(int mob)
    {
        L2NpcInstance actor = getActor();
        ZakenManager.ZakenInstanceInfo instanceInfo = ZakenManager.instances.get(actor.getReflectionId());
        Location loc = instanceInfo.getZakenLoc();
        Location pos = GeoEngine.findPointToStay(loc.x, loc.y, loc.z, 200, 200, actor.getReflection().getGeoIndex());
		try
		{
			L2Spawn spawn = new L2Spawn(NpcTable.getTemplate(mob));
			actor.getReflection().addSpawn(spawn);
			spawn.setReflection(actor.getReflectionId());
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

    @Override
	protected boolean createNewTask()
	{
		clearTasks();
		L2Character target;
		if((target = prepareTarget()) == null)
			return false;

		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return false;

		int rnd_per = Rnd.get(100);

		double distance = actor.getDistance(target);

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, DeadlyDualSwordWeapon);
		addDesiredSkill(d_skill, target, distance, DeadlyDualSwordWeaponRangeAttack);
		addDesiredSkill(d_skill, target, distance, Hold);
		addDesiredSkill(d_skill, target, distance, AbsorbHPMP);

		L2Skill r_skill = selectTopSkill(d_skill);

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	/**
	 * Метод вызываемый при смерте закена.
	 */
	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		actor.broadcastPacket(new PlaySound(1, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));

        L2Player player = killer.getPlayer();
		FastMap<Integer, InstancedZone> izs = InstancedZoneManager.getInstance().getById(getActor().getReflection().getInstancedZoneId());
		if(izs == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}
		InstancedZone iz = izs.get(0);
		if(iz == null)
		{
			player.sendPacket(Msg.SYSTEM_ERROR);
			return;
		}
		Reflection r = player.getReflection();
        L2Party party = player.getParty();

		if(player.getAttainment() != null)
		{
			if(party != null)
			{
				List<String> hwid = new ArrayList<String>();
				for(L2Player member : party.getPartyMembers())
					if(member.getReflection() == r && member.getLevel() >= 85 && !hwid.contains(member.getHWIDs()))
					{
						member.getAttainment().setKillZaken();
						hwid.add(member.getHWIDs());
					}
				hwid.clear();
			}
			else
				player.getAttainment().setKillZaken();
		}

		if(actor.getNpcId() == 29181)
		{
			if(party != null)
			{
				ZakenManager.ZakenInstanceInfo instanceInfo = ZakenManager.instances.get(actor.getReflectionId());
				long i5 = (instanceInfo.getTimer() - System.currentTimeMillis())/1000;
				L2CommandChannel cc = party.getCommandChannel();
				if(cc != null)
				{
					for(L2Player member : cc.getMembers()) 
					{
						//SendUIEvent(member,1,0,0,"1","1","1","Затраченное время : ","60","0");
						if(member.getLevel() >= ConfigValue.NeadLevelToZeken)
							if(i5 <= 5 * 60)
							{
								if(Rnd.get(100) < 50)
									GiveItem1(member,15763,1);
							}
							else if(i5 <= 10 * 60)
							{
								if(Rnd.get(100) < 30)
									GiveItem1(member,15764,1);
							}
							else if(i5 <= 15 * 60)
							{
								if(Rnd.get(100) < 25)
									GiveItem1(member,15763,1);
							}
					}
				}
				else
				{
					for(L2Player member : party.getPartyMembers())
					{
						//SendUIEvent(member,1,0,0,"1","1","1","Затраченное время : ","60","0");
						if(member.getLevel() >= ConfigValue.NeadLevelToZeken)
							if(i5 <= 5 * 60)
							{
								if(Rnd.get(100) < 50)
									GiveItem1(member,15763,1);
							}
							else if(i5 <= 10 * 60)
							{
								if(Rnd.get(100) < 30)
									GiveItem1(member,15764,1);
							}
							else if(i5 <= 15 * 60)
							{
								if(Rnd.get(100) < 25)
									GiveItem1(member,15763,1);
							}
					}
				}
			}
		}
		String time = String.valueOf(System.currentTimeMillis());
		int world_id = actor.getReflectionId();
        //Задаём рефлект всем кто был в инстанте.
		try
		{
			if(ZakenManager.getMembersCC().size() > 0)
			{
				for(Integer objId : ZakenManager.getMembersCC().get(world_id))
				{
					L2Player member = L2ObjectsStorage.getPlayer(objId);
					if(member != null)
					{
						member.setVarInst(actor.getReflection().getName(), time);
						member.sendPacket(new ExSendUIEvent(member, true, true, 0, 10, ""));
					}
					else
						mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", objId, actor.getReflection().getName(), time);
				}
			}
		}
		catch(NullPointerException e)
		{
			_log.warning("ZakenAI(260) actor="+(actor != null)+", player="+(player != null)+", getMembersCC="+(ZakenManager.getMembersCC() != null)+", ref="+(actor.getReflection() != null)+", p_ref="+(player.getReflection() != null));
		}
		ZakenManager.getMembersCC().remove(world_id);
		try
		{
			if(ZakenManager.getMembersParty().size() > 0)
			{
				for(Integer objId : ZakenManager.getMembersParty().get(world_id))
				{
					L2Player member = L2ObjectsStorage.getPlayer(objId);
					if(member != null)
					{
						member.setVarInst(actor.getReflection().getName(), time);
						member.sendPacket(new ExSendUIEvent(member, true, true, 0, 10, ""));
					}
					else
						mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,-1)", objId, actor.getReflection().getName(), time);
				}
			}
		}
		catch(NullPointerException e)
		{

			_log.warning("ZakenAI(277) actor="+(actor != null)+", player="+(player != null)+", getMembersParty="+(ZakenManager.getMembersParty() != null)+", ref="+(actor.getReflection() != null)+", p_ref="+(player.getReflection() != null));
		}
		ZakenManager.getMembersParty().remove(world_id);
		r.clearReflection(10, true);
        ZakenManager.instances.remove(r.getId());
		super.MY_DYING(killer);
	}

	/**
	 * Запуск АИ
	 */
	@Override
	public void startAITask()
	{
		if(_aiTask == null)
		{
			GameTimeController.getInstance().getListenerEngine().addPropertyChangeListener(PropertyCollection.GameTimeControllerDayNightChange, _timeListener);
			_zone.getListenerEngine().addMethodInvokedListener(_zoneListener);
		}
		super.startAITask();
	}

	/**
	 * Остановка АИ.
	 */
	@Override
	public void stopAITask()
	{
		if(_aiTask != null)
		{
			GameTimeController.getInstance().getListenerEngine().removePropertyChangeListener(PropertyCollection.GameTimeControllerDayNightChange, _timeListener);
			_zone.getListenerEngine().removeMethodInvokedListener(_zoneListener);
		}
		super.stopAITask();
	}

	/**
	 * Листенер времени суток. (День, ночь)
	 */
	private class NightInvulDayNightListener extends DayNightChangeListener
	{
		private NightInvulDayNightListener()
		{
			if(GameTimeController.getInstance().isNowNight())
				switchToNight();
			else
				switchToDay();
		}

		/**
		 * Вызывается, когда на сервере наступает ночь
		 */
		@Override
		public void switchToNight()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
			{
				if(_isInLightRoom)
				{
					actor.getTemplate().baseHpReg = (float) (_baseHpReg * 7.5);
					actor.getTemplate().baseMpReg = (float) (_baseMpReg * 7.5);
				}
				else
				{
					actor.getTemplate().baseHpReg = (float) (_baseHpReg * 10.);
					actor.getTemplate().baseMpReg = (float) (_baseMpReg * 10.);
				}
				actor.broadcastSkill(new MagicSkillUse(actor, actor, FaceChanceDayToNight, 1, 1100, 0));
			}
		}

		/**
		 * Вызывается, когда на сервере наступает день
		 */
		@Override
		public void switchToDay()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
			{
				actor.getTemplate().baseHpReg = _baseHpReg;
				actor.getTemplate().baseMpReg = _baseMpReg;
				actor.broadcastSkill(new MagicSkillUse(actor, actor, FaceChanceNightToDay, 1, 1100, 0));
			}
		}
	}
	
	/**
	 * Листенер зон (вход - выход).
	 */
	private class ZoneListener extends L2ZoneEnterLeaveListener
	{
		@Override
		public void objectEntered(L2Zone zone, L2Object object)
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			actor.getTemplate().baseHpReg = (float) (_baseHpReg * 0.75);
			actor.getTemplate().baseMpReg = (float) (_baseMpReg * 0.75);
			_isInLightRoom = true;
		}

		@Override
		public void objectLeaved(L2Zone zone, L2Object object)
		{
			L2NpcInstance actor = getActor();
			if(actor == null)
				return;
			actor.getTemplate().baseHpReg = _baseHpReg;
			actor.getTemplate().baseMpReg = _baseMpReg;
			_isInLightRoom = false;
		}
	}

    private class SpawnTask extends l2open.common.RunnableImpl {

        public SpawnTask() {
        }

        @Override
        public void runImpl() {
            spawn = false;
        }
    }
}