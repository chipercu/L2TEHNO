package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.BannedIpsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
