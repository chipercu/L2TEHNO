package ai;

import l2open.gameserver.ai.Fighter;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Spawn;
import l2open.gameserver.model.Reflection;
import l2open.gameserver.model.instances.L2MonsterInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.skills.funcs.FuncMul;

/**
 * @author Drizzy
 * @date 04.05.11
 * @time 00:01
 * Инстант для лабиринта бездны (камалока). Предназначен для снижения статов у рб. При убийстве первой комнаты п. деф рб падает на 1\3. 
 * 2-я комната снижает м.деф на 1\3. 3-я комната снижает п.атк на 1\3.
 */
public class LabyrinthOfTheAbyss extends Fighter
{
    private int FirstInt = 0;
    private int SecondInt = 0;
	private int state = 0;

    public LabyrinthOfTheAbyss (L2Character actor)
    {
        super(actor);
    }

    @Override
    protected void MY_DYING(L2Character killer)
    {
        L2NpcInstance actor = getActor();
		L2Player player = killer.getPlayer();
        int InstanceId = actor.getReflection().getInstancedZoneId();
        switch (InstanceId)
        {
            case 73:
                checkKillProgress(73, actor, player);
                break;
            case 74:
                checkKillProgress(74, actor, player);
                break;
            case 75:
                checkKillProgress(75, actor, player);
                break;
            case 76:
                checkKillProgress(76, actor, player);
                break;
            case 77:
                checkKillProgress(77, actor, player);
                break;
            case 78:
                checkKillProgress(78, actor, player);
                break;
            case 79:
                checkKillProgress(79, actor, player);
                break;
            case 134:
                checkKillProgress(134, actor, player);
                break;
        }
        super.MY_DYING(killer);
    }

	private void checkKillProgress(int Id, L2NpcInstance actor, L2Character killer)
    {
        Reflection ref = actor.getReflection();

        for(L2Spawn spawn : ref.getSpawns())
        {
			if(spawn.getNpcId() == getFirstId(Id))
			{
				for(L2NpcInstance npc : spawn.getAllSpawned())
				{
					if(npc.isDead())
					{
						if(actor != null && killer != null && killer.getReflection() != null && actor.getNpcId() == getFirstId(Id) && killer.getReflection() == actor.getReflection())
						{
							if(FirstInt < 9)
								FirstInt++;
							if(FirstInt == 9)
							{
								FirstInt = 0; // Обнуляем на всякий случай.
								FirstRoom(actor, Id, killer);
							}
						}
					}
				}
			}
			if(spawn.getNpcId() == getSecondId(Id))
			{
				for(L2NpcInstance npc : spawn.getAllSpawned())
				{
					if(npc.isDead())
					{
						if(actor.getNpcId() == getSecondId(Id) && killer.getReflection() == actor.getReflection())
						{
							if(SecondInt < 5)
								SecondInt++;
							if(SecondInt == 5)
							{
								SecondInt = 0; //Обнуляем на всякий случай.
								SecondRoom(actor, Id, killer);
							}
						}
					}
				}
			}
			if(spawn.getNpcId() == getThirdId(Id))
			{
				for(L2NpcInstance npc : spawn.getAllSpawned())
				{
					if(npc.isDead())
					{
						if(actor.getNpcId() == getThirdId(Id) && killer.getReflection() == actor.getReflection())
						{
							ThirdRoom(actor, Id, killer);
						}
					}
				}
			}
		}
    }

    private void FirstRoom(L2NpcInstance actor, int id, L2Character killer)
    {
        Reflection ref = actor.getReflection();
		if(ref.getId() == killer.getReflectionId())
			for(L2MonsterInstance npc : ref.getMonsters())
				if(npc.getNpcId() == getRBId(id))
					npc.addStatFunc(new FuncMul(l2open.gameserver.skills.Stats.p_physical_defence, 0x30, npc, 0.34d));
    }

    private void SecondRoom(L2NpcInstance actor, int id, L2Character killer)
    {
        Reflection ref = actor.getReflection();
		if(ref.getId() == killer.getReflectionId())
			for(L2MonsterInstance npc : ref.getMonsters())
				if(npc.getNpcId() == getRBId(id))
					npc.addStatFunc(new FuncMul(l2open.gameserver.skills.Stats.p_magical_defence, 0x30, npc, 0.34d));
    }

    private void ThirdRoom(L2NpcInstance actor, int id, L2Character killer)
    {
        Reflection ref = actor.getReflection();
		if(ref.getId() == killer.getReflectionId())
			for(L2MonsterInstance npc : ref.getMonsters())
				if(npc.getNpcId() == getRBId(id))
					npc.addStatFunc(new FuncMul(l2open.gameserver.skills.Stats.p_physical_attack, 0x30, npc, 0.34d));
    }

    private int getFirstId(int id)
    {
        switch (id)
        {
            case 73:
                return 22485;
            case 74:
                return 22488;
            case 75:
                return 22491;
            case 76:
                return 22494;
            case 77:
                return 22497;
            case 78:
                return 22501;
            case 79:
                return 22503;
            case 134:
                return 25707;
        }
        return 0;
    }

    private int getSecondId(int id)
    {
		switch (id)
        {
            case 73:
                return 22487;
            case 74:
                return 22490;
            case 75:
                return 22493;
            case 76:
                return 22496;
            case 77:
                return 22499;
            case 78:
                return 22502;
            case 79:
                return 22505;
            case 134:
                return 25708;
        }
        return 0;
    }

    private int getThirdId(int id)
    {
        switch (id)
        {
            case 73:
                return 25616;
            case 74:
                return 25617;
            case 75:
                return 25618;
            case 76:
                return 25619;
            case 77:
                return 25620;
            case 78:
                return 25621;
            case 79:
                return 25622;
            case 134:
                return 25709;
        }
        return 0;
    }

    private int getRBId(int id)
    {
        switch (id)
        {
            case 73:
                return 29129;
            case 74:
                return 29132;
            case 75:
                return 29135;
            case 76:
                return 29138;
            case 77:
                return 29141;
            case 78:
                return 29144;
            case 79:
                return 29147;
            case 134:
                return 25710;
        }
        return 0;
    }
}