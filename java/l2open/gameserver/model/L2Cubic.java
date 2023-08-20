package l2open.gameserver.model;

import l2open.common.ThreadPoolManager;
import l2open.gameserver.serverpackets.*;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;
import l2open.util.Rnd;
import l2open.util.reference.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: Ragnarok
 * @date: 22.08.11   0:37
 */
public final class L2Cubic
{
    //Template параметры
    private final int level;
    private final int id;
    private final int slot;
    private final int delay;
    private final int duration;
    private final float power;
    private final String targetType;
    private final String opCond;
    private final List<CubicSkill> cubicSkills;
    private final StatsSet set;
    private final int max_count;

    //Параметры для действующего кубика
    private boolean isMyCubic;
    private ScheduledFuture<?> lifeTask;
    private ScheduledFuture<?> actionTask;
	private HardReference<? extends L2Player> cubic_owner = HardReferences.emptyRef();
	private HardReference<? extends L2Character> cubic_target = HardReferences.emptyRef();
    private long lastSuccessAction = 0;
    private int currentCount = 0;

    public L2Cubic(int id, int level, StatsSet set)
	{
        this.set = set;
        this.id = id;
        this.level = level;
        this.slot = set.getInteger("slot");
        this.duration = set.getInteger("duration");
        this.delay = set.getInteger("delay");
        this.max_count = set.getInteger("max_count");
        this.power = set.getFloat("power");
        this.targetType = set.getString("target_type");
        this.opCond = set.getString("op_cond");
        cubicSkills = new ArrayList<CubicSkill>();
    }

    public void addSkill(CubicSkill cubicSkill) 
	{
        cubicSkills.add(cubicSkill);
    }

    /**
     * Генерирует и возвращает уникальный идентификатор для кубика
     *
     * @return уникальный идентификатор кубика
     */
    public final int getMask() 
	{
        return id * 1000 + level;
    }

    public static int getMask(int maskId, int maskLevel) 
	{
        return maskId * 1000 + maskLevel;
    }

    public L2Cubic copy() 
	{
        L2Cubic cubic = new L2Cubic(id, level, set);
        cubic.getCubicSkills().addAll(getCubicSkills());
        return cubic;
    }

    public List<CubicSkill> getCubicSkills() 
	{
        return cubicSkills;
    }

    public int getId() 
	{
        return id;
    }

    public String getTargetType() 
	{
        return targetType;
    }

    public void setIsMyCubic(boolean myCubic) 
	{
        isMyCubic = myCubic;
    }

    public boolean isMyCubic()
	{
        return isMyCubic;
    }

