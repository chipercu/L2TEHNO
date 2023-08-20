package npc.model;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.scripts.Functions;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.clientpackets.Say2C;
import l2open.gameserver.model.L2Party;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2World;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.NpcHtmlMessage;
import l2open.gameserver.serverpackets.NpcSay;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Location;
import l2open.util.Rnd;
import l2open.util.Util;
import zones.TullyWorkshopZone;


public class WorkshopServantInstance extends L2NpcInstance
{

	private static final int[] REWARDS =
	{
		10427, 10428, 10429, 10430, 10431
	};
	private static final String[] phrases = {
			"We won't let you go with this knowledge! Die!",
			"Mysterious Agent has failed! Kill him!",
			"Mates! Attack those fools!",
	};

	public WorkshopServantInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	//Чиним девайс
	public class repair_device extends l2open.common.RunnableImpl
	{
		private L2NpcInstance _npc;
		public repair_device(L2NpcInstance npc)
		{
			_npc = npc;
		}
		@Override
		public void runImpl()
		{
			_npc.broadcastPacket(new NpcSay(_npc, Say2C.NPC_SHOUT, 1010631));
			TullyWorkshopZone.brokenContraptions.remove(_npc.getObjectId());
		}
	}

	@Override
	public void onBypassFeedback(L2Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.isInOlympiadMode())
		{
			player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
			return;
		}
		if (command.startsWith("touch_device"))
		{
			int i0 = TullyWorkshopZone.talkedContraptions.contains(this.getObjectId()) ? 0 : 1;
			int i1 = player.getClassId().equalsOrChildOf(ClassId.maestro) ? 6 : 3;

			if (Rnd.get(1000) < ((i1 - i0) * 100))
			{
				TullyWorkshopZone.talkedContraptions.add(getObjectId());
				showHtmlFile(player, player.getClassId().equalsOrChildOf(ClassId.maestro) ? "32371-03a.htm" : "32371-03.htm");
			}
			else
			{
				TullyWorkshopZone.brokenContraptions.add(getObjectId());
				ThreadPoolManager.getInstance().schedule(new repair_device(this), 60000);
				showHtmlFile(player, "32371-04.htm");
			}
		}
		else if (command.startsWith("take_reward"))
		{
			boolean alreadyHaveItem = false;
			for(int itemId : REWARDS)
			{
				if(player.getInventory().getItemByItemId(itemId) != null)
				{
					alreadyHaveItem = true;
					break;
				}
			}

			if (!alreadyHaveItem && !TullyWorkshopZone.rewardedContraptions.contains(getObjectId()))
			{
				int idx = TullyWorkshopZone.postMortemSpawn.indexOf(this);
				if ((idx > -1) && (idx < 5))
				{
					Functions.addItem(player, REWARDS[idx], 1);
					TullyWorkshopZone.rewardedContraptions.add(getObjectId());
					if (idx != 0)
					{
						deleteMe();
					}
				}
			}
			else
			{
				showHtmlFile(player, "32371-05.htm");
			}
		}
		else if(command.startsWith("requestteleport"))
		{
			final L2Party party = player.getParty();
			if(party == null)
				player.teleToLocation(-12176, 279696, -13596);
			else
			{
				if(party.getPartyLeader().getObjectId() != player.getObjectId())
				{
					player.sendPacket(new SystemMessage(SystemMessage.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER));
					return;
				}

				for(L2Player partyMember : party.getPartyMembers())
					if(!Util.checkIfInRange(3000, partyMember, this, true))
					{
						showHtmlFile(player, "32370-01f.htm");
						return;
					}

				for(L2Player partyMember : party.getPartyMembers())
					if(Util.checkIfInRange(6000, partyMember, this, true))
						partyMember.teleToLocation(-12176, 279696, -13596);
			}
		}
		else if(command.startsWith("teletoroof"))
			player.teleToLocation(22616, 244888, 11062);
		else if(command.startsWith("teleto7thfloor"))
			player.teleToLocation(-12520, 280120, -11649);
		else if(command.startsWith("acceptjob"))
		{
			if(ConfigValue.MysteriousAgentBuff)
			{
				broadcastSkill(new MagicSkillUse(this, player, 5526, 1, 0, 0));
				altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(5526, 1));
			}
			player.teleToLocation(22616, 244888, 11062);
		}
		else if(command.startsWith("rejectjob"))
		{
			for(L2NpcInstance challenger : L2World.getAroundNpc(this, 600, 300))
			{
				challenger.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
				switch(challenger.getNpcId())
				{
					case 25600:
						Functions.npcSay(challenger, phrases[0]);
						break;
					case 25601:
						Functions.npcSay(challenger, phrases[1]);
						break;
					case 25602:
						Functions.npcSay(challenger, phrases[2]);
						break;
					default:
						break;
				}
			}
			Functions.npcSay(this, "Oh...");
			doDie(null);
		}
		else if(command.startsWith("tryanomicentry"))
		{
			if(getNpcId() == 32344)
			{
				final L2Party party = player.getParty();
				if(party == null)
				{
					showHtmlFile(player, "32344-03.htm");
					return;
				}

				boolean[] haveItems =
				{
					false, false, false, false, false
				};
				// For teleportation party should have all 5 medals
				for (L2Player pl : party.getPartyMembers())
				{
					if (pl == null)
					{
						continue;
					}

					for (int i = 0; i < REWARDS.length; i++)
					{
						if ((player.getInventory().getItemByItemId(REWARDS[i]) != null) && Util.checkIfInRange(300, pl, this, true))
						{
							haveItems[i] = true;
							break;
						}
					}
				}

				int medalsCount = 0;
				for(boolean haveItem : haveItems)
					if(haveItem)
						medalsCount++;

				if(medalsCount == 0)
				{
					showHtmlFile(player, "32344-03.htm");
					return;
				}
				else if(medalsCount < 5)
				{
					showHtmlFile(player, "32344-02.htm");
					return;
				}

				for(L2Player pl : party.getPartyMembers())
					if(pl != null && Util.checkIfInRange(6000, pl, this, false))
						pl.teleToLocation(26612, 248567, -2856);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2Player player, int val)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(getNpcId());
		else
			pom = getNpcId() + "-" + val;

		if(getNpcId() == 32372)
		{
			if(getAI().Floor == 7)
			{
				showHtmlFile(player, "32372-floor.htm");
				return;
			}
		}
		else if(getNpcId() == 32371)
		{
			if(TullyWorkshopZone.talkedContraptions.contains(getObjectId()))
			{
				showHtmlFile(player, "32371-02.htm");
				return;
			}
			else if(!TullyWorkshopZone.brokenContraptions.contains(getObjectId()))
			{
				if(player.getClassId().equalsOrChildOf(ClassId.maestro))
				{
					showHtmlFile(player, "32371-01a.htm");
					return;
				}
				showHtmlFile(player, "32371.htm");
				return;
			}
			showHtmlFile(player, "32371-04.htm");
			return;
		}
		else if(getNpcId() == 32344)
		{
			for(int itemId : REWARDS)
				if(player.getInventory().getItemByItemId(itemId) != null)
				{
					showHtmlFile(player, "32344.htm");
					return;
				}
			showHtmlFile(player, "32344-01a.htm");
			return;
		}
		else if(getNpcId() == 32370)
		{
			if(TullyWorkshopZone.postMortemSpawn.indexOf(this) == 11)
			{
				broadcastPacket(new NpcSay(this, Say2C.NPC_ALL, 1800135));
				deleteMe();
				return;
			}
			else if(TullyWorkshopZone.postMortemSpawn.indexOf(this) == 12 || TullyWorkshopZone.postMortemSpawn.indexOf(this) == 0)
			{
				showHtmlFile(player, "32370.htm");
				return;
			}
			else if(isInRange(new Location(-45531, 245872, -14192), 100)) // Hello from Tower of Naia! :) Due to onFirstTalk limitation it should be here
			{
				showHtmlFile(player, "32370-03.htm");
				return;
			}
			else
			{
				showHtmlFile(player, "32370-02.htm");
				return;
			}
		}
		showHtmlFile(player, pom + ".htm");
	}

	public void showHtmlFile(L2Player player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("data/html/default/" + file);
		player.sendPacket(html);
	}
}