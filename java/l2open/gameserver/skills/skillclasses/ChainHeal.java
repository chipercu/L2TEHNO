package l2open.gameserver.skills.skillclasses;

import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.SeducedInvestigatorInstance;
import l2open.gameserver.serverpackets.SystemMessage;
import l2open.gameserver.skills.Stats;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChainHeal extends L2Skill
{
	private final int[] _healPercents;
	private final int _healRadius;
	private final int _maxTargets;

	public ChainHeal(StatsSet set)
	{
		super(set);
		_healRadius = set.getInteger("healRadius", 350);
		String[] params = set.getString("healPercents", "").split(";");
		_maxTargets = params.length;
		_healPercents = new int[params.length];
		for (int i = 0; i < params.length; i++)
			_healPercents[i] = Integer.parseInt(params[i]);
	}

	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target.isDead() || activeChar.isAutoAttackable(target))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		int curTarget = 0;
		for(L2Character target : targets)
		{
			if(target == null)
				continue;

			getEffects(activeChar, target, getActivateRate() > 0, false);

			double hp = _healPercents[curTarget] * target.getMaxHp() / 100.;
			double addToHp = Math.max(0, hp);

			if(addToHp > 0)
				addToHp = target.setCurrentHp(addToHp + target.getCurrentHp(), false);

			if(target.isPlayer())
				if(activeChar != target)
					target.sendPacket(new SystemMessage(SystemMessage.XS2S_HP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
				else
					activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));

			curTarget++;
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}

	@Override
	public GArray<L2Character> getTargets(L2Character activeChar, L2Character aimingTarget, boolean forceUse)
	{
		if(ConfigValue.EnableSkillTargetTest)
			return super.getTargets(activeChar, aimingTarget, forceUse);

		GArray<L2Character> result = new GArray<L2Character>();
		GArray<L2Character> targets = aimingTarget.getAroundCharacters(_healRadius, 128);
		if(targets == null || targets.isEmpty())
		{
			if(!aimingTarget.isAutoAttackable(activeChar) && !activeChar.isHealBlocked(true, false) && !activeChar.isCursedWeaponEquipped() && !activeChar.block_hp.get())
				result.add(aimingTarget);
			return result;
		}

		List<HealTarget> healTargets = new ArrayList<HealTarget>();

		if(aimingTarget instanceof SeducedInvestigatorInstance)
			healTargets.add(new HealTarget(-100, aimingTarget));
		//else if(!aimingTarget.isHealBlocked(true, false) && !aimingTarget.isDead() && !aimingTarget.isCursedWeaponEquipped() && !aimingTarget.isAutoAttackable(activeChar))
		//	healTargets.add(new HealTarget(100, aimingTarget));

		for(L2Character target : targets)
		{
			if(target == null || target == activeChar && aimingTarget != activeChar || target.isInvisible() || target.isHealBlocked(true, false) && !(aimingTarget instanceof SeducedInvestigatorInstance) || target.isDead() || target.isCursedWeaponEquipped()/* || target.getPvpFlag() != 0 || target.getKarma() > 0*/ || target.isAutoAttackable(activeChar) || activeChar.getPlayer() != null && target.getPlayer() != null && (activeChar.getPlayer().atWarWith(target.getPlayer()) || target.getPlayer().atWarWith(activeChar.getPlayer())) || target.block_hp.get())
				continue;

			double hpPercent = target.getCurrentHp() / target.getMaxHp();
			healTargets.add(new HealTarget(hpPercent, target));
		}

		HealTarget[] healTargetsArr = new HealTarget[healTargets.size()];
		healTargets.toArray(healTargetsArr);
		Arrays.sort(healTargetsArr, new Comparator<HealTarget>()
		{
			@Override
			public int compare(HealTarget o1, HealTarget o2)
			{
				if(o1 == null || o2 == null)
					return 0;
				if(o1.getHpPercent() < o2.getHpPercent())
					return -1;
				if(o1.getHpPercent() > o2.getHpPercent())
					return 1;
				return 0;
			}
		});

		int targetsCount = 0;
		boolean add_target = false;
		if(!aimingTarget.isHealBlocked(true, false) && !aimingTarget.isDead() && !aimingTarget.isCursedWeaponEquipped() && !aimingTarget.block_hp.get() && !aimingTarget.isAutoAttackable(activeChar))
		{
			add_target = true;
			targetsCount++;
		}

		for(HealTarget ht : healTargetsArr)
		{
			result.add(ht.getTarget());
			targetsCount++;
			if(targetsCount >= _maxTargets)
				break;
		}

		if(add_target)
			result.add(aimingTarget);

		return result;
	}

	private static class HealTarget
	{
		private final double hpPercent;
		private final L2Character target;
		
		public HealTarget(double hpPercent, L2Character target)
		{
			this.hpPercent = hpPercent;
			this.target = target;
		}

		public double getHpPercent()
		{
			return hpPercent;
		}

		public L2Character getTarget()
		{
			return target;
		}
	}
}