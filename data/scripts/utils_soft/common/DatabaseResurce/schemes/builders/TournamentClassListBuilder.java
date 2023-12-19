package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.TournamentClassListResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class TournamentClassListBuilder extends ResourceBuilder<TournamentClassListResource> {

    public TournamentClassListBuilder() {
        try {
            resourceClass = TournamentClassListResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public TournamentClassListBuilder withClassName(String value) {
        with(TournamentClassListResource.CLASS_NAME, value);
        return this;
    }

    public TournamentClassListBuilder withClassId(Integer value) {
        with(TournamentClassListResource.CLASS_ID, value);
        return this;
    }

    public TournamentClassListBuilder withType(Integer value) {
        with(TournamentClassListResource.TYPE, value);
        return this;
    }

}
