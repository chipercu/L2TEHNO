package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClanPrivsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClanPrivsBuilder extends ResourceBuilder<ClanPrivsResource> {

    public ClanPrivsBuilder() {
        try {
            resourceClass = ClanPrivsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanPrivsBuilder withClanId(Integer value) {
        with(ClanPrivsResource.CLAN_ID, value);
        return this;
    }

    public ClanPrivsBuilder withRank(Integer value) {
        with(ClanPrivsResource.RANK, value);
        return this;
    }

    public ClanPrivsBuilder withPrivilleges(Integer value) {
        with(ClanPrivsResource.PRIVILLEGES, value);
        return this;
    }

}
