package utils_soft.common.DatabaseResurce.schemes.generate;

import utils_soft.common.DatabaseResurce.anotations.DefValue;
import utils_soft.common.DatabaseResurce.anotations.Field;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.generate.CharTemplatesTable.*;

@Table(
        name = "char_templates",
        fields = {
                @Field(name = CLASS_ID),
                @Field(name = CLASS_NAME),
                @Field(name = RACE_ID),
                @Field(name = PARENT),
                @Field(name = LEVEL),
                @Field(name = S_TR),
                @Field(name = C_ON),
                @Field(name = D_EX),
                @Field(name = __IN_T),
                @Field(name = W_IT),
                @Field(name = M_EN),
                @Field(name = P__AT_K),
                @Field(name = P__DE_F),
                @Field(name = M__AT_K),
                @Field(name = M__DE_F),
                @Field(name = P__SP_D),
                @Field(name = M__SP_D),
                @Field(name = A_CC),
                @Field(name = C_RI_TI_CA_L),
                @Field(name = E_VA_SI_ON),
                @Field(name = R_UN__SP_D),
                @Field(name = W_AL_K__SP_D),
                @Field(name = X),
                @Field(name = Y),
                @Field(name = Z),
                @Field(name = CAN_CRAFT),
                @Field(name = M__UN_K1),
                @Field(name = M__UN_K2),
                @Field(name = M__CO_L__R),
                @Field(name = M__CO_L__H),
                @Field(name = F__UN_K1),
                @Field(name = F__UN_K2),
                @Field(name = F__CO_L__R),
                @Field(name = F__CO_L__H),
                @Field(name = ITEMS1),
                @Field(name = ITEMS2),
                @Field(name = ITEMS3),
                @Field(name = ITEMS4),
                @Field(name = ITEMS5),
                @Field(name = P_ATK_MOD),
                @Field(name = M_ATK_MOD),
                @Field(name = P_DEF_MOD),
                @Field(name = M_DEF_MOD),
                @Field(name = M_ATK_SPD_MOD),
                @Field(name = P_ATK_SPD_MOD),
                @Field(name = M_ATK_CRIT_CHANCE_MOD),
                @Field(name = P_ATK_CRIT_CHANCE_MOD),
                @Field(name = P_CRITICAL_DAMAGE_PER_MOD),
                @Field(name = P_CRITICAL_DAMAGE_DIFF_MOD),
                @Field(name = HP_MOD),
                @Field(name = MP_MOD),
                @Field(name = P_CRITICAL_RATE_MOD),
        }
)
public class CharTemplatesTable extends DataBaseTable<CharTemplatesTable> {

    public static final String CLASS_ID = "ClassId";
    public static final String CLASS_NAME = "ClassName";
    public static final String RACE_ID = "RaceId";
    public static final String PARENT = "parent";
    public static final String LEVEL = "level";
    public static final String S_TR = "STR";
    public static final String C_ON = "CON";
    public static final String D_EX = "DEX";
    public static final String __IN_T = "_INT";
    public static final String W_IT = "WIT";
    public static final String M_EN = "MEN";
    public static final String P__AT_K = "P_ATK";
    public static final String P__DE_F = "P_DEF";
    public static final String M__AT_K = "M_ATK";
    public static final String M__DE_F = "M_DEF";
    public static final String P__SP_D = "P_SPD";
    public static final String M__SP_D = "M_SPD";
    public static final String A_CC = "ACC";
    public static final String C_RI_TI_CA_L = "CRITICAL";
    public static final String E_VA_SI_ON = "EVASION";
    public static final String R_UN__SP_D = "RUN_SPD";
    public static final String W_AL_K__SP_D = "WALK_SPD";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String CAN_CRAFT = "canCraft";
    public static final String M__UN_K1 = "M_UNK1";
    public static final String M__UN_K2 = "M_UNK2";
    public static final String M__CO_L__R = "M_COL_R";
    public static final String M__CO_L__H = "M_COL_H";
    public static final String F__UN_K1 = "F_UNK1";
    public static final String F__UN_K2 = "F_UNK2";
    public static final String F__CO_L__R = "F_COL_R";
    public static final String F__CO_L__H = "F_COL_H";
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

    public CharTemplatesTable() {
super(CharTemplatesTable.class);
}

