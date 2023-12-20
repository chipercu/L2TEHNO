package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.TournamentVariablesResource;

public class TournamentVariablesBuilder extends ResourceBuilder<TournamentVariablesResource> {

    public TournamentVariablesBuilder() {
        try {
            resourceClass = TournamentVariablesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TournamentVariablesBuilder withName(String value) {
        with(TournamentVariablesResource.NAME, value);
        return this;
    }

    public TournamentVariablesBuilder withValue(String value) {
        with(TournamentVariablesResource.VALUE, value);
        return this;
    }

}
