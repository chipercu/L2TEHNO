package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.TownManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import l2open.gameserver.serverpackets.ConfirmDlg;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Recall extends L2Skill
{
	private final int _townId;
	private final boolean _clanhall;
	private final boolean _castle;
	private final boolean _fortress;

	public Recall(StatsSet set)
	{
		super(set);
		_townId = set.getInteger("townId", 0);
		_clanhall = set.getBool("clanhall", false);
		_castle = set.getBool("castle", false);
		_fortress = set.getBool("fortress", false);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		// BSOE в кланхолл/замок работает только при наличии оного
		if(getHitTime() == 200)
		{
			L2Player player = activeChar.getPlayer();
			if(_clanhall)
			{
				if(player.getClan() == null || player.getClan().getHasHideout() == 0)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
					return false;
				}
			}
			else if(_castle)
			{
				if(player.getClan() == null || player.getClan().getHasCastle() == 0)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
					return false;
				}
			}
			else if(_fortress)
				if(player.getClan() == null || player.getClan().getHasFortress() == 0)
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
					return false;
				}
		}

		if(activeChar.isPlayer())
		{
			L2Player p = (L2Player) activeChar;
			if(p.isCombatFlagEquipped() || p.isTerritoryFlagEquipped())
			{
				activeChar.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
				return false;
			}
			if(p.getDuel() != null || p.getTeam() != 0)
			{
				activeChar.sendMessage(new CustomMessage("common.RecallInDuel", activeChar));
				return false;
			}
			if(RainbowSpringSiege.getInstance().isPlayerInArena(p))
				return false;
		}

		if(activeChar.isInZone(ZoneType.no_escape) || _townId > 0 && activeChar.getReflection().getCoreLoc() != null)
		{
			activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Recall.Here", activeChar));
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(final L2Character activeChar, GArray<L2Character> targets)
	{
		for(final L2Character target : targets)
			if(target != null)
			{
				final L2Player pcTarget = target.getPlayer();
				if(pcTarget == null)
					continue;

				if(pcTarget.isCombatFlagEquipped() || pcTarget.isTerritoryFlagEquipped())
				{
					activeChar.sendPacket(Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
					continue;
				}
				if(pcTarget.isFestivalParticipant())
				{
					activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Recall.Festival", activeChar));
					continue;
				}
				if(pcTarget.isInOlympiadMode())
				{
					activeChar.sendPacket(Msg.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
					return;
				}
				if(pcTarget.getDuel() != null || pcTarget.getTeam() != 0)
				{
					activeChar.sendMessage(new CustomMessage("common.RecallInDuel", activeChar));
					return;
				}
				if(pcTarget.isFlying() && (_townId > 0 || _clanhall || _castle || _fortress))
				{
					//activeChar.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Recall.Flying", activeChar));
					continue;
				}
				if(RainbowSpringSiege.getInstance().isPlayerInArena(pcTarget) || pcTarget.getVar("jailed") != null)
					return;

				if(ConfigValue.RecallToRequest && pcTarget.getVarB("party_recall_invite", false) && pcTarget != activeChar)
				{
					pcTarget.sendPacket(new ConfirmDlg(SystemMessage.S1, 30000, 6).addString(new CustomMessage("PartyRecallRequest", pcTarget).toString()));
					pcTarget.i_ai6 = System.currentTimeMillis() + 31000;
				}
				else
					ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
					{
						public void runImpl()
						{
							target.abortAttack(true, true);
							target.abortCast(true);
							target.stopMove();
							if(_isItemHandler)
							{
								//TODO: переделать SOE по TownId на SOE по Loc_id
								if(_itemConsumeId[0] == 7125) // floran
								{
									pcTarget.teleToLocation(17144, 170156, -3502, 0);
									return;
								}
								if(_itemConsumeId[0] == 7127) // hardin's academy
								{
									pcTarget.teleToLocation(105918, 109759, -3207, 0);
									return;
								}
								if(_itemConsumeId[0] == 7130) // ivory
								{
									pcTarget.teleToLocation(85475, 16087, -3672, 0);
									return;
								}
								if(_itemConsumeId[0] == 9716 || _itemConsumeId[0] == 12753) // Scroll of Escape: Kamael Village for starters
								{
									pcTarget.teleToLocation(-120000, 44500, 360, 0);
									return;
								}
								if(_itemConsumeId[0] == 7618)
								{
									pcTarget.teleToLocation(149864, -81062, -5618, 0);
									return;
								}
								if(_itemConsumeId[0] == 7619)
								{
									pcTarget.teleToLocation(108275, -53785, -2524, 0);
									return;
								}
								if(_itemConsumeId[0] == 14835)
								{
									pcTarget.teleToLocation(89417, -44506, -2126, 0);
									return;
								}
								if(_townId > 0) // SoE: Town
								{
									pcTarget.teleToLocation(TownManager.getInstance().getTown(_townId).getSpawn(), 0);
									return;
								}
								if(_castle) // SoE: Castle
								{
									pcTarget.teleToCastle();
									return;
								}
								if(_clanhall) // SoE: Clanhall
								{
									pcTarget.teleToClanhall();
									return;
								}
								if(_fortress) // SoE: Clanhall
								{
									pcTarget.teleToFortress();
									return;
								}
							}
							if(target.isInZone(ZoneType.battle_zone) && target.getZone(ZoneType.battle_zone).getRestartPoints() != null)
							{
								target.teleToLocation(target.getZone(ZoneType.battle_zone).getSpawn());
								return;
							}
							if(target.isInZone(ZoneType.peace_zone) && target.getZone(ZoneType.peace_zone).getRestartPoints() != null)
							{
								target.teleToLocation(target.getZone(ZoneType.peace_zone).getSpawn());
								return;
							}
							target.teleToClosestTown();
						}
					}, 100, true);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}