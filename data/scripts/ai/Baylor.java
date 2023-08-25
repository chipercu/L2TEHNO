package ai;

import java.util.HashMap;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.util.Rnd;
import bosses.BaylorManager;

/**
 * @author Diamond
 */
public class Baylor extends DefaultAI
{
	final L2Skill Berserk; // Increases P. Atk. and P. Def.
	final L2Skill Invincible; // Неуязвимость при 30% hp
	final L2Skill Imprison; // Помещает одиночную цель в тюрьму, рейндж 600
	final L2Skill GroundStrike; // Массовая атака, 2500 каст
	final L2Skill JumpAttack; // Массовая атака, 2500 каст
	final L2Skill StrongPunch; // Откидывает одиночную цель кулаком, и оглушает, рейндж 600
	final L2Skill Stun1; // Массовое оглушение, 5000 каст
	final L2Skill Stun2; // Массовое оглушение, 3000 каст
	final L2Skill Stun3; // Массовое оглушение, 2000 каст
	//final L2Skill Stun4; // Не работает?

	final int PresentationBalor2 = 5402; // Прыжок, удар по земле
	final int PresentationBalor3 = 5403; // Электрическая аура
	final int PresentationBalor4 = 5404; // Электрическая аура, в конце сияние

	final int PresentationBalor10 = 5410; // Не работает?
	final int PresentationBalor11 = 5411; // Не работает?
	final int PresentationBalor12 = 5412; // Массовый удар

	private static final int Water_Dragon_Claw = 2360;

	private boolean _isUsedInvincible = false;

	private int _claw_count = 0;
	private long _last_claw_time = 0;

	private class SpawnSocial extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor != null)
				actor.broadcastSkill(new MagicSkillUse(actor, actor, PresentationBalor2, 1, 4000, 0));
		}
	}

	public Baylor(L2Character actor)
	{
		super(actor);

		HashMap<Integer, L2Skill> skills = getActor().getTemplate().getSkills();

		Berserk = skills.get(5224);
		Invincible = skills.get(5225);
		Imprison = skills.get(5226);
		GroundStrike = skills.get(5227);
		JumpAttack = skills.get(5228);
		StrongPunch = skills.get(5229);
		Stun1 = skills.get(5230);
		Stun2 = skills.get(5231);
		Stun3 = skills.get(5232);
		//Stun4 = skills.get(5401);
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new SpawnSocial(), 20000);
	}

	@Override
	protected void onEvtSeeSpell(L2Skill skill, L2Character caster)
	{
		L2NpcInstance actor = getActor();
		if(actor == null || actor.isDead() || skill == null || caster == null)
			return;

		if(System.currentTimeMillis() - _last_claw_time > 5000)
			_claw_count = 0;

		if(skill.getId() == Water_Dragon_Claw)
		{
			_claw_count++;
			_last_claw_time = System.currentTimeMillis();
		}

		L2Player player = caster.getPlayer();
		if(player == null)
			return;

		int count = 1;
		L2Party party = player.getParty();
		if(party != null)
			count = party.getMemberCount();

		// Снимаем неуязвимость
		if(_claw_count >= count)
		{
			_claw_count = 0;
			actor.getEffectList().stopEffect(Invincible);
			Functions.npcSay(actor, "Да как вы посмели! Я непобедим!!!");
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

		if(!BaylorManager.getZone().checkIfInZone(actor))
		{
			teleportHome(true);
			return false;
		}

		double distance = actor.getDistance(target);
		double actor_hp_precent = actor.getCurrentHpPercents();

		if(actor_hp_precent < 30 && !_isUsedInvincible)
		{
			_isUsedInvincible = true;
			addTaskBuff(actor, Invincible);
			Functions.npcSay(actor, "Ахаха! Теперь вы все умрете.");
			return true;
		}

		int rnd_per = Rnd.get(100);
		if(rnd_per < 7 && actor.getEffectList().getEffectsBySkill(Berserk) == null)
		{
			addTaskBuff(actor, Berserk);
			Functions.npcSay(actor, "Beleth, дай мне силу!");
			return true;
		}

		if(rnd_per < 15 || rnd_per < 33 && actor.getEffectList().getEffectsBySkill(Berserk) != null)
			return chooseTaskAndTargets(StrongPunch, target, distance);

		//if(rnd_per < 5 && target.getEffectList().getEffectsBySkill(Imprison) == null)
		//{
		//	_isUsedInvincible = true;
		//	addTaskCast(target, Imprison);
		//	return true;
		//}

		if(!actor.isAMuted() && rnd_per < 50)
			return chooseTaskAndTargets(null, target, distance);

		FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, GroundStrike);
		addDesiredSkill(d_skill, target, distance, JumpAttack);
		addDesiredSkill(d_skill, target, distance, StrongPunch);
		addDesiredSkill(d_skill, target, distance, Stun1);
		addDesiredSkill(d_skill, target, distance, Stun2);
		addDesiredSkill(d_skill, target, distance, Stun3);

		L2Skill r_skill = selectTopSkill(d_skill);
		if(r_skill != null && !r_skill.isOffensive())
			target = actor;

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		L2NpcInstance actor = getActor();
		if(actor != null && !BaylorManager.getZone().checkIfInZone(actor))
			teleportHome(true);
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}