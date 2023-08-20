package l2open.gameserver.ai;

import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.ai.L2PlayableAI.nextAction;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.cache.*;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.vehicle.L2Vehicle;
import l2open.gameserver.model.instances.L2MinionInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.CharMoveToLocation;
import l2open.gameserver.serverpackets.Die;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.StatusUpdate;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.SkillAbnormalType;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2Item;
import l2open.gameserver.xml.ItemTemplates;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.MinionList;
import l2open.util.Rnd;

import java.util.Arrays;

public class L2CharacterAI extends AbstractAI
{
	public L2CharacterAI(L2Character actor)
	{
		super(actor);
	}

	/** **/
	public int Floor = 5;
	public int clearer_mode = 0;
	public int isThemePark = -1;
	public int ROOM_ID = -1;
	public int LOC_ID = 0;
	public int ID = 0;
	public int ROOM = 0;
	public int id = 0;
	public int ROUTE = 0;
	public int LOC_NUMBER = 4;
	public int CONTROL_NUMBER = 0;
	public int BURNER_NUMBER = 0;

	/****/
	@Override
	protected void onIntentionIdle()
	{
		clientStopMoving();
		changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
	}

	@Override
	protected void onIntentionActive()
	{
		clientStopMoving();
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
	}

	@Override
	protected void onIntentionAttack(L2Character target)
	{
		//if(ConfigValue.DebugOnAction && getActor().isPlayer())
		//	_log.info("DebugOnAction: CHARACTER_AI:onIntentionAttack->onEvtThink");
		setAttackTarget(target);
		changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
		onEvtThink();
	}

	@Override
	protected void onIntentionCast(L2Skill skill, L2Character target, boolean NextActionCast)
	{
		setAttackTarget(target);
		changeIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		onEvtThink();
	}

