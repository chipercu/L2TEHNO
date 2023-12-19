package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.SkillLearnResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class SkillLearnBuilder extends ResourceBuilder<SkillLearnResource> {

    public SkillLearnBuilder() {
        try {
            resourceClass = SkillLearnResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SkillLearnBuilder withNpcId(Integer value) {
        with(SkillLearnResource.NPC_ID, value);
        return this;
    }

    public SkillLearnBuilder withClassId(Integer value) {
        with(SkillLearnResource.CLASS_ID, value);
        return this;
    }

}
