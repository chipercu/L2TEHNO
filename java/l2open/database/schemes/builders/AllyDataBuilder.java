package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.AllyDataResource;

import java.sql.Array;

public class AllyDataBuilder extends ResourceBuilder<AllyDataResource> {

    public AllyDataBuilder() {
        try {
            resourceClass = AllyDataResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AllyDataBuilder withAllyId(Integer value) {
        with(AllyDataResource.ALLY_ID, value);
        return this;
    }

    public AllyDataBuilder withAllyName(String value) {
        with(AllyDataResource.ALLY_NAME, value);
        return this;
    }

    public AllyDataBuilder withLeaderId(Integer value) {
        with(AllyDataResource.LEADER_ID, value);
        return this;
    }

    public AllyDataBuilder withExpelledMember(Integer value) {
        with(AllyDataResource.EXPELLED_MEMBER, value);
        return this;
    }

    public AllyDataBuilder withCrest(Array value) {
        with(AllyDataResource.CREST, value);
        return this;
    }

}
