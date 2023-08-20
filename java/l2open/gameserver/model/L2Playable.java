package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.ai.DefaultAI;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.instancemanager.SiegeManager;
import l2open.gameserver.model.L2Skill.SkillTargetType;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.entity.Duel;
import l2open.gameserver.model.entity.Duel.DuelState;
import l2open.gameserver.model.instances.L2DoorInstance;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.instances.SeducedInvestigatorInstance;
import l2open.gameserver.model.items.Inventory;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.model.quest.QuestState;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.EffectType;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2CharTemplate;
import l2open.gameserver.templates.L2EtcItem;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.L2Weapon.WeaponType;
import l2open.util.AtomicState;
import l2open.util.GArray;
import l2open.util.GCSArray;
import l2open.util.Rnd;
import l2open.util.Util;
import l2open.util.reference.HardReference;

import java.util.Map.Entry;

import static l2open.gameserver.model.L2Zone.ZoneType.Siege;
import static l2open.gameserver.model.L2Zone.ZoneType.peace_zone;

public abstract class L2Playable extends L2Character
{
	private AtomicState _isSilentMoving;
	private long _checkAggroTimestamp = 0;

	public L2Playable(int objectId, L2CharTemplate template)
	{
		super(objectId, template);

		_isSilentMoving = new AtomicState(0, objectId, "_isSilentMoving", isPlayer());
	}

	public abstract Inventory getInventory();

	/**
	 * Проверяет, выставлять ли PvP флаг для игрока.<BR><BR>
	 */
	@Override
	public boolean checkPvP(final L2Character target, L2Skill skill)
	{
		L2Player player = getPlayer();

		if(isDead() || target == null || player == null || target == this || target == player || target == player.getPet() || player.getKarma() > 0)
			return false;

		if(skill != null)
		{
			if(skill.getSkillType().equals(SkillType.BEAST_FEED))
				return false;
			if(skill.getTargetType() == SkillTargetType.TARGET_UNLOCKABLE)
				return false;
			if(skill.getTargetType() == SkillTargetType.TARGET_CHEST)
				return false;
		}

		// Проверка на дуэли... Мэмбэры одной дуэли не флагаются
		if(getDuel() != null && getDuel() == target.getDuel())
			return false;

		if(isInZoneBattle() && target.isInZoneBattle())
			return false;
		if(isInZone(Siege) && target.isInZone(Siege))
			return false;
		if(skill == null || skill.isOffensive())
		{
			if(target.getKarma() > 0)
				return false;
			else if(target.isPlayable())
				return true;
		}
		else if(target.getPvpFlag() > 0 || target.getKarma() > 0 || target.isMonster() && !(target instanceof SeducedInvestigatorInstance))
			return true;

		return false;
	}

