package l2open.gameserver.model.instances;

import l2open.common.ThreadPoolManager;
import l2open.config.ConfigValue;
import l2open.gameserver.cache.Msg;
import l2open.gameserver.model.*;
import l2open.gameserver.model.base.ClassId;
import l2open.gameserver.serverpackets.MagicSkillUse;
import l2open.gameserver.serverpackets.SkillList;
import l2open.gameserver.tables.SkillTable;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author SYS
 */
public class L2AgathionInstance {
    protected static final Logger _log = Logger.getLogger(L2AgathionInstance.class.getName());

    public static final int BEASTLY_AGATHION_ID = 16026;
    public static final int RAINBOW_AGATHION_ID = 16027;
    public static final int CASTLE_LORD_AGATHION_ID = 16028;
    public static final int FORTRESS_AGATHION_ID = 16029;
    public static final int LITTLE_ANGEL_AGATHION_ID = 16031;
    public static final int LITTLE_DEVIL_AGATHION_ID = 16032;
    public static final int RUDOLPH_AGATHION_ID = 16033;

    public static final int BABY_PANDA_AGATHION_ID = 1505;
    public static final int BAMBOO_PANDA_AGATHION_ID = 1506;
    public static final int SEXY_PANDA_AGATHION_ID = 1507;

    public static final int CHARMING_CUPID_AGATHION_ID = 1508;
    public static final int NAUGHTY_CUPID_AGATHION_ID = 1509;

    public static final int MAJO_AGATHION_ID = 1501;
    public static final int GOLD_MAJO_AGATHION_ID = 1502;
    public static final int BLACK_MAJO_AGATHION_ID = 1503;

    public static final int PLAIPITAK_AGATHION_ID = 1504;

    public static final int WHITE_MANEKI_AGATHION_ID = 1510;
    public static final int BLACK_MANEKI_AGATHION_ID = 1511;
    public static final int BROWN_MANEKI_AGATHION_ID = 1512;

    public static final int BAT_DROVE_AGATHION_ID = 1513;

    public static final int PEGASUS_AGATHION_ID = 1514;

    public static final int YELLOW_ROBED_TOJIGONG_AGATHION_ID = 1515;
    public static final int BLUE_ROBED_TOJIGONG_AGATHION_ID = 1516;
    public static final int GREEN_ROBED_TOJIGONG_AGATHION_ID = 1517;

    public static final int BUGBEAR_AGATHION_ID = 1518;

    private L2Player _owner = null;
    private L2Character _target = null;
    private int _id;

    private boolean useSpoil;

    /**
     * скилы агнишена
     */
    private GArray<L2Skill> _skills = new GArray<L2Skill>();
    /**
     * скилы, добавляемые хозяину
     */
    private GArray<L2Skill> _addSkills = new GArray<L2Skill>();

    private Future<?> _actionTask;

