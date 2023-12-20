package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClanSubpledgesResource;

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