	/**
	 * Проверяет, можно ли атаковать цель (для физ атак)
	 */
	public boolean checkAttack(L2Character target)
	{
		L2Player player = getPlayer();
		if(player == null)
			return false;

		if(target == null || target.isDead())
		{
			player.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(!isInRangeZ(target, 2000))
		{
			player.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		if(target.isDoor() && !((L2DoorInstance) target).isAttackable(this))
		{
			player.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(target.paralizeOnAttack(player))
		{
			if(ConfigValue.ParalizeOnRaidLevelDiff)
				paralizeMe(target);
			return false;
		}

		if(!GeoEngine.canAttacTarget(this, target, false) || getReflection() != target.getReflection())
		{
			player.sendPacket(Msg.CANNOT_SEE_TARGET());
			//player.sendMessage("CANNOT_SEE_TARGET() 1");
			return false;
		}

		// Запрет на атаку мирных NPC в осадной зоне на TW. Иначе таким способом набивают очки.
		if(player.getTerritorySiege() > -1 && target.isNpc() && !(target instanceof L2TerritoryFlagInstance) && !(target.getAI() instanceof DefaultAI) && player.isInZone(ZoneType.Siege))
		{
			player.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		if(target.isPlayable())
		{
			// Нельзя атаковать того, кто находится на арене, если ты сам не на арене
			if(isInZoneBattle() != target.isInZoneBattle())
			{
				player.sendPacket(Msg.INVALID_TARGET());
				return false;
			}

			// Если цель либо атакующий находится в мирной зоне - атаковать нельзя
			if((isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack)
			{
				player.sendPacket(Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
				return false;
			}
			if(player.isInOlympiadMode() && !player.isOlympiadCompStart())
				return false;
		}

		return true;
	}

	@Override
	public void doAttack(L2Character target, boolean force)
	{
		//_log.info("L2Playable: doAttack->: [170]");
		L2Player player = getPlayer();
		if(player == null)
			return;

		if(isAMuted() || isAttackingNow())
		{
			player.sendActionFailed();
			return;
		}

		if(player.inObserverMode())
		{
			player.sendMessage(new CustomMessage("l2open.gameserver.model.L2Playable.OutOfControl.ObserverNoAttack", player));
			return;
		}

		if(!checkAttack(target))
		{
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
			player.sendActionFailed();
			return;
		}

		// Прерывать дуэли если цель не дуэлянт
		if(getDuel() != null)
			if(target.getDuel() != getDuel())
				getDuel().setDuelState(player, DuelState.Interrupted);
			else if(getDuel().getDuelState(player) == DuelState.Interrupted)
			{
				player.sendPacket(Msg.INVALID_TARGET());
				return;
			}

		L2Weapon weaponItem = getActiveWeaponItem();

		if(weaponItem != null && (weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW) && (getPlayer().getTransformation() == 0 || getPlayer().isTransformLalka() || !isPlayer()) && (getPlayer().getEventMaster() == null || !getPlayer().getEventMaster().attackFirst(getPlayer())))
		{
			if(!player.checkAndEquipArrows())
			{
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
				player.sendPacket(player.getActiveWeaponInstance().getItemType() == WeaponType.BOW ? Msg.YOU_HAVE_RUN_OUT_OF_ARROWS : Msg.NOT_ENOUGH_BOLTS);
				player.sendActionFailed();
				return;
			}

			double bowMpConsume = weaponItem.getMpConsume();
			if(bowMpConsume > 0)
			{
				// cheap shot SA
				double chance = calcStat(Stats.MP_USE_BOW_CHANCE, 0., target, null);
				if(chance > 0 && Rnd.chance(chance))
					bowMpConsume = calcStat(Stats.MP_USE_BOW, bowMpConsume, target, null);

				if(_currentMp < bowMpConsume)
				{
					getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
					player.sendPacket(Msg.NOT_ENOUGH_MP);
					player.sendActionFailed();
					return;
				}

				reduceCurrentMp(bowMpConsume, null);
			}
		}

		super.doAttack(target, force);
	}

	private GCSArray<QuestState> _NotifyQuestOfDeathList;
	private GCSArray<QuestState> _NotifyQuestOfPlayerKillList;

	public void addNotifyQuestOfDeath(QuestState qs)
	{
		if(qs == null || _NotifyQuestOfDeathList != null && _NotifyQuestOfDeathList.contains(qs))
			return;
		if(_NotifyQuestOfDeathList == null)
			_NotifyQuestOfDeathList = new GCSArray<QuestState>();
		_NotifyQuestOfDeathList.add(qs);
	}

	public void addNotifyOfPlayerKill(QuestState qs)
	{
		if(qs == null || _NotifyQuestOfPlayerKillList != null && _NotifyQuestOfPlayerKillList.contains(qs))
			return;
		if(_NotifyQuestOfPlayerKillList == null)
			_NotifyQuestOfPlayerKillList = new GCSArray<QuestState>();
		_NotifyQuestOfPlayerKillList.add(qs);
	}

	public void removeNotifyOfPlayerKill(QuestState qs)
	{
		if(qs == null || _NotifyQuestOfPlayerKillList == null)
			return;
		_NotifyQuestOfPlayerKillList.remove(qs);
		if(_NotifyQuestOfPlayerKillList.isEmpty())
			_NotifyQuestOfPlayerKillList = null;
	}

	public GCSArray<QuestState> getNotifyOfPlayerKillList()
	{
		return _NotifyQuestOfPlayerKillList;
	}

	@Override
	public void reduceCurrentHp(double i, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp) 
	{
		if(this != attacker && canReflect) 
		{
			L2Effect transferDam = getEffectList().getEffectByType(EffectType.TransferDam);
			if(transferDam != null)
			{
				L2Character effector = transferDam.getEffector();
				if(effector != this && !effector.isDead() && isInRange(effector, 1200))
				{
					boolean summon = (isPet() || isSummon());
					L2Player thisPlayer = getPlayer();
					L2Player effectorPlayer = effector.getPlayer();
					if((thisPlayer != null && effectorPlayer != null) && effectorPlayer.isOnline())
					{
						if(summon && (thisPlayer == effectorPlayer) || thisPlayer.isInParty() && thisPlayer.getParty() == effectorPlayer.getParty())
						{
							int transferDmg = 0;
							transferDmg = (int) i * (int) calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0, null, null) / 100;
							transferDmg = Math.min((int) effector.getCurrentHp() - 1, transferDmg);
							if(transferDmg > 0 && attacker.isPlayable())
							{
								effector.reduceCurrentHp(transferDmg, attacker, null, false, false, false, false, false, transferDmg, true, bow, false, false);
								i -= transferDmg;
							}
						}
					}
				}
			}
		}
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, isDot, i2, sendMesseg, bow, crit, tp);
	}

	@Override
	public void doDie(L2Character killer)
	{
		super.doDie(killer);

		if(killer != null)
		{
			L2Player pk = killer.getPlayer();
			L2Player player = getPlayer();
			if(pk != null && player != null)
			{
				L2Party party = pk.getParty();
				if(party == null)
				{
					GCSArray<QuestState> killList = pk.getNotifyOfPlayerKillList();
					if(killList != null)
						for(QuestState qs : killList)
							qs.getQuest().notifyPlayerKill(player, qs);
				}
				else
					for(L2Player member : party.getPartyMembers())
						if(member != null && member.isInRange(pk, 2000))
						{
							GCSArray<QuestState> killList = member.getNotifyOfPlayerKillList();
							if(killList != null)
								for(QuestState qs : killList)
									qs.getQuest().notifyPlayerKill(player, qs);
						}
			}
		}

		if(_NotifyQuestOfDeathList != null)
		{
			for(QuestState qs : _NotifyQuestOfDeathList)
				qs.getQuest().notifyDeath(killer, this, qs);
			_NotifyQuestOfDeathList = null;
		}
	}

	@Override
	public double getPAtkSpd()
	{
		return Math.max((calcStat(Stats.p_attack_speed, calcStat(Stats.ATK_BASE, _template.basePAtkSpd, null, null), null, null) / getArmourExpertisePenalty()), 1);
	}

	@Override
	public int getPAtk(final L2Character target)
	{
		double init = getActiveWeaponInstance() == null ? _template.basePAtk : 0;
		return (int) calcStat(Stats.p_physical_attack, init, target, null);
	}

	@Override
	public int getMAtk(final L2Character target, final L2Skill skill)
	{
		if(skill != null && skill.getMatak() > 0)
			return skill.getMatak();
		final double init = getActiveWeaponInstance() == null ? _template.baseMAtk : 0;
		return (int) calcStat(Stats.p_magical_attack, init, target, skill);
	}

	@Override
	public boolean isAttackable(L2Character attacker)
	{
		return checkTarget(attacker, true);
	}

	@Override
	public boolean isAutoAttackable(L2Character attacker)
	{
		return checkTarget(attacker, false);
	}

	private static void println(String text)
	{
		if(ConfigValue.DebugChecktargetPlayable)
			_log.info("L2Playable: "+text);
	}

	private boolean checkTarget(L2Character attacker, boolean force)
	{
		L2Player player = getPlayer();
		//println("checkTarget: ["+(attacker.getPlayer())+"]["+(!isSummon())+"]["+(!isPet())+"]");
		if(attacker == null || player == null || attacker == this || attacker.getPlayer() == player && !isSummon() && !isPet() || isDead() || attacker.isAlikeDead())
		{
			println("checkTarget(false): 1");
			return false;
		}
		else if(/*force && !GeoEngine.canAttacTarget(attacker, this, false) || */getReflection() != attacker.getReflection() || isInvisible())
		{
			println("checkTarget(false): 2");
			return false;
		}
		else if(isInVehicle())
		{
			println("checkTarget(false): 3");
			return false;
		}
		L2Player pcAttacker = attacker.getPlayer();
		L2Clan clan1 = player.getClan();
		if(pcAttacker != null)
		{
			Duel duel1 = player.getDuel();
			Duel duel2 = pcAttacker.getDuel();

			if(pcAttacker.isInVehicle())
			{
				println("checkTarget(false): 4");
				return false;
			}
			else if(pcAttacker.getBlockCheckerArena() > -1 || player.getBlockCheckerArena() > -1)
			{
				println("checkTarget(false): 5");
				return false;
			}
			else if(player != pcAttacker && duel1 != null && duel1 == duel2)
			{
				if(duel1.getTeamForPlayer(pcAttacker) == duel1.getTeamForPlayer(player))
				{
					println("checkTarget(false): 6");
					return false;
				}
				else if(duel1.getDuelState(player) != Duel.DuelState.Fighting)
				{
					println("checkTarget(false): 7");
					return false;
				}
				else if(duel1.getDuelState(pcAttacker) != Duel.DuelState.Fighting)
				{
					println("checkTarget(false): 8");
					return false;
				}
				println("checkTarget(true): 1");
				return true;
			}
			else if(player.getObjectId() == pcAttacker.getObjectId() && (isSummon() || isPet()))
			{
				println("checkTarget("+force+"): 2");
				return force;
			}
			else if(!force && duel1 != null && duel1 != duel2)
			{
				println("checkTarget(false): 9");
				return false;
			}
			else if(player.isInZone(L2Zone.ZoneType.epic) != pcAttacker.isInZone(L2Zone.ZoneType.epic))
			{
				println("checkTarget(false): 10");
				return false;
			}
			else if((player.isInOlympiadMode() || pcAttacker.isInOlympiadMode()) && (player.getOlympiadGame() != pcAttacker.getOlympiadGame()))
			{
				println("checkTarget(false): 11");
				return false;
			}
			else if(player.isInOlympiadMode() && !player.isOlympiadCompStart())
			{
				println("checkTarget(false): 12");
				return false;
			}
			else if(!force && player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcAttacker.getOlympiadSide())
			{
				println("checkTarget(false): 13");
				return false;
			}
			else if(pcAttacker.getTeam() > 0 && pcAttacker.isChecksForTeam() > 0 && player.getTeam() == 0)
			{
				println("checkTarget(false): 14");
				return false;
			}
			else if(player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcAttacker.getTeam() == 0)
			{
				println("checkTarget(false): 15");
				return false;
			}
			else if(player.getTeam() > 0 && player.isChecksForTeam() > 0 && pcAttacker.getTeam() > 0 && pcAttacker.isChecksForTeam() > 0 && player.getTeam() == pcAttacker.getTeam() && (pcAttacker.isChecksForTeam() > 1 || !force))
			{
				println("checkTarget(false): 16");
				return false;
			}
			else if(isInZoneBattle() != attacker.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack)
			{
				println("checkTarget(false): 17");
				return false;
			}
			else if(isInZonePeace() || pcAttacker.isInZonePeace() && !player.getPlayerAccess().PeaceAttack)
			{
				println("checkTarget(false): 18");
				return false;
			}
			else if(!force && player.getParty() != null && player.getParty() == pcAttacker.getParty())
			{
				println("checkTarget(false): 19");
				return false;
			}

			L2Zone set_fame = getZone(ZoneType.set_fame);
			if(set_fame != null && pcAttacker.getZone(ZoneType.set_fame) != null && set_fame._no_attack_time > 0 && (player.getOnlineTime() < set_fame._no_attack_time || pcAttacker.getOnlineTime() < set_fame._no_attack_time))
				return false;
			else if(isInZoneBattle() && pcAttacker.isInZoneBattle())
			{
				//println("checkTarget(true)("+getLoc()+isInZoneBattle()+")("+pcAttacker.getLoc()+pcAttacker.isInZoneBattle()+"): 3");
				//if(getPlayer().getName().equals("Arca"))
				//	Util.test();
				if(set_fame != null && !set_fame._batle && !force && player.getClanId() != 0 && player.getClanId() == pcAttacker.getClanId())
					return false;
				return true;
			}
			else if(!force && player.getClanId() != 0 && player.getClanId() == pcAttacker.getClanId())
			{
				println("checkTarget(false): 20");
				return false;
			}
			else if(!force && player.getClan() != null && pcAttacker.getClan() != null && player.getClan().getAllyId() != 0 && pcAttacker.getClan().getAllyId() != 0 && player.getClan().getAllyId() == pcAttacker.getClan().getAllyId())
			{
				println("checkTarget(false): 21");
				return false;
			}
			else if(isInZone(L2Zone.ZoneType.Siege) && attacker.isInZone(L2Zone.ZoneType.Siege))
			{
				L2Clan clan2 = pcAttacker.getClan();
				if(player.getTerritorySiege() > -1 && player.getTerritorySiege() == pcAttacker.getTerritorySiege())
				{
					println("checkTarget(false): 22");
					return false;
				}
				else if(clan1 == null || clan2 == null)
				{
					println("checkTarget(true): 4");
					return true;
				}
				else if(clan1.getSiege() == null || clan2.getSiege() == null)
				{
					println("checkTarget(true): 5");
					return true;
				}
				else if(clan1.getSiege() != clan2.getSiege())
				{
					println("checkTarget(true): 6");
					return true;
				}
				else if(clan1.isDefender() && clan2.isDefender())
				{
					println("checkTarget(false): 23");
					return false;
				}
				else if(clan1.getSiege().isMidVictory())
				{
					println("checkTarget(true): 7");
					return true;
				}
				else if(clan1.isAttacker() && clan2.isAttacker())
				{
					println("checkTarget(false): 24");
					return false;
				}
				println("checkTarget(true): 8");
				return true;
			}
			else if(pcAttacker.atMutualWarWith(player) || pcAttacker.isFactionWar(player))
			{
				println("checkTarget(true): 9");
				return true;
			}
			else if(player.getKarma() > 0 && !force)
			{
				println("checkTarget(true): 10");
				return true;
			}
			else if(player.getPvpFlag() != 0 && !force)
			{
				println("checkTarget(true): 11");
				return true;
			}
			else if(!force && pcAttacker.getPvpFlag() == 0 && player.getPvpFlag() != 0 && pcAttacker.getAI() != null && pcAttacker.getAI().getAttackTarget() != this) 
			{
				println("checkTarget(false): 25");
				return false;
			}
			println("checkTarget("+force+"): force");
			return force;
		}
		else if(attacker.isSiegeGuard() && clan1 != null && clan1.isDefender() && SiegeManager.getSiege(this, true) == clan1.getSiege())
		{
			println("checkTarget(false): 26");
			return false;
		}
		else if(!force && isInZonePeace()) // Гварды с пикой, будут атаковать только одиночные цели в городе 
		{
			println("checkTarget(false): 27");
			return false;
		}
		println("checkTarget(true): 12");
		return true;
	} 


	@Override
	public int getKarma()
	{
		L2Player player = getPlayer();
		return player == null ? 0 : player.getKarma();
	}

	@Override
	public void callSkill(L2Skill skill, GArray<L2Character> targets, boolean useActionSkills)
	{
		L2Player player = getPlayer();
		if(player == null)
			return;

		GArray<L2Character> toRemove = new GArray<L2Character>();

		if(useActionSkills && !skill.getSkillType().equals(SkillType.BEAST_FEED))
			for(L2Character target : targets)
			{
				if(target == null)
					continue;
				else if((target.isPetrification() && skill.getId() != 1551 && skill.getId() != 1018 || target.isInvul()) && skill.isOffensive() && !skill.isCancel() && skill.getSkillType() != SkillType.STEAL_BUFF || (target.isInvul() && target.isPlayer() && ((L2Player) target).isGM()))
				{
					if(isPlayer())
						sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(skill._displayId, skill._displayLevel));
					toRemove.add(target);
				}

				if(!skill.isOffensive())
				{
					if(target.isPlayable() && target != getPet() && !(this instanceof L2Summon && target == player))
					{
						int aggro = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : Math.max(1, (int) skill.getPower());
						for(Entry<L2NpcInstance, HateInfo> entry : target.getHateList().entrySet())
							if(entry.getKey() != null && !entry.getKey().isDead() && entry.getValue().hate > 0 && entry.getKey().isInRange(this, 2000) && entry.getKey().getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
							{
								if(!skill.isHandler() && entry.getKey().paralizeOnAttack(player))
								{
									if(ConfigValue.ParalizeOnRaidLevelDiff)
										paralizeMe(entry.getKey());
									return;
								}
								entry.getKey().getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
								if(GeoEngine.canAttacTarget(entry.getKey(), target, false)) // Моб агрится только если видит цель, которую лечишь/бафаешь.
									entry.getKey().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, aggro);
							}
					}
				}
				else if(target.isNpc())
				{
					// mobs will hate on debuff
					if(target.paralizeOnAttack(player))
					{
						if(ConfigValue.ParalizeOnRaidLevelDiff)
							paralizeMe(target);
						return;
					}
					target.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
					int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int)skill.getPower();
					if(!skill.isAI() && damage != 0)
					{
						target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, new Object[] { this, damage, skill });
						target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, damage);
					}
				}
				// Check for PvP Flagging / Drawing Aggro
				if(checkPvP(target, skill))
					startPvPFlag(target);
			}

		for(L2Character cha : toRemove)
			targets.remove(cha);

		super.callSkill(skill, targets, useActionSkills);
	}

