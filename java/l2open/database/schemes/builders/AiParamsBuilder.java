package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AiParamsResource;

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
