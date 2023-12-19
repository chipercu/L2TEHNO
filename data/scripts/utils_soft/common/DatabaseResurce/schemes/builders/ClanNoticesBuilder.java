package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClanNoticesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClanNoticesBuilder extends ResourceBuilder<ClanNoticesResource> {

    public ClanNoticesBuilder() {
        try {
            resourceClass = ClanNoticesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanNoticesBuilder withClanID(Integer value) {
        with(ClanNoticesResource.CLAN_ID, value);
        return this;
    }

    public ClanNoticesBuilder withNotice(String value) {
        with(ClanNoticesResource.NOTICE, value);
        return this;
    }

    public ClanNoticesBuilder withEnabled(String value) {
        with(ClanNoticesResource.ENABLED, value);
        return this;
    }

}
