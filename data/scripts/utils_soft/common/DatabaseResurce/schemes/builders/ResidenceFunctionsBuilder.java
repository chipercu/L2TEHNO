package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ResidenceFunctionsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ResidenceFunctionsBuilder extends ResourceBuilder<ResidenceFunctionsResource> {

    public ResidenceFunctionsBuilder() {
        try {
            resourceClass = ResidenceFunctionsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ResidenceFunctionsBuilder withId(Integer value) {
        with(ResidenceFunctionsResource.ID, value);
        return this;
    }

    public ResidenceFunctionsBuilder withType(Integer value) {
        with(ResidenceFunctionsResource.TYPE, value);
        return this;
    }

    public ResidenceFunctionsBuilder withLvl(Integer value) {
        with(ResidenceFunctionsResource.LVL, value);
        return this;
    }

    public ResidenceFunctionsBuilder withLease(Integer value) {
        with(ResidenceFunctionsResource.LEASE, value);
        return this;
    }

    public ResidenceFunctionsBuilder withRate(Long value) {
        with(ResidenceFunctionsResource.RATE, value);
        return this;
    }

    public ResidenceFunctionsBuilder withEndTime(Integer value) {
        with(ResidenceFunctionsResource.END_TIME, value);
        return this;
    }

    public ResidenceFunctionsBuilder withInDebt(Integer value) {
        with(ResidenceFunctionsResource.IN_DEBT, value);
        return this;
    }

}
