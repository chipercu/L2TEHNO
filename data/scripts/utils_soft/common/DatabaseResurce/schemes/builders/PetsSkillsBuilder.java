package utils_soft.common.DatabaseResurce.schemes.builders;

import utils_soft.common.DatabaseResurce.ResourceBuilder;
import utils_soft.common.DatabaseResurce.schemes.resources.PetsSkillsResource;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class PetsSkillsBuilder extends ResourceBuilder<PetsSkillsResource> {

    public PetsSkillsBuilder() {
        try {
            resourceClass = PetsSkillsResource.class;
            resource = resourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public PetsSkillsBuilder withTemplateId(Integer value) {
        with(PetsSkillsResource.TEMPLATE_ID, value);
        return this;
    }

    public PetsSkillsBuilder withMinLvl(Integer value) {
        with(PetsSkillsResource.MIN_LVL, value);
        return this;
    }

    public PetsSkillsBuilder withSkillId(Integer value) {
        with(PetsSkillsResource.SKILL_ID, value);
        return this;
    }

    public PetsSkillsBuilder withSkillLvl(Integer value) {
        with(PetsSkillsResource.SKILL_LVL, value);
        return this;
    }

}
