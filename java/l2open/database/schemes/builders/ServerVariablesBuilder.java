package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ServerVariablesResource;

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
