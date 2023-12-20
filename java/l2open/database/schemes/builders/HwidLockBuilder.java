package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.HwidLockResource;

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
