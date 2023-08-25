package l2open.gameserver.skills.skillclasses;

import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Clan;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.entity.residence.ResidenceType;
import l2open.gameserver.model.entity.siege.Siege;
import l2open.gameserver.model.entity.siege.SiegeClan;
import l2open.gameserver.model.entity.siege.castle.CastleSiege;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2PetInstance;
import l2open.gameserver.skills.Formulas;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class Resurrect extends L2Skill
{
	public Resurrect(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final L2Character activeChar, final L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayable())
			return false;

		if(target == null || target != activeChar && !target.isDead())
		{
			activeChar.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		L2Player player = activeChar.getPlayer();
		L2Player pcTarget = target.getPlayer();

		if(pcTarget == null)
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		if(player.isInOlympiadMode() || pcTarget.isInOlympiadMode())
		{
			player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		if(pcTarget.getEventMaster() == null || pcTarget.getEventMaster()._ref == null || pcTarget.getEventMaster()._ref.getId() != pcTarget.getReflectionId())
		{
			if(TerritorySiege.checkIfInZone(target))
			{
				if(pcTarget.getTerritorySiege() == -1) // Не зарегистрирован на осаду
				{
					activeChar.sendPacket(Msg.IT_IS_IMPOSSIBLE_TO_BE_RESSURECTED_IN_BATTLEFIELDS_WHERE_SIEGE_WARS_ARE_IN_PROCESS);
					return false;
				}

				L2Clan clan = pcTarget.getClan();
				SiegeClan siegeClan = TerritorySiege.getSiegeClan(clan);
				if(siegeClan == null || siegeClan.getHeadquarter() == null) // Возможно, стоит разрешить воскрешаться одиночкам
				{
					activeChar.sendPacket(Msg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
					return false;
				}
			}

			Siege siege = SiegeManager.getSiege(target, true);
			if(siege != null)
			{
				L2Clan clan = pcTarget.getClan();
				if(clan == null || clan.getSiege() == null || clan.getSiege() != siege) // Не зарегистрирован на осаду
				{
					activeChar.sendPacket(Msg.IT_IS_IMPOSSIBLE_TO_BE_RESSURECTED_IN_BATTLEFIELDS_WHERE_SIEGE_WARS_ARE_IN_PROCESS);
					return false;
				}

				// Атакующая сторона, проверка на наличие флага
				SiegeClan attackClan = siege.getAttackerClan(clan);
				if(attackClan != null && attackClan.getHeadquarter() == null)
				{
					activeChar.sendPacket(Msg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
					return false;
				}

				// Защищающая сторона, проверка на наличие кристалов в замке
				if(siege.checkIsDefender(clan) && siege.getSiegeUnit().getType() == ResidenceType.Castle && ((CastleSiege) siege).isAllTowersDead())
				{
					activeChar.sendPacket(Msg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
					return false;
				}
			}
		}
		else if(pcTarget.getEventMaster().siege_event)
		{
			// Атакующая сторона, проверка на наличие флага
			SiegeClan attackClan = pcTarget.getEventMaster().getSiegeClan(pcTarget.getPlayer());
			if(attackClan != null && attackClan.getHeadquarter() == null || pcTarget.getEventMaster()._defender_clan != null && pcTarget.getEventMaster()._defender_clan.getClanId() == pcTarget.getClanId())
			{
				activeChar.sendPacket(Msg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
				return false;
			}
		}

		if(oneTarget(activeChar))
			if(target.isPlayer())
			{
				if(_targetType == SkillTargetType.TARGET_PET)
				{
					player.sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
					return false;
				}
				// Check to see if the player is in a festival.
				if(pcTarget.isFestivalParticipant())
				{
					player.sendMessage(new CustomMessage("l2open.gameserver.skills.skillclasses.Resurrect", player));
					return false;
				}
			}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		double percent = _power;

		if(percent < 100 && !isHandler())
		{
			double wit_bonus = _power * (Formulas.WITbonus[activeChar.getWIT()] - 1);
			percent += wit_bonus > 20 ? 20 : wit_bonus;
			if(percent > 90)
				percent = 90;
		}

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.getPlayer() == null)
					continue;

				if(target.isPet())
				{
					if(target.getPlayer() == activeChar)
						((L2PetInstance) target).doRevive(percent);
					else
						target.getPlayer().reviveRequest(activeChar.getPlayer(), percent, true);
				}
				else if(target.isPlayer())
				{
					if(_targetType == SkillTargetType.TARGET_PET)
						continue;

					L2Player targetPlayer = (L2Player) target;

					if(targetPlayer.isFestivalParticipant())
						continue;

					if(activeChar.getPlayer().getAttainment() != null)
						activeChar.getPlayer().getAttainment().char_resurection(target);
					targetPlayer.reviveRequest(activeChar.getPlayer(), percent, false);
				}
				else
					continue;

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}