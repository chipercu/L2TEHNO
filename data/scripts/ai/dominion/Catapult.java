package ai.dominion;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.*;
import l2open.gameserver.instancemanager.QuestManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.quest.Quest;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.ExShowScreenMessage;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author VISTALL
 * @date 3:35/23.06.2011
 */
public class Catapult extends DefaultAI
{
	private static final IntObjectMap<Integer[]> MESSAGES = new HashIntObjectMap<Integer[]>(9);
	
	private boolean dominion_first_attack = true;

	private static final int[][] catapult_doors = 
	{
		{19210001, 19210002, 19210003, 19210004, 19210005, 19210006, 19210007, 19210008, 19210009},
		{20220001, 20220002, 20220005, 20220006, 20220007, 20220008, 20220009},
		{23220001, 23220002, 23220005, 23220006, 23220007, 23220008, 23220009},
		{22190001, 22190002, 22190005, 22190006, 22190007, 22190008, 22190009},
		{24180001, 24180002, 24180004, 24180005, 24180007, 24180008, 24180009, 24180010, 24180012, 24180013, 24180014, 24180015, 24180016, 24180018, 24180019, 24180020, 24180021},
		{23250001, 23250002, 23250005, 23250006, 23250007, 23250008, 23250009},
		{24160009, 24160010, 24160011, 24160012, 24160013, 24160014, 24160015, 24160016, 24160017, 24160018},
		{20160001, 20160002, 20160003, 20160004, 20160006},
		{22130001, 22130002, 22130006, 22130007, 22130008, 22130009, 22130010, 22130011, 22130014, 22130015}
	};

	static
	{
		MESSAGES.put(1, new Integer[] {72951, 72961});
		MESSAGES.put(2, new Integer[] {72952, 72962});
		MESSAGES.put(3, new Integer[] {72953, 72963});
		MESSAGES.put(4, new Integer[] {72954, 72964});
		MESSAGES.put(5, new Integer[] {72955, 72965});
		MESSAGES.put(6, new Integer[] {72956, 72966});
		MESSAGES.put(7, new Integer[] {72957, 72967});
		MESSAGES.put(8, new Integer[] {72958, 72968});
		MESSAGES.put(9, new Integer[] {72959, 72969});
	}

	public Catapult(L2NpcInstance actor)
	{
		super(actor);
	}

		@Override
	public boolean thinkActive()
	{
		return false;
	}

	@Override
	public void ATTACKED(L2Character attacker, int dam, L2Skill skill)
	{
		L2NpcInstance actor = getActor();

		if(dominion_first_attack)
		{
			TerritorySiege.protectObjectAtacked[getDomId(actor)-1][0] = true;
			dominion_first_attack = false;
			Integer msg = MESSAGES.get(getDomId(actor))[0];
			Quest q = QuestManager.getQuest("_729_ProtectTheTerritoryCatapult");
			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				if(player.getTerritorySiege() == getDomId(actor))
				{
					player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false));

					QuestState questState = player.getQuestState("_729_ProtectTheTerritoryCatapult");
					if(questState == null)
					{
						questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
						questState.setCond(1, false);
						questState.setStateAndNotSave(Quest.STARTED);
					}
				}
			}
		}
	}

	@Override
	public void onEvtAggression(L2Character attacker, int d)
	{
		//
	}

	@Override
	public void MY_DYING(L2Character killer)
	{
		L2NpcInstance actor = getActor();
		super.MY_DYING(killer);

		Integer msg = MESSAGES.get(getDomId(actor))[1];
		for(L2Player player : L2ObjectsStorage.getPlayers())
		{
			if(player.getTerritorySiege() == getDomId(actor))
			{
				player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false));

				QuestState questState = player.getQuestState("_729_ProtectTheTerritoryCatapult");
				if(questState != null)
					questState.abortQuest();
			}
		}

		for(int id : catapult_doors[getDomId(actor)-1])
			Functions.openDoor(id, 0);
		TerritorySiege.guardSpawn(getDomId(actor), false);

		L2Player player = killer.getPlayer();
		if(player == null)
			return;

		if(player.getParty() == null)
		{
			if(player.getTerritorySiege() != -1 && player.getTerritorySiege() != getDomId(actor))
				TerritorySiege.addReward(player, TerritorySiege.STATIC_BADGES, 15, getDomId(actor));
		}
		else
		{
			for(L2Player member : player.getParty().getPartyMembers())
				if(member.isInRange(player, ConfigValue.AltPartyDistributionRange))
					if(member.getTerritorySiege() != -1 && member.getTerritorySiege() != getDomId(actor))
						TerritorySiege.addReward(member, TerritorySiege.STATIC_BADGES, 15, getDomId(actor));
		}
		TerritorySiege.protectObjectAtacked[getDomId(actor)-1][0] = false;
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();
		dominion_first_attack = true;
	}

	private int getDomId(L2NpcInstance actor)
	{
		return actor.getNpcId() - 36498;
	}
}
