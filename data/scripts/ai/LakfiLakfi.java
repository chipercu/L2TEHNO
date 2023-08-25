package ai;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Object;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.*;
import l2open.util.Rnd;

public class LakfiLakfi extends DefaultAI
{
	private static final int MAX_RADIUS = 500;
	private static final L2Skill s_display_bug_of_fortune1 = SkillTable.getInstance().getInfo(6045, 1);
	private static final L2Skill s_display_jackpot_firework = SkillTable.getInstance().getInfo(5778, 1);

	private long _nextEat;
	private int i_ai2, actor_lvl, prev_st;
	private boolean _firstSaid;
	public LakfiLakfi(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		
		L2NpcInstance actor = getActor();
		
		AddTimerEx(7778, 1000);
		
		if(getFirstSpawned(actor))
		{
			i_ai2 = 0;
			prev_st = 0;
		}	
		else
		{
			i_ai2 = 3;
			prev_st = 3;
		}
		_firstSaid = false;

		actor_lvl = actor.getLevel();
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
			
		if(i_ai2 > 9)	
		{
			if(!_firstSaid)
			{
				Functions.npcSayInRange(actor, 600, 1800289);
				_firstSaid = true;
			}	
			return;
		}	
		L2ItemInstance closestItem = null;
		if(_nextEat < System.currentTimeMillis())
		{
			for(L2Object obj : L2World.getAroundObjects(actor, 20, 200))
				if(obj.isItem() && ((L2ItemInstance) obj).getItemId() == 57)
					closestItem = (L2ItemInstance) obj;

			if(closestItem != null && closestItem.getCount() >= ConfigValue.MinAdenaLakfiEat)
			{
				closestItem.deleteMe();
				actor.altUseSkill(s_display_bug_of_fortune1, actor);
				Functions.npcSayInRange(actor, 600, 1800291);
				_firstSaid = false;
				
				if(i_ai2 == 2 && getFirstSpawned(actor))
				{
					L2NpcInstance npc = NpcTable.getTemplate(getCurrActor(actor)).getNewInstance();
					npc.setLevel(actor.getLevel());
					npc.setSpawnedLoc(actor.getLoc());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					actor.doDie(actor);
					actor.deleteMe();
					AddTimerEx(1500, ConfigValue.TimeIfNotFeedDissapear*60000);
				}
					
				i_ai2++;
				
				_nextEat = System.currentTimeMillis() + ConfigValue.IntervalBetweenEating*1000;
			}
			
			else if(closestItem != null && closestItem.getCount() < ConfigValue.MinAdenaLakfiEat && !_firstSaid)
			{
				Functions.npcShout(actor, "Is this all? I want More!!! I won't eat below "+ConfigValue.MinAdenaLakfiEat+" Adena!!!");
				_firstSaid = true;
			}	
		}
	}

