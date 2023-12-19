package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.NpcResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class NpcBuilder extends ResourceBuilder<NpcResource> {

    public NpcBuilder() {
        try {
            resourceClass = NpcResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public NpcBuilder withOrdinal(Integer value) {
        with(NpcResource.ORDINAL, value);
        return this;
    }

    public NpcBuilder withId(Integer value) {
        with(NpcResource.ID, value);
        return this;
    }

    public NpcBuilder withName(String value) {
        with(NpcResource.NAME, value);
        return this;
    }

    public NpcBuilder withTitle(String value) {
        with(NpcResource.TITLE, value);
        return this;
    }

    public NpcBuilder withClass(String value) {
        with(NpcResource.CLASS, value);
        return this;
    }

    public NpcBuilder withRace(String value) {
        with(NpcResource.RACE, value);
        return this;
    }

    public NpcBuilder withCollisionRadius(Double value) {
        with(NpcResource.COLLISION_RADIUS, value);
        return this;
    }

    public NpcBuilder withCollisionHeight(Double value) {
        with(NpcResource.COLLISION_HEIGHT, value);
        return this;
    }

    public NpcBuilder withLevel(Integer value) {
        with(NpcResource.LEVEL, value);
        return this;
    }

    public NpcBuilder withSex(String value) {
        with(NpcResource.SEX, value);
        return this;
    }

    public NpcBuilder withType(String value) {
        with(NpcResource.TYPE, value);
        return this;
    }

    public NpcBuilder withAiType(String value) {
        with(NpcResource.AI_TYPE, value);
        return this;
    }

    public NpcBuilder withAttackrange(Integer value) {
        with(NpcResource.ATTACKRANGE, value);
        return this;
    }

    public NpcBuilder withHp(Integer value) {
        with(NpcResource.HP, value);
        return this;
    }

    public NpcBuilder withBaseHpRegen(Double value) {
        with(NpcResource.BASE_HP_REGEN, value);
        return this;
    }

    public NpcBuilder withMp(Integer value) {
        with(NpcResource.MP, value);
        return this;
    }

    public NpcBuilder withBaseMpRegen(Double value) {
        with(NpcResource.BASE_MP_REGEN, value);
        return this;
    }

    public NpcBuilder withStr(Integer value) {
        with(NpcResource.STR, value);
        return this;
    }

    public NpcBuilder withCon(Integer value) {
        with(NpcResource.CON, value);
        return this;
    }

    public NpcBuilder withDex(Integer value) {
        with(NpcResource.DEX, value);
        return this;
    }

    public NpcBuilder withInt(Integer value) {
        with(NpcResource._INT, value);
        return this;
    }

    public NpcBuilder withWit(Integer value) {
        with(NpcResource.WIT, value);
        return this;
    }

    public NpcBuilder withMen(Integer value) {
        with(NpcResource.MEN, value);
        return this;
    }

    public NpcBuilder withExp(Integer value) {
        with(NpcResource.EXP, value);
        return this;
    }

    public NpcBuilder withSp(Integer value) {
        with(NpcResource.SP, value);
        return this;
    }

    public NpcBuilder withPatk(Integer value) {
        with(NpcResource.PATK, value);
        return this;
    }

    public NpcBuilder withPdef(Integer value) {
        with(NpcResource.PDEF, value);
        return this;
    }

    public NpcBuilder withMatk(Integer value) {
        with(NpcResource.MATK, value);
        return this;
    }

    public NpcBuilder withMdef(Integer value) {
        with(NpcResource.MDEF, value);
        return this;
    }

    public NpcBuilder withAtkspd(Integer value) {
        with(NpcResource.ATKSPD, value);
        return this;
    }

    public NpcBuilder withAggro(Integer value) {
        with(NpcResource.AGGRO, value);
        return this;
    }

    public NpcBuilder withMatkspd(Integer value) {
        with(NpcResource.MATKSPD, value);
        return this;
    }

    public NpcBuilder withRhand(Integer value) {
        with(NpcResource.RHAND, value);
        return this;
    }

    public NpcBuilder withLhand(Integer value) {
        with(NpcResource.LHAND, value);
        return this;
    }

    public NpcBuilder withArmor(Integer value) {
        with(NpcResource.ARMOR, value);
        return this;
    }

    public NpcBuilder withWalkspd(Integer value) {
        with(NpcResource.WALKSPD, value);
        return this;
    }

    public NpcBuilder withRunspd(Integer value) {
        with(NpcResource.RUNSPD, value);
        return this;
    }

    public NpcBuilder withFactionId(String value) {
        with(NpcResource.FACTION_ID, value);
        return this;
    }

    public NpcBuilder withFactionRange(Integer value) {
        with(NpcResource.FACTION_RANGE, value);
        return this;
    }

    public NpcBuilder withDisplayId(Integer value) {
        with(NpcResource.DISPLAY_ID, value);
        return this;
    }

    public NpcBuilder withShieldDefenseRate(Integer value) {
        with(NpcResource.SHIELD_DEFENSE_RATE, value);
        return this;
    }

    public NpcBuilder withShieldDefense(Integer value) {
        with(NpcResource.SHIELD_DEFENSE, value);
        return this;
    }

    public NpcBuilder withCorpseTime(Integer value) {
        with(NpcResource.CORPSE_TIME, value);
        return this;
    }

    public NpcBuilder withBaseRandDam(Integer value) {
        with(NpcResource.BASE_RAND_DAM, value);
        return this;
    }

    public NpcBuilder withBaseCritical(Integer value) {
        with(NpcResource.BASE_CRITICAL, value);
        return this;
    }

    public NpcBuilder withPhysicalHitModify(Integer value) {
        with(NpcResource.PHYSICAL_HIT_MODIFY, value);
        return this;
    }

    public NpcBuilder withBaseReuseDelay(Integer value) {
        with(NpcResource.BASE_REUSE_DELAY, value);
        return this;
    }

    public NpcBuilder withPhysicalAvoidModify(Integer value) {
        with(NpcResource.PHYSICAL_AVOID_MODIFY, value);
        return this;
    }

    public NpcBuilder withHitTimeFactor(Double value) {
        with(NpcResource.HIT_TIME_FACTOR, value);
        return this;
    }

    public NpcBuilder withIsDropHerbs(Enum value) {
        with(NpcResource.IS_DROP_HERBS, value);
        return this;
    }

    public NpcBuilder withShots(Enum value) {
        with(NpcResource.SHOTS, value);
        return this;
    }

    public NpcBuilder withMapFlag(Integer value) {
        with(NpcResource.MAP_FLAG, value);
        return this;
    }

    public NpcBuilder withBossFlag(Integer value) {
        with(NpcResource.BOSS_FLAG, value);
        return this;
    }

    public NpcBuilder withAgroRange(Integer value) {
        with(NpcResource.AGRO_RANGE, value);
        return this;
    }

    public NpcBuilder withEventFlag(Integer value) {
        with(NpcResource.EVENT_FLAG, value);
        return this;
    }

    public NpcBuilder withCanBeAttacked(Integer value) {
        with(NpcResource.CAN_BE_ATTACKED, value);
        return this;
    }

    public NpcBuilder withUndying(Integer value) {
        with(NpcResource.UNDYING, value);
        return this;
    }

    public NpcBuilder withBaseAttackType(String value) {
        with(NpcResource.BASE_ATTACK_TYPE, value);
        return this;
    }

}
