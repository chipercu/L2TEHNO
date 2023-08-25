package l2open.gameserver.skills.skillclasses;

import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Fishing;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.StatsSet;
import l2open.util.GArray;

public class ReelingPumping extends L2Skill
{
	private static final int PUMPING = 1;
	private static final int REELING = 2;
	private final int _fishSkillType;

	public ReelingPumping(StatsSet set)
	{
		super(set);
		_fishSkillType = _skillType == SkillType.PUMPING ? PUMPING : REELING;
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!((L2Player) activeChar).isFishing())
		{
			activeChar.sendPacket(_fishSkillType == PUMPING ? Msg.PUMPING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING : Msg.REELING_SKILL_IS_AVAILABLE_ONLY_WHILE_FISHING);
			activeChar.sendActionFailed();
			return false;
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character caster, GArray<L2Character> targets)
	{
		if(caster == null || !caster.isPlayer())
			return;

		L2Player player = (L2Player) caster;
		L2Fishing fish = player.getFishCombat();
		L2Weapon weaponItem = player.getActiveWeaponItem();
		int SS = player.getChargedFishShot() ? 2 : 1;
		int pen = 0;
		double gradebonus = 1 + weaponItem.getCrystalType().ordinal() * 0.1;
		int dmg = (int) (getPower() * gradebonus * SS);

		if(player.getSkillLevel(1315) < getLevel() - 2) // 1315 - Fish Expertise
		{
			// Penalty
			player.sendPacket(Msg.SINCE_THE_SKILL_LEVEL_OF_REELING_PUMPING_IS_HIGHER_THAN_THE_LEVEL_OF_YOUR_FISHING_MASTERY_A_PENALTY_OF_S1_WILL_BE_APPLIED);
			pen = 50;
			int penatlydmg = dmg - pen;
			if(player.isGM())
				player.sendMessage("Dmg w/o penalty = " + dmg);
			dmg = penatlydmg;
		}

		if(SS == 2)
			player.unChargeFishShot();

		if(fish != null)
			if(getSkillType() == SkillType.REELING)
				fish.UseRealing(dmg, pen);
			else
				fish.UsePomping(dmg, pen);
	}
}