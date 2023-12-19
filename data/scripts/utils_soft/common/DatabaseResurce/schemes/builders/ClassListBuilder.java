package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ClassListResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ClassListBuilder extends ResourceBuilder<ClassListResource> {

    public ClassListBuilder() {
        try {
            resourceClass = ClassListResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassListBuilder withClassName(String value) {
        with(ClassListResource.CLASS_NAME, value);
        return this;
    }

    public ClassListBuilder withId(Integer value) {
        with(ClassListResource.ID, value);
        return this;
    }

    public ClassListBuilder withParentId(Integer value) {
        with(ClassListResource.PARENT_ID, value);
        return this;
    }

    public ClassListBuilder withParentId2(Integer value) {
        with(ClassListResource.PARENT_ID2, value);
        return this;
    }

}
