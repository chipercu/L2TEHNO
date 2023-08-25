package ai.hellbound;

import java.util.HashMap;

import javolution.util.FastMap;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.idfactory.IdFactory;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TrapInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.tables.NpcTable;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Location;
import l2open.util.Rnd;

/**
 * @author Diamond
 * 25713(25531)-A
 */
public class Darnel extends DefaultAI
{
	private class TrapTask extends l2open.common.RunnableImpl
	{
		public void runImpl()
		{
			L2NpcInstance actor = getActor();
			if(actor == null || actor.isDead())
				return;

			// Спавним 10 ловушек
			for(int i = 0; i < 10; i++)
				new L2TrapInstance(IdFactory.getInstance().getNextId(), NpcTable.getTemplate(13037), actor, trapSkills[Rnd.get(trapSkills.length)], new Location(Rnd.get(151896, 153608), Rnd.get(145032, 146808), -12584), false);
		}
	}

	final L2Skill[] trapSkills = new L2Skill[] { SkillTable.getInstance().getInfo(5267, 1),
			SkillTable.getInstance().getInfo(5268, 1), SkillTable.getInstance().getInfo(5269, 1),
			SkillTable.getInstance().getInfo(5270, 1) };

	final L2Skill Poison;
	final L2Skill Paralysis;

	public Darnel(L2Character actor)
	{
		super(actor);

		HashMap<Integer, L2Skill> skills = getActor().getTemplate().getSkills();

		Poison = skills.get(4182);
		Paralysis = skills.get(4189);
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

		if(rnd_per < 5)
		{
			actor.broadcastSkill(new MagicSkillUse(actor, actor, 5440, 1, 3000, 0));
			ThreadPoolManager.getInstance().schedule(new TrapTask(), 3000);
			return true;
		}

		double distance = actor.getDistance(target);

		if(!actor.isAMuted() && rnd_per < 75)
			return chooseTaskAndTargets(null, target, distance);

		FastMap<L2Skill, Integer> d_skill = new FastMap<L2Skill, Integer>();

		addDesiredSkill(d_skill, target, distance, Poison);
		addDesiredSkill(d_skill, target, distance, Paralysis);

		L2Skill r_skill = selectTopSkill(d_skill);

		return chooseTaskAndTargets(r_skill, target, distance);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	public void MY_DYING(L2Character last_attacker)
	{
		L2NpcInstance myself = getActor();
		L2Party party0 = last_attacker.getPlayer().getParty();
		//myself.InstantZone_MarkRestriction();
		//myself.CreateOnePrivateEx(1032276,"ai_telecube_oracle_raid",0,0,152766,145951,-12584,0,0,0,0);
		if(party0 != null)
		{
			for(L2Player c0 : party0.getPartyMembers())
			{
				if(c0 != null)
				{
					if(c0.getReflectionId() == myself.getReflectionId())
					{
						if(myself.OwnItemCount(c0,9690) >= 1)
						{
							myself.GiveItem1(c0,9695,1);
							myself.GiveItem1(c0,9597,1);
						}
						else
						{
							//myself.InstantZone_Leave(c0);
						}
					}
				}
			}
		}
		super.MY_DYING(last_attacker);
	}
}