package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.DdosIpsResource;

public class DdosIpsBuilder extends ResourceBuilder<DdosIpsResource> {

    public DdosIpsBuilder() {
        try {
            resourceClass = DdosIpsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DdosIpsBuilder withIp(String value) {
        with(DdosIpsResource.IP, value);
        return this;
    }

    public DdosIpsBuilder withAdmin(String value) {
        with(DdosIpsResource.ADMIN, value);
        return this;
    }

    public DdosIpsBuilder withExpiretime(Integer value) {
        with(DdosIpsResource.EXPIRETIME, value);
        return this;
    }

    public DdosIpsBuilder withComments(String value) {
        with(DdosIpsResource.COMMENTS, value);
        return this;
    }

}