    public Integer getClassId() {
        return get(CLASS_ID, Integer.class);
    }
    public String getClassName() {
        return get(CLASS_NAME, String.class);
    }
    public Integer getRaceId() {
        return get(RACE_ID, Integer.class);
    }
    public Integer getParent() {
        return get(PARENT, Integer.class);
    }
    public Integer getLevel() {
        return get(LEVEL, Integer.class);
    }
    public Integer getSTR() {
        return get(S_TR, Integer.class);
    }
    public Integer getCON() {
        return get(C_ON, Integer.class);
    }
    public Integer getDEX() {
        return get(D_EX, Integer.class);
    }
    public Integer getINT() {
        return get(__IN_T, Integer.class);
    }
    public Integer getWIT() {
        return get(W_IT, Integer.class);
    }
    public Integer getMEN() {
        return get(M_EN, Integer.class);
    }
    public Integer getPATK() {
        return get(P__AT_K, Integer.class);
    }
    public Integer getPDEF() {
        return get(P__DE_F, Integer.class);
    }
    public Integer getMATK() {
        return get(M__AT_K, Integer.class);
    }
    public Integer getMDEF() {
        return get(M__DE_F, Integer.class);
    }
    public Integer getPSPD() {
        return get(P__SP_D, Integer.class);
    }
    public Integer getMSPD() {
        return get(M__SP_D, Integer.class);
    }
    public Integer getACC() {
        return get(A_CC, Integer.class);
    }
    public Integer getCRITICAL() {
        return get(C_RI_TI_CA_L, Integer.class);
    }
    public Integer getEVASION() {
        return get(E_VA_SI_ON, Integer.class);
    }
    public Integer getRUNSPD() {
        return get(R_UN__SP_D, Integer.class);
    }
    public Integer getWALKSPD() {
        return get(W_AL_K__SP_D, Integer.class);
    }
    public Integer getX() {
        return get(X, Integer.class);
    }
    public Integer getY() {
        return get(Y, Integer.class);
    }
    public Integer getZ() {
        return get(Z, Integer.class);
    }
    public Integer getCanCraft() {
        return get(CAN_CRAFT, Integer.class);
    }
    public Double getMUNK1() {
        return get(M__UN_K1, Double.class);
    }
    public Double getMUNK2() {
        return get(M__UN_K2, Double.class);
    }
    public Double getMCOLR() {
        return get(M__CO_L__R, Double.class);
    }
    public Double getMCOLH() {
        return get(M__CO_L__H, Double.class);
    }
    public Double getFUNK1() {
        return get(F__UN_K1, Double.class);
    }
    public Double getFUNK2() {
        return get(F__UN_K2, Double.class);
    }
    public Double getFCOLR() {
        return get(F__CO_L__R, Double.class);
    }
    public Double getFCOLH() {
        return get(F__CO_L__H, Double.class);
    }
    public Integer getItems1() {
        return get(ITEMS1, Integer.class);
    }
    public Integer getItems2() {
        return get(ITEMS2, Integer.class);
    }
    public Integer getItems3() {
        return get(ITEMS3, Integer.class);
    }
    public Integer getItems4() {
        return get(ITEMS4, Integer.class);
    }
    public Integer getItems5() {
        return get(ITEMS5, Integer.class);
    }
    public Double getPAtkMod() {
        return get(P_ATK_MOD, Double.class);
    }
    public Double getMAtkMod() {
        return get(M_ATK_MOD, Double.class);
    }
    public Double getPDefMod() {
        return get(P_DEF_MOD, Double.class);
    }
    public Double getMDefMod() {
        return get(M_DEF_MOD, Double.class);
    }
    public Double getMAtkSpdMod() {
        return get(M_ATK_SPD_MOD, Double.class);
    }
    public Double getPAtkSpdMod() {
        return get(P_ATK_SPD_MOD, Double.class);
    }
    public Double getMAtkCritChanceMod() {
        return get(M_ATK_CRIT_CHANCE_MOD, Double.class);
    }
    public Double getPAtkCritChanceMod() {
        return get(P_ATK_CRIT_CHANCE_MOD, Double.class);
    }
    public Double getPCriticalDamagePerMod() {
        return get(P_CRITICAL_DAMAGE_PER_MOD, Double.class);
    }
    public Integer getPCriticalDamageDiffMod() {
        return get(P_CRITICAL_DAMAGE_DIFF_MOD, Integer.class);
    }
    public Double getHpMod() {
        return get(HP_MOD, Double.class);
    }
    public Double getMpMod() {
        return get(MP_MOD, Double.class);
    }
    public Double getPCriticalRateMod() {
        return get(P_CRITICAL_RATE_MOD, Double.class);
    }

