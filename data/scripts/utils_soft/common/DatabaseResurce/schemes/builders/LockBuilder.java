package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.LockResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class LockBuilder extends ResourceBuilder<LockResource> {

    public LockBuilder() {
        try {
            resourceClass = LockResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LockBuilder withLogin(String value) {
        with(LockResource.LOGIN, value);
        return this;
    }

    public LockBuilder withType(Enum value) {
        with(LockResource.TYPE, value);
        return this;
    }

    public LockBuilder withString(String value) {
        with(LockResource.STRING, value);
        return this;
    }

}