	@Override
	public void setXYZ(int x, int y, int z, boolean MoveTask)
	{
		//_log.info("L2Playable: "+x+","+y+","+z);
		super.setXYZ(x, y, z, MoveTask);
		L2Player player = getPlayer();

		if(!MoveTask || player == null || isAlikeDead() || isInvul() || !isVisible() || getCurrentRegion() == null)
			return;

		long now = System.currentTimeMillis();
		if(now - _checkAggroTimestamp < ConfigValue.AggroCheckInterval || player.getNonAggroTime() > now)
			return;

		_checkAggroTimestamp = now;
		if(getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW && (!isPlayer() || getFollowTarget() != null && getFollowTarget().getPlayer() != null && !getFollowTarget().getPlayer().isSilentMoving()))
			return;

		for(L2NpcInstance obj : L2World.getAroundNpc(this))
			if(obj != null)
				obj.getAI().checkAggression(this);
	}

	/**
	 * Оповещает других игроков о поднятии вещи
	 * @param item предмет который был поднят
	 */
	public void broadcastPickUpMsg(L2ItemInstance item)
	{
		L2Player player = getPlayer();

		if(item == null || player == null || player.isInvisible())
			return;

		if(item.isEquipable() && !(item.getItem() instanceof L2EtcItem))
		{
			SystemMessage msg = null;
			String player_name = player.getName();

			if(player.getEventMaster() != null)
				player_name = player.getEventMaster().getCharName(player);

			if(item.getRealEnchantLevel() > 0)
			{
				int msg_id = isPlayer() ? SystemMessage.ATTENTION_S1_PICKED_UP__S2_S3 : SystemMessage.ATTENTION_S1_PET_PICKED_UP__S2_S3;
				msg = new SystemMessage(msg_id).addString(player_name).addNumber(item.getRealEnchantLevel()).addItemName(item.getItemId());
			}
			else
			{
				int msg_id = isPlayer() ? SystemMessage.ATTENTION_S1_PICKED_UP_S2 : SystemMessage.ATTENTION_S1_PET_PICKED_UP__S2_S3;
				msg = new SystemMessage(msg_id).addString(player_name).addItemName(item.getItemId());
			}
			player.broadcastPacket(msg);
		}
	}

	public void paralizeMe(L2Character effector)
	{
		L2Skill revengeSkill = SkillTable.getInstance().getInfo(L2Skill.SKILL_RAID_CURSE, 1);
		L2Player player = getPlayer();
		if(player != this)
			revengeSkill.getEffects(effector, this, false, false);
		if(player != null)
			revengeSkill.getEffects(effector, player, false, false);
	}

	public boolean startSilentMoving()
	{
		return _isSilentMoving.getAndSet(true);
	}

	public boolean stopSilentMoving()
	{
		return _isSilentMoving.setAndGet(false);
	}

	/**
	 * @return True if the Silent Moving mode is active.<BR><BR>
	 */
	public boolean isSilentMoving()
	{
		return _isSilentMoving.get();
	}

	@Override
	public boolean isPlayable()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<? extends L2Playable> getRef()
	{
		return (HardReference<? extends L2Playable>) super.getRef();
	}
}