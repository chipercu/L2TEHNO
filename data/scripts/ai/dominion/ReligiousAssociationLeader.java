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
 * @date 4:51/23.06.2011
 */
public class ReligiousAssociationLeader extends SiegeGuardFighter // SiegeGuardMystic
{
	private static final IntObjectMap<Integer[]> MESSAGES = new HashIntObjectMap<Integer[]>(9);

	private boolean dominion_first_attack = true;

	static
	{
		MESSAGES.put(1, new Integer[] {73251, 73261});
		MESSAGES.put(2, new Integer[] {73252, 73262});
		MESSAGES.put(3, new Integer[] {73253, 73263});
		MESSAGES.put(4, new Integer[] {73254, 73264});
		MESSAGES.put(5, new Integer[] {73255, 73265});
		MESSAGES.put(6, new Integer[] {73256, 73266});
		MESSAGES.put(7, new Integer[] {73257, 73267});
		MESSAGES.put(8, new Integer[] {73258, 73268});
		MESSAGES.put(9, new Integer[] {73259, 73269});
	}

	public ReligiousAssociationLeader(L2NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void ATTACKED(L2Character attacker, int dam, L2Skill skill)
	{
		super.ATTACKED(attacker, dam, skill);

		L2NpcInstance actor = getActor();

		if(dominion_first_attack)
		{
			dominion_first_attack = false;
			TerritorySiege.protectObjectAtacked[getDomId(actor)-1][3] = true;
			Integer msg = MESSAGES.get(getDomId(actor))[0];
			Quest q = QuestManager.getQuest("_732_ProtectTheReligiousAssociationLeader");
			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				if(player.getTerritorySiege() == getDomId(actor))
				{
					player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false));

					QuestState questState = player.getQuestState("_732_ProtectTheReligiousAssociationLeader");
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

				QuestState questState = player.getQuestState("_732_ProtectTheReligiousAssociationLeader");
				if(questState != null)
					questState.abortQuest();
			}
		}

		L2Player player = killer.getPlayer();
		if(player == null)
			return;

		if(player.getParty() == null)
		{
			if(player.getTerritorySiege() != -1 && player.getTerritorySiege() != getDomId(actor))
				TerritorySiege.addReward(player, TerritorySiege.STATIC_BADGES, 5, getDomId(actor));
		}
		else
			for(L2Player member : player.getParty().getPartyMembers())
				if(member.isInRange(player, ConfigValue.AltPartyDistributionRange))
					if(member.getTerritorySiege() != -1 && member.getTerritorySiege() != getDomId(actor))
						TerritorySiege.addReward(member, TerritorySiege.STATIC_BADGES, 5, getDomId(actor));
		TerritorySiege.protectObjectAtacked[getDomId(actor)-1][3] = false;
	}

	private int getDomId(L2NpcInstance actor)
	{
		switch(actor.getNpcId())
		{
			case 36510:
				return 1;
			case 36516:
				return 2;
			case 36522:
				return 3;
			case 36528:
				return 4;
			case 36534:
				return 5;
			case 36540:
				return 6;
			case 36546:
				return 7;
			case 36552:
				return 8;
			case 36558:
				return 9;
			default:
				return 0;
		}
	}
}