	private boolean getFirstSpawned(L2NpcInstance actor)
	{
		if(actor.getNpcId() == 2503 || actor.getNpcId() == 2502)
			return false;
		return true;	
	}
	private int getCurrActor(L2NpcInstance npc)
	{
		if(Rnd.chance(ConfigValue.GoldLakfiChance))
			return 2503;
		return 2502;	
	
	}
	@Override
	protected boolean thinkActive()
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead())
			return true;

		if(!actor.isMoving && _nextEat < System.currentTimeMillis())
		{
			L2ItemInstance closestItem = null;
			for(L2Object obj : L2World.getAroundObjects(actor, MAX_RADIUS, 200))
				if(obj.isItem() && ((L2ItemInstance) obj).getItemId() == 57)
					closestItem = (L2ItemInstance) obj;
			if(closestItem != null)
				actor.moveToLocation(closestItem.getLoc(), 0, true);
		}
		return false;
	}

	public int getChance(int stage)
	{
		switch(stage)
		{
			case 4:
				return 10;
			case 5:
				return 20;
			case 6:
				return 40;
			case 7:
				return 60;
			case 8:
				return 70;
			case 9:
				return 80;
			case 10:
				return 100;
			default:
				return 0;
		}
	}

	@Override
	protected void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(killer != null)
		{
			if(i_ai2 >= 0 && i_ai2 < 3)
			{
				Functions.npcSayInRange(actor, 600, 1800290);
				return;
			}	
			else
				actor.broadcastSkill(new MagicSkillUse(actor, s_display_jackpot_firework.getId(), 1, s_display_jackpot_firework.getHitTime(), 0));
			if(Rnd.chance(getChance(i_ai2)))
			{
				int random = Rnd.get(0,100);
				if(actor_lvl == 52)
				{
					if(actor.getNpcId() == 2502)
					{
						if(random <= 50)
							actor.dropItem(killer.getPlayer(), 8755, 1);
						else
							actor.dropItem(killer.getPlayer(), 8755, 2);
					}
					else if(actor.getNpcId() == 2503)
						actor.dropItem(killer.getPlayer(), 14678, 1);
				}
				else if(actor_lvl == 70)
				{
					if(actor.getNpcId() == 2502)
					{
						if(random <= 16)
							actor.dropItem(killer.getPlayer(), 5577, 1);
						else if(random > 16 && random < 32)
							actor.dropItem(killer.getPlayer(), 5578, 1);
						else if(random > 32 && random < 48)
							actor.dropItem(killer.getPlayer(), 5579, 2);
						else if(random > 48 && random < 64)
							actor.dropItem(killer.getPlayer(), 5577, 1);
						else if(random > 64 && random < 80)
							actor.dropItem(killer.getPlayer(), 5578, 1);
						else if(random > 80)
							actor.dropItem(killer.getPlayer(), 5579, 1);
					}
					else if(actor.getNpcId() == 2503)
						actor.dropItem(killer.getPlayer(), 14679, 1);
				}
				else if(actor_lvl == 80)
				{
					if(actor.getNpcId() == 2502)
					{
						if(random <= 8)
							actor.dropItem(killer.getPlayer(), 9552, 1);
						else if(random > 8 && random < 16)
							actor.dropItem(killer.getPlayer(), 9552, 2);
						else if(random > 16 && random < 24)
							actor.dropItem(killer.getPlayer(), 9554, 1);
						else if(random > 24 && random < 32)
							actor.dropItem(killer.getPlayer(), 9554, 2);
						else if(random > 32 && random < 40)
							actor.dropItem(killer.getPlayer(), 9556, 1);
						else if(random > 40 && random < 48)
							actor.dropItem(killer.getPlayer(), 9556, 2);	
						else if(random > 48 && random <= 56)
							actor.dropItem(killer.getPlayer(), 9553, 1);
						else if(random > 56 && random < 64)
							actor.dropItem(killer.getPlayer(), 9553, 2);
						else if(random > 64 && random < 72)
							actor.dropItem(killer.getPlayer(), 9555, 1);
						else if(random > 72 && random < 80)
							actor.dropItem(killer.getPlayer(), 9555, 2);
						else if(random > 80 && random < 90)
							actor.dropItem(killer.getPlayer(), 9557, 1);
						else if(random > 90)
							actor.dropItem(killer.getPlayer(), 9557, 2);								
							
					}
					else if(actor.getNpcId() == 2503)
						actor.dropItem(killer.getPlayer(), 14680, 1);
				}				
			}
		}
		super.MY_DYING(killer);
	}

	@Override
	public void TIMER_FIRED_EX(int timer_id, Object[] arg)
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(timer_id == 7778)
		{
			switch(i_ai2)
			{
				case 0:
					Functions.npcSayInRange(actor, 600, 1800279);
					break;
				case 1:
					Functions.npcSayInRange(actor, 600, 1800280);
					break;
				case 2:
					Functions.npcSayInRange(actor, 600, 1800281);
					break;
				case 3:
					Functions.npcSayInRange(actor, 600, 1800284);
					break;
				case 4:
					Functions.npcSayInRange(actor, 600, 1800282);
					break;
				case 5:
					Functions.npcSayInRange(actor, 600, 1800283);
					break;
				case 6:
					Functions.npcSayInRange(actor, 600, 1800286);
					break;
				case 7:
					Functions.npcSayInRange(actor, 600, 1800285);
					break;
				case 8:
					Functions.npcSayInRange(actor, 600, 1800288);
					break;
				case 9:
					Functions.npcSayInRange(actor, 600, 1800287);
					break;
				case 10:
					Functions.npcSayInRange(actor, 600, 1800289);
					break;					
			}
			
			AddTimerEx(7778, 10000 + Rnd.get(10) * 1000);
		}
		else if(timer_id == 1500)
		{
			if(prev_st == i_ai2 && prev_st != 0 && i_ai2 != 10)
			{
				actor.doDie(actor);
			}
			else
			{
				prev_st = i_ai2;
				AddTimerEx(1500, ConfigValue.TimeIfNotFeedDissapear*60000);
			}	
				
		}
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill skill)
	{}

	@Override
	protected void onEvtAggression(L2Character target, int aggro)
	{}
}
