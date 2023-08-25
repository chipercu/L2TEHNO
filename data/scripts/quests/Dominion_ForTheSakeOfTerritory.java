package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class Dominion_ForTheSakeOfTerritory extends Quest implements ScriptFile
{
	@Override
	public void onPlayerEnter(QuestState st)
	{
		if(TerritorySiege.isInProgress())
		{
			if(st.getPlayer().getTerritorySiege() == -1 || st.getPlayer().getTerritorySiege() != getDominionId)
				return;

			QuestState questState = st.getPlayer().getQuestState(Dominion_ForTheSakeOfTerritory.this.getClass());
			if(st.getPlayer().getLevel() > 61 && questState == null)
			{
				questState = newQuestState(st.getPlayer(), Quest.CREATED);
				questState.setState(Quest.STARTED);
				questState.setCond(1);
			}
		}
	}

	private final int[] supplyBoxes = 		  {36591, 36592, 36593, 36594, 36595, 36596, 36597, 36598, 36599};
	private final int[] catapultas = 		  {36499, 36500, 36501, 36502, 36503, 36504, 36505, 36506, 36507};
	private final int[] militaryUnitLeaders = {36508, 36514, 36520, 36526, 36532, 36538, 36544, 36550, 36556};
	private final int[] religionUnitLeaders = {36510, 36516, 36522, 36528, 36534, 36540, 36546, 36552, 36558};
	private final int[] economicUnitLeaders = {36513, 36519, 36525, 36531, 36537, 36543, 36549, 36555, 36561};
	
	protected int getDominionId;

	public Dominion_ForTheSakeOfTerritory(int id)
	{
		super(1, id);
		addKillId(supplyBoxes);
		addKillId(catapultas);
		addKillId(militaryUnitLeaders);
		addKillId(religionUnitLeaders);
		addKillId(economicUnitLeaders);
	}

	private boolean isValidNpcKill(L2Player killer, L2NpcInstance npc)
	{
		int territoryId = getTerritoryIdForThisNPCId(npc.getNpcId());

		if(territoryId == 0 || killer.getTerritorySiege() == -1)
			return false;
		if(territoryId == killer.getTerritorySiege())
			return false;
		return true;
	}

	private void handleReward(QuestState st)
	{
		L2Player player = st.getPlayer();
		if(player == null)
			return;

		TerritorySiege.addReward(player, TerritorySiege.STATIC_BADGES, 10, getDominionId);
	}

	@Override
	public String onKill(L2NpcInstance npc, QuestState st)
	{
		if(!isValidNpcKill(st.getPlayer(), npc))
			return null;

		if(st.getCond() == 1)
		{
			if(ArrayUtils.contains(catapultas, npc.getNpcId()))
				st.setCond(2);
			else if(ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
				st.setCond(3);
			else if(ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
				st.setCond(4);
		}
		else if(st.getCond() == 2)
		{
			if(ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
				st.setCond(5);
			else if(ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
				st.setCond(6);
		}
		else if(st.getCond() == 3)
		{
			if(ArrayUtils.contains(catapultas, npc.getNpcId()))
				st.setCond(7);
			else if(ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
				st.setCond(8);
		}
		else if(st.getCond() == 4)
		{
			if(ArrayUtils.contains(catapultas, npc.getNpcId()))
				st.setCond(9);
			else if(ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
				st.setCond(10);
		}
		else if(st.getCond() == 5)
		{
			if(ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if(st.getCond() == 6)
		{
			if(ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if(st.getCond() == 7)
		{
			if(ArrayUtils.contains(militaryUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(religionUnitLeaders, npc.getNpcId()) || ArrayUtils.contains(economicUnitLeaders, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if(st.getCond() == 8)
		{
			if(ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if(st.getCond() == 9)
		{
			if(ArrayUtils.contains(supplyBoxes, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}
		else if(st.getCond() == 10)
		{
			if(ArrayUtils.contains(catapultas, npc.getNpcId()))
			{
				st.setCond(11);
				handleReward(st);
			}
		}

		return null;
	}

	@Override
	public boolean canAbortByPacket()
	{
		return false;
	}

	public static int getTerritoryIdForThisNPCId(int npcid)
	{
		switch(npcid)
		{
			case 36591:
			case 36499:
			case 36508:
			case 36510:
			case 36513:
				return 1;
			case 36592:
			case 36500:
			case 36514:
			case 36516:
			case 36519:
				return 2;
			case 36593:
			case 36501:
			case 36520:
			case 36522:
			case 36525:
				return 3;
			case 36594:
			case 36502:
			case 36526:
			case 36528:
			case 36531:
				return 4;
			case 36595:
			case 36503:
			case 36532:
			case 36534:
			case 36537:
				return 5;
			case 36543:
			case 36540:
			case 36538:
			case 36596:
			case 36504:
				return 6;
			case 36549:
			case 36546:
			case 36544:
			case 36597:
			case 36505:
				return 7;
			case 36555:
			case 36552:
			case 36550:
			case 36598:
			case 36506:
				return 8;
			case 36561:
			case 36558:
			case 36556:
			case 36599:
			case 36507:
				return 9;
			default:
				return 0;
		}
	}

	@Override
	public void onLoad()
	{

	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
