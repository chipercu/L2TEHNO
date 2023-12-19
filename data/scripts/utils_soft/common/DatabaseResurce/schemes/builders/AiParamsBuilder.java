package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.AiParamsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class AiParamsBuilder extends ResourceBuilder<AiParamsResource> {

    public AiParamsBuilder() {
        try {
            resourceClass = AiParamsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AiParamsBuilder withNpcId(Integer value) {
        with(AiParamsResource.NPC_ID, value);
        return this;
    }

    public AiParamsBuilder withName(String value) {
        with(AiParamsResource.NAME, value);
        return this;
    }

    public AiParamsBuilder withParam(String value) {
        with(AiParamsResource.PARAM, value);
        return this;
    }

    public AiParamsBuilder withValue(String value) {
        with(AiParamsResource.VALUE, value);
        return this;
    }

}
