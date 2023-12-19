package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SiegeClansResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SiegeClansBuilder extends ResourceBuilder<SiegeClansResource> {

    public SiegeClansBuilder() {
        try {
            resourceClass = SiegeClansResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SiegeClansBuilder withUnitId(Integer value) {
        with(SiegeClansResource.UNIT_ID, value);
        return this;
    }

    public SiegeClansBuilder withClanId(Integer value) {
        with(SiegeClansResource.CLAN_ID, value);
        return this;
    }

    public SiegeClansBuilder withType(Integer value) {
        with(SiegeClansResource.TYPE, value);
        return this;
    }

}
