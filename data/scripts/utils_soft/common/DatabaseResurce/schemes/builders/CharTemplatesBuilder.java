package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CharTemplatesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CharTemplatesBuilder extends ResourceBuilder<CharTemplatesResource> {

    public CharTemplatesBuilder() {
        try {
            resourceClass = CharTemplatesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CharTemplatesBuilder withClassId(Integer value) {
        with(CharTemplatesResource.CLASS_ID, value);
        return this;
    }

    public CharTemplatesBuilder withClassName(String value) {
        with(CharTemplatesResource.CLASS_NAME, value);
        return this;
    }

    public CharTemplatesBuilder withRaceId(Integer value) {
        with(CharTemplatesResource.RACE_ID, value);
        return this;
    }

    public CharTemplatesBuilder withParent(Integer value) {
        with(CharTemplatesResource.PARENT, value);
        return this;
    }

    public CharTemplatesBuilder withLevel(Integer value) {
        with(CharTemplatesResource.LEVEL, value);
        return this;
    }

    public CharTemplatesBuilder withSTR(Integer value) {
        with(CharTemplatesResource.S_TR, value);
        return this;
    }

    public CharTemplatesBuilder withCON(Integer value) {
        with(CharTemplatesResource.C_ON, value);
        return this;
    }

    public CharTemplatesBuilder withDEX(Integer value) {
        with(CharTemplatesResource.D_EX, value);
        return this;
    }

    public CharTemplatesBuilder withINT(Integer value) {
        with(CharTemplatesResource.__IN_T, value);
        return this;
    }

    public CharTemplatesBuilder withWIT(Integer value) {
        with(CharTemplatesResource.W_IT, value);
        return this;
    }

    public CharTemplatesBuilder withMEN(Integer value) {
        with(CharTemplatesResource.M_EN, value);
        return this;
    }

    public CharTemplatesBuilder withPATK(Integer value) {
        with(CharTemplatesResource.P__AT_K, value);
        return this;
    }

    public CharTemplatesBuilder withPDEF(Integer value) {
        with(CharTemplatesResource.P__DE_F, value);
        return this;
    }

    public CharTemplatesBuilder withMATK(Integer value) {
        with(CharTemplatesResource.M__AT_K, value);
        return this;
    }

    public CharTemplatesBuilder withMDEF(Integer value) {
        with(CharTemplatesResource.M__DE_F, value);
        return this;
    }

    public CharTemplatesBuilder withPSPD(Integer value) {
        with(CharTemplatesResource.P__SP_D, value);
        return this;
    }

    public CharTemplatesBuilder withMSPD(Integer value) {
        with(CharTemplatesResource.M__SP_D, value);
        return this;
    }

    public CharTemplatesBuilder withACC(Integer value) {
        with(CharTemplatesResource.A_CC, value);
        return this;
    }

    public CharTemplatesBuilder withCRITICAL(Integer value) {
        with(CharTemplatesResource.C_RI_TI_CA_L, value);
        return this;
    }

    public CharTemplatesBuilder withEVASION(Integer value) {
        with(CharTemplatesResource.E_VA_SI_ON, value);
        return this;
    }

    public CharTemplatesBuilder withRUNSPD(Integer value) {
        with(CharTemplatesResource.R_UN__SP_D, value);
        return this;
    }

    public CharTemplatesBuilder withWALKSPD(Integer value) {
        with(CharTemplatesResource.W_AL_K__SP_D, value);
        return this;
    }

    public CharTemplatesBuilder withX(Integer value) {
        with(CharTemplatesResource.X, value);
        return this;
    }

    public CharTemplatesBuilder withY(Integer value) {
        with(CharTemplatesResource.Y, value);
        return this;
    }

    public CharTemplatesBuilder withZ(Integer value) {
        with(CharTemplatesResource.Z, value);
        return this;
    }

    public CharTemplatesBuilder withCanCraft(Integer value) {
        with(CharTemplatesResource.CAN_CRAFT, value);
        return this;
    }

    public CharTemplatesBuilder withMUNK1(Double value) {
        with(CharTemplatesResource.M__UN_K1, value);
        return this;
    }

    public CharTemplatesBuilder withMUNK2(Double value) {
        with(CharTemplatesResource.M__UN_K2, value);
        return this;
    }

    public CharTemplatesBuilder withMCOLR(Double value) {
        with(CharTemplatesResource.M__CO_L__R, value);
        return this;
    }

    public CharTemplatesBuilder withMCOLH(Double value) {
        with(CharTemplatesResource.M__CO_L__H, value);
        return this;
    }

    public CharTemplatesBuilder withFUNK1(Double value) {
        with(CharTemplatesResource.F__UN_K1, value);
        return this;
    }

    public CharTemplatesBuilder withFUNK2(Double value) {
        with(CharTemplatesResource.F__UN_K2, value);
        return this;
    }

    public CharTemplatesBuilder withFCOLR(Double value) {
        with(CharTemplatesResource.F__CO_L__R, value);
        return this;
    }

    public CharTemplatesBuilder withFCOLH(Double value) {
        with(CharTemplatesResource.F__CO_L__H, value);
        return this;
    }

    public CharTemplatesBuilder withItems1(Integer value) {
        with(CharTemplatesResource.ITEMS1, value);
        return this;
    }

    public CharTemplatesBuilder withItems2(Integer value) {
        with(CharTemplatesResource.ITEMS2, value);
        return this;
    }

    public CharTemplatesBuilder withItems3(Integer value) {
        with(CharTemplatesResource.ITEMS3, value);
        return this;
    }

    public CharTemplatesBuilder withItems4(Integer value) {
        with(CharTemplatesResource.ITEMS4, value);
        return this;
    }

    public CharTemplatesBuilder withItems5(Integer value) {
        with(CharTemplatesResource.ITEMS5, value);
        return this;
    }

    public CharTemplatesBuilder withPAtkMod(Double value) {
        with(CharTemplatesResource.P_ATK_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withMAtkMod(Double value) {
        with(CharTemplatesResource.M_ATK_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPDefMod(Double value) {
        with(CharTemplatesResource.P_DEF_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withMDefMod(Double value) {
        with(CharTemplatesResource.M_DEF_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withMAtkSpdMod(Double value) {
        with(CharTemplatesResource.M_ATK_SPD_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPAtkSpdMod(Double value) {
        with(CharTemplatesResource.P_ATK_SPD_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withMAtkCritChanceMod(Double value) {
        with(CharTemplatesResource.M_ATK_CRIT_CHANCE_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPAtkCritChanceMod(Double value) {
        with(CharTemplatesResource.P_ATK_CRIT_CHANCE_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPCriticalDamagePerMod(Double value) {
        with(CharTemplatesResource.P_CRITICAL_DAMAGE_PER_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPCriticalDamageDiffMod(Integer value) {
        with(CharTemplatesResource.P_CRITICAL_DAMAGE_DIFF_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withHpMod(Double value) {
        with(CharTemplatesResource.HP_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withMpMod(Double value) {
        with(CharTemplatesResource.MP_MOD, value);
        return this;
    }

    public CharTemplatesBuilder withPCriticalRateMod(Double value) {
        with(CharTemplatesResource.P_CRITICAL_RATE_MOD, value);
        return this;
    }

}
