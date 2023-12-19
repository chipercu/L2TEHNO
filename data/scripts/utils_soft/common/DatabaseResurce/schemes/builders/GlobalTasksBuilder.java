package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.GlobalTasksResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

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
