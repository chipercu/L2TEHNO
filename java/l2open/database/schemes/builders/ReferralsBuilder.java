package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ReferralsResource;

public class ReferralsBuilder extends ResourceBuilder<ReferralsResource> {

    public ReferralsBuilder() {
        try {
            resourceClass = ReferralsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ReferralsBuilder withLogin(String value) {
        with(ReferralsResource.LOGIN, value);
        return this;
    }

    public ReferralsBuilder withServer(Integer value) {
        with(ReferralsResource.SERVER, value);
        return this;
    }

    public ReferralsBuilder withChar(Integer value) {
        with(ReferralsResource._CHAR, value);
        return this;
    }

    public ReferralsBuilder withBonus1(String value) {
        with(ReferralsResource.BONUS1, value);
        return this;
    }

    public ReferralsBuilder withBonus2(String value) {
        with(ReferralsResource.BONUS2, value);
        return this;
    }

}
