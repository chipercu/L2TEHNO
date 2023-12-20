package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.LockResource;

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
