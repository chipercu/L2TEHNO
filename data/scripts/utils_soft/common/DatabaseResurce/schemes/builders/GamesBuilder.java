package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.GamesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class GamesBuilder extends ResourceBuilder<GamesResource> {

    public GamesBuilder() {
        try {
            resourceClass = GamesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GamesBuilder withId(Integer value) {
        with(GamesResource.ID, value);
        return this;
    }

    public GamesBuilder withIdnr(Integer value) {
        with(GamesResource.IDNR, value);
        return this;
    }

    public GamesBuilder withNumber1(Integer value) {
        with(GamesResource.NUMBER1, value);
        return this;
    }

    public GamesBuilder withNumber2(Integer value) {
        with(GamesResource.NUMBER2, value);
        return this;
    }

    public GamesBuilder withPrize(Long value) {
        with(GamesResource.PRIZE, value);
        return this;
    }

    public GamesBuilder withNewprize(Long value) {
        with(GamesResource.NEWPRIZE, value);
        return this;
    }

    public GamesBuilder withPrize1(Long value) {
        with(GamesResource.PRIZE1, value);
        return this;
    }

    public GamesBuilder withPrize2(Long value) {
        with(GamesResource.PRIZE2, value);
        return this;
    }

    public GamesBuilder withPrize3(Long value) {
        with(GamesResource.PRIZE3, value);
        return this;
    }

    public GamesBuilder withEnddate(Double value) {
        with(GamesResource.ENDDATE, value);
        return this;
    }

    public GamesBuilder withFinished(Integer value) {
        with(GamesResource.FINISHED, value);
        return this;
    }

}
