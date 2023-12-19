package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SevenSignsFestivalResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SevenSignsFestivalBuilder extends ResourceBuilder<SevenSignsFestivalResource> {

    public SevenSignsFestivalBuilder() {
        try {
            resourceClass = SevenSignsFestivalResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SevenSignsFestivalBuilder withFestivalId(Integer value) {
        with(SevenSignsFestivalResource.FESTIVAL_ID, value);
        return this;
    }

    public SevenSignsFestivalBuilder withCabal(String value) {
        with(SevenSignsFestivalResource.CABAL, value);
        return this;
    }

    public SevenSignsFestivalBuilder withCycle(Integer value) {
        with(SevenSignsFestivalResource.CYCLE, value);
        return this;
    }

    public SevenSignsFestivalBuilder withDate(Long value) {
        with(SevenSignsFestivalResource._DATE, value);
        return this;
    }

    public SevenSignsFestivalBuilder withScore(Integer value) {
        with(SevenSignsFestivalResource.SCORE, value);
        return this;
    }

    public SevenSignsFestivalBuilder withMembers(String value) {
        with(SevenSignsFestivalResource.MEMBERS, value);
        return this;
    }

    public SevenSignsFestivalBuilder withNames(String value) {
        with(SevenSignsFestivalResource.NAMES, value);
        return this;
    }

}
