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
 * @date 4:32/23.06.2011
 */
public class MilitaryAssociationLeader extends SiegeGuardFighter
{
	private static final IntObjectMap<Integer[]> MESSAGES = new HashIntObjectMap<Integer[]>(9);

	private boolean dominion_first_attack = true;

	static
	{
		MESSAGES.put(1, new Integer[] {73151, 73161});
		MESSAGES.put(2, new Integer[] {73152, 73162});
		MESSAGES.put(3, new Integer[] {73153, 73163});
		MESSAGES.put(4, new Integer[] {73154, 73164});
		MESSAGES.put(5, new Integer[] {73155, 73165});
		MESSAGES.put(6, new Integer[] {73156, 73166});
		MESSAGES.put(7, new Integer[] {73157, 73167});
		MESSAGES.put(8, new Integer[] {73158, 73168});
		MESSAGES.put(9, new Integer[] {73159, 73169});
	}

	public MilitaryAssociationLeader(L2NpcInstance actor)
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
			TerritorySiege.protectObjectAtacked[getDomId(actor)-1][2] = true;
			Integer msg = MESSAGES.get(getDomId(actor))[0];
			Quest q = QuestManager.getQuest("_731_ProtectTheMilitaryAssociationLeader");
			for(L2Player player : L2ObjectsStorage.getPlayers())
			{
				if(player.getTerritorySiege() == getDomId(actor))
				{
					player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, false));

					QuestState questState = player.getQuestState("_731_ProtectTheMilitaryAssociationLeader");
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

				QuestState questState = player.getQuestState("_731_ProtectTheMilitaryAssociationLeader");
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
		TerritorySiege.protectObjectAtacked[getDomId(actor)-1][2] = false;
	}

	private int getDomId(L2NpcInstance actor)
	{
		switch(actor.getNpcId())
		{
			case 36508:
				return 1;
			case 36514:
				return 2;
			case 36520:
				return 3;
			case 36526:
				return 4;
			case 36532:
				return 5;
			case 36538:
				return 6;
			case 36544:
				return 7;
			case 36550:
				return 8;
			case 36556:
				return 9;
			default:
				return 0;
		}
	}
}