	@Override
	protected void onIntentionFollow(L2Character target, Integer offset)
	{
		changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, target, offset);
		L2Character actor = getActor();
		if(actor != null)
			actor.followToCharacter(target, offset, false, ConfigValue.FollowFindPathType < 2);
	}

	@Override
	protected void onIntentionInteract(L2Object object, int type)
	{}

	@Override
	protected void onIntentionPickUp(L2Object item)
	{}

	@Override
	protected void onIntentionRest()
	{}

	@Override
	protected void onIntentionCoupleAction(L2Player player, Integer socialId)
	{}

	@Override
	protected void onEvtArrivedBlocked(Location blocked_at_pos)
	{
		L2Character actor = getActor();
		if(actor != null && actor.isPlayer())
		{
			if(ConfigValue.SetApperWall)
			{
				if(actor.isInRange(blocked_at_pos, 50))
					actor.setLoc(blocked_at_pos, true);
				else
				{
					Location loc = ((L2Player) actor).getLastServerPosition();
					if(loc != null)
						actor.setLoc(loc, true);
				}
				actor.broadcastPacket(new CharMoveToLocation(actor.getObjectId(), actor.getLoc(), actor.getLoc()));
				actor.stopMove(false, false, true, false);
			}
			else
			{
				// Приводит к застреванию в стенах:
				//if(actor.isInRange(blocked_at_pos, 1000))
				//	actor.setLoc(blocked_at_pos, true);
				// Этот способ надежнее:
				Location loc = ((L2Player) actor).getLastServerPosition();
				if(loc != null)
					actor.setLoc(loc, true);
				actor.stopMove();
			}
		}
		onEvtThink();
	}

	@Override
	protected void onEvtForgetObject(L2Object object)
	{
		L2Character actor = getActor();
		if(actor == null || object == null)
			return;

		if(actor.isPlayer() && actor.getPlayer().getPet() != null) // Убираем обьект с атаки и у пета/суммона.
			actor.getPlayer().getPet().getAI().onEvtForgetObject(object);

		if(actor.isAttackingNow() && getAttackTarget() == object)
			actor.abortAttack(true, true);

		if(actor.isCastingNow() && getAttackTarget() == object)
			actor.abortCast(true);

		if(getAttackTarget() == object)
			setAttackTarget(null);

		if(actor.getTargetId() == object.getObjectId())
			actor.setTarget(null);
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2Character actor = getActor();
		if(actor != null)
		{
			actor.abortAttack(true, true);
			actor.abortCast(true);
			actor.stopMove();
			actor.broadcastPacket(new Die(actor));
		}
		setIntention(CtrlIntention.AI_INTENTION_IDLE);
	}

	@Override
	protected void onEvtFakeDeath()
	{
		clientStopMoving();
		setIntention(CtrlIntention.AI_INTENTION_IDLE);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{
		L2Character actor = getActor();
		if(actor != null)
			actor.startAttackStanceTask();
		if(attacker != null)
			attacker.startAttackStanceTask();
	}

	@Override
	protected void CLAN_DIED(L2Character attacked_member, L2Character attacker)
	{}

	@Override
	protected void onEvtClanAttacked(L2Character attacked_member, L2Character attacker, int damage)
	{}
	
	@Override
	protected void PARTY_DIED(L2Character killer, L2Character party_member_died)
	{}
	
	@Override
	protected void PARTY_ATTACKED(L2Character attacker, L2Character party_member_attacked, int damage)
	{}

	public void Attack(L2Object target, boolean forceUse, boolean dontMove)
	{
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}

	public void Cast(L2Skill skill, L2Character target)
	{
		Cast(skill, target, false, false);
	}

	public void Cast(L2Skill skill, L2Character target, boolean forceUse, boolean dontMove)
	{
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}

	@Override
	protected void onEvtThink()
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}

	@Override
	protected void onEvtFinishCasting(L2Skill skill, L2Character actor, L2Character target)
	{}

	@Override
	protected void onEvtReadyToAct()
	{}

	@Override
	protected void onEvtArrived()
	{
		L2Character actor = getActor();
		if(actor != null && actor.isVehicle())
			((L2Vehicle) actor).VehicleArrived();
	}

	@Override
	protected void onEvtArrivedTarget(int i)
	{}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{}

	@Override
	protected void onEvtSpawn()
	{}

	public void stopAITask()
	{}

	public void startAITask()
	{}

	public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3)
	{}

	public void clearTasks()
	{}
	
	public void clearAttackTasks()
	{}

	public void clearNextAction()
	{}

	public void teleportHome(boolean clearAggro)
	{}

	public void checkAggression(L2Character target)
	{}

	public void SEE_CREATURE(L2Character target)
	{}

	public boolean isActive()
	{
		return true;
	}

	public boolean isGlobalAggro()
	{
		return true;
	}

	public boolean canSeeInSilentMove(L2Playable target)
	{
		return true;
	}
	
	public boolean canSeeInInvis(L2Playable target)
	{
		return true;
	}

	public void addTaskMove(Location loc, boolean pathfind)
	{}

	public void addTaskAttack(L2Character target)
	{}

	public void addTaskBuff(L2Character target, L2Skill skill)
	{}

	public void addTaskCast(L2Character target, L2Skill skill)
	{}

	public void AddUseSkillDesire(L2Character target, L2Skill skill, int weight)
	{}

	public void AddAttackDesire(L2Character target, int arg, int weight)
	{}

	public void AddAttackDesireEx(L2Character target, int arg1, int arg2, int weight)
	{}

	// ------------------------------ Методы с офф скриптов, что бы было проще с ними работать... --------------------------------
	@Override
	public void TIMER_FIRED_EX(int timerId, Object[] arg)
	{}
	
	@Override
	public void SPELL_SUCCESSED(L2Skill skill, L2Character actor)
	{}

	
	@Override
	public void ATTACK_FINISHED(L2Character target)
	{}

	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2)
	{}
	
	@Override
	public void SCRIPT_EVENT(int script_event_arg1, int script_event_arg2, int script_event_arg3)
	{}

	@Override
	public void NO_DESIRE()
	{}
	// ---------------------------------------------------------------------------------------------------------------------------
	public void AddTimerEx(int timerId, long delay)
	{
		ThreadPoolManager.getInstance().scheduleAI(new Timer(timerId, null), delay);
	}

	public void AddTimerEx(int timerId, Object[] arg, long delay)
	{
		ThreadPoolManager.getInstance().scheduleAI(new Timer(timerId, arg), delay);
	}

	protected class Timer extends RunnableImpl
	{
		private int _timerId;
		private Object[] _arg;

		public Timer(int timerId, Object[] arg)
		{
			_timerId = timerId;
			_arg = arg;
		}

		public void runImpl()
		{
			notifyEvent(CtrlEvent.EVT_TIMER, _timerId, _arg);
		}
	}
	
	public int Skill_InReuseDelay(int id)
	{
		return getActor().isSkillDisabled(ConfigValue.SkillReuseType == 0 ? id*65536L+1 : id) ? 1 : 0;
	}

	public double Skill_GetConsumeHP(int id)
	{
		return SkillTable.getInstance().getInfo(id, 1).getHpConsume();
	}

	public int Skill_GetConsumeHP(int id, int level)
	{
		return SkillTable.getInstance().getInfo(id, level).getHpConsume();
	}

	public int Skill_GetConsumeHP(L2Skill skill)
	{
		return skill.getHpConsume();
	}

	public double Skill_GetConsumeMP(int id)
	{
		L2Skill skill =  SkillTable.getInstance().getInfo(id, 1);
		return skill.getMpConsume();
	}

	public double Skill_GetConsumeMP(int id, int level)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(id, level);
		return skill.getMpConsume();
	}

	public double Skill_GetConsumeMP(L2Skill skill)
	{
		return skill.getMpConsume();
	}
	
	public void Despawn(L2Character myself)
	{
		myself.onDecay();
	}

	// Убежать от цели, бежать на растояние @value...
	public void AddFleeDesire(L2Character target, int value)
	{
		/*if(getActor().getNpcId() == 22323 || getActor().getNpcId() == 22659 || getActor().getNpcId() == 22658)
		{
			_log.info("AddFleeDesire["+getActor().getNpcId()+"]["+getActor().getObjectId()+"]: "+target+" value="+value);
			l2open.util.Util.test();
		}*/
		double angle = Math.toRadians(Rnd.get(-80, 80)+Location.calculateAngleFrom(target, getActor()));
		int oldX = getActor().getX();
		int oldY = getActor().getY();
		int x = oldX + (int)(400 * Math.cos(angle));
		int y = oldY + (int)(400 * Math.sin(angle));

		getActor().moveToLocation(GeoEngine.moveCheck(oldX, oldY, getActor().getZ(), x, y, getActor().getReflection().getGeoIndex()), 0, false);
		/**
		
		int posX = actor.getX();
		int posY = actor.getY();
		int posZ = actor.getZ();

		int signx = posX < attacker.getX() ? -1 : 1;
		int signy = posY < attacker.getY() ? -1 : 1;

		int range = 200;

		posX += Math.round(signx * range);
		posY += Math.round(signy * range);
		posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getReflection().getGeoIndex());

		if(GeoEngine.canMoveToCoord(attacker.getX(), attacker.getY(), attacker.getZ(), posX, posY, posZ, actor.getReflection().getGeoIndex()))
			addTaskMove(posX, posY, posZ, false);
		**/
	}

	// Ходить в течении @time по @value точек...
	public void AddMoveAroundDesire(final int time, final int value)
	{
		// Пускай временно будет отдельным потоком, Серый, напомни мне про эту херь чуток позже, что бы я ее снес и сделал по человечески...
		//for(int i = 0;i<time;i++)
		//{
			/*Location sloc = ((L2NpcInstance)getActor()).getSpawnedLoc();
			if(sloc == null)
				return;
			if(getActor().isMoving)
				return;
			int x = sloc.x + Rnd.get(-value, value);
			int y = sloc.y + Rnd.get(-value, value);
			int z = GeoEngine.getHeight(x, y, sloc.z, getActor().getReflection().getGeoIndex());
			getActor().setWalking();
			getActor().moveToLocation(x, y, z, 0, false);*/
			/*
			int oldX = getActor().getX();
			int oldY = getActor().getY();
			int x = oldX + Rnd.get(value);
			int y = oldY + Rnd.get(value);

			addTaskMove(GeoEngine.moveCheck(oldX, oldY, getActor().getZ(), x, y, getActor().getReflection().getGeoIndex()), false);
			*/
			/*try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e){}*/
		//}
	}

	// следовать за обьектом на ростоянии(или в течении я так и не понял)...
	public void AddFollowDesire(L2Character target, int value)
	{
		getActor().setFollowTarget(target);
		setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target, 120);

	}

	public void CreatePrivates(int... ids)
	{
		for(int id : ids)
		{
			MinionList list = ((L2MonsterInstance)getActor()).getMinionList();
			Location loc = getActor().getLoc();
			loc.set(loc.x + Rnd.get(150), loc.y + Rnd.get(150), loc.z);
			L2MinionInstance newMinion = new L2MinionInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(id), ((L2MonsterInstance)getActor()));
			newMinion.setSpawnedLoc(loc);
			newMinion.onSpawn();
			newMinion.spawnMe(loc);
			if(list != null)
				list.addSpawnedMinion(newMinion);
		}
	}

	// AddMoveToDesire(getMyLeader().getX(), getMyLeader().getY(), getMyLeader().getZ(),1000000);
	public void AddMoveToDesire(int x, int y, int z, int value)
	{
		getActor().moveToLocation(new Location(x, y, z), value, true);
	}

	public int IsNullCreature(L2Character target)
	{
		return target == null ? 1 : 0;
	}
	
	public int IsNullParty(L2Party target)
	{
		return target == null ? 1 : 0;
	}

	public void DropItem2(L2NpcInstance npc, int item_id, int item_count, L2Player player) // DropItem2(_npc,event_coin,1,(_atacker5 + ID));
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		item.setCount(item_count);
		item.dropToTheGround(player, npc);
	}
	
	public void DropItem1(L2NpcInstance npc, int item_id, int item_count) // DropItem1
	{
		L2ItemInstance item = ItemTemplates.getInstance().createItem(item_id);
		item.setCount(item_count);
		item.dropToTheGround(npc, npc.getLoc());
	}

	// Вызывает действие у АИ мобов которые попадают под радиус, не распростроняется на другие слои геодаты(верхнии или нижнеии этажи)
	public void BroadcastScriptEvent(int script_event_arg1, int script_event_arg2, int radius)
	{
		for(L2Character act : L2World.getAroundCharacters(getActor(), radius, 256))
			act.getAI().notifyEvent(CtrlEvent.SCR_EVENT, new Object[] {script_event_arg1, script_event_arg2, 0});
	}
	
	// Вызывает действие у АИ мобов которые попадают под радиус, не распростроняется на другие слои геодаты(верхнии или нижнеии этажи)
	public void BroadcastScriptEvent(int script_event_arg1, int script_event_arg2, int script_event_arg3,  int radius)
	{
		for(L2Character act : L2World.getAroundCharacters(getActor(), radius, 256))
			act.getAI().notifyEvent(CtrlEvent.SCR_EVENT, new Object[] {script_event_arg1, script_event_arg2, script_event_arg3});
	}

	// Вызывает действие у АИ мобов которые попадают под радиус, не распростроняется на другие слои геодаты(верхнии или нижнеии этажи)
	public void BroadcastScriptEventEx(int script_event_arg1, int script_event_arg2, int script_event_arg3,  int radius)
	{
		for(L2Character act : L2World.getAroundCharacters(getActor(), radius, 256))
			act.getAI().notifyEvent(CtrlEvent.SCR_EVENT, new Object[] {script_event_arg1, script_event_arg2, script_event_arg3});
	}

	// Вызывает действие у выбраной цели...
	public void SendScriptEvent(L2Character to, int arg1, int arg2)
	{
		if(to != null)
			to.getAI().notifyEvent(CtrlEvent.SCR_EVENT, new Object[] {arg1, arg2, 0});
	}

	// Вызывает действие у выбраной цели...
	public void SendScriptEvent(L2Character to, int arg1, int arg2, int arg3)
	{
		to.getAI().notifyEvent(CtrlEvent.SCR_EVENT, new Object[] {arg1, arg2, arg3});
	}
	
	public void Suicide(L2Character actor)
	{
		actor.doDie(null);
	}

	public L2Character GetCreatureFromID(int id)
	{
		return L2ObjectsStorage.getCharacter(id);
	}

	public int DistFromMe(L2Character actor)
	{
		return (int)getActor().getRealDistance3D(actor);
	}

	public void Say(int strId)
	{
		NpcSay ns = new NpcSay(((L2NpcInstance)getActor()), Say2C.NPC_ALL, strId);
		getActor().broadcastPacket2(ns);
	}

	public void Say(String strId)
	{
		NpcSay ns = new NpcSay(((L2NpcInstance)getActor()), Say2C.NPC_ALL, strId);
		getActor().broadcastPacket2(ns);
	}

	public void Shout(int strId)
	{
		NpcSay ns = new NpcSay(((L2NpcInstance)getActor()), Say2C.NPC_SHOUT, strId);
		getActor().broadcastPacket2(ns);
	}

	/**
	* Добавить предмет игроку
	* @param itemId
	* @param count
	**/
	public static L2ItemInstance GiveItem1(L2Player player, int itemId, long count)
	{
		if(player == null)
			return null;

		if(count <= 0)
			count = 1;

		L2Item template = ItemTemplates.getInstance().getTemplate(itemId);
		if(template == null)
			return null;

		L2ItemInstance ret = null;
		if(template.isStackable())
		{
			L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
			item.setCount(count);
			ret = player.getInventory().addItem(item);
			Log.LogItem(player, Log.Sys_GetItem, item);
		}
		else
			for(int i = 0; i < count; i++)
			{
				L2ItemInstance item = ItemTemplates.getInstance().createItem(itemId);
				ret = player.getInventory().addItem(item);
				Log.LogItem(player, Log.Sys_GetItem, item);
			}

		player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
		player.sendStatusUpdate(false, StatusUpdate.CUR_LOAD);
		return ret;
	}

	public int GetIndexFromCreature(L2Character arg0)
	{
		return arg0.getObjectId();
	}

	public L2Character GetCreatureFromIndex(int arg0)
	{
		return L2ObjectsStorage.getCharacter(arg0);
	}

	public String MakeFString(int msgId, String add1, String add2, String add3, String add4, String add5)
	{
		String t = FStringCache.getString(msgId);
		if(!add1.isEmpty())
			t = t.replace("$s1", add1);
		if(!add1.isEmpty())
			t = t.replace("$s2", add2);
		if(!add1.isEmpty())
			t = t.replace("$s3", add3);
		if(!add1.isEmpty())
			t = t.replace("$s4", add4);
		if(!add1.isEmpty())
			t = t.replace("$s5", add5);
		return t;
	}

	public void MakeAttackEvent(L2Character attacker, int damage, int i0)
	{
		notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
		changeIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker, null);
	}

	public int GetAbnormalLevel(L2Character target, SkillAbnormalType type)
	{
		return target.getEffectList().GetAbnormalLevel(type);
	}

	public SkillAbnormalType Skill_GetAbnormalType(L2Skill skill)
	{
	 	return skill.getAbnormalType();
	}

	public void StopMove(L2Character actor)
	{
		actor.stopMove(true, false);
	}

	public void RemoveAllDesire(L2Character actor)
	{
		actor.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	public void ChangeMoveType(int type)
	{
		if(type == 0)
			getActor().setWalking();
		else if (type == 1)
			getActor().setRunning();
	}

	public Location GetRandomPosInCreature(L2Object creature, int min_dist, int max_dist)
	{
		return creature.getLoc().rnd(min_dist, max_dist, false);
	}
	/**
	
	Узнать, находиться ли чар в заданной категории.
 Список категорий задается в файлах category_pch.txt и categorydata.txt
 Пример , здесь мы определяем , имеет ли чар 3 профу 
Код:
if( IsInCategory(8,talker.occupation) == 1 ){
...
}
	**/
	public boolean is_pts = false;
	public void MENU_SELECTED(L2Player talker, int ask, int reply){}
	public void TALKED(L2Player talker, int _code, int _from_choice){}
}