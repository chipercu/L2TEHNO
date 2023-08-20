package l2open.gameserver.model;

import l2open.config.ConfigValue;
import l2open.common.RunnableImpl;
import l2open.common.ThreadPoolManager;
import l2open.extensions.multilang.CustomMessage;
import l2open.gameserver.ai.CtrlEvent;
import l2open.gameserver.ai.CtrlIntention;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.geodata.GeoEngine;
import l2open.gameserver.model.L2Zone.ZoneType;
import l2open.gameserver.model.L2Skill.SkillType;
import l2open.gameserver.model.entity.Hero;
import l2open.gameserver.model.entity.siege.territory.TerritorySiege;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.gameserver.model.instances.L2TerritoryFlagInstance;
import l2open.gameserver.model.instances.L2TrapInstance;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2open.util.GArray;
import l2open.util.Location;
import l2open.util.Log;
import l2open.util.Rnd;
import l2open.util.reference.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class L2ObjectTasks
{
	static final Logger _log = Logger.getLogger(L2ObjectTasks.class.getName());

	// ============================ Таски для L2Player ==============================
	public static class WaterTaskZ extends l2open.common.RunnableImpl
	{
		private HardReference<? extends L2Character> owner_ref;
		private int z_dest;

		public WaterTaskZ(L2Character _char, int z_d)
		{
			owner_ref = _char.getRef();
			z_dest = z_d;
		}

		public void runImpl()
		{
			L2Character _char = owner_ref.get();
			if(_char == null || !_char.isMoving)
				return;
			_char.broadcastPacket(new CharMoveToLocation(_char, z_dest, false));
			_char._moveWaterTask = ThreadPoolManager.getInstance().scheduleMV(new WaterTaskZ(_char, z_dest), 800);
		}
	}

	public static class AutoCpTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> owner_ref;
		private int _type;

		public AutoCpTask(L2Player player, int type)
		{
			owner_ref = player.getRef();
			_type = type;
		}

		public void runImpl()
		{
			L2Player pl = owner_ref.get();
			if(pl == null)
				return;
			pl.autoCpStart(_type, true);
		}
	}

	public static class BotCheck extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> owner_ref;
		private final boolean _kick;

		public BotCheck(L2Player player, boolean kick)
		{
			owner_ref = player.getRef();
			_kick = kick;
		}

		public void runImpl()
		{
			L2Player pl = owner_ref.get();
			if(pl == null)
				return;
			if(_kick)
				pl.logout(false, false, false, true);
			else
				pl.botCheck();
		}
	}

	public static class EnchantResetCount extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> owner_ref;

		public EnchantResetCount(L2Player player)
		{
			owner_ref = player.getRef();
		}

		public void runImpl()
		{
			L2Player pl = owner_ref.get();
			if(pl == null)
				return;
			pl._enchantCount = 0;
		}
	}

	public static class SoulConsumeTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public SoulConsumeTask(L2Player player)
		{
			_player =  player.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.setConsumedSouls(pl.getConsumedSouls() + 1, null);
		}
	}

	public static class ReturnTerritoryFlagTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public ReturnTerritoryFlagTask(L2Player player)
		{
			_player =  player.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			if(pl.isTerritoryFlagEquipped())
			{
				L2ItemInstance flag = pl.getActiveWeaponInstance();
				if(flag != null && flag.getCustomType1() != 77) // 77 это эвентовый флаг
				{
					L2TerritoryFlagInstance flagNpc = TerritorySiege.getNpcFlagByItemId(flag.getItemId());
					flagNpc.returnToCastle(pl);
				}
			}
		}
	}

	public static class ReturnTerritoryFlagTaskDroped extends l2open.common.RunnableImpl
	{
		private final HardReference<L2TerritoryFlagInstance> _flag;

		public ReturnTerritoryFlagTaskDroped(L2TerritoryFlagInstance flag)
		{
			_flag = flag.getRef();
		}

		public void runImpl()
		{
			L2TerritoryFlagInstance flag = _flag.get();
			if(flag == null)
				return;
			TerritorySiege.removeFlag(flag);
			TerritorySiege.spawnFlags(flag.getBaseTerritoryId()); // Заспавнит только нужный нам флаг в замке
			flag.deleteMe();
			//TerritorySiege.setWardLoc(flag.getBaseTerritoryId(), flag.getLoc());
		}
	}

	/** PvPFlagTask */
	public static class PvPFlagTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public PvPFlagTask(L2Player player)
		{
			_player =  player.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			try
			{
				long diff = Math.abs(System.currentTimeMillis() - pl.getlastPvpAttack());
				if(diff > ConfigValue.PvPTime)
					pl.stopPvPFlag();
				else if(diff > ConfigValue.PvPTime - 20000)
					pl.updatePvPFlag(2);
				else
					pl.updatePvPFlag(1);
			}
			catch(Exception e)
			{
				_log.log(Level.WARNING, "error in pvp flag task:", e);
			}
		}
	}

	/** LookingForFishTask */
	public static class LookingForFishTask extends l2open.common.RunnableImpl
	{
		boolean _isNoob, _isUpperGrade;
		int _fishType, _fishGutsCheck, _gutsCheckTime;
		long _endTaskTime;
		private HardReference<L2Player> _player;

		protected LookingForFishTask(L2Player player, int fishWaitTime, int fishGutsCheck, int fishType, boolean isNoob, boolean isUpperGrade)
		{
			_fishGutsCheck = fishGutsCheck;
			_endTaskTime = System.currentTimeMillis() + fishWaitTime + 10000;
			_fishType = fishType;
			_isNoob = isNoob;
			_isUpperGrade = isUpperGrade;
			_player =  player.getRef();
		}

		@Override
		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			if(System.currentTimeMillis() >= _endTaskTime)
			{
				pl.endFishing(false);
				return;
			}
			if(_fishType == -1)
				return;
			int check = Rnd.get(1000);
			if(_fishGutsCheck > check)
			{
				pl.stopLookingForFishTask();
				pl.startFishCombat(_isNoob, _isUpperGrade);
			}
		}
	}

	/** BonusTask */
	public static class BonusTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public BonusTask(L2Player player)
		{
			_player =  player.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null || pl.getNetConnection() == null)
				return;
			pl.getNetConnection().setBonus(1);
			pl.restoreBonus();
			if(pl.getParty() != null)
				pl.getParty().recalculatePartyData();
			String msg = new CustomMessage("scripts.services.RateBonus.LuckEnded", pl).toString();
			pl.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true), new ExBrPremiumState(pl, 0));
			pl.sendMessage(msg);
		}
	}

	/** BonusTask */
	public static class BonusTask2 extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;
		private final int _type;

		public BonusTask2(L2Player player, int type)
		{
			_player =  player.getRef();
			_type = type;
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null || pl.getNetConnection() == null)
				return;
			if(_type == 10)
				pl.getBonus().CanByTradeItemPA = false;
			else if(_type == 11)
				pl.getBonus().EventSponsor = false;
			else if(_type == 12)
				pl.getBonus().PremiumBuffer = false;
			else
			{
				pl.getNetConnection().setBonus(1);
				pl.restoreBonus();
				if(pl.getParty() != null)
					pl.getParty().recalculatePartyData();
				pl.sendPacket(new ExBrPremiumState(pl, 0));
			}
			if(_type != 11 && _type != 12)
			{
				String msg = new CustomMessage("scripts.services.RateBonus.LuckEnded", pl).toString();
				pl.sendMessage(msg);
				pl.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true));
			}
		}
	}

	/** WaterTask */
	public static class WaterTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public WaterTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			if(pl.isDead() || !pl.isInZone(ZoneType.water))
			{
				pl.stopWaterTask();
				return;
			}

			double reduceHp = pl.getMaxHp() < 100 ? 1 : pl.getMaxHp() / 100;
			pl.reduceCurrentHp(reduceHp, pl, null, false, false, true, false, false, reduceHp, true, false, false, false);
			pl.sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE).addNumber((long) reduceHp));
		}
	}

	/** KickTask */
	public static class KickTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public KickTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.setOfflineMode(false);
			pl.logout(false, false, true, true);
		}
	}

	public static class UnsetHero extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;
		private int _type;

		public UnsetHero(L2Player pl, int type)
		{
			_player =  pl.getRef();
			_type = type;
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.setHero(false, -1);
			Hero.removeSkills(pl);
			pl.sendPacket(new SkillList(pl));
			if(_type == 1)
				pl.unsetVar("HeroEvent");
			else
				pl.unsetVar("HeroPremium"); 
			pl.broadcastUserInfo(true);
		}
	}

	/** TeleportTask */
	public static class TeleportTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;
		private Location _loc;
		int _reflection; //TODO unused ?

		public TeleportTask(L2Player pl, Location p, int reflection)
		{
			_player =  pl.getRef();
			_loc = p;
			_reflection = reflection;
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.teleToLocation(ConfigValue.UnJailLocation.length == 0 ? _loc : new Location(ConfigValue.UnJailLocation), _reflection);
			pl.unsetVar("jailed"); 
			pl.unsetVar("jailedFrom");
			pl.unsetVar("reflection"); 
		}
	}

	/** UserInfoTask */
	public static class UserInfoTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public UserInfoTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.sendPacket(new UserInfo(pl), new ExBrExtraUserInfo(pl), new ExVoteSystemInfo(pl));
			pl._userInfoTask = null;
		}
	}

	/** BroadcastCharInfoTask */
	public static class BroadcastCharInfoTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public BroadcastCharInfoTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.broadcastCharInfo();
			pl._broadcastCharInfoTask = null;
		}
	}

	/** EndSitDownTask */
	public static class EndSitDownTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public EndSitDownTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.sittingTaskLaunched = false;
			pl.getAI().clearNextAction();
		}
	}

	/** EndStandUpTask */
	public static class EndStandUpTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public EndStandUpTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl.sittingTaskLaunched = false;
			pl._isSitting = false;
			if(!pl.getAI().setNextIntention())
				pl.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}

	/** InventoryEnableTask */
	public static class InventoryEnableTask extends l2open.common.RunnableImpl
	{
		private HardReference<L2Player> _player;

		public InventoryEnableTask(L2Player pl)
		{
			_player =  pl.getRef();
		}

		public void runImpl()
		{
			L2Player pl = _player.get();
			if(pl == null)
				return;
			pl._inventoryDisable = false;
		}
	}

	// ============================ Таски для L2Character ==============================

	/** AltMagicUseTask */
	public static class AltMagicUseTask extends l2open.common.RunnableImpl
	{
		public final L2Skill _skill;
		private final HardReference<? extends L2Character> cha;
		private final HardReference<? extends L2Character> _target;

		public AltMagicUseTask(L2Character character, L2Character target, L2Skill skill)
		{
			cha = character.getRef();
			_target = target.getRef();
			_skill = skill;
		}

		public void runImpl()
		{
			L2Character character = cha.get();
			L2Character target = _target.get();
			if(character == null || target == null)
				return;
			character.altOnMagicUseTimer(target, _skill);
		}
	}

	/** CancelAttackStanceTask */
	public static class CancelAttackStanceTask extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2Character> character;

		public CancelAttackStanceTask(L2Character cha)
		{
			character = cha.getRef();
		}

		public void runImpl()
		{
			L2Character cha = character.get();
			if(cha == null)
				return;
			cha.stopAttackStanceTask();
		}
	}

	/** Task lauching the function enableSkill() */
	public static class EnableSkillTask extends l2open.common.RunnableImpl
	{
		private final long _skillId;
		private final HardReference<? extends L2Character> character;

		public EnableSkillTask(L2Character cha, long skillId)
		{
			character = cha.getRef();
			_skillId = skillId;
		}

		@Override
		public void runImpl()
		{
			try
			{
				L2Character cha = character.get();
				if(cha == null)
					return;
				cha.enableSkill(_skillId);
			}
			catch(Throwable e)
			{
				_log.log(Level.SEVERE, "", e);
			}
		}
	}

	/** CastEndTimeTask */
	public static class CastEndTimeTask extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2Character> character;
		private final HardReference<? extends L2Character> target;
		private L2Skill skill;
		private boolean forceUse;

		public CastEndTimeTask(L2Skill _skill, L2Character charact, L2Character targett, boolean fo)
		{
			skill = _skill;
			character = charact.getRef();
			if(targett == null)
				target = HardReferences.emptyRef();
			else
				target = targett.getRef();
			forceUse=fo;
		}

		public void runImpl()
		{
			L2Character cha = character.get();
			if(cha == null)
				return;
			cha.onCastEndTime(skill, cha, target.get(), forceUse);
		}
	}

	/** HitTask */
	public static class HitTask extends l2open.common.RunnableImpl
	{
		boolean _crit, _miss, _shld, _soulshot, _unchargeSS, _notify, _bow;
		int _damage;
		int _sAtk;
		private final HardReference<? extends L2Character> character;
		private final HardReference<? extends L2Character> target;

		public HitTask(L2Character cha, L2Character targt, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS, boolean notify, boolean bow, int sAtk)
		{
			character = cha.getRef();
			target = targt.getRef();
			_damage = damage;
			_crit = crit;
			_shld = shld;
			_miss = miss;
			_soulshot = soulshot;
			_unchargeSS = unchargeSS;
			_notify = notify;
			_bow = bow;
			_sAtk = sAtk;
		}

		public void runImpl()
		{
			L2Character cha = character.get();
			L2Character _target = target.get();
			if(cha == null || _target == null || cha.isAttackAborted())
				return;
	
			cha.onHitTimer(_target, _damage, _crit, _miss, _soulshot, _shld, _unchargeSS, _bow);
			
			if(_notify)
				ThreadPoolManager.getInstance().schedule(new NotifyAITask(cha, CtrlEvent.EVT_READY_TO_ACT, null, null), _sAtk);
				//cha.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
		}
	}

	/** Task launching the function onMagicUseTimer() */
	public static class MagicUseTask extends l2open.common.RunnableImpl
	{
		public boolean _forceUse;
		private final HardReference<? extends L2Character> character;
		private final L2Skill _skill;

		public MagicUseTask(L2Character cha, L2Skill skill, boolean forceUse)
		{
			character = cha.getRef();
			_forceUse = forceUse;
			_skill = skill;
		}

		public void runImpl()
		{
			L2Character cha = character.get();
			if(cha == null)
				return;
			if((cha.isPet() || cha.isSummon()) && cha.getPlayer() == null)
			{
				cha.clearCastVars();
				return;
			}
			else if(_skill.getHpConsume() > 0)
			{
				if(cha.getCurrentHp() < _skill.getHpConsume() + 1)
				{
					cha.sendPacket(Msg.NOT_ENOUGH_HP);
					cha.abortCast(true);
					return;
				}
				cha.setCurrentHp(Math.max(0, cha.getCurrentHp() - _skill.getHpConsume()), false);
			}

			double mpConsume2 = _skill.getMpConsume2();
			if(mpConsume2 > 0)
			{
				if(_skill.getFlyType() == FlyToLocation.FlyType.NONE || _skill.getFlyType() == FlyToLocation.FlyType.DUMMY)
				{
					if(_skill.isMusic())
					{
						mpConsume2 += cha.getEffectList().getActiveMusicCount(0)*mpConsume2/2;
						mpConsume2 = cha.calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, cha, _skill);
					}
					else if(_skill.isMagic())
						mpConsume2 = cha.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, cha, _skill);
					else
						mpConsume2 = cha.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, cha, _skill);
				}

				if(cha.getCurrentMp() < mpConsume2 && cha.isPlayable())
				{
					cha.sendPacket(Msg.NOT_ENOUGH_MP);
					//cha.onCastEndTime(_skill, cha, _forceUse);
					cha.abortCast(true);
					return;
				}
				cha.reduceCurrentMp(mpConsume2, null);
			}
			//if(cha.getCastingSkill() ==  null)
			//	_log.info("L2ObjectTasks: -> MagicUseTask: -> Owner: '"+cha+"'  getCastingSkill == null PrevSkill: "+cha.getCastingSkill2());
			cha.onMagicUseTimer(cha.getCastingTarget(), cha.getCastingSkill(), _forceUse);
		}
	}

	/** MagicLaunchedTask */
	public static class MagicLaunchedTask extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2Character> character;
		private final HardReference<? extends L2Character> attack_target;
		private final L2Skill _skill;
		private final boolean _forceUse;

		public MagicLaunchedTask(L2Character cha, L2Skill skill, L2Character attack_t, boolean forceUse)
		{
			character = cha.getRef();
			_skill = skill;
			attack_target = attack_t.getRef();
			_forceUse = forceUse;
		}

		public void runImpl()
		{
			L2Character cha = character.get();
			L2Character cha_tar = attack_target.get();
			if(cha == null)
				return;
			L2Skill castingSkill = cha.getCastingSkill();
			if(castingSkill == null || (cha.isPet() || cha.isSummon()) && cha.getPlayer() == null)
			{
				cha.clearCastVars();
				return;
			}

			// Если цель убежала за макс дистанцию каста то не дамажим.
			if(_skill.getEffectiveRange() > 0 && cha.getDistance(cha_tar) > (_skill.getEffectiveRange()+cha.getMinDistance(cha_tar)))
			{
				cha.sendPacket(Msg.CANNOT_SEE_TARGET());
				//cha.sendMessage("CANNOT_SEE_TARGET() 10: ["+_skill.getEffectiveRange()+"]["+cha.getDistance(cha_tar)+"]["+cha.getDistance3D(cha_tar)+"]["+cha.getMinDistance(cha_tar)+"]");
				cha.sendActionFailed();
				cha.abortCast(true, false);
				return;
			}

			if(!_skill.checkCondition(cha, cha_tar, _forceUse, false, false))
			{
				//cha.onCastEndTime(_skill, cha, _forceUse);
				cha.abortCast(true);
				return;
			}
			if(_skill.getCastRange() != -2 && _skill.getSkillType() != SkillType.TAKECASTLE && _skill.getSkillType() != SkillType.TAKEFORTRESS && !GeoEngine.canAttacTarget(cha, cha_tar, cha.isFlying()))
			{
				cha.sendPacket(Msg.CANNOT_SEE_TARGET());
				cha.broadcastSkill(new MagicSkillCanceled(cha.getObjectId()), true);
				//cha.onCastEndTime(_skill, cha, _forceUse);
				cha.abortCast(true);
				//cha.sendMessage("CANNOT_SEE_TARGET() 0");
				return;
			}
			switch(_skill.getFlyType())
			{
				case THROW_UP:
				case THROW_HORIZONTAL:
				{
					Location flyLoc;
					for(L2Character target : _skill.getTargets(cha, cha_tar, _forceUse))
					{
						target.setHeading(cha.getHeading());
						flyLoc = cha.getFlyLocation(null, _skill);
						cha.broadcastPacket(new FlyToLocation(target, flyLoc, _skill.getFlyType()));
						target.setLoc(flyLoc);
					}
					break;
				}
			}
			// Только CHARGE, DUMMY уже в конце каста срабатывает
			if(_skill.getFlyType() == FlyToLocation.FlyType.CHARGE)
			{
				cha.setFlyLoc(null);
				if(_skill.getSkillType() == SkillType.PDAM && !_skill.hasEffects() && Rnd.chance(cha_tar.calcStat(Stats.PSKILL_EVASION, 0, cha, _skill)))
				{
					cha.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(cha));
					cha_tar.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(cha_tar).addName(cha));
				}
				else
				{
					Location flyLoc = cha.getFlyLocation(cha_tar, _skill);
					if(flyLoc != null)
					{
						cha.setFlyLoc(flyLoc);
						cha.broadcastPacket(new FlyToLocation(cha, flyLoc, _skill.getFlyType()));
						cha.setLoc(flyLoc);
						cha.setHeading(cha_tar.getHeading());
					}
					else
					{
						cha.sendPacket(Msg.CANNOT_SEE_TARGET());
						//cha.sendMessage("CANNOT_SEE_TARGET() 9");
						return;
					}
				}
			}
			cha.broadcastSkill(new MagicSkillLaunched(cha.getObjectId(), castingSkill.getDisplayId(), castingSkill.getDisplayLevel(), castingSkill.getTargets(cha, cha.getCastingTarget(), _forceUse), castingSkill.isOffensive()), true);
		}
	}

	/** Task of AI notification */
	public static class NotifyAITask extends l2open.common.RunnableImpl
	{
		private final CtrlEvent _evt;
		private final Object _agr0;
		private final Object _agr1;
		private final HardReference<? extends L2Character> _cha;

		public NotifyAITask(L2Character cha, CtrlEvent evt, Object agr0, Object agr1)
		{
			_cha = cha.getRef();
			_evt = evt;
			_agr0 = agr0;
			_agr1 = agr1;
			/*if(cha.getNpcId() == 22323 || cha.getNpcId() == 22659 || cha.getNpcId() == 22658)
			{
				_log.info("L2ObjectTasks: NotifyAITask->: id["+cha.getNpcId()+"]["+cha.getFollowTarget()+"]["+cha.getObjectId()+"] evt="+evt);
				l2open.util.Util.test();
			}*/
		}

		public void runImpl()
		{
			L2Character cha = _cha.get();
			if(cha == null || (cha.isPet() || cha.isSummon()) && cha.getPlayer() == null)
				return;
			try
			{
				cha.getAI().notifyEvent(_evt, _agr0, _agr1);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	public static class MoveNextTask extends l2open.common.RunnableImpl
	{
        private double alldist;
        private double donedist;
        private Location begin;
		private HardReference<? extends L2Character> owner_ref;

		public MoveNextTask(L2Character _character)
		{
			owner_ref = _character.getRef();
		}

		public void updateStoreId(L2Character _character)
		{
			owner_ref = _character.getRef();
		}

		public MoveNextTask setDist(double dist, Location startLoc)
		{
            alldist = dist;
            donedist = 0;
            begin = startLoc;
            return this;
        }

		public void runImpl()
		{
			L2Character follow_target = null, character = owner_ref.get();
			if(character == null || !character.isMoving)
				return;

			if(ConfigValue.NewGeoEngine)
			{
				character.moveLock.lock();
				try
				{
					if(!character.isMoving)
						return;

					if(character.isMovementDisabled())
					{
						character.stopMove();
						return;
					}

					float speed = character.getMoveSpeed();
					if (speed <= 0)
					{
						character.stopMove();
						return;
					}
					long now = System.currentTimeMillis();
					if (character.isFollow)
					{
						follow_target = character.getFollowTarget();
						if(follow_target == null)
						{
							character.stopMove();
							return;
						}
						if(character.isInRangeZ(follow_target, (long)character._offset) && GeoEngine.canSeeTarget(character, follow_target, false))
						{
							character.stopMove(character.isPlayer(), false, false, false);
							ThreadPoolManager.getInstance().execute(new NotifyAITask(character, CtrlEvent.EVT_ARRIVED_TARGET, 2, null));
							if(!character.isPlayer())
								character.validateLocation(1);
							return;
						}
					}
					if(alldist <= 0.0)
					{
						character.moveNext(false);
						return;
					}

					donedist += (double)(now - character._startMoveTime) * (double)character._previousSpeed / 1000.0;
					double done = donedist / alldist;
					if (done < 0.0)
						done = 0.0;
					if (done >= 1.0)
					{
						character.moveNext(false);
						return;
					}
					if (character.isMovementDisabled())
					{
						character.stopMove();
						return;
					}
					double doneSize = (double)(character.moveList.size() - 1) * done;
					int index = (int)doneSize;
					if (index >= character.moveList.size())
						index = character.moveList.size() - 1;
					if (index < 0)
						index = 0;

					Location loc;
					Location location = loc = index == 0 ? begin : ((Location)character.moveList.get(index)).clone().geo2world();
					if (character.isPlayer())
					{
						if (index < character.moveList.size() - 1)
							loc.correctByPart(((Location)character.moveList.get(index + 1)).clone().geo2world(), doneSize);
						else if (index == character.moveList.size() - 1 && GeoEngine.isTheSameBlock(character.destination, loc))
							loc.correctByPart(character.destination, doneSize);
					}
					if(!character.isFlying() && !character.isInVehicle() && !character.isSwimming() && !character.isVehicle() && loc.z - character.getZ() > 256)
					{
						String bug_text = "geo bug 1 at: " + character.getLoc() + " => " + loc.x + "," + loc.y + "," + loc.z + "\tAll path: " + character.moveList.get(0) + " => " + character.moveList.get(character.moveList.size() - 1);
						Log.add(bug_text, "geo");
						if(character.isPlayer() && character.getAccessLevel() >= 100)
							character.sendMessage(bug_text);
						character.stopMove();
						return;
					}
					if (loc == null || character.isMovementDisabled())
					{
						character.stopMove();
						return;
					}
					// Проверяем, на всякий случай
					if(!character.isMoving || loc == null)
						return;

					character.setLoc(loc, true);

					// В процессе изменения координат, мы остановились
					if(!character.isMoving)
						return;

					if (character.isMovementDisabled())
					{
						character.stopMove();
						return;
					}
					if (character.isFollow && now - character._followTimestamp > (long)(character._forestalling ? 500 : 1000) && follow_target != null && !follow_target.isInRange(character.movingDestTempPos, (long)Math.max(50, character._offset)))
					{
						if (Math.abs(character.getZ() - loc.z) > 1000 && !character.isFlying())
						{
							character.sendPacket(Msg.CANNOT_SEE_TARGET());
							character.stopMove();
							return;
						}
						if(!character.buildPathTo(follow_target.getX(), follow_target.getY(), follow_target.getZ(), character._offset, !character.isPlayer() || ConfigValue.AllowFollowAttack, true, true, follow_target))
						{
							character.stopMove();
							return;
						}
						character.movingDestTempPos.set(follow_target.getX(), follow_target.getY(), follow_target.getZ());
						character.moveNext(true);
						return;
					}
					character._previousSpeed = speed;
					character._startMoveTime = now;
					character._moveTask = ThreadPoolManager.getInstance().scheduleMV(character._moveTaskRunnable, character.getMoveTickInterval());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					character.moveLock.unlock();
				}
			}
			else
			{
				character.moveLock.lock();
				try
				{
					if(!character.isMoving)
						return;
					float speed = character.getMoveSpeed();
					if(speed <= 0)
					{
						character.stopMove();
						return;
					}
					long now = System.currentTimeMillis();

					if(character.isFollow)
					{
						follow_target = character.getFollowTarget();
						if(follow_target == null)
						{
							character.stopMove();
							return;
						}
						if(character.isInRangeZ(follow_target, character._offset) && GeoEngine.canSeeTarget(character, follow_target, false))
						{
							character.stopMove();
							ThreadPoolManager.getInstance().execute(new NotifyAITask(character, CtrlEvent.EVT_ARRIVED_TARGET, 2, null));
							if(!character.isPlayer())
								character.validateLocation(1);
							return;
						}
					}

					if(alldist <= 0)
					{
						character.moveNext(false);
						return;
					}

					donedist += (now - character._startMoveTime) * character._previousSpeed / 1000f;
					double done = donedist / alldist;
					if(done < 0)
						done = 0;
					if(done >= 1)
					{
						character.moveNext(false);
						return;
					}

					Location loc = null;
					try
					{
						int index = (int) (character.moveList.size() * done);
						if(index >= character.moveList.size())
							index = character.moveList.size() - 1;
						if(index < 0)
							index = 0;

						loc = character.moveList.get(index).clone().geo2world();

						if(!character.isFlying() && !character.isInVehicle() && !character.isSwimming() && !character.isVehicle())
							if(loc.z - character.getZ() > 256)
							{
								String bug_text = "geo bug 1 at: " + character.getLoc() + " => " + loc.x + "," + loc.y + "," + loc.z + "\tAll path: " + character.moveList.get(0) + " => " + character.moveList.get(character.moveList.size() - 1);
								Log.add(bug_text, "geo");
								if(character.isPlayer() && character.getAccessLevel() >= 100)
									character.sendMessage(bug_text);
								character.stopMove();
								return;
							}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}

					// Проверяем, на всякий случай
					if(!character.isMoving || loc == null)
						return;

					character.setLoc(loc, true);

					// В процессе изменения координат, мы остановились
					if(!character.isMoving)
						return;

					if(character.isFollow && now - character._followTimestamp > (character._forestalling ? 500 : 1000) && follow_target != null && !follow_target.isInRange(character.movingDestTempPos, Math.max(100, character._offset)))
					{
						if(Math.abs(character.getZ() - loc.z) > 1000 && !character.isFlying())
						{
							character.sendPacket(Msg.CANNOT_SEE_TARGET());
							//character.sendMessage("CANNOT_SEE_TARGET() 8");
							character.stopMove();
							return;
						}
						if(character.buildPathTo(follow_target.getX(), follow_target.getY(), follow_target.getZ(), character._offset, !character.isPlayer() || ConfigValue.AllowFollowAttack, true, true, follow_target))
							character.movingDestTempPos.set(follow_target.getX(), follow_target.getY(), follow_target.getZ());
						else
						{
							character.stopMove();
							return;
						}
						character.moveNext(true);
						return;
					}

					character._previousSpeed = speed;
					character._startMoveTime = now;
					character._moveTask = ThreadPoolManager.getInstance().scheduleMV(character._moveTaskRunnable, character.getMoveTickInterval());
				}
				finally
				{
					character.moveLock.unlock();
				}
			}
		}
	}

	public static class ExecuteFollow extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2Playable> _player;
		private final HardReference<? extends L2Character> _target;
		private int _range;
		private boolean _find_path;

		public ExecuteFollow(L2Playable player, L2Character target, int range, boolean find_path)
		{
			_player = player.getRef();
			_target = target.getRef();
			_range = range;
			_find_path = find_path;
		}

		public void runImpl()
		{
			L2Playable player = _player.get();
			if(player == null)
				return;
			if(ConfigValue.DEbug9)
				player.validateLocation(1);

			if(_target.get().isDoor())
				player.moveToLocation(_target.get().getLoc(), 40, true);
			else
				//player.followToCharacter(_target.get(), _range, false, true);
				player.followToCharacter(_target.get(), _range, true, _find_path);
		}
	}
	// ============================ Таски для L2NpcInstance ==============================

	/** NotifyFactionTask */
	public static class NotifyFactionTask extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2NpcInstance> _npc;
		private final HardReference<? extends L2Character> _attacker;
		private final int _damage;
		private final boolean _isKill;

		public NotifyFactionTask(L2NpcInstance npc, L2Character attacker, int damage, boolean isKill)
		{
			_npc = npc.getRef();
			_attacker = attacker.getRef();
			_damage = damage;
			_isKill = isKill;
		}

		public void runImpl()
		{
			L2NpcInstance npc = _npc.get();
			L2Character cha = _attacker.get();
			if(npc == null || cha == null)
				return;
			try
			{
				String faction_id = npc.getFactionId();
				L2WorldRegion region = L2World.getRegion(npc);
				if(region != null && region.getObjectsSize() > 0)
					for(L2Object obj : region.getObjects())
						if(obj != null && obj.isNpc() && obj.getObjectId() != npc.getObjectId() && (npc.getReflection().getId() == -1 || obj.getReflection().getId() == npc.getReflection().getId()) && !((L2NpcInstance)obj).isDead() && faction_id.equalsIgnoreCase(((L2NpcInstance)obj).getFactionId()))
							((L2NpcInstance)obj).onClanAttacked(npc, cha, _damage, _isKill);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	/** RandomAnimationTask */
	public static class RandomAnimationTask extends l2open.common.RunnableImpl
	{
		private final HardReference<? extends L2NpcInstance> _npc;
		private final int interval;

		public RandomAnimationTask(L2NpcInstance npc)
		{
			_npc = npc.getRef();
			interval = 1000 * Rnd.get(ConfigValue.MinNPCAnimation, ConfigValue.MaxNPCAnimation);
			ThreadPoolManager.getInstance().schedule(this, interval);
		}

		public void runImpl()
		{
			L2NpcInstance npc = _npc.get();
			if(npc == null)
				return;
			if(!npc.isDead() && !npc.isMoving)
				npc.onRandomAnimation();

			ThreadPoolManager.getInstance().schedule(this, interval);
		}
	}

	// ============================ Таски для L2TrapInstance ==============================

	/** TrapDestroyTask */
	public static class TrapDestroyTask extends l2open.common.RunnableImpl
	{
		private final HardReference<L2TrapInstance> _trap;

		public TrapDestroyTask(L2TrapInstance trap)
		{
			_trap = trap.getRef();
		}

		public void runImpl()
		{
			try
			{
				L2TrapInstance trap = _trap.get();
				if(trap != null)
					trap.destroy();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static class DeleteTask extends RunnableImpl
	{
		private final HardReference<? extends L2Character> _ref;

		public DeleteTask(L2Character c)
		{
			_ref = c.getRef();
		}

		@Override
		public void runImpl()
		{
			L2Character c = _ref.get();

			if(c != null)
				c.deleteMe();
		}
	}
}