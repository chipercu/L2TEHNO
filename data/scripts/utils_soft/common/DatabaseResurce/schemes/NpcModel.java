package utils_soft.common.DatabaseResurce.schemes;

import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Weapon;
import utils_soft.common.DatabaseResurce.DataBaseTable;

public class NpcModel extends DataBaseTable<NpcModel> {

    public NpcModel(int id, String name, int displayId, String type, int level) {
        super(NpcModel.class);

        setStat("id", id);
        setStat("name", name);
        setStat("level", level);
        setStat("type", type);
        setStat("displayId", displayId);
    }

    public NpcModel() {
        super(NpcModel.class);
    }

    public int getOrdinal() {
        return getStat("ordinal", 0);
    }

    public int getId() {
        return getStat("id", 0);
    }

    public String getName() {
        return getStat("name", "empty");
    }

    public String getTitle() {
        return getStat("title", "");
    }

    public String getClass_() {
        return getStat("class", "etc");
    }

    public String getRace() {
        return getStat("race", "etc");
    }

    public int getCollision_radius() {
        return getStat("collision_radius", 5);
    }

    public int getCollision_height() {
        return getStat("collision_height", 5);
    }

    public int getLevel() {
        return getStat("level", 1);
    }

    public String getSex() {
        return getStat("sex", "male");
    }

    public String getType() {
        return getStat("type", "L2Npc");
    }

    public String getAi_type() {
        return getStat("ai_type", "Fighter");
    }

    public int getAttackRange() {
        return getStat("attackrange", 40);
    }

    public int getHP() {
        return getStat("hp", 100);
    }

    public double getBaseHpRegen() {
        return getStat("base_hp_regen", 5.0);
    }

    public int getMP() {
        return getStat("mp", 100);
    }

    public double getBaseMpRegen() {
        return getStat("base_mp_regen", 3.0);
    }

    public int getSTR() {
        return getStat("str", 40);
    }

    public int getCON() {
        return getStat("con", 40);
    }

    public int getDEX() {
        return getStat("dex", 30);
    }

    public int getINT() {
        return getStat("int", 20);
    }

    public int getWIT() {
        return getStat("wit", 20);
    }

    public int getMEN() {
        return getStat("men", 10);
    }

    public int getExp() {
        return getStat("exp", 0);
    }

    public int getSP() {
        return getStat("sp", 0);
    }

    public int getPatk() {
        return getStat("patk", 100);
    }

    public int getPdef() {
        return getStat("pdef", 100);
    }

    public int getMatk() {
        return getStat("matk", 100);
    }

    public int getMdef() {
        return getStat("mdef", 100);
    }

    public int getAtkspd() {
        return getStat("atkspd", 300);
    }

    public int getAggro() {
        return getStat("aggro", 0);
    }

    public int getMatkspd() {
        return getStat("matkspd", 500);
    }

    public int getRhand() {
        return getStat("rhand", 0);
    }

    public int getLhand() {
        return getStat("lhand", 0);
    }

    public int getArmor() {
        return getStat("armor", 0);
    }

    public int getWalkSpd() {
        return getStat("walkspd", 100);
    }

    public int getRunSpd() {
        return getStat("runspd", 170);
    }

    public String getFactionId() {
        return getStat("faction_id", "");
    }

    public int getFactionRange() {
        return getStat("faction_range", 0);
    }

    public int getDisplayId() {
        return getStat("displayId", 0);
    }

    public int getShieldDefenseRate() {
        return getStat("shield_defense_rate", 0);
    }

    public int getShieldDefense() {
        return getStat("shield_defense", 0);
    }

    public int getCorpseTime() {
        return getStat("shield_defense", 7);
    }

    public int getBaseRandDam() {
        return getStat("base_rand_dam", 10);
    }

    public int getBaseCritical() {
        return getStat("base_critical", 1);
    }

    public int getPhysicalHitModify() {
        return getStat("physical_hit_modify", 4);
    }

    public int getBaseReuseDelay() {
        return getStat("base_reuse_delay", 0);
    }

    public int getPhysicalAvoidModify() {
        return getStat("physical_avoid_modify", 0);
    }

    public double getHitTimeFactor() {
        return getStat("hit_time_factor", 0.6);
    }

    public boolean isDropHerbs() {
        return getStat("isDropHerbs", false);
    }

    public L2NpcTemplate.ShotsType getShots() {
        return getStat("shots", L2NpcTemplate.ShotsType.class, L2NpcTemplate.ShotsType.NONE);
    }

    public int getMapFlag() {
        return getStat("map_flag", 0);
    }

    public int getBossFlag() {
        return getStat("boss_flag", 0);
    }

    public int getAgroRange() {
        return getStat("agro_range", 0);
    }

    public int getEventFlag() {
        return getStat("event_flag", 0);
    }

    public int getCanBeAttacked() {
        return getStat("can_be_attacked", 1);
    }

    public int getUndying() {
        return getStat("undying", 0);
    }

    public L2Weapon.WeaponType getBaseAttackType() {
        return getStat("base_attack_type", L2Weapon.WeaponType.class, L2Weapon.WeaponType.SWORD);
    }
}
