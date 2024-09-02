package npc.model;

import l2open.gameserver.model.L2Character;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.L2Skill;
import l2open.gameserver.model.instances.L2ChestInstance;
import l2open.gameserver.serverpackets.PlaySound;
import l2open.gameserver.tables.SkillTable;
import l2open.gameserver.templates.L2NpcTemplate;
import l2open.util.Rnd;


public class TreasureChestInstance extends L2ChestInstance {
    private static final int TREASURE_BOMB_SKILL_ID = 4143;
    private static final int UNLOCK_SKILL_ID = 27;
    private static final int COMMON_TREASURE_OPEN_CHANCE = 25;

    public TreasureChestInstance(int objectId, L2NpcTemplate template) {
        super(objectId, template);
    }

    public void tryOpen(L2Player player, L2Skill skill) {
        int chance = calcChance(player, skill);
        if (chance == -1 || Rnd.chance(chance)) {
            player.addDamageHate(this, 10000, 0);
            doDie(player);
        } else {
            fakeOpen(player);
            player.sendPacket(new PlaySound("ItemSound2.broken_key"));
        }
        setSpoiled(false, null);
    }

    public int calcChance(L2Player player, L2Skill skill) {
        int skillId = skill.getId();
        int skillLvl = skill.getLevel();

        int npcLvl = getLevel();
        int playerLvl = player.getLevel();

        int npcLvlDiff = playerLvl - npcLvl;
        int baseDiff = playerLvl <= 77 ? 6 : 5;
        // Если игрок 77 уровня и ниже то, если уровень персонажа превышает на 6 или более уровень сундука, то награду игрок не получит
        // Если сундук старше персонажа более чем на baseDiff, тогда награды не даем (не открываемся).
        if (isCommonTreasureChest() && (npcLvlDiff >= baseDiff || npcLvlDiff <= (baseDiff * -1)))
            return 0;

			/*int chance = baseChance - (((npcLvl - (skillLvl * 4)) - 16) * 6);
			if(chance > baseChance)
				chance = baseChance;
			if(isCommonTreasureChest())
			{
				if(chance > COMMON_TREASURE_OPEN_CHANCE)
					chance = COMMON_TREASURE_OPEN_CHANCE;
			}
			return chance;
		}*/
        return skill.getActivateRate();
    }

    private void fakeOpen(L2Character opener) {
        L2Skill bomb = SkillTable.getInstance().getInfo(TREASURE_BOMB_SKILL_ID, getBombLvl());
        if (bomb != null)
            doCast(bomb, opener, true);
        onDecay();
    }

    private int getBombLvl() {
        return getLevel() / 10;
    }

    private boolean isCommonTreasureChest() {
        int npcId = getNpcId();
        if (npcId >= 18265 && npcId <= 18286)
            return true;
        return false;
    }

    @Override
    public void reduceCurrentHp(final double damage, final L2Character attacker, L2Skill skill, final boolean awake, final boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp) {
        if (!isCommonTreasureChest())
            fakeOpen(attacker);
    }
}