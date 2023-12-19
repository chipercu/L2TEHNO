package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.FishResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class FishBuilder extends ResourceBuilder<FishResource> {

    public FishBuilder() {
        try {
            resourceClass = FishResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FishBuilder withId(Integer value) {
        with(FishResource.ID, value);
        return this;
    }

    public FishBuilder withLevel(Integer value) {
        with(FishResource.LEVEL, value);
        return this;
    }

    public FishBuilder withName(String value) {
        with(FishResource.NAME, value);
        return this;
    }

    public FishBuilder withHp(Integer value) {
        with(FishResource.HP, value);
        return this;
    }

    public FishBuilder withHpregen(Integer value) {
        with(FishResource.HPREGEN, value);
        return this;
    }

    public FishBuilder withFishType(Integer value) {
        with(FishResource.FISH_TYPE, value);
        return this;
    }

    public FishBuilder withFishGroup(Integer value) {
        with(FishResource.FISH_GROUP, value);
        return this;
    }

    public FishBuilder withFishGuts(Integer value) {
        with(FishResource.FISH_GUTS, value);
        return this;
    }

    public FishBuilder withGutsCheckTime(Integer value) {
        with(FishResource.GUTS_CHECK_TIME, value);
        return this;
    }

    public FishBuilder withWaitTime(Integer value) {
        with(FishResource.WAIT_TIME, value);
        return this;
    }

    public FishBuilder withCombatTime(Integer value) {
        with(FishResource.COMBAT_TIME, value);
        return this;
    }

}
