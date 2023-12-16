package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.NpcTable.*;

@Table(
        name = "npc",
        fields = {
                @Field(name = ORDINAL,
                @Field(name = ID,
                @Field(name = NAME,
                @Field(name = TITLE,
                @Field(name = CLASS,
                @Field(name = RACE,
                @Field(name = COLLISION_RADIUS,
                @Field(name = COLLISION_HEIGHT,
                @Field(name = LEVEL,
                @Field(name = SEX,
                @Field(name = TYPE,
                @Field(name = AI_TYPE,
                @Field(name = ATTACKRANGE,
                @Field(name = HP,
                @Field(name = BASE_HP_REGEN,
                @Field(name = MP,
                @Field(name = BASE_MP_REGEN,
                @Field(name = STR,
                @Field(name = CON,
                @Field(name = DEX,
                @Field(name = INT,
                @Field(name = WIT,
                @Field(name = MEN,
                @Field(name = EXP,
                @Field(name = SP,
                @Field(name = PATK,
                @Field(name = PDEF,
                @Field(name = MATK,
                @Field(name = MDEF,
                @Field(name = ATKSPD,
                @Field(name = AGGRO,
                @Field(name = MATKSPD,
                @Field(name = RHAND,
                @Field(name = LHAND,
                @Field(name = ARMOR,
                @Field(name = WALKSPD,
                @Field(name = RUNSPD,
                @Field(name = FACTION_ID,
                @Field(name = FACTION_RANGE,
                @Field(name = DISPLAYID,
                @Field(name = SHIELD_DEFENSE_RATE,
                @Field(name = SHIELD_DEFENSE,
                @Field(name = CORPSE_TIME,
                @Field(name = BASE_RAND_DAM,
                @Field(name = BASE_CRITICAL,
                @Field(name = PHYSICAL_HIT_MODIFY,
                @Field(name = BASE_REUSE_DELAY,
                @Field(name = PHYSICAL_AVOID_MODIFY,
                @Field(name = HIT_TIME_FACTOR,
                @Field(name = ISDROPHERBS,
                @Field(name = SHOTS,
                @Field(name = MAP_FLAG,
                @Field(name = BOSS_FLAG,
                @Field(name = AGRO_RANGE,
                @Field(name = EVENT_FLAG,
                @Field(name = CAN_BE_ATTACKED,
                @Field(name = UNDYING,
                @Field(name = BASE_ATTACK_TYPE,
        }
)
public class NpcTable extends DataBaseTable<NpcTable> {

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
    public static final String INT = "int";
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
    public static final String DISPLAYID = "displayId";
    public static final String SHIELD_DEFENSE_RATE = "shield_defense_rate";
    public static final String SHIELD_DEFENSE = "shield_defense";
    public static final String CORPSE_TIME = "corpse_time";
    public static final String BASE_RAND_DAM = "base_rand_dam";
    public static final String BASE_CRITICAL = "base_critical";
    public static final String PHYSICAL_HIT_MODIFY = "physical_hit_modify";
    public static final String BASE_REUSE_DELAY = "base_reuse_delay";
    public static final String PHYSICAL_AVOID_MODIFY = "physical_avoid_modify";
    public static final String HIT_TIME_FACTOR = "hit_time_factor";
    public static final String ISDROPHERBS = "isDropHerbs";
    public static final String SHOTS = "shots";
    public static final String MAP_FLAG = "map_flag";
    public static final String BOSS_FLAG = "boss_flag";
    public static final String AGRO_RANGE = "agro_range";
    public static final String EVENT_FLAG = "event_flag";
    public static final String CAN_BE_ATTACKED = "can_be_attacked";
    public static final String UNDYING = "undying";
    public static final String BASE_ATTACK_TYPE = "base_attack_type";
    public NpcTable() {
super(NpcTable.class);
}
    public IntegergetOrdinal() {
        return get(ORDINAL, Integer.class);
    }
    public IntegergetId() {
        return get(ID, Integer.class);
    }
    public StringgetName() {
        return get(NAME, String.class);
    }
    public StringgetTitle() {
        return get(TITLE, String.class);
    }
    public StringgetClass() {
        return get(CLASS, String.class);
    }
    public StringgetRace() {
        return get(RACE, String.class);
    }
    public DoublegetCollision_radius() {
        return get(COLLISION_RADIUS, Double.class);
    }
    public DoublegetCollision_height() {
        return get(COLLISION_HEIGHT, Double.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public StringgetSex() {
        return get(SEX, String.class);
    }
    public StringgetType() {
        return get(TYPE, String.class);
    }
    public StringgetAi_type() {
        return get(AI_TYPE, String.class);
    }
    public IntegergetAttackrange() {
        return get(ATTACKRANGE, Integer.class);
    }
    public IntegergetHp() {
        return get(HP, Integer.class);
    }
    public DoublegetBase_hp_regen() {
        return get(BASE_HP_REGEN, Double.class);
    }
    public IntegergetMp() {
        return get(MP, Integer.class);
    }
    public DoublegetBase_mp_regen() {
        return get(BASE_MP_REGEN, Double.class);
    }
    public IntegergetStr() {
        return get(STR, Integer.class);
    }
    public IntegergetCon() {
        return get(CON, Integer.class);
    }
    public IntegergetDex() {
        return get(DEX, Integer.class);
    }
    public IntegergetInt() {
        return get(INT, Integer.class);
    }
    public IntegergetWit() {
        return get(WIT, Integer.class);
    }
    public IntegergetMen() {
        return get(MEN, Integer.class);
    }
    public IntegergetExp() {
        return get(EXP, Integer.class);
    }
    public IntegergetSp() {
        return get(SP, Integer.class);
    }
    public IntegergetPatk() {
        return get(PATK, Integer.class);
    }
    public IntegergetPdef() {
        return get(PDEF, Integer.class);
    }
    public IntegergetMatk() {
        return get(MATK, Integer.class);
    }
    public IntegergetMdef() {
        return get(MDEF, Integer.class);
    }
    public IntegergetAtkspd() {
        return get(ATKSPD, Integer.class);
    }
    public IntegergetAggro() {
        return get(AGGRO, Integer.class);
    }
    public IntegergetMatkspd() {
        return get(MATKSPD, Integer.class);
    }
    public IntegergetRhand() {
        return get(RHAND, Integer.class);
    }
    public IntegergetLhand() {
        return get(LHAND, Integer.class);
    }
    public IntegergetArmor() {
        return get(ARMOR, Integer.class);
    }
    public IntegergetWalkspd() {
        return get(WALKSPD, Integer.class);
    }
    public IntegergetRunspd() {
        return get(RUNSPD, Integer.class);
    }
    public StringgetFaction_id() {
        return get(FACTION_ID, String.class);
    }
    public IntegergetFaction_range() {
        return get(FACTION_RANGE, Integer.class);
    }
    public IntegergetDisplayId() {
        return get(DISPLAYID, Integer.class);
    }
    public IntegergetShield_defense_rate() {
        return get(SHIELD_DEFENSE_RATE, Integer.class);
    }
    public IntegergetShield_defense() {
        return get(SHIELD_DEFENSE, Integer.class);
    }
    public IntegergetCorpse_time() {
        return get(CORPSE_TIME, Integer.class);
    }
    public IntegergetBase_rand_dam() {
        return get(BASE_RAND_DAM, Integer.class);
    }
    public IntegergetBase_critical() {
        return get(BASE_CRITICAL, Integer.class);
    }
    public IntegergetPhysical_hit_modify() {
        return get(PHYSICAL_HIT_MODIFY, Integer.class);
    }
    public IntegergetBase_reuse_delay() {
        return get(BASE_REUSE_DELAY, Integer.class);
    }
    public IntegergetPhysical_avoid_modify() {
        return get(PHYSICAL_AVOID_MODIFY, Integer.class);
    }
    public DoublegetHit_time_factor() {
        return get(HIT_TIME_FACTOR, Double.class);
    }
    public ObjectgetIsDropHerbs() {
        return get(ISDROPHERBS, Object.class);
    }
    public ObjectgetShots() {
        return get(SHOTS, Object.class);
    }
    public IntegergetMap_flag() {
        return get(MAP_FLAG, Integer.class);
    }
    public IntegergetBoss_flag() {
        return get(BOSS_FLAG, Integer.class);
    }
    public IntegergetAgro_range() {
        return get(AGRO_RANGE, Integer.class);
    }
    public IntegergetEvent_flag() {
        return get(EVENT_FLAG, Integer.class);
    }
    public IntegergetCan_be_attacked() {
        return get(CAN_BE_ATTACKED, Integer.class);
    }
    public IntegergetUndying() {
        return get(UNDYING, Integer.class);
    }
    public StringgetBase_attack_type() {
        return get(BASE_ATTACK_TYPE, String.class);
    }

    public void setOrdinal() {
        set(ORDINAL, value);
    }
    public void setId() {
        set(ID, value);
    }
    public void setName() {
        set(NAME, value);
    }
    public void setTitle() {
        set(TITLE, value);
    }
    public void setClass() {
        set(CLASS, value);
    }
    public void setRace() {
        set(RACE, value);
    }
    public void setCollision_radius() {
        set(COLLISION_RADIUS, value);
    }
    public void setCollision_height() {
        set(COLLISION_HEIGHT, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setSex() {
        set(SEX, value);
    }
    public void setType() {
        set(TYPE, value);
    }
    public void setAi_type() {
        set(AI_TYPE, value);
    }
    public void setAttackrange() {
        set(ATTACKRANGE, value);
    }
    public void setHp() {
        set(HP, value);
    }
    public void setBase_hp_regen() {
        set(BASE_HP_REGEN, value);
    }
    public void setMp() {
        set(MP, value);
    }
    public void setBase_mp_regen() {
        set(BASE_MP_REGEN, value);
    }
    public void setStr() {
        set(STR, value);
    }
    public void setCon() {
        set(CON, value);
    }
    public void setDex() {
        set(DEX, value);
    }
    public void setInt() {
        set(INT, value);
    }
    public void setWit() {
        set(WIT, value);
    }
    public void setMen() {
        set(MEN, value);
    }
    public void setExp() {
        set(EXP, value);
    }
    public void setSp() {
        set(SP, value);
    }
    public void setPatk() {
        set(PATK, value);
    }
    public void setPdef() {
        set(PDEF, value);
    }
    public void setMatk() {
        set(MATK, value);
    }
    public void setMdef() {
        set(MDEF, value);
    }
    public void setAtkspd() {
        set(ATKSPD, value);
    }
    public void setAggro() {
        set(AGGRO, value);
    }
    public void setMatkspd() {
        set(MATKSPD, value);
    }
    public void setRhand() {
        set(RHAND, value);
    }
    public void setLhand() {
        set(LHAND, value);
    }
    public void setArmor() {
        set(ARMOR, value);
    }
    public void setWalkspd() {
        set(WALKSPD, value);
    }
    public void setRunspd() {
        set(RUNSPD, value);
    }
    public void setFaction_id() {
        set(FACTION_ID, value);
    }
    public void setFaction_range() {
        set(FACTION_RANGE, value);
    }
    public void setDisplayId() {
        set(DISPLAYID, value);
    }
    public void setShield_defense_rate() {
        set(SHIELD_DEFENSE_RATE, value);
    }
    public void setShield_defense() {
        set(SHIELD_DEFENSE, value);
    }
    public void setCorpse_time() {
        set(CORPSE_TIME, value);
    }
    public void setBase_rand_dam() {
        set(BASE_RAND_DAM, value);
    }
    public void setBase_critical() {
        set(BASE_CRITICAL, value);
    }
    public void setPhysical_hit_modify() {
        set(PHYSICAL_HIT_MODIFY, value);
    }
    public void setBase_reuse_delay() {
        set(BASE_REUSE_DELAY, value);
    }
    public void setPhysical_avoid_modify() {
        set(PHYSICAL_AVOID_MODIFY, value);
    }
    public void setHit_time_factor() {
        set(HIT_TIME_FACTOR, value);
    }
    public void setIsDropHerbs() {
        set(ISDROPHERBS, value);
    }
    public void setShots() {
        set(SHOTS, value);
    }
    public void setMap_flag() {
        set(MAP_FLAG, value);
    }
    public void setBoss_flag() {
        set(BOSS_FLAG, value);
    }
    public void setAgro_range() {
        set(AGRO_RANGE, value);
    }
    public void setEvent_flag() {
        set(EVENT_FLAG, value);
    }
    public void setCan_be_attacked() {
        set(CAN_BE_ATTACKED, value);
    }
    public void setUndying() {
        set(UNDYING, value);
    }
    public void setBase_attack_type() {
        set(BASE_ATTACK_TYPE, value);
    }

}
