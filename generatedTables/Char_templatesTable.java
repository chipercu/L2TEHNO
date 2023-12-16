package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.Char_templatesTable.*;

@Table(
        name = "char_templates",
        fields = {
                @Field(name = CLASSID,
                @Field(name = CLASSNAME,
                @Field(name = RACEID,
                @Field(name = PARENT,
                @Field(name = LEVEL,
                @Field(name = STR,
                @Field(name = CON,
                @Field(name = DEX,
                @Field(name = _INT,
                @Field(name = WIT,
                @Field(name = MEN,
                @Field(name = P_ATK,
                @Field(name = P_DEF,
                @Field(name = M_ATK,
                @Field(name = M_DEF,
                @Field(name = P_SPD,
                @Field(name = M_SPD,
                @Field(name = ACC,
                @Field(name = CRITICAL,
                @Field(name = EVASION,
                @Field(name = RUN_SPD,
                @Field(name = WALK_SPD,
                @Field(name = X,
                @Field(name = Y,
                @Field(name = Z,
                @Field(name = CANCRAFT,
                @Field(name = M_UNK1,
                @Field(name = M_UNK2,
                @Field(name = M_COL_R,
                @Field(name = M_COL_H,
                @Field(name = F_UNK1,
                @Field(name = F_UNK2,
                @Field(name = F_COL_R,
                @Field(name = F_COL_H,
                @Field(name = ITEMS1,
                @Field(name = ITEMS2,
                @Field(name = ITEMS3,
                @Field(name = ITEMS4,
                @Field(name = ITEMS5,
                @Field(name = P_ATK_MOD,
                @Field(name = M_ATK_MOD,
                @Field(name = P_DEF_MOD,
                @Field(name = M_DEF_MOD,
                @Field(name = M_ATK_SPD_MOD,
                @Field(name = P_ATK_SPD_MOD,
                @Field(name = M_ATK_CRIT_CHANCE_MOD,
                @Field(name = P_ATK_CRIT_CHANCE_MOD,
                @Field(name = P_CRITICAL_DAMAGE_PER_MOD,
                @Field(name = P_CRITICAL_DAMAGE_DIFF_MOD,
                @Field(name = HP_MOD,
                @Field(name = MP_MOD,
                @Field(name = P_CRITICAL_RATE_MOD,
        }
)
public class Char_templatesTable extends DataBaseTable<Char_templatesTable> {

    public static final String CLASSID = "ClassId";
    public static final String CLASSNAME = "ClassName";
    public static final String RACEID = "RaceId";
    public static final String PARENT = "parent";
    public static final String LEVEL = "level";
    public static final String STR = "STR";
    public static final String CON = "CON";
    public static final String DEX = "DEX";
    public static final String _INT = "_INT";
    public static final String WIT = "WIT";
    public static final String MEN = "MEN";
    public static final String P_ATK = "P_ATK";
    public static final String P_DEF = "P_DEF";
    public static final String M_ATK = "M_ATK";
    public static final String M_DEF = "M_DEF";
    public static final String P_SPD = "P_SPD";
    public static final String M_SPD = "M_SPD";
    public static final String ACC = "ACC";
    public static final String CRITICAL = "CRITICAL";
    public static final String EVASION = "EVASION";
    public static final String RUN_SPD = "RUN_SPD";
    public static final String WALK_SPD = "WALK_SPD";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String CANCRAFT = "canCraft";
    public static final String M_UNK1 = "M_UNK1";
    public static final String M_UNK2 = "M_UNK2";
    public static final String M_COL_R = "M_COL_R";
    public static final String M_COL_H = "M_COL_H";
    public static final String F_UNK1 = "F_UNK1";
    public static final String F_UNK2 = "F_UNK2";
    public static final String F_COL_R = "F_COL_R";
    public static final String F_COL_H = "F_COL_H";
    public static final String ITEMS1 = "items1";
    public static final String ITEMS2 = "items2";
    public static final String ITEMS3 = "items3";
    public static final String ITEMS4 = "items4";
    public static final String ITEMS5 = "items5";
    public static final String P_ATK_MOD = "p_atk_mod";
    public static final String M_ATK_MOD = "m_atk_mod";
    public static final String P_DEF_MOD = "p_def_mod";
    public static final String M_DEF_MOD = "m_def_mod";
    public static final String M_ATK_SPD_MOD = "m_atk_spd_mod";
    public static final String P_ATK_SPD_MOD = "p_atk_spd_mod";
    public static final String M_ATK_CRIT_CHANCE_MOD = "m_atk_crit_chance_mod";
    public static final String P_ATK_CRIT_CHANCE_MOD = "p_atk_crit_chance_mod";
    public static final String P_CRITICAL_DAMAGE_PER_MOD = "p_critical_damage_per_mod";
    public static final String P_CRITICAL_DAMAGE_DIFF_MOD = "p_critical_damage_diff_mod";
    public static final String HP_MOD = "hp_mod";
    public static final String MP_MOD = "mp_mod";
    public static final String P_CRITICAL_RATE_MOD = "p_critical_rate_mod";
    public Char_templatesTable() {
super(Char_templatesTable.class);
}
    public IntegergetClassId() {
        return get(CLASSID, Integer.class);
    }
    public StringgetClassName() {
        return get(CLASSNAME, String.class);
    }
    public IntegergetRaceId() {
        return get(RACEID, Integer.class);
    }
    public IntegergetParent() {
        return get(PARENT, Integer.class);
    }
    public IntegergetLevel() {
        return get(LEVEL, Integer.class);
    }
    public IntegergetSTR() {
        return get(STR, Integer.class);
    }
    public IntegergetCON() {
        return get(CON, Integer.class);
    }
    public IntegergetDEX() {
        return get(DEX, Integer.class);
    }
    public Integerget_INT() {
        return get(_INT, Integer.class);
    }
    public IntegergetWIT() {
        return get(WIT, Integer.class);
    }
    public IntegergetMEN() {
        return get(MEN, Integer.class);
    }
    public IntegergetP_ATK() {
        return get(P_ATK, Integer.class);
    }
    public IntegergetP_DEF() {
        return get(P_DEF, Integer.class);
    }
    public IntegergetM_ATK() {
        return get(M_ATK, Integer.class);
    }
    public IntegergetM_DEF() {
        return get(M_DEF, Integer.class);
    }
    public IntegergetP_SPD() {
        return get(P_SPD, Integer.class);
    }
    public IntegergetM_SPD() {
        return get(M_SPD, Integer.class);
    }
    public IntegergetACC() {
        return get(ACC, Integer.class);
    }
    public IntegergetCRITICAL() {
        return get(CRITICAL, Integer.class);
    }
    public IntegergetEVASION() {
        return get(EVASION, Integer.class);
    }
    public IntegergetRUN_SPD() {
        return get(RUN_SPD, Integer.class);
    }
    public IntegergetWALK_SPD() {
        return get(WALK_SPD, Integer.class);
    }
    public IntegergetX() {
        return get(X, Integer.class);
    }
    public IntegergetY() {
        return get(Y, Integer.class);
    }
    public IntegergetZ() {
        return get(Z, Integer.class);
    }
    public IntegergetCanCraft() {
        return get(CANCRAFT, Integer.class);
    }
    public DoublegetM_UNK1() {
        return get(M_UNK1, Double.class);
    }
    public DoublegetM_UNK2() {
        return get(M_UNK2, Double.class);
    }
    public DoublegetM_COL_R() {
        return get(M_COL_R, Double.class);
    }
    public DoublegetM_COL_H() {
        return get(M_COL_H, Double.class);
    }
    public DoublegetF_UNK1() {
        return get(F_UNK1, Double.class);
    }
    public DoublegetF_UNK2() {
        return get(F_UNK2, Double.class);
    }
    public DoublegetF_COL_R() {
        return get(F_COL_R, Double.class);
    }
    public DoublegetF_COL_H() {
        return get(F_COL_H, Double.class);
    }
    public IntegergetItems1() {
        return get(ITEMS1, Integer.class);
    }
    public IntegergetItems2() {
        return get(ITEMS2, Integer.class);
    }
    public IntegergetItems3() {
        return get(ITEMS3, Integer.class);
    }
    public IntegergetItems4() {
        return get(ITEMS4, Integer.class);
    }
    public IntegergetItems5() {
        return get(ITEMS5, Integer.class);
    }
    public ObjectgetP_atk_mod() {
        return get(P_ATK_MOD, Object.class);
    }
    public ObjectgetM_atk_mod() {
        return get(M_ATK_MOD, Object.class);
    }
    public ObjectgetP_def_mod() {
        return get(P_DEF_MOD, Object.class);
    }
    public ObjectgetM_def_mod() {
        return get(M_DEF_MOD, Object.class);
    }
    public ObjectgetM_atk_spd_mod() {
        return get(M_ATK_SPD_MOD, Object.class);
    }
    public ObjectgetP_atk_spd_mod() {
        return get(P_ATK_SPD_MOD, Object.class);
    }
    public ObjectgetM_atk_crit_chance_mod() {
        return get(M_ATK_CRIT_CHANCE_MOD, Object.class);
    }
    public ObjectgetP_atk_crit_chance_mod() {
        return get(P_ATK_CRIT_CHANCE_MOD, Object.class);
    }
    public ObjectgetP_critical_damage_per_mod() {
        return get(P_CRITICAL_DAMAGE_PER_MOD, Object.class);
    }
    public IntegergetP_critical_damage_diff_mod() {
        return get(P_CRITICAL_DAMAGE_DIFF_MOD, Integer.class);
    }
    public ObjectgetHp_mod() {
        return get(HP_MOD, Object.class);
    }
    public ObjectgetMp_mod() {
        return get(MP_MOD, Object.class);
    }
    public ObjectgetP_critical_rate_mod() {
        return get(P_CRITICAL_RATE_MOD, Object.class);
    }

    public void setClassId() {
        set(CLASSID, value);
    }
    public void setClassName() {
        set(CLASSNAME, value);
    }
    public void setRaceId() {
        set(RACEID, value);
    }
    public void setParent() {
        set(PARENT, value);
    }
    public void setLevel() {
        set(LEVEL, value);
    }
    public void setSTR() {
        set(STR, value);
    }
    public void setCON() {
        set(CON, value);
    }
    public void setDEX() {
        set(DEX, value);
    }
    public void set_INT() {
        set(_INT, value);
    }
    public void setWIT() {
        set(WIT, value);
    }
    public void setMEN() {
        set(MEN, value);
    }
    public void setP_ATK() {
        set(P_ATK, value);
    }
    public void setP_DEF() {
        set(P_DEF, value);
    }
    public void setM_ATK() {
        set(M_ATK, value);
    }
    public void setM_DEF() {
        set(M_DEF, value);
    }
    public void setP_SPD() {
        set(P_SPD, value);
    }
    public void setM_SPD() {
        set(M_SPD, value);
    }
    public void setACC() {
        set(ACC, value);
    }
    public void setCRITICAL() {
        set(CRITICAL, value);
    }
    public void setEVASION() {
        set(EVASION, value);
    }
    public void setRUN_SPD() {
        set(RUN_SPD, value);
    }
    public void setWALK_SPD() {
        set(WALK_SPD, value);
    }
    public void setX() {
        set(X, value);
    }
    public void setY() {
        set(Y, value);
    }
    public void setZ() {
        set(Z, value);
    }
    public void setCanCraft() {
        set(CANCRAFT, value);
    }
    public void setM_UNK1() {
        set(M_UNK1, value);
    }
    public void setM_UNK2() {
        set(M_UNK2, value);
    }
    public void setM_COL_R() {
        set(M_COL_R, value);
    }
    public void setM_COL_H() {
        set(M_COL_H, value);
    }
    public void setF_UNK1() {
        set(F_UNK1, value);
    }
    public void setF_UNK2() {
        set(F_UNK2, value);
    }
    public void setF_COL_R() {
        set(F_COL_R, value);
    }
    public void setF_COL_H() {
        set(F_COL_H, value);
    }
    public void setItems1() {
        set(ITEMS1, value);
    }
    public void setItems2() {
        set(ITEMS2, value);
    }
    public void setItems3() {
        set(ITEMS3, value);
    }
    public void setItems4() {
        set(ITEMS4, value);
    }
    public void setItems5() {
        set(ITEMS5, value);
    }
    public void setP_atk_mod() {
        set(P_ATK_MOD, value);
    }
    public void setM_atk_mod() {
        set(M_ATK_MOD, value);
    }
    public void setP_def_mod() {
        set(P_DEF_MOD, value);
    }
    public void setM_def_mod() {
        set(M_DEF_MOD, value);
    }
    public void setM_atk_spd_mod() {
        set(M_ATK_SPD_MOD, value);
    }
    public void setP_atk_spd_mod() {
        set(P_ATK_SPD_MOD, value);
    }
    public void setM_atk_crit_chance_mod() {
        set(M_ATK_CRIT_CHANCE_MOD, value);
    }
    public void setP_atk_crit_chance_mod() {
        set(P_ATK_CRIT_CHANCE_MOD, value);
    }
    public void setP_critical_damage_per_mod() {
        set(P_CRITICAL_DAMAGE_PER_MOD, value);
    }
    public void setP_critical_damage_diff_mod() {
        set(P_CRITICAL_DAMAGE_DIFF_MOD, value);
    }
    public void setHp_mod() {
        set(HP_MOD, value);
    }
    public void setMp_mod() {
        set(MP_MOD, value);
    }
    public void setP_critical_rate_mod() {
        set(P_CRITICAL_RATE_MOD, value);
    }

}
