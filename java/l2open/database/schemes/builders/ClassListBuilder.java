package l2open.database.schemes.builders;

import l2open.database.ResourceBuilder;
import l2open.database.schemes.resources.ClassListResource;

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
