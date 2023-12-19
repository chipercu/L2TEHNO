package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClanSubpledgesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClanSubpledgesBuilder extends ResourceBuilder<ClanSubpledgesResource> {

    public ClanSubpledgesBuilder() {
        try {
            resourceClass = ClanSubpledgesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanSubpledgesBuilder withClanId(Integer value) {
        with(ClanSubpledgesResource.CLAN_ID, value);
        return this;
    }

    public ClanSubpledgesBuilder withType(Integer value) {
        with(ClanSubpledgesResource.TYPE, value);
        return this;
    }

    public ClanSubpledgesBuilder withName(String value) {
        with(ClanSubpledgesResource.NAME, value);
        return this;
    }

    public ClanSubpledgesBuilder withLeaderId(Integer value) {
        with(ClanSubpledgesResource.LEADER_ID, value);
        return this;
    }

}
