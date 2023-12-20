package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanWarsResource;

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
