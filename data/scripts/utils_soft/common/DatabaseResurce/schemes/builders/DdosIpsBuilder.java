package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.DdosIpsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