    public L2AgathionInstance(final L2Player owner, final int id) {
        _owner = owner;
        _id = id;

        if (ConfigValue.EnableAgationSpoil && Arrays.stream(ConfigValue.AgationsIds).anyMatch(value -> value == id)) {
            if (ConfigValue.AgationSpoilOnlyPremiumAccount) {
                if (owner.getBonus().RATE_XP > 1) {
                    this.useSpoil = true;
                }
            } else {
                this.useSpoil = true;
            }

        }

        switch (id) {
            case BEASTLY_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5413, 1)); // See a summoned Agathion perform tricks. Beast Farm.
                break;
            case RAINBOW_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5414, 1)); // See a summoned Agathion perform tricks. Rainbow Clan Hall.
                break;
            case CASTLE_LORD_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5415, 1)); // See a summoned Agathion perform tricks. Castle.
                break;
            case FORTRESS_AGATHION_ID:
                _addSkills.add(SkillTable.getInstance().getInfo(5458, 1)); // See a summoned Agathion perform tricks. Fortress.
                break;
            case LITTLE_ANGEL_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5535, 1)); // Little Angel Agathion Cuteness Attack
                break;
            case LITTLE_DEVIL_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5536, 1)); // Little Devil Agathion Cuteness Attack
                break;
            case RUDOLPH_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(5537, 1)); // Rudolph Agathion Cuteness Attack
                break;
            case BABY_PANDA_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23010, 1)); // Baby Panda Agathion Cute Trick
                break;
            case BAMBOO_PANDA_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23011, 1)); // Bamboo Panda Agathion Cute Trick
                break;
            case SEXY_PANDA_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23012, 1)); // Sexy Panda Agathion Cute Trick
                break;
            case CHARMING_CUPID_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23025, 1)); // Charming Cupid Agathion Cute Trick
                break;
            case NAUGHTY_CUPID_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23026, 1)); // Naughty Cupid Agathion Cute Trick
                break;
            case MAJO_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23000, 1)); // Majo Agathion Cute Trick
                break;
            case GOLD_MAJO_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23001, 1)); // Gold Majo Agathion Cute Trick
                break;
            case BLACK_MAJO_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23002, 1)); // Black Majo Agathion Cute Trick
                break;
            case PLAIPITAK_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23003, 1)); // Plaipitak Agathion Cute Trick
                break;
            case WHITE_MANEKI_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23027, 1)); // White Maneki Neko Agathion Cute Trick I
                break;
            case BLACK_MANEKI_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23028, 1)); // Black Maneki Neko Agathion Cute Trick I
                break;
            case BROWN_MANEKI_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23029, 1)); // Brown Maneki Neko Agathion Cute Trick I
                break;
            case BAT_DROVE_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23033, 1)); // One-Eyed Bat Drove Agathion Cute Trick
                break;
            case PEGASUS_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23036, 1)); // Pegasus Agathion Cute Trick
                break;
            case YELLOW_ROBED_TOJIGONG_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23039, 1)); // Yellow-Robed Tojigong Agathion Cute Trick
                break;
            case BLUE_ROBED_TOJIGONG_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23040, 1)); // Blue-Robed Tojigong Agathion Cute Trick
                break;
            case GREEN_ROBED_TOJIGONG_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23041, 1)); // Green-Robed Tojigong Agathion Cute Trick
                break;
            case BUGBEAR_AGATHION_ID:
                _skills.add(SkillTable.getInstance().getInfo(23045, 1)); // Bugbear Agathion Cute Trick
                break;
            default:
                return;
        }

        // Выдаем скилы хозяину
        for (L2Skill s : _addSkills) {
            owner.addSkill(s);
        }

        owner.sendPacket(new SkillList(owner));

    }

    public void doSweep(L2Character target){

        L2Player owner = getPlayer();
        if (_target == target || owner == null || owner == target) {
            return;
        }

        if (useSpoil && target.isMonster()) {
            L2MonsterInstance monster = (L2MonsterInstance) target;
            if (monster.isSpoiled()) {
                return;
            }

            monster.setSpoiled(true, owner); //single target spoil
            owner.sendPacket(Msg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
            monster.altUseSkill(SkillTable.getInstance().getInfo(254, 10), monster);
            monster.broadcastSkill(new MagicSkillUse(monster, monster,254,1,  100, 0));
//
//
//            monster.getAroundNpc(250, 200)
//                    .stream()
//                    .filter(L2Object::isMonster)
//                    .map(npcInstance -> (L2MonsterInstance) npcInstance)
//                    .filter(l2MonsterInstance -> !l2MonsterInstance.isSpoiled())
//                    .forEach(l2MonsterInstance -> {
//                        l2MonsterInstance.setSpoiled(true, owner); //single target spoil
//                        owner.sendPacket(Msg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
//                    });


//            owner.altUseSkill(SkillTable.getInstance().getInfo(254, 10), monster); //single target spoil
//            owner.altUseSkill(SkillTable.getInstance().getInfo(302, 8), monster); //multi target spoil
        }
    }

    public void doAction(L2Character target) {
        L2Player owner = getPlayer();
        if (_target == target || owner == null || owner == target) {
            return;
        }
        stopAction();
        _target = target;
        switch (_id) {
            case BEASTLY_AGATHION_ID:
            case RAINBOW_AGATHION_ID:
            case CASTLE_LORD_AGATHION_ID:
            case LITTLE_ANGEL_AGATHION_ID:
            case LITTLE_DEVIL_AGATHION_ID:
            case RUDOLPH_AGATHION_ID:
                _actionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Action(), 0, 10000);
                break;
            case FORTRESS_AGATHION_ID:
                _actionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Action(), 0, 8000);
                break;
        }
    }

    public boolean isUseSpoil() {
        return useSpoil;
    }

    public int getId() {
        return _id;
    }

    public void stopAction() {
        _target = null;
        if (_actionTask != null) {
            _actionTask.cancel(false);
            _actionTask = null;
        }
    }

    public void deleteMe() {
        stopAction();

        L2Player owner = getPlayer();
        if (owner != null) {
            // Забираем скилы у хозяина
            for (L2Skill s : _addSkills)
                owner.removeSkill(s, false, false);
            owner.updateEffectIcons();
            owner.sendPacket(new SkillList(owner));
        }

        _owner = null;
        _skills = null;
        _addSkills = null;
    }

    private class Action extends l2open.common.RunnableImpl {
        public void runImpl() {
            L2Player owner = getPlayer();
            if (owner == null) {
                deleteMe();
                return;
            }

            L2Character target = getTarget();


            if (_skills.size() > 0) {
                L2Skill skill = _skills.get(Rnd.get(_skills.size()));
                if (owner.isDead() || target == null || target.isDead() || !owner.isInRangeZ(target, skill.getCastRange())) {
                    stopAction();
                    if (owner.isDead()) {
                        owner.setAgathion(0);
                        owner.broadcastUserInfo(true);
                    }
                    return;
                }

                try {
                    if (skill != null && Rnd.chance(50) && skill.checkCondition(owner, target, false, false, true)) {
                        owner.altUseSkill(skill, target);
                        owner.broadcastSkill(new MagicSkillUse(owner, target, skill.getId(), 1, 0, 0), true);
                    }
                } catch (final Exception e) {
                    _log.log(Level.SEVERE, "", e);
                } finally {
                    _actionTask = ThreadPoolManager.getInstance().schedule(new Action(), (long) (6. * owner.calculateAttackDelay()), true);
                }
            }
        }
    }

    public L2Player getPlayer() {
        return _owner;
    }

    public L2Character getTarget() {
        return _target;
    }
}