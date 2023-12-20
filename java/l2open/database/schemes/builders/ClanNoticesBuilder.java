package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanNoticesResource;

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
