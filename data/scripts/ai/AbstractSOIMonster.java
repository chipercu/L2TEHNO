package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.skills.funcs.FuncAdd;
import l2open.gameserver.tables.SkillTable;
import l2open.util.Rnd;

public class AbstractSOIMonster extends Fighter
{
	private static final L2Skill[] skill_list1 = { SkillTable.getInstance().getInfo(5915, 1), SkillTable.getInstance().getInfo(5915, 2), SkillTable.getInstance().getInfo(5915, 3), SkillTable.getInstance().getInfo(5915, 4), SkillTable.getInstance().getInfo(5915, 5), SkillTable.getInstance().getInfo(5915, 6), SkillTable.getInstance().getInfo(5915, 7), SkillTable.getInstance().getInfo(5915, 8), SkillTable.getInstance().getInfo(5915, 9) };
	private static final L2Skill[] skill_list2 = { SkillTable.getInstance().getInfo(5916, 1), SkillTable.getInstance().getInfo(5916, 2), SkillTable.getInstance().getInfo(5916, 3), SkillTable.getInstance().getInfo(5916, 4), SkillTable.getInstance().getInfo(5916, 5), SkillTable.getInstance().getInfo(5916, 6), SkillTable.getInstance().getInfo(5916, 7), SkillTable.getInstance().getInfo(5916, 8), SkillTable.getInstance().getInfo(5916, 9) };
    private static final L2Skill[] skill_list3 = { SkillTable.getInstance().getInfo(5917, 1), SkillTable.getInstance().getInfo(5917, 2), SkillTable.getInstance().getInfo(5917, 3), SkillTable.getInstance().getInfo(5917, 4), SkillTable.getInstance().getInfo(5917, 5), SkillTable.getInstance().getInfo(5917, 6), SkillTable.getInstance().getInfo(5917, 7), SkillTable.getInstance().getInfo(5917, 8), SkillTable.getInstance().getInfo(5917, 9) };
	private boolean chars = false;
	private static final L2Skill[][] attacker_skill_list = new L2Skill[][] { skill_list1, skill_list2, skill_list3 };
	private static final String messeg = "I am already dead. You cannot kill me again...";
	private int attackCount = 0;

	public AbstractSOIMonster(L2Character actor)
	{
		super(actor);
	}

	@Override
	protected void ATTACKED(L2Character attacker, int damage, L2Skill sk)
	{
		L2NpcInstance npc = getActor();
		if (attacker == null || npc == null)
			return;
		L2Skill skill = null;
		int i = Rnd.get(3);
		if (attackCount <= 0 && npc.getCurrentHpPercents() <= 90)
		{
			attackCount = 1;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 1 && npc.getCurrentHpPercents() <= 80)
		{
			attackCount = 2;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 2 && npc.getCurrentHpPercents() <= 70)
		{
			attackCount = 3;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 3 && npc.getCurrentHpPercents() <= 60)
		{
			attackCount = 4;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 4 && npc.getCurrentHpPercents() <= 50)
		{
			attackCount = 5;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 5 && npc.getCurrentHpPercents() <= 40)
		{
			attackCount = 6;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 6 && npc.getCurrentHpPercents() <= 30)
		{
			attackCount = 7;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 7 && npc.getCurrentHpPercents() <= 20)
		{
			attackCount = 8;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		else if (attackCount <= 8 && npc.getCurrentHpPercents() <= 10)
		{
			attackCount = 9;
			skill = attacker_skill_list[i][attackCount - 1];
		}
		if (skill != null)
			AddUseSkillDesire(attacker, skill, 1000000);
		if (npc.getCurrentHpPercents() <= 10 && Rnd.chance(20) && !chars)
		{
			chars = true;
			NpcSay say = new NpcSay(npc, Say2C.NPC_ALL, messeg);
			for(L2Player player : L2World.getAroundPlayers(npc, 500, 200))
			{
				if (player != null)
					player.sendPacket(say);
			}
			npc.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, 64, damage, 10.));
		}
		super.ATTACKED(attacker, damage, sk);
	}
}