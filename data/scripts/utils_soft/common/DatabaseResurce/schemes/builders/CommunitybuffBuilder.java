package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.CommunitybuffResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class CommunitybuffBuilder extends ResourceBuilder<CommunitybuffResource> {

    public CommunitybuffBuilder() {
        try {
            resourceClass = CommunitybuffResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommunitybuffBuilder withSkillID(Integer value) {
        with(CommunitybuffResource.SKILL_ID, value);
        return this;
    }

    public CommunitybuffBuilder withSkillLvl(Integer value) {
        with(CommunitybuffResource.SKILL_LVL, value);
        return this;
    }

}
