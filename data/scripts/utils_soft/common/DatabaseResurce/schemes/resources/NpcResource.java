package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.NpcResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npc",
        primary_key = {ID},
        fields = {
                @Column(is_null = NO , type = SMALLINT , type_size = 0 , name = ORDINAL , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ID , defValue = "0"),
                @Column(is_null = YES , type = VARCHAR , type_size = 45 , name = NAME , defValue = "NULL"),
                @Column(is_null = NO , type = VARCHAR , type_size = 35 , name = TITLE , defValue = "null"),
                @Column(is_null = YES , type = VARCHAR , type_size = 50 , name = CLASS , defValue = "NULL"),
                @Column(is_null = NO , type = VARCHAR , type_size = 45 , name = RACE , defValue = "none"),
                @Column(is_null = YES , type = DECIMAL , type_size = 0 , name = COLLISION_RADIUS , defValue = "NULL"),
                @Column(is_null = YES , type = DECIMAL , type_size = 0 , name = COLLISION_HEIGHT , defValue = "NULL"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LEVEL , defValue = "1"),
                @Column(is_null = NO , type = VARCHAR , type_size = 6 , name = SEX , defValue = "male"),
                @Column(is_null = NO , type = VARCHAR , type_size = 30 , name = TYPE , defValue = "L2Npc"),
                @Column(is_null = NO , type = VARCHAR , type_size = 50 , name = AI_TYPE , defValue = "npc"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ATTACKRANGE , defValue = "40"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = HP , defValue = "2444"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = BASE_HP_REGEN , defValue = "7.500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MP , defValue = "2444"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = BASE_MP_REGEN , defValue = "2.700"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = STR , defValue = "40"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CON , defValue = "43"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DEX , defValue = "30"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = _INT , defValue = "21"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = WIT , defValue = "20"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MEN , defValue = "10"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = EXP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SP , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PATK , defValue = "500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PDEF , defValue = "500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MATK , defValue = "500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MDEF , defValue = "500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ATKSPD , defValue = "253"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AGGRO , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = MATKSPD , defValue = "500"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RHAND , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = LHAND , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ARMOR , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = WALKSPD , defValue = "110"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RUNSPD , defValue = "180"),
                @Column(is_null = NO , type = VARCHAR , type_size = 40 , name = FACTION_ID , defValue = "null"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = FACTION_RANGE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = DISPLAY_ID , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SHIELD_DEFENSE_RATE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = SHIELD_DEFENSE , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CORPSE_TIME , defValue = "7"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BASE_RAND_DAM , defValue = "30"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BASE_CRITICAL , defValue = "4"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PHYSICAL_HIT_MODIFY , defValue = "4"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = BASE_REUSE_DELAY , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = PHYSICAL_AVOID_MODIFY , defValue = "0"),
                @Column(is_null = NO , type = DOUBLE , type_size = 0 , name = HIT_TIME_FACTOR , defValue = "0.60"),
                @Column(is_null = NO , type = ENUM , type_size = 5 , name = IS_DROP_HERBS , defValue = "true"),
                @Column(is_null = NO , type = ENUM , type_size = 12 , name = SHOTS , defValue = "NONE"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = MAP_FLAG , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = BOSS_FLAG , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = AGRO_RANGE , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = EVENT_FLAG , defValue = "0"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = CAN_BE_ATTACKED , defValue = "1"),
                @Column(is_null = NO , type = TINYINT , type_size = 0 , name = UNDYING , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 10 , name = BASE_ATTACK_TYPE , defValue = "FIST"),
        }
)
public class NpcResource extends DataBaseTable<NpcResource> {

    public static final String ORDINAL = "ordinal";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String CLASS = "class";
    public static final String RACE = "race";
    public static final String COLLISION_RADIUS = "collision_radius";
    public static final String COLLISION_HEIGHT = "collision_height";
    public static final String LEVEL = "level";
    public static final String SEX = "sex";
    public static final String TYPE = "type";
    public static final String AI_TYPE = "ai_type";
    public static final String ATTACKRANGE = "attackrange";
    public static final String HP = "hp";
    public static final String BASE_HP_REGEN = "base_hp_regen";
    public static final String MP = "mp";
    public static final String BASE_MP_REGEN = "base_mp_regen";
    public static final String STR = "str";
    public static final String CON = "con";
    public static final String DEX = "dex";
    public static final String _INT = "int";
    public static final String WIT = "wit";
    public static final String MEN = "men";
    public static final String EXP = "exp";
    public static final String SP = "sp";
    public static final String PATK = "patk";
    public static final String PDEF = "pdef";
    public static final String MATK = "matk";
    public static final String MDEF = "mdef";
    public static final String ATKSPD = "atkspd";
    public static final String AGGRO = "aggro";
    public static final String MATKSPD = "matkspd";
    public static final String RHAND = "rhand";
    public static final String LHAND = "lhand";
    public static final String ARMOR = "armor";
    public static final String WALKSPD = "walkspd";
    public static final String RUNSPD = "runspd";
    public static final String FACTION_ID = "faction_id";
    public static final String FACTION_RANGE = "faction_range";
    public static final String DISPLAY_ID = "displayId";
    public static final String SHIELD_DEFENSE_RATE = "shield_defense_rate";
    public static final String SHIELD_DEFENSE = "shield_defense";
    public static final String CORPSE_TIME = "corpse_time";
    public static final String BASE_RAND_DAM = "base_rand_dam";
    public static final String BASE_CRITICAL = "base_critical";
    public static final String PHYSICAL_HIT_MODIFY = "physical_hit_modify";
    public static final String BASE_REUSE_DELAY = "base_reuse_delay";
    public static final String PHYSICAL_AVOID_MODIFY = "physical_avoid_modify";
    public static final String HIT_TIME_FACTOR = "hit_time_factor";
    public static final String IS_DROP_HERBS = "isDropHerbs";
    public static final String SHOTS = "shots";
    public static final String MAP_FLAG = "map_flag";
    public static final String BOSS_FLAG = "boss_flag";
    public static final String AGRO_RANGE = "agro_range";
    public static final String EVENT_FLAG = "event_flag";
    public static final String CAN_BE_ATTACKED = "can_be_attacked";
    public static final String UNDYING = "undying";
    public static final String BASE_ATTACK_TYPE = "base_attack_type";

