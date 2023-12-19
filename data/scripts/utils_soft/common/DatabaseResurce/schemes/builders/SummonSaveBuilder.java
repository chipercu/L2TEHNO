package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SummonSaveResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SummonSaveBuilder extends ResourceBuilder<SummonSaveResource> {

    public SummonSaveBuilder() {
        try {
            resourceClass = SummonSaveResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SummonSaveBuilder withCharObjId(Integer value) {
        with(SummonSaveResource.CHAR_OBJ_ID, value);
        return this;
    }

    public SummonSaveBuilder withClassId(Integer value) {
        with(SummonSaveResource.CLASS_ID, value);
        return this;
    }

    public SummonSaveBuilder withItemObjId(Integer value) {
        with(SummonSaveResource.ITEM_OBJ_ID, value);
        return this;
    }

    public SummonSaveBuilder withNpcId(Integer value) {
        with(SummonSaveResource.NPC_ID, value);
        return this;
    }

    public SummonSaveBuilder withLifeTime(Integer value) {
        with(SummonSaveResource.LIFE_TIME, value);
        return this;
    }

    public SummonSaveBuilder withItemConsumeIdInTime(Integer value) {
        with(SummonSaveResource.ITEM_CONSUME_ID_IN_TIME, value);
        return this;
    }

    public SummonSaveBuilder withItemConsumeCountInTime(Integer value) {
        with(SummonSaveResource.ITEM_CONSUME_COUNT_IN_TIME, value);
        return this;
    }

    public SummonSaveBuilder withItemConsumeDelay(Integer value) {
        with(SummonSaveResource.ITEM_CONSUME_DELAY, value);
        return this;
    }

    public SummonSaveBuilder withExpPenalty(Double value) {
        with(SummonSaveResource.EXP_PENALTY, value);
        return this;
    }

}
