package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.KillcountResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class KillcountBuilder extends ResourceBuilder<KillcountResource> {

    public KillcountBuilder() {
        try {
            resourceClass = KillcountResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public KillcountBuilder withCharId(Integer value) {
        with(KillcountResource.CHAR_ID, value);
        return this;
    }

    public KillcountBuilder withNpcId(Integer value) {
        with(KillcountResource.NPC_ID, value);
        return this;
    }

    public KillcountBuilder withCount(Integer value) {
        with(KillcountResource.COUNT, value);
        return this;
    }

}
