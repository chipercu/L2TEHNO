package l2open.gameserver.model.instances;

import l2open.config.ConfigValue;
import l2open.common.ThreadPoolManager;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.ClassType;
import l2open.gameserver.model.base.PlayerClass;
import l2open.gameserver.model.items.L2ItemInstance;
import l2open.gameserver.tables.PetDataTable;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;

import java.util.concurrent.Future;

public final class L2PetBabyInstance extends L2PetInstance
{
	private Future<?> _actionTask;
	private boolean _buffEnabled = true;

	public L2PetBabyInstance(int objectId, L2NpcTemplate template, L2Player owner, L2ItemInstance control, byte _currentLevel, long exp)
	{
		super(objectId, template, owner, control, _currentLevel, exp);
	}

	public L2PetBabyInstance(int objectId, L2NpcTemplate template, L2Player owner, L2ItemInstance control)
	{
		super(objectId, template, owner, control);
	}

	private static final int Recharge = 5200;

	class ActionTask extends l2open.common.RunnableImpl
	{
		@Override
		public void runImpl()
		{
			L2Skill skill = onActionTask();
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), (long)(skill == null ? 1000 : skill.getHitTime() * 333 / Math.max(getMAtkSpd(), 1) - 100), false);
		}
	}

	public L2Skill[] getBuffs()
	{
		switch(getNpcId())
		{
			case PetDataTable.IMPROVED_BABY_COUGAR_ID:
				return COUGAR_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_BABY_BUFFALO_ID:
				return BUFFALO_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_BABY_KOOKABURRA_ID:
				return KOOKABURRA_BUFFS[getBuffLevel()];
			case PetDataTable.FAIRY_PRINCESS_ID:
				return ConfigValue.EnableEmerlPet ? FAIRY_PRINCESS_BUFFS_2[getBuffLevel()] : FAIRY_PRINCESS_BUFFS[getBuffLevel()];
			case PetDataTable.WHITE_WEASEL_ID:
				return ConfigValue.EnableEmerlPet ? WHITE_WEASEL_BUFFS_2[getBuffLevel()] : WHITE_WEASEL_BUFFS[getBuffLevel()];
			case PetDataTable.SPIRIT_SHAMAN_ID:
				return ConfigValue.EnableEmerlPet ? SPIRIT_SHAMAN_BUFFS_2[getBuffLevel()] : SPIRIT_SHAMAN_BUFFS[getBuffLevel()];
			case PetDataTable.TOY_KNIGHT_ID:
				return TOY_KNIGHT_BUFFS[getBuffLevel()];
			case PetDataTable.TURTLE_ASCETIC_ID:
				return TURTLE_ASCETIC_BUFFS[getBuffLevel()];
			case PetDataTable.SUPER_KAT_THE_CAT_Z_ID:
				return TOY_KNIGHT_BUFFS[getBuffLevel()];
			case PetDataTable.SUPER_MEW_THE_CAT_Z_ID:
				return TURTLE_ASCETIC_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_DESELOPH_ID:
				return ROSE_DESELOPH_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_HYUM_ID:
				return ROSE_HYUM_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_REKANG_ID:
				return ROSE_REKANG_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_LILIAS_ID:
				return ROSE_LILIAS_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_LAPHAM_ID:
				return ROSE_LAPHAM_BUFFS[getBuffLevel()];
			case PetDataTable.ROSE_MAPHUM_ID:
				return ROSE_MAPHUM_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_DESELOPH_ID:
				return IMPROVED_ROSE_DESELOPH_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_HYUM_ID:
				return IMPROVED_ROSE_HYUM_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_REKANG_ID:
				return IMPROVED_ROSE_REKANG_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_LILIAS_ID:
				return IMPROVED_ROSE_LILIAS_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_LAPHAM_ID:
				return IMPROVED_ROSE_LAPHAM_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_ROSE_MAPHUM_ID:
				return IMPROVED_ROSE_MAPHUM_BUFFS[getBuffLevel()];
			case PetDataTable.OWL_MONK_ID:
				return OWL_MONK_ID_BUFFS[getBuffLevel()];
			default:
				return new L2Skill[0];
		}
	}

	public L2Skill onActionTask()
	{
		try
		{
			L2Player owner = getPlayer();
			if(owner != null && !owner.isDead() && !owner.isInvul() && !isCastingNow() && !isAttackingNow() && !isStunned() && !isSleeping() && !isParalyzed() && !isAlikeDead() && !isAfraid() &&!isActionBlock())
			{
				if(getEffectList().getEffectsCountForSkill(5753) > 0) // Awakening
					return null;

				boolean improved = PetDataTable.isImprovedBabyPet(getNpcId()) || (PetDataTable.isPremiumPet(getNpcId()));
				L2Skill skill = null;

				if(!ConfigValue.PetsHealOnlyInBattle || owner.isInCombat() || (PlayerClass.values()[owner.getActiveClassId()].isOfType(ClassType.Priest)))
				{
					// проверка лечения
					double curHp = owner.getCurrentHpPercents();
					if(curHp < 90 && Rnd.chance((100 - curHp) / 3))
						// затычка...
						if(ConfigValue.EnableEmerlPet && (getNpcId() == 16051 || getNpcId() == 16046 || getNpcId() == 16050 || getNpcId() == 16045))
						{
							skill = SkillTable.getInstance().getInfo(5195, 12);
						}
						else if(curHp < 18) // экстренная ситуация, сильный хил
						{
							if(PetDataTable.isPremiumPet(getNpcId()))
								skill = SkillTable.getInstance().getInfo((improved) ? 5590 : 1218, getHealLevel());
							else
								skill = SkillTable.getInstance().getInfo((improved) ? 5590 : 4718, getHealLevel());
						}
						else if(getNpcId() != PetDataTable.IMPROVED_BABY_KOOKABURRA_ID)
							if(PetDataTable.isPremiumPet(getNpcId()))
								skill = SkillTable.getInstance().getInfo((improved) ? 5195 : 5590, getHealLevel());
							else
								skill = SkillTable.getInstance().getInfo((improved) ? 5195 : 4717, getHealLevel());

					// проверка речарджа
					if(skill == null && (getNpcId() == 16035 || getNpcId() == 16046 || getNpcId() == 16051))
					{
						double curMp = owner.getCurrentMpPercents();
						if(curMp < 66 && Rnd.chance((100 - curMp) / 3))
							skill = SkillTable.getInstance().getInfo(Recharge, getRechargeLevel());
					}

					if(skill != null && !isMuted(skill) && skill.checkCondition(L2PetBabyInstance.this, owner, false, !isFollow(), true))
					{
						setTarget(owner);
						getAI().Cast(skill, owner, false, !isFollow());
						return skill;
					}
				}

				if(!improved || owner.isInOfflineMode()/* || owner.getEffectList().getEffectsCountForSkill(5771) > 0*/)
					return null;

				if(isBuffEnabled())
					outer: for(L2Skill buff : getBuffs())
					{
						if(buff == null || getCurrentMp() < buff.getMpConsume2() || buff.isBlockedByChar(owner, buff))
							continue;

						for(L2Effect ef : owner.getEffectList().getAllEffects())
							if(checkEffect(ef, buff))
								continue outer;

						if(!isMuted(buff) && buff.checkCondition(L2PetBabyInstance.this, owner, false, !isFollow(), true))
						{
							setTarget(owner);
							getAI().Cast(buff, owner, false, !isFollow());
							return buff;
						}
						return null;
					}
			}
			else if(ConfigValue.EnableEmerlPet && owner != null && owner.isDead() && (getNpcId() == 16051 || getNpcId() == 16046 || getNpcId() == 16050 || getNpcId() == 16045))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(3263, 1);
				setTarget(owner);
				getAI().Cast(skill, owner, false, !isFollow());
				return skill;
			}
		}
		catch(Throwable e)
		{
			_log.warning("Pet [#" + getNpcId() + "] a buff task error has occurred: " + e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Возвращает true если эффект для скилла уже есть и заново накладывать не надо
	 */
	private boolean checkEffect(L2Effect ef, L2Skill skill)
	{
		if(ef == null || !ef.isInUse() || !EffectList.checkStackType(ef.getAbnormalType(), skill.getAbnormalType()) || ef.getAbnormalLv() < skill.getAbnormalLv())
			return false;
		else if(ef.getTimeLeft() > 10000)
			return true;
		return false;
	}

	public synchronized void stopBuffTask()
	{
		if(_actionTask != null)
		{
			_actionTask.cancel(false);
			_actionTask = null;
		}
	}

	public synchronized void startBuffTask()
	{
		if(_actionTask != null)
			stopBuffTask();

		if(_actionTask == null && !isDead())
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 3500, false);
	}

	public boolean isBuffEnabled()
	{
		return _buffEnabled;
	}

	public void triggerBuff()
	{
		_buffEnabled = !_buffEnabled;
		L2Player owner = getPlayer();
		if(owner != null)
			owner.sendMessage(getName() + ": Buff is now " + (_buffEnabled ? "on." : "off."));
	}

	@Override
	public void doDie(L2Character killer)
	{
		stopBuffTask();
		super.doDie(killer);
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		startBuffTask();
	}

	@Override
	public void unSummon()
	{
		stopBuffTask();
		super.unSummon();
	}

	public int getHealLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 12), 1), 12);
	}

	public int getRechargeLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 8), 1), 8);
	}

	public int getBuffLevel()
	{
		if(getNpcId() == 16045 || getNpcId() == 16046 || (getNpcId() >= 1562 && getNpcId() <= 1573))
			return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 3), 0), 3);
		return Math.min(Math.max((getLevel() - 55) / 5, 0), 3);
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return 1;
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return 1;
	}

	// new
	private static final L2Skill[][] OWL_MONK_ID_BUFFS =
	{
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		}
	};

	private static final L2Skill[][] COUGAR_BUFFS = {
			{ SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5189, 6) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(5186, 2) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5187, 4),
					SkillTable.getInstance().getInfo(5588, 3) } };

	private static final L2Skill[][] BUFFALO_BUFFS = {
			{ SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5191, 3) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5191, 3),
					SkillTable.getInstance().getInfo(5187, 4),
					SkillTable.getInstance().getInfo(5186, 2) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5191, 3),
					SkillTable.getInstance().getInfo(5187, 4),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5589, 3) } };

	private static final L2Skill[][] KOOKABURRA_BUFFS = {
			{ SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(5201, 6) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(5201, 6) } };

	private static final L2Skill[][] WHITE_WEASEL_BUFFS_2 =
	{
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23276, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		}
	};
	private static final L2Skill[][] WHITE_WEASEL_BUFFS = {
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) } };

	private static final L2Skill[][] FAIRY_PRINCESS_BUFFS_2 =
	{
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		}
	};

	private static final L2Skill[][] FAIRY_PRINCESS_BUFFS =
	/*{
		{
			SkillTable.getInstance().getInfo(26082, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26082, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26082, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26082, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		}
	};*/
	{
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3)
			},
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3)
			},
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3)
			},
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3)
			}
	};
	private static final L2Skill[][] SPIRIT_SHAMAN_BUFFS_2 =
	{
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		},
		{
			SkillTable.getInstance().getInfo(23275, 1),
			//SkillTable.getInstance().getInfo(23237, 1),
			SkillTable.getInstance().getInfo(1232, 93),
			SkillTable.getInstance().getInfo(1182, 93),
			SkillTable.getInstance().getInfo(1189, 93),
			SkillTable.getInstance().getInfo(1191, 93),
			SkillTable.getInstance().getInfo(1548, 93),
			SkillTable.getInstance().getInfo(1392, 33),
			SkillTable.getInstance().getInfo(1393, 33)
		}
	};
	private static final L2Skill[][] SPIRIT_SHAMAN_BUFFS =
	{
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) }
	};

	private static final L2Skill[][] TOY_KNIGHT_BUFFS =
	/*{
		{
			SkillTable.getInstance().getInfo(26081, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26081, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26081, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		},
		{
			SkillTable.getInstance().getInfo(26081, 46), // POW -->
			SkillTable.getInstance().getInfo(1191, 78), // RFIRE -->
			SkillTable.getInstance().getInfo(1182, 78), // RWATER -->
			SkillTable.getInstance().getInfo(1189, 78), // RWIND -->
			SkillTable.getInstance().getInfo(1392, 18), // RHOLY -->
			SkillTable.getInstance().getInfo(1393, 18), // RDARK -->
			SkillTable.getInstance().getInfo(1542, 1), // CC -->
			SkillTable.getInstance().getInfo(1259, 94), // RSHOCK -->
			SkillTable.getInstance().getInfo(1238, 78)
		}
	};*/
	{
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4)
			},
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4)
			},
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4)
			},
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4)
			}
	};

	/*private static final L2Skill[][] TURTLE_ASCETIC_BUFFS =
	{
		{
			SkillTable.getInstance().getInfo(1232, 78),
			SkillTable.getInstance().getInfo(1182, 78),
			SkillTable.getInstance().getInfo(1189, 78),
			SkillTable.getInstance().getInfo(1191, 78),
			SkillTable.getInstance().getInfo(1259, 94),
			SkillTable.getInstance().getInfo(1392, 18),
			SkillTable.getInstance().getInfo(1393, 18),
			SkillTable.getInstance().getInfo(1548, 78),
			SkillTable.getInstance().getInfo(1363, 46)
		},
		{
			SkillTable.getInstance().getInfo(1232, 78),
			SkillTable.getInstance().getInfo(1182, 78),
			SkillTable.getInstance().getInfo(1189, 78),
			SkillTable.getInstance().getInfo(1191, 78),
			SkillTable.getInstance().getInfo(1259, 94),
			SkillTable.getInstance().getInfo(1392, 18),
			SkillTable.getInstance().getInfo(1393, 18),
			SkillTable.getInstance().getInfo(1548, 78),
			SkillTable.getInstance().getInfo(1363, 46)
		},
		{
			SkillTable.getInstance().getInfo(1232, 78),
			SkillTable.getInstance().getInfo(1182, 78),
			SkillTable.getInstance().getInfo(1189, 78),
			SkillTable.getInstance().getInfo(1191, 78),
			SkillTable.getInstance().getInfo(1259, 94),
			SkillTable.getInstance().getInfo(1392, 18),
			SkillTable.getInstance().getInfo(1393, 18),
			SkillTable.getInstance().getInfo(1548, 78),
			SkillTable.getInstance().getInfo(1363, 46)
		},
		{
			SkillTable.getInstance().getInfo(1232, 78),
			SkillTable.getInstance().getInfo(1182, 78),
			SkillTable.getInstance().getInfo(1189, 78),
			SkillTable.getInstance().getInfo(1191, 78),
			SkillTable.getInstance().getInfo(1259, 94),
			SkillTable.getInstance().getInfo(1392, 18),
			SkillTable.getInstance().getInfo(1393, 18),
			SkillTable.getInstance().getInfo(1548, 78),
			SkillTable.getInstance().getInfo(1363, 46)
		}
	};*/
		private static final L2Skill[][] TURTLE_ASCETIC_BUFFS =
		{
			{
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(6429, 1), // -
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(6429, 1), // -
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(6429, 1), // -
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(6429, 1), // -
					SkillTable.getInstance().getInfo(5987, 1) } };


	private static final L2Skill[][] ROSE_DESELOPH_BUFFS = {
			{ SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5588, 3) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) } };

	private static final L2Skill[][] ROSE_HYUM_BUFFS = {
			{ SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) } };

	private static final L2Skill[][] ROSE_REKANG_BUFFS = {
			{ SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5192, 2) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) } };

	private static final L2Skill[][] ROSE_LILIAS_BUFFS = {
			{ SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5588, 3) },
			{ SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5588, 3) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5586, 3),
					SkillTable.getInstance().getInfo(5186, 2),
					SkillTable.getInstance().getInfo(5588, 3),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5589, 3),
					SkillTable.getInstance().getInfo(5187, 4) } };

	private static final L2Skill[][] ROSE_LAPHAM_BUFFS = {
			{ SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) },
			{
					SkillTable.getInstance().getInfo(5194, 3),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5193, 3) } };

	private static final L2Skill[][] ROSE_MAPHUM_BUFFS = {
			{ SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5192, 2) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(5189, 6),
					SkillTable.getInstance().getInfo(5192, 2),
					SkillTable.getInstance().getInfo(5190, 6),
					SkillTable.getInstance().getInfo(5587, 3),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_DESELOPH_BUFFS = {
			{ SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1519, 1) },
			{
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1519, 1),
					SkillTable.getInstance().getInfo(1502, 1) },
			{
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1519, 1),
					SkillTable.getInstance().getInfo(1502, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_HYUM_BUFFS = {
			{ SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(1501, 1) },
			{ SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1499, 1) },
			{
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1500, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_REKANG_BUFFS = {
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1) },
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(5988, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_LILIAS_BUFFS = {
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1) },
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1519, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1519, 1),
					SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1519, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1502, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_LAPHAM_BUFFS = {
			{ SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(1501, 1) },
			{ SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1499, 1) },
			{
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(5193, 3),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(1500, 1) } };

	private static final L2Skill[][] IMPROVED_ROSE_MAPHUM_BUFFS = {
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1) },
			{ SkillTable.getInstance().getInfo(1499, 1), SkillTable.getInstance().getInfo(1501, 1), SkillTable.getInstance().getInfo(1504, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) },
			{
					SkillTable.getInstance().getInfo(1499, 1),
					SkillTable.getInstance().getInfo(1501, 1),
					SkillTable.getInstance().getInfo(1504, 1),
					SkillTable.getInstance().getInfo(5988, 1),
					SkillTable.getInstance().getInfo(5987, 1) } };

	// Не отображаем на петах значки клана. 
	@Override
	public boolean isCrestEnable()
	{
		return false;
	}
}