    /**
     * Инициализирует новый кубик. Запускает таск на удаление. Запускает таск на действие
     *
     * @param owner - тот, на ком этот кубик висит
     */
    public void initialize(L2Player owner) 
	{
        cubic_owner = owner.getRef();
        lifeTask = ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl() 
		{
            @Override
            public void runImpl() 
			{
                if(actionTask != null) 
				{
                    actionTask.cancel(false);
                    actionTask = null;
                }
                L2Player owner = cubic_owner.get();
                if(owner != null && owner.getCubic(getSlot()) != null) 
                    owner.deleteCubic(getSlot());
            }
        }, duration * 1000);
        actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1, false);
    }

    private class ActionTask extends l2open.common.RunnableImpl
	{
        @Override
        public void runImpl() 
		{
			final L2Player owner = cubic_owner.get();
            if(owner == null) 
			{
                deleteMe();
                return;
            }
			else if(lastSuccessAction + delay * 1000 > System.currentTimeMillis()) // Откат кубика 
			{
                actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
                return;
            }
            CubicSkill cubicSkill = null;
            L2Character target = cubic_target.get();
            String skillOpCond = getOpCond();
            if(getTargetType().startsWith("target")) 
			{
                if(!checkCond(skillOpCond, owner, target)) 
				{
                    actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
                    return;
                }
                cubicSkill = getRandomSkill();
            } 
			else if(getTargetType().startsWith("by_skill")) 
			{
                cubicSkill = getRandomSkill();
                if(cubicSkill != null && !checkCond(cubicSkill.getSkillCond(), owner, target))
				{
                    actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
                    return;
                }
            } 
			else if(getTargetType().startsWith("heal"))
			{
                String[] parts = getTargetType().split(";");
                int[] healPercents = {Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])};
				double percentleft = 100.0;
				if(owner.getParty() != null)
				{
					for(L2Player member : owner.getParty().getPartyMembers())
					{
						if(member == null || member.isDead())
							continue;
						else if(isInCubicRange(owner, member) && member.getCurrentHp() < member.getMaxHp() && percentleft > (member.getCurrentHp() / member.getMaxHp()))
						{
							percentleft = (member.getCurrentHp() / member.getMaxHp());
							target = member;
						}
						if(member.getPet() != null)
						{
							if(member.getPet().isDead())
								continue;
							else if(!isInCubicRange(owner, member.getPet()))
								continue;
							else if(member.getPet().getCurrentHp() < member.getPet().getMaxHp() && percentleft > (member.getPet().getCurrentHp() / member.getPet().getMaxHp()))
							{
								percentleft = (member.getPet().getCurrentHp() / member.getPet().getMaxHp());
								target = member.getPet();
							}
						}
					}
				}
				else
				{
					if (owner.getCurrentHp() < owner.getMaxHp())
					{
						percentleft = (owner.getCurrentHp() / owner.getMaxHp());
						target = owner;
					}
					if (owner.getPet() != null)
						if (!owner.getPet().isDead() && owner.getPet().getCurrentHp() < owner.getPet().getMaxHp() && percentleft > (owner.getPet().getCurrentHp() / owner.getPet().getMaxHp()) && isInCubicRange(owner, owner.getPet()))
						{
							target = owner.getPet();
						}
				}
				if(target == null)
				{
                    actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
                    return;
                }
                for(int i = 0; i < 3; i++) 
				{
                    if(target.getCurrentHpPercents() < healPercents[i]) 
					{
                        cubicSkill = cubicSkills.get(i);
                    }
                }
            }
            if(cubicSkill != null) 
			{
                if(cubicSkill.getSkillTarget().equals("master")) 
                    target = owner;
				else if(cubicSkill.getSkillTarget().equals("heal") && getId() == 12)
					target = owner;
				else if(cubicSkill.getSkillTarget().equals("heal") && getId() != 12)
				{
					double percentleft = 100.0;
					if(owner.getParty() != null)
					{
						for(L2Player member : owner.getParty().getPartyMembers())
						{
							if(member == null || member.isDead())
								continue;
							else if(cubicSkill.getSkill() != null && isInCubicRange(owner, member) && member.getCurrentHp() < member.getMaxHp() && percentleft > (member.getCurrentHp() / member.getMaxHp()))
							{
								percentleft = (member.getCurrentHp() / member.getMaxHp());
								target = member;
							}
							if(member.getPet() != null)
							{
								if(member.getPet().isDead())
									continue;
								else if(!isInCubicRange(owner, member.getPet()))
									continue;
								else if(cubicSkill.getSkill() != null && member.getPet().getCurrentHp() < member.getPet().getMaxHp() && percentleft > (member.getPet().getCurrentHp() / member.getPet().getMaxHp()))
								{
									percentleft = (member.getPet().getCurrentHp() / member.getPet().getMaxHp());
									target = member.getPet();
								}
							}
						}
					}
					else
					{
						if(owner.getCurrentHp() < owner.getMaxHp())
						{
							percentleft = (owner.getCurrentHp() / owner.getMaxHp());
							target = owner;
						}
						if(owner.getPet() != null && !owner.getPet().isDead() && owner.getPet().getCurrentHp() < owner.getPet().getMaxHp() && percentleft > (owner.getPet().getCurrentHp() / owner.getPet().getMaxHp()) && isInCubicRange(owner, owner.getPet()))
							target = owner.getPet();
					}
                }
				if(target == null || target.isAlikeDead() || target.isInvisible() || (!getTargetType().startsWith("heal") && target != owner && !owner.isAutoAttackable(target)) || owner.getDistance(target) > 500 || (!getTargetType().startsWith("heal") && target.isPlayer() && target != owner && !target.isInZoneBattle() && target.getPvpFlag() == 0) || getTargetType().startsWith("target") && target.isPlayable() && target.getPlayer() == cubic_owner.get())
				{
                    cubic_target = HardReferences.emptyRef();
                    actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
                    return;
                }
				else if(Rnd.get(100) < cubicSkill.getActivateChance() || ++currentCount >= max_count)
				{
					final L2Skill skill = cubicSkill.getSkill();
					final L2Character _target = target;

					owner.broadcastSkill(new MagicSkillUse(owner, _target, skill.getDisplayId(), skill.getLevel(), skill.getHitTime(), 0), true);
					ThreadPoolManager.getInstance().schedule(new l2open.common.RunnableImpl()
					{
						@Override
						public void runImpl() throws Exception
						{
							//owner.altUseSkill(skill, _target);

							GArray<L2Character> targets = skill.getTargets(owner, _target, true);
							owner.broadcastSkill(new MagicSkillLaunched(owner.getObjectId(), skill.getDisplayId(), skill.getLevel(), targets, skill.isOffensive()), true);
							owner.callSkill(skill, targets, false);
						}
					}, 2370);

                    currentCount = 0;
                    lastSuccessAction = System.currentTimeMillis()+3370;
                }
            } 
			else 
                currentCount++;
            actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 1000, false);
        }
    }

    private boolean checkCond(String skillCond, L2Player owner, L2Character target) 
	{
		if(target != null && !getTargetType().startsWith("heal"))
            if(target.isAlikeDead() || target.isInvisible() || !getTargetType().startsWith("heal") && target.isPlayer() && (!owner.isAutoAttackable(target) || owner.getDistance(target) > 500 || (!getTargetType().startsWith("heal") && target.isPlayer() && target != owner && !target.isInZoneBattle() && target.getPvpFlag() == 0)))
			{
                cubic_target = HardReferences.emptyRef();
                return false;
            }
        if(skillCond.startsWith("debuff"))
		{
            for(L2Effect e : owner.getEffectList().getAllEffects())
                if(e.isOffensive() && e.getSkill().isCancelable() && e.getSkill().getId() != 985)
                    return true;
            return false;
        }
		else if(skillCond.isEmpty())
            return true;
		else 
		{
            String[] parts = skillCond.split(";");
            int hpPercent = Integer.parseInt(parts[1]);
            int hp = Integer.parseInt(parts[2]);
            return target != null && ((target.getMaxHp() > hpPercent * 100 && target.getCurrentHp() > hp) || (target.getMaxHp() < hpPercent * 100 && target.getCurrentHpPercents() > hpPercent));
        }
    }

    private CubicSkill getRandomSkill() 
	{
        int chance = Rnd.get(100);
        int oldChance;
        int currentChance = 0;
        for(CubicSkill iterateSkill : cubicSkills)
		{
            oldChance = currentChance;
            currentChance += iterateSkill.getChoiseChance();
            if(chance >= oldChance && chance < currentChance)
			{
                return iterateSkill;
            }
        }
        return null;
    }

    public void deleteMe() 
	{
        if(actionTask != null)
		{
            actionTask.cancel(false);
            actionTask = null;
        }
        if(lifeTask != null)
		{
            lifeTask.cancel(false);
            lifeTask = null;
        }
    }

    /**
     * Для всех кубиков, кроме Heal
     *
     * @param target - атакауемая цель
     */
    public void startAttack(L2Character target) 
	{
        if(target == null || target.isInvisible() || target.isPlayable() && target.getPlayer() == cubic_owner.get())
            return;
        if(getTargetType().startsWith("target") || getTargetType().startsWith("by_skill"))
		{
			if(target != cubic_owner.get())
				cubic_target = target.getRef();
        }
    }

    /**
     * Для всех кубиков кроме Heal
     */
    public void stopAttack() 
	{
        if(getTargetType().startsWith("target") || getTargetType().startsWith("by_skill"))
            cubic_target = HardReferences.emptyRef();
    }

    public final int getSlot() 
	{
        return slot;
    }

    public String getOpCond() 
	{
        return opCond;
    }

    /*public float getPower() 
	{
        return power;
    }*/

	public boolean isInCubicRange(L2Character owner, L2Character target)
	{
		if (owner == null || target == null)
			return false;

		int x, y, z;
		int range = 900; //Need fixed this.

		x = (owner.getX() - target.getX());
		y = (owner.getY() - target.getY());
		z = (owner.getZ() - target.getZ());

		return ((x * x) + (y * y) + (z * z) <= (range * range));
	}

    public static class CubicSkill 
	{
        private final int choiseChance;
        private final L2Skill skill;
        private final int activateChance;
        private final boolean castToStatic;
        private String skillTarget;
        private String skillCond;

        public CubicSkill(int choiseChance, L2Skill skill, int activateChance, boolean castToStatic) 
		{
            this.choiseChance = choiseChance;
            this.skill = skill;
            this.activateChance = activateChance;
            this.castToStatic = castToStatic;
            this.skillTarget = "";
            this.skillCond = "";
        }

        public int getChoiseChance() 
		{
            return choiseChance;
        }

        public void setSkillTarget(String skillTarget) 
		{
            this.skillTarget = skillTarget;
        }

        public void setSkillCond(String skillCond) 
		{
            this.skillCond = skillCond;
        }

        public String getSkillTarget() 
		{
            return skillTarget;
        }

        public String getSkillCond() 
		{
            return skillCond;
        }

        public L2Skill getSkill() 
		{
            return skill;
        }

        public int getActivateChance() 
		{
            return activateChance;
        }
    }
}
