package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.GlobalTasksResource;

public class GlobalTasksBuilder extends ResourceBuilder<GlobalTasksResource> {

    public GlobalTasksBuilder() {
        try {
            resourceClass = GlobalTasksResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GlobalTasksBuilder withId(Integer value) {
        with(GlobalTasksResource.ID, value);
        return this;
    }

    public GlobalTasksBuilder withTask(String value) {
        with(GlobalTasksResource.TASK, value);
        return this;
    }

    public GlobalTasksBuilder withType(String value) {
        with(GlobalTasksResource.TYPE, value);
        return this;
    }

    public GlobalTasksBuilder withLastActivation(Integer value) {
        with(GlobalTasksResource.LAST_ACTIVATION, value);
        return this;
    }

    public GlobalTasksBuilder withParam1(String value) {
        with(GlobalTasksResource.PARAM1, value);
        return this;
    }

    public GlobalTasksBuilder withParam2(String value) {
        with(GlobalTasksResource.PARAM2, value);
        return this;
    }

    public GlobalTasksBuilder withParam3(String value) {
        with(GlobalTasksResource.PARAM3, value);
        return this;
    }

}
