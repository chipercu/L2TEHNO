package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.TournamentVariablesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
