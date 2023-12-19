package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.HwidLockResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class HwidLockBuilder extends ResourceBuilder<HwidLockResource> {

    public HwidLockBuilder() {
        try {
            resourceClass = HwidLockResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public HwidLockBuilder withLogin(String value) {
        with(HwidLockResource.LOGIN, value);
        return this;
    }

    public HwidLockBuilder withHwid(String value) {
        with(HwidLockResource.HWID, value);
        return this;
    }

}