    public void setClassId(Integer value) {
        set(CLASS_ID, value);
    }
    public void setClassName(String value) {
        set(CLASS_NAME, value);
    }
    public void setRaceId(Integer value) {
        set(RACE_ID, value);
    }
    public void setParent(Integer value) {
        set(PARENT, value);
    }
    public void setLevel(Integer value) {
        set(LEVEL, value);
    }
    public void setSTR(Integer value) {
        set(S_TR, value);
    }
    public void setCON(Integer value) {
        set(C_ON, value);
    }
    public void setDEX(Integer value) {
        set(D_EX, value);
    }
    public void setINT(Integer value) {
        set(__IN_T, value);
    }
    public void setWIT(Integer value) {
        set(W_IT, value);
    }
    public void setMEN(Integer value) {
        set(M_EN, value);
    }
    public void setPATK(Integer value) {
        set(P__AT_K, value);
    }
    public void setPDEF(Integer value) {
        set(P__DE_F, value);
    }
    public void setMATK(Integer value) {
        set(M__AT_K, value);
    }
    public void setMDEF(Integer value) {
        set(M__DE_F, value);
    }
    public void setPSPD(Integer value) {
        set(P__SP_D, value);
    }
    public void setMSPD(Integer value) {
        set(M__SP_D, value);
    }
    public void setACC(Integer value) {
        set(A_CC, value);
    }
    public void setCRITICAL(Integer value) {
        set(C_RI_TI_CA_L, value);
    }
    public void setEVASION(Integer value) {
        set(E_VA_SI_ON, value);
    }
    public void setRUNSPD(Integer value) {
        set(R_UN__SP_D, value);
    }
    public void setWALKSPD(Integer value) {
        set(W_AL_K__SP_D, value);
    }
    public void setX(Integer value) {
        set(X, value);
    }
    public void setY(Integer value) {
        set(Y, value);
    }
    public void setZ(Integer value) {
        set(Z, value);
    }
    public void setCanCraft(Integer value) {
        set(CAN_CRAFT, value);
    }
    public void setMUNK1(Double value) {
        set(M__UN_K1, value);
    }
    public void setMUNK2(Double value) {
        set(M__UN_K2, value);
    }
    public void setMCOLR(Double value) {
        set(M__CO_L__R, value);
    }
    public void setMCOLH(Double value) {
        set(M__CO_L__H, value);
    }
    public void setFUNK1(Double value) {
        set(F__UN_K1, value);
    }
    public void setFUNK2(Double value) {
        set(F__UN_K2, value);
    }
    public void setFCOLR(Double value) {
        set(F__CO_L__R, value);
    }
    public void setFCOLH(Double value) {
        set(F__CO_L__H, value);
    }
    public void setItems1(Integer value) {
        set(ITEMS1, value);
    }
    public void setItems2(Integer value) {
        set(ITEMS2, value);
    }
    public void setItems3(Integer value) {
        set(ITEMS3, value);
    }
    public void setItems4(Integer value) {
        set(ITEMS4, value);
    }
    public void setItems5(Integer value) {
        set(ITEMS5, value);
    }
    public void setPAtkMod(Double value) {
        set(P_ATK_MOD, value);
    }
    public void setMAtkMod(Double value) {
        set(M_ATK_MOD, value);
    }
    public void setPDefMod(Double value) {
        set(P_DEF_MOD, value);
    }
    public void setMDefMod(Double value) {
        set(M_DEF_MOD, value);
    }
    public void setMAtkSpdMod(Double value) {
        set(M_ATK_SPD_MOD, value);
    }
    public void setPAtkSpdMod(Double value) {
        set(P_ATK_SPD_MOD, value);
    }
    public void setMAtkCritChanceMod(Double value) {
        set(M_ATK_CRIT_CHANCE_MOD, value);
    }
    public void setPAtkCritChanceMod(Double value) {
        set(P_ATK_CRIT_CHANCE_MOD, value);
    }
    public void setPCriticalDamagePerMod(Double value) {
        set(P_CRITICAL_DAMAGE_PER_MOD, value);
    }
    public void setPCriticalDamageDiffMod(Integer value) {
        set(P_CRITICAL_DAMAGE_DIFF_MOD, value);
    }
    public void setHpMod(Double value) {
        set(HP_MOD, value);
    }
    public void setMpMod(Double value) {
        set(MP_MOD, value);
    }
    public void setPCriticalRateMod(Double value) {
        set(P_CRITICAL_RATE_MOD, value);
    }

}
