package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.FishrewardResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class FishrewardBuilder extends ResourceBuilder<FishrewardResource> {

    public FishrewardBuilder() {
        try {
            resourceClass = FishrewardResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public FishrewardBuilder withFishid(Integer value) {
        with(FishrewardResource.FISHID, value);
        return this;
    }

    public FishrewardBuilder withRewardid(Integer value) {
        with(FishrewardResource.REWARDID, value);
        return this;
    }

    public FishrewardBuilder withMin(Integer value) {
        with(FishrewardResource.MIN, value);
        return this;
    }

    public FishrewardBuilder withMax(Integer value) {
        with(FishrewardResource.MAX, value);
        return this;
    }

    public FishrewardBuilder withChance(Integer value) {
        with(FishrewardResource.CHANCE, value);
        return this;
    }

}
