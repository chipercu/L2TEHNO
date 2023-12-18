package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.NpcResource.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "npc",
        primary_key = {ID},
        fields = {
                @Field(name = ORDINAL , type = SMALLINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = NAME , type = VARCHAR , type_size = 45 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = TITLE , type = VARCHAR , type_size = 35 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = CLASS , type = VARCHAR , type_size = 50 , nullable = true , defValue = @DefValue(String = "")),
                @Field(name = RACE , type = VARCHAR , type_size = 45 , nullable = false , defValue = @DefValue(String = "none")),
                @Field(name = COLLISION_RADIUS , type = DECIMAL , nullable = true , defValue = @DefValue(Double = NULL)),
                @Field(name = COLLISION_HEIGHT , type = DECIMAL , nullable = true , defValue = @DefValue(Double = NULL)),
                @Field(name = LEVEL , type = INT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = SEX , type = VARCHAR , type_size = 6 , nullable = false , defValue = @DefValue(String = "male")),
                @Field(name = TYPE , type = VARCHAR , type_size = 30 , nullable = false , defValue = @DefValue(String = "L2Npc")),
                @Field(name = AI_TYPE , type = VARCHAR , type_size = 50 , nullable = false , defValue = @DefValue(String = "npc")),
                @Field(name = ATTACKRANGE , type = INT , nullable = false , defValue = @DefValue(Integer = 40)),
                @Field(name = HP , type = INT , nullable = false , defValue = @DefValue(Integer = 2444)),
                @Field(name = BASE_HP_REGEN , type = DOUBLE , nullable = false , defValue = @DefValue(Double = 7.500)),
                @Field(name = MP , type = INT , nullable = false , defValue = @DefValue(Integer = 2444)),
                @Field(name = BASE_MP_REGEN , type = DOUBLE , nullable = false , defValue = @DefValue(Double = 2.700)),
                @Field(name = STR , type = INT , nullable = false , defValue = @DefValue(Integer = 40)),
                @Field(name = CON , type = INT , nullable = false , defValue = @DefValue(Integer = 43)),
                @Field(name = DEX , type = INT , nullable = false , defValue = @DefValue(Integer = 30)),
                @Field(name = _INT , type = INT , nullable = false , defValue = @DefValue(Integer = 21)),
                @Field(name = WIT , type = INT , nullable = false , defValue = @DefValue(Integer = 20)),
                @Field(name = MEN , type = INT , nullable = false , defValue = @DefValue(Integer = 10)),
                @Field(name = EXP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SP , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PATK , type = INT , nullable = false , defValue = @DefValue(Integer = 500)),
                @Field(name = PDEF , type = INT , nullable = false , defValue = @DefValue(Integer = 500)),
                @Field(name = MATK , type = INT , nullable = false , defValue = @DefValue(Integer = 500)),
                @Field(name = MDEF , type = INT , nullable = false , defValue = @DefValue(Integer = 500)),
                @Field(name = ATKSPD , type = INT , nullable = false , defValue = @DefValue(Integer = 253)),
                @Field(name = AGGRO , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = MATKSPD , type = INT , nullable = false , defValue = @DefValue(Integer = 500)),
                @Field(name = RHAND , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = LHAND , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = ARMOR , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = WALKSPD , type = INT , nullable = false , defValue = @DefValue(Integer = 110)),
                @Field(name = RUNSPD , type = INT , nullable = false , defValue = @DefValue(Integer = 180)),
                @Field(name = FACTION_ID , type = VARCHAR , type_size = 40 , nullable = false , defValue = @DefValue(String = "")),
                @Field(name = FACTION_RANGE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = DISPLAY_ID , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SHIELD_DEFENSE_RATE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = SHIELD_DEFENSE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CORPSE_TIME , type = INT , nullable = false , defValue = @DefValue(Integer = 7)),
                @Field(name = BASE_RAND_DAM , type = INT , nullable = false , defValue = @DefValue(Integer = 30)),
                @Field(name = BASE_CRITICAL , type = INT , nullable = false , defValue = @DefValue(Integer = 4)),
                @Field(name = PHYSICAL_HIT_MODIFY , type = INT , nullable = false , defValue = @DefValue(Integer = 4)),
                @Field(name = BASE_REUSE_DELAY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = PHYSICAL_AVOID_MODIFY , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = HIT_TIME_FACTOR , type = DOUBLE , nullable = false , defValue = @DefValue(Double = 0.60)),
                @Field(name = IS_DROP_HERBS , type = ENUM , type_size = 5 , nullable = false , defValue = @DefValue(Boolean = false)),
                @Field(name = SHOTS , type = ENUM , type_size = 12 , nullable = false , defValue = @DefValue(String = "NONE")),
                @Field(name = MAP_FLAG , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BOSS_FLAG , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = AGRO_RANGE , type = INT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = EVENT_FLAG , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = CAN_BE_ATTACKED , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 1)),
                @Field(name = UNDYING , type = TINYINT , nullable = false , defValue = @DefValue(Integer = 0)),
                @Field(name = BASE_ATTACK_TYPE , type = VARCHAR , type_size = 10 , nullable = false , defValue = @DefValue(String = "FIST")),
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


    public NpcResource(String id){
        super(NpcResource.class);
        getSTAT_SET().set(ID, id);
    }

    public Integer getOrdinal() {
        return get(ORDINAL, Integer.class);
    }
    public Integer getId() {
        return get(ID, Integer.class);
    }
    public String getName() {
        return get(NAME, String.class);
    }
    public String getTitle() {
        return get(TITLE, String.class);
    }
    public String getClass_() {
        return get(CLASS, String.class);
    }
    public String getRace() {
        return get(RACE, String.class);
    }
    public Double getCollisionRadius() {
        return get(COLLISION_RADIUS, Double.class);
    }
    public Double getCollisionHeight() {
        return get(COLLISION_HEIGHT, Double.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public String getSex() {
        return get(SEX, String.class);
    }
    public String getType() {
        return get(TYPE, String.class);
    }
    public String getAiType() {
        return get(AI_TYPE, String.class);
    }
    public Integer getAttackrange() {
        return get(ATTACKRANGE, Integer.class);
    }
    public Integer getHp() {
        return get(HP, Integer.class);
    }
    public Double getBaseHpRegen() {
        return get(BASE_HP_REGEN, Double.class);
    }
    public Integer getMp() {
        return get(MP, Integer.class);
    }
    public Double getBaseMpRegen() {
        return get(BASE_MP_REGEN, Double.class);
    }
    public Integer getStr() {
        return get(STR, Integer.class);
    }
    public Integer getCon() {
        return get(CON, Integer.class);
    }
    public Integer getDex() {
        return get(DEX, Integer.class);
    }
    public Integer getInt() {
        return get(_INT, Integer.class);
    }
    public Integer getWit() {
        return get(WIT, Integer.class);
    }
    public Integer getMen() {
        return get(MEN, Integer.class);
    }
    public Integer getExp() {
        return get(EXP, Integer.class);
    }
    public Integer getSp() {
        return get(SP, Integer.class);
    }
    public Integer getPatk() {
        return get(PATK, Integer.class);
    }
    public Integer getPdef() {
        return get(PDEF, Integer.class);
    }
    public Integer getMatk() {
        return get(MATK, Integer.class);
    }
    public Integer getMdef() {
        return get(MDEF, Integer.class);
    }
    public Integer getAtkspd() {
        return get(ATKSPD, Integer.class);
    }
    public Integer getAggro() {
        return get(AGGRO, Integer.class);
    }
    public Integer getMatkspd() {
        return get(MATKSPD, Integer.class);
    }
    public Integer getRhand() {
        return get(RHAND, Integer.class);
    }
    public Integer getLhand() {
        return get(LHAND, Integer.class);
    }
    public Integer getArmor() {
        return get(ARMOR, Integer.class);
    }
    public Integer getWalkspd() {
        return get(WALKSPD, Integer.class);
    }
    public Integer getRunspd() {
        return get(RUNSPD, Integer.class);
    }
    public String getFactionId() {
        return get(FACTION_ID, String.class);
    }
    public Integer getFactionRange() {
        return get(FACTION_RANGE, Integer.class);
    }
    public Integer getDisplayId() {
        return get(DISPLAY_ID, Integer.class);
    }
    public Integer getShieldDefenseRate() {
        return get(SHIELD_DEFENSE_RATE, Integer.class);
    }
    public Integer getShieldDefense() {
        return get(SHIELD_DEFENSE, Integer.class);
    }
    public Integer getCorpseTime() {
        return get(CORPSE_TIME, Integer.class);
    }
    public Integer getBaseRandDam() {
        return get(BASE_RAND_DAM, Integer.class);
    }
    public Integer getBaseCritical() {
        return get(BASE_CRITICAL, Integer.class);
    }
    public Integer getPhysicalHitModify() {
        return get(PHYSICAL_HIT_MODIFY, Integer.class);
    }
    public Integer getBaseReuseDelay() {
        return get(BASE_REUSE_DELAY, Integer.class);
    }
    public Integer getPhysicalAvoidModify() {
        return get(PHYSICAL_AVOID_MODIFY, Integer.class);
    }
    public Double getHitTimeFactor() {
        return get(HIT_TIME_FACTOR, Double.class);
    }
    public Boolean getIsDropHerbs() {
        return get(IS_DROP_HERBS, Boolean.class);
    }
    public String getShots() {
        return get(SHOTS, String.class);
    }
    public Integer getMapFlag() {
        return get(MAP_FLAG, Integer.class);
    }
    public Integer getBossFlag() {
        return get(BOSS_FLAG, Integer.class);
    }
    public Integer getAgroRange() {
        return get(AGRO_RANGE, Integer.class);
    }
    public Integer getEventFlag() {
        return get(EVENT_FLAG, Integer.class);
    }
    public Integer getCanBeAttacked() {
        return get(CAN_BE_ATTACKED, Integer.class);
    }
    public Integer getUndying() {
        return get(UNDYING, Integer.class);
    }
    public String getBaseAttackType() {
        return get(BASE_ATTACK_TYPE, String.class);
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
