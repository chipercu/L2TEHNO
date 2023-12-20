package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanPrivsResource;

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
