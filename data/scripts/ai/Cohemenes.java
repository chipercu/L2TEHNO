package ai;

import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class Cohemenes extends Fighter
{
	private static final L2Skill l_skill = SkillTable.getInstance().getInfo(5930, 1);
	private static final L2Skill r_skill = SkillTable.getInstance().getInfo(5929, 1);

	private L2NpcInstance _brother;

	private long _wait_timeout = 0;
	private int LeftAtackCount = 0;
	private int time = 0;
	private int atackCount = 0;

	private void getThincAtack()
	{
		L2NpcInstance actor = getActor();
		if(actor  == null)
			return;
		if(_brother == null)
		{
			L2MonsterInstance npc = (L2MonsterInstance) NpcTable.getTemplate(25635).getNewInstance();
			npc.setSpawnedLoc(actor.getLoc());
			npc.setReflection(actor.getReflectionId());
			npc.onSpawn();
			npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
			npc.spawnMe(actor.getLoc().rnd(60, 60, false));
			_brother = npc;
			_brother.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getRandomHated(), Rnd.get(1, 100));
			Functions.npcShout(actor, "Impressive.... Hahaha it's so much fun, but I need to chill a little while.  Argekunte, clear the way!");
		}
	}

	public Cohemenes(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character actor, int damage, L2Skill skill)
	{
		L2NpcInstance mob = getActor();
		if(mob == null)
			return;
		if(actor != null)
		{
			if(mob.getDistance(actor) > 200 || (actor.getCastingSkill() != null && actor.getCastingSkill().isMagic()))
			{
				atackCount++;
				LeftAtackCount--;
			}
			else
			{
				atackCount--;
				LeftAtackCount++;
			}
			if(time < System.currentTimeMillis()/1000)
			{
				if(LeftAtackCount > 100 && LeftAtackCount > atackCount * 4)
				{
					Functions.npcShout(mob, "Clinging on won't help you! Ultimate forgotten magic, Blade Turn!");
					mob.getEffectList().stopEffect(l_skill);
					AddUseSkillDesire(mob, r_skill, 1000000);
				}
				else if(atackCount > 50 && atackCount > LeftAtackCount * 2)
				{
					Functions.npcShout(mob, "Even special sauce can't help you! Ultimate forgotten magic, Force Shield!");
					mob.getEffectList().stopEffect(r_skill);
					AddUseSkillDesire(mob, l_skill, 1000000);
				}
				atackCount = 0;
				LeftAtackCount = 0;
				time = (int)(System.currentTimeMillis() / 1000L + 60);
			}
		}
		if(_brother == null)
			searchBrother();
		super.ATTACKED(actor, damage, skill);
	}

	@Override
	protected boolean thinkActive()
	{
		if (_brother == null)
			searchBrother();
		return super.thinkActive();
	}

	private boolean searchBrother()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return false;
		if(_brother == null)
		{
			// Ищем брата не чаще, чем раз в 15 секунд, если по каким-то причинам его нету
			if(System.currentTimeMillis() > _wait_timeout)
			{
				_wait_timeout = (System.currentTimeMillis() + 15000);
				for(L2NpcInstance npc : L2World.getAroundNpc(actor))
					if(npc.getNpcId() == 25635)
					{
						_brother = npc;
						return true;
					}
			}
		}
		return false;
	}

	@Override
	protected void thinkAttack()
	{
		L2NpcInstance actor = getActor();
		if(actor == null)
			return;
		if(_brother == null)
			getThincAtack();
		else if(_brother.isDead())
		{
			_brother.deleteMe();
			_brother = null;
			L2MonsterInstance mob = (L2MonsterInstance) NpcTable.getTemplate(25635).getNewInstance();
			mob.setSpawnedLoc(actor.getLoc());
			mob.setReflection(actor.getReflectionId());
			mob.onSpawn();
			mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp(), true);
			mob.spawnMe(actor.getLoc().rnd(60, 60, false));
			mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getRandomHated(), Rnd.get(1, 100));
			_brother = mob;
			Functions.npcShout(actor, "Kahahaha! That guy's nothing! He can't even kill without my permission! See here! Ultimate forgotten magic! Deathless Guardian!");
		}
		super.thinkAttack();
	}
}