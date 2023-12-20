package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.PetDataResource;

public class PetDataBuilder extends ResourceBuilder<PetDataResource> {

    public PetDataBuilder() {
        try {
            resourceClass = PetDataResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PetDataBuilder withType(String value) {
        with(PetDataResource.TYPE, value);
        return this;
    }

    public PetDataBuilder withId(Integer value) {
        with(PetDataResource.ID, value);
        return this;
    }

    public PetDataBuilder withLevel(Integer value) {
        with(PetDataResource.LEVEL, value);
        return this;
    }

    public PetDataBuilder withExp(Double value) {
        with(PetDataResource.EXP, value);
        return this;
    }

    public PetDataBuilder withHp(Integer value) {
        with(PetDataResource.HP, value);
        return this;
    }

    public PetDataBuilder withMp(Integer value) {
        with(PetDataResource.MP, value);
        return this;
    }

    public PetDataBuilder withPatk(Integer value) {
        with(PetDataResource.PATK, value);
        return this;
    }

    public PetDataBuilder withPdef(Integer value) {
        with(PetDataResource.PDEF, value);
        return this;
    }

    public PetDataBuilder withMatk(Integer value) {
        with(PetDataResource.MATK, value);
        return this;
    }

    public PetDataBuilder withMdef(Integer value) {
        with(PetDataResource.MDEF, value);
        return this;
    }

    public PetDataBuilder withAcc(Integer value) {
        with(PetDataResource.ACC, value);
        return this;
    }

    public PetDataBuilder withEvasion(Integer value) {
        with(PetDataResource.EVASION, value);
        return this;
    }

    public PetDataBuilder withCrit(Integer value) {
        with(PetDataResource.CRIT, value);
        return this;
    }

    public PetDataBuilder withSpeed(Integer value) {
        with(PetDataResource.SPEED, value);
        return this;
    }

    public PetDataBuilder withAtkSpeed(Integer value) {
        with(PetDataResource.ATK_SPEED, value);
        return this;
    }

    public PetDataBuilder withCastSpeed(Integer value) {
        with(PetDataResource.CAST_SPEED, value);
        return this;
    }

    public PetDataBuilder withMaxMeal(Integer value) {
        with(PetDataResource.MAX_MEAL, value);
        return this;
    }

    public PetDataBuilder withBattleMeal(Integer value) {
        with(PetDataResource.BATTLE_MEAL, value);
        return this;
    }

    public PetDataBuilder withNormalMeal(Integer value) {
        with(PetDataResource.NORMAL_MEAL, value);
        return this;
    }

    public PetDataBuilder withLoadMax(Integer value) {
        with(PetDataResource.LOAD_MAX, value);
        return this;
    }

    public PetDataBuilder withHpregen(Double value) {
        with(PetDataResource.HPREGEN, value);
        return this;
    }

    public PetDataBuilder withMpregen(Double value) {
        with(PetDataResource.MPREGEN, value);
        return this;
    }

    public PetDataBuilder withGetExpType(Double value) {
        with(PetDataResource.GET_EXP_TYPE, value);
        return this;
    }

    public PetDataBuilder withSpiritshotCount(Integer value) {
        with(PetDataResource.SPIRITSHOT_COUNT, value);
        return this;
    }

    public PetDataBuilder withSoulshotCount(Integer value) {
        with(PetDataResource.SOULSHOT_COUNT, value);
        return this;
    }

    public PetDataBuilder withItem(Integer value) {
        with(PetDataResource.ITEM, value);
        return this;
    }

    public PetDataBuilder withFood(Integer value) {
        with(PetDataResource.FOOD, value);
        return this;
    }

    public PetDataBuilder withAddMeal(Integer value) {
        with(PetDataResource.ADD_MEAL, value);
        return this;
    }

    public PetDataBuilder withHungryLimit(Integer value) {
        with(PetDataResource.HUNGRY_LIMIT, value);
        return this;
    }

    public PetDataBuilder withMountabe(Enum value) {
        with(PetDataResource.MOUNTABE, value);
        return this;
    }

}
