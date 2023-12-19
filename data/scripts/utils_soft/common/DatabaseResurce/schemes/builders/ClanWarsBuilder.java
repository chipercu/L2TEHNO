package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClanWarsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClanWarsBuilder extends ResourceBuilder<ClanWarsResource> {

    public ClanWarsBuilder() {
        try {
            resourceClass = ClanWarsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanWarsBuilder withClan1(Integer value) {
        with(ClanWarsResource.CLAN1, value);
        return this;
    }

    public ClanWarsBuilder withClan2(Integer value) {
        with(ClanWarsResource.CLAN2, value);
        return this;
    }

}
