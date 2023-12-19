package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.schemes.ResourceBuilder;
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
        try {
            final Field field = resourceClass.getDeclaredField("NAME");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

    public ServerVariablesBuilder withValue(String value) {
        try {
            final Field field = resourceClass.getDeclaredField("VALUE");
            field.setAccessible(true);
            statsSet.set((String) field.get(resource), value);
        } catch (Exception ignored) {
        }
        return this;
    }

}
