package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.ForumsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class ForumsBuilder extends ResourceBuilder<ForumsResource> {

    public ForumsBuilder() {
        try {
            resourceClass = ForumsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ForumsBuilder withForumId(Integer value) {
        with(ForumsResource.FORUM_ID, value);
        return this;
    }

    public ForumsBuilder withForumName(String value) {
        with(ForumsResource.FORUM_NAME, value);
        return this;
    }

    public ForumsBuilder withForumParent(Integer value) {
        with(ForumsResource.FORUM_PARENT, value);
        return this;
    }

    public ForumsBuilder withForumPost(Integer value) {
        with(ForumsResource.FORUM_POST, value);
        return this;
    }

    public ForumsBuilder withForumType(Integer value) {
        with(ForumsResource.FORUM_TYPE, value);
        return this;
    }

    public ForumsBuilder withForumPerm(Integer value) {
        with(ForumsResource.FORUM_PERM, value);
        return this;
    }

    public ForumsBuilder withForumOwnerId(Integer value) {
        with(ForumsResource.FORUM_OWNER_ID, value);
        return this;
    }

}