    public NpcResource() {
        super(NpcResource.class);
}

    public Integer getOrdinal() {
        return get(ORDINAL);
    }
    public Integer getId() {
        return get(ID);
    }
    public String getName() {
        return get(NAME);
    }
    public String getTitle() {
        return get(TITLE);
    }
    public String getClass_() {
        return get(CLASS);
    }
    public String getRace() {
        return get(RACE);
    }
    public Double getCollisionRadius() {
        return get(COLLISION_RADIUS);
    }
    public Double getCollisionHeight() {
        return get(COLLISION_HEIGHT);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public String getSex() {
        return get(SEX);
    }
    public String getType() {
        return get(TYPE);
    }
    public String getAiType() {
        return get(AI_TYPE);
    }
    public Integer getAttackrange() {
        return get(ATTACKRANGE);
    }
    public Integer getHp() {
        return get(HP);
    }
    public Double getBaseHpRegen() {
        return get(BASE_HP_REGEN);
    }
    public Integer getMp() {
        return get(MP);
    }
    public Double getBaseMpRegen() {
        return get(BASE_MP_REGEN);
    }
    public Integer getStr() {
        return get(STR);
    }
    public Integer getCon() {
        return get(CON);
    }
    public Integer getDex() {
        return get(DEX);
    }
    public Integer getInt() {
        return get(_INT);
    }
    public Integer getWit() {
        return get(WIT);
    }
    public Integer getMen() {
        return get(MEN);
    }
    public Integer getExp() {
        return get(EXP);
    }
    public Integer getSp() {
        return get(SP);
    }
    public Integer getPatk() {
        return get(PATK);
    }
    public Integer getPdef() {
        return get(PDEF);
    }
    public Integer getMatk() {
        return get(MATK);
    }
    public Integer getMdef() {
        return get(MDEF);
    }
    public Integer getAtkspd() {
        return get(ATKSPD);
    }
    public Integer getAggro() {
        return get(AGGRO);
    }
    public Integer getMatkspd() {
        return get(MATKSPD);
    }
    public Integer getRhand() {
        return get(RHAND);
    }
    public Integer getLhand() {
        return get(LHAND);
    }
    public Integer getArmor() {
        return get(ARMOR);
    }
    public Integer getWalkspd() {
        return get(WALKSPD);
    }
    public Integer getRunspd() {
        return get(RUNSPD);
    }
    public String getFactionId() {
        return get(FACTION_ID);
    }
    public Integer getFactionRange() {
        return get(FACTION_RANGE);
    }
    public Integer getDisplayId() {
        return get(DISPLAY_ID);
    }
    public Integer getShieldDefenseRate() {
        return get(SHIELD_DEFENSE_RATE);
    }
    public Integer getShieldDefense() {
        return get(SHIELD_DEFENSE);
    }
    public Integer getCorpseTime() {
        return get(CORPSE_TIME);
    }
    public Integer getBaseRandDam() {
        return get(BASE_RAND_DAM);
    }
    public Integer getBaseCritical() {
        return get(BASE_CRITICAL);
    }
    public Integer getPhysicalHitModify() {
        return get(PHYSICAL_HIT_MODIFY);
    }
    public Integer getBaseReuseDelay() {
        return get(BASE_REUSE_DELAY);
    }
    public Integer getPhysicalAvoidModify() {
        return get(PHYSICAL_AVOID_MODIFY);
    }
    public Double getHitTimeFactor() {
        return get(HIT_TIME_FACTOR);
    }
    public Boolean getIsDropHerbs() {
        return get(IS_DROP_HERBS);
    }
    public String getShots() {
        return get(SHOTS);
    }
    public Integer getMapFlag() {
        return get(MAP_FLAG);
    }
    public Integer getBossFlag() {
        return get(BOSS_FLAG);
    }
    public Integer getAgroRange() {
        return get(AGRO_RANGE);
    }
    public Integer getEventFlag() {
        return get(EVENT_FLAG);
    }
    public Integer getCanBeAttacked() {
        return get(CAN_BE_ATTACKED);
    }
    public Integer getUndying() {
        return get(UNDYING);
    }
    public String getBaseAttackType() {
        return get(BASE_ATTACK_TYPE);
    }

    public void setOrdinal(Integer value) {
        set(ORDINAL, value);
    }
    public void setId(Integer value) {
        set(ID, value);
    }
    public void setName(String value) {
        set(NAME, value);
    }
    public void setTitle(String value) {
        set(TITLE, value);
    }
    public void setClass(String value) {
        set(CLASS, value);
    }
    public void setRace(String value) {
        set(RACE, value);
    }
    public void setCollisionRadius(Double value) {
        set(COLLISION_RADIUS, value);
    }
    public void setCollisionHeight(Double value) {
        set(COLLISION_HEIGHT, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setSex(String value) {
        set(SEX, value);
    }
    public void setType(String value) {
        set(TYPE, value);
    }
    public void setAiType(String value) {
        set(AI_TYPE, value);
    }
    public void setAttackrange(Integer value) {
        set(ATTACKRANGE, value);
    }
    public void setHp(Integer value) {
        set(HP, value);
    }
    public void setBaseHpRegen(Double value) {
        set(BASE_HP_REGEN, value);
    }
    public void setMp(Integer value) {
        set(MP, value);
    }
    public void setBaseMpRegen(Double value) {
        set(BASE_MP_REGEN, value);
    }
    public void setStr(Integer value) {
        set(STR, value);
    }
    public void setCon(Integer value) {
        set(CON, value);
    }
    public void setDex(Integer value) {
        set(DEX, value);
    }
    public void setInt(Integer value) {
        set(_INT, value);
    }
    public void setWit(Integer value) {
        set(WIT, value);
    }
    public void setMen(Integer value) {
        set(MEN, value);
    }
    public void setExp(Integer value) {
        set(EXP, value);
    }
    public void setSp(Integer value) {
        set(SP, value);
    }
    public void setPatk(Integer value) {
        set(PATK, value);
    }
    public void setPdef(Integer value) {
        set(PDEF, value);
    }
    public void setMatk(Integer value) {
        set(MATK, value);
    }
    public void setMdef(Integer value) {
        set(MDEF, value);
    }
    public void setAtkspd(Integer value) {
        set(ATKSPD, value);
    }
    public void setAggro(Integer value) {
        set(AGGRO, value);
    }
    public void setMatkspd(Integer value) {
        set(MATKSPD, value);
    }
    public void setRhand(Integer value) {
        set(RHAND, value);
    }
    public void setLhand(Integer value) {
        set(LHAND, value);
    }
    public void setArmor(Integer value) {
        set(ARMOR, value);
    }
    public void setWalkspd(Integer value) {
        set(WALKSPD, value);
    }
    public void setRunspd(Integer value) {
        set(RUNSPD, value);
    }
    public void setFactionId(String value) {
        set(FACTION_ID, value);
    }
    public void setFactionRange(Integer value) {
        set(FACTION_RANGE, value);
    }
    public void setDisplayId(Integer value) {
        set(DISPLAY_ID, value);
    }
    public void setShieldDefenseRate(Integer value) {
        set(SHIELD_DEFENSE_RATE, value);
    }
    public void setShieldDefense(Integer value) {
        set(SHIELD_DEFENSE, value);
    }
    public void setCorpseTime(Integer value) {
        set(CORPSE_TIME, value);
    }
    public void setBaseRandDam(Integer value) {
        set(BASE_RAND_DAM, value);
    }
    public void setBaseCritical(Integer value) {
        set(BASE_CRITICAL, value);
    }
    public void setPhysicalHitModify(Integer value) {
        set(PHYSICAL_HIT_MODIFY, value);
    }
    public void setBaseReuseDelay(Integer value) {
        set(BASE_REUSE_DELAY, value);
    }
    public void setPhysicalAvoidModify(Integer value) {
        set(PHYSICAL_AVOID_MODIFY, value);
    }
    public void setHitTimeFactor(Double value) {
        set(HIT_TIME_FACTOR, value);
    }
    public void setIsDropHerbs(Boolean value) {
        set(IS_DROP_HERBS, value);
    }
    public void setShots(String value) {
        set(SHOTS, value);
    }
    public void setMapFlag(Integer value) {
        set(MAP_FLAG, value);
    }
    public void setBossFlag(Integer value) {
        set(BOSS_FLAG, value);
    }
    public void setAgroRange(Integer value) {
        set(AGRO_RANGE, value);
    }
    public void setEventFlag(Integer value) {
        set(EVENT_FLAG, value);
    }
    public void setCanBeAttacked(Integer value) {
        set(CAN_BE_ATTACKED, value);
    }
    public void setUndying(Integer value) {
        set(UNDYING, value);
    }
    public void setBaseAttackType(String value) {
        set(BASE_ATTACK_TYPE, value);
    }

}
