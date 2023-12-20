package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.BannedIpsResource;

public class BannedIpsBuilder extends ResourceBuilder<BannedIpsResource> {

    public BannedIpsBuilder() {
        try {
            resourceClass = BannedIpsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public BannedIpsBuilder withIp(String value) {
        with(BannedIpsResource.IP, value);
        return this;
    }

    public BannedIpsBuilder withAdmin(String value) {
        with(BannedIpsResource.ADMIN, value);
        return this;
    }

    public BannedIpsBuilder withExpiretime(Integer value) {
        with(BannedIpsResource.EXPIRETIME, value);
        return this;
    }

    public BannedIpsBuilder withComments(String value) {
        with(BannedIpsResource.COMMENTS, value);
        return this;
    }

}
