package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ServerVariablesResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ServerVariablesBuilder extends ResourceBuilder<ServerVariablesResource> {

    public ServerVariablesBuilder() {
        try {
            resourceClass = ServerVariablesResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerVariablesBuilder withName(String value) {
        with(ServerVariablesResource.NAME, value);
        return this;
    }

    public ServerVariablesBuilder withValue(String value) {
        with(ServerVariablesResource.VALUE, value);
        return this;
    }

}
