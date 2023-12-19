package utils_soft.common.DatabaseResurce.schemes.resources;

import utils_soft.common.DatabaseResurce.anotations.Column;
import utils_soft.common.DatabaseResurce.anotations.Table;
import utils_soft.common.DatabaseResurce.DataBaseTable;
import static utils_soft.common.DatabaseResurce.schemes.resources.CharTemplatesResource.*;
import static utils_soft.common.DatabaseResurce.anotations.IS_NULLABLE.*;
import static utils_soft.common.DatabaseResurce.anotations.DATA_TYPE.*;

@Table(
        name = "char_templates",
        primary_key = {CLASS_ID},
        fields = {
                @Column(is_null = NO , type = INT , type_size = 0 , name = CLASS_ID , defValue = "0"),
                @Column(is_null = NO , type = VARCHAR , type_size = 20 , name = CLASS_NAME , defValue = ""),
                @Column(is_null = NO , type = INT , type_size = 0 , name = RACE_ID , defValue = "0"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = PARENT , defValue = "255"),
                @Column(is_null = YES , type = TINYINT , type_size = 0 , name = LEVEL , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = S_TR , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = C_ON , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = D_EX , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = __IN_T , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = W_IT , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = M_EN , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = P__AT_K , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = P__DE_F , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = M__AT_K , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = M__DE_F , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = P__SP_D , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = M__SP_D , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = A_CC , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = C_RI_TI_CA_L , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = E_VA_SI_ON , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = R_UN__SP_D , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = W_AL_K__SP_D , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = X , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Y , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = Z , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = CAN_CRAFT , defValue = "0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = M__UN_K1 , defValue = "0.00"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = M__UN_K2 , defValue = "0.000000"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = M__CO_L__R , defValue = "0.0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = M__CO_L__H , defValue = "0.0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = F__UN_K1 , defValue = "0.00"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = F__UN_K2 , defValue = "0.000000"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = F__CO_L__R , defValue = "0.0"),
                @Column(is_null = NO , type = DECIMAL , type_size = 0 , name = F__CO_L__H , defValue = "0.0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEMS1 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEMS2 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEMS3 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEMS4 , defValue = "0"),
                @Column(is_null = NO , type = INT , type_size = 0 , name = ITEMS5 , defValue = "0"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_ATK_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = M_ATK_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_DEF_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = M_DEF_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = M_ATK_SPD_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_ATK_SPD_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = M_ATK_CRIT_CHANCE_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_ATK_CRIT_CHANCE_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_CRITICAL_DAMAGE_PER_MOD , defValue = "1"),
                @Column(is_null = YES , type = INT , type_size = 0 , name = P_CRITICAL_DAMAGE_DIFF_MOD , defValue = "0"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = HP_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = MP_MOD , defValue = "1"),
                @Column(is_null = YES , type = FLOAT , type_size = 0 , name = P_CRITICAL_RATE_MOD , defValue = "1"),
        }
)
public class CharTemplatesResource extends DataBaseTable<CharTemplatesResource> {

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

    public CharTemplatesResource() {
        super(CharTemplatesResource.class);
}

    public Integer getClassId() {
        return get(CLASS_ID);
    }
    public String getClassName() {
        return get(CLASS_NAME);
    }
    public Integer getRaceId() {
        return get(RACE_ID);
    }
    public Integer getParent() {
        return get(PARENT);
    }
    public Integer getLevel() {
        return get(LEVEL);
    }
    public Integer getSTR() {
        return get(S_TR);
    }
    public Integer getCON() {
        return get(C_ON);
    }
    public Integer getDEX() {
        return get(D_EX);
    }
    public Integer getINT() {
        return get(__IN_T);
    }
    public Integer getWIT() {
        return get(W_IT);
    }
    public Integer getMEN() {
        return get(M_EN);
    }
    public Integer getPATK() {
        return get(P__AT_K);
    }
    public Integer getPDEF() {
        return get(P__DE_F);
    }
    public Integer getMATK() {
        return get(M__AT_K);
    }
    public Integer getMDEF() {
        return get(M__DE_F);
    }
    public Integer getPSPD() {
        return get(P__SP_D);
    }
    public Integer getMSPD() {
        return get(M__SP_D);
    }
    public Integer getACC() {
        return get(A_CC);
    }
    public Integer getCRITICAL() {
        return get(C_RI_TI_CA_L);
    }
    public Integer getEVASION() {
        return get(E_VA_SI_ON);
    }
    public Integer getRUNSPD() {
        return get(R_UN__SP_D);
    }
    public Integer getWALKSPD() {
        return get(W_AL_K__SP_D);
    }
    public Integer getX() {
        return get(X);
    }
    public Integer getY() {
        return get(Y);
    }
    public Integer getZ() {
        return get(Z);
    }
    public Integer getCanCraft() {
        return get(CAN_CRAFT);
    }
    public Double getMUNK1() {
        return get(M__UN_K1);
    }
    public Double getMUNK2() {
        return get(M__UN_K2);
    }
    public Double getMCOLR() {
        return get(M__CO_L__R);
    }
    public Double getMCOLH() {
        return get(M__CO_L__H);
    }
    public Double getFUNK1() {
        return get(F__UN_K1);
    }
    public Double getFUNK2() {
        return get(F__UN_K2);
    }
    public Double getFCOLR() {
        return get(F__CO_L__R);
    }
    public Double getFCOLH() {
        return get(F__CO_L__H);
    }
    public Integer getItems1() {
        return get(ITEMS1);
    }
    public Integer getItems2() {
        return get(ITEMS2);
    }
    public Integer getItems3() {
        return get(ITEMS3);
    }
    public Integer getItems4() {
        return get(ITEMS4);
    }
    public Integer getItems5() {
        return get(ITEMS5);
    }
    public Double getPAtkMod() {
        return get(P_ATK_MOD);
    }
    public Double getMAtkMod() {
        return get(M_ATK_MOD);
    }
    public Double getPDefMod() {
        return get(P_DEF_MOD);
    }
    public Double getMDefMod() {
        return get(M_DEF_MOD);
    }
    public Double getMAtkSpdMod() {
        return get(M_ATK_SPD_MOD);
    }
    public Double getPAtkSpdMod() {
        return get(P_ATK_SPD_MOD);
    }
    public Double getMAtkCritChanceMod() {
        return get(M_ATK_CRIT_CHANCE_MOD);
    }
    public Double getPAtkCritChanceMod() {
        return get(P_ATK_CRIT_CHANCE_MOD);
    }
    public Double getPCriticalDamagePerMod() {
        return get(P_CRITICAL_DAMAGE_PER_MOD);
    }
    public Integer getPCriticalDamageDiffMod() {
        return get(P_CRITICAL_DAMAGE_DIFF_MOD);
    }
    public Double getHpMod() {
        return get(HP_MOD);
    }
    public Double getMpMod() {
        return get(MP_MOD);
    }
    public Double getPCriticalRateMod() {
        return get(P_CRITICAL_RATE_MOD);
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
