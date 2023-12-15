package utils_soft.NpcEditor.models;

import l2open.gameserver.templates.L2NpcTemplate;
import l2open.gameserver.templates.L2Weapon;
import l2open.gameserver.templates.StatsSet;
import utils_soft.common.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class NpcModel extends Model {

    public NpcModel(int id, String name, int displayId, String type, int level) {
        getStatsSet().set("id", id);
        getStatsSet().set("name", name);
        getStatsSet().set("level", level);
        getStatsSet().set("type", type);
        getStatsSet().set("displayId", displayId);
    }

    public NpcModel(ResultSet set) {
        super(set);
    }

    public int getOrdinal() {
        return getStatsSet().getInteger("ordinal");
    }

    public int getId() {
        return getStatsSet().getInteger("id");
    }

    public String getName() {
        return getStat("name", "empty");
    }

    public String getTitle() {
        return getStatsSet().getString("title", "");
    }

    public String getClass_() {
        return getStatsSet().getString("class", "etc");
    }

    public String getRace() {
        return getStatsSet().getString("race", "etc");
    }

    public int getCollision_radius() {
        return getStatsSet().getInteger("collision_radius", 5);
    }

    public int getCollision_height() {
        return getStatsSet().getInteger("collision_height", 5);
    }

    public int getLevel() {
        return getStatsSet().getInteger("level", 1);
    }

    public String getSex() {
        return getStatsSet().getString("sex", "male");
    }

    public String getType() {
        return getStatsSet().getString("type", "L2Npc");
    }

    public String getAi_type() {
        return getStatsSet().getString("ai_type", "Fighter");
    }

    public int getAttackRange() {
        return getStatsSet().getInteger("attackrange", 40);
    }

    public int getHP() {
        return getStatsSet().getInteger("hp", 100);
    }

    public double getBaseHpRegen() {
        return getStatsSet().getDouble("base_hp_regen", 5.0);
    }

    public int getMP() {
        return getStatsSet().getInteger("mp", 100);
    }

    public double getBaseMpRegen() {
        return getStatsSet().getDouble("base_mp_regen", 3.0);
    }

    public int getSTR() {
        return getStatsSet().getInteger("str", 40);
    }

    public int getCON() {
        return getStatsSet().getInteger("con", 40);
    }

    public int getDEX() {
        return getStatsSet().getInteger("dex", 30);
    }

    public int getINT() {
        return getStatsSet().getInteger("int", 20);
    }

    public int getWIT() {
        return getStatsSet().getInteger("wit", 20);
    }

    public int getMEN() {
        return getStatsSet().getInteger("men", 10);
    }

    public int getExp() {
        return getStatsSet().getInteger("exp", 0);
    }

    public int getSP() {
        return getStatsSet().getInteger("sp", 0);
    }

    public int getPatk() {
        return getStatsSet().getInteger("patk", 100);
    }

    public int getPdef() {
        return getStatsSet().getInteger("pdef", 100);
    }

    public int getMatk() {
        return getStatsSet().getInteger("matk", 100);
    }

    public int getMdef() {
        return getStatsSet().getInteger("mdef", 100);
    }

    public int getAtkspd() {
        return getStatsSet().getInteger("atkspd", 300);
    }

    public int getAggro() {
        return getStatsSet().getInteger("aggro", 0);
    }

    public int getMatkspd() {
        return getStatsSet().getInteger("matkspd", 500);
    }

    public int getRhand() {
        return getStatsSet().getInteger("rhand", 0);
    }

    public int getLhand() {
        return getStatsSet().getInteger("lhand", 0);
    }

    public int getArmor() {
        return getStatsSet().getInteger("armor", 0);
    }

    public int getWalkSpd() {
        return getStatsSet().getInteger("walkspd", 100);
    }

    public int getRunSpd() {
        return getStatsSet().getInteger("runspd", 170);
    }

    public String getFactionId() {
        return getStatsSet().getString("faction_id", "");
    }

    public int getFactionRange() {
        return getStatsSet().getInteger("faction_range", 0);
    }

    public int getDisplayId() {
        return getStatsSet().getInteger("displayId", 0);
    }

    public int getShieldDefenseRate() {
        return getStatsSet().getInteger("shield_defense_rate", 0);
    }

    public int getShieldDefense() {
        return getStatsSet().getInteger("shield_defense", 0);
    }

    public int getCorpseTime() {
        return getStatsSet().getInteger("shield_defense", 7);
    }

    public int getBaseRandDam() {
        return getStatsSet().getInteger("base_rand_dam", 10);
    }

    public int getBaseCritical() {
        return getStatsSet().getInteger("base_critical", 1);
    }

    public int getPhysicalHitModify() {
        return getStatsSet().getInteger("physical_hit_modify", 4);
    }

    public int getBaseReuseDelay() {
        return getStatsSet().getInteger("base_reuse_delay", 0);
    }

    public int getPhysicalAvoidModify() {
        return getStatsSet().getInteger("physical_avoid_modify", 0);
    }

    public double getHitTimeFactor() {
        return getStatsSet().getDouble("hit_time_factor", 0.6);
    }

    public boolean isDropHerbs() {
        return getStatsSet().getBool("isDropHerbs", false);
    }

    public L2NpcTemplate.ShotsType getShots() {
        return getStatsSet().getEnum("shots", L2NpcTemplate.ShotsType.class, L2NpcTemplate.ShotsType.NONE);
    }

    public int getMapFlag() {
        return getStatsSet().getInteger("map_flag", 0);
    }

    public int getBossFlag() {
        return getStatsSet().getInteger("boss_flag", 0);
    }

    public int getAgroRange() {
        return getStatsSet().getInteger("agro_range", 0);
    }

    public int getEventFlag() {
        return getStatsSet().getInteger("event_flag", 0);
    }

    public int getCanBeAttacked() {
        return getStatsSet().getInteger("can_be_attacked", 1);
    }

    public int getUndying() {
        return getStatsSet().getInteger("undying", 0);
    }

    public L2Weapon.WeaponType getBaseAttackType() {
        return getStatsSet().getEnum("base_attack_type", L2Weapon.WeaponType.class, L2Weapon.WeaponType.SWORD);
    }
